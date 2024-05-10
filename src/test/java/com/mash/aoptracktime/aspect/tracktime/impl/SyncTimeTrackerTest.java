package com.mash.aoptracktime.aspect.tracktime.impl;

import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime;
import com.mash.aoptracktime.entity.TrackTimeMethodStatus;
import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.repository.TrackTimeStatsRepository;
import com.mash.aoptracktime.service.TrackTimeStatsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncTimeTrackerTest {

    static class FooClass {
        static final String FOO_GROUP = "fooGroup";

        @TrackTime(groupName = FooClass.FOO_GROUP)
        public int foo(int n) {
            int sum = 0;

            for (int i = 0; i < n; i++) {
                sum += i;
            }

            return sum;
        }

        @TrackTime(groupName = FooClass.FOO_GROUP, ignoreOnException = true)
        public int fooIgnored(int n) {
            return this.foo(n);
        }
    }

    @Mock
    ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    MethodSignature methodSignature;

    @Mock
    TrackTimeStatsRepository repository;

    @InjectMocks
    TrackTimeStatsService service;

    FooClass fooInstance;

    SyncTimeTracker syncTimeTracker;

    @BeforeEach
    void setUp() {
        this.fooInstance = new FooClass();
        this.syncTimeTracker = new SyncTimeTracker(this.service);
    }

    @Test
    void handleSync_whenMethodReturnsNormally_thenTrackerSavesMeasurementsInStatusCompleted() throws Throwable {
        Method method = ReflectionUtils.findMethod(FooClass.class, "foo", int.class)
                .orElseThrow(() -> new IllegalStateException("Method doesn't exist"));
        int fooResult = this.fooInstance.foo(100);

        when(this.proceedingJoinPoint.getSignature()).thenReturn(this.methodSignature);
        when(this.proceedingJoinPoint.proceed()).thenReturn(fooResult);
        when(this.methodSignature.getDeclaringType()).thenReturn(method.getDeclaringClass());
        when(this.methodSignature.getReturnType()).thenReturn(method.getReturnType());
        when(this.methodSignature.getParameterTypes()).thenReturn(method.getParameterTypes());
        when(this.methodSignature.getMethod()).thenReturn(method);
        when(this.methodSignature.getName()).thenReturn(method.getName());

        Object result = this.syncTimeTracker.bind(this.proceedingJoinPoint);

        ArgumentCaptor<TrackTimeStat> captor = ArgumentCaptor.forClass(TrackTimeStat.class);
        verify(this.repository).save(captor.capture());
        TrackTimeStat stat = captor.getValue();
        assertNotNull(stat);
        assertEquals(fooResult, result);
        assertEquals(method.getAnnotation(TrackTime.class).groupName(), stat.getGroupName());
        assertEquals(method.getDeclaringClass().getPackageName(), stat.getPackageName());
        assertEquals(method.getDeclaringClass().getSimpleName(), stat.getClassName());
        assertEquals(method.getName(), stat.getMethodName());
        assertEquals(Arrays.stream(method.getParameters())
                .map(p -> p.getType().getSimpleName())
                .collect(Collectors.joining(", ")), stat.getParameters());
        assertEquals(method.getReturnType().getCanonicalName(), stat.getReturnType());
        assertEquals(TrackTimeMethodStatus.COMPLETED, stat.getStatus());
        assertInstanceOf(Long.class, stat.getExecutionTime());
    }

    @Test
    void handleSync_whenMethodReturnsExceptionally_thenTrackerSavesMeasurementsInStatusException() throws Throwable {
        Method method = ReflectionUtils.findMethod(FooClass.class, "foo", int.class)
                .orElseThrow(() -> new IllegalStateException("Method doesn't exist"));

        when(this.proceedingJoinPoint.getSignature()).thenReturn(this.methodSignature);
        when(this.proceedingJoinPoint.proceed()).thenThrow(new RuntimeException("Something went wrong"));
        when(this.methodSignature.getDeclaringType()).thenReturn(method.getDeclaringClass());
        when(this.methodSignature.getReturnType()).thenReturn(method.getReturnType());
        when(this.methodSignature.getParameterTypes()).thenReturn(method.getParameterTypes());
        when(this.methodSignature.getMethod()).thenReturn(method);
        when(this.methodSignature.getName()).thenReturn(method.getName());

        assertThrows(RuntimeException.class, () -> this.syncTimeTracker.bind(this.proceedingJoinPoint));

        ArgumentCaptor<TrackTimeStat> captor = ArgumentCaptor.forClass(TrackTimeStat.class);
        verify(this.repository).save(captor.capture());
        TrackTimeStat stat = captor.getValue();
        assertNotNull(stat);
        assertEquals(method.getAnnotation(TrackTime.class).groupName(), stat.getGroupName());
        assertEquals(method.getDeclaringClass().getPackageName(), stat.getPackageName());
        assertEquals(method.getDeclaringClass().getSimpleName(), stat.getClassName());
        assertEquals(method.getName(), stat.getMethodName());
        assertEquals(Arrays.stream(method.getParameters())
                .map(p -> p.getType().getSimpleName())
                .collect(Collectors.joining(", ")), stat.getParameters());
        assertEquals(method.getReturnType().getCanonicalName(), stat.getReturnType());
        assertEquals(TrackTimeMethodStatus.EXCEPTION, stat.getStatus());
        assertInstanceOf(Long.class, stat.getExecutionTime());
    }

    @Test
    void handleSync_whenIgnoreOnExceptionSetAndExceptionThrown_thenTrackerDoesntSaveMeasurements() throws Throwable {
        Method method = ReflectionUtils.findMethod(FooClass.class, "fooIgnored", int.class)
                .orElseThrow(() -> new IllegalStateException("Method doesn't exist"));

        when(this.proceedingJoinPoint.getSignature()).thenReturn(this.methodSignature);
        when(this.proceedingJoinPoint.proceed()).thenThrow(new RuntimeException("Something went wrong"));
        when(this.methodSignature.getMethod()).thenReturn(method);

        assertThrows(RuntimeException.class, () -> this.syncTimeTracker.bind(this.proceedingJoinPoint));
        verifyNoInteractions(this.repository);
    }
}