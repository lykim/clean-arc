package com.bit.core.gateway;

import com.bit.core.model.request.EmailRequest;
import com.bit.core.response.base.ResponseModel;

public interface EmailGateway extends NotificationGateway<EmailRequest>{
	ResponseModel send(EmailRequest request);
}
