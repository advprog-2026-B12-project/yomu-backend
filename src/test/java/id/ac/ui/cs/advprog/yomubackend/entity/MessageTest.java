package id.ac.ui.cs.advprog.yomubackend.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageTest {

    @Test
    void testMessageEntityLombokMethods() {
        Message msg1 = new Message();
        msg1.setId(1L);
        msg1.setContent("Hello World");

        Message msg2 = new Message();
        msg2.setId(1L);
        msg2.setContent("Hello World");

        assertEquals(1L, msg1.getId());
        assertEquals("Hello World", msg1.getContent());
        assertEquals(msg1, msg2);
        assertEquals(msg1.hashCode(), msg2.hashCode());
        assertTrue(msg1.toString().contains("Hello World"));
    }
}