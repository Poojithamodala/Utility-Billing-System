import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';

@Component({
  selector: 'app-consumer-bills',
  imports: [CommonModule],
  templateUrl: './consumer-bills.html',
  styleUrl: './consumer-bills.css',
})
export class ConsumerBills {
  bills: any[] = [];
  filteredBills: any[] = [];

  utilities = ['ELECTRICITY', 'WATER', 'GAS', 'INTERNET'];
  activeUtility = 'ELECTRICITY';

  loading = true;
  errorMessage = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

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
}
