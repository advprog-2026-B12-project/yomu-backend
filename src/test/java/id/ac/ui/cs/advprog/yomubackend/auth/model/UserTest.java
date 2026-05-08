package id.ac.ui.cs.advprog.yomubackend.auth.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.yomubackend.auth.model.Role;

class UserTest {

    @Test
    void defaultRole_IsPelajar() {
        User user = new User();
        assertEquals(Role.PELAJAR, user.getRole());
    }

    @Test
    void setRole_UpdatesRole() {
        User user = new User();
        user.setRole(Role.ADMIN);
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void getName_ReturnsUsername() {
        User user = new User();
        user.setUsername("ahmadFaiq41");
        assertEquals("ahmadFaiq41", user.getName());
    }

    @Test
    void getName_ReturnsNullWhenUsernameIsNull() {
        User user = new User();
        assertNull(user.getName());
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        User user = new User();
        UUID id = UUID.randomUUID();

        user.setId(id);
        user.setUsername("faiq");
        user.setEmail("faiq@example.com");
        user.setDisplayName("Faiq");
        user.setPassword("secret");

        assertEquals(id, user.getId());
        assertEquals("faiq", user.getUsername());
        assertEquals("faiq@example.com", user.getEmail());
        assertEquals("Faiq", user.getDisplayName());
        assertEquals("secret", user.getPassword());
    }

    @Test
    void equals_SameObject_ReturnsTrue() {
        User user = new User();
        user.setId(UUID.randomUUID());
        assertEquals(user, user);
    }

    @Test
    void equals_NullAndDifferentType_ReturnsFalse() {
        User user = new User();
        assertNotEquals(null, user);
        assertNotEquals("string", user);
    }

    @Test
    void equals_EqualInstances_ReturnsTrue() {
        UUID id = UUID.randomUUID();

        User a = new User();
        a.setId(id);
        a.setUsername("u");
        a.setEmail("u@e.com");
        a.setDisplayName("U");
        a.setPassword("p");

        User b = new User();
        b.setId(id);
        b.setUsername("u");
        b.setEmail("u@e.com");
        b.setDisplayName("U");
        b.setPassword("p");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        User a = new User();
        a.setId(UUID.randomUUID());

        User b = new User();
        b.setId(UUID.randomUUID());

        assertNotEquals(a, b);
    }

    @Test
    void equals_DifferentFields_ReturnsFalse() {
        UUID id = UUID.randomUUID();

        User a = new User();
        a.setId(id);
        a.setUsername("alice");

        User b = new User();
        b.setId(id);
        b.setUsername("bob");

        assertNotEquals(a, b);
    }

    @Test
    void toString_ContainsFieldValues() {
        User user = new User();
        user.setUsername("ahmadFaiq41");
        user.setEmail("faiq@example.com");

        String result = user.toString();
        assertTrue(result.contains("ahmadFaiq41"));
        assertTrue(result.contains("faiq@example.com"));
    }
}
