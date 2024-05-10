package com.mash.aoptracktime.aspect.tracktime.impl;

import com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime;
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
import org.springframework.scheduling.annotation.AsyncResult;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncTimeTrackerTest {

    static class FooClass {
        static final String FOO_GROUP = "fooGroup";

        @TrackAsyncTime(groupName = FooClass.FOO_GROUP)
        public int foo(int n) {
            int sum = 0;

            for (int i = 0; i < n; i++) {
                sum += i;
            }

            return sum;
        }

        @TrackAsyncTime(groupName = FooClass.FOO_GROUP)
        public CompletableFuture<Integer> completedFuture(int n) {
            return CompletableFuture.completedFuture(this.foo(n));
        }

        @TrackAsyncTime(groupName = FooClass.FOO_GROUP)
        public CompletableFuture<Void> failedFuture() {
            return CompletableFuture.failedFuture(new RuntimeException("Something went wrong"));
        }

        @TrackAsyncTime(groupName = FooClass.FOO_GROUP)
        public Future<Integer> future() {
            return new AsyncResult<>(1);
        }

        @TrackAsyncTime(groupName = FooClass.FOO_GROUP)
        public Future<Void> throwableFuture() {
            return AsyncResult.forExecutionException(new RuntimeException("Something went wrong"));
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

    AsyncTimeTracker asyncTimeTracker;

    @BeforeEach
    void setUp() {
        this.fooInstance = new FooClass();
        this.asyncTimeTracker = new AsyncTimeTracker(this.service);
    }

    @Test
    void handleAsync_whenMethodReturnsNormally_thenTrackerSavesMeasurementsInStatusCompleted() throws Throwable {
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

        Object result = this.asyncTimeTracker.bind(this.proceedingJoinPoint);

        ArgumentCaptor<TrackTimeStat> captor = ArgumentCaptor.forClass(TrackTimeStat.class);
        verify(this.repository).save(captor.capture());
        TrackTimeStat stat = captor.getValue();

        assertNotNull(stat);
        assertEquals(fooResult, result);
        assertEquals(method.getAnnotation(TrackAsyncTime.class).groupName(), stat.getGroupName());
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
    void handleAsync_whenCompletableFutureReturnsNormally_thenTrackerSavesMeasurementsInStatusCompleted() throws Throwable {
        Method method = ReflectionUtils.findMethod(FooClass.class, "completedFuture", int.class)
                .orElseThrow(() -> new IllegalStateException("Method doesn't exist"));
        CompletableFuture<Integer> fooResult = this.fooInstance.completedFuture(100);

        when(this.proceedingJoinPoint.getSignature()).thenReturn(this.methodSignature);
        when(this.proceedingJoinPoint.proceed()).thenReturn(fooResult);
        when(this.methodSignature.getDeclaringType()).thenReturn(method.getDeclaringClass());
        when(this.methodSignature.getReturnType()).thenReturn(method.getReturnType());
        when(this.methodSignature.getParameterTypes()).thenReturn(method.getParameterTypes());
        when(this.methodSignature.getMethod()).thenReturn(method);
        when(this.methodSignature.getName()).thenReturn(method.getName());

        Object result = this.asyncTimeTracker.bind(this.proceedingJoinPoint);

        assertInstanceOf(CompletableFuture.class, result);
        assertEquals(fooResult.get(), ((CompletableFuture<?>) result).get());

        ArgumentCaptor<TrackTimeStat> captor = ArgumentCaptor.forClass(TrackTimeStat.class);
        verify(this.repository).save(captor.capture());
        TrackTimeStat stat = captor.getValue();

        assertNotNull(stat);
        assertEquals(method.getAnnotation(TrackAsyncTime.class).groupName(), stat.getGroupName());
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
    void handleAsync_whenCompletableFutureReturnsExceptionally_thenTrackerSavesMeasurementsInStatusException() throws Throwable {
        Method method = ReflectionUtils.findMethod(FooClass.class, "failedFuture")
                .orElseThrow(() -> new IllegalStateException("Method doesn't exist"));
        CompletableFuture<Void> fooResult = this.fooInstance.failedFuture();

        when(this.proceedingJoinPoint.getSignature()).thenReturn(this.methodSignature);
        when(this.proceedingJoinPoint.proceed()).thenReturn(fooResult);
        when(this.methodSignature.getDeclaringType()).thenReturn(method.getDeclaringClass());
        when(this.methodSignature.getReturnType()).thenReturn(method.getReturnType());
        when(this.methodSignature.getParameterTypes()).thenReturn(method.getParameterTypes());
        when(this.methodSignature.getMethod()).thenReturn(method);
        when(this.methodSignature.getName()).thenReturn(method.getName());

        Object result = this.asyncTimeTracker.bind(this.proceedingJoinPoint);

        assertInstanceOf(CompletableFuture.class, result);
        assertThrows(ExecutionException.class, () -> ((CompletableFuture<?>) result).get());

        ArgumentCaptor<TrackTimeStat> captor = ArgumentCaptor.forClass(TrackTimeStat.class);
        verify(this.repository).save(captor.capture());
        TrackTimeStat stat = captor.getValue();

        assertNotNull(stat);
        assertEquals(method.getAnnotation(TrackAsyncTime.class).groupName(), stat.getGroupName());
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
    void handleAsync_whenFutureReturnsNormally_thenTrackerSavesMeasurementsInStatusCompleted() throws Throwable {
        Method method = ReflectionUtils.findMethod(FooClass.class, "future")
                .orElseThrow(() -> new IllegalStateException("Method doesn't exist"));
        Future<Integer> fooResult = this.fooInstance.future();

        when(this.proceedingJoinPoint.getSignature()).thenReturn(this.methodSignature);
        when(this.proceedingJoinPoint.proceed()).thenReturn(fooResult);
        when(this.methodSignature.getDeclaringType()).thenReturn(method.getDeclaringClass());
        when(this.methodSignature.getReturnType()).thenReturn(method.getReturnType());
        when(this.methodSignature.getParameterTypes()).thenReturn(method.getParameterTypes());
        when(this.methodSignature.getMethod()).thenReturn(method);
        when(this.methodSignature.getName()).thenReturn(method.getName());

        Object result = this.asyncTimeTracker.bind(this.proceedingJoinPoint);

        assertInstanceOf(Future.class, result);
        assertEquals(fooResult.get(), ((Future<?>) result).get());

        ArgumentCaptor<TrackTimeStat> captor = ArgumentCaptor.forClass(TrackTimeStat.class);
        verify(this.repository).save(captor.capture());
        TrackTimeStat stat = captor.getValue();

        assertNotNull(stat);
        assertEquals(method.getAnnotation(TrackAsyncTime.class).groupName(), stat.getGroupName());
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
    void handleAsync_whenFutureReturnsExceptionally_thenTrackerSavesMeasurementsInStatusException() throws Throwable {
        Method method = ReflectionUtils.findMethod(FooClass.class, "throwableFuture")
                .orElseThrow(() -> new IllegalStateException("Method doesn't exist"));
        Future<Void> fooResult = this.fooInstance.throwableFuture();

        when(this.proceedingJoinPoint.getSignature()).thenReturn(this.methodSignature);
        when(this.proceedingJoinPoint.proceed()).thenReturn(fooResult);
        when(this.methodSignature.getDeclaringType()).thenReturn(method.getDeclaringClass());
        when(this.methodSignature.getReturnType()).thenReturn(method.getReturnType());
        when(this.methodSignature.getParameterTypes()).thenReturn(method.getParameterTypes());
        when(this.methodSignature.getMethod()).thenReturn(method);
        when(this.methodSignature.getName()).thenReturn(method.getName());

        Object result = this.asyncTimeTracker.bind(this.proceedingJoinPoint);

        assertInstanceOf(Future.class, result);
        assertThrows(ExecutionException.class, () -> ((Future<?>) result).get());

        ArgumentCaptor<TrackTimeStat> captor = ArgumentCaptor.forClass(TrackTimeStat.class);
        verify(this.repository).save(captor.capture());
        TrackTimeStat stat = captor.getValue();

        assertNotNull(stat);
        assertEquals(method.getAnnotation(TrackAsyncTime.class).groupName(), stat.getGroupName());
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
}