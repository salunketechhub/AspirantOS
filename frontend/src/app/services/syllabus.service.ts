import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  ExamResponse,
  OptionalSubjectResponse,
  SubjectResponse,
  SyllabusTreeResponse,
  TopicResponse,
} from '../models/syllabus.models';

@Injectable({
  providedIn: 'root',
})
export class SyllabusService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/syllabus`;

  /**
   * Get full aggregated syllabus hierarchy tree
   */
  getFullSyllabusTree(): Observable<SyllabusTreeResponse> {
    return this.http.get<SyllabusTreeResponse>(this.baseUrl);
  }

  /**
   * Get all exam stages (Prelims, Mains)
   */
  getExams(): Observable<ExamResponse[]> {
    return this.http.get<ExamResponse[]>(`${this.baseUrl}/exams`);
  }

  /**
   * Get specific exam details
   */
  getExam(examId: string): Observable<ExamResponse> {
    return this.http.get<ExamResponse>(`${this.baseUrl}/exams/${examId}`);
  }

  /**
   * Get all subjects under an exam stage
   */
  getSubjectsByExam(examId: string): Observable<SubjectResponse[]> {
    return this.http.get<SubjectResponse[]>(`${this.baseUrl}/exams/${examId}/subjects`);
  }

  /**
   * Get specific subject details
   */
  getSubject(subjectId: string): Observable<SubjectResponse> {
    return this.http.get<SubjectResponse>(`${this.baseUrl}/subjects/${subjectId}`);
  }

  /**
   * Get hierarchical topic tree for a subject
   */
  getTopicsBySubject(subjectId: string): Observable<TopicResponse[]> {
    return this.http.get<TopicResponse[]>(`${this.baseUrl}/subjects/${subjectId}/topics`);
  }

  /**
   * Get specific topic details with its children
   */
  getTopic(topicId: string): Observable<TopicResponse> {
    return this.http.get<TopicResponse>(`${this.baseUrl}/topics/${topicId}`);
  }

  /**
   * Get UPSC optional subjects catalogue
   */
  getOptionals(): Observable<OptionalSubjectResponse[]> {
    return this.http.get<OptionalSubjectResponse[]>(`${this.baseUrl}/optionals`);
  }
}
