package com.github.seungjae97.alyak.alyakapiserver.domain.family.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.UserDetailsImpl;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.request.AcceptFamilyInviteRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.request.FamilyJoinByQrRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.FamilyMemberInfoResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
@Tag(name = "06.가족", description = "가족 정보 관련 API")
public class FamilyController {

    private final FamilyService familyService;

    @GetMapping("/members")
    @Operation(summary = "구성원 검색", description = "userId 기준 가족 구성원 조회")
    public ResponseEntity<List<FamilyMemberInfoResponse>> findMembers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<FamilyMemberInfoResponse> members = familyService.findMembersByUserId(userDetails.getUser().getUserId());
        return ResponseEntity.ok(members);
    }

    @GetMapping("/qrcode")
    @Operation(summary = "생성된 qr코드를 리턴", description = "임시 코드를 키 값, userId를 value로 생성한다.")
    public ResponseEntity<String> getQrCode(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String qrCode = familyService.getQrCode(userDetails.getUser().getUserId());
        return ResponseEntity.ok(qrCode);
    }



    @PostMapping("/invite")
    @Operation(summary = "이메일로 가족 추가", description = "입력한 이메일로 초대코드를 전송한다.")
    public ResponseEntity<Boolean> inviteEmail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String email
    ) {
        return ResponseEntity.ok(familyService.inviteByEmail(email, userDetails.getUser().getUserId()));
    }

    @PostMapping("/invite/accept")
    @Operation(summary = "가족 초대 수락", description = "로그인한 사용자가 이메일 초대를 수락해 초대자의 가족에 합류합니다.")
    public ResponseEntity<Void> acceptInvite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody AcceptFamilyInviteRequest body
    ) {
        familyService.acceptInvite(
                userDetails.getUser().getUserId(),
                body.getInviterUserId()
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/join/qr")
    @Operation(summary = "QR로 가족 합류", description = "스캔한 코드로 초대자를 확인한 뒤 로그인 사용자를 해당 가족에 합류시킵니다.")
    public ResponseEntity<Void> joinByQr(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody FamilyJoinByQrRequest body
    ) {
        familyService.joinByQrCode(
                userDetails.getUser().getUserId(),
                body.getScannedCode()
        );
        return ResponseEntity.noContent().build();
    }
}
