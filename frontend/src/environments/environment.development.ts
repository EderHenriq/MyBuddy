export const environment = {
  production: false,
  apiUrl: 'http://localhost:8081/api/',
  envName: 'Development',
  mercadoPagoPublicKey: 'APP_USR-112696f3-3603-4175-826f-167cf58606b2',
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'mybuddy',
    clientId: 'mybuddy-frontend',
    silentCheckSsoRedirectUri: 'http://localhost:4200/silent-check-sso.html',
  },
};
