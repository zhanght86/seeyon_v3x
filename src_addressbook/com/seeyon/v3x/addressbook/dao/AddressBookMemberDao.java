/**
 * $Id: AddressBookMemberDao.java,v 1.17 2010/07/28 07:11:56 renhy Exp $
 `* 
 * Licensed to the UFIDA
 */
package com.seeyon.v3x.addressbook.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.addressbook.domain.AddressBookMember;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * <p/> Title: 外部联系人<数据访问对象>
 * </p>
 * <p/> Description: 外部联系人<数据访问对象>
 * </p>
 * <p/> Date: 2007-5-25
 * </p>
 * 
 * @author paul(qdlake@gmail.com)
 * @see com.seeyon.v3x.addressbook.domain.AddressBookMember
 */
public class AddressBookMemberDao extends BaseHibernateDao<AddressBookMember> {
	
	private transient static final Log LOG = LogFactory
	.getLog(AddressBookMemberDao.class);

	/**
	 * 查出该用户创建的所有外部联系人
	 * 
	 * @param creatorId
	 *            用户ID
	 * @return 外部联系人列表
	 */
	public List findMembersByCreatorId(final Long creatorId) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
			throws HibernateException {
		StringBuffer sHql = new StringBuffer();
		sHql.append("select mem from com.seeyon.v3x.addressbook.domain.AddressBookMember mem");
		sHql.append(" where mem.creatorId = :creator_id order by mem.createdTime");
		Query query = session.createQuery(sHql.toString());
//		query.setLong(0, creatorId);
//		query.setParameter("creatorId", creatorId, Hibernate.LONG);
		query.setLong("creator_id", creatorId);
		return query.list();
			}
		});
//		List<AddressBookMember> memberList = null;
//		StringBuffer hql = null;
//	
//		hql = new StringBuffer();
//		hql.append(" from com.seeyon.v3x.addressbook.domain.AddressBookMember as mem ");
//		hql.append(" where mem.creatorId = :cid ");
		
//		Object[] params = new Object[1];
//		params[0] = creatorId;
//		memberList = this.find(hql.toString(), params);
//		memberList = this.getHibernateTemplate().find(hql.toString(), creatorId);
//		memberList = this.getHibernateTemplate().findByNamedParam(hql.toString(), "cid", creatorId);
//		memberList = this.find(hql.toString(), null);
		
