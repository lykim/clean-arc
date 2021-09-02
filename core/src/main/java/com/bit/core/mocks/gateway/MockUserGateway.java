package com.bit.core.mocks.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bit.core.constant.Direction;
import com.bit.core.entity.RoleGroup;
import com.bit.core.entity.User;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.gateway.UserGateway;
import com.bit.core.gateway.factory.GatewayFactory;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyUserRequestModel;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;

public class MockUserGateway implements UserGateway{
	Set<User> users = MockStorage.users;
	private static UserGateway INSTANCE;
	
	private MockUserGateway() {}
	
	public static UserGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new MockUserGateway();
		}
		return INSTANCE;
	}
	
	@Override
	public User findById(String id) {
		Optional<User> userOpt = users.stream().filter(data -> id.equals(data.getId())).findAny();
		User user = userOpt.isPresent() ? userOpt.get() : null;
		if(user != null) user.setPassword("");
		setFullRoleGroupToUser(user);
		return user;
	}
	
	private void setFullRoleGroupToUser(User entity) {
		if(entity!= null && !CollectionUtils.isEmpty(entity.getRoleGroups())) {
			Set<RoleGroup> roleGroups = entity.getRoleGroups().stream().map(roleGroup -> getRoleGroupFromRoleGroupId(roleGroup.getId())).collect(Collectors.toSet());
			entity.setRoleGroups(roleGroups);
		}
	}
	
	private RoleGroup getRoleGroupFromRoleGroupId(String id) {
		RoleGroupGateway roleGroupGateway = (RoleGroupGateway)GatewayFactory.ROLE_GROUP_GATEWAY.get();
		return roleGroupGateway.findById(id);
	}
	
	@Override
	public User findByIdShowPassword(String id) {
		Optional<User> user = users.stream().filter(data -> id.equals(data.getId())).findAny();
		return user.isPresent() ? user.get() : null;
	}

	@Override
	public void save(User entity) {
		users.add(entity);
	}

	@Override
	public User findByUsername(String username) {
		Optional<User> userOpt = users.stream().filter(data -> username.equals(data.getUsername())).findAny();
		User user = userOpt.isPresent() ? userOpt.get() : null;
		if(user != null) user.setPassword("");
		return user;
	}
	
	@Override
	public User findByUsernameShowPassword(String username) {
		Optional<User> userOpt = users.stream().filter(data -> username.equals(data.getUsername())).findAny();
		return userOpt.isPresent() ? userOpt.get() : null;
	}

	@Override
	public void clean() {
		users.clear();
	}

	@Override
	public void update(User entity, String id) {
		User user = findById(id);
		user.setUsername(entity.getUsername());
		user.setEmail(entity.getEmail());
		user.setRoleGroups(entity.getRoleGroups());
		user.setSalesmanCode(entity.getSalesmanCode());
	}

	@Override
	public void setActive(String id, boolean active) {
		User user = findById(id);
		user.setActive(active);
	}

	@Override
	public Page<User> find(FindManyRequestModel param) {
		List<User> listUsers = new ArrayList<>();
		if(param.direction == Direction.DESCENDING) {
			if(StringUtils.isNotEmpty(param.orderBy)) {
				if(param.orderBy.equals("username")) {
					listUsers = users.stream().sorted(Comparator.comparing(User::getUsername).reversed() ).collect(Collectors.toList());								
				}
			}else {
				listUsers = users.stream().sorted(Comparator.comparing(User::getUsername).reversed() ).collect(Collectors.toList());							
			}
		}else {
			if(StringUtils.isNotEmpty(param.orderBy)) {
				if(param.orderBy.equals("username")) {
					listUsers = users.stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList());			
				}
			}else {
				listUsers = users.stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList());							
			}

		}
		FindManyUserRequestModel userParam = (FindManyUserRequestModel)param;
		if(StringUtils.isNotEmpty(userParam.usernameLike)) {
			listUsers = listUsers.stream().filter(data -> data.getUsername().contains(userParam.usernameLike)).collect(Collectors.toList());
		}
		int usersSize = listUsers.size();
		List<User> results = listUsers.stream().collect(Collectors.toList());
		Collections.copy(results, listUsers);
		if(param.pageSize > 0) {
			int indexStart = ((param.pageNumber * param.pageSize) - param.pageSize) + 1;
			indexStart = indexStart - 1;
			int indexEnd = indexStart + param.pageSize;
			indexEnd = results.size() < indexEnd ? results.size() : indexEnd;
			results = listUsers.subList(indexStart, indexEnd);
		}
		Page<User> page = new WebPage<>(results, usersSize, param.pageSize);
		return page;
	}

	@Override
	public void changePassword(String id, String password) {
		Optional<User> userOpt = users.stream().filter(data -> id.equals(data.getId())).findAny();
		if(userOpt.isEmpty()) throw new RuntimeException("No User in persistent");
		User user = userOpt.get();
		user.setPassword(password);
	}
}
