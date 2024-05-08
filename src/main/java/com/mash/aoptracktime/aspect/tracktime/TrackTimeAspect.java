package com.mash.aoptracktime.aspect.tracktime;

import com.mash.aoptracktime.aspect.AspectProceedingBinder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Aspect to «track» methods execution time for both synchronous and asynchronous calls.
 *
 * @author Mikhail Shamanov
 */
@Slf4j
@Aspect
@Component
public class TrackTimeAspect {

    private final AspectProceedingBinder timeTracker;
    private final AspectProceedingBinder timeAsyncTracker;

    public TrackTimeAspect(@Qualifier("syncTimeTracker") AspectProceedingBinder timeTracker,
                           @Qualifier("asyncTimeTracker") AspectProceedingBinder timeAsyncTracker) {
        this.timeTracker = timeTracker;
        this.timeAsyncTracker = timeAsyncTracker;
    }

    @Pointcut("@annotation(com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime) && " +
              "!@annotation(com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime)")
    private void trackTimeAspect() {
    }

    @Pointcut("@annotation(com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime) && " +
              "!@annotation(com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime)")
    private void asyncTrackTimeAspect() {
    }

    /**
     * Processes synchronous method calls
     *
     * @param proceedingJoinPoint join point in process
     * @return result of the method call
     * @throws Throwable thrown exception
     */
    @Around("trackTimeAspect()")
    public Object trackTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return this.timeTracker.bind(proceedingJoinPoint);
    }

    /**
     * Processes asynchronous method calls
     *
     * @param proceedingJoinPoint join point in process
     * @return result of the method call
     * @throws Throwable thrown exception
     */
    @Around("asyncTrackTimeAspect()")
    public Object trackAsyncTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return this.timeAsyncTracker.bind(proceedingJoinPoint);
    }
}
