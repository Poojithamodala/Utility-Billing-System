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
    this.loading = true;

    this.http.post<any>(
      'http://localhost:8765/auth-service/auth/login',
      this.loginData
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
        this.cdr.detectChanges();
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Invalid credentials';
        this.loading = false;
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
