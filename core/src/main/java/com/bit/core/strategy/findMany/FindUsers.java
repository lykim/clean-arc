package com.bit.core.strategy.findMany;

import com.bit.core.entity.User;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.FindManyUserRequestModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;

public class FindUsers extends FindManyStrategy<User, FindManyUserRequestModel>{
	UserGateway userGateway;
	public FindUsers(FindManyUserRequestModel param) {
		this.userGateway = (UserGateway)GatewayFactory.USER_GATEWAY.get();
		this.param = param;
	}
	@Override
	public Page<User> find() {
		return userGateway.find(param);
	}

}
