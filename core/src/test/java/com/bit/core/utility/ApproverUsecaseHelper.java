package com.bit.core.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bit.core.constant.ApproverModule;
import com.bit.core.constant.ApproverableType;
import com.bit.core.constant.NotificatorType;
import com.bit.core.constant.RoleCode;
import com.bit.core.model.request.ApproverRequestModel;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.usecase.approver.CreateApproverUsecase;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.RequestModelHelper;
import com.bit.core.utils.TokenUtils;

public class ApproverUsecaseHelper {
	private static List<String> roles = Arrays.asList(RoleCode.ROLE_APPROVER_CUD);
	private static String token = TokenUtils.createJWT( "ruly", roles,  60000);
	
	private RequestModelHelper<ApproverRequestModel> requestModelHelper;
	
	public ApproverUsecaseHelper() {
		requestModelHelper = new RequestModelHelper<ApproverRequestModel>(new ApproverRequestModel(), token);
	}
	public String createApprover(int numOfApprovers) {
		List<String> approverIds = UserUsecaseUtils.createUsers("abcUnique", "@mail.com", "p@ssw0rd", numOfApprovers);
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		List<String> notificationTypes = generateNotificatorTypes();
		inputRequestModel(requestModel, ApproverableType.SIMPLE.getAbbreviation(), ApproverModule.TEST.getAbbreviation(), 
				approverIds, notificationTypes);
		CreateResponseModel response = (CreateResponseModel)runCreateUsecaseAndGetResponse(requestModel);
		return response.id;
	}
	
	public String createApprover(ApproverableType approveAbleType,  ApproverModule  approverModule, List<String> approverIds) {
		ApproverRequestModel requestModel = requestModelHelper.getRequestModelWithToken();
		List<String> notificationTypes = generateNotificatorTypes();
		inputRequestModel(requestModel, approveAbleType.getAbbreviation(), approverModule.getAbbreviation(), 
				approverIds, notificationTypes);
		CreateResponseModel response = (CreateResponseModel)runCreateUsecaseAndGetResponse(requestModel);
		return response.id;
	}
	
	private ResponseModel runCreateUsecaseAndGetResponse(ApproverRequestModel requestModel) {
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
