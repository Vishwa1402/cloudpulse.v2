import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

type Message = {
  sender: 'user' | 'ai';
  text: string;
  timestamp: Date;
};

@Component({
  selector: 'app-ai-assistant',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6 max-w-4xl mx-auto">
      <!-- Welcome Header -->
      <div>
        <h2 class="text-title-md font-bold text-gray-800 dark:text-white/90">AI Telemetry Command Assistant</h2>
        <p class="text-sm text-gray-500">Query live prometheus indices, request analytics, and request automated troubleshooting analysis.</p>
      </div>

      <!-- Main Chat Terminal -->
      <div class="rounded-2xl border border-gray-300 dark:border-gray-800 bg-[#0F172A] p-6 shadow-2xl flex flex-col h-[550px]">
        <!-- Terminal Header -->
        <div class="flex items-center justify-between border-b border-slate-800 pb-4 mb-4">
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 rounded-full bg-rose-500"></span>
            <span class="w-3 h-3 rounded-full bg-amber-500"></span>
            <span class="w-3 h-3 rounded-full bg-emerald-500"></span>
            <span class="text-xs font-mono text-slate-400 ml-2">nexusops-ai-terminal v1.0.0</span>
          </div>
          <span class="text-xs font-semibold px-2 py-0.5 rounded bg-emerald-950/40 text-emerald-400 border border-emerald-500/20">Active Grounding Mode</span>
        </div>

        <!-- Messaging Frame -->
        <div class="flex-1 overflow-y-auto space-y-4 pr-2 font-mono scrollbar-thin scrollbar-thumb-slate-800">
          <!-- Initial AI Hello -->
          <div class="flex gap-3">
            <div class="flex-shrink-0 w-8 h-8 rounded-full bg-indigo-600 flex items-center justify-center text-xs font-bold text-white">AI</div>
            <div class="rounded-2xl bg-slate-900 border border-slate-800 p-4 max-w-[85%] text-slate-300">
              <p class="text-emerald-400 font-semibold mb-1">✓ NexusOps Autonomous Assistant ready.</p>
              <p class="text-sm">I have access to live Prometheus vector data. What performance metrics or anomalous node logs would you like me to analyze?</p>
            </div>
          </div>

          <!-- Dynamic Chat Stream -->
          <div *ngFor="let msg of messages" class="flex gap-3" [ngClass]="{'justify-end': msg.sender === 'user'}">
            <!-- User Message Bubble -->
            <div *ngIf="msg.sender === 'user'" class="rounded-2xl bg-indigo-600 px-4 py-3 max-w-[80%] text-white text-sm">
              {{ msg.text }}
            </div>

            <!-- AI Message Bubble -->
            <ng-container *ngIf="msg.sender === 'ai'">
              <div class="flex-shrink-0 w-8 h-8 rounded-full bg-indigo-600 flex items-center justify-center text-xs font-bold text-white">AI</div>
              <div class="rounded-2xl bg-slate-900 border border-slate-800 p-4 max-w-[85%] text-slate-300">
                <pre class="text-sm font-mono whitespace-pre-wrap leading-relaxed text-slate-200">{{ msg.text }}</pre>
              </div>
            </ng-container>
          </div>

          <!-- Live Thinking status -->
          <div *ngIf="thinking" class="flex gap-3 items-center">
            <div class="flex-shrink-0 w-8 h-8 rounded-full bg-indigo-600 flex items-center justify-center text-xs font-bold text-white">AI</div>
            <div class="rounded-2xl bg-slate-900 border border-slate-800 px-4 py-3 text-sm text-slate-400 flex items-center gap-2 font-mono">
              <span class="animate-pulse">Analyzing real-time Prometheus vectors</span>
              <span class="flex gap-1">
                <span class="w-1.5 h-1.5 bg-slate-400 rounded-full animate-bounce" style="animation-delay: 0ms"></span>
                <span class="w-1.5 h-1.5 bg-slate-400 rounded-full animate-bounce" style="animation-delay: 150ms"></span>
                <span class="w-1.5 h-1.5 bg-slate-400 rounded-full animate-bounce" style="animation-delay: 300ms"></span>
              </span>
            </div>
          </div>
        </div>

        <!-- Dynamic Action Chips -->
        <div class="flex flex-wrap gap-2 mt-4 pt-4 border-t border-slate-800">
          <button *ngFor="let p of prompts" (click)="selectPrompt(p)" class="text-xs font-mono rounded bg-slate-900 hover:bg-slate-800 border border-slate-800 hover:border-indigo-500/50 px-3 py-1.5 text-slate-400 hover:text-white transition-all">
            $ {{ p }}
          </button>
        </div>

        <!-- Chat Input Form -->
        <form (ngSubmit)="sendMessage()" class="flex items-center gap-3 mt-3 pt-3 border-t border-slate-800">
          <input type="text" [(ngModel)]="currentQuery" name="query" [disabled]="thinking" class="flex-1 rounded-lg border border-slate-800 bg-[#0B0F19] text-slate-200 font-mono text-sm px-4 py-3 outline-none focus:border-indigo-500 placeholder:text-slate-600 transition-all" placeholder="Ask AI: e.g. Why did CPU spike?">
          <button type="submit" [disabled]="thinking || !currentQuery.trim()" class="rounded-lg bg-indigo-600 hover:bg-indigo-700 disabled:bg-slate-800 disabled:text-slate-600 text-white font-mono px-5 py-3 text-sm font-semibold transition-all">
            EXEC
          </button>
        </form>
      </div>
    </div>
  `
})
export class AiAssistantComponent {
  messages: Message[] = [];
  currentQuery = '';
  thinking = false;

  prompts = [
    'What is the overall system health?',
    'Why did CPU spike?',
    'Show live memory usage',
    'Explain current HTTP error rate'
  ];

  constructor(private http: HttpClient) {}

  selectPrompt(prompt: string) {
    this.currentQuery = prompt;
    this.sendMessage();
  }

  sendMessage() {
    if (!this.currentQuery.trim() || this.thinking) return;

    const userText = this.currentQuery;
    this.messages.push({
      sender: 'user',
      text: userText,
      timestamp: new Date()
    });

    this.currentQuery = '';
    this.thinking = true;

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.http.post<any>('http://localhost:8080/api/ai/chat', { query: userText }, { headers })
      .subscribe({
        next: (res) => {
          this.messages.push({
            sender: 'ai',
            text: res.reply,
            timestamp: new Date()
          });
          this.thinking = false;
        },
        error: (err) => {
          this.messages.push({
            sender: 'ai',
            text: '[ERROR]\n- Downstream connection to NexusOps telemetry daemon failed.\n- Verify that the Spring Boot backend server is hosted on port 8080.',
            timestamp: new Date()
          });
          this.thinking = false;
          console.error(err);
        }
      });
  }
}
