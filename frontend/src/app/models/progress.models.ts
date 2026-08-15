export type ProgressStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';

export interface ProgressStatusRequest {
  status: ProgressStatus;
}

export interface TopicProgressResponse {
  topicId: string;
  topicCode: string;
  topicName: string;
  status: ProgressStatus;
  subjectId: string;
  subjectCode: string;
  subjectName: string;
}

export interface SubjectProgressResponse {
  subjectId: string;
  subjectCode: string;
  subjectName: string;
  totalTopics: number;
  completedTopics: number;
  inProgressTopics: number;
  notStartedTopics: number;
  completionPercentage: number;
}

export interface OverallProgressResponse {
  totalTopics: number;
  completedTopics: number;
  inProgressTopics: number;
  notStartedTopics: number;
  completionPercentage: number;
  prelimsPercentage: number;
  mainsPercentage: number;
  optionalPercentage: number;
}
