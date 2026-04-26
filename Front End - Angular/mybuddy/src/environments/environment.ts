export const environment = {
    production: true,
    apiUrl: '/api',
    envName: 'production',
    keycloak: {
        url: 'http://localhost:8080',
        realm: 'mybuddy',
        clientId: 'mybuddy-frontend',
        silentCheckSsoRedirectUri: 'http://localhost:4200/silent-check-sso.html',  
    },
};