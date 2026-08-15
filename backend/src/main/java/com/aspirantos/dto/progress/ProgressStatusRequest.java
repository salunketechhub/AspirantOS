package com.aspirantos.dto.progress;

import com.aspirantos.entity.ProgressStatus;
import jakarta.validation.constraints.NotNull;

public class ProgressStatusRequest {

    @NotNull(message = "Progress status is required (NOT_STARTED, IN_PROGRESS, COMPLETED)")
    private ProgressStatus status;

    public ProgressStatusRequest() {
    }

    public ProgressStatusRequest(ProgressStatus status) {
        this.status = status;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status;
    }
}
