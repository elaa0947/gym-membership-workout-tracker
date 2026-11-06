package com.gymtracker.service;

import com.gymtracker.model.AttendanceRecord;
import com.gymtracker.model.Member;
import com.gymtracker.model.WorkoutSession;
import com.gymtracker.repository.AttendanceRepository;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.WeightEntryRepository;
import com.gymtracker.repository.WorkoutSessionRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WeightEntryRepository weightEntryRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminService(
            MemberRepository memberRepository,
            AttendanceRepository attendanceRepository,
            WorkoutSessionRepository workoutSessionRepository,
            WeightEntryRepository weightEntryRepository) {
        this.memberRepository = memberRepository;
        this.attendanceRepository = attendanceRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.weightEntryRepository = weightEntryRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Member loginAdmin(String email, String password) {
        String cleanEmail = email.trim().toLowerCase(Locale.ROOT);
        Member member = memberRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid administrator credentials."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("Invalid administrator credentials.");
        }

        if (!"ADMIN".equalsIgnoreCase(member.getRole())) {
            throw new IllegalArgumentException("Access denied. This portal is restricted to authorized gym administrators.");
        }

        return member;
    }

    public Map<String, Object> getOverviewStats() {
        LocalDate today = LocalDate.now();
        List<Member> allMembers = memberRepository.findAllByOrderByCreatedAtDesc();
        List<Member> nonAdmins = allMembers.stream()
                .filter(m -> !"ADMIN".equalsIgnoreCase(m.getRole()))
                .collect(Collectors.toList());

        long totalMembers = nonAdmins.size();
        long activeMemberships = nonAdmins.stream()
                .filter(m -> "ACTIVE".equalsIgnoreCase(m.getMembershipStatus()))
                .count();
        long expiredMemberships = nonAdmins.stream()
                .filter(m -> "EXPIRED".equalsIgnoreCase(m.getMembershipStatus()))
                .count();
        long noneMemberships = totalMembers - activeMemberships - expiredMemberships;

        double totalRevenue = nonAdmins.stream()
                .filter(m -> "ACTIVE".equalsIgnoreCase(m.getMembershipStatus()))
                .mapToDouble(m -> m.getMembershipPrice() != null ? m.getMembershipPrice() : 0.0)
                .sum();

        long todayAttendance = attendanceRepository.countByDate(today);
        long totalWorkouts = workoutSessionRepository.count();
        long totalAttendance = attendanceRepository.count();

        List<Map<String, Object>> recentMembers = nonAdmins.stream()
                .limit(5)
                .map(this::mapMemberSummary)
                .collect(Collectors.toList());

        List<AttendanceRecord> recentAttendanceRecords = attendanceRepository.findAllByOrderByDateDesc();
        List<Map<String, Object>> recentAttendance = recentAttendanceRecords.stream()
                .limit(10)
                .map(this::mapAttendanceSummary)
                .collect(Collectors.toList());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMembers", totalMembers);
        stats.put("activeMemberships", activeMemberships);
        stats.put("expiredMemberships", expiredMemberships);
        stats.put("noneMemberships", noneMemberships);
        stats.put("totalRevenue", totalRevenue);
        stats.put("todayAttendance", todayAttendance);
        stats.put("totalWorkouts", totalWorkouts);
        stats.put("totalAttendance", totalAttendance);
        stats.put("recentMembers", recentMembers);
        stats.put("recentAttendance", recentAttendance);
        return stats;
    }

    public List<Map<String, Object>> getAllMembers() {
        return memberRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapMemberDetailed)
                .collect(Collectors.toList());
    }

    public Member createMember(Member member) {
        String cleanEmail = member.getEmail().trim().toLowerCase(Locale.ROOT);
        if (memberRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("A member with this email already exists.");
        }

        member.setEmail(cleanEmail);
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        if (member.getRole() == null || member.getRole().isBlank()) {
            member.setRole("MEMBER");
        }
        member.setCreatedAt(LocalDateTime.now());
        if (member.getMembershipStatus() == null) {
            member.setMembershipStatus("NONE");
        }
        return memberRepository.save(member);
    }

    public Member updateMember(Long id, String name, String phone, String fitnessGoal, String role) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        if (name != null && !name.isBlank()) member.setName(name.trim());
        if (phone != null && !phone.isBlank()) member.setPhone(phone.trim());
        if (fitnessGoal != null) member.setFitnessGoal(fitnessGoal.trim());
        if (role != null && !role.isBlank()) member.setRole(role.trim().toUpperCase());

        return memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        attendanceRepository.deleteAllByMemberId(id);
        workoutSessionRepository.deleteAllByMemberId(id);
        weightEntryRepository.deleteAllByMemberId(id);
        memberRepository.delete(member);
    }

    public Member updateMembership(
            Long memberId,
            String plan,
            Integer durationMonths,
            Double price,
            String status) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        if (plan != null && !plan.isBlank()) {
            member.setMembershipPlan(plan.trim().toUpperCase());
        }
        if (durationMonths != null && durationMonths > 0) {
            member.setMembershipDurationMonths(durationMonths);
            LocalDate start = LocalDate.now();
            member.setMembershipStartDate(start);
            member.setMembershipEndDate(start.plusMonths(durationMonths));
        }
        if (price != null) {
            member.setMembershipPrice(price);
        }
        if (status != null && !status.isBlank()) {
            member.setMembershipStatus(status.trim().toUpperCase());
        } else {
            member.setMembershipStatus("ACTIVE");
        }

        return memberRepository.save(member);
    }

    public List<Map<String, Object>> getAllAttendance() {
        return attendanceRepository.findAllByOrderByDateDesc().stream()
                .map(this::mapAttendanceSummary)
                .collect(Collectors.toList());
    }

    public AttendanceRecord markAttendance(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        if (attendanceRepository.existsByMemberIdAndDate(memberId, targetDate)) {
            throw new IllegalArgumentException("Member is already checked in for " + targetDate + ".");
        }

        return attendanceRepository.save(new AttendanceRecord(member, targetDate));
    }

    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new IllegalArgumentException("Attendance record not found.");
        }
        attendanceRepository.deleteById(id);
    }

    public List<Map<String, Object>> getAllWorkouts() {
        return workoutSessionRepository.findAllByOrderByDateDescSavedAtDesc().stream()
                .map(this::mapWorkoutSummary)
                .collect(Collectors.toList());
    }

    private Map<String, Object> mapMemberSummary(Member m) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", m.getId());
        map.put("name", m.getName());
        map.put("email", m.getEmail());
        map.put("membershipPlan", m.getMembershipPlan() != null ? m.getMembershipPlan() : "NONE");
        map.put("membershipStatus", m.getMembershipStatus() != null ? m.getMembershipStatus() : "NONE");
        map.put("createdAt", m.getCreatedAt());
        return map;
    }

    private Map<String, Object> mapMemberDetailed(Member m) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", m.getId());
        map.put("name", m.getName());
        map.put("email", m.getEmail());
        map.put("phone", m.getPhone());
        map.put("role", m.getRole());
        map.put("height", m.getHeight());
        map.put("weight", m.getWeight());
        map.put("fitnessGoal", m.getFitnessGoal());
        map.put("onboardingCompleted", m.getOnboardingCompleted());
        map.put("membershipPlan", m.getMembershipPlan());
        map.put("membershipStatus", m.getMembershipStatus());
        map.put("membershipStartDate", m.getMembershipStartDate());
        map.put("membershipEndDate", m.getMembershipEndDate());
        map.put("membershipPrice", m.getMembershipPrice());
        map.put("createdAt", m.getCreatedAt());

        long attendanceCount = attendanceRepository.findAllByMemberIdOrderByDateDesc(m.getId()).size();
        long workoutCount = workoutSessionRepository.findAllByMemberIdOrderByDateDescSavedAtDesc(m.getId()).size();
        map.put("attendanceCount", attendanceCount);
        map.put("workoutCount", workoutCount);
        return map;
    }

    private Map<String, Object> mapAttendanceSummary(AttendanceRecord record) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", record.getId());
        map.put("date", record.getDate());
        Member member = record.getMember();
        if (member != null) {
            map.put("memberId", member.getId());
            map.put("memberName", member.getName());
            map.put("memberEmail", member.getEmail());
        }
        return map;
    }

    private Map<String, Object> mapWorkoutSummary(WorkoutSession session) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", session.getId());
        map.put("date", session.getDate());
        map.put("savedAt", session.getSavedAt());
        map.put("exercises", session.getExercises());
        Member member = session.getMember();
        if (member != null) {
            map.put("memberId", member.getId());
            map.put("memberName", member.getName());
            map.put("memberEmail", member.getEmail());
        }
        return map;
    }
}
