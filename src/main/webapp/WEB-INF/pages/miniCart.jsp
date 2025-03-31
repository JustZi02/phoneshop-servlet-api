<%@ page import="com.es.phoneshop.model.cart.Cart" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<a class="mini-cart" href="${pageContext.servletContext.contextPath}/cart">
    <c:choose>
        <c:when test="${cart.totalQuantity == 0}">
            Cart
        </c:when>

        <c:otherwise>
            Cart: ${cart.totalQuantity} items
        </c:otherwise>
    </c:choose>
</a>
