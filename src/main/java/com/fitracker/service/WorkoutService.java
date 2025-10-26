package com.fitracker.service;

import com.fitracker.dto.*;
import com.fitracker.entity.*;
import com.fitracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
public class WorkoutService {
    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanDayRepository planDayRepository;

    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;

    @Autowired
    private CoefficientRepository coefficientRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserService userService;

    public WorkoutDayResponse getWorkoutForDate(User user, LocalDate date) {
        LocalDate weekStart = date.with(DayOfWeek.MONDAY);

        UUID planId = planRepository.findIdByUserIdAndWeekStart(user.getId(), weekStart)
                .orElseThrow(() -> new IllegalStateException("No plan found for this week"));

        UUID dayId = planDayRepository.findIdByPlanIdAndDate(planId, date)
                .orElseThrow(() -> new IllegalStateException("No workout scheduled for this day"));

        List<SessionExercise> sessionExercises = sessionExerciseRepository.findByPlanDayId(dayId);

        List<SessionExerciseDto> dtos = sessionExercises.stream()
                .map(se -> {
                    Exercise e = exerciseRepository.findById(se.getExerciseId())
                            .orElseThrow(() -> new IllegalStateException("Exercise not found: " + se.getExerciseId()));

                    return new SessionExerciseDto(
                            se.getId(),
                            se.getExerciseId(),
                            e.getName(),
                            e.getMuscleGroup(),
                            se.getSets(),
                            se.getReps(),
                            se.getWeightKg(),
                            se.getEstimatedCalories(),
                            se.getStatus()
                    );
                })
                .toList();

        return new WorkoutDayResponse(date, planId, dtos);
    }


    public WorkoutCompletionResponse completeWorkoutDay(WorkoutCompletionRequest request, Authentication authentication) {
        int completed = 0;
        int skipped = 0;
        int calories = 0;

        Map<String, Double> deltaByGroup = new HashMap<>();

        for (ExerciseResultDto result : request.getResults()) {
            UUID sessionId = result.getSessionExerciseId();
            String status = result.getStatus().toLowerCase();

            SessionExercise session = sessionExerciseRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalStateException("Session exercise not found: " + sessionId));

            session.setStatus(status);
            sessionExerciseRepository.save(session);

            Exercise exercise = exerciseRepository.findById(session.getExerciseId())
                    .orElseThrow(() -> new IllegalStateException("Exercise not found: " + session.getExerciseId()));

            String group = exercise.getMuscleGroup();

            if ("done".equals(status)) {
                completed++;
                calories += session.getEstimatedCalories();
                deltaByGroup.merge(group, +0.05, Double::sum);
            } else if ("skipped".equals(status)) {
                skipped++;
                deltaByGroup.merge(group, -0.03, Double::sum);
            }
        }

        var user = userService.getCurrentUser(authentication);
        // updating coefficients
        for (Map.Entry<String, Double> entry : deltaByGroup.entrySet()) {
            String group = entry.getKey();
            double delta = entry.getValue();

            Coefficient coeff = coefficientRepository.findByUserIdAndMuscleGroup(user.getId(), group)
                    .orElseGet(() -> new Coefficient(user.getId(), group, 1.0));

            double updated = Math.max(0.5, Math.min(2.0, coeff.getValue() + delta));
            coeff.setValue(updated);
            coefficientRepository.save(coeff);
        }

        WorkoutCompletionResponse.Summary summary = new WorkoutCompletionResponse.Summary(completed, skipped, calories);
        return new WorkoutCompletionResponse(request.getDate(), summary, true);
    }
}
