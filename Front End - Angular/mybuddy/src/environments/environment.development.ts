export const environment = {
    production: false,
    apiUrl: 'https://localhost:8081/api/',
    envName: 'Development',
    keycloak: {
        url: 'http://localhost:8080',
        realm: 'mybuddy',
        clientId: 'mybuddy-frontend',
        silentCheckSsoRedirectUri: 'http://localhost:4200/silent-check-sso.html',
    },
};