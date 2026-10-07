package com.gymtracker.controller;

import com.gymtracker.model.Member;
import com.gymtracker.service.MemberService;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> registerMember(@Valid @RequestBody Member member) {

        try {
            Member registeredMember = memberService.registerMember(member);

            registeredMember.setPassword(null);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(registeredMember);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}