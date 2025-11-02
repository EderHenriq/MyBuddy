/* Lógica do Slider de Navegação */
const slider = document.querySelector(".nav-slider");
const links = document.querySelectorAll(".nav-links a");

function moveSlider(element) {
    if (!slider || !element) return;
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

/* Lógica do Modal Adicionar Pet */
const addPetBtn = document.querySelector(".addPet-btn button");
const addPetModal = document.getElementById("addPetModal");
const closeAddPetModal = document.getElementById("closeAddPetModal");

function closeModal(modal) {
    if (modal) modal.style.display = "none";
}

if (addPetBtn && addPetModal) {
    addPetBtn.addEventListener("click", () => {
        addPetModal.style.display = "flex";
    });
}

if (closeAddPetModal) {
    closeAddPetModal.addEventListener("click", () => closeModal(addPetModal));
}

window.addEventListener("click", (e) => {
    if (e.target === addPetModal) closeModal(addPetModal);
});


/* Lógica Principal de Gestão de Pets (Em DOMContentLoaded) */
document.addEventListener("DOMContentLoaded", () => {
    const petsContainer = document.querySelector(".pets-cards-wrapper");
    const petRegisterForm = document.getElementById("addPetForm");
    const petImageInput = document.getElementById("pet-photos");
    const photosPreview = document.getElementById("photos-preview"); 
    const petSearchInput = document.querySelector(".search-input");
    const petSearchBtn = document.querySelector(".search-btn");
    const petDetailsModal = document.getElementById("petDetailsModal");
    
    let backendPets = [];

    /* Pega os cards que já estão no HTML */
    const existingCards = Array.from(document.querySelectorAll(".pets-card"));
    const existingPets = existingCards.map((card) => ({
        nome: card.querySelector(".pet-name").textContent,
        raca: card.querySelector(".pet-breed").textContent.replace("Raça: ", ""),
        idade: card.querySelector(".pet-age")?.textContent.replace("Idade: ", "") || "",
        sexo: card.querySelector(".pet-gender")?.textContent.replace("Sexo: ", "") || "",
        imageUrl: card.querySelector("img")?.src || "/Web HTML/src/assets/imgs/placeholder.jpg",
        isFavorite: false,
    }));

    /* Mensagem "Nenhum Pet Encontrado" */
    const noPetsMessage = document.createElement("p");
    noPetsMessage.textContent = "Nenhum pet encontrado.";
    noPetsMessage.style.textAlign = "center";
    if (petsContainer && petsContainer.parentElement) {
        petsContainer.parentElement.insertBefore(noPetsMessage, petsContainer);
        noPetsMessage.style.display = "none";
    }
    
    /* Configura o Modal de Detalhes */
    if (petDetailsModal) {
        const closeDetailsModal = petDetailsModal.querySelector(".close-button");
        if (closeDetailsModal) {
            closeDetailsModal.addEventListener("click", () => closeModal(petDetailsModal));
        }
        window.addEventListener("click", (e) => {
            if (e.target === petDetailsModal) closeModal(petDetailsModal);
        });
    }

    /* Sincroniza Favoritos do LocalStorage */
    function syncFavorites(pets) {
        const favoritePets = JSON.parse(localStorage.getItem("favoritePets")) || [];
        pets.forEach((pet) => {
            pet.isFavorite = favoritePets.some((f) => f.nome === pet.nome);
        });
    }

    /* Cria o Card do Pet */
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
          <div class="pet-name-wrapper" style="display:flex; align-items:center; justify-content:space-between;">
            <h3 class="pet-name">${pet.nome}</h3>
            <button class="favorite-btn" aria-label="Favoritar pet">
              <span class="material-symbols-outlined">${
                pet.isFavorite ? "favorite" : "favorite_border"
              }</span>
            </button>
          </div>
          <p class="pet-breed">Raça: ${pet.raca || "Não informada"}</p>
          <p class="pet-age">Idade: ${pet.idade || "Não informada"}</p>
          <p class="pet-gender">Sexo: ${pet.sexo || "Não informado"}</p>
          <div class="pet-actions">
            <button class="view-details">Ver detalhes</button>
            <button class="pet-edit">Editar</button>
          </div>
        `;

        const favoriteBtn = info.querySelector(".favorite-btn");
        const icon = favoriteBtn.querySelector("span");

        function updateFavoriteIcon(isFav) {
            if (isFav) {
                icon.textContent = "favorite";
                icon.classList.remove("material-symbols-outlined");
                icon.classList.add("material-symbols-rounded");
                favoriteBtn.classList.add("active");
            } else {
                icon.textContent = "favorite_border";
                icon.classList.remove("material-symbols-rounded");
                icon.classList.add("material-symbols-outlined");
                favoriteBtn.classList.remove("active");
            }
        }

        updateFavoriteIcon(pet.isFavorite);

        const allPets = [...existingPets, ...backendPets];
        favoriteBtn.addEventListener("click", (e) => {
            e.stopPropagation();
            pet.isFavorite = !pet.isFavorite;
            updateFavoriteIcon(pet.isFavorite);

            localStorage.setItem(
                "favoritePets",
                JSON.stringify(allPets.filter((p) => p.isFavorite))
            );
        });

        const btnDetails = info.querySelector(".view-details");
        btnDetails.addEventListener("click", (e) => {
            e.stopPropagation();
            openDetailsModal(pet);
        });

        card.appendChild(img);
        card.appendChild(info);
        return card;
    }

    /* Renderiza Pets */
    function renderPets(pets) {
        if (!petsContainer) return;
        petsContainer.innerHTML = "";
        if (pets.length === 0) {
            noPetsMessage.style.display = "block";
        } else {
            noPetsMessage.style.display = "none";
            pets.forEach((pet) => petsContainer.appendChild(createPetCard(pet)));
        }
    }

    /* Busca Pets do Backend */
    async function fetchPets() {
        try {
            const res = await fetch("http://localhost:8080/api/pets");
            if (!res.ok) throw new Error(`Erro HTTP ${res.status}`);
            const data = await res.json();
            backendPets = data.content || [];
        } catch (err) {
            console.error("Erro ao buscar pets:", err);
            backendPets = [];
        }

        syncFavorites(existingPets);
        syncFavorites(backendPets);

        renderPets([...existingPets, ...backendPets]);
    }

    fetchPets();

    /* Cadastro de Pet */
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
            formData.forEach((v, k) => (petData[k] = v));
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

    /* Modal Detalhes */
    function openDetailsModal(pet) {
        if (!petDetailsModal) return;
        const img = petDetailsModal.querySelector(".pet-photo-modal");
        const name = petDetailsModal.querySelector(".pet-name-modal");
        if (img) img.src = pet.imageUrl || "/Web HTML/src/assets/imgs/placeholder.jpg";
        if (name) name.textContent = pet.nome;
        petDetailsModal.style.display = "flex";
    }

    /* Função de Busca e Filtro */
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

    if (petSearchBtn) {
        petSearchBtn.addEventListener("click", (e) => {
            e.preventDefault();
            applyFilters();
        });
    }

    if (petSearchInput) {
        petSearchInput.addEventListener("input", applyFilters);
    }

    /* Preview de Fotos (Movido para dentro de DOMContentLoaded) */
    if (petImageInput && photosPreview) {
        petImageInput.addEventListener("change", () => {
            photosPreview.innerHTML = "";
            Array.from(petImageInput.files).forEach((file) => {
                const reader = new FileReader();
                reader.onload = (e) => {
                    const img = document.createElement("img");
                    img.src = e.target.result;
                    photosPreview.appendChild(img);
                };
                reader.readAsDataURL(file);
            });
        });
    }
});


/* Lógica do Menu Pop-up de Notificações */
const notificationsBtn = document.querySelector(".notification-btn");
const notificationsMenu = document.querySelector(".notification-menu"); 
const notificationsList = document.getElementById("notificationsList");

/* Exemplo de Notificações */
const notifications = [
    "Farofa recebeu um novo carinho!",
    "Cristal acabou de ser adotada!",
    "Nevasca precisa de atualização de vacinação."
];

function renderNotifications() {
    if (!notificationsList) return;
    notificationsList.innerHTML = "";
    if (notifications.length === 0) {
        const li = document.createElement("li");
        li.textContent = "Nenhuma notificação no momento.";
        notificationsList.appendChild(li);
    } else {
        notifications.forEach(n => {
            const li = document.createElement("li");
            li.textContent = n;
            notificationsList.appendChild(li);
        });
    }
}

if (notificationsBtn && notificationsMenu) {
    notificationsBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        
        // Garante que o menu de perfil esteja fechado ao abrir notificações
        const profileMenu = document.querySelector(".profile-menu");
        if (profileMenu && profileMenu.classList.contains("show")) {
            profileMenu.classList.remove("show");
        }
        
        renderNotifications();
        notificationsMenu.classList.toggle("show");
    });
}

/* Lógica do Menu Pop-up de Perfil */
const profileBtn = document.querySelector(".profile-btn");
const profileMenu = document.querySelector(".profile-menu");

if (profileBtn && profileMenu) {
    profileBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        
        // Garante que o menu de notificações esteja fechado ao abrir perfil
        const notificationsMenu = document.querySelector(".notification-menu");
        if (notificationsMenu && notificationsMenu.classList.contains("show")) {
            notificationsMenu.classList.remove("show");
        }
        
        profileMenu.classList.toggle("show");
    });
}

/* Fechamento Geral e Escape */
window.addEventListener("click", (e) => {
    // Fecha o menu de Notificações ao clicar fora
    if (notificationsMenu && notificationsBtn && !notificationsBtn.contains(e.target) && !notificationsMenu.contains(e.target)) {
        notificationsMenu.classList.remove("show");
    }
    
    // Fecha o menu de Perfil ao clicar fora
    if (profileMenu && profileBtn && !profileBtn.contains(e.target) && !profileMenu.contains(e.target)) {
        profileMenu.classList.remove("show");
    }
});

document.addEventListener("keydown", (e) => {
    // Fecha os pop-ups com a tecla Escape, se estiverem abertos
    if (e.key === "Escape") {
        if (notificationsMenu && notificationsMenu.classList.contains("show")) {
            notificationsMenu.classList.remove("show");
        }
        if (profileMenu && profileMenu.classList.contains("show")) {
            profileMenu.classList.remove("show");
        }
    }
});