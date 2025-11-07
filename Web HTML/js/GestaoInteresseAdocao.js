document.addEventListener("DOMContentLoaded", async () => {
  const interessesContainer = document.getElementById("interessesContainer");
  const noInteressesMessage = document.getElementById("noInteressesMessage");
  const refreshBtn = document.getElementById("refreshBtn");
  const petSelect = document.getElementById("petSelect");
  const userFilter = document.getElementById("userFilter");
  const userInfo = document.getElementById("userInfo");

  const jwtToken = localStorage.getItem("accessToken");
  const userEmail = localStorage.getItem("userEmail");
  const roles = JSON.parse(localStorage.getItem("userRoles") || "[]");
  const isAdmin = roles.includes("ROLE_ADMIN");
  const isOng = roles.includes("ROLE_ONG");
  const isAdotante = roles.includes("ROLE_ADOTANTE");
  const canModerate = isAdmin || isOng;

  // Obter o ID da organização para ONGs
  const organizationId = localStorage.getItem("organizationId");

  if (!jwtToken) {
    alert("Você precisa estar logado para acessar!");
    window.location.href = "login.html";
    return;
  }

  userInfo.textContent = userEmail
    ? `Conectado como: ${userEmail}`
    : "Usuário autenticado";

  let todosPets = [];
  let todosInteresses = [];

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

  // --- Funções Auxiliares para Endpoints ---
  function getPetsEndpoint() {
    if (isOng && organizationId) {
      return `http://localhost:8080/api/pets/organizacao/${organizationId}`;
    }
    return "http://localhost:8080/api/pets"; // Para ADMIN e ADOTANTE (todos os pets)
  }

  function getInteressesEndpoint() {
    if (isAdmin) {
      return "http://localhost:8080/api/interesses"; // Todos os interesses
    } else if (isOng && organizationId) {
      return `http://localhost:8080/api/ongs/me/interesses`; // Interesses dos pets da ONG
    } else if (isAdotante) {
      return "http://localhost:8080/api/usuarios/me/interesses"; // Meus interesses como adotante
    }
    // Caso nenhuma role válida seja encontrada
    return null;
  }

  // ---- BUSCAR PETS ----
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
      if (!resp.ok) throw new Error("Falha ao carregar pets");
      const dados = await resp.json();
      // O endpoint para ONG e ADMIN pode retornar uma lista direta ou um Page<PetResponse>
      // Ajustamos para lidar com ambos.
      todosPets = Array.isArray(dados) ? dados : dados.content || [];
      renderPetSelect();
    } catch (error) {
      console.warn("API de pets offline ou inacessível, usando mock.", error);
      todosPets = [
        { id: 10, nome: "Rex" },
        { id: 11, nome: "Mia" },
        { id: 12, nome: "Bob" },
      ];
      renderPetSelect();
    }
  }

  function renderPetSelect() {
    petSelect.innerHTML = '<option value="">Todos os Pets</option>';
    todosPets.forEach((pet) => {
      const option = document.createElement("option");
      option.value = pet.id;
      option.textContent = pet.nome;
      petSelect.appendChild(option);
    });
  }

  // ---- BUSCAR INTERESSES ----
  async function fetchInteresses() {
    interessesContainer.innerHTML = "<p>Carregando...</p>";
    const endpointInteresses = getInteressesEndpoint();

    if (!endpointInteresses) {
      alert("Você não tem permissão para visualizar interesses.");
      window.location.href = "index.html"; // Redireciona para a home
      return;
    }

    try {
      const resp = await fetch(endpointInteresses, {
        headers: { Authorization: "Bearer " + jwtToken },
      });

      if (resp.status === 403) {
        throw new Error(
          "Acesso negado. Você não tem permissão para ver estes interesses."
        );
      }
      if (!resp.ok) throw new Error("Erro ao carregar interesses.");

      todosInteresses = await resp.json();
      aplicarFiltros();
    } catch (error) {
      console.error("Erro ao carregar interesses:", error.message);
      interessesContainer.innerHTML = `<p class="empty-message">${error.message}</p>`;
      noInteressesMessage.style.display = "block";
    }
  }

  // ---- APLICAR FILTROS ----
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

  // ---- RENDERIZAR INTERESSES ----
  function renderInteresses(interesses) {
    interessesContainer.innerHTML = "";

    if (!interesses.length) {
      noInteressesMessage.style.display = "block";
      return;
    }
    noInteressesMessage.style.display = "none";

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

      // Ações só são exibidas se o usuário pode moderar E o interesse estiver PENDENTE
      const actionsHtml =
        canModerate && i.status === "PENDENTE"
          ? `
              <button class="approve-btn">Aprovar</button>
              <button class="reject-btn">Rejeitar</button>
            `
          : '<span class="status-badge status-cell">-</span>'; // Exibe um traço ou nada se não houver ações

      card.innerHTML = `
        <div>${escapeHtml(i.usuario?.nome || "N/A")}</div>
        <div>${escapeHtml(i.pet?.nome || "N/A")}</div>
        <div>${new Date(i.criadoEm).toLocaleDateString("pt-BR")}</div>
        <div class="status-cell">${statusHtml}</div>
        <div class="actions">${actionsHtml}</div>
      `;

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

  // ---- ATUALIZAR STATUS COM ANIMAÇÃO ----
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

      if (!resp.ok) throw new Error("Falha ao atualizar status");

      // Atualiza o estado local do interesse
      const interesseIndex = todosInteresses.findIndex((int) => int.id === id);
      if (interesseIndex > -1) {
        todosInteresses[interesseIndex].status = novoStatus;
      }

      const badgeClass =
        novoStatus === "APROVADO" ? "status-aprovado" : "status-rejeitado";
      const badgeLabel = novoStatus === "APROVADO" ? "Aprovado" : "Rejeitado";

      // Efeito de transição do botão → badge
      if (actionsCell)
        actionsCell.innerHTML = `<span class="status-badge ${badgeClass} animate-badge">${badgeLabel}</span>`;
      if (statusCell)
        statusCell.innerHTML = `<span class="status-badge ${badgeClass} animate-badge">${badgeLabel}</span>`;
      card.dataset.status = novoStatus; // Atualiza o data-status do card

      // Animação de fundo
      card.style.backgroundColor = novoStatus === "APROVADO" ? "#eafaf0" : "#fdeaea";
      setTimeout(() => {
        card.style.transition = "background-color 1s ease";
        card.style.backgroundColor = "#fff";
      }, 400);

      showNotification(
        `Pet ${petNome} foi ${badgeLabel.toLowerCase()} com sucesso!`,
        "success"
      );
      // Re-aplica filtros para garantir que o item sumirá ou mudará de posição se o filtro de status estiver ativo
      aplicarFiltros();
    } catch (err) {
      console.error(err);
      showNotification("Erro ao atualizar status.", "error");
    } finally {
      setTimeout(() => card.classList.remove("changing"), 1000);
    }
  }

  // ---- NOTIFICAÇÃO CENTRAL ----
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

  // ---- EVENTOS ----
  petSelect.onchange = aplicarFiltros;
  document
    .querySelectorAll('input[name="status"]')
    .forEach((cb) => (cb.onchange = aplicarFiltros));
  userFilter.oninput = aplicarFiltros;
  refreshBtn.onclick = fetchInteresses;

  // Inicialização
  await fetchPets();
  await fetchInteresses();
});