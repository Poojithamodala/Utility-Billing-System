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
    { title: 'Active Connections', value: 33 },
    { title: 'Readings Pending', value: 16 },
    { title: 'Bills Generated (This Month)', value: 60 },
    { title: 'Bills Yet to Generate', value: 20 }
  ];
}
