package id.ac.ui.cs.advprog.yomubackend.comment.dto;

import id.ac.ui.cs.advprog.yomubackend.comment.entity.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReactionRequest {

    @NotNull(message = "Tipe reaksi tidak boleh kosong!")
    private ReactionType reactionType;
}
