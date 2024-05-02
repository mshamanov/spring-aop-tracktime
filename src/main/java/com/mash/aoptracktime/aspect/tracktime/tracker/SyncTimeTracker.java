package com.mash.aoptracktime.aspect.tracktime.tracker;

import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTimeAnnotationData;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class SyncTimeTracker extends AbstractTimeTracker {

    public SyncTimeTracker(TrackTimeStatsService trackTimeStatsService) {
        super(trackTimeStatsService);
    }

    @Override
    protected Object bind(Object result, ProceedingJoinPoint proceedingJoinPoint, StopWatch stopWatch, Throwable t) {
        this.recordStat(proceedingJoinPoint, stopWatch, new TrackTimeAnnotationData(proceedingJoinPoint), t);
        return result;
    }
}
