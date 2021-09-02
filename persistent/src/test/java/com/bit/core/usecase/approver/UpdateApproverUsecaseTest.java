package com.bit.core.usecase.approver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bit.core.constant.ApproverableType;
import com.bit.core.constant.ErrorMessage;
import com.bit.core.constant.RoleCode;
import com.bit.core.entity.Approver;
import com.bit.core.entity.User;
import com.bit.core.model.request.ApproverRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.BaseUsecaseTest;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utility.ApproverUsecaseHelper;
import com.bit.core.utility.UserUsecaseUtils;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.string.EmptyValidator;

public class UpdateApproverUsecaseTest extends BaseUsecaseTest {
	private static String token;
	private static RequestModelHelper<ApproverRequestModel> requestModelHelper;
	private static List<String> approverIds;
	private static int numOfApprovers = 2;
	private static String approvalId1;
	private static ApproverUsecaseHelper approvalHelper;
	@BeforeAll
	public static void beforeAll() {
		List<String> roles = Arrays.asList(RoleCode.ROLE_APPROVER_CUD);
		token = TokenUtils.createJWT( "ruly", roles,  60000);
		requestModelHelper = new RequestModelHelper<ApproverRequestModel>(new ApproverRequestModel(), token);
		approverIds = UserUsecaseUtils.createUsers("abc", "@mail.com", "p@ssw0rd", numOfApprovers);
		approvalHelper = new ApproverUsecaseHelper();
		approvalId1 = approvalHelper.createApprover(2);
	}
	
	@Test
	public void givenBlankId_willGetValidationMessage() {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		ResponseModel response = runUsecaseAndGetResponse(requestModel);
		assertFalse(response.isSuccess);
		assertEquals(EmptyValidator.VALIDATION_MESSAGE, response.validationMessages.get(RequestModel.ID_LABEL));
	}
	
	@Test
	public void givenUnstoredId_willThrowError() {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = UUID.randomUUID().toString();
		requestModel.type = ApproverableType.SIMPLE.getAbbreviation();
		ResponseModel response = runUsecaseAndGetResponse(requestModel);
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.ID_IS_NOT_EXIST, response.errorMessage);
	}
	
	@Test
	public void givenInvalidType_willThrowError() {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = approvalId1;
		requestModel.type = "lalala";
		ResponseModel response = runUsecaseAndGetResponse(requestModel);
		assertFalse(response.isSuccess);
		assertEquals(ErrorMessage.CODE_IS_NOT_IN_ENUM, response.errorMessage);
	}
	
	@Test
	public void givenType_thenTypeIsSaved() {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = approvalId1;
		requestModel.type = ApproverableType.TEST.getAbbreviation();
		ResponseModel response = runUsecaseAndGetResponse(requestModel);
		assertTrue(response.isSuccess);
		Approver approver = approverGateway.findById(approvalId1);
		assertEquals(ApproverableType.TEST, approver.getApproverabelType());
	}
	
	@Test
	public void givenUserToApprove_thenDataIsSaved() {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		requestModel.id = approvalId1;
		List<String> newApprovers = UserUsecaseUtils.createUsers("bca", "@xmail.com", "p@ssw0rd", 3);
		requestModel.approverIds = newApprovers; 
		ResponseModel response = runUsecaseAndGetResponse(requestModel);
		assertTrue(response.isSuccess);
		Approver approver = approverGateway.findById(approvalId1);
		assertEquals(newApprovers.size(), approver.getApprovers().size() );
		for(User user : approver.getApprovers()) {
			assertTrue(newApprovers.contains(user.getId()));
		}
	}
	
	private ResponseModel runUsecaseAndGetResponse(ApproverRequestModel requestModel) {
		Usecase<?,?> usecase = new UpdateApproverUsecase(requestModel);
		usecase.run();
		return usecase.getResponseModel();
	}
	
}
