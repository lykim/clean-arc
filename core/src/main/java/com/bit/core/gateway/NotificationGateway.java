package com.bit.core.gateway;

import com.bit.core.model.request.base.NotificationRequest;
import com.bit.core.response.base.ResponseModel;

public interface NotificationGateway<R extends NotificationRequest> {
	ResponseModel send(R r);
}
