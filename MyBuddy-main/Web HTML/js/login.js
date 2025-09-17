document.addEventListener("DOMContentLoaded", () => {
    const signInBtn = document.querySelector(".sign-in");
    const signUpBtn = document.querySelector(".sign-up");
    const glider = document.querySelector(".glider");
    const signInForm = document.querySelector(".sign-in-form");
    const signUpForm = document.querySelector(".sign-up-form");

    // Novos elementos para os formulários de registro
    const formSignUp = document.getElementById('formSignUp');
    const signUpNome = document.getElementById('signUpNome');
    const signUpEmail = document.getElementById('signUpEmail');
    const signUpSenha = document.getElementById('signUpSenha');
    const signUpTelefone = document.getElementById('signUpTelefone'); // Campo Telefone
    const mensagemRegistro = document.getElementById('mensagemRegistro'); // Elemento para exibir mensagens de registro

    // Constante para a URL base da sua API
    const API_BASE_URL = 'http://localhost:8080/api/usuarios'; // Ajuste conforme sua URL de usuários

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
        limparMensagem(mensagemRegistro); // Limpa mensagem de registro ao trocar
    });

    signUpBtn.addEventListener("click", () => {
        glider.style.transform = "translateX(100%)";
        signUpBtn.classList.add("active");
        signInBtn.classList.remove("active");

        signUpForm.classList.add("active");
        signInForm.classList.remove("active");
        // Opcional: Limpar campos do login ao trocar para registro
        // document.getElementById('signInEmail').value = '';
        // document.getElementById('signInSenha').value = '';
    });

    // --- Lógica de Registro de Usuário (API) ---
    formSignUp.addEventListener('submit', async (event) => {
        event.preventDefault(); // Impede o recarregamento da página ao submeter o formulário

        limparMensagem(mensagemRegistro); // Limpa mensagens anteriores

        const nome = signUpNome.value.trim();
        const email = signUpEmail.value.trim();
        const senha = signUpSenha.value; // Senha não precisa de trim (espaços são parte dela)
        const telefone = signUpTelefone.value.trim(); // Pega o valor do campo Telefone

        // Validação básica no frontend (você pode adicionar mais)
        if (!nome || !email || !senha || !telefone) {
            exibirMensagem(mensagemRegistro, 'Por favor, preencha todos os campos.', 'erro');
            return;
        }

        const novoUsuario = { nome, email, senha, telefone }; // Objeto com os dados a serem enviados

        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST', // Método HTTP para criar um novo recurso
                headers: {
                    'Content-Type': 'application/json' // Informa ao servidor que o corpo é JSON
                },
                body: JSON.stringify(novoUsuario) // Converte o objeto JS para uma string JSON
            });

            if (response.status === 201) { // Status 201 CREATED indica sucesso na criação
                const usuarioCriado = await response.json(); // Pega a resposta JSON do servidor
                exibirMensagem(mensagemRegistro, `Usuário "${usuarioCriado.nome}" cadastrado com sucesso!`, 'sucesso');
                formSignUp.reset(); // Limpa o formulário de registro
                // Opcional: Mudar para a tela de login após o registro
                // signInBtn.click();

            } else if (response.status === 409) { // Status 409 CONFLICT: e-mail já em uso
                exibirMensagem(mensagemRegistro, 'Erro: O e-mail informado já está em uso.', 'erro');
            } else {
                // Para outros erros, tenta pegar a mensagem do servidor ou exibe um genérico
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                exibirMensagem(mensagemRegistro, `Erro ao cadastrar: ${errorData.message || 'Erro desconhecido'}`, 'erro');
            }
        } catch (error) {
            console.error('Erro de rede ou na API:', error);
            exibirMensagem(mensagemRegistro, 'Erro de conexão com o servidor. Verifique se a API está online.', 'erro');
        }
    });

    // --- Lógica de Login de Usuário (API) - A SER IMPLEMENTADA FUTURAMENTE ---
    const formSignIn = document.getElementById('formSignIn');
    const mensagemLogin = document.getElementById('mensagemLogin');

    formSignIn.addEventListener('submit', async (event) => {
        event.preventDefault();
        limparMensagem(mensagemLogin);
        
        const email = document.getElementById('signInEmail').value.trim();
        const senha = document.getElementById('signInSenha').value;

        if (!email || !senha) {
            exibirMensagem(mensagemLogin, 'Por favor, preencha email e senha.', 'erro');
            return;
        }

        // --- AQUI VOCÊ CONECTARIA COM O ENDPOINT DE LOGIN DA SUA API ---
        // Exemplo (será diferente dependendo de como você implementar o Spring Security):
        // const loginData = { email, senha };
        // try {
        //     const response = await fetch('http://localhost:8080/api/auth/login', {
        //         method: 'POST',
        //         headers: { 'Content-Type': 'application/json' },
        //         body: JSON.stringify(loginData)
        //     });
        //     if (response.ok) {
        //         const data = await response.json();
        //         // Sucesso no login, provavelmente você receberia um JWT aqui
        //         localStorage.setItem('jwt_token', data.token);
        //         exibirMensagem(mensagemLogin, 'Login realizado com sucesso!', 'sucesso');
        //         // Redirecionar para a página principal
        //         window.location.href = '/dashboard.html';
        //     } else {
        //         const errorData = await response.json().catch(() => ({ message: response.statusText }));
        //         exibirMensagem(mensagemLogin, `Erro no login: ${errorData.message || 'Credenciais inválidas'}`, 'erro');
        //     }
        // } catch (error) {
        //     console.error('Erro de rede ou na API ao fazer login:', error);
        //     exibirMensagem(mensagemLogin, 'Erro de conexão com o servidor.', 'erro');
        // }
        
        exibirMensagem(mensagemLogin, 'Funcionalidade de login ainda não conectada à API.', 'info');
    });
});