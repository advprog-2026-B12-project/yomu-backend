package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminQuestionControllerTest {

    QuestionService service = mock(QuestionService.class);
    MockMvc mvc = MockMvcBuilders.standaloneSetup(new AdminQuestionController(service)).build();

    @Test
    void testCreateQuestion() throws Exception {
        UUID readingId = UUID.randomUUID();

        Question saved = new Question();
        saved.setQuestionText("What is the capital of France?");

        when(service.create(eq(readingId), any())).thenReturn(saved);

        String body = """
                {"questionText": "What is the capital of France?"}
                """;

        mvc.perform(post("/api/admin/questions/" + readingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionText").value("What is the capital of France?"));
    }

    @Test
    void testGetByReading() throws Exception {
        Question q = new Question();
        q.setQuestionText("Question");

        when(service.findByReading(any())).thenReturn(List.of(q));

        mvc.perform(get("/api/admin/questions/reading/" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionText").value("Question"));
    }

    @Test
    void testDeleteQuestion() throws Exception {
        mvc.perform(delete("/api/admin/questions/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());

        verify(service).delete(any());
    }
}