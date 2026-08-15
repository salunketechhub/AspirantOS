import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../core/auth/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthResponse } from '../../core/auth/auth.models';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceMock = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'login']);
    routerMock = jasmine.createSpyObj('Router', ['navigateByUrl']);

    authServiceMock.isAuthenticated.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParams: { registered: 'true', returnUrl: '/dashboard' },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create LoginComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should display success message if registered=true is in queryParams', () => {
    expect(component.successMessage()).toContain('Account created successfully');
  });

  it('should validate email and password as required', () => {
    component.loginForm.controls['email'].setValue('');
    component.loginForm.controls['password'].setValue('');

    expect(component.loginForm.valid).toBeFalse();
  });

  it('should call authService.login on valid form submit and navigate to returnUrl', () => {
    const mockAuthResponse: AuthResponse = {
      accessToken: 'token',
      tokenType: 'Bearer',
      user: {
        id: '1',
        firstName: 'Aarav',
        lastName: 'Sharma',
        email: 'aarav@example.com',
        role: 'USER',
      },
    };

    authServiceMock.login.and.returnValue(of(mockAuthResponse));

    component.loginForm.controls['email'].setValue('aarav@example.com');
    component.loginForm.controls['password'].setValue('Password123');

    component.onSubmit();

    expect(authServiceMock.login).toHaveBeenCalledWith({
      email: 'aarav@example.com',
      password: 'Password123',
    });
    expect(routerMock.navigateByUrl).toHaveBeenCalledWith('/dashboard');
  });

  it('should display error message on failed login', () => {
    authServiceMock.login.and.returnValue(
      throwError(() => ({ status: 401, error: { message: 'Invalid credentials' } }))
    );

    component.loginForm.controls['email'].setValue('aarav@example.com');
    component.loginForm.controls['password'].setValue('WrongPassword');

    component.onSubmit();

    expect(component.errorMessage()).toBe('Invalid credentials');
  });
});
