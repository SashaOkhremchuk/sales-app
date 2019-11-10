package com.app.web.validation;

import com.app.data.entity.Order;
import com.app.exception.ValidationException;
import org.springframework.util.StringUtils;

public class OrderValidation {

    private static final String EMPTY_PROPERTY_EXCEPTION_MESSAGE = "Client field parameter '%s' must be provided";

    public static void validate(Order order) throws ValidationException {
        validateNotEmptyProperty(order.getPaymentMethod(), "paymentMethod");
    }

    private static void validateNotEmptyProperty(Object value, String propertyName) {
        if (value == null || StringUtils.isEmpty(value)) {
            throw new ValidationException(String.format(EMPTY_PROPERTY_EXCEPTION_MESSAGE, propertyName));
        }
    }
}
