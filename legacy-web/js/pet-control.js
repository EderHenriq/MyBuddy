// ===========================================
// SELETORES E VARIÁVEIS GLOBAIS
// ===========================================

// Modais
const addEditPetModal = document.getElementById("addEditPetModal");
const closeAddEditPetModalBtn = document.getElementById("closeAddEditPetModal");
const openAddPetModalBtn = document.getElementById("openAddPetModalBtn"); // Botão "Adicionar novo Pet"
const modalTitle = document.getElementById("modalTitle"); // Título do modal de Add/Edit
const addEditPetForm = document.getElementById("addEditPetForm");
const submitPetBtn = document.getElementById("submitPetBtn");
const petIdInput = document.getElementById("pet-id"); // Campo hidden para ID do pet
const filterModal = document.getElementById("filterModal");
const closeFilterModalBtn = document.getElementById("closeFilterModal");
const openFilterModalBtn = document.getElementById("openFilterModalBtn"); // Botão "Mostrar Filtros"
const advancedFiltersForm = document.getElementById("advancedFiltersForm");
const petDetailsModal = document.getElementById("petDetailsModal"); // O SEU NOVO MODAL DE DETALHES
const closePetDetailsModalBtn = document.getElementById("closePetDetailsModal");
const detailsModalTitle = document.getElementById("detailsModalTitle"); // Título do modal de detalhes (NOVO)
const detailsPetImage = document.getElementById("details-pet-image");
const detailPetName = document.getElementById("detail-pet-name");
const detailPetSpecies = document.getElementById("detail-pet-species");
const detailPetBreed = document.getElementById("detail-pet-race");
const detailPetAge = document.getElementById("detail-pet-age");
const detailPetGender = document.getElementById("detail-pet-gender");
const detailPetSize = document.getElementById("detail-pet-port");
const detailPetColor = document.getElementById("detail-pet-color");
const detailPetCoat = document.getElementById("detail-pet-coat");
const detailPetVaccinated = document.getElementById("detail-pet-vacinado");
const detailPetNeutered = document.getElementById("detail-pet-castrado");
const detailPetMicrochipped = document.getElementById("detail-pet-microchipado");
const detailPetStatus = document.getElementById("detail-pet-status");
const detailPetShelter = document.getElementById("detail-pet-shelter");
const detailPetCity = document.getElementById("detail-pet-city");
const detailPetState = document.getElementById("detail-pet-state");
const closeDetailsModalBtn = document.getElementById("closeDetailsModalBtn"); // Botão "Fechar" dentro do modal de detalhes
// Modais de Mensagem
const successMessageModal = document.getElementById("successMessageModal");
const closeSuccessMessageModalBtn = document.getElementById("closeSuccessMessageModal");
const goToInterestsPageBtn = document.getElementById("goToInterestsPageBtn"); // Botão para ir para a página de interesses

// Elementos do Header
const authButtons = document.querySelector('.auth-buttons.logged-out'); // Botões Login/Cadastro
const profileArea = document.querySelector('.profile-area.logged-in'); // Área "Olá, Nome"
const userNameDisplay = document.getElementById('userNameDisplay'); // Onde "Nome" será exibido
const logoutButton = document.querySelector('.logout-btn'); // Botão Sair do header
const userProfileLink = document.getElementById('userProfileLink'); // Link para perfil (se tiver)

// Seção de Pets
const petsContainer = document.querySelector(".pets-cards-wrapper");
const petSearchInput = document.getElementById("searchInput"); // Alterado para buscar por ID
const petSearchBtn = document.querySelector(".search-btn");
const noPetsMessage = document.createElement("p"); // Mensagem "Nenhum pet encontrado"
noPetsMessage.textContent = "Nenhum pet encontrado.";
noPetsMessage.style.textAlign = "center";
document.querySelector('.main-container').insertBefore(noPetsMessage, petsContainer);
noPetsMessage.style.display = "none";

// Upload de Imagem
const petImageInput = document.getElementById("pet-photos");
const photosPreview = document.getElementById("photos-preview");

// Campos de seleção de organização
const petShelterSelect = document.getElementById("pet-shelter"); // Seleciona o select de ONG/Tutor no formulário de pet
const filterShelterSelect = document.getElementById("filter-shelter"); // Seleciona o select de ONG/Tutor nos filtros

// Backend URL
const API_BASE_URL = 'http://localhost:8080/api';// URL base da sua API

// Arrays para guardar os pets e organizações
let allPets = []; // Todos os pets carregados do backend
let allOrganizations = []; // Todas as organizações carregadas do backend
let currentRole = "ROLE_ADOTANTE"; // Role padrão, será atualizada pelo JS

// ===========================================
// FUNÇÕES AUXILIARES
// ===========================================

function openModal(modalElement) {
    modalElement.style.display = "flex"; // Usa flex para centralizar
    document.body.style.overflow = "hidden"; // Impede o scroll da página
}

function closeModal(modalElement) {
    modalElement.style.display = "none";
    document.body.style.overflow = "auto"; // Restaura o scroll da página
}

function clearAddEditPetForm() {
    addEditPetForm.reset(); // Reseta todos os campos do formulário
    petIdInput.value = ''; // Garante que o ID hidden seja limpo
    photosPreview.innerHTML = '<span>Nenhuma imagem selecionada.</span>'; // Limpa a pré-visualização de fotos com placeholder
    photosPreview.classList.remove('has-images'); // Remove a classe de indicação de imagens
    submitPetBtn.textContent = "Adicionar Pet"; // Volta o texto do botão para "Adicionar Pet"
    modalTitle.textContent = "Adicionar Novo Pet"; // Volta o título do modal
    delete petImageInput.dataset.existingImageUrl; // Limpa a URL da imagem existente
    document.getElementById("pet-vaccinated").checked = false;
    document.getElementById("pet-neutered").checked = false;
    document.getElementById("pet-microchipped").checked = false;

    // Garante que os campos estão habilitados para um novo cadastro
    addEditPetForm.querySelectorAll('input, select, textarea').forEach(field => {
        field.removeAttribute('disabled');
    });
    if (petImageInput) petImageInput.style.display = 'block'; // Garante que o input de foto aparece
    if (submitPetBtn) submitPetBtn.style.display = 'block'; // Garante que o botão de submit aparece
}

function displayImagePreviews(files) {
    photosPreview.innerHTML = ""; // Limpa prévias existentes
    if (files.length > 0) {
        photosPreview.classList.add('has-images');
        Array.from(files).forEach(file => {
            const reader = new FileReader();
            reader.onload = (e) => {
                const img = document.createElement("img");
                img.src = e.target.result;
                img.alt = "Prévia da foto do pet";
                photosPreview.appendChild(img);
            };
            reader.readAsDataURL(file);
        });
    } else {
        photosPreview.classList.remove('has-images');
        photosPreview.innerHTML = '<span>Nenhuma imagem selecionada.</span>'; // Adiciona o placeholder novamente
    }
}

function isLoggedIn() {
    return localStorage.getItem('accessToken') !== null;
}

function updateHeaderUI() {
    if (isLoggedIn()) {
        if (authButtons) authButtons.style.display = 'none';
        if (profileArea) profileArea.style.display = 'flex'; // Ou 'block', dependendo do seu CSS

        const userName = localStorage.getItem('userName');
        if (userNameDisplay && userName) {
            userNameDisplay.textContent = `Olá, ${userName}`;
        }

    } else {
        if (authButtons) authButtons.style.display = 'flex'; // Ou 'block'
        if (profileArea) profileArea.style.display = 'none';
    }
}

function logout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userRoles');
    localStorage.removeItem('userName');
    localStorage.removeItem('userId');
    localStorage.removeItem('userOrganizacaoId'); // Remover este também

    // Redireciona para a página inicial ou de login
    window.location.href = 'login_screen.html';
}

function mapStatusAdocaoToFriendlyText(status) {
    if (!status) return "Não informado";
    switch (status.toUpperCase()) {
        case "ADOTADO":
            return "Adotado";
        case "RESERVADO":
            return "Reservado";
        case "INDISPONIVEL":
            return "Indisponível para Adoção";
        case "DISPONIVEL":
            return "Disponível para Adoção";
        default:
            return "Não informado";
    }
}

function mapEspecieToFriendlyText(especie) {
    if (!especie) return "Não informada";
    switch (especie.toUpperCase()) { // Converte para maiúsculas antes de comparar
        case "CAO":
        case "DOG": // Se o backend envia "Dog"
            return "Cachorro";
        case "GATO":
        case "CAT": // Se o backend envia "Cat"
            return "Gato";
        case "COELHO":
        case "RABBIT": // Se o backend envia "Rabbit"
            return "Coelho";
        case "PASSARO":
        case "BIRD": // Se o backend envia "Bird"
            return "Pássaro";
        case "ROEDOR":
        case "RODENT": // Se o backend envia "Rodent"
            return "Roedor";
        case "PEIXE":
        case "FISH": // Se o backend envia "Fish"
            return "Peixe";
        case "OUTROS":
        case "OTHER": // Se o backend envia "Other"
            return "Outros";
        default:
            return "Não informada";
    }
}

function mapPorteToFriendlyText(porte) {
    if (!porte) return "Não informado";
    switch (porte.toUpperCase()) {
        case "PEQUENO":
        case "SMALL": // Adicionado: se o backend envia "Small"
            return "Pequeno";
        case "MEDIO":
        case "MEDIUM": // Adicionado: se o backend envia "Medium"
            return "Médio";
        case "GRANDE":
        case "LARGE": // Adicionado: se o backend envia "Large"
            return "Grande";
        // Se você tiver outras opções de porte, adicione aqui
        default:
            return "Não informado";
    }
}

function mapPelagemToFriendlyText(pelagem) {
    if (!pelagem || pelagem.trim() === '') return "Não informada";
    const normalizedPelagem = pelagem.toUpperCase().trim();

    if (normalizedPelagem === 'NÃO INFORMADO' || normalizedPelagem === 'NAO INFORMADO') {
        return "Não informada";
    }

    switch (normalizedPelagem) {
        case "CURTA":
        case "SHORT": // Adicionado: se o backend envia "Short"
            return "Curta";
        case "MEDIA":
        case "MEDIUM": // Adicionado: se o backend envia "Medium"
            return "Média";
        case "LONGA":
        case "LONG": // Adicionado: se o backend envia "Long"
            return "Longa";
        // Adicione outros casos de pelagem se houver
        default:
            return "Não informada";
    }
}

