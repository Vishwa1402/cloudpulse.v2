import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {

  email = '';
  password = '';
  message = '';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login() {

    this.http.post<{ token: string }>(
      'http://localhost:8080/api/auth/login',
      {
        email: this.email,
        password: this.password
      }
    ).subscribe({

      next: (res) => {

        console.log(res);

        localStorage.setItem('token', res.token);
        try {
          const payload = JSON.parse(atob(res.token.split('.')[1]));
          localStorage.setItem('role', payload.role || 'VIEWER');
        } catch (e) {
          console.error('Error parsing JWT', e);
        }

        this.router.navigate(['/dashboard']);
      },

      error: (err) => {

        console.error(err);

        this.message = 'Invalid email or password';
      }
    });
  }
}