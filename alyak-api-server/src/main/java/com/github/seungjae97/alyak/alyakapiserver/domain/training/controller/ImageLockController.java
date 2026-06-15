package com.github.seungjae97.alyak.alyakapiserver.domain.training.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.UserDetailsImpl;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.service.ImageLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageLockController {

    private final ImageLockService imageLockService;

    @PostMapping("/{imageId}/lock")
    public ResponseEntity<Void> acquireLock(
            @PathVariable Long imageId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        imageLockService.acquireLock(imageId, user.getUser().getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{imageId}/lock")
    public ResponseEntity<Void> releaseLock(
            @PathVariable Long imageId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        imageLockService.releaseLock(imageId, user.getUser().getUserId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{imageId}/lock")
    public ResponseEntity<Void> refreshLock(
            @PathVariable Long imageId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        imageLockService.refreshLock(imageId, user.getUser().getUserId());
        return ResponseEntity.ok().build();
    }
}
