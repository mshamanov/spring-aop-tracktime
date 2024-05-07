package com.mash.aoptracktime.aspect.tracktime.impl;

import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTimeAnnotationData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public class TrackSyncTimeAnnotationData implements TrackTimeAnnotationData {
    private final TrackTime trackTime;

    public TrackSyncTimeAnnotationData(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();

        if (!method.isAnnotationPresent(TrackTime.class)) {
            throw new IllegalStateException("Method " + method.getName() + " does not have @TrackTime annotation");
        }

        this.trackTime = method.getAnnotation(TrackTime.class);
    }

    @Override
    public String groupName() {
        return this.trackTime.groupName();
    }

    @Override
    public boolean ignoreOnException() {
        return this.trackTime.ignoreOnException();
    }

    @Override
    public TrackTime getAnnotation() {
        return this.trackTime;
    }
}
