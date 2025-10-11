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
    // REMOVIDO: const signUpSenha = document.getElementById('signUpSenha'); // Este campo não existe mais no HTML
    const signUpTelefone = document.getElementById('signUpTelefone'); // Campo Telefone (que é a 'senha' para registro)
    const mensagemRegistro = document.getElementById('mensagemRegistro');

    // Elementos do formulário de login
    const formSignIn = document.getElementById('formSignIn');
    const signInEmail = document.getElementById('signInEmail');
    const signInTelefone = document.getElementById('signInTelefone'); // Campo Telefone (que é a 'senha' para login)
    const mensagemLogin = document.getElementById('mensagemLogin');

    // Constante para a URL base da sua API
    const API_BASE_URL_REGISTRO = 'http://localhost:8080/api/usuarios'; // Endpoint para registrar usuários
    // Você precisará de um endpoint de LOGIN separado no seu backend
    const API_BASE_URL_LOGIN = 'http://localhost:8080/api/auth/login'; // Exemplo de endpoint de login


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
        limparMensagem(mensagemRegistro); // Limpa mensagem de registro
        limparMensagem(mensagemLogin); // Limpa mensagem de login
    });

    signUpBtn.addEventListener("click", () => {
        glider.style.transform = "translateX(100%)";
        signUpBtn.classList.add("active");
        signInBtn.classList.remove("active");

        signUpForm.classList.add("active");
        signInForm.classList.remove("active");
        limparMensagem(mensagemRegistro); // Limpa mensagem de registro
        limparMensagem(mensagemLogin); // Limpa mensagem de login
        // Opcional: Limpar campos do login ao trocar para registro
        signInEmail.value = '';
        signInTelefone.value = '';
    });

    // --- Lógica de Registro de Usuário (API) ---
    formSignUp.addEventListener('submit', async (event) => {
        event.preventDefault();

        limparMensagem(mensagemRegistro);

        const nome = signUpNome.value.trim();
        const email = signUpEmail.value.trim();
        const telefone = signUpTelefone.value.trim(); // O telefone é agora o que a API vai receber como 'senha'

        // Validação básica no frontend
        if (!nome || !email || !telefone) { // Removida 'senha' da validação
            exibirMensagem(mensagemRegistro, 'Por favor, preencha todos os campos: Nome, Email e Telefone.', 'erro');
            return;
        }

        // Objeto com os dados a serem enviados para a API de REGISTRO
        // Assumimos que sua API no backend espera 'nome', 'email' e 'telefone' para criar o usuário
        const novoUsuario = { nome, email, telefone }; 

        try {
            const response = await fetch(API_BASE_URL_REGISTRO, { // Usa o endpoint de registro
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(novoUsuario)
            });

            if (response.status === 201) {
                const usuarioCriado = await response.json();
                exibirMensagem(mensagemRegistro, `Usuário "${usuarioCriado.nome}" cadastrado com sucesso!`, 'sucesso');
                formSignUp.reset();
                // Opcional: Mudar para a tela de login após o registro
                // signInBtn.click();

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
        const telefone = signInTelefone.value.trim(); // Pega o telefone do campo de login

        if (!email || !telefone) {
            exibirMensagem(mensagemLogin, 'Por favor, preencha email e telefone (senha).', 'erro');
            return;
        }

        // Objeto com os dados a serem enviados para a API de LOGIN
        // Sua API de login (ex: /api/auth/login) provavelmente espera 'email' e 'senha'
        // Neste caso, estamos usando 'telefone' como 'senha'.
        const loginData = { email, senha: telefone }; 

        try {
            // AQUI VOCÊ CONECTARIA COM O ENDPOINT DE LOGIN DA SUA API
            const response = await fetch(API_BASE_URL_LOGIN, { // Usa o endpoint de login
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(loginData)
            });

            if (response.ok) { // Status 200 OK indica sucesso no login
                const data = await response.json();
                // Sucesso no login, provavelmente você receberia um JWT aqui
                // localStorage.setItem('jwt_token', data.token); // Exemplo de como salvar um token

                exibirMensagem(mensagemLogin, 'Login realizado com sucesso! Redirecionando...', 'sucesso');
                // Redirecionar para a página principal
                setTimeout(() => {
                    // CUIDADO: Ajuste este caminho para o seu index.html real
                    // Ex: './index.html' se estiver na mesma pasta
                    // Ex: '../../index.html' se estiver duas pastas acima (como na estrutura que você usou com ../js/login.js)
                    window.location.href = '../../index.html'; 
                }, 1000); // Redireciona após 1 segundo
            } else {
                // Para outros erros (401 Unauthorized, 400 Bad Request, etc.)
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                exibirMensagem(mensagemLogin, `Erro no login: ${errorData.message || 'Credenciais inválidas'}`, 'erro');
            }
        } catch (error) {
            console.error('Erro de rede ou na API ao fazer login:', error);
            exibirMensagem(mensagemLogin, 'Erro de conexão com o servidor de login. Verifique se a API está online.', 'erro');
        }
    });
});