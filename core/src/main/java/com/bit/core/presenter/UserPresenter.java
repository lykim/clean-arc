package com.bit.core.presenter;

import com.bit.core.entity.User;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.ChangePasswordRequestModel;
import com.bit.core.model.request.FindManyUserRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

public interface UserPresenter {
	CreateResponseModel create(UserRequestModel request);
	ResponseModel update(UserRequestModel request);
	DetailResponseModel<User> findById(UserRequestModel request);
	ResponseModel setActive(ActiveInactiveRequestModel requestModel);
	DetailResponseModel<User> findByToken(String token);
	FindManyResponseModel<User> findAll(FindManyUserRequestModel requestModel);
	ResponseModel changePassword(ChangePasswordRequestModel request);
}
