import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../core/auth/auth.service';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { signal } from '@angular/core';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let apiServiceMock: jasmine.SpyObj<ApiService>;
  let authServiceMock: any;

  beforeEach(async () => {
    apiServiceMock = jasmine.createSpyObj('ApiService', ['getHealth', 'getDbHealth']);
    authServiceMock = {
      isAuthenticated: signal<boolean>(false),
      currentUser: signal(null),
      logout: jasmine.createSpy('logout'),
    };

    apiServiceMock.getHealth.and.returnValue(
      of({
        status: 'UP',
        application: 'AspirantOS',
        message: 'Backend is running successfully',
      })
    );

    apiServiceMock.getDbHealth.and.returnValue(
      of({
        status: 'UP',
        database: 'PostgreSQL',
        message: 'Database connection successful',
      })
    );

    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        { provide: ApiService, useValue: apiServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create HomeComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should display "AspirantOS" title and tagline', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('AspirantOS');
    expect(compiled.textContent).toContain('UPSC Preparation Command Center');
    expect(compiled.textContent).toContain('Plan.');
  });

  it('should update state to connected when health check succeeds', () => {
    expect(component.backendData()?.status).toBe('UP');
    expect(component.dbData()?.status).toBe('UP');
  });

  it('should handle backend error gracefully', () => {
    apiServiceMock.getHealth.and.returnValue(
      throwError(() => new Error('Connection refused'))
    );

    component.checkBackendHealth();
    fixture.detectChanges();

    expect(component.backendData()).toBeNull();
    expect(component.backendError()).toContain('Connection refused');
  });
});
