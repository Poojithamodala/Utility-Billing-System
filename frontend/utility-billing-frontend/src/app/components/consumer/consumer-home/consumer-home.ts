import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-consumer-home',
  imports: [],
  templateUrl: './consumer-home.html',
  styleUrl: './consumer-home.css',
})
export class ConsumerHome {
  summary = {
    activeConnections: 0,
    pendingBills: 0,
    totalDue: 0,
    lastPayment: null as null | { amount: number; date: string }
  };

  recentBills: any[] = [];
  alerts: string[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard() {
    this.http.get<any>('http://localhost:8765/consumer-service/dashboard')
      .subscribe(res => {
        this.summary = res.summary;
        this.recentBills = res.recentBills;
        this.alerts = res.alerts;
      });
  }
}
