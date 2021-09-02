package com.bit.persistent.gateway;

import com.bit.core.entity.User;
import com.bit.core.gateway.LoginGateway;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.LoginRequestModel;
import com.bit.core.utils.PasswordUtils;

public class LoginGatewayImpl implements LoginGateway{

	private static LoginGateway INSTANCE;
	private LoginGatewayImpl() {};
	
	public static LoginGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new LoginGatewayImpl();
		}
		return INSTANCE;
	}
	
	@Override
	public void clean() {}
	
	@Override
	public boolean login(LoginRequestModel request, User user) {
		if(user != null) {
			if(PasswordUtils.isVerified(request.password, user.getPassword())) {
				return true;
			}
		}
		return false;
	}

	public User getUser(LoginRequestModel request) {
		UserGateway userGateway = (UserGateway)GatewayFactory.USER_GATEWAY.get();
		return userGateway.findByUsernameShowPassword(request.username);
	}
}
