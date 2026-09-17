document.addEventListener("DOMContentLoaded", () => {
    const app = document.getElementById("categoryApp");

    if (!app) {
        return;
    }

    const apiBase = app.dataset.apiUrl;
    const loginUrl = app.dataset.loginUrl;

    const state = {
        keyword: "",
        page: 0,
        size: 5,
        totalPages: 0
    };

    const searchForm = document.getElementById("searchForm");
    const keywordInput = document.getElementById("keyword");
    const pageSizeSelect = document.getElementById("pageSize");
    const resetSearchButton = document.getElementById("btnResetSearch");

    const tableBody = document.getElementById("categoryTableBody");
    const loadingElement = document.getElementById("categoryLoading");
    const emptyElement = document.getElementById("categoryEmpty");
    const pageInfoElement = document.getElementById("categoryPageInfo");
    const paginationElement = document.getElementById("categoryPagination");
    const messageElement = document.getElementById("categoryMessage");

    const dialog = document.getElementById("categoryDialog");
    const dialogTitle = document.getElementById("categoryDialogTitle");
    const categoryForm = document.getElementById("categoryForm");
    const formError = document.getElementById("categoryFormError");

    const categoryIdInput = document.getElementById("categoryId");
    const categoryNameInput = document.getElementById("categoryName");
    const statusInput = document.getElementById("status");
    const iconFileInput = document.getElementById("iconFile");
    const imagePreview = document.getElementById("categoryImagePreview");

    const openCreateButton = document.getElementById("btnOpenCreate");
    const saveButton = document.getElementById("btnSaveCategory");

    /**
     * Gọi REST API.
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

        const contentType = response.headers.get("content-type") || "";

        let payload = null;

        if (contentType.includes("application/json")) {
            payload = await response.json();
        }

        if (!response.ok) {
            let message =
                payload?.message ||
                `Yêu cầu thất bại, HTTP ${response.status}`;

            if (payload?.errors) {
                const validationMessages =
                    Object.values(payload.errors).join("<br>");

                if (validationMessages) {
                    message = validationMessages;
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
     * Tải danh sách Category.
     */
    async function loadCategories() {
        setLoading(true);

        const parameters = new URLSearchParams({
            keyword: state.keyword,
            page: state.page.toString(),
            size: state.size.toString()
        });

        try {
            const result = await apiRequest(
                `${apiBase}?${parameters.toString()}`
            );

            renderTable(result.data.content);

            state.page = result.data.page;
            state.size = result.data.size;
            state.totalPages = result.data.totalPages;

            renderPageInfo(result.data);
            renderPagination(result.data);
        } catch (error) {
            tableBody.innerHTML = "";
            emptyElement.hidden = false;
            paginationElement.innerHTML = "";
            pageInfoElement.textContent = "";

            showMessage(error.message, "error");

            if (error.status === 401) {
                window.setTimeout(() => {
                    window.location.href = loginUrl;
                }, 1200);
            }
        } finally {
            setLoading(false);
        }
    }

    /**
     * Render bảng.
     */
    function renderTable(categories) {
        tableBody.innerHTML = "";

        if (!categories || categories.length === 0) {
            emptyElement.hidden = false;
            return;
        }

        emptyElement.hidden = true;

        tableBody.innerHTML = categories.map(category => {
            const id = Number(category.categoryId);

            const icon = category.iconUrl
                ? `
                    <img src="${escapeHtml(category.iconUrl)}"
                         class="category-icon"
                         alt="${escapeHtml(category.categoryName)}"
                         onerror="this.style.display='none'">
                  `
                : `<span class="no-image">Không có ảnh</span>`;

            const status = Number(category.status) === 1
                ? `<span class="status-badge status-active">Hoạt động</span>`
                : `<span class="status-badge status-inactive">Ngừng hoạt động</span>`;

            return `
                <tr>
                    <td>${id}</td>

                    <td>${icon}</td>

                    <td>
                        <strong>
                            ${escapeHtml(category.categoryName)}
                        </strong>
                    </td>

                    <td>${status}</td>

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

    /**
     * Thông tin trang hiện tại.
     */
    function renderPageInfo(pageData) {
        if (pageData.totalElements === 0) {
            pageInfoElement.textContent = "Không có dữ liệu";
            return;
        }

        pageInfoElement.textContent =
            `Trang ${pageData.page + 1}/${pageData.totalPages}` +
            ` - Tổng ${pageData.totalElements} danh mục`;
    }

    /**
     * Render nút phân trang.
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

        const startPage = Math.max(0, pageData.page - 2);
        const endPage = Math.min(
            pageData.totalPages - 1,
            pageData.page + 2
        );

        for (let page = startPage; page <= endPage; page++) {
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

    function createPageButton(label, page, disabled, active) {
        const button = document.createElement("button");

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
     * Mở form thêm mới.
     */
    function openCreateDialog() {
        categoryForm.reset();

        categoryIdInput.value = "";
        statusInput.value = "1";

        dialogTitle.textContent = "Thêm danh mục";
        saveButton.textContent = "Thêm danh mục";

        hideFormError();
        hideImagePreview();

        dialog.showModal();
        categoryNameInput.focus();
    }

    /**
     * Mở form cập nhật.
     */
    async function openEditDialog(id) {
    
        hideFormError();

        try {
            const result = await apiRequest(`${apiBase}/${id}`);
            const category = result.data;

            categoryIdInput.value = category.categoryId;
            categoryNameInput.value = category.categoryName;
            statusInput.value = category.status;
            iconFileInput.value = "";

            dialogTitle.textContent = "Cập nhật danh mục";
            saveButton.textContent = "Lưu thay đổi";

            if (category.iconUrl) {
                showImagePreview(category.iconUrl);
            } else {
                hideImagePreview();
            }

            dialog.showModal();
            categoryNameInput.focus();
        } catch (error) {
            showMessage(error.message, "error");
        }
    }

    /**
     * Gửi form thêm hoặc cập nhật.
     */
    async function saveCategory(event) {
        event.preventDefault();

        hideFormError();

        const categoryName = categoryNameInput.value.trim();

        if (!categoryName) {
            showFormError("Tên danh mục không được để trống.");
            categoryNameInput.focus();
            return;
        }

        const id = categoryIdInput.value;
        const formData = new FormData(categoryForm);

        // categoryId không không thuộc CategoryRequest.
        formData.delete("categoryId");

        const url = id
            ? `${apiBase}/${id}`
            : apiBase;

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

            showMessage(result.message, "success");

            // Sau khi thêm mới, quay về trang đầu vì dữ liệu sắp xếp ID giảm dần.
            if (!id) {
                state.page = 0;
            }

            await loadCategories();
        } catch (error) {
            showFormError(error.message);
        } finally {
            saveButton.disabled = false;
            saveButton.textContent = id
                ? "Lưu thay đổi"
                : "Thêm danh mục";
        }
    }

    /**
     * Xóa Category.
     */
    async function deleteCategory(id) {
        const accepted = window.confirm(
            `Bạn có chắc muốn xóa danh mục có ID ${id}?`
        );

        if (!accepted) {
            return;
        }

        try {
            const result = await apiRequest(`${apiBase}/${id}`, {
                method: "DELETE"
            });

            showMessage(result.message, "success");

            /*
             * Nếu vừa xóa phần tử cuối cùng của trang hiện tại,
             * lùi lại một trang.
             */
            if (tableBody.children.length === 1 && state.page > 0) {
                state.page--;
            }

            await loadCategories();
        } catch (error) {
            /*
             * API trả 409 nếu Category đang có Product.
             */
            showMessage(error.message, "error");
        }
    }

    /**
     * Xem trước ảnh được chọn.
     */
    function previewSelectedImage() {
        const file = iconFileInput.files[0];

        if (!file) {
            return;
        }

        if (!file.type.startsWith("image/")) {
            iconFileInput.value = "";
            showFormError("Tệp được chọn không phải là hình ảnh.");
            return;
        }

        const imageUrl = URL.createObjectURL(file);
        showImagePreview(imageUrl);

        imagePreview.onload = () => {
            URL.revokeObjectURL(imageUrl);
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
        messageElement.innerHTML = message;
        messageElement.className = `ajax-message ${type}`;
        messageElement.hidden = false;
    }

    function hideMessage() {
        messageElement.hidden = true;
        messageElement.textContent = "";
    }

    function showFormError(message) {
        formError.innerHTML = message;
        formError.hidden = false;
    }

    function hideFormError() {
        formError.hidden = true;
        formError.textContent = "";
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
     * Sự kiện tìm kiếm.
     */
    searchForm.addEventListener("submit", event => {
        event.preventDefault();

        state.keyword = keywordInput.value.trim();
        state.page = 0;

        loadCategories();
    });

    resetSearchButton.addEventListener("click", () => {
        keywordInput.value = "";

        state.keyword = "";
        state.page = 0;

        loadCategories();
    });

    pageSizeSelect.addEventListener("change", () => {
        state.size = Number(pageSizeSelect.value);
        state.page = 0;

        loadCategories();
    });

    /**
     * Sự kiện phân trang.
     */
    paginationElement.addEventListener("click", event => {
        const button = event.target.closest("button[data-page]");

        if (!button || button.disabled) {
            return;
        }

        state.page = Number(button.dataset.page);
        loadCategories();
    });

    /**
     * Sự kiện nút sửa và xóa trong bảng.
     */
    tableBody.addEventListener("click", event => {
        const button = event.target.closest("button[data-action]");

        if (!button) {
            return;
        }

        const id = Number(button.dataset.id);
        const action = button.dataset.action;

        if (action === "edit") {
            openEditDialog(id);
        }

        if (action === "delete") {
            deleteCategory(id);
        }
    });

    openCreateButton.addEventListener("click", openCreateDialog);

    categoryForm.addEventListener("submit", saveCategory);

    iconFileInput.addEventListener("change", previewSelectedImage);

    document.querySelectorAll("[data-close-dialog]")
        .forEach(button => {
            button.addEventListener("click", () => {
                dialog.close();
            });
        });

    dialog.addEventListener("click", event => {
        if (event.target === dialog) {
            dialog.close();
        }
    });

    /*
     * Lần đầu vào trang: gọi API thay vì dùng th:each.
     */
    loadCategories();
});
