package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateClanRequestTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        CreateClanRequest req = new CreateClanRequest();
        req.setName("Warriors");
        req.setDescription("Fight together");

        assertEquals("Warriors", req.getName());
        assertEquals("Fight together", req.getDescription());
    }

    @Test
    void defaultValues_areNull() {
        CreateClanRequest req = new CreateClanRequest();

        assertNull(req.getName());
        assertNull(req.getDescription());
    }

    @Test
    void description_canBeNull() {
        CreateClanRequest req = new CreateClanRequest();
        req.setName("Nameless");
        req.setDescription(null);

        assertEquals("Nameless", req.getName());
        assertNull(req.getDescription());
    }

    @Test
    void equals_returnsTrueForSameValues() {
        CreateClanRequest req1 = new CreateClanRequest();
        req1.setName("Warriors");
        req1.setDescription("desc");

        CreateClanRequest req2 = new CreateClanRequest();
        req2.setName("Warriors");
        req2.setDescription("desc");

        assertEquals(req1, req2);
    }

    @Test
    void equals_returnsFalseForDifferentValues() {
        CreateClanRequest req1 = new CreateClanRequest();
        req1.setName("Warriors");

        CreateClanRequest req2 = new CreateClanRequest();
        req2.setName("Rangers");

        assertNotEquals(req1, req2);
    }

    @Test
    void hashCode_isSameForEqualObjects() {
        CreateClanRequest req1 = new CreateClanRequest();
        req1.setName("Warriors");
        req1.setDescription("desc");

        CreateClanRequest req2 = new CreateClanRequest();
        req2.setName("Warriors");
        req2.setDescription("desc");

        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void toString_containsFields() {
        CreateClanRequest req = new CreateClanRequest();
        req.setName("Warriors");
        req.setDescription("Fight together");

        String str = req.toString();

        assertTrue(str.contains("Warriors"));
        assertTrue(str.contains("Fight together"));
    }
}
