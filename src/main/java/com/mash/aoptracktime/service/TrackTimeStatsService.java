package com.mash.aoptracktime.service;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.repository.TrackTimeStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackTimeStatsService {
    private final TrackTimeStatsRepository trackTimeStatsRepository;

    @Transactional
    public void save(TrackTimeStat stats) {
        this.trackTimeStatsRepository.save(stats);
    }

    @Async
    @Transactional
    public void saveAsync(TrackTimeStat stats) {
        this.save(stats);
    }

    public List<TrackTimeStat> findAll(Specification<TrackTimeStat> specification) {
        return this.trackTimeStatsRepository.findAll(specification);
    }
}