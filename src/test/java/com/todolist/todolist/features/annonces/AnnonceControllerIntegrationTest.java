package com.todolist.todolist.features.annonces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todolist.todolist.AbstractIntegrationTest;
import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.annonces.repository.AnnonceRepository;
import com.todolist.todolist.features.auth.service.JwtService;
import com.todolist.todolist.features.categories.entity.CategoryEntity;
import com.todolist.todolist.features.categories.repository.CategoryRepository;
import com.todolist.todolist.features.users.entity.UserEntity;
import com.todolist.todolist.features.users.enums.RoleEnum;
import com.todolist.todolist.features.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
class AnnonceControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtService jwtService;

    private UserEntity userEntity;
    private UserEntity adminEntity;
    private CategoryEntity category;
    private AnnonceEntity annonce;

    private String userToken;
    private String adminToken;

    // ============================================================
    // Setup
    // ============================================================

    @BeforeEach
    void setUp() {
        annonceRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        userEntity = userRepository.save(UserEntity.builder()
                .username("nikola")
                .email("nikola@mail.com")
                .password("password123")
                .role(RoleEnum.ROLE_USER)
                .build());

        adminEntity = userRepository.save(UserEntity.builder()
                .username("admin")
                .email("admin@mail.com")
                .password("admin123")
                .role(RoleEnum.ROLE_ADMIN)
                .build());

        category = categoryRepository.save(CategoryEntity.builder()
                .label("Meuble")
                .build());

        annonce = annonceRepository.save(AnnonceEntity.builder()
                .title("Canapé")
                .description("Beau canapé")
                .adress("Paris")
                .mail("nikola@mail.com")
                .status(StatusEnum.DRAFT)
                .version(0L)
                .author(userEntity)
                .categoryEntity(category)
                .date(Timestamp.from(Instant.now()))
                .build());

        userToken = jwtService.generateToken(userEntity);
        adminToken = jwtService.generateToken(adminEntity);
    }

    // ============================================================
    // Exercice 8.2 — Endpoint protégé sans token → 401
    // ============================================================

    @Test
    void findAll_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/annonces"))
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // Exercice 8.3 — Token invalide → 401
    // ============================================================

    @Test
    void findAll_shouldReturn401_whenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/annonces")
                        .header("Authorization", "Bearer token.invalide.ici"))
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // Exercice 8.4 — Rôle insuffisant → 403
    // ============================================================

    @Test
    void delete_shouldReturn403_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(delete("/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // Exercice 8.5 — CRUD complet
    // ============================================================

    @Test
    void findAll_shouldReturnPage_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/annonces")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Canapé"));
    }

    @Test
    void findOne_shouldReturnAnnonce_whenExists() throws Exception {
        mockMvc.perform(get("/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(annonce.getId()))
                .andExpect(jsonPath("$.title").value("Canapé"));
    }

    @Test
    void findOne_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/annonces/99999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn201_whenValid() throws Exception {
        String body = """
            {
              "title": "Table",
              "description": "Belle table",
              "adress": "Lyon",
              "mail": "nikola@mail.com",
              "status": "DRAFT",
              "author_id": %d,
              "category_id": %d
            }
            """.formatted(userEntity.getId(), category.getId());

        mockMvc.perform(post("/annonces")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Table"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void create_shouldReturn400_whenMissingFields() throws Exception {
        AnnonceDTO dto = AnnonceDTO.builder().build();

        mockMvc.perform(post("/annonces")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturn200_whenAuthorUpdates() throws Exception {
        String body = """
            {
              "title": "Canapé modifié",
              "description": "Nouvelle description",
              "adress": "Paris",
              "mail": "nikola@mail.com",
              "status": "DRAFT",
              "author_id": %d,
              "category_id": %d
            }
            """.formatted(userEntity.getId(), category.getId());

        mockMvc.perform(put("/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Canapé modifié"));
    }

    @Test
    void update_shouldReturn403_whenUserTriesToArchive() throws Exception {
        String body = """
            {
              "title": "Canapé",
              "description": "Beau canapé",
              "adress": "Paris",
              "mail": "nikola@mail.com",
              "status": "ARCHIVED",
              "author_id": %d,
              "category_id": %d
            }
            """.formatted(userEntity.getId(), category.getId());

        mockMvc.perform(put("/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_shouldReturn204_whenAdmin() throws Exception {
        mockMvc.perform(delete("/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
