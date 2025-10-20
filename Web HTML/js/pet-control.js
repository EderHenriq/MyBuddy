// NAV SLIDER
const slider = document.querySelector(".nav-slider");
const links = document.querySelectorAll(".nav-links a");

function moveSlider(element) {
  slider.style.width = element.offsetWidth + "px";
  slider.style.left = element.offsetLeft + "px";
  slider.style.height = element.offsetHeight + "px";
}

const activeLink = document.querySelector(".nav-links a.active");
if (activeLink) moveSlider(activeLink);

links.forEach((link) => {
  link.addEventListener("click", () => {
    links.forEach((l) => l.classList.remove("active"));
    link.classList.add("active");
    moveSlider(link);
  });
});

// MODAL ADICIONAR PET
const addPetBtn = document.querySelector(".addPet-btn button");
const addPetModal = document.getElementById("addPetModal");
const closeAddPetModal = document.getElementById("closeAddPetModal");

addPetBtn.addEventListener("click", () => {
  addPetModal.style.display = "flex";
});

function closeModal(modal) {
  modal.style.display = "none";
}

closeAddPetModal.addEventListener("click", () => closeModal(addPetModal));

window.addEventListener("click", (e) => {
  if (e.target === addPetModal) closeModal(addPetModal);
});

document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") closeModal(addPetModal);
});

// GESTÃO DE PETS
document.addEventListener("DOMContentLoaded", () => {
  console.log("Script carregado!");

  const petsContainer = document.querySelector(".pets-cards-wrapper");
  const petRegisterForm = document.getElementById("addPetForm");
  const petImageInput = document.getElementById("pet-photos");
  const petSearchInput = document.querySelector(".search-input");
  const petSearchBtn = document.querySelector(".search-btn");

  // Pega os cards que já estão no HTML
  const existingCards = Array.from(document.querySelectorAll(".pets-card"));
  const existingPets = existingCards.map((card) => ({
    nome: card.querySelector(".pet-name").textContent,
    raca: card.querySelector(".pet-breed").textContent.replace("Raça: ", ""),
    idade: card.querySelector(".pet-age")?.textContent.replace("Idade: ", "") || "",
    sexo: card.querySelector(".pet-gender")?.textContent.replace("Sexo: ", "") || "",
    imageUrl: card.querySelector("img")?.src || "/Web HTML/src/assets/imgs/placeholder.jpg",
  }));

  const noPetsMessage = document.createElement("p");
  noPetsMessage.textContent = "Nenhum pet encontrado.";
  noPetsMessage.style.textAlign = "center";
  petsContainer.parentElement.insertBefore(noPetsMessage, petsContainer);
  noPetsMessage.style.display = "none";

  // Modal de detalhes
  const petDetailsModal = document.getElementById("petDetailsModal");
  if (petDetailsModal) {
    const closeDetailsModal = petDetailsModal.querySelector(".close-button");
    closeDetailsModal.addEventListener("click", () =>
      closeModal(petDetailsModal)
    );
    window.addEventListener("click", (e) => {
      if (e.target === petDetailsModal) closeModal(petDetailsModal);
    });
  }

  // Cria card
  function createPetCard(pet) {
    const card = document.createElement("article");
    card.classList.add("pets-card");

    const img = document.createElement("img");
    img.className = "pet-photo";
    img.src = pet.imageUrl || "/Web HTML/src/assets/imgs/placeholder.jpg";
    img.alt = `Foto de ${pet.nome}`;

    const info = document.createElement("div");
    info.className = "pet-info";
    info.innerHTML = `
      <h3 class="pet-name">${pet.nome}</h3>
      <p class="pet-breed">Raça: ${pet.raca || "Não informada"}</p>
      <p class="pet-age">Idade: ${pet.idade || "Não informada"}</p>
      <p class="pet-gender">Sexo: ${pet.sexo || "Não informado"}</p>
      <div class="pet-actions">
          <button class="view-details">Ver detalhes</button>
          <button class="pet-edit">Editar</button>
      </div>
    `;

    const btnDetails = info.querySelector(".view-details");
    btnDetails.addEventListener("click", (e) => {
      e.stopPropagation();
      openDetailsModal(pet);
    });

    card.appendChild(img);
    card.appendChild(info);
    return card;
  }

  // Renderiza apenas os pets passados
  function renderPets(pets) {
    petsContainer.innerHTML = "";
    if (pets.length === 0) {
      noPetsMessage.style.display = "block";
    } else {
      noPetsMessage.style.display = "none";
      pets.forEach((pet) => petsContainer.appendChild(createPetCard(pet)));
    }
  }

  let backendPets = [];

  async function fetchPets() {
    try {
      const res = await fetch(`http://localhost:8080/api/pets`);
      if (!res.ok) throw new Error(`Erro HTTP ${res.status}`);
      const data = await res.json();
      backendPets = data.content || [];
      renderPets([...existingPets, ...backendPets]);
    } catch (err) {
      console.error("Erro ao buscar pets:", err);
      renderPets(existingPets);
    }
  }

  fetchPets();

  // Cadastro de pet
  if (petRegisterForm) {
    petRegisterForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const formData = new FormData(petRegisterForm);
      let imageUrl = null;

      if (petImageInput.files.length > 0) {
        const file = petImageInput.files[0];
        const fd = new FormData();
        fd.append("file", file);

        try {
          const res = await fetch(
            "http://localhost:8080/api/pets/upload-image",
            { method: "POST", body: fd }
          );
          if (!res.ok) throw new Error("Erro no upload da imagem");
          imageUrl = await res.text();
        } catch (err) {
          alert(err.message);
          return;
        }
      }

      const petData = {};
      formData.forEach((v, k) => {
        petData[k] = v;
      });
      if (imageUrl) petData.imageUrl = imageUrl;

      try {
        const res = await fetch("http://localhost:8080/api/pets", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(petData),
        });
        if (!res.ok) throw new Error("Erro ao cadastrar pet");
        await res.json();
        fetchPets();
        closeModal(addPetModal);
      } catch (err) {
        alert(err.message);
      }
    });
  }

  // Modal detalhes
  function openDetailsModal(pet) {
    if (!petDetailsModal) return;
    const img = petDetailsModal.querySelector(".pet-photo-modal");
    const name = petDetailsModal.querySelector(".pet-name-modal");
    img.src = pet.imageUrl || "/Web HTML/src/assets/imgs/placeholder.jpg";
    name.textContent = pet.nome;
    petDetailsModal.style.display = "flex";
  }

  // BUSCA REATIVA E POR BOTÃO
  function applyFilters() {
    const query = petSearchInput.value.trim().toLowerCase();

    if (!query) {
      renderPets([...existingPets, ...backendPets]);
      return;
    }

    const filtered = [...existingPets, ...backendPets].filter(
      (pet) =>
        pet.nome.toLowerCase().includes(query) ||
        (pet.raca && pet.raca.toLowerCase().includes(query))
    );

    renderPets(filtered);
  }

  // Clique no botão
  if (petSearchBtn) {
    petSearchBtn.addEventListener("click", (e) => {
      e.preventDefault();
      applyFilters();
    });
  }

  // Enquanto digita
  if (petSearchInput) {
    petSearchInput.addEventListener("input", applyFilters);
  }

});
