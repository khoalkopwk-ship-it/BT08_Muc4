<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Hồ sơ | UTE EcoMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/profile.css"></head>
<body class="store-page">
<jsp:include page="/WEB-INF/views/fragments/header.jsp"/>
<main class="store-main eco-main"><section class="profile-page"><div class="container-eco">
    <div class="profile-heading"><span class="eyebrow" style="background:var(--eco-green-soft);color:var(--eco-green)">Tài khoản</span><h1>Hồ sơ cá nhân</h1><p>Cập nhật thông tin liên hệ và ảnh đại diện của bạn.</p></div>
    <c:if test="${not empty success}"><div class="alert-eco alert-eco--success"><c:out value="${success}"/></div></c:if>
    <div class="profile-card">
        <div class="profile-avatar"><c:choose><c:when test="${not empty profile.images}"><img src="${pageContext.request.contextPath}/${profile.images}" alt="Ảnh đại diện"></c:when><c:otherwise><img src="${pageContext.request.contextPath}/images/admin.png" alt="Ảnh đại diện mặc định"></c:otherwise></c:choose><strong><c:out value="${profile.fullname}"/></strong><span><c:out value="${profile.email}"/></span></div>
        <form:form cssClass="profile-form" method="post" modelAttribute="profileForm" enctype="multipart/form-data" action="${pageContext.request.contextPath}/profile">
            <form:errors path="*" cssClass="alert-eco alert-eco--error" element="div"/>
            <div class="form-group"><label>Tên đăng nhập</label><input type="text" value="${profile.username}" readonly></div>
            <div class="form-group"><label>Email</label><input type="email" value="${profile.email}" readonly></div>
            <div class="form-group"><label for="fullname">Họ và tên</label><form:input id="fullname" path="fullname"/><form:errors path="fullname" cssClass="field-error"/></div>
            <div class="form-group"><label for="phone">Số điện thoại</label><form:input id="phone" path="phone"/><form:errors path="phone" cssClass="field-error"/></div>
            <div class="form-group"><label for="image">Ảnh đại diện mới</label><input id="image" type="file" name="image" accept=".jpg,.jpeg,.png,.gif,.webp"><small>Kích thước tối đa 5 MB.</small></div>
            <button class="btn-eco" type="submit">Lưu thay đổi</button>
        </form:form>
    </div>
</div></section></main>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
</body></html>
