package com.mash.aoptracktime.aspect;

import com.mash.aoptracktime.aspect.annotation.TrackAsyncTime;
import com.mash.aoptracktime.aspect.annotation.TrackTime;
import com.mash.aoptracktime.entity.TrackTimeMethodStatus;
import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class TrackTimeAspect {

    private static final class AnnotationValuesWrapper {
        private Optional<TrackTime> trackTimeAnnotation = Optional.empty();
        private Optional<TrackAsyncTime> trackAsyncTimeAnnotation = Optional.empty();

        private AnnotationValuesWrapper(ProceedingJoinPoint proceedingJoinPoint) {
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = methodSignature.getMethod();

            if (method.isAnnotationPresent(TrackTime.class)) {
                this.trackTimeAnnotation = Optional.ofNullable(method.getAnnotation(TrackTime.class));
            } else if (method.isAnnotationPresent(TrackAsyncTime.class)) {
                this.trackAsyncTimeAnnotation = Optional.ofNullable(method.getAnnotation(TrackAsyncTime.class));
            } else {
                throw new IllegalArgumentException(
                        "Annotated element must be annotated with either %s or %s".formatted(
                                TrackTime.class.getSimpleName(), TrackAsyncTime.class.getSimpleName()));
            }
        }

        private String groupName() {
            return this.trackTimeAnnotation.map(TrackTime::groupName)
                    .or(() -> this.trackAsyncTimeAnnotation.map(TrackAsyncTime::groupName))
                    .orElse("");
        }

        private boolean ignoreOnException() {
            return this.trackTimeAnnotation.map(TrackTime::ignoreOnException)
                    .or(() -> this.trackAsyncTimeAnnotation.map(TrackAsyncTime::ignoreOnException))
                    .orElse(false);
        }
    }

    private final TrackTimeStatsService trackTimeStatsService;

    @Around("@annotation(com.mash.aoptracktime.aspect.annotation.TrackTime) && " +
            "!@annotation(com.mash.aoptracktime.aspect.annotation.TrackAsyncTime) && " +
            "!@annotation(org.springframework.scheduling.annotation.Async)")
    public Object trackTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();

        Object result;
        Throwable throwable = null;
        try {
            stopWatch.start();
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            trackTime(proceedingJoinPoint, stopWatch, throwable);
        }

        return result;
    }

    @Around("@annotation(org.springframework.scheduling.annotation.Async) && " +
            "@annotation(com.mash.aoptracktime.aspect.annotation.TrackAsyncTime) && " +
            "!@annotation(com.mash.aoptracktime.aspect.annotation.TrackTime)"
    )
    public Object trackAsyncTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        Object result = null;
        Throwable throwable = null;

        try {
            stopWatch.start();
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            if (result == null) {
                trackTime(proceedingJoinPoint, stopWatch, throwable);
            } else {
                if (this.isAsyncReturningCompletableFuture(proceedingJoinPoint)) {
                    result = attachTrackTimeForCompletableFuture(proceedingJoinPoint, (CompletableFuture<?>) result, stopWatch);
                } else if (this.isAsyncReturningFuture(proceedingJoinPoint)) {
                    result = attachTrackTimeForFuture(proceedingJoinPoint, (Future<?>) result, stopWatch);
                }
            }
        }

        return result;
    }

    private boolean isAsyncReturningFuture(ProceedingJoinPoint proceedingJoinPoint) {
        return Future.class.isAssignableFrom(
                ((MethodSignature) proceedingJoinPoint.getSignature()).getReturnType());
    }

    private boolean isAsyncReturningCompletableFuture(ProceedingJoinPoint proceedingJoinPoint) {
        return CompletableFuture.class.isAssignableFrom(
                ((MethodSignature) proceedingJoinPoint.getSignature()).getReturnType());
    }

    private CompletableFuture<?> attachTrackTimeForCompletableFuture(ProceedingJoinPoint proceedingJoinPoint, CompletableFuture<?> future, StopWatch stopWatch) {
        return future.whenComplete((res, t) -> trackTime(proceedingJoinPoint, stopWatch, t));
    }

    private Future<?> attachTrackTimeForFuture(ProceedingJoinPoint proceedingJoinPoint, Future<?> future, StopWatch stopWatch) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get(5, TimeUnit.MINUTES);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                throw new CompletionException(e);
            }
        }).whenComplete((res, t) -> trackTime(proceedingJoinPoint, stopWatch, t));
    }

    private void trackTime(ProceedingJoinPoint proceedingJoinPoint, StopWatch stopWatch, Throwable throwable) {
        if (!stopWatch.isRunning()) {
            throw new IllegalStateException("StopWatch is not running");
        }

        stopWatch.stop();

        AnnotationValuesWrapper annotationValuesWrapper = new AnnotationValuesWrapper(proceedingJoinPoint);
        boolean ignoreOnException = annotationValuesWrapper.ignoreOnException();

        if (throwable != null && ignoreOnException) {
            return;
        }

        String groupName = annotationValuesWrapper.groupName();
        long executionTime = stopWatch.getTotalTimeMillis();

        TrackTimeStat trackTimeStat = createTrackTimeStat(proceedingJoinPoint, groupName, executionTime, throwable);
        trackTime(trackTimeStat);
    }

    private void trackTime(TrackTimeStat trackTimeStat) {
        log.info("Execution time of {}.{}.{} :: {} ms [{}]",
                trackTimeStat.getPackageName(),
                trackTimeStat.getClassName(),
                trackTimeStat.getMethodName(),
                trackTimeStat.getExecutionTime(),
                trackTimeStat.getStatus());


        this.trackTimeStatsService.saveAsync(trackTimeStat);
    }

    private TrackTimeStat createTrackTimeStat(ProceedingJoinPoint proceedingJoinPoint, String groupName,
                                              long executionTime, Throwable t) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        return TrackTimeStat.builder()
                .groupName(groupName)
                .returnType(methodSignature.getReturnType().getCanonicalName())
                .packageName(methodSignature.getDeclaringType().getPackageName())
                .className(methodSignature.getDeclaringType().getSimpleName())
                .methodName(methodSignature.getName())
                .parameters(
                        Arrays.stream(methodSignature.getParameterTypes())
                                .map(Class::getSimpleName)
                                .collect(Collectors.joining(", ")))
                .executionTime(executionTime)
                .status(t == null ? TrackTimeMethodStatus.COMPLETED : TrackTimeMethodStatus.EXCEPTION)
                .build();
    }
}
