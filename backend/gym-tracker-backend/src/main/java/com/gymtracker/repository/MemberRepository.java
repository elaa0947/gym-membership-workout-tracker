package com.gymtracker.repository;

import com.gymtracker.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Member> findAllByOrderByCreatedAtDesc();

    List<Member> findByRoleOrderByCreatedAtDesc(String role);

    long countByRole(String role);

    long countByMembershipStatus(String membershipStatus);
}
