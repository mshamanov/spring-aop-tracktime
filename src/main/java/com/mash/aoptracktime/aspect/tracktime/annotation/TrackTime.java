package com.mash.aoptracktime.aspect.tracktime.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a synchronous method to be a subject of measuring method execution time.
 * Synchronous method implies being performed in a sequential order,
 * i.e. common method calls without being run in a separate thread.
 * When asynchronous time tracking is needed you should choose {@link TrackAsyncTime} instead.
 *
 * @author Mikhail Shamanov
 * @see TrackAsyncTime
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackTime {
    /**
     * Sets the group name of the current method.
     *
     * @return group name
     */
    String groupName() default "sync";

    /**
     * Sets whether this method should be a subject of time measuring if it throws exception.
     *
     * @return true if there is no need to record measuring data when this method throws exception,
     * otherwise false. By default, any result is valid.
     */
    boolean ignoreOnException() default false;
}