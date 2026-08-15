import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from './auth.service';

describe('authGuard', () => {
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authServiceMock = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'hasStoredToken']);
    routerMock = jasmine.createSpyObj('Router', ['createUrlTree']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    });
  });

  it('should allow access when user is authenticated', () => {
    authServiceMock.isAuthenticated.and.returnValue(true);
    authServiceMock.hasStoredToken.and.returnValue(true);

    const dummyRoute = {} as ActivatedRouteSnapshot;
    const dummyState = { url: '/dashboard' } as RouterStateSnapshot;

    const result = TestBed.runInInjectionContext(() => authGuard(dummyRoute, dummyState));

    expect(result).toBeTrue();
  });

  it('should redirect unauthenticated users to /login with returnUrl', () => {
    authServiceMock.isAuthenticated.and.returnValue(false);
    authServiceMock.hasStoredToken.and.returnValue(false);

    const dummyUrlTree = {} as UrlTree;
    routerMock.createUrlTree.and.returnValue(dummyUrlTree);

    const dummyRoute = {} as ActivatedRouteSnapshot;
    const dummyState = { url: '/dashboard' } as RouterStateSnapshot;

    const result = TestBed.runInInjectionContext(() => authGuard(dummyRoute, dummyState));

    expect(routerMock.createUrlTree).toHaveBeenCalledWith(['/login'], {
      queryParams: { returnUrl: '/dashboard' },
    });
    expect(result).toBe(dummyUrlTree);
  });
});
