document.addEventListener("DOMContentLoaded", () => {
    const signInBtn = document.querySelector(".sign-in");
    const signUpBtn = document.querySelector(".sign-up");
    const glider = document.querySelector(".glider");
    const signInForm = document.querySelector(".sign-in-form");
    const signUpForm = document.querySelector(".sign-up-form");

    const formSignUp = document.getElementById('formSignUp');
    const signUpRole = document.getElementById('signUpRole');
    const mensagemRegistro = document.getElementById('mensagemRegistro');

    const camposComunsDiv = document.getElementById('camposComuns');
    const camposOngDiv = document.getElementById('camposOng');

    const signUpNome = document.getElementById('signUpNome');
    const signUpEmail = document.getElementById('signUpEmail');
    const signUpTelefoneUsuario = document.getElementById('signUpTelefoneUsuario');
    const signUpPassword = document.getElementById('signUpPassword');

    const ongNomeFantasia = document.getElementById('ongNomeFantasia');
    const ongEmailContato = document.getElementById('ongEmailContato');
    const ongCnpj = document.getElementById('ongCnpj');
    const ongTelefoneContato = document.getElementById('ongTelefoneContato');
    const ongEndereco = document.getElementById('ongEndereco');
    const ongDescricao = document.getElementById('ongDescricao');
    const ongWebsite = document.getElementById('ongWebsite');

    const formSignIn = document.getElementById('formSignIn');
    const signInEmail = document.getElementById('signInEmail');
    const signInPassword = document.getElementById('signInPassword');
    const mensagemLogin = document.getElementById('mensagemLogin');

    const API_BASE_URL_REGISTRO = 'http://localhost:8080/api/auth/cadastro';
    const API_BASE_URL_LOGIN = 'http://localhost:8080/api/auth/login';

    function exibirMensagem(elemento, mensagem, tipo = 'info') {
        elemento.textContent = mensagem;
        elemento.className = `mensagem-area mensagem-${tipo}`;
    }

    function limparMensagem(elemento) {
        elemento.textContent = '';
        elemento.className = 'mensagem-area';
    }

    function resetarCamposDeRegistro() {
        signUpRole.value = '';
        camposComunsDiv.style.display = 'none';
        camposOngDiv.style.display = 'none';

        signUpNome.value = '';
        signUpNome.removeAttribute('required');
        signUpNome.setAttribute('disabled', 'true');

        signUpEmail.value = '';
        signUpEmail.removeAttribute('required');
        signUpEmail.setAttribute('disabled', 'true');

        signUpTelefoneUsuario.value = '';
        signUpTelefoneUsuario.removeAttribute('required');
        signUpTelefoneUsuario.setAttribute('disabled', 'true');

        signUpPassword.value = '';
        signUpPassword.removeAttribute('required');
        signUpPassword.setAttribute('disabled', 'true');

        ongNomeFantasia.value = '';
        ongNomeFantasia.removeAttribute('required');
        ongNomeFantasia.setAttribute('disabled', 'true');

        ongEmailContato.value = '';
        ongEmailContato.removeAttribute('required');
        ongEmailContato.setAttribute('disabled', 'true');

        ongCnpj.value = '';
        ongCnpj.removeAttribute('required');
        ongCnpj.setAttribute('disabled', 'true');

        ongTelefoneContato.value = '';
        ongTelefoneContato.removeAttribute('required');
        ongTelefoneContato.setAttribute('disabled', 'true');

        ongEndereco.value = '';
        ongEndereco.removeAttribute('required');
        ongEndereco.setAttribute('disabled', 'true');

        ongDescricao.value = '';
        ongDescricao.removeAttribute('required');
        ongDescricao.setAttribute('disabled', 'true');

        ongWebsite.value = '';
        ongWebsite.removeAttribute('required');
        ongWebsite.setAttribute('disabled', 'true');
    }

    function habilitarCamposComuns() {
        camposComunsDiv.style.display = 'flex';
        signUpNome.setAttribute('required', 'true');
        signUpNome.removeAttribute('disabled');
        signUpEmail.setAttribute('required', 'true');
        signUpEmail.removeAttribute('disabled');
        signUpTelefoneUsuario.setAttribute('required', 'true');
        signUpTelefoneUsuario.removeAttribute('disabled');
        signUpPassword.setAttribute('required', 'true');
        signUpPassword.removeAttribute('disabled');
    }

    function habilitarCamposOng() {
        camposOngDiv.style.display = 'flex';
        ongNomeFantasia.setAttribute('required', 'true');
        ongNomeFantasia.removeAttribute('disabled');
        ongEmailContato.setAttribute('required', 'true');
        ongEmailContato.removeAttribute('disabled');
        ongCnpj.setAttribute('required', 'true');
        ongCnpj.removeAttribute('disabled');
        ongTelefoneContato.setAttribute('required', 'true');
        ongTelefoneContato.removeAttribute('disabled');
        ongEndereco.setAttribute('required', 'true');
        ongEndereco.removeAttribute('disabled');
        ongDescricao.removeAttribute('disabled');
        ongWebsite.removeAttribute('disabled');
    }

    signInBtn.addEventListener("click", () => {
        glider.style.transform = "translateX(0)";
        signInBtn.classList.add("active");
        signUpBtn.classList.remove("active");

        signInForm.classList.add("active");
        signUpForm.classList.remove("active");
        limparMensagem(mensagemRegistro);
        limparMensagem(mensagemLogin);
        resetarCamposDeRegistro();
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
        signInPassword.value = '';

        resetarCamposDeRegistro();
    });

    signUpRole.addEventListener('change', () => {
        const selectedRole = signUpRole.value;
        limparMensagem(mensagemRegistro);

        resetarCamposDeRegistro();
        signUpRole.removeAttribute('disabled');
        signUpRole.value = selectedRole;

        if (selectedRole === 'adotante') {
            habilitarCamposComuns();
        } else if (selectedRole === 'ong') {
            habilitarCamposComuns();
            habilitarCamposOng();
        }
    });

    resetarCamposDeRegistro();

    formSignUp.addEventListener('submit', async (event) => {
        event.preventDefault();

        limparMensagem(mensagemRegistro);

        const nome = signUpNome.value.trim();
        const email = signUpEmail.value.trim();
        const telefone = signUpTelefoneUsuario.value.trim();
        const password = signUpPassword.value.trim();
        const role = signUpRole.value;

        if (!role) {
            exibirMensagem(mensagemRegistro, 'Por favor, selecione seu tipo de usuário.', 'erro');
            return;
        }

        if (camposComunsDiv.style.display === 'flex' && (!nome || !email || !telefone || !password)) {
            exibirMensagem(mensagemRegistro, 'Por favor, preencha todos os campos obrigatórios.', 'erro');
            return;
        }

        let payload = {
            nome,
            email,
            telefone,
            password,
            roles: [role.toUpperCase()]
        };

        if (role === 'ong') {
            const nomeFantasia = ongNomeFantasia.value.trim();
            const emailContato = ongEmailContato.value.trim();
            const cnpj = ongCnpj.value.trim();
            const telefoneContato = ongTelefoneContato.value.trim();
            const endereco = ongEndereco.value.trim();
            const descricao = ongDescricao.value.trim();
            const website = ongWebsite.value.trim();

            if (!nomeFantasia || !emailContato || !cnpj || !telefoneContato || !endereco) {
                exibirMensagem(mensagemRegistro, 'Por favor, preencha todos os campos obrigatórios da ONG.', 'erro');
                return;
            }

            payload.organizacaoCnpj = cnpj;
            payload.organizacaoNomeFantasia = nomeFantasia;
            payload.organizacaoEmailContato = emailContato;
            payload.organizacaoTelefoneContato = telefoneContato;
            payload.organizacaoEndereco = endereco;
            if (descricao) payload.organizacaoDescricao = descricao;
            if (website) payload.organizacaoWebsite = website;
        }

        try {
            const response = await fetch(API_BASE_URL_REGISTRO, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (response.ok) {
                exibirMensagem(mensagemRegistro, `Usuário "${nome}" cadastrado com sucesso!`, 'sucesso');
                formSignUp.reset();
                resetarCamposDeRegistro();
            } else {
                exibirMensagem(mensagemRegistro, `Erro ao cadastrar: ${data.message || 'Erro desconhecido'}`, 'erro');
            }
        } catch (error) {
            console.error('Erro de rede ou na API de registro:', error);
            exibirMensagem(mensagemRegistro, 'Erro de conexão com o servidor de registro. Verifique se a API está online.', 'erro');
        }
    });

    formSignIn.addEventListener('submit', async (event) => {
        event.preventDefault();
        limparMensagem(mensagemLogin);

        const email = signInEmail.value.trim();
        const password = signInPassword.value.trim();

        if (!email || !password) {
            exibirMensagem(mensagemLogin, 'Por favor, preencha email e senha.', 'erro');
            return;
        }

        const loginData = { email, password: password };

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
                localStorage.setItem('userName', data.username); // 'username' é o email, 'nome' é o nome real
                localStorage.setItem('userRoles', JSON.stringify(data.roles));
            
                if (data.organizacaoId) {
                    localStorage.setItem('userOrganizacaoId', data.organizacaoId);
                } else {
                    localStorage.removeItem('userOrganizacaoId');
                }

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