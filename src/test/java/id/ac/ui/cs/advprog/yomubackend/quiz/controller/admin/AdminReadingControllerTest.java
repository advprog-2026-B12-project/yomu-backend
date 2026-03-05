package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminReadingControllerTest {

    ReadingService service = mock(ReadingService.class);
    MockMvc mvc = MockMvcBuilders.standaloneSetup(new AdminReadingController(service)).build();

    @Test
    void testCreateReading() throws Exception {
        Reading r = new Reading();
        r.setId(UUID.randomUUID());
        r.setTitle("Title");
        r.setContent("Content");

        when(service.create(any())).thenReturn(r);

        mvc.perform(post("/api/admin/readings")
                        .contentType("application/json")
                        .content("""
                        {"title":"Title","content":"Content"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void testGetAll() throws Exception {

        Reading r = new Reading();
        r.setId(UUID.randomUUID());
        r.setTitle("Title");
        r.setContent("Content");

        when(service.findAll()).thenReturn(List.of(r));

        mvc.perform(get("/api/admin/readings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title"));
    }
}