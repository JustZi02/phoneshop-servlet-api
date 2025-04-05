<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Checkout">
    <jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
    <br>
    <c:if test="${not empty errors}">
        <div class="error">
            There were errors placing order.
        </div>
    </c:if>
    <c:if test="${not empty param.message}">
        <div class="success">
                ${param.message}
        </div>
    </c:if>
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
    <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
        <table>
                <tags:orderFormRow label="First name" name="firstName" order="${order}" errors="${errors}"></tags:orderFormRow>
                <tags:orderFormRow label="Last name" name="lastName" order="${order}" errors="${errors}"></tags:orderFormRow>
                <tags:orderFormRow label="Phone" name="phone" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tr>
                <td>Delivery date:<span style="color:red">*</span></td>
                <td>
                    <c:set var="error" value="${errors['deliveryDate']}"/>
                    <input type="date" name="deliveryDate"
                           value="${not empty error ? param['deliveryDate'] : order.deliveryDate}"/>
                    <c:if test="${not empty error}">
                        <div class="error">
                                ${error}
                        </div>
                    </c:if>
                </td>
            </tr>
                <tags:orderFormRow label="Delivery address" name="deliveryAddress" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tr>
                <td>Payment method:<span style="color:red">*</span></td>
                <td>
                    <c:set var="error" value="${errors['paymentMethod']}"/>
                    <select name="paymentMethod">
                        <option></option>
                        <c:forEach var="paymentMethod" items="${paymentMethods}">
                            <option value="${paymentMethod}"
                                    <c:if test="${not empty error ? param['paymentMethod'] == paymentMethod : order.paymentMethod == paymentMethod}">
                                        selected
                                    </c:if>
                            >${paymentMethod}</option>
                        </c:forEach>
                    </select>
                    <c:if test="${not empty error}">
                    <div class="error">
                            ${error}
                    </div>
                    </c:if>
                </td>
            </tr>

        </table>

        <button type="submit">Place order</button>
    </form>

    <tags:searchHistory/>
</tags:master>
