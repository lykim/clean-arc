package com.bit.core.gateway;

import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.response.base.Page;

public interface CrudGateway<T,K> extends Gateway{
	T findById(K id);
	void save(T entity);
	void update(T entity, K id);
	Page<T> find(FindManyRequestModel param);
}
