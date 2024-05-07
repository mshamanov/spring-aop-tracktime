package com.mash.aoptracktime.aspect.tracktime.impl;

import com.mash.aoptracktime.aspect.AspectProceedingBinder;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTimeAnnotationData;
import com.mash.aoptracktime.entity.TrackTimeMethodStatus;
import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class AbstractTimeTracker implements AspectProceedingBinder {

    private final TrackTimeStatsService trackTimeStatsService;

    public AbstractTimeTracker(TrackTimeStatsService trackTimeStatsService) {
        this.trackTimeStatsService = trackTimeStatsService;
    }

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

    protected abstract Object bind(Object result, ProceedingJoinPoint proceedingJoinPoint,
                                   StopWatch stopWatch, Throwable t);

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

    protected TrackTimeStat buildStat(ProceedingJoinPoint proceedingJoinPoint, StopWatch stopWatch,
                                      TrackTimeAnnotationData annotationData, Throwable t) {
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
                .status(t == null ? TrackTimeMethodStatus.COMPLETED : TrackTimeMethodStatus.EXCEPTION)
                .build();
    }
}
