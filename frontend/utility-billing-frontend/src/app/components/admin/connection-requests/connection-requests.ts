import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-connection-requests',
  imports: [CommonModule, FormsModule],
  templateUrl: './connection-requests.html',
  styleUrl: './connection-requests.css',
})
export class ConnectionRequests {
  requests: any[] = [];
  loading = true;
  error = '';

  showPopup = false;
  popupTitle = '';
  popupMessage = '';
  popupType: 'success' | 'error' | 'warning' = 'warning';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadRequests();
  }

  loadRequests() {
    this.http.get<any[]>(
      'http://localhost:8765/consumer-service/consumers/connection-requests?status=PENDING'
    ).subscribe({
      next: res => {
        this.requests = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Unable to load requests';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openPopup(title: string, message: string, type: 'success' | 'error' | 'warning') {
    this.popupTitle = title;
    this.popupMessage = message;
    this.popupType = type;
    this.showPopup = true;
    this.cdr.detectChanges();
  }

  closePopup() {
    this.showPopup = false;
  }

  approve(request: any) {
    if (!request.meterNumber || !request.meterNumber.trim()) {
      this.openPopup(
        'Validation Error',
        'Meter number is required before approval.',
        'warning'
      );
      return;
    }

    const payload = {
      requestId: request.id,
      meterNumber: request.meterNumber
    };

    this.http.post(
      'http://localhost:8765/connection-service/connections/approve',
      payload
    ).subscribe({
      next: () => {
        this.openPopup(
          'Approved',
          '✅ Connection approved successfully.',
          'success'
        );
        this.loadRequests();
      },
      error: err => {
        this.openPopup(
          'Approval Failed',
          err.error?.message || 'Unable to approve connection.',
          'error'
        );
      }
    });
  }
}
