package com.gymtracker.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertFalse;

class MemberJsonSerializationTest {

    @Test
    void doesNotSerializePasswordHashes() throws Exception {
        Member member = new Member();
        member.setEmail("member@example.com");
        member.setPassword("stored-hash");

        String json = new ObjectMapper().writeValueAsString(member);

        assertFalse(json.contains("stored-hash"));
        assertFalse(json.contains("\"password\""));
    }
}
