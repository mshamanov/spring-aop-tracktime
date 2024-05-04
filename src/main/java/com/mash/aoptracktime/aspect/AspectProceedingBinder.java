package com.mash.aoptracktime.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

public interface AspectProceedingBinder {
    Object bind(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;
}
