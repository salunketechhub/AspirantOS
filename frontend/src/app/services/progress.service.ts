import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  OverallProgressResponse,
  ProgressStatus,
  ProgressStatusRequest,
  SubjectProgressResponse,
  TopicProgressResponse,
} from '../models/progress.models';

@Injectable({
  providedIn: 'root',
})
export class ProgressService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/progress`;

  /**
   * Get overall UPSC preparation progress summary (overall, prelims, mains percentages, pyq percentage)
   */
  getOverallProgress(): Observable<OverallProgressResponse> {
    return this.http.get<OverallProgressResponse>(this.baseUrl);
  }

  /**
   * Get progress for a specific syllabus topic
   */
  getTopicProgress(topicId: string): Observable<TopicProgressResponse> {
    return this.http.get<TopicProgressResponse>(`${this.baseUrl}/topics/${topicId}`);
  }

  /**
   * Update completion status and/or PYQ for a syllabus topic
   */
  updateTopicProgress(topicId: string, status?: ProgressStatus, pyqDone?: boolean): Observable<TopicProgressResponse> {
    const body: ProgressStatusRequest = { status, pyqDone };
    return this.http.put<TopicProgressResponse>(`${this.baseUrl}/topics/${topicId}`, body);
  }

  /**
   * Toggle PYQ solved state for a topic
   */
  toggleTopicPyq(topicId: string): Observable<TopicProgressResponse> {
    return this.http.post<TopicProgressResponse>(`${this.baseUrl}/topics/${topicId}/pyq/toggle`, {});
  }

  /**
   * Get subject-level completion progress and stats
   */
  getSubjectProgress(subjectId: string): Observable<SubjectProgressResponse> {
    return this.http.get<SubjectProgressResponse>(`${this.baseUrl}/subjects/${subjectId}`);
  }

  /**
   * Bulk retrieve all user topic progress records as a mapping of topicId -> ProgressStatus
   */
  getAllProgressMap(): Observable<Record<string, ProgressStatus>> {
    return this.http.get<Record<string, ProgressStatus>>(`${this.baseUrl}/all`);
  }

  /**
   * Bulk retrieve all user topic PYQ records as a mapping of topicId -> Boolean
   */
  getAllPyqMap(): Observable<Record<string, boolean>> {
    return this.http.get<Record<string, boolean>>(`${this.baseUrl}/pyq-map`);
  }
}