function mapSexoToFriendlyText(sexo) {
    if (!sexo) return "Não informado";
    switch (sexo.toUpperCase()) {
        case "MACHO":
        case "M": // Mantido, pois o JSON mostra "M"
            return "Macho";
        case "FEMEA":
        case "F": // Adicionado para consistência
            return "Fêmea";
        default:
            return "Não informado";
    }
}

function mapBooleanToText(value) {
    if (value === true) return 'Sim';
    if (value === false) return 'Não';
    return 'Não informado'; // Adicionado para lidar com null/undefined, se ocorrer
}

// ===========================================
// LÓGICA DE ROLES E PERMISSÕES
// ===========================================

function getUserRole() {
    const rolesJson = localStorage.getItem('userRoles'); // Altera para 'userRoles' (plural)
    if (rolesJson) {
        try {
            const rolesArray = JSON.parse(rolesJson); // Converte a string JSON para um array
            if (rolesArray.includes("ROLE_ADMIN")) {
                return "ROLE_ADMIN";
            }
            if (rolesArray.includes("ROLE_ONG")) {
                return "ROLE_ONG";
            }
            if (rolesArray.length > 0) {
                return rolesArray[0];
            }
        } catch (e) {
            console.error("Erro ao parsear userRoles do localStorage:", e);
        }
    }
    return 'ROLE_ADOTANTE'; // Fallback padrão se nada for encontrado ou houver erro
}

function applyRolePermissions() {
    currentRole = getUserRole(); // Atualiza a role
    console.log("Role do usuário logado:", currentRole);

    const isOngOrAdmin = currentRole === "ROLE_ONG" || currentRole === "ROLE_ADMIN";
    const isAdmin = currentRole === "ROLE_ADMIN";
    const isAdotante = currentRole === "ROLE_ADOTANTE";

    // Botão "Adicionar novo Pet"
    if (openAddPetModalBtn) {
        openAddPetModalBtn.style.display = isOngOrAdmin ? "flex" : "none";
    }

    // Campos no modal de adicionar/editar pet (ex: pet-shelter, pet-availability)
    const shelterLabel = document.querySelector('label[for="pet-shelter"]');
    const shelterInput = document.getElementById("pet-shelter");
    const availabilityLabel = document.querySelector('label[for="pet-availability"]');
    const availabilitySelect = document.getElementById("pet-availability");

    if (shelterLabel && shelterInput) {
        shelterLabel.style.display = isOngOrAdmin ? "block" : "none";
        shelterInput.style.display = isOngOrAdmin ? "block" : "none";

        // Se for ONG, pré-seleciona a própria ONG e desabilita o campo.
        if (currentRole === "ROLE_ONG") {
            const userOrgId = localStorage.getItem('userOrganizacaoId'); // Assumindo que você salva o ID da ONG
            if (userOrgId && petShelterSelect.options.length > 1) { // Verifica se as opções já foram carregadas
                petShelterSelect.value = userOrgId;
                petShelterSelect.setAttribute('disabled', 'true');
            } else if (!userOrgId) {
                console.warn("userOrganizacaoId não encontrado no localStorage para ROLE_ONG.");
            }
        } else {
            petShelterSelect.removeAttribute('disabled');
        }
    }

    if (availabilityLabel && availabilitySelect) {
        availabilityLabel.style.display = isOngOrAdmin ? "block" : "none";
        availabilitySelect.style.display = isOngOrAdmin ? "block" : "none";
        // Lógica adicional: Restrições de opções no select dependendo da role
        if (!isAdmin) {
            const adoptedOption = availabilitySelect.querySelector('option[value="ADOTADO"]');
            if (adoptedOption) adoptedOption.style.display = 'none';
        } else {
            const adoptedOption = availabilitySelect.querySelector('option[value="ADOTADO"]');
            if (adoptedOption) adoptedOption.style.display = 'block';
        }
    }
}


// ===========================================
// GESTÃO DE ORGANIZAÇÕES
// ===========================================

