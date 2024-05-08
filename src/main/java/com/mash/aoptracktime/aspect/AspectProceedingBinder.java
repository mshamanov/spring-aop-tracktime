package com.mash.aoptracktime.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Interface to be used in the context of Aspect processing where instance of the ProceedingJoinPoint
 * becomes a starting point to be bound with.
 *
 * @author Mikhail Shamanov
 */
public interface AspectProceedingBinder {
    /**
     * Binds an instance of ProceedingJoinPoint with the current implementation.
     *
     * @param proceedingJoinPoint join point in process
     * @return either unmodified result of the method being processed or modified if it is needed
     * @throws Throwable any exception to be thrown
     */
    Object bind(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;
}
