package com.gymtracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Name must contain at least 2 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[6-9]\\d{9}$",
        message = "Enter a valid 10-digit phone number"
    )
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column
    private Double height;

    @Column
    private Double weight;

    @Column
    private String fitnessGoal;

    @Column
    private Boolean onboardingCompleted = false;

    @Column(name = "membership_plan")
    private String membershipPlan;

    @Column(name = "membership_status")
    private String membershipStatus = "NONE";

    @Column(name = "membership_duration_months")
    private Integer membershipDurationMonths;

    @Column(name = "membership_start_date")
    private LocalDate membershipStartDate;

    @Column(name = "membership_end_date")
    private LocalDate membershipEndDate;

    @Column(name = "membership_price")
    private Double membershipPrice;

    public String getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(String membershipPlan) {
        this.membershipPlan = membershipPlan;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public Integer getMembershipDurationMonths() {
        return membershipDurationMonths;
    }

    public void setMembershipDurationMonths(Integer membershipDurationMonths) {
        this.membershipDurationMonths = membershipDurationMonths;
    }

    public LocalDate getMembershipStartDate() {
        return membershipStartDate;
    }

    public void setMembershipStartDate(LocalDate membershipStartDate) {
        this.membershipStartDate = membershipStartDate;
    }

    public LocalDate getMembershipEndDate() {
        return membershipEndDate;
    }

    public void setMembershipEndDate(LocalDate membershipEndDate) {
        this.membershipEndDate = membershipEndDate;
    }

    public Double getMembershipPrice() {
        return membershipPrice;
    }

    public void setMembershipPrice(Double membershipPrice) {
        this.membershipPrice = membershipPrice;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public Boolean getOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(Boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }
    public Member() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
}