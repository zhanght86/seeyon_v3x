package com.seeyon.v3x.plugin.dee.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.common.base.page.Page;
import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBean;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.plugin.dee.model.JDBCResourceBean;
import com.seeyon.v3x.plugin.dee.model.JNDIResourceBean;

/**
 * 功能说明：对Dee数据源的操作
 * @author XQ
 *
 */
public interface DeeDataSourceManager {

	public List<DeeResourceBean> findDataSourceList() throws TransformException;
	
	public List<DeeResourceBean> findDataSourceList(String condition,String byDis_name) throws TransformException;
	
	public DeeResourceBean findById(String id) throws TransformException;
	
	public void update(DeeResourceBean drb) throws TransformException;
	
	public void delete(String[] ids) throws TransformException;
	/**
	 * JDBC连接测试
	 * @param jdbcbean
	 * @return
	 * @throws Exception
	 */
	public boolean testCon(JDBCResourceBean jdbcbean) throws Exception;
	/**
	 * JNDI连接测试
	 * @param jndibean
	 * @return
	 * @throws Exception
	 */
	public boolean testJNDICon(JNDIResourceBean jndibean) throws Exception;
}
