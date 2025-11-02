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

// NOVO: Seletores para o modal de detalhes
const petDetailsModal = document.getElementById("petDetailsModal"); // O SEU NOVO MODAL DE DETALHES
const closePetDetailsModalBtn = document.getElementById("closePetDetailsModal");
const detailsModalTitle = document.getElementById("detailsModalTitle"); // Título do modal de detalhes (NOVO)
const detailsPetImage = document.getElementById("details-pet-image");
const detailPetName = document.getElementById("detail-pet-name"); // Corrigido para detail-pet-name
const detailPetSpecies = document.getElementById("detail-pet-species");
const detailPetBreed = document.getElementById("detail-pet-race"); // Corrigido para detail-pet-race (seu HTML)
const detailPetAge = document.getElementById("detail-pet-age");
const detailPetGender = document.getElementById("detail-pet-gender");
const detailPetSize = document.getElementById("detail-pet-port"); // Corrigido para detail-pet-port (seu HTML)
const detailPetColor = document.getElementById("detail-pet-color");
const detailPetCoat = document.getElementById("detail-pet-coat");
const detailPetVaccinated = document.getElementById("detail-pet-vacinado"); // Corrigido para detail-pet-vacinado
const detailPetNeutered = document.getElementById("detail-pet-castrado"); // Corrigido para detail-pet-castrado
const detailPetMicrochipped = document.getElementById("detail-pet-microchipado"); // Corrigido para detail-pet-microchipado
const detailPetStatus = document.getElementById("detail-pet-status");
const detailPetShelter = document.getElementById("detail-pet-shelter");
const detailPetCity = document.getElementById("detail-pet-city");
const detailPetState = document.getElementById("detail-pet-state");
const closeDetailsModalBtn = document.getElementById("closeDetailsModalBtn"); // Botão "Fechar" dentro do modal de detalhes


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
const API_BASE_URL = "http://localhost:8080/api"; // URL base da sua API

// Arrays para guardar os pets e organizações
let allPets = []; // Todos os pets carregados do backend
let allOrganizations = []; // Todas as organizações carregadas do backend
let currentRole = "ROLE_ADOTANTE"; // Role padrão, será atualizada pelo JS

// ===========================================
// FUNÇÕES AUXILIARES
// ===========================================

/**
 * Função genérica para abrir um modal.
 * @param {HTMLElement} modalElement O elemento DOM do modal.
 */
function openModal(modalElement) {
    modalElement.style.display = "flex"; // Usa flex para centralizar
    document.body.style.overflow = "hidden"; // Impede o scroll da página
}

/**
 * Função genérica para fechar um modal.
 * @param {HTMLElement} modalElement O elemento DOM do modal.
 */
function closeModal(modalElement) {
    modalElement.style.display = "none";
    document.body.style.overflow = "auto"; // Restaura o scroll da página
}

/**
 * Limpa o formulário de adicionar/editar pet.
 */
