package com.mash.aoptracktime.aspect.tracktime.impl;

import com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTimeAnnotationData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Class to retrieve data from methods annotated with {@link TrackAsyncTime} annotation,
 * used in the context of Aspect processing.
 *
 * @author Mikhail Shamanov
 * @see TrackTime
 * @see AsyncTimeTracker
 */
public class TrackAsyncTimeAnnotationData implements TrackTimeAnnotationData {
    private final TrackAsyncTime trackAsyncTime;

    public TrackAsyncTimeAnnotationData(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();

        if (!method.isAnnotationPresent(TrackAsyncTime.class)) {
            throw new IllegalStateException("Method " + method.getName() + " does not have @TrackAsyncTime annotation");
        }

        this.trackAsyncTime = method.getAnnotation(TrackAsyncTime.class);
    }

    @Override
    public String groupName() {
        return this.trackAsyncTime.groupName();
    }

    @Override
    public boolean ignoreOnException() {
        return this.trackAsyncTime.ignoreOnException();
    }

    @Override
    public TrackAsyncTime getAnnotation() {
        return this.trackAsyncTime;
    }
}
