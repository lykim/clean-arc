package com.bit.core.validator;

import java.util.Map;

import com.bit.core.validator.base.ValidatorChain;

public class InitialValidator extends ValidatorChain<Object>{

	public InitialValidator(Map<String, String> validationMessages) {
		super(validationMessages);
	}

	@Override
	protected void validateValue() {
		
	}

}
