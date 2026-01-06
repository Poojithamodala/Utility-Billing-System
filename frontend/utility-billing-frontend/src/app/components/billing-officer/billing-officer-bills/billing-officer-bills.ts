import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';

@Component({
  selector: 'app-billing-officer-bills',
  imports: [CommonModule],
  templateUrl: './billing-officer-bills.html',
  styleUrl: './billing-officer-bills.css',
})
export class BillingOfficerBills {
  utilities = ['ELECTRICITY', 'WATER', 'GAS', 'INTERNET'];
  activeUtility = 'ELECTRICITY';

  allBills: any[] = [];
  filteredBills: any[] = [];

  loading = true;
  error = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadAllBills();
  }

  loadAllBills() {
    this.http.get<any[]>(
      'http://localhost:8765/billing-service/bills'
    ).subscribe({
      next: res => {
        this.allBills = res;
        this.filterByUtility(this.activeUtility);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Unable to load bills';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterByUtility(utility: string) {
    this.activeUtility = utility;
    this.filteredBills = this.allBills.filter(
      bill => bill.utilityType === utility
    );
  }
}
