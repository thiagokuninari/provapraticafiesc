package br.com.xbrain.autenticacao.modules.comum.util;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;

@UtilityClass
public class ValidationUtils {

    public static void aplicarValidacoes(Object request, Class<?>... groupsValidations) {
        @Cleanup var factory = Validation.buildDefaultValidatorFactory();
        var validator = factory.getValidator();
        var constraintViolationSet = validator.validate(request, groupsValidations);
        if (!constraintViolationSet.isEmpty()) {
            throw new ConstraintViolationException(constraintViolationSet);
        }
    }
}
