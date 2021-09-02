package com.bit.core.gateway;

import com.bit.core.entity.base.Entity;

public interface ActiveableGateway<E extends Entity,K>{
	E findById(K id);
	void setActive(K id, boolean active);
}
