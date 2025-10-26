package com.fitracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutCompletionResponse {
    private LocalDate date;
    private Summary summary;
    private boolean adjustmentQueued;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private int completed;
        private int skipped;
        private int caloriesBurned;

    }

}
