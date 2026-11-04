package com.gymtracker.repository;

import com.gymtracker.model.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findAllByMemberIdOrderByDateDescSavedAtDesc(Long memberId);
}
