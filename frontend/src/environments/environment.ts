export const environment = {
  production: true,
  apiUrl: '/api/',
  envName: 'production',
  mockApi: false,
  mercadoPagoPublicKey: 'APP_USR-112696f3-3603-4175-826f-167cf58606b2',
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'mybuddy',
    clientId: 'mybuddy-frontend',
    silentCheckSsoRedirectUri: 'http://localhost/silent-check-sso.html',
  },
};
