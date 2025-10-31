document.addEventListener('DOMContentLoaded', () => {
    // 1. Carregar e exibir o nome do usuário logado e botão de logout
    const userNameDisplay = document.getElementById('userNameDisplay');
    const logoutBtn = document.getElementById('logoutBtn');
    const storedUserName = localStorage.getItem('userName');
    const accessToken = localStorage.getItem('accessToken'); // Verifica se há token para exibir login

    if (storedUserName && accessToken) {
        userNameDisplay.textContent = `Olá, ${storedUserName}!`;
        logoutBtn.style.display = 'block'; // Mostra o botão de logout
    } else {
        // Se não há usuário logado, redireciona para a tela de login
        window.location.href = './login_screen.html'; // Ajuste o caminho conforme sua estrutura
        return; // Sai da função para não processar o resto do script da home
    }

    logoutBtn.addEventListener('click', () => {
        // Limpa o localStorage e redireciona para a página de login
        localStorage.clear();
        window.location.href = './login_screen.html'; // Ajuste o caminho
    });


    // 2. Controlar visibilidade dos cards específicos por role
    const userRolesRaw = localStorage.getItem('userRoles');
    let userRoles = [];

    if (userRolesRaw) {
        try {
            userRoles = JSON.parse(userRolesRaw);
        } catch (e) {
            console.error("Erro ao parsear roles do usuário:", e);
        }
    }

    const adminDashboardCard = document.getElementById('adminDashboardCard');
    const ongDashboardCard = document.getElementById('ongDashboardCard');
    const myPetsCard = document.getElementById('myPetsCard'); // Cartão "Meus Pets"

    if (userRoles.includes('ROLE_ADMIN')) {
        adminDashboardCard.style.display = 'flex'; // ADMIN vê o card de admin
        myPetsCard.style.display = 'none'; // ADMIN não precisa do "Meus Pets" padrão, talvez tenha algo mais global
    } else {
        adminDashboardCard.style.display = 'none';
    }

    if (userRoles.includes('ROLE_ONG')) {
        ongDashboardCard.style.display = 'flex'; // ONG vê o card de gestão da ONG
        myPetsCard.style.display = 'none'; // ONG não precisa do "Meus Pets" padrão, talvez gerencie animais da ONG
    } else {
        ongDashboardCard.style.display = 'none';
    }
    
    // Se não for ADMIN nem ONG, garante que "Meus Pets" esteja visível (para ADOTANTE)
    if (!userRoles.includes('ROLE_ADMIN') && !userRoles.includes('ROLE_ONG')) {
        myPetsCard.style.display = 'flex'; 
    } else if (userRoles.includes('ROLE_ADMIN') || userRoles.includes('ROLE_ONG')) {
        // Esconde o Meus Pets padrão se for ADMIN ou ONG, para dar lugar aos seus dashboards
        myPetsCard.style.display = 'none';
    }


    // 3. Lógica para cliques nos cards de ação
    document.querySelectorAll('.action-card').forEach(card => {
        card.addEventListener('click', (event) => {
            const page = card.dataset.page;
            // Aqui você define a lógica de navegação real
            switch (page) {
                case 'adocao':
                    alert('Navegando para a página de Adoção de Pets...');
                     window.location.href = './pet-control.html';
                    break;
                case 'petshops':
                    alert('Navegando para a página de PetShops Próximos...');
                    // window.location.href = './petshops.html';
                    break;
                case 'veterinarios':
                    alert('Navegando para a página de Veterinários & Hospitais...');
                    // window.location.href = './veterinarios.html';
                    break;
                case 'perdidos-achados':
                    alert('Navegando para a página de Pets Perdidos & Achados...');
                    // window.location.href = './perdidos-achados.html';
                    break;
                case 'meus-pets':
                    alert('Navegando para a página Meus Pets...');
                    // window.location.href = './meus-pets.html';
                    break;
                case 'admin-dashboard':
                    alert('Navegando para o Painel Administrativo...');
                    // window.location.href = './PerfilADM.html'; // Assumindo esta página
                    break;
                case 'ong-dashboard':
                    alert('Navegando para a Gestão da ONG...');
                    // window.location.href = './PerfilOng.html'; // Assumindo esta página
                    break;
                default:
                    alert(`Navegando para ${page}...`);
            }
        });
    });

    // 4. Lógica de navegação do cabeçalho
    const navItems = document.querySelectorAll('.header-nav .nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', (event) => {
            event.preventDefault(); 
            const page = item.dataset.navPage;
            
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');

            // Aqui você define a navegação para os itens do menu superior
            switch(page) {
                case 'home':
                    // alert('Você já está na Home!');
                    // window.location.href = './home.html'; // Já está na home
                    break;
                case 'meus-pets':
                    alert('Navegando para Meus Pets (menu)...');
                    // window.location.href = './meus-pets.html';
                    break;
                case 'favoritos':
                    alert('Navegando para Favoritos...');
                    // window.location.href = './favoritos.html';
                    break;
                case 'fale-conosco':
                    alert('Navegando para Fale Conosco...');
                    // window.location.href = './fale-conosco.html';
                    break;
                default:
                    alert(`Navegando para ${page}...`);
            }
        });
    });
});