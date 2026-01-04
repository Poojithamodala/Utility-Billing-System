import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-home',
  imports: [],
  templateUrl: './admin-home.html',
  styleUrl: './admin-home.css',
})
export class AdminHome {
  stats = {
    pendingRequests: 5,
    totalConsumers: 128,
    activeConnections: 94
  };
}
