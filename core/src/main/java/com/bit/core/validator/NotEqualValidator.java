package com.bit.core.validator;

import com.bit.core.validator.base.ValidatorChain;

public class NotEqualValidator extends ValidatorChain<String> {
	public final static String VALIDATION_MESSAGE = "Is not equal";
	private String comparedValue;
	
	public NotEqualValidator(String label, String value, String comparedValue) {
		super(label, value);
		this.comparedValue = comparedValue;
	}
	@Override
	protected void validateValue() {
		if(!value.equals(comparedValue)) setValidationMessage();
	}
	
	private void setValidationMessage() {
		getValidationMessages().put(label, VALIDATION_MESSAGE);
	}
}
