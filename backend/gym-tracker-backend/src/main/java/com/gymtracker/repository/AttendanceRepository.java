package com.gymtracker.repository;

import com.gymtracker.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findAllByMemberIdOrderByDateDesc(Long memberId);

    List<AttendanceRecord> findAllByOrderByDateDesc();

    boolean existsByMemberIdAndDate(Long memberId, LocalDate date);

    Optional<AttendanceRecord> findByMemberIdAndDate(Long memberId, LocalDate date);

    long countByDate(LocalDate date);

    void deleteAllByMemberId(Long memberId);
}
