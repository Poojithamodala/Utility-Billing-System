import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';

@Component({
  selector: 'app-consumer-tariffs',
  imports: [CommonModule],
  templateUrl: './consumer-tariffs.html',
  styleUrl: './consumer-tariffs.css',
})
export class ConsumerTariffs {
  utilities = ['ELECTRICITY', 'WATER', 'GAS', 'INTERNET'];
  selectedUtility = 'ELECTRICITY';

  tariffs: any[] = [];
  loading = false;
  error = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadTariffs(this.selectedUtility);
  }

  loadTariffs(utility: string) {
    this.selectedUtility = utility;
    this.loading = true;
    this.error = '';

    this.http.get<any[]>(
      `http://localhost:8765/connection-service/tariffs/utility/${utility}`
    ).subscribe({
      next: res => {
        this.tariffs = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Unable to load tariff plans';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  requestConnection(tariff: any) {

  const payload = {
    utilityType: tariff.utilityType,
    tariffPlanId: tariff.id,
    billingCycle: 'MONTHLY'
  };

  this.http.post(
    'http://localhost:8765/consumer-service/consumers/request-connection',
    payload,
    { responseType: 'text' } 
  ).subscribe({
    next: () => {
      alert('✅ Connection request submitted successfully, Wait for admin approval');
    },
    error: err => {
      alert(err.error?.message || 'Failed to request connection');
    }
  });
}
}
