package com.mash.aoptracktime.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class, a relational representation of the data in the database,
 * used to store method execution time measurements.
 *
 * @author Mikhail Shamanov
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tracktimestats")
public class TrackTimeStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Column(name = "return_type", nullable = false)
    private String returnType;

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "method_name", nullable = false)
    private String methodName;

    @Column(name = "parameters", nullable = false)
    private String parameters;

    @Column(name = "execution_time", nullable = false)
    private Long executionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "method_status", nullable = false)
    private TrackTimeMethodStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}