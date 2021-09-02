package com.bit.core.presenter;

import java.util.Arrays;
import java.util.List;

import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Approver;
import com.bit.core.model.request.ApproverRequestModel;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.findMany.FindApprovers;
import com.bit.core.strategy.findOne.FindApproverById;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.usecase.approver.CreateApproverUsecase;
import com.bit.core.usecase.approver.UpdateApproverUsecase;

public class ApproverPresenterImpl implements ApproverPresenter{

	@Override
	public CreateResponseModel create(ApproverRequestModel request) {
		CreateApproverUsecase usecase = new CreateApproverUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public ResponseModel update(ApproverRequestModel request) {
		UpdateApproverUsecase usecase = new UpdateApproverUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public DetailResponseModel<Approver> findById(ApproverRequestModel request) {
		FindOneStrategy<Approver, RequestModel> findOneStrategy = new FindApproverById(request);
		List<String> roles = Arrays.asList(RoleCode.ROLE_APPROVER_VIEW);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<Approver>(), findOneStrategy, roles);
		usecase.run();
		return (DetailResponseModel<Approver>)usecase.getResponseModel();
	}

	@Override
	public FindManyResponseModel<Approver> findAll(FindManyRequestModel requestModel) {
		FindManyStrategy<Approver, FindManyRequestModel> findManyStrategy = new FindApprovers(requestModel);
		List<String> roles = Arrays.asList(RoleCode.ROLE_APPROVER_VIEW);
		FindManyUsecase<?,?,FindManyResponseModel<Approver>> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Approver>(), findManyStrategy, roles);
		usecase.run();
		return usecase.getResponseModel();
	}

}
