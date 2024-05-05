package com.mash.aoptracktime.service;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.repository.TrackTimeStatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackTimeStatsServiceTest {
    @Mock
    TrackTimeStatsRepository repository;

    @InjectMocks
    TrackTimeStatsService service;

    @Test
    void saveStat_shouldCallRepository() {
        TrackTimeStat trackTimeStat = mock(TrackTimeStat.class);
        when(this.repository.save(trackTimeStat)).thenReturn(trackTimeStat);

        TrackTimeStat saved = this.service.save(trackTimeStat);

        verify(this.repository).save(trackTimeStat);
        assertEquals(trackTimeStat, saved);
    }

    @Test
    void saveAsyncStat_shouldCallRepository() throws Exception {
        TrackTimeStat trackTimeStat = mock(TrackTimeStat.class);
        when(this.repository.save(trackTimeStat)).thenReturn(trackTimeStat);

        CompletableFuture<TrackTimeStat> completableFuture = this.service.saveAsync(trackTimeStat);

        verify(this.repository).save(trackTimeStat);
        assertEquals(trackTimeStat, completableFuture.get());
    }

    @Test
    void findAll_shouldCallRepository() {
        this.service.findAll();

        verify(this.repository).findAll();
    }

    @Test
    void findAllWithSpecification_shouldCallRepository() {
        Specification<TrackTimeStat> specification = Specification.anyOf();
        this.service.findAll(specification);

        verify(this.repository).findAll(specification);
    }
}