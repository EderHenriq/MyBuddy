document.addEventListener('DOMContentLoaded', () => {
    const servicosGrid = document.getElementById('servicosGrid');
    const loadingMessage = document.getElementById('loadingMessage');
    const noServicosMessage = document.getElementById('noServicosMessage');
    const filterButtons = document.querySelectorAll('.filter-btn');

    const servicosData = [
        {
            id: 1,
            nome: "Clínica Veterinária Pet Feliz",
            endereco: "Av. Mandacaru, 1500 - Zona 03, Maringá - PR",
            telefone: "(44) 3026-1500",
            horario: "Seg-Sáb: 08h-20h | Plantão 24h",
            categoria: "clinica",
            imagemUrl: "../src/assets/servicos/2.jpg" // Imagem local para clínica
        },
        {
            id: 2,
            nome: "Spa & Estética Canina Luxo Pet",
            endereco: "Rua Osvaldo Cruz, 789 - Centro, Maringá - PR",
            telefone: "(44) 3227-0789",
            horario: "Ter-Sáb: 09h-18h",
            categoria: "spa",
            imagemUrl: "../src/assets/servicos/spa.jpg" // Imagem local para spa
        },
        {
            id: 3,
            nome: "Hotelzinho Paraíso dos Pets",
            endereco: "Rod. PR-317, Km 10 - Zona Rural, Maringá - PR",
            telefone: "(44) 99876-5432",
            horario: "Seg-Dom: 08h-18h (Entrada/Saída)",
            categoria: "hotel",
            imagemUrl: "../src/assets/servicos/3.jpg" // Imagem local para hotel
        },
        {
            id: 4,
            nome: "Hospital Veterinário UniAnimais",
            endereco: "Av. Morangueira, 2500 - Zona 07, Maringá - PR",
            telefone: "(44) 3033-2500",
            horario: "Atendimento 24h",
            categoria: "clinica",
            imagemUrl: "../src/assets/servicos/vet.jpg"
        },
        {
            id: 5,
            nome: "Day Care & Treinamento Patas Urbanas",
            endereco: "Rua Santos Dumont, 123 - Zona 01, Maringá - PR",
            telefone: "(44) 3344-1234",
            horario: "Seg-Sex: 07h-19h",
            categoria: "spa", // Pode ser considerado spa/creche
            imagemUrl: "../src/assets/servicos/spa 2.jpg"
        },
        {
            id: 6,
            nome: "Hospedagem Familiar Meu Melhor Amigo",
            endereco: "R. Pioneiro João José Paiz, 50 - Jd. Alvorada, Maringá - PR",
            telefone: "(44) 99123-4567",
            horario: "Agendamento prévio",
            categoria: "hotel",
            imagemUrl: "../src/assets/servicos/4.jpg"
        }
     
    ];

    // Função para criar um card de Serviço
    function createServicoCard(servico) {
        const card = document.createElement('article');
        card.classList.add('servico-card');

        card.innerHTML = `
            <div class="servico-image-container">
                <img src="${servico.imagemUrl}" alt="Foto do ${servico.nome}" class="servico-image">
            </div>
            <div class="servico-info">
                <h3 class="servico-name">${servico.nome}</h3>
                <p class="servico-detail">
                    <i class="fas fa-map-marker-alt"></i> ${servico.endereco}
                </p>
                <p class="servico-detail">
                    <i class="fas fa-phone"></i> ${servico.telefone}
                </p>
                <p class="servico-detail">
                    <i class="fas fa-clock"></i> ${servico.horario}
                </p>
            </div>
        `;
        return card;
    }

    // Função para carregar e exibir os serviços, com filtragem
    function loadServicos(filterCategory = 'all') {
        loadingMessage.style.display = 'block';
        servicosGrid.innerHTML = '';
        noServicosMessage.style.display = 'none';

        setTimeout(() => { // Simula um atraso de rede
            loadingMessage.style.display = 'none';

            let filteredServicos = servicosData;
            if (filterCategory !== 'all') {
                filteredServicos = servicosData.filter(servico => servico.categoria === filterCategory);
            }

            if (filteredServicos.length > 0) {
                filteredServicos.forEach(servico => {
                    servicosGrid.appendChild(createServicoCard(servico));
                });
            } else {
                noServicosMessage.style.display = 'block';
            }
        }, 500); // Carrega após meio segundo
    }

    // Adiciona event listeners aos botões de filtro
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Remove a classe 'active' de todos os botões
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // Adiciona a classe 'active' ao botão clicado
            button.classList.add('active');
            
            const category = button.dataset.category;
            loadServicos(category);
        });
    });

    // Inicializa o carregamento dos serviços ao carregar a página (todos por padrão)
    loadServicos();
});