
package com.gymtracker.service;

import com.gymtracker.model.Member;
import com.gymtracker.repository.MemberRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberValidationEvidenceTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    // BB-01 to BB-05: Phone number validation

    @Test
    void BB01_validPhoneNumber() {
        Member member = validMember();
        member.setPhone("9876543210");
        assertTrue(validator.validate(member).isEmpty());
    }

    @Test
    void BB02_phoneStartingBelowSixIsInvalid() {
        Member member = validMember();
        member.setPhone("1234567890");
        assertFalse(validator.validate(member).isEmpty());
    }

    @Test
    void BB03_nineDigitPhoneIsInvalid() {
        Member member = validMember();
        member.setPhone("987654321");
        assertFalse(validator.validate(member).isEmpty());
    }

    @Test
    void BB04_elevenDigitPhoneIsInvalid() {
        Member member = validMember();
        member.setPhone("98765432100");
        assertFalse(validator.validate(member).isEmpty());
    }

    @Test
    void BB05_emptyPhoneIsInvalid() {
        Member member = validMember();
        member.setPhone("");
        assertFalse(validator.validate(member).isEmpty());
    }

    // Password validation: registration model rules

    @Test
    void PW01_validPassword() {
        Member member = validMember();
        member.setPassword("password123");
        assertTrue(validator.validate(member).isEmpty());
    }

    @Test
    void PW02_emptyPasswordIsInvalid() {
        Member member = validMember();
        member.setPassword("");
        assertFalse(validator.validate(member).isEmpty());
    }

    @Test
    void PW03_shortPasswordIsInvalid() {
        Member member = validMember();
        member.setPassword("pass123");
        assertFalse(validator.validate(member).isEmpty());
    }

    @Test
    void PW04_eightCharacterPasswordIsValid() {
        Member member = validMember();
        member.setPassword("12345678");
        assertTrue(validator.validate(member).isEmpty());
    }

    // Body weight validation: onboarding service

    @Test
    void BW01_positiveWeightIsAccepted() {
        MemberService service = serviceForExistingMember();
        assertDoesNotThrow(() ->
            service.completeOnboarding(1L, 175.0, 65.5, "Fitness")
        );
    }

    @Test
    void BW02_nullWeightIsRejected() {
        MemberService service = serviceForExistingMember();
        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> service.completeOnboarding(1L, 175.0, null, "Fitness")
        );
        assertEquals("Weight must be greater than 0.", error.getMessage());
    }

    @Test
    void BW03_zeroWeightIsRejected() {
        MemberService service = serviceForExistingMember();
        assertThrows(IllegalArgumentException.class,
            () -> service.completeOnboarding(1L, 175.0, 0.0, "Fitness"));
    }

    @Test
    void BW04_negativeWeightIsRejected() {
        MemberService service = serviceForExistingMember();
        assertThrows(IllegalArgumentException.class,
            () -> service.completeOnboarding(1L, 175.0, -5.0, "Fitness"));
    }

    private static Member validMember() {
        Member member = new Member();
        member.setName("Test User");
        member.setEmail("test@example.com");
        member.setPhone("9876543210");
        member.setPassword("password123");
        return member;
    }

    private MemberService serviceForExistingMember() {
        MemberRepository repository = mock(MemberRepository.class);
        Member member = validMember();

        when(repository.findById(1L)).thenReturn(
            java.util.Optional.of(member)
        );
        when(repository.save(any(Member.class))).thenAnswer(
            invocation -> invocation.getArgument(0)
        );

        return new MemberService(repository);
    }
}

