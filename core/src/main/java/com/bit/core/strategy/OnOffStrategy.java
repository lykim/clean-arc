package com.bit.core.strategy;

import com.bit.core.entity.base.Entity;
import com.bit.core.gateway.ActiveableGateway;

public class OnOffStrategy<E extends Entity, K, G extends ActiveableGateway<E, K>> {
	G gateway;
	K id;
	public OnOffStrategy(K id, G gateway) {
		this.gateway = gateway;
		this.id = id;
	}
	public boolean isNotInStorage() {
		E entity = gateway.findById(id);
		if(entity == null) return true;
		return false;
	}
	public void setActive(boolean active) {
//		E entity = gateway.findById(id);
//		entity.setActive(active);
		gateway.setActive(id, active);
	}
}
