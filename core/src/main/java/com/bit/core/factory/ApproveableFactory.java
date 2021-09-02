package com.bit.core.factory;

import com.bit.core.constant.ApproverableType;
import com.bit.core.entity.Approver;
import com.bit.core.entity.SimpleMultipleApproveable;
import com.bit.core.mocks.MockApproveable;
import com.bit.core.strategy.Approveable;

public class ApproveableFactory {
	public static Approveable get(Approver approver) {
		if(approver == null ) return null;
		if(approver.getApproverabelType() == null) return null;
		if(approver.getApproverabelType() == ApproverableType.TEST) {
			return new MockApproveable(approver);
		}
		if(approver.getApproverabelType() == ApproverableType.SIMPLE) {
			return new SimpleMultipleApproveable(approver);
		}
		return null;
	}

}
