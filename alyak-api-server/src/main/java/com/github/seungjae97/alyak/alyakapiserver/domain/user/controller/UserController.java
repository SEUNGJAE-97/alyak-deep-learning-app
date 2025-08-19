package com.github.seungjae97.alyak.alyakapiserver.domain.user.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.service.UserService;
import com.github.seungjae97.alyak.alyakapiserver.global.auth.dto.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody User user) {
        Optional<User> existingUser = userService.getById(userDetails.getUser().getId());
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(existingUser.get().getId());
        User updatedUser = userService.update(user);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<User> user = userService.getById(userDetails.getUser().getId());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.delete(user.get().getId());
        return ResponseEntity.noContent().build();
    }
} 