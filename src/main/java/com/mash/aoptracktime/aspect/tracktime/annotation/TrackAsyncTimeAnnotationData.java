package com.mash.aoptracktime.aspect.tracktime.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public class TrackAsyncTimeAnnotationData implements TrackAnnotationData {
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
