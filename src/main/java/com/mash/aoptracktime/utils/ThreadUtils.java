package com.mash.aoptracktime.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

/**
 * Utility class to perform actions with threads.
 *
 * @author Mikhail Shamanov
 */
@UtilityClass
public class ThreadUtils {
    /**
     * Makes a thread sleep for a specified amount of time.
     *
     * @param timeout amount of time
     * @param unit    units of time
     */
    public void sleep(long timeout, TimeUnit unit) {
        try {
            unit.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}