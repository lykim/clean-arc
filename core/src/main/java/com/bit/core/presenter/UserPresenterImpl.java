package com.bit.core.presenter;

import java.util.Arrays;
import java.util.List;

import com.bit.core.constant.RoleCode;
import com.bit.core.entity.User;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.ChangePasswordRequestModel;
import com.bit.core.model.request.FindManyUserRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.OnOffStrategy;
import com.bit.core.strategy.findMany.FindUsers;
import com.bit.core.strategy.findOne.FindUserById;
import com.bit.core.strategy.findOne.FindUserByToken;
import com.bit.core.usecase.ActiveInactiveUsecase;
import com.bit.core.usecase.FindManyUsecase;
import com.bit.core.usecase.FindOneUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.ChangePasswordUsecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.usecase.user.UpdateUserUsecase;

public class UserPresenterImpl implements UserPresenter{

	@Override
	public CreateResponseModel create(UserRequestModel request) {
		CreateUserUsecase usecase = new CreateUserUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public ResponseModel update(UserRequestModel request) {
		UpdateUserUsecase usecase = new UpdateUserUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public DetailResponseModel<User> findById(UserRequestModel request) {
		FindOneStrategy<User, UserRequestModel> findOneStrategy = new FindUserById(request);
		List<String> roles = Arrays.asList(RoleCode.ROLE_USER_VIEW);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase(request, new DetailResponseModel<User>(), findOneStrategy, roles);
		usecase.run();
		DetailResponseModel<User> details = (DetailResponseModel)usecase.getResponseModel();
		details.entity.setPassword("");
		return details;
	}

	@Override
	public ResponseModel setActive(ActiveInactiveRequestModel requestModel) {
		OnOffStrategy<?, ?, ?> onOffAble = new OnOffStrategy<>(requestModel.id, (UserGateway)GatewayFactory.USER_GATEWAY.get());
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_CUD);
		Usecase<?,?> usecase = new ActiveInactiveUsecase(requestModel, onOffAble, roles);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public DetailResponseModel<User> findByToken(String token) {
		RequestModel request = new UserRequestModel();
		request.token = token;
		FindOneStrategy<User, RequestModel> findByToken = new FindUserByToken(request);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<User>(), findByToken, null);
		usecase.run();
		DetailResponseModel<User> details = (DetailResponseModel<User>)usecase.getResponseModel();
		if(details.entity != null) details.entity.setPassword("");
		return details;
	}

	@Override
	public FindManyResponseModel<User> findAll(FindManyUserRequestModel requestModel) {
		FindManyStrategy<User, FindManyUserRequestModel> findManyStrategy = new FindUsers(requestModel);
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_VIEW);
		FindManyUsecase<?,?,FindManyResponseModel<User>> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<User>(), findManyStrategy, roles);
		usecase.run();
		return usecase.getResponseModel();
	}

	@Override
	public ResponseModel changePassword(ChangePasswordRequestModel request) {
		ChangePasswordUsecase usecase = new ChangePasswordUsecase(request);
		usecase.run();
		return usecase.getResponseModel();
	}

}
