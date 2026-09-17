document.addEventListener("DOMContentLoaded", () => {
    const app = document.getElementById("productApp");

    if (!app) {
        return;
    }

    const productApi = app.dataset.productApi;
    const categoryApi = app.dataset.categoryApi;
    const loginUrl = app.dataset.loginUrl;

    const state = {
        keyword: "",
        page: 0,
        size: 5,
        totalPages: 0
    };

    const searchForm =
        document.getElementById("productSearchForm");

    const keywordInput =
        document.getElementById("productKeyword");

    const pageSizeSelect =
        document.getElementById("productPageSize");

    const resetSearchButton =
        document.getElementById("btnResetProductSearch");

    const tableBody =
        document.getElementById("productTableBody");

    const loadingElement =
        document.getElementById("productLoading");

    const emptyElement =
        document.getElementById("productEmpty");

    const pageInfoElement =
        document.getElementById("productPageInfo");

    const paginationElement =
        document.getElementById("productPagination");

    const messageElement =
        document.getElementById("productMessage");

    const dialog =
        document.getElementById("productDialog");

    const dialogTitle =
        document.getElementById("productDialogTitle");

    const productForm =
        document.getElementById("productForm");

    const formError =
        document.getElementById("productFormError");

    const productIdInput =
        document.getElementById("productId");

    const productNameInput =
        document.getElementById("productName");

    const priceInput =
        document.getElementById("productPrice");

    const categoryInput =
        document.getElementById("productCategory");

    const descriptionInput =
        document.getElementById("productDescription");

    const imageFileInput =
        document.getElementById("productImageFile");

    const imagePreview =
        document.getElementById("productImagePreview");

    const openCreateButton =
        document.getElementById("btnOpenCreateProduct");

    const saveButton =
        document.getElementById("btnSaveProduct");

    /**
     * Gọi REST API và xử lý JSON chung.
     */
    async function apiRequest(url, options = {}) {
        const headers = new Headers(options.headers || {});
        const method = (options.method || "GET").toUpperCase();

        if (method !== "GET" && method !== "HEAD") {
            const csrfToken = document
                .querySelector('meta[name="_csrf"]')
                ?.getAttribute("content");

            const csrfHeader = document
                .querySelector('meta[name="_csrf_header"]')
                ?.getAttribute("content");

            if (csrfToken && csrfHeader) {
                headers.set(csrfHeader, csrfToken);
            }
        }

        const response = await fetch(url, {
            ...options,
            headers
        });

        const contentType =
            response.headers.get("content-type") || "";

        let payload = null;

        if (contentType.includes("application/json")) {
            payload = await response.json();
        }

        if (!response.ok) {
            let message =
                payload?.message ||
                `Yêu cầu thất bại, HTTP ${response.status}`;

            if (payload?.errors) {
                const validationMessage =
                    Object.values(payload.errors).join("\n");

                if (validationMessage) {
                    message = validationMessage;
                }
            }

            const error = new Error(message);
            error.status = response.status;
            error.payload = payload;

            throw error;
        }

        return payload;
    }

    /**
     * Tải danh sách Product.
     */
    async function loadProducts() {
        setLoading(true);

        const parameters = new URLSearchParams({
            keyword: state.keyword,
            page: state.page.toString(),
            size: state.size.toString()
        });

        try {
            const result = await apiRequest(
                `${productApi}?${parameters.toString()}`
            );

            const pageData = result.data;

            state.page = pageData.page;
            state.size = pageData.size;
            state.totalPages = pageData.totalPages;

            renderTable(pageData.content);
            renderPageInfo(pageData);
            renderPagination(pageData);
        } catch (error) {
            tableBody.innerHTML = "";
            emptyElement.hidden = false;
            pageInfoElement.textContent = "";
            paginationElement.innerHTML = "";

            showMessage(error.message, "error");
            handleAuthenticationError(error);
        } finally {
            setLoading(false);
        }
    }

    /**
     * Render dữ liệu Product.
     */
    function renderTable(products) {
        tableBody.innerHTML = "";

        if (!products || products.length === 0) {
            emptyElement.hidden = false;
            return;
        }

        emptyElement.hidden = true;

        tableBody.innerHTML = products.map(product => {
            const id = Number(product.productId);

            const image = product.imageUrl
                ? `
                    <img src="${escapeHtml(product.imageUrl)}"
                         class="product-thumbnail"
                         alt="${escapeHtml(product.productName)}"
                         onerror="this.style.display='none'">
                  `
                : `
                    <span class="no-image">
                        Không có ảnh
                    </span>
                  `;

            return `
                <tr>
                    <td>${id}</td>

                    <td>${image}</td>

                    <td>
                        <strong>
                            ${escapeHtml(product.productName)}
                        </strong>
                    </td>

                    <td>
                        ${escapeHtml(
                            product.categoryName || "Không xác định"
                        )}
                    </td>

                    <td class="product-price">
                        ${formatCurrency(product.price)}
                    </td>

                    <td>
                        ${formatDateTime(product.createdDate)}
                    </td>

                    <td>
                        <div class="action-buttons">
                            <button type="button"
                                    class="btn btn-edit"
                                    data-action="edit"
                                    data-id="${id}">
                                Sửa
                            </button>

                            <button type="button"
                                    class="btn btn-delete"
                                    data-action="delete"
                                    data-id="${id}">
                                Xóa
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join("");
    }

    function renderPageInfo(pageData) {
        if (pageData.totalElements === 0) {
            pageInfoElement.textContent =
                "Không có dữ liệu";
            return;
        }

        pageInfoElement.textContent =
            `Trang ${pageData.page + 1}/${pageData.totalPages}` +
            ` - Tổng ${pageData.totalElements} sản phẩm`;
    }

    /**
     * Render thanh phân trang.
     */
    function renderPagination(pageData) {
        paginationElement.innerHTML = "";

        if (pageData.totalPages <= 1) {
            return;
        }

        paginationElement.appendChild(
            createPageButton(
                "«",
                pageData.page - 1,
                pageData.first,
                false
            )
        );

        const startPage =
            Math.max(0, pageData.page - 2);

        const endPage =
            Math.min(
                pageData.totalPages - 1,
                pageData.page + 2
            );

        for (
            let page = startPage;
            page <= endPage;
            page++
        ) {
            paginationElement.appendChild(
                createPageButton(
                    String(page + 1),
                    page,
                    false,
                    page === pageData.page
                )
            );
        }

        paginationElement.appendChild(
            createPageButton(
                "»",
                pageData.page + 1,
                pageData.last,
                false
            )
        );
    }

    function createPageButton(
        label,
        page,
        disabled,
        active
    ) {
        const button =
            document.createElement("button");

        button.type = "button";
        button.textContent = label;
        button.dataset.page = page.toString();
        button.disabled = disabled;
        button.className = "pagination-button";

        if (active) {
            button.classList.add("active");
        }

        return button;
    }

    /**
     * Lấy Category đang hoạt động cho combobox.
     *
     * selectedId và selectedName được dùng khi sửa Product.
     * Nếu Category cũ đã bị ngừng hoạt động thì vẫn thêm tạm
     * vào combobox để tránh làm mất liên kết hiện tại.
     */
    async function loadCategoryOptions(
        selectedId = null,
        selectedName = null
    ) {
        const result = await apiRequest(categoryApi);
        const categories = result.data || [];

        categoryInput.innerHTML = `
            <option value="">
                -- Chọn danh mục --
            </option>
        `;

        for (const category of categories) {
            const option =
                document.createElement("option");

            option.value =
                String(category.categoryId);

            option.textContent =
                category.categoryName;

            categoryInput.appendChild(option);
        }

        if (selectedId !== null) {
            const selectedValue =
                String(selectedId);

            let selectedOption =
                Array.from(categoryInput.options)
                    .find(option =>
                        option.value === selectedValue
                    );

            /*
             * Product có thể đang thuộc Category đã ngừng hoạt động.
             * Endpoint /options chỉ trả Category hoạt động.
             */
            if (!selectedOption) {
                selectedOption =
                    document.createElement("option");

                selectedOption.value =
                    selectedValue;

                selectedOption.textContent =
                    `${selectedName || "Danh mục cũ"} ` +
                    "(ngừng hoạt động)";

                categoryInput.appendChild(selectedOption);
            }

            categoryInput.value = selectedValue;
        }
    }

    /**
     * Mở form thêm Product.
     */
    async function openCreateDialog() {
        hideMessage();
        hideFormError();

        productForm.reset();
        productIdInput.value = "";

        hideImagePreview();

        dialogTitle.textContent =
            "Thêm sản phẩm";

        saveButton.textContent =
            "Thêm sản phẩm";

        openCreateButton.disabled = true;

        try {
            await loadCategoryOptions();

            dialog.showModal();
            productNameInput.focus();
        } catch (error) {
            showMessage(
                `Không tải được danh mục: ${error.message}`,
                "error"
            );

            handleAuthenticationError(error);
        } finally {
            openCreateButton.disabled = false;
        }
    }

    /**
     * Mở form sửa Product.
     */
    async function openEditDialog(id) {
        hideMessage();
        hideFormError();

        try {
            const result =
                await apiRequest(`${productApi}/${id}`);

            const product = result.data;

            await loadCategoryOptions(
                product.categoryId,
                product.categoryName
            );

            productIdInput.value =
                product.productId;

            productNameInput.value =
                product.productName || "";

            priceInput.value =
                product.price ?? "";

            descriptionInput.value =
                product.description || "";

            imageFileInput.value = "";

            dialogTitle.textContent =
                "Cập nhật sản phẩm";

            saveButton.textContent =
                "Lưu thay đổi";

            if (product.imageUrl) {
                showImagePreview(product.imageUrl);
            } else {
                hideImagePreview();
            }

            dialog.showModal();
            productNameInput.focus();
        } catch (error) {
            showMessage(error.message, "error");
            handleAuthenticationError(error);
        }
    }

    /**
     * Thêm hoặc cập nhật Product.
     */
    async function saveProduct(event) {
        event.preventDefault();

        hideFormError();

        const productName =
            productNameInput.value.trim();

        const price =
            Number(priceInput.value);

        const categoryId =
            categoryInput.value;

        if (!productName) {
            showFormError(
                "Tên sản phẩm không được để trống."
            );

            productNameInput.focus();
            return;
        }

        if (
            priceInput.value.trim() === "" ||
            !Number.isFinite(price) ||
            price < 0
        ) {
            showFormError(
                "Giá sản phẩm phải là số lớn hơn hoặc bằng 0."
            );

            priceInput.focus();
            return;
        }

        if (!categoryId) {
            showFormError(
                "Bạn chưa chọn danh mục."
            );

            categoryInput.focus();
            return;
        }

        const id = productIdInput.value;
        const formData = new FormData(productForm);

        /*
         * ProductRequest không có productId.
         * ID được truyền trên URL.
         */
        formData.delete("productId");

        const url = id
            ? `${productApi}/${id}`
            : productApi;

        const method = id
            ? "PUT"
            : "POST";

        saveButton.disabled = true;
        saveButton.textContent = "Đang lưu...";

        try {
            const result = await apiRequest(url, {
                method,
                body: formData
            });

            dialog.close();

            if (!id) {
                /*
                 * Product mới nằm đầu danh sách do sắp xếp
                 * productId giảm dần.
                 */
                state.page = 0;
            }

            await loadProducts();

            showMessage(
                result.message,
                "success"
            );
        } catch (error) {
            showFormError(error.message);
            handleAuthenticationError(error);
        } finally {
            saveButton.disabled = false;

            saveButton.textContent = id
                ? "Lưu thay đổi"
                : "Thêm sản phẩm";
        }
    }

    /**
     * Xóa Product.
     */
    async function deleteProduct(id) {
        const accepted = window.confirm(
            `Bạn có chắc muốn xóa sản phẩm có ID ${id}?`
        );

        if (!accepted) {
            return;
        }

        hideMessage();

        try {
            const result = await apiRequest(
                `${productApi}/${id}`,
                {
                    method: "DELETE"
                }
            );

            /*
             * Nếu vừa xóa dòng cuối của trang hiện tại,
             * lùi lại một trang.
             */
            if (
                tableBody.children.length === 1 &&
                state.page > 0
            ) {
                state.page--;
            }

            await loadProducts();

            showMessage(
                result.message,
                "success"
            );
        } catch (error) {
            showMessage(error.message, "error");
            handleAuthenticationError(error);
        }
    }

    /**
     * Xem trước ảnh được chọn.
     */
    function previewSelectedImage() {
        hideFormError();

        const file = imageFileInput.files[0];

        if (!file) {
            return;
        }

        if (!file.type.startsWith("image/")) {
            imageFileInput.value = "";

            showFormError(
                "Tệp được chọn không phải hình ảnh."
            );

            return;
        }

        const maximumSize =
            5 * 1024 * 1024;

        if (file.size > maximumSize) {
            imageFileInput.value = "";

            showFormError(
                "Kích thước ảnh không được vượt quá 5 MB."
            );

            return;
        }

        const previewUrl =
            URL.createObjectURL(file);

        showImagePreview(previewUrl);

        imagePreview.onload = () => {
            URL.revokeObjectURL(previewUrl);
        };
    }

    function showImagePreview(url) {
        imagePreview.src = url;
        imagePreview.hidden = false;
    }

    function hideImagePreview() {
        imagePreview.removeAttribute("src");
        imagePreview.hidden = true;
    }

    function setLoading(loading) {
        loadingElement.hidden = !loading;
    }

    function showMessage(message, type) {
        /*
         * Dùng textContent để tránh chèn HTML không an toàn.
         */
        messageElement.textContent = message;
        messageElement.className =
            `ajax-message ${type}`;

        messageElement.hidden = false;
    }

    function hideMessage() {
        messageElement.textContent = "";
        messageElement.hidden = true;
    }

    function showFormError(message) {
        formError.textContent = message;
        formError.hidden = false;
    }

    function hideFormError() {
        formError.textContent = "";
        formError.hidden = true;
    }

    function handleAuthenticationError(error) {
        if (error.status === 401) {
            window.setTimeout(() => {
                window.location.href = loginUrl;
            }, 1000);
        }
    }

    function formatCurrency(value) {
        const number = Number(value);

        if (!Number.isFinite(number)) {
            return "0 ₫";
        }

        return new Intl.NumberFormat(
            "vi-VN",
            {
                style: "currency",
                currency: "VND",
                maximumFractionDigits: 2
            }
        ).format(number);
    }

    function formatDateTime(value) {
        if (!value) {
            return "";
        }

        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return escapeHtml(value);
        }

        return new Intl.DateTimeFormat(
            "vi-VN",
            {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit"
            }
        ).format(date);
    }

    function escapeHtml(value) {
        return String(value ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    /**
     * Tìm kiếm.
     */
    searchForm.addEventListener(
        "submit",
        event => {
            event.preventDefault();

            hideMessage();

            state.keyword =
                keywordInput.value.trim();

            state.page = 0;

            loadProducts();
        }
    );

    resetSearchButton.addEventListener(
        "click",
        () => {
            hideMessage();

            keywordInput.value = "";
            state.keyword = "";
            state.page = 0;

            loadProducts();
        }
    );

    pageSizeSelect.addEventListener(
        "change",
        () => {
            hideMessage();

            state.size =
                Number(pageSizeSelect.value);

            state.page = 0;

            loadProducts();
        }
    );

    /**
     * Phân trang.
     */
    paginationElement.addEventListener(
        "click",
        event => {
            const button =
                event.target.closest(
                    "button[data-page]"
                );

            if (!button || button.disabled) {
                return;
            }

            hideMessage();

            state.page =
                Number(button.dataset.page);

            loadProducts();
        }
    );

    /**
     * Nút sửa và xóa.
     */
    tableBody.addEventListener(
        "click",
        event => {
            const button =
                event.target.closest(
                    "button[data-action]"
                );

            if (!button) {
                return;
            }

            const id =
                Number(button.dataset.id);

            const action =
                button.dataset.action;

            if (action === "edit") {
                openEditDialog(id);
            }

            if (action === "delete") {
                deleteProduct(id);
            }
        }
    );

    openCreateButton.addEventListener(
        "click",
        openCreateDialog
    );

    productForm.addEventListener(
        "submit",
        saveProduct
    );

    imageFileInput.addEventListener(
        "change",
        previewSelectedImage
    );

    document
        .querySelectorAll(
            "[data-close-product-dialog]"
        )
        .forEach(button => {
            button.addEventListener(
                "click",
                () => dialog.close()
            );
        });

    dialog.addEventListener(
        "click",
        event => {
            if (event.target === dialog) {
                dialog.close();
            }
        }
    );

    /**
     * Tải dữ liệu lần đầu.
     */
    loadProducts();
});