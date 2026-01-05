import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-pending-meter-readings',
  imports: [CommonModule, FormsModule],
  templateUrl: './pending-meter-readings.html',
  styleUrl: './pending-meter-readings.css',
})
export class PendingMeterReadings {
  utilities = ['ELECTRICITY', 'WATER', 'GAS', 'INTERNET'];
  activeUtility = 'ELECTRICITY';

  allPending: any[] = [];
  filteredPending: any[] = [];

  loading = true;
  error = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadPendingReadings();
  }

  loadPendingReadings() {
    this.loading = true;

    this.http.get<any[]>(
      'http://localhost:8765/meter-reading-service/meter-readings/pending'
    ).subscribe({
      next: res => {
        this.allPending = res.map(r => ({
          ...r,
          currentReading: null,
          readingSaved: false,
          meterReadingId: null
        }));
        this.filterByUtility(this.activeUtility);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Unable to load pending meter readings';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterByUtility(utility: string) {
    this.activeUtility = utility;
    this.filteredPending = this.allPending.filter(
      r => r.utilityType === utility
    );
  }

  submitReading(row: any) {

    if (row.currentReading == null) {
      alert('Enter current reading');
      return;
    }

    if (row.currentReading < (row.lastReadingValue || 0)) {
      alert('Current reading cannot be less than previous reading');
      return;
    }

    const payload = {
      connectionId: row.connectionId,
      currentReading: row.currentReading,
      readingDate: new Date().toISOString().split('T')[0]
    };

    this.http.post<any>(
      'http://localhost:8765/meter-reading-service/meter-readings',
      payload
    ).subscribe({
      next: res => {
        row.readingSaved = true;
        row.meterReadingId = res.id;
        alert('✅ Meter reading recorded');
        this.cdr.detectChanges();
      },
      error: err => {
        alert(err.error?.message || 'Failed to record reading');
        this.cdr.detectChanges();
      }
    });
  }

  generateBill(row: any) {

    const payload = {
      meterReadingId: row.meterReadingId
    };

    this.http.post(
      'http://localhost:8765/billing-service/bills/generate',
      payload
    ).subscribe({
      next: () => {
        alert('💰 Bill generated successfully');

        this.allPending = this.allPending.filter(
          r => r.connectionId !== row.connectionId
        );
        this.filterByUtility(this.activeUtility);
        this.cdr.detectChanges();
      },
      error: err => {
        alert(err.error?.message || 'Failed to generate bill');
        this.cdr.detectChanges();
      }
    });
  }
}
