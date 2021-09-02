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

import com.bit.core.entity.Role;
import com.bit.core.model.request.ActiveInactiveRequestModel;
import com.bit.core.model.request.FindManyRoleRequestModel;
import com.bit.core.model.request.RoleRequestModel;
import com.bit.core.presenter.RolePresenter;
import com.bit.core.presenter.RolePresenterImpl;
import com.bit.core.response.CreateResponseModel;
import com.bit.core.response.DetailResponseModel;
import com.bit.core.response.FindManyResponseModel;
import com.bit.core.response.base.ResponseModel;

@RestController
@RequestMapping("roles")
public class RoleController {

	RolePresenter presenter;
	
	public RoleController() {
		presenter = new RolePresenterImpl();
	}
	
	@PostMapping
	public CreateResponseModel create(@RequestHeader HttpHeaders requestHeader, @RequestBody RoleRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.create(requestModel);
	}
	
	@PostMapping("many")
	public FindManyResponseModel<Role> findMany(@RequestHeader HttpHeaders requestHeader, @RequestBody FindManyRoleRequestModel requestModel){
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.findAll(requestModel);
	}
	
	@PatchMapping
	public ResponseModel update(@RequestHeader HttpHeaders requestHeader, @RequestBody RoleRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.update(requestModel);
	}
	
	@GetMapping("{id}")
	public DetailResponseModel<Role> get(@RequestHeader HttpHeaders requestHeader, @PathVariable String id){
		RoleRequestModel requestModel = new RoleRequestModel();
		requestModel.token = getTokenFromHeader(requestHeader);
		requestModel.id = id;
		return presenter.findById(requestModel);
	}
	
	@PatchMapping("activate")
	public ResponseModel activate(@RequestHeader HttpHeaders requestHeader, @RequestBody ActiveInactiveRequestModel requestModel) {
		requestModel.token = getTokenFromHeader(requestHeader);
		return presenter.setActive(requestModel);
	}
	
	private String getTokenFromHeader(HttpHeaders requestHeader) {
		List<String> authorizations = requestHeader.get(HttpHeaders.AUTHORIZATION);
		String bearerToken = authorizations.get(0);
		return bearerToken.substring(6).trim();
	}
}
