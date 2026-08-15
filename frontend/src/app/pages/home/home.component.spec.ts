import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';
import { AuthService } from '../../core/auth/auth.service';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let authServiceMock: any;

  beforeEach(async () => {
    authServiceMock = {
      isAuthenticated: signal<boolean>(false),
      currentUser: signal(null),
      logout: jasmine.createSpy('logout'),
    };

    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
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
    expect(compiled.querySelector('h1')?.textContent).toContain('Plan. Study.');
    expect(compiled.textContent).toContain('UPSC Preparation Command Center');
  });

  it('should call authService.logout when onLogout is called', () => {
    component.onLogout();
    expect(authServiceMock.logout).toHaveBeenCalledWith('/');
  });
});
