package com.mash.aoptracktime.repository;

import com.mash.aoptracktime.entity.TrackTimeStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackTimeStatsRepository extends JpaRepository<TrackTimeStat, Long>, JpaSpecificationExecutor<TrackTimeStat> {
}