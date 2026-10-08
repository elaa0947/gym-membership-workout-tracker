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

    // =========================
    // VIEW MEMBER PROFILE
    // =========================

    public Member getMemberProfile(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Member not found.")
                );

        // Never return password
        member.setPassword(null);

        return member;
    }

    // =========================
    // UPDATE MEMBER PROFILE
    // =========================

    public Member updateMemberProfile(
            Long id,
            String name,
            String phone) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Member not found.")
                );

        member.setName(name);
        member.setPhone(phone);

        Member updatedMember = memberRepository.save(member);

        // Never return password
        updatedMember.setPassword(null);

        return updatedMember;
    }
public Member completeOnboarding(
        Long id,
        Double height,
        Double weight,
        String fitnessGoal) {

    Member member =
            memberRepository.findById(id)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Member not found."
                            ));

    if (height == null || height <= 0) {
        throw new IllegalArgumentException(
                "Height must be greater than 0."
        );
    }

    if (weight == null || weight <= 0) {
        throw new IllegalArgumentException(
                "Weight must be greater than 0."
        );
    }

    if (fitnessGoal == null ||
            fitnessGoal.trim().isEmpty()) {

        throw new IllegalArgumentException(
                "Fitness goal is required."
        );
    }

    member.setHeight(height);
    member.setWeight(weight);
    member.setFitnessGoal(fitnessGoal);
    member.setOnboardingCompleted(true);

    Member updatedMember =
            memberRepository.save(member);

    updatedMember.setPassword(null);

    return updatedMember;
}
}