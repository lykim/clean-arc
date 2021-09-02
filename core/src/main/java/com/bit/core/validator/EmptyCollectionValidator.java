package com.bit.core.validator;

import java.util.Collection;

import com.bit.core.utils.CollectionUtils;
import com.bit.core.validator.base.ValidatorChain;

public class EmptyCollectionValidator<T>  extends ValidatorChain<Collection<T>>{

	public final static String VALIDATION_MESSAGE = "Collection is Empty";
	
	public EmptyCollectionValidator(String label, Collection<T> value) {
		super(label, value);
	}
	
	@Override
	protected void validateValue() {
		if(CollectionUtils.isEmpty(value)) setValidationMessage();
		
	}
	private void setValidationMessage() {
		getValidationMessages().put(label, VALIDATION_MESSAGE);
	}

}
