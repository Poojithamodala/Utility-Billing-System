import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-account-officer-dashboard',
  imports: [CommonModule],
  templateUrl: './account-officer-dashboard.html',
  styleUrl: './account-officer-dashboard.css',
})
export class AccountOfficerDashboard {
  stats = {
    totalRevenue: 125000,
    outstanding: 34000,
    todayPayments: 12,
    unpaidBills: 27
  };

  recentPayments = [
    {
      email: 'karan@gmail.com',
      utility: 'ELECTRICITY',
      amount: 2450,
      mode: 'UPI',
      date: '2026-01-10'
    },
    {
      email: 'vijay@gmail.com',
      utility: 'WATER',
      amount: 980,
      mode: 'CARD',
      date: '2026-01-10'
    },
    {
      email: 'arya@gmail.com',
      utility: 'GAS',
      amount: 1540,
      mode: 'ONLINE',
      date: '2026-01-09'
    }
  ];
}
