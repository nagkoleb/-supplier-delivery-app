let suppliersCache = [];
let productsCache = [];
let deliveryItems = [];

async function loadSuppliers() {
    const response = await fetch("/api/suppliers");
    const suppliers = await response.json();

    suppliersCache = suppliers;

    const table = document.getElementById("suppliersTable");
    table.innerHTML = "";

    suppliers.forEach(supplier => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${supplier.id}</td>
            <td>${supplier.name}</td>
        `;

        table.appendChild(row);
    });

    fillSupplierSelects();
}


async function createSupplier() {
    const name = document
        .getElementById("supplierName").value;

    const response = await fetch("/api/suppliers", {
        method: "POST", headers: {
            "Content-Type": "application/json"
        }, body: JSON.stringify({
            name: name
        })
    });

    const message = document.getElementById("supplierMessage");

    if (!response.ok) {
        const error = await response.json();

        message.textContent = getErrorMessage(error);

        return;
    }

    message.textContent = "Поставщик создан";

    document.getElementById("supplierName").value = "";

    await loadSuppliers();
}


async function loadProducts() {
    const response = await fetch("/api/products");
    const products = await response.json();

    productsCache = products;

    const table = document.getElementById("productsTable");
    table.innerHTML = "";

    products.forEach(product => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${product.id}</td>
            <td>${product.name}</td>
            <td>${product.type}</td>
        `;

        table.appendChild(row);
    });

    fillProductSelects();
}


async function createProduct() {
    const name = document
        .getElementById("productName").value;

    const type = document
        .getElementById("productType").value;

    const response = await fetch("/api/products", {
        method: "POST", headers: {
            "Content-Type": "application/json"
        }, body: JSON.stringify({
            name: name, type: type
        })
    });

    const message = document.getElementById("productMessage");

    if (!response.ok) {
        const error = await response.json();

        message.textContent = getErrorMessage(error);

        return;
    }

    message.textContent = "Продукт создан";

    document.getElementById("productName").value = "";

    await loadProducts();
}


async function loadReport() {
    const dateFrom = document
        .getElementById("dateFrom").value;

    const dateTo = document
        .getElementById("dateTo").value;

    const response = await fetch(`/api/reports/deliveries` + `?dateFrom=${dateFrom}` + `&dateTo=${dateTo}`);
    const message = document.getElementById("reportMessage");

    if (!response.ok) {
        const error = await response.json();

        message.textContent = getErrorMessage(error);
        return;
    }

    const report = await response.json();

    const table = document.getElementById("reportTable");

    table.innerHTML = "";
    message.textContent = "";

    report.forEach(item => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${item.supplierName}</td>
            <td>${item.productName}</td>
            <td>${item.totalWeight}</td>
            <td>${item.totalCost}</td>
        `;

        table.appendChild(row);
    });
}

function fillSupplierSelects() {
    const selects = [document.getElementById("priceSupplier"), document.getElementById("deliverySupplier")];

    selects.forEach(select => {
        select.innerHTML = "";

        suppliersCache.forEach(supplier => {
            const option = document.createElement("option");

            option.value = supplier.id;
            option.textContent = supplier.name;

            select.appendChild(option);
        });
    });
}


function fillProductSelects() {
    const selects = [document.getElementById("priceProduct"), document.getElementById("deliveryProduct")];

    selects.forEach(select => {
        select.innerHTML = "";

        productsCache.forEach(product => {
            const option = document.createElement("option");

            option.value = product.id;
            option.textContent = product.name;

            select.appendChild(option);
        });
    });
}

async function loadPrices() {
    const response = await fetch("/api/supplier-prices");
    const prices = await response.json();

    const table = document.getElementById("pricesTable");
    table.innerHTML = "";

    prices.forEach(price => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${price.supplierName}</td>
            <td>${price.productName}</td>
            <td>${price.price}</td>
            <td>${price.dateFrom}</td>
            <td>${price.dateTo ?? "—"}</td>
        `;

        table.appendChild(row);
    });
}


async function createSupplierPrice() {
    const supplierId = Number(document.getElementById("priceSupplier").value);

    const productId = Number(document.getElementById("priceProduct").value);

    const price = Number(document.getElementById("priceValue").value);

    const dateFrom = document
        .getElementById("priceDateFrom").value;

    const response = await fetch("/api/supplier-prices", {
        method: "POST", headers: {
            "Content-Type": "application/json"
        }, body: JSON.stringify({
            supplierId: supplierId, productId: productId, price: price, dateFrom: dateFrom, dateTo: null
        })
    });

    const message = document.getElementById("priceMessage");

    if (!response.ok) {
        const error = await response.json();
        message.textContent = getErrorMessage(error);
        return;
    }

    message.textContent = "Цена добавлена";

    document.getElementById("priceValue").value = "";
    document.getElementById("priceDateFrom").value = "";

    await loadPrices();
}

function addDeliveryItem() {
    const productId = Number(document.getElementById("deliveryProduct").value);

    const weight = Number(document.getElementById("deliveryWeight").value);

    if (!productId || weight <= 0) {
        return;
    }

    const product = productsCache.find(item => item.id === productId);

    deliveryItems.push({
        productId: productId, productName: product.name, weight: weight
    });

    document.getElementById("deliveryWeight").value = "";

    renderDeliveryItems();
}


function renderDeliveryItems() {
    const table = document.getElementById("deliveryItemsTable");

    table.innerHTML = "";

    deliveryItems.forEach((item, index) => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${item.productName}</td>
            <td>${item.weight}</td>
            <td>
                <button onclick="removeDeliveryItem(${index})">
                    Удалить
                </button>
            </td>
        `;

        table.appendChild(row);
    });
}


function removeDeliveryItem(index) {
    deliveryItems.splice(index, 1);
    renderDeliveryItems();
}

async function createDelivery() {
    const supplierId = Number(document.getElementById("deliverySupplier").value);

    const deliveryDate = document
        .getElementById("deliveryDate").value;

    const message = document.getElementById("deliveryMessage");

    if (deliveryItems.length === 0) {
        message.textContent = "Добавьте хотя бы один товар";

        return;
    }

    const items = deliveryItems.map(item => ({
        productId: item.productId, weight: item.weight
    }));

    const response = await fetch("/api/deliveries", {
        method: "POST", headers: {
            "Content-Type": "application/json"
        }, body: JSON.stringify({
            supplierId: supplierId, deliveryDate: deliveryDate, items: items
        })
    });

    if (!response.ok) {
        const error = await response.json();
        message.textContent = getErrorMessage(error);
        return;
    }

    const result = await response.json();

    message.textContent = `Поставка создана. ID: ${result.id}`;

    deliveryItems = [];
    renderDeliveryItems();

    document.getElementById("deliveryDate").value = "";
}

function getErrorMessage(error) {
    if (error.error) {
        return error.error;
    }

    return Object.values(error).join(", ");
}


loadSuppliers();
loadProducts();
loadPrices();