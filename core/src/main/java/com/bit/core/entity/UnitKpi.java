package com.bit.core.entity;

import com.bit.core.entity.base.AuditableEntity;

public class UnitKpi extends AuditableEntity{
	private String name;
	private String description;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
