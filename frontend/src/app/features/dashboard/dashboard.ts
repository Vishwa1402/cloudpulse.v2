import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent {

  constructor(private router: Router) {}

  logout() {

    // remove JWT token
    localStorage.removeItem('token');

    // redirect to login
    this.router.navigate(['/login']);
  }
}