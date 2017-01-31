package com.seeyon.v3x.exchange.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;
import com.seeyon.v3x.exchange.util.Constants;
import com.seeyon.v3x.util.SQLWildcardUtil;

public class EdocSendRecordDao extends BaseHibernateDao<EdocSendRecord>{

	public List<EdocSendRecord> getEdocSendRecords(int status) {
		String hsql = "from EdocSendRecord as a where a.status=? order by a.createTime";
		return super.find(hsql, status);
	}
	/**
	 * 查询待交换记录
	 * @param isAccount：是否为单位收发员
	 * @param accountId：当前单位ID
	 * @param departIds：任部门收发员的部门id串，以逗号分割
	 * @param status
	 * @return
	 */
	public List<EdocSendRecord> findEdocSendRecords(String accountIds,String departIds,int status,String condition,String value) {
		String accWhere=null;
		String depWhere=null;
		boolean bool = false;
		if((accountIds==null || "".equalsIgnoreCase(accountIds)) && (departIds==null || "".equalsIgnoreCase(departIds)))
		{
			return null;
		}
		String hsql = "";
		if (status == Constants.C_iStatus_Tosend) {
			// 待发送，包括发送已回退的
			hsql = "from EdocSendRecord as a where (a.status=? or a.status=2)";
		} else {
			// 已发送
			hsql = "from EdocSendRecord as a where (a.status=?)";
		}
		if(null!=condition && !"".equals(condition) && null!=value && !"".equals(value)){
			hsql += " and a."+ condition + " like ? ";
			bool = true;
		}
		if(accountIds!=null && !"".equals(accountIds))
		{
			accWhere=" (exchangeType="+ Constants.C_iExchangeType_Org;
			accWhere+=" and exchangeOrgId in ("+accountIds+"))";
		}
		if(departIds!=null && !"".equals(departIds))
		{
			depWhere=" (exchangeType="+ Constants.C_iExchangeType_Dept;
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
		if(status==0){
			//未发送
			hsql+=" order by a.createTime desc";
		}else{
			//已发送
			hsql+=" order by a.sendTime desc";
		}
		if(bool){
			return super.find(hsql, status, "%"+SQLWildcardUtil.escape(value)+"%");
		}else{
			return super.find(hsql, status);
		}
	}	
}
