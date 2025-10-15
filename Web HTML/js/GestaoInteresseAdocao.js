document.addEventListener("DOMContentLoaded", () => {
  const interessesContainer = document.getElementById("interessesContainer");
  const noInteressesMessage = document.getElementById("noInteressesMessage");
  const refreshBtn = document.getElementById("refreshBtn");
  const petSelect = document.getElementById("petSelect");
  const petModal = document.getElementById("petModal");
  const closeModal = document.getElementById("closeModal");
  const petDetails = document.getElementById("petDetails");
  const userFilter = document.getElementById("userFilter");

  // Pegando token JWT e userId salvos durante login
  const jwtToken = localStorage.getItem("accessToken");
  const usuarioId = localStorage.getItem("userId");

  let todosInteresses = [];
  let todosPets = [];

  // Redireciona se não estiver autenticado
  if (!jwtToken || !usuarioId) {
    alert("Você precisa estar logado para acessar!");
    window.location.href = "login.html";
    return;
  }

  // --- Carregando lista de pets autenticado ---
async function fetchPets() {
  try {
    const resp = await fetch('http://localhost:8080/api/pets', {
      headers: {
        'Authorization': 'Bearer ' + jwtToken,
        'Content-Type': 'application/json'
      }
    });
    const dados = await resp.json();
    todosPets = dados.content; // É AQUI!
    renderPetSelect();
    console.log('Pets carregados:', todosPets);
  } catch {
    interessesContainer.innerHTML = '<p style="color:red;">Erro ao carregar pets.</p>';
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
      card.innerHTML = `
        <div>${interesse.usuario?.nome || "N/A"}</div>
        <div>
          <span class="pet-link" onclick="mostrarDetalhesPet(${
            interesse.pet?.id || 0
          })">
            ${interesse.pet?.nome || "N/A"}
          </span>
        </div>
        <div>${new Date(
          interesse.criadoEm || interesse.dataCriacao
        ).toLocaleDateString()}</div>
        <div>${interesse.status}</div>
        <div class="actions">
          <button class="approve-btn" onclick="atualizarStatus(${
            interesse.id
          }, 'Aprovado')">Aprovar</button>
          <button class="reject-btn" onclick="atualizarStatus(${
            interesse.id
          }, 'Rejeitado')">Rejeitar</button>
        </div>
      `;
      interessesContainer.appendChild(card);
    });
  }

  // --- Carregamento dos interesses via API autenticada ---
  async function fetchInteresses() {
    interessesContainer.innerHTML = "<p>Carregando...</p>";
    try {
      const resp = await fetch(
        `http://localhost:8080/api/usuarios/me/interesses?usuarioId=${usuarioId}`,
        {
          headers: {
            Authorization: "Bearer " + jwtToken,
            "Content-Type": "application/json",
          },
        }
      );
      todosInteresses = await resp.json();
      aplicarFiltros();
    } catch {
      interessesContainer.innerHTML =
        '<p style="color:red;">Erro ao carregar interesses.</p>';
    }
  }

  // --- Aplicar filtros ---
  function aplicarFiltros() {
    let interessesFiltrados = [...todosInteresses];

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

  // --- Atualização do status (autenticado) ---
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
      if (resp.ok) {
        alert("Status atualizado com sucesso!");
        fetchInteresses();
      } else {
        throw new Error();
      }
    } catch {
      alert("Erro ao atualizar status.");
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
      const pet = await resp.json();
      petDetails.innerHTML = `
        <h3>${pet.nome}</h3>
        <p><strong>Espécie:</strong> ${pet.especie || "N/A"}</p>
        <p><strong>Raça:</strong> ${pet.raca || "N/A"}</p>
        <p><strong>Idade:</strong> ${pet.idade || "N/A"}</p>
        <p><strong>Descrição:</strong> ${pet.descricao || "N/A"}</p>
        <p><strong>Status:</strong> ${pet.status || "N/A"}</p>
      `;
      petModal.style.display = "block";
    } catch {
      alert("Erro ao carregar detalhes do pet");
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
