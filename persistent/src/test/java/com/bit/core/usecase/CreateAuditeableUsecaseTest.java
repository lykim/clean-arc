package com.bit.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.config.GatewayConfig;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.User;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.mocks.gateway.MockUserGateway;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.model.request.base.Authenticateable;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utils.TokenUtils;

public class CreateAuditeableUsecaseTest extends BaseUsecaseTest{
	private String USERNAME_1 = "user1";
	private String PASSWORD_1 = "user1Password";
	private String VALID_EMAIL_1 = "user1@email.com";
	private static String token;
	
	@BeforeAll
	public static void beforeAll() {
		List<String> roles = Arrays.asList(RoleCode.ROLE_USER_CUD);
		token = TokenUtils.createJWT( "ruly", roles,  60000);
	}
	
	
	@Test
	public void givenAllRequiredFields_thenAuditableIsStored() {
		UserRequestModel requestModel = new UserRequestModel();
		requestModel.token = token;
		requestModel.username = USERNAME_1;
		requestModel.email = VALID_EMAIL_1;
		requestModel.password = PASSWORD_1;
		Authenticateable authenticateable = new Authenticateable();
		authenticateable.requesterId = "randomId";
		requestModel.authentication = authenticateable;
		Usecase<?, CreateResponseModel> usecase = new CreateUserUsecase(requestModel);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		User user = userGateway.findById(response.id);
		assertEquals(requestModel.authentication.requesterId, user.getCreatedBy());
		assertNotNull(user.getCreatedTime());
	}
	
}
