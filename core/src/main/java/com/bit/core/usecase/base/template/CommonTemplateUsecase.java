package com.bit.core.usecase.base.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bit.core.constant.ErrorMessage;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.ResponseModel;
import com.bit.core.security.Authorizeable;
import com.bit.core.usecase.base.Usecase;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;
import com.bit.core.utils.TokenUtils;
import com.bit.core.validator.InitialValidator;
import com.bit.core.validator.NullValidator;
import com.bit.core.validator.base.ValidatorChain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public abstract class CommonTemplateUsecase<I extends RequestModel, O extends ResponseModel> extends Usecase<I,O>{
	
	Map<String,String> validationMessages;
	protected ValidatorChain<?> validator;
	protected Authorizeable authorize;
	
	protected abstract void setupValidation();
	protected abstract void doBussinessProcess();
	protected abstract void parseRequestModelToEntity();
	
	@Override
	public void run() {
		try {
			authorizing();
			initialize();
			setupValidation();
			validator.validate();
			if(isValidationMessagesNotEmpty()) {
				responseModel.validationMessages = validationMessages;
			} else {
				parseRequestModelToEntity();
				doBussinessProcess();
				finishing();
			}
		}catch (Exception e) {
			setErrorMessage(e);
		}finally {
			setSuccessFlagByValidationMessages();
		}
	}
	
	protected void finishing() {}
	
	private void authorizing() {
		if(authorize != null) {
			if(StringUtils.isNotEmpty(requestModel.token)) {
				Jws<Claims> jwsClaims = TokenUtils.parseClaims(requestModel.token);
				if(jwsClaims == null) throw new RuntimeException(ErrorMessage.UNAUTHORIZED);
				if(!CollectionUtils.isEmpty(authorize.getAcceptedRoles())) {
					Claims claims = jwsClaims.getBody();
					String plainRoles = claims.getAudience();
					String[] rolesArr =  plainRoles.split(",");
					List<String> roles = new ArrayList<>();
					for(String role : rolesArr) {
						roles.add(role);
					}
					boolean isRolesAccepted = authorize.getAcceptedRoles().stream().anyMatch(element -> roles.contains(element));
					if(!isRolesAccepted) throw new RuntimeException(ErrorMessage.UNAUTHORIZED);
				}
			}else {
				throw new RuntimeException(ErrorMessage.UNAUTHORIZED);
			}
		}
	}
	
	
	private void setErrorMessage(Exception e) {
		responseModel.isSuccess = false;
		responseModel.errorMessage = e.getMessage();
	}

	private void initialize() {
		responseModel.isSuccess = true;
		validationMessages = new HashMap<>();
		validator = new InitialValidator(validationMessages);
		validator.setNextValidator(new NullValidator(RequestModel.REQUEST_MODEL, requestModel));
	}
	private boolean isValidationMessagesNotEmpty() {
		return validationMessages != null && !validationMessages.isEmpty();
	}
	
	private void setSuccessFlagByValidationMessages(){
		if(responseModel.validationMessages != null) {
			if(!responseModel.validationMessages.isEmpty()) {
				responseModel.isSuccess = false;
			}
		} 
	}
}
