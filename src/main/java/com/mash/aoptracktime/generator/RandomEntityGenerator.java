package com.mash.aoptracktime.generator;

import java.util.List;

/**
 * Interface to generate specified entities.
 *
 * @param <T> specified entity
 */
@FunctionalInterface
public interface RandomEntityGenerator<T> {
    List<T> generate(Long n);
}
