<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Product Details">
    <jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
    <p>${cart}</p>

    <c:if test="${not empty sessionScope.message}">
        <div class="success">
                ${sessionScope.message}
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="error">
            There was an error adding to cart.
        </div>
    </c:if>
    <p>
            ${product.description}
    </p>

    <form method="post">
        <table class="product-details">
            <tr>
                <td>Image</td>
                <td><img src="${product.imageUrl}" alt="Image"></td>
            </tr>
            <tr>
                <td>Code</td>
                <td>${product.code}</td>
            </tr>
            <tr>
                <td>Price</td>
                <td class="price">
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
            <tr>
                <td>Stock</td>
                <td>${product.stock}</td>
            </tr>
            <tr>
                <td>Quantity</td>
                <td>
                    <label>
                        <input name="quantity"
                               value="${not empty errorMessage ? param.quantity : 1}">
                    </label>
                    <c:if test="${not empty errorMessage}">
                        <div class="error">
                                ${errorMessage}
                        </div>
                    </c:if>
                </td>
            </tr>

        </table>
        <button>Add to cart</button>
    </form>
    <tags:searchHistory/>
</tags:master>