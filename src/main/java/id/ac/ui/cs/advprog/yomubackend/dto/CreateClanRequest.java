package id.ac.ui.cs.advprog.yomubackend.dto;

import lombok.Data;

@Data
public class CreateClanRequest {
    private Long userId;
    private String name;
    private String description;
}