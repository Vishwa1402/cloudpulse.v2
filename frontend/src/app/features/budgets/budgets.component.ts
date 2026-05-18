import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-title-md font-bold text-gray-800 dark:text-white/90">Budgets & Alerts</h2>
      </div>

      <!-- Create Budget Form -->
      <div class="rounded-2xl border border-gray-200 bg-white p-5 dark:border-gray-800 dark:bg-white/[0.03]">
        <h3 class="text-lg font-medium mb-4">Set New Budget</h3>
        <form (ngSubmit)="createBudget()" class="flex flex-col md:flex-row gap-4 items-end">
          <div class="w-full">
            <label class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">Cloud Provider</label>
            <select [(ngModel)]="newBudget.provider" name="provider" class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 outline-none focus:border-brand-500 dark:border-gray-700">
              <option value="AWS">AWS</option>
              <option value="GCP">GCP</option>
              <option value="AZURE">Azure</option>
            </select>
          </div>
          <div class="w-full">
            <label class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">Monthly Limit ($)</label>
            <input type="number" [(ngModel)]="newBudget.monthlyAmount" name="amount" class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 outline-none focus:border-brand-500 dark:border-gray-700" placeholder="e.g. 5000">
          </div>
          <div class="w-full">
            <label class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">Alert Threshold (%)</label>
            <input type="number" [(ngModel)]="newBudget.alertThresholdPercentage" name="threshold" class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 outline-none focus:border-brand-500 dark:border-gray-700" placeholder="e.g. 80">
          </div>
          <button type="submit" class="flex items-center justify-center rounded-lg bg-brand-500 px-6 py-2.5 font-medium text-white hover:bg-brand-600 w-full md:w-auto h-11">
            Save Budget
          </button>
        </form>
      </div>

      <!-- Budgets Table -->
      <div class="rounded-2xl border border-gray-200 bg-white p-5 dark:border-gray-800 dark:bg-white/[0.03]">
        <table class="w-full text-left">
          <thead>
            <tr class="border-b border-gray-200 dark:border-gray-800">
              <th class="py-3 font-medium text-gray-500">Provider</th>
              <th class="py-3 font-medium text-gray-500">Monthly Budget</th>
              <th class="py-3 font-medium text-gray-500">Alert Threshold</th>
              <th class="py-3 font-medium text-gray-500">Status</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let b of budgets" class="border-b border-gray-100 dark:border-gray-800/50">
              <td class="py-3 font-medium">{{ b.provider }}</td>
              <td class="py-3">\${{ b.monthlyAmount | number }}</td>
              <td class="py-3">{{ b.alertThresholdPercentage }}%</td>
              <td class="py-3">
                <span class="rounded-full bg-success-50 px-2.5 py-0.5 text-sm font-medium text-success-600">Active</span>
              </td>
            </tr>
            <tr *ngIf="budgets.length === 0">
              <td colspan="4" class="py-4 text-center text-gray-500">No budgets defined yet.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class BudgetsComponent implements OnInit {
  budgets: any[] = [];
  newBudget = {
    provider: 'AWS',
    monthlyAmount: null,
    alertThresholdPercentage: 80
  };

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadBudgets();
  }

  loadBudgets() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get<any[]>('http://localhost:8080/api/finance/budgets', { headers })
      .subscribe({
        next: (data) => this.budgets = data,
        error: (err) => console.error('Failed to load budgets', err)
      });
  }

  createBudget() {
    if (!this.newBudget.monthlyAmount) return;
    
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.post('http://localhost:8080/api/finance/budgets', this.newBudget, { headers })
      .subscribe({
        next: (data) => {
          this.loadBudgets(); // reload
          this.newBudget.monthlyAmount = null; // reset
        },
        error: (err) => {
          alert('Failed to save budget. Are you an ADMIN or FINANCE user?');
          console.error(err);
        }
      });
  }
}
