package com.todolist.todolist.features.annonces;

import com.todolist.todolist.exceptions.ForbiddenException;
import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.annonces.exceptions.AnnonceNotEditableException;
import com.todolist.todolist.features.annonces.exceptions.AnnonceNotFoundException;
import com.todolist.todolist.features.annonces.exceptions.AuthorNotFoundException;
import com.todolist.todolist.features.annonces.mapper.AnnonceMapper;
import com.todolist.todolist.features.annonces.repository.AnnonceRepository;
import com.todolist.todolist.features.annonces.service.AnnonceServiceImpl;
import com.todolist.todolist.features.categories.entity.CategoryEntity;
import com.todolist.todolist.features.categories.exceptions.CategoryNotFoundException;
import com.todolist.todolist.features.categories.repository.CategoryRepository;
import com.todolist.todolist.features.users.entity.UserEntity;
import com.todolist.todolist.features.users.enums.RoleEnum;
import com.todolist.todolist.features.users.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceImplTest {

    @Mock
    private AnnonceRepository annonceRepository;
    @Mock
    private AnnonceMapper annonceMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnnonceServiceImpl annonceService;

    private UserEntity author;
    private CategoryEntity category;
    private AnnonceEntity annonce;
    private AnnonceDTO annonceDTO;

    @BeforeEach
    void setUp() {
        author = UserEntity.builder()
                .id(1L)
                .username("nikola")
                .email("nikola@mail.com")
                .password("hashed")
                .role(RoleEnum.ROLE_USER)
                .build();

        category = CategoryEntity.builder()
                .id(1L)
                .label("Meuble")
                .build();

        annonce = AnnonceEntity.builder()
                .id(14L)
                .version(0L)
                .title("Titre")
                .description("Description")
                .adress("Adresse")
                .mail("test@mail.com")
                .status(StatusEnum.DRAFT)
                .author(author)
                .categoryEntity(category)
                .build();

        annonceDTO = AnnonceDTO.builder()
                .id(14L)
                .title("Titre")
                .description("Description")
                .adress("Adresse")
                .mail("test@mail.com")
                .status(StatusEnum.DRAFT)
                .author_id(1L)
                .category_id(1L)
                .build();
    }

    // ============================================================
    // findOne
    // ============================================================

    @Test
    void findOne_shouldReturnDTO_whenAnnonceExists() {
        when(annonceRepository.findById(14L)).thenReturn(Optional.of(annonce));
        when(annonceMapper.toDTO(annonce)).thenReturn(annonceDTO);

        AnnonceDTO result = annonceService.findOne(14L);

        assertThat(result).isEqualTo(annonceDTO);
        verify(annonceRepository).findById(14L);
    }

    @Test
    void findOne_shouldThrow_whenAnnonceNotFound() {
        when(annonceRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(AnnonceNotFoundException.class, () -> annonceService.findOne(99L));
    }

    // ============================================================
    // create
    // ============================================================

    @Test
    void create_shouldReturnDTO_whenValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(annonceMapper.toEntity(annonceDTO)).thenReturn(annonce);
        when(annonceRepository.save(annonce)).thenReturn(annonce);
        when(annonceMapper.toDTO(annonce)).thenReturn(annonceDTO);

        AnnonceDTO result = annonceService.create(annonceDTO);

        assertThat(result).isEqualTo(annonceDTO);
        verify(annonceRepository).save(annonce);
    }

    @Test
    void create_shouldThrow_whenAuthorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> annonceService.create(annonceDTO));

        verify(annonceRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenCategoryNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> annonceService.create(annonceDTO));

        verify(annonceRepository, never()).save(any());
    }

    // ============================================================
    // update — règles métier
    // ============================================================

    private void mockSecurityContext(UserEntity user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void update_shouldThrow_whenNotAuthor() {
        UserEntity otherUser = UserEntity.builder()
                .id(2L)
                .username("other")
                .role(RoleEnum.ROLE_USER)
                .build();

        mockSecurityContext(otherUser);
        when(annonceRepository.findById(14L)).thenReturn(Optional.of(annonce));

        assertThrows(ForbiddenException.class, () -> annonceService.update(14L, annonceDTO));
    }

    @Test
    void update_shouldThrow_whenAnnonceIsPublished() {
        annonce.setStatus(StatusEnum.PUBLISHED);
        mockSecurityContext(author);
        when(annonceRepository.findById(14L)).thenReturn(Optional.of(annonce));

        assertThrows(AnnonceNotEditableException.class, () -> annonceService.update(14L, annonceDTO));
    }

    @Test
    void update_shouldThrow_whenUserTriesToArchive() {
        annonceDTO.setStatus(StatusEnum.ARCHIVED);
        annonceDTO.setAuthor_id(null);
        annonceDTO.setCategory_id(null);
        mockSecurityContext(author);
        when(annonceRepository.findById(14L)).thenReturn(Optional.of(annonce));

        assertThrows(ForbiddenException.class, () -> annonceService.update(14L, annonceDTO));
    }

    @Test
    void update_shouldSucceed_whenAdminArchives() {
        UserEntity admin = UserEntity.builder()
                .id(1L)
                .username("nikola")
                .role(RoleEnum.ROLE_ADMIN)
                .build();

        annonceDTO.setStatus(StatusEnum.ARCHIVED);
        mockSecurityContext(admin);
        when(annonceRepository.findById(14L)).thenReturn(Optional.of(annonce));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(annonceRepository.save(annonce)).thenReturn(annonce);
        when(annonceMapper.toDTO(annonce)).thenReturn(annonceDTO);

        assertDoesNotThrow(() -> annonceService.update(14L, annonceDTO));
    }

    // ============================================================
    // delete
    // ============================================================

    @Test
    void delete_shouldDelete_whenAnnonceExists() {
        when(annonceRepository.findById(14L)).thenReturn(Optional.of(annonce));

        annonceService.delete(14L);

        verify(annonceRepository).deleteById(14L);
    }

    @Test
    void delete_shouldThrow_whenAnnonceNotFound() {
        assertThatThrownBy(() -> annonceService.delete(1L));

        verify(annonceRepository, never()).deleteById(any());
    }
}
