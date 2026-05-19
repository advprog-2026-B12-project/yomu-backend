package id.ac.ui.cs.advprog.yomubackend.clan.exception;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ApiMessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ClanExceptionTest {

    // ── Exception constructors ────────────────────────────────────────────────

    @Test
    void clanNotFoundException_storesMessage() {
        ClanNotFoundException ex = new ClanNotFoundException("Clan not found");
        assertEquals("Clan not found", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void unauthorizedClanActionException_storesMessage() {
        UnauthorizedClanActionException ex = new UnauthorizedClanActionException("Not authorized");
        assertEquals("Not authorized", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void userAlreadyInClanException_storesMessage() {
        UserAlreadyInClanException ex = new UserAlreadyInClanException("Already in clan");
        assertEquals("Already in clan", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void userNotInClanException_storesMessage() {
        UserNotInClanException ex = new UserNotInClanException("Not in clan");
        assertEquals("Not in clan", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    // ── GlobalExceptionHandler ────────────────────────────────────────────────

    @Test
    void handleClanNotFound_returns404WithMessage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ClanNotFoundException ex = new ClanNotFoundException("Clan not found");

        ResponseEntity<ApiMessageResponse> response = handler.handleClanNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Clan not found", response.getBody().getMessage());
    }

    @Test
    void handleBadRequest_withUserAlreadyInClan_returns400() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        UserAlreadyInClanException ex = new UserAlreadyInClanException("Already in clan");

        ResponseEntity<ApiMessageResponse> response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Already in clan", response.getBody().getMessage());
    }

    @Test
    void handleBadRequest_withUserNotInClan_returns400() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        UserNotInClanException ex = new UserNotInClanException("Not in clan");

        ResponseEntity<ApiMessageResponse> response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not in clan", response.getBody().getMessage());
    }

    @Test
    void handleBadRequest_withIllegalArgument_returns400() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ApiMessageResponse> response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid argument", response.getBody().getMessage());
    }

    @Test
    void handleForbidden_returns403WithMessage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        UnauthorizedClanActionException ex = new UnauthorizedClanActionException("Forbidden");

        ResponseEntity<ApiMessageResponse> response = handler.handleForbidden(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Forbidden", response.getBody().getMessage());
    }
}
