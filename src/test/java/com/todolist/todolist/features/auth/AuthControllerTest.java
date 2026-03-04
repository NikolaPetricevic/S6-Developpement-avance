package com.todolist.todolist.features.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todolist.todolist.AbstractIntegrationTest;
import com.todolist.todolist.features.auth.dto.LoginRequestDTO;
import com.todolist.todolist.features.users.entity.UserEntity;
import com.todolist.todolist.features.users.enums.RoleEnum;
import com.todolist.todolist.features.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class AuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;


    // ============================================================
    // Setup
    // ============================================================

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userRepository.save(UserEntity.builder()
                .username("nikola")
                .email("nikola@mail.com")
                .password("password123")
                .role(RoleEnum.ROLE_USER)
                .build());
    }


    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("nikola");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("nikola"));
    }

    @Test
    void login_shouldReturn401_whenPasswordIsWrong() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("nikola");
        request.setPassword("mauvais");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

}
