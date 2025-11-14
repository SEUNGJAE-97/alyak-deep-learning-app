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

    @GetMapping("/me")
    @Operation(summary = "회원정보 조회", description = "사용자 정보를 조회할때 호출하는 API")
    public ResponseEntity<User> getUserById(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<User> user = userService.getById(userDetails.getUser().getId());
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    @Operation(summary = "회원정보 수정", description = "사용자 정보를 수정할때 사용하는 API")
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