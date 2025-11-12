// perfilAdotante.js

document.addEventListener('DOMContentLoaded', async () => {
    const profileNameDisplay = document.getElementById('profileNameDisplay');
    const profileLocation = document.getElementById('profileLocation');
    const memberSince = document.getElementById('memberSince');
    const profileEmail = document.getElementById('profileEmail');
    const profilePhone = document.getElementById('profilePhone');
    const profileAvatar = document.getElementById('profileAvatar');
    const numAdoptedPets = document.getElementById('numAdoptedPets');
    const adoptedPetsGrid = document.getElementById('adoptedPetsGrid');
    const noAdoptedPetsMessage = document.getElementById('noAdoptedPetsMessage');

    const editProfileButton = document.getElementById('editProfileButton');
    const editProfileModal = document.getElementById('editProfileModal');
    const closeEditProfileModal = document.getElementById('closeEditProfileModal');
    const editProfileForm = document.getElementById('editProfileForm');
    const editName = document.getElementById('editName');
    const editLocation = document.getElementById('editLocation');
    const editEmail = document.getElementById('editEmail');
    const editPhone = document.getElementById('editPhone');

    const avatarUpload = document.getElementById('avatarUpload');
    const uploadAvatarButton = document.getElementById('uploadAvatarButton');

    // URL base da sua API (ajuste conforme necessário)
    const API_BASE_URL = 'http://localhost:8080/api'; // Exemplo, ajuste para a sua URL real

    // --- Funções de Autenticação e Requisição ---
    function getJwtToken() {
        // Usar a função global do common.js
        return window.getJwtToken();
    }

    async function fetchUserProfile() {
        const token = getJwtToken();
        if (!token) {
            alert('Você não está logado. Redirecionando para o login.');
            window.location.href = 'login.html';
            return;
        }

        try {
            // A rota do backend pode ser '/api/usuarios/meu-perfil' ou algo similar
            // É importante que o backend use o token JWT para identificar o usuário logado
            const response = await fetch(`${API_BASE_URL}/usuarios/meu-perfil`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                if (response.status === 401) {
                    alert('Sessão expirada. Faça login novamente.');
                    localStorage.clear();
                    window.location.href = 'login.html';
                }
                throw new Error('Erro ao carregar perfil do usuário.');
            }

            const userData = await response.json();
            displayUserProfile(userData);
            fetchAdoptedPets(userData.id); // Busca os pets adotados após carregar o perfil
        } catch (error) {
            console.error('Erro ao buscar perfil do usuário:', error);
            alert('Não foi possível carregar as informações do seu perfil.');
        }
    }

    async function updateProfile(profileData) {
        const token = getJwtToken();
        if (!token) return; // Já tratado em fetchUserProfile

        try {
            // Rota para atualização do perfil. Pode ser PUT ou PATCH.
            // O backend deve usar o ID do usuário do token JWT para atualizar.
            const response = await fetch(`${API_BASE_URL}/usuarios/meu-perfil`, {
                method: 'PUT', // ou 'PATCH'
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(profileData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Erro ao atualizar perfil.');
            }

            const updatedUserData = await response.json();
            alert('Perfil atualizado com sucesso!');
            displayUserProfile(updatedUserData);
            editProfileModal.style.display = 'none'; // Fecha o modal
        } catch (error) {
            console.error('Erro ao atualizar perfil:', error);
            alert('Não foi possível atualizar seu perfil: ' + error.message);
        }
    }

    async function uploadAvatar(file) {
        const token = getJwtToken();
        if (!token) return;

        const formData = new FormData();
        formData.append('file', file); // 'file' deve corresponder ao nome esperado pelo seu backend

        try {
            // Rota para upload de avatar.
            const response = await fetch(`${API_BASE_URL}/usuarios/meu-perfil/avatar`, {
                method: 'POST', // ou 'PUT' dependendo da sua API
                headers: {
                    'Authorization': `Bearer ${token}`
                    // 'Content-Type' não é necessário com FormData, o navegador define.
                },
                body: formData
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Erro ao fazer upload da imagem.');
            }

            const result = await response.json();
            profileAvatar.src = result.avatarUrl || profileAvatar.src; // Atualiza a imagem com a nova URL
            alert('Imagem de perfil atualizada com sucesso!');
        } catch (error) {
            console.error('Erro ao fazer upload do avatar:', error);
            alert('Não foi possível fazer upload da imagem: ' + error.message);
        }
    }

    async function fetchAdoptedPets(userId) {
        const token = getJwtToken();
        if (!token) return;

        try {
            // Assumindo que você tem um endpoint para buscar pets adotados por um usuário
            const response = await fetch(`${API_BASE_URL}/adocoes/adotados-por/${userId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error('Erro ao carregar pets adotados.');
            }

            const adoptedPets = await response.json();
            displayAdoptedPets(adoptedPets);
            numAdoptedPets.textContent = adoptedPets.length;
        } catch (error) {
            console.error('Erro ao buscar pets adotados:', error);
            alert('Não foi possível carregar os pets adotados.');
            numAdoptedPets.textContent = '0'; // Garante que mostre 0 em caso de erro
            noAdoptedPetsMessage.style.display = 'block'; // Mostra a mensagem de nenhum pet
        }
    }

    // --- Funções de Display e Interação com a UI ---
    function displayUserProfile(userData) {
        profileNameDisplay.textContent = userData.nomeCompleto || 'Adotante MyBuddy';
        profileLocation.textContent = userData.endereco?.cidade || 'Localização não informada';
        profileEmail.textContent = userData.email || 'email@exemplo.com';
        profilePhone.textContent = userData.telefone || 'Telefone não informado';
        
        // Exibir a data de criação da conta (se disponível no backend como 'dataCriacao' ou 'membroDesde')
        if (userData.dataCriacao) {
            const date = new Date(userData.dataCriacao);
            memberSince.textContent = `${date.toLocaleString('pt-BR', { month: 'long', year: 'numeric' })}`;
        } else {
            memberSince.textContent = 'Data não informada';
        }

        // Atualizar avatar. 'avatarUrl' deve vir do backend
        profileAvatar.src = userData.avatarUrl || '../src/assets/imgs/default-avatar.png';

        // Preencher o modal de edição
        editName.value = userData.nomeCompleto || '';
        editLocation.value = userData.endereco?.cidade || ''; // Ajuste para a estrutura real do seu objeto de endereço
        editEmail.value = userData.email || '';
        editPhone.value = userData.telefone || '';

        // Atualizar nome no header (usando a função do common.js)
        const userNameHeader = document.getElementById('userNameOrEmail');
        if (userNameHeader) {
            userNameHeader.textContent = userData.nomeCompleto || userData.email || 'usuário@mybuddy.com';
            // Salvar no localStorage para uso em outras páginas se o nome completo estiver disponível
            if (userData.nomeCompleto) {
                localStorage.setItem('userEmail', userData.nomeCompleto); 
            }
        }
    }

    function createPetCard(pet) {
        // Esta função cria um card de pet adotado dinamicamente
        const card = document.createElement('article');
        card.classList.add('pet-card');
        card.innerHTML = `
            <div class="pet-image">
                <img src="${pet.imagemUrl || '/placeholder.svg'}" alt="Foto do ${pet.nome}">
            </div>
            <div class="pet-info">
                <div class="pet-header">
                    <div>
                        <h3 class="pet-name">${pet.nome}</h3>
                        <p class="pet-breed">${pet.raca || 'Raça desconhecida'}</p>
                    </div>
                    <span class="pet-type-badge">${pet.tipo || 'Pet'}</span>
                </div>
                <div class="pet-details">
                    <span class="pet-detail">
                        <i class="fas fa-birthday-cake"></i> ${pet.idade || '?'} anos
                    </span>
                    <span class="pet-detail">
                        <i class="fas fa-heart"></i> Adotado em ${new Date(pet.dataAdocao).toLocaleDateString('pt-BR') || 'Data desconhecida'}
                    </span>
                </div>
                <button class="btn-details">Ver Detalhes</button>
            </div>
        `;

        card.querySelector('.btn-details').addEventListener('click', () => {
            alert(`Visualizar detalhes de ${pet.nome} (ID: ${pet.id})`);
            // Implementar redirecionamento ou modal de detalhes do pet aqui
        });
        return card;
    }

    function displayAdoptedPets(pets) {
        adoptedPetsGrid.innerHTML = ''; // Limpa os cards existentes
        if (pets && pets.length > 0) {
            noAdoptedPetsMessage.style.display = 'none';
            pets.forEach(pet => {
                adoptedPetsGrid.appendChild(createPetCard(pet));
            });
            // Re-observar os novos cards para a animação
            document.querySelectorAll('.pet-card').forEach(card => {
                observer.observe(card);
            });
        } else {
            noAdoptedPetsMessage.style.display = 'block';
        }
    }

    // --- Event Listeners ---

    // Botão de Editar Perfil
    editProfileButton.addEventListener('click', () => {
        editProfileModal.style.display = 'flex'; // Exibe o modal
    });

    // Fechar Modal de Edição
    closeEditProfileModal.addEventListener('click', () => {
        editProfileModal.style.display = 'none';
    });

    window.addEventListener('click', (event) => {
        if (event.target == editProfileModal) {
            editProfileModal.style.display = 'none';
        }
    });

    // Submissão do formulário de edição
    editProfileForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const updatedData = {
            nomeCompleto: editName.value,
            endereco: { // Assumindo que endereço é um objeto no backend
                cidade: editLocation.value // Ajuste conforme a estrutura real
            },
            telefone: editPhone.value
            // Email geralmente não é editável ou requer um processo de verificação separado
        };
        await updateProfile(updatedData);
    });

    // Botão de Upload de Avatar
    uploadAvatarButton.addEventListener('click', () => {
        avatarUpload.click(); // Dispara o clique no input file oculto
    });

    avatarUpload.addEventListener('change', async (event) => {
        const file = event.target.files[0];
        if (file) {
            await uploadAvatar(file);
        }
    });

    // --- Animações (mantido e adaptado) ---
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '0';
                entry.target.style.transform = 'translateY(20px)';

                setTimeout(() => {
                    entry.target.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }, 100);

                // Apenas remova o observer se você quiser que a animação aconteça uma única vez
                // Se o card puder aparecer/desaparecer (ex: filtros), mantenha o observer
                // observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Stats animation on load (adaptado para o único stat "Adotados")
    const statNumber = document.getElementById('numAdoptedPets'); // Agora é apenas um
    const animateStat = (element) => {
        const finalValue = parseInt(element.textContent);
        let currentValue = 0;
        const increment = finalValue / 30; // 30 passos para a animação

        const counter = setInterval(() => {
            currentValue += increment;
            if (currentValue >= finalValue) {
                element.textContent = finalValue;
                clearInterval(counter);
            } else {
                element.textContent = Math.floor(currentValue);
            }
        }, 30);
    };


    // --- Inicialização ---
    // Inicia o carregamento do perfil quando a página é carregada
    await fetchUserProfile();

    // Inicia a animação do stat depois que o valor for carregado
    // Este `setTimeout` é uma gambiarra para garantir que o `fetchUserProfile` termine antes
    // O ideal seria chamar `animateStat` dentro de `fetchUserProfile` ou `displayUserProfile`
    setTimeout(() => {
        if (statNumber) {
            animateStat(statNumber);
        }
    }, 500); // Pequeno atraso para dar tempo ao fetch

});