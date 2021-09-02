package com.bit.persistent.gateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.bit.core.constant.Direction;
import com.bit.core.entity.Role;
import com.bit.core.entity.User;
import com.bit.core.gateway.RoleGateway;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyRoleRequestModel;
import com.bit.core.model.request.FindManyUserRequestModel;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;
import com.bit.persistent.DataSource;

public class RoleGatewayImpl implements RoleGateway{

	private static RoleGateway INSTANCE;
	private RoleGatewayImpl() {};
	private static String DEFAULT_ORDER_BY = "code";
	
	public static RoleGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new RoleGatewayImpl();
		}
		return INSTANCE;
	}
	
	@Override
	public Role findById(String id) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt= connection.prepareStatement("SELECT * FROM \"role\" WHERE id=?");
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();  
			List<Role> roles = mapRoleFromResultSet(rs);
			if(CollectionUtils.isEmpty(roles)) return null;
			return roles.get(0);
		} catch (Exception e) {
			throw new RuntimeException("database error",e);
		}
	}

	@Override
	public void save(Role entity) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"role\" (id, code, description, active, "
					+ "created_by, created_at, updated_by, updated_at) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)");
			stmt.setString(1, entity.getId());
			stmt.setString(2,  entity.getCode());
			stmt.setString(3,  entity.getDescription());
			stmt.setBoolean(4,  entity.isActive());
			stmt.setString(5,  entity.getCreatedBy());
			stmt.setTimestamp(6,  Timestamp.from(entity.getCreatedTime()) );
			stmt.setString(7,  entity.getUpdatedBy());
			stmt.setTimestamp(8,  Timestamp.from(entity.getUpdatedTime()) );
			stmt.execute();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}

	@Override
	public void update(Role entity, String id) {
		try(Connection connection = DataSource.getConnection()) {
			String fieldsToUpdate = "";
			if(StringUtils.isNotEmpty(entity.getCode())) {
				fieldsToUpdate += "code=?";
			}
			if(StringUtils.isNotEmpty(entity.getDescription())) {
				fieldsToUpdate += StringUtils.isNotEmpty(fieldsToUpdate) ? "," : "";
				fieldsToUpdate += "description=?";
			}
			PreparedStatement stmt = connection.prepareStatement("UPDATE \"role\" SET "+ fieldsToUpdate +" WHERE id = ?");
			int position= 0;
			if(StringUtils.isNotEmpty(entity.getCode())) {
				stmt.setString(++position, entity.getCode());
			}
			if(StringUtils.isNotEmpty(entity.getDescription())) {
				stmt.setString(++position, entity.getDescription());
			}
			stmt.setString(++position, entity.getId());
			stmt.execute();			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Page<Role> find(FindManyRequestModel param) {
		try(Connection connection = DataSource.getConnection()) {
			String selectQuery = "SELECT * FROM \"role\"";
			FindManyRoleRequestModel userParam = (FindManyRoleRequestModel)param;
			String whereQuery = getWhereQuery(userParam);
			String orderQuery = getOrderQuery(param);
			String limitQuery = getLimitQuery(param);

			PreparedStatement stmt= connection.prepareStatement(selectQuery + whereQuery + orderQuery + limitQuery);
			setPreparedStatement(stmt, userParam);
			
			ResultSet rs = stmt.executeQuery();  
			List<Role> roles = mapRoleFromResultSet(rs);
			
			String countQuery = "SELECT COUNT(*) as total From \"role\"";
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
	private String getWhereQuery(FindManyRoleRequestModel param) {
		String where = "";
		if(StringUtils.isNotEmpty(param.codeLike)) {
			where = formatWhereQuery(where, " code LIKE ? ", "");
		}
		if(StringUtils.isNotEmpty(param.activeEqual)) {
			where += formatWhereQuery(where, " active = ? ", "AND");
		}
		return where;
	}
	
	// make sure the condition order of this method must same with getWhereQuery method
	private void setPreparedStatement(PreparedStatement stmt, FindManyRoleRequestModel param) throws SQLException {
		int index = 0;
		if(StringUtils.isNotEmpty(param.codeLike)) {
			stmt.setString(++index, "%"+param.codeLike+"%");
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
	public void clean() {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt = connection.prepareStatement("DELETE FROM \"role\"");
			stmt.execute();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}

	@Override
	public void setActive(String id, boolean active) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmtParticipant = connection.prepareStatement("UPDATE \"role\" SET active=? WHERE id = ?");
			stmtParticipant.setBoolean(1, active);
			stmtParticipant.setString(2, id);
			stmtParticipant.execute();			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Role findByCode(String code) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt= connection.prepareStatement("SELECT * FROM \"role\" WHERE code=?");
			stmt.setString(1, code);
			ResultSet rs = stmt.executeQuery();  
			List<Role> roles = mapRoleFromResultSet(rs);
			if(CollectionUtils.isEmpty(roles)) return null;
			return roles.get(0);
		} catch (Exception e) {
			throw new RuntimeException("database error",e);
		}
	}
	
	private List<Role> mapRoleFromResultSet(ResultSet rs) throws SQLException {
		List<Role> roles = new ArrayList<>();
		while(rs.next()){
			Role role = new Role();
			role.setId(rs.getString("id"));
			role.setCode(rs.getString("code"));
			role.setDescription(rs.getString("description"));
			role.setActive(rs.getBoolean("active"));
			role.setCreatedBy(rs.getString("created_by"));
			role.setCreatedTime(rs.getTimestamp("created_at").toInstant());
			role.setUpdatedBy(rs.getString("updated_by"));
			role.setUpdatedTime(rs.getTimestamp("updated_at").toInstant());
			roles.add(role);
		 }		
		return roles;
	}

}
