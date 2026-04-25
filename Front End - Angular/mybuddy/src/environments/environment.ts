export const environment = {
    production: true,
    apiUrl: '/api',
    envName: 'production',
    keycloak: {
        url: 'http://localhost:8080',
        realm: 'mybuddy',
        clientId: 'mybuddy-frontend',
        silentCheckSsoRedirectUri: 'https://nossaurl.com/silent-check-sso.html',  
    },
};