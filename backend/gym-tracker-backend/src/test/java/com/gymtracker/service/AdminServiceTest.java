package com.gymtracker.service;

import com.gymtracker.model.Member;
import com.gymtracker.repository.AttendanceRepository;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.WeightEntryRepository;
import com.gymtracker.repository.WorkoutSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private MemberRepository memberRepository;
    private AttendanceRepository attendanceRepository;
    private WorkoutSessionRepository workoutSessionRepository;
    private WeightEntryRepository weightEntryRepository;
    private AdminService adminService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        attendanceRepository = mock(AttendanceRepository.class);
        workoutSessionRepository = mock(WorkoutSessionRepository.class);
        weightEntryRepository = mock(WeightEntryRepository.class);
        adminService = new AdminService(
                memberRepository,
                attendanceRepository,
                workoutSessionRepository,
                weightEntryRepository
        );
    }

    @Test
    void allowsAdminLoginWithValidCredentials() {
        Member admin = new Member();
        admin.setEmail("admin@corelygym.com");
        admin.setPassword(encoder.encode("SecretPass123!"));
        admin.setRole("ADMIN");

        when(memberRepository.findByEmail("admin@corelygym.com")).thenReturn(Optional.of(admin));

        Member result = adminService.loginAdmin("admin@corelygym.com", "SecretPass123!");
        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    void rejectsAdminLoginForRegularMember() {
        Member member = new Member();
        member.setEmail("user@example.com");
        member.setPassword(encoder.encode("SecretPass123!"));
        member.setRole("MEMBER");

        when(memberRepository.findByEmail("user@example.com")).thenReturn(Optional.of(member));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> adminService.loginAdmin("user@example.com", "SecretPass123!")
        );
        assertTrue(ex.getMessage().contains("restricted to authorized gym administrators"));
    }

    @Test
    void providesOverviewStatsCorrectly() {
        Member member = new Member();
        member.setRole("MEMBER");
        member.setMembershipStatus("ACTIVE");
        member.setMembershipPrice(4200.0);

        when(memberRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(member));
        when(attendanceRepository.countByDate(any())).thenReturn(5L);
        when(attendanceRepository.count()).thenReturn(20L);
        when(workoutSessionRepository.count()).thenReturn(15L);

        Map<String, Object> stats = adminService.getOverviewStats();
        assertEquals(1L, stats.get("totalMembers"));
        assertEquals(1L, stats.get("activeMemberships"));
        assertEquals(4200.0, stats.get("totalRevenue"));
        assertEquals(5L, stats.get("todayAttendance"));
    }
}
