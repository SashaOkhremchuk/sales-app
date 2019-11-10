package com.app.web.validation;

import com.app.data.entity.Client;
import com.app.exception.ValidationException;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientValidation {

    private static final String EMPTY_PROPERTY_EXCEPTION_MESSAGE = "Client field parameter '%s' must be provided";
    private static final String REGEX_EXCEPTION_MESSAGE = "Client field parameter '%s' is invalid";

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private static final String PHONE_NUMBER_REGEX = "^\\+?3?8?(0[5-9][0-9]\\d{7})$";

    public static void validate(Client client) throws ValidationException {
        validateNotEmptyProperty(client.getFirstName(), "firstName");
        validateNotEmptyProperty(client.getLastName(), "lastName");
        validateNotEmptyProperty(client.getEmail(), "email");
        validateNotEmptyProperty(client.getPhoneNumber(), "phoneNumber");
        validateNotEmptyProperty(client.getAddress(), "address");

        validateWithRegularExpression(client.getEmail(), EMAIL_REGEX, "email");
        validateWithRegularExpression(client.getPhoneNumber(), PHONE_NUMBER_REGEX, "phoneNumber");
    }

    private static void validateNotEmptyProperty(Object value, String propertyName) {
        if (value == null || StringUtils.isEmpty(value)) {
            throw new ValidationException(String.format(EMPTY_PROPERTY_EXCEPTION_MESSAGE, propertyName));
        }
    }

    private static void validateWithRegularExpression(Object value, String regex, String propertyName) {
        Matcher matcher = Pattern.compile(regex).matcher(String.valueOf(value));
        if (!matcher.matches()) {
            throw new ValidationException(String.format(REGEX_EXCEPTION_MESSAGE, propertyName));
        }
    }
}
