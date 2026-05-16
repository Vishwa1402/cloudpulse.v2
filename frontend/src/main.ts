import { bootstrapApplication } from '@angular/platform-browser';

(window as any).global = window;
import { appConfig } from './app/app.config';
import { App } from './app/app';

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
