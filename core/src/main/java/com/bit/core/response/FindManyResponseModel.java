package com.bit.core.response;

import java.util.List;

import com.bit.core.entity.base.Entity;
import com.bit.core.response.base.Page;
import com.bit.core.response.base.ResponseModel;

public class FindManyResponseModel<E extends Entity> extends ResponseModel{
	public Page<E> page;
}
