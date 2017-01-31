package com.seeyon.v3x.plugin.dee.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.base.page.Page;
import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;
import com.seeyon.v3x.dee.common.db.resource.dao.DeeResourceDAO;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;

import com.seeyon.v3x.dee.datasource.JDBCDataSource;
import com.seeyon.v3x.dee.datasource.JNDIDataSource;
import com.seeyon.v3x.plugin.dee.model.JDBCResourceBean;
import com.seeyon.v3x.plugin.dee.model.JNDIResourceBean;
import java.util.concurrent.Callable;  

public class DeeDataSourceManagerImpl implements DeeDataSourceManager {
	/**
	 * DEE实例化
	 */
	private static final DEEConfigService configService = DEEConfigService.getInstance();

	@Override
	public List<DeeResourceBean> findDataSourceList() throws TransformException {
		// TODO Auto-generated method stub
		return  configService.getAllDataResList();
		//return  new DeeResourceDAO().findAll();
	}
	public List<DeeResourceBean> findDataSourceList(String condition,String byDis_name) throws TransformException{
		List<DeeResourceBean> DeeResourceList = configService.getAllDataResList();
		List<DeeResourceBean> resultList = new ArrayList<DeeResourceBean>();
		if(StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(byDis_name)){
			if("byDis_name".equals(condition)){
				for(DeeResourceBean deeResourceBean :DeeResourceList){
					if((deeResourceBean.getDis_name() == null?"":deeResourceBean.getDis_name()).contains(byDis_name)){
						resultList.add(deeResourceBean);
					}
				}
			}
		}
		return resultList;
	}
	
	public DeeResourceBean findById(String id) throws TransformException{
		return configService.getResByResId(id);
	}
	
	public void update(DeeResourceBean drb) throws TransformException{
		configService.updateRes(drb);
	}
	public void delete(String[] ids) throws TransformException{
		//new DeeResourceDAO().deleteByIds(ids);
	}
	/**
	 * 连接测试
	 * 
	 * @param jdbcbean
	 * @return
	 */
	public boolean testCon(JDBCResourceBean jdbcbean) throws Exception {
        JDBCDataSource ds;
        Connection con = null;
        try {
            Class.forName(jdbcbean.getDriver());
            //定义超时时间，单位为秒
            DriverManager.setLoginTimeout(5);
            con = DriverManager.getConnection(jdbcbean.getUrl(), jdbcbean.getUser(), jdbcbean.getPassword());
			
			if(con != null){
				return true;
			}
        } catch(Exception e) {
        	return false;
        }
        finally {
            if(con != null) {
                try {
                    con.close();
                } catch(SQLException e) {
                	throw e;
                }
            }
        }
        return false;
	}

	/**
	 * JNDI连接测试
	 * 
	 * @param jdbcbean
	 * @return
	 */
	public boolean testJNDICon(JNDIResourceBean jndibean) throws Exception {
        JNDIDataSource ds;
        Connection con = null;
        try {
            ds = new JNDIDataSource();
            ds.setJndi(jndibean.getJndi());
            con = ds.getConnection();
            if(con != null)
                return true;
        }catch(Exception e) {
        	throw e;
        }
        finally {
            if(con != null) {
                try {
                    con.close();
                } catch(SQLException e) {
                	throw e;
                }
            }
        }
        return false;
	}
}

