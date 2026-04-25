import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter, withViewTransitions, withComponentInputBinding } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

import { providePrimeNG } from 'primeng/config';
import { MyBuddyPreset } from '../styles/mypreset';

import { provideHttpClient, withFetch } from '@angular/common/http';

import { provideKeycloak } from 'keycloak-angular';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideKeycloak({
      config: {
        url: environment.keycloak.url,
        realm: environment.keycloak.realm,
        clientId: environment.keycloak.clientId,
      },

      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
      },
    }),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding(), withViewTransitions()),
    provideClientHydration(withEventReplay()),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideHttpClient(withFetch()),
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
