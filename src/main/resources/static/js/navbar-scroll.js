document.addEventListener('DOMContentLoaded', () => {
  const navbar = document.querySelector('.navbar');
  const heroSection = document.querySelector('.hero-section'); // Если нужно учитывать высоту hero

  window.addEventListener('scroll', () => {
    if (window.scrollY > 0) { // Или window.scrollY > heroSection.offsetHeight, если после hero
      navbar.classList.add('navbar-scrolled');
    } else {
      navbar.classList.remove('navbar-scrolled');
    }
  });
});