package com.gymtracker.service;

import com.gymtracker.model.Member;
import com.gymtracker.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
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
}