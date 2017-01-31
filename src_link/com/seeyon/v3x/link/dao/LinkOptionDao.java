package com.seeyon.v3x.link.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.link.domain.LinkOption;

public class LinkOptionDao extends BaseHibernateDao<LinkOption> {
    @SuppressWarnings("unchecked")
	public List<LinkOption> getLinkOptionBySystemId(long linkSystemId){
		String hsql="from LinkOption as link where link.linkSystemId=? order by link.orderNum asc";
		List<LinkOption> list = super.find(hsql, linkSystemId);
		return list;
	}
	
    public LinkOption findLinkOptionBy(final long linkSystemId, final String paramName){
	    String hsql = "from LinkOption as link where link.linkSystemId=:linkSystemId and link.paramName=:paramName";
	    Map<String, Object> namedParameterMap = new HashMap<String, Object>();
	    namedParameterMap.put("linkSystemId", linkSystemId);
	    namedParameterMap.put("paramName", paramName);
	    Object[] indexParameter = null;
        return (LinkOption)super.findUnique(hsql, namedParameterMap, indexParameter);
    }
}
