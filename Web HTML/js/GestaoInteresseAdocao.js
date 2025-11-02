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
  const canModerate = isAdmin || isOng;

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

  // ---- BUSCAR PETS ----
  async function fetchPets() {
    try {
      const resp = await fetch("http://localhost:8080/api/pets", {
        headers: { Authorization: "Bearer " + jwtToken },
      });
      if (!resp.ok) throw new Error();
      const dados = await resp.json();
      todosPets = Array.isArray(dados) ? dados : dados.content || [];
      renderPetSelect();
    } catch {
      console.warn("API de pets offline, usando mock.");
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
    try {
      const resp = await fetch("http://localhost:8080/api/interesses", {
        headers: { Authorization: "Bearer " + jwtToken },
      });
      if (!resp.ok) throw new Error();
      todosInteresses = await resp.json();
      aplicarFiltros();
    } catch {
      console.error("Erro ao carregar interesses.");
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

      const actionsHtml =
        canModerate && i.status === "PENDENTE"
          ? `
              <button class="approve-btn">Aprovar</button>
              <button class="reject-btn">Rejeitar</button>
            `
          : "-";

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
      const resp = await fetch(`http://localhost:8080/api/interesses/${id}/status`, {
        method: "PUT",
        headers: {
          Authorization: "Bearer " + jwtToken,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ status: novoStatus }),
      });

      if (!resp.ok) throw new Error("Falha ao atualizar status");

      const badgeClass = novoStatus === "APROVADO" ? "status-aprovado" : "status-rejeitado";
      const badgeLabel = novoStatus === "APROVADO" ? "Aprovado" : "Rejeitado";

      // Efeito de transição do botão → badge
      actionsCell.innerHTML = `<span class="status-badge ${badgeClass} animate-badge">${badgeLabel}</span>`;
      statusCell.innerHTML = `<span class="status-badge ${badgeClass} animate-badge">${badgeLabel}</span>`;

      // Animação de fundo
      card.style.backgroundColor = novoStatus === "APROVADO" ? "#eafaf0" : "#fdeaea";
      setTimeout(() => {
        card.style.transition = "background-color 1s ease";
        card.style.backgroundColor = "#fff";
      }, 400);

      showNotification(` Pet ${petNome} foi ${badgeLabel.toLowerCase()} com sucesso!`, "success");
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

  await fetchPets();
  await fetchInteresses();
});
