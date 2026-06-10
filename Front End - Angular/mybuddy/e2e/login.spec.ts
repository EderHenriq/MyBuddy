import { test, expect } from '@playwright/test';

test.describe('Fluxo de Autenticação / Login', () => {
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
  });

  test('deve realizar login com sucesso e redirecionar para a home', async ({ page }) => {
    await page.route('**/protocol/openid-connect/token', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          access_token: 'mock-jwt-token-user',
          expires_in: 3600,
          refresh_token: 'mock-refresh-token',
        }),
      });
    });

    await page.route('**/api/usuarios/meu-perfil', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          id: 99,
          nome: 'Usuário Teste Adotante',
          email: 'user@mybuddy.com',
          roles: ['ROLE_USER'],
        }),
      });
    });

    const hydrationPromise = page.waitForEvent('console', msg => msg.text().includes('Angular hydrated'));
    await page.goto('/auth/login');
    await hydrationPromise;

    await page.fill('#email', 'user@mybuddy.com');
    await page.fill('#password', 'Senha123');

    await page.click('button[type="submit"]');

    await expect(page).toHaveURL(/\/home/);
  });

  test('deve exibir mensagem de erro ao falhar na autenticação', async ({ page }) => {
    await page.route('**/protocol/openid-connect/token', async (route) => {
      await route.fulfill({
        status: 401,
        contentType: 'application/json',
        body: JSON.stringify({
          error: 'invalid_grant',
          error_description: 'Invalid user credentials',
        }),
      });
    });

    const hydrationPromise = page.waitForEvent('console', msg => msg.text().includes('Angular hydrated'));
    await page.goto('/auth/login');
    await hydrationPromise;

    await page.fill('#email', 'invalid@mybuddy.com');
    await page.fill('#password', 'WrongPassword123');

    await page.click('button[type="submit"]');

    const alert = page.locator('.alert-error');
    await expect(alert).toBeVisible();
    await expect(alert).toContainText('E-mail ou senha incorretos');
  });
});
