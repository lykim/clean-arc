package com.bit.persistent.gateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bit.core.constant.Direction;
import com.bit.core.entity.Role;
import com.bit.core.entity.RoleGroup;
import com.bit.core.gateway.RoleGroupGateway;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyRoleGroupRequestModel;
import com.bit.core.model.request.FindManyRoleRequestModel;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;
import com.bit.persistent.DataSource;

public class RoleGroupGatewayImpl implements RoleGroupGateway{

	private static RoleGroupGateway INSTANCE;
	private RoleGroupGatewayImpl() {};
	private static String DEFAULT_ORDER_BY = "name";
	
	public static RoleGroupGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new RoleGroupGatewayImpl();
		}
		return INSTANCE;
	}

	@Override
	public RoleGroup findById(String id) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt= connection.prepareStatement("SELECT rg.id as rg_id, rg.name as rg_name, rg.description as rg_description, rg.active as rg_active "
					+ " ,rg.created_at as rg_created_at, rg.created_by as rg_created_by, rg.updated_at as rg_updated_by, rg.updated_at as rg_updated_at "
					+ " ,r.code as r_code, r.description as r_description, r.id as r_id "
					+ " FROM \"role_group\" rg LEFT JOIN \"role_group_role\" rgr ON rg.id = rgr.role_group_id "
					+ " LEFT JOIN \"role\" r ON rgr.role_id = r.id  WHERE rg.id=?");
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();  
			List<RoleGroup> roleGroups = mapRoleGroupFromResultSet(rs);
			if(CollectionUtils.isEmpty(roleGroups)) return null;
			return roleGroups.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("database error",e);
		}
	}
	
	private List<RoleGroup> mapRoleGroupFromResultSet(ResultSet rs) throws SQLException {
		Map<String,RoleGroup> roleGroupMap = new HashMap<>();
		while(rs.next()){
			RoleGroup roleGroup = roleGroupMap.getOrDefault(rs.getString("rg_id"), createRoleGroupFromRs(rs));
			if(StringUtils.isNotEmpty(rs.getString("r_id"))){
				Role role = new Role();
				role.setId(rs.getString("r_id"));
				role.setCode(rs.getString("r_code"));
				role.setDescription(rs.getString("r_description"));
				roleGroup.getRoles().add(role);
			}
			roleGroupMap.put(rs.getString("rg_id"), roleGroup);
		}
		return new ArrayList<>(roleGroupMap.values());
	}
	
	private List<RoleGroup> mapSimpleRoleGroupFromResultSet(ResultSet rs) throws SQLException {
		List<RoleGroup> roleGroups = new ArrayList<>();
		while(rs.next()){
			RoleGroup roleGroup = new RoleGroup();
			roleGroup.setId(rs.getString("id"));
			roleGroup.setName(rs.getString("name"));
			roleGroup.setDescription(rs.getString("description"));
			roleGroup.setActive(rs.getBoolean("active"));
			roleGroup.setCreatedBy(rs.getString("created_by"));
			roleGroup.setCreatedTime(rs.getTimestamp("created_at").toInstant());
			roleGroup.setUpdatedBy(rs.getString("updated_by"));
			roleGroup.setUpdatedTime(rs.getTimestamp("updated_at").toInstant());
			roleGroups.add(roleGroup);
		}
		return roleGroups;
	}
	
	private RoleGroup createRoleGroupFromRs(ResultSet rs)  throws SQLException {
		RoleGroup roleGroup = new RoleGroup();
		roleGroup.setId(rs.getString("rg_id"));
		roleGroup.setName(rs.getString("rg_name"));
		roleGroup.setDescription(rs.getString("rg_description"));
		roleGroup.setActive(rs.getBoolean("rg_active"));
		roleGroup.setCreatedBy(rs.getString("rg_created_by"));
		roleGroup.setCreatedTime(rs.getTimestamp("rg_created_at").toInstant());
		roleGroup.setUpdatedBy(rs.getString("rg_updated_by"));
		roleGroup.setUpdatedTime(rs.getTimestamp("rg_updated_at").toInstant());
		return roleGroup;
	}

	@Override
	public void save(RoleGroup entity) {
		try(Connection connection = DataSource.getConnection()) {
			connection.setAutoCommit(false);
			saveRoleGroup(connection, entity);
			saveRoleGroupRole(connection, entity);
			connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}
	
	private void saveRoleGroupRole(Connection connection, RoleGroup entity) throws SQLException {
		if(!CollectionUtils.isEmpty(entity.getRoles())) {
			for(Role role : entity.getRoles()) {
				PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"role_group_role\" (role_group_id, role_id) values (?,?)");
				stmt.setString(1, entity.getId());
				stmt.setString(2, role.getId());
				stmt.execute();
			}							
		}
	}
	
	private void saveRoleGroup(Connection connection, RoleGroup entity) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"role_group\" (id, name, description, active, "
				+ "created_by, created_at, updated_by, updated_at) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)");
		stmt.setString(1, entity.getId());
		stmt.setString(2,  entity.getName());
		stmt.setString(3,  entity.getDescription());
		stmt.setBoolean(4,  entity.isActive());
		stmt.setString(5,  entity.getCreatedBy());
		stmt.setTimestamp(6,  Timestamp.from(entity.getCreatedTime()) );
		stmt.setString(7,  entity.getUpdatedBy());
		stmt.setTimestamp(8,  Timestamp.from(entity.getUpdatedTime()) );
		stmt.execute();
	}

	@Override
	public void update(RoleGroup entity, String id) {
		try(Connection connection = DataSource.getConnection()) {
			connection.setAutoCommit(false);
			updateRoleGroup(connection, entity);	
			saveRoleGroupRoleRelations(connection, entity,id);
			connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveRoleGroupRoleRelations(Connection connection, RoleGroup entity, String id) throws SQLException {
		if(!CollectionUtils.isEmpty(entity.getRoles())) {
			deleteRoleGroupRoleRelations(connection, id);
			for(Role role : entity.getRoles()) {
				saveRoleGroupRoleRelation(connection, role, id);
			}
		}
	}
	
	private void saveRoleGroupRoleRelation(Connection connection, Role entity, String id) throws SQLException {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"role_group_role\" (role_group_id, role_id) VALUES ( ?, ?)");
			stmt.setString(1, id);
			stmt.setString(2, entity.getId());
			stmt.execute();
	}
	
	private void deleteRoleGroupRoleRelations(Connection connection, String id) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("DELETE FROM \"role_group_role\" WHERE role_group_id = ?");
		stmt.setString(1, id);
		stmt.execute();
	}
	
	private void updateRoleGroup(Connection connection, RoleGroup entity) throws SQLException {
		String fieldsToUpdate = "";
		if(StringUtils.isNotEmpty(entity.getName())) {
			fieldsToUpdate += "name=?";
		}
		if(StringUtils.isNotEmpty(entity.getDescription())) {
			fieldsToUpdate += StringUtils.isNotEmpty(fieldsToUpdate) ? "," : "";
			fieldsToUpdate += "description=?";
		}
		PreparedStatement stmt = connection.prepareStatement("UPDATE \"role_group\" SET "+ fieldsToUpdate +" WHERE id = ?");
		int position= 0;
		if(StringUtils.isNotEmpty(entity.getName())) {
			stmt.setString(++position, entity.getName());
		}
		if(StringUtils.isNotEmpty(entity.getDescription())) {
			stmt.setString(++position, entity.getDescription());
		}
		stmt.setString(++position, entity.getId());
		stmt.execute();	
	}
	
	@Override
	public Page<RoleGroup> find(FindManyRequestModel param) {
		try(Connection connection = DataSource.getConnection()) {
			String selectQuery = "SELECT * FROM \"role_group\"";
			FindManyRoleGroupRequestModel userParam = (FindManyRoleGroupRequestModel)param;
			String whereQuery = getWhereQuery(userParam);
			String orderQuery = getOrderQuery(param);
			String limitQuery = getLimitQuery(param);

			PreparedStatement stmt= connection.prepareStatement(selectQuery + whereQuery + orderQuery + limitQuery);
			setPreparedStatement(stmt, userParam);
			
			ResultSet rs = stmt.executeQuery();  
			List<RoleGroup> roles = mapSimpleRoleGroupFromResultSet(rs);
			
			String countQuery = "SELECT COUNT(*) as total From \"role_group\"";
			PreparedStatement countStmt= connection.prepareStatement(countQuery + whereQuery);
			setPreparedStatement(countStmt, userParam);
			ResultSet countRs = countStmt.executeQuery(); 
			int total = 0;
			while(countRs.next()){
				total = countRs.getInt("total");
			}
			
			return new WebPage<>(roles, total, param.pageSize);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("database error",e);
		}
	}

	@Override
	public void clean() {
		try(Connection connection = DataSource.getConnection()) {
			connection.setAutoCommit(false);
			PreparedStatement stmtRoleGroupRole = connection.prepareStatement("DELETE FROM \"role_group_role\"");
			stmtRoleGroupRole.execute();
			PreparedStatement stmtRoleGroup = connection.prepareStatement("DELETE FROM \"role_group\"");
			stmtRoleGroup.execute();
			
			connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}

	@Override
	public void setActive(String id, boolean active) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmtParticipant = connection.prepareStatement("UPDATE \"role_group\" SET active=? WHERE id = ?");
			stmtParticipant.setBoolean(1, active);
			stmtParticipant.setString(2, id);
			stmtParticipant.execute();			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public RoleGroup findByName(String name) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt= connection.prepareStatement("SELECT rg.id as rg_id, rg.name as rg_name, rg.description as rg_description, rg.active as rg_active "
					+ " ,rg.created_at as rg_created_at, rg.created_by as rg_created_by, rg.updated_at as rg_updated_by, rg.updated_at as rg_updated_at "
					+ " ,r.code as r_code, r.description as r_description, r.id as r_id "
					+ " FROM \"role_group\" rg LEFT JOIN \"role_group_role\" rgr ON rg.id = rgr.role_group_id "
					+ " LEFT JOIN \"role\" r ON rgr.role_id = r.id  WHERE rg.name=?");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();  
			List<RoleGroup> roleGroups = mapRoleGroupFromResultSet(rs);
			if(CollectionUtils.isEmpty(roleGroups)) return null;
			return roleGroups.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("database error",e);
		}
	}
	
	// make sure the condition order of this method must same with setPreparedStatement method
	private String getWhereQuery(FindManyRoleGroupRequestModel param) {
		String where = "";
		if(StringUtils.isNotEmpty(param.nameLike)) {
			where = formatWhereQuery(where, " name LIKE ? ", "");
		}
		if(StringUtils.isNotEmpty(param.activeEqual)) {
			where += formatWhereQuery(where, " active = ? ", "AND");
		}
		return where;
	}
	// make sure the condition order of this method must same with getWhereQuery method
	private void setPreparedStatement(PreparedStatement stmt, FindManyRoleGroupRequestModel param) throws SQLException {
		int index = 0;
		if(StringUtils.isNotEmpty(param.nameLike)) {
			stmt.setString(++index, "%"+param.nameLike+"%");
		}
		if(StringUtils.isNotEmpty(param.activeEqual)) {
			boolean active = false;
			if(param.activeEqual.equalsIgnoreCase("active")) {
				active = true;
			}
			stmt.setBoolean(++index, active);
		}
	}
	
	private String getOrderQuery(FindManyRequestModel param) {
		String orderSql = " ORDER BY ";
		String orderBy = StringUtils.isNotEmpty(param.orderBy) ? param.orderBy : DEFAULT_ORDER_BY;
		String sortBy = param.direction == Direction.DESCENDING ? param.direction.getAbbreviation() : Direction.ASCENDING.getAbbreviation();
		return orderSql + orderBy + " " + sortBy;
	}
	
	private String getLimitQuery(FindManyRequestModel param) {
		if(param.pageSize > 0) {
			int offset = ((param.pageNumber * param.pageSize) - param.pageSize);
			return " offset "+offset+" limit "+param.pageSize +" ";
		}
		return "";
	}
	
	private String formatWhereQuery(String where, String statement, String operand) {
		where += StringUtils.isNotEmpty(where) ? " "+ operand + " "+ statement : " where " + statement;
		return where;
	}
}
