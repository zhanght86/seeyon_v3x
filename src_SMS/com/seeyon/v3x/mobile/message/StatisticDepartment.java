/**
 * 
 */
package com.seeyon.v3x.mobile.message;

import com.seeyon.v3x.mobile.message.domain.MobileMessage;
import com.seeyon.v3x.util.EnumUtil;

/**
 * 按部门统计结果对象
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-4-16
 */
public final class StatisticDepartment {
	private int count;

	private long departmentId;

	private MobileMessage.SMSType type;
	
	public StatisticDepartment(int count, long departmentId, int type) {
		super();
		this.count = count;
		this.departmentId = departmentId;
		this.type = EnumUtil.getEnumByOrdinal(MobileMessage.SMSType.class, type);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(long departmentId) {
		this.departmentId = departmentId;
	}

	public MobileMessage.SMSType getType() {
		return type;
	}

	public void setType(MobileMessage.SMSType type) {
		this.type = type;
	}

}
