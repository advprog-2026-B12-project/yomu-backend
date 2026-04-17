package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ApiMessageResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.service.LeagueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeagueControllerTest {

    @Mock
    private LeagueService leagueService;

    @Mock
    private LeaderboardEntryResponse entry1;

    @Mock
    private LeaderboardEntryResponse entry2;

    private LeagueController leagueController;

    @BeforeEach
    void setUp() {
        leagueController = new LeagueController(leagueService);
    }

    @Test
    void getLeaderboard_shouldReturnOkAndEntries_whenDivisionHasData() {
        String division = "gold";
        List<LeaderboardEntryResponse> expected = List.of(entry1, entry2);

        when(leagueService.getLeaderboardByDivision(division)).thenReturn(expected);

        ResponseEntity<List<LeaderboardEntryResponse>> response =
                leagueController.getLeaderboard(division);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertSame(expected, response.getBody());

        verify(leagueService, times(1)).getLeaderboardByDivision(division);
        verifyNoMoreInteractions(leagueService);
    }

    @Test
    void getLeaderboard_shouldReturnOkAndEmptyList_whenDivisionHasNoData() {
        String division = "bronze";
        List<LeaderboardEntryResponse> expected = List.of();

        when(leagueService.getLeaderboardByDivision(division)).thenReturn(expected);

        ResponseEntity<List<LeaderboardEntryResponse>> response =
                leagueController.getLeaderboard(division);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        assertSame(expected, response.getBody());

        verify(leagueService, times(1)).getLeaderboardByDivision(division);
        verifyNoMoreInteractions(leagueService);
    }

    @Test
    void getLeaderboard_shouldPassExactDivisionToService() {
        String division = "diamond";

        when(leagueService.getLeaderboardByDivision(division)).thenReturn(List.of(entry1));

        ResponseEntity<List<LeaderboardEntryResponse>> response =
                leagueController.getLeaderboard(division);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(leagueService, times(1)).getLeaderboardByDivision("diamond");
        verifyNoMoreInteractions(leagueService);
    }

    @Test
    void getLeaderboard_shouldThrowException_whenServiceFails() {
        String division = "master";

        when(leagueService.getLeaderboardByDivision(division))
                .thenThrow(new RuntimeException("Failed to fetch leaderboard"));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> leagueController.getLeaderboard(division)
        );

        assertEquals("Failed to fetch leaderboard", thrown.getMessage());

        verify(leagueService, times(1)).getLeaderboardByDivision(division);
        verifyNoMoreInteractions(leagueService);
    }

    @Test
    void getLeaderboard_shouldThrowException_whenDivisionIsInvalid() {
        String division = "unknown-division";

        when(leagueService.getLeaderboardByDivision(division))
                .thenThrow(new IllegalArgumentException("Invalid division"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> leagueController.getLeaderboard(division)
        );

        assertEquals("Invalid division", thrown.getMessage());

        verify(leagueService, times(1)).getLeaderboardByDivision(division);
        verifyNoMoreInteractions(leagueService);
    }

    @Test
    void getLeaderboard_shouldThrowException_whenDivisionIsNull() {
        when(leagueService.getLeaderboardByDivision(null))
                .thenThrow(new IllegalArgumentException("Division must not be null"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> leagueController.getLeaderboard(null)
        );

        assertEquals("Division must not be null", thrown.getMessage());

        verify(leagueService, times(1)).getLeaderboardByDivision(null);
        verifyNoMoreInteractions(leagueService);
    }

    @Test
    void getLeaderboard_shouldThrowException_whenDivisionIsEmpty() {
        String division = "";

        when(leagueService.getLeaderboardByDivision(division))
                .thenThrow(new IllegalArgumentException("Division must not be blank"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> leagueController.getLeaderboard(division)
        );

        assertEquals("Division must not be blank", thrown.getMessage());

        verify(leagueService, times(1)).getLeaderboardByDivision(division);
        verifyNoMoreInteractions(leagueService);
    }

    @Test
    void getLeaderboard_shouldThrowException_whenDivisionIsBlank() {
        String division = "   ";

        when(leagueService.getLeaderboardByDivision(division))
                .thenThrow(new IllegalArgumentException("Division must not be blank"));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> leagueController.getLeaderboard(division)
        );

        assertEquals("Division must not be blank", thrown.getMessage());

        verify(leagueService, times(1)).getLeaderboardByDivision(division);
        verifyNoMoreInteractions(leagueService);
    }

    @Test
    void triggerSeasonReset_shouldReturnOkAndSuccessMessage() {
        ResponseEntity<ApiMessageResponse> response = leagueController.triggerSeasonReset();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Season reset triggered", response.getBody().getMessage());

        verify(leagueService, times(1)).triggerSeasonReset();
        verifyNoMoreInteractions(leagueService);
    }

    @Test
    void triggerSeasonReset_shouldThrowException_whenServiceFails() {
        doThrow(new RuntimeException("Season reset failed"))
                .when(leagueService).triggerSeasonReset();

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> leagueController.triggerSeasonReset()
        );

        assertEquals("Season reset failed", thrown.getMessage());

        verify(leagueService, times(1)).triggerSeasonReset();
        verifyNoMoreInteractions(leagueService);
    }
}