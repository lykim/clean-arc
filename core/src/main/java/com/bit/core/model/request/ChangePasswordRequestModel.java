package com.bit.core.model.request;

import com.bit.core.model.request.base.RequestModel;

public class ChangePasswordRequestModel extends RequestModel{
	public String password;
	public String newPassword;
	public String verifyNewPassword;
	
	public ChangePasswordRequestModel() {}
	public ChangePasswordRequestModel(String password, String newPassword, String verifyNewPassword) {
		this.password = password;
		this.newPassword = newPassword;
		this.verifyNewPassword = verifyNewPassword;
	}
	
	public static final class Label{
		public static final String PASSWORD = "password"; 
		public static final String NEW_PASSWORD = "new_password";
		public static final String VERIFY_NEW_PASSWORD = "verify new password";
	}
}
