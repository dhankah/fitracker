package com.fitracker.service;

import com.fitracker.dto.ExerciseDto;
import com.fitracker.dto.PlanDayDto;
import com.fitracker.dto.WeeklyPlanResponse;
import com.fitracker.entity.*;
import com.fitracker.repository.ExerciseRepository;
import com.fitracker.repository.PlanDayRepository;
import com.fitracker.repository.PlanRepository;
import com.fitracker.repository.SessionExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Collections;

@Service
public class PlanService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanDayRepository planDayRepository;

    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;

    public WeeklyPlanResponse generatePlanForUser(User user) {
        UUID planId = UUID.randomUUID();
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);

        List<String> targetGroups = getTargetMuscleGroups(user.getGoal());
        List<Exercise> pool = exerciseRepository.findByMuscleGroupIn(targetGroups);

        Plan planEntity = new Plan();
        planEntity.setId(planId);
        planEntity.setUserId(user.getId());
        planEntity.setWeekStart(weekStart);

        planRepository.save(planEntity);

        Random random = new Random();
        List<PlanDayDto> dayDtos = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            UUID dayId = UUID.randomUUID();

            PlanDay dayEntity = new PlanDay();
            dayEntity.setId(dayId);
            dayEntity.setDate(date);
            dayEntity.setPlan(planEntity);

            planDayRepository.save(dayEntity);

            List<ExerciseDto> exerciseDtos = new ArrayList<>();

            if (i % 2 == 0) {
                List<Exercise> selected = pickRandom(pool, 3, random);
                for (Exercise e : selected) {
                    SessionExercise se = new SessionExercise();
                    se.setId(UUID.randomUUID());
                    se.setPlanDayId(dayId);
                    se.setExerciseId(e.getId());
                    se.setSets(adjustSets(e.getBaseSets(), user));
                    se.setReps(adjustReps(e.getBaseReps(), user));
                    se.setWeightKg(adjustWeight(e.getBaseWeightKg(), user));
                    se.setEstimatedCalories(e.getBaseCalories());
                    se.setStatus("pending");

                    sessionExerciseRepository.save(se);

                    exerciseDtos.add(new ExerciseDto(
                            e.getId(),
                            e.getName(),
                            e.getMuscleGroup(),
                            se.getSets(),
                            se.getReps(),
                            se.getWeightKg(),
                            se.getEstimatedCalories()
                    ));
                }
            }

            dayDtos.add(new PlanDayDto(date, exerciseDtos));
        }

        return new WeeklyPlanResponse(planId, weekStart, dayDtos);
    }


    private List<String> getTargetMuscleGroups(String goal) {
        return switch (goal.toLowerCase()) {
            case "gain" -> List.of("chest", "back", "legs", "arms");
            case "lose" -> List.of("legs", "core", "cardio");
            default -> List.of("fullbody");
        };
    }

    private List<Exercise> pickRandom(List<Exercise> pool, int count, Random random) {
        List<Exercise> mutablePool = new ArrayList<>(pool);
        Collections.shuffle(mutablePool, random);
        return pool.stream().limit(count).toList();
    }

    private int adjustSets(int base, User user) {
        return base + (user.getGoal().equals("gain") ? 1 : 0);
    }

    private int adjustReps(int base, User user) {
        return base + (user.getGoal().equals("endurance") ? 4 : 0);
    }

    private double adjustWeight(double base, User user) {
        return user.getGoal().equals("gain") ? base * 1.2 : base;
    }
}
