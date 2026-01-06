import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-create-tariff',
  imports: [CommonModule, FormsModule],
  templateUrl: './create-tariff.html',
  styleUrl: './create-tariff.css',
})
export class CreateTariff {
  utilities = ['ELECTRICITY', 'WATER', 'GAS', 'INTERNET'];

  tariff = {
    utilityType: '',
    name: '',
    fixedCharge: 0,
    taxPercentage: 0,
    slabs: [
      { fromUnit: 0, toUnit: 100, rate: 1 }
    ]
  };

  loading = false;
  message = '';
  error = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  addSlab() {
    const last = this.tariff.slabs[this.tariff.slabs.length - 1];

    this.tariff.slabs.push({
      fromUnit: last.toUnit + 1,
      toUnit: last.toUnit + 100,
      rate: 1
    });
  }

  removeSlab(index: number) {
    if (this.tariff.slabs.length === 1) return;
    this.tariff.slabs.splice(index, 1);
  }

  createTariff() {
    this.message = '';
    this.error = '';
    this.loading = true;

    this.http.post(
      'http://localhost:8765/connection-service/tariffs',
      this.tariff
    ).subscribe({
      next: () => {
        this.message = '✅ Tariff created successfully';
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        this.error = err.error || 'Failed to create tariff';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
