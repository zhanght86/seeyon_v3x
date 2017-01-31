package com.seeyon.v3x.organization.inexportutil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;


import www.seeyon.com.v3x.form.base.SeeyonFormException;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.organization.controller.OrganizationController;
import com.seeyon.v3x.organization.inexportutil.inf.IPublicPara;
/**
 * 
 * @author kyt
 *
 */
public class DataDao extends BaseHibernateDao{
	private static final Log log = LogFactory
							.getLog(DataDao.class);
	/**
	 * 从后台取出表的结构，并组成DataObject,组装list
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public List getDataStructure(String tableName) throws Exception{
		Session session = getSession();
		Connection conn = session.connection();
		Statement smt = null;
		ResultSet rset = null;
		ResultSetMetaData rsmd = null;
		List resultlst = new ArrayList();
		try {
			smt = conn.createStatement();
			String tablerealname = DataUtil.getRealTableName(tableName);
			if(tablerealname == null){
				throw new Exception("传入表没有找到！");
			}
			rset = (ResultSet)smt.executeQuery(IPublicPara.select + tablerealname);
			rsmd = rset.getMetaData();
			if(rsmd != null){
				int colnum = rsmd.getColumnCount();		
				for(int i=1;i<colnum;i++){
					DataObject dataobj = new DataObject();
					dataobj.setFieldName(rsmd.getColumnName(i));
					dataobj.setLength(rsmd.getScale(i));
					dataobj.setTableName(tableName);
					resultlst.add(dataobj);
				}
			}
		}finally{
		smt.close();
		conn.close();
		session.disconnect();
		}
		return resultlst;
	}
	/**
	 * 执行批量的sql语句
	 * @param sqlstr
	 * @throws SeeyonFormException
	 * @throws SQLException
	 */
	public void execSQLList(List sqlstr) throws Exception{
		Session session = getSession();
		Connection fcon = session.connection();
		fcon.setAutoCommit(false);  
		Statement statement = fcon.createStatement();
		try {
			if(sqlstr != null && sqlstr.size() != 0){
				for (int i = 0; i < sqlstr.size(); i++) {
					StringBuffer str = (StringBuffer) sqlstr.get(i);
					if (str!=null){
						log.info("sqlstr "+i+"="+str);
					  statement.addBatch(str.toString());
					}
				}
				statement.executeBatch();
			}
		} catch(Exception e){
			fcon.rollback();
			statement.close();
			fcon.close();
			session.disconnect();
			log.info("批量sql执行错误！",e);
			throw new Exception("批量sql执行错误！",e);
		}finally{
			fcon.commit();
			statement.close();
			fcon.close();
			session.disconnect();
		}
	}
}
