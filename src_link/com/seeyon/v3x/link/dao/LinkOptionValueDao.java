package com.seeyon.v3x.link.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.link.domain.LinkOption;
import com.seeyon.v3x.link.domain.LinkOptionValue;

public class LinkOptionValueDao extends BaseHibernateDao<LinkOptionValue> {
    private static final Log logger = LogFactory.getLog(LinkOptionValueDao.class);
    
	public LinkOptionValue findOptionValues(long userId,long linkOptionId){
		String hsql="from LinkOptionValue as link where link.userId=? and link.linkOptionId=?";
		List list=super.find(hsql, userId,linkOptionId);
		if(list != null && list.isEmpty()==false){
			LinkOptionValue value=(LinkOptionValue)list.get(0);
			return value;
		}
		return null;
	}
	
	public List<LinkOptionValue> getOptionValues(long userId,List<Long> optionId){
		StringBuffer buffer=new StringBuffer();
		buffer.append("from LinkOptionValue as link where link.userId=:userId and link.linkOptionId in ( :ids)");
		/**
		for(int i=0;i<optionId.size();i++){
			if(i != optionId.size()-1){
				buffer.append(optionId.get(i));
				buffer.append(",");
			}else{
				buffer.append(optionId.get(i));
				buffer.append(")");
			}
		}
		*/
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", optionId);
		map.put("userId", userId) ;
		List<LinkOptionValue> list=super.find(buffer.toString(),-1,-1, map);
		return list;
	}
	
	public LinkOptionValue findOptionValues(final long linkSystemId, final String paramName, final long userId){
	    return (LinkOptionValue) this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                StringBuffer sbf = new StringBuffer();
                sbf.append("select lov.id, lov.value, lov.user_id, lov.link_option_id from v3x_link_option_value lov ");
                sbf.append("inner join v3x_link_option lo on lov.link_option_id = lo.id ");
                sbf.append("where lo.link_system_id=? and lo.param_name=? and lov.user_id=? ");
                PreparedStatement ps = null;
                ResultSet rs = null;
                Connection con = session.connection();
                try {
                    ps = con.prepareStatement(sbf.toString());
                    ps.setLong(1, linkSystemId);
                    ps.setString(2, paramName);
                    ps.setLong(3, userId);
                    rs = ps.executeQuery();
                    while(rs.next()){
                        LinkOptionValue lov = new LinkOptionValue();
                        lov.setId(rs.getLong(1));
                        lov.setValue(rs.getString(2));
                        lov.setUserId(rs.getLong(3));
                        lov.setLinkOptionId(rs.getLong(4));
                        return lov;
                    }
                } catch(SQLException e) {
                    logger.error("error when find LinkOptionValue caused by:" + e);
                } finally {
                    try {
                        if(rs != null){
                            rs.close();
                        }
                        if(ps != null){
                            ps.close();
                        }
                        if(con != null){
                            con.close();
                        }
                        if(session != null){
                            session.close();
                        }
                    } catch(SQLException e) {
                        logger.error("error when close connection, caused by:" + e);
                    }
                }
                return null;
            }
        });
    }
    
	@SuppressWarnings("unchecked")
	public List<Object[]> statisticsLinkOptionValue(final List<LinkOption> linkOptionList) {
        if(linkOptionList == null || linkOptionList.size() == 0){
            return null;
        }
        if(linkOptionList.get(0).getLinkOptionValue() == null || linkOptionList.get(0).getLinkOptionValue().isEmpty()){
            return null;
        }
        List<Long> linkIds = new ArrayList<Long>();
        StringBuffer hql = new StringBuffer();
        hql.append("select lov.userId,");
        for(LinkOption lo : linkOptionList){
        	linkIds.add(lo.getId());
            hql.append(" max(case lov.linkOptionId when " + lo.getId() + " then lov.value else '' end),");
        }
        hql.delete(hql.length() - 1, hql.length());
        hql.append(" from LinkOptionValue lov where lov.linkOptionId in (:linkIds) ");
        hql.append(" group by lov.userId");
        StringBuffer hql2 = new StringBuffer();
        hql2.append("select count(distinct lov.userId) from LinkOptionValue lov where lov.linkOptionId in (:linkIds)");
        Map<String, Object> namedParameterMap = new HashMap<String,Object>();
        namedParameterMap.put("linkIds", linkIds);
        return super.findWithCount(hql.toString(), hql2.toString(), namedParameterMap);
    }
	
	public void deleteParamValues(final List<Long> linkOptionIds, final List<Long> userIds) {
        StringBuffer hql = new StringBuffer();
        hql.append("delete from LinkOptionValue as a where a.linkOptionId in (:linkOptionIds) and a.userId in (:userIds)");
        Map<String, Object> nameParameters = new HashMap<String, Object>();
        nameParameters.put("linkOptionIds", linkOptionIds);
        nameParameters.put("userIds", userIds);
        this.bulkUpdate(hql.toString(), nameParameters);
    }
}
