document.addEventListener('DOMContentLoaded', () => {
    // --- Elementos do Header ---
    const userNameDisplay = document.getElementById('userNameDisplay');
    const logoutBtn = document.getElementById('logoutBtn');
    const profileIcon = document.getElementById('profileIcon');
    const profileArea = document.querySelector('.header-right .profile-area');

    // --- Cards de Ação Específicos ---
    const adminDashboardCard = document.getElementById('adminDashboardCard');
    const ongDashboardCard = document.getElementById('ongDashboardCard');
    const myPetsCard = document.getElementById('myPetsCard');
    const myInterestsCard = document.getElementById('myInterestsCard');

    // --- Variáveis de Usuário Globais para o Contexto da Página ---
    const accessToken = localStorage.getItem('accessToken');
    const storedUserName = localStorage.getItem('userName');
    const userRolesString = localStorage.getItem('userRoles');
    let userRoles = [];

    if (userRolesString) {
        try {
            userRoles = JSON.parse(userRolesString);
        } catch (e) {
            console.error("Erro ao parsear userRoles do localStorage:", e);
            userRoles = [];
        }
    }


    // --- Funções Auxiliares ---

    function navigateTo(url) {
        window.location.href = url;
    }

    function updateHeaderAuthDisplay() {
        if (!profileArea) {
            console.error("Elemento '.profile-area' não encontrado no HTML. Header de autenticação não pode ser atualizado.");
            return;
        }

        if (storedUserName && accessToken) {
            let displayUserName = storedUserName;
            if (storedUserName.includes('@')) {
                displayUserName = storedUserName.split('@')[0];
            }
            userNameDisplay.textContent = `Olá, ${displayUserName}!`;
            profileArea.style.display = 'flex';
        } else {
            profileArea.style.display = 'none';
        }
    }

    function updateCardVisibility() {
        if (adminDashboardCard) adminDashboardCard.style.display = 'none';
        if (ongDashboardCard) ongDashboardCard.style.display = 'none';
        if (myPetsCard) myPetsCard.style.display = 'none';
        if (myInterestsCard) myInterestsCard.style.display = 'none';

        if (userRoles.length > 0) {
            if (userRoles.includes('ROLE_ADMIN') && adminDashboardCard) {
                adminDashboardCard.style.display = 'flex';
            }
            if (userRoles.includes('ROLE_ONG') && ongDashboardCard) {
                ongDashboardCard.style.display = 'flex';
                if (myInterestsCard) myInterestsCard.style.display = 'flex';
            }
            if (userRoles.includes('ROLE_ADOTANTE') && myPetsCard) {
                myPetsCard.style.display = 'flex';
                if (myInterestsCard) myInterestsCard.style.display = 'flex';
            }
        }
    }

    // --- Inicialização ao Carregar a Página ---
    updateHeaderAuthDisplay();
    updateCardVisibility();

    // --- Event Listeners ---

    // Logout
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.clear();
            navigateTo('login_screen.html');
        });
    }

    // Ícone de Perfil
    if (profileIcon) {
        profileIcon.addEventListener('click', () => {
            if (accessToken) {
                if (userRoles.includes('ROLE_ADMIN')) {
                    navigateTo('./perfilADM.html');
                } else if (userRoles.includes('ROLE_ONG')) {
                    navigateTo('./perfilONG.html');
                } else { 
                    navigateTo('perfilAdotante.html');
                }
            } else {
                navigateTo('./login-screen.html');
            }
        });
    }

    // Navegação do Header
    document.querySelectorAll('.header-nav .nav-item').forEach(item => {
        item.addEventListener('click', (event) => {
            event.preventDefault();

            document.querySelectorAll('.header-nav .nav-item').forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');

            const pageLink = item.dataset.pageLink;
            switch (pageLink) {
                case 'home':
                    navigateTo('home.html');
                    break;
                case 'adocao':
                    navigateTo('pet-control.html');
                    break;
                case 'petshops':
                     navigateTo('pet-shops.html');
                    break;
                case 'servicos':
                    navigateTo('servicos.html');;
                    break;
                case 'meus-interesses':
                    navigateTo('GestaoInteresseAdoacao.html');
                    break;
                default:
                    console.warn('Navegação não configurada para:', pageLink);
                    break;
            }
        });
    });

    // Navegação dos Cards de Ação
    document.querySelectorAll('.action-card').forEach(card => {
        card.addEventListener('click', () => {
            const pageLink = card.dataset.pageLink;
            if (pageLink) {
                switch (pageLink) {
                    case 'adocao':
                        navigateTo('pet-control.html');
                        break;
                    case 'petshops':
                        navigateTo('pet-shops.html');
                        break;
                    case 'servicos':
                        navigateTo('servicos.html');
                        break;
                    case 'perdidos-achados':
                        alert('Página de Pets Perdidos & Achados em construção!');
                        break;
                    case 'meus-interesses':
                        navigateTo('GestaoInteresseAdoacao.html');
                        break;
                    case 'meus-pets':
                        navigateTo('pet-control.html');
                        break;
                    case 'admin-dashboard':
                        navigateTo('perfilADM.html');
                        break;
                    case 'ong-dashboard':
                        navigateTo('perfilONG.html');
                        break;
                    case 'como-funciona':
                        alert('Página "Como Funciona?" em construção!');
                        break;
                    default:
                        console.warn('Página de destino não definida para:', pageLink);
                        break;
                }
            }
        });
    });

    // --- Lógica para o primeiro carregamento para destacar o 'Home' no header ---
    const currentPath = window.location.pathname.split('/').pop();
    const homeNavLink = document.querySelector('.header-nav .nav-item[data-page-link="home"]');
    if (homeNavLink) {
        if (currentPath === 'home.html' || currentPath === '' || currentPath === 'index.html') {
            document.querySelectorAll('.header-nav .nav-item').forEach(nav => nav.classList.remove('active'));
            homeNavLink.classList.add('active');
        }
    }
});