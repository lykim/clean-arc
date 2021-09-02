package com.bit.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.constant.Direction;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.User;
import com.bit.core.model.request.FindManyUserRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.Page;
import com.bit.core.strategy.FindManyStrategy;
import com.bit.core.strategy.findMany.FindUsers;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utility.UserUsecaseUtils;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;

public class FindManyUsecaseTest extends BaseUsecaseTest{
	private static String usernameAbc = "abc";
	private static String usernameCba = "cba";
	private static int numOfRows = 20;
	private static String token;
	private static RequestModelHelper<FindManyUserRequestModel> requestModelHelper;
	private static List<String> loginRoles;
	private static List<String> viewRoles;
	
	@BeforeAll
	public static void beforeAll() {
		viewRoles = Arrays.asList(RoleCode.ROLE_USER_VIEW);
		loginRoles = Arrays.asList(RoleCode.ROLE_USER_VIEW, RoleCode.ROLE_USER_CUD);
		token = TokenUtils.createJWT( "ruly", loginRoles,  60000);
		requestModelHelper = new RequestModelHelper<FindManyUserRequestModel>(new FindManyUserRequestModel(), token);
		String username = usernameAbc;
		String emailDomain = "@mail.com";
		String password = "random";
		for(int i=0; i < numOfRows; i++) {
			if(i >=10) {
				username = usernameCba;
			}
			String name = username + "_" +i;
			UserUsecaseUtils.createUser(name, name + emailDomain, password);			
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void givenSortAscending_thenSortedAscending() {
		FindManyUserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.ASCENDING;
		FindManyStrategy<User, FindManyUserRequestModel> findManyStrategy = new FindUsers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase<>(requestModel, new FindManyResponseModel<User>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<User> response = (FindManyResponseModel<User>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<User> page = response.page;
		List<User> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			User user = result.get(i);
			if(i <10) {
				assertEquals(user.getUsername(), usernameAbc+"_"+i);				
			}else {
				assertEquals(user.getUsername(), usernameCba+"_"+i);
			}
		}
	}
	
	@Test
	public void givenSortDescending_thenSortedDescending() {
		FindManyUserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.DESCENDING;
		FindManyStrategy<User, FindManyUserRequestModel> findManyStrategy = new FindUsers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<User>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<User> response = (FindManyResponseModel<User>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<User> page = response.page;
		List<User> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			User user = result.get(i);
			int index = result.size() - (i+1);
			if(i < 10) {
				assertEquals(user.getUsername(), usernameCba+"_"+index);				
			}else {
				assertEquals(user.getUsername(), usernameAbc+"_"+index);
			}
		}
	}
	
	@Test
	public void givenOrderBy_thenSortBasedByOrderBy() {
		FindManyUserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.direction = Direction.DESCENDING;
		requestModel.orderBy = "username";
		FindManyStrategy<User, FindManyUserRequestModel> findManyStrategy = new FindUsers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<User>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<User> response = (FindManyResponseModel<User>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<User> page = response.page;
		List<User> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			User user = result.get(i);
			int index = result.size() - (i+1);
			if(i < 10) {
				assertEquals(user.getUsername(), usernameCba+"_"+index);				
			}else {
				assertEquals(user.getUsername(), usernameAbc+"_"+index);
			}
		}
	}
	
	@Test
	public void givenPageSize_thenResultSizeSameAsPageSize() {
		FindManyUserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		FindManyStrategy<User, FindManyUserRequestModel> findManyStrategy = new FindUsers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<User>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<User> response = (FindManyResponseModel<User>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<User> page = response.page;
		List<User> result = page.getResult();
		assertEquals(requestModel.pageSize, result.size());
	}
	
	@Test
	public void givenPageSize_thenNumberOfPagesIsCountedCorrectly() {
		FindManyUserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 3;
		FindManyStrategy<User, FindManyUserRequestModel> findManyStrategy = new FindUsers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<User>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<User> response = (FindManyResponseModel<User>)usecase.getResponseModel();
		Page<User> page = response.page;
		assertTrue(response.isSuccess);
		int numPage = (int)Math.ceil(numOfRows / requestModel.pageSize);
		assertEquals(numPage, page.getNumOfPages());
	}
	
	@Test
	public void givenPage1With5PageSizeAndSortAscending_thenWillGetCorrectRows() {
		FindManyUserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 1;
		FindManyStrategy<User, FindManyUserRequestModel> findManyStrategy = new FindUsers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<User>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<User> response = (FindManyResponseModel<User>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<User> page = response.page;
		List<User> result = page.getResult();
		for(int i=0; i > result.size(); i++) {
			User user = result.get(i);
			assertEquals(user.getUsername(), usernameAbc+"_"+i);
		}
	}
	
	@Test
	public void givenPage2With5PageSizeAndSortAscending_thenWillGetCorrectRows() {
		FindManyUserRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 2;
		FindManyStrategy<User, FindManyUserRequestModel> findManyStrategy = new FindUsers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<User>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<User> response = (FindManyResponseModel<User>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<User> page = response.page;
		List<User> result = page.getResult();
		for(int i=0; i < result.size(); i++) {
			User user = result.get(i);
			assertEquals(user.getUsername(), usernameAbc+"_"+ (i + ((requestModel.pageSize * requestModel.pageNumber)- requestModel.pageSize)));
		}
	}
	
	@Test
	public void givenUsername_thenSearchBasedByUsername() {
		FindManyUserRequestModel requestModel =requestModelHelper.getRequestModelWithToken();
		requestModel.pageSize = 5;
		requestModel.pageNumber = 1;
		requestModel.usernameLike = usernameCba;
		int indexStart = requestModel.usernameLike.equals(usernameCba) ? 10 : 0;
		FindManyStrategy<User, FindManyUserRequestModel> findManyStrategy = new FindUsers(requestModel);
		FindManyUsecase<?,?,?> usecase = new FindManyUsecase(requestModel, new FindManyResponseModel<User>(), findManyStrategy, viewRoles);
		usecase.run();
		FindManyResponseModel<User> response = (FindManyResponseModel<User>)usecase.getResponseModel();
		assertTrue(response.isSuccess);
		Page<User> page = response.page;
		List<User> result = page.getResult();
		assertEquals(2, page.getNumOfPages());
		for(int i=0; i < result.size(); i++) {
			User user = result.get(i);
			assertEquals(user.getUsername(), usernameCba+"_"+ (i + ((requestModel.pageSize * requestModel.pageNumber)- requestModel.pageSize) + indexStart));
		}
	}
}
