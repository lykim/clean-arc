package com.bit.core.mocks.usecase;

import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;

public class MockCommonTemplateUsecase extends CommonTemplateUsecase<RequestModel,ResponseModel>{
	
	public MockCommonTemplateUsecase(RequestModel requestModel, Authorizeable authorize) {
		this.requestModel = requestModel;
		this.responseModel = new ResponseModel();
		this.authorize = authorize;
	}

	@Override
	protected void setupValidation() {

	}

	@Override
	protected void doBussinessProcess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void parseRequestModelToEntity() {
		// TODO Auto-generated method stub
		
	}
}
