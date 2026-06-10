import { test, expect } from '@playwright/test';

test.describe('Fluxo de Checkout e Pagamento', () => {
  // Configurar o mock do SDK do Mercado Pago para todas as rotas de script do MP
  test.beforeEach(async ({ page }) => {
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
    // Acessar rota com parâmetros de teste
    await page.goto('/checkout/pagamento?petId=10&petNome=Bidu&amount=75');

    // Validar os elementos da interface
    await expect(page.locator('h3')).toContainText('Bidu');
    await expect(page.locator('.resumo-valor')).toContainText('R$ 75.00');
    await expect(page.locator('.total-valor')).toContainText('R$ 75.00');
  });

  test('deve simular o pagamento com redirecionamento ao clicar no botao principal', async ({ page }) => {
    const mockInitPoint = 'https://www.mercadopago.com.br/sandbox/mock-checkout-redirection';

    // Interceptar requisição de criação de pagamento do backend
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

    // Interceptar o próprio redirecionamento para o sandbox mockado
    await page.route(mockInitPoint, async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'text/html',
        body: '<h1>Mock Mercado Pago Sandbox Redirect Screen</h1>',
      });
    });

    await page.goto('/checkout/pagamento?petId=10&petNome=Bidu&amount=75');

    // Clicar em pagar com Mercado Pago
    await page.click('.btn-pagar');

    // Confirmar se redirecionou para o initPoint correspondente
    await expect(page).toHaveURL(mockInitPoint);
  });

  test('deve carregar o wallet brick do Mercado Pago ao escolher pagar no site', async ({ page }) => {
    // Interceptar a API de criação de preferência
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

    // Clicar em Pagar no site
    await page.click('.btn-brick');

    // Verificar se o elemento mockado do Mercado Pago foi renderizado dentro do container do brick
    const mockMpButton = page.locator('#mock-mp-wallet-button');
    await expect(mockMpButton).toBeVisible();
    await expect(mockMpButton).toContainText('[Mock] Finalizar com Mercado Pago');
  });
});
