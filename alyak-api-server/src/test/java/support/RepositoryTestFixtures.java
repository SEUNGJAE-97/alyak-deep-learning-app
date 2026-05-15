package support;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageBox;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageData;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.MedicationLog;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.enums.MedicationStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DevicePlatform;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DeviceToken;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.ScheduleBackup;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchive;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJob;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class RepositoryTestFixtures {

    private RepositoryTestFixtures() {
    }

    public static User user(String suffix) {
        return User.builder()
                .email("user-" + suffix + "@test.com")
                .name("테스트" + suffix)
                .build();
    }

    public static Pill pill(long id, String name) {
        return Pill.builder()
                .id(id)
                .pillName(name)
                .build();
    }

    public static ScheduleBackup scheduleBackup(User user, String pillName) {
        return ScheduleBackup.builder()
                .user(user)
                .pillName(pillName)
                .dosage(1)
                .scheduledTime(LocalTime.of(9, 0))
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .build();
    }

    public static MedicationLog medicationLog(User user) {
        return MedicationLog.builder()
                .user(user)
                .pillName("타이레놀")
                .dosage(1)
                .scheduledTime(LocalDateTime.of(2026, 5, 15, 9, 0))
                .status(MedicationStatus.TAKEN)
                .build();
    }

    public static DeviceToken deviceToken(User user, String deviceId, String fcmToken) {
        LocalDateTime now = LocalDateTime.now();
        return DeviceToken.builder()
                .user(user)
                .deviceId(deviceId)
                .fcmToken(fcmToken)
                .platform(DevicePlatform.ANDROID)
                .enabled(true)
                .lastSeenAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static TrainingJob trainingJob() {
        return TrainingJob.builder()
                .datasetFilter("all")
                .paramsJson("{}")
                .build();
    }

    public static ModelArchive modelArchive(String version) {
        return ModelArchive.builder()
                .version(version)
                .runDir("/runs/" + version)
                .argsPath("/args/" + version)
                .resultsPath("/results/" + version)
                .build();
    }

    public static PillImageData pillImageData(String path) {
        return PillImageData.builder()
                .imagePath(path)
                .status(DataStatus.INBOX)
                .build();
    }

    public static PillImageBox pillImageBox(int index) {
        return PillImageBox.builder()
                .boxIndex(index)
                .xMin(BigDecimal.valueOf(0.1))
                .yMin(BigDecimal.valueOf(0.1))
                .xMax(BigDecimal.valueOf(0.9))
                .yMax(BigDecimal.valueOf(0.9))
                .build();
    }
}
