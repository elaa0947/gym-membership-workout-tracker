package com.gymtracker.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class MemberSessionInterceptorTest {

    private final MemberSessionInterceptor interceptor = new MemberSessionInterceptor();

    @Test
    void allowsRequestsForTheSignedInMember() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/members/42/attendance");
        request.getSession().setAttribute("memberId", 42L);

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void rejectsRequestsForAnotherMember() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/members/profile/42");
        request.getSession().setAttribute("memberId", 7L);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(401, response.getStatus());
    }

    @Test
    void rejectsRequestsWithoutASession() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/members/42/workouts");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(401, response.getStatus());
    }
}
