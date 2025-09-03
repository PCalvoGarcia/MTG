package com.MagicTheGathering.user;

import com.MagicTheGathering.auth.AuthServiceHelper;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestAdmin;
import com.MagicTheGathering.user.dto.ADMIN.UserRequestUpdateAdmin;
import com.MagicTheGathering.user.dto.USER.UserRequest;
import com.MagicTheGathering.user.dto.UserMapperDto;
import com.MagicTheGathering.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Tag(name = "Users", description = "Operations related to users")
public class UserController {

    private UserService userService;
    private AuthServiceHelper authServiceHelper;

    @GetMapping("/api/users")
    @Operation(summary = "Get all users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users returned successfully"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/api/users/{id}")
    @Operation(summary = "Get user by Id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User returned successfully"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/UserNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<UserResponse> getUserById( @PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/api/users/my-user")
    @Operation(summary = "Get my user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User returned successfully"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/UserNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<UserResponse> getLoggedUser(){
        return ResponseEntity.ok(UserMapperDto.fromEntity(userService.getAuthenticatedUser()));
    }


    @PostMapping("/auth/refresh")
    @Operation(summary = "Refresh access token using refresh token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing token"),
                    @ApiResponse(responseCode = "401", description = "Invalid refresh token")
            })
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> body) {
        return authServiceHelper.handleRefreshToken(body.get("refreshToken"));
    }

    @PostMapping("/register")
    @Operation(summary = "Create a new user.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "409", description = "Conflict with username or email"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        UserResponse registeredUser = userService.registerUser(request);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/register/admin")
    @Operation(summary = "Create new user by user with role ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "409", description = "Conflict with username or email"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<UserResponse> registerUserAdmin(@Valid @RequestBody UserRequestAdmin request) {
        UserResponse registeredUser = userService.registerUserByAdmin(request);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }


    @PutMapping("/api/users/{id}")
    @Operation(summary = "Update user by user with role ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestUpdateAdmin request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PutMapping("/api/users/my-user")
    @Operation(summary = "Update my user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<UserResponse> updateLoggedUser( @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateLoggedUser(request));
    }


    @DeleteMapping("/api/users/{id}")
    @Operation(summary = "Get all reviews by user.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/UserNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User with id " + id + " has been deleted", HttpStatus.NO_CONTENT);
    }
}
