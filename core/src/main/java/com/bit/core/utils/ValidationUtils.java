package com.bit.core.utils;

import com.bit.core.validator.NullValidator;
import com.bit.core.validator.base.ValidatorChain;
import com.bit.core.validator.string.EmptyValidator;

public class ValidationUtils {
	public static final void setEmptyValidator(ValidatorChain<?> validator, String label, String token) {
		validator.setNextValidator(new EmptyValidator(label, token));	
	}
	public static final void setNullValidator(ValidatorChain<?> validator, String label, Object token) {
		validator.setNextValidator(new NullValidator(label, token));
	}
}
