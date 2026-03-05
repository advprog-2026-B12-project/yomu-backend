package id.ac.ui.cs.advprog.yomubackend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoinClanRequestTest {

    @Test
    void getterAndSetter_workCorrectly() {
        JoinClanRequest req = new JoinClanRequest();
        req.setUserId(7L);

        assertEquals(7L, req.getUserId());
    }

    @Test
    void defaultValue_isNull() {
        JoinClanRequest req = new JoinClanRequest();

        assertNull(req.getUserId());
    }

    @Test
    void equals_returnsTrueForSameUserId() {
        JoinClanRequest req1 = new JoinClanRequest();
        req1.setUserId(5L);

        JoinClanRequest req2 = new JoinClanRequest();
        req2.setUserId(5L);

        assertEquals(req1, req2);
    }

    @Test
    void equals_returnsFalseForDifferentUserId() {
        JoinClanRequest req1 = new JoinClanRequest();
        req1.setUserId(1L);

        JoinClanRequest req2 = new JoinClanRequest();
        req2.setUserId(2L);

        assertNotEquals(req1, req2);
    }

    @Test
    void hashCode_isSameForEqualObjects() {
        JoinClanRequest req1 = new JoinClanRequest();
        req1.setUserId(5L);

        JoinClanRequest req2 = new JoinClanRequest();
        req2.setUserId(5L);

        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void toString_containsUserId() {
        JoinClanRequest req = new JoinClanRequest();
        req.setUserId(42L);

        assertTrue(req.toString().contains("42"));
    }
}