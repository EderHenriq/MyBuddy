document.addEventListener("DOMContentLoaded", () => {
    const signInBtn = document.querySelector(".sign-in");
    const signUpBtn = document.querySelector(".sign-up");
    const glider = document.querySelector(".glider");
    const signInForm = document.querySelector(".sign-in-form");
    const signUpForm = document.querySelector(".sign-up-form");

    // Elementos do formulário de registro
    const formSignUp = document.getElementById('formSignUp');
    const signUpNome = document.getElementById('signUpNome');
    const signUpEmail = document.getElementById('signUpEmail');
    const signUpTelefone = document.getElementById('signUpTelefone');
    const signUpRole = document.getElementById('signUpRole'); // <<--- NOVO: Pegar o elemento select da role
    const mensagemRegistro = document.getElementById('mensagemRegistro');

    // Elementos do formulário de login
    const formSignIn = document.getElementById('formSignIn');
    const signInEmail = document.getElementById('signInEmail');
    const signInTelefone = document.getElementById('signInTelefone');
    const mensagemLogin = document.getElementById('mensagemLogin');

    // Constante para a URL base da sua API
    const API_BASE_URL_REGISTRO = 'http://localhost:8080/api/auth/cadastro'; 
    const API_BASE_URL_LOGIN = 'http://localhost:8080/api/auth/login';

    // --- Funções Auxiliares para Mensagens ---
    function exibirMensagem(elemento, mensagem, tipo = 'info') {
        elemento.textContent = mensagem;
        elemento.className = `mensagem-area mensagem-${tipo}`;
    }

    function limparMensagem(elemento) {
        elemento.textContent = '';
        elemento.className = 'mensagem-area';
    }

    // --- Lógica de Alternância de Formulários ---
    signInBtn.addEventListener("click", () => {
        glider.style.transform = "translateX(0)";
        signInBtn.classList.add("active");
        signUpBtn.classList.remove("active");

        signInForm.classList.add("active");
        signUpForm.classList.remove("active");
        limparMensagem(mensagemRegistro);
        limparMensagem(mensagemLogin);
    });

    signUpBtn.addEventListener("click", () => {
        glider.style.transform = "translateX(100%)";
        signUpBtn.classList.add("active");
        signInBtn.classList.remove("active");

        signUpForm.classList.add("active");
        signInForm.classList.remove("active");
        limparMensagem(mensagemRegistro);
        limparMensagem(mensagemLogin);
        signInEmail.value = '';
        signInTelefone.value = '';
    });

    // --- Lógica de Registro de Usuário (API) ---
    formSignUp.addEventListener('submit', async (event) => {
        event.preventDefault();

        limparMensagem(mensagemRegistro);

        const nome = signUpNome.value.trim();
        const email = signUpEmail.value.trim();
        const telefone = signUpTelefone.value.trim();
        const role = signUpRole.value; // <<--- NOVO: Pegar o valor da role selecionada

        if (!nome || !email || !telefone || !role) { // <<--- NOVO: Validar se a role foi selecionada
            exibirMensagem(mensagemRegistro, 'Por favor, preencha todos os campos e selecione seu tipo de usuário.', 'erro');
            return;
        }

        // Objeto com os dados a serem enviados para a API de REGISTRO
        // <<--- NOVO: Incluir a role no objeto novoUsuario
        const novoUsuario = { nome, email, telefone, role: [role] }; // O backend espera um array de roles, ex: { "role": ["adotante"] }

        try {
            const response = await fetch(API_BASE_URL_REGISTRO, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(novoUsuario)
            });

            if (response.status === 200) { 
                const usuarioCriado = await response.json();
                exibirMensagem(mensagemRegistro, `Usuário "${novoUsuario.nome}" cadastrado com sucesso!`, 'sucesso');
                formSignUp.reset();
                // Opcional: Mudar para a tela de login após o registro
                // signInBtn.click();

            } else if (response.status === 400 && response.text().includes("Role is not found")) { // Tratamento específico para role não encontrada
                exibirMensagem(mensagemRegistro, 'Erro: Tipo de usuário inválido.', 'erro');
            } else if (response.status === 409) {
                exibirMensagem(mensagemRegistro, 'Erro: O e-mail informado já está em uso.', 'erro');
            } else {
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                exibirMensagem(mensagemRegistro, `Erro ao cadastrar: ${errorData.message || 'Erro desconhecido'}`, 'erro');
            }
        } catch (error) {
            console.error('Erro de rede ou na API de registro:', error);
            exibirMensagem(mensagemRegistro, 'Erro de conexão com o servidor de registro. Verifique se a API está online.', 'erro');
        }
    });

    // --- Lógica de Login de Usuário (API) ---
    formSignIn.addEventListener('submit', async (event) => {
        event.preventDefault();
        limparMensagem(mensagemLogin);
        
        const email = signInEmail.value.trim();
        const telefone = signInTelefone.value.trim();

        if (!email || !telefone) {
            exibirMensagem(mensagemLogin, 'Por favor, preencha email e telefone (senha).', 'erro');
            return;
        }

        const loginData = { email, telefone: telefone }; 

        try {
            const response = await fetch(API_BASE_URL_LOGIN, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(loginData)
            });

            if (response.ok) {
                const data = await response.json();
                
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('userId', data.id);
                localStorage.setItem('userEmail', data.email);
                localStorage.setItem('userName', data.username);
                localStorage.setItem('userRoles', JSON.stringify(data.roles));

                exibirMensagem(mensagemLogin, 'Login realizado com sucesso! Redirecionando...', 'sucesso');
                
                setTimeout(() => {
                    window.location.href = '../index.html'; 
                }, 1000); 
            } else {
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                exibirMensagem(mensagemLogin, `Erro no login: ${errorData.message || 'Credenciais inválidas'}`, 'erro');
            }
        } catch (error) {
            console.error('Erro de rede ou na API ao fazer login:', error);
            exibirMensagem(mensagemLogin, 'Erro de conexão com o servidor de login. Verifique se a API está online.', 'erro');
        }
    });
});