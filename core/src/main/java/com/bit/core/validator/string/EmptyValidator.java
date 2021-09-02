package com.bit.core.validator.string;

import com.bit.core.utils.StringUtils;
import com.bit.core.validator.base.ValidatorChain;

public class EmptyValidator extends ValidatorChain<String>{
	public final static String VALIDATION_MESSAGE = "Cannot be empty";

	public EmptyValidator(String label, String value) {
		super(label, value);
	}
	
	@Override
	protected void validateValue() {
		if(!StringUtils.isNotEmpty(value)) {
			getValidationMessages().put(label, VALIDATION_MESSAGE);
		}
	}
}
