import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6 max-w-6xl mx-auto font-mono">
      <!-- Title Header -->
      <div>
        <h2 class="text-title-md font-bold text-gray-800 dark:text-white/90 font-sans">Immutable Administrative Audit Log</h2>
        <p class="text-sm text-gray-500 font-sans">A tamper-proof ledger tracking developer actions, auth successes, and incident resolution bounds.</p>
      </div>

      <!-- Controls & Search -->
      <div class="rounded-xl border border-gray-300 dark:border-gray-800 bg-[#0F172A] p-4 flex flex-col md:flex-row gap-4 items-center justify-between shadow-2xl">
        <div class="flex items-center gap-2 w-full md:w-auto">
          <span class="w-2.5 h-2.5 rounded-full bg-rose-500 animate-pulse"></span>
          <span class="text-xs text-slate-400 font-sans">cloudpulse-audit-ledger v1.0.0</span>
        </div>
        <div class="flex items-center gap-3 w-full md:w-auto font-sans">
          <input type="text" [(ngModel)]="searchQuery" placeholder="Search logs (e.g. LOGIN, OPERATOR)..." class="w-full md:w-80 rounded-lg border border-slate-800 bg-[#0B0F19] text-slate-200 text-sm px-4 py-2 outline-none focus:border-indigo-500 transition-all placeholder:text-slate-600">
        </div>
      </div>

      <!-- Live Terminal Output -->
      <div class="rounded-2xl border border-gray-300 dark:border-gray-800 bg-[#070B14] shadow-2xl overflow-hidden flex flex-col h-[550px]">
        <!-- Terminal Header -->
        <div class="bg-[#0F172A] px-6 py-3 border-b border-slate-900 flex justify-between items-center font-sans">
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 rounded-full bg-rose-500"></span>
            <span class="w-3 h-3 rounded-full bg-amber-500"></span>
            <span class="w-3 h-3 rounded-full bg-emerald-500"></span>
            <span class="text-xs text-slate-400 ml-2">secure_audit_trail.log</span>
          </div>
          <span class="text-xs text-emerald-400 bg-emerald-950/40 px-2 py-0.5 rounded border border-emerald-500/20">LIVE RECORDING ACTIVE</span>
        </div>

        <!-- Terminal Logs Panel -->
        <div class="flex-1 overflow-y-auto p-6 space-y-3.5 scrollbar-thin scrollbar-thumb-slate-800 text-xs md:text-sm text-slate-300">
          <div *ngFor="let log of filteredLogs()" class="flex flex-col md:flex-row md:items-start gap-2 border-b border-slate-900/50 pb-2.5 hover:bg-slate-900/10 transition-colors">
            <span class="text-slate-500 select-none flex-shrink-0">[{{ formatTimestamp(log.createdAt) }}]</span>
            <span class="px-2 py-0.5 rounded font-bold uppercase select-none flex-shrink-0 text-center text-[10px] tracking-wider" 
                  [ngClass]="{
                    'bg-emerald-950/40 text-emerald-400 border border-emerald-500/20': log.action === 'LOGIN' || log.action === 'USER_REGISTER',
                    'bg-indigo-950/40 text-indigo-400 border border-indigo-500/20': log.action === 'SERVICE_REGISTER',
                    'bg-amber-950/40 text-amber-400 border border-amber-500/20': log.action === 'INCIDENT_RESOLVE',
                    'bg-rose-950/40 text-rose-400 border border-rose-500/20': log.action === 'RULE_CREATE'
                  }">
              {{ log.action }}
            </span>
            <span class="text-indigo-400 select-none font-semibold flex-shrink-0">@{{ log.performedBy }}</span>
            <span class="text-slate-300 break-all leading-relaxed">{{ log.details }}</span>
          </div>

          <div *ngIf="filteredLogs().length === 0" class="text-center text-slate-500 py-20 font-sans">
            No matching audit records found.
          </div>
        </div>
      </div>
    </div>
  `
})
export class AuditLogsComponent implements OnInit {
  logs: any[] = [];
  searchQuery = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchAuditLogs();
  }

  fetchAuditLogs() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.http.get<any[]>('http://localhost:8080/api/audit-logs', { headers }).subscribe({
      next: (data) => {
        // Sort descending by date
        this.logs = data.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
      },
      error: (e) => console.error(e)
    });
  }

  filteredLogs(): any[] {
    if (!this.searchQuery.trim()) {
      return this.logs;
    }
    const q = this.searchQuery.toLowerCase();
    return this.logs.filter(log => 
      log.action.toLowerCase().includes(q) || 
      log.performedBy.toLowerCase().includes(q) || 
      log.details.toLowerCase().includes(q)
    );
  }

  formatTimestamp(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toISOString().replace('T', ' ').substring(0, 19);
  }
}
