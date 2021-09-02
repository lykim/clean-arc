package com.bit.presenter.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bit.core.model.request.LoginRequestModel;
import com.bit.core.presenter.LoginPresenter;
import com.bit.core.presenter.LoginPresenterImpl;
import com.bit.core.response.LoginResponseModel;

@RestController
@RequestMapping("login")
public class LoginController {
	LoginPresenter presenter = new LoginPresenterImpl();
	
	@PostMapping
	public LoginResponseModel loginUsernamePassword(@RequestBody LoginRequestModel requestModel) {
		return presenter.usernamePasswordLogin(requestModel);
	}
}
