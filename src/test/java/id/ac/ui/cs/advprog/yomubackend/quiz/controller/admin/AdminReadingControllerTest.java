package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.mapper.QuizResponseMapper;
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
    MockMvc mvc = MockMvcBuilders.standaloneSetup(new AdminReadingController(service, new QuizResponseMapper())).build();

    @Test
    void testCreateReading() throws Exception {
        Reading r = new Reading();
        r.setId(UUID.randomUUID());
        r.setTitle("Title");
        r.setCategory("News & Media");
        r.setContent("Content");

        when(service.create(any())).thenReturn(r);

        mvc.perform(post("/api/admin/readings")
                        .contentType("application/json")
                        .content("""
                    {"title":"Title","category":"News & Media","content":"Content"}
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.category").value("News & Media"));
    }

    @Test
    void testGetAll() throws Exception {

        Reading r = new Reading();
        r.setId(UUID.randomUUID());
        r.setTitle("Title");
        r.setCategory("News & Media");
        r.setContent("Content");

        when(service.findAll()).thenReturn(List.of(r));

        mvc.perform(get("/api/admin/readings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title"))
                .andExpect(jsonPath("$[0].category").value("News & Media"));
    }

    @Test
    void testUpdateReading() throws Exception {
        UUID id = UUID.randomUUID();

        Reading updated = new Reading();
        updated.setId(id);
        updated.setTitle("Updated");
        updated.setCategory("Science");
        updated.setContent("Updated Content");

        when(service.update(eq(id), any())).thenReturn(updated);

        mvc.perform(put("/api/admin/readings/" + id)
                        .contentType("application/json")
                        .content("""
                    {"title":"Updated","category":"Science","content":"Updated Content"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.category").value("Science"))
                .andExpect(jsonPath("$.content").value("Updated Content"));
    }

    @Test
    void testDeleteReading() throws Exception {
        UUID id = UUID.randomUUID();

        mvc.perform(delete("/api/admin/readings/" + id))
                .andExpect(status().isNoContent());

        verify(service).delete(id);
    }

    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();

        Reading r = new Reading();
        r.setId(id);
        r.setTitle("Detail Title");
        r.setCategory("Science");
        r.setContent("Detail Content");

        when(service.findById(id)).thenReturn(r);

        mvc.perform(get("/api/admin/readings/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Detail Title"))
                .andExpect(jsonPath("$.category").value("Science"));
    }
}
