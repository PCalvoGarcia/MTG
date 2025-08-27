package com.MagicTheGathering.user;

import com.MagicTheGathering.auth.AuthServiceHelper;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestAdmin;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestUpdateAdmin;
import com.MagicTheGathering.user.dto.USER.UserRequest;
import com.MagicTheGathering.user.dto.UserMapperDto;
import com.MagicTheGathering.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private AuthServiceHelper authServiceHelper;

    @GetMapping("/api/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserResponse> getUserById( @PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/api/users/my-user")
    public ResponseEntity<UserResponse> getLoggedUser(){
        return ResponseEntity.ok(UserMapperDto.fromEntity(userService.getAuthenticatedUser()));
    }


    @PostMapping("/auth/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> body) {
        return authServiceHelper.handleRefreshToken(body.get("refreshToken"));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        UserResponse registeredUser = userService.registerUser(request);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<UserResponse> registerUserAdmin(@Valid @RequestBody UserRequestAdmin request) {
        UserResponse registeredUser = userService.registerUserByAdmin(request);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }


    @PutMapping("/api/users/{id}")
    public ResponseEntity<UserResponse> updateUserRoleRole( @PathVariable Long id, @Valid @RequestBody UserRequestUpdateAdmin request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }


    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User with id " + id + " has been deleted", HttpStatus.NO_CONTENT);
    }
}
