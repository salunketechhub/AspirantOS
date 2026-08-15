import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { DatabaseHealthResponse, HealthResponse } from '../models/health.model';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  /**
   * Check backend application health
   */
  getHealth(): Observable<HealthResponse> {
    return this.http.get<HealthResponse>(`${this.baseUrl}/health`);
  }

  /**
   * Check PostgreSQL database connectivity via backend
   */
  getDbHealth(): Observable<DatabaseHealthResponse> {
    return this.http.get<DatabaseHealthResponse>(`${this.baseUrl}/health/db`);
  }
}
