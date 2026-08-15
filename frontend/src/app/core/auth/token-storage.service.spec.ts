import { TestBed } from '@angular/core/testing';
import { TokenStorageService } from './token-storage.service';

describe('TokenStorageService', () => {
  let service: TokenStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TokenStorageService],
    });
    service = TestBed.inject(TokenStorageService);
    service.clearToken();
  });

  afterEach(() => {
    service.clearToken();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should save and retrieve token', () => {
    service.saveToken('test-jwt-token-xyz');
    expect(service.getToken()).toBe('test-jwt-token-xyz');
    expect(service.hasToken()).toBeTrue();
  });

  it('should clear token from storage', () => {
    service.saveToken('test-jwt-token-xyz');
    expect(service.hasToken()).toBeTrue();

    service.clearToken();
    expect(service.getToken()).toBeNull();
    expect(service.hasToken()).toBeFalse();
  });
});
