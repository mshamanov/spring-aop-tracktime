package com.mash.aoptracktime.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class Employee {
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
}