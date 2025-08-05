package com.MagicTheGathering.user.dto;

import com.MagicTheGathering.user.User;
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

}
