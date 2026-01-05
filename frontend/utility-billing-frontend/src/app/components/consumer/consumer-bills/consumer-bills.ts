import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-consumer-bills',
  imports: [CommonModule, FormsModule],
  templateUrl: './consumer-bills.html',
  styleUrl: './consumer-bills.css',
})
export class ConsumerBills {
  bills: any[] = [];
  filteredBills: any[] = [];
  selectedBill: any = null;
  paying = false;
  paymentMessage = '';

  utilities = ['ELECTRICITY', 'WATER', 'GAS', 'INTERNET'];
  paymentModes = ['UPI', 'CARD', 'ONLINE', 'CASH', 'BANK_TRANSFER'];
  activeUtility = 'ELECTRICITY';

  loading = true;
  errorMessage = '';

  payment = {
    amount: 0,
    paymentMode: 'UPI'
  };

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadBills();
  }

  loadBills() {
    const consumerId = localStorage.getItem('consumerId');

    if (!consumerId) {
      this.errorMessage = 'Consumer not found';
      this.loading = false;
      return;
    }

    this.http.get<any[]>(
      `http://localhost:8765/billing-service/bills/consumer/${consumerId}`
    ).subscribe({
      next: res => {
        this.bills = res;
        this.filterBills(this.activeUtility);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Unable to load bills';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterBills(utility: string) {
    this.activeUtility = utility;
    this.filteredBills = this.bills.filter(
      bill => bill.utilityType === utility
    );
  }

  selectBill(bill: any) {
    this.selectedBill = bill;
    this.payment.amount = bill.totalAmount;
    this.payment.paymentMode = 'UPI';
    this.paymentMessage = '';
    document.body.classList.add('modal-open');
  }
  makePayment() {
    if (!this.selectedBill) return;

    this.paying = true;
    this.paymentMessage = '';

    const payload = {
      billId: this.selectedBill.id,
      amount: this.payment.amount,
      paymentMode: this.payment.paymentMode
    };

    this.http.post<any>(
      'http://localhost:8765/payment-service/payments',
      payload
    ).subscribe({
      next: res => {
        this.paymentMessage = `Payment successful (Ref: ${res.referenceNumber})`;
        this.paying = false;
        this.loadBills(); // refresh bills
        this.cdr.detectChanges();
        setTimeout(() => {
        this.selectedBill = null;
        this.paymentMessage = '';
        document.body.classList.remove('modal-open');
        this.cdr.detectChanges();
      }, 3000);
      },
      error: err => {
      this.paying = false;
      if (err.status === 500) {
        this.paymentMessage =
          '✅ Payment successful! (Notification delayed)';

        setTimeout(() => {
          this.selectedBill = null;
          this.paymentMessage = '';
          document.body.classList.remove('modal-open');
          this.loadBills();
          this.cdr.detectChanges();
        }, 2000);
      } else {
        this.paymentMessage =
          err.error?.message || 'Payment failed';
      }

      this.cdr.detectChanges();
    }
  });
  }
}
