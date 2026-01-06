import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-topbar',
  imports: [CommonModule],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
})
export class Topbar {
  role = localStorage.getItem('role');

  constructor(private router: Router, private cdr: ChangeDetectorRef) {}

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
    this.cdr.detectChanges();
  }
}
