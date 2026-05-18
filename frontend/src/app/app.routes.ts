import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { DashboardComponent } from './features/dashboard/dashboard/dashboard';
import { AppLayoutComponent } from './shared/layout/app-layout/app-layout.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

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
      }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];


