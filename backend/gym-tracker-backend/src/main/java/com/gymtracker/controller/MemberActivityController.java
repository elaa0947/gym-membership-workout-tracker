package com.gymtracker.controller;

import com.gymtracker.model.AttendanceRecord;
import com.gymtracker.model.WeightEntry;
import com.gymtracker.model.WorkoutSession;
import com.gymtracker.service.MemberActivityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members/{memberId}")
@Validated
public class MemberActivityController {

    private final MemberActivityService activityService;

    public MemberActivityController(MemberActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/attendance")
    public List<AttendanceRecord> getAttendance(@PathVariable Long memberId) {
        return activityService.getAttendance(memberId);
    }

    @PostMapping("/attendance")
    public ResponseEntity<AttendanceRecord> checkIn(
            @PathVariable Long memberId,
            @RequestBody(required = false) AttendanceRequest request) {
        LocalDate date = request == null || request.date() == null
                ? LocalDate.now() : request.date();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityService.checkIn(memberId, date));
    }

    @GetMapping("/workouts")
    public List<WorkoutSession> getWorkouts(@PathVariable Long memberId) {
        return activityService.getWorkouts(memberId);
    }

    @PostMapping("/workouts")
    public ResponseEntity<WorkoutSession> saveWorkout(
            @PathVariable Long memberId,
            @Valid @RequestBody WorkoutRequest request) {
        WorkoutSession session = activityService.saveWorkout(
                memberId, request.exercises(), request.date());
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @GetMapping("/weights")
    public List<WeightEntry> getWeights(@PathVariable Long memberId) {
        return activityService.getWeights(memberId);
    }

    @PostMapping("/weights")
    public ResponseEntity<WeightEntry> saveWeight(
            @PathVariable Long memberId,
            @Valid @RequestBody WeightRequest request) {
        WeightEntry entry = activityService.saveWeight(
                memberId, request.date(), request.weight());
        return ResponseEntity.ok(entry);
    }

    @GetMapping("/dashboard-summary")
    public Map<String, Object> getDashboardSummary(@PathVariable Long memberId) {
        return activityService.getDashboardSummary(memberId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidActivity(
            IllegalArgumentException exception) {
        HttpStatus status = "Member not found.".equals(exception.getMessage())
                ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(Map.of("message", exception.getMessage()));
    }

    public record AttendanceRequest(LocalDate date) {
    }

    public record WorkoutRequest(
            @NotNull LocalDate date,
            @NotEmpty @Size(max = 30) List<String> exercises) {
    }

    public record WeightRequest(
            @NotNull LocalDate date,
            @NotNull Double weight) {
    }
}
