<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Sản phẩm | UTE EcoMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/ecomart.css"></head>
<body class="store-page">
<jsp:include page="/WEB-INF/views/fragments/header.jsp"/>
<main class="store-main">
    <section class="page-hero"><div class="container-eco"><h1>Tất cả sản phẩm</h1><p>Tìm kiếm và khám phá những lựa chọn mới tại EcoMart.</p></div></section>
    <section class="section"><div class="container-eco">
        <form class="search-bar" method="get" action="${pageContext.request.contextPath}/product"><input name="keyword" value="${keyword}" placeholder="Tìm theo tên sản phẩm..." aria-label="Tên sản phẩm"><button class="btn-eco" type="submit">Tìm kiếm</button><a class="btn-eco btn-eco--light" href="${pageContext.request.contextPath}/product">Làm mới</a></form>
        <div class="result-bar"><span>Tìm thấy <strong>${result.totalElements}</strong> sản phẩm</span><span>Trang ${result.number + 1}/${result.totalPages == 0 ? 1 : result.totalPages}</span></div>
        <div class="product-grid">
            <c:forEach var="product" items="${result.content}"><article class="product-card">
                <a class="product-card__image" href="${pageContext.request.contextPath}/product/detail?id=${product.productId}"><c:choose><c:when test="${not empty product.image}"><img src="${pageContext.request.contextPath}/${product.image}" alt="${product.productName}"></c:when><c:otherwise><img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="Chưa có ảnh"></c:otherwise></c:choose></a>
                <div class="product-card__body"><span class="product-card__category"><c:out value="${product.category.categoryName}"/></span><h3><c:out value="${product.productName}"/></h3><div class="price"><fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/> ₫</div><a class="btn-eco" href="${pageContext.request.contextPath}/product/detail?id=${product.productId}">Xem chi tiết</a></div>
            </article></c:forEach>
            <c:if test="${result.totalElements == 0}"><div class="empty-state">Không tìm thấy sản phẩm phù hợp.</div></c:if>
        </div>
        <c:if test="${result.totalPages > 1}"><nav class="pagination-eco" aria-label="Phân trang sản phẩm"><c:forEach begin="0" end="${result.totalPages - 1}" var="number"><c:url var="pageUrl" value="/product"><c:param name="keyword" value="${keyword}"/><c:param name="page" value="${number}"/></c:url><a class="${number == result.number ? 'active' : ''}" href="${pageUrl}">${number + 1}</a></c:forEach></nav></c:if>
    </div></section>
</main>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
</body></html>
