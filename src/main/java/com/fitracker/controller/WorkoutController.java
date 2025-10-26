package com.fitracker.controller;

import com.fitracker.dto.WorkoutCompletionRequest;
import com.fitracker.dto.WorkoutCompletionResponse;
import com.fitracker.dto.WorkoutDayResponse;
import com.fitracker.entity.User;
import com.fitracker.service.UserService;
import com.fitracker.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private UserService userService;

    @GetMapping("/day")
    public ResponseEntity<WorkoutDayResponse> getWorkoutForDay(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {

        User user = userService.getCurrentUser(authentication);

        WorkoutDayResponse response = workoutService.getWorkoutForDate(user, date);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/day/complete")
    public ResponseEntity<WorkoutCompletionResponse> completeWorkoutDay(Authentication authentication,
            @RequestBody WorkoutCompletionRequest request) {
        WorkoutCompletionResponse response = workoutService.completeWorkoutDay(request, authentication);
        return ResponseEntity.ok(response);
    }
}
