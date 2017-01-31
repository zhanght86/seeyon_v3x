package com.seeyon.v3x.news.domain;

import java.util.List;

import com.seeyon.v3x.bulletin.util.Constants;

public class NewsTypeModel{
	private boolean canNewOfCurrent;   // 当前用户是否有新建权限
	private boolean canAdminOfCurrent;   // 当前用户是否可以管理
	private boolean canAuditOfCurrent;   // 当前用户是否审核员
	private NewsType newsType;
	private long userId;
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public NewsTypeModel(NewsType newsType, long userId , List<Long> domainIds){
		this.newsType = newsType;
		this.userId = userId;
		this.setProps(domainIds);
	}
	
	private void setProps( List<Long> domainIds){
		if(newsType.isAuditFlag()){
			if(newsType.getAuditUser().longValue() == userId){
				setCanAuditOfCurrent(true);
			}
		}
		for(NewsTypeManagers tm:newsType.getNewsTypeManagers()){
//			if(tm.getManagerId().longValue() == userId){
			if(domainIds.contains(tm.getManagerId())){
				if(Constants.MANAGER_FALG.equals(tm.getExt1())){
					setCanAdminOfCurrent(true);
					setCanNewOfCurrent(true);
					break;
				}else if(Constants.WRITE_FALG.equals(tm.getExt1()))
					setCanNewOfCurrent(true);
			}			
		}
	}

	public NewsType getNewsType() {
		return newsType;
	}

	public void setNewsType(NewsType newsType) {
		this.newsType = newsType;
	}

	public boolean getCanAdminOfCurrent() {
		return canAdminOfCurrent;
	}

	public void setCanAdminOfCurrent(boolean canAdminOfCurrent) {
		this.canAdminOfCurrent = canAdminOfCurrent;
	}

	public boolean getCanAuditOfCurrent() {
		return canAuditOfCurrent;
	}

	public void setCanAuditOfCurrent(boolean canAuditOfCurrent) {
		this.canAuditOfCurrent = canAuditOfCurrent;
	}

	public boolean getCanNewOfCurrent() {
		return canNewOfCurrent;
	}

	public void setCanNewOfCurrent(boolean canNewOfCurrent) {
		this.canNewOfCurrent = canNewOfCurrent;
	}
}
