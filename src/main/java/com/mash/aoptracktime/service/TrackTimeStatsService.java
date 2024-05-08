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

/**
 * Service to manipulate with method execution time measurements {@link TrackTimeStat}.
 *
 * @author Mikhail Shamanov
 */
@Service
@RequiredArgsConstructor
public class TrackTimeStatsService {
    private final TrackTimeStatsRepository repository;

    /**
     * Saves entity.
     *
     * @param stats measurement data
     * @return saved entity
     */
    @Transactional
    public TrackTimeStat save(TrackTimeStat stats) {
        return this.repository.save(stats);
    }

    /**
     * Saves entity asynchronously.
     *
     * @param stats measurement data
     * @return saved entity wrapped in CompletableFuture {@link CompletableFuture}
     */
    @Async
    @Transactional
    public CompletableFuture<TrackTimeStat> saveAsync(TrackTimeStat stats) {
        return CompletableFuture.completedFuture(this.save(stats));
    }

    /**
     * Returns the list of all entities.
     *
     * @return list of all entities
     */
    public List<TrackTimeStat> findAll() {
        return this.repository.findAll();
    }

    /**
     * Returns the list of entities by criteria {@link Specification}.
     *
     * @param specification custom criteria
     * @return list of entities by criteria
     */
    public List<TrackTimeStat> findAll(Specification<TrackTimeStat> specification) {
        return this.repository.findAll(specification);
    }

    /**
     * Deletes all entities.
     */
    public void deleteAll() {
        this.repository.deleteAll();
    }
}