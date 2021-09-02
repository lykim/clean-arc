package com.bit.core.usecase.roleGroup;

import static com.bit.core.utils.ValidationUtils.setEmptyValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Role;
import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.RoleGroupRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.CollectionUtils;

public class CreateRoleGroupUsecase extends CommonTemplateUsecase<RoleGroupRequestModel, CreateResponseModel>{

	RoleGroupGateway gateway;
	RoleGroup roleGroup;
	public CreateRoleGroupUsecase(RoleGroupRequestModel request) {
		this.requestModel = request;
		this.responseModel = new CreateResponseModel();
		this.gateway = (RoleGroupGateway)GatewayFactory.ROLE_GROUP_GATEWAY.get();
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_CUD);
		authorize = new Authorizeable(roles);
	}
	@Override
	protected void setupValidation() {
		setEmptyValidator(validator, RoleGroupRequestModel.Label.NAME, requestModel.name);
	}

	@Override
	protected void doBussinessProcess() {
		checkNamenameAvailability();
		this.gateway.save(roleGroup);
		responseModel.id = roleGroup.getId();
	}

	@Override
	protected void parseRequestModelToEntity() {
		roleGroup = new RoleGroup();
		roleGroup.setName(requestModel.name.trim());
		roleGroup.setDescription(requestModel.description);
		if(!CollectionUtils.isEmpty(requestModel.roleIds)) {
			Set<Role> roles =  requestModel.roleIds.stream().map(id -> {
				Role role = new Role();
				role.setId(id);
				return role;
			}).collect(Collectors.toSet());
			roleGroup.setRoles(roles);			
		}
	}
	private void checkNamenameAvailability() {
		RoleGroup entity = gateway.findByName(requestModel.name);
		if(entity != null) throw new RuntimeException(ErrorMessage.NAME_ALREADY_TAKEN);
	}

}
