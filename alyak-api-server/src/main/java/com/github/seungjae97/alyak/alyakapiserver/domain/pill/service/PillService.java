package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

public interface PillService {

    /**
     * DB에 존재하지 않는 알약의 경우에는 외부 API 호출해서 DB에
     * 저장 후 사용자에게 알약 정보 반환
     * @param pillName 알약 이름
     * */
    void findPill(String pillName);


}
