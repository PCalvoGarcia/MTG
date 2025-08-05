package com.MagicTheGathering.user;


import com.MagicTheGathering.auth.AuthServiceHelper;
import com.MagicTheGathering.role.Role;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestAdmin;
import com.MagicTheGathering.user.dto.USER.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthServiceHelper authServiceHelper;

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN"})
    void should_getAllUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)))
                .andExpect(jsonPath("$.[0].username").value("admin"))
                .andExpect(jsonPath("$.[0].email").value("admin@happytravel.com"))
                .andExpect(jsonPath("$.[1].username").value("user"))
                .andExpect(jsonPath("$.[1].email").value("user@happytravel.com"))
                .andExpect(jsonPath("$.[2].username").value("john_doe"))
                .andExpect(jsonPath("$.[2].email").value("john@example.com"))
                .andExpect(jsonPath("$.[3].username").value("jane_smith"))
                .andExpect(jsonPath("$.[3].email").value("jane@example.com"))
                .andExpect(jsonPath("$.[4].username").value("mike_wilson"))
                .andExpect(jsonPath("$.[4].email").value("mike@example.com"))
                .andExpect(jsonPath("$.[5].username").value("sarah_jones"))
                .andExpect(jsonPath("$.[5].email").value("sarah@example.com"))
                .andExpect(jsonPath("$.[6].username").value("david_brown"))
                .andExpect(jsonPath("$.[6].email").value("david@example.com"));
    }

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN"})
    void should_getUserById() throws Exception {
        Long userId = 5L;

        mockMvc.perform(get("/api/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("mike_wilson"))
                .andExpect(jsonPath("$.email").value("mike@example.com"));
    }

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN", "USER"})
    void should_refreshToken() throws Exception {
        Mockito.when(authServiceHelper.handleRefreshToken("validToken"))
                .thenReturn(ResponseEntity.ok(Map.of("accessToken", "newAccessToken123")));

        Map<String, String> requestBody = Map.of("refreshToken", "validToken");

        mockMvc.perform(post("/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken123"));
    }

    @Test
    @Transactional
    void should_registerUser_fromRequest() throws Exception{
        UserRequest userRequest = new UserRequest("userTest", "usertest@test.com", "password123");

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("userTest"))
                .andExpect(jsonPath("$.email").value("usertest@test.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN"})
    void should_registerUserByAdmin_fromRequest() throws Exception{
        UserRequestAdmin userRequest = new UserRequestAdmin("userTest", "usertest@test.com", "password123", Role.ADMIN);

        mockMvc.perform(post("/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("userTest"))
                .andExpect(jsonPath("$.email").value("usertest@test.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN"})
    void should_updateUser_fromRequest() throws Exception{
        Long userId = 1L;
        UserRequestAdmin userRequest = new UserRequestAdmin("updateTest", "updatetest@test.com", "password123", Role.USER);

        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("updateTest"))
                .andExpect(jsonPath("$.email").value("updatetest@test.com"));
    }

    @Test
    @Transactional
    @WithMockUser(roles = {"ADMIN"})
    void should_deleteUser ()throws Exception{
        Long userId = 1L;

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
}
