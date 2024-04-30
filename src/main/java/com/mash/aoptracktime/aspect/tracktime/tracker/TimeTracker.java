package com.mash.aoptracktime.aspect.tracktime.tracker;

import org.aspectj.lang.ProceedingJoinPoint;

public interface TimeTracker {
    Object bind(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;
}
