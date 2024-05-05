package com.mash.aoptracktime.service;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.repository.TrackTimeStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TrackTimeStatsService {
    private final TrackTimeStatsRepository repository;

    @Transactional
    public TrackTimeStat save(TrackTimeStat stats) {
        return this.repository.save(stats);
    }

    @Async
    @Transactional
    public CompletableFuture<TrackTimeStat> saveAsync(TrackTimeStat stats) {
        return CompletableFuture.completedFuture(this.save(stats));
    }

    public List<TrackTimeStat> findAll() {
        return this.repository.findAll();
    }

    public List<TrackTimeStat> findAll(Specification<TrackTimeStat> specification) {
        return this.repository.findAll(specification);
    }

    public void deleteAll() {
        this.repository.deleteAll();
    }
}