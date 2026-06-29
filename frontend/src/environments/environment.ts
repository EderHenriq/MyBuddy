export const environment = {
  production: true,
  apiUrl: '/api/',
  envName: 'production',
  mercadoPagoPublicKey: '__MP_PUBLIC_KEY__',
  keycloak: {
    url: '__KEYCLOAK_URL__',
    realm: 'mybuddy',
    clientId: 'mybuddy-frontend',
    silentCheckSsoRedirectUri: '__PUBLIC_URL__/silent-check-sso.html',
  },
};
