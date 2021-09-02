package com.bit.core.gateway;

import com.bit.core.entity.RoleGroup;

public interface RoleGroupGateway  extends CrudGateway<RoleGroup, String>, ActiveableGateway<RoleGroup, String>{
	RoleGroup findByName(String name);
}
