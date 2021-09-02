package com.bit.core.strategy.findOne;

import com.bit.core.entity.User;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.utils.TokenUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class FindUserByToken extends FindOneStrategy<User, RequestModel>{
	UserGateway userGateway;
	public FindUserByToken(RequestModel param) {
		this.userGateway = (UserGateway)GatewayFactory.USER_GATEWAY.get();
		this.param = param;
	}
	
	
	@Override
	public User findOne() {
		Jws<Claims> claims = TokenUtils.parseClaims(param.token);
		Claims claimsBody = claims.getBody();
		return userGateway.findByUsername(claimsBody.getSubject());
	}

}
