import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {

  name = '';
  email = '';
  password = '';
  organizationName = '';
  role = 'VIEWER';

  constructor(private auth: AuthService) {}

  register() {
    this.auth.register({
      name: this.name,
      email: this.email,
      password: this.password,
      organizationName: this.organizationName,
      role: this.role
    }).subscribe(res => {
      console.log('User created:', res);
      alert('User inserted successfully!');
    });
  }
}