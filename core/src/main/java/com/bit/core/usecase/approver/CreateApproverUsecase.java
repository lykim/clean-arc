package com.bit.core.usecase.approver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bit.core.constant.ApproverModule;
import com.bit.core.constant.ApproverableType;
import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.NotificatorType;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Approver;
import com.bit.core.entity.User;
import com.bit.core.entity.base.Notificator;
import com.bit.core.factory.NotificatorFactory;
import com.bit.core.gateway.ApproverGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.ApproverRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.template.CommonTemplateUsecase;
import com.bit.core.utils.StringUtils;
import com.bit.core.validator.EmptyCollectionValidator;
import com.bit.core.validator.string.EmptyValidator;

public class CreateApproverUsecase extends CommonTemplateUsecase<ApproverRequestModel, CreateResponseModel> {
	
	private ApproverGateway gateway;
	private Approver approver;
	public CreateApproverUsecase(ApproverRequestModel request) {
		this.requestModel = request;
		this.responseModel = new CreateResponseModel();
		this.gateway = (ApproverGateway)GatewayFactory.APPROVEABLE_GATEWAY.get();
		List<String> roles = Arrays.asList(RoleCode.ROLE_APPROVER_CUD);
		authorize = new Authorizeable(roles);
	}
	
	@Override
	protected void setupValidation() {
		validator.setNextValidator(new EmptyValidator(ApproverRequestModel.Label.type, requestModel.type));
		validator.setNextValidator(new EmptyValidator(ApproverRequestModel.Label.module, requestModel.module));
		validator.setNextValidator(new EmptyCollectionValidator<>(ApproverRequestModel.Label.approverIds, requestModel.approverIds));
		validator.setNextValidator(new EmptyCollectionValidator<>(ApproverRequestModel.Label.notificatorTypes, requestModel.notificatorTypes));
	}

	@Override
	protected void doBussinessProcess() {
		gateway.save(approver);
		responseModel.id = approver.getId();
	}

	@Override
	protected void parseRequestModelToEntity() {
		checkIfApprovalWithModuleExist();
		approver = new Approver();
		approver.setApproverabelType(parseApproveableType());	
		approver.setModule(parseApproverModule());
		approver.setApprovers(parseUser());
		approver.setNotificators(parseNotificator());
	}
	
	private void checkIfApprovalWithModuleExist() {
		Approver app =  gateway.findByModule(ApproverModule.get(requestModel.module));
		if(app != null) throw new RuntimeException(ErrorMessage.NAME_ALREADY_TAKEN);
	}
	
	private Set<Notificator> parseNotificator(){
		Set<Notificator> notificators = new HashSet<>();
		for(String notificatorType : requestModel.notificatorTypes) {
			Notificator notif = NotificatorFactory.get(NotificatorType.get(notificatorType));
			notificators.add(notif);
		}
		return notificators;
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
	
	private ApproverableType parseApproveableType() {
		ApproverableType type = ApproverableType.get(requestModel.type);
		if(type == null) throw new RuntimeException(ErrorMessage.CODE_IS_NOT_IN_ENUM);
		return type;
	}
	
	private ApproverModule parseApproverModule() {
		ApproverModule module = ApproverModule.get(requestModel.module);
		if(module == null) throw new RuntimeException(ErrorMessage.CODE_IS_NOT_IN_ENUM);
		return module;
	}
	
	

}
