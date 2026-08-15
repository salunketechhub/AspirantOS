import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { DatabaseHealthResponse, HealthResponse } from '../../models/health.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  private readonly apiService = inject(ApiService);

  // Reactive state signals
  readonly backendLoading = signal<boolean>(false);
  readonly backendData = signal<HealthResponse | null>(null);
  readonly backendError = signal<string | null>(null);
  readonly backendTimestamp = signal<Date | null>(null);

  readonly dbLoading = signal<boolean>(false);
  readonly dbData = signal<DatabaseHealthResponse | null>(null);
  readonly dbError = signal<string | null>(null);
  readonly dbTimestamp = signal<Date | null>(null);

  readonly showPayloads = signal<boolean>(false);

  ngOnInit(): void {
    this.refreshAllHealth();
  }

  refreshAllHealth(): void {
    this.checkBackendHealth();
    this.checkDatabaseHealth();
  }

  checkBackendHealth(): void {
    this.backendLoading.set(true);
    this.backendError.set(null);

    this.apiService.getHealth().subscribe({
      next: (data) => {
        this.backendData.set(data);
        this.backendError.set(null);
        this.backendTimestamp.set(new Date());
        this.backendLoading.set(false);
      },
      error: (err) => {
        this.backendData.set(null);
        this.backendError.set(
          err?.error?.message || err?.message || 'Failed to connect to backend server (http://localhost:8080)'
        );
        this.backendTimestamp.set(new Date());
        this.backendLoading.set(false);
      },
    });
  }

  checkDatabaseHealth(): void {
    this.dbLoading.set(true);
    this.dbError.set(null);

    this.apiService.getDbHealth().subscribe({
      next: (data) => {
        this.dbData.set(data);
        this.dbError.set(null);
        this.dbTimestamp.set(new Date());
        this.dbLoading.set(false);
      },
      error: (err) => {
        const errorMsg =
          err?.error?.message || err?.message || 'Database is unreachable or connection failed';
        this.dbData.set(err?.error?.status ? err.error : null);
        this.dbError.set(errorMsg);
        this.dbTimestamp.set(new Date());
        this.dbLoading.set(false);
      },
    });
  }

  togglePayloads(): void {
    this.showPayloads.update((prev) => !prev);
  }
}
