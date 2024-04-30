package com.mash.aoptracktime.aspect.tracktime.annotation;

import java.lang.annotation.Annotation;

public interface TrackAnnotationData {
    String groupName();

    boolean ignoreOnException();

    Annotation getAnnotation();
}
