package com.bit.core.gateway;

import com.bit.core.entity.User;

public interface UserGateway extends CrudGateway<User, String>, ActiveableGateway<User, String>{
	User findByUsername(String username);
	User findByUsernameShowPassword(String username);
	User findByIdShowPassword(String id);
	void changePassword(String id, String password);
}
