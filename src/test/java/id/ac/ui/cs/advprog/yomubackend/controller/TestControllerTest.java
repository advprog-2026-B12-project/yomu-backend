package id.ac.ui.cs.advprog.yomubackend.controller;

import id.ac.ui.cs.advprog.yomubackend.entity.Message;
import id.ac.ui.cs.advprog.yomubackend.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    @Mock
    private MessageRepository repository;

    @InjectMocks
    private TestController testController;

    private Message message;

    @BeforeEach
    void setUp() {
        message = new Message();
        message.setId(1L);
        message.setContent("Test Message");
    }

    @Test
    void testGetAllMessages() {
        when(repository.findAll()).thenReturn(Collections.singletonList(message));
        List<Message> result = testController.getAllMessages();
        assertEquals(1, result.size());
        assertEquals("Test Message", result.getFirst().getContent());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testInitData_WhenRepositoryIsEmpty() throws Exception {
        when(repository.count()).thenReturn(0L);
        CommandLineRunner runner = testController.initData();
        runner.run();
        verify(repository, times(1)).count();
        verify(repository, times(1)).save(any(Message.class));
    }

    @Test
    void testInitData_WhenRepositoryIsNotEmpty() throws Exception {
        when(repository.count()).thenReturn(1L);
        CommandLineRunner runner = testController.initData();
        runner.run();
        verify(repository, times(1)).count();
        verify(repository, never()).save(any(Message.class));
    }
}