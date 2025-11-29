package com.fitracker.service;

import com.fitracker.dto.*;
import com.fitracker.entity.Coefficient;
import com.fitracker.entity.Exercise;
import com.fitracker.entity.SessionExercise;
import com.fitracker.entity.User;
import com.fitracker.mapper.SessionExerciseMapper;
import com.fitracker.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @InjectMocks
    private WorkoutService workoutService;

    @Mock
    private UserService userService;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PlanDayRepository planDayRepository;

    @Mock
    private SessionExerciseRepository sessionExerciseRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private CoefficientRepository coefficientRepository;

    @Mock
    private SessionExerciseMapper sessionExerciseMapper;

    @Mock
    Authentication authentication;

    @Test
    void getWorkoutForDate_shouldReturnExercisesForSpecificDate() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        UUID dayId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2025, 10, 21);
        LocalDate weekStart = date.with(DayOfWeek.MONDAY);

        User user = new User();
        user.setId(userId);

        SessionExercise session = new SessionExercise();
        session.setId(sessionId);
        session.setExerciseId("deadlift-1");
        session.setSets(4);
        session.setReps(6);
        session.setWeightKg(80.0);
        session.setEstimatedCalories(120);
        session.setStatus("pending");

        // Expected DTO after mapping
        SessionExerciseDto dto = new SessionExerciseDto();
        dto.setExerciseId("deadlift-1");
        dto.setName("Deadlift");
        dto.setMuscleGroup("back");
        dto.setStatus("pending");
        dto.setSets(4);
        dto.setReps(6);
        dto.setWeightKg(80.0);
        dto.setEstimatedCalories(120);

        when(planRepository.findIdByUserIdAndWeekStart(userId, weekStart)).thenReturn(Optional.of(planId));
        when(planDayRepository.findIdByPlanIdAndDate(planId, date)).thenReturn(Optional.of(dayId));
        when(sessionExerciseRepository.findByPlanDayId(dayId)).thenReturn(List.of(session));
        when(sessionExerciseMapper.mapToDto(session)).thenReturn(dto); // ✅ mock mapper, not exerciseRepository

        // When
        WorkoutDayResponse response = workoutService.getWorkoutForDate(user, date);

        // Then
        assertEquals(date, response.getDate());
        assertEquals(planId, response.getPlanId());
        assertEquals(1, response.getExercises().size());

        SessionExerciseDto actualDto = response.getExercises().get(0);
        assertEquals("deadlift-1", actualDto.getExerciseId());
        assertEquals("Deadlift", actualDto.getName());
        assertEquals("back", actualDto.getMuscleGroup());
        assertEquals("pending", actualDto.getStatus());
        assertEquals(4, actualDto.getSets());
        assertEquals(6, actualDto.getReps());
        assertEquals(80.0, actualDto.getWeightKg());
        assertEquals(120, actualDto.getEstimatedCalories());
    }

    @Test
    void completeWorkoutDay_shouldCountCompletedAndSkippedCorrectly() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID sessionId1 = UUID.randomUUID();
        UUID sessionId2 = UUID.randomUUID();
        UUID sessionId3 = UUID.randomUUID();

        var user = new User();
        user.setId(userId);
        when(userService.getCurrentUser(authentication)).thenReturn(user);

        WorkoutCompletionRequest request = new WorkoutCompletionRequest();
        request.setDate(LocalDate.of(2025, 10, 21));
        request.setResults(List.of(
                new ExerciseResultDto(sessionId1, "done"),
                new ExerciseResultDto(sessionId2, "skipped"),
                new ExerciseResultDto(sessionId3, "done")
        ));

        SessionExercise se1 = new SessionExercise();
        se1.setId(sessionId1);
        se1.setExerciseId("pushups-1");
        se1.setEstimatedCalories(160);
        se1.setStatus("pending");

        SessionExercise se2 = new SessionExercise();
        se2.setId(sessionId2);
        se2.setExerciseId("plank-1");
        se2.setEstimatedCalories(30);
        se2.setStatus("pending");

        SessionExercise se3 = new SessionExercise();
        se3.setId(sessionId3);
        se3.setExerciseId("squat-1");
        se3.setEstimatedCalories(160);
        se3.setStatus("pending");

        Exercise ex1 = new Exercise();
        ex1.setId("pushups-1");
        ex1.setMuscleGroup("chest");

        Exercise ex2 = new Exercise();
        ex2.setId("plank-1");
        ex2.setMuscleGroup("core");

        Exercise ex3 = new Exercise();
        ex3.setId("squat-1");
        ex3.setMuscleGroup("legs");

        when(sessionExerciseRepository.findById(sessionId1)).thenReturn(Optional.of(se1));
        when(sessionExerciseRepository.findById(sessionId2)).thenReturn(Optional.of(se2));
        when(sessionExerciseRepository.findById(sessionId3)).thenReturn(Optional.of(se3));

        when(exerciseRepository.findById("pushups-1")).thenReturn(Optional.of(ex1));
        when(exerciseRepository.findById("plank-1")).thenReturn(Optional.of(ex2));
        when(exerciseRepository.findById("squat-1")).thenReturn(Optional.of(ex3));

        when(coefficientRepository.findByUserIdAndMuscleGroup(userId, "chest"))
                .thenReturn(Optional.of(new Coefficient(userId, "chest", 1.0)));
        when(coefficientRepository.findByUserIdAndMuscleGroup(userId, "core"))
                .thenReturn(Optional.of(new Coefficient(userId, "core", 1.0)));
        when(coefficientRepository.findByUserIdAndMuscleGroup(userId, "legs"))
                .thenReturn(Optional.of(new Coefficient(userId, "legs", 1.0)));

        // When
        WorkoutCompletionResponse response = workoutService.completeWorkoutDay(request, authentication);

        // Then
        assertEquals(2, response.getSummary().getCompleted());
        assertEquals(1, response.getSummary().getSkipped());
        assertEquals(320, response.getSummary().getCaloriesBurned());
        assertEquals(LocalDate.of(2025, 10, 21), response.getDate());

        verify(sessionExerciseRepository).save(se1);
        verify(sessionExerciseRepository).save(se2);
        verify(sessionExerciseRepository).save(se3);
        verify(coefficientRepository).save(argThat(c ->
                c.getMuscleGroup().equals("chest") && c.getValue() == 1.05));
        verify(coefficientRepository).save(argThat(c ->
                c.getMuscleGroup().equals("core") && c.getValue() == 0.97));
        verify(coefficientRepository).save(argThat(c ->
                c.getMuscleGroup().equals("legs") && c.getValue() == 1.05));
    }

}