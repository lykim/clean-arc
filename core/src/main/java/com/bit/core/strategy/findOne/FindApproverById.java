package com.bit.core.strategy.findOne;

import com.bit.core.entity.Approver;
import com.bit.core.gateway.ApproverGateway;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.strategy.FindOneStrategy;

public class FindApproverById extends FindOneStrategy<Approver, RequestModel>{

	public FindApproverById(RequestModel param) {
		this.param = param;
	}
	
	@Override
	public Approver findOne() {
		ApproverGateway gateway = (ApproverGateway)GatewayFactory.APPROVEABLE_GATEWAY.get();
		return gateway.findById(param.id);
	}

}
