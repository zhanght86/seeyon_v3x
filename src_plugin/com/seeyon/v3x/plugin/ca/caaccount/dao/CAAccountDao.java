package com.seeyon.v3x.plugin.ca.caaccount.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.milyn.util.CollectionsUtil;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.login.principal.domain.JetspeedPrincipal;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.plugin.ca.caaccount.domain.CAAccount;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

public class CAAccountDao extends BaseHibernateDao<CAAccount> {
    
    @SuppressWarnings("unchecked")
    public List<CAAccount> findAllByPage() {
        String hsql = "from CAAccount";
        Map<String, Object> namedParameterMap = null;
        Object[] indexParameter = null;
        return super.find(hsql, namedParameterMap, indexParameter);
    }
    
    @SuppressWarnings("unchecked")
    public CAAccount findByMemberId(long memberId) {
        CAAccount returnObj = null;
        String hsql = "from CAAccount as caa where caa.memberId=?";
        List<CAAccount> caAccountList = super.find(hsql, memberId);
        if(caAccountList == null || caAccountList.isEmpty()){
            this.logger.warn("caAccountList is empty, memberId=" + memberId + ",method:findByMemberId(long memberId)");
        } else if(caAccountList.size() > 1){
            this.logger.error("caAccountList size > 1, memberId=" + memberId + ",method:findByMemberId(long memberId)");
        } else {
            returnObj = caAccountList.get(0);
        }
        return returnObj;
    }
    
    @SuppressWarnings("unchecked")
    public CAAccount findByKeyNum(String keyNum) {
        CAAccount returnObj = null;
        String hsql = "from CAAccount as caa where caa.keyNum=?";
        List<CAAccount> caAccountList = super.find(hsql, keyNum);
        if(caAccountList == null || caAccountList.isEmpty()){
            this.logger.warn("caAccountList is empty, keyNum=" + keyNum + ",method:findByKeyNum(String keyNum)");
        } else if(caAccountList.size() > 1){
            this.logger.error("caAccountList size > 1, keyNum=" + keyNum + ",method:findByKeyNum(String keyNum)");
        } else {
            returnObj = caAccountList.get(0);
        }
        return returnObj;
    }
    
    public boolean deleteByMemberIds(final long[] memberIds){
        return (Boolean) this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                boolean result = false;
                if(memberIds == null || memberIds.length == 0){
                    return result;
                }
                StringBuffer sbf = new StringBuffer();
                sbf.append("delete from v3x_ca_account where ");
                sbf.append("member_id in (");
                for(int i = 0; i < memberIds.length; i++){
                    if(i == memberIds.length - 1){
                        sbf.append("?");
                    } else {
                        sbf.append("?,");
                    }
                }
                sbf.append(")");
                PreparedStatement ps = null;
                Connection con = session.connection();
                try {
                    ps = con.prepareStatement(sbf.toString());
                    for(int i = 0; i < memberIds.length; i++){
                        ps.setLong(i + 1, memberIds[i]);
                    }
                    if(ps.executeUpdate() > 0){
                        result = true;
                    } else {
                        result = false;
                    }
                } catch(SQLException e) {
                    logger.error("error when find deleteByMemberIds caused by:" + e);
                } finally {
                    try {
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
                return result;
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<CAAccount> findByMemberIds(String condition, String  value, Long[] systemAndAuditMemberId) {
    	Map<String, Object> namedParameterMap = new HashMap<String, Object>();
    	StringBuffer hsql = new StringBuffer();
    	hsql.append("from CAAccount caa where caa.memberId in");
        if(condition.equals("loginName")){
        	hsql.append(" ( select sp.entityId from "+JetspeedPrincipal.class.getName()+" sp where sp.fullPath like :textfield ) ");
        	namedParameterMap.put("textfield","%/user/%"+value+"%");
        }else  if(condition.equals("name")){
        	hsql.append(" ( select id from "+V3xOrgMember.class.getName()+" v3xmember where v3xmember.name like :textfield ) ");
        	namedParameterMap.put("textfield", "%"+SQLWildcardUtil.escape(value)+"%");
        }
        
        if(systemAndAuditMemberId != null){
    		hsql.append(" or  caa.memberId in (:systemAndAuditMemberId)");
    		namedParameterMap.put("systemAndAuditMemberId", systemAndAuditMemberId);
    	}
        Object[] indexParameter = null;
        return  super.find(hsql.toString(), namedParameterMap, indexParameter);
    }
    
    @SuppressWarnings("unchecked")
    public List<CAAccount> findByKeyNumFuzzily(String keyNum) {
        if(keyNum == null || keyNum.trim().length() == 0){
            return findAllByPage();
        }
        String hsql = "from CAAccount caa where caa.keyNum like :keyNum";
        Map<String, Object> namedParameterMap = new HashMap<String, Object>();
        namedParameterMap.put("keyNum",  "%" + keyNum + "%");
        Object[] indexParameter = null;
        return super.find(hsql, namedParameterMap, indexParameter);
    }
}