import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { WebSocketService } from '../../../core/services/websocket.service';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule
  ],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit, OnDestroy {

  summary: any = {};

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private webSocketService: WebSocketService
  ) {}

  ngOnInit(): void {

    const token = localStorage.getItem('token');

    const headers = {
      Authorization: `Bearer ${token}`
    };

    
    this.http.get(
  'http://localhost:8080/api/dashboard/summary',
  {
    headers: {
      Authorization: `Bearer ${token}`
    }
  }
)
.subscribe({
  next: (data) => {
    this.summary = data;
    this.cdr.detectChanges();
    console.log(data);
  },
  error: (err) => {
    console.error(err);
  }
});

    // Connect to WebSocket for live data updates
    this.webSocketService.connect();
    this.webSocketService.costs$.subscribe((liveData: any) => {
      this.summary = liveData;
      this.cdr.detectChanges();
    });
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }
}



