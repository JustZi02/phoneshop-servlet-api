<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Cart">
    <jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>

    <p>${cart}</p>

    <c:if test="${not empty errors}">
        <div class="error">There were errors updating cart.</div>
        <br>
    </c:if>
    <c:if test="${not empty param.message}">
        <div class="success">${param.message}</div>
        <br>
    </c:if>

    <form method="post" action="${pageContext.servletContext.contextPath}/cart">
        <table>
            <tr>
                <td>Image</td>
                <td>Description</td>
                <td class="price">Price</td>
                <td class="quantity">Quantity</td>
                <td>Stock</td>
                <td></td>
            </tr>
            <c:forEach var="cartItem" items="${cart.items}">
                <tr>
                    <td><img class="product-tile" src="${cartItem.product.imageUrl}" alt=""></td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${cartItem.product.id}">
                                ${cartItem.product.description}
                        </a>
                    </td>
                    <td class="price">
                        <fmt:formatNumber value="${cartItem.product.price}" type="currency"
                                          currencySymbol="${cartItem.product.currency.symbol}"/>
                    </td>
                    <td>
                        <label>
                            <input class="quantity" name="quantity"
                                   value="${not empty errors[cartItem.product.id] ? quantities[cartItem.product.id] : cartItem.quantity}">
                            <input type="hidden" name="productId" value="${cartItem.product.id}">
                        </label>
                        <c:if test="${not empty errors[cartItem.product.id]}">
                            <div class="error">${errors[cartItem.product.id]}</div>
                        </c:if>
                    </td>
                    <td>${cartItem.product.stock}</td>
                    <td>
                        <button form="deleteCartItem"
                                formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${cartItem.product.id}">
                            Delete
                        </button>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td></td>
                <td></td>
                <td>Total cost: ${cart.totalPrice}$</td>
                <td>Total quantity: ${cart.totalQuantity} items</td>
                <td></td>
            </tr>
        </table>
        <p>
            <button>Confirm</button>
        </p>
    </form>

    <form id="deleteCartItem" method="post"></form>
    <tags:searchHistory/>
</tags:master>
