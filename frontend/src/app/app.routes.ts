import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    title: 'AspirantOS — UPSC Preparation Command Center',
  },
  {
    path: 'login',
    component: LoginComponent,
    title: 'Sign In — AspirantOS',
  },
  {
    path: 'register',
    component: RegisterComponent,
    title: 'Create Account — AspirantOS',
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard],
    title: 'Dashboard — AspirantOS',
  },
  {
    path: '**',
    redirectTo: '',
  },
];
