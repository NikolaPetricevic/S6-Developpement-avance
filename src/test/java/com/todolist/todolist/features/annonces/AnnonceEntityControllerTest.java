package com.todolist.todolist.features.annonces;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.users.entity.UserEntity;
import com.todolist.todolist.utils.PaginatedResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnonceEntityControllerTest {

    @Mock
    private AnnonceService annonceService;
    @Mock
    private AuthService authService;

    @InjectMocks
    private AnnonceController annonceController;

    private static final String VALID_AUTH_HEADER = "Bearer valid-token";
    private static final String VALID_TOKEN = "valid-token";
    private static final String INVALID_AUTH_HEADER = "Bearer invalid-token";
    private static final String INVALID_TOKEN = "invalid-token";

    private UserEntity mockUserEntity;
    private AnnonceDTO mockAnnonceDTO;

    @BeforeEach
    void setUp() {
        mockUserEntity = new UserEntity();
        mockUserEntity.setId(1L);
        mockUserEntity.setUsername("testuser");

        mockAnnonceDTO = AnnonceDTO.builder()
                .id(100L)
                .title("Appartement centre-ville")
                .description("Beau logement")
                .adress("Paris")
                .mail("contact@test.com")
                .status(StatusEnum.DRAFT)
                .author_id(1L)
                .category_id(10L)
                .build();

        lenient().when(authService.extractToken(VALID_AUTH_HEADER)).thenReturn(VALID_TOKEN);
        lenient().when(authService.extractToken(INVALID_AUTH_HEADER)).thenReturn(INVALID_TOKEN);
        lenient().when(authService.isValidToken(VALID_TOKEN)).thenReturn(true);
        lenient().when(authService.isValidToken(INVALID_TOKEN)).thenReturn(false);
        lenient().when(authService.isValidToken(null)).thenReturn(false);
        lenient().when(authService.getCurrentUser(VALID_TOKEN)).thenReturn(mockUserEntity);
        lenient().when(authService.getCurrentUser(INVALID_TOKEN)).thenReturn(null);
    }

    @Test
    void getAllAnnonces_validToken_returns200() {
        PaginatedResponse<AnnonceDTO> paginatedResponse = new PaginatedResponse<>(
                List.of(mockAnnonceDTO), 0, 20, 1L, 1);
        when(annonceService.searchAnnonces(null, null, null, 0, 20))
                .thenReturn(paginatedResponse);

        Response response = annonceController.getAllAnnonces(0, 20, null, null, null, VALID_AUTH_HEADER);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getAllAnnonces_invalidToken_returns401() {
        Response response = annonceController.getAllAnnonces(0, 20, null, null, null, INVALID_AUTH_HEADER);

        assertEquals(401, response.getStatus());
        verify(annonceService, never()).searchAnnonces(any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void getAllAnnonces_noToken_returns401() {
        Response response = annonceController.getAllAnnonces(0, 20, null, null, null, null);

        assertEquals(401, response.getStatus());
        verify(annonceService, never()).searchAnnonces(any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void getAllAnnonces_withFilters_returns200() {
        PaginatedResponse<AnnonceDTO> paginatedResponse = new PaginatedResponse<>(
                List.of(mockAnnonceDTO), 0, 5, 1L, 1);
        when(annonceService.searchAnnonces("appartement", 10L, StatusEnum.DRAFT, 0, 5))
                .thenReturn(paginatedResponse);

        Response response = annonceController.getAllAnnonces(0, 5, "appartement", 10L, StatusEnum.DRAFT, VALID_AUTH_HEADER);

        assertEquals(200, response.getStatus());
    }

    @Test
    void getAnnonce_validTokenAndExistingId_returns200() {
        when(annonceService.findOne(100L)).thenReturn(mockAnnonceDTO);

        Response response = annonceController.getAnnonce(100L, VALID_AUTH_HEADER);

        assertEquals(200, response.getStatus());
        assertEquals(mockAnnonceDTO, response.getEntity());
    }

    @Test
    void getAnnonce_invalidToken_returns401() {
        Response response = annonceController.getAnnonce(100L, INVALID_AUTH_HEADER);

        assertEquals(401, response.getStatus());
        verify(annonceService, never()).findOne(any());
    }

    @Test
    void getAnnonce_notFound_propagatesException() {
        when(annonceService.findOne(999L)).thenThrow(new ResourceNotFoundException("Annonce", 999L));

        assertThrows(ResourceNotFoundException.class,
                () -> annonceController.getAnnonce(999L, VALID_AUTH_HEADER));
    }

    @Test
    void createAnnonce_validToken_returns201() {
        when(annonceService.createAnnonce(mockAnnonceDTO)).thenReturn(mockAnnonceDTO);

        Response response = annonceController.createAnnonce(mockAnnonceDTO, VALID_AUTH_HEADER);

        assertEquals(201, response.getStatus());
        assertEquals(mockAnnonceDTO, response.getEntity());
        verify(annonceService).createAnnonce(mockAnnonceDTO);
    }

    @Test
    void createAnnonce_invalidToken_returns401() {
        Response response = annonceController.createAnnonce(mockAnnonceDTO, INVALID_AUTH_HEADER);

        assertEquals(401, response.getStatus());
        verify(annonceService, never()).createAnnonce(any());
    }

    @Test
    void createAnnonce_serviceThrows_propagatesException() {
        when(annonceService.createAnnonce(mockAnnonceDTO))
                .thenThrow(new ResourceNotFoundException("User", 1L));

        assertThrows(ResourceNotFoundException.class,
                () -> annonceController.createAnnonce(mockAnnonceDTO, VALID_AUTH_HEADER));
    }

    @Test
    void updateAnnonce_validTokenAndOwner_returns200() {
        when(annonceService.updateAnnonce(100L, mockAnnonceDTO, 1L)).thenReturn(mockAnnonceDTO);

        Response response = annonceController.updateAnnonce(100L, mockAnnonceDTO, VALID_AUTH_HEADER);

        assertEquals(200, response.getStatus());
        assertEquals(mockAnnonceDTO, response.getEntity());
    }

    @Test
    void updateAnnonce_invalidToken_returns401() {
        Response response = annonceController.updateAnnonce(100L, mockAnnonceDTO, INVALID_AUTH_HEADER);

        assertEquals(401, response.getStatus());
        verify(annonceService, never()).updateAnnonce(any(), any(), any());
    }

    @Test
    void updateAnnonce_notOwner_returns403() {
        when(annonceService.updateAnnonce(100L, mockAnnonceDTO, 1L))
                .thenThrow(new ForbiddenException("You are not allowed to update this annonce"));

        Response response = annonceController.updateAnnonce(100L, mockAnnonceDTO, VALID_AUTH_HEADER);

        assertEquals(403, response.getStatus());
    }

    @Test
    void updateAnnonce_concurrentModification_returns409() {
        when(annonceService.updateAnnonce(100L, mockAnnonceDTO, 1L))
                .thenThrow(new OptimisticLockException("Conflict"));

        Response response = annonceController.updateAnnonce(100L, mockAnnonceDTO, VALID_AUTH_HEADER);

        assertEquals(409, response.getStatus());
    }

    @Test
    void updateAnnonce_notFound_propagatesException() {
        when(annonceService.updateAnnonce(999L, mockAnnonceDTO, 1L))
                .thenThrow(new ResourceNotFoundException("Annonce", 999L));

        assertThrows(ResourceNotFoundException.class,
                () -> annonceController.updateAnnonce(999L, mockAnnonceDTO, VALID_AUTH_HEADER));
    }

    @Test
    void deleteAnnonce_validTokenAndOwner_returns204() {
        doNothing().when(annonceService).deleteAnnonce(100L, 1L);

        Response response = annonceController.deleteAnnonce(100L, VALID_AUTH_HEADER);

        assertEquals(204, response.getStatus());
        verify(annonceService).deleteAnnonce(100L, 1L);
    }

    @Test
    void deleteAnnonce_invalidToken_returns401() {
        Response response = annonceController.deleteAnnonce(100L, INVALID_AUTH_HEADER);

        assertEquals(401, response.getStatus());
        verify(annonceService, never()).deleteAnnonce(any(), any());
    }

    @Test
    void deleteAnnonce_notOwner_returns403() {
        doThrow(new ForbiddenException("You are not allowed to delete this annonce"))
                .when(annonceService).deleteAnnonce(100L, 1L);

        Response response = annonceController.deleteAnnonce(100L, VALID_AUTH_HEADER);

        assertEquals(403, response.getStatus());
    }

    @Test
    void deleteAnnonce_notFound_propagatesException() {
        doThrow(new ResourceNotFoundException("Annonce", 100L))
                .when(annonceService).deleteAnnonce(100L, 1L);

        assertThrows(ResourceNotFoundException.class,
                () -> annonceController.deleteAnnonce(100L, VALID_AUTH_HEADER));
    }

    @Test
    void deleteAnnonce_notArchived_returns403() {
        doThrow(new NotArchivedException("The annonce is not archived."))
                .when(annonceService).deleteAnnonce(100L, 1L);

        Response response = annonceController.deleteAnnonce(100L, VALID_AUTH_HEADER);

        assertEquals(403, response.getStatus());
    }
}