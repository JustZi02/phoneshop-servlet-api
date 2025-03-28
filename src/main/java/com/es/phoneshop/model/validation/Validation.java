package com.es.phoneshop.model.validation;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Validation {
    public int QuantityStringToInt(String stringQuantity, Locale locale) throws ParseException {
        stringQuantity = stringQuantity.trim();
        if (!stringQuantity.matches("[\\d\\s,.]+")) {
            throw new ParseException("Invalid number format", 0);
        }
        NumberFormat formatter = NumberFormat.getInstance(locale);
        Number number = formatter.parse(stringQuantity);
        if (number.doubleValue() % 1 != 0) {
            throw new ParseException("Invalid number format", 0);
        }
        int quantity = number.intValue();
        if (quantity < 1) {
            throw new ParseException("Quantity must be a positive number", 0);
        }
        return quantity;
    }
}
