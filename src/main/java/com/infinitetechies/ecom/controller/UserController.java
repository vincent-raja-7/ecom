package com.infinitetechies.ecom.controller;
import com.infinitetechies.ecom.model.dto.response.UserResponse;
import com.infinitetechies.ecom.service.inf.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUserById(long id) {
        UserResponse response = userService.getUser(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    // No @PreAuthorize or security: Returns all users, no restriction
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}

