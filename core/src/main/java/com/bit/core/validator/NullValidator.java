package com.bit.core.validator;

import com.bit.core.validator.base.ValidatorChain;

public class NullValidator extends ValidatorChain<Object>{
	public final static String VALIDATION_MESSAGE = "Cannot be null";

	public NullValidator(String label, Object value) {
		super(label, value);
	}
	
	@Override
	protected void validateValue() {
		if(value == null) {
			getValidationMessages().put(label, VALIDATION_MESSAGE);
		}
	}

}
