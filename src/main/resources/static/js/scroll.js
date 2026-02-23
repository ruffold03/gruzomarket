// Добавляем в конец файла перед </body> или в отдельный script.js
document.addEventListener('DOMContentLoaded', function () {
    const carousel = document.getElementById('categoriesCarousel');

    if (!carousel) return;

    // Инициализируем карусель
    const carouselInstance = new bootstrap.Carousel(carousel, {
        interval: 4000,
        wrap: true,
        pause: 'hover',
        touch: true
    });

    // Обработка прокрутки колесиком
    let wheelTimeout;
    carousel.addEventListener('wheel', function (e) {
        e.preventDefault();

        // Дебаунс
        clearTimeout(wheelTimeout);
        wheelTimeout = setTimeout(() => {
            if (e.deltaY > 5) {
                carouselInstance.next();
            } else if (e.deltaY < -5) {
                carouselInstance.prev();
            }
        }, 100);
    }, { passive: false });

    // Добавляем классы для анимации при ручном переключении
    carousel.addEventListener('slide.bs.carousel', function (e) {
        const items = carousel.querySelectorAll('.carousel-item');
        items.forEach(item => {
            item.classList.remove('slide-in', 'slide-out');
        });

        if (e.direction === 'left') {
            // Следующий слайд
            e.relatedTarget.classList.add('slide-in');
            e.from.classList.add('slide-out');
        } else {
            // Предыдущий слайд
            e.relatedTarget.classList.add('slide-in');
            e.from.classList.add('slide-out');
        }
    });

    const brandsCarousel = document.getElementById('brandsCarousel');
    if (!brandsCarousel) return;

    const track = brandsCarousel.querySelector('.brands-carousel-track');
    const indicatorsContainer = brandsCarousel.querySelector('.brand-indicators');
    const prevBtn = brandsCarousel.querySelector('.brand-arrow-prev');
    const nextBtn = brandsCarousel.querySelector('.brand-arrow-next');

    const brands = [
        { id: 12, name: 'КАМАЗ', image: '/images/brands/kamaz.png' },
        { id: 1, name: 'HOWO', image: '/images/brands/howo.png' },
        { id: 16, name: 'МАЗ', image: '/images/brands/maz.png' },
        { id: 18, name: 'МТЗ', image: '/images/brands/mtz.png' },
        { id: 26, name: 'Урал', image: '/images/brands/ural.png' },
        { id: 11, name: 'ЗИЛ', image: '/images/brands/zil.png' },
        { id: 28, name: 'ЯМЗ', image: '/images/brands/ymz.png' },
        { id: 2, name: 'Shaanxi', image: '/images/brands/shaanxi.png' },
        { id: 14, name: 'КРАЗ', image: '/images/brands/kraz.png' },
        { id: 10, name: 'ДТ-75', image: '/images/brands/dt75.png' }
    ];

    let currentIndex = 2;
    let autoSlideInterval;

    function initCarousel() {
        track.innerHTML = '';
        indicatorsContainer.innerHTML = '';

        brands.forEach((brand, index) => {
            const brandItem = document.createElement('div');
            brandItem.className = 'brand-item';
            brandItem.dataset.index = index;

            brandItem.innerHTML = `
                <img src="${brand.image}" alt="${brand.name}" class="brand-carousel-logo" 
                     onerror="this.src='/images/engine.jpg'; this.style.opacity='0.5'">
                <div class="brand-name">${brand.name}</div>
            `;

            // Кликабельность - переход в каталог
            brandItem.style.cursor = 'pointer';
            brandItem.addEventListener('click', () => {
                window.location.href = `/products?brandIds=${brand.id}`;
            });

            track.appendChild(brandItem);

            const indicator = document.createElement('div');
            indicator.className = 'brand-indicator';
            indicator.dataset.index = index;
            indicator.addEventListener('click', (e) => {
                e.stopPropagation();
                goToSlide(index);
            });
            indicatorsContainer.appendChild(indicator);
        });

        setTimeout(updateCarousel, 100);
        startAutoSlide();
    }

    function updateCarousel() {
        const brandItems = track.querySelectorAll('.brand-item');
        const indicators = indicatorsContainer.querySelectorAll('.brand-indicator');
        if (brandItems.length === 0) return;

        brandItems.forEach(item => {
            item.classList.remove('active', 'side-1', 'side-2', 'side-3', 'side-4');
        });
        indicators.forEach(ind => ind.classList.remove('active'));

        const activeItem = brandItems[currentIndex];
        if (activeItem) {
            activeItem.classList.add('active');
            indicators[currentIndex].classList.add('active');

            // Эффект глубины
            for (let i = 1; i <= 2; i++) {
                const prevIdx = (currentIndex - i + brands.length) % brands.length;
                const nextIdx = (currentIndex + i) % brands.length;
                brandItems[prevIdx]?.classList.add(`side-${i}`);
                brandItems[nextIdx]?.classList.add(`side-${i}`);
            }

            // Точное центрирование по координатам
            const containerWidth = brandsCarousel.offsetWidth;
            const itemWidth = activeItem.offsetWidth;
            const itemOffsetLeft = activeItem.offsetLeft;

            // Центр контейнера минус центр элемента
            const targetTranslate = (containerWidth / 2) - (itemOffsetLeft + itemWidth / 2);
            track.style.transform = `translateX(${targetTranslate}px)`;
        }
    }

    function goToSlide(index) {
        currentIndex = index;
        updateCarousel();
        resetAutoSlide();
    }

    function nextSlide() {
        currentIndex = (currentIndex + 1) % brands.length;
        updateCarousel();
    }

    function prevSlide() {
        currentIndex = (currentIndex - 1 + brands.length) % brands.length;
        updateCarousel();
    }

    function startAutoSlide() {
        clearInterval(autoSlideInterval);
        autoSlideInterval = setInterval(nextSlide, 5000);
    }

    function resetAutoSlide() {
        startAutoSlide();
    }

    prevBtn.addEventListener('click', (e) => { e.stopPropagation(); prevSlide(); resetAutoSlide(); });
    nextBtn.addEventListener('click', (e) => { e.stopPropagation(); nextSlide(); resetAutoSlide(); });

    brandsCarousel.addEventListener('mouseenter', () => clearInterval(autoSlideInterval));
    brandsCarousel.addEventListener('mouseleave', startAutoSlide);

    // Прокрутка колесиком мышки
    let brandWheelTimeout;
    brandsCarousel.addEventListener('wheel', function (e) {
        e.preventDefault();
        clearTimeout(brandWheelTimeout);
        brandWheelTimeout = setTimeout(() => {
            if (e.deltaY > 5 || e.deltaX > 5) {
                nextSlide();
                resetAutoSlide();
            } else if (e.deltaY < -5 || e.deltaX < -5) {
                prevSlide();
                resetAutoSlide();
            }
        }, 80);
    }, { passive: false });

    window.addEventListener('resize', updateCarousel);

    initCarousel();

    // Принудительная подстройка размеров (для анимации и шрифтов)
    function forceSizes() {
        const activeLogo = track.querySelector('.brand-item.active .brand-carousel-logo');
        if (activeLogo) {
            document.querySelectorAll('.brand-carousel-logo').forEach(img => {
                if (img !== activeLogo) {
                    img.style.width = '300px';
                    img.style.height = '300px';
                    img.style.maxWidth = 'none';
                    img.style.maxHeight = 'none';
                }
            });
            activeLogo.style.width = '450px';
            activeLogo.style.height = '450px';
            activeLogo.style.maxWidth = 'none';
            activeLogo.style.maxHeight = 'none';
        }
    }

    setInterval(forceSizes, 500);
});

    // Intersection Observer for scroll-reveal animations
    const revealCallback = (entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                // Once it's visible, we don't need to observe it anymore
                observer.unobserve(entry.target);
            }
        });
    };

    const revealObserver = new IntersectionObserver(revealCallback, {
        root: null,
        threshold: 0.1, // 10% visibility to trigger
        rootMargin: '0px 0px -50px 0px' // Trigger slightly before it comes into view
    });

    document.querySelectorAll('.scroll-reveal').forEach(el => {
        revealObserver.observe(el);
    });
