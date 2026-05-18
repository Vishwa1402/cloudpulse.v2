import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-service-registry',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6 max-w-6xl mx-auto">
      <div>
        <h2 class="text-title-md font-bold text-gray-800 dark:text-white/90">Service & Project Registry</h2>
        <p class="text-sm text-gray-500">Configure tenant scopes and register active distributed microservices.</p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Register Project & Service Forms -->
        <div class="space-y-6 lg:col-span-1">
          <!-- Create Project -->
          <div class="rounded-2xl border border-gray-300 dark:border-gray-800 bg-white dark:bg-slate-900 p-6 shadow">
            <h3 class="text-lg font-bold text-slate-800 dark:text-slate-100 mb-4 flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-indigo-500"></span>
              Create Project
            </h3>
            <form (ngSubmit)="createProject()" class="space-y-4">
              <div>
                <label class="block text-xs font-semibold text-slate-400 mb-1.5 uppercase">Project Name</label>
                <input type="text" [(ngModel)]="newProjName" name="projName" required class="w-full rounded-lg border border-gray-300 dark:border-slate-800 bg-transparent dark:bg-[#0B0F19] text-slate-800 dark:text-slate-200 text-sm px-4 py-2.5 outline-none focus:border-indigo-500 transition-all">
              </div>
              <button type="submit" [disabled]="!newProjName" class="w-full rounded-lg bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold py-2.5 transition-all">
                Deploy Project Scope
              </button>
            </form>
          </div>

          <!-- Register Service -->
          <div class="rounded-2xl border border-gray-300 dark:border-gray-800 bg-white dark:bg-slate-900 p-6 shadow">
            <h3 class="text-lg font-bold text-slate-800 dark:text-slate-100 mb-4 flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>
              Register Service
            </h3>
            <form (ngSubmit)="createService()" class="space-y-4">
              <div>
                <label class="block text-xs font-semibold text-slate-400 mb-1.5 uppercase">Target Project</label>
                <select [(ngModel)]="selectedProjId" name="selectedProj" required class="w-full rounded-lg border border-gray-300 dark:border-slate-800 bg-white dark:bg-[#0B0F19] text-slate-800 dark:text-slate-200 text-sm px-4 py-2.5 outline-none focus:border-indigo-500 transition-all">
                  <option *ngFor="let p of projects" [value]="p.id">{{ p.name }}</option>
                </select>
              </div>
              <div>
                <label class="block text-xs font-semibold text-slate-400 mb-1.5 uppercase">Service Identifier</label>
                <input type="text" [(ngModel)]="newServName" name="servName" placeholder="e.g. billing-service" required class="w-full rounded-lg border border-gray-300 dark:border-slate-800 bg-transparent dark:bg-[#0B0F19] text-slate-800 dark:text-slate-200 text-sm px-4 py-2.5 outline-none focus:border-indigo-500 transition-all">
              </div>
              <button type="submit" [disabled]="!newServName || !selectedProjId" class="w-full rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-semibold py-2.5 transition-all">
                Register Microservice
              </button>
            </form>
          </div>
        </div>

        <!-- Inventory List Grid -->
        <div class="lg:col-span-2 space-y-6">
          <div class="rounded-2xl border border-gray-300 dark:border-gray-800 bg-white dark:bg-slate-900 p-6 shadow">
            <h3 class="text-lg font-bold text-slate-800 dark:text-slate-100 mb-4 flex items-center justify-between">
              <span>Active Topology Grid</span>
              <span class="text-xs px-2 py-0.5 rounded bg-indigo-950/40 text-indigo-400 border border-indigo-500/20">
                Multi-Tenant Scoped
              </span>
            </h3>

            <!-- Projects & Services Cards list -->
            <div *ngIf="projects.length === 0" class="text-center py-12 text-slate-400">
              No active scopes found. Register a project and service environment to begin.
            </div>

            <div class="space-y-4">
              <div *ngFor="let p of projects" class="border border-slate-200 dark:border-slate-800 rounded-xl p-4 bg-slate-50 dark:bg-[#0F172A]/50">
                <div class="flex justify-between items-center mb-3">
                  <h4 class="font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4.5 w-4.5 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
                    </svg>
                    {{ p.name }}
                  </h4>
                  <span class="text-xs text-slate-400">Project ID: #{{ p.id }}</span>
                </div>

                <!-- Sub-items: Registered Services -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-3 pl-6 border-l border-indigo-500/20">
                  <div *ngFor="let s of getServicesForProject(p.id)" class="bg-white dark:bg-[#0B0F19] border border-slate-200 dark:border-slate-800/80 rounded-lg p-3 flex justify-between items-center shadow-sm">
                    <div class="flex items-center gap-2">
                      <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
                      <div>
                        <p class="text-sm font-semibold text-slate-800 dark:text-slate-200">{{ s.name }}</p>
                        <p class="text-xs text-slate-400 uppercase">Environment: STAGING</p>
                      </div>
                    </div>
                    <span class="text-xs px-2 py-0.5 rounded bg-emerald-950/40 text-emerald-400 border border-emerald-500/10">Active</span>
                  </div>
                  <div *ngIf="getServicesForProject(p.id).length === 0" class="text-xs text-slate-400 py-2">
                    No services registered under this project scope.
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ServiceRegistryComponent implements OnInit {
  projects: any[] = [];
  services: any[] = [];

  newProjName = '';
  newServName = '';
  selectedProjId: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchInventory();
  }

  fetchInventory() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.http.get<any[]>('http://localhost:8080/api/projects', { headers }).subscribe({
      next: (projData) => {
        this.projects = projData;
        if (this.projects.length > 0) {
          this.selectedProjId = this.projects[0].id.toString();
        }
      },
      error: (e) => console.error(e)
    });

    this.http.get<any[]>('http://localhost:8080/api/services', { headers }).subscribe({
      next: (servData) => this.services = servData,
      error: (e) => console.error(e)
    });
  }

  getServicesForProject(projId: number): any[] {
    return this.services.filter(s => s.project && s.project.id === projId);
  }

  createProject() {
    if (!this.newProjName.trim()) return;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.http.post<any>('http://localhost:8080/api/projects', { name: this.newProjName }, { headers }).subscribe({
      next: () => {
        this.newProjName = '';
        this.fetchInventory();
      },
      error: (e) => console.error(e)
    });
  }

  createService() {
    if (!this.newServName.trim() || !this.selectedProjId) return;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.http.post<any>(`http://localhost:8080/api/services?projectId=${this.selectedProjId}`, { name: this.newServName }, { headers }).subscribe({
      next: () => {
        this.newServName = '';
        this.fetchInventory();
      },
      error: (e) => console.error(e)
    });
  }
}
