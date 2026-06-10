import { test, expect } from '@playwright/test';

test.describe('Fluxo de Autenticação / Login', () => {
  test('deve realizar login com sucesso e redirecionar para a home', async ({ page }) => {
    // Interceptar a chamada de token do Keycloak
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

    // Interceptar a chamada de perfil do usuário
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

    // Acessar a tela de login
    await page.goto('/auth/login');

    // Preencher campos de e-mail e senha
    await page.fill('#email', 'user@mybuddy.com');
    await page.fill('#password', 'Senha123');

    // Submeter formulário
    await page.click('button[type="submit"]');

    // Verificar se redirecionou para /home
    await expect(page).toHaveURL(/\/home/);
  });

  test('deve exibir mensagem de erro ao falhar na autenticação', async ({ page }) => {
    // Interceptar com erro 401
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

    // Acessar a tela de login
    await page.goto('/auth/login');

    // Preencher campos incorretamente
    await page.fill('#email', 'invalid@mybuddy.com');
    await page.fill('#password', 'WrongPassword123');

    // Submeter formulário
    await page.click('button[type="submit"]');

    // Verificar a exibição da mensagem de alerta de erro
    const alert = page.locator('.alert-error');
    await expect(alert).toBeVisible();
    await expect(alert).toContainText('E-mail ou senha incorretos');
  });
});
