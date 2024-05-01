package com.mash.aoptracktime.rest.mapper;

import com.mash.aoptracktime.entity.TrackTimeStat;
import com.mash.aoptracktime.rest.model.TrackTimeDto;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TrackTimeRequestToSpecificationMapper implements Function<TrackTimeDto, Specification<TrackTimeStat>> {
    private final TrackTimeDtoToEntityMapper toEntityMapper;

    @Override
    public Specification<TrackTimeStat> apply(TrackTimeDto requestDto) {
        TrackTimeStat trackTimeStat = this.toEntityMapper.apply(requestDto);

        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            ReflectionUtils.doWithLocalFields(TrackTimeStat.class, (field) -> {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = ReflectionUtils.getField(field, trackTimeStat);

                Predicate predicate = null;

                if (value instanceof String str) {
                    if (str.contains("*")) {
                        str = str.replaceAll("\\*", "%");
                        predicate = builder.like(root.get(fieldName), str);
                    }
                }

                if (this.isCreatedAtField(field)) {
                    LocalDate startDate = requestDto.getStartDate();
                    LocalDate endDate = requestDto.getEndDate();

                    if (startDate != null && endDate != null) {
                        predicate = builder.between(root.get(fieldName).as(LocalDate.class), startDate, endDate);
                    } else if (startDate != null) {
                        predicate = builder.greaterThanOrEqualTo(root.get(fieldName)
                                .as(LocalDate.class), startDate);
                    } else if (endDate != null) {
                        predicate = builder.lessThanOrEqualTo(root.get(fieldName).as(LocalDate.class), endDate);
                    } else if (value != null) {
                        predicate = builder.equal(root.get(fieldName).as(LocalDate.class), value);
                    }
                }

                if (value != null && predicate == null) {
                    predicate = builder.equal(root.get(fieldName), value);
                }

                if (predicate != null) {
                    predicates.add(predicate);
                }
            });

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private boolean isCreatedAtField(Field field) {
        return field.getName().equals("createdAt") ||
                field.getName().equals("created_at") ||
                field.isAnnotationPresent(CreationTimestamp.class) ||
                field.isAnnotationPresent(CreatedDate.class);
    }
}
