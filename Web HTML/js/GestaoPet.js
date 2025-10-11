// Web HTML/js/GestaoPet.js

document.addEventListener('DOMContentLoaded', () => {
    console.log('Script GestaoPet.js carregado!');

    const petsContainer = document.getElementById('petsContainer');
    const noPetsMessage = document.getElementById('noPetsMessage');
    const addPetBtn = document.getElementById('addPetBtn'); 

    // Elementos do Modal de Cadastro
    const petRegisterModal = document.getElementById('petRegisterModal');
    const closeRegisterModal = petRegisterModal.querySelector('.close-button');
    const petRegisterForm = document.getElementById('petRegisterForm');
    const registerMessage = document.getElementById('registerMessage');
    const petImageInput = document.getElementById('petImage'); // NOVO: Campo de input da imagem

    // Elementos do Modal de Detalhes do Pet
    const petDetailsModal = document.getElementById('petDetailsModal');
    const closeDetailsModal = document.getElementById('closeDetailsModal');
    const detailsPetImage = document.getElementById('detailsPetImage');
    const detailsPetName = document.getElementById('detailsPetName');
    const detailsPetSpecies = document.getElementById('detailsPetSpecies');
    const detailsPetBreed = document.getElementById('detailsPetBreed');
    const detailsPetAge = document.getElementById('detailsPetAge');
    const detailsPetSize = document.getElementById('detailsPetSize');
    const detailsPetColor = document.getElementById('detailsPetColor');
    const detailsPetGender = document.getElementById('detailsPetGender');
    const interestBtn = document.getElementById('interestBtn');

    // Filtros e Busca
    const petSearchInput = document.getElementById('petSearchInput');
    const speciesFilter = document.getElementById('speciesFilter');
    const sizeFilter = document.getElementById('sizeFilter');
    const genderFilter = document.getElementById('genderFilter');
    const colorFilter = document.getElementById('colorFilter');

    // --- Funções do Modal de Cadastro ---

    function openRegisterModal() {
        petRegisterModal.style.display = 'flex';
        petRegisterForm.reset();
        registerMessage.style.display = 'none';
        petImageInput.value = ''; // Limpa o input de arquivo também
    }

    function closeRegisterModalFunc() {
        petRegisterModal.style.display = 'none';
    }

    // --- Funções do Modal de Detalhes ---

    function openDetailsModal(pet) {
        // Usa a URL da imagem do pet, ou um placeholder se não houver
        detailsPetImage.src = pet.imageUrl || 'https://via.placeholder.com/300x300?text=Foto+do+Pet';
        detailsPetName.textContent = pet.nome;
        detailsPetSpecies.textContent = pet.especie;
        detailsPetBreed.textContent = pet.raca || 'Não informada';
        detailsPetAge.textContent = pet.idade;
        detailsPetSize.textContent = pet.porte;
        detailsPetColor.textContent = pet.cor || 'Não informada';
        detailsPetGender.textContent = pet.sexo || 'Não informado';
        
        interestBtn.dataset.petId = pet.id; 

        petDetailsModal.style.display = 'flex';
    }

    function closeDetailsModalFunc() {
        petDetailsModal.style.display = 'none';
    }

    // --- Funções para Listagem de Pets ---

    function createPetCard(pet) {
        const petCard = document.createElement('div');
        petCard.classList.add('pet-card');
        petCard.dataset.petId = pet.id;

        // Usa a URL da imagem do pet, ou um placeholder se não houver
        const imageUrl = pet.imageUrl || 'https://via.placeholder.com/200x200?text=Pet+Image'; 
        
        petCard.innerHTML = `
            <img src="${imageUrl}" alt="Foto de ${pet.nome}" class="pet-image">
            <div class="pet-info">
                <h3 class="pet-name">${pet.nome}</h3>
                <p class="pet-details">
                    ${pet.especie} - ${pet.idade} anos - Porte ${pet.porte}<br>
                    Cor: ${pet.cor || 'Não informada'} - Sexo: ${pet.sexo || 'Não informado'}
                </p>
                <button class="view-details-btn">Ver Detalhes</button>
            </div>
        `;

        const viewDetailsBtn = petCard.querySelector('.view-details-btn');
        viewDetailsBtn.addEventListener('click', async (event) => {
            event.stopPropagation();
            try {
                const response = await fetch(`http://localhost:8080/api/pets/${pet.id}`);
                if (!response.ok) {
                    throw new Error(`Erro ao buscar detalhes do pet: ${response.status}`);
                }
                const petDetails = await response.json();
                openDetailsModal(petDetails);
            } catch (error) {
                console.error("Erro ao carregar detalhes do pet:", error);
                alert("Não foi possível carregar os detalhes do pet.");
            }
        });

        petCard.addEventListener('click', async () => {
             try {
                const response = await fetch(`http://localhost:8080/api/pets/${pet.id}`);
                if (!response.ok) {
                    throw new Error(`Erro ao buscar detalhes do pet: ${response.status}`);
                }
                const petDetails = await response.json();
                openDetailsModal(petDetails);
            } catch (error) {
                console.error("Erro ao carregar detalhes do pet:", error);
                alert("Não foi possível carregar os detalhes do pet.");
            }
        });

        return petCard;
    }

    function renderPets(petsPage) {
        petsContainer.innerHTML = '';
        const pets = petsPage.content;
        
        if (pets && pets.length > 0) {
            pets.forEach(pet => {
                petsContainer.appendChild(createPetCard(pet));
            });
            noPetsMessage.style.display = 'none';
        } else {
            noPetsMessage.style.display = 'block';
        }
        console.log("Informações de Paginação:", petsPage);
    }

    async function fetchPets(filters = {}) {
        let queryString = new URLSearchParams(filters).toString();
        const apiUrl = `http://localhost:8080/api/pets?${queryString}`; 

        try {
            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    // 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}` 
                },
            });

            if (!response.ok) {
                // Aqui pode haver um erro no caso do JSON ser vazio ou não parseável
                // Por isso, tentamos ler como texto primeiro e depois como JSON
                const errorText = await response.text();
                try {
                    const errorData = JSON.parse(errorText);
                    throw new Error(errorData.message || `Erro HTTP: ${response.status} - ${errorText}`);
                } catch (jsonError) {
                    throw new Error(`Erro HTTP: ${response.status} - ${errorText}`);
                }
            }

            const petsPage = await response.json();
            renderPets(petsPage);
        } catch (error) {
            console.error('Erro ao buscar pets:', error);
            petsContainer.innerHTML = `<p style="text-align: center; color: red;">Erro ao carregar pets: ${error.message}</p>`;
            noPetsMessage.style.display = 'none';
        }
    }

    // --- Event Listeners ---

    // Abre o modal de cadastro
    if (addPetBtn) {
        addPetBtn.addEventListener('click', openRegisterModal);
    }

    // Fecha o modal de cadastro ao clicar no 'x'
    if (closeRegisterModal) {
        closeRegisterModal.addEventListener('click', closeRegisterModalFunc);
    }

    // Fecha o modal de detalhes ao clicar no 'x'
    if (closeDetailsModal) {
        closeDetailsModal.addEventListener('click', closeDetailsModalFunc);
    }

    // Fecha qualquer modal ao clicar fora dele
    window.addEventListener('click', (event) => {
        if (event.target === petRegisterModal) {
            closeRegisterModalFunc();
        }
        if (event.target === petDetailsModal) {
            closeDetailsModalFunc();
        }
    });

    // Envio do formulário de cadastro de pet (AGORA COM UPLOAD DE IMAGEM)
    petRegisterForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // Impede o recarregamento da página

        registerMessage.style.display = 'block';
        registerMessage.style.color = 'blue';
        registerMessage.textContent = 'Processando cadastro...';

        let imageUrl = null; // Variável para armazenar a URL da imagem

        // PASSO 1: Fazer o upload da imagem, se um arquivo foi selecionado
        if (petImageInput.files.length > 0) {
            registerMessage.textContent = 'Fazendo upload da imagem...';
            const file = petImageInput.files[0];
            const formDataImage = new FormData();
            formDataImage.append('file', file); // 'file' deve corresponder ao @RequestParam("file") do backend

            try {
                const responseUpload = await fetch('http://localhost:8080/api/pets/upload-image', {
                    method: 'POST',
                    // Não defina 'Content-Type' para FormData, o navegador fará isso automaticamente
                    // e adicionará o boundary correto.
                    body: formDataImage,
                    // 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}` // Se seu endpoint de upload for protegido
                });

                if (!responseUpload.ok) {
                    const errorText = await responseUpload.text(); // Lê como texto para depuração
                    throw new Error(`Erro ao fazer upload da imagem: ${responseUpload.status} - ${errorText}`);
                }

                imageUrl = await responseUpload.text(); // O backend retorna a URL da imagem como String
                console.log('Imagem carregada com sucesso:', imageUrl);
                registerMessage.textContent = 'Imagem carregada. Cadastrando pet...';

            } catch (error) {
                console.error('Erro no upload da imagem:', error);
                registerMessage.style.color = 'red';
                registerMessage.textContent = `Erro no upload da imagem: ${error.message}`;
                return; // Impede o cadastro do pet se o upload da imagem falhou
            }
        }

        // PASSO 2: Preparar os dados do pet, incluindo a imageUrl (se houver)
        const formDataPet = new FormData(petRegisterForm);
        const petData = {};
        formDataPet.forEach((value, key) => {
            if (key === 'idade') {
                petData[key] = parseInt(value, 10);
            } else if (key !== 'petImage') { // Ignora o input 'petImage' ao montar o JSON
                petData[key] = value;
            }
        });

        // Adiciona a URL da imagem ao objeto petData
        petData.imageUrl = imageUrl; 

        // PASSO 3: Enviar os dados do pet para o backend
        try {
            const responsePet = await fetch('http://localhost:8080/api/pets', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
                },
                body: JSON.stringify(petData),
            });

            if (!responsePet.ok) {
                const errorText = await responsePet.text();
                try {
                    const errorData = JSON.parse(errorText);
                    throw new Error(errorData.message || `Erro HTTP: ${responsePet.status} - ${errorText}`);
                } catch (jsonError) {
                    throw new Error(`Erro HTTP: ${responsePet.status} - ${errorText}`);
                }
            }

            const newPet = await responsePet.json();
            registerMessage.style.color = 'green';
            registerMessage.textContent = `Pet ${newPet.nome} cadastrado com sucesso!`;
            console.log('Novo pet cadastrado:', newPet);

            fetchPets(); 
            setTimeout(closeRegisterModalFunc, 2000); // Fecha o modal após 2 segundos
        } catch (error) {
            console.error('Erro ao cadastrar pet:', error);
            registerMessage.style.color = 'red';
            registerMessage.textContent = `Erro ao cadastrar pet: ${error.message}`;
        }
    });

    // Event listener para o botão "Tenho Interesse em Adoção" (mantido)
    if (interestBtn) {
        interestBtn.addEventListener('click', () => {
            const petId = interestBtn.dataset.petId;
            if (petId) {
                alert(`Você manifestou interesse em adotar o pet ID: ${petId}. Um responsável entrará em contato.`);
                closeDetailsModalFunc();
            } else {
                alert('Erro: ID do pet não encontrado para manifestar interesse.');
            }
        });
    }

    // Event listeners para filtros e busca (mantidos)
    const applyFilters = () => {
        const filters = {
            nome: petSearchInput.value,
            especie: speciesFilter.value,
            porte: sizeFilter.value,
            sexo: genderFilter.value,
            cor: colorFilter.value
        };
        Object.keys(filters).forEach(key => filters[key] === '' && delete filters[key]);
        fetchPets(filters);
    };

    petSearchInput.addEventListener('input', applyFilters);
    speciesFilter.addEventListener('change', applyFilters);
    sizeFilter.addEventListener('change', applyFilters);
    genderFilter.addEventListener('change', applyFilters);
    colorFilter.addEventListener('change', applyFilters);

    // --- Inicialização ---
    fetchPets();
});