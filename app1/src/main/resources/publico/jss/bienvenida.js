// Agregar clase de animación al título
document.addEventListener("DOMContentLoaded", function () {
    let h1 = document.querySelector("h1");

    // Agregar efecto de parpadeo después de 2 segundos
    setTimeout(() => {
        h1.classList.add("blink");
    }, 2000);

    // Mostrar mensaje en la consola
    console.log("¡Bienvenido a la página!");
});
