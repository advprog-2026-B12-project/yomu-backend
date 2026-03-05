package id.ac.ui.cs.advprog.yomubackend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateClanRequestTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        CreateClanRequest req = new CreateClanRequest();
        req.setUserId(1L);
        req.setName("Warriors");
        req.setDescription("Fight together");

        assertEquals(1L, req.getUserId());
        assertEquals("Warriors", req.getName());
        assertEquals("Fight together", req.getDescription());
    }

    @Test
    void defaultValues_areNull() {
        CreateClanRequest req = new CreateClanRequest();

        assertNull(req.getUserId());
        assertNull(req.getName());
        assertNull(req.getDescription());
    }

    @Test
    void description_canBeNull() {
        CreateClanRequest req = new CreateClanRequest();
        req.setUserId(1L);
        req.setName("Nameless");
        req.setDescription(null);

        assertNull(req.getDescription());
    }

    @Test
    void equals_returnsTrueForSameValues() {
        CreateClanRequest req1 = new CreateClanRequest();
        req1.setUserId(1L);
        req1.setName("Warriors");
        req1.setDescription("desc");

        CreateClanRequest req2 = new CreateClanRequest();
        req2.setUserId(1L);
        req2.setName("Warriors");
        req2.setDescription("desc");

        assertEquals(req1, req2);
    }

    @Test
    void equals_returnsFalseForDifferentValues() {
        CreateClanRequest req1 = new CreateClanRequest();
        req1.setUserId(1L);
        req1.setName("Warriors");

        CreateClanRequest req2 = new CreateClanRequest();
        req2.setUserId(2L);
        req2.setName("Rangers");

        assertNotEquals(req1, req2);
    }

    @Test
    void hashCode_isSameForEqualObjects() {
        CreateClanRequest req1 = new CreateClanRequest();
        req1.setUserId(1L);
        req1.setName("Warriors");
        req1.setDescription("desc");

        CreateClanRequest req2 = new CreateClanRequest();
        req2.setUserId(1L);
        req2.setName("Warriors");
        req2.setDescription("desc");

        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void toString_containsAllFields() {
        CreateClanRequest req = new CreateClanRequest();
        req.setUserId(1L);
        req.setName("Warriors");
        req.setDescription("Fight together");

        String str = req.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("Warriors"));
        assertTrue(str.contains("Fight together"));
    }
}