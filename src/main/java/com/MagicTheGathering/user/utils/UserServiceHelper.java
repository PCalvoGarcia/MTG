package com.MagicTheGathering.user.utils;

import com.MagicTheGathering.role.Role;
import com.MagicTheGathering.user.UserRepository;
import com.MagicTheGathering.user.dto.UserMapperDto;
import com.MagicTheGathering.user.dto.UserResponse;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestUpdateAdmin;
import com.MagicTheGathering.user.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserServiceHelper {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceHelper( UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void checkEmail(String request) {
        Optional<User> isExistingEmail = userRepository.findByEmail(request);
        if (isExistingEmail.isPresent()) {
            throw new RuntimeException("exception");
        }
    }

    public void checkUsername(String request) {
        Optional<User> isExistingUsername = userRepository.findByUsername(request);
        if (isExistingUsername.isPresent()) {
            throw new RuntimeException("exception");
        }
    }

    public User checkUserId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("exception");
        return user;
    }

    public Optional<User> getUserLogin(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()){
            throw new UsernameNotFoundException(username + " does not exist.");
        }
        return optionalUser;
    }

    public String getEncodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public List<UserResponse> getAllUserResponseList() {
        List<UserResponse> userResponseList = userRepository.findAll()
                .stream()
                .map(UserMapperDto::fromEntity)
                .collect(Collectors.toList());
        return userResponseList;
    }

    public void updateUserData(UserRequestUpdateAdmin request, User user) {
        String username = request.username() != null && !request.username().isEmpty()
                ? request.username() :
                user.getUsername();

        String email = request.email() != null && !request.email().isEmpty()
                ? request.email() :
                user.getEmail();

        String password = request.password() != null && !request.password().isEmpty()
                ? this.getEncodePassword(request.password()) :
                user.getPassword();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);


        Set<Role> roles = new HashSet<>();
        roles.add(request.role());
        user.setRoles(roles);
    }


}