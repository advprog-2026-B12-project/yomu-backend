package id.ac.ui.cs.advprog.yomubackend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Isi komentar tidak boleh kosong!")
    @Size(max = 5000, message = "Isi komentar maksimal 5000 karakter!")
    private String content;
}
