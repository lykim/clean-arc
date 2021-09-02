package com.bit.core.strategy;

import java.util.Set;

import com.bit.core.constant.ApproverModule;
import com.bit.core.entity.Approver;
import com.bit.core.entity.User;
import com.bit.core.entity.base.Notificator;
import com.bit.core.gateway.ApproverGateway;
import com.bit.core.gateway.factory.GatewayFactory;

public abstract class Approveable{

	private ApproverModule approverModule;
	protected ApproverGateway approverGateway;
	protected Approver approver;
	public Approveable() {}
	public Approveable(Approver approver) {
//		this.approverGateway = (ApproverGateway)GatewayFactory.APPROVEABLE_GATEWAY.get();
//		this.approver = this.approverGateway.findByModule(approverModule);
		this.approver = approver;
	}
	
	public Approver getApprover() {
		return approver;
	}
	
	public Set<User> getApprovers(){
		return approver.getApprovers() ;
	}
	public Set<Notificator> getNotificators(){
		return approver.getNotificators();
	}

	public ApproverModule getModule() {
		return approverModule;
	}

	public void setApprovers(Set<User> approvers) {
		this.approverGateway.updateApprovers(approvers, this.approver.getId());
	}

	public void setNotificators(Set<Notificator> notificators) {
		this.approverGateway.updateNotificators(notificators, this.approver.getId());
	}
}
