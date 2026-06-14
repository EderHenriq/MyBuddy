import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withViewTransitions, withComponentInputBinding } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { providePrimeNG } from 'primeng/config';
import { MyBuddyPreset } from '../styles/mypreset';
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
import { cacheInterceptor } from './core/interceptors/cache.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { loadingInterceptor } from './core/interceptors/loading.interceptor';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { retryInterceptor } from './core/interceptors/retry.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding(), withViewTransitions()),
    provideClientHydration(withEventReplay()),
    provideHttpClient(withFetch(), withInterceptors([authInterceptor, cacheInterceptor, retryInterceptor, errorInterceptor, loadingInterceptor])),
    provideZoneChangeDetection({ eventCoalescing: true }),
    providePrimeNG({
      theme: {
        preset: MyBuddyPreset,
        options: {
          darkModeSelector: false,
        },
      },
    }),
  ],
};
