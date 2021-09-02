package com.bit.presenter.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bit.core.entity.Approver;
import com.bit.core.model.request.ApproverRequestModel;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.presenter.ApproverPresenter;
import com.bit.core.presenter.ApproverPresenterImpl;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

@RestController
@RequestMapping("approvers")
public class ApproverController {
	ApproverPresenter presenter;
	
	public ApproverController() {
		presenter = new ApproverPresenterImpl();
	}
	
	@PostMapping
	public CreateResponseModel create(@RequestHeader HttpHeaders requestHeader, @RequestBody ApproverRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.create(requestModel);
	}
	
	@PostMapping("many")
	public FindManyResponseModel<Approver> findMany(@RequestHeader HttpHeaders requestHeader, @RequestBody FindManyRequestModel requestModel){
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.findAll(requestModel);
	}
	
	@PatchMapping
	public ResponseModel update(@RequestHeader HttpHeaders requestHeader, @RequestBody ApproverRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.update(requestModel);
	}
	
	@GetMapping("{id}")
	public DetailResponseModel<Approver> get(@RequestHeader HttpHeaders requestHeader, @PathVariable String id){
		ApproverRequestModel requestModel = new ApproverRequestModel();
		requestModel.token = getTokenFromHeader(requestHeader);
		requestModel.id = id;
		return presenter.findById(requestModel);
	}
	
	private String getTokenFromHeader(HttpHeaders requestHeader) {
		List<String> authorizations = requestHeader.get(HttpHeaders.AUTHORIZATION);
		String bearerToken = authorizations.get(0);
		return bearerToken.substring(6).trim();
	}
}
