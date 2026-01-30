(() => {
  const API = {
    categories: "/api/categories",
    brands: "/api/brands",
    productsQuery: "/api/products/query",
    cartAdd: "/api/cart/add",
    cartCount: "/api/cart/count",
  };

  const $ = (sel) => document.querySelector(sel);
  const $$ = (sel) => Array.from(document.querySelectorAll(sel));

  function fmtMoneyRub(v) {
    try {
      return Number(v).toLocaleString("ru-RU") + " ₽";
    } catch {
      return v + " ₽";
    }
  }

  async function fetchJson(url, opts) {
    const res = await fetch(url, {
      headers: { Accept: "application/json", "Content-Type": "application/json" },
      ...opts,
    });
    if (!res.ok) throw new Error(`HTTP ${res.status} for ${url}`);
    return res.json();
  }

  function selectedIds(rootSel) {
    const root = $(rootSel);
    if (!root) return [];
    return Array.from(root.querySelectorAll('input[type="checkbox"]:checked')).map((x) => Number(x.value));
  }

  function buildCheckbox(item, prefix) {
    const label = document.createElement("label");
    label.className = "custom-checkbox";

    const input = document.createElement("input");
    input.type = "checkbox";
    input.value = String(item.id);
    input.id = `${prefix}_${item.id}`;

    const box = document.createElement("span");
    box.className = "checkBox";
    const t = document.createElement("div");
    t.className = "transition";
    box.appendChild(t);

    const text = document.createElement("span");
    text.className = "check-label";
    text.textContent = item.name;

    label.appendChild(input);
    label.appendChild(box);
    label.appendChild(text);
    return label;
  }

  function getInStockValue() {
    const a = $("#available")?.checked;
    const o = $("#order")?.checked;
    if (a && !o) return true;
    if (!a && o) return false;
    return null;
  }

  function updatePriceLabel() {
    const slider = $("#maxPriceSlider");
    const spans = $$(".price-values span");
    if (!slider || spans.length < 2) return;
    spans[1].textContent = fmtMoneyRub(Number(slider.value || "0"));
  }

  function productImageByCategory(categoryId) {
    const map = {
      1: "/assets/engine.jpg",
      2: "/assets/transmission.jpg",
      3: "/assets/podveska.jpg",
      4: "/assets/tormoz.jpg",
      5: "/assets/electric.jpg",
      6: "/assets/kuzov.jpg",
    };
    return map[categoryId] || "/assets/background.jpg";
  }

  async function loadFilters() {
    const catRoot = $("#filterCategories");
    const brandRoot = $("#filterBrands");
    if (catRoot) catRoot.innerHTML = "";
    if (brandRoot) brandRoot.innerHTML = "";

    const [categories, brands] = await Promise.all([fetchJson(API.categories), fetchJson(API.brands)]);
    categories.forEach((c) => catRoot?.appendChild(buildCheckbox(c, "cat")));
    brands.forEach((b) => brandRoot?.appendChild(buildCheckbox(b, "brand")));
  }

  function buildQueryParams() {
    const params = new URLSearchParams();

    const q = $("#globalSearchInput")?.value?.trim();
    if (q) params.set("q", q);

    selectedIds("#filterCategories").forEach((id) => params.append("categoryIds", String(id)));
    selectedIds("#filterBrands").forEach((id) => params.append("brandIds", String(id)));

    const maxPrice = Number($("#maxPriceSlider")?.value || "0");
    if (maxPrice > 0) params.set("maxPrice", String(maxPrice));
    params.set("minPrice", "0");

    const inStock = getInStockValue();
    if (inStock !== null) params.set("inStock", String(inStock));

    params.set("page", "0");
    params.set("size", "12");
    params.set("sort", "name_asc");

    return params;
  }

  function renderProducts(products) {
    const grid = $("#productsGrid");
    if (!grid) return;
    grid.innerHTML = "";

    if (!products || products.length === 0) {
      const empty = document.createElement("div");
      empty.className = "col-12";
      empty.innerHTML = `<div class="text-center text-muted py-5">Товары не найдены</div>`;
      grid.appendChild(empty);
      return;
    }

    products.forEach((p) => {
      const col = document.createElement("div");
      col.className = "col-md-4";

      const qty = Number(p.quantity ?? 0);
      const badgeText = qty > 0 ? "В наличии" : "Под заказ";
      const badgeStyle = qty > 0 ? "" : ' style="background: #2d3748;"';
      const img = productImageByCategory(p.categoryId);

      col.innerHTML = `
        <div class="product-card">
          <span class="product-badge"${badgeStyle}>${badgeText}</span>
          <div class="product-image" style="background-image: url('${img}');">
            <div class="product-overlay">
              <a class="quick-view-btn" href="/products">Каталог</a>
            </div>
          </div>
          <div class="product-content">
            <h5 class="product-title">${p.name ?? ""}</h5>
            <div class="product-article">Артикул: ${p.article ?? ""}</div>
            <div class="product-price">${fmtMoneyRub(p.price ?? 0)}</div>
            <div class="product-actions">
              <button class="btn-add-cart" type="button" data-product-id="${p.id}" ${qty > 0 ? "" : "disabled"}>
                <i class="fas fa-cart-plus me-2"></i>В корзину
              </button>
              <button class="btn-favorite" type="button"><i class="far fa-heart"></i></button>
            </div>
          </div>
        </div>
      `;
      grid.appendChild(col);
    });

    grid.querySelectorAll(".btn-add-cart").forEach((btn) => {
      btn.addEventListener("click", async () => {
        const productId = Number(btn.getAttribute("data-product-id"));
        await fetchJson(API.cartAdd, { method: "POST", body: JSON.stringify({ productId, quantity: 1 }) });
        await refreshCartCount();
      });
    });
  }

  async function refreshCartCount() {
    const count = await fetchJson(API.cartCount);
    const badge = $("#cartCountBadge");
    if (badge) badge.textContent = String(count ?? 0);
  }

  async function fetchAndRender() {
    const params = buildQueryParams();
    const url = `${API.productsQuery}?${params.toString()}`;
    const res = await fetchJson(url);
    renderProducts(res.content || []);
  }

  function wireGlobalSearch() {
    const input = $("#globalSearchInput");
    if (!input) return;
    input.addEventListener("keydown", (e) => {
      if (e.key === "Enter") {
        e.preventDefault();
        // If we are on catalog page, run search; otherwise go to catalog.
        if ($("#productsGrid")) {
          fetchAndRender().catch(console.error);
        } else {
          window.location.href = "/products";
        }
      }
    });
  }

  function wireCatalogUi() {
    $("#applyFiltersBtn")?.addEventListener("click", (e) => {
      e.preventDefault();
      fetchAndRender().catch(console.error);
    });
    $("#resetFiltersBtn")?.addEventListener("click", (e) => {
      e.preventDefault();
      $("#filterCategories")?.querySelectorAll('input[type="checkbox"]').forEach((x) => (x.checked = false));
      $("#filterBrands")?.querySelectorAll('input[type="checkbox"]').forEach((x) => (x.checked = false));
      if ($("#maxPriceSlider")) $("#maxPriceSlider").value = "50000";
      if ($("#available")) $("#available").checked = true;
      if ($("#order")) $("#order").checked = false;
      updatePriceLabel();
      fetchAndRender().catch(console.error);
    });

    $("#maxPriceSlider")?.addEventListener("input", updatePriceLabel);
    updatePriceLabel();
  }

  document.addEventListener("DOMContentLoaded", async () => {
    wireGlobalSearch();
    try {
      await refreshCartCount();
    } catch {}

    if ($("#productsGrid")) {
      try {
        await loadFilters();
        wireCatalogUi();
        await fetchAndRender();
      } catch (e) {
        console.error(e);
      }
    }

    // Bind add-to-cart buttons on homepage cards (SSR)
    document.querySelectorAll(".btn-add-cart[data-product-id]").forEach((btn) => {
      btn.addEventListener("click", async () => {
        const productId = Number(btn.getAttribute("data-product-id"));
        await fetchJson(API.cartAdd, { method: "POST", body: JSON.stringify({ productId, quantity: 1 }) });
        await refreshCartCount();
      });
    });
  });
})();






