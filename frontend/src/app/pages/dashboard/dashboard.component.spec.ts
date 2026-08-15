import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { AuthService } from '../../core/auth/auth.service';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { UserResponse } from '../../core/auth/auth.models';
import { signal } from '@angular/core';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let authServiceMock: any;

  const mockUser: UserResponse = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    firstName: 'Aarav',
    lastName: 'Sharma',
    email: 'aarav@example.com',
    role: 'USER',
  };

  beforeEach(async () => {
    authServiceMock = {
      currentUser: signal<UserResponse | null>(mockUser),
      userName: signal<string>('Aarav Sharma'),
      fetchCurrentUser: jasmine.createSpy('fetchCurrentUser').and.returnValue(of(mockUser)),
      logout: jasmine.createSpy('logout'),
    };

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create DashboardComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should display welcome title with user first name', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Welcome to AspirantOS, Aarav');
  });

  it('should test /api/auth/me on init and store test data', () => {
    expect(authServiceMock.fetchCurrentUser).toHaveBeenCalled();
    expect(component.testProfileData()).toEqual(mockUser);
  });

  it('should call authService.logout on sign out click', () => {
    component.onLogout();
    expect(authServiceMock.logout).toHaveBeenCalledWith('/login');
  });
});
