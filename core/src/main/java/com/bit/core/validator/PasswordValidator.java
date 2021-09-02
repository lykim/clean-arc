package com.bit.core.validator;

import com.bit.core.utils.StringUtils;
import com.bit.core.validator.base.ValidatorChain;

public class PasswordValidator  extends ValidatorChain<String>{
	public final static String VALIDATION_MESSAGE = "Password is invalid";
	private final int MIN_PASSWORD_LENGTH = 6;

	public PasswordValidator(String label, String value) {
		super(label, value);
	}
	
	@Override
	protected void validateValue() {
		if(StringUtils.isNotEmpty(value)) {
			if(value.length() < MIN_PASSWORD_LENGTH) setValidationMessage();
		}
	}
	
	private void setValidationMessage() {
		getValidationMessages().put(label, VALIDATION_MESSAGE);
	}
}
