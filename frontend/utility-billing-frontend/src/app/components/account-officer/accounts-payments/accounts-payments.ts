import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';

@Component({
  selector: 'app-accounts-payments',
  imports: [CommonModule],
  templateUrl: './accounts-payments.html',
  styleUrl: './accounts-payments.css',
})
export class AccountsPayments {
  utilities = ['ELECTRICITY', 'WATER', 'GAS', 'INTERNET'];
  activeUtility = 'ELECTRICITY';

  allPayments: any[] = [];
  filteredPayments: any[] = [];

  loading = true;
  error = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadPayments();
  }

  loadPayments() {
    this.http.get<any[]>(
      'http://localhost:8765/payment-service/payments'
    ).subscribe({
      next: res => {
        this.allPayments = res;
        this.filterByUtility(this.activeUtility);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Unable to load payments';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterByUtility(utility: string) {
    this.activeUtility = utility;
    this.filteredPayments = this.allPayments.filter(
      p => p.utilityType === utility
    );
  }
}
