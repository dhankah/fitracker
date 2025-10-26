package com.fitracker.service;

import com.fitracker.dto.*;
import com.fitracker.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @InjectMocks
    private WorkoutService workoutService;


    @Test
    void getWorkoutForDate_shouldReturnExercisesForSpecificDate() {
        // Given
        User user = new User();
        LocalDate date = LocalDate.of(2025, 10, 21);

        // When
        WorkoutDayResponse response = workoutService.getWorkoutForDate(user, date);

        // Then
        assertEquals(date, response.getDate());
        assertNotNull(response.getPlanId());
        assertEquals(1, response.getExercises().size());

        SessionExerciseDto exercise = response.getExercises().get(0);
        assertEquals("deadlift-1", exercise.getExerciseId());
        assertEquals("Deadlift", exercise.getName());
        assertEquals("back", exercise.getMuscleGroup());
        assertEquals("pending", exercise.getStatus());
    }

    @Test
    void getWorkoutForDate_shouldReturnEmptyListForOtherDates() {
        // Given
        LocalDate date = LocalDate.of(2025, 10, 22);

        // When
        WorkoutDayResponse response = workoutService.getWorkoutForDate(new User(), date);

        // Then
        assertEquals(date, response.getDate());
        assertTrue(response.getExercises().isEmpty());
    }

    @Test
    void completeWorkoutDay_shouldCountCompletedAndSkippedCorrectly() {
        // Given
        WorkoutCompletionRequest request = new WorkoutCompletionRequest();
        request.setDate(LocalDate.of(2025, 10, 21));
        request.setResults(List.of(
                new ExerciseResultDto(UUID.randomUUID(), "done"),
                new ExerciseResultDto(UUID.randomUUID(), "skipped"),
                new ExerciseResultDto(UUID.randomUUID(), "done")
        ));

        // When
        WorkoutCompletionResponse response = workoutService.completeWorkoutDay(request);

        // Then
        assertEquals(2, response.getSummary().getCompleted());
        assertEquals(1, response.getSummary().getSkipped());
        assertEquals(320, response.getSummary().getCaloriesBurned());
        assertEquals(LocalDate.of(2025, 10, 21), response.getDate());
        assertTrue(response.isAdjustmentQueued());
    }

}