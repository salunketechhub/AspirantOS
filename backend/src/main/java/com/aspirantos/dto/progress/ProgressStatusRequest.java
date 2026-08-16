package com.aspirantos.dto.progress;

import com.aspirantos.entity.ProgressStatus;

public class ProgressStatusRequest {

    private ProgressStatus status;
    private Boolean pyqDone;

    public ProgressStatusRequest() {
    }

    public ProgressStatusRequest(ProgressStatus status) {
        this.status = status;
    }

    public ProgressStatusRequest(ProgressStatus status, Boolean pyqDone) {
        this.status = status;
        this.pyqDone = pyqDone;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status;
    }

    public Boolean getPyqDone() {
        return pyqDone;
    }

    public void setPyqDone(Boolean pyqDone) {
        this.pyqDone = pyqDone;
    }
}
