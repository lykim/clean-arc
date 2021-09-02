package com.bit.core.gateway;

import com.bit.core.entity.Role;

public interface RoleGateway extends CrudGateway<Role, String>, ActiveableGateway<Role, String>{
	Role findByCode(String code);
}
