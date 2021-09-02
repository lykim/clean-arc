package com.bit.persistent.gateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.bit.core.constant.Direction;
import com.bit.core.entity.UnitKpi;
import com.bit.core.gateway.UnitKpiGateway;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyUnitKpiRequestModel;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;
import com.bit.persistent.DataSource;

public class UnitKpiGatewayImpl implements UnitKpiGateway{

	private static UnitKpiGateway INSTANCE;
	private UnitKpiGatewayImpl() {};
	private static String DEFAULT_ORDER_BY = "name";
	
	public static UnitKpiGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new UnitKpiGatewayImpl();
		}
		return INSTANCE;
	}
	
	@Override
	public UnitKpi findById(String id) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt= connection.prepareStatement("SELECT * FROM unit_kpi WHERE id=?");
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();  
			List<UnitKpi> unitKpis = mapUnitKpiFromResultSet(rs);
			if(CollectionUtils.isEmpty(unitKpis)) return null;
			return unitKpis.get(0);
		} catch (Exception e) {
			throw new RuntimeException("database error",e);
		}
	}

	@Override
	public void save(UnitKpi entity) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO unit_kpi (id, name, description, active, "
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
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
		
	}

	@Override
	public void update(UnitKpi entity, String id) {
		try(Connection connection = DataSource.getConnection()) {
			String fieldsToUpdate = "";
			if(StringUtils.isNotEmpty(entity.getName())) {
				fieldsToUpdate += "name=?";
			}
			if(StringUtils.isNotEmpty(entity.getDescription())) {
				fieldsToUpdate += StringUtils.isNotEmpty(fieldsToUpdate) ? "," : "";
				fieldsToUpdate += "description=?";
			}
			PreparedStatement stmt = connection.prepareStatement("UPDATE unit_kpi SET "+ fieldsToUpdate +" WHERE id = ?");
			int position= 0;
			if(StringUtils.isNotEmpty(entity.getName())) {
				stmt.setString(++position, entity.getName());
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
	public Page<UnitKpi> find(FindManyRequestModel param) {
		try(Connection connection = DataSource.getConnection()) {
			String selectQuery = "SELECT * FROM \"unit_kpi\"";
			FindManyUnitKpiRequestModel userParam = (FindManyUnitKpiRequestModel)param;
			String whereQuery = getWhereQuery(userParam);
			String orderQuery = getOrderQuery(param);
			String limitQuery = getLimitQuery(param);

			PreparedStatement stmt= connection.prepareStatement(selectQuery + whereQuery + orderQuery + limitQuery);
			setPreparedStatement(stmt, userParam);
			
			ResultSet rs = stmt.executeQuery();  
			List<UnitKpi> unitKpis = mapUnitKpiFromResultSet(rs);
			
			String countQuery = "SELECT COUNT(*) as total From \"unit_kpi\"";
			PreparedStatement countStmt= connection.prepareStatement(countQuery + whereQuery);
			setPreparedStatement(countStmt, userParam);
			ResultSet countRs = countStmt.executeQuery(); 
			int total = 0;
			while(countRs.next()){
				total = countRs.getInt("total");
			}
			
			return new WebPage<>(unitKpis, total, param.pageSize);
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
	private String getWhereQuery(FindManyUnitKpiRequestModel param) {
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
	private void setPreparedStatement(PreparedStatement stmt, FindManyUnitKpiRequestModel param) throws SQLException {
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
	
	private String formatWhereQuery(String where, String statement, String operand) {
		where += StringUtils.isNotEmpty(where) ? " "+ operand + " "+ statement : " where " + statement;
		return where;
	}

	@Override
	public void clean() {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt = connection.prepareStatement("DELETE FROM unit_kpi");
			stmt.execute();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}

	@Override
	public void setActive(String id, boolean active) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmtParticipant = connection.prepareStatement("UPDATE unit_kpi SET active=? WHERE id = ?");
			stmtParticipant.setBoolean(1, active);
			stmtParticipant.setString(2, id);
			stmtParticipant.execute();			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public UnitKpi findByName(String name) {
		try(Connection connection = DataSource.getConnection()) {
			PreparedStatement stmt= connection.prepareStatement("SELECT * FROM unit_kpi WHERE name=?");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();  
			List<UnitKpi> unitKpis = mapUnitKpiFromResultSet(rs);
			if(CollectionUtils.isEmpty(unitKpis)) return null;
			return unitKpis.get(0);
		} catch (Exception e) {
			throw new RuntimeException("database error",e);
		}
	}

	private List<UnitKpi> mapUnitKpiFromResultSet(ResultSet rs) throws SQLException {
		List<UnitKpi> unitKpis = new ArrayList<>();
		while(rs.next()){
			UnitKpi unitKpi = new UnitKpi();
			unitKpi.setId(rs.getString("id"));
			unitKpi.setName(rs.getString("name"));
			unitKpi.setDescription(rs.getString("description"));
			unitKpi.setActive(rs.getBoolean("active"));
			unitKpi.setCreatedBy(rs.getString("created_by"));
			unitKpi.setCreatedTime(rs.getTimestamp("created_at").toInstant());
			unitKpi.setUpdatedBy(rs.getString("updated_by"));
			unitKpi.setUpdatedTime(rs.getTimestamp("updated_at").toInstant());
			unitKpis.add(unitKpi);
		 }		
		return unitKpis;
	}
}
