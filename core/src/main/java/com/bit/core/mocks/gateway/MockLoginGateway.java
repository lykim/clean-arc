package com.bit.core.mocks.gateway;

import java.util.Optional;
import java.util.Set;

import com.bit.core.entity.User;
import com.bit.core.gateway.LoginGateway;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.LoginRequestModel;
import com.bit.core.utils.PasswordUtils;

public class MockLoginGateway implements LoginGateway{
	Set<User> users = MockStorage.users;
	private static LoginGateway INSTANCE;
	
	private MockLoginGateway() {}
	
	public static LoginGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new MockLoginGateway();
		}
		return INSTANCE;
	}
	
	@Override
	public void clean() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean login(LoginRequestModel login, User user) {
		if(user != null) {
			if(PasswordUtils.isVerified(login.password, user.getPassword())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public User getUser(LoginRequestModel request) {
		UserGateway userGateway = (UserGateway)GatewayFactory.USER_GATEWAY.get();
		return userGateway.findByUsernameShowPassword(request.username);
	}

}
