package com.bit.core.presenter;

import java.util.Arrays;
import java.util.List;

import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Role;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.FindManyRoleRequestModel;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.OnOffStrategy;
import com.bit.core.strategy.findMany.FindRoles;
import com.bit.core.strategy.findOne.FindRoleById;
import com.bit.core.usecase.ActiveInactiveUsecase;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.role.CreateRoleUsecase;
import com.bit.core.usecase.role.UpdateRoleUsecase;

public class RolePresenterImpl implements RolePresenter{

	@Override
	public CreateResponseModel create(RoleRequestModel request) {
		CreateRoleUsecase usecase = new CreateRoleUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public ResponseModel update(RoleRequestModel request) {
		UpdateRoleUsecase usecase = new UpdateRoleUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public DetailResponseModel<Role> findById(RoleRequestModel request) {
		FindOneStrategy<Role, RoleRequestModel> findOneStrategy = new FindRoleById(request);
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_VIEW);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<Role>(), findOneStrategy, roles);
		usecase.run();
		return (DetailResponseModel<Role>)usecase.getResponseModel();
	}

	@Override
	public ResponseModel setActive(ActiveInactiveRequestModel requestModel) {
		OnOffStrategy<?, ?, ?> onOffAble = new OnOffStrategy<>(requestModel.id, (RoleGateway)GatewayFactory.ROLE_GATEWAY.get());
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_CUD);
		Usecase<?,?> usecase = new ActiveInactiveUsecase(requestModel, onOffAble, roles);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public FindManyResponseModel<Role> findAll(FindManyRoleRequestModel requestModel) {
		FindManyStrategy<Role, FindManyRoleRequestModel> findManyStrategy = new FindRoles(requestModel);
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_VIEW);
		FindManyUsecase<?,?,FindManyResponseModel<Role>> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<Role>(), findManyStrategy, roles);
		usecase.run();
		return usecase.getResponseModel();
	}

}
