<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${product.productId == null ? 'Thêm' : 'Cập nhật'} sản phẩm | EcoMart Admin</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css"></head>
<body class="admin-layout">
<c:set var="activeMenu" value="product" scope="request"/><jsp:include page="/WEB-INF/views/fragments/admin-nav.jsp"/>
<div class="admin-main">
    <header class="admin-topbar"><strong>Hệ thống quản trị EcoMart</strong><span>${product.productId == null ? 'Sản phẩm mới' : 'Chỉnh sửa sản phẩm'}</span></header>
    <main class="admin-content">
        <div class="admin-title"><div><h1>${product.productId == null ? 'Thêm sản phẩm' : 'Cập nhật sản phẩm'}</h1><p>Quản lý thông tin, giá bán, danh mục và hình ảnh sản phẩm.</p></div></div>
        <div class="panel"><form:form cssClass="form-grid" method="post" modelAttribute="product" enctype="multipart/form-data" action="${pageContext.request.contextPath}/admin/products/save">
            <form:hidden path="productId"/><form:errors path="*" cssClass="alert-eco alert-eco--error form-group--full" element="div"/>
            <div class="form-group"><label for="productName">Tên sản phẩm</label><form:input id="productName" path="productName" cssClass="form-control-eco"/><form:errors path="productName" cssClass="field-error"/></div>
            <div class="form-group"><label for="price">Giá bán</label><form:input id="price" path="price" type="number" min="0" step="0.01" cssClass="form-control-eco"/><form:errors path="price" cssClass="field-error"/></div>
            <div class="form-group"><label for="categoryId">Danh mục</label><select id="categoryId" name="categoryId" class="form-control-eco" required><option value="">-- Chọn danh mục --</option><c:forEach var="category" items="${categories}"><option value="${category.categoryId}" ${category.categoryId == categoryId ? 'selected' : ''}><c:out value="${category.categoryName}"/></option></c:forEach></select></div>
            <div class="form-group"><label for="imageFile">Ảnh sản phẩm</label><input id="imageFile" type="file" name="imageFile" class="form-control-eco" accept=".jpg,.jpeg,.png,.gif,.webp"><c:if test="${not empty product.image}"><img class="preview-image" src="${pageContext.request.contextPath}/${product.image}" alt="Ảnh hiện tại"></c:if></div>
            <div class="form-group form-group--full"><label for="description">Mô tả</label><form:textarea id="description" path="description" rows="5" cssClass="form-control-eco"/><form:errors path="description" cssClass="field-error"/></div>
            <div class="form-actions"><button class="btn-eco" type="submit">Lưu sản phẩm</button><a class="btn-eco btn-eco--light" href="${pageContext.request.contextPath}/admin/products">Quay lại</a></div>
        </form:form></div>
    </main>
</div>
</body></html>
