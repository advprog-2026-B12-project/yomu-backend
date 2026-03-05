package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.service.OptionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminOptionControllerTest {

    OptionService service = mock(OptionService.class);
    MockMvc mvc = MockMvcBuilders.standaloneSetup(new AdminOptionController(service)).build();

    @Test
    void testDeleteOption() throws Exception {

        mvc.perform(delete("/api/admin/options/" + UUID.randomUUID()))
                .andExpect(status().isOk());

        verify(service).delete(any());
    }
}