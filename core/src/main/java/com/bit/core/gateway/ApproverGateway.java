package com.bit.core.gateway;

import java.util.Set;

import com.bit.core.constant.ApproverModule;
import com.bit.core.entity.Approver;
import com.bit.core.entity.User;
import com.bit.core.entity.base.Notificator;

public interface ApproverGateway extends CrudGateway<Approver, String>, ActiveableGateway<Approver, String>{
	Approver findByModule(ApproverModule module);
	void updateApprovers(Set<User> approvers, String id);
	void updateNotificators(Set<Notificator> notificators, String id);
}
