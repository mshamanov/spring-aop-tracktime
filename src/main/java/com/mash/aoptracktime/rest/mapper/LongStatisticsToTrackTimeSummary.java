package com.mash.aoptracktime.rest.mapper;

import com.mash.aoptracktime.rest.model.TrackTimeSummary;
import org.springframework.stereotype.Component;

import java.util.LongSummaryStatistics;
import java.util.function.Function;

@Component
public class LongStatisticsToTrackTimeSummary implements Function<LongSummaryStatistics, TrackTimeSummary> {
    @Override
    public TrackTimeSummary apply(LongSummaryStatistics statistics) {
        long count = statistics.getCount();
        long min = statistics.getMin();
        long max = statistics.getMax();
        double average = statistics.getAverage();

        return new TrackTimeSummary(count, min == Long.MAX_VALUE ? 0 : min, max == Long.MIN_VALUE ? 0 : max, average);
    }
}
