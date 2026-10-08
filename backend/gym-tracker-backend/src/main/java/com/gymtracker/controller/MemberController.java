package com.gymtracker.controller;

import com.gymtracker.model.Member;
import com.gymtracker.service.MemberService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    // =========================
    // MEMBER REGISTRATION
    // =========================

    @PostMapping("/register")
    public ResponseEntity<?> registerMember(
            @Valid @RequestBody Member member) {

        try {

            Member registeredMember =
                    memberService.registerMember(member);

            // Never return password to frontend
            registeredMember.setPassword(null);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(registeredMember);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body("{\"message\":\""
                            + e.getMessage()
                            + "\"}");
        }
    }


    // =========================
    // MEMBER LOGIN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<?> loginMember(
            @Valid @RequestBody LoginRequest loginRequest) {

        try {

            Member member =
                    memberService.loginMember(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    );

            // Never return password to frontend
            member.setPassword(null);

            return ResponseEntity.ok(member);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\":\""
                            + e.getMessage()
                            + "\"}");
        }
    }


    // =========================
    // VIEW MEMBER PROFILE
    // =========================

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getMemberProfile(
            @PathVariable Long id) {

        try {

            Member member =
                    memberService.getMemberProfile(id);

            return ResponseEntity.ok(member);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"message\":\""
                            + e.getMessage()
                            + "\"}");
        }
    }


    // =========================
    // UPDATE MEMBER PROFILE
    // =========================

    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateMemberProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {

        try {

            Member updatedMember =
                    memberService.updateMemberProfile(
                            id,
                            request.getName(),
                            request.getPhone()
                    );

            return ResponseEntity.ok(updatedMember);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"message\":\""
                            + e.getMessage()
                            + "\"}");
        }
    }


    // =========================
    // MEMBER ONBOARDING
    // =========================

    @PutMapping("/onboarding/{id}")
    public ResponseEntity<?> completeOnboarding(
            @PathVariable Long id,
            @RequestBody OnboardingRequest request) {

        try {

            Member member =
                    memberService.completeOnboarding(
                            id,
                            request.getHeight(),
                            request.getWeight(),
                            request.getFitnessGoal()
                    );

            return ResponseEntity.ok(member);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body("{\"message\":\""
                            + e.getMessage()
                            + "\"}");
        }
    }


    // =========================
    // UPDATE PROFILE REQUEST
    // =========================

    public static class UpdateProfileRequest {

        @NotBlank(message = "Name is required")
        @Size(
                min = 2,
                message = "Name must contain at least 2 characters"
        )
        private String name;

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[6-9]\\d{9}$",
                message = "Enter a valid 10-digit phone number"
        )
        private String phone;

        public UpdateProfileRequest() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }


    // =========================
    // LOGIN REQUEST
    // =========================

    public static class LoginRequest {

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                message = "Password must contain at least 8 characters"
        )
        private String password;

        public LoginRequest() {
        }

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


    // =========================
    // ONBOARDING REQUEST
    // =========================

    public static class OnboardingRequest {

        private Double height;
        private Double weight;
        private String fitnessGoal;

        public OnboardingRequest() {
        }

        public Double getHeight() {
            return height;
        }

        public void setHeight(Double height) {
            this.height = height;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public String getFitnessGoal() {
            return fitnessGoal;
        }

        public void setFitnessGoal(String fitnessGoal) {
            this.fitnessGoal = fitnessGoal;
        }
    }
}