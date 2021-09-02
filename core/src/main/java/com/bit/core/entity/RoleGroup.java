package com.bit.core.entity;

import java.util.HashSet;
import java.util.Set;

import com.bit.core.entity.base.AuditableEntity;

public class RoleGroup extends AuditableEntity{
	private String name;
	private String description;
	private Set<Role> roles = new HashSet<>();
	
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
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}
