package com.bit.plugins;

import com.bit.core.gateway.EmailGateway;
import com.bit.core.model.request.EmailRequest;
import com.bit.core.response.base.ResponseModel;

public class EmailGatewayImpl implements EmailGateway{
	private static EmailGateway INSTANCE;
	private EmailGatewayImpl() {};
	
	public static EmailGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new EmailGatewayImpl();
		}
		return INSTANCE;
	}
	@Override
	public ResponseModel send(EmailRequest emailRequest) {
		ResponseModel emailResponse = new ResponseModel();
		try {
			new EmailSender().isAuth(true).isEnableStartTLS(true).withHost("mail.bhakti.co.id")
			.withPort(587).withTrustSSL("mail.bhakti.co.id").withUsername("no-reply@bhakti.co.id").withPassword("UtkTdkDibalas123")
			.from(emailRequest.from).to(emailRequest.to).withSubject(emailRequest.subject)
			.withContent( emailRequest.content )
			.send();
			emailResponse.isSuccess = true;
		}catch (Exception e) {
			emailResponse.isSuccess = false;
			emailResponse.errorMessage = e.getMessage();
		}
		return emailResponse;
	}
}
