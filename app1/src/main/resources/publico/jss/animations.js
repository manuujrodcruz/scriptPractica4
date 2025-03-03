window.addEventListener('load', function() {
    let form = document.querySelector('form');
    let title = document.querySelector('h1');
    let inputs = document.querySelectorAll('input');
    let submitBtn = document.querySelector('input[type="submit"]');

    // Fade-in para el título
    title.style.opacity = '0';
    title.style.transform = 'translateY(-40px)';
    title.style.transition = 'opacity 1.5s ease-out, transform 1.5s ease-out';
    setTimeout(function() {
        title.style.opacity = '1';
        title.style.transform = 'translateY(0)';
    }, 200);

    // Animación de entrada del formulario
    form.style.opacity = '0';
    form.style.transform = 'translateY(30px)';
    form.style.transition = 'opacity 1s ease-out, transform 1s ease-out';
    setTimeout(function() {
        form.style.opacity = '1';
        form.style.transform = 'translateY(0)';
    }, 400);

    // Efecto de enfoque para los inputs
    inputs.forEach(input => {
        input.style.transition = 'all 0.3s ease';
        input.addEventListener('focus', function() {
            input.style.transform = 'scale(1.05)';
            input.style.boxShadow = '0px 4px 10px rgba(0, 123, 255, 0.5)';
        });
        input.addEventListener('blur', function() {
            input.style.transform = 'scale(1)';
            input.style.boxShadow = 'none';
        });
    });

    // Efecto de hover en el botón
    submitBtn.style.transition = 'background-color 0.3s ease, transform 0.3s ease';
    submitBtn.addEventListener('mouseover', function() {
        submitBtn.style.backgroundColor = '#0056b3';
        submitBtn.style.transform = 'scale(1.05)';
    });
    submitBtn.addEventListener('mouseout', function() {
        submitBtn.style.backgroundColor = '#007bff';
        submitBtn.style.transform = 'scale(1)';
    });
});
