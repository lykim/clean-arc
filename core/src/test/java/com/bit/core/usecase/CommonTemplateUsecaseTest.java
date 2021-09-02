package com.bit.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.mocks.usecase.MockCommonTemplateUsecase;
import com.bit.core.mocks.usecase.MockRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.NullValidator;

public class CommonTemplateUsecaseTest {
	@Test
	public void givenNullRequestModel_willGetValidationMessage() {
		Usecase<?,?> usecase = new MockCommonTemplateUsecase(null, null);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(NullValidator.VALIDATION_MESSAGE, response.validationMessages.get(RequestModel.REQUEST_MODEL));
	}
	
	@Test
	public void givenEmptyToken_cannotAccesAuthorizeUsecase() {
		Authorizeable authorize = new Authorizeable(null);
		MockRequestModel requestModel = new MockRequestModel();
		Usecase<?,?> usecase = new MockCommonTemplateUsecase(requestModel, authorize);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.UNAUTHORIZED , response.errorMessage);
	}
	
	@Test
	public void givenUnverifiedToken_cannotAccesAuthorizeUsecase() {
		Authorizeable authorize = new Authorizeable(null);
		MockRequestModel requestModel = new MockRequestModel();
		requestModel.token = "unauthorized";
		Usecase<?,?> usecase = new MockCommonTemplateUsecase(requestModel, authorize);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.UNAUTHORIZED , response.errorMessage);
	}
	
	@Test
	public void givenVerifiedToken_canAccessAuthorizeUsecase() {
		String token = TokenUtils.createJWT( "ruly", null,  5000);
		Authorizeable authorize = new Authorizeable(null);
		MockRequestModel requestModel = new MockRequestModel();
		requestModel.token = token;
		Usecase<?,?> usecase = new MockCommonTemplateUsecase(requestModel, authorize);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
	}
	
	@Test
	public void givenVerifiedTokenWithWrongRoles_cannotAccessAuthorizeUsecase() {
		List<String> acceptedRoles = Arrays.asList("ROLE_USER_CREATE", "ROLE_USER_ADD");
		List<String> notAcceptedRoles = Arrays.asList("ROLE_USER_READ");
		String token = TokenUtils.createJWT( "ruly", notAcceptedRoles,  5000);
		Authorizeable authorize = new Authorizeable(acceptedRoles);
		MockRequestModel requestModel = new MockRequestModel();
		requestModel.token = token;
		Usecase<?,?> usecase = new MockCommonTemplateUsecase(requestModel, authorize);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.UNAUTHORIZED , response.errorMessage);
	}
	
	@Test
	public void givenVerifiedTokenWithAcceptedRoles_canAccessAuthorizeUsecase() {
		List<String> acceptedRoles = Arrays.asList("ROLE_USER_CREATE", "ROLE_USER_ADD");
		String token = TokenUtils.createJWT( "ruly", acceptedRoles,  5000);
		Authorizeable authorize = new Authorizeable(acceptedRoles);
		MockRequestModel requestModel = new MockRequestModel();
		requestModel.token = token;
		Usecase<?,?> usecase = new MockCommonTemplateUsecase(requestModel, authorize);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
	}
	
	@Test
	public void accessingAuthorizeUsecase_whenTimeout_thenCannotAccessUsecase() throws InterruptedException {
		List<String> acceptedRoles = Arrays.asList("ROLE_USER_CREATE", "ROLE_USER_ADD");
		String token = TokenUtils.createJWT( "ruly", acceptedRoles,  100);
		Authorizeable authorize = new Authorizeable(acceptedRoles);
		MockRequestModel requestModel = new MockRequestModel();
		requestModel.token = token;
		Usecase<?,?> usecase = new MockCommonTemplateUsecase(requestModel, authorize);
		Thread.sleep(200);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.UNAUTHORIZED , response.errorMessage);
	}

}