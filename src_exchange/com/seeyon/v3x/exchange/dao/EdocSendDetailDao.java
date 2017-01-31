package com.seeyon.v3x.exchange.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.exchange.domain.EdocSendDetail;

public class EdocSendDetailDao extends BaseHibernateDao<EdocSendDetail>{
	public EdocSendDetail findDetailBySendId(Long sendId){
		
		String sql =  "from EdocSendDetail as detail where detail.sendRecordId = ? ";
		Object[] values = {sendId};
		
		List<EdocSendDetail> list = super.find(sql, values); 
		
		if(null!=list && list.size()>0){
			
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public List<EdocSendDetail> findDetailListBySendId(Long sendId){
		String sql =  "from EdocSendDetail as detail where detail.sendRecordId = ? order by detail.recTime";
		Object[] values = {sendId};
		
		List<EdocSendDetail> list = super.find(sql, values); 
		return list;		
	}
}
