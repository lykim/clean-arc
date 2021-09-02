package com.bit.core.strategy;

import com.bit.core.entity.base.Entity;
import com.bit.core.model.request.base.RequestModel;

public abstract class FindOneStrategy<E extends Entity, K extends RequestModel> {
	protected K param;
	public abstract E findOne();
}
