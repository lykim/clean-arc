package com.bit.core.constant;

import com.bit.core.entity.KeyPerformanceIndicator;

public class ErrorMessage {
	public static String NAME_ALREADY_TAKEN = "name already taken";
	public static String CODE_ALREADY_TAKEN = "code already taken";
	public static String ID_IS_NOT_EXIST = "id is not exist";
	public static String CODE_IS_NOT_EXIST = "code is not exist";
	public static String USERNAME_PASSWORD_INVALID = "wrong username or password";
	public static String UNAUTHORIZED = "Unauthorize";
	public static String BOBOT_NOT_MAX = "bobot must be "+KeyPerformanceIndicator.BOBOT_MAXIMUM;
	public static String STATUS_CODE_IS_INVALID = "Status code is invalid";
	public static String TARGET_KPI_IS_EMPTY = "target kpi is empty";
	public static String STATUS_IS_NOT_DRAFT = "status is not draft";
	public static String WRONG_PASSWORD = "wrong password";
	public static String CODE_IS_NOT_IN_ENUM = "code is not in enum";
	public static String APPROVER_MODULE_NOT_FOUND = "approver module not found";
}
