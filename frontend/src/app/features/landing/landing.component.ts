import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent {
  features = [
    {
      icon: '⚡',
      title: 'Real-Time Telemetry',
      description: 'Scrape, process, and ingest application metrics instantly with our active scraper pipelines.'
    },
    {
      icon: '🧠',
      title: 'AI Root-Cause Analysis',
      description: 'Ground AI models with real prometheus metrics to get deterministic diagnostic summaries.'
    },
    {
      icon: '🔔',
      title: 'Incident Command Center',
      description: 'Track full incident lifecycles with state machines, comments, assignments, and audit trails.'
    },
    {
      icon: '🛡️',
      title: 'Multi-Tenant Isolation',
      description: 'Fully isolated tenant workspaces ensuring high security and granular user role validation.'
    }
  ];

  testimonials = [
    {
      quote: "CloudPulse transformed our on-call operations, cutting incident MTTR by over 60%.",
      author: "Sarah Jenkins, VP of Site Reliability"
    },
    {
      quote: "The metrics-grounded AI suggestions are incredibly accurate. No more vague summaries.",
      author: "David Chen, Lead Infrastructure Architect"
    }
  ];
}
