package com.bit.core.model.request;

import com.bit.core.model.request.base.RequestModel;

public class LoginRequestModel extends RequestModel{
	public String username;
	public String password;
	
	public LoginRequestModel() {}
	
	public LoginRequestModel(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public static class Label{
		public static String USERNAME = "username";
		public static String PASSWORD = "password";
	}

}
