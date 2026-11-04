package com.gymtracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "weight_entries",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_weight_member_date",
                columnNames = {"member_id", "measurement_date"}
        )
)
public class WeightEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;

    @Column(name = "measurement_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double weight;

    protected WeightEntry() {
    }

    public WeightEntry(Member member, LocalDate date, Double weight) {
        this.member = member;
        this.date = date;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
