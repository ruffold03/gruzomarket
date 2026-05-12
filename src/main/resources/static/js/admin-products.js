document.addEventListener('DOMContentLoaded', function () {
    // Visibility toggle logic
    document.querySelectorAll('.visibility-toggle').forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            const productId = this.getAttribute('data-id');
            const isVisible = this.checked;

            fetch(`/admin/products/${productId}/toggle-visibility?visible=${isVisible}`, {
                method: 'POST'
            }).then(response => {
                if (response.ok) {
                    console.log(`Product ${productId} visibility updated to ${isVisible}`);
                } else {
                    alert('Ошибка при обновлении видимости');
                    this.checked = !isVisible; // Revert
                }
            }).catch(error => {
                console.error('Error:', error);
                alert('Ошибка сети');
                this.checked = !isVisible; // Revert
            });
        });
    });
});