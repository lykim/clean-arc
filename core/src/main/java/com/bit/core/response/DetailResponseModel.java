package com.bit.core.response;

import com.bit.core.entity.base.Entity;
import com.bit.core.response.base.ResponseModel;

public class DetailResponseModel<E extends Entity> extends ResponseModel{
	public E entity;
}
