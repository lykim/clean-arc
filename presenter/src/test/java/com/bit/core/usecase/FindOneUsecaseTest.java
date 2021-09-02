package com.bit.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.strategy.FindOneStrategy;
import com.bit.core.strategy.findOne.FindUserById;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utils.TokenUtils;

public class FindOneUsecaseTest extends BaseUsecaseTest{
	private static String defaultId1;
	private static String defaultUsername1 = "user1";
	private static String defaultEmail1 = "email1@mail.com";
	private static String defaultPassword = "@Pas123";
	private static String token;
	private static List<String> loginRoles;
	private static List<String> viewRoles;
	
	@BeforeAll
	public static void beforeAll() {
		viewRoles = Arrays.asList(RoleCode.ROLE_USER_VIEW);
		loginRoles = Arrays.asList(RoleCode.ROLE_USER_VIEW, RoleCode.ROLE_USER_CUD);
		token = TokenUtils.createJWT( "ruly", loginRoles,  60000);
		defaultId1 = createUser(defaultUsername1,defaultEmail1,defaultPassword);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void findById_willReturnData() {
		UserRequestModel request = new UserRequestModel();
		request.token = token;
		request.id = defaultId1;
		FindOneStrategy<User, UserRequestModel> findOneStrategy = new FindUserById(request);
		FindOneUsecase<?,?,?> usecase = new FindOneUsecase<>(request, new DetailResponseModel<User>(), findOneStrategy, viewRoles);
		usecase.run();
		DetailResponseModel<User> response = (DetailResponseModel<User>) usecase.getResponseModel();
		assertEquals( response.entity.getEmail(), defaultEmail1);
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
