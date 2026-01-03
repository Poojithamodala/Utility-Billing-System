import { Component } from '@angular/core';
import { AuthService } from '../../services/auth';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  credentials = {
    username: '',
    password: ''
  };

  loading = false;
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login() {
    this.error = '';
    this.loading = true;

    this.authService.login(this.credentials).subscribe({
      next: (res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);

        // Role-based redirect
        switch (res.role) {
          case 'ADMIN':
            this.router.navigate(['/admin']);
            break;
          case 'BILLING_OFFICER':
            this.router.navigate(['/billing']);
            break;
          case 'ACCOUNTS_OFFICER':
            this.router.navigate(['/accounts']);
            break;
          case 'CONSUMER':
            this.router.navigate(['/consumer']);
            break;
          default:
            this.router.navigate(['/login']);
        }
      },
      error: (err) => {
        this.error = err.error?.message || 'Login failed';
        this.loading = false;
      }
    });
  }
}
