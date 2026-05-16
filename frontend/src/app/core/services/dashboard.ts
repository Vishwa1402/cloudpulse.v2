import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

export interface DashboardSummary {

  monthlyCost: number;
  activeResources: number;
  alerts: number;
  savings: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private api =
    'http://localhost:8080/api/dashboard';

  constructor(
    private http: HttpClient
  ) {}

  getSummary():
    Observable<DashboardSummary> {

    return this.http.get<DashboardSummary>(
      `${this.api}/summary`
    );
  }
}