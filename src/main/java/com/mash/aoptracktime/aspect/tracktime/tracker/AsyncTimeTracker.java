package com.mash.aoptracktime.aspect.tracktime.tracker;

import com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTimeAnnotationData;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.concurrent.*;

@Component
public class AsyncTimeTracker extends AbstractTimeTracker {

    public AsyncTimeTracker(TrackTimeStatsService trackTimeStatsService) {
        super(trackTimeStatsService);
    }

    @Override
    protected Object bind(Object result, ProceedingJoinPoint proceedingJoinPoint, StopWatch stopWatch, Throwable t) {
        TrackAsyncTimeAnnotationData annotationData = new TrackAsyncTimeAnnotationData(proceedingJoinPoint);

        boolean bindToFuture = annotationData.getAnnotation().bindToFuture();

        if (bindToFuture) {
            return this.bindToFuture(result, proceedingJoinPoint, stopWatch, annotationData);
        }

        stopWatch.stop();
        CompletableFuture.runAsync(() -> this.recordStat(proceedingJoinPoint, stopWatch, annotationData, t));
        return result;
    }

    private CompletableFuture<?> bindToFuture(Object result, ProceedingJoinPoint proceedingJoinPoint, StopWatch stopWatch, TrackAsyncTimeAnnotationData annotationData) {
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

        if (future == null) {
            throw new IllegalStateException(
                    "@TrackAsyncTime to be bound to future result must return either CompletableFuture or Future");
        }

        return future.whenComplete((res, err) -> {
            stopWatch.stop();
            this.recordStat(proceedingJoinPoint, stopWatch, annotationData, err);
        });
    }
}