//		Criteria criteria = getSession().createCriteria(AddressBookMember.class);
//		criteria.add(Expression.eq("creatorId",creatorId));
//		criteria.addOrder(Order.desc("createdTime"));
//		memberList = criteria.list();
//		
//		if ((memberList != null) && (!memberList.isEmpty())) {
//			return memberList;
//		}
//		
//		return null;
	}
	
	/**
	 * 删除外部联系人
	 * @param memberIds 外部联系人列表
	 */
	public void deleteMembersByIds(final List<Long> memberIds) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete AddressBookMember");
				sHql.append(" where id in (:memberIds)");
				Query query = session.createQuery(sHql.toString());
				query.setParameterList("memberIds", memberIds);
				return query.executeUpdate();
			}
		});
	}
	
	public List findMembersByTeamId(final Long teamId){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Long userId = CurrentUser.get().getId();
				StringBuffer sHql = new StringBuffer();
				sHql.append("select mem from com.seeyon.v3x.addressbook.domain.AddressBookMember mem");
				sHql.append(" where mem.category = :teamId and mem.creatorId=:userId");
				Query query = session.createQuery(sHql.toString());
				query.setLong("teamId", teamId);
				query.setLong("userId", userId);
				return query.list();
			}
		});
	}
	
	/**
	 * 按姓名查找员工
	 */
	public List findOrgMembersByName(final String name){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("select orgMem from com.seeyon.v3x.organization.domain.V3xOrgMember orgMem");
				sHql.append(" where orgMem.name like :name");
				Query query = session.createQuery(sHql.toString());
				query.setString("name", "%"+name+"%");
				return query.list();
			}
		});
	}
	
	/**
	 * 按姓名查找外部联系人
	 */
	public List findMemberByName(final String name){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Long userId = CurrentUser.get().getId();
				StringBuffer sHql = new StringBuffer();
				sHql.append("select mem from com.seeyon.v3x.addressbook.domain.AddressBookMember mem");
				sHql.append(" where mem.creatorId=:userId and mem.name like :name ");
				Query query = session.createQuery(sHql.toString());
				query.setLong("userId", userId);
				query.setString("name", "%"+name+"%");
				return query.list();
			}
		});
	}
	
	/**
	 * 按手机号码查找外部联系人
	 */
	public List findMemberByTel(final String tel){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Long userId = CurrentUser.get().getId();
				StringBuffer sHql = new StringBuffer();
				sHql.append("select mem from com.seeyon.v3x.addressbook.domain.AddressBookMember mem");
				sHql.append(" where mem.creatorId=:userId and mem.mobilePhone like :tel ");
				Query query = session.createQuery(sHql.toString());
				query.setLong("userId", userId);
				query.setString("tel", "%"+tel+"%");
				return query.list();
			}
		});
	}
	
	/**
	 * 按职务级别查找员工
	 */
	public List findOrgMemberByLevelName(final String levelName){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("select orgMem from com.seeyon.v3x.organization.domain.V3xOrgMember orgMem");
				sHql.append(" ,com.seeyon.v3x.organization.domain.V3xOrgLevel orgLevel");
				sHql.append(" where orgMem.orgLevelId = orgLevel.id");
				sHql.append(" and orgLevel.name like :levelName");
				Query query = session.createQuery(sHql.toString());
				query.setString("levelName", "%"+levelName+"%");
				return query.list();
			}
		});
	}
	
	/**
	 * 按职务级别查找外部联系人
	 */
	public List findMemberByLevelName(final String levelName){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Long userId = CurrentUser.get().getId();
				StringBuffer sHql = new StringBuffer();
				sHql.append("select mem from com.seeyon.v3x.addressbook.domain.AddressBookMember mem");
				sHql.append(" where mem.creatorId = :userId");
				if(Strings.isNotBlank(levelName)){
					sHql.append(" and mem.companyLevel like :levelName");
				}
				Query query = session.createQuery(sHql.toString());
				query.setLong("userId", userId);
				if(Strings.isNotBlank(levelName)){
					query.setString("levelName", "%"+SQLWildcardUtil.escape(levelName)+"%");
				}
				return query.list();
			}
		});
	}
	
	/**
	 * 判断是否有相同的邮件地址
	 */
//	public boolean hasSameMail(String mail, String memberId) {
//		List<AddressBookMember> memberList = null;
//		Criteria criteria = getSession().createCriteria(AddressBookMember.class);
//		criteria.add(Expression.eq("email", mail)).add(Expression.eq("id", Long.parseLong(memberId)));
//		memberList = criteria.list();
//		if (null != memberList && !memberList.isEmpty()){
//					return true;
//		}
//		return false;
//	}
	
	public boolean hasSameMail(String mail, String memberId) {
		Session session = super.getSession();
		List<AddressBookMember> memberList = null;
		try{
			Criteria criteria = session.createCriteria(AddressBookMember.class);
			criteria.add(Expression.eq("email", mail.trim()));
			memberList = criteria.list();
		}catch(Exception ex){
			LOG.error("" , ex);
		}finally{
			super.releaseSession(session);
		}
		if(null != memberList && !memberList.isEmpty()){
			//if(memberList.get(0).getId() != Long.parseLong(memberId))
			if(memberList.get(0).getCreatorId() != Long.parseLong(memberId))
				return true;
		}
		return false;
	}
    /**
     * 按姓名和所属组查找外部联系人
     */
    public List findMemberByNameAndTeam(final String Name,final Long categoryId,final Long createrId){
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException {
                StringBuffer sHql = new StringBuffer();
                sHql.append("select mem from com.seeyon.v3x.addressbook.domain.AddressBookMember mem");
                sHql.append(" where mem.name = :userName");
                sHql.append(" and mem.category =:categoryId");
                sHql.append(" and mem.creatorId =:creatorId");
                Query query = session.createQuery(sHql.toString());
                query.setString("userName", Name);
                query.setLong("categoryId", categoryId);
                query.setLong("creatorId", createrId);
                return query.list();
            }
        });
    }
}
