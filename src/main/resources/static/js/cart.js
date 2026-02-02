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
      row.className = "d-flex align-items-center justify-content-between py-2 border-bottom";
      row.innerHTML = `
        <div class="me-3">
          <div class="fw-bold">${it.name ?? ""}</div>
          <div class="text-muted small">Артикул: ${it.article ?? ""}</div>
          <div class="text-muted small">Цена: ${fmtMoneyRub(it.unitPrice ?? 0)}</div>
        </div>
        <div class="d-flex align-items-center gap-2">
          <input class="form-control form-control-sm" style="width:90px" type="number" min="1" value="${it.quantity}" data-qty-product="${it.productId}">
          <div class="fw-bold" style="min-width:120px;text-align:right">${fmtMoneyRub(it.lineTotal ?? 0)}</div>
          <button class="btn btn-sm btn-outline-danger" type="button" data-remove-product="${it.productId}">
            <i class="fas fa-times"></i>
          </button>
        </div>
      `;
      root.appendChild(row);
    });

    $("#cartTotal").textContent = fmtMoneyRub(summary.totalAmount ?? 0);

    root.querySelectorAll("[data-qty-product]").forEach((inp) => {
      inp.addEventListener("change", async () => {
        const productId = Number(inp.getAttribute("data-qty-product"));
        const quantity = Number(inp.value || "1");
        const updated = await fetchJson(API.update, { method: "POST", body: JSON.stringify({ productId, quantity }) });
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
    const count = await fetchJson(API.cartCount);
    const badge = $("#cartCountBadge");
    if (badge) badge.textContent = String(count ?? 0);
  }

  async function loadCart() {
    const summary = await fetchJson(API.cart);
    renderCart(summary);
  }

  function wireCartUi() {
    $("#clearCartBtn")?.addEventListener("click", async () => {
      const summary = await fetchJson(API.clear, { method: "POST" });
      await refreshCartCount();
      renderCart(summary);
    });

    $("#checkoutForm")?.addEventListener("submit", async (e) => {
      e.preventDefault();
      const fd = new FormData(e.target);
      const payload = Object.fromEntries(fd.entries());
      try {
        const order = await fetchJson(API.checkout, { method: "POST", body: JSON.stringify(payload) });
        await refreshCartCount();
        $("#checkoutResult").textContent = `Заявка отправлена. Номер: ${order.id}. Мы свяжемся с вами.`;
        await loadCart();
      } catch (err) {
        $("#checkoutResult").textContent = "Ошибка отправки заявки. Проверьте телефон/email и корзину.";
        console.error(err);
      }
    });
  }

  document.addEventListener("DOMContentLoaded", async () => {
    if (!$("#cartItems")) return;
    wireCartUi();
    await refreshCartCount();
    await loadCart();
  });
})();







