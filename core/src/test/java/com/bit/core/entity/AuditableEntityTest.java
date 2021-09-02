package com.bit.core.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import com.bit.core.entity.base.AuditableEntity;
import com.bit.core.mocks.entity.MockAuditableEntity;

public class AuditableEntityTest {
	
	@Test
	public void instantiateAuditableEntityWillGenerateAuditFields() {
		AuditableEntity entity = new MockAuditableEntity();
		assertTrue(StringUtils.isNotBlank(entity.getCreatedBy()));
		assertTrue(StringUtils.isNotBlank(entity.getUpdatedBy()));
		assertNotNull(entity.getCreatedTime());
		assertNotNull(entity.getUpdatedTime());
	}
}
