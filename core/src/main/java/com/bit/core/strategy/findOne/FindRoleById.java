package com.bit.core.strategy.findOne;

import com.bit.core.entity.Role;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.strategy.FindOneStrategy;

public class FindRoleById extends FindOneStrategy<Role, RoleRequestModel>{
	RoleGateway gateway;
	public FindRoleById(RoleRequestModel param) {
		this.gateway = (RoleGateway)GatewayFactory.ROLE_GATEWAY.get();
		this.param = param;
	}
	
	@Override
	public Role findOne() {
		return gateway.findById(param.id);
	}
}
