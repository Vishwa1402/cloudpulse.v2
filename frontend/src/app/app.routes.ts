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
        path: 'budgets',
        loadComponent: () =>
          import('./features/budgets/budgets.component')
            .then(m => m.BudgetsComponent),
        canActivate: [authGuard]
      },
      {
        path: 'billing/:provider',
        loadComponent: () =>
          import('./features/billing/billing.component')
            .then(m => m.BillingComponent),
        canActivate: [authGuard]
      },
      {
        path: 'optimizer',
        loadComponent: () =>
          import('./features/optimizer/optimizer.component')
            .then(m => m.OptimizerComponent),
        canActivate: [authGuard]
      }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];


