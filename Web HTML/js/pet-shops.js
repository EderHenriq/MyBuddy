document.addEventListener('DOMContentLoaded', () => {
    const petShopsGrid = document.getElementById('petShopsGrid');
    const loadingMessage = document.getElementById('loadingMessage');
    const noPetShopsMessage = document.getElementById('noPetShopsMessage');

    const petShopsData = [
        {
            id: 1,
            nome: "Pet Shop Amigo Fiel",
            endereco: "Av. Tiradentes, 1234 - Zona 01, Maringá - PR",
            telefone: "(44) 3028-1234",
            horario: "Seg-Sex: 08h-18h | Sáb: 08h-14h",
            imagemUrl: "../src/assets/pet-shop/petshop-1000x500.jpg"
        },
        {
            id: 2,
            nome: "Mundo Animal Pet Center",
            endereco: "R. Paranaguá, 567 - Centro, Maringá - PR",
            telefone: "(44) 3225-5678",
            horario: "Seg-Sáb: 08h30-19h",
            imagemUrl: "../src/assets/pet-shop/petshop2.jpg" 
        },
        {
            id: 3,
            nome: "Clínica Veterinária e Pet Shop Cão Feliz",
            endereco: "Av. Colombo, 2000 - Zona 07, Maringá - PR",
            telefone: "(44) 3031-2000",
            horario: "Seg-Dom: 07h-22h",
            imagemUrl: "../src/assets/pet-shop/petshop2.jpg" 
        },
        {
            id: 4,
            nome: "Reino dos Pets",
            endereco: "Rua Joubert de Carvalho, 800 - Centro, Maringá - PR",
            telefone: "(44) 3345-0800",
            horario: "Seg-Sex: 09h-18h | Sáb: 09h-13h", 
            imagemUrl: "../src/assets/pet-shop/petshop4.jpg" 
        },
        {
            id: 5,
            nome: "Casa do Veterinário",
            endereco: "Av. Brasil, 4500 - Zona 03, Maringá - PR",
            telefone: "(44) 3262-4500",
            horario: "Seg-Sex: 08h-18h | Sáb: 08h-12h",
            imagemUrl: "../src/assets/pet-shop/petshop-1000x500.jpg" 
        }
    ];

    function createPetShopCard(petShop) {
        const card = document.createElement('article');
        card.classList.add('pet-shop-card');

        card.innerHTML = `
            <div class="pet-shop-image-container">
                <img src="${petShop.imagemUrl}" alt="Foto da ${petShop.nome}" class="pet-shop-image">
            </div>
            <div class="pet-shop-info">
                <h3 class="pet-shop-name">${petShop.nome}</h3>
                <p class="pet-shop-detail">
                    <i class="fas fa-map-marker-alt"></i> ${petShop.endereco}
                </p>
                <p class="pet-shop-detail">
                    <i class="fas fa-phone"></i> ${petShop.telefone}
                </p>
                <p class="pet-shop-detail">
                    <i class="fas fa-clock"></i> ${petShop.horario}
                </p>
            </div>
        `;
        return card;
    }

    function loadPetShops() {
        loadingMessage.style.display = 'block';
        petShopsGrid.innerHTML = '';
        noPetShopsMessage.style.display = 'none';

        setTimeout(() => {
            loadingMessage.style.display = 'none';

            if (petShopsData.length > 0) {
                petShopsData.forEach(petShop => {
                    petShopsGrid.appendChild(createPetShopCard(petShop));
                });
            } else {
                noPetShopsMessage.style.display = 'block';
            }
        }, 1000);
    }

    loadPetShops();

});