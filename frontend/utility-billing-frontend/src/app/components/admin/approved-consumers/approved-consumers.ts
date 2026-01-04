import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-approved-consumers',
  imports: [CommonModule, FormsModule],
  templateUrl: './approved-consumers.html',
  styleUrl: './approved-consumers.css',
})
export class ApprovedConsumers {
  consumers: any[] = [];
  editingConsumer: any = null;
  deletingConsumer: any = null;
  loading = true;
  error = '';
  editError = '';
  deleteError = '';

  editForm = {
    name: '',
    email: '',
    phone: '',
    address: ''
  };

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) { }

  ngOnInit() {
    this.fetchConsumers();
  }

  fetchConsumers() {
    const token = localStorage.getItem('token');

    this.http.get<any[]>(
      'http://localhost:8765/consumer-service/consumers',
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    ).subscribe({
      next: res => {
        this.consumers = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        this.error = 'Failed to load consumers';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openEdit(consumer: any) {
    this.editingConsumer = consumer;
    this.editForm = { ...consumer };
    this.editError = '';
  }

  closeEdit() {
    this.editingConsumer = null;
  }

  updateConsumer() {
    const token = localStorage.getItem('token');

    this.http.put(
      `http://localhost:8765/consumer-service/consumers/${this.editingConsumer.id}`,
      this.editForm,
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    ).subscribe({
      next: () => {
        this.fetchConsumers();
        this.closeEdit();
        this.cdr.detectChanges();
      },
      error: err => {
        this.editError = err.error?.message || 'Update failed';
        this.cdr.detectChanges();
      }
    });
  }

  confirmDelete(consumer: any) {
    this.deletingConsumer = consumer;
    this.deleteError = '';
  }

  cancelDelete() {
    this.deletingConsumer = null;
  }

  deleteConsumer() {
    const token = localStorage.getItem('token');

    this.http.delete(
      `http://localhost:8765/consumer-service/consumers/${this.deletingConsumer.id}`,
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    ).subscribe({
      next: () => {
        this.fetchConsumers(); 
        this.deletingConsumer = null;
        this.cdr.detectChanges();
      },
      error: err => {
        this.deleteError = err.error?.message || 'Delete failed';
        this.cdr.detectChanges();
      }
    });
  }

}
