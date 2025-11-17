package com.github.seungjae97.alyak.alyakapiserver.domain.family.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.FamilyMemberInfoResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    /**
     * @param userId 유저 아이디
     * @return List<FamilyMemberInfoResponse> members 가족에 속하는 구성원들의 정보
     * */
    public List<FamilyMemberInfoResponse> findMembersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_EXIST));

        Long familyId = Optional.ofNullable(user.getFamily())
                .map(Family::getId)
                .orElseThrow(() -> new BusinessException(BusinessError.DONT_EXIST_FAMILY));

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BusinessException(BusinessError.DONT_EXIST_FAMILY));

        return family.getUsers().stream()
                .map(FamilyMemberInfoResponse::from)
                .toList();
    }
}