async function fetchOrganizations() {
    try {
        const token = localStorage.getItem('accessToken');
        const headers = { 'Content-Type': 'application/json' };
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const res = await fetch(`${API_BASE_URL}/organizacoes`, {
            method: 'GET',
            headers: headers
        });

        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Erro ao buscar organizações: ${res.status} - ${errorText}`);
        }
        allOrganizations = await res.json();
        populateOrganizationSelects(allOrganizations);
    } catch (err) {
        console.error("Erro ao carregar organizações:", err);
        alert(`Não foi possível carregar as organizações: ${err.message}`);
    }
}

function populateOrganizationSelects(organizations) {
    // Popula o select no formulário de Adicionar/Editar Pet
    if (petShelterSelect) {
        // Mantém a opção padrão "Selecione a ONG/Tutor"
        petShelterSelect.innerHTML = '<option value="">Selecione a ONG/Tutor</option>';
        organizations.forEach(org => {
            const option = document.createElement('option');
            option.value = org.id;
            option.textContent = org.nomeFantasia;
            petShelterSelect.appendChild(option);
        });
        // Re-aplica permissões para pré-selecionar/desabilitar para ONGs
        applyRolePermissions();
    }

    // Popula o select no modal de Filtros
    if (filterShelterSelect) {
        filterShelterSelect.innerHTML = '<option value="All">Todos</option>';
        organizations.forEach(org => {
            const option = document.createElement('option');
            option.value = org.id;
            option.textContent = org.nomeFantasia;
            filterShelterSelect.appendChild(option);
        });
    }
}


// ===========================================
// GESTÃO DE PETS
// ===========================================

function showManifestInterestModal(petId) {
    const userMessage = prompt("Adicione uma mensagem para a ONG/Tutor :");

    // Se o usuário clicou em Cancelar no prompt, não faz nada
    if (userMessage === null) {
        return;
    }

    // Chama a função real para manifestar interesse
    manifestarInteresseNoPet(petId, userMessage || "") // Passa a mensagem (vazia se o usuário não digitou)
        .then(() => {
            // Se a manifestação de interesse foi bem-sucedida, exibe o modal de sucesso
            openModal(successMessageModal);
        })
        .catch(error => {
            console.error("Erro ao mostrar modal de interesse:", error);
            alert("Não foi possível registrar seu interesse. Tente novamente.");
        });
}

function createPetCard(pet) {
    const card = document.createElement("article");
    card.classList.add("pets-card");
    card.setAttribute("data-pet-id", pet.id);
    const imageUrlFromBackend = pet.fotosUrls && pet.fotosUrls.length > 0 ? pet.fotosUrls[0] : null;

    let finalImageUrl = '../src/assets/imgs/placeholder.jpg'; // Default para o placeholder local

    if (imageUrlFromBackend && typeof imageUrlFromBackend === 'string' && imageUrlFromBackend.length > 0) {
        finalImageUrl = `${API_BASE_URL.replace('/api', '')}/uploads/${imageUrlFromBackend}`;
    }

    // Encontra o nome da organização pelo ID
    const organizationName = allOrganizations.find(org => org.id === pet.organizacaoId)?.nomeFantasia || 'Não Informado';

    card.innerHTML = `
        <img src="${finalImageUrl}" alt="Foto de ${pet.nome}" class="pet-photo">
        <div class="pet-info">
            <h3 class="pet-name">${pet.nome}</h3>
            <p class="pet-breed">Raça: ${pet.raca || "Não informada"}</p>
            <p class="pet-age">Idade: ${pet.idade} ${pet.idade > 1 ? 'anos' : 'ano'}</p>
            <p class="pet-gender">Sexo: ${mapSexoToFriendlyText(pet.sexo)}</p>
            <p class="pet-status">Status: ${mapStatusAdocaoToFriendlyText(pet.statusAdocao)}</p>
            <p class="pet-organization">ONG/Tutor: ${organizationName}</p>
            <div class="pet-actions">
                <button class="view-details card-action-btn"><i class="fas fa-info-circle"></i> Ver detalhes</button>
                <button class="pet-edit card-action-btn"><i class="fas fa-edit"></i> Editar</button>
                <button class="pet-delete card-action-btn"><i class="fas fa-trash-alt"></i> Excluir</button>
                <button class="manifestar-interesse success-btn"><i class="fas fa-heart"></i> Manifestar Interesse</button>
            </div>
        </div>
    `;

    // Ações de visibilidade dos botões
    const btnDetails = card.querySelector(".view-details");
    const btnEdit = card.querySelector(".pet-edit");
    const btnDelete = card.querySelector(".pet-delete");
    const btnManifestarInteresse = card.querySelector(".manifestar-interesse"); // NOVO BOTÃO

    const isOngOrAdmin = currentRole === "ROLE_ONG" || currentRole === "ROLE_ADMIN";
    const isAdotante = currentRole === "ROLE_ADOTANTE";

    // Esconder/mostrar botões de editar/excluir baseados na role
    if (btnEdit) btnEdit.style.display = isOngOrAdmin ? "flex" : "none";
    if (btnDelete) btnDelete.style.display = isOngOrAdmin ? "flex" : "none";

    // Mostrar botão Manifestar Interesse apenas para adotantes e se o pet estiver "DISPONIVEL" ou "EM_ADOCAO"
    if (btnManifestarInteresse) {
        if (isAdotante && pet.statusAdocao === 'DISPONIVEL') { 
            btnManifestarInteresse.style.display = "flex";
        } else {
            btnManifestarInteresse.style.display = "none";
        }
    }

    // Adiciona event listeners aos botões
    btnDetails.addEventListener("click", (e) => {
        e.stopPropagation();
        displayPetDetails(pet); // Chama a nova função para exibir os detalhes no modal
    });

    if (btnEdit) {
        btnEdit.addEventListener("click", (e) => {
            e.stopPropagation();
            fillAddEditPetForm(pet); // Preenche o formulário para edição
            openModal(addEditPetModal); // Abre o modal de Add/Edit
        });
    }

    if (btnDelete) {
        btnDelete.addEventListener("click", (e) => {
            e.stopPropagation();
            if (confirm(`Tem certeza que deseja excluir o pet ${pet.nome}?`)) {
                deletePet(pet.id);
            }
        });
    }

    if (btnManifestarInteresse) {
        btnManifestarInteresse.addEventListener("click", (e) => {
            e.stopPropagation();
            showManifestInterestModal(pet.id); // Nova função para manifestar interesse
        });
    }

    return card;
}

function renderPets(petsToRender) {
    petsContainer.innerHTML = ""; // Limpa os cards existentes
    if (petsToRender.length === 0) {
        noPetsMessage.style.display = "block";
    } else {
        noPetsMessage.style.display = "none";
        petsToRender.forEach((pet) => petsContainer.appendChild(createPetCard(pet)));
    }
}

function fillAddEditPetForm(pet) {
    clearAddEditPetForm(); // Limpa antes de preencher
    if (!pet) return;

    modalTitle.textContent = "Editar Pet";
    submitPetBtn.textContent = "Salvar Alterações";
    submitPetBtn.style.display = 'block'; // Garante que o botão Salvar aparece

    petIdInput.value = pet.id || '';
    // Mapeamento direto pelos `id`s dos elementos do formulário
    document.getElementById("pet-name").value = pet.nome || '';
    document.getElementById("pet-species").value = pet.especie || '';
    document.getElementById("pet-breed").value = pet.raca || '';
    document.getElementById("pet-age").value = pet.idade || '';
    document.getElementById("pet-gender").value = pet.sexo || '';
    document.getElementById("pet-size").value = pet.porte || '';
    document.getElementById("pet-color").value = pet.cor || '';
    document.getElementById("pet-coat").value = pet.pelagem || '';

    // Campos booleanos (checkboxes)
    document.getElementById("pet-vaccinated").checked = pet.vacinado || false;
    document.getElementById("pet-neutered").checked = pet.castrado || false;
    document.getElementById("pet-microchipped").checked = pet.microchipado || false;

    // NOVO: Preenche o select de status de adoção
    document.getElementById("pet-availability").value = pet.statusAdocao || '';

    if (pet.organizacaoId) { 
        petShelterSelect.value = pet.organizacaoId;
    } else {
        petShelterSelect.value = ""; // Nenhuma organização selecionada
    }

    document.getElementById("pet-city").value = pet.cidade || '';
    document.getElementById("pet-state").value = pet.estado || '';

    // Se tiver uma imagem já, exibe a prévia
    photosPreview.innerHTML = "";
    // Adição para pegar o primeiro URL da lista fotosUrls, como no createPetCard
    const currentImageUrlFromBackend = pet.fotosUrls && pet.fotosUrls.length > 0 ? pet.fotosUrls[0] : null;

    if (currentImageUrlFromBackend) {
        photosPreview.classList.add('has-images');
        // Usar a mesma lógica de imageUrl do createPetCard para garantir que a imagem seja carregada corretamente
        const imgDisplayUrl = `${API_BASE_URL.replace('/api', '')}/uploads/${currentImageUrlFromBackend}`;
        const img = document.createElement("img");
        img.src = imgDisplayUrl;
        img.alt = `Foto de ${pet.nome}`;
        photosPreview.appendChild(img);
        // Salva a URL *relativa/nome do arquivo* da imagem existente para não perdê-la em caso de não upload de nova imagem
        // Pois o backend espera apenas o nome do arquivo para PUT
        petImageInput.dataset.existingImageUrl = currentImageUrlFromBackend;
    } else {
        photosPreview.classList.remove('has-images');
        photosPreview.innerHTML = '<span>Nenhuma imagem selecionada.</span>'; // Placeholder
        delete petImageInput.dataset.existingImageUrl;
    }

    // Garante que os campos não estejam desabilitados (apenas para edição, não para visualização)
    addEditPetForm.querySelectorAll('input, select, textarea').forEach(field => {
        field.removeAttribute('disabled');
    });
    if (petImageInput) petImageInput.style.display = 'block'; // Garante que o input de foto aparece

    // Re-aplica as permissões de role para o select de organização, caso ele tenha sido desabilitado
    applyRolePermissions();
}

function displayPetDetails(pet) {
    // Título do modal de detalhes
    detailsModalTitle.textContent = `Detalhes de ${pet.nome}`;

    // Imagem do pet
    const detailsImageUrlFromBackend = pet.fotosUrls && pet.fotosUrls.length > 0 ? pet.fotosUrls[0] : null;

    let finalDetailsImageUrl = '../src/assets/imgs/placeholder.jpg'; // Default para o placeholder local

    if (detailsImageUrlFromBackend && typeof detailsImageUrlFromBackend === 'string' && detailsImageUrlFromBackend.length > 0) {
        finalDetailsImageUrl = `${API_BASE_URL.replace('/api', '')}/uploads/${detailsImageUrlFromBackend}`;
    }
    detailsPetImage.src = finalDetailsImageUrl;
    detailsPetImage.alt = `Foto de ${pet.nome}`;

    // Preenche os campos de detalhes usando os seletores corrigidos e funções de mapeamento
    detailPetName.textContent = pet.nome || 'Não informado';
    // Utiliza as novas funções de mapeamento para tradução dos enums
    detailPetSpecies.textContent = mapEspecieToFriendlyText(pet.especie);
    detailPetBreed.textContent = pet.raca || 'Não informada';
    detailPetAge.textContent = (pet.idade !== undefined && pet.idade !== null) ? `${pet.idade} ${pet.idade > 1 ? 'anos' : 'ano'}` : 'Não informado';
    detailPetGender.textContent = mapSexoToFriendlyText(pet.sexo);
    detailPetSize.textContent = mapPorteToFriendlyText(pet.porte);
    detailPetColor.textContent = pet.cor || 'Não informada';
    detailPetCoat.textContent = mapPelagemToFriendlyText(pet.pelagem);

    detailPetVaccinated.textContent = mapBooleanToText(pet.vacinado);
    detailPetNeutered.textContent = mapBooleanToText(pet.castrado);
    detailPetMicrochipped.textContent = mapBooleanToText(pet.microchipado);

    detailPetStatus.textContent = mapStatusAdocaoToFriendlyText(pet.statusAdocao);

    if (pet.organizacao && pet.organizacao.nomeFantasia) {
        detailPetShelter.textContent = pet.organizacao.nomeFantasia;
    } else {
        const organization = allOrganizations.find(org => org.id === pet.organizacaoId);
        detailPetShelter.textContent = organization ? organization.nomeFantasia : 'Não informado';
    }

    detailPetCity.textContent = pet.cidade || 'Não informado';
    detailPetState.textContent = pet.estado || 'Não informado';

    openModal(petDetailsModal); // Abre o modal de detalhes
}

async function fetchPets(filters = {}) {
    try {
        const queryParams = new URLSearchParams(filters).toString();
        const url = `${API_BASE_URL}/pets${queryParams ? `?${queryParams}` : ''}`;

        const token = localStorage.getItem('accessToken'); // Pega o token JWT

        const headers = {
            'Content-Type': 'application/json'
        };
        if (token) {
            headers['Authorization'] = `Bearer ${token}`; // Adiciona o token no cabeçalho
        }

        const res = await fetch(url, {
            method: 'GET',
            headers: headers
        });

        if (!res.ok) {
            const errorText = await res.text();
            if (res.status === 401) {
                alert("Sessão expirada ou não autenticada. Faça login novamente.");
                 window.location.href = '/login_screen.html'; // Redireciona
            }
            throw new Error(`Erro ao buscar pets: ${res.status} - ${errorText}`);
        }
        const data = await res.json();
        allPets = data.content || [];
        renderPets(allPets);
    } catch (err) {
        console.error("Erro ao buscar pets:", err);
        alert(`Não foi possível carregar os pets: ${err.message}`);
        renderPets([]);
    }
}

async function sendPetToBackend(petData, method, petId = null) {
    try {
        const url = petId ? `${API_BASE_URL}/pets/${petId}` : `${API_BASE_URL}/pets`;
        const token = localStorage.getItem('accessToken');

        const res = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(petData),
        });

        if (!res.ok) {
            const errorBody = await res.json();
            throw new Error(`Erro na operação: ${res.status} - ${errorBody.message || 'Erro desconhecido'}`);
        }

        await res.json();
        alert(`Pet ${method === 'POST' ? 'cadastrado' : 'atualizado'} com sucesso!`);
        closeModal(addEditPetModal);
        clearAddEditPetForm();
        fetchPets();
    } catch (err) {
        console.error(`Erro ao ${method === 'POST' ? 'cadastrar' : 'atualizar'} pet:`, err);
        alert(`Erro: ${err.message}`);
    }
}

async function deletePet(petId) {
    try {
        const token = localStorage.getItem('accessToken');
        const res = await fetch(`${API_BASE_URL}/pets/${petId}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!res.ok) {
            const errorBody = await res.json();
            throw new Error(`Erro ao excluir pet: ${res.status} - ${errorBody.message || 'Erro desconhecido'}`);
        }

        alert("Pet excluído com sucesso!");
        fetchPets();
    } catch (err) {
        console.error("Erro ao excluir pet:", err);
        alert(`Erro: ${err.message}`);
    }
}

