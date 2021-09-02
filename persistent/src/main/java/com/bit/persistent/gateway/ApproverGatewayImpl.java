package com.bit.persistent.gateway;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bit.core.constant.ApproverModule;
import com.bit.core.constant.ApproverableType;
import com.bit.core.constant.Direction;
import com.bit.core.constant.NotificatorType;
import com.bit.core.entity.Approver;
import com.bit.core.entity.EmailNotificator;
import com.bit.core.entity.KeyPerformanceIndicator;
import com.bit.core.entity.RoleGroup;
import com.bit.core.entity.Salesman;
import com.bit.core.entity.User;
import com.bit.core.entity.base.Notificator;
import com.bit.core.factory.NotificatorFactory;
import com.bit.core.gateway.ApproverGateway;
import com.bit.core.model.request.FindManyRequestModel;
import com.bit.core.model.request.FindManyRoleGroupRequestModel;
import com.bit.core.model.request.SalesmanRequestModel;
import com.bit.core.response.WebPage;
import com.bit.core.response.base.Page;
import com.bit.core.utils.CollectionUtils;
import com.bit.core.utils.StringUtils;
import com.bit.persistent.DataSource;

public class ApproverGatewayImpl implements ApproverGateway{

	private static ApproverGateway INSTANCE;
	private ApproverGatewayImpl() {};
	
