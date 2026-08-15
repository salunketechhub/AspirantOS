import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';
import { ApiService } from '../../services/api.service';
import { of, throwError } from 'rxjs';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let apiServiceMock: jasmine.SpyObj<ApiService>;

  beforeEach(async () => {
    apiServiceMock = jasmine.createSpyObj('ApiService', ['getHealth', 'getDbHealth']);

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
      providers: [{ provide: ApiService, useValue: apiServiceMock }],
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