async function manifestarInteresseNoPet(petId, mensagem) {
    const jwtToken = localStorage.getItem("accessToken"); // Certifique-se de pegar o token aqui também!

    if (!jwtToken) {
        alert("Você precisa estar logado para manifestar interesse.");
        window.location.href = "login.html";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/interesses", { 
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwtToken}` 
            },
            body: JSON.stringify({ petId: petId, mensagem: mensagem })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Erro ao manifestar interesse.");
        }

        fetchPets();

        //alert("Interesse manifestado com sucesso!");
        // Fechar modal ou atualizar UI
    } catch (error) {
        console.error("Erro ao manifestar interesse:", error);
        alert("Erro ao manifestar interesse: " + error.message);
    }
}


// ===========================================
// EVENT LISTENERS GERAIS
// ===========================================

document.addEventListener("DOMContentLoaded", async () => {
    console.log("Script pet-control.js carregado!");

    updateHeaderUI(); 

    // Adiciona o event listener para o botão de logout
    if (logoutButton) {
        logoutButton.addEventListener('click', (e) => {
            e.preventDefault(); // Previne o comportamento padrão do link
            logout();
        });
    }

    // Carrega as organizações primeiro, pois são necessárias para os selects e para renderizar os pets
    await fetchOrganizations();
    applyRolePermissions();
    fetchPets();

    // Abrir modal de Adicionar/Editar Pet
    if (openAddPetModalBtn) {
        openAddPetModalBtn.addEventListener("click", () => {
            clearAddEditPetForm();
            // Garante que os campos estão habilitados para um novo cadastro
            addEditPetForm.querySelectorAll('input, select, textarea').forEach(field => {
                field.removeAttribute('disabled');
            });
            if (petImageInput) petImageInput.style.display = 'block';
            if (submitPetBtn) submitPetBtn.style.display = 'block';
            openModal(addEditPetModal);
            applyRolePermissions(); // Re-aplica permissões para configurar o select de ONG
        });
    }

    // Fechar modal de Adicionar/Editar Pet
    if (closeAddEditPetModalBtn) {
        closeAddEditPetModalBtn.addEventListener("click", () => closeModal(addEditPetModal));
    }
    // Adiciona listener para o botão "Cancelar" no modal de Adicionar/Editar
    const cancelPetBtn = document.getElementById("cancelPetBtn");
    if (cancelPetBtn) {
        cancelPetBtn.addEventListener("click", () => closeModal(addEditPetModal));
    }

    window.addEventListener("click", (e) => {
        if (e.target === addEditPetModal) closeModal(addEditPetModal);
    });
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") closeModal(addEditPetModal);
    });

    //Fechar modal de Detalhes do Pet
    if (closePetDetailsModalBtn) {
        closePetDetailsModalBtn.addEventListener("click", () => closeModal(petDetailsModal));
    }
    // Adiciona listener para o botão "Fechar" dentro do modal de detalhes
    if (closeDetailsModalBtn) {
        closeDetailsModalBtn.addEventListener("click", () => closeModal(petDetailsModal));
    }

    window.addEventListener("click", (e) => {
        if (e.target === petDetailsModal) closeModal(petDetailsModal);
    });
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") closeModal(petDetailsModal);
    });

    // Fechar modal de Mensagem de Sucesso
if (closeSuccessMessageModalBtn) {
    closeSuccessMessageModalBtn.addEventListener("click", () => closeModal(successMessageModal));
}
window.addEventListener("click", (e) => {
    if (e.target === successMessageModal) closeModal(successMessageModal);
});
document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeModal(successMessageModal);
});

// Botão "Ver Meus Interesses" no modal de sucesso
if (goToInterestsPageBtn) {
    goToInterestsPageBtn.addEventListener("click", () => {
        closeModal(successMessageModal);
        // Redireciona para a página de interesses de adoção
        // Assumindo que você terá uma página chamada 'meus-interesses.html'
        window.location.href = 'GestaoInteresseAdocao.html'; // Ajuste o nome do arquivo se for diferente
    });
}


    // Abrir modal de Filtros
    if (openFilterModalBtn) {
        openFilterModalBtn.addEventListener("click", () => openModal(filterModal));
    }

    // Fechar modal de Filtros
    if (closeFilterModalBtn) {
        closeFilterModalBtn.addEventListener("click", () => closeModal(filterModal));
    }
    // Adiciona listener para o botão "Cancelar" no modal de Filtros
    const cancelFilterBtn = document.getElementById("closeFilterModalBtn"); // ID do botão de fechar nos filtros
    if (cancelFilterBtn) {
        cancelFilterBtn.addEventListener("click", () => closeModal(filterModal));
    }

    window.addEventListener("click", (e) => {
        if (e.target === filterModal) closeModal(filterModal);
    });
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") closeModal(filterModal);
    });


    // Lógica de pré-visualização de imagens
    if (petImageInput) {
        petImageInput.addEventListener("change", (e) => {
            displayImagePreviews(e.target.files);
        });
    }

    // Submissão do formulário de Adicionar/Editar Pet
    if (addEditPetForm) {
        addEditPetForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const petId = petIdInput.value;
            const method = petId ? "PUT" : "POST";

            const formData = new FormData(addEditPetForm);
            let petData = {};

            // Inicializa todos os checkboxes como false, caso não venham do formData
            const checkboxFields = ['vacinado', 'castrado', 'microchipado'];
            checkboxFields.forEach(field => {
                petData[field] = false;
            });

            for (let [key, value] of formData.entries()) {
                // Ignora o campo de upload de arquivo aqui, pois será tratado separadamente
                if (key === 'pet-photos') continue;

                // Converte idade para número
                if (key === 'idade') {
                    petData[key] = parseInt(value, 10);
                }
                // Mapeia o ID da organização para 'organizacaoId'
                else if (key === 'organizacaoId') {
                    if (value && value !== "") {
                        petData.organizacaoId = parseInt(value, 10);
                    } else {
                        petData.organizacaoId = null;
                    }
                }
                // Campos checkbox: se estiverem no formData, significa que foram marcados
                else if (checkboxFields.includes(key)) {
                    petData[key] = true;
                }
                // Outros campos de texto e selects
                else {
                    petData[key] = value;
                }
            }

            // Se for ONG e o campo `organizacaoId` estiver desabilitado, pegamos o ID da ONG do localStorage
            if (currentRole === "ROLE_ONG" && petShelterSelect.hasAttribute('disabled')) {
                const userOrgId = localStorage.getItem('userOrganizacaoId');
                if (userOrgId) {
                    petData.organizacaoId = parseInt(userOrgId, 10);
                } else {
                    alert("Erro: ID da organização do usuário não encontrado no armazenamento local.");
                    return;
                }
            }

            // Validação final da organizacaoId antes de enviar (se for um campo visível e editável)
            // Se o campo de seleção de organização estiver visível E não estiver desabilitado E não tiver um valor válido
            if (petShelterSelect.style.display !== 'none' && !petShelterSelect.hasAttribute('disabled') && (!petData.organizacaoId || isNaN(petData.organizacaoId))) {
                alert("Por favor, selecione uma ONG/Tutor para o pet.");
                return; // Impede o envio do formulário
            }

            // Verifica se o pet já tem uma imagem e a mantém se não houver novo upload
            let imageUrl = '';
            const existingImageUrl = petImageInput.dataset.existingImageUrl;
            if (petImageInput.files.length === 0 && existingImageUrl) {
                // Já está salvo no dataset.existingImageUrl apenas o nome do arquivo.
                imageUrl = existingImageUrl;
            }

            // Processa o upload da imagem SOMENTE se um novo arquivo foi selecionado
            if (petImageInput.files.length > 0) {
                const file = petImageInput.files[0];
                const fd = new FormData();
                fd.append("file", file);

                try {
                    const token = localStorage.getItem('accessToken');
                    const res = await fetch(`${API_BASE_URL}/pets/upload-image`, {
                        method: "POST",
                        headers: {
                            "Authorization": `Bearer ${token}`
                        },
                        body: fd
                    });
                    if (!res.ok) {
                        const errorText = await res.text();
                        throw new Error(`Erro no upload da imagem: ${res.status} - ${errorText}`);
                    }
                    // O backend retorna a URL completa, precisamos extrair apenas o nome do arquivo
                    const fullImageUrl = await res.text();
                    imageUrl = fullImageUrl.split('/').pop();
                } catch (err) {
                    console.error("Erro no upload da imagem:", err);
                    alert(`Erro no upload da imagem: ${err.message}`);
                    return;
                }
            }
            petData.imageUrl = imageUrl;
            if (imageUrl) {
            petData.fotosUrls = [imageUrl]; // Se o backend espera um array de strings
            } else {
            petData.fotosUrls = []; // Garante que seja um array vazio se não houver imagem
        }

            console.log("Pet Data final para envio:", petData);
            await sendPetToBackend(petData, method, petId);
        });
    }


    // Aplicação de Filtros Avançados
    if (advancedFiltersForm) {
        advancedFiltersForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const filterData = {};
            advancedFiltersForm.querySelectorAll('select').forEach(select => {
                if (select.value !== "All" && select.value !== "") {
                    // Os `name` dos selects do filtro já correspondem aos nomes do backend
                    // O `name="organizacaoId"` já está correto
                    if (select.name === 'organizacaoId') {
                        filterData[select.name] = parseInt(select.value, 10);
                    } else if (['vacinado', 'castrado', 'microchipado'].includes(select.name)) {
                        filterData[select.name] = select.value === 'true'; // Converte para boolean
                    } else { // Para statusAdocao, especie, sexo, porte, cor, pelagem
                        filterData[select.name] = select.value;
                    }
                }
            });

            const query = petSearchInput.value.trim();
            if (query) {
                filterData.search = query;
            }

            console.log("Aplicando filtros:", filterData);
            await fetchPets(filterData);
            closeModal(filterModal);
        });
    }

    // Busca Reativa e por Botão
    function applySearchAndFilters() {
        const query = petSearchInput.value.trim();

        const currentAdvancedFilters = {};
        if (advancedFiltersForm) {
            advancedFiltersForm.querySelectorAll('select').forEach(select => {
                if (select.value !== "All" && select.value !== "") {
                    // A mesma lógica de mapeamento dos filtros deve ser aplicada aqui
                    if (select.name === 'organizacaoId') {
                        currentAdvancedFilters[select.name] = parseInt(select.value, 10);
                    } else if (['vacinado', 'castrado', 'microchipado'].includes(select.name)) {
                        currentAdvancedFilters[select.name] = select.value === 'true'; // Converte para boolean
                    } else { // Para statusAdocao, especie, sexo, porte, cor, pelagem
                        currentAdvancedFilters[select.name] = select.value;
                    }
                }
            });
        }

        const combinedFilters = { ...currentAdvancedFilters };
        if (query) {
            combinedFilters.search = query;
        }

        fetchPets(combinedFilters);
    }

    if (petSearchBtn) {
        petSearchBtn.addEventListener("click", (e) => {
            e.preventDefault();
            applySearchAndFilters();
        });
    }

    if (petSearchInput) {
        let searchTimeout;
        petSearchInput.addEventListener("input", () => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                applySearchAndFilters();
            }, 300);
        });
    }
    //Fechar modal de Mensagem de Sucesso
if (closeSuccessMessageModalBtn) {
    closeSuccessMessageModalBtn.addEventListener("click", () => closeModal(successMessageModal));
}
window.addEventListener("click", (e) => {
    if (e.target === successMessageModal) closeModal(successMessageModal);
});
document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeModal(successMessageModal);
});

//Botão "Ver Meus Interesses" no modal de sucesso
if (goToInterestsPageBtn) {
    goToInterestsPageBtn.addEventListener("click", () => {
        closeModal(successMessageModal);
        window.location.href = 'GestaoInteresseAdocao.html'; 
    });
}
});