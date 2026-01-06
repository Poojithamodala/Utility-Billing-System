import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';

@Component({
  selector: 'app-outstanding-bills',
  imports: [CommonModule],
  templateUrl: './outstanding-bills.html',
  styleUrl: './outstanding-bills.css',
})
export class OutstandingBills {
  utilities = ['ELECTRICITY', 'WATER', 'GAS', 'INTERNET'];
  activeUtility = 'ELECTRICITY';

  allBills: any[] = [];
  filteredBills: any[] = [];

  loading = true;
  error = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadOutstandingBills();
  }

  loadOutstandingBills() {
    this.loading = true;

    this.http.get<any[]>(
      'http://localhost:8765/billing-service/bills/outstanding'
    ).subscribe({
      next: res => {
        this.allBills = res;
        this.filterByUtility(this.activeUtility);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Unable to load outstanding bills';
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
