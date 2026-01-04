import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';

@Component({
  selector: 'app-consumer-profile',
  imports: [CommonModule],
  templateUrl: './consumer-profile.html',
  styleUrl: './consumer-profile.css',
})
export class ConsumerProfile {
  loading = true;
  errorMessage = '';

  consumer: any = null;

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile() {
    this.http.get<any>(
      'http://localhost:8765/consumer-service/consumers/profile'
    ).subscribe({
      next: res => {
        this.consumer = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Unable to load profile';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
