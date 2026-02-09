document.addEventListener('DOMContentLoaded', function() {
    // Состояние
    let currentPage = 0;
    const pageSize = 12;
    let totalPages = 0;
    let totalElements = 0;

    // Фильтры
    let filters = {
        categoryIds: [],
        brandIds: [],
        minPrice: 0,
        maxPrice: 60000,
        searchQuery: '',
        sortBy: 'name,asc'
    };

    // Элементы DOM
    const productsGrid = document.getElementById('productsGrid');
    const pagination = document.getElementById('pagination');
    const searchInput = document.getElementById('searchInput');
    const sortSelect = document.getElementById('sortSelect');
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    const resetFiltersBtn = document.getElementById('resetFiltersBtn');
    const minSlider = document.getElementById('minSlider');
    const maxSlider = document.getElementById('maxSlider');
    const minPriceInput = document.getElementById('minPriceInput');
    const maxPriceInput = document.getElementById('maxPriceInput');
    const sliderFilled = document.getElementById('sliderFilled');


    // Минимальная разница между min и max
    const MIN_PRICE_DIFF = 100;
    const MAX_PRICE = 60000;

    // Форматирование цены
    function formatPrice(price) {
        return new Intl.NumberFormat('ru-RU').format(Math.round(price));
    }


    // Обновление слайдера заполнения
    function updateSliderFilled() {
        const minPercent = (filters.minPrice / MAX_PRICE) * 100;
        const maxPercent = (filters.maxPrice / MAX_PRICE) * 100;

        if (sliderFilled) {
            sliderFilled.style.left = `${minPercent}%`;
            sliderFilled.style.width = `${maxPercent - minPercent}%`;
        }
    }

    // Проверка минимальной разницы в цене
    function validatePriceDifference(min, max) {
        if (max - min < MIN_PRICE_DIFF) {
            if (min === parseInt(minSlider.value)) {
                // Если двигали min ползунок
                return { min: min, max: min + MIN_PRICE_DIFF };
            } else {
                // Если двигали max ползунок
                return { min: max - MIN_PRICE_DIFF, max: max };
            }
        }
        return { min: min, max: max };
    }

    // Обновление ползунков и полей ввода
    function updatePriceControls(min, max) {
        // Обновляем ползунки
        if (minSlider) minSlider.value = min;
        if (maxSlider) maxSlider.value = max;

        // Обновляем поля ввода
        if (minPriceInput) minPriceInput.value = min;
        if (maxPriceInput) maxPriceInput.value = max;

        // Обновляем визуальное заполнение
        updateSliderFilled();
    }

    // Получение товаров
    async function fetchProducts(page = 0) {
        try {
            // Показываем индикатор загрузки
            const productsGrid = document.getElementById('productsGrid');
                // Проверяем, существует ли элемент
            if (!productsGrid) {
                console.error('Элемент productsGrid не найден!');
                return;
            }
            productsGrid.innerHTML = '<div class="spinner-border text-primary" role="status"><span class="visually-hidden">Загрузка...</span></div>';

            // Собираем параметры запроса
            const params = new URLSearchParams({
                page: page,
                size: pageSize,
                sort: filters.sortBy
            });

            // Добавляем фильтры
            params.append('q', filters.searchQuery);
            params.set('sort', filters.sortBy.replace(',', '_'));
            if (filters.categoryIds.length > 0) params.append('categoryIds', filters.categoryIds.join(','));
            if (filters.brandIds.length > 0) params.append('brandIds', filters.brandIds.join(','));
            if (filters.minPrice > 0) params.append('minPrice', filters.minPrice);
            if (filters.maxPrice < MAX_PRICE) params.append('maxPrice', filters.maxPrice);

            // Делаем запрос
            const response = await fetch(`/api/products/query?${params.toString()}`);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            // Обновляем состояние
            currentPage = page;
            totalPages = data.totalPages || 1;
            totalElements = data.totalElements || 0;

            // Рендерим товары и пагинацию
            renderProducts(data.content || []);
            renderPagination();

        } catch (error) {
            const productsGrid = document.getElementById('productsGrid');
            if (productsGrid) {
                productsGrid.innerHTML = '<p class="text-danger">Ошибка загрузки товаров</p>';
            }
            console.error('Ошибка загрузки товаров:', error);
        }
    }

    // После функции fetchProducts в init() добавьте:
    async function checkFavoriteStatus() {
        try {
            const response = await fetch('/api/favorites/ids');
            if (response.ok) {
                const favoriteIds = await response.json();

                // Обновляем иконки на странице
                document.querySelectorAll('.btn-favorite').forEach(btn => {
                    const onclickAttr = btn.getAttribute('onclick');
                    if (!onclickAttr) return;

                    const match = onclickAttr.match(/toggleFavorite\((\d+)\)/);
                    if (!match) return;

                    const productId = parseInt(match[1]);
                    const icon = btn.querySelector('i');

                    if (favoriteIds.includes(productId)) {
                        icon.classList.remove('far');
                        icon.classList.add('fas');
                        icon.style.color = '#ff4757';
                    } else {
                        icon.classList.remove('fas');
                        icon.classList.add('far');
                        icon.style.color = '';
                    }
                });
            }
        } catch (error) {
            console.error('Ошибка загрузки избранного:', error);
        }
    }

    // Рендеринг товаров
    function renderProducts(products) {
        if (!products || products.length === 0) {
            productsGrid.innerHTML = `
                <div class="col-12 text-center py-5">
                    <i class="fas fa-search fa-3x mb-3 text-muted"></i>
                    <h4>Товары не найдены</h4>
                    <p class="text-muted">Попробуйте изменить параметры фильтрации</p>
                </div>
            `;
            return;
        }

        productsGrid.innerHTML = products.map(product => `
            <div class="col-md-4 col-lg-3 mb-4">
                <div class="product-card h-100">
                    ${product.quantity > 0 ?
                        '<span class="product-badge">В наличии</span>' :
                        '<span class="product-badge" style="background: #2d3748;">Под заказ</span>'
                    }

                    <div class="product-image"
                         style="background-image: url('${product.imageUrl || '/images/no-image.jpg'}');">
                        <div class="product-overlay">
                            <button class="quick-view-btn" onclick="quickView(${product.id})">
                                Быстрый просмотр
                            </button>
                        </div>
                    </div>

                    <div class="product-content">
                        <div class="product-category">${product.category?.name || 'Запчасти'}</div>
                        <h5 class="product-title">${product.name}</h5>
                        <div class="product-article">Артикул: ${product.article || 'N/A'}</div>
                        <div class="product-price">${formatPrice(product.price || 0)} ₽</div>

                        <div class="product-actions">
                            <button class="btn-add-cart"
                                    onclick="addToCart(${product.id})"
                                    ${product.quantity <= 0 ? 'disabled' : ''}>
                                <i class="fas fa-cart-plus me-2"></i>В корзину
                            </button>
                            <button class="btn-favorite" onclick="toggleFavorite(${product.id})">
                                <i class="far fa-heart"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
        setTimeout(checkFavoriteStatus, 100);
    }

    // Рендеринг пагинации
    function renderPagination() {
        if (totalPages <= 1) {
            pagination.innerHTML = '';
            return;
        }

        let paginationHtml = '';
        const maxVisiblePages = 5;
        let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
        let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

        if (endPage - startPage + 1 < maxVisiblePages) {
            startPage = Math.max(0, endPage - maxVisiblePages + 1);
        }

        // Кнопка "Назад"
        paginationHtml += `
            <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" ${currentPage > 0 ? `onclick="changePage(${currentPage - 1})"` : ''}>
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `;

        // Первая страница
        if (startPage > 0) {
            paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePage(0)">1</a>
                </li>
                ${startPage > 1 ? '<li class="page-item disabled"><span class="page-link">...</span></li>' : ''}
            `;
        }

        // Страницы
        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="changePage(${i})">${i + 1}</a>
                </li>
            `;
        }

        // Последняя страница
        if (endPage < totalPages - 1) {
            paginationHtml += `
                ${endPage < totalPages - 2 ? '<li class="page-item disabled"><span class="page-link">...</span></li>' : ''}
                <li class="page-item">
                    <a class="page-link" href="#" onclick="changePage(${totalPages - 1})">${totalPages}</a>
                </li>
            `;
        }

        // Кнопка "Вперед"
        paginationHtml += `
            <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" ${currentPage < totalPages - 1 ? `onclick="changePage(${currentPage + 1})"` : ''}>
                    <i class="fas fa-chevron-right"></i>
                </a>
            </li>
        `;

        pagination.innerHTML = paginationHtml;
    }

    // Смена страницы
    window.changePage = function(page) {
        if (page >= 0 && page < totalPages) {
            fetchProducts(page);
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    };

    // Обновление фильтров
    function updateFilters() {
        // Категории
        filters.categoryIds = Array.from(document.querySelectorAll('.category-filter:checked'))
            .map(cb => parseInt(cb.value));

        // Бренды
        filters.brandIds = Array.from(document.querySelectorAll('.brand-filter:checked'))
            .map(cb => parseInt(cb.value));

        // Цена (уже обновлена через обработчики событий)

        // Сортировка
        filters.sortBy = sortSelect?.value || 'name,asc';

        // Сбрасываем на первую страницу
        currentPage = 0;

        // Загружаем товары
        fetchProducts(0);
    }

    // Сброс фильтров
    function resetFilters() {
        // Сброс чекбоксов
        document.querySelectorAll('.category-filter, .brand-filter').forEach(cb => {
            cb.checked = false;
        });

        // Сброс цены
        filters.minPrice = 0;
        filters.maxPrice = MAX_PRICE;
        updatePriceControls(0, MAX_PRICE);

        // Сброс поиска
        if (searchInput) searchInput.value = '';
        filters.searchQuery = '';

        // Сброс сортировки
        if (sortSelect) sortSelect.value = 'name,asc';

        // Обновляем и загружаем
        updateFilters();
    }

    // Обработчики событий для ползунков цены
    function setupPriceSlider() {
        if (!minSlider || !maxSlider) return;

        // Функция для обновления цены
        function updatePriceFromSliders() {
            let min = parseInt(minSlider.value);
            let max = parseInt(maxSlider.value);

            // Проверяем минимальную разницу
            const validated = validatePriceDifference(min, max);
            min = validated.min;
            max = validated.max;

            // Обновляем фильтры
            filters.minPrice = min;
            filters.maxPrice = max;

            // Обновляем контролы
            updatePriceControls(min, max);

            // Автоматически применяем фильтры (debounce)
            clearTimeout(window.priceChangeTimeout);
            window.priceChangeTimeout = setTimeout(updateFilters, 500);
        }

        // События для ползунков
        minSlider.addEventListener('input', updatePriceFromSliders);
        maxSlider.addEventListener('input', updatePriceFromSliders);

        // События для полей ввода
        if (minPriceInput && maxPriceInput) {
            function updatePriceFromInputs() {
                let min = parseInt(minPriceInput.value) || 0;
                let max = parseInt(maxPriceInput.value) || MAX_PRICE;

                // Ограничения
                min = Math.max(0, Math.min(min, MAX_PRICE - MIN_PRICE_DIFF));
                max = Math.min(MAX_PRICE, Math.max(max, MIN_PRICE_DIFF));

                // Проверяем минимальную разницу
                const validated = validatePriceDifference(min, max);
                min = validated.min;
                max = validated.max;

                // Обновляем фильтры
                filters.minPrice = min;
                filters.maxPrice = max;

                // Обновляем контролы
                updatePriceControls(min, max);

                // Автоматически применяем фильтры (debounce)
                clearTimeout(window.priceInputTimeout);
                window.priceInputTimeout = setTimeout(updateFilters, 800);
            }

            minPriceInput.addEventListener('input', updatePriceFromInputs);
            maxPriceInput.addEventListener('input', updatePriceFromInputs);
        }
    }

    // Инициализация
    function init() {
        // Настройка слайдера цены
        setupPriceSlider();
        const urlParams = new URLSearchParams(window.location.search);
        const searchTerm = urlParams.get('search');
        if (searchTerm) {
            searchInput.value = searchTerm;
            filters.searchQuery = searchTerm;
        }

        // Начальная загрузка
        updateSliderFilled();
        fetchProducts(0);

        // События фильтров
        if (applyFiltersBtn) {
            applyFiltersBtn.addEventListener('click', updateFilters);
        }

        if (resetFiltersBtn) {
            resetFiltersBtn.addEventListener('click', resetFilters);
        }

        // События поиска
        if (searchInput) {
            let searchTimeout;

            // Поиск при вводе (debounce)
            searchInput.addEventListener('input', (e) => {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => {
                    filters.searchQuery = e.target.value.trim();
                    updateFilters();
                }, 800);
            });

            // Поиск при нажатии Enter
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    filters.searchQuery = e.target.value.trim();
                    updateFilters();
                }
            });
        }

        const globalSearchInput = document.getElementById('globalSearchInput');
        if (globalSearchInput) {
            globalSearchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    const query = e.target.value.trim();

                    // Перенаправляем на страницу каталога с параметром поиска
                    window.location.href = `/products?search=${encodeURIComponent(query)}`;
                }
            });
        }

        // События сортировки
        if (sortSelect) {
            sortSelect.addEventListener('change', updateFilters);
        }

        // События чекбоксов (автоматическое применение)
        document.querySelectorAll('.category-filter, .brand-filter').forEach(checkbox => {
            checkbox.addEventListener('change', () => {
                clearTimeout(window.checkboxTimeout);
                window.checkboxTimeout = setTimeout(updateFilters, 300);
            });
        });
    }

    // Вспомогательные функции
    window.quickView = function(productId) {
        // TODO: Реализовать модальное окно
        console.log('Быстрый просмотр товара ID:', productId);
    };

    window.addToCart = function(productId) {
        fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                productId: productId,
                quantity: 1
            })
        })
        .then(response => {
            if (response.ok) {
                // Показываем уведомление
                showNotification('Товар добавлен в корзину!', 'success');
                updateCartCounter();
            } else {
                showNotification('Ошибка добавления товара', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Ошибка сети', 'error');
        });
    };

    window.toggleFavorite = async function(productId) {
        const btn = event.currentTarget;
        const icon = btn.querySelector('i');

        try {
            if (icon.classList.contains('far')) {
                // Добавляем в избранное
                const response = await fetch(`/api/favorites/${productId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                if (response.ok) {
                    icon.classList.remove('far');
                    icon.classList.add('fas');
                    icon.style.color = '#ff4757';
                    showNotification('Добавлено в избранное', 'success');
                } else {
                    throw new Error('Ошибка добавления в избранное');
                }
            } else {
                // Удаляем из избранного
                const response = await fetch(`/api/favorites/${productId}`, {
                    method: 'DELETE'
                });

                if (response.ok) {
                    icon.classList.remove('fas');
                    icon.classList.add('far');
                    icon.style.color = '';
                    showNotification('Удалено из избранного', 'info');
                } else {
                    throw new Error('Ошибка удаления из избранного');
                }
            }

            // Обновляем счетчик избранного на всех страницах
            updateFavoriteCounterOnAllPages();

        } catch (error) {
            console.error('Error:', error);
            showNotification('Ошибка операции', 'error');
        }
    };

    // Функция для обновления счетчика на всех страницах
    async function updateFavoriteCounterOnAllPages() {
        try {
            const response = await fetch('/api/favorites/count');
            if (response.ok) {
                const count = await response.text();

                // Обновляем счетчики везде, где они есть
                document.querySelectorAll('.favorite-count').forEach(counter => {
                    counter.textContent = count;
                    counter.classList.add('pulse');
                    setTimeout(() => {
                        counter.classList.remove('pulse');
                    }, 500);
                });
            }
        } catch (error) {
            console.error('Ошибка обновления счетчика избранного:', error);
        }
    }

    function showNotification(message, type = 'info') {
        // Создаем уведомление
        const notification = document.createElement('div');
        notification.className = `alert alert-${type === 'success' ? 'success' : type === 'error' ? 'danger' : 'info'} alert-dismissible fade show`;
        notification.style.position = 'fixed';
        notification.style.top = '20px';
        notification.style.right = '20px';
        notification.style.zIndex = '9999';
        notification.style.minWidth = '300px';
        notification.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        document.body.appendChild(notification);

        // Автоматически удаляем через 3 секунды
        setTimeout(() => {
            notification.remove();
        }, 3000);
    }

    function updateCartCounter() {
        const cartCount = document.querySelector('.cart-count');
        if (cartCount) {
            let count = parseInt(cartCount.textContent) || 0;
            cartCount.textContent = count + 1;
            cartCount.classList.add('pulse');
            setTimeout(() => {
                cartCount.classList.remove('pulse');
            }, 500);
        }
    }

    // Запуск
    init();
});