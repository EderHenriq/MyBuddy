import { test, expect } from '@playwright/test';

test.describe('Fluxo de Checkout e Pagamento', () => {
  test.beforeEach(async ({ page }) => {
    page.on('console', msg => console.log(`[Browser Console] ${msg.type()}: ${msg.text()}`));
    page.on('pageerror', err => console.error(`[Browser PageError] ${err.message}`));

    await page.route('**/realms/mybuddy', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          realm: 'mybuddy',
          public_key: 'mock-public-key',
        }),
      });
    });

    await page.route('**/realms/mybuddy/.well-known/openid-configuration', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          issuer: 'http://localhost:8080/realms/mybuddy',
          authorization_endpoint: 'http://localhost:8080/realms/mybuddy/protocol/openid-connect/auth',
          token_endpoint: 'http://localhost:8080/realms/mybuddy/protocol/openid-connect/token',
          userinfo_endpoint: 'http://localhost:8080/realms/mybuddy/protocol/openid-connect/userinfo',
          end_session_endpoint: 'http://localhost:8080/realms/mybuddy/protocol/openid-connect/logout',
          jwks_uri: 'http://localhost:8080/realms/mybuddy/protocol/openid-connect/certs',
          grant_types_supported: ['password', 'authorization_code', 'refresh_token'],
        }),
      });
    });

    await page.route('https://sdk.mercadopago.com/js/v2', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/javascript',
        body: `
          class MercadoPago {
            constructor(publicKey, options) {
              this.publicKey = publicKey;
              this.options = options;
            }
            bricks() {
              return {
                create: async (type, targetId, settings) => {
                  const container = document.getElementById(targetId);
                  if (container) {
                    container.innerHTML = '<button id="mock-mp-wallet-button" style="padding: 12px; background: #009ee3; color: white; border: none; border-radius: 8px;">[Mock] Finalizar com Mercado Pago</button>';
                  }
                  return {};
                }
              };
            }
          }
          window.MercadoPago = MercadoPago;
        `,
      });
    });
  });

  test('deve renderizar o resumo do pedido corretamente com base nos query params', async ({ page }) => {
    await page.goto('/checkout/pagamento?petId=10&petNome=Bidu&amount=75');

    await expect(page.locator('h3')).toContainText('Bidu');
    await expect(page.locator('.resumo-valor')).toContainText('R$ 75.00');
    await expect(page.locator('.total-valor')).toContainText('R$ 75.00');
  });

  test('deve simular o pagamento com redirecionamento ao clicar no botao principal', async ({ page }) => {
    const mockInitPoint = 'https://www.mercadopago.com.br/sandbox/mock-checkout-redirection';

    await page.route('**/api/payments/create', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          id: 456,
          mpPreferenceId: 'pref-456',
          initPoint: mockInitPoint,
          amount: 75.00,
          status: 'PENDING',
        }),
      });
    });

    await page.route(mockInitPoint, async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'text/html',
        body: '<h1>Mock Mercado Pago Sandbox Redirect Screen</h1>',
      });
    });

    await page.goto('/checkout/pagamento?petId=10&petNome=Bidu&amount=75');

    await page.click('.btn-pagar');

    await expect(page).toHaveURL(mockInitPoint);
  });

  test('deve carregar o wallet brick do Mercado Pago ao escolher pagar no site', async ({ page }) => {
    await page.route('**/api/payments/create', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          id: 789,
          mpPreferenceId: 'pref-789',
          initPoint: 'https://sandbox.mercadopago.com',
          amount: 75.00,
          status: 'PENDING',
        }),
      });
    });

    await page.goto('/checkout/pagamento?petId=10&petNome=Bidu&amount=75');

    await page.click('.btn-brick');

    const mockMpButton = page.locator('#mock-mp-wallet-button');
    await expect(mockMpButton).toBeVisible();
    await expect(mockMpButton).toContainText('[Mock] Finalizar com Mercado Pago');
  });
});
