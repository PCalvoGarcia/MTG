package com.MagicTheGathering.user.dto;

import com.MagicTheGathering.user.User;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestAdmin;
import com.MagicTheGathering.user.dto.USER.UserRequest;
import org.springframework.stereotype.Component;

@Component
public class UserMapperDto {
    public static UserResponse fromEntity(User user) {
        java.util.Set<String> roles = user.getRoles() == null ? java.util.Collections.emptySet()
                : user.getRoles()
                .stream()
                .map(Enum::name)
                .collect(java.util.stream.Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }

    public static User toEntity(UserRequest userRequest) {
        User user = User.builder()
                .username(userRequest.username())
                .email(userRequest.email())
                .build();

        return user;
    }
    public static User toEntityAdmin(UserRequestAdmin userRequest) {

        User user = User.builder()
                .username(userRequest.username())
                .email(userRequest.email())
                .build();

        return user;
    }
}
