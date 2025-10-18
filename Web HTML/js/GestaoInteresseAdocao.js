document.addEventListener("DOMContentLoaded", () => {
  const interessesContainer = document.getElementById("interessesContainer");
  const noInteressesMessage = document.getElementById("noInteressesMessage");
  const refreshBtn = document.getElementById("refreshBtn");
  const petSelect = document.getElementById("petSelect");
  const petModal = document.getElementById("petModal");
  const closeModal = document.getElementById("closeModal");
  const petDetails = document.getElementById("petDetails");
  const userFilter = document.getElementById("userFilter");

  // Token e roles do usuário autenticado
  const jwtToken = localStorage.getItem("accessToken");
  const roles = JSON.parse(localStorage.getItem("userRoles") || "[]");
  const isAdmin = roles.includes("ROLE_ADMIN");
  const isOng = roles.includes("ROLE_ONG");
  const canModerate = isAdmin || isOng;

  let todosInteresses = [];
  let todosPets = [];

  // Redireciona se não estiver autenticado
  if (!jwtToken) {
    alert("Você precisa estar logado para acessar!");
    window.location.href = "login.html";
    return;
  }

  // Utilitário simples para escapar HTML ao renderizar strings
  const escapeHtml = (s) =>
    String(s ?? "")
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");

  // Handler genérico para respostas HTTP
  async function handleResponse(resp) {
    if (resp.status === 401) {
      alert("Sessão expirada ou não autenticado. Faça login novamente.");
      window.location.href = "./login_screen.html";
      throw new Error("401");
    }
    if (resp.status === 403) {
      alert("Permissão negada. Seu usuário não possui acesso a esta ação.");
      throw new Error("403");
    }
    if (!resp.ok) {
      const msg = await resp.text().catch(() => "");
      throw new Error(msg || "Erro ao processar requisição.");
    }
    return resp;
  }

  // --- Carregando lista de pets autenticado ---
  async function fetchPets() {
    try {
      const resp = await fetch("http://localhost:8080/api/pets", {
        headers: {
          Authorization: "Bearer " + jwtToken,
          "Content-Type": "application/json",
        },
      });
      await handleResponse(resp);
      const dados = await resp.json();
      todosPets = Array.isArray(dados) ? dados : (dados.content || []);
      renderPetSelect();
      console.log("Pets carregados:", todosPets);
    } catch (e) {
      console.error(e);
      interessesContainer.innerHTML =
        '<p style="color:red;">Erro ao carregar pets.</p>';
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

  // --- Renderização dos cards de interesses ---
  function renderInteresses(interesses) {
    interessesContainer.innerHTML = "";
    if (!interesses.length) {
      noInteressesMessage.style.display = "block";
      return;
    }
    noInteressesMessage.style.display = "none";

    // Cabeçalho da tabela
    const header = document.createElement("div");
    header.classList.add("interesse-card");
    header.style.fontWeight = "bold";
    header.style.background = "#f0f0f0";
    header.innerHTML = `
      <div>Usuário</div>
      <div>Pet</div>
      <div>Data</div>
      <div>Status</div>
      <div>Ações</div>
    `;
    interessesContainer.appendChild(header);

    interesses.forEach((interesse) => {
      const card = document.createElement("div");
      card.classList.add("interesse-card");

      const usuarioNome = escapeHtml(interesse.usuario?.nome || "N/A");
      const petId = interesse.pet?.id || 0;
      const petNome = escapeHtml(interesse.pet?.nome || "N/A");
      const dataStr = new Date(
        interesse.criadoEm || interesse.dataCriacao || Date.now()
      ).toLocaleDateString();
      const statusStr = escapeHtml(interesse.status || "Pendente");

      const actionsHtml = canModerate
        ? `
          <button class="approve-btn" onclick="atualizarStatus(${interesse.id}, 'Aprovado')">Aprovar</button>
          <button class="reject-btn" onclick="atualizarStatus(${interesse.id}, 'Rejeitado')">Rejeitar</button>
        `
        : `<span>-</span>`;

      card.innerHTML = `
        <div>${usuarioNome}</div>
        <div>
          <span class="pet-link" onclick="mostrarDetalhesPet(${petId})">
            ${petNome}
          </span>
        </div>
        <div>${escapeHtml(dataStr)}</div>
        <div>${statusStr}</div>
        <div class="actions">
          ${actionsHtml}
        </div>
      `;
      interessesContainer.appendChild(card);
    });
  }

  // --- Carregamento dos interesses via API autenticada ---
  async function fetchInteresses() {
    interessesContainer.innerHTML = "<p>Carregando...</p>";
    try {
      const url = canModerate
        ? "http://localhost:8080/api/interesses" // ADMIN/ONG lista geral
        : "http://localhost:8080/api/usuarios/me/interesses"; // usuário comum lista os próprios (sem usuarioId)
      const resp = await fetch(url, {
        headers: {
          Authorization: "Bearer " + jwtToken,
          "Content-Type": "application/json",
        },
      });
      await handleResponse(resp);
      todosInteresses = await resp.json();
      aplicarFiltros();
    } catch (e) {
      console.error(e);
      interessesContainer.innerHTML =
        '<p style="color:red;">Erro ao carregar interesses.</p>';
    }
  }

  // --- Aplicar filtros ---
  function aplicarFiltros() {
    let interessesFiltrados = Array.isArray(todosInteresses)
      ? [...todosInteresses]
      : [];

    // Filtro por pet
    const petSelecionado = petSelect.value;
    if (petSelecionado) {
      interessesFiltrados = interessesFiltrados.filter(
        (i) => i.pet?.id == petSelecionado
      );
    }

    // Filtro por status (checkboxes)
    const statusSelecionados = Array.from(
      document.querySelectorAll('input[name="status"]:checked')
    ).map((cb) => cb.value);
    if (statusSelecionados.length > 0) {
      interessesFiltrados = interessesFiltrados.filter((i) =>
        statusSelecionados.includes(i.status)
      );
    }

    // Filtro por usuário
    const userNome = userFilter.value?.toLowerCase();
    if (userNome) {
      interessesFiltrados = interessesFiltrados.filter((i) =>
        (i.usuario?.nome || "").toLowerCase().includes(userNome)
      );
    }

    renderInteresses(interessesFiltrados);
  }

  // --- Atualização do status (autenticado e autorizado) ---
  window.atualizarStatus = async function (id, novoStatus) {
    if (!confirm(`Confirma alterar status para "${novoStatus}"?`)) return;
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
      await handleResponse(resp);
      alert("Status atualizado com sucesso!");
      fetchInteresses();
    } catch (e) {
      if (e.message !== "401" && e.message !== "403") {
        alert("Erro ao atualizar status.");
      }
    }
  };

  // --- Modal de detalhes do pet (autenticado) ---
  window.mostrarDetalhesPet = async function (petId) {
    if (!petId) return;
    try {
      const resp = await fetch(`http://localhost:8080/api/pets/${petId}`, {
        headers: {
          Authorization: "Bearer " + jwtToken,
          "Content-Type": "application/json",
        },
      });
      await handleResponse(resp);
      const pet = await resp.json();
      petDetails.innerHTML = `
        <h3>${escapeHtml(pet.nome)}</h3>
        <p><strong>Espécie:</strong> ${escapeHtml(pet.especie || "N/A")}</p>
        <p><strong>Raça:</strong> ${escapeHtml(pet.raca || "N/A")}</p>
        <p><strong>Idade:</strong> ${escapeHtml(pet.idade || "N/A")}</p>
        <p><strong>Descrição:</strong> ${escapeHtml(pet.descricao || "N/A")}</p>
        <p><strong>Status:</strong> ${escapeHtml(pet.status || "N/A")}</p>
      `;
      petModal.style.display = "block";
    } catch (e) {
      if (e.message !== "401" && e.message !== "403") {
        alert("Erro ao carregar detalhes do pet");
      }
    }
  };

  // --- Event Listeners ---
  refreshBtn.onclick = fetchInteresses;
  petSelect.onchange = aplicarFiltros;
  document.querySelectorAll('input[name="status"]').forEach((cb) => {
    cb.onchange = aplicarFiltros;
  });
  userFilter.oninput = aplicarFiltros;

  closeModal.onclick = () => (petModal.style.display = "none");
  window.onclick = (e) => {
    if (e.target === petModal) petModal.style.display = "none";
  };

  // --- Inicialização ---
  fetchPets();
  fetchInteresses();
});
