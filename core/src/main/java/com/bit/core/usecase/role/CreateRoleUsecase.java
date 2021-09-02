package com.bit.core.usecase.role;

import static com.bit.core.utils.ValidationUtils.setEmptyValidator;

import java.util.Arrays;
import java.util.List;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Role;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;

public class CreateRoleUsecase extends CommonTemplateUsecase<RoleRequestModel, CreateResponseModel>{

	RoleGateway gateway;
	Role role;
	public CreateRoleUsecase(RoleRequestModel request) {
		this.requestModel = request;
		this.responseModel = new CreateResponseModel();
		this.gateway = (RoleGateway)GatewayFactory.ROLE_GATEWAY.get();
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_CUD);
		authorize = new Authorizeable(roles);
	}
	@Override
	protected void setupValidation() {
		setEmptyValidator(validator, RoleRequestModel.Label.CODE, requestModel.code);
	}

	@Override
	protected void doBussinessProcess() {
		checkCodenameAvailability();
		this.gateway.save(role);
		responseModel.id = role.getId();
	}

	@Override
	protected void parseRequestModelToEntity() {
		role = new Role();
		role.setCode(requestModel.code.trim());
		role.setDescription(requestModel.description);
		
	}
	private void checkCodenameAvailability() {
		Role entity = gateway.findByCode(requestModel.code);
		if(entity != null) throw new RuntimeException(ErrorMessage.CODE_ALREADY_TAKEN);
	}

}
