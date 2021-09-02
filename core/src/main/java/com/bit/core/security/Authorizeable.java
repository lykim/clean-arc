package com.bit.core.security;

import java.util.List;

public class Authorizeable {
	
	public Authorizeable(List<String> acceptedRoles) {
		this.acceptedRoles = acceptedRoles;
	}
	
	private List<String> acceptedRoles;

	public List<String> getAcceptedRoles() {
		return acceptedRoles;
	}
	public void setAcceptedRoles(List<String> acceptedRoles) {
		this.acceptedRoles = acceptedRoles;
	}
	
	
}
