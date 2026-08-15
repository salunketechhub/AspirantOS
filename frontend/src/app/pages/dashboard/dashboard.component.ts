import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { UserResponse } from '../../core/auth/auth.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  readonly authService = inject(AuthService);

  testProfileLoading = signal<boolean>(false);
  testProfileData = signal<UserResponse | null>(null);
  testProfileError = signal<string | null>(null);
  testCheckedAt = signal<Date | null>(null);

  ngOnInit(): void {
    this.testMeEndpoint();
  }

  testMeEndpoint(): void {
    this.testProfileLoading.set(true);
    this.testProfileError.set(null);

    this.authService.fetchCurrentUser().subscribe({
      next: (user) => {
        this.testProfileData.set(user);
        this.testProfileError.set(null);
        this.testCheckedAt.set(new Date());
        this.testProfileLoading.set(false);
      },
      error: (err) => {
        this.testProfileData.set(null);
        this.testProfileError.set(err?.error?.message || err?.message || 'Failed to fetch /api/auth/me');
        this.testCheckedAt.set(new Date());
        this.testProfileLoading.set(false);
      },
    });
  }

  onLogout(): void {
    this.authService.logout('/login');
  }
}
