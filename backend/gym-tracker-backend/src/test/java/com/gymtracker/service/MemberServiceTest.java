package com.gymtracker.service;

import com.gymtracker.model.Member;
import com.gymtracker.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    @Test
    void shouldRejectDuplicateEmail() {

        MemberRepository repository = mock(MemberRepository.class);

        when(repository.existsByEmail("test2@test.com"))
                .thenReturn(true);

        MemberService service = new MemberService(repository);

        Member member = new Member();
        member.setEmail("test2@test.com");
        member.setPassword("password123");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registerMember(member)
        );

        verify(repository).existsByEmail("test2@test.com");
        verify(repository, never()).save(any());
    }

    @Test
    void shouldRegisterValidMember() {

        MemberRepository repository = mock(MemberRepository.class);

        Member member = new Member();
        member.setName("Test User");
        member.setEmail("valid@test.com");
        member.setPhone("9876543213");
        member.setPassword("password123");

        when(repository.existsByEmail("valid@test.com"))
                .thenReturn(false);

        when(repository.save(any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MemberService service = new MemberService(repository);

        Member result = service.registerMember(member);

        assertNotNull(result);
        assertNotNull(result.getPassword());
        assertNotEquals("password123", result.getPassword());
        assertNotNull(result.getCreatedAt());

        verify(repository).existsByEmail("valid@test.com");
        verify(repository).save(member);
    }
    @Test
    void shouldLoginWithCorrectCredentials() {

        MemberRepository repository = mock(MemberRepository.class);

        Member member = new Member();
        member.setName("Test User");
        member.setEmail("test2@test.com");

        // Password is BCrypt encoded just like registration
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        member.setPassword(
                encoder.encode("password123")
        );

        when(repository.findByEmail("test2@test.com"))
                .thenReturn(java.util.Optional.of(member));

        MemberService service =
                new MemberService(repository);

        Member result =
                service.loginMember(
                        "test2@test.com",
                        "password123"
                );

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test2@test.com", result.getEmail());

        verify(repository)
                .findByEmail("test2@test.com");
    }


    @Test
    void shouldRejectUnknownEmail() {

        MemberRepository repository =
                mock(MemberRepository.class);

        when(repository.findByEmail("unknown@test.com"))
                .thenReturn(java.util.Optional.empty());

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
                .thenReturn(java.util.Optional.of(member));

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

}