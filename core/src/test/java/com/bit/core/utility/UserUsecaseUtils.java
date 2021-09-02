package com.bit.core.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bit.core.constant.RoleCode;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utils.TokenUtils;

public class UserUsecaseUtils {
	static List<String> loginRoles = Arrays.asList(RoleCode.ROLE_USER_VIEW, RoleCode.ROLE_USER_CUD);
	static String token = TokenUtils.createJWT( "ruly", loginRoles,  60000);
	
	public static List<String> createUsers(String prefixName, String emailDomain, String prefixPassword,  int count){
		List<String> ids = new ArrayList<>();
		for(int i=0; i < count; i++) {
			String id = createUser(prefixName+"_"+i, prefixName+"_"+i+emailDomain, prefixPassword+"_"+i);
			ids.add(id);
		}
		return ids;
	}
	
	public static String createUser(String username, String email, String password) {

//		UserRequestModel request = new UserRequestModel();
//		request.token = token;
//		request.username = username;
//		request.email = email;
//		request.password = password;
//		Usecase<?, CreateResponseModel> usecase = new CreateUserUsecase(request);
//		usecase.run();
//		CreateResponseModel response = usecase.getResponseModel();
		return createUser(username, email, password, null);
	}
	public static String createUser(String username, String email, String password, String salesmanCode) {
		UserRequestModel request = new UserRequestModel();
		request.token = token;
		request.username = username;
		request.email = email;
		request.password = password;
		request.salesmanCode = salesmanCode;
		Usecase<?, CreateResponseModel> usecase = new CreateUserUsecase(request);
		usecase.run();
		CreateResponseModel response = usecase.getResponseModel();
		return response.id;
	}
}
