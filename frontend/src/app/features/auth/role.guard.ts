import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const requiredRoles = route.data['roles'] as Array<string>;
    const userRole = localStorage.getItem('role');

    if (!userRole) {
      this.router.navigate(['/login']);
      return false;
    }

    if (requiredRoles && requiredRoles.indexOf(userRole) === -1) {
      // User doesn't have required role, redirect to dashboard
      this.router.navigate(['/dashboard']);
      return false;
    }

    return true;
  }
}
