package com.bit.core.strategy;

import java.util.List;

import com.bit.core.entity.base.Entity;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.Page;
import com.bit.core.validator.base.ValidatorChain;

public abstract class FindManyStrategy<E extends Entity, K extends RequestModel> {
	protected K param;
	public abstract Page<E> find();
	public List<ValidatorChain<?>> getValidatorChains(){return null; };
}
