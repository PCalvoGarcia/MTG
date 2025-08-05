package com.MagicTheGathering.user;

import com.MagicTheGathering.role.Role;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestAdmin;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestUpdateAdmin;
import com.MagicTheGathering.user.dto.UserMapperDto;
import com.MagicTheGathering.user.dto.USER.UserRequest;
import com.MagicTheGathering.user.dto.UserResponse;
import com.MagicTheGathering.user.utils.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserServiceHelper userServiceHelper;


    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = optionalUser.orElseThrow();
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }

    public UserResponse registerUser(UserRequest request){
        Optional<User> isExistingUsername = userRepository.findByUsername(request.username());
        if (isExistingUsername.isPresent()) {
            throw new RuntimeException("Username already exist");
        }
        Optional<User> isExistingEmail = userRepository.findByEmail(request.email());
        if (isExistingEmail.isPresent()) {
            throw new RuntimeException("Email already exist");
        }
        User user = UserMapperDto.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(Role.USER));

        User savedUser = userRepository.save(user);

        return UserMapperDto.fromEntity(savedUser);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
    }


    public UserResponse registerUserByAdmin(UserRequestAdmin request) {
        try{
            userServiceHelper.checkUsername(request.username());
            userServiceHelper.checkEmail(request.email());

            User user = UserMapperDto.toEntityAdmin(request);
            user.setPassword(userServiceHelper.getEncodePassword(request.password()));
            user.setRoles(Set.of(request.role()));

            User savedUser = userRepository.save(user);


            return UserMapperDto.fromEntity(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Username or email already exists");
        }

    }

    public List<UserResponse> getAllUsers() {
        if (userServiceHelper.getAllUserResponseList().isEmpty()){
            throw new RuntimeException("Error");
        }

        return userServiceHelper.getAllUserResponseList();
    }

    public UserResponse getUserById(Long id){
        User user = userServiceHelper.checkUserId(id);
        return UserMapperDto.fromEntity(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequestUpdateAdmin request) {
        User user = userServiceHelper.checkUserId(id);
        userServiceHelper.updateUserData(request, user);

        return UserMapperDto.fromEntity(user);
    }

    public void deleteUser(Long id) {
        userServiceHelper.checkUserId(id);
        userRepository.deleteById(id);
    }

}
