// common.js

document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logoutButton');
    const userNameOrEmailSpan = document.getElementById('userNameOrEmail');

    // Função para obter o token JWT do localStorage
    function getJwtToken() {
        return localStorage.getItem('accessToken'); // Ajustei para 'accessToken' conforme seu login
    }

    // Função para obter o nome de exibição do localStorage
    function getUserDisplayName() {
        // Tenta obter o nome que salvamos no login. Se não existir, volta para o email.
        return localStorage.getItem('userNameDisplay') || localStorage.getItem('userEmail') || 'Usuário MyBuddy';
    }

    // Exibir o nome/email no header
    if (userNameOrEmailSpan) {
        userNameOrEmailSpan.textContent = getUserDisplayName();
    }

    // Lógica do botão de Logout
    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            localStorage.clear(); // Limpa todo o localStorage ao sair
            window.location.href = 'login.html'; // Redireciona para a página de login
        });
    }

    // Exportar funções para que outros scripts possam usá-las
    window.getJwtToken = getJwtToken;
    window.getUserDisplayName = getUserDisplayName; // Nova função exportada
    window.getUserEmail = () => localStorage.getItem('userEmail'); // Mantém para compatibilidade, mas o foco é DisplayName
    window.getUserId = () => localStorage.getItem('userId'); // Exporta o userId
    window.getUserRoles = () => {
        try {
            return JSON.parse(localStorage.getItem('userRoles')) || [];
        } catch (e) {
            return [];
        }
    }; // Exporta as roles
    window.getUserOrganizacaoId = () => localStorage.getItem('userOrganizacaoId'); // Exporta o ID da organização
});