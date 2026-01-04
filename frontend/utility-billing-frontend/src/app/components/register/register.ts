import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule],
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

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  submit() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.http.post(
      'http://localhost:8765/consumer-service/consumers/request',
      this.form
    ).subscribe({
      next: () => {
        this.successMessage =
          'Registration submitted successfully. Admin approval required.';
        this.loading = false;
        this.form = { name: '', email: '', phone: '', address: '' };
        this.cdr.detectChanges();
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Registration failed';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
