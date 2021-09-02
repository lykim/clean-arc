package com.bit.core.presenter;

import com.bit.core.entity.Role;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.FindManyRoleRequestModel;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

public interface RolePresenter {
	CreateResponseModel create(RoleRequestModel request);
	ResponseModel update(RoleRequestModel request);
	DetailResponseModel<Role> findById(RoleRequestModel request);
	ResponseModel setActive(ActiveInactiveRequestModel requestModel);
	FindManyResponseModel<Role> findAll(FindManyRoleRequestModel requestModel);
}
