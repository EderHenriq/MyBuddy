document.addEventListener("DOMContentLoaded", () => {
    const signInBtn = document.querySelector(".sign-in");
    const signUpBtn = document.querySelector(".sign-up");
    const glider = document.querySelector(".glider");
    const signInForm = document.querySelector(".sign-in-form");
    const signUpForm = document.querySelector(".sign-up-form");

    // Elementos do formulário de registro (para USUÁRIO)
    const formSignUp = document.getElementById('formSignUp');
    const signUpNome = document.getElementById('signUpNome');
    const signUpEmail = document.getElementById('signUpEmail');
    const signUpTelefoneUsuario = document.getElementById('signUpTelefoneUsuario'); // ALTERADO: telefone real do usuário
    const signUpPassword = document.getElementById('signUpPassword'); // NOVO: Senha do usuário
    const signUpRole = document.getElementById('signUpRole');
    const mensagemRegistro = document.getElementById('mensagemRegistro');

    // Elementos do formulário de registro (para ONG)
    const ongFieldsDiv = document.getElementById('ongFields'); // NOVO: Container dos campos da ONG
    const ongNomeFantasia = document.getElementById('ongNomeFantasia');
    const ongEmailContato = document.getElementById('ongEmailContato');
    const ongCnpj = document.getElementById('ongCnpj');
    const ongTelefoneContato = document.getElementById('ongTelefoneContato');
    const ongEndereco = document.getElementById('ongEndereco');
    const ongDescricao = document.getElementById('ongDescricao'); // Opcional
    const ongWebsite = document.getElementById('ongWebsite');     // Opcional


    // Elementos do formulário de login
    const formSignIn = document.getElementById('formSignIn');
    const signInEmail = document.getElementById('signInEmail');
    const signInPassword = document.getElementById('signInPassword'); // ALTERADO: Senha para login
    const mensagemLogin = document.getElementById('mensagemLogin');

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
        signInPassword.value = ''; // Limpa o campo de senha do login
    });

    // --- Lógica de Exibição de Campos da ONG ---
    signUpRole.addEventListener('change', () => {
        if (signUpRole.value === 'ong') {
            ongFieldsDiv.style.display = 'flex'; // Exibe os campos da ONG
            // Tornar campos obrigatórios para ONG (exceto descrição e website)
            ongNomeFantasia.setAttribute('required', 'true');
            ongEmailContato.setAttribute('required', 'true');
            ongCnpj.setAttribute('required', 'true');
            ongTelefoneContato.setAttribute('required', 'true');
            ongEndereco.setAttribute('required', 'true');
            // Descricao e Website são opcionais, não precisa setar required
        } else {
            ongFieldsDiv.style.display = 'none'; // Oculta os campos da ONG
            // Remover 'required' se não for ONG
            ongNomeFantasia.removeAttribute('required');
            ongEmailContato.removeAttribute('required');
            ongCnpj.removeAttribute('required');
            ongTelefoneContato.removeAttribute('required');
            ongEndereco.removeAttribute('required');
        }
    });

    // --- Lógica de Registro de Usuário (API) ---
    formSignUp.addEventListener('submit', async (event) => {
        event.preventDefault();

        limparMensagem(mensagemRegistro);

        const nome = signUpNome.value.trim();
        const email = signUpEmail.value.trim();
        const telefone = signUpTelefoneUsuario.value.trim(); // Telefone real do usuário
        const password = signUpPassword.value.trim();       // Senha do usuário
        const role = signUpRole.value;

        // Validação básica para campos comuns a ambos os tipos de usuário
        if (!nome || !email || !telefone || !password || !role) {
            exibirMensagem(mensagemRegistro, 'Por favor, preencha todos os campos obrigatórios e selecione seu tipo de usuário.', 'erro');
            return;
        }

        let payload = {
            nome,
            email,
            telefone,
            password,
            role: [role] // O backend espera um array de roles
        };

        // Adicionar campos da ONG se a role for 'ong'
        if (role === 'ong') {
            const nomeFantasia = ongNomeFantasia.value.trim();
            const emailContato = ongEmailContato.value.trim();
            const cnpj = ongCnpj.value.trim();
            const telefoneContato = ongTelefoneContato.value.trim();
            const endereco = ongEndereco.value.trim();
            const descricao = ongDescricao.value.trim(); // Opcional
            const website = ongWebsite.value.trim();     // Opcional

            // Validação para campos obrigatórios da ONG
            if (!nomeFantasia || !emailContato || !cnpj || !telefoneContato || !endereco) {
                exibirMensagem(mensagemRegistro, 'Por favor, preencha todos os campos obrigatórios da ONG.', 'erro');
                return;
            }
            
            // O payload do signup para ONG no backend não recebe os dados completos da ONG
            // ele espera apenas o `organizacaoId` ou `organizacaoCnpj`.
            // Para cadastrar uma ONG completa, o ideal é ter um endpoint separado para ONGs.
            // Por enquanto, vamos enviar o CNPJ para que o AuthController tente buscar/validar.
            payload.organizacaoCnpj = cnpj; 
            // Se você tivesse um endpoint para CRIAR ONG primeiro,
            // então após criar a ONG, você passaria o ID dela aqui.

            // Nota: Se a ideia é o usuário se registrar como ONG e *criar* a ONG,
            // a API precisaria de um payload mais complexo no /cadastro para
            // receber os dados da ONG ou um endpoint para criar a ONG separadamente.
            // Pelo seu AuthController, ele espera um ID ou CNPJ de uma ONG *existente*.
            // Por simplicidade, assumimos que o AuthController tentará encontrar/validar o CNPJ.
        }

        try {
            const response = await fetch(API_BASE_URL_REGISTRO, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json(); // Tenta parsear a resposta JSON

            if (response.ok) { // response.ok é true para status 2xx
                exibirMensagem(mensagemRegistro, `Usuário "${nome}" cadastrado com sucesso!`, 'sucesso');
                formSignUp.reset(); // Limpa o formulário
                // Opcional: Mudar para a tela de login após o registro
                // signInBtn.click();
            } else {
                exibirMensagem(mensagemRegistro, `Erro ao cadastrar: ${data.message || 'Erro desconhecido'}`, 'erro');
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
        const password = signInPassword.value.trim(); // ALTERADO: Pegar a senha

        if (!email || !password) { // ALTERADO: Validar email e password
            exibirMensagem(mensagemLogin, 'Por favor, preencha email e senha.', 'erro');
            return;
        }

        const loginData = { email, password: password }; // ALTERADO: Enviar password

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
                    window.location.href = './home.html';
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