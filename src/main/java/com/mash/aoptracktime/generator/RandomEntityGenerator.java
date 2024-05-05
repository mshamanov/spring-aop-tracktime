package com.mash.aoptracktime.generator;

import java.util.List;

@FunctionalInterface
public interface RandomEntityGenerator<T> {
    List<T> generate(Long n);
}
