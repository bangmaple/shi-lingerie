package com.bangmaple.webflux.utils;

import com.bangmaple.webflux.aop.EntityValidatingException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.InvocationTargetException;

@Component
public class ValidatorUtil<E> {

    @SneakyThrows({NoSuchMethodException.class, InvocationTargetException.class,
            InstantiationException.class, IllegalAccessException.class})
    public <V extends Validator> E validate(Class<V> clazz, E entity) {
        Validator validator = clazz.getDeclaredConstructor().newInstance();
        Errors errors = new BeanPropertyBindingResult(entity, entity.getClass().getName());
        validator.validate(entity, errors);
        if (errors.getAllErrors().size() > 0) {
            throw new EntityValidatingException().setErrors(errors.getAllErrors());
        }
        return entity;
    }

}
