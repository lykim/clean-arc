package com.bit.core.strategy.findOne;

import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.strategy.FindOneStrategy;

public class FindRoleGroupById extends FindOneStrategy<RoleGroup, RoleGroupRequestModel>{
	RoleGroupGateway gateway;
	public FindRoleGroupById(RoleGroupRequestModel param) {
		this.gateway = (RoleGroupGateway)GatewayFactory.ROLE_GROUP_GATEWAY.get();
		this.param = param;
	}
	
	@Override
	public RoleGroup findOne() {
		return gateway.findById(param.id);
	}
}
