import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenStorageService } from './token-storage.service';
import { AuthResponse, LoginRequest, RegisterRequest, UserResponse } from './auth.models';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  // Reactive state signals
  readonly currentUser = signal<UserResponse | null>(null);
  readonly isLoading = signal<boolean>(false);
  readonly isInitialized = signal<boolean>(false);

  // Derived state computed signals
  readonly isAuthenticated = computed(() => !!this.currentUser());
  readonly userName = computed(() => {
    const user = this.currentUser();
    return user ? `${user.firstName} ${user.lastName}` : '';
  });

  constructor() {
    this.initAuthState().subscribe();
  }

  /**
   * Initializes auth state from stored token on app boot
   */
  initAuthState(): Observable<UserResponse | null> {
    const token = this.tokenStorage.getToken();

    if (!token) {
      this.currentUser.set(null);
      this.isInitialized.set(true);
      return of(null);
    }

    this.isLoading.set(true);
    return this.fetchCurrentUser().pipe(
      tap((user) => {
        this.currentUser.set(user);
        this.isLoading.set(false);
        this.isInitialized.set(true);
      }),
      catchError(() => {
        this.tokenStorage.clearToken();
        this.currentUser.set(null);
        this.isLoading.set(false);
        this.isInitialized.set(true);
        return of(null);
      })
    );
  }

  /**
   * Register a new user account (does not auto-authenticate)
   */
  register(request: RegisterRequest): Observable<UserResponse> {
    this.isLoading.set(true);
    return this.http.post<UserResponse>(`${this.baseUrl}/register`, request).pipe(
      tap(() => {
        this.isLoading.set(false);
      }),
      catchError((err) => {
        this.isLoading.set(false);
        throw err;
      })
    );
  }

  /**
   * Log in user, store token in storage abstraction, and update user state
   */
  login(request: LoginRequest): Observable<AuthResponse> {
    this.isLoading.set(true);
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, request).pipe(
      tap((response) => {
        this.tokenStorage.saveToken(response.accessToken);
        this.currentUser.set(response.user);
        this.isLoading.set(false);
      }),
      catchError((err) => {
        this.isLoading.set(false);
        throw err;
      })
    );
  }

  /**
   * Retrieve current user profile from protected endpoint
   */
  fetchCurrentUser(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.baseUrl}/me`).pipe(
      tap((user) => {
        this.currentUser.set(user);
      })
    );
  }

  /**
   * Log out user: clears token from storage, resets state, and navigates to login
   */
  logout(redirectUrl: string = '/login'): void {
    this.tokenStorage.clearToken();
    this.currentUser.set(null);
    this.router.navigateByUrl(redirectUrl);
  }

  /**
   * Check if an active token is present
   */
  hasStoredToken(): boolean {
    return this.tokenStorage.hasToken();
  }
}
