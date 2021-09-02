package com.bit.core.usecase.user;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.entity.User;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.ChangePasswordRequestModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.PasswordUtils;
import com.bit.core.validator.NotEqualValidator;
import com.bit.core.validator.string.EmptyValidator;

public class ChangePasswordUsecase extends CommonTemplateUsecase<ChangePasswordRequestModel, ResponseModel>{

	UserGateway gateway;
	User user;
	public ChangePasswordUsecase(ChangePasswordRequestModel requestModel) {
		this.requestModel = requestModel;
		this.responseModel = new ResponseModel();
		gateway = (UserGateway)GatewayFactory.USER_GATEWAY.get();
	}
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyValidator(ChangePasswordRequestModel.ID_LABEL, requestModel.id));
		validator.setNextValidator(new EmptyValidator(ChangePasswordRequestModel.Label.PASSWORD, requestModel.password));
		validator.setNextValidator(new EmptyValidator(ChangePasswordRequestModel.Label.NEW_PASSWORD, requestModel.newPassword));
		validator.setNextValidator(new EmptyValidator(ChangePasswordRequestModel.Label.VERIFY_NEW_PASSWORD, requestModel.verifyNewPassword));
		validator.setNextValidator(new NotEqualValidator(ChangePasswordRequestModel.Label.VERIFY_NEW_PASSWORD, requestModel.newPassword, requestModel.verifyNewPassword));
	}

	@Override
	protected void doBussinessProcess() {
		gateway.changePassword(requestModel.id, PasswordUtils.generate(requestModel.newPassword));
	}

	@Override
	protected void parseRequestModelToEntity() {
		preParse();
	}
	
	private void preParse() {
		setUserById();
		checkPassword();
	}
	
	private void setUserById() {
		user = gateway.findByIdShowPassword(requestModel.id);
	}
	
	private void checkPassword() {
		if(!PasswordUtils.isVerified(requestModel.password, user.getPassword())) throw new RuntimeException(ErrorMessage.WRONG_PASSWORD);
	}

}
