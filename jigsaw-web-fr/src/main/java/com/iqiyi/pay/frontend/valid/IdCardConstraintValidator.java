package com.iqiyi.pay.frontend.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by leishengbao on 11/28/16.
 */
public class IdCardConstraintValidator implements ConstraintValidator<IdCard, CharSequence>{


    @Override
    public void initialize(IdCard constraintAnnotation) {

    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        IdcardValidator idcardValidator = new IdcardValidator();
        return idcardValidator.isValidatedAllIdcard(value.toString());
    }
}
