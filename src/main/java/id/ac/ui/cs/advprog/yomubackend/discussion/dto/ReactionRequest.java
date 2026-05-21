package id.ac.ui.cs.advprog.yomubackend.discussion.dto;

import id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReactionRequest {

    @NotNull(message = "Tipe reaksi tidak boleh kosong!")
    private ReactionType reactionType;
}
