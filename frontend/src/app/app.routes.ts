import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { DashboardComponent } from './features/dashboard/dashboard/dashboard';
import { AppLayoutComponent } from './shared/layout/app-layout/app-layout.component';
import { LandingComponent } from './features/landing/landing.component';

export const routes: Routes = [
  { path: '', component: LandingComponent, pathMatch: 'full' },

  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: '',
    component: AppLayoutComponent,
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard/dashboard')
            .then(m => m.DashboardComponent),
        canActivate: [authGuard]
      },
      {
        path: 'incident-rules',
        loadComponent: () =>
          import('./features/incident-rules/incident-rules.component')
            .then(m => m.IncidentRulesComponent),
        canActivate: [authGuard]
      },
      {
        path: 'ai-assistant',
        loadComponent: () =>
          import('./features/ai-assistant/ai-assistant.component')
            .then(m => m.AiAssistantComponent),
        canActivate: [authGuard]
      },
      {
        path: 'incident-console',
        loadComponent: () =>
          import('./features/incident-console/incident-console.component')
            .then(m => m.IncidentConsoleComponent),
        canActivate: [authGuard]
      },
      {
        path: 'services',
        loadComponent: () =>
          import('./features/service-registry/service-registry.component')
            .then(m => m.ServiceRegistryComponent),
        canActivate: [authGuard]
      },
      {
        path: 'audit-logs',
        loadComponent: () =>
          import('./features/audit-logs/audit-logs.component')
            .then(m => m.AuditLogsComponent),
        canActivate: [authGuard]
      },
      {
        path: 'notifications',
        loadComponent: () =>
          import('./features/notifications/notifications.component')
            .then(m => m.NotificationsComponent),
        canActivate: [authGuard]
      }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];


