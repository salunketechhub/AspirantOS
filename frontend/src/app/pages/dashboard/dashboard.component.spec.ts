import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { AuthService } from '../../core/auth/auth.service';
import { ProgressService } from '../../services/progress.service';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { UserResponse } from '../../core/auth/auth.models';
import { OverallProgressResponse } from '../../models/progress.models';
import { signal } from '@angular/core';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let authServiceMock: any;
  let progressServiceMock: jasmine.SpyObj<ProgressService>;

  const mockUser: UserResponse = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    firstName: 'Aarav',
    lastName: 'Sharma',
    email: 'aarav@example.com',
    role: 'USER',
  };

  const mockProgress: OverallProgressResponse = {
    totalTopics: 35,
    completedTopics: 14,
    inProgressTopics: 6,
    notStartedTopics: 15,
    completionPercentage: 40,
    prelimsPercentage: 50,
    mainsPercentage: 30,
    optionalPercentage: 0,
  };

  beforeEach(async () => {
    authServiceMock = {
      currentUser: signal<UserResponse | null>(mockUser),
      userName: signal<string>('Aarav Sharma'),
      logout: jasmine.createSpy('logout'),
    };

    progressServiceMock = jasmine.createSpyObj('ProgressService', ['getOverallProgress']);
    progressServiceMock.getOverallProgress.and.returnValue(of(mockProgress));

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: ProgressService, useValue: progressServiceMock },
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
    expect(compiled.querySelector('h1')?.textContent).toContain('Welcome back, Aarav');
  });

  it('should load overall progress on init', () => {
    expect(progressServiceMock.getOverallProgress).toHaveBeenCalled();
    expect(component.overallProgress()).toEqual(mockProgress);
    expect(component.overallProgress()?.completionPercentage).toBe(40);
  });

  it('should call authService.logout on sign out click', () => {
    component.onLogout();
    expect(authServiceMock.logout).toHaveBeenCalledWith('/login');
  });
});
