package com.bit.core.entity;

import java.util.HashSet;
import java.util.Set;

import com.bit.core.constant.Position;
import com.bit.core.entity.base.AuditableEntity;
import com.bit.core.model.request.base.Authenticateable;

public class User extends AuditableEntity{
	private String username;
	private String email;
	private String password;
	private Set<RoleGroup> roleGroups = new HashSet<>();
	private String salesmanCode;
	private Position position;
	private String branchId;
	public User() {
		super();
	}
	public User(Authenticateable authenticateable) {
		super(authenticateable);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<RoleGroup> getRoleGroups() {
		return roleGroups;
	}
	public void setRoleGroups(Set<RoleGroup> roleGroups) {
		this.roleGroups = roleGroups;
	}
	public String getSalesmanCode() {
		return salesmanCode;
	}
	public void setSalesmanCode(String salesmanCode) {
		this.salesmanCode = salesmanCode;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
}
