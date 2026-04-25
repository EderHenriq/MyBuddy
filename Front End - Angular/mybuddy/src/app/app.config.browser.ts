import { ApplicationConfig, mergeApplicationConfig } from '@angular/core';
import { environment } from '../environments/environment';
import { appConfig } from './app.config';   
import { provideKeycloak } from 'keycloak-angular';

const browserConfig: ApplicationConfig = {
    providers: [
        provideKeycloak({
            config:{
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