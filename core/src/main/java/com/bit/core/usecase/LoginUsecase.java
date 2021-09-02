package com.bit.core.usecase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.entity.Role;
import com.bit.core.entity.RoleGroup;
import com.bit.core.entity.User;
import com.bit.core.gateway.LoginGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.LoginRequestModel;
import com.bit.core.response.LoginResponseModel;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.string.EmptyValidator;

public class LoginUsecase extends CommonTemplateUsecase<LoginRequestModel,LoginResponseModel>{

	LoginGateway gateway;
	public LoginUsecase(LoginRequestModel requestModel) {
		this.requestModel = requestModel;
		this.responseModel = new LoginResponseModel();
		gateway = (LoginGateway)GatewayFactory.LOGIN_GATEWAY.get();
	}
	
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyValidator(LoginRequestModel.Label.USERNAME, requestModel.username));
		validator.setNextValidator(new EmptyValidator(LoginRequestModel.Label.PASSWORD, requestModel.password));
	}

	@Override
	protected void doBussinessProcess() {
		User user = gateway.getUser(requestModel);
		boolean isLogin = gateway.login(requestModel, user);
		if(!isLogin) throw new RuntimeException(ErrorMessage.USERNAME_PASSWORD_INVALID);
		List<String> roles = new ArrayList<>();
		if(!CollectionUtils.isEmpty(user.getRoleGroups())) {
			Set<String> rolesSet = new HashSet<>();
			for(RoleGroup roleGroup :  user.getRoleGroups()) {
				for(Role role :  roleGroup.getRoles()) {
					rolesSet.add(role.getCode());
				}
			}
			roles = new ArrayList<>(rolesSet);
		}
		String jws = TokenUtils.createJWT(requestModel.username, roles, 86400000);
		responseModel.accessToken = jws;
	}

	@Override
	protected void parseRequestModelToEntity() {}
	
}
