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

  popupMessage = '';
  popupVisible = false;

  showPopup(message: string) {
    this.popupMessage = message;
    this.popupVisible = true;
  }

  closePopup() {
    this.popupVisible = false;
  }

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) { }

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
      payload
    ).subscribe({
      next: () => {
        this.showPopup(
          'Connection request submitted successfully. Please wait for Billing Officer approval.'
        );
      },
      error: err => {
        let backendMessage = 'Failed to request connection';

        if (err?.error?.error) {
          backendMessage = err.error.error;
        } else if (typeof err?.error === 'string') {
          backendMessage = err.error;
        } else if (err?.message) {
          backendMessage = err.message;
        }
        this.showPopup(backendMessage);
      }
    });
  }
}
