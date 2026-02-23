(() => {
  const API = {
    cart: "/api/cart",
    cartCount: "/api/cart/count",
    update: "/api/cart/update",
    remove: (id) => `/api/cart/remove/${id}`,
    clear: "/api/cart/clear",
    checkout: "/api/cart/checkout",
  };

  const $ = (sel) => document.querySelector(sel);

  async function fetchJson(url, opts) {
    const res = await fetch(url, {
      headers: { Accept: "application/json", "Content-Type": "application/json" },
      ...opts,
    });
    if (!res.ok) throw new Error(`HTTP ${res.status} for ${url}`);
    return res.json();
  }

  function fmtMoneyRub(v) {
    try {
      return Number(v).toLocaleString("ru-RU") + " ₽";
    } catch {
      return v + " ₽";
    }
  }

  function renderCart(summary) {
    const root = $("#cartItems");
    if (!root) return;
    root.innerHTML = "";

    const items = summary?.items || [];
    if (items.length === 0) {
      root.innerHTML = `<div class="text-muted">Корзина пуста. Перейдите в <a href="/products">каталог</a>.</div>`;
      $("#cartTotal").textContent = "0 ₽";
      return;
    }

    items.forEach((it) => {
      const row = document.createElement("div");
      row.className = "cart-item-row d-flex align-items-center justify-content-between py-3 border-bottom";
      row.innerHTML = `
        <div class="cart-item-info me-3">
          <div class="cart-item-name fw-bold mb-1">${it.name ?? ""}</div>
          <div class="cart-item-meta d-flex gap-3 small text-muted">
            <span>Артикул: ${it.article ?? "N/A"}</span>
            <span>Цена: ${fmtMoneyRub(it.unitPrice ?? 0)}</span>
          </div>
        </div>
        <div class="d-flex align-items-center gap-4">
          <div class="qty-picker-container">
            <div class="qty-picker d-flex align-items-center">
              <button class="qty-btn" type="button" data-qty-action="dec" data-product-id="${it.productId}">
                <i class="fas fa-minus"></i>
              </button>
              <input class="qty-input" type="number" min="1" max="${it.quantityAvailable}" value="${it.quantity}" 
                     data-qty-product="${it.productId}" readonly>
              <button class="qty-btn" type="button" data-qty-action="inc" data-product-id="${it.productId}" 
                      ${it.quantity >= it.quantityAvailable ? 'disabled' : ''}>
                <i class="fas fa-plus"></i>
              </button>
            </div>
            <div class="qty-stock-hint small text-muted mt-1 text-center">
              ${it.quantityAvailable > 0 ? `В наличии: ${it.quantityAvailable}` : '<span class="text-danger">Нет в наличии</span>'}
            </div>
          </div>
          
          <div class="cart-item-total fw-bold text-end" style="min-width:120px">
            ${fmtMoneyRub(it.lineTotal ?? 0)}
          </div>
          
          <button class="cart-remove-btn" type="button" data-remove-product="${it.productId}">
            <i class="fas fa-trash-alt"></i>
          </button>
        </div>
      `;
      root.appendChild(row);
    });

    $("#cartTotal").textContent = fmtMoneyRub(summary.totalAmount ?? 0);

    // Обработчики кнопок количества
    root.querySelectorAll(".qty-btn").forEach(btn => {
      btn.addEventListener("click", async () => {
        const productId = Number(btn.getAttribute("data-product-id"));
        const action = btn.getAttribute("data-qty-action");
        const input = root.querySelector(`input[data-qty-product="${productId}"]`);
        let currentQty = Number(input.value);
        const maxQty = Number(input.getAttribute("max"));

        if (action === "inc" && currentQty < maxQty) {
          currentQty++;
        } else if (action === "dec" && currentQty > 1) {
          currentQty--;
        } else {
          return; // Ничего не делаем
        }

        const updated = await fetchJson(API.update, { method: "POST", body: JSON.stringify({ productId, quantity: currentQty }) });
        await refreshCartCount();
        renderCart(updated);
      });
    });

    root.querySelectorAll("[data-remove-product]").forEach((btn) => {
      btn.addEventListener("click", async () => {
        const productId = Number(btn.getAttribute("data-remove-product"));
        const updated = await fetchJson(API.remove(productId), { method: "DELETE" });
        await refreshCartCount();
        renderCart(updated);
      });
    });
  }

  async function refreshCartCount() {
    try {
      const count = await fetchJson(API.cartCount);
      const badge = $("#cartCountBadge");
      if (badge) badge.textContent = String(count ?? 0);
    } catch (err) {
      console.warn("Не удалось обновить счетчик корзины:", err);
    }
  }

  async function loadCart() {
    const summary = await fetchJson(API.cart);
    renderCart(summary);
  }

  function setupPhoneMask() {
    const phoneInput = $("#checkoutPhone");
    if (!phoneInput) return;

    // Инициализация при загрузке (если есть значение)
    if (phoneInput.value && phoneInput.value.length >= 1) {
      phoneInput.value = formatPhone(phoneInput.value);
    } else {
      phoneInput.value = "+7 ";
    }

    phoneInput.addEventListener("input", function (e) {
      let numbers = this.value.replace(/\D/g, "");

      // Если номер начинается не с 7, корректируем (для копипаста)
      if (numbers.length > 0 && numbers.charAt(0) !== "7") {
        numbers = "7" + numbers.substring(1);
      }

      numbers = numbers.substring(0, 11);
      this.value = formatPhone(numbers);
    });

    phoneInput.addEventListener('keydown', function (e) {
      if (e.key === 'Backspace') {
        const cursorPos = this.selectionStart;
        const value = this.value;

        // Если курсор находится сразу после символа форматирования, сдвигаем его к цифре
        if (cursorPos > 0 && !/\d/.test(value.charAt(cursorPos - 1))) {
          // Запрещаем удалять "+7"
          if (cursorPos <= 3) {
            e.preventDefault();
            return;
          }

          // Сдвигаем курсор назад до цифры
          let newPos = cursorPos - 1;
          while (newPos > 3 && !/\d/.test(value.charAt(newPos - 1))) {
            newPos--;
          }
          this.setSelectionRange(newPos, newPos);
        }
      }
    });

    phoneInput.addEventListener("focus", function () {
      if (this.value === "") this.value = "+7 ";
    });

    // Вспомогательная функция формата
    function formatPhone(numbers) {
      numbers = numbers.replace(/\D/g, "");
      if (numbers.length === 0) return "+7 ";
      if (numbers.length === 1) return "+7 ";

      let formatted = "+7";
      if (numbers.length > 1) {
        formatted += " (" + numbers.substring(1, 4);
      }
      if (numbers.length >= 4) {
        formatted += ") " + numbers.substring(4, 7);
      }
      if (numbers.length >= 7) {
        formatted += "-" + numbers.substring(7, 9);
      }
      if (numbers.length >= 9) {
        formatted += "-" + numbers.substring(9, 11);
      }
      return formatted;
    }
  }

  function wireCartUi() {
    $("#clearCartBtn")?.addEventListener("click", async () => {
      const summary = await fetchJson(API.clear, { method: "POST" });
      await refreshCartCount();
      renderCart(summary);
    });

    $("#checkoutForm")?.addEventListener("submit", async (e) => {
      e.preventDefault();
      const form = e.target;
      const btn = form.querySelector('button[type="submit"]');
      const resultDiv = $("#checkoutResult");

      const fd = new FormData(form);

      // Очищаем телефон перед отправкой (только цифры)
      const phoneInput = $("#checkoutPhone");
      const cleanPhone = phoneInput ? phoneInput.value.replace(/\D/g, "") : "";

      // Базовая валидация на клиенте
      const currentCartCount = await fetchJson(API.cartCount);
      if (currentCartCount === 0) {
        resultDiv.className = "text-danger small mt-3";
        resultDiv.textContent = "Ваша корзина пуста. Добавьте товары перед оформлением.";
        return;
      }

      if (cleanPhone.length < 11) {
        resultDiv.className = "text-danger small mt-3";
        resultDiv.textContent = "Пожалуйста, введите корректный номер телефона (11 цифр).";
        phoneInput?.focus();
        return;
      }

      fd.set("phone", cleanPhone);
      const payload = Object.fromEntries(fd.entries());

      try {
        // Блокируем кнопку
        btn.disabled = true;
        const originalBtnText = btn.textContent;
        btn.textContent = "Отправка...";
        resultDiv.textContent = "";

        const res = await fetch(API.checkout, {
          method: "POST",
          headers: { Accept: "application/json", "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });

        const data = await res.json();

        if (!res.ok) {
          throw new Error(data.message || `Ошибка сервера (${res.status})`);
        }

        await refreshCartCount();
        resultDiv.className = "text-success fw-bold mt-3";
        resultDiv.textContent = `Заявка успешно отправлена! Номер заказа: #${data.id}. Мы свяжемся с вами в ближайшее время.`;

        // Очищаем форму
        form.reset();
        if (phoneInput) phoneInput.value = "+7 ";

        await loadCart();
      } catch (err) {
        resultDiv.className = "text-danger small mt-3";
        resultDiv.textContent = err.message || "Ошибка отправки заявки. Попробуйте еще раз позже.";
        console.error(err);
      } finally {
        btn.disabled = false;
        btn.textContent = "Отправить заявку";
      }
    });
  }

  document.addEventListener("DOMContentLoaded", async () => {
    if (!$("#cartItems")) return;
    setupPhoneMask();
    wireCartUi();
    await refreshCartCount();
    await loadCart();
  });
})();








