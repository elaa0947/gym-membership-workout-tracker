package com.gymtracker.controller;

import com.gymtracker.model.Member;
import com.gymtracker.service.MemberService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @PostMapping("/register")
    public ResponseEntity<?> registerMember(
            @Valid @RequestBody Member member) {

        try {

            Member registeredMember =
                    memberService.registerMember(member);

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


    @PostMapping("/login")
    public ResponseEntity<?> loginMember(
            @Valid @RequestBody LoginRequest loginRequest) {

        try {

            Member member =
                    memberService.loginMember(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    );

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
}