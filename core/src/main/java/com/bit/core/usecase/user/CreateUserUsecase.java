package com.bit.core.usecase.user;

import static com.bit.core.utils.ValidationUtils.setEmptyValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.Position;
import com.bit.core.constant.RoleCode;
import com.bit.core.constant.TargetKpiStatus;
import com.bit.core.entity.RoleGroup;
import com.bit.core.entity.User;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.PasswordUtils;
import com.bit.core.utils.StringUtils;
import com.bit.core.validator.EmailValidator;
import com.bit.core.validator.PasswordValidator;

public class CreateUserUsecase extends CommonTemplateUsecase<UserRequestModel, CreateResponseModel>{
	private UserGateway userGateway;
	private User user;
	public CreateUserUsecase(UserRequestModel requestModel) {
		this.requestModel = requestModel;
		this.responseModel = new CreateResponseModel();
		userGateway = (UserGateway)GatewayFactory.USER_GATEWAY.get();
		List<String> roles = Arrays.asList(RoleCode.ROLE_USER_CUD);
		authorize = new Authorizeable(roles);
	}

	@Override
	protected void setupValidation() {
		setEmptyValidator(validator, UserRequestModel.Label.USERNAME, requestModel.username);
		setEmptyValidator(validator, UserRequestModel.Label.EMAIL, requestModel.email);
		setEmptyValidator(validator, UserRequestModel.Label.PASSWORD, requestModel.password);
		validator.setNextValidator(new PasswordValidator(UserRequestModel.Label.PASSWORD, requestModel.password));
		validator.setNextValidator(new EmailValidator(UserRequestModel.Label.EMAIL, requestModel.email));
	}

	@Override
	protected void parseRequestModelToEntity() {
		preParse();
		user = new User(requestModel.authentication);
		user.setUsername(requestModel.username.trim());
		user.setEmail(requestModel.email.trim());
		user.setPassword(PasswordUtils.generate(requestModel.password));
		if(!CollectionUtils.isEmpty(requestModel.roleGroupIDs)) {
			Set<RoleGroup> roleGroups =  requestModel.roleGroupIDs.stream().map(id -> {
				RoleGroup roleGroup = new RoleGroup();
				roleGroup.setId(id);
				return roleGroup;
			}).collect(Collectors.toSet());
			user.setRoleGroups(roleGroups);			
		}
		user.setSalesmanCode(requestModel.salesmanCode);
		user.setPosition(Position.get(requestModel.positionCode));
		user.setBranchId(requestModel.branchId);
	}
	private void preParse() {
		if(requestModel.positionCode > 0) {
			checkEnumCode();			
		}
	}
	private void checkEnumCode() {
		try {
			Position position = Position.get(requestModel.positionCode);
			if(position == null) throw new RuntimeException(ErrorMessage.CODE_IS_NOT_EXIST);
		}catch (Exception e) {
			throw new RuntimeException(ErrorMessage.CODE_IS_NOT_EXIST);
		}
	}
	
	@Override
	protected void doBussinessProcess() {
		checkUsernameAvailability();
		userGateway.save(user);
		responseModel.id = user.getId();
	}

	private void checkUsernameAvailability() {
		User user = userGateway.findByUsername(requestModel.username);
		if(user != null) throw new RuntimeException(ErrorMessage.NAME_ALREADY_TAKEN);
	}
	
}
