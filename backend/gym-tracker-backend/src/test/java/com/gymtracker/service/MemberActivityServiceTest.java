package com.gymtracker.service;

import com.gymtracker.model.AttendanceRecord;
import com.gymtracker.model.Member;
import com.gymtracker.model.WeightEntry;
import com.gymtracker.repository.AttendanceRepository;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.WeightEntryRepository;
import com.gymtracker.repository.WorkoutSessionRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MemberActivityServiceTest {

    private final MemberRepository members = mock(MemberRepository.class);
    private final AttendanceRepository attendance = mock(AttendanceRepository.class);
    private final WorkoutSessionRepository workouts = mock(WorkoutSessionRepository.class);
    private final WeightEntryRepository weights = mock(WeightEntryRepository.class);
    private final MemberActivityService service =
            new MemberActivityService(members, attendance, workouts, weights);

    @Test
    void checkInIsIdempotentForTheSameMemberAndDate() {
        Member member = new Member();
        AttendanceRecord existing = new AttendanceRecord(member, LocalDate.now());
        when(members.findById(5L)).thenReturn(Optional.of(member));
        when(attendance.findByMemberIdAndDate(5L, LocalDate.now()))
                .thenReturn(Optional.of(existing));

        assertEquals(existing, service.checkIn(5L, LocalDate.now()));
        verify(attendance, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void savesAChangedWeightForTheExistingDate() {
        Member member = new Member();
        WeightEntry existing = new WeightEntry(member, LocalDate.now(), 70.0);
        when(members.findById(5L)).thenReturn(Optional.of(member));
        when(weights.findByMemberIdAndDate(5L, LocalDate.now()))
                .thenReturn(Optional.of(existing));
        when(weights.save(existing)).thenReturn(existing);

        WeightEntry saved = service.saveWeight(5L, LocalDate.now(), 69.5);

        assertEquals(69.5, saved.getWeight());
        verify(weights).save(existing);
        verify(members).save(member);
        assertEquals(69.5, member.getWeight());
    }

    @Test
    void generatesDashboardSummaryCorrectly() {
        Member member = new Member();
        member.setName("Alex");
        member.setHeight(180.0);
        member.setWeight(75.0);
        when(members.findById(5L)).thenReturn(Optional.of(member));

        var summary = service.getDashboardSummary(5L);
        assertEquals(23.1, summary.get("bmi"));
        org.junit.jupiter.api.Assertions.assertNotNull(summary.get("attendance"));
        org.junit.jupiter.api.Assertions.assertNotNull(summary.get("workouts"));
        org.junit.jupiter.api.Assertions.assertNotNull(summary.get("progress"));
    }
}
