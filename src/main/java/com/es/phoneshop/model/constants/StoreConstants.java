package com.es.phoneshop.model.constants;

public class StoreConstants {
    public static final class Messages {
        public final static String OUT_OF_STOCK_MESSAGE = "Sorry, we don't have enough product stock! Asked quantity: %s, available quantity %s";
        public final static String INVALID_NUMBER_FORMAT_MESSAGE = "Invalid number format.";
        public final static String PRODUCT_ADDED_TO_CART_MESSAGE = "Product added to cart successfully!";
    }

    public static final class Parameters {
        public final static String ERROR_MESSAGE = "errorMessage";
        public final static String SUCCESS_MESSAGE = "message";
        public final static String ERROR_PRODUCT_ID = "errorItem";
        public final static String PRODUCT_QUANTITY = "quantity";
        public final static String PRODUCT_ID = "productId";
    }

    public static final class Pages {
        public final static String PRODUCT_LIST = "/WEB-INF/pages/productList.jsp";
        public final static String PRODUCT_DETAILS = "/WEB-INF/pages/productDetails.jsp";
        public final static String CART = "/WEB-INF/pages/cart.jsp";
        public final static String MINI_CART = "/WEB-INF/pages/miniCart.jsp";
        public final static String CHECKOUT = "/WEB-INF/pages/checkout.jsp";
        public final static String ERROR_NO_SUCH_ELEMENT_EXCEPTION = "/WEB-INF/pages/errorNoSuchElementException.jsp";
        public final static String ERROR_ORDER_NOT_FOUND = "/WEB-INF/pages/errorOrderNotFoundException.jsp";
        public final static String ORDER_OVERVIEW = "/WEB-INF/pages/orderOverview.jsp";
    }
}
