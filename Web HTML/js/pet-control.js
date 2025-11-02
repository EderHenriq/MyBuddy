// ===========================================
// SELETORES E VARIÁVEIS GLOBAIS
// ===========================================

// Modais
const addEditPetModal = document.getElementById("addEditPetModal");
const closeAddEditPetModalBtn = document.getElementById("closeAddEditPetModal");
const openAddPetModalBtn = document.getElementById("openAddPetModalBtn"); // Botão "Adicionar novo Pet"
const modalTitle = document.getElementById("modalTitle");
const addEditPetForm = document.getElementById("addEditPetForm");
const submitPetBtn = document.getElementById("submitPetBtn");
const petIdInput = document.getElementById("pet-id"); // Campo hidden para ID do pet

const filterModal = document.getElementById("filterModal");
const closeFilterModalBtn = document.getElementById("closeFilterModal");
const openFilterModalBtn = document.getElementById("openFilterModalBtn"); // Botão "Mostrar Filtros"
const advancedFiltersForm = document.getElementById("advancedFiltersForm");

// Seção de Pets
const petsContainer = document.querySelector(".pets-cards-wrapper");
const petSearchInput = document.querySelector(".search-input");
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
const petShelterSelect = document.getElementById("pet-shelter"); // NOVO: Seleciona o select de ONG/Tutor no formulário de pet
const filterShelterSelect = document.getElementById("filter-shelter"); // NOVO: Seleciona o select de ONG/Tutor nos filtros

// Backend URL
const API_BASE_URL = "http://localhost:8080/api"; // URL base da sua API

// Arrays para guardar os pets e organizações
let allPets = []; // Todos os pets carregados do backend
let allOrganizations = []; // NOVO: Todas as organizações carregadas do backend
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
    photosPreview.innerHTML = ""; // Limpa a pré-visualização de fotos
    photosPreview.classList.remove('has-images'); // Remove a classe de indicação de imagens
    submitPetBtn.textContent = "Adicionar Pet"; // Volta o texto do botão para "Adicionar Pet"
    modalTitle.textContent = "Adicionar Novo Pet"; // Volta o título do modal
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
        // Você pode adicionar um placeholder de texto aqui se desejar
        // photosPreview.textContent = "Nenhuma imagem selecionada.";
    }
}

/**
 * Converte um objeto FormData para um objeto JSON.
 * @param {FormData} formData O objeto FormData do formulário.
 * @returns {Object} O objeto JSON.
 */
function formDataToJson(formData) {
    const obj = {};
    for (let [key, value] of formData.entries()) {
        obj[key] = value;
    }
    return obj;
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

        // NOVO: Se for ONG, pré-seleciona a própria ONG e desabilita o campo.
        if (currentRole === "ROLE_ONG") {
            const userOrgId = localStorage.getItem('userOrganizationId'); // Assumindo que você salva o ID da ONG
            if (userOrgId) {
                petShelterSelect.value = userOrgId;
                petShelterSelect.setAttribute('disabled', 'true');
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
    if (currentRole === "ROLE_ADOTANTE") {
        addEditPetForm.querySelectorAll('input:not(#pet-id), select, textarea').forEach(field => {
            field.setAttribute('disabled', 'true');
        });
        if (submitPetBtn) submitPetBtn.style.display = 'none';
        if (petImageInput) petImageInput.style.display = 'none';
    } else {
        addEditPetForm.querySelectorAll('input, select, textarea').forEach(field => {
            field.removeAttribute('disabled');
        });
        if (submitPetBtn) submitPetBtn.style.display = 'block';
        if (petImageInput) petImageInput.style.display = 'block';
    }
}


// ===========================================
// GESTÃO DE ORGANIZAÇÕES (NOVA SEÇÃO)
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
            option.textContent = org.nomeFantasia;;
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
            option.textContent = org.nomeFantasia;;
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
    card.setAttribute("data-id", pet.id); // Adiciona o ID do pet ao card

    const imageUrl = pet.imageUrl
        ? (pet.imageUrl.startsWith('http') ? pet.imageUrl : `http://localhost:8080/uploads/${pet.imageUrl}`)
        : '../assets/imgs/placeholder.jpg'; // Caminho para o placeholder local

    // NOVO: Encontra o nome da organização pelo ID
    const organizationName = allOrganizations.find(org => org.id === pet.organizacaoId)?.nomeFantasia || 'Não Informado';

    card.innerHTML = `
        <img src="${imageUrl}" alt="Foto de ${pet.nome}" class="pet-photo">
        <div class="pet-info">
            <h3 class="pet-name">${pet.nome}</h3>
            <p class="pet-breed">Raça: ${pet.raca || "Não informada"}</p>
            <p class="pet-age">Idade: ${pet.idade} ${pet.idade > 1 ? 'anos' : 'ano'}</p>
            <p class="pet-gender">Sexo: ${pet.sexo === 'M' ? 'Macho' : 'Fêmea'}</p>
            <p class="pet-status">Status: ${mapStatusAdocaoToFriendlyText(pet.statusAdocao)}</p>
            <p class="pet-organization">ONG/Tutor: ${organizationName}</p>             <div class="pet-actions">
                <button class="view-details primary-btn"><i class="fas fa-info-circle"></i> Ver detalhes</button>
                <button class="pet-edit secondary-btn"><i class="fas fa-edit"></i> Editar</button>
                <button class="pet-delete logout-btn"><i class="fas fa-trash-alt"></i> Excluir</button>
            </div>
        </div>
    `;

    // Adiciona event listeners aos botões
    const btnDetails = card.querySelector(".view-details");
    btnDetails.addEventListener("click", (e) => {
        e.stopPropagation();
        fillAddEditPetForm(pet, true); // true para modo somente leitura
        openModal(addEditPetModal);
    });

    const btnEdit = card.querySelector(".pet-edit");
    if (btnEdit) { // Botão só existe se a role permitir
        btnEdit.addEventListener("click", (e) => {
            e.stopPropagation();
            modalTitle.textContent = "Editar Pet";
            submitPetBtn.textContent = "Salvar Alterações";
            fillAddEditPetForm(pet);
            openModal(addEditPetModal);
        });
    }

    const btnDelete = card.querySelector(".pet-delete");
    if (btnDelete) { // Botão só existe se a role permitir
        btnDelete.addEventListener("click", (e) => {
            e.stopPropagation();
            if (confirm(`Tem certeza que deseja excluir o pet ${pet.nome}?`)) {
                deletePet(pet.id);
            }
        });
    }

    // Esconder/mostrar botões de editar/excluir baseados na role
    const isOngOrAdmin = currentRole === "ROLE_ONG" || currentRole === "ROLE_ADMIN";
    if (btnEdit) btnEdit.style.display = isOngOrAdmin ? "flex" : "none";
    if (btnDelete) btnDelete.style.display = isOngOrAdmin ? "flex" : "none";

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
 * @param {boolean} readOnly Se deve preencher o formulário em modo somente leitura.
 */
function fillAddEditPetForm(pet, readOnly = false) {
    clearAddEditPetForm(); // Limpa antes de preencher
    if (!pet) return;

    petIdInput.value = pet.id || '';
    document.getElementById("pet-name").value = pet.nome || '';
    document.getElementById("pet-species").value = pet.especie || '';
    document.getElementById("pet-breed").value = pet.raca || '';
    document.getElementById("pet-age").value = pet.idade || '';
    document.getElementById("pet-gender").value = pet.sexo || '';
    document.getElementById("pet-size").value = pet.porte || '';
    document.getElementById("pet-color").value = pet.cor || '';
    document.getElementById("pet-coat").value = pet.pelagem || '';
    // Corrigido: Agora usa 'true'/'false' em vez de 'Yes'/'No'
    document.getElementById("pet-vaccinated").value = pet.vacinado ? 'true' : 'false';
    document.getElementById("pet-neutered").value = pet.castrado ? 'true' : 'false';
    document.getElementById("pet-microchipped").value = pet.microchipado ? 'true' : 'false';
    document.getElementById("pet-availability").value = pet.statusAdocao || ''; // O Enum do backend já é o valor correto

    // NOVO: Preenche o select de organização com o ID da organização do pet
    if (pet.organizacaoId) {
        petShelterSelect.value = pet.organizacaoId;
    } else {
        petShelterSelect.value = ""; // Nenhuma organização selecionada
    }
    
    document.getElementById("pet-city").value = pet.cidade || '';
    document.getElementById("pet-state").value = pet.estado || '';

    // Se tiver uma imagem já, exibe a prévia
    if (pet.imageUrl) {
        photosPreview.innerHTML = "";
        photosPreview.classList.add('has-images');
        const img = document.createElement("img");
        img.src = pet.imageUrl.startsWith('http') ? pet.imageUrl : `http://localhost:8080/uploads/${pet.imageUrl}`;
        img.alt = "Foto atual do pet";
        photosPreview.appendChild(img);
        // Salva a URL da imagem existente para não perdê-la em caso de não upload de nova imagem
        petImageInput.dataset.existingImageUrl = pet.imageUrl;
    } else {
        delete petImageInput.dataset.existingImageUrl; // Remove a URL se não houver imagem
    }


    // Modo somente leitura
    addEditPetForm.querySelectorAll('input:not(#pet-id), select, textarea').forEach(field => {
        if (readOnly) {
            field.setAttribute('disabled', 'true');
        } else {
            field.removeAttribute('disabled');
        }
    });
    if (submitPetBtn) submitPetBtn.style.display = readOnly ? 'none' : 'block';
    if (petImageInput) petImageInput.style.display = readOnly ? 'none' : 'block';
    if (modalTitle) modalTitle.textContent = readOnly ? `Detalhes de ${pet.nome}` : "Editar Pet";

    // Re-aplica as permissões de role para o select de organização, caso ele tenha sido desabilitado
    applyRolePermissions();
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

document.addEventListener("DOMContentLoaded", async () => { // Adicionado 'async'
    console.log("Script pet-control.js carregado!");

    // Carrega as organizações primeiro, pois são necessárias para os selects e para renderizar os pets
   await fetchOrganizations();
    applyRolePermissions();
    fetchPets();

    // Abrir modal de Adicionar/Editar Pet
    if (openAddPetModalBtn) {
        openAddPetModalBtn.addEventListener("click", () => {
            clearAddEditPetForm(); 
            modalTitle.textContent = "Adicionar Novo Pet";
            submitPetBtn.textContent = "Adicionar Pet";
            addEditPetForm.querySelectorAll('input, select, textarea').forEach(field => {
                field.removeAttribute('disabled');
            });
            petImageInput.style.display = 'block'; 
            submitPetBtn.style.display = 'block'; 
            openModal(addEditPetModal);
            applyRolePermissions(); // Re-aplica permissões para configurar o select de ONG
        });
    }

    // Fechar modal de Adicionar/Editar Pet
    if (closeAddEditPetModalBtn) {
        closeAddEditPetModalBtn.addEventListener("click", () => closeModal(addEditPetModal));
    }
    window.addEventListener("click", (e) => {
        if (e.target === addEditPetModal) closeModal(addEditPetModal);
    });
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") closeModal(addEditPetModal);
    });


    // Abrir modal de Filtros
    if (openFilterModalBtn) {
        openFilterModalBtn.addEventListener("click", () => openModal(filterModal));
    }

    // Fechar modal de Filtros
    if (closeFilterModalBtn) {
        closeFilterModalBtn.addEventListener("click", () => closeModal(filterModal));
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

            // Mapeia os dados do formulário para o petData
            for (let [key, value] of formData.entries()) {
                // Ignora o campo de upload de arquivo aqui, pois será tratado separadamente
                if (key === 'pet-photos') continue; 
                
                // Converte campos booleanos
                if (key === 'vacinado' || key === 'castrado' || key === 'microchipado') {
                    petData[key] = value === 'true';
                } 
                // Converte idade para número
                else if (key === 'idade') {
                    petData[key] = parseInt(value, 10);
                } 
                // Mapeia o ID da organização para 'organizacaoId'
                else if (key === 'pet-shelter') { // O 'name' do seu select de ONG deve ser 'pet-shelter'
                    if (value && value !== "") {
                        petData.organizacaoId = parseInt(value, 10);
                    } else {
                        petData.organizacaoId = null; // Ou trate como erro se for obrigatório
                    }
                }
                // Outros campos diretamente
                else {
                    petData[key] = value;
                }
            }

// Pega o ID da organização do campo selecionado
// Se o campo estiver desabilitado (para ROLE_ONG), o FormData não o incluirá.
// Precisamos pegá-lo diretamente.
let selectedOrgId = petShelterSelect.value; 

// Se for ONG, e o campo estiver desabilitado, pegamos o ID da ONG do localStorage
if (currentRole === "ROLE_ONG" && petShelterSelect.hasAttribute('disabled')) {
                const userOrgId = localStorage.getItem('userOrganizationId');
                if (userOrgId) {
                    petData.organizacaoId = parseInt(userOrgId, 10);
                } else {
                    alert("Erro: ID da organização do usuário não encontrado no armazenamento local.");
                    return;
                }
            }
            // Validação final da organizaçãoId antes de enviar
            if (!petData.organizacaoId) {
                alert("Por favor, selecione uma ONG/Tutor para o pet.");
                return; // Impede o envio do formulário
            }


            // Corrigido: Mapeamento de 'true'/'false' para booleanos para o backend
            petData.vacinado = petData.vacinado === 'true';
            petData.castrado = petData.castrado === 'true';
            petData.microchipado = petData.microchipado === 'true';
            
            // Certifique-se de que idade seja um número
            petData.idade = parseInt(petData.idade, 10);
            
            // NOVO: O campo pet-shelter agora tem name="organizacaoId" e seu value já é o ID.
            // Certifica-se de que é um número.
            if (petData.organizacaoId) {
                petData.organizacaoId = parseInt(petData.organizacaoId, 10);
            } else {
                // Se não houver organização selecionada, ou se for string vazia, pode enviar null
                petData.organizacaoId = null;
            }


            let imageUrl = ''; // Inicializa vazio para garantir que só adicione se houver upload ou pet existente

            // Verifica se o pet já tem uma imagem e a mantém se não houver novo upload
            const existingImageUrl = petImageInput.dataset.existingImageUrl; // Pega do dataset
            if (petImageInput.files.length === 0 && existingImageUrl) {
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
                    imageUrl = await res.text(); 
                    alert("Imagem enviada com sucesso!");
                } catch (err) {
                    console.error("Erro no upload da imagem:", err);
                    alert(`Erro no upload da imagem: ${err.message}`);
                    return; 
                }
            }
            petData.imageUrl = imageUrl; 

            // Remove o campo 'pet-photos' que não é parte do modelo do pet
            delete petData['pet-photos'];

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
                    // No caso dos booleanos, o select.value já será 'true' ou 'false' (string)
                    // para o backend de filtro, isso pode ser interpretado como booleano ou string.
                    // Para query params, é comum enviar como string. O Spring deve converter.
                    // NOVO: Converte para número se for organizacaoId
                    if (select.name === 'organizacaoId') {
                        filterData[select.name] = parseInt(select.value, 10);
                    } else {
                        filterData[select.name || select.id.replace('filter-', '')] = select.value;
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
                    // NOVO: Converte para número se for organizacaoId
                    if (select.name === 'organizacaoId') {
                        currentAdvancedFilters[select.name] = parseInt(select.value, 10);
                    } else {
                        currentAdvancedFilters[select.name || select.id.replace('filter-', '')] = select.value;
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