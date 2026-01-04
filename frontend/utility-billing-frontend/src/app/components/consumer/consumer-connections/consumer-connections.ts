import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';

@Component({
  selector: 'app-consumer-connections',
  imports: [CommonModule],
  templateUrl: './consumer-connections.html',
  styleUrl: './consumer-connections.css',
})
export class ConsumerConnections {
  connections: any[] = [];
  loading = true;
  errorMessage = '';

  consumerId = localStorage.getItem('consumerId'); 

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadConnections();
  }

  loadConnections() {
    this.http.get<any[]>(
      `http://localhost:8765/connection-service/connections/consumer/${this.consumerId}`
    ).subscribe({
      next: res => {
        this.connections = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        this.errorMessage = err.error?.message || 'No active connections found';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
