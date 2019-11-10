package com.app.web.validation;

import com.app.data.entity.Item;
import com.app.exception.ValidationException;
import org.springframework.util.StringUtils;

public class ItemValidation {

    private static final String EMPTY_PROPERTY_EXCEPTION_MESSAGE = "Item field parameter '%s' must be provided";

    public static void validate(Item item) throws ValidationException {
        validateNotEmptyProperty(item.getName(), "name");
        validateNotEmptyProperty(item.getPrice(), "price");
        validateNotEmptyProperty(item.getDescription(), "description");
    }

    private static void validateNotEmptyProperty(Object value, String propertyName) {
        if (value == null || StringUtils.isEmpty(value)) {
            throw new ValidationException(String.format(EMPTY_PROPERTY_EXCEPTION_MESSAGE, propertyName));
        }
    }
}
