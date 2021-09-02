package com.bit.core.strategy.findMany;

import com.bit.core.entity.Approver;
import com.bit.core.entity.Role;
import com.bit.core.gateway.ApproverGateway;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyRoleRequestModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;

public class FindApprovers extends FindManyStrategy<Approver, FindManyRequestModel>{
	public FindApprovers(FindManyRequestModel param) {
		this.param = param;
	}
	@Override
	public Page<Approver> find() {
		ApproverGateway gateway = (ApproverGateway)GatewayFactory.APPROVEABLE_GATEWAY.get();
		return gateway.find(param);
	}

}

