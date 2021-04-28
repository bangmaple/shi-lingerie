package com.bangmaple.webflux.validator;

import com.bangmaple.webflux.entities.Users;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.constraintvalidators.hv.LengthValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class UsersValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Users.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Users entity = (Users) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fullname", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "role", "field.required");
        if (entity.getUsername() != null && entity.getUsername().trim().length() < 3) {
            errors.rejectValue("username", "field.min.length",
                    new Object[]{6}, "The code must be at least [3] characters in length.");
        }
    }
}
