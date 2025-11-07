// Button interactions
document.addEventListener('DOMContentLoaded', () => {
    const editBtn = document.querySelector('.btn-edit');
    const detailButtons = document.querySelectorAll('.btn-details');

    // Edit Profile Button
    if (editBtn) {
        editBtn.addEventListener('click', () => {
            alert('Função de editar perfil em desenvolvimento!');
        });
    }

    // Details Buttons
    detailButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            const petCard = e.target.closest('.pet-card');
            const petName = petCard.querySelector('.pet-name').textContent;
            alert(`Visualizar detalhes de ${petName}`);
        });
    });

    // Animate cards on scroll
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '0';
                entry.target.style.transform = 'translateY(20px)';
                
                setTimeout(() => {
                    entry.target.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }, 100);
                
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Observe all pet cards
    document.querySelectorAll('.pet-card').forEach(card => {
        observer.observe(card);
    });

    // Stats animation on load
    const statNumbers = document.querySelectorAll('.stat-number');
    statNumbers.forEach(stat => {
        const finalValue = parseInt(stat.textContent);
        let currentValue = 0;
        const increment = finalValue / 30;
        
        const counter = setInterval(() => {
            currentValue += increment;
            if (currentValue >= finalValue) {
                stat.textContent = finalValue;
                clearInterval(counter);
            } else {
                stat.textContent = Math.floor(currentValue);
            }
        }, 30);
    });
});
