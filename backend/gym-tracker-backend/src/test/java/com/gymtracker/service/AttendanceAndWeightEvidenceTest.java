
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AttendanceAndWeightEvidenceTest {

    private final MemberRepository members = mock(MemberRepository.class);
    private final AttendanceRepository attendance = mock(AttendanceRepository.class);
    private final WorkoutSessionRepository workouts = mock(WorkoutSessionRepository.class);
    private final WeightEntryRepository weights = mock(WeightEntryRepository.class);

    private final MemberActivityService service =
            new MemberActivityService(members, attendance, workouts, weights);

    // DECISION TABLE TESTING — ATTENDANCE CHECK-IN (FR7)

    @Test
    void DT01_rejectsUnknownMember() {
        when(members.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.checkIn(999L, LocalDate.now()));

        verify(attendance, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void DT02_rejectsMissingAttendanceDate() {
        when(members.findById(5L)).thenReturn(Optional.of(new Member()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.checkIn(5L, null));

        assertEquals("Attendance date must be today or earlier.",
                exception.getMessage());

        verify(attendance, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void DT03_rejectsFutureAttendanceDate() {
        when(members.findById(5L)).thenReturn(Optional.of(new Member()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.checkIn(5L, LocalDate.now().plusDays(1)));

        assertEquals("Attendance date must be today or earlier.",
                exception.getMessage());

        verify(attendance, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void DT04_returnsExistingRecordWithoutCreatingDuplicate() {
        Member member = new Member();
        LocalDate today = LocalDate.now();
        AttendanceRecord existing = new AttendanceRecord(member, today);

        when(members.findById(5L)).thenReturn(Optional.of(member));
        when(attendance.findByMemberIdAndDate(5L, today))
                .thenReturn(Optional.of(existing));

        AttendanceRecord result = service.checkIn(5L, today);

        assertSame(existing, result);
        verify(attendance, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void DT05_createsNewAttendanceRecordForFirstCheckIn() {
        Member member = new Member();
        LocalDate today = LocalDate.now();

        when(members.findById(5L)).thenReturn(Optional.of(member));
        when(attendance.findByMemberIdAndDate(5L, today))
                .thenReturn(Optional.empty());
        when(attendance.save(any(AttendanceRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceRecord result = service.checkIn(5L, today);

        assertNotNull(result);
        assertEquals(today, result.getDate());
        verify(attendance, times(1)).save(any(AttendanceRecord.class));
    }

    // WHITE BOX / BASIS PATH TESTING — saveWeight() (FR9)

    @Test
    void WB01_rejectsMissingMeasurementDate() {
        when(members.findById(5L)).thenReturn(Optional.of(new Member()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.saveWeight(5L, null, 72.5));

        assertEquals("Measurement date must be today or earlier.",
                exception.getMessage());

        verify(weights, never()).save(any(WeightEntry.class));
    }

    @Test
    void WB02_rejectsFutureMeasurementDate() {
        when(members.findById(5L)).thenReturn(Optional.of(new Member()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.saveWeight(5L, LocalDate.now().plusDays(1), 72.5));

        assertEquals("Measurement date must be today or earlier.",
                exception.getMessage());

        verify(weights, never()).save(any(WeightEntry.class));
    }

    @Test
    void WB03_rejectsMissingWeight() {
        when(members.findById(5L)).thenReturn(Optional.of(new Member()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.saveWeight(5L, LocalDate.now(), null));

        assertEquals("Weight must be between 20 and 350 kg.",
                exception.getMessage());

        verify(weights, never()).save(any(WeightEntry.class));
    }

    @Test
    void WB04_rejectsNaNWeightAtServiceLevel() {
        when(members.findById(5L)).thenReturn(Optional.of(new Member()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.saveWeight(5L, LocalDate.now(), Double.NaN));

        assertEquals("Weight must be between 20 and 350 kg.",
                exception.getMessage());

        verify(weights, never()).save(any(WeightEntry.class));
    }

    @Test
    void WB05_rejectsWeightBelowMinimum() {
        when(members.findById(5L)).thenReturn(Optional.of(new Member()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.saveWeight(5L, LocalDate.now(), 15.0));

        assertEquals("Weight must be between 20 and 350 kg.",
                exception.getMessage());

        verify(weights, never()).save(any(WeightEntry.class));
    }

    @Test
    void WB06_rejectsWeightAboveMaximum() {
        when(members.findById(5L)).thenReturn(Optional.of(new Member()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.saveWeight(5L, LocalDate.now(), 400.0));

        assertEquals("Weight must be between 20 and 350 kg.",
                exception.getMessage());

        verify(weights, never()).save(any(WeightEntry.class));
    }

    @Test
    void WB07_savesValidWeightAndUpdatesMember() {
        Member member = new Member();
        LocalDate today = LocalDate.now();

        when(members.findById(5L)).thenReturn(Optional.of(member));
        when(weights.findByMemberIdAndDate(5L, today))
                .thenReturn(Optional.empty());
        when(weights.save(any(WeightEntry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WeightEntry result = service.saveWeight(5L, today, 72.5);

        assertNotNull(result);
        assertEquals(72.5, result.getWeight());
        assertEquals(today, result.getDate());
        assertEquals(72.5, member.getWeight());

        verify(weights, times(1)).save(any(WeightEntry.class));
        verify(members, times(1)).save(member);
    }
}

