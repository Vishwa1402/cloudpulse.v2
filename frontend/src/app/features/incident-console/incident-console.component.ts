import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';

type Incident = {
  id: number;
  serviceName: string;
  metricType: string;
  metricValue: number;
  severity: string;
  status: string;
  description: string;
  aiSummary: string;
  detectedAt: string;
  resolvedAt: string;
};

@Component({
  selector: 'app-incident-console',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h2 class="text-title-md font-bold text-gray-800 dark:text-white/90">Incident Command Console</h2>
          <p class="text-sm text-gray-500">Autonomous anomaly alerts log with real-time AI-assisted root-cause diagnosis.</p>
        </div>
        <div class="flex gap-2">
          <button (click)="loadIncidents()" class="rounded-lg border border-gray-300 dark:border-gray-700 px-4 py-2 text-sm font-medium hover:bg-gray-50 dark:hover:bg-white/[0.02] transition-all">
            Refresh
          </button>
        </div>
      </div>

      <!-- Quick Metrics Summary -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-5">
        <div class="rounded-2xl border border-gray-200 bg-white p-5 dark:border-gray-800 dark:bg-white/[0.03]">
          <span class="text-sm font-medium text-gray-500">Active Outages</span>
          <h3 class="text-2xl font-bold mt-1 text-rose-500">{{ getActiveCount() }}</h3>
        </div>
        <div class="rounded-2xl border border-gray-200 bg-white p-5 dark:border-gray-800 dark:bg-white/[0.03]">
          <span class="text-sm font-medium text-gray-500">Resolved Incidents</span>
          <h3 class="text-2xl font-bold mt-1 text-emerald-500">{{ getResolvedCount() }}</h3>
        </div>
        <div class="rounded-2xl border border-gray-200 bg-white p-5 dark:border-gray-800 dark:bg-white/[0.03]">
          <span class="text-sm font-medium text-gray-500">SLA Availability</span>
          <h3 class="text-2xl font-bold mt-1 text-blue-500">99.98%</h3>
        </div>
      </div>

      <!-- Incidents Log Table -->
      <div class="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03]">
        <h3 class="text-lg font-medium mb-4 text-gray-800 dark:text-white/90">Anomaly Incident History</h3>
        
        <div class="overflow-x-auto">
          <table class="w-full text-left">
            <thead>
              <tr class="border-b border-gray-200 dark:border-gray-800">
                <th class="py-3 px-4 font-medium text-gray-500">Node/Service</th>
                <th class="py-3 px-4 font-medium text-gray-500">Severity</th>
                <th class="py-3 px-4 font-medium text-gray-500">Metric Type</th>
                <th class="py-3 px-4 font-medium text-gray-500">Description</th>
                <th class="py-3 px-4 font-medium text-gray-500">Status</th>
                <th class="py-3 px-4 font-medium text-gray-500">Detected At</th>
                <th class="py-3 px-4 font-medium text-gray-500 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              <ng-container *ngFor="let incident of incidents">
                <!-- Main Row -->
                <tr class="border-b border-gray-100 dark:border-gray-800/50 hover:bg-gray-50 dark:hover:bg-white/[0.01] transition-all cursor-pointer"
                    (click)="toggleExpand(incident.id)">
                  <td class="py-4 px-4 font-semibold text-gray-800 dark:text-white/90 flex items-center gap-2">
                    <span class="w-2.5 h-2.5 rounded-full" [ngClass]="{'bg-rose-500 animate-pulse': incident.status === 'ACTIVE', 'bg-emerald-500': incident.status === 'RESOLVED'}"></span>
                    {{ incident.serviceName }}
                  </td>
                  <td class="py-4 px-4">
                    <span class="rounded-full px-2.5 py-0.5 text-xs font-bold"
                          [ngClass]="{
                            'bg-rose-100 dark:bg-rose-950/30 text-rose-700 dark:text-rose-400': incident.severity === 'CRITICAL',
                            'bg-amber-100 dark:bg-amber-950/30 text-amber-700 dark:text-amber-400': incident.severity === 'HIGH',
                            'bg-blue-100 dark:bg-blue-950/30 text-blue-700 dark:text-blue-400': incident.severity === 'MEDIUM' || incident.severity === 'LOW'
                          }">
                      {{ incident.severity }}
                    </span>
                  </td>
                  <td class="py-4 px-4 font-medium text-gray-700 dark:text-gray-300">{{ incident.metricType }}</td>
                  <td class="py-4 px-4 text-gray-500 dark:text-gray-400 text-sm max-w-xs truncate">{{ incident.description }}</td>
                  <td class="py-4 px-4">
                    <span class="rounded-full px-2.5 py-0.5 text-xs font-semibold"
                          [ngClass]="{
                            'bg-rose-50 dark:bg-rose-950/20 text-rose-600 dark:text-rose-400': incident.status === 'ACTIVE',
                            'bg-emerald-50 dark:bg-emerald-950/20 text-emerald-600 dark:text-emerald-400': incident.status === 'RESOLVED'
                          }">
                      {{ incident.status }}
                    </span>
                  </td>
                  <td class="py-4 px-4 text-xs text-gray-500">{{ incident.detectedAt | date:'mediumTime' }}</td>
                  <td class="py-4 px-4 text-right" (click)="$event.stopPropagation()">
                    <button *ngIf="incident.status === 'ACTIVE'"
                            (click)="resolveIncident(incident.id)"
                            class="rounded bg-emerald-600 hover:bg-emerald-700 text-white font-medium text-xs px-2.5 py-1.5 transition-all">
                      Acknowledge & Resolve
                    </button>
                    <span *ngIf="incident.status === 'RESOLVED'" class="text-xs text-emerald-500 font-semibold flex items-center justify-end gap-1">
                      ✓ Closed
                    </span>
                  </td>
                </tr>

                <!-- Expanded Dropdown for AI Root Cause Analysis -->
                <tr *ngIf="expandedIncidentId === incident.id" class="bg-gray-50/50 dark:bg-white/[0.01]">
                  <td colspan="7" class="p-6 border-b border-gray-100 dark:border-gray-800">
                    <div class="rounded-2xl border border-rose-200/50 dark:border-rose-950/30 bg-rose-500/[0.02] p-5">
                      <div class="flex items-center gap-2 mb-3">
                        <svg class="w-5 h-5 text-rose-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
                        </svg>
                        <h4 class="text-sm font-bold text-rose-600 dark:text-rose-400 uppercase tracking-wider">AI Root-Cause Suggestion</h4>
                      </div>
                      <pre class="text-sm text-gray-700 dark:text-gray-300 font-mono whitespace-pre-wrap leading-relaxed">{{ incident.aiSummary || 'Analyzing metric anomalies...' }}</pre>
                    </div>
                  </td>
                </tr>
              </ng-container>

              <tr *ngIf="incidents.length === 0">
                <td colspan="7" class="py-12 text-center text-gray-500">
                  <div class="flex flex-col items-center justify-center">
                    <svg class="w-12 h-12 text-emerald-500 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <p class="font-bold text-gray-800 dark:text-white/90">All scanned microservices are operating within SLA bounds.</p>
                    <p class="text-xs text-gray-500 mt-1">Deploy stress from the main Dashboard Stress Injector to test anomaly pipelines.</p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class IncidentConsoleComponent implements OnInit, OnDestroy {
  incidents: Incident[] = [];
  expandedIncidentId: number | null = null;
  private intervalId: any;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadIncidents();
    this.intervalId = setInterval(() => this.loadIncidents(), 3000);
  }

  ngOnDestroy() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  loadIncidents() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    this.http.get<Incident[]>('http://localhost:8080/api/incidents', { headers })
      .subscribe({
        next: (data) => this.incidents = data,
        error: (err) => console.error('Failed to query incidents log', err)
      });
  }

  resolveIncident(id: number) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    
    this.http.post(`http://localhost:8080/api/incidents/${id}/resolve`, {}, { headers })
      .subscribe({
        next: () => this.loadIncidents(),
        error: (err) => console.error('Failed to resolve incident manually', err)
      });
  }

  toggleExpand(id: number) {
    this.expandedIncidentId = this.expandedIncidentId === id ? null : id;
  }

  getActiveCount(): number {
    return this.incidents.filter(i => i.status === 'ACTIVE').length;
  }

  getResolvedCount(): number {
    return this.incidents.filter(i => i.status === 'RESOLVED').length;
  }
}
