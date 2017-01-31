package com.seeyon.v3x.doc.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocAcl;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;

public class DocAclDao extends BaseHibernateDao<DocAcl> {
	/**
	 * 删除不在userid中的对象
	 * @param docResourceId
	 * @param userid
	 * @param isMine
	 */
	public void deleteBorrow(final Long docResourceId, final List<Long> userid, final boolean isMine)
	    {
	        this.getHibernateTemplate().execute(new HibernateCallback()
	        {
	            public Object doInHibernate(Session s) throws HibernateException, SQLException
	            {
	        		String hsql = "delete from "+DocAcl.class.getName()+" as a where a.docResourceId=:docResourceId";
	        		if (isMine) {
	        			hsql = hsql + " and a.sharetype=" + Constants.SHARETYPE_PERSBORROW
	        				+ " and a.potenttype=" + Constants.PERSONALBORROW + " and a.userId not in (:userIds)";
	        		} else {
	        			hsql = hsql + " and a.sharetype=" + Constants.SHARETYPE_DEPTBORROW 
	        				+ " and a.potenttype=" + Constants.DEPTBORROW + " and a.userId not in (:userIds)";
	        		}	
	            	
	                Query query = s.createQuery(hsql);
	                query.setParameter("docResourceId", docResourceId) ;
	                query.setParameterList("userIds", userid) ;
	                query.executeUpdate() ;
	                return null;
	            }
	        });
	    }
	
	/**
	 * 删除不在userid中的对象
	 * @param docResourceId
	 * @param userid
	 * @param isMine
	******/
	public void deletePersonalShare(final Long docResourceId, final List<Long> userid, final boolean isMine)
	    {
	        this.getHibernateTemplate().execute(new HibernateCallback()
	        {
	            public Object doInHibernate(Session s) throws HibernateException, SQLException
	            {
	        		String hsql = "delete from "+ DocAcl.class.getName()+" as a where a.docResourceId=:docResourceId";
	        		if (isMine) {
	        			hsql = hsql + " and a.sharetype=" + Constants.SHARETYPE_PERSSHARE
	        				+ " and a.potenttype=" + Constants.PERSONALSHARE + " and a.userId not in (:userIds)";
	        		} else {
	        			hsql = hsql + " and a.sharetype=" + Constants.SHARETYPE_DEPTSHARE
	        				 + " and a.userId not in (:userIds)" ;
	        		}	
	            	
	                Query query = s.createQuery(hsql);
	                query.setParameter("docResourceId", docResourceId) ;
	                query.setParameterList("userIds", userid) ;
	                query.executeUpdate() ;
	                return null;
	            }
	        });
	    }
	

	
    /**
	 * 执行DetachedCriteria
	 * 
	 * @param detachedCriteria
	 * @param firstResult
	 *            -1表示不限制
	 * @param maxResults
	 *            -1表示不限制
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List executeCriteria(final DetachedCriteria detachedCriteria, final int firstResult, final int maxResults) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Criteria criteria = detachedCriteria.getExecutableCriteria(session);
				if(firstResult > -1){
					criteria.setFirstResult(firstResult);
				}
				
				if(maxResults > -1){
					criteria.setMaxResults(maxResults);
				}
				
				List items = criteria.list();

				return items;
			}
		}, true);
	}
	
	/**
	 * 新建文档全文检索入库时，读取入库所需的权限信息
	 * @param logicalPath	当前保存文档的逻辑路径
	 */
	@SuppressWarnings("unchecked")
	public Map<Long, String> getAclMap4Index(String logicalPath) {
		Long folderId = this.getSelectedAclFolderId(logicalPath);
		String hql = "select userId, userType, potenttype from DocAcl where docResourceId = :folderId and potenttype in (:potenttypes)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("folderId", folderId);
		params.put("potenttypes", Constants.aclLevels4Index);
		List<Object[]> arrList = this.find(hql, -1, -1, params);
		
		Map<Long, String> ret = new HashMap<Long, String>();
		if(CollectionUtils.isNotEmpty(arrList)) {
			for(Object[] arr : arrList) {
				ret.put((Long)arr[0], (String)arr[1]);
			}
		}
		return ret;
	}
	
	/**
	 * 当需要递归查询，决定使用哪一级文档夹权限列表时，<br>
	 * 由最下级到最顶层文件夹，定位到第一个拥有自身权限设置的文件夹并获取其ID<br>
	 * @param logicalPath	当前文件的逻辑路径
	 */
	@SuppressWarnings("unchecked")
	public Long getSelectedAclFolderId(String logicalPath) {
//		List<Long> ids = FormBizConfigUtils.parseStr2Ids(logicalPath.substring(0, logicalPath.lastIndexOf(".")), "\\.");
		List<Long> ids = null;
		if(logicalPath.lastIndexOf(".") != -1){
			ids = FormBizConfigUtils.parseStr2Ids(logicalPath.substring(0, logicalPath.lastIndexOf(".")), "\\.");
		}else{
			ids = new ArrayList<Long>();
			ids.add(Long.parseLong(logicalPath));
		}
		Collections.reverse(ids);
		
		String hql = "select a.docResourceId, count(a.docResourceId) from DocResource d, DocAcl a where d.id=a.docResourceId and d.id in (:ids) group by a.docResourceId";
		List<Object[]> objs = this.find(hql, -1, -1, FormBizConfigUtils.newHashMap("ids", ids));
		Map<Long, Integer> map = new HashMap<Long, Integer>();
		for(Object[] arr : objs) {
			map.put((Long)arr[0], (Integer)arr[1]);
		}
		
		Long result = null;
		for(Long drId : ids) {
			if(map.get(drId) != null && map.get(drId) > 0) {
				result = drId;
				break;
			}
		}
		return result;
	}
}
