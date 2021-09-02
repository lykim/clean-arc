package com.bit.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.constant.ApproverModule;
import com.bit.core.constant.ApproverableType;
import com.bit.core.constant.NotificatorType;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Approver;
import com.bit.core.entity.EmailNotificator;
import com.bit.core.entity.User;
import com.bit.core.entity.base.Notificator;
import com.bit.core.factory.ApproveableFactory;
import com.bit.core.factory.NotificatorFactory;
import com.bit.core.mocks.MockApproveable;
import com.bit.core.model.request.FindManyUserRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.NotificationResponse;
import com.bit.core.strategy.Approveable;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.usecase.user.CreateUserUsecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;

public class ApproveableTest extends BaseUsecaseTest{
	private static String usernameAbc = "abc";
	private static String usernameCba = "cba";
	private static int numOfRows = 20;
	private static String token;
	private static RequestModelHelper<FindManyUserRequestModel> requestModelHelper;
	private static List<String> loginRoles;
	private static List<String> viewRoles;
	private static String testModuleName = "TEST_MODULE";
	private static List<User> users;
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
			createUser(name, name + emailDomain, password);			
		}
		users = generateUsers(2);
		persistApprover(ApproverModule.TEST, generateApprovers(users), generateNotificators());
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
	
	@Test
	public void whenApproversAreSet_thenCanGetApprovers() {
		Approver approver = approverGateway.findByModule(ApproverModule.TEST);
		Approveable approveable = ApproveableFactory.get(approver);
		Set<User> approvers = approveable.getApprovers();
		List<User> approverList = new ArrayList<>(approvers);
		assertEquals(users.size(), approverList.size());
		for(User user: users) {
			assertTrue(approverList.contains(user));
		}
	}
	
	@Test
	public void whenNotificatorsAreSet_thenCanGetNotificators() {
		Approver approver = approverGateway.findByModule(ApproverModule.TEST);
		Approveable approveable = ApproveableFactory.get(approver);
		Set<Notificator> notificators = generateNotificators();
		Set<Notificator> persistedNotificators = approveable.getNotificators();
		assertEquals(notificators.size(), persistedNotificators.size());
	}
	
	@Test
	public void whenTypeIsSet_thenGetApproveableByType() {
		Approver approver = approverGateway.findByModule(ApproverModule.TEST);
		Approveable approveable = ApproveableFactory.get(approver);
		assertTrue( approveable instanceof MockApproveable );
	}
	
	
	@Test
	public void whenNotificatorsAreSet_thenNotificationCanBeSend() {
		Approver approver = approverGateway.findByModule(ApproverModule.TEST);
		Approveable approveable = ApproveableFactory.get(approver);
		Set<Notificator> persistedNotificators = approveable.getNotificators();
		for(Notificator notificator : persistedNotificators) {
			NotificationResponse resp = notificator.send(approveable.getApprovers());
			assertTrue(resp.isAllSuccess);
		}
	}
	
	private static void persistApprover(ApproverModule approverModule, Set<User> users, Set<Notificator> notificators) {
		Approver approver = new Approver(approverModule,users,notificators, ApproverableType.TEST);
		approverGateway.save(approver);
	}
	
	private static Set<Notificator> generateNotificators(){
		Set<Notificator> notificators = new HashSet<>();
		EmailNotificator emailNotificator = (EmailNotificator)NotificatorFactory.get(NotificatorType.EMAIL);
		notificators.add(emailNotificator);
		return notificators;
	}
	
	private static List<User> generateUsers(int count){
		List<User> users = new ArrayList<>();
		for(int i=0; i < count; i++) {
			User user = userGateway.findByUsername(usernameAbc + "_" +i);
			users.add(user);
		}
		return users;
	}
	
	private static Set<User> generateApprovers(List<User> users){
		Set<User> approvers = new HashSet<>();
		users.forEach(user -> approvers.add(user));
		return approvers;
	}
}
