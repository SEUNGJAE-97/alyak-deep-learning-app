package com.github.seungjae97.alyak.alyakapiserver.domain.notification.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DeviceToken;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.repository.DeviceTokenRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class FcmPushNotificationService implements PushNotificationService {

    private static final String DATA_TYPE = "FAMILY_INVITE";

    private final FirebaseApp firebaseApp;
    private final DeviceTokenRepository deviceTokenRepository;

    @Override
    @Transactional
    public void sendInvite(List<DeviceToken> deviceTokens, Long inviterUserId, String inviterName) {
        if (deviceTokens.isEmpty()) {
            return;
        }

        FirebaseMessaging messaging = FirebaseMessaging.getInstance(firebaseApp);
        String title = "ALYAK 가족 초대";
        String body = inviterName + "님이 가족 구성원으로 초대했습니다.";

        List<Message> messages = new ArrayList<>(deviceTokens.size());
        for (DeviceToken dt : deviceTokens) {
            messages.add(
                    Message.builder()
                            .setToken(dt.getFcmToken())
                            .setNotification(
                                    Notification.builder()
                                            .setTitle(title)
                                            .setBody(body)
                                            .build()
                            )
                            .putData("type", DATA_TYPE)
                            .putData("inviterUserId", String.valueOf(inviterUserId))
                            .putData("inviterName", inviterName == null ? "" : inviterName)
                            .build()
            );
        }

        try {
            BatchResponse batchResponse = messaging.sendEach(messages);
            List<SendResponse> responses = batchResponse.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                SendResponse sendResponse = responses.get(i);
                if (sendResponse.isSuccessful()) {
                    continue;
                }
                FirebaseMessagingException ex = sendResponse.getException();
                if (ex == null) {
                    continue;
                }
                log.warn("FCM 전송 실패: tokenIndex={}, errorCode={}, message={}",
                        i, ex.getMessagingErrorCode(), ex.getMessage());
                if (ex.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
                        || ex.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                    String badToken = deviceTokens.get(i).getFcmToken();
                    disableTokensByFcmValue(badToken);
                }
            }
        } catch (Exception e) {
            log.error("FCM sendEach 처리 중 오류", e);
            throw new IllegalStateException("FCM 초대 알림 전송에 실패했습니다.", e);
        }
    }

    private void disableTokensByFcmValue(String fcmToken) {
        LocalDateTime now = LocalDateTime.now();
        List<DeviceToken> rows = deviceTokenRepository.findAllByFcmToken(fcmToken);
        for (DeviceToken row : rows) {
            row.disable(now);
        }
    }
}
