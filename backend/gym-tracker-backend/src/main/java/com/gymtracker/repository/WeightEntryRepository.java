package com.gymtracker.repository;

import com.gymtracker.model.WeightEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeightEntryRepository extends JpaRepository<WeightEntry, Long> {

    List<WeightEntry> findAllByMemberIdOrderByDateAsc(Long memberId);

    Optional<WeightEntry> findByMemberIdAndDate(Long memberId, LocalDate date);

    void deleteAllByMemberId(Long memberId);
}
