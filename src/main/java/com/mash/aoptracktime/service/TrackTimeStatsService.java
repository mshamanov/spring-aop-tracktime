package com.mash.aoptracktime.service;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.repository.TrackTimeStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackTimeStatsService {
    private final TrackTimeStatsRepository trackTimeStatsRepository;

    public void save(TrackTimeStat stats) {
        this.trackTimeStatsRepository.save(stats);
    }

    @Async
    public void saveAsync(TrackTimeStat stats) {
        this.save(stats);
    }
}