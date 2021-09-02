package com.bit.core.model.request;

import java.util.Set;

import com.bit.core.model.request.base.Authenticateable;
import com.bit.core.model.request.base.RequestModel;

public final class UserRequestModel extends RequestModel{
	public static class Label{
		public static String USERNAME = "username";
		public static String EMAIL = "email";
		public static String PASSWORD = "password";
	}
	public String username;
	public String email;
	public String password;
	public Authenticateable authentication;
	public Set<String> roleGroupIDs;
	public String salesmanCode;
	public int positionCode;
	public String branchId;
}
