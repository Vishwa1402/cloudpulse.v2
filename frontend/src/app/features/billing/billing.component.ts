import { Component } from '@angular/core';

@Component({
  selector: 'app-billing',
  standalone: true,
  template: `
    <div class="space-y-6">
      <div>
        <h2 class="text-title-md font-bold text-gray-800 dark:text-white/90">Multi-Cloud Billing</h2>
        <p class="mt-2 text-gray-500">Detailed billing breakdown by provider coming soon.</p>
      </div>
    </div>
  `
})
export class BillingComponent {}
