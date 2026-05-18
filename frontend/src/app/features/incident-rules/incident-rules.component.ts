import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

type IncidentRule = {
  id: number;
  metric: string;
  threshold: number;
  durationMin: number;
  status: string;
  description: string;
};

@Component({
  selector: 'app-incident-rules',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <div>
          <h2 class="text-title-md font-bold text-gray-800 dark:text-white/90">Incident Rule Engine</h2>
          <p class="text-sm text-gray-500">Configure alert thresholds and automated system response rules.</p>
        </div>
      </div>

      <!-- Create Incident Rule Form -->
      <div class="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03]">
        <h3 class="text-lg font-medium mb-4 text-gray-800 dark:text-white/90">Configure New Threshold Rule</h3>
        <form (ngSubmit)="createRule()" class="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
          <div class="w-full">
            <label class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">Target Metric</label>
            <select [(ngModel)]="newRule.metric" name="metric" class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 outline-none focus:border-brand-500 dark:border-gray-700 dark:bg-gray-900">
              <option value="CPU">CPU Utilization (%)</option>
              <option value="MEMORY">Memory Allocation (%)</option>
              <option value="ERROR_RATE">HTTP Error Rate (%)</option>
              <option value="LATENCY">API Response Latency (ms)</option>
            </select>
          </div>
          <div class="w-full">
            <label class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">Breach Threshold</label>
            <input type="number" [(ngModel)]="newRule.threshold" name="threshold" class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 outline-none focus:border-brand-500 dark:border-gray-700 dark:bg-gray-900" placeholder="e.g. 80">
          </div>
          <div class="w-full">
            <label class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">Evaluation Window</label>
            <select [(ngModel)]="newRule.durationMin" name="duration" class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 outline-none focus:border-brand-500 dark:border-gray-700 dark:bg-gray-900">
              <option value="1">1 minute</option>
              <option value="2">2 minutes</option>
              <option value="5">5 minutes</option>
              <option value="10">10 minutes</option>
            </select>
          </div>
          <button type="submit" class="flex items-center justify-center rounded-lg bg-rose-600 px-6 py-2.5 font-medium text-white hover:bg-rose-700 w-full h-11 transition-all">
            Deploy Rule
          </button>
        </form>
      </div>

      <!-- Rules Table -->
      <div class="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-white/[0.03]">
        <h3 class="text-lg font-medium mb-4 text-gray-800 dark:text-white/90">Active Monitoring Rules</h3>
        <div class="overflow-x-auto">
          <table class="w-full text-left">
            <thead>
              <tr class="border-b border-gray-200 dark:border-gray-800">
                <th class="py-3 px-4 font-medium text-gray-500">Metric Type</th>
                <th class="py-3 px-4 font-medium text-gray-500">Condition</th>
                <th class="py-3 px-4 font-medium text-gray-500">Duration</th>
                <th class="py-3 px-4 font-medium text-gray-500">Description</th>
                <th class="py-3 px-4 font-medium text-gray-500">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of rules" class="border-b border-gray-100 dark:border-gray-800/50 hover:bg-gray-50 dark:hover:bg-white/[0.01] transition-all cursor-pointer">
                <td class="py-4 px-4 font-semibold text-gray-800 dark:text-white/90">{{ r.metric }}</td>
                <td class="py-4 px-4 text-rose-500 font-medium">> {{ r.threshold }}{{ r.metric === 'LATENCY' ? ' ms' : '%' }}</td>
                <td class="py-4 px-4 text-gray-600 dark:text-gray-300">{{ r.durationMin }}m window</td>
                <td class="py-4 px-4 text-gray-500 dark:text-gray-400 text-sm">{{ r.description }}</td>
                <td class="py-4 px-4">
                  <span class="rounded-full bg-emerald-100 dark:bg-emerald-950/30 px-2.5 py-0.5 text-xs font-semibold text-emerald-700 dark:text-emerald-400">
                    {{ r.status }}
                  </span>
                </td>
              </tr>
              <tr *ngIf="rules.length === 0">
                <td colspan="5" class="py-4 text-center text-gray-500">No monitoring rules deployed yet.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class IncidentRulesComponent implements OnInit {
  rules: IncidentRule[] = [
    {
      id: 1,
      metric: 'CPU',
      threshold: 80,
      durationMin: 2,
      status: 'ACTIVE',
      description: 'Trigger CRITICAL incident when CPU utilization spikes past 80% on monitored microservices.'
    },
    {
      id: 2,
      metric: 'MEMORY',
      threshold: 90,
      durationMin: 5,
      status: 'ACTIVE',
      description: 'Trigger CRITICAL incident to prevent out-of-memory container crashes when allocation exceeds 90%.'
    },
    {
      id: 3,
      metric: 'ERROR_RATE',
      threshold: 1,
      durationMin: 1,
      status: 'ACTIVE',
      description: 'Alert ops team immediately if HTTP 5xx responses rise above 1% of total transaction volume.'
    }
  ];

  newRule = {
    metric: 'CPU',
    threshold: 80,
    durationMin: 2
  };

  ngOnInit() {
    this.loadRules();
  }

  loadRules() {
    const saved = localStorage.getItem('incident_rules');
    if (saved) {
      this.rules = JSON.parse(saved);
    } else {
      this.saveToStorage();
    }
  }

  createRule() {
    if (!this.newRule.threshold) return;

    let desc = '';
    if (this.newRule.metric === 'CPU') {
      desc = `Custom CPU guard threshold set at ${this.newRule.threshold}%.`;
    } else if (this.newRule.metric === 'MEMORY') {
      desc = `Trigger incident when server memory saturates past ${this.newRule.threshold}%.`;
    } else if (this.newRule.metric === 'ERROR_RATE') {
      desc = `Raise high alert when error rate breaches ${this.newRule.threshold}%.`;
    } else {
      desc = `Trigger latency warning when response latency exceeds ${this.newRule.threshold} ms.`;
    }

    const rule: IncidentRule = {
      id: Date.now(),
      metric: this.newRule.metric,
      threshold: this.newRule.threshold,
      durationMin: this.newRule.durationMin,
      status: 'ACTIVE',
      description: desc
    };

    this.rules.unshift(rule);
    this.saveToStorage();
    
    // reset
    this.newRule.threshold = 80;
  }

  private saveToStorage() {
    localStorage.setItem('incident_rules', JSON.stringify(this.rules));
  }
}
