package com.gymtracker.config;

import com.gymtracker.model.Member;
import com.gymtracker.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminDataInitializer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@corelygym.com";
        if (memberRepository.findByEmail(adminEmail).isEmpty()) {
            Member admin = new Member();
            admin.setName("Corely Administrator");
            admin.setEmail(adminEmail);
            admin.setPhone("9876543210");
            admin.setPassword(passwordEncoder.encode("AdminPass123!"));
            admin.setRole("ADMIN");
            admin.setFitnessGoal("Operations & Management");
            admin.setOnboardingCompleted(true);
            admin.setMembershipPlan("ELITE");
            admin.setMembershipStatus("ACTIVE");
            admin.setCreatedAt(LocalDateTime.now());
            memberRepository.save(admin);
        }
    }
}
