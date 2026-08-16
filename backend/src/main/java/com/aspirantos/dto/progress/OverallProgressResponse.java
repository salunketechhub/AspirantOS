package com.aspirantos.dto.progress;

public class OverallProgressResponse {

    private int totalTopics;
    private int completedTopics;
    private int inProgressTopics;
    private int notStartedTopics;
    private int completionPercentage;
    private int prelimsPercentage;
    private int mainsPercentage;
    private int optionalPercentage;
    private int totalPyqDone;
    private int pyqPercentage;

    public OverallProgressResponse() {
    }

    public OverallProgressResponse(int totalTopics, int completedTopics, int inProgressTopics, int notStartedTopics,
                                   int completionPercentage, int prelimsPercentage, int mainsPercentage, int optionalPercentage,
                                   int totalPyqDone, int pyqPercentage) {
        this.totalTopics = totalTopics;
        this.completedTopics = completedTopics;
        this.inProgressTopics = inProgressTopics;
        this.notStartedTopics = notStartedTopics;
        this.completionPercentage = completionPercentage;
        this.prelimsPercentage = prelimsPercentage;
        this.mainsPercentage = mainsPercentage;
        this.optionalPercentage = optionalPercentage;
        this.totalPyqDone = totalPyqDone;
        this.pyqPercentage = pyqPercentage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int totalTopics;
        private int completedTopics;
        private int inProgressTopics;
        private int notStartedTopics;
        private int completionPercentage;
        private int prelimsPercentage;
        private int mainsPercentage;
        private int optionalPercentage;
        private int totalPyqDone;
        private int pyqPercentage;

        public Builder totalTopics(int totalTopics) {
            this.totalTopics = totalTopics;
            return this;
        }

        public Builder completedTopics(int completedTopics) {
            this.completedTopics = completedTopics;
            return this;
        }

        public Builder inProgressTopics(int inProgressTopics) {
            this.inProgressTopics = inProgressTopics;
            return this;
        }

        public Builder notStartedTopics(int notStartedTopics) {
            this.notStartedTopics = notStartedTopics;
            return this;
        }

        public Builder completionPercentage(int completionPercentage) {
            this.completionPercentage = completionPercentage;
            return this;
        }

        public Builder prelimsPercentage(int prelimsPercentage) {
            this.prelimsPercentage = prelimsPercentage;
            return this;
        }

        public Builder mainsPercentage(int mainsPercentage) {
            this.mainsPercentage = mainsPercentage;
            return this;
        }

        public Builder optionalPercentage(int optionalPercentage) {
            this.optionalPercentage = optionalPercentage;
            return this;
        }

        public Builder totalPyqDone(int totalPyqDone) {
            this.totalPyqDone = totalPyqDone;
            return this;
        }

        public Builder pyqPercentage(int pyqPercentage) {
            this.pyqPercentage = pyqPercentage;
            return this;
        }

        public OverallProgressResponse build() {
            return new OverallProgressResponse(totalTopics, completedTopics, inProgressTopics, notStartedTopics,
                    completionPercentage, prelimsPercentage, mainsPercentage, optionalPercentage,
                    totalPyqDone, pyqPercentage);
        }
    }

    public int getTotalTopics() {
        return totalTopics;
    }

    public void setTotalTopics(int totalTopics) {
        this.totalTopics = totalTopics;
    }

    public int getCompletedTopics() {
        return completedTopics;
    }

    public void setCompletedTopics(int completedTopics) {
        this.completedTopics = completedTopics;
    }

    public int getInProgressTopics() {
        return inProgressTopics;
    }

    public void setInProgressTopics(int inProgressTopics) {
        this.inProgressTopics = inProgressTopics;
    }

    public int getNotStartedTopics() {
        return notStartedTopics;
    }

    public void setNotStartedTopics(int notStartedTopics) {
        this.notStartedTopics = notStartedTopics;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public int getPrelimsPercentage() {
        return prelimsPercentage;
    }

    public void setPrelimsPercentage(int prelimsPercentage) {
        this.prelimsPercentage = prelimsPercentage;
    }

    public int getMainsPercentage() {
        return mainsPercentage;
    }

    public void setMainsPercentage(int mainsPercentage) {
        this.mainsPercentage = mainsPercentage;
    }

    public int getOptionalPercentage() {
        return optionalPercentage;
    }

    public void setOptionalPercentage(int optionalPercentage) {
        this.optionalPercentage = optionalPercentage;
    }

    public int getTotalPyqDone() {
        return totalPyqDone;
    }

    public void setTotalPyqDone(int totalPyqDone) {
        this.totalPyqDone = totalPyqDone;
    }

    public int getPyqPercentage() {
        return pyqPercentage;
    }

    public void setPyqPercentage(int pyqPercentage) {
        this.pyqPercentage = pyqPercentage;
    }
}
