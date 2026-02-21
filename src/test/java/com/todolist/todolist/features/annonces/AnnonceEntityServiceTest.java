package com.todolist.todolist.features.annonces;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.categories.entity.CategoryEntity;
import com.todolist.todolist.features.users.entity.UserEntity;
import com.todolist.todolist.utils.PaginatedResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.ws.rs.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AnnonceEntityServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction entityTransaction;

    @InjectMocks
    private AnnonceService annonceService;

    private UserEntity mockUserEntity;
    private CategoryEntity mockCategoryEntity;
    private AnnonceEntity mockAnnonceEntity;
    private AnnonceDTO mockAnnonceDTO;

    @BeforeEach
    void setUp() {
        mockUserEntity = new UserEntity();
        mockUserEntity.setId(1L);
        mockUserEntity.setUsername("testuser");

        mockCategoryEntity = new CategoryEntity();
        mockCategoryEntity.setId(10L);
        mockCategoryEntity.setLabel("Immobilier");

        mockAnnonceEntity = AnnonceEntity.builder()
                .id(100L)
                .title("Appartement centre-ville")
                .description("Beau logement")
                .adress("Paris")
                .mail("contact@test.com")
                .date(Timestamp.from(Instant.now()))
                .status(StatusEnum.DRAFT)
                .author(mockUserEntity)
                .categoryEntity(mockCategoryEntity)
                .build();

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

    }

    @Test
    void searchAnnonces_noFilter_returnsPaginatedResponse() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(annonceRepository.searchWithFilters(eq(entityManager), isNull(), isNull(), isNull(), eq(0), eq(5)))
                .thenReturn(List.of(mockAnnonceEntity));
        when(annonceRepository.countWithFilters(eq(entityManager), isNull(), isNull(), isNull()))
                .thenReturn(1L);

        try (MockedStatic<AnnonceMapper> mapperMock = mockStatic(AnnonceMapper.class)) {
            mapperMock.when(() -> AnnonceMapper.toDTO(mockAnnonceEntity)).thenReturn(mockAnnonceDTO);

            PaginatedResponse<AnnonceDTO> response = annonceService.searchAnnonces(null, null, null, 0, 5);

            assertNotNull(response);
            assertEquals(1, response.getData().size());
            assertEquals(1L, response.getTotalElements());
            assertEquals(1, response.getTotalPages());
            assertEquals(0, response.getPage());
            verify(entityManager).close();
        }
    }

    @Test
    void searchAnnonces_withKeywordAndStatus_returnsFilteredResults() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(annonceRepository.searchWithFilters(eq(entityManager), eq("appartement"), isNull(), eq(StatusEnum.DRAFT), eq(0), eq(10)))
                .thenReturn(List.of(mockAnnonceEntity));
        when(annonceRepository.countWithFilters(eq(entityManager), eq("appartement"), isNull(), eq(StatusEnum.DRAFT)))
                .thenReturn(1L);

        try (MockedStatic<AnnonceMapper> mapperMock = mockStatic(AnnonceMapper.class)) {
            mapperMock.when(() -> AnnonceMapper.toDTO(mockAnnonceEntity)).thenReturn(mockAnnonceDTO);

            PaginatedResponse<AnnonceDTO> response = annonceService.searchAnnonces("appartement", null, StatusEnum.DRAFT, 0, 10);

            assertEquals(1, response.getData().size());
        }
    }

    @Test
    void searchAnnonces_emptyResult_returnsZeroPages() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(annonceRepository.searchWithFilters(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of());
        when(annonceRepository.countWithFilters(any(), any(), any(), any()))
                .thenReturn(0L);

        try (MockedStatic<AnnonceMapper> ignored = mockStatic(AnnonceMapper.class)) {
            PaginatedResponse<AnnonceDTO> response = annonceService.searchAnnonces("inexistant", null, null, 0, 5);

            assertEquals(0, response.getTotalElements());
            assertEquals(0, response.getTotalPages());
            assertTrue(response.getData().isEmpty());
        }
    }


    @Test
    void findOne_existingId_returnsDTO() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(annonceRepository.findOne(entityManager, 100L)).thenReturn(mockAnnonceEntity);

        try (MockedStatic<AnnonceMapper> mapperMock = mockStatic(AnnonceMapper.class)) {
            mapperMock.when(() -> AnnonceMapper.toDTO(mockAnnonceEntity)).thenReturn(mockAnnonceDTO);

            AnnonceDTO result = annonceService.findOne(100L);

            assertNotNull(result);
            assertEquals(100L, result.getId());
            verify(entityManager).close();
        }
    }

    @Test
    void findOne_nonExistingId_throwsResourceNotFoundException() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(annonceRepository.findOne(entityManager, 999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> annonceService.findOne(999L));
        verify(entityManager).close();
    }

    @Test
    void createAnnonce_validDTO_returnsCreatedDTO() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(userRepository.findOne(1L)).thenReturn(mockUserEntity);
        when(categoryRepository.findOne(10L)).thenReturn(mockCategoryEntity);
        when(annonceRepository.create(eq(entityManager), any(AnnonceEntity.class))).thenReturn(mockAnnonceEntity);

        try (MockedStatic<AnnonceMapper> mapperMock = mockStatic(AnnonceMapper.class)) {
            mapperMock.when(() -> AnnonceMapper.toEntity(mockAnnonceDTO)).thenReturn(mockAnnonceEntity);
            mapperMock.when(() -> AnnonceMapper.toDTO(mockAnnonceEntity)).thenReturn(mockAnnonceDTO);

            AnnonceDTO result = annonceService.createAnnonce(mockAnnonceDTO);

            assertNotNull(result);
            verify(entityTransaction).begin();
            verify(entityTransaction).commit();
            verify(entityTransaction, never()).rollback();
            verify(entityManager).close();
        }
    }

    @Test
    void createAnnonce_authorNotFound_throwsResourceNotFoundException() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(userRepository.findOne(1L)).thenReturn(null);

        try (MockedStatic<AnnonceMapper> mapperMock = mockStatic(AnnonceMapper.class)) {
            mapperMock.when(() -> AnnonceMapper.toEntity(mockAnnonceDTO)).thenReturn(mockAnnonceEntity);

            assertThrows(ResourceNotFoundException.class, () -> annonceService.createAnnonce(mockAnnonceDTO));
            verify(entityTransaction).rollback();
            verify(entityTransaction, never()).commit();
        }
    }

    @Test
    void createAnnonce_categoryNotFound_throwsResourceNotFoundException() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(userRepository.findOne(1L)).thenReturn(mockUserEntity);
        when(categoryRepository.findOne(10L)).thenReturn(null);

        try (MockedStatic<AnnonceMapper> mapperMock = mockStatic(AnnonceMapper.class)) {
            mapperMock.when(() -> AnnonceMapper.toEntity(mockAnnonceDTO)).thenReturn(mockAnnonceEntity);

            assertThrows(ResourceNotFoundException.class, () -> annonceService.createAnnonce(mockAnnonceDTO));
            verify(entityTransaction).rollback();
            verify(entityTransaction, never()).commit();
        }
    }

    @Test
    void updateAnnonce_draftByOwner_updatesFieldsAndStatus() {
        AnnonceDTO updateDTO = AnnonceDTO.builder()
                .title("Nouveau titre")
                .description("Nouvelle description")
                .adress("Lyon")
                .mail("new@test.com")
                .status(StatusEnum.PUBLISHED)
                .author_id(1L)
                .category_id(10L)
                .build();

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(annonceRepository.findOne(entityManager, 100L)).thenReturn(mockAnnonceEntity);
        when(userRepository.findOne(1L)).thenReturn(mockUserEntity);
        when(categoryRepository.findOne(10L)).thenReturn(mockCategoryEntity);
        when(annonceRepository.update(eq(entityManager), any(AnnonceEntity.class))).thenReturn(mockAnnonceEntity);

        try (MockedStatic<AnnonceMapper> mapperMock = mockStatic(AnnonceMapper.class)) {
            mapperMock.when(() -> AnnonceMapper.toDTO(any())).thenReturn(updateDTO);

            AnnonceDTO result = annonceService.updateAnnonce(100L, updateDTO, 1L);

            assertNotNull(result);
            assertEquals("Nouveau titre", mockAnnonceEntity.getTitle());
            assertEquals("Nouvelle description", mockAnnonceEntity.getDescription());
            assertEquals(StatusEnum.PUBLISHED, mockAnnonceEntity.getStatus());
            verify(entityTransaction).commit();
        }
    }

    @Test
    void updateAnnonce_publishedByOwner_onlyUpdatesStatus() {
        mockAnnonceEntity.setStatus(StatusEnum.PUBLISHED);

        AnnonceDTO updateDTO = AnnonceDTO.builder()
                .title("Titre tenté")
                .description("Description tentée")
                .adress("Marseille")
                .mail("changed@test.com")
                .status(StatusEnum.ARCHIVED)
                .author_id(1L)
                .category_id(10L)
                .build();

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(annonceRepository.findOne(entityManager, 100L)).thenReturn(mockAnnonceEntity);
        when(annonceRepository.update(eq(entityManager), any(AnnonceEntity.class))).thenReturn(mockAnnonceEntity);

        try (MockedStatic<AnnonceMapper> mapperMock = mockStatic(AnnonceMapper.class)) {
            mapperMock.when(() -> AnnonceMapper.toDTO(any())).thenReturn(updateDTO);

            annonceService.updateAnnonce(100L, updateDTO, 1L);

            assertEquals("Appartement centre-ville", mockAnnonceEntity.getTitle());
            assertEquals(StatusEnum.ARCHIVED, mockAnnonceEntity.getStatus());
            verify(entityTransaction).commit();
        }
    }

    @Test
    void updateAnnonce_annonceNotFound_throwsResourceNotFoundException() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(annonceRepository.findOne(entityManager, 999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> annonceService.updateAnnonce(999L, mockAnnonceDTO, 1L));
        verify(entityTransaction).rollback();
    }

    @Test
    void updateAnnonce_notOwner_throwsForbiddenException() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(annonceRepository.findOne(entityManager, 100L)).thenReturn(mockAnnonceEntity);

        assertThrows(ForbiddenException.class,
                () -> annonceService.updateAnnonce(100L, mockAnnonceDTO, 2L));
        verify(entityTransaction).rollback();
    }

    @Test
    void deleteAnnonce_archivedByOwner_deletesSuccessfully() {
        mockAnnonceEntity.setStatus(StatusEnum.ARCHIVED);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(annonceRepository.findOne(entityManager, 100L)).thenReturn(mockAnnonceEntity);

        annonceService.deleteAnnonce(100L, 1L);

        verify(annonceRepository).delete(entityManager, 100L);
        verify(entityTransaction).commit();
        verify(entityTransaction, never()).rollback();
        verify(entityManager).close();
    }

    @Test
    void deleteAnnonce_notArchived_throwsNotArchivedException() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(annonceRepository.findOne(entityManager, 100L)).thenReturn(mockAnnonceEntity);

        assertThrows(NotArchivedException.class,
                () -> annonceService.deleteAnnonce(100L, 1L));
        verify(entityTransaction).rollback();
        verify(annonceRepository, never()).delete(any(), any());
    }

    @Test
    void deleteAnnonce_notOwner_throwsForbiddenException() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(annonceRepository.findOne(entityManager, 100L)).thenReturn(mockAnnonceEntity);

        assertThrows(ForbiddenException.class,
                () -> annonceService.deleteAnnonce(100L, 99L));
        verify(entityTransaction).rollback();
        verify(annonceRepository, never()).delete(any(), any());
    }

    @Test
    void deleteAnnonce_notFound_throwsResourceNotFoundException() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(annonceRepository.findOne(entityManager, 999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> annonceService.deleteAnnonce(999L, 1L));
        verify(entityTransaction).rollback();
        verify(annonceRepository, never()).delete(any(), any());
    }
}
