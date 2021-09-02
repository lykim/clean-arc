package com.bit.core.presenter;

import com.bit.core.model.request.LoginRequestModel;
import com.bit.core.response.LoginResponseModel;
import com.bit.core.usecase.LoginUsecase;

public class LoginPresenterImpl implements LoginPresenter{

	@Override
	public LoginResponseModel usernamePasswordLogin(LoginRequestModel requestModel) {
		LoginUsecase loginUsecase = new LoginUsecase(requestModel);
		loginUsecase.run();
		return loginUsecase.getResponseModel();
	}

}
