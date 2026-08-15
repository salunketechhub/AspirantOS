import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  loginForm!: FormGroup;
  showPassword = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  isSubmitting = signal<boolean>(false);

  private returnUrl: string = '/dashboard';

  ngOnInit(): void {
    // If already logged in, redirect to dashboard
    if (this.authService.isAuthenticated()) {
      this.router.navigateByUrl('/dashboard');
      return;
    }

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';

    if (this.route.snapshot.queryParams['registered'] === 'true') {
      this.successMessage.set('Account created successfully! Please sign in with your credentials.');
    }

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword.update((val) => !val);
  }

  onSubmit(): void {
    if (this.loginForm.invalid || this.isSubmitting()) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.errorMessage.set(null);
    this.isSubmitting.set(true);

    const { email, password } = this.loginForm.value;

    this.authService.login({ email, password }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.router.navigateByUrl(this.returnUrl);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        const message =
          err?.error?.message ||
          (err?.status === 401
            ? 'Invalid email or password. Please check your credentials.'
            : 'An unexpected error occurred. Please check your backend connection.');
        this.errorMessage.set(message);
      },
    });
  }
}
