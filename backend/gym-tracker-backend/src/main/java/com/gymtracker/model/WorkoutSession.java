package com.gymtracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_sessions")
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;

    @Column(name = "session_date", nullable = false)
    private LocalDate date;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "workout_exercises",
            joinColumns = @JoinColumn(name = "workout_id")
    )
    @Column(name = "exercise_name", nullable = false, length = 120)
    private List<String> exercises = new ArrayList<>();

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    protected WorkoutSession() {
    }

    public WorkoutSession(Member member, LocalDate date, List<String> exercises) {
        this.member = member;
        this.date = date;
        this.exercises = new ArrayList<>(exercises);
        this.savedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<String> getExercises() {
        return List.copyOf(exercises);
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }
}
