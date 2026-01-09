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

  showConfirm = false;

logout() {
  this.showConfirm = true;
}

confirmLogout() {
  localStorage.clear();
  this.router.navigate(['/login']);
  this.showConfirm = false;
  this.cdr.detectChanges();
}

cancelLogout() {
  this.showConfirm = false;
}
  // logout() {
  // const confirmLogout = window.confirm('Are you sure you want to logout?');

  // if (!confirmLogout) {
  //   return;
  // }
  //   localStorage.clear();
  //   this.router.navigate(['/login']);
  //   this.cdr.detectChanges();
  // }
}
