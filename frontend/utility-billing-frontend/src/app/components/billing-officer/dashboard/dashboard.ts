import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  stats = [
    { title: 'Active Connections', value: 128 },
    { title: 'Readings Pending', value: 24 },
    { title: 'Bills Generated (This Month)', value: 96 },
    { title: 'Bills Yet to Generate', value: 32 }
  ];
}
