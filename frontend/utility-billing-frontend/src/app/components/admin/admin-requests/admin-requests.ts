import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-requests',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-requests.html',
  styleUrl: './admin-requests.css',
})
export class AdminRequests {
  requests: any[] = [];
  loading = false;
  error = '';

  showRejectModal = false;
  rejectReason = '';
  selectedRequestId: string | null = null;

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadRequests();
  }

  private getAuthHeaders() {
    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${localStorage.getItem('token')}`
      })
    };
  }

  loadRequests() {
    this.loading = true;
    this.http.get<any[]>(
      'http://localhost:8765/consumer-service/consumers/requests',
      this.getAuthHeaders()
    ).subscribe({
      next: res => {
        this.requests = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        this.error = 'Failed to load requests';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  approve(id: string) {
    this.http.post(
      `http://localhost:8765/consumer-service/consumers/requests/${id}/approve`,
      {},
      this.getAuthHeaders()
    ).subscribe({
      next: () => this.loadRequests(),
      error: () => alert('Approval failed')
    });
  }

  openReject(id: string) {
    this.selectedRequestId = id;
    this.rejectReason = '';
    this.showRejectModal = true;
    this.cdr.detectChanges();
  }

  reject() {
    if (!this.rejectReason.trim()) return;

    this.http.post(
      `http://localhost:8765/consumer-service/consumers/requests/${this.selectedRequestId}/reject`,
      { reason: this.rejectReason },
      this.getAuthHeaders()
    ).subscribe({
      next: () => {
        this.showRejectModal = false;
        this.loadRequests();
      },
      error: () => alert('Rejection failed')
    });
  }

  closeModal() {
    this.showRejectModal = false;
    this.cdr.detectChanges();
  }
}