	public static ApproverGateway getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ApproverGatewayImpl();
		}
		return INSTANCE;
	}
	
	@Override
	public Approver findById(String id) {
		try(Connection connection = DataSource.getConnection()) {
			String whereQuery = " WHERE ap.id = ? ";
			PreparedStatement stmt= connection.prepareStatement(getSelectQuery() + whereQuery);
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			return getOneApproverFromRs(rs);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("database error",e);
		}
	}

	@Override
	public void save(Approver entity) {
		try(Connection connection = DataSource.getConnection()) {
			connection.setAutoCommit(false);
			executeSaveApprovalPreparedStatement(connection, entity);			
			executeBatchSaveApprovalApproverPreparedStatement(connection, entity);
			executeBatchSaveApprovalNotifatorPreparedStatement(connection, entity);
			connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}

	@Override
	public void update(Approver entity, String id) {
		try(Connection connection = DataSource.getConnection()) {
			connection.setAutoCommit(false);
			executeUpdateApprover(connection, entity, id);
			executeUpdateApprovalApprover(connection, entity, id);
			connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}
	
	private void executeUpdateApprovalApprover(Connection connection, Approver entity, String approvalId) throws SQLException {
		if(!CollectionUtils.isEmpty(entity.getApprovers())) {
			deleteApprovalApprover(connection, approvalId);
			executeBatchSaveApprovalApproverPreparedStatement(connection, entity);
		}
	}
	
	private void deleteApprovalApprover(Connection connection, String approvalId)throws SQLException {
		PreparedStatement stmtDelete = connection.prepareStatement("DELETE FROM approval_approver where approval_id=?");
		stmtDelete.setString(1,  approvalId);
		stmtDelete.execute();
	}
	
	private void executeUpdateApprover(Connection connection, Approver entity, String id) throws SQLException {
		if(entity.getApproverabelType() == null) return;
		String fieldsToUpdate = "";
		if(entity.getApproverabelType() != null) {
			fieldsToUpdate += " \"type\" = ? ";
		}
		PreparedStatement stmt = connection.prepareStatement("UPDATE \"approval\" SET "+ fieldsToUpdate +" WHERE id = ?");
		int position= 0;
		if(entity.getApproverabelType() != null) {
			stmt.setString(++position, entity.getApproverabelType().getAbbreviation());
		}
		stmt.setString(++position, id);
		stmt.execute();
	}
	private String getWhereQuery(FindManyRequestModel param) {
		String whereQuery = "";
		if(StringUtils.isNotEmpty(param.filterEqual.get("module"))) {
			whereQuery += " ap.module=?";
		}
		return whereQuery;
	}

	// make sure the condition order of this method must same with getWhereQuery method
	private void setPreparedStatement(PreparedStatement stmt, FindManyRequestModel param) throws SQLException {
		int index = 0;
		if(StringUtils.isNotEmpty(param.filterEqual.get("module"))) {
			stmt.setString(++index, "%"+param.filterEqual.get("module")+"%");
		}
	}


	private String getOrderQuery(FindManyRequestModel param) {
		String orderSql = " ORDER BY ";
		String orderBy = getFormatedOrderBy(param.orderBy);
		String sortBy = param.direction == Direction.DESCENDING ? param.direction.getAbbreviation() : Direction.ASCENDING.getAbbreviation();
		return orderSql + orderBy + " " + sortBy;
	}
	
	private String getFormatedOrderBy(String orderBy) {
		if(StringUtils.isNotEmpty(orderBy)) {
			if(orderBy.equals("type")) return "\"type\"";
		}else {
			return "ap.module";
		}
		return orderBy;
	}
	
	private String getMainSelectQueryForPaging(FindManyRequestModel param) {
		String selectQuery = "SELECT * FROM approval ";
		return selectQuery + getWhereQuery(param)  + getLimitQuery(param);
	}
	
	private String getSelectQueryForPaging(FindManyRequestModel param) {
		return "SELECT ap.id as ap_id, ap.*, "
				+ " u.id as u_id, u.*,"
				+ " ap_nt.* "
				+ " FROM ("+ getMainSelectQueryForPaging(param) +") ap "
				+ " JOIN approval_approver ap_ap ON ap_ap.approval_id = ap.id "
				+ " JOIN \"user\" u ON u.id = ap_ap.user_id "
				+ " JOIN approval_notificator ap_nt ON ap_nt.approval_id = ap.id ";
		
	}
	
	private String getLimitQuery(FindManyRequestModel param) {
		if(param.pageSize > 0) {
			int offset = ((param.pageNumber * param.pageSize) - param.pageSize);
			return " offset "+offset+" limit "+param.pageSize +" ";
		}
		return "";
	}
	
	@Override
	public Page<Approver> find(FindManyRequestModel param) {
		try(Connection connection = DataSource.getConnection()) {
			String whereQuery = getWhereQuery(param);
			String orderQuery = getOrderQuery(param);

			PreparedStatement stmt= connection.prepareStatement(getSelectQueryForPaging(param) + orderQuery);
			setPreparedStatement(stmt, param);

			ResultSet rs = stmt.executeQuery();  
			List<Approver> roles = mapApproverFromRs(rs);
			
			String countQuery = getCountQuery();
			PreparedStatement countStmt= connection.prepareStatement(countQuery + whereQuery);
			setPreparedStatement(countStmt, param);
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
			PreparedStatement stmtKpiPeriod = connection.prepareStatement("DELETE FROM \"approval_notificator\"");
			stmtKpiPeriod.execute();
			PreparedStatement stmtTargetKpi = connection.prepareStatement("DELETE FROM \"approval_approver\"");
			stmtTargetKpi.execute();
			PreparedStatement stmt = connection.prepareStatement("DELETE FROM \"approval\"");
			stmt.execute();
			connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}

	@Override
	public void setActive(String id, boolean active) {
		// TODO Auto-generated method stub
		
	}

	private String getSelectQuery() {
		return "SELECT ap.id as ap_id, ap.*, "
				+ " u.id as u_id, u.*,"
				+ " ap_nt.* "
				+ " FROM approval ap "
				+ " JOIN approval_approver ap_ap ON ap_ap.approval_id = ap.id "
				+ " JOIN \"user\" u ON u.id = ap_ap.user_id "
				+ " JOIN approval_notificator ap_nt ON ap_nt.approval_id = ap.id ";
	}
	
	private String getCountQuery() {
		return "SELECT count(distinct ap.id) as total "
				+ " FROM approval ap "
				+ " JOIN approval_approver ap_ap ON ap_ap.approval_id = ap.id "
				+ " JOIN \"user\" u ON u.id = ap_ap.user_id "
				+ " JOIN approval_notificator ap_nt ON ap_nt.approval_id = ap.id ";
	}
	
	@Override
	public Approver findByModule(ApproverModule module) {
		try(Connection connection = DataSource.getConnection()) {
			String whereQuery = " WHERE ap.module = ? ";
			PreparedStatement stmt= connection.prepareStatement(getSelectQuery() + whereQuery);
			stmt.setString(1, module.getAbbreviation());
			ResultSet rs = stmt.executeQuery();
			return getOneApproverFromRs(rs);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("database error",e);
		}
	}
	
	private Approver getOneApproverFromRs(ResultSet rs) throws SQLException {
		List<Approver> approvers = mapApproverFromRs(rs);
		if(CollectionUtils.isEmpty(approvers)) return null;
		return approvers.get(0);
	}
	
	private List<Approver> mapApproverFromRs(ResultSet rs) throws SQLException{
		Map<String, Approver> approverMap = new HashMap<>();
		Map<String, Map<String,User>> userApproverMap = new HashMap<>();
		Map<String, Map<String, Notificator>> notificatorApproverMap = new HashMap<>();
		String approverId = "";
		List<Approver> approvers = new ArrayList<>();
		while(rs.next()) {
			approverId = rs.getString("ap_id");
			if(StringUtils.isNotEmpty(approverId)) {
				Approver approver = approverMap.getOrDefault(approverId, null);
				if(approver == null) {
					approver = createApprovalFromRs(rs);
					approvers.add(approver);
				}
				String userId = rs.getString("u_id");
				if(StringUtils.isNotEmpty(userId)) {
					Map<String,User> userMap = userApproverMap.getOrDefault(approverId, new HashMap<>());
					User user = userMap.getOrDefault(userId, createUserFromRs(rs));
					userMap.put(userId, user);
					userApproverMap.put(approverId, userMap);
				}
				Map<String,User> userMap = userApproverMap.getOrDefault(approverId, new HashMap<>());
				approver.setApprovers(new HashSet<>(userMap.values()));
				String notificator =  rs.getString("notificator");
				if(StringUtils.isNotEmpty(notificator)) {
					Map<String, Notificator> notificatorMap = notificatorApproverMap.getOrDefault(approverId, new HashMap<>());
					Notificator notif = notificatorMap.getOrDefault(notificatorMap, createNotificatorFromRs(rs));
					notificatorMap.put(notificator, notif);
					notificatorApproverMap.put(approverId, notificatorMap);
				}
				Map<String, Notificator> notificatorMap = notificatorApproverMap.getOrDefault(approverId, new HashMap<>());
				approver.setNotificators(new HashSet<>( notificatorMap.values() ));
				approverMap.put(approverId, approver);
			}
		}
		return approvers;
	}
	
	private Notificator createNotificatorFromRs(ResultSet rs)throws SQLException {
		return NotificatorFactory.get(NotificatorType.get(rs.getString("notificator")));
	}
	
	private Approver createApprovalFromRs(ResultSet rs) throws SQLException {
		Approver approver = new Approver();
		approver.setId(rs.getString("ap_id"));
		approver.setModule(ApproverModule.get(rs.getString("module")) );
		approver.setApproverabelType( ApproverableType.get( rs.getString("type") ));
		return approver;
	}
	
	private User createUserFromRs(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getString("u_id"));
		user.setEmail(rs.getString("email"));
		user.setUsername(rs.getString("username"));
		return user;
	}

	@Override
	public void updateApprovers(Set<User> approvers, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateNotificators(Set<Notificator> notificators, String id) {
		// TODO Auto-generated method stub
		
	}
	
	private void executeBatchSaveApprovalNotifatorPreparedStatement(Connection connection, Approver entity) throws SQLException {
		PreparedStatement stmtInsertNotificator = connection.prepareStatement("INSERT INTO approval_notificator (approval_id, notificator) VALUES(?,?)");
		for(Notificator notificator : entity.getNotificators()) {
			stmtInsertNotificator.setString(1, entity.getId());
			stmtInsertNotificator.setString(2, notificator.getType());
			stmtInsertNotificator.addBatch();
		};
		stmtInsertNotificator.executeBatch();
	}
	private void executeBatchSaveApprovalApproverPreparedStatement(Connection connection, Approver entity) throws SQLException {
		PreparedStatement stmtInsertApprover = connection.prepareStatement("INSERT INTO approval_approver (approval_id, user_id) VALUES(?,?)");
		for(User user : entity.getApprovers()) {
			stmtInsertApprover.setString(1, entity.getId());
			stmtInsertApprover.setString(2, user.getId());
			stmtInsertApprover.addBatch();
		};
		stmtInsertApprover.executeBatch();
	}
	
	private void executeSaveApprovalPreparedStatement(Connection connection, Approver entity) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO \"approval\" (id, module, type, active, "
				+ "created_by, created_at, updated_by, updated_at) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)");
		stmt.setString(1, entity.getId());
		stmt.setString(2,  entity.getModule().getAbbreviation());
		stmt.setString(3,  entity.getApproverabelType().getAbbreviation());
		stmt.setBoolean(4,  entity.isActive());
		stmt.setString(5,  entity.getCreatedBy());
		stmt.setTimestamp(6,  Timestamp.from(entity.getCreatedTime()) );
		stmt.setString(7,  entity.getUpdatedBy());
		stmt.setTimestamp(8,  Timestamp.from(entity.getUpdatedTime()) );
		stmt.execute();
	}

}
