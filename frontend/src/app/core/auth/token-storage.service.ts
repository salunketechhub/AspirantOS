import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class TokenStorageService {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly TOKEN_KEY = 'aspirantos_auth_token';

  /**
   * Store the JWT access token in storage abstraction
   */
  saveToken(token: string): void {
    if (isPlatformBrowser(this.platformId)) {
      try {
        localStorage.setItem(this.TOKEN_KEY, token);
      } catch (e) {
        console.error('Failed to save access token in storage', e);
      }
    }
  }

  /**
   * Retrieve the JWT access token
   */
  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      try {
        return localStorage.getItem(this.TOKEN_KEY);
      } catch (e) {
        console.error('Failed to retrieve access token from storage', e);
        return null;
      }
    }
    return null;
  }

  /**
   * Clear the JWT access token from storage
   */
  clearToken(): void {
    if (isPlatformBrowser(this.platformId)) {
      try {
        localStorage.removeItem(this.TOKEN_KEY);
      } catch (e) {
        console.error('Failed to clear access token from storage', e);
      }
    }
  }

  /**
   * Check if an access token exists in storage
   */
  hasToken(): boolean {
    return !!this.getToken();
  }
}
