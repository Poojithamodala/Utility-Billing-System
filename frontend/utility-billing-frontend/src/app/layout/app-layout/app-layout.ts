import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-app-layout',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './app-layout.html',
  styleUrl: './app-layout.css',
})
export class AppLayout {
  menu: { label: string; path: string }[] = [];

  ngOnInit() {
    const role = localStorage.getItem('role');

    if (role === 'ADMIN') {
      this.menu = [
        { label: 'Consumers', path: 'consumers' },
        { label: 'Officers', path: 'officers' },
        { label: 'Tariffs', path: 'tariffs' }
      ];
    }

    if (role === 'BILLING_OFFICER') {
      this.menu = [
        { label: 'Meter Readings', path: 'meter-readings' },
        { label: 'Generate Bills', path: 'bills' }
      ];
    }

    if (role === 'ACCOUNTS_OFFICER') {
      this.menu = [
        { label: 'Payments', path: 'payments' },
        { label: 'Outstanding', path: 'outstanding' }
      ];
    }

    if (role === 'CONSUMER') {
      this.menu = [
        { label: 'My Bills', path: 'bills' },
        { label: 'Payments', path: 'payments' }
      ];
    }
  }
}
