// Добавляем в конец файла перед </body> или в отдельный script.js
document.addEventListener('DOMContentLoaded', function() {
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
    carousel.addEventListener('wheel', function(e) {
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
    carousel.addEventListener('slide.bs.carousel', function(e) {
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

    // Данные брендов (можно получить с сервера)
    const brands = [
        { id: 1, name: 'КАМАЗ', image: '/images/brands/kamaz.png' },
        { id: 2, name: 'HOWO', image: '/images/brands/howo.png' },
        { id: 3, name: 'МАЗ', image: '/images/brands/maz.png' },
        { id: 4, name: 'МТЗ', image: '/images/brands/mtz.png' },
        { id: 5, name: 'Урал', image: '/images/brands/ural.png' },
        { id: 6, name: 'ЗИЛ', image: '/images/brands/zil.png' },
        { id: 7, name: 'ЯМЗ', image: '/images/brands/ymz.png' },
        { id: 8, name: 'Shaanxi', image: '/images/brands/shaanxi.png' },
        { id: 9, name: 'КРАЗ', image: '/images/brands/kraz.png' },
        { id: 10, name: 'ДТ-75', image: '/images/brands/dt75.png' }
    ];

    let currentIndex = 2; // Стартуем с центрального элемента
    const visibleCount = 5; // Сколько брендов показывать одновременно

    // Инициализация карусели
    function initCarousel() {
        // Очищаем трек и индикаторы
        track.innerHTML = '';
        indicatorsContainer.innerHTML = '';

        // Создаем элементы брендов
        brands.forEach((brand, index) => {
            const brandItem = document.createElement('div');
            brandItem.className = 'brand-item';
            brandItem.dataset.index = index;

            brandItem.innerHTML = `
                <img src="${brand.image}" alt="${brand.name}" class="brand-logo">
                <div class="brand-name">${brand.name}</div>
            `;

            track.appendChild(brandItem);

            // Создаем индикатор
            const indicator = document.createElement('div');
            indicator.className = 'brand-indicator';
            indicator.dataset.index = index;
            indicator.addEventListener('click', () => goToSlide(index));
            indicatorsContainer.appendChild(indicator);
        });

        updateCarousel();

        // Автопрокрутка
        let autoSlideInterval = setInterval(() => {
            nextSlide();
        }, 4000);

        // Останавливаем автопрокрутку при наведении
        brandsCarousel.addEventListener('mouseenter', () => {
            clearInterval(autoSlideInterval);
        });

        brandsCarousel.addEventListener('mouseleave', () => {
            autoSlideInterval = setInterval(() => {
                nextSlide();
            }, 4000);
        });
    }

    // Обновление отображения карусели
    function updateCarousel() {
        const brandItems = track.querySelectorAll('.brand-item');
        const indicators = indicatorsContainer.querySelectorAll('.brand-indicator');

        // Сбрасываем все классы
        brandItems.forEach(item => {
            item.classList.remove('active', 'side-1', 'side-2', 'side-3', 'side-4');
        });

        indicators.forEach(ind => ind.classList.remove('active'));

        // Устанавливаем активный элемент и его соседей
        brandItems[currentIndex]?.classList.add('active');
        indicators[currentIndex]?.classList.add('active');

        // Устанавливаем классы для соседних элементов
        for (let i = 1; i <= 2; i++) {
            const prevIndex = (currentIndex - i + brands.length) % brands.length;
            const nextIndex = (currentIndex + i) % brands.length;

            if (brandItems[prevIndex]) {
                brandItems[prevIndex].classList.add(`side-${i}`);
            }
            if (brandItems[nextIndex]) {
                brandItems[nextIndex].classList.add(`side-${i}`);
            }
        }

        // Прокручиваем трек к активному элементу
        const itemWidth = brandItems[0]?.offsetWidth + 20; // 20px это gap
        const offset = currentIndex * itemWidth - (track.offsetWidth / 2) + (itemWidth / 2);
        track.style.transform = `translateX(-${offset}px)`;
    }

    // Переход к слайду
    function goToSlide(index) {
        currentIndex = index;
        updateCarousel();
    }

    // Следующий слайд
    function nextSlide() {
        currentIndex = (currentIndex + 1) % brands.length;
        updateCarousel();
    }

    // Предыдущий слайд
    function prevSlide() {
        currentIndex = (currentIndex - 1 + brands.length) % brands.length;
        updateCarousel();
    }

    // Обработчики событий
    prevBtn.addEventListener('click', prevSlide);
    nextBtn.addEventListener('click', nextSlide);

    // Прокрутка колесиком мыши
    track.addEventListener('wheel', function(e) {
        e.preventDefault();

        if (e.deltaY > 5) {
            nextSlide();
        } else if (e.deltaY < -5) {
            prevSlide();
        }
    }, { passive: false });

    // Инициализируем карусель
    initCarousel();

    // Ресайз окна
    window.addEventListener('resize', updateCarousel);

    document.querySelectorAll('.brand-logo').forEach(img => {
        console.log('Image size:', img.naturalWidth, 'x', img.naturalHeight);
        console.log('Current CSS size:', img.offsetWidth, 'x', img.offsetHeight);

        // Принудительно меняем размер
        img.style.width = '300px';
        img.style.height = '300px';
        img.style.minWidth = '300px';
        img.style.minHeight = '300px';
        img.style.maxWidth = '300px';
        img.style.maxHeight = '300px';
        img.style.objectFit = 'contain';
    });

    // Для активного элемента
    document.querySelectorAll('.brand-item.active .brand-logo').forEach(img => {
        img.style.width = '400px';
        img.style.height = '400px';
    });
});