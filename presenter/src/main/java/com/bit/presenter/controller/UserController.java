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

import com.bit.core.entity.User;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.ChangePasswordRequestModel;
import com.bit.core.model.request.FindManyUserRequestModel;
import com.bit.core.model.request.UserRequestModel;
import com.bit.core.model.request.base.RequestModel;
import com.bit.core.presenter.UserPresenter;
import com.bit.core.presenter.UserPresenterImpl;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

@RestController
@RequestMapping("users")
public class UserController {
	
	UserPresenter userPresenter;
	
	public UserController() {
		userPresenter = new UserPresenterImpl();
	}
	
	@PostMapping
	public CreateResponseModel create(@RequestHeader HttpHeaders requestHeader, @RequestBody UserRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return userPresenter.create(requestModel);
	}
	
	@PostMapping("many")
	public FindManyResponseModel<User> findMany(@RequestHeader HttpHeaders requestHeader, @RequestBody FindManyUserRequestModel requestModel){
		requestModel.token = getTokenFromHeader(requestHeader);
		return userPresenter.findAll(requestModel);
	}
	
	@PatchMapping
	public ResponseModel update(@RequestHeader HttpHeaders requestHeader, @RequestBody UserRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return userPresenter.update(requestModel);
	}
	
	@PatchMapping("changePassword")
	public ResponseModel changePassword(@RequestHeader HttpHeaders requestHeader, @RequestBody ChangePasswordRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return userPresenter.changePassword(requestModel);
	}
	
	@GetMapping("{id}")
	public DetailResponseModel<User> get(@RequestHeader HttpHeaders requestHeader, @PathVariable String id){
		UserRequestModel requestModel = new UserRequestModel();
		requestModel.token = getTokenFromHeader(requestHeader);
		requestModel.id = id;
		return userPresenter.findById(requestModel);
	}
	
	@GetMapping("token")
	public DetailResponseModel<User> getByToken(@RequestHeader HttpHeaders requestHeader){
		return userPresenter.findByToken(getTokenFromHeader(requestHeader));
	}
	
	@PatchMapping("activate")
	public ResponseModel activate(@RequestHeader HttpHeaders requestHeader, @RequestBody ActiveInactiveRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return userPresenter.setActive(requestModel);
	}
	
	private String getTokenFromHeader(HttpHeaders requestHeader) {
		List<String> authorizations = requestHeader.get(HttpHeaders.AUTHORIZATION);
		String bearerToken = authorizations.get(0);
		return bearerToken.substring(6).trim();
	}
}
