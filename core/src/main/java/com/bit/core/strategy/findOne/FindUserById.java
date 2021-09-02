package com.bit.core.strategy.findOne;

import com.bit.core.entity.User;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.strategy.FindOneStrategy;

public class FindUserById extends FindOneStrategy<User, UserRequestModel>{
	UserGateway userGateway;
	UserRequestModel param;
	public FindUserById(UserRequestModel param) {
		this.userGateway = (UserGateway)GatewayFactory.USER_GATEWAY.get();
		this.param = param;
	}
	
	@Override
	public User findOne() {
		return userGateway.findById(param.id);
	}

}
