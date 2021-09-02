package com.bit.core.usecase.approver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bit.core.constant.ApproverableType;
import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Approver;
import com.bit.core.entity.User;
import com.bit.core.gateway.ApproverGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.ApproverRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;
import com.bit.core.validator.string.EmptyValidator;

public class UpdateApproverUsecase extends CommonTemplateUsecase<ApproverRequestModel, ResponseModel> {

	private ApproverGateway gateway;
	private Approver approver;
	private Approver approverToUpdate;
	public UpdateApproverUsecase(ApproverRequestModel request) {
		this.requestModel = request;
		this.responseModel = new CreateResponseModel();
		this.gateway = (ApproverGateway)GatewayFactory.APPROVEABLE_GATEWAY.get();
		List<String> roles = Arrays.asList(RoleCode.ROLE_APPROVER_CUD);
		authorize = new Authorizeable(roles);
	}
	
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyValidator(RequestModel.ID_LABEL, requestModel.id));
	}

	@Override
	protected void doBussinessProcess() {
		gateway.update(approverToUpdate, approver.getId());
		
	}

	@Override
	protected void parseRequestModelToEntity() {
		preParse();
		ApproverableType type = parseApproveableType();
		approverToUpdate = new Approver();
		approverToUpdate.setId(approver.getId());
		approverToUpdate.setApproverabelType( type );
		if(!CollectionUtils.isEmpty(requestModel.approverIds)) {
			approverToUpdate.setApprovers(parseUser());
		}
	}
	private Set<User> parseUser(){
		Set<User> users = new HashSet<>();
		for(String userId : requestModel.approverIds) {
			if(StringUtils.isNotEmpty(userId)) {
				User user = new User();
				user.setId(userId);
				users.add(user);				
			}
		}
		return users;
	}
	
	private void preParse() {
		syncApprover();
	}
	
	private void syncApprover() {
		approver = gateway.findById(requestModel.id);
		if(approver == null) throw new RuntimeException(ErrorMessage.ID_IS_NOT_EXIST);
	}
	private ApproverableType parseApproveableType() {
		if(StringUtils.isNotEmpty(requestModel.type)) {
			ApproverableType type = ApproverableType.get(requestModel.type);
			if(type == null) throw new RuntimeException(ErrorMessage.CODE_IS_NOT_IN_ENUM);
			return type;			
		}else {
			return null;
		}

	}

}
