package com.bit.core.gateway;

import com.bit.core.entity.User;
import com.bit.core.model.request.LoginRequestModel;

public interface LoginGateway extends Gateway{
	boolean login(LoginRequestModel  login, User user);
	public User getUser(LoginRequestModel request);
}
