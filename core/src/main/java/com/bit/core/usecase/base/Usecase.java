package com.bit.core.usecase.base;

import com.bit.core.model.request.base.RequestModel;
import com.bit.core.response.base.ResponseModel;

public abstract class Usecase<I extends RequestModel, O extends ResponseModel> {
	protected O responseModel;
	protected I requestModel;
	
	public abstract void run();
	public O getResponseModel() {
		return responseModel;
	}
}