function clearAddEditPetForm() {
    addEditPetForm.reset(); // Reseta todos os campos do formulário
    petIdInput.value = ''; // Garante que o ID hidden seja limpo
    photosPreview.innerHTML = '<span>Nenhuma imagem selecionada.</span>'; // Limpa a pré-visualização de fotos com placeholder
    photosPreview.classList.remove('has-images'); // Remove a classe de indicação de imagens
    submitPetBtn.textContent = "Adicionar Pet"; // Volta o texto do botão para "Adicionar Pet"
    modalTitle.textContent = "Adicionar Novo Pet"; // Volta o título do modal
    delete petImageInput.dataset.existingImageUrl; // Limpa a URL da imagem existente
    // Garante que os checkboxes voltem ao estado padrão (desmarcados, ou como definido pelo reset())
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

/**
 * Exibe as miniaturas das imagens selecionadas no input de arquivo.
 * @param {FileList} files Os arquivos de imagem selecionados.
 */
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

/**
 * Mapeia o valor do Enum `StatusAdocao` para um texto amigável.
 * @param {string} status Enum `StatusAdocao` (ex: "EM_ADOCAO").
 * @returns {string} Texto amigável (ex: "Em Adoção Temporária").
*/
function mapStatusAdocaoToFriendlyText(status) {
    switch (status) {
        case "EM_ADOCAO":
            return "Em Adoção Temporária";
        case "ADOTADO":
            return "Adotado";
        case "RESERVADO":
            return "Reservado";
        case "INDISPONIVEL":
            return "Indisponível para Adoção";
        case "DISPONIVEL": // Novo status
            return "Disponível para Adoção";
        default:
            return "Não informado";
    }
}

/**
 * Mapeia valores booleanos para texto amigável "Sim" / "Não".
 * @param {boolean} value Valor booleano.
 * @returns {string} "Sim" ou "Não".
 */
function mapBooleanToText(value) {
    return value ? 'Sim' : 'Não';
}

// ===========================================
// LÓGICA DE ROLES E PERMISSÕES
// ===========================================

/**
 * Obtém a role do usuário logado do localStorage.
 * Agora lida com o nome da chave 'userRoles' e o formato de array JSON.
 * @returns {string} A role principal do usuário (ex: "ROLE_ADOTANTE", "ROLE_ONG", "ROLE_ADMIN").
 */
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

/**
 * Ajusta a visibilidade dos elementos da UI com base na role do usuário.
 * Esta função agora lida com os campos do formulário de adicionar/editar Pet
 * E a visibilidade dos botões de ação nos cards.
 */
function applyRolePermissions() {
    currentRole = getUserRole(); // Atualiza a role
    console.log("Role do usuário logado:", currentRole);

    const isOngOrAdmin = currentRole === "ROLE_ONG" || currentRole === "ROLE_ADMIN";
    const isAdmin = currentRole === "ROLE_ADMIN";

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
            const userOrgId = localStorage.getItem('userOrganizationId'); // Assumindo que você salva o ID da ONG
            if (userOrgId && petShelterSelect.options.length > 1) { // Verifica se as opções já foram carregadas
                petShelterSelect.value = userOrgId;
                petShelterSelect.setAttribute('disabled', 'true');
            } else if (!userOrgId) {
                console.warn("userOrganizationId não encontrado no localStorage para ROLE_ONG.");
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

    // Desabilita campos para ADOTANTE no modal de adicionar/editar se ele abrir
    // Esta parte agora é mais específica para o modo "somente leitura"
    // e é chamada pela `fillAddEditPetForm` e `openAddPetModalBtn`
}


// ===========================================
// GESTÃO DE ORGANIZAÇÕES
// ===========================================

/**
 * Busca todas as organizações do backend e popula os selects.
 */
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

/**
 * Popula os elementos <select> com as organizações.
 * @param {Array<Object>} organizations Lista de objetos de organização { id: ..., nome: ... }.
 */
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

/**
 * Cria e retorna um elemento de card de pet.
 * @param {Object} pet Objeto com os dados do pet.
 * @returns {HTMLElement} O elemento HTML do card do pet.
 */
function createPetCard(pet) {
    const card = document.createElement("article");
    card.classList.add("pets-card");
    card.setAttribute("data-pet-id", pet.id); // Renomeado para data-pet-id para evitar conflitos

    const imageUrl = pet.imageUrl
        ? (pet.imageUrl.startsWith('http') ? pet.imageUrl : `http://localhost:8080/uploads/${pet.imageUrl}`)
        : '../src/assets/imgs/placeholder.jpg'; // Caminho para o placeholder local (ajustado para src/assets)

    // Encontra o nome da organização pelo ID
    const organizationName = allOrganizations.find(org => org.id === pet.organizacaoId)?.nomeFantasia || 'Não Informado';

    card.innerHTML = `
        <img src="${imageUrl}" alt="Foto de ${pet.nome}" class="pet-photo">
        <div class="pet-info">
            <h3 class="pet-name">${pet.nome}</h3>
            <p class="pet-breed">Raça: ${pet.raca || "Não informada"}</p>
            <p class="pet-age">Idade: ${pet.idade} ${pet.idade > 1 ? 'anos' : 'ano'}</p>
            <p class="pet-gender">Sexo: ${pet.sexo === 'M' ? 'Macho' : 'Fêmea'}</p>
            <p class="pet-status">Status: ${mapStatusAdocaoToFriendlyText(pet.statusAdocao)}</p>
            <p class="pet-organization">ONG/Tutor: ${organizationName}</p>
            <div class="pet-actions">
                <button class="view-details primary-btn"><i class="fas fa-info-circle"></i> Ver detalhes</button>
                <button class="pet-edit secondary-btn"><i class="fas fa-edit"></i> Editar</button>
                <button class="pet-delete logout-btn"><i class="fas fa-trash-alt"></i> Excluir</button>
            </div>
        </div>
    `;

    // Ações de visibilidade dos botões
    const btnDetails = card.querySelector(".view-details");
    const btnEdit = card.querySelector(".pet-edit");
    const btnDelete = card.querySelector(".pet-delete");

    const isOngOrAdmin = currentRole === "ROLE_ONG" || currentRole === "ROLE_ADMIN";
    
    // Esconder/mostrar botões de editar/excluir baseados na role
    if (btnEdit) btnEdit.style.display = isOngOrAdmin ? "flex" : "none";
    if (btnDelete) btnDelete.style.display = isOngOrAdmin ? "flex" : "none";

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

    return card;
}

/**
 * Renderiza a lista de pets no container.
 * @param {Array<Object>} petsToRender A lista de pets a serem exibidos.
 */
function renderPets(petsToRender) {
    petsContainer.innerHTML = ""; // Limpa os cards existentes
    if (petsToRender.length === 0) {
        noPetsMessage.style.display = "block";
    } else {
        noPetsMessage.style.display = "none";
        petsToRender.forEach((pet) => petsContainer.appendChild(createPetCard(pet)));
    }
}

/**
 * Preenche o formulário do modal de adicionar/editar com os dados de um pet.
 * @param {Object} pet O objeto pet para preencher o formulário.
 */
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
    document.getElementById("pet-breed").value = pet.raca || ''; // Corrigido para `pet-breed`
    document.getElementById("pet-age").value = pet.idade || '';
    document.getElementById("pet-gender").value = pet.sexo || '';
    document.getElementById("pet-size").value = pet.porte || ''; // Corrigido para `pet-size`
    document.getElementById("pet-color").value = pet.cor || '';
    document.getElementById("pet-coat").value = pet.pelagem || '';
    
    // Campos booleanos (checkboxes)
    document.getElementById("pet-vaccinated").checked = pet.vacinado || false;
    document.getElementById("pet-neutered").checked = pet.castrado || false;
    document.getElementById("pet-microchipped").checked = pet.microchipado || false;
    
    document.getElementById("pet-availability").value = pet.statusAdocao || '';

    // Preenche o select de organização com o ID da organização do pet
    if (pet.organizacaoId) {
        petShelterSelect.value = pet.organizacaoId;
    } else {
        petShelterSelect.value = ""; // Nenhuma organização selecionada
    }
    
    document.getElementById("pet-city").value = pet.cidade || '';
    document.getElementById("pet-state").value = pet.estado || '';

    // Se tiver uma imagem já, exibe a prévia
    photosPreview.innerHTML = "";
    if (pet.imageUrl) {
        photosPreview.classList.add('has-images');
        const img = document.createElement("img");
        img.src = pet.imageUrl.startsWith('http') ? pet.imageUrl : `http://localhost:8080/uploads/${pet.imageUrl}`;
        img.alt = `Foto de ${pet.nome}`;
        photosPreview.appendChild(img);
        // Salva a URL da imagem existente para não perdê-la em caso de não upload de nova imagem
        petImageInput.dataset.existingImageUrl = pet.imageUrl;
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

/**
 * Preenche e exibe o modal de detalhes do pet.
 * @param {Object} pet O objeto pet para exibir os detalhes.
 */
function displayPetDetails(pet) {
    // Título do modal de detalhes
    detailsModalTitle.textContent = `Detalhes de ${pet.nome}`; 

    // Imagem do pet
    detailsPetImage.src = pet.imageUrl
        ? (pet.imageUrl.startsWith('http') ? pet.imageUrl : `http://localhost:8080/uploads/${pet.imageUrl}`)
        : '../src/assets/imgs/placeholder.jpg'; // Corrigido o caminho do placeholder
    detailsPetImage.alt = `Foto de ${pet.nome}`;

    // Preenche os campos de detalhes usando os seletores corrigidos
    detailPetName.textContent = pet.nome || 'Não informado';
    detailPetSpecies.textContent = pet.especie || 'Não informado';
    detailPetBreed.textContent = pet.raca || 'Não informada'; // Mapeia `pet.raca` para `detail-pet-race`
    detailPetAge.textContent = (pet.idade !== undefined && pet.idade !== null) ? `${pet.idade} ${pet.idade > 1 ? 'anos' : 'ano'}` : 'Não informado';
    detailPetGender.textContent = (pet.sexo === 'M' ? 'Macho' : (pet.sexo === 'F' ? 'Fêmea' : 'Não informado'));
    detailPetSize.textContent = pet.porte || 'Não informado'; // Mapeia `pet.porte` para `detail-pet-port`
    detailPetColor.textContent = pet.cor || 'Não informada';
    detailPetCoat.textContent = pet.pelagem || 'Não informada';
    
    detailPetVaccinated.textContent = mapBooleanToText(pet.vacinado); // Mapeia `pet.vacinado` para `detail-pet-vacinado`
    detailPetNeutered.textContent = mapBooleanToText(pet.castrado); // Mapeia `pet.castrado` para `detail-pet-castrado`
    detailPetMicrochipped.textContent = mapBooleanToText(pet.microchipado); // Mapeia `pet.microchipado` para `detail-pet-microchipado`
    
    detailPetStatus.textContent = mapStatusAdocaoToFriendlyText(pet.statusAdocao);
    
    const organization = allOrganizations.find(org => org.id === pet.organizacaoId);
    detailPetShelter.textContent = organization ? organization.nomeFantasia : 'Não informado';
    
    detailPetCity.textContent = pet.cidade || 'Não informado';
    detailPetState.textContent = pet.estado || 'Não informado';

    openModal(petDetailsModal); // Abre o modal de detalhes
}


/**
 * Busca os pets do backend, aplica filtros e renderiza.
 * @param {Object} [filters={}] Objeto com os filtros a serem aplicados.
 */
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
                // Opcional: window.location.href = '/login_screen.html'; // Redireciona
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

/**
 * Envia um pet para o backend (cadastro ou edição).
 * @param {Object} petData Os dados do pet.
 * @param {string} method O método HTTP ('POST' para cadastro, 'PUT' para edição).
 * @param {number} [petId=null] O ID do pet se for uma edição.
 */
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

/**
 * Exclui um pet do backend.
 * @param {number} petId O ID do pet a ser excluído.
 */
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


// ===========================================
// EVENT LISTENERS GERAIS
// ===========================================

document.addEventListener("DOMContentLoaded", async () => { 
    console.log("Script pet-control.js carregado!");

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

    // NOVO: Fechar modal de Detalhes do Pet
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

            // Mapeia os dados do formulário para o petData usando os `name`s dos inputs
            // Os `name` dos inputs no seu HTML são:
            // nome, especie, raca, idade, sexo, porte, cor, pelagem, vacinado, castrado, microchipado,
            // statusAdocao, organizacaoId, cidade, estado
            
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
                const userOrgId = localStorage.getItem('userOrganizationId');
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
                // Remove a URL base para enviar apenas o nome do arquivo para o backend
                // Assumindo que o backend espera apenas o nome do arquivo, não a URL completa
                imageUrl = existingImageUrl.replace('http://localhost:8080/uploads/', ''); 
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
                    imageUrl = await res.text(); // A API deve retornar o nome do arquivo ou a URL relativa
                    alert("Imagem enviada com sucesso!");
                } catch (err) {
                    console.error("Erro no upload da imagem:", err);
                    alert(`Erro no upload da imagem: ${err.message}`);
                    return; 
                }
            }
            petData.imageUrl = imageUrl; 

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
});