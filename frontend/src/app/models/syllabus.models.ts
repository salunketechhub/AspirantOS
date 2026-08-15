export type ExamStage = 'PRELIMS' | 'MAINS' | 'INTERVIEW';

export interface ExamResponse {
  id: string;
  code: string;
  name: string;
  description: string;
  stage: ExamStage;
  displayOrder: number;
  subjectCount: number;
}

export interface SubjectResponse {
  id: string;
  examId: string;
  examCode: string;
  examName: string;
  code: string;
  name: string;
  description: string;
  paper: string;
  displayOrder: number;
  topicCount: number;
}

export interface TopicResponse {
  id: string;
  subjectId: string;
  subjectCode: string;
  subjectName: string;
  parentTopicId?: string | null;
  code: string;
  name: string;
  description?: string;
  level: number;
  displayOrder: number;
  subtopics: TopicResponse[];
  subtopicCount: number;
}

export interface OptionalSubjectResponse {
  id: string;
  code: string;
  name: string;
  description: string;
  displayOrder: number;
}

export interface SubjectTreeResponse {
  id: string;
  code: string;
  name: string;
  description: string;
  paper: string;
  displayOrder: number;
  topics: TopicResponse[];
}

export interface ExamTreeResponse {
  id: string;
  code: string;
  name: string;
  description: string;
  stage: ExamStage;
  displayOrder: number;
  subjects: SubjectTreeResponse[];
}

export interface SyllabusTreeResponse {
  exams: ExamTreeResponse[];
  optionals: OptionalSubjectResponse[];
  totalExams: number;
  totalSubjects: number;
  totalTopics: number;
  totalOptionals: number;
}
