package com.seeyon.v3x.edoc.dao;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.edoc.domain.EdocStat;
import com.seeyon.v3x.edoc.util.Constants;

public class EdocStatDao extends BaseHibernateDao<EdocStat> {
	
	public Hashtable getEdocStatResult(int edocType, String queryCondition, int groupType) {
		Hashtable<String,Integer> hashtable = new Hashtable<String,Integer>();
		String groupName = "deptId";
		if (groupType == Constants.EDOC_STAT_GROUPBY_DOCTYPE) {
			groupName = "docType";
		}
		StringBuffer sb = new StringBuffer("select count(*),es." + groupName + " from EdocStat es");
		sb.append(" where es.edocType=" + edocType);
		//sb.append(" and es.docType <> -1 ");
		if (queryCondition != null && !queryCondition.equals("") && !queryCondition.equals("null")) {
			sb.append(queryCondition);
		}
		sb.append(" group by es." + groupName);			
				
			List tempList = execute(sb.toString());
			
			if (tempList != null && tempList.size() > 0) {
				for (int i = 0; i < tempList.size(); i++) {
					Object[] objs = (Object[])tempList.get(i);
					if (objs != null) {
						String key = null;
						if (groupType == Constants.EDOC_STAT_GROUPBY_DEPT) {
							key = String.valueOf((Long)objs[1]);
						}
						else {
							key = (String)objs[1];
						}
						
						
						Integer value = (Integer)objs[0];
						if(null==key || "".equals(key)){
							key="nulldata";
						}
						hashtable.put(key, value);
					}
				}
			}		
		return hashtable;
	}
	
	public List<EdocStat> queryEdocStat(String hsql,Map objects) {		
		return super.find(hsql, objects);
	}
	
	public List<EdocStat> queryEdocStatAll(String hsql,Map objects) {		
		return super.find(hsql, -1, -1, objects);
	}
	
	public List execute(final String hsql){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return session.createQuery(hsql).list();
			}
		}, true);
	}
	
}
