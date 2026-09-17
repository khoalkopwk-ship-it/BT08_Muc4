<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>UTE EcoMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/ecomart.css"></head>
<body class="store-page">
<jsp:include page="/WEB-INF/views/fragments/header.jsp"/>
<main class="store-main">
    <section class="hero"><div class="container-eco"><div class="hero__content">
        <span class="eyebrow">Phong cách · Tiện ích · Bền vững</span><h1>Mua sắm xanh, lựa chọn thông minh</h1>
        <p>Khám phá những sản phẩm mới với trải nghiệm mua sắm nhẹ nhàng, rõ ràng và thân thiện.</p>
        <a class="btn-eco" href="${pageContext.request.contextPath}/product">Khám phá sản phẩm →</a>
    </div></div></section>
    <section class="section section--soft"><div class="container-eco">
        <div class="section-heading"><div><h2>Danh mục nổi bật</h2><p>Tìm nhanh nhóm sản phẩm bạn quan tâm.</p></div><a href="${pageContext.request.contextPath}/category">Xem tất cả →</a></div>
        <div class="category-grid">
            <c:forEach var="category" items="${categories}"><a class="category-card" href="${pageContext.request.contextPath}/category">
                <c:choose><c:when test="${not empty category.icon}"><img src="${pageContext.request.contextPath}/${category.icon}" alt="${category.categoryName}"></c:when><c:otherwise><img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="UTE EcoMart"></c:otherwise></c:choose>
                <div><h3><c:out value="${category.categoryName}"/></h3><span>Khám phá →</span></div>
            </a></c:forEach>
            <c:if test="${empty categories}"><div class="empty-state">Hiện chưa có danh mục sản phẩm.</div></c:if>
        </div>
    </div></section>
    <section class="section"><div class="container-eco">
        <div class="section-heading"><div><h2>10 sản phẩm mới nhất</h2><p>Những lựa chọn vừa được cập nhật tại cửa hàng.</p></div><a href="${pageContext.request.contextPath}/product">Xem tất cả →</a></div>
        <div class="product-grid">
            <c:forEach var="product" items="${products}"><article class="product-card">
                <a class="product-card__image" href="${pageContext.request.contextPath}/product/detail?id=${product.productId}"><span class="product-card__badge">Mới</span><c:choose><c:when test="${not empty product.image}"><img src="${pageContext.request.contextPath}/${product.image}" alt="${product.productName}"></c:when><c:otherwise><img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="Chưa có ảnh"></c:otherwise></c:choose></a>
                <div class="product-card__body"><span class="product-card__category"><c:out value="${product.category.categoryName}"/></span><h3><c:out value="${product.productName}"/></h3><div class="price"><fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/> ₫</div><a class="btn-eco btn-eco--light" href="${pageContext.request.contextPath}/product/detail?id=${product.productId}">Xem chi tiết</a></div>
            </article></c:forEach>
            <c:if test="${empty products}"><div class="empty-state">Chưa có sản phẩm mới.</div></c:if>
        </div>
    </div></section>
</main>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
</body></html>
