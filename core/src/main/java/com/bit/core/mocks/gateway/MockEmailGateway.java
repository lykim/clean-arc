package com.bit.core.mocks.gateway;

import java.util.Set;

import com.bit.core.entity.Role;
import com.bit.core.gateway.EmailGateway;
import com.bit.core.model.request.EmailRequest;
import com.bit.core.response.base.ResponseModel;

public class MockEmailGateway implements EmailGateway{
	private Set<Role> roles = MockStorage.roles;
	private static EmailGateway INSTANCE;
	
	private MockEmailGateway() {}
	
	public static EmailGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new MockEmailGateway();
		}
		return INSTANCE;
	}
	
	@Override
	public ResponseModel send(EmailRequest request) {
		ResponseModel response = new ResponseModel();
		response.isSuccess = true;
		return response;
	}

}
