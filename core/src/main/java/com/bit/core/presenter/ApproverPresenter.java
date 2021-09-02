package com.bit.core.presenter;

import com.bit.core.entity.Approver;
import com.bit.core.model.request.ApproverRequestModel;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

public interface ApproverPresenter {
	CreateResponseModel create(ApproverRequestModel request);
	ResponseModel update(ApproverRequestModel request);
	DetailResponseModel<Approver> findById(ApproverRequestModel request);
	FindManyResponseModel<Approver> findAll(FindManyRequestModel requestModel);
}
