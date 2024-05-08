package com.mash.aoptracktime.aspect.tracktime.impl;

import com.mash.aoptracktime.aspect.AspectProceedingBinder;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTimeAnnotationData;
import com.mash.aoptracktime.entity.TrackTimeMethodStatus;
import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Abstract class to represent time tracker binder {@link AspectProceedingBinder},
 * used in the context of Aspect processing to record execution time measurements of method calls.
 *
 * @author Mikhail Shamanov
 * @see TrackTime
 * @see TrackAsyncTime
 */
@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractTimeTracker implements AspectProceedingBinder {

    private final TrackTimeStatsService trackTimeStatsService;

    public Object bind(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
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
            result = this.bind(result, proceedingJoinPoint, stopWatch, throwable);
        }

        return result;
    }

    /**
     * Runs binding process after having result of the method call, as well as passes the time measuring data
     * and throwable to indicate whether the method failed or not.
     *
     * @param result              result of the method call
     * @param proceedingJoinPoint join point in process
     * @param stopWatch           time measuring data of the method call
     * @param throwable           throwable whether the method call threw exception
     * @return either unmodified result of the method call or modified
     */
    protected abstract Object bind(Object result, ProceedingJoinPoint proceedingJoinPoint,
                                   StopWatch stopWatch, Throwable throwable);

    /**
     * Performs building and recording time measuring data of the method being processed.
     *
     * @param proceedingJoinPoint join point in process
     * @param stopWatch           time measuring data of the method call
     * @param annotationData      data with additional information related to the method
     * @param throwable           throwable whether the method call threw exception
     */
    protected void recordStat(ProceedingJoinPoint proceedingJoinPoint, StopWatch stopWatch,
                              TrackTimeAnnotationData annotationData, Throwable throwable) {
        if (!stopWatch.isRunning()) {
            throw new IllegalStateException("StopWatch is not running");
        }

        stopWatch.stop();

        if (throwable != null && annotationData.ignoreOnException()) {
            return;
        }

        TrackTimeStat stats = this.buildStat(proceedingJoinPoint, stopWatch, annotationData, throwable);

        log.info("Execution time of {}.{}.{} :: {} ms [{}]",
                stats.getPackageName(),
                stats.getClassName(),
                stats.getMethodName(),
                stats.getExecutionTime(),
                stats.getStatus());

        this.trackTimeStatsService.saveAsync(stats);
    }

    /**
     * Builds statistics entity {@link TrackTimeStat}
     *
     * @param proceedingJoinPoint join point in process
     * @param stopWatch           time measuring data of the method call
     * @param annotationData      data with additional information related to the method
     * @param throwable           throwable whether the method call threw exception
     */
    protected TrackTimeStat buildStat(ProceedingJoinPoint proceedingJoinPoint, StopWatch stopWatch,
                                      TrackTimeAnnotationData annotationData, Throwable throwable) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        String groupName = annotationData.groupName();
        String returnType = methodSignature.getReturnType().getCanonicalName();
        String packageName = methodSignature.getDeclaringType().getPackageName();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        long executionTime = stopWatch.getTotalTimeMillis();

        String parameters = Arrays.stream(methodSignature.getParameterTypes())
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", "));

        return TrackTimeStat.builder()
                .groupName(groupName)
                .returnType(returnType)
                .packageName(packageName)
                .className(className)
                .methodName(methodName)
                .parameters(parameters)
                .executionTime(executionTime)
                .status(throwable == null ? TrackTimeMethodStatus.COMPLETED : TrackTimeMethodStatus.EXCEPTION)
                .build();
    }
}
