package com.mash.aoptracktime.aspect.tracktime.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate an asynchronous method to be a subject of measuring method execution time.
 * Asynchronous method implies either @Async annotated method or the method returning CompletableFuture or Future,
 * as well as other methods that semantically perform async tasks.
 * When synchronous time tracking is needed you should choose {@link TrackTime} instead.
 *
 * @author Mikhail Shamanov
 * @see TrackTime
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackAsyncTime {
    /**
     * Sets the group name of the current method.
     *
     * @return group name
     */
    String groupName() default "async";

    /**
     * Sets whether this method should be a subject of time measuring if it throws exception.
     *
     * @return true if there is no need to record measuring data when this method throws exception,
     * otherwise false. By default, any result is valid.
     */
    boolean ignoreOnException() default false;
}