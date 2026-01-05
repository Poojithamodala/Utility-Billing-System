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

  approve(request: any) {
    if (!request.meterNumber) {
      alert('Enter meter number');
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
        alert('✅ Connection approved');
        this.loadRequests(); 
      },
      error: err => {
        alert(err.error?.message || 'Approval failed');
      }
    });
  }
}
