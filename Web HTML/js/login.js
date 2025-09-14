document.addEventListener('DOMContentLoaded', () => {
    const buttonsContainer = document.querySelector('.buttons-login');
    const buttons = buttonsContainer.querySelectorAll('button');
    const glider = buttonsContainer.querySelector('.glider');
    
    function moveGlider(targetButton) {
        const targetRect = targetButton.getBoundingClientRect();
        const containerRect = buttonsContainer.getBoundingClientRect();

        const targetWidth = targetRect.width;
        // Calcula a posição 'left' relativa ao container
        const targetLeft = targetRect.left - containerRect.left;

        glider.style.width = `${targetWidth}px`;
        glider.style.transform = `translateX(${targetLeft}px)`;

        buttons.forEach(btn => btn.classList.remove('active'));
        targetButton.classList.add('active');
    }

    buttons.forEach(button => {
        button.addEventListener('click', (e) => {
            moveGlider(e.currentTarget);
        });
    });

    const initialActiveButton = document.querySelector('.buttons-login button.active');
    if (initialActiveButton) {
        moveGlider(initialActiveButton);
    }
});
