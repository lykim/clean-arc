package com.bit.core.presenter;

import com.bit.core.entity.RoleGroup;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.FindManyRoleGroupRequestModel;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

public interface RoleGroupPresenter {
	CreateResponseModel create(RoleGroupRequestModel request);
	ResponseModel update(RoleGroupRequestModel request);
	DetailResponseModel<RoleGroup> findById(RoleGroupRequestModel request);
	ResponseModel setActive(ActiveInactiveRequestModel requestModel);
	FindManyResponseModel<RoleGroup> findAll(FindManyRoleGroupRequestModel requestModel);
}
