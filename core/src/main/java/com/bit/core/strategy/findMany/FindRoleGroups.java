package com.bit.core.strategy.findMany;

import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.FindManyRoleGroupRequestModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;

public class FindRoleGroups extends FindManyStrategy<RoleGroup, FindManyRoleGroupRequestModel>{
	RoleGroupGateway gateway;
	public FindRoleGroups(FindManyRoleGroupRequestModel param) {
		this.gateway = (RoleGroupGateway)GatewayFactory.ROLE_GROUP_GATEWAY.get();
		this.param = param;
	}
	@Override
	public Page<RoleGroup> find() {
		return gateway.find(param);
	}

}
