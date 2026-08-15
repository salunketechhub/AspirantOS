import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../core/auth/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { UserResponse } from '../../core/auth/auth.models';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let routerMock: jasmine.SpyObj<Router>;

  const mockUser: UserResponse = {
    id: '1',
    firstName: 'Aarav',
    lastName: 'Sharma',
    email: 'aarav@example.com',
    role: 'USER',
  };

  beforeEach(async () => {
    authServiceMock = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'register']);
    routerMock = jasmine.createSpyObj('Router', ['navigate', 'navigateByUrl']);

    authServiceMock.isAuthenticated.and.returnValue(false);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create RegisterComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should validate password mismatch', () => {
    component.registerForm.controls['firstName'].setValue('Aarav');
    component.registerForm.controls['lastName'].setValue('Sharma');
    component.registerForm.controls['email'].setValue('aarav@example.com');
    component.registerForm.controls['password'].setValue('Password123');
    component.registerForm.controls['confirmPassword'].setValue('DifferentPassword');

    expect(component.registerForm.valid).toBeFalse();
    expect(component.registerForm.controls['confirmPassword'].errors?.['passwordMismatch']).toBeTrue();
  });

  it('should call authService.register and navigate to /login?registered=true on success', () => {
    authServiceMock.register.and.returnValue(of(mockUser));

    component.registerForm.controls['firstName'].setValue('Aarav');
    component.registerForm.controls['lastName'].setValue('Sharma');
    component.registerForm.controls['email'].setValue('aarav@example.com');
    component.registerForm.controls['password'].setValue('Password123');
    component.registerForm.controls['confirmPassword'].setValue('Password123');

    component.onSubmit();

    expect(authServiceMock.register).toHaveBeenCalledWith({
      firstName: 'Aarav',
      lastName: 'Sharma',
      email: 'aarav@example.com',
      password: 'Password123',
    });
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { registered: 'true' },
    });
  });

  it('should display error message when registration fails with 409', () => {
    authServiceMock.register.and.returnValue(
      throwError(() => ({ status: 409, error: { message: 'Email already exists' } }))
    );

    component.registerForm.controls['firstName'].setValue('Aarav');
    component.registerForm.controls['lastName'].setValue('Sharma');
    component.registerForm.controls['email'].setValue('duplicate@example.com');
    component.registerForm.controls['password'].setValue('Password123');
    component.registerForm.controls['confirmPassword'].setValue('Password123');

    component.onSubmit();

    expect(component.errorMessage()).toBe('Email already exists');
  });
});
