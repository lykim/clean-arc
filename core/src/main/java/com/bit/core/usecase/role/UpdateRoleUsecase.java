package com.bit.core.usecase.role;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.entity.Role;
import com.bit.core.entity.User;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.StringUtils;
import com.bit.core.validator.string.EmptyValidator;

public class UpdateRoleUsecase extends CommonTemplateUsecase<RoleRequestModel, ResponseModel>{

	private RoleGateway gateway;
	private Role role;
	public UpdateRoleUsecase(RoleRequestModel requestModel) {
		this.requestModel = requestModel;
		this.responseModel = new ResponseModel();
		gateway = (RoleGateway)GatewayFactory.ROLE_GATEWAY.get();
		authorize = new Authorizeable(null);
	}
	
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyValidator(RequestModel.ID_LABEL, requestModel.id));
	}

	@Override
	protected void doBussinessProcess() {
		gateway.update(role, requestModel.id);
	}

	@Override
	protected void parseRequestModelToEntity() {
		Role roleInStorage = getRoleById();
		role = new Role();
		role.setId(requestModel.id);
		if(StringUtils.isNotEmpty(requestModel.code) && isCodeNeedToSet(roleInStorage)) {
			role.setCode(requestModel.code);				
		}
		if(StringUtils.isNotEmpty(requestModel.description)) {
			role.setDescription(requestModel.description);
		}
	}
	private boolean isCodeNeedToSet(Role roleInStorage) {
		if(roleInStorage.getCode().equals(requestModel.code)) {
			return false;
		}
		checkRoleCodeAvailability();
		return true;
	}
	private Role getRoleById() {
		Role role = gateway.findById(requestModel.id);
		if(role == null) throw new RuntimeException(ErrorMessage.ID_IS_NOT_EXIST);
		return role;
	}
	private void checkRoleCodeAvailability() {
		Role roleLocal = gateway.findByCode(requestModel.code);
		if(roleLocal != null) throw new RuntimeException(ErrorMessage.CODE_ALREADY_TAKEN);			
	}
}
