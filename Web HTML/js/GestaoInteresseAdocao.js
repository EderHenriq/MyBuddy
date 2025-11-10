document.addEventListener("DOMContentLoaded", async () => {
    // --- Elementos de UI específicos da página de Gestão de Interesses ---
    const interessesContainer = document.getElementById("interessesContainer");
    const noInteressesMessage = document.getElementById("noInteressesMessage");
    const refreshBtn = document.getElementById("refreshBtn");
    const petSelect = document.getElementById("petSelect");
    const userFilter = document.getElementById("userFilter");
    
    // --- Elementos de UI do Header (para controle de autenticação e navegação) ---
    const userNameDisplay = document.getElementById("userNameDisplay");
    const profileIcon = document.getElementById("profileIcon");
    const logoutBtnHeader = document.getElementById("logoutBtnHeader");
    const profileAreaLoggedIn = document.querySelector(".profile-area.logged-in");
    const authButtonsLoggedOut = document.querySelector(".auth-buttons.logged-out");

    // --- Elementos do Modal de Detalhes do Pet ---
    const petModal = document.getElementById("petModal");
    const closeModalBtn = document.getElementById("closeModal");
    const petDetailsDiv = document.getElementById("petDetails");

    // --- Dados de Autenticação do localStorage ---
    const jwtToken = localStorage.getItem("accessToken");
    const userNome = localStorage.getItem("userNome");
    const roles = JSON.parse(localStorage.getItem("userRoles") || "[]");
    const isAdmin = roles.includes("ROLE_ADMIN");
    const isOng = roles.includes("ROLE_ONG");
    const isAdotante = roles.includes("ROLE_ADOTANTE");
    const canModerate = isAdmin || isOng;
    const organizacaoId = localStorage.getItem("userOrganizacaoId");
    const userId = localStorage.getItem("userId");

    // --- Função de Notificação (mantida da sua implementação) ---
    function showNotification(mensagem, tipo = "info") {
        const box = document.createElement("div");
        box.className = `notification-box ${tipo}`;
        box.textContent = mensagem;
        document.body.appendChild(box);

        setTimeout(() => box.classList.add("show"), 50);
        setTimeout(() => {
            box.classList.remove("show");
            setTimeout(() => box.remove(), 300);
        }, 2500);
    }

    // --- Verificação de Autenticação e Redirecionamento ---
    if (!jwtToken) {
        showNotification("Você precisa estar logado para acessar esta página.", "error");
        setTimeout(() => {
            window.location.href = "login-screen.html";
        }, 1500);
        return;
    }

    // --- Lógica de Exibição do Header (Login/Logout) ---
    if (jwtToken) {
        profileAreaLoggedIn.style.display = "flex";
        authButtonsLoggedOut.style.display = "none";

        if (userNameDisplay && userNome) {
            userNameDisplay.textContent = `Olá, ${userNome}!`;
        }
    } else {
        // Isso não deve ocorrer se o jwtToken for nulo e o redirecionamento funcionar,
        // mas é bom ter uma fallback para o caso.
        profileAreaLoggedIn.style.display = "none";
        authButtonsLoggedOut.style.display = "flex";
    }

    // --- Manipulador de Evento para o botão de Logout no Header ---
    if (logoutBtnHeader) {
        logoutBtnHeader.addEventListener("click", () => {
            localStorage.clear();
            showNotification("Sessão encerrada com sucesso!", "info");
            setTimeout(() => {
                window.location.href = "login_screen.html";
            }, 1000);
        });
    }

    // --- Lógica de Navegação do Header ---
    const headerNavItems = document.querySelectorAll('.header-nav .nav-item');
    headerNavItems.forEach(item => {
        item.addEventListener('click', (event) => {
            event.preventDefault();

            headerNavItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');

            const pageLink = item.dataset.pageLink;
            let targetPage = '';

            switch (pageLink) {
                case 'home':
                    targetPage = 'home.html';
                    break;
                case 'adocao':
                    // Corrigido para GestaoPet.html
                    targetPage = 'GestaoPet.html'; 
                    break;
                case 'petshops':
                    targetPage = 'pet-shops.html';
                    break;
                case 'servicos':
                    targetPage = 'veterinarios-e-servicos.html';
                    break;
                case 'meus-interesses':
                    targetPage = 'GestaoInteresseAdoacao.html';
                    break;
                default:
                    console.warn('Navegação não configurada para:', pageLink);
                    return;
            }

            if (targetPage) {
                window.location.href = targetPage;
            }
        });
    });

    // --- Navegação do Ícone de Perfil ---
    if (profileIcon) {
        profileIcon.addEventListener('click', () => {
            let perfilPage = 'perfilAdotante.html';

            if (isAdmin) {
                perfilPage = 'perfilADM.html';
            } else if (isOng) {
                perfilPage = 'perfilONG.html';
            }
            window.location.href = perfilPage;
        });
    }

    // --- Funções Auxiliares (escapeHtml, statusLabels) ---
    const escapeHtml = (s) =>
        String(s ?? "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");

    const statusLabels = {
        PENDENTE: "Em Análise",
        APROVADO: "Aprovado",
        REJEITADO: "Rejeitado",
    };

    // --- Variáveis de dados e endpoints ---
    let todosPets = [];
    let todosInteresses = [];

    function getPetsEndpoint() {
        if (isOng && organizacaoId) {
            return `http://localhost:8080/api/pets/organizacao/${organizacaoId}`;
        }
        return "http://localhost:8080/api/pets"; 
    }

    function getInteressesEndpoint() {
        if (isAdmin) {
            return "http://localhost:8080/api/interesses"; 
        } else if (isOng && organizacaoId) {
            return `http://localhost:8080/api/ongs/me/interesses`;
        } else if (isAdotante && userId) {
            return "http://localhost:8080/api/interesses/usuarios/me/interesses";
        }
        return null;
    }

    // --- Funções de Fetch de Dados ---
    async function fetchPets() {
        try {
            const petEndpoint = getPetsEndpoint();
            if (!petEndpoint) {
                console.error("Nenhum endpoint de pets definido para a role atual.");
                return;
            }
            const resp = await fetch(petEndpoint, {
                headers: { Authorization: "Bearer " + jwtToken },
            });
            if (!resp.ok) {
                const errorData = await resp.json();
                throw new Error(errorData.message || "Falha ao carregar pets");
            }
            const dados = await resp.json();
            todosPets = Array.isArray(dados) ? dados : dados.content || [];
            renderPetSelect();
        } catch (error) {
            console.warn("API de pets offline ou inacessível, usando mock para filtro.", error);
            todosPets = [];
            renderPetSelect();
        }
    }

    async function fetchInteresses() {
        interessesContainer.innerHTML = "<p>Carregando...</p>";
        const endpointInteresses = getInteressesEndpoint();

        if (!endpointInteresses) {
            showNotification("Você não tem permissão para visualizar interesses ou dados de usuário ausentes.", "error");
            setTimeout(() => {
                window.location.href = "home.html";
            }, 1500);
            return;
        }

        try {
            const resp = await fetch(endpointInteresses, {
                headers: { Authorization: "Bearer " + jwtToken },
            });

            if (resp.status === 403) {
                throw new Error("Acesso negado. Você não tem permissão para ver estes interesses.");
            }
            if (!resp.ok) {
                const errorData = await resp.json();
                throw new Error(errorData.message || `Erro ao carregar interesses. Status: ${resp.status}`);
            }

            todosInteresses = await resp.json();
            aplicarFiltros();
        } catch (error) {
            console.error("Erro ao carregar interesses:", error.message);
            interessesContainer.innerHTML = `<p class="empty-message">Ocorreu um erro interno no servidor: ${error.message}</p>`;
            noInteressesMessage.style.display = "block";
        }
    }

    // --- Funções de Renderização e Filtros ---
    function renderPetSelect() {
        petSelect.innerHTML = '<option value="">Todos os Pets</option>';
        todosPets.forEach((pet) => {
            const option = document.createElement("option");
            option.value = pet.id;
            option.textContent = pet.nome;
            petSelect.appendChild(option);
        });
    }

    function aplicarFiltros() {
        let filtrados = [...todosInteresses];

        const petSelecionado = petSelect.value;
        if (petSelecionado) {
            filtrados = filtrados.filter((i) => i.pet?.id == petSelecionado);
        }

        const statusSelecionados = Array.from(
            document.querySelectorAll('input[name="status"]:checked')
        ).map((cb) => cb.value);

        if (statusSelecionados.length > 0) {
            filtrados = filtrados.filter((i) => statusSelecionados.includes(i.status));
        }

        const termo = userFilter.value.trim().toLowerCase();
        if (termo) {
            filtrados = filtrados.filter((i) =>
                (i.usuario?.nome || "").toLowerCase().includes(termo)
            );
        }

        renderInteresses(filtrados);
    }

    function renderInteresses(interesses) {
        interessesContainer.innerHTML = "";

        // Garante que o noInteressesMessage esteja escondido se houver interesses
        if (interesses.length) {
            noInteressesMessage.style.display = "none";
        } else {
            noInteressesMessage.style.display = "block";
        }

        // Esconde filtros de Pet e Usuário para Adotantes
        const petSelectLabel = document.querySelector('.filters-bar label[for="petSelect"]');
        const petSelectElement = document.getElementById("petSelect");
        const userFilterInput = document.getElementById("userFilter");

        if (isAdotante) {
            if (petSelectLabel) petSelectLabel.style.display = 'none';
            if (petSelectElement) petSelectElement.style.display = 'none';
            if (userFilterInput) userFilterInput.style.display = 'none';
        } else {
            if (petSelectLabel) petSelectLabel.style.display = '';
            if (petSelectElement) petSelectElement.style.display = '';
            if (userFilterInput) userFilterInput.style.display = '';
        }

        const header = document.createElement("div");
        header.className = "interesse-card";
        header.style.fontWeight = "700";
        header.style.background = "#f8f9fa";
        header.innerHTML = `
            <div>Usuário</div>
            <div>Pet</div>
            <div>Data</div>
            <div>Status</div>
            <div>Ações</div>
        `;
        interessesContainer.appendChild(header);

        interesses.forEach((i) => {
            const card = document.createElement("div");
            card.className = "interesse-card";
            card.dataset.id = i.id;
            card.dataset.status = i.status;

            const statusClass =
                {
                    APROVADO: "status-aprovado",
                    REJEITADO: "status-rejeitado",
                    PENDENTE: "status-pendente",
                }[i.status] || "status-pendente";

            const statusHtml = `<span class="status-badge status-cell ${statusClass}">${statusLabels[i.status]}</span>`;

            const actionsHtml =
                canModerate && i.status === "PENDENTE"
                    ? `
                        <button class="approve-btn">Aprovar</button>
                        <button class="reject-btn">Rejeitar</button>
                    `
                    : '<span class="status-badge status-cell">-</span>';

            card.innerHTML = `
                <div>${escapeHtml(i.usuario?.nome || "N/A")}</div>
                <div class="pet-name-clickable" data-pet-id="${i.pet?.id}">${escapeHtml(i.pet?.nome || "N/A")}</div>
                <div>${new Date(i.criadoEm).toLocaleDateString("pt-BR")}</div>
                <div class="status-cell">${statusHtml}</div>
                <div class="actions">${actionsHtml}</div>
            `;

            card.querySelector(".pet-name-clickable")?.addEventListener("click", (e) => {
                const petId = e.target.dataset.petId;
                if (petId) {
                    showPetDetailsModal(petId);
                }
            });

            if (canModerate && i.status === "PENDENTE") {
                card.querySelector(".approve-btn")?.addEventListener("click", () =>
                    atualizarStatus(i.id, "APROVADO", i.pet?.nome)
                );
                card.querySelector(".reject-btn")?.addEventListener("click", () =>
                    atualizarStatus(i.id, "REJEITADO", i.pet?.nome)
                );
            }

            interessesContainer.appendChild(card);
        });
    }

    // --- Modal de Detalhes do Pet ---
    async function showPetDetailsModal(petId) {
        try {
            const resp = await fetch(`http://localhost:8080/api/pets/${petId}`, {
                headers: { Authorization: "Bearer " + jwtToken },
            });
            if (!resp.ok) throw new Error("Falha ao carregar detalhes do pet.");
            const pet = await resp.json();

            petDetailsDiv.innerHTML = `
                <h3>${escapeHtml(pet.nome)}</h3>
                <p><strong>Raça:</strong> ${escapeHtml(pet.raca)}</p>
                <p><strong>Idade:</strong> ${escapeHtml(pet.idade)} ${escapeHtml(pet.unidadeIdade)}</p>
                <p><strong>Sexo:</strong> ${escapeHtml(pet.sexo)}</p>
                <p><strong>Status:</strong> ${escapeHtml(pet.statusAdocao)}</p>
                <p><strong>ONG/Tutor:</strong> ${escapeHtml(pet.organizacao?.nome || 'N/A')}</p>
                <p><strong>Descrição:</strong> ${escapeHtml(pet.descricao)}</p>
                ${pet.fotoUrl ? `<img src="http://localhost:8080/uploads/${pet.fotoUrl}" alt="${escapeHtml(pet.nome)}" style="max-width: 100%; height: auto; border-radius: 8px; margin-top: 15px;">` : ''}
            `;
            petModal.style.display = "flex";
        } catch (error) {
            console.error("Erro ao carregar detalhes do pet:", error.message);
            showNotification("Erro ao carregar detalhes do pet.", "error");
        }
    }

    closeModalBtn.addEventListener("click", () => {
        petModal.style.display = "none";
    });

    window.addEventListener("click", (event) => {
        if (event.target == petModal) {
            petModal.style.display = "none";
        }
    });

    // --- Função de Atualização de Status ---
    async function atualizarStatus(id, novoStatus, petNome) {
        const card = document.querySelector(`.interesse-card[data-id="${id}"]`);
        if (!card) return;

        const actionsCell = card.querySelector(".actions");
        const statusCell = card.querySelector(".status-cell");

        card.classList.add("changing");

        try {
            const resp = await fetch(
                `http://localhost:8080/api/interesses/${id}/status`,
                {
                    method: "PUT",
                    headers: {
                        Authorization: "Bearer " + jwtToken,
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ status: novoStatus }),
                }
            );

            if (!resp.ok) {
                const errorData = await resp.json();
                throw new Error(errorData.message || "Falha ao atualizar status");
            }

            const interesseIndex = todosInteresses.findIndex((int) => int.id === id);
            if (interesseIndex > -1) {
                todosInteresses[interesseIndex].status = novoStatus;
            }

            const badgeClass =
                novoStatus === "APROVADO" ? "status-aprovado" : "status-rejeitado";
            const badgeLabel = statusLabels[novoStatus];

            if (actionsCell)
                actionsCell.innerHTML = `<span class="status-badge ${badgeClass} animate-badge">${badgeLabel}</span>`;
            if (statusCell)
                statusCell.innerHTML = `<span class="status-badge ${badgeClass} animate-badge">${badgeLabel}</span>`;
            card.dataset.status = novoStatus;

            card.style.backgroundColor = novoStatus === "APROVADO" ? "#eafaf0" : "#fdeaea";
            setTimeout(() => {
                card.style.transition = "background-color 1s ease";
                card.style.backgroundColor = "#fff";
            }, 400);

            showNotification(
                `Interesse para o pet ${petNome} foi ${badgeLabel.toLowerCase()} com sucesso!`,
                "success"
            );
            aplicarFiltros();
        } catch (err) {
            console.error(err);
            showNotification("Erro ao atualizar status: " + err.message, "error");
        } finally {
            setTimeout(() => card.classList.remove("changing"), 1000);
        }
    }

    // --- Adição de Event Listeners para Filtros e Refresh ---
    petSelect.onchange = aplicarFiltros;
    document
        .querySelectorAll('input[name="status"]')
        .forEach((cb) => (cb.onchange = aplicarFiltros));
    userFilter.oninput = aplicarFiltros;
    refreshBtn.onclick = fetchInteresses;

    // --- Inicialização da Página ---
    await fetchPets();
    await fetchInteresses();

    // No momento em que a página é carregada, se o modal não deve estar visível,
    // garantimos que o display é 'none'. Isso resolve o "aviso em branco".
    petModal.style.display = "none";
});