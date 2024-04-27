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
import java.util.concurrent.*;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class TrackTimeAspect {

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

    private MethodSignature getMethodSignature(ProceedingJoinPoint proceedingJoinPoint) {
        return (MethodSignature) proceedingJoinPoint.getSignature();
    }

    private String getMethodGroupName(MethodSignature methodSignature) {
        Method method = methodSignature.getMethod();

        if (method.isAnnotationPresent(TrackTime.class)) {
            return method.getAnnotation(TrackTime.class).groupName();
        } else if (method.isAnnotationPresent(TrackAsyncTime.class)) {
            return method.getAnnotation(TrackAsyncTime.class).groupName();
        }

        return "";
    }

    private boolean isAsyncReturningFuture(ProceedingJoinPoint proceedingJoinPoint) {
        return Future.class.isAssignableFrom(this.getMethodSignature(proceedingJoinPoint).getReturnType());
    }

    private boolean isAsyncReturningCompletableFuture(ProceedingJoinPoint proceedingJoinPoint) {
        return CompletableFuture.class.isAssignableFrom(this.getMethodSignature(proceedingJoinPoint).getReturnType());
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

        long executionTime = stopWatch.getTotalTimeMillis();
        TrackTimeMethodStatus status;

        if (throwable == null) {
            status = TrackTimeMethodStatus.COMPLETED;
        } else {
            status = TrackTimeMethodStatus.EXCEPTION;
        }

        log.info("Execution time of {} :: {} ms [{}]", proceedingJoinPoint.getSignature()
                .toLongString(), executionTime, status);

        TrackTimeStat trackTimeStat = createTrackTimeStat(proceedingJoinPoint, executionTime, status);
        this.trackTimeStatsService.saveAsync(trackTimeStat);
    }

    private TrackTimeStat createTrackTimeStat(ProceedingJoinPoint proceedingJoinPoint, long executionTime, TrackTimeMethodStatus status) {
        MethodSignature methodSignature = this.getMethodSignature(proceedingJoinPoint);
        Class<?> declaringType = methodSignature.getDeclaringType();

        return TrackTimeStat.builder()
                .groupName(this.getMethodGroupName(methodSignature))
                .returnType(methodSignature.getReturnType().getCanonicalName())
                .packageName(declaringType.getPackageName())
                .className(declaringType.getSimpleName())
                .methodName(methodSignature.getName())
                .parameters(Arrays.toString(methodSignature.getParameterTypes()))
                .executionTime(executionTime)
                .status(status)
                .build();
    }
}
