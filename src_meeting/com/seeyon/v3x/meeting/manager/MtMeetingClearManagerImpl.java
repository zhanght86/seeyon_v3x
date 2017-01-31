/**
 * 
 */
package com.seeyon.v3x.meeting.manager;

import java.util.Date;

import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.RunInRightEvent;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;

/**
 * 清理会议待办的垃圾数据
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2012-3-30
 */
public class MtMeetingClearManagerImpl extends RunInRightEvent {

	private AffairManager affairManager;
	
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void run() {
		affairManager.bulkUpdate("delete from Affair where app=? and completeTime<?", null, ApplicationCategoryEnum.meeting.getKey(), new Date());
	}

}
