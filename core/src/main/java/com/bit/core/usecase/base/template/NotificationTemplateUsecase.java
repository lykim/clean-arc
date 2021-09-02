package com.bit.core.usecase.base.template;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bit.core.constant.ApproverModule;
import com.bit.core.entity.Approver;
import com.bit.core.entity.User;
import com.bit.core.entity.base.Notificator;
import com.bit.core.factory.ApproveableFactory;
import com.bit.core.gateway.ApproverGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.NotificationResponseModel;
import com.bit.core.response.base.NotificationResponse;
import com.bit.core.strategy.Approveable;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;

public abstract class NotificationTemplateUsecase<I extends RequestModel, O extends NotificationResponseModel> extends CommonTemplateUsecase<I,O>{
	protected Approveable approveable;
	protected ApproverModule approveableModule;
	private Set<Notificator> notificators = new HashSet<>();
	private Set<User> receivers = new HashSet<>();
	public NotificationTemplateUsecase(ApproverModule approveableModule){
		this.approveableModule = approveableModule;
		ApproverGateway approverGateway = (ApproverGateway)GatewayFactory.APPROVEABLE_GATEWAY.get();
		Approver approver = approverGateway.findByModule(approveableModule);
		this.approveable = ApproveableFactory.get(approver);
	}
	
	@Override
	protected void finishing() {
		setNotificationResponse(Boolean.TRUE,null);
		if(approveable == null)	{
			setNotificationResponse(Boolean.FALSE, "approveable is null");
		}else {
			checkApproverExist();
		}
	}
	
	private void checkApproverExist() {
		if(approveable.getApprover() == null) {
			setNotificationResponse(Boolean.FALSE, "approver instance is null");
		}else {
			setupNotificators();				
		}		
	}
	
	private void setupNotificators() {
		if(CollectionUtils.isEmpty(approveable.getNotificators())) {
			setNotificationResponse(Boolean.FALSE, "notificators is null");
		}else {
			notificators = approveable.getNotificators();
			setupReceivers();
		}
	}
	
	private void setupReceivers() {
		if(CollectionUtils.isEmpty(approveable.getApprovers())) {
			setNotificationResponse(Boolean.FALSE, "receivers is null");
		}else {
			receivers = approveable.getApprovers();
			sendNotification();
		}
	}
	
	private void sendNotification() {
		Map<String,String> errorMap = new HashMap<>();
		for(Notificator notificator : this.notificators) {
			NotificationResponse resp =  notificator.send(receivers);
			errorMap.putAll(resp.errorMessages);
			if(!resp.isAllSuccess) {
				setNotificationResponse(Boolean.FALSE, null);
			}
		}
		if(!errorMap.isEmpty()) {
			setNotificationResponse(Boolean.FALSE, StringUtils.convertWithStream(errorMap));
		}
	}
	
	private void setNotificationResponse(Boolean isSuccess, String message) {
		if(responseModel != null) {
			if(isSuccess != null) {
				responseModel.isSentNotificationToAllSuccess = isSuccess.booleanValue();				
			}
			if(StringUtils.isNotEmpty(message)) {
				responseModel.notificationErrorMessage = message;				
			}
		}
	}
	
}
