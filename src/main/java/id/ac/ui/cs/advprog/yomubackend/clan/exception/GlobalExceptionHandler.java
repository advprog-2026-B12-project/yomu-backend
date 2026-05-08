package id.ac.ui.cs.advprog.yomubackend.clan.exception;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ApiMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClanNotFoundException.class)
    public ResponseEntity<ApiMessageResponse> handleClanNotFound(ClanNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiMessageResponse(e.getMessage()));
    }

    @ExceptionHandler({
            UserAlreadyInClanException.class,
            UserNotInClanException.class,
            IllegalArgumentException.class
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