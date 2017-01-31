package com.seeyon.v3x.guestbook.domain;

import java.util.List;

/**
 * 部门空间留言本
 * 
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 * 
 */
public class LeaveWordVo {
	private LeaveWord leaveWord;
	private List<LeaveWord> subLeaveWord;
	private boolean hasNodes;

	public boolean isHasNodes() {
		return hasNodes;
	}

	public void setHasNodes(boolean hasNodes) {
		this.hasNodes = hasNodes;
	}

	public LeaveWord getLeaveWord() {
		return leaveWord;
	}

	public void setLeaveWord(LeaveWord leaveWord) {
		this.leaveWord = leaveWord;
	}

	public List<LeaveWord> getSubLeaveWord() {
		return subLeaveWord;
	}

	public void setSubLeaveWord(List<LeaveWord> subLeaveWord) {
		this.subLeaveWord = subLeaveWord;
	}

}
