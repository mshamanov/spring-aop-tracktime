package com.mash.aoptracktime.aspect.tracktime.impl;

import com.mash.aoptracktime.aspect.AspectProceedingBinder;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.concurrent.*;

/**
 * Implementation class to represent an asynchronous time tracker binder {@link AspectProceedingBinder},
 * used in the context of Aspect processing to record execution time measurements of method calls.
 *
 * @author Mikhail Shamanov
 * @see TrackAsyncTime
 */
@Component
public class AsyncTimeTracker extends AbstractTimeTracker {

    public AsyncTimeTracker(TrackTimeStatsService trackTimeStatsService) {
        super(trackTimeStatsService);
    }

    @Override
    protected Object bind(Object result, ProceedingJoinPoint proceedingJoinPoint, StopWatch stopWatch, Throwable t) {
        TrackAsyncTimeAnnotationData annotationData = new TrackAsyncTimeAnnotationData(proceedingJoinPoint);

        Future<?> future = this.tryBindToFuture(result, proceedingJoinPoint, stopWatch, annotationData);

        if (future != null) {
            return future;
        }

        this.recordStat(proceedingJoinPoint, stopWatch, annotationData, t);
        return result;
    }

    private CompletableFuture<?> tryBindToFuture(Object result, ProceedingJoinPoint proceedingJoinPoint,
                                                 StopWatch stopWatch, TrackAsyncTimeAnnotationData annotationData) {
        CompletableFuture<?> future = null;

        if (result instanceof CompletableFuture<?>) {
            future = ((CompletableFuture<?>) result);
        } else if (result instanceof Future<?>) {
            future = CompletableFuture.supplyAsync(() -> {
                try {
                    return ((Future<?>) result).get(5, TimeUnit.MINUTES);
                } catch (ExecutionException | InterruptedException | TimeoutException e) {
                    throw new CompletionException(e);
                }
            });
        }

        if (future != null) {
            return future.whenComplete((res, err) -> this.recordStat(proceedingJoinPoint, stopWatch, annotationData, err));
        }

        return future;
    }
}
