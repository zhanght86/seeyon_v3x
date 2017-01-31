package com.seeyon.v3x.mobile.adapter.jindi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.dao.BaseJDBCDao;
import com.seeyon.v3x.common.dao.JdbcAccessDataException;
import com.seeyon.v3x.mobile.adapter.jindi.domain.SendTask;

public class AdapterMobileJinDiDao extends BaseJDBCDao {

	public int saveSendTask(boolean needSetId,SendTask st) throws JdbcAccessDataException{
		if(needSetId){
			return super.bulkUpdate("adapter.mobile.jindi.sql.saveSendTask", st.getDestNumber(),st.getContent(),st.getMsgTyep(),st.getSendFlag(),st.getCommPort(),st.getTaskId());
		}else{
			return super.bulkUpdate("adapter.mobile.jindi.sql.saveSendTask", st.getDestNumber(),st.getContent(),st.getMsgTyep(),st.getSendFlag(),st.getCommPort());
		}
	}
	
	public List<Object[]> findReceiveMes()throws JdbcAccessDataException{
		return super.query("adapter.mobile.jindi.sql.selectReceive");
	}
	
	public void setReceiveReaded(List<Long> ids)throws JdbcAccessDataException{
		DataSource dataSource = SystemEnvironment.getA8DataSource();
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer();
			int size = ids.size();
			for(int i = 0 ;i < size ;i++){
				if(sb.length() > 0){
					sb.append(",");
				}
				sb.append(ids.get(i).toString());
				if((i != 0 && i % 300 ==0) || i == size-1){
					pst = conn.prepareStatement("delete from t_recrecord where SmsIndex in ("+sb.toString()+")");
					pst.execute();
					sb = new StringBuffer();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn != null){
					conn.close();
				}
				if(pst != null){
					pst.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
