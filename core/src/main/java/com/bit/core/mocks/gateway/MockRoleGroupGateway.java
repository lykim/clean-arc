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
import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyRoleGroupRequestModel;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;

public class MockRoleGroupGateway implements RoleGroupGateway{
	
	private Set<RoleGroup> roleGroups = MockStorage.roleGroups;
	private static RoleGroupGateway INSTANCE;
	
	private MockRoleGroupGateway() {}
	
	public static RoleGroupGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new MockRoleGroupGateway();
		}
		return INSTANCE;
	}
	@Override
	public RoleGroup findById(String id) {
		Optional<RoleGroup> optional = roleGroups.stream().filter(data -> id.equals(data.getId())).findAny();
		RoleGroup entity = optional.isPresent() ? optional.get() : null;
		setFullRoleToRoleGroup(entity);
		return entity;
	}
	
	private void setFullRoleToRoleGroup(RoleGroup entity) {
		if(entity!= null && !CollectionUtils.isEmpty(entity.getRoles())) {
			Set<Role> roles = entity.getRoles().stream().map(role -> getRoleFromRoleId(role.getId())).collect(Collectors.toSet());
			entity.setRoles(roles);
		}
	}
	
	private Role getRoleFromRoleId(String id) {
		RoleGateway roleGateway = (RoleGateway)GatewayFactory.ROLE_GATEWAY.get();
		return roleGateway.findById(id);
	}

	@Override
	public void save(RoleGroup entity) {
		roleGroups.add(entity);
	}

	@Override
	public void update(RoleGroup entity, String id) {
		RoleGroup roleGroup = findById(id);
		roleGroup.setName(entity.getName());
		roleGroup.setDescription(entity.getDescription());
	}

	@Override
	public Page<RoleGroup> find(FindManyRequestModel param) {
		List<RoleGroup> listRoleGroups = new ArrayList<>();
		if(param.direction == Direction.DESCENDING) {
			if(StringUtils.isNotEmpty(param.orderBy)) {
				if(param.orderBy.equals("name")) {
					listRoleGroups = roleGroups.stream().sorted(Comparator.comparing(RoleGroup::getName).reversed() ).collect(Collectors.toList());								
				}
			}else {
				listRoleGroups = roleGroups.stream().sorted(Comparator.comparing(RoleGroup::getName).reversed() ).collect(Collectors.toList());							
			}
		}else {
			if(StringUtils.isNotEmpty(param.orderBy)) {
				if(param.orderBy.equals("name")) {
					listRoleGroups = roleGroups.stream().sorted(Comparator.comparing(RoleGroup::getName)).collect(Collectors.toList());			
				}
			}else {
				listRoleGroups = roleGroups.stream().sorted(Comparator.comparing(RoleGroup::getName)).collect(Collectors.toList());							
			}

		}
		FindManyRoleGroupRequestModel roleGroupParam = (FindManyRoleGroupRequestModel)param;
		if(StringUtils.isNotEmpty(roleGroupParam.nameLike)) {
			listRoleGroups = listRoleGroups.stream().filter(data -> data.getName().contains(roleGroupParam.nameLike)).collect(Collectors.toList());
		}
		int roleGroupsSize = listRoleGroups.size();
		List<RoleGroup> results = listRoleGroups.stream().collect(Collectors.toList());
		Collections.copy(results, listRoleGroups);
		if(param.pageSize > 0) {
			int indexStart = ((param.pageNumber * param.pageSize) - param.pageSize) + 1;
			indexStart = indexStart - 1;
			int indexEnd = indexStart + param.pageSize;
			indexEnd = results.size() < indexEnd ? results.size() : indexEnd;
			results = listRoleGroups.subList(indexStart, indexEnd);
		}
		Page<RoleGroup> page = new WebPage<>(results, roleGroupsSize, param.pageSize);
		return page;
	}

	@Override
	public void clean() {
		roleGroups.clear();
	}

	@Override
	public void setActive(String id, boolean active) {
		RoleGroup roleGroup = findById(id);
		roleGroup.setActive(active);
	}

	@Override
	public RoleGroup findByName(String name) {
		Optional<RoleGroup> optional = roleGroups.stream().filter(data -> name.equals(data.getName())).findAny();
		return optional.isPresent() ? optional.get() : null;
	}
}