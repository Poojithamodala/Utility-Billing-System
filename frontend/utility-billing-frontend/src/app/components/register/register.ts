import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  form = {
    name: '',
    email: '',
    phone: '',
    address: ''
  };

  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef, private router: Router) { }

  submit() {
    this.errorMessage = '';
    this.successMessage = '';
    if (this.loading) return;

    const payload = {
      name: this.form.name.trim(),
      email: this.form.email.trim().toLowerCase(),
      phone: this.form.phone.trim(),
      address: this.form.address.trim()
    };

    if (!payload.name || payload.name.length < 3) {
      this.errorMessage = 'Name must be at least 3 characters';
      return;
    }

    if (!/^[A-Za-z ]+$/.test(payload.name)) {
      this.errorMessage = 'Name can contain only letters and spaces';
      return;
    }

    if (!payload.email) {
      this.errorMessage = 'Email is required';
      return;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(payload.email)) {
      this.errorMessage = 'Enter a valid email address';
      return;
    }

    if (!/^\d{10}$/.test(payload.phone)) {
      this.errorMessage = 'Phone number must be 10 digits';
      return;
    }

    if (payload.address.length < 5) {
      this.errorMessage = 'Address must be at least 5 characters';
      return;
    }

    this.loading = true;

    this.http.post(
      'http://localhost:8765/consumer-service/consumers/request',
      payload
    ).subscribe({
      next: () => {
        this.successMessage =
          '✅ Registration submitted successfully. Admin approval required.';
        this.loading = false;
        this.form = { name: '', email: '', phone: '', address: '' };
        this.cdr.detectChanges();
      },
      error: err => {
        this.loading = false;
        console.log('Registration error:', err);
        let message = 'Registration failed. Please try again.';
        if (err.error) {
          if (typeof err.error === 'string') {
            message = err.error;
          } else if (err.error.message) {
            message = err.error.message;
          } else if (err.error.error) {
            message = err.error.error;
          }
        }
        this.errorMessage = message;
        if (message.toLowerCase().includes('approved account')) {
          setTimeout(() => this.router.navigate(['/login']), 2000);
        }
        this.cdr.detectChanges();
      }

    });
  }
}
