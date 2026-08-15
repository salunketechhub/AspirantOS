import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ApiService } from './api.service';
import { environment } from '../../environments/environment';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ApiService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch backend health', () => {
    const mockHealth = {
      status: 'UP',
      application: 'AspirantOS',
      message: 'Backend is running successfully',
    };

    service.getHealth().subscribe((data) => {
      expect(data).toEqual(mockHealth);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/health`);
    expect(req.request.method).toBe('GET');
    req.flush(mockHealth);
  });

  it('should fetch database health', () => {
    const mockDbHealth = {
      status: 'UP',
      database: 'PostgreSQL',
      message: 'Database connection successful',
    };

    service.getDbHealth().subscribe((data) => {
      expect(data).toEqual(mockDbHealth);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/health/db`);
    expect(req.request.method).toBe('GET');
    req.flush(mockDbHealth);
  });
});
