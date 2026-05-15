import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient,  withInterceptors } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { jwtInterceptor }
from './core/interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    importProvidersFrom(FormsModule),
    provideHttpClient(
    withInterceptors([jwtInterceptor])
)
  ]
};


