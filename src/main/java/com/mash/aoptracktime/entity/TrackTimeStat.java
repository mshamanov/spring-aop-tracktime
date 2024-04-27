package com.mash.aoptracktime.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tracktimestats")
public class TrackTimeStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "return_type")
    private String returnType;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "class_name")
    private String className;

    @Column(name = "method_name")
    private String methodName;

    @Column(name = "parameters")
    private String parameters;

    @Column(name = "execution_time")
    private Long executionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "method_status")
    private TrackTimeMethodStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}