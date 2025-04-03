<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Order Overview">
    <jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
    <br>
    <table>
        <tr>
            <td>Image</td>
            <td>Description</td>
            <td class="price">Price</td>
            <td class="quantity">Quantity</td>
        </tr>
        <c:forEach var="cartItem" items="${order.items}">
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
                        ${cartItem.quantity}
                </td>
            </tr>
        </c:forEach>
        <tr>
            <td></td>
            <td></td>
            <td>Total quantity:</td>
            <td>${order.totalQuantity} items</td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td>Delivery:</td>
            <td>${order.deliveryCost} $</td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td>Subtotal:</td>
            <td>${order.subtotal} $</td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td>Total cost:</td>
            <td>${order.totalPrice}$</td>
        </tr>
    </table>
    <h1>Your data:</h1>
    <table>
        <tags:orderOverviewFormRow label="First name" name="firstName" order="${order}"></tags:orderOverviewFormRow>
        <tags:orderOverviewFormRow label="Last name" name="lastName" order="${order}"></tags:orderOverviewFormRow>
        <tags:orderOverviewFormRow label="Phone" name="phone" order="${order}"></tags:orderOverviewFormRow>
        <tr>
            <td>Delivery date:</td>
            <td>
                    ${order.deliveryDate}
            </td>
        </tr>
        <tr>
            <tags:orderOverviewFormRow label="Delivery address" name="deliveryAddress"
                                       order="${order}"></tags:orderOverviewFormRow>
        </tr>
        <tr>
            <td>Payment method:</td>
            <td>
                    ${order.paymentMethod}
            </td>
        </tr>

    </table>
</tags:master>
