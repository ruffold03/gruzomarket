// Search functionality
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    }

    // Filter functionality
    setupFilters();
    
    // Cart functionality
    updateCartBadge();
});

function performSearch() {
    const searchTerm = document.getElementById('searchInput').value;
    if (searchTerm.trim()) {
        window.location.href = `/products?search=${encodeURIComponent(searchTerm)}`;
    }
}

function setupFilters() {
    const filterInputs = document.querySelectorAll('.filter-input, .checkbox-item input, .price-input');
    filterInputs.forEach(input => {
        input.addEventListener('change', function() {
            applyFilters();
        });
    });
}

function applyFilters() {
    const params = new URLSearchParams();
    
    // Category filter
    const selectedCategories = Array.from(document.querySelectorAll('.checkbox-item input[type="checkbox"]:checked'))
        .map(cb => cb.value);
    if (selectedCategories.length > 0) {
        params.append('categories', selectedCategories.join(','));
    }
    
    // Price range
    const minPrice = document.getElementById('minPrice')?.value;
    const maxPrice = document.getElementById('maxPrice')?.value;
    if (minPrice) params.append('minPrice', minPrice);
    if (maxPrice) params.append('maxPrice', maxPrice);
    
    // Brand filter
    const selectedBrands = Array.from(document.querySelectorAll('#brandFilter input[type="checkbox"]:checked'))
        .map(cb => cb.value);
    if (selectedBrands.length > 0) {
        params.append('brands', selectedBrands.join(','));
    }
    
    // Sort
    const sortBy = document.getElementById('sortSelect')?.value;
    if (sortBy) params.append('sort', sortBy);
    
    const currentUrl = new URL(window.location.href);
    const baseUrl = currentUrl.pathname;
    window.location.href = `${baseUrl}?${params.toString()}`;
}

function addToCart(productId, quantity = 1) {
    fetch(`/api/cart/add`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            productId: productId,
            quantity: quantity
        })
    })
    .then(response => response.json())
    .then(data => {
        updateCartBadge();
        showNotification('Товар добавлен в корзину!', 'success');
    })
    .catch(error => {
        showNotification('Ошибка при добавлении товара', 'error');
    });
}

function updateCartBadge() {
    fetch('/api/cart/count')
        .then(response => response.json())
        .then(count => {
            const badge = document.querySelector('.cart-badge');
            if (badge) {
                badge.textContent = count;
                badge.style.display = count > 0 ? 'flex' : 'none';
            }
        })
        .catch(() => {
            // Ignore errors
        });
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background-color: ${type === 'success' ? '#10b981' : '#ef4444'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 10000;
        animation: slideIn 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Add CSS animation
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
















