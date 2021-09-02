package com.bit.core.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bit.core.utils.StringUtils;
import com.bit.core.validator.base.ValidatorChain;

public class EmailValidator extends ValidatorChain<String>{
	public final static String VALIDATION_MESSAGE = "Email is invalid";
	private String VALID_EMAIL_FORMAT = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

	public EmailValidator(String label, String value) {
		super(label, value);
	}
	
	@Override
	protected void validateValue() {
		if(StringUtils.isNotEmpty(value)) {
			if(!isValidEmail(value)) setValidationMessage();
		}
	}
	
	private void setValidationMessage() {
		getValidationMessages().put(label, VALIDATION_MESSAGE);
	}
	
	private boolean isValidEmail(String email) {
		Pattern pattern = Pattern.compile(VALID_EMAIL_FORMAT);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
