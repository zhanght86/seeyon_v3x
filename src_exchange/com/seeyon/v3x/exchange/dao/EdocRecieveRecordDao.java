package com.seeyon.v3x.exchange.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.exchange.domain.EdocRecieveRecord;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;
import com.seeyon.v3x.exchange.util.Constants;
import com.seeyon.v3x.util.SQLWildcardUtil;

public class EdocRecieveRecordDao extends BaseHibernateDao<EdocRecieveRecord>{

	public List<EdocRecieveRecord> getEdocRecieveRecords(int status) {
		String hsql = "from EdocRecieveRecord as a where a.status=? order by a.createTime";
		return super.find(hsql, status);
	}	
	
	public List<EdocRecieveRecord> getToRegisterEdocs(long userId) {
		String hsql = "from EdocRecieveRecord as a where a.registerUserId=? and a.status=? order by a.recTime DESC";
		Object[] values = {userId, Constants.C_iStatus_Recieved};
		return super.find(hsql, -1,-1, null, values);
	}	
	
	public List<EdocRecieveRecord> findEdocRecieveRecords(String accountIds,String departIds,Set<Integer> statusSet,String condition,String value) {
		String accWhere=null;
		String depWhere=null;
		int statusFlag = 0;//待签收
		boolean bool = false;
		if((accountIds==null || "".equalsIgnoreCase(accountIds)) && (departIds==null || "".equalsIgnoreCase(departIds)))
		{
			return null;
		}
		if(statusSet == null  || statusSet.size() == 0)
			return null;
		String hsql = "from EdocRecieveRecord as a where ";
		if(statusSet.size() > 1){
			statusFlag = 1;//已签收
		}
		// 
		String statusHql = "";
		int i = 0;
		for(int s : statusSet){
			if(i == 0)
				statusHql += " a.status = " + s;
			else
				statusHql += " or a.status = " + s;
			i++;
		}
		if(!"".equals(statusHql)){hsql += "("+statusHql+")";}
		
		if(null!=condition && !"".equals(condition) && null!=value && !"".equals(value)){
			hsql += " and a."+ condition + " like ? ";
			bool = true;
		}
		if(accountIds!=null && !"".equals(accountIds))
		{
			accWhere=" (exchangeType="+ Constants.C_iAccountType_Org;
			accWhere+=" and exchangeOrgId in ("+accountIds+"))";
		}
		if(departIds!=null && !"".equals(departIds))
		{
			depWhere=" (exchangeType="+ Constants.C_iAccountType_Dept;
			depWhere+=" and exchangeOrgId in ("+departIds+"))";
		}
		if(accWhere!=null && depWhere!=null)
		{
			hsql+=" and ("+accWhere+" or "+depWhere+")";
		}
		else if(accWhere!=null && depWhere==null)
		{
			hsql+=" and "+accWhere;
		}
		else if(accWhere==null && depWhere!=null)
		{
			hsql+=" and "+depWhere;
		}
		if(statusFlag==1){
			//已签收
			hsql+=" order by a.recTime desc";
		}else{
			//待签收
			hsql+=" order by a.createTime desc";
		}
		if(bool){
			return super.find(hsql, "%"+SQLWildcardUtil.escape(value)+"%");
		}else{
			return super.find(hsql);
		}
	}
	
	public void deleteReceiveRecordByReplayId(long replayId)throws Exception{
		String hsql = "delete from EdocRecieveRecord where replyId = ? ";
		Object[] values = {String.valueOf(replayId)};
		 super.bulkUpdate(hsql, null, values);
	}
	
	public EdocRecieveRecord getRecRecordByReplayId(long replyId)throws Exception{
		String hsql = "from EdocRecieveRecord  where replyId = ? ";
		Object[] values = {String.valueOf(replyId)};
		List<EdocRecieveRecord> list =  super.find(hsql, values);
		if(null!=list && list.size()>0)
			return list.get(0);
		else
			return null;
	}
	

	public EdocRecieveRecord getEdocRecieveRecordByReciveEdocId(long id) {
		String hsql = "from EdocRecieveRecord as a where a.reciveEdocId=? ";
		List<EdocRecieveRecord> list =  super.find(hsql, id);
		if(null!=list && list.size()>0)
			return list.get(0);
		else
			return null;
	}	
	
}
