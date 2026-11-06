package com.gymtracker.controller;

import com.gymtracker.model.AttendanceRecord;
import com.gymtracker.model.Member;
import com.gymtracker.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(
            @Valid @RequestBody AdminLoginRequest loginRequest,
            HttpServletRequest request) {
        try {
            Member admin = adminService.loginAdmin(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );

            HttpSession existing = request.getSession(false);
            if (existing != null) {
                existing.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("adminId", admin.getId());
            session.setAttribute("memberId", admin.getId());
            session.setAttribute("role", "ADMIN");

            return ResponseEntity.ok(admin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not signed in as administrator."));
        }
        Long adminId = (Long) session.getAttribute("adminId");
        return ResponseEntity.ok(Map.of("adminId", adminId, "role", "ADMIN"));
    }

    @GetMapping("/overview")
    public ResponseEntity<?> getOverview() {
        return ResponseEntity.ok(adminService.getOverviewStats());
    }

    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers() {
        return ResponseEntity.ok(adminService.getAllMembers());
    }

    @PostMapping("/members")
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(adminService.createMember(member));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<?> updateMember(
            @PathVariable Long id,
            @RequestBody AdminUpdateMemberRequest req) {
        try {
            return ResponseEntity.ok(adminService.updateMember(
                    id,
                    req.name(),
                    req.phone(),
                    req.fitnessGoal(),
                    req.role()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        try {
            adminService.deleteMember(id);
            return ResponseEntity.ok(Map.of("message", "Member and all related records removed."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/members/{id}/membership")
    public ResponseEntity<?> updateMembership(
            @PathVariable Long id,
            @RequestBody AdminMembershipRequest req) {
        try {
            return ResponseEntity.ok(adminService.updateMembership(
                    id,
                    req.plan(),
                    req.durationMonths(),
                    req.price(),
                    req.status()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/attendance")
    public ResponseEntity<?> getAttendance() {
        return ResponseEntity.ok(adminService.getAllAttendance());
    }

    @PostMapping("/attendance")
    public ResponseEntity<?> markAttendance(@RequestBody AdminAttendanceRequest req) {
        try {
            AttendanceRecord record = adminService.markAttendance(req.memberId(), req.date());
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/attendance/{id}")
    public ResponseEntity<?> deleteAttendance(@PathVariable Long id) {
        try {
            adminService.deleteAttendance(id);
            return ResponseEntity.ok(Map.of("message", "Attendance record deleted."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/workouts")
    public ResponseEntity<?> getWorkouts() {
        return ResponseEntity.ok(adminService.getAllWorkouts());
    }

    public static class AdminLoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password is required")
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public record AdminUpdateMemberRequest(
            String name,
            String phone,
            String fitnessGoal,
            String role) {
    }

    public record AdminMembershipRequest(
            String plan,
            Integer durationMonths,
            Double price,
            String status) {
    }

    public record AdminAttendanceRequest(
            Long memberId,
            LocalDate date) {
    }
}
