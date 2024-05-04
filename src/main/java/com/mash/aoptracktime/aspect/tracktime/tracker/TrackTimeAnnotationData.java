package com.mash.aoptracktime.aspect.tracktime.tracker;

import java.lang.annotation.Annotation;

public interface TrackTimeAnnotationData {
    String groupName();

    boolean ignoreOnException();

    Annotation getAnnotation();
}
