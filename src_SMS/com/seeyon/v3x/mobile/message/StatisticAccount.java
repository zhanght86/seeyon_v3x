/**
 * 
 */
package com.seeyon.v3x.mobile.message;

import com.seeyon.v3x.mobile.message.domain.MobileMessage;
import com.seeyon.v3x.util.EnumUtil;

/**
 * 按单位统计结果对象
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-4-16
 */
public final class StatisticAccount {
	private int count;

	private long accountId;

	private MobileMessage.SMSType type;
	
	public StatisticAccount(int count, long accountId, int type) {
		super();
		this.count = count;
		this.accountId = accountId;
		this.type = EnumUtil.getEnumByOrdinal(MobileMessage.SMSType.class, type);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public MobileMessage.SMSType getType() {
		return type;
	}

	public void setType(MobileMessage.SMSType type) {
		this.type = type;
	}

}
