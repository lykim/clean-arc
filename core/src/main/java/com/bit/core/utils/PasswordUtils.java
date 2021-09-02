package com.bit.core.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtils {
	public static String generate(String token) {
		return BCrypt.withDefaults().hashToString(12, token.toCharArray());
	}
	
	public static boolean isVerified(String token, String hashedToken) {
		BCrypt.Result result = BCrypt.verifyer().verify(token.toCharArray(), hashedToken);
		return result.verified;
	}
}
