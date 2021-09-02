package com.bit.core.mocks.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bit.core.constant.Direction;
import com.bit.core.entity.Role;
import com.bit.core.entity.Role;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyRoleRequestModel;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.utils.StringUtils;

public class MockRoleGateway implements RoleGateway{
	
	private Set<Role> roles = MockStorage.roles;
	private static RoleGateway INSTANCE;
	
	private MockRoleGateway() {}
	
	public static RoleGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new MockRoleGateway();
		}
		return INSTANCE;
	}
	@Override
	public Role findById(String id) {
		Optional<Role> optional = roles.stream().filter(data -> id.equals(data.getId())).findAny();
		Role entity = optional.isPresent() ? optional.get() : null;
		return entity;
	}

	@Override
	public void save(Role entity) {
		roles.add(entity);
	}

	@Override
	public void update(Role entity, String id) {
		Role role = findById(id);
		role.setCode(entity.getCode());
		role.setDescription(entity.getDescription());
	}

	@Override
	public Page<Role> find(FindManyRequestModel param) {
		List<Role> listRoles = new ArrayList<>();
		if(param.direction == Direction.DESCENDING) {
			if(StringUtils.isNotEmpty(param.orderBy)) {
				if(param.orderBy.equals("code")) {
					listRoles = roles.stream().sorted(Comparator.comparing(Role::getCode).reversed() ).collect(Collectors.toList());								
				}
			}else {
				listRoles = roles.stream().sorted(Comparator.comparing(Role::getCode).reversed() ).collect(Collectors.toList());							
			}
		}else {
			if(StringUtils.isNotEmpty(param.orderBy)) {
				if(param.orderBy.equals("code")) {
					listRoles = roles.stream().sorted(Comparator.comparing(Role::getCode)).collect(Collectors.toList());			
				}
			}else {
				listRoles = roles.stream().sorted(Comparator.comparing(Role::getCode)).collect(Collectors.toList());							
			}

		}
		FindManyRoleRequestModel roleParam = (FindManyRoleRequestModel)param;
		if(StringUtils.isNotEmpty(roleParam.codeLike)) {
			listRoles = listRoles.stream().filter(data -> data.getCode().contains(roleParam.codeLike)).collect(Collectors.toList());
		}
		int rolesSize = listRoles.size();
		List<Role> results = listRoles.stream().collect(Collectors.toList());
		Collections.copy(results, listRoles);
		if(param.pageSize > 0) {
			int indexStart = ((param.pageNumber * param.pageSize) - param.pageSize) + 1;
			indexStart = indexStart - 1;
			int indexEnd = indexStart + param.pageSize;
			indexEnd = results.size() < indexEnd ? results.size() : indexEnd;
			results = listRoles.subList(indexStart, indexEnd);
		}
		Page<Role> page = new WebPage<>(results, rolesSize, param.pageSize);
		return page;
	}

	@Override
	public void clean() {
		roles.clear();
	}

	@Override
	public void setActive(String id, boolean active) {
		Role role = findById(id);
		role.setActive(active);
	}

	@Override
	public Role findByCode(String code) {
		Optional<Role> optional = roles.stream().filter(data -> code.equals(data.getCode())).findAny();
		return optional.isPresent() ? optional.get() : null;
	}

}
