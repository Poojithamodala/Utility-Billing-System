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

  showPopup = false;
  popupTitle = '';
  popupMessage = '';
  popupType: 'success' | 'error' | 'warning' = 'warning';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

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
    if (!this.tariff.utilityType) {
      this.openPopup('Validation Error', 'Please select a utility type.', 'warning');
      return;
    }

    if (!this.tariff.name || this.tariff.name.trim().length < 3) {
      this.openPopup(
        'Validation Error',
        'Tariff name must be at least 3 characters.',
        'warning'
      );
      return;
    }
    if (this.tariff.fixedCharge < 0) {
      this.openPopup('Validation Error', 'Fixed charge cannot be negative.', 'warning');
      return;
    }

    if (this.tariff.taxPercentage < 0 || this.tariff.taxPercentage > 100) {
      this.openPopup(
        'Validation Error',
        'Tax percentage must be between 0 and 100.',
        'warning'
      );
      return;
    }

    for (const slab of this.tariff.slabs) {
      if (slab.fromUnit < 0 || slab.toUnit < 0) {
        this.openPopup(
          'Invalid Slab',
          'Slab units cannot be negative.',
          'warning'
        );
        return;
      }

      if (slab.fromUnit > slab.toUnit) {
        this.openPopup(
          'Invalid Slab',
          'From Unit cannot be greater than To Unit.',
          'warning'
        );
        return;
      }

      if (slab.rate <= 0) {
        this.openPopup(
          'Invalid Rate',
          'Slab rate must be greater than 0.',
          'warning'
        );
        return;
      }
    }
    this.message = '';
    this.error = '';
    this.loading = true;

    this.http.post(
      'http://localhost:8765/connection-service/tariffs',
      this.tariff
    ).subscribe({
      next: () => {
        this.openPopup(
          'Success',
          '✅ Tariff created successfully.',
          'success'
        );
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        this.openPopup(
          'Creation Failed',
          err.error || 'Failed to create tariff.',
          'error'
        );
        this.loading = false;
      }
    });
  }
}
