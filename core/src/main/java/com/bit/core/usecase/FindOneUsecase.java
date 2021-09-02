package com.bit.core.usecase;

import java.util.List;

import com.bit.core.entity.base.Entity;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;

public class FindOneUsecase<E extends Entity,I extends RequestModel, O extends DetailResponseModel<E>> extends CommonTemplateUsecase<I, O>{

	UserGateway gateway;
	FindOneStrategy<?, ?> findOneStrategy;
	public FindOneUsecase(I requestModel, O responseModel, FindOneStrategy<E , I> findOneStrategy, List<String> roles) {
		gateway = (UserGateway)GatewayFactory.USER_GATEWAY.get();
		this.requestModel = requestModel;
		this.findOneStrategy = findOneStrategy;
		this.responseModel = responseModel;
		authorize = new Authorizeable(roles);
	}
	@Override
	protected void setupValidation() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void doBussinessProcess() {
		responseModel.entity = (E)findOneStrategy.findOne();
	}
	@Override
	protected void parseRequestModelToEntity() {
		// TODO Auto-generated method stub
		
	}
	

}
