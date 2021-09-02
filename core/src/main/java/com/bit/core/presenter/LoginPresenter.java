package com.bit.core.presenter;

import com.bit.core.model.request.LoginRequestModel;
import com.bit.core.response.LoginResponseModel;

public interface LoginPresenter {
	LoginResponseModel usernamePasswordLogin(LoginRequestModel requestModel);
}
