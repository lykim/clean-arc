package com.bit.core.entity;

import com.bit.core.strategy.Approveable;

public class SimpleMultipleApproveable extends Approveable{
	public SimpleMultipleApproveable(Approver approver) {
		super(approver);
	}
}
