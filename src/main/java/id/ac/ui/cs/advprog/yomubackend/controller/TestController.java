package id.ac.ui.cs.advprog.yomubackend.controller;

import id.ac.ui.cs.advprog.yomubackend.entity.Message;
import id.ac.ui.cs.advprog.yomubackend.repository.MessageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Configuration
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:3000") // NextJs
public class TestController {

    private final MessageRepository repository;

    public TestController(MessageRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Message> getAllMessages() {
        return repository.findAll();
    }

    // SEEDER (Hanya untuk demo, ini akan isi data otomatis saat aplikasi nyala)
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (repository.count() == 0) {
                Message msg = new Message();
                msg.setContent("Halo! Ini data dari PostgreSQL + Spring Boot");
                repository.save(msg);
                System.out.println("Data dummy berhasil disimpan ke Database!");
            }
        };
    }
}