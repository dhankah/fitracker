package com.fitracker.service;

import com.fitracker.dto.ExerciseDto;
import com.fitracker.dto.PlanDayDto;
import com.fitracker.dto.WeeklyPlanResponse;
import com.fitracker.entity.Exercise;
import com.fitracker.entity.User;
import com.fitracker.repository.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {
    @InjectMocks
    private PlanService planService;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Test
    void generatePlanForUser_shouldCreate7DayPlanWithExercises() {
        // Given
        User user = new User();
        user.setGoal("gain");

        List<Exercise> mockExercises = List.of(
                new Exercise("pushups-1", "Push-ups", "chest", 3, 12, 0.0, 40),
                new Exercise("squat-1", "Squat", "legs", 4, 10, 60.0, 100),
                new Exercise("deadlift-1", "Deadlift", "back", 4, 6, 80.0, 120),
                new Exercise("curl-1", "Bicep Curl", "arms", 3, 10, 15.0, 50)
        );

        when(exerciseRepository.findByMuscleGroupIn(List.of("chest", "back", "legs", "arms")))
                .thenReturn(mockExercises);

        // When
        WeeklyPlanResponse response = planService.generatePlanForUser(user);

        // Then
        assertNotNull(response);
        assertEquals(7, response.getDays().size());

        long trainingDays = response.getDays().stream()
                .filter(day -> !day.getExercises().isEmpty())
                .count();

        assertEquals(4, trainingDays);

        for (PlanDayDto day : response.getDays()) {
            for (ExerciseDto ex : day.getExercises()) {
                assertTrue(List.of("chest", "back", "legs", "arms").contains(ex.getMuscleGroup()));
                assertTrue(ex.getSets() >= 3); // adjusted sets
            }
        }
    }

}