package com.fitracker.mapper;

import com.fitracker.dto.ExerciseDto;
import com.fitracker.dto.SessionExerciseDto;
import com.fitracker.entity.Exercise;
import com.fitracker.entity.SessionExercise;
import com.fitracker.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SessionExerciseMapper {

    @Mapping(target = "sessionExerciseId", source = "id")
    @Mapping(target = "name", source = "exercise.name")
    @Mapping(target = "muscleGroup", source = "exercise.muscleGroup")
    SessionExerciseDto mapToDto(SessionExercise sessionExercise);

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "planDayId", source = "dayId")
    @Mapping(target = "exerciseId", source = "exercise.id")
    @Mapping(target = "sets", expression = "java(adjustSets(exercise.getBaseSets(), user))")
    @Mapping(target = "reps", expression = "java(adjustReps(exercise.getBaseReps(), user))")
    @Mapping(target = "weightKg", expression = "java(adjustWeight(exercise.getBaseWeightKg(), user))")
    @Mapping(target = "estimatedCalories", source = "exercise.baseCalories")
    @Mapping(target = "status", constant = "pending")
    SessionExercise buildFromExercise(Exercise exercise, User user, UUID dayId);

    default int adjustSets(int base, User user) {
        return base + (user.getGoal().equals("gain") ? 1 : 0);
    }

    default int adjustReps(int base, User user) {
        return base + (user.getGoal().equals("endurance") ? 4 : 0);
    }

    default double adjustWeight(double base, User user) {
        return user.getGoal().equals("gain") ? base * 1.2 : base;
    }
}
