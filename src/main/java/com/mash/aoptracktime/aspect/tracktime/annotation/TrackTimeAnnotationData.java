package com.mash.aoptracktime.aspect.tracktime.annotation;

import java.lang.annotation.Annotation;

/**
 * Interface used to retrieve common data from TrackTime annotations group.
 *
 * @author Mikhail Shamanov
 * @see TrackTime
 * @see TrackAsyncTime
 */
public interface TrackTimeAnnotationData {
    /**
     * Retrieves the group name of the method annotated with {@link TrackTime} annotation.
     *
     * @return group name
     */
    String groupName();

    /**
     * Indicates whether this method should be a subject of time measuring if it throws exception.
     *
     * @return true if there is no need to record measuring data when this method throws exception,
     * otherwise false. By default, any result is valid.
     */
    boolean ignoreOnException();

    /**
     * Retrieves annotation data from the method annotated with {@link TrackTime} annotation.
     *
     * @return annotation data
     */
    Annotation getAnnotation();
}
