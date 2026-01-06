import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  loginData = {
    username: '',
    password: ''
  };

  loading = false;
  errorMessage = '';

  constructor(private http: HttpClient, private router: Router, private cdr: ChangeDetectorRef) { }

  login() {
    this.errorMessage = '';
    const username = this.loginData.username?.trim();
    const password = this.loginData.password;

    if (!username) {
      this.errorMessage = 'Username is required';
      return;
    }

    if (username.length < 3 || username.length > 20) {
      this.errorMessage = 'Username must be 3–20 characters';
      return;
    }

    if (!password) {
      this.errorMessage = 'Password is required';
      return;
    }

    if (password.length < 8) {
      this.errorMessage = 'Password must be at least 8 characters';
      return;
    }

    this.loading = true;

    this.http.post<any>(
      'http://localhost:8765/auth-service/auth/login',
      {
        username,
        password
      }
    ).subscribe({
      next: res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);

        switch (res.role) {
          case 'ADMIN':
            this.router.navigate(['/admin/home']);
            break;

          case 'BILLING_OFFICER':
            this.router.navigate(['/billing/home']);
            break;

          case 'ACCOUNTS_OFFICER':
            this.router.navigate(['/accounts/home']);
            break;

          case 'CONSUMER':
            this.fetchConsumerProfile();
            break;
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        this.loading = false;
        if (err.error) {
          if (err.error.message) {
            this.errorMessage = err.error.message;
          }
          else if (typeof err.error === 'string') {
            this.errorMessage = err.error;
          }
          else if (err.error.error) {
            this.errorMessage = err.error.error;
          }
          else {
            this.errorMessage = 'Login failed. Please try again.';
          }
        } else {
          this.errorMessage = 'Unable to connect to server';
        }
        this.cdr.detectChanges();
      }
    });
  }

  fetchConsumerProfile() {
    this.http.get<any>(
      'http://localhost:8765/consumer-service/consumers/profile'
    ).subscribe({
      next: consumer => {
        localStorage.setItem('consumerId', consumer.id);
        this.router.navigate(['/consumer/home']);
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Unable to load consumer profile';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
