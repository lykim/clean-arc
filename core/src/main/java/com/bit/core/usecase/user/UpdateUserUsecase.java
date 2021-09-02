package com.bit.core.usecase.user;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.RoleGroup;
import com.bit.core.entity.User;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;
import com.bit.core.validator.EmailValidator;
import com.bit.core.validator.string.EmptyValidator;

public class UpdateUserUsecase extends CommonTemplateUsecase<UserRequestModel, ResponseModel>{

	private UserGateway userGateway;
	private User user;
	public UpdateUserUsecase(UserRequestModel requestModel) {
		this.requestModel = requestModel;
		this.responseModel = new ResponseModel();
		userGateway = (UserGateway)GatewayFactory.USER_GATEWAY.get();
		List<String> roles = Arrays.asList(RoleCode.ROLE_USER_CUD);
		authorize = new Authorizeable(roles);
	}
	
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyValidator(RequestModel.ID_LABEL, requestModel.id));
		validator.setNextValidator(new EmailValidator(UserRequestModel.Label.EMAIL , requestModel.email));
	}

	@Override
	protected void doBussinessProcess() {
		userGateway.update(user, requestModel.id);
	}

	@Override
	protected void parseRequestModelToEntity() {
		User userInStorage = getUserById();
		user = new User();
		user.setId(requestModel.id);
		if(StringUtils.isNotEmpty(requestModel.username) && isUsernameNeedToSet(userInStorage)) {
			user.setUsername(requestModel.username);				
		}
		if(StringUtils.isNotEmpty(requestModel.email)) {
			user.setEmail(requestModel.email);
		}
		if(StringUtils.isNotEmpty(requestModel.salesmanCode)) {
			user.setSalesmanCode(requestModel.salesmanCode);
		}
		if(!CollectionUtils.isEmpty(requestModel.roleGroupIDs)) {
			Set<RoleGroup> roleGroups =  requestModel.roleGroupIDs.stream().map(id -> {
				RoleGroup roleGroup = new RoleGroup();
				roleGroup.setId(id);
				return roleGroup;
			}).collect(Collectors.toSet());
			user.setRoleGroups(roleGroups);			
		}
	}
	
	private boolean isUsernameNeedToSet(User userInStorage) {
		if(userInStorage.getUsername().equals(requestModel.username)) {
			return false;
		}
		checkUsernameAvailability();
		return true;
	}
	
	private User getUserById() {
		User user = userGateway.findById(requestModel.id);
		if(user == null) throw new RuntimeException(ErrorMessage.ID_IS_NOT_EXIST);
		return user;
	}
	
	private void checkUsernameAvailability() {
		User userLocal = userGateway.findByUsername(requestModel.username);
		if(userLocal != null) throw new RuntimeException(ErrorMessage.NAME_ALREADY_TAKEN);			
	}

}
