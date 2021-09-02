package com.bit.core.usecase.roleGroup;

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
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;
import com.bit.core.validator.string.EmptyValidator;

public class UpdateRoleGroupUsecase extends CommonTemplateUsecase<RoleGroupRequestModel, ResponseModel>{

	private RoleGroupGateway gateway;
	private RoleGroup roleGroup;
	public UpdateRoleGroupUsecase(RoleGroupRequestModel requestModel) {
		this.requestModel = requestModel;
		this.responseModel = new ResponseModel();
		gateway = (RoleGroupGateway)GatewayFactory.ROLE_GROUP_GATEWAY.get();
		List<String> roles = Arrays.asList(RoleCode.ROLE_ROLE_GROUP_CUD);
		authorize = new Authorizeable(roles);
	}
	
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyValidator(RequestModel.ID_LABEL, requestModel.id));
	}

	@Override
	protected void doBussinessProcess() {
		gateway.update(roleGroup, requestModel.id);
	}

	@Override
	protected void parseRequestModelToEntity() {
		RoleGroup roleGroupInStorage = getRoleGroupById();
		roleGroup = new RoleGroup();
		roleGroup.setId(requestModel.id);
		if(StringUtils.isNotEmpty(requestModel.name) && isNameNeedToSet(roleGroupInStorage)) {
			roleGroup.setName(requestModel.name);				
		}
		if(StringUtils.isNotEmpty(requestModel.description)) {
			roleGroup.setDescription(requestModel.description);
		}
		if(!CollectionUtils.isEmpty(requestModel.roleIds)) {
			Set<Role> roles =  requestModel.roleIds.stream().map(id -> {
				Role role = new Role();
				role.setId(id);
				return role;
			}).collect(Collectors.toSet());
			roleGroup.setRoles(roles);			
		}
	}
	private boolean isNameNeedToSet(RoleGroup roleGroupInStorage) {
		if(roleGroupInStorage.getName().equals(requestModel.name)) {
			return false;
		}
		checkRoleGroupNameAvailability();
		return true;
	}
	private RoleGroup getRoleGroupById() {
		RoleGroup roleGroup = gateway.findById(requestModel.id);
		if(roleGroup == null) throw new RuntimeException(ErrorMessage.ID_IS_NOT_EXIST);
		return roleGroup;
	}
	private void checkRoleGroupNameAvailability() {
		RoleGroup roleGroupLocal = gateway.findByName(requestModel.name);
		if(roleGroupLocal != null) throw new RuntimeException(ErrorMessage.NAME_ALREADY_TAKEN);			
	}
}
