package id.ac.ui.cs.advprog.yomubackend.clan.exception;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ApiMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "id.ac.ui.cs.advprog.yomubackend.clan")
public class ClanExceptionHandler {

    @ExceptionHandler({
            ClanNotFoundException.class,
            ClanJoinRequestNotFoundException.class
    })
    public ResponseEntity<ApiMessageResponse> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiMessageResponse(e.getMessage()));
    }

    @ExceptionHandler({
            UserAlreadyInClanException.class,
            UserNotInClanException.class,
            ClanNameBlankException.class,
            ClanNameAlreadyTakenException.class,
            PendingJoinRequestAlreadyExistsException.class,
            JoinRequestAlreadyResolvedException.class,
            ClanLeaderCannotLeaveException.class
    })
    public ResponseEntity<ApiMessageResponse> handleBadRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiMessageResponse(e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedClanActionException.class)
    public ResponseEntity<ApiMessageResponse> handleForbidden(UnauthorizedClanActionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiMessageResponse(e.getMessage()));
    }
}
