package com.github.seungjae97.alyak.alyakapiserver.domain.user.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.dto.UserUpdateRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.service.UserService;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "05. 유저", description = "유저 관련 API")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUserById(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<User> user = userService.getById(userDetails.getUser().getId());
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email")
    public ResponseEntity<User> getUserByEmail(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<User> user = userService.getByEmail(userDetails.getUser().getEmail());
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userService.existsByEmail(userDetails.getUser().getEmail())){
            return ResponseEntity.badRequest().build();
        }
        User createdUser = userService.create(userDetails.getUser());
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserUpdateRequest request) {
        Optional<User> existingUser = userService.getById(userDetails.getUser().getId());
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User updatedUser = userService.update(existingUser.get().getId(), request);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping
    @Operation(summary = "회원탈퇴", description = "사용자가 회원탈퇴를 할때 호출하는 API")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<User> user = userService.getById(userDetails.getUser().getId());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.delete(user.get().getId());
        return ResponseEntity.noContent().build();
    }
} 