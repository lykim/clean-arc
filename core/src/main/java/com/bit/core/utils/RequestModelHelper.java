package com.bit.core.utils;

import com.bit.core.model.request.base.RequestModel;

public class RequestModelHelper<T extends RequestModel> {
	private T requestModel;
	
	public RequestModelHelper(T requestModel, String token) {
		this.requestModel = requestModel;
		this.requestModel.token = token;
	}
	
	public T getRequestModelWithToken() {
		T newRequest;
		try {
			newRequest = (T)requestModel.getClass().newInstance();
			newRequest.token = requestModel.token;
			return newRequest;
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
