package com.gymtracker.service;

import com.gymtracker.model.Member;
import com.gymtracker.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Member registerMember(Member member) {

        member.setEmail(member.getEmail().trim().toLowerCase(Locale.ROOT));
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setCreatedAt(LocalDateTime.now());

        return memberRepository.save(member);
    }

    public Member loginMember(String email, String password) {

        Member member = memberRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
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

    return updatedMember;
}

    public Map<String, Object> getMembership(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        Map<String, Object> response = new HashMap<>();
        response.put("memberId", member.getId());
        response.put("memberName", member.getName());
        response.put("email", member.getEmail());
        response.put("phone", member.getPhone());

        String plan = member.getMembershipPlan();
        String status = member.getMembershipStatus();
        LocalDate start = member.getMembershipStartDate();
        LocalDate end = member.getMembershipEndDate();

        if (status == null || "NONE".equalsIgnoreCase(status) || plan == null) {
            response.put("status", "INACTIVE");
            response.put("plan", null);
            response.put("planName", "No Active Plan");
            response.put("durationMonths", 0);
            response.put("startDate", null);
            response.put("endDate", null);
            response.put("daysRemaining", 0);
            response.put("price", 0.0);
            return response;
        }

        LocalDate today = LocalDate.now();
        if (end != null && end.isBefore(today)) {
            status = "EXPIRED";
        } else {
            status = "ACTIVE";
        }

        long daysRemaining = (end != null && !end.isBefore(today))
                ? ChronoUnit.DAYS.between(today, end)
                : 0;

        String planName = switch (plan.toUpperCase(Locale.ROOT)) {
            case "STARTER" -> "Starter Plan";
            case "PRO" -> "Pro Plan";
            case "ELITE" -> "Elite Plan";
            default -> plan + " Plan";
        };

        response.put("status", status);
        response.put("plan", plan);
        response.put("planName", planName);
        response.put("durationMonths", member.getMembershipDurationMonths() != null ? member.getMembershipDurationMonths() : 1);
        response.put("startDate", start != null ? start.toString() : null);
        response.put("endDate", end != null ? end.toString() : null);
        response.put("daysRemaining", daysRemaining);
        response.put("price", member.getMembershipPrice() != null ? member.getMembershipPrice() : 0.0);

        return response;
    }

    public Map<String, Object> updateMembership(Long memberId, String plan, Integer durationMonths, Double price) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        if (plan == null || plan.trim().isEmpty()) {
            throw new IllegalArgumentException("Membership plan is required.");
        }

        String normalizedPlan = plan.trim().toUpperCase(Locale.ROOT);
        if (!normalizedPlan.equals("STARTER") && !normalizedPlan.equals("PRO") && !normalizedPlan.equals("ELITE")) {
            throw new IllegalArgumentException("Invalid plan. Choose STARTER, PRO, or ELITE.");
        }

        int months = durationMonths != null && durationMonths > 0 ? durationMonths : (
                normalizedPlan.equals("STARTER") ? 1 : (normalizedPlan.equals("PRO") ? 3 : 6)
        );

        double planPrice = price != null && price > 0 ? price : (
                normalizedPlan.equals("STARTER") ? 999.0 : (normalizedPlan.equals("PRO") ? 1999.0 : 3499.0)
        );

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(months);

        member.setMembershipPlan(normalizedPlan);
        member.setMembershipStatus("ACTIVE");
        member.setMembershipDurationMonths(months);
        member.setMembershipStartDate(startDate);
        member.setMembershipEndDate(endDate);
        member.setMembershipPrice(planPrice);

        memberRepository.save(member);
        return getMembership(memberId);
    }
}