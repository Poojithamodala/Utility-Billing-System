import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  role = localStorage.getItem('role');

  constructor(private router: Router, private cdr: ChangeDetectorRef) {}

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
    this.cdr.detectChanges();
  }
}
