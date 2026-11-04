package com.gymtracker.service;

import com.gymtracker.model.AttendanceRecord;
import com.gymtracker.model.Member;
import com.gymtracker.model.WeightEntry;
import com.gymtracker.model.WorkoutSession;
import com.gymtracker.repository.AttendanceRepository;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.WeightEntryRepository;
import com.gymtracker.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberActivityService {

    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final WorkoutSessionRepository workoutRepository;
    private final WeightEntryRepository weightRepository;
    private final MemberService memberService;

    public MemberActivityService(
            MemberRepository memberRepository,
            AttendanceRepository attendanceRepository,
            WorkoutSessionRepository workoutRepository,
            WeightEntryRepository weightRepository) {
        this(memberRepository, attendanceRepository, workoutRepository, weightRepository, null);
    }

    @Autowired
    public MemberActivityService(
            MemberRepository memberRepository,
            AttendanceRepository attendanceRepository,
            WorkoutSessionRepository workoutRepository,
            WeightEntryRepository weightRepository,
            MemberService memberService) {
        this.memberRepository = memberRepository;
        this.attendanceRepository = attendanceRepository;
        this.workoutRepository = workoutRepository;
        this.weightRepository = weightRepository;
        this.memberService = memberService;
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getAttendance(Long memberId) {
        requireMember(memberId);
        return attendanceRepository.findAllByMemberIdOrderByDateDesc(memberId);
    }

    public AttendanceRecord checkIn(Long memberId, LocalDate date) {
        Member member = requireMember(memberId);
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Attendance date must be today or earlier.");
        }
        return attendanceRepository.findByMemberIdAndDate(memberId, date)
                .orElseGet(() -> attendanceRepository.save(new AttendanceRecord(member, date)));
    }

    @Transactional(readOnly = true)
    public List<WorkoutSession> getWorkouts(Long memberId) {
        requireMember(memberId);
        return workoutRepository.findAllByMemberIdOrderByDateDescSavedAtDesc(memberId);
    }

    public WorkoutSession saveWorkout(Long memberId, List<String> exercises, LocalDate date) {
        Member member = requireMember(memberId);
        if (exercises == null || exercises.isEmpty()
                || exercises.stream().anyMatch(name -> name == null
                || name.isBlank() || name.length() > 120)) {
            throw new IllegalArgumentException("Select at least one valid exercise.");
        }
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Workout date must be today or earlier.");
        }
        return workoutRepository.save(new WorkoutSession(member, date, exercises));
    }

    @Transactional(readOnly = true)
    public List<WeightEntry> getWeights(Long memberId) {
        requireMember(memberId);
        return weightRepository.findAllByMemberIdOrderByDateAsc(memberId);
    }

    public WeightEntry saveWeight(Long memberId, LocalDate date, Double weight) {
        Member member = requireMember(memberId);
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Measurement date must be today or earlier.");
        }
        if (weight == null || !Double.isFinite(weight) || weight < 20 || weight > 350) {
            throw new IllegalArgumentException("Weight must be between 20 and 350 kg.");
        }
        WeightEntry entry = weightRepository.findByMemberIdAndDate(memberId, date)
                .orElseGet(() -> new WeightEntry(member, date, weight));
        entry.setWeight(weight);
        WeightEntry saved = weightRepository.save(entry);

        member.setWeight(weight);
        memberRepository.save(member);

        return saved;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardSummary(Long memberId) {
        Member member = requireMember(memberId);
        List<AttendanceRecord> attendance = attendanceRepository.findAllByMemberIdOrderByDateDesc(memberId);
        List<WorkoutSession> workouts = workoutRepository.findAllByMemberIdOrderByDateDescSavedAtDesc(memberId);
        List<WeightEntry> weights = weightRepository.findAllByMemberIdOrderByDateAsc(memberId);
        Map<String, Object> membership = memberService != null
                ? memberService.getMembership(memberId)
                : Collections.emptyMap();

        Map<String, Object> summary = new HashMap<>();

        Map<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("id", member.getId());
        memberInfo.put("name", member.getName());
        memberInfo.put("email", member.getEmail());
        memberInfo.put("phone", member.getPhone());
        memberInfo.put("height", member.getHeight());
        memberInfo.put("weight", member.getWeight());
        memberInfo.put("fitnessGoal", member.getFitnessGoal());
        memberInfo.put("onboardingCompleted", member.getOnboardingCompleted());
        summary.put("member", memberInfo);

        Double bmi = null;
        if (member.getHeight() != null && member.getHeight() > 0 && member.getWeight() != null && member.getWeight() > 0) {
            double heightM = member.getHeight() / 100.0;
            bmi = Math.round((member.getWeight() / (heightM * heightM)) * 10.0) / 10.0;
        }
        summary.put("bmi", bmi);
        summary.put("membership", membership);

        LocalDate today = LocalDate.now();
        String currentMonthPrefix = today.getYear() + "-" + String.format("%02d", today.getMonthValue());
        long visitsThisMonth = attendance.stream()
                .filter(a -> a.getDate() != null && a.getDate().toString().startsWith(currentMonthPrefix))
                .count();
        boolean checkedInToday = attendance.stream()
                .anyMatch(a -> today.equals(a.getDate()));

        int streak = 0;
        Set<LocalDate> dateSet = attendance.stream()
                .map(AttendanceRecord::getDate)
                .collect(Collectors.toSet());
        LocalDate checkDate = checkedInToday ? today : today.minusDays(1);
        while (dateSet.contains(checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }

        Map<String, Object> attendanceStats = new HashMap<>();
        attendanceStats.put("totalVisits", attendance.size());
        attendanceStats.put("visitsThisMonth", visitsThisMonth);
        attendanceStats.put("currentStreak", streak);
        attendanceStats.put("checkedInToday", checkedInToday);
        attendanceStats.put("recentRecords", attendance.stream().limit(5).toList());
        summary.put("attendance", attendanceStats);

        Map<String, Object> workoutStats = new HashMap<>();
        workoutStats.put("totalSessions", workouts.size());
        workoutStats.put("latestSession", workouts.isEmpty() ? null : workouts.get(0));
        workoutStats.put("recentSessions", workouts.stream().limit(5).toList());
        summary.put("workouts", workoutStats);

        Map<String, Object> progressStats = new HashMap<>();
        progressStats.put("totalLogs", weights.size());
        Double weightChange = null;
        if (weights.size() >= 2) {
            double startWeight = weights.get(0).getWeight();
            double lastWeight = weights.get(weights.size() - 1).getWeight();
            weightChange = Math.round((lastWeight - startWeight) * 10.0) / 10.0;
        }
        progressStats.put("weightChange", weightChange);
        progressStats.put("recentWeights", weights.stream().sorted((a, b) -> b.getDate().compareTo(a.getDate())).limit(7).toList());
        summary.put("progress", progressStats);

        return summary;
    }

    private Member requireMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));
    }
}
