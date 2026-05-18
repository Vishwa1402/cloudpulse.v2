import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6 max-w-6xl mx-auto">
      <!-- Header -->
      <div>
        <h2 class="text-title-md font-bold text-gray-800 dark:text-white/90">Notification Channels Management</h2>
        <p class="text-sm text-gray-500">Configure real-time integrations to dispatch critical microservice alerts directly to target platforms.</p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Add Integration Form -->
        <div class="space-y-6 lg:col-span-1">
          <div class="rounded-2xl border border-gray-300 dark:border-gray-800 bg-white dark:bg-slate-900 p-6 shadow">
            <h3 class="text-lg font-bold text-slate-800 dark:text-slate-100 mb-4 flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v3m0 0v3m0-3h3m-3 0H9m12 0a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              Deploy Alert Target
            </h3>
            <form (ngSubmit)="createChannel()" class="space-y-4">
              <div>
                <label class="block text-xs font-semibold text-slate-400 mb-1.5 uppercase">Channel Nickname</label>
                <input type="text" [(ngModel)]="newChanName" name="chanName" placeholder="e.g. Operations Slack Hook" required class="w-full rounded-lg border border-gray-300 dark:border-slate-800 bg-transparent dark:bg-[#0B0F19] text-slate-800 dark:text-slate-200 text-sm px-4 py-2.5 outline-none focus:border-indigo-500 transition-all">
              </div>
              <div>
                <label class="block text-xs font-semibold text-slate-400 mb-1.5 uppercase">Integration Type</label>
                <select [(ngModel)]="newChanType" name="chanType" class="w-full rounded-lg border border-gray-300 dark:border-slate-800 bg-white dark:bg-[#0B0F19] text-slate-800 dark:text-slate-200 text-sm px-4 py-2.5 outline-none focus:border-indigo-500 transition-all font-sans">
                  <option value="SLACK">SLACK WEBHOOK</option>
                  <option value="EMAIL">EMAIL RECIPIENT</option>
                </select>
              </div>
              <div>
                <label class="block text-xs font-semibold text-slate-400 mb-1.5 uppercase">Connection Endpoint / Address</label>
                <input type="text" [(ngModel)]="newChanConfig" name="chanConfig" placeholder="e.g. https://hooks.slack.com/... or admin@org.com" required class="w-full rounded-lg border border-gray-300 dark:border-slate-800 bg-transparent dark:bg-[#0B0F19] text-slate-800 dark:text-slate-200 text-sm px-4 py-2.5 outline-none focus:border-indigo-500 transition-all">
              </div>
              <button type="submit" [disabled]="!newChanName || !newChanConfig" class="w-full rounded-lg bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold py-2.5 transition-all">
                Connect Alert Target
              </button>
            </form>
          </div>
        </div>

        <!-- Integrated Targets Grid List -->
        <div class="lg:col-span-2 space-y-6">
          <div class="rounded-2xl border border-gray-300 dark:border-gray-800 bg-white dark:bg-slate-900 p-6 shadow">
            <h3 class="text-lg font-bold text-slate-800 dark:text-slate-100 mb-4 flex items-center justify-between">
              <span>Active Notifications Outposts</span>
              <span class="text-xs px-2 py-0.5 rounded bg-emerald-950/40 text-emerald-400 border border-emerald-500/20">Operational</span>
            </h3>

            <div *ngIf="channels.length === 0" class="text-center py-12 text-slate-400">
              No alert targets configured yet. Register a Slack webhook or Email recipient to receive automated system alerts.
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div *ngFor="let c of channels" class="border border-slate-200 dark:border-slate-800 rounded-xl p-4 bg-slate-50 dark:bg-[#0F172A]/50 flex flex-col justify-between">
                <div>
                  <div class="flex justify-between items-center mb-3">
                    <span class="text-xs px-2 py-0.5 rounded font-mono font-bold" 
                          [ngClass]="{
                            'bg-emerald-950/40 text-emerald-400 border border-emerald-500/20': c.type === 'EMAIL',
                            'bg-indigo-950/40 text-indigo-400 border border-indigo-500/20': c.type === 'SLACK'
                          }">
                      {{ c.type }}
                    </span>
                    <button (click)="deleteChannel(c.id)" class="text-slate-400 hover:text-rose-500 transition-colors">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4.5 w-4.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                  <h4 class="font-bold text-slate-800 dark:text-slate-100 text-sm mb-1">{{ c.name }}</h4>
                  <p class="text-xs text-slate-400 break-all font-mono">{{ c.config }}</p>
                </div>
                <div class="mt-4 pt-3 border-t border-slate-200 dark:border-slate-800 flex justify-between items-center text-xs">
                  <span class="text-slate-400">Created: {{ formatTimestamp(c.createdAt) }}</span>
                  <span class="text-emerald-400 flex items-center gap-1 font-sans">
                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse font-sans"></span> Connected
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class NotificationsComponent implements OnInit {
  channels: any[] = [];

  newChanName = '';
  newChanType = 'SLACK';
  newChanConfig = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchChannels();
  }

  fetchChannels() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.http.get<any[]>('http://localhost:8080/api/notifications/channels', { headers }).subscribe({
      next: (data) => this.channels = data,
      error: (e) => console.error(e)
    });
  }

  createChannel() {
    if (!this.newChanName.trim() || !this.newChanConfig.trim()) return;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    const payload = {
      name: this.newChanName,
      type: this.newChanType,
      config: this.newChanConfig
    };

    this.http.post<any>('http://localhost:8080/api/notifications/channels', payload, { headers }).subscribe({
      next: () => {
        this.newChanName = '';
        this.newChanConfig = '';
        this.fetchChannels();
      },
      error: (e) => console.error(e)
    });
  }

  deleteChannel(id: number) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.http.delete(`http://localhost:8080/api/notifications/channels/${id}`, { headers }).subscribe({
      next: () => this.fetchChannels(),
      error: (e) => console.error(e)
    });
  }

  formatTimestamp(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleDateString();
  }
}
