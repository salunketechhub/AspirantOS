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
   * Get overall UPSC preparation progress summary (overall, prelims, mains percentages)
   */
  getOverallProgress(): Observable<OverallProgressResponse> {
    return this.http.get<OverallProgressResponse>(this.baseUrl);
  }

  /**
   * Get progress for a specific syllabus topic (defaults to NOT_STARTED)
   */
  getTopicProgress(topicId: string): Observable<TopicProgressResponse> {
    return this.http.get<TopicProgressResponse>(`${this.baseUrl}/topics/${topicId}`);
  }

  /**
   * Update completion status for a syllabus topic
   */
  updateTopicProgress(topicId: string, status: ProgressStatus): Observable<TopicProgressResponse> {
    const body: ProgressStatusRequest = { status };
    return this.http.put<TopicProgressResponse>(`${this.baseUrl}/topics/${topicId}`, body);
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
}
