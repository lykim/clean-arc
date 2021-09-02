package com.bit.core.mocks.gateway;

import java.util.HashSet;
import java.util.Set;

import com.bit.core.entity.Approver;
import com.bit.core.entity.Role;
import com.bit.core.entity.RoleGroup;
import com.bit.core.entity.Salesman;
import com.bit.core.entity.UnitKpi;
import com.bit.core.entity.User;

public class MockStorage {
	public static Set<User> users;
	public static Set<Role> roles;
	public static Set<RoleGroup> roleGroups;
	public static Set<UnitKpi> unitKpis;
	public static Set<Salesman> salesmans;
	public static Set<Approver> approvers;
	static {
		users = new HashSet<>();
		roles = new HashSet<>();
		roleGroups = new HashSet<>();
		unitKpis = new HashSet<>();
		salesmans = new HashSet<>();
		approvers = new HashSet<>();
	}
	
	public static void cleanAll() {
		users = new HashSet<>();
		roles = new HashSet<>();
		roleGroups = new HashSet<>();
	}
}
