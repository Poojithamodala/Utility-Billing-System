import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { ViewChild, ElementRef, AfterViewInit } from '@angular/core';

import {
  Chart,
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  PieController,
  ArcElement,
  LineController,
  LineElement,
  PointElement,
  Tooltip,
  Legend
} from 'chart.js';

Chart.register(
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  PieController,
  ArcElement,
  LineController,
  LineElement,
  PointElement,
  Tooltip,
  Legend
);

@Component({
  selector: 'app-admin-reports',
  imports: [CommonModule],
  templateUrl: './admin-reports.html',
  styleUrl: './admin-reports.css',
})
export class AdminReports implements AfterViewInit {

  @ViewChild('revenueCanvas') revenueCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('utilityCanvas') utilityCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('growthCanvas') growthCanvas!: ElementRef<HTMLCanvasElement>;

  private revenueChart?: Chart;
  private utilityChart?: Chart;
  private growthChart?: Chart;

  revenueMonthly: any[] = [];
  outstandingTotal = 0;
  utilityConsumption: any[] = [];
  consumerGrowth: any[] = [];

  loading = true;
  error = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngAfterViewInit() {
    // charts will be rendered AFTER data load
  }

  ngOnInit() {
    this.loadReports();
  }

  loadReports() {
  Promise.all([
    this.http.get<any[]>('http://localhost:8765/payment-service/payments/revenue/monthly').toPromise(),
    this.http.get<any>('http://localhost:8765/billing-service/bills/outstanding/total').toPromise(),
    this.http.get<any[]>('http://localhost:8765/meter-reading-service/meter-readings/consumption/utility').toPromise(),
    this.http.get<any[]>('http://localhost:8765/consumer-service/consumers/reports/growth').toPromise()
  ])
  .then(([revenue, outstanding, consumption, growth]) => {
    this.revenueMonthly = revenue || [];
    this.outstandingTotal = outstanding?.totalOutstandingAmount || 0;
    this.utilityConsumption = consumption || [];
    this.consumerGrowth = growth || [];

    this.loading = false;
    this.cdr.detectChanges();

    // ✅ Delay chart rendering until DOM is stable
    setTimeout(() => this.renderCharts(), 0);
  })
  .catch(() => {
    this.error = 'Failed to load reports';
    this.loading = false;
    this.cdr.detectChanges();
  });
}

  renderCharts() {
  this.renderRevenueChart();
  this.renderUtilityChart();
  this.renderConsumerGrowthChart();
}

renderRevenueChart() {
  this.revenueChart?.destroy();

  this.revenueChart = new Chart(this.revenueCanvas.nativeElement, {
    type: 'bar',
    data: {
      labels: this.revenueMonthly.map(r => r.month),
      datasets: [{
        label: 'Revenue (₹)',
        data: this.revenueMonthly.map(r => r.totalRevenue),
        backgroundColor: '#5F9EA0',
        borderRadius: 8
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: false } }
    }
  });
}

renderUtilityChart() {
  this.utilityChart?.destroy();

  this.utilityChart = new Chart(this.utilityCanvas.nativeElement, {
    type: 'pie',
    data: {
      labels: this.utilityConsumption.map(u => u.utilityType),
      datasets: [{
        data: this.utilityConsumption.map(u => u.totalUnitsConsumed),
        backgroundColor: ['#5F9EA0', '#A8DADC', '#457B9D', '#1D3557']
      }]
    }
  });
}

renderConsumerGrowthChart() {
  this.growthChart?.destroy();

  this.growthChart = new Chart(this.growthCanvas.nativeElement, {
    type: 'line',
    data: {
      labels: this.consumerGrowth.map(c => c.month),
      datasets: [{
        label: 'New Consumers',
        data: this.consumerGrowth.map(c => c.consumerCount),
        borderColor: '#5F9EA0',
        backgroundColor: 'rgba(95,158,160,0.15)',
        fill: true,
        tension: 0.4,
        pointRadius: 4
      }]
    }
  });
}

  
}
