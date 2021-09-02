package com.bit.core.entity;

import java.util.Set;

import com.bit.core.constant.ApproverModule;
import com.bit.core.constant.ApproverableType;
import com.bit.core.entity.base.AuditableEntity;
import com.bit.core.entity.base.Notificator;

public class Approver extends AuditableEntity implements Cloneable{
	private Set<User> approvers;
	private Set<Notificator> notificators;
	private ApproverModule module;
	private ApproverableType approverabelType;
	
	public Approver() {}
	public Approver(ApproverModule module, Set<User> approvers, Set<Notificator> notificators, ApproverableType approverabelType) {
		this.module = module;
		this.approvers = approvers;
		this.notificators = notificators;
		this.approverabelType = approverabelType;
	}
	
	public Set<User> getApprovers() {
		return approvers;
	}
	public void setApprovers(Set<User> approvers) {
		this.approvers = approvers;
	}
	public Set<Notificator> getNotificators() {
		return notificators;
	}
	public void setNotificators(Set<Notificator> notificators) {
		this.notificators = notificators;
	}
	public ApproverModule getModule() {
		return module;
	}
	public void setModule(ApproverModule module) {
		this.module = module;
	}
	public ApproverableType getApproverabelType() {
		return approverabelType;
	}
	public void setApproverabelType(ApproverableType approverabelType) {
		this.approverabelType = approverabelType;
	}
	
}
