package com.bit.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.User;
import com.bit.core.model.request.ChangePasswordRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.ChangePasswordUsecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utils.PasswordUtils;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.NotEqualValidator;
import com.bit.core.validator.string.EmptyValidator;

public class ChangePasswordUsecaseTest extends BaseUsecaseTest{
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
	public void givenEmptyRequiredFields_ThenReturnValidationMessages() {
		ChangePasswordRequestModel request = new ChangePasswordRequestModel("","","");
		Usecase<?,?> usecase = new ChangePasswordUsecase(request);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(ChangePasswordRequestModel.ID_LABEL));
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(ChangePasswordRequestModel.Label.PASSWORD));
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(ChangePasswordRequestModel.Label.NEW_PASSWORD));
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(ChangePasswordRequestModel.Label.VERIFY_NEW_PASSWORD));
	}
	
	@Test
	public void givenUnMatchVerifyPassword_ThenReturnValidationMessages() {
		ChangePasswordRequestModel request = new ChangePasswordRequestModel(defaultPassword,"new@1234","notsame@123");
		Usecase<?,?> usecase = new ChangePasswordUsecase(request);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(NotEqualValidator.VALIDATION_MESSAGE, response.validationMessages.get(ChangePasswordRequestModel.Label.VERIFY_NEW_PASSWORD));
	}
	
	@Test
	public void givenWrongPassword_ThenReturnValidationMessages() {
		ChangePasswordRequestModel request = new ChangePasswordRequestModel("wrong@123","new@1234","new@1234");
		request.id = defaultId1;
		Usecase<?,?> usecase = new ChangePasswordUsecase(request);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.WRONG_PASSWORD, response.errorMessage);
	}
	
	@Test
	public void givenCorrectData_ThenPasswordIsChanged() {
		ChangePasswordRequestModel request = new ChangePasswordRequestModel(defaultPassword,"new@1234","new@1234");
		request.id = defaultId1;
		Usecase<?,?> usecase = new ChangePasswordUsecase(request);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertTrue(response.isSuccess);
		User user = userGateway.findByIdShowPassword(defaultId1);
		assertTrue(PasswordUtils.isVerified("new@1234", user.getPassword()));
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
