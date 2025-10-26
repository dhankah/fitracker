package com.fitracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyPlanResponse {
    private UUID planId;
    private LocalDate weekStart;
    private List<PlanDayDto> days;

}
