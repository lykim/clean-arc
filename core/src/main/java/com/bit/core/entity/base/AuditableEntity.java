package com.bit.core.entity.base;

import java.time.Instant;

import com.bit.core.model.request.base.Authenticateable;

public abstract class AuditableEntity extends Entity{
	private String createdBy;
	private String updatedBy;
	private Instant createdTime;
	private Instant updatedTime;
	
	public AuditableEntity() {
		super();
		setAuditorId("System");
		setAuditorTime();
	}
	
	public AuditableEntity(Authenticateable authenticateable) {
		super();
		if(authenticateable != null) {
			setAuditorId(authenticateable.requesterId);
		}else {
			setAuditorId("System");
		}

		Instant now =  Instant.now();
		this.createdTime = now;
		this.updatedTime = now;
	}
	
	private void setAuditorId(String id) {
		this.createdBy = id;
		this.updatedBy = id;
	}
	
	private void setAuditorTime() {
		Instant now =  Instant.now();
		this.createdTime = now;
		this.updatedTime = now;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public Instant getCreatedTime() {
		return createdTime;
	}
	public Instant getUpdatedTime() {
		return updatedTime;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setCreatedTime(Instant createdTime) {
		this.createdTime = createdTime;
	}

	public void setUpdatedTime(Instant updatedTime) {
		this.updatedTime = updatedTime;
	}
	
}
