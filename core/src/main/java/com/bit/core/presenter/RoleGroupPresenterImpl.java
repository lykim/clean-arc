package com.bit.core.presenter;

import java.util.Arrays;
import java.util.List;

import com.bit.core.constant.RoleCode;
import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.FindManyRoleGroupRequestModel;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.OnOffStrategy;
import com.bit.core.strategy.findMany.FindRoleGroups;
import com.bit.core.strategy.findOne.FindRoleGroupById;
import com.bit.core.usecase.ActiveInactiveUsecase;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.roleGroup.CreateRoleGroupUsecase;
import com.bit.core.usecase.roleGroup.UpdateRoleGroupUsecase;

public class RoleGroupPresenterImpl implements RoleGroupPresenter{

	@Override
	public CreateResponseModel create(RoleGroupRequestModel request) {
		CreateRoleGroupUsecase usecase = new CreateRoleGroupUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public ResponseModel update(RoleGroupRequestModel request) {
		UpdateRoleGroupUsecase usecase = new UpdateRoleGroupUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public DetailResponseModel<RoleGroup> findById(RoleGroupRequestModel request) {
		FindOneStrategy<RoleGroup, RoleGroupRequestModel> findOneStrategy = new FindRoleGroupById(request);
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_VIEW);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<RoleGroup>(), findOneStrategy, roles);
		usecase.run();
		return (DetailResponseModel<RoleGroup>)usecase.getResponseModel();
	}

	@Override
	public ResponseModel setActive(ActiveInactiveRequestModel requestModel) {
		OnOffStrategy<?, ?, ?> onOffAble = new OnOffStrategy<>(requestModel.id, (RoleGroupGateway)GatewayFactory.ROLE_GROUP_GATEWAY.get());
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_CUD);
		Usecase<?,?> usecase = new ActiveInactiveUsecase(requestModel, onOffAble, roles);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public FindManyResponseModel<RoleGroup> findAll(FindManyRoleGroupRequestModel requestModel) {
		FindManyStrategy<RoleGroup, FindManyRoleGroupRequestModel> findManyStrategy = new FindRoleGroups(requestModel);
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_VIEW);
		FindManyUsecase<?,?,FindManyResponseModel<RoleGroup>> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<RoleGroup>(), findManyStrategy, roles);
		usecase.run();
		return usecase.getResponseModel();
	}

}
