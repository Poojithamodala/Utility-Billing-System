import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-manage-tariff',
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-tariff.html',
  styleUrl: './manage-tariff.css',
})
export class ManageTariff {
  utilities = ['ELECTRICITY', 'WATER', 'GAS', 'INTERNET'];
  activeUtility = 'ELECTRICITY';

  tariffs: any[] = [];
  loading = false;
  error = '';

  editingTariff: any = null;

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadTariffs(this.activeUtility);
  }

  loadTariffs(utility: string) {
    this.activeUtility = utility;
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
        this.error = 'Unable to load tariffs';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  editTariff(tariff: any) {
    this.editingTariff = JSON.parse(JSON.stringify(tariff));
  }

  updateTariff() {
    this.http.put(
      `http://localhost:8765/connection-service/tariffs/${this.editingTariff.id}`,
      this.editingTariff
    ).subscribe({
      next: () => {
        alert('✅ Tariff updated successfully');
        this.editingTariff = null;
        this.loadTariffs(this.activeUtility);
        this.cdr.detectChanges();
      },
      error: err => {
        alert(err.error || 'Update failed');
        this.cdr.detectChanges();
      }
    });
  }

  deleteTariff(tariffId: string) {
    if (!confirm('Are you sure you want to delete this tariff?')) return;

    this.http.delete(
      `http://localhost:8765/connection-service/tariffs/${tariffId}`
    ).subscribe({
      next: () => {
        alert('Tariff deleted');
        this.loadTariffs(this.activeUtility);
        this.cdr.detectChanges();
      },
      error: err => {
        alert(err.error || 'Cannot delete tariff');
        this.cdr.detectChanges();
      }
    });
  }
}
