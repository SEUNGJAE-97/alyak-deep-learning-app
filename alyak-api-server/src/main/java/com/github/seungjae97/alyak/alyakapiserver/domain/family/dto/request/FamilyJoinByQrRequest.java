package com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FamilyJoinByQrRequest {

    private String scannedCode;
}
