package com.bit.core.strategy.findMany;

import com.bit.core.entity.Role;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.FindManyRoleRequestModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;

public class FindRoles extends FindManyStrategy<Role, FindManyRoleRequestModel>{
	RoleGateway gateway;
	public FindRoles(FindManyRoleRequestModel param) {
		this.gateway = (RoleGateway)GatewayFactory.ROLE_GATEWAY.get();
		this.param = param;
	}
	@Override
	public Page<Role> find() {
		return gateway.find(param);
	}

}

