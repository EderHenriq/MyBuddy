const slider = document.querySelector('.nav-slider');
const links = document.querySelectorAll('.nav-links a');

function moveSlider(element) {
    slider.style.width = element.offsetWidth + 'px';
    slider.style.left = element.offsetLeft + 'px';
    slider.style.height = element.offsetHeight + 'px'; // ajusta altura da pílula
}

// Inicializa no link ativo
const activeLink = document.querySelector('.nav-links a.active');
if (activeLink) moveSlider(activeLink);

// Atualiza ao clicar
links.forEach(link => {
    link.addEventListener('click', () => {
        links.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
        moveSlider(link);
    });
});
