package com.seeyon.v3x.hr.dao;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.Assess;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.EduExperience;
import com.seeyon.v3x.hr.domain.PostChange;
import com.seeyon.v3x.hr.domain.Relationship;
import com.seeyon.v3x.hr.domain.RewardsAndPunishment;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.domain.WorkRecord;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;


public class StaffInfoDao extends BaseHibernateDao<StaffInfo>{
	private transient static final Log LOG = LogFactory
	.getLog(StaffInfoDao.class);
	/**
	 * 方法描述：根据人员ID查询该人员基本信息
	 * 
	 * @return StaffInsfo
	 * @throws Exception
	 *
	 */
	public StaffInfo getStaffInfoById(long staffid)throws Exception{
		Session session = super.getSession();
		StaffInfo staffInfo = new StaffInfo();
		try{
			String hql = "From StaffInfo where org_member_id = :staffid ";
			Query query = session.createQuery(hql).setLong("staffid", staffid);
			staffInfo = (StaffInfo)query.uniqueResult();
//			年龄从组织模型的生日来取出
			/*if(null!=staffInfo && null!=staffInfo.getBirthday()){
			    staffInfo.setAgeByBirthday(staffInfo.getBirthday());
			}*/

//		if(null!=staffInfo && null!=staffInfo.getWork_starting_date()){
//		    staffInfo.setWorking_timeByWork_starting_date(staffInfo.getWork_starting_date());
//		}
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return staffInfo;
	}
	public Map<Long, StaffInfo> getStaffInfos(List<Long> staffidList)throws Exception
	{
		Map<Long, StaffInfo> result = new HashMap<Long, StaffInfo> ();
		StringBuffer hql = new StringBuffer("from StaffInfo where 1=2");
		for(Long id:staffidList)
		{
			hql.append(" or org_member_id=?");
		}
		List<StaffInfo> list = getHibernateTemplate().find(hql.toString(),staffidList.toArray());
		if(list!=null)
		{
			for(StaffInfo info:list)
			{
				result.put(info.getOrg_member_id(),info);
			}
		}
		return result;
	}
	
//返回应是List<Long>
//	public Long getMemberIdByName(String name)throws Exception{
//		StringBuffer strbuf = new StringBuffer();
//        strbuf.append("select org_member_id from StaffInfo where ");
//        strbuf.append("name like '%");
//        strbuf.append(name);
//        strbuf.append("%' ");
//        Query query = super.getSession().createQuery(strbuf.toString());
//        Long id = (Long)query.uniqueResult();
//        return id;
//	}
	
	/**
	 * 根据人员ID查询该人员联系信息
	 * 
	 */
	public ContactInfo getContactInfoByStafferId(long staffid)throws Exception{
		Session session = super.getSession();
		ContactInfo contactInfo = null;
		try{
			String hql = "from ContactInfo where member_id = :staffid";
			Query query = session.createQuery(hql).setLong("staffid", staffid);
			//contactInfo = (ContactInfo)query.uniqueResult();
			// 由于此前个人信息设置中没有添加提交防护，可能导致用户多次重复提交而添加多条重复记录，而使得以上方法出现异常
			// 改为如果出现多条重复记录时，只取第一条记录，并为解决历史遗留问题，删除多余的几条记录，同时添加日志进行跟踪
			// modified by Meng Yang at 2009-06-27
			List<ContactInfo> list = query.list();
			if(list!=null && list.size()>0) {
				contactInfo = list.get(0);
			}
			
			if(list!=null && list.size()>1) {
				list.remove(0);
				List<Long> repeatedRecords = new ArrayList<Long>();
				for(ContactInfo info : list) {
					repeatedRecords.add(info.getId());
				}
				String hqlDelete = "delete from ContactInfo as c where c.id in (:ids)";
				Query queryDelete = session.createQuery(hqlDelete).setParameterList("ids", repeatedRecords);
				int i = queryDelete.executeUpdate();
				LOG.warn("当前员工存在" + i + "条多余重复通讯记录，已被删除");
			} 
			
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return contactInfo;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Long, ContactInfo> getAllContactInfo() throws Exception {
		Map<Long, ContactInfo> map = new HashMap<Long, ContactInfo>();
		String hql = " select c.member_id, c.blog, c.website, c.postalcode, c.address from " + ContactInfo.class.getName() + " c ";
		List<Object[]> list = this.find(hql, -1, -1, null);
		if (CollectionUtils.isNotEmpty(list)) {
			ContactInfo contactInfo = null;
			for (Object[] objet : list) {
				contactInfo = new ContactInfo();
				contactInfo.setMember_id((Long) objet[0]);
				contactInfo.setBlog((String) objet[1]);
				contactInfo.setWebsite((String) objet[2]);
				contactInfo.setPostalcode((String) objet[3]);
				contactInfo.setAddress((String) objet[4]);
				map.put(contactInfo.getMember_id(), contactInfo);
			}
		}
		return map;
	}
	
	/**
	 * 根据人员ID查询该人员家庭成员与社会关系信息
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Relationship> getRelationshipByStafferId(final long staffid)throws Exception{
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {				
				String hql = "From Relationship where member_id = :staffid";
				Query query = session.createQuery(hql).setLong("staffid", staffid);
				List<Relationship> relationship = query.list();
				return relationship;
			}
		});
//修改前		
//		List<Relationship> relationship = getHibernateTemplate().find("From Relationship where member_id = "+staffid);
//		return relationship;
	}
	
	/**
	 * 根据人员ID查询该人员工作履历
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<WorkRecord> getWorkRecordByStafferId(final long staffid)throws Exception{
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {				
				String hql = "From WorkRecord where member_id = :staffid";
				Query query = session.createQuery(hql).setLong("staffid", staffid);
				List<WorkRecord> workRecord = query.list();
				return workRecord;
			}
		});
//		修改前		
//		List<WorkRecord> workRecord = getHibernateTemplate().find("From WorkRecord where member_id = "+staffid);
//		return workRecord;
	}
	
	/**
	 * 根据人员ID查询该人员教育培训经历
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<EduExperience> getEduExperienceByStafferId(final long staffid)throws Exception{
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {				
				String hql = "From EduExperience where member_id = :staffid";
				Query query = session.createQuery(hql).setLong("staffid", staffid);
				List<EduExperience> eduExperience = query.list();
				return eduExperience;
			}
		});
//		List<EduExperience> eduExperience = getHibernateTemplate().find("From EduExperience where member_id = "+staffid);
//		return eduExperience;
	}
	
	/**
	 * 根据人员ID查询该人员职务变动
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<PostChange> getPostChangeByStafferId(final long staffid)throws Exception{
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {				
				String hql = "From PostChange where member_id = :staffid";
				Query query = session.createQuery(hql).setLong("staffid", staffid);
				List<PostChange> postChange = query.list();
				return postChange;
			}
		});
//		List<PostChange> postChange = getHibernateTemplate().find("From PostChange where member_id = "+staffid);
//		return postChange;
	}
	
	/**
	 * 根据人员ID查询该人员考核情况
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Assess> getAssessByStafferId(final long staffid)throws Exception{
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {				
				String hql = "From Assess where member_id = :staffid";
				Query query = session.createQuery(hql).setLong("staffid", staffid);
				List<Assess> assess = query.list();
				return assess;
			}
		});
//		List<Assess> assess = getHibernateTemplate().find("From Assess where member_id = "+staffid);
//		return assess;
	}
	
	/**
	 * 根据人员ID查询该人员奖惩档案
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<RewardsAndPunishment> getRewardsAndPunishmentByStafferId(final long staffid)throws Exception{
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {				
				String hql = "From RewardsAndPunishment where member_id = :staffid";
				Query query = session.createQuery(hql).setLong("staffid", staffid);
				List<RewardsAndPunishment> rewardsAndPunishment = query.list();
				return rewardsAndPunishment;
			}
		});
//		List<RewardsAndPunishment> rewardsAndPunishment = getHibernateTemplate().find("From RewardsAndPunishment where member_id = "+staffid);
//		return rewardsAndPunishment;
	}
	
	/**
	 * 根据id查询一条家庭成员与社会关系信息记录
	 * 
	 */
	public Relationship getRelationshipById(long id)throws Exception{
		Session session = super.getSession();
		Relationship relationship = new Relationship();
		try{
			String hql = "From Relationship where id = :id";
			Query query = session.createQuery(hql).setLong("id",id);
			relationship = (Relationship)query.uniqueResult();
		}catch(Exception e){
		    throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return relationship;
	}
	
	/**
	 * 根据id查询一条工作履历记录
	 * 
	 */
	public WorkRecord getWorkRecordById(long id)throws Exception{
		Session session = super.getSession();
		WorkRecord workRecord = new WorkRecord();
		try{
			String hql = "From WorkRecord where id = :id";
			Query query = session.createQuery(hql).setLong("id",id);
		
			workRecord =  (WorkRecord)query.uniqueResult();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return workRecord;
	}
	
	/**
	 * 根据id查询一条教育培训经历记录
	 * 
	 */
	public EduExperience getEduExperienceById(long id)throws Exception{
		Session session = super.getSession();
		EduExperience eduExperience = new EduExperience();
		try{
			String hql = "From EduExperience where id = :id";
			Query query = session.createQuery(hql).setLong("id",id);
	
			eduExperience =(EduExperience)query.uniqueResult();
		}catch(Exception e){
		    throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return eduExperience;
	}
	
	/**
	 * 根据id查询一条职务变动记录
	 * 
	 */
	public PostChange getPostChangeById(long id)throws Exception{
		Session session = super.getSession();
		PostChange postChange = new PostChange();
		try{
			String hql = "From PostChange where id = :id";
			Query query = session.createQuery(hql).setLong("id",id);
	
			postChange =(PostChange)query.uniqueResult();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return postChange;
	}
	
	/**
	 * 根据id查询一条考核情况记录
	 * 
	 */
	public Assess getAssessById(long id)throws Exception{
		Session session = super.getSession();
		Assess assess = new Assess();
		try{
			String hql = "From Assess where id = :id";
			Query query = session.createQuery(hql).setLong("id",id);
	
			assess = (Assess)query.uniqueResult();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return assess;
	}
	
	/**
	 * 根据id查询一条奖惩档案记录
	 * 
	 */
	public RewardsAndPunishment getRewardsAndPunishmentById(long id)throws Exception{
		Session session = super.getSession();
		RewardsAndPunishment rewardsAndPunishment = new RewardsAndPunishment();
		try{
			String hql = "From RewardsAndPunishment where id = :id";
			Query query = session.createQuery(hql).setLong("id",id);
	
			rewardsAndPunishment = (RewardsAndPunishment)query.uniqueResult();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return rewardsAndPunishment;
	}
	
	
	/**
	 * 删除某人员的基本信息
	 * 
	 */
	public void deleteStaffInfo(long staffid)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete StaffInfo where org_member_id = :staffid";
			session.createQuery(hql).setLong("staffid",staffid).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	/**
	 * 删除某人员的联系信息
	 * 
	 */
	public void deleteContactInfoByStaffId(long staffid)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete ContactInfo where member_id = :staffid";
			session.createQuery(hql).setLong("staffid",staffid).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	/**
	 * 删除某人员的家庭成员与社会关系信息
	 * 
	 */
	public void deleteRelationshipByStaffId(long staffid)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete Relationship where member_id = :staffid";
			session.createQuery(hql).setLong("staffid",staffid).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	
	/**
	 * 删除某人员的工作履历
	 * 
	 */
	public void deleteWorkRecordByStaffId(long staffid)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete WorkRecord where member_id = :staffid";
			session.createQuery(hql).setLong("staffid",staffid).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	/**
	 * 删除某人员的教育培训经历
	 * 
	 */
	public void deleteEduExperienceByStaffId(long staffid)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete EduExperience where member_id = :staffid";
		    session.createQuery(hql).setLong("staffid",staffid).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	
	/**
	 * 删除某人员的职务变动记录
	 * 
	 */
	public void deletePostChangeByStaffId(long staffid)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete PostChange where member_id = :staffid";
		    session.createQuery(hql).setLong("staffid",staffid).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	
	/**
	 * 删除某人员的考核情况记录
	 * 
	 */
	public void deleteAssessByStaffId(long staffid)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete Assess where member_id = :staffid";
		    session.createQuery(hql).setLong("staffid",staffid).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	
	/**
	 * 删除某人员的奖惩档案
	 * 
	 */
	public void deleteRewardsAndPunishmentByStaffId(long staffid)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete RewardsAndPunishment where member_id = :staffid";
		    session.createQuery(hql).setLong("staffid",staffid).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	
	
	
	
	
	/**
	 * 删除一条家庭成员与社会关系信息
	 * 
	 */
	public void deleteRelationship(long id)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete Relationship where id = :id";
		    session.createQuery(hql).setLong("id",id).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	
	/**
	 * 删除一条工作履历
	 * 
	 */
	public void deleteWorkRecord(long id)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete WorkRecord where id = :id";
		    session.createQuery(hql).setLong("id",id).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	/**
	 * 删除一条教育培训经历
	 * 
	 */
	public void deleteEduExperience(long id)throws Exception{
		Session session = super.getSession();
			try{
			String hql = "delete EduExperience where id = :id";
		    session.createQuery(hql).setLong("id",id).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	
	/**
	 * 删除一条职务变动记录
	 * 
	 */
	public void deletePostChange(long id)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete PostChange where id = :id";
		    session.createQuery(hql).setLong("id",id).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	
	/**
	 * 删除一条考核情况记录
	 * 
	 */
	public void deleteAssess(long id)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete Assess where id = :id";
		    session.createQuery(hql).setLong("id",id).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	
	/**
	 * 删除一条奖惩档案
	 * 
	 */
	public void deleteRewardsAndPunishment(long id)throws Exception{
		Session session = super.getSession();
		try{
			String hql = "delete RewardsAndPunishment where id = :id";
		    session.createQuery(hql).setLong("id",id).executeUpdate();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	}
	
	/**
	 * 获取全部有效职员信息，辅助载入常驻内存
	 */
	public List<StaffInfo> getValidStaffInfos() {
		String hql = "select s from " + StaffInfo.class.getName() + " as s, " + 
		 			 V3xOrgMember.class.getName()+ " as m where s.org_member_id=m.id and m.isDeleted=false and m.enabled=true " +
		 			 "and m.isLoginable=true and m.isAssigned=true and m.state=?";
		Byte state = V3xOrgEntity.MEMBER_STATE_ONBOARD;
		return this.find(hql, state);
	}
	
}
