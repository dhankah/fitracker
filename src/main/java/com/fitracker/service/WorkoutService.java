package com.fitracker.service;

import com.fitracker.dto.*;
import com.fitracker.entity.*;
import com.fitracker.exception.NotFoundException;
import com.fitracker.mapper.SessionExerciseMapper;
import com.fitracker.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class WorkoutService {
    private PlanRepository planRepository;

    private PlanDayRepository planDayRepository;

    private SessionExerciseRepository sessionExerciseRepository;

    private CoefficientRepository coefficientRepository;

    private ExerciseRepository exerciseRepository;

    private UserService userService;

    private SessionExerciseMapper sessionExerciseMapper;

    public WorkoutDayResponse getWorkoutForDate(User user, LocalDate date) {
        LocalDate weekStart = date.with(DayOfWeek.MONDAY);

        UUID planId = planRepository.findIdByUserIdAndWeekStart(user.getId(), weekStart)
                .orElseThrow(() -> new NotFoundException("No plan found for this week"));

        UUID dayId = planDayRepository.findIdByPlanIdAndDate(planId, date)
                .orElseThrow(() -> new NotFoundException("No workout scheduled for this day"));

        List<SessionExercise> sessionExercises = sessionExerciseRepository.findByPlanDayId(dayId);

        List<SessionExerciseDto> dtos = sessionExercises.stream()
                .map(se -> sessionExerciseMapper.mapToDto(se))
                .toList();

        return new WorkoutDayResponse(date, planId, dtos);
    }


    public WorkoutCompletionResponse completeWorkoutDay(WorkoutCompletionRequest request, Authentication auth) {
        int completed = 0;
        int skipped = 0;
        int calories = 0;

        Map<String, Double> deltas = new HashMap<>();
        UUID userId = userService.getCurrentUser(auth).getId();

        for (ExerciseResultDto result : request.getResults()) {
            var session = sessionExerciseRepository.findById(result.getSessionExerciseId())
                    .orElseThrow(() -> new NotFoundException("Session exercise not found: " + result.getSessionExerciseId()));

            session.setStatus(result.getStatus().toLowerCase());
            sessionExerciseRepository.save(session);

            var exercise = exerciseRepository.findById(session.getExerciseId())
                    .orElseThrow(() -> new NotFoundException("Exercise not found: " + session.getExerciseId()));

            String group = exercise.getMuscleGroup();
            String status = session.getStatus();

            switch (status) {
                case "done":
                    completed++;
                    calories += session.getEstimatedCalories();
                    deltas.merge(group, 0.05, Double::sum);
                    break;
                case "skipped":
                    skipped++;
                    deltas.merge(group, -0.03, Double::sum);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + status);
            }
        }

        for (var entry : deltas.entrySet()) {
            var coeff = coefficientRepository.findByUserIdAndMuscleGroup(userId, entry.getKey())
                    .orElseGet(() -> new Coefficient(userId, entry.getKey(), 1.0));

            double updated = Math.max(0.5, Math.min(2.0, coeff.getValue() + entry.getValue()));
            coeff.setValue(updated);
            coefficientRepository.save(coeff);
        }

        var summary = new WorkoutCompletionResponse.Summary(completed, skipped, calories);
        return new WorkoutCompletionResponse(request.getDate(), summary);
    }
}
