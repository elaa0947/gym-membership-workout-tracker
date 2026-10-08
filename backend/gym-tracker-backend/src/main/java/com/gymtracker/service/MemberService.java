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

    public Member loginMember(String email, String password) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password.")
                );

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return member;
    }
}