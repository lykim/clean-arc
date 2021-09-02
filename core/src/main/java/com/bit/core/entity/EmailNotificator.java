package com.bit.core.entity;

import java.util.Set;

import com.bit.core.constant.NotificatorType;
import com.bit.core.entity.base.Notificator;
import com.bit.core.gateway.EmailGateway;
import com.bit.core.gateway.factory.NotificationGatewayFactory;
import com.bit.core.model.request.EmailRequest;
import com.bit.core.response.base.NotificationResponse;
import com.bit.core.response.base.ResponseModel;

public class EmailNotificator extends Notificator{
	EmailGateway emailGateway;
	
	public EmailNotificator() {
		emailGateway = (EmailGateway )NotificationGatewayFactory.EMAIL_NOTIFICATION.get();
		type = NotificatorType.EMAIL.getAbbreviation();
	}
	
	@Override
	public NotificationResponse send(Set<User> receivers) {
		NotificationResponse notifResp = new NotificationResponse();
		notifResp.isAllSuccess = true;
		receivers.forEach(receiver -> {
			EmailRequest request = new EmailRequest();
			request.to = receiver.getEmail();
			request.subject = "Salesman Target Kpi, need approval";
			request.content = "please go to below link to approve";
			request.from = "no-reply@bhakti.co.id";
			ResponseModel response = emailGateway.send(request);
			if(!response.isSuccess) {
				notifResp.isAllSuccess = false;
				notifResp.errorMessages.put(receiver.getEmail(), response.errorMessage);
			}
		});
		return notifResp;
	}

}
