import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { TokenStorageService } from './token-storage.service';
import { environment } from '../../../environments/environment';
import { AuthResponse, UserResponse } from './auth.models';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let tokenStorageMock: jasmine.SpyObj<TokenStorageService>;
  let routerMock: jasmine.SpyObj<Router>;

  const mockUser: UserResponse = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    firstName: 'Aarav',
    lastName: 'Sharma',
    email: 'aarav@example.com',
    role: 'USER',
  };

  const mockAuthResponse: AuthResponse = {
    accessToken: 'mock.jwt.token',
    tokenType: 'Bearer',
    user: mockUser,
  };

  beforeEach(() => {
    tokenStorageMock = jasmine.createSpyObj('TokenStorageService', [
      'getToken',
      'saveToken',
      'clearToken',
      'hasToken',
    ]);
    routerMock = jasmine.createSpyObj('Router', ['navigateByUrl', 'navigate']);

    tokenStorageMock.getToken.and.returnValue(null);
    tokenStorageMock.hasToken.and.returnValue(false);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TokenStorageService, useValue: tokenStorageMock },
        { provide: Router, useValue: routerMock },
      ],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should register a new user without storing token', () => {
    const registerReq = {
      firstName: 'Aarav',
      lastName: 'Sharma',
      email: 'aarav@example.com',
      password: 'Password123',
    };

    service.register(registerReq).subscribe((user) => {
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerReq);
    req.flush(mockUser);

    expect(tokenStorageMock.saveToken).not.toHaveBeenCalled();
  });

  it('should log in user, save token, and update currentUser signal', () => {
    const loginReq = {
      email: 'aarav@example.com',
      password: 'Password123',
    };

    service.login(loginReq).subscribe((res) => {
      expect(res).toEqual(mockAuthResponse);
      expect(service.currentUser()).toEqual(mockUser);
      expect(service.isAuthenticated()).toBeTrue();
      expect(service.userName()).toBe('Aarav Sharma');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockAuthResponse);

    expect(tokenStorageMock.saveToken).toHaveBeenCalledWith('mock.jwt.token');
  });

  it('should fetch /api/auth/me and update user state', () => {
    service.fetchCurrentUser().subscribe((user) => {
      expect(user).toEqual(mockUser);
      expect(service.currentUser()).toEqual(mockUser);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/me`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('should logout, clear token, reset state, and navigate to login', () => {
    service.currentUser.set(mockUser);
    expect(service.isAuthenticated()).toBeTrue();

    service.logout('/login');

    expect(tokenStorageMock.clearToken).toHaveBeenCalled();
    expect(service.currentUser()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
    expect(routerMock.navigateByUrl).toHaveBeenCalledWith('/login');
  });
});
