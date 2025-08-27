package com.MagicTheGathering.user;

import com.MagicTheGathering.Exceptions.EmptyListException;
import com.MagicTheGathering.role.Role;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestAdmin;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestUpdateAdmin;
import com.MagicTheGathering.user.dto.UserMapperDto;
import com.MagicTheGathering.user.dto.USER.UserRequest;
import com.MagicTheGathering.user.dto.UserResponse;
import com.MagicTheGathering.user.utils.UserSecurityUtils;
import com.MagicTheGathering.user.utils.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserServiceHelper userServiceHelper;


    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userServiceHelper.getUserLogin(username);
        User user = optionalUser.orElseThrow();
        List<GrantedAuthority> authorities = UserSecurityUtils.getAuthoritiesRole(user);

        return UserSecurityUtils.createUserByUserDetails(user, authorities);
    }

    public UserResponse registerUser(UserRequest request) {
        try {
            userServiceHelper.checkUsername(request.username());
            userServiceHelper.checkEmail(request.email());

            User user = UserMapperDto.toEntity(request);
            user.setPassword(userServiceHelper.getEncodePassword(request.password()));
            user.setRoles(Set.of(Role.USER));

            User savedUser = userRepository.save(user);

            return UserMapperDto.fromEntity(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Username or email already exists");
        }

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
            throw new EmptyListException();
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

    @Transactional
    public UserResponse updateLoggedUser(UserRequestUpdateAdmin request) {
        User user = getAuthenticatedUser();
        userServiceHelper.updateUserData(request, user);

        return UserMapperDto.fromEntity(user);
    }

    public void deleteUser(Long id) {
        userServiceHelper.checkUserId(id);
        userRepository.deleteById(id);
    }

}
