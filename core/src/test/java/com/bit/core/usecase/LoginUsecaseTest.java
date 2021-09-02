package com.bit.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.config.GatewayConfig;
import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockLoginGateway;
import com.bit.core.mocks.gateway.MockUserGateway;
import com.bit.core.model.request.LoginRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.LoginResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.string.EmptyValidator;

public class LoginUsecaseTest extends BaseUsecaseTest{
	private static String defaultId1;
	private static String defaultUsername1 = "user1";
	private static String defaultEmail1 = "email1@mail.com";
	private static String defaultPassword = "@Pas123";
	private static String token;
	
	@BeforeAll
	public static void beforeAll() {
		List<String> roles = Arrays.asList(RoleCode.ROLE_USER_CUD);
		token = TokenUtils.createJWT( "ruly", roles,  60000);
		defaultId1 = createUser(defaultUsername1,defaultEmail1,defaultPassword);
	}
	
	@Test
	public void givenEmptyRequiredFields_ThenReturnValidationMessages() {
		LoginRequestModel request = new LoginRequestModel("","");
		Usecase<?,LoginResponseModel> usecase = new LoginUsecase(request);
		usecase.run();
		LoginResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(LoginRequestModel.Label.USERNAME));
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(LoginRequestModel.Label.PASSWORD));
	}
	
	@Test
	public void givenFalseUsername_thenReturnErrorMessage() {
		LoginRequestModel request = new LoginRequestModel("falseuser",defaultPassword);
		Usecase<?,?> usecase = new LoginUsecase(request);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.USERNAME_PASSWORD_INVALID, response.errorMessage);
	}
	
	@Test
	public void givenFalsePassword_thenReturnErrorMessage() {
		LoginRequestModel request = new LoginRequestModel(defaultUsername1,"wrongpass");
		Usecase<?,?> usecase = new LoginUsecase(request);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.USERNAME_PASSWORD_INVALID, response.errorMessage);
	}
	
	@Test
	public void givenTrueUsernameAndPassword_thenGetToken() {
		LoginRequestModel request = new LoginRequestModel(defaultUsername1, defaultPassword);
		Usecase<?,LoginResponseModel> usecase = new LoginUsecase(request);
		usecase.run();
		LoginResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		assertNotNull(response.accessToken);
	}
	
	
	private static String createUser(String username, String email, String password) {
		UserRequestModel request = new UserRequestModel();
		request.token = token;
		request.username = username;
		request.email = email;
		request.password = password;
		Usecase<?, CreateResponseModel> usecase = new CreateUserUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
	
}
