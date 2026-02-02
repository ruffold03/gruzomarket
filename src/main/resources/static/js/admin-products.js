document.addEventListener('DOMContentLoaded', function() {
    const tableRows = document.querySelectorAll('tbody tr');
    const filterName = document.getElementById('filterName');
    const filterArticle = document.getElementById('filterArticle');
    const filterMinPrice = document.getElementById('filterMinPrice');
    const filterMaxPrice = document.getElementById('filterMaxPrice');
    const filterNoArticle = document.getElementById('filterNoArticle');
    const filterNoName = document.getElementById('filterNoName');

    // Функция применения фильтров
    function applyFilters() {
        const nameQuery = filterName.value.trim().toLowerCase();
        const articleQuery = filterArticle.value.trim().toLowerCase();
        const minPrice = parseFloat(filterMinPrice.value) || 0;
        const maxPrice = parseFloat(filterMaxPrice.value) || Infinity;
        const noArticle = filterNoArticle.checked;
        const noName = filterNoName.checked;

        tableRows.forEach(row => {
            const name = row.querySelector('td:nth-child(2)').textContent.trim().toLowerCase();
            const article = row.querySelector('td:nth-child(3)').textContent.trim().toLowerCase();
            const priceText = row.querySelector('td:nth-child(4)').textContent.replace(/[^0-9.,]/g, '').replace(',', '.');
            const price = parseFloat(priceText) || 0;
            const quantity = row.querySelector('td:nth-child(5)').textContent.trim();

            let show = true;

            // Фильтр по названию
            if (nameQuery && !name.includes(nameQuery)) show = false;

            // Фильтр по артикулу
            if (articleQuery && !article.includes(articleQuery)) show = false;

            // Фильтр по цене
            if (price < minPrice || price > maxPrice) show = false;

            // Только без артикула
            if (noArticle && article !== '') show = false;

            // Только без названия
            if (noName && name !== '') show = false;

            // Показываем/скрываем строку
            row.style.display = show ? '' : 'none';
        });
    }

    // События на изменения
    [filterName, filterArticle, filterMinPrice, filterMaxPrice].forEach(input => {
        input.addEventListener('input', applyFilters);
    });
    [filterNoArticle, filterNoName].forEach(checkbox => {
        checkbox.addEventListener('change', applyFilters);
    });

    // Начальный вызов (если нужно)
    applyFilters();
});