package com.bit.core.validator.base;

import java.util.Map;

public abstract class ValidatorChain <V>{
	private ValidatorChain<?> lastValidator;
	private ValidatorChain<?> nextValidator;
	private Map<String, String> validationMessages;
	protected String label;
	protected V value;
	
	protected ValidatorChain(Map<String, String> validationMessages) {
		this.validationMessages = validationMessages;
		lastValidator = this;
	}
	
	protected ValidatorChain(V value) {
		this.value = value;
		lastValidator = this;
	}
	
	protected ValidatorChain(String label, V value) {
		this.label = label;
		this.value = value;
		lastValidator = this;
	}
	
	public void validate() {
		validateValue();
		validateNextValidator();
	}
	
	protected abstract void validateValue();
	
	private void validateNextValidator() {
		if(nextValidator != null) {
			nextValidator.validate();
		}
	}
	
	public void setNextValidator(ValidatorChain<?> nextValidator) {
		if(nextValidator != null) {
			passValidationMessagesToNextValidator(nextValidator);
			this.lastValidator.nextValidator = nextValidator;
			this.lastValidator = nextValidator;
		}
	}
	
	public void setValidationMessages(Map<String, String> validationMessages) {
		this.validationMessages = validationMessages;
	}
	
	public Map<String, String> getValidationMessages(){
		return this.validationMessages;
	}
	
	private void passValidationMessagesToNextValidator(ValidatorChain<?> nextValidator) {
		nextValidator.setValidationMessages(getValidationMessages());
	}
}
