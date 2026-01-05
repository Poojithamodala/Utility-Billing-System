import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';

@Component({
  selector: 'app-consumer-payments',
  imports: [CommonModule],
  templateUrl: './consumer-payments.html',
  styleUrl: './consumer-payments.css',
})
export class ConsumerPayments {
  payments: any[] = [];
  loading = true;
  errorMessage = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadPayments();
  }

  loadPayments() {
    const consumerId = localStorage.getItem('consumerId');

    if (!consumerId) {
      this.errorMessage = 'Consumer not found';
      this.loading = false;
      return;
    }

    this.http.get<any[]>(
      `http://localhost:8765/payment-service/payments/consumer/${consumerId}`
    ).subscribe({
      next: res => {
        this.payments = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Unable to load payment history';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
