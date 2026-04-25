import { ApplicationConfig, mergeApplicationConfig } from '@angular/core';
import { environment } from '../environments/environment';
import { appConfig } from './app.config';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideKeycloak, INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG } from 'keycloak-angular';

const browserConfig: ApplicationConfig = {
    providers: [
        provideHttpClient(withFetch(), withInterceptors([authInterceptor])),
        {
            provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
            useValue: [
                {
                    urlPattern: /^(http:\/\/localhost:8081)(\/.*)?$/,
                    bearerPrefix: "Bearer "
                },
            ]
        },
        provideKeycloak({
            config: {
                url: environment.keycloak.url,
                realm: environment.keycloak.realm,
                clientId: environment.keycloak.clientId,
            },
            initOptions: {
                onLoad: 'check-sso',
                silentCheckSsoRedirectUri: environment.keycloak.silentCheckSsoRedirectUri,
            },
        }),
    ],
};

export const config = mergeApplicationConfig(appConfig, browserConfig);