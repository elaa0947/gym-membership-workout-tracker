package com.gymtracker.service;

import com.gymtracker.model.Member;
import com.gymtracker.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Member registerMember(Member member) {

        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setCreatedAt(LocalDateTime.now());

        return memberRepository.save(member);
    }
}