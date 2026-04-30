package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiMessageResponseTest {

    @Test
    void testAllArgsConstructorAndGetter() {
        ApiMessageResponse response = new ApiMessageResponse("Success");

        assertEquals("Success", response.getMessage());
    }

    @Test
    void testSetter() {
        ApiMessageResponse response = new ApiMessageResponse("Old message");
        response.setMessage("New message");

        assertEquals("New message", response.getMessage());
    }

    @Test
    void testEqualsAndHashCode() {
        ApiMessageResponse response1 = new ApiMessageResponse("Same");
        ApiMessageResponse response2 = new ApiMessageResponse("Same");
        ApiMessageResponse response3 = new ApiMessageResponse("Different");

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        ApiMessageResponse response = new ApiMessageResponse("Hello");

        String result = response.toString();

        assertNotNull(result);
        assertTrue(result.contains("Hello"));
    }
}