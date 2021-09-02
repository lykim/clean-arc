package com.bit.core.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import com.bit.core.entity.base.Entity;
import com.bit.core.mocks.entity.MockEntity;

public class EntityTest {

	@Test
	public void createEntityWillGenerateId() {
		Entity entity = new MockEntity();
		assertTrue(StringUtils.isNotBlank(entity.getId()));
	}
	
	@Test
	public void entitiesWithDifferentIdAreNotSame() {
		Entity entity1 = new MockEntity();
		Entity entity2 = new MockEntity();
		assertNotEquals(entity1, entity2);
	}
	
	@Test
	public void entitiesWithSameIdAreSame() {
		Entity entity1 = new MockEntity();
		Entity entity2 = new MockEntity();
		entity2.setId(entity1.getId());
		assertEquals(entity1, entity2);
	}
	
	@Test
	public void createEntityWillGetActiveEntity() {
		Entity entity = new MockEntity();
		assertTrue(entity.isActive());
	}
	
}
