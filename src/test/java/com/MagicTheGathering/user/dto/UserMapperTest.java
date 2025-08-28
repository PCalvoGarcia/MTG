package com.MagicTheGathering.user.dto;

import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestAdmin;
import com.MagicTheGathering.user.dto.USER.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class UserMapperTest {

    @Test
    void when_fromEntityNoRoles_return_emptyRoles(){
        User user = new User();
        user.setId(1L);
        user.setEmail("testUser@email.com");
        user.setUsername("testUser");
        user.setRoles(null);

        UserResponse response = UserMapperDto.fromEntity(user);

        assertNotNull(response);
        assertNotNull(response.roles());
        assertTrue(response.roles().isEmpty(), "Expected roles to be empty when user has no roles");

    }

    @Test
    void when_fromEntity_return_null_from_nullUser(){
        User user = null;

        assertNull(UserMapperDto.fromEntity(user));
    }

    @Test
    void when_toEntity_return_null_from_nullUser(){
        UserRequest userRequest = null;

        assertNull(UserMapperDto.toEntity(userRequest));
    }

    @Test
    void when_toEntityAdmin_return_null_from_nullUser(){
        UserRequestAdmin userRequestAdmin = null;

        assertNull(UserMapperDto.toEntityAdmin(userRequestAdmin));
    }

}
