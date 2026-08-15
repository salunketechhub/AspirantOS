import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ProgressService } from '../../services/progress.service';
import { OverallProgressResponse } from '../../models/progress.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  readonly authService = inject(AuthService);
  private readonly progressService = inject(ProgressService);

  readonly overallProgress = signal<OverallProgressResponse | null>(null);
  readonly progressLoading = signal<boolean>(false);
  readonly progressError = signal<string | null>(null);

  ngOnInit(): void {
    this.loadProgress();
  }

  loadProgress(): void {
    this.progressLoading.set(true);
    this.progressError.set(null);

    this.progressService.getOverallProgress().subscribe({
      next: (data) => {
        this.overallProgress.set(data);
        this.progressLoading.set(false);
      },
      error: (err) => {
        this.progressError.set(err?.error?.message || 'Failed to load progress summary.');
        this.progressLoading.set(false);
      },
    });
  }

  onLogout(): void {
    this.authService.logout('/login');
  }
}
