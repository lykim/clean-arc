package com.bit.persistent.gateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bit.core.constant.Direction;
import com.bit.core.constant.Position;
import com.bit.core.entity.Role;
import com.bit.core.entity.RoleGroup;
import com.bit.core.entity.User;
import com.bit.core.gateway.UserGateway;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyUserRequestModel;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;
import com.bit.persistent.DataSource;

public class UserGatewayImpl implements UserGateway{

	private static UserGateway INSTANCE;
	private UserGatewayImpl() {};
	private static String DEFAULT_ORDER_BY = "username";
	
	public static UserGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new UserGatewayImpl();
		}
		return INSTANCE;
	}
	
	@Override
	public User findById(String id) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt= connection.prepareStatement("SELECT u.id as u_id, u.username as u_name, u.email as u_email, "
					+ " u.salesman_code as u_salesman_code, u.active as u_active,"
					+ "  '' as u_password,  u.created_at as u_created_at, u.position as u_position "
					+ " ,u.created_by as u_created_by, u.updated_at as u_updated_by, u.updated_at as u_updated_at "
					+ " ,rg.name as rg_name, rg.id as rg_id, rg.description as rg_description "
					+ " ,r.code as r_code, r.description as r_description, r.id as r_id "
					+ " FROM \"user\" u LEFT JOIN user_role_group ugr ON u.id=ugr.user_id "
					+ " LEFT JOIN role_group rg ON rg.id = ugr.role_group_id "
					+ " LEFT JOIN role_group_role rgr ON rgr.role_group_id = rg.id "
					+ " LEFT JOIN role r ON r.id = rgr.role_id "
					+ " WHERE u.id=?");
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();  
			List<User> users = mapFullUserFromResultSet(rs);
			if(CollectionUtils.isEmpty(users)) return null;
			return users.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("database error",e);
		}
	}
	
	private List<User> mapFullUserFromResultSet(ResultSet rs) throws SQLException {
		Map<String,User> userMap = new HashMap<>();
		Map<String,RoleGroup> roleGroupMap = new HashMap<>();
		String id = "";
		while(rs.next()){
			id = rs.getString("u_id");
			User user = userMap.getOrDefault(rs.getString("u_id"), createUserFromRs(rs));
			if(StringUtils.isNotEmpty(rs.getString("rg_id"))){
				RoleGroup roleGroup = roleGroupMap.getOrDefault(rs.getString("rg_id"), 
						createRoleGroupFromRs(rs));
				if(StringUtils.isNotEmpty(rs.getString("r_id"))) {
					Role role = new Role();
					role.setId(rs.getString("r_id"));
					role.setCode(rs.getString("r_code"));
					role.setDescription(rs.getString("r_description"));
					roleGroup.getRoles().add(role);
				}
				roleGroupMap.put(rs.getString("rg_id"), roleGroup);
				user.getRoleGroups().add(roleGroup);
			}
			userMap.put(rs.getString("u_id"), user);
		}
		return new ArrayList<>(userMap.values());
	}
	
	private RoleGroup createRoleGroupFromRs(ResultSet rs) throws SQLException{
		RoleGroup roleGroup = new RoleGroup();
		roleGroup.setId(rs.getString("rg_id"));
		roleGroup.setName(rs.getString("rg_name"));
		roleGroup.setDescription(rs.getString("rg_description"));
		return roleGroup;
	}
	
	private User createUserFromRs(ResultSet rs)  throws SQLException {
		User user = new User();
		user.setId(rs.getString("u_id"));
		user.setSalesmanCode(rs.getString("u_salesman_code"));
		user.setUsername(rs.getString("u_name"));
		user.setEmail(rs.getString("u_email"));
		user.setActive(rs.getBoolean("u_active"));
		user.setCreatedBy(rs.getString("u_created_by"));
		user.setCreatedTime(rs.getTimestamp("u_created_at").toInstant());
		user.setUpdatedBy(rs.getString("u_updated_by"));
		user.setUpdatedTime(rs.getTimestamp("u_updated_at").toInstant());
		int positionCode = rs.getInt("u_position");
		if(positionCode > 0) {
			user.setPosition(Position.get(positionCode));
		}
		if(StringUtils.isNotEmpty(rs.getString("u_password"))) {
			user.setPassword(rs.getString("u_password"));
		}
		return user;
	}
	
	@Override
	public User findByIdShowPassword(String id) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt= connection.prepareStatement("SELECT * FROM \"user\" WHERE id=?");
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();  
			List<User> users = mapUserFromResultSetShowPassword(rs);
			if(CollectionUtils.isEmpty(users)) return null;
			return users.get(0);
		} catch (Exception e) {
			throw new RuntimeException("database error",e);
		}
	}
	
	@Override
	public void save(User user) {
		try(Connection connection = DataSource.getConnection()) {
			connection.setAutoCommit(false);
			saveUser(connection, user);
			saveUserRoleGroup(connection, user);
			connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}
	
	private void saveUserRoleGroup(Connection connection, User entity) throws SQLException {
		if(!CollectionUtils.isEmpty(entity.getRoleGroups())) {
			for(RoleGroup roleGroup : entity.getRoleGroups()) {
				PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"user_role_group\" (role_group_id, user_id) values (?,?)");
				stmt.setString(1, roleGroup.getId());
				stmt.setString(2, entity.getId());
				stmt.execute();
			}							
		}
	}
	
	private void saveUser(Connection connection, User user) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"user\" (id, username, password_hash, email, salesman_code, active, "
				+ "created_by, created_at, updated_by, updated_at, position) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");
		stmt.setString(1, user.getId());
		stmt.setString(2,  user.getUsername());
		stmt.setString(3,  user.getPassword());
		stmt.setString(4,  user.getEmail());
		stmt.setString(5,  user.getSalesmanCode());
		stmt.setBoolean(6,  user.isActive());
		stmt.setString(7,  user.getCreatedBy());
		stmt.setTimestamp(8,  Timestamp.from(user.getCreatedTime()) );
		stmt.setString(9,  user.getUpdatedBy());
		stmt.setTimestamp(10,  Timestamp.from(user.getUpdatedTime()) );
		stmt.setInt(11, user.getPosition() != null ? user.getPosition().getCode() : 0);
		stmt.execute();
	}

	@Override
	public void update(User user, String id) {
		try(Connection connection = DataSource.getConnection()) {
			connection.setAutoCommit(false);
			updateUser(connection, user);
			deleteRoleGroupRelations(connection, id);
			saveRoleGroupRelations(connection, user, id);
			connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveRoleGroupRelations(Connection connection, User entity, String id) throws SQLException {
		if(!CollectionUtils.isEmpty(entity.getRoleGroups())) {
			deleteRoleGroupRelations(connection, id);
			for(RoleGroup roleGroup : entity.getRoleGroups()) {
				saveRoleGroupRelation(connection, roleGroup, id);
			}
		}
	}
	
	private void saveRoleGroupRelation(Connection connection, RoleGroup entity, String id) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"user_role_group\" (user_id, role_group_id) VALUES ( ?, ?)");
		stmt.setString(1, id);
		stmt.setString(2, entity.getId());
		stmt.execute();
	}
	
	private void deleteRoleGroupRelations(Connection connection, String id) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("DELETE FROM \"user_role_group\" WHERE user_id = ?");
		stmt.setString(1, id);
		stmt.execute();
	}
	
	private void updateUser(Connection connection, User user) throws SQLException {
		String fieldsToUpdate = "";
		if(StringUtils.isNotEmpty(user.getUsername())) {
			fieldsToUpdate += "username=?";
		}
		if(StringUtils.isNotEmpty(user.getEmail())) {
			fieldsToUpdate += StringUtils.isNotEmpty(fieldsToUpdate) ? "," : "";
			fieldsToUpdate += "email=?";
		}
		if(StringUtils.isNotEmpty(user.getSalesmanCode())) {
			fieldsToUpdate += StringUtils.isNotEmpty(fieldsToUpdate) ? "," : "";
			fieldsToUpdate += "salesman_code=?";
		}
		if(user.getPosition() != null) {
			fieldsToUpdate += StringUtils.isNotEmpty(fieldsToUpdate) ? "," : "";
			fieldsToUpdate += "position=?";
		}
		PreparedStatement stmt = connection.prepareStatement("UPDATE \"user\" SET "+ fieldsToUpdate +" WHERE id = ?");
		int position= 0;
		if(StringUtils.isNotEmpty(user.getUsername())) {
			stmt.setString(++position, user.getUsername());
		}
		if(StringUtils.isNotEmpty(user.getEmail())) {
			stmt.setString(++position, user.getEmail());
		}
		if(StringUtils.isNotEmpty(user.getSalesmanCode())) {
			stmt.setString(++position, user.getSalesmanCode());
		}
		if(user.getPosition() != null) {
			stmt.setInt(++position, user.getPosition().getCode());
		}
		stmt.setString(++position, user.getId());
		stmt.execute();		
	}

	@Override
	public void clean() {
		try(Connection connection = DataSource.getConnection()) {			
			connection.setAutoCommit(false);
			PreparedStatement stmtRoleGroupRole = connection.prepareStatement("DELETE FROM \"user_role_group\"");
			stmtRoleGroupRole.execute();
			PreparedStatement stmt = connection.prepareStatement("DELETE FROM \"user\"");
			stmt.execute();
			connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}

	@Override
	public void setActive(String id, boolean active) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmtParticipant = connection.prepareStatement("UPDATE \"user\" SET active=? WHERE id = ?");
			stmtParticipant.setBoolean(1, active);
			stmtParticipant.setString(2, id);
			stmtParticipant.execute();			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public User findByUsername(String username) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt= connection.prepareStatement("SELECT u.id as u_id, u.username as u_name, u.email as u_email, "
					+ " u.salesman_code as u_salesman_code, u.active as u_active, u.position as u_position, "
					+ "  '' as u_password,  u.created_at as u_created_at "
					+ " ,u.created_by as u_created_by, u.updated_at as u_updated_by, u.updated_at as u_updated_at "
					+ " ,rg.name as rg_name, rg.id as rg_id, rg.description as rg_description "
					+ " ,r.code as r_code, r.description as r_description, r.id as r_id "
					+ " FROM \"user\" u LEFT JOIN user_role_group ugr ON u.id=ugr.user_id "
					+ " LEFT JOIN role_group rg ON rg.id = ugr.role_group_id "
					+ " LEFT JOIN role_group_role rgr ON rgr.role_group_id = rg.id "
					+ " LEFT JOIN role r ON r.id = rgr.role_id "
					+ " WHERE u.username=?");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();  
			List<User> users = mapFullUserFromResultSet(rs);
			if(CollectionUtils.isEmpty(users)) return null;
			return users.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("database error",e);
		}
	}
	
	@Override
	public User findByUsernameShowPassword(String username) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt= connection.prepareStatement("SELECT u.id as u_id, u.username as u_name, u.password_hash as u_password, "
					+ " u.email as u_email, u.salesman_code as u_salesman_code, u.active as u_active, u.created_at as u_created_at "
					+ " ,u.created_by as u_created_by, u.updated_at as u_updated_by, u.updated_at as u_updated_at, u.position as u_position "
					+ " ,rg.name as rg_name, rg.id as rg_id, rg.description as rg_description "
					+ " ,r.code as r_code, r.description as r_description, r.id as r_id "
					+ " FROM \"user\" u LEFT JOIN user_role_group ugr ON u.id=ugr.user_id "
					+ " LEFT JOIN role_group rg ON rg.id = ugr.role_group_id "
					+ " LEFT JOIN role_group_role rgr ON rgr.role_group_id = rg.id "
					+ " LEFT JOIN role r ON r.id = rgr.role_id "
					+ " WHERE u.username=?");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();  
			List<User> users = mapFullUserFromResultSet(rs);
			if(CollectionUtils.isEmpty(users)) return null;
			return users.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("database error",e);
		}
	}
	
	private List<User> mapUserFromResultSet(ResultSet rs) throws SQLException {
		List<User> users = new ArrayList<>();
		while(rs.next()){
			User user = new User();
			user.setId(rs.getString("id"));
			user.setUsername(rs.getString("username"));
			user.setSalesmanCode(rs.getString("salesman_code"));
			user.setEmail(rs.getString("email"));
			user.setActive(rs.getBoolean("active"));
			user.setCreatedBy(rs.getString("created_by"));
			user.setCreatedTime(rs.getTimestamp("created_at").toInstant());
			user.setUpdatedBy(rs.getString("updated_by"));
			user.setUpdatedTime(rs.getTimestamp("updated_at").toInstant());
			int positionCode = rs.getInt("position");
			if(positionCode > 0) {
				user.setPosition(Position.get(positionCode));
			}
			users.add(user);
		 }		
		return users;
	}
	
	private List<User> mapUserFromResultSetShowPassword(ResultSet rs) throws SQLException {
		List<User> users = new ArrayList<>();
		while(rs.next()){
			User user = new User();
			user.setId(rs.getString("id"));
			user.setUsername(rs.getString("username"));
			user.setSalesmanCode(rs.getString("salesman_code"));
			user.setPassword(rs.getString("password_hash"));
			user.setEmail(rs.getString("email"));
			user.setActive(rs.getBoolean("active"));
			user.setCreatedBy(rs.getString("created_by"));
			user.setCreatedTime(rs.getTimestamp("created_at").toInstant());
			user.setUpdatedBy(rs.getString("updated_by"));
			user.setUpdatedTime(rs.getTimestamp("updated_at").toInstant());
			int positionCode = rs.getInt("position");
			if(positionCode > 0) {
				user.setPosition(Position.get(positionCode));
			}
			users.add(user);
		 }		
		return users;
	}
	@Override
	public Page<User> find(FindManyRequestModel param) {
		try(Connection connection = DataSource.getConnection()) {
			User user = new User();
			String selectQuery = "SELECT * FROM \"user\"";
			FindManyUserRequestModel userParam = (FindManyUserRequestModel)param;
			String whereQuery = getWhereQuery(userParam);
			String orderQuery = getOrderQuery(param);
			String limitQuery = getLimitQuery(param);

			PreparedStatement stmt= connection.prepareStatement(selectQuery + whereQuery + orderQuery + limitQuery);
			setPreparedStatement(stmt, userParam);
			
			ResultSet rs = stmt.executeQuery();  
			List<User> users = mapUserFromResultSet(rs);
			
			String countQuery = "SELECT COUNT(*) as total From \"user\"";
			PreparedStatement countStmt= connection.prepareStatement(countQuery + whereQuery);
			setPreparedStatement(countStmt, userParam);
			ResultSet countRs = countStmt.executeQuery(); 
			int total = 0;
			while(countRs.next()){
				total = countRs.getInt("total");
			}
			
			return new WebPage<>(users, total, param.pageSize);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("database error",e);
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
	
	// make sure the condition order of this method must same with setPreparedStatement method
	private String getWhereQuery(FindManyUserRequestModel param) {
		String where = "";
		if(StringUtils.isNotEmpty(param.usernameLike)) {
			where = formatWhereQuery(where, " username LIKE ? ", "");
		}
		if(StringUtils.isNotEmpty(param.activeEqual)) {
			where += formatWhereQuery(where, " active = ? ", "AND");
		}
		return where;
	}
	
	// make sure the condition order of this method must same with getWhereQuery method
	private void setPreparedStatement(PreparedStatement stmt, FindManyUserRequestModel param) throws SQLException {
		int index = 0;
		if(StringUtils.isNotEmpty(param.usernameLike)) {
			stmt.setString(++index, "%"+param.usernameLike+"%");
		}
		if(StringUtils.isNotEmpty(param.activeEqual)) {
			boolean active = false;
			if(param.activeEqual.equalsIgnoreCase("active")) {
				active = true;
			}
			stmt.setBoolean(++index, active);
		}
	}
	
	private String formatWhereQuery(String where, String statement, String operand) {
		where += StringUtils.isNotEmpty(where) ? " "+ operand + " "+ statement : " where " + statement;
		return where;
	}

	@Override
	public void changePassword(String id, String password) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmtParticipant = connection.prepareStatement("UPDATE \"user\" SET password_hash=? WHERE id = ?");
			stmtParticipant.setString(1, password);
			stmtParticipant.setString(2, id);
			stmtParticipant.execute();			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}	
}
