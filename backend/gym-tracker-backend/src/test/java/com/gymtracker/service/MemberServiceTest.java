package com.gymtracker.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.gymtracker.model.Member;
import com.gymtracker.repository.MemberRepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    // =========================================================
    // REGISTRATION TESTS
    // =========================================================

    @Test
    void shouldRejectDuplicateEmail() {

        MemberRepository repository =
                mock(MemberRepository.class);

        when(repository.existsByEmail("test2@test.com"))
                .thenReturn(true);

        MemberService service =
                new MemberService(repository);

        Member member = new Member();
        member.setEmail("test2@test.com");
        member.setPassword("password123");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registerMember(member)
        );

        verify(repository)
                .existsByEmail("test2@test.com");

        verify(repository, never())
                .save(any());
    }

    @Test
    void shouldRegisterValidMember() {

        MemberRepository repository =
                mock(MemberRepository.class);

        Member member = new Member();

        member.setName("Test User");
        member.setEmail("valid@test.com");
        member.setPhone("9876543213");
        member.setPassword("password123");

        when(repository.existsByEmail("valid@test.com"))
                .thenReturn(false);

        when(repository.save(any(Member.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        MemberService service =
                new MemberService(repository);

        Member result =
                service.registerMember(member);

        assertNotNull(result);

        assertNotNull(
                result.getPassword()
        );

        assertNotEquals(
                "password123",
                result.getPassword()
        );

        assertNotNull(
                result.getCreatedAt()
        );

        verify(repository)
                .existsByEmail("valid@test.com");

        verify(repository)
                .save(member);
    }

    // =========================================================
    // LOGIN TESTS
    // =========================================================

    @Test
    void shouldLoginWithCorrectCredentials() {

        MemberRepository repository =
                mock(MemberRepository.class);

        Member member = new Member();

        member.setName("Test User");
        member.setEmail("test2@test.com");

        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        member.setPassword(
                encoder.encode("password123")
        );

        when(repository.findByEmail("test2@test.com"))
                .thenReturn(Optional.of(member));

        MemberService service =
                new MemberService(repository);

        Member result =
                service.loginMember(
                        "test2@test.com",
                        "password123"
                );

        assertNotNull(result);

        assertEquals(
                "Test User",
                result.getName()
        );

        assertEquals(
                "test2@test.com",
                result.getEmail()
        );

        verify(repository)
                .findByEmail("test2@test.com");
    }

    @Test
    void shouldRejectUnknownEmail() {

        MemberRepository repository =
                mock(MemberRepository.class);

        when(repository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        MemberService service =
                new MemberService(repository);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.loginMember(
                        "unknown@test.com",
                        "password123"
                )
        );

        verify(repository)
                .findByEmail("unknown@test.com");
    }

    @Test
    void shouldRejectWrongPassword() {

        MemberRepository repository =
                mock(MemberRepository.class);

        Member member = new Member();

        member.setName("Test User");
        member.setEmail("test2@test.com");

        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        member.setPassword(
                encoder.encode("password123")
        );

        when(repository.findByEmail("test2@test.com"))
                .thenReturn(Optional.of(member));

        MemberService service =
                new MemberService(repository);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.loginMember(
                        "test2@test.com",
                        "wrongpassword"
                )
        );

        verify(repository)
                .findByEmail("test2@test.com");
    }

    // =========================================================
    // PROFILE TESTS - US-3
    // =========================================================

    @Test
    void shouldGetMemberProfileWithoutPassword() {

        MemberRepository repository =
                mock(MemberRepository.class);

        Member member = new Member();

        member.setName("elaa");
        member.setEmail("elaa0947@gmail.com");
        member.setPhone("9655205008");
        member.setPassword("encoded-password");
        member.setCreatedAt(LocalDateTime.now());

        when(repository.findById(3L))
                .thenReturn(Optional.of(member));

        MemberService service =
                new MemberService(repository);

        Member result =
                service.getMemberProfile(3L);

        assertNotNull(result);

        assertEquals(
                "elaa",
                result.getName()
        );

        assertEquals(
                "elaa0947@gmail.com",
                result.getEmail()
        );

        assertEquals(
                "9655205008",
                result.getPhone()
        );

        assertNull(
                result.getPassword()
        );

        verify(repository)
                .findById(3L);
    }

    @Test
    void shouldRejectProfileForUnknownMember() {

        MemberRepository repository =
                mock(MemberRepository.class);

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        MemberService service =
                new MemberService(repository);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getMemberProfile(999L)
        );

        verify(repository)
                .findById(999L);
    }

    @Test
    void shouldUpdateMemberProfile() {

        MemberRepository repository =
                mock(MemberRepository.class);

        Member member = new Member();

        member.setName("elaa");
        member.setEmail("elaa0947@gmail.com");
        member.setPhone("9655205008");
        member.setPassword("encoded-password");

        when(repository.findById(3L))
                .thenReturn(Optional.of(member));

        when(repository.save(any(Member.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        MemberService service =
                new MemberService(repository);

        Member result =
                service.updateMemberProfile(
                        3L,
                        "Elaa Updated",
                        "9876543211"
                );

        assertNotNull(result);

        assertEquals(
                "Elaa Updated",
                result.getName()
        );

        assertEquals(
                "9876543211",
                result.getPhone()
        );

        assertNull(
                result.getPassword()
        );

        verify(repository)
                .findById(3L);

        verify(repository)
                .save(member);
    }

    @Test
    void shouldRejectUpdateForUnknownMember() {

        MemberRepository repository =
                mock(MemberRepository.class);

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        MemberService service =
                new MemberService(repository);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateMemberProfile(
                        999L,
                        "Unknown User",
                        "9876543211"
                )
        );

        verify(repository)
                .findById(999L);

        verify(repository, never())
                .save(any(Member.class));
    }
}