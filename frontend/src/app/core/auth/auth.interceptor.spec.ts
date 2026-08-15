import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { TokenStorageService } from './token-storage.service';
import { AuthService } from './auth.service';

describe('authInterceptor', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;
  let tokenStorageMock: jasmine.SpyObj<TokenStorageService>;
  let authServiceMock: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    tokenStorageMock = jasmine.createSpyObj('TokenStorageService', ['getToken']);
    authServiceMock = jasmine.createSpyObj('AuthService', ['logout']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: TokenStorageService, useValue: tokenStorageMock },
        { provide: AuthService, useValue: authServiceMock },
      ],
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should attach Authorization header when token is present on protected route', () => {
    tokenStorageMock.getToken.and.returnValue('my-test-jwt-token');

    httpClient.get('/api/auth/me').subscribe();

    const req = httpMock.expectOne('/api/auth/me');
    expect(req.request.headers.has('Authorization')).toBeTrue();
    expect(req.request.headers.get('Authorization')).toBe('Bearer my-test-jwt-token');
    req.flush({});
  });

  it('should not attach Authorization header on login/register endpoints', () => {
    tokenStorageMock.getToken.and.returnValue('my-test-jwt-token');

    httpClient.post('/api/auth/login', {}).subscribe();

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('should call authService.logout on 401 Unauthorized response from protected route', () => {
    tokenStorageMock.getToken.and.returnValue('expired-token');

    httpClient.get('/api/auth/me').subscribe({
      next: () => fail('Should have failed with 401'),
      error: (err) => {
        expect(err.status).toBe(401);
      },
    });

    const req = httpMock.expectOne('/api/auth/me');
    req.flush({ message: 'Token expired' }, { status: 401, statusText: 'Unauthorized' });

    expect(authServiceMock.logout).toHaveBeenCalledWith('/login');
  });
});
