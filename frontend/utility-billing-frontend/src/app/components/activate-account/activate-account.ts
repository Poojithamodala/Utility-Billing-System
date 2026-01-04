import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-activate-account',
  imports: [CommonModule, FormsModule],
  templateUrl: './activate-account.html',
  styleUrl: './activate-account.css',
})
export class ActivateAccount {
   form = {
    email: '',
    password: '',
    confirmPassword: ''
  };

  loading = false;
  error = '';
  success = '';

  constructor(private route: ActivatedRoute, private http: HttpClient, 
    private router: Router, private cdr: ChangeDetectorRef) {
    this.route.queryParams.subscribe(params => {
    if (params['email']) {
      this.form.email = params['email'];
    }
  });
  }

  activate() {
    this.error = '';
    this.success = '';

    if (!this.form.email || !this.form.password) {
      this.error = 'All fields are required';
      return;
    }

    if (this.form.password.length < 8) {
      this.error = 'Password must be at least 8 characters';
      return;
    }

    if (this.form.password !== this.form.confirmPassword) {
      this.error = 'Passwords do not match';
      return;
    }

    this.loading = true;

    this.http.post(
      'http://localhost:8765/auth-service/auth/activate',
      {
        email: this.form.email,
        password: this.form.password
      }
    ).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'Account activated successfully. Redirecting to login...';

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);

        this.cdr.detectChanges();
      },
      error: err => {
        this.loading = false;
        this.error = err.error?.message || 'Activation failed';
        this.cdr.detectChanges();
      }
    });
  }
}
