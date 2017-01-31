/**
 * 
 */
package com.seeyon.v3x.collaboration.manager.impl;

import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-3-31
 */
public class ColLock {

	public static enum COL_ACTION{
		finishWorkItem, // 处理协同
		cancel, // 撤销协同
		stepback, // 回退
		tackback, // 取回
		insertPeople, // 加签
		deletePeople, // 减签
		inform, // 知会
		colAssign, // 会签
		stepstop
		// 终止
	}

	private static ColLock instance = null;

	private ColLock() {
	}

	public static ColLock getInstance() {
		if (instance == null) {
			instance = new ColLock();
		}

		return instance;
	}

	public void checkCanAction(long summary, long memberId, String memberName, ColLock.COL_ACTION currentAction) throws BusinessException{

	}

	public void removeLock(long summaryId){

	}


}
