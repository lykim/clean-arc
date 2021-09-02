package com.bit.core.usecase;

import java.util.List;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.strategy.OnOffStrategy;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.validator.NullValidator;
import com.bit.core.validator.string.EmptyValidator;

public class ActiveInactiveUsecase extends CommonTemplateUsecase<ActiveInactiveRequestModel, ResponseModel>{

	OnOffStrategy<?,?,?> onoffable;
	public ActiveInactiveUsecase(ActiveInactiveRequestModel requestModel, OnOffStrategy<?,?,?> onoffable, List<String> roles) {
		this.requestModel = requestModel;
		this.responseModel = new ResponseModel();
		this.onoffable = onoffable;
		authorize = new Authorizeable(roles);
	}
	
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyValidator(RequestModel.ID_LABEL, requestModel.id));
		validator.setNextValidator(new NullValidator(ActiveInactiveRequestModel.ACTIVE_LABEL, requestModel.active));
	}

	@Override
	protected void doBussinessProcess() {
		onoffable.setActive(requestModel.active);
	}

	@Override
	protected void parseRequestModelToEntity() {
		preParse();
	}
	
	private void preParse() {
		if(onoffable.isNotInStorage()) throw new RuntimeException(ErrorMessage.ID_IS_NOT_EXIST);
	}
}
