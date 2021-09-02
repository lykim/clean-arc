package com.bit.core.usecase.approver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.bit.core.constant.ApproverModule;
import com.bit.core.constant.ApproverableType;
import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.NotificatorType;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Approver;
import com.bit.core.entity.User;
import com.bit.core.entity.base.Notificator;
import com.bit.core.model.request.ApproverRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utility.UserUsecaseUtils;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.EmptyCollectionValidator;
import com.bit.core.validator.string.EmptyValidator;
@TestMethodOrder(OrderAnnotation.class)
public class CreateApproverUsecaseTest extends BaseUsecaseTest{
	private static String token;
	private static RequestModelHelper<ApproverRequestModel> requestModelHelper;
	private static List<String> approverIds;
	private static int numOfApprovers = 2;
	@BeforeAll
	public static void beforeAll() {
		List<String> roles = Arrays.asList(RoleCode.ROLE_APPROVER_CUD);
		token = TokenUtils.createJWT( "ruly", roles,  60000);
		requestModelHelper = new RequestModelHelper<ApproverRequestModel>(new ApproverRequestModel(), token);
		approverIds = UserUsecaseUtils.createUsers("abc", "@mail.com", "p@ssw0rd", numOfApprovers);
	}
	
	@Test
	@Order(1)
	public void givenBlankAllRequiredFields_willGetValidationMessage() {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		Usecase<?,?> usecase = new CreateApproverUsecase(requestModel);
		usecase.run();
		ResponseModel response = usecase.getResponseModel();
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(ApproverRequestModel.Label.type));
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(ApproverRequestModel.Label.module));
		assertEquals(EmptyCollectionValidator.VALIDATION_MESSAGE, response.validationMessages.get(ApproverRequestModel.Label.approverIds));
		assertEquals(EmptyCollectionValidator.VALIDATION_MESSAGE, response.validationMessages.get(ApproverRequestModel.Label.notificatorTypes));
	}
	
	@Test
	@Order(2)
	public void givenUnknownType_willThrowError() {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		inputRequestModel(requestModel, "ABC", ApproverModule.TEST.getAbbreviation(), approverIds, generateNotificatorTypes());
		ResponseModel response = runUsecaseAndGetResponse(requestModel);
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.CODE_IS_NOT_IN_ENUM, response.errorMessage);
	}
	
	@Test
	@Order(3)
	public void givenUnknownModule_willThrowError() {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		inputRequestModel(requestModel, ApproverableType.SIMPLE.getAbbreviation(), "BDA", approverIds, generateNotificatorTypes());
		ResponseModel response = runUsecaseAndGetResponse(requestModel);
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.CODE_IS_NOT_IN_ENUM, response.errorMessage);
	}
	
	@Test
	@Order(4)
	public void givenRequiredData_thenWillBeSaved() {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		List<String> notificationTypes = generateNotificatorTypes();
		inputRequestModel(requestModel, ApproverableType.SIMPLE.getAbbreviation(), ApproverModule.TEST.getAbbreviation(), 
				approverIds, notificationTypes);
		CreateResponseModel response = (CreateResponseModel)runUsecaseAndGetResponse(requestModel);
		assertTrue(response.isSuccess);
		Approver approver = approverGateway.findById(response.id);
		assertEquals(ApproverableType.SIMPLE, approver.getApproverabelType());
		assertEquals(ApproverModule.TEST, approver.getModule());
		for(User user : approver.getApprovers()) {
			assertTrue(approverIds.contains(user.getId()));
		}
		for(Notificator notif : approver.getNotificators()) {
			assertTrue(notificationTypes.contains(notif.getType()));
		}
	}
	
	@Test
	@Order(5)
	public void givenExistingModulet_thenWillThrowError() {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		List<String> notificationTypes = generateNotificatorTypes();
		inputRequestModel(requestModel, ApproverableType.SIMPLE.getAbbreviation(), ApproverModule.TEST.getAbbreviation(), 
				approverIds, notificationTypes);
		CreateResponseModel response = (CreateResponseModel)runUsecaseAndGetResponse(requestModel);
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.NAME_ALREADY_TAKEN, response.errorMessage);
	}
	
	private ResponseModel runUsecaseAndGetResponse(ApproverRequestModel requestModel) {
		Usecase<?,?> usecase = new CreateApproverUsecase(requestModel);
		usecase.run();
		return usecase.getResponseModel();
	}
	
	private void inputRequestModel(ApproverRequestModel request, String type, String module, List<String> approverIds, List<String> notifcatorTypes) {
		request.type = type;
		request.module = module;
		request.approverIds = approverIds;
		request.notificatorTypes = notifcatorTypes;
	}
	
	
	private List<String> generateNotificatorTypes(){
		List<String> notificators = new ArrayList<>();
		notificators.add(NotificatorType.EMAIL.getAbbreviation());
		return notificators;
	}
}
