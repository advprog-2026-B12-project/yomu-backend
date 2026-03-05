package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.OptionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminOptionControllerTest {

    OptionService service = mock(OptionService.class);
    MockMvc mvc = MockMvcBuilders.standaloneSetup(new AdminOptionController(service)).build();

    @Test
    void testCreateOption() throws Exception {
        UUID questionId = UUID.randomUUID();

        Option saved = new Option();
        saved.setOptionText("Paris");
        saved.setCorrect(true);

        when(service.create(eq(questionId), any())).thenReturn(saved);

        String body = """
                {"optionText": "Paris", "isCorrect": true}
                """;

        mvc.perform(post("/api/admin/options/" + questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.optionText").value("Paris"))
                .andExpect(jsonPath("$.correct").value(true));
    }

    @Test
    void testGetByQuestion() throws Exception {
        UUID questionId = UUID.randomUUID();

        Option option = new Option();
        option.setOptionText("Paris");
        option.setCorrect(false);

        when(service.findByQuestion(questionId)).thenReturn(List.of(option));

        mvc.perform(get("/api/admin/options/question/" + questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].optionText").value("Paris"));
    }

    @Test
    void testDeleteOption() throws Exception {
        mvc.perform(delete("/api/admin/options/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());

        verify(service).delete(any());
    }
}