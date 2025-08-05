package com.MagicTheGathering.user;

import com.MagicTheGathering.role.Role;
import com.MagicTheGathering.user.dto.UserMapperDto;
import com.MagicTheGathering.user.dto.UserRequest;
import com.MagicTheGathering.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = optionalUser.orElseThrow();
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("SCOPE_" + role.name()))
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
}
