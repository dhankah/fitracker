package com.fitracker.controller;

import com.fitracker.dto.WeeklyPlanResponse;
import com.fitracker.service.PlanService;
import com.fitracker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/plans")
public class PlanController {

    private PlanService planService;

    private UserService userService;

    @PostMapping("/generate")
    public ResponseEntity<WeeklyPlanResponse> generatePlan(Authentication authentication) {
        var user = userService.getCurrentUser(authentication);
        WeeklyPlanResponse response = planService.generatePlanForUser(user);
        return ResponseEntity.ok(response);
    }
}
