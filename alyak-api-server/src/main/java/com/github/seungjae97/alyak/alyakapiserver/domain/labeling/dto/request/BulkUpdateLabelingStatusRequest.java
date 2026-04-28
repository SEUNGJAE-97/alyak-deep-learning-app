package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.request;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkUpdateLabelingStatusRequest {
    private List<Long> ids;
    private DataStatus status;
}
