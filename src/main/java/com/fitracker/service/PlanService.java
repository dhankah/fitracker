package com.fitracker.service;

import com.fitracker.dto.ExerciseDto;
import com.fitracker.dto.PlanDayDto;
import com.fitracker.dto.WeeklyPlanResponse;
import com.fitracker.entity.*;
import com.fitracker.mapper.SessionExerciseMapper;
import com.fitracker.repository.ExerciseRepository;
import com.fitracker.repository.PlanDayRepository;
import com.fitracker.repository.PlanRepository;
import com.fitracker.repository.SessionExerciseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Collections;

@Service
@AllArgsConstructor
public class PlanService {

    private ExerciseRepository exerciseRepository;

    private PlanRepository planRepository;

    private PlanDayRepository planDayRepository;

    private SessionExerciseRepository sessionExerciseRepository;

    private SessionExerciseMapper sessionExerciseMapper;

    public WeeklyPlanResponse generatePlanForUser(User user) {
        UUID planId = UUID.randomUUID();
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);

        List<String> targetGroups = getTargetMuscleGroups(user.getGoal());
        List<Exercise> pool = exerciseRepository.findByMuscleGroupIn(targetGroups);

        Plan planEntity = Plan.builder()
                .id(planId)
                .userId(user.getId())
                .weekStart(weekStart)
                .build();

        planRepository.save(planEntity);

        Random random = new Random();
        List<PlanDayDto> dayDtos = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            UUID dayId = UUID.randomUUID();

            PlanDay dayEntity = PlanDay.builder()
                    .id(dayId)
                    .plan(planEntity)
                    .date(date)
                    .build();

            planDayRepository.save(dayEntity);

            List<ExerciseDto> exerciseDtos = new ArrayList<>();

            if (i % 2 == 0) {
                List<Exercise> selected = pickRandom(pool, 3, random);
                for (Exercise e : selected) {
                    var se = sessionExerciseMapper.buildFromExercise(e, user, dayId);

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
        return mutablePool.stream().limit(count).toList();
    }
}
