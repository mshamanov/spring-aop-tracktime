package com.mash.aoptracktime.aspect.tracktime;

import com.mash.aoptracktime.aspect.tracktime.tracker.TimeTracker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class TrackTimeAspect {

    private final TimeTracker timeTracker;
    private final TimeTracker timeAsyncTracker;

    public TrackTimeAspect(@Qualifier("syncTimeTracker") TimeTracker timeTracker,
                           @Qualifier("asyncTimeTracker") TimeTracker timeAsyncTracker) {
        this.timeTracker = timeTracker;
        this.timeAsyncTracker = timeAsyncTracker;
    }

    @Pointcut("@annotation(com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime) && " +
            "!@annotation(com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime) && " +
            "!@annotation(org.springframework.scheduling.annotation.Async)")
    private void trackTimeAspect() {
    }

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Async) && " +
            "@annotation(com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime) && " +
            "!@annotation(com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime)")
    private void asyncTrackTimeAspect() {
    }

    @Around("trackTimeAspect()")
    public Object trackTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return this.timeTracker.bind(proceedingJoinPoint);
    }

    @Around("asyncTrackTimeAspect()")
    public Object trackAsyncTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return this.timeAsyncTracker.bind(proceedingJoinPoint);
    }
}
