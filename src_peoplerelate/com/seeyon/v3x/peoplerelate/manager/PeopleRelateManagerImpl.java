package com.seeyon.v3x.peoplerelate.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.dao.PeopleRelateDao;
import com.seeyon.v3x.peoplerelate.domain.PeopleRelate;
import com.seeyon.v3x.util.Strings;

/**
 * 关联人员业务类
 * 
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 */
public class PeopleRelateManagerImpl implements PeopleRelateManager
{

    private static final Log log = LogFactory.getLog(PeopleRelateManagerImpl.class);

    private PeopleRelateDao peoplerelateDao;

    private OrgManager orgManager;
    
    private StaffInfoManager staffInfoManager;
    
	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}

    public void setOrgManager(OrgManager orgManager)
    {
        this.orgManager = orgManager;
    }

    public PeopleRelateDao getPeoplerelateDao()
    {
        return peoplerelateDao;
    }

    public void setPeoplerelateDao(PeopleRelateDao peoplerelateDao)
    {
        this.peoplerelateDao = peoplerelateDao;
    }

    /**
     * 添加关联人员
     */
    @SuppressWarnings("unchecked")
    public void addPeopleRelate(PeopleRelate pr)
    {
        Long relateId = pr.getRelateMemberId();
        Long relatedId = pr.getRelatedMemberId();
        PeopleRelate relateMember = null;
        try
        {

            DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                    Restrictions.eq("relateMemberId", relatedId)).add(
                    Restrictions.eq("relatedMemberId", relateId));
            List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);
            if (l != null && l.size() != 0)
            {
                relateMember = l.get(0);
                relateMember.setRelateWsbs(PeopleRelate.wsbs_sure);
                peoplerelateDao.update(relateMember);
                // 将自己握手状态更新 已确认
                pr.setRelateWsbs(PeopleRelate.wsbs_sure);
            }

            peoplerelateDao.save(pr);

        }
        catch (Exception e)
        {
            log.error("保存关联人员失败", e);
        }
    }
    public void updatePeopleRelate(PeopleRelate pr) throws Exception {
        Long relateId = pr.getRelateMemberId();
        Long relatedId = pr.getRelatedMemberId();
        PeopleRelate relateMember = null;
        try
        {

            DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                    Restrictions.eq("relateMemberId", relateId)).add(
                    Restrictions.eq("relatedMemberId", relatedId));
            List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);

            if (l != null && l.size() != 0)
            {
                relateMember = l.get(0);
                relateMember.setOrderNum(pr.getOrderNum());
                peoplerelateDao.update(relateMember);
              
            }
        }
        catch (Exception e)
        {
            log.error("更新关联人员失败", e);
        }   	
    }
    // public void addPeopleRelated(PeopleRelate pr) {
    // Long relateId = pr.getRelateMemberId();
    // Long relatedId = pr.getRelatedMemberId();
    // PeopleRelate relateMember = null;
    // try {
    //            
    // DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class)
    // .add(Restrictions.eq("relateMemberId",
    // relatedId)).add(Restrictions.eq("relatedMemberId", relateId));
    // List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc,-1,-1);
    // if(l!=null&&l.size()!=0){
    // relateMember = l.get(0);
    // relateMember.setRelateWsbs(PeopleRelate.wsbs_sure);
    // peoplerelateDao.update(relateMember);
    // // 将自己握手状态更新 已确认
    // pr.setRelateWsbs(PeopleRelate.wsbs_sure);
    // }
    //            
    // peoplerelateDao.save(pr);
    //            
    // } catch (Exception e) {
    // log.error("保存关联人员失败", e);
    // }
    // }

    // 根据登录人和类型查询关联人员
    @SuppressWarnings("unchecked")
    public List<PeopleRelate> getPeopleRelateList(Long userId, int type) throws Exception
    {
        V3xOrgMember vm = null;

        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relatedMemberId", userId))
                .add(Restrictions.eq("relateType", type));
        List<PeopleRelate> list = peoplerelateDao.executeCriteria(dc, -1, -1);
        for (PeopleRelate peopleRelate : list)
        {
            vm = orgManager.getMemberById(new Long(peopleRelate.getRelateMemberId()));
            peopleRelate.setRelateMemberName(vm.getName());
            peopleRelate.setRelateMemberTel(vm.getTelNumber());
            Long deptId = vm.getOrgDepartmentId();
            V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
            if (dept != null)
            {
                peopleRelate.setRelateMemberDept(dept.getName());
            }
            Long postId = vm.getOrgPostId();
            V3xOrgPost post = orgManager.getPostById(postId);
            if (post != null)
            {
                peopleRelate.setRelateMemberPost(post.getName());
            }
        }
        return list;
    }

    // 取得关联人员是我的人员列表
    @SuppressWarnings("unchecked")
    public List<PeopleRelate> getPeopleRelateList(Long userId) throws Exception
    {

        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", userId)).addOrder(Order.asc("orderNum"));
        List<PeopleRelate> list = peoplerelateDao.executeCriteria(dc, -1, -1);
        return list;
    }
    
//  取得我的关联人员列表
    @SuppressWarnings("unchecked")
    public List<PeopleRelate> getPeopleRelatedList(Long userId) throws Exception
    {

        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relatedMemberId", userId)).addOrder(Order.asc("orderNum"));
        List<PeopleRelate> list = peoplerelateDao.executeCriteria(dc, -1, -1);
        return list;
    }

    // 根据类型判断是否有与登录人关联的人员
    @SuppressWarnings("unchecked")
    public boolean isRelateExist(Long relateMemberId, Long uid, int type) throws Exception
    {

        boolean b = false;
        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", relateMemberId)).add(
                Restrictions.eq("relatedMemberId", uid)).add(Restrictions.eq("relateType", type));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);
        if (l.size() != 0)
        {
            b = true;
        }
        return b;
    }

    /**
     * 是否有与我关联的人员
     */
    @SuppressWarnings("unchecked")
    public boolean isRelateExist(Long relateMemberId, Long uid) throws Exception
    {

        boolean b = false;
        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", relateMemberId)).add(
                Restrictions.eq("relatedMemberId", uid));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);
        if (l.size() != 0)
        {
            b = true;
        }
        return b;
    }

    public boolean isRelateExistNotConfreres(Long relateMemberId, Long uid, int type)
            throws Exception
    {
        boolean b = false;
        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", relateMemberId)).add(
                Restrictions.eq("relatedMemberId", uid)).add(Restrictions.eq("relateType", type));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);
        if (l.size() != 0)
        {
            b = true;
        }
        return b;
    }

    /**
     * 是否有与我关联的人员(未确认的)
     */
    @SuppressWarnings("unchecked")
    public boolean isRelateExistUnSure(Long relateMemberId, Long uid, int isSure) throws Exception
    {

        boolean b = false;
        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", relateMemberId)).add(
                Restrictions.eq("relatedMemberId", uid)).add(Restrictions.eq("relateWsbs", isSure));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);
        if (l.size() != 0)
        {
            b = true;
        }
        return b;
    }

    // by Yongzhang 2008-07-07 解决不再选择的关联人员删除问题
    @SuppressWarnings("deprecation")
    public void delRelateMembers(List deleteIds, Long relatedMemberId, int type) throws Exception
    {
        // by YongZhang 2008-09-25 把被设置为秘书的上级的人员不进行删除

        Map<Long, PeopleRelate> peopleMap = this.getAllRelateLongMap(relatedMemberId,
                RelationType.assistant.key());
        if (!deleteIds.isEmpty())
        {
            if (type == RelationType.leader.key())
            {
                peoplerelateDao.deletePeopleRelatebyRelateType(deleteIds, relatedMemberId,
                        RelationType.junior.key());
                peoplerelateDao.deletePeopleRelatebyRelateType(deleteIds, relatedMemberId,
                        RelationType.assistant.key());
            }
            else if (type == RelationType.junior.key())
            {
                List<PeopleRelate> peopleRelate = peoplerelateDao.getNotSelectedRelatebyTypeAndWS(
                        deleteIds, relatedMemberId, RelationType.leader.key());
                for (PeopleRelate relate : peopleRelate)
                {
                    if (peopleMap.get(relate.getRelatedMemberId()) == null)
                    {
                        peoplerelateDao.deletePeopleRelateByOne(relate.getRelateMemberId(),
                                relatedMemberId, RelationType.leader.key());
                    }
                }
                // if (peopleMap.isEmpty())
                // {
                // peoplerelateDao.deletePeopleRelatebyRelateType(deleteIds,
                // relatedMemberId,
                // RelationType.leader.key());
                // }

            }
            // else if (type == RelationType.assistant.key())
            // {
            // peoplerelateDao.deletePeopleRelatebyRelateType(deleteIds,
            // relatedMemberId,
            // RelationType.leader.key());
            // }
            peoplerelateDao.deletePeopleRelatebyType(deleteIds, relatedMemberId, type);
        }
        else
        {
            if (type == RelationType.leader.key())
            {
                peoplerelateDao.deletePeopleRelatebyTypeAndWS(relatedMemberId, RelationType.junior
                        .key(), PeopleRelate.wsbs_sure);
                peoplerelateDao.deletePeopleRelatebyTypeAndWS(relatedMemberId,
                        RelationType.assistant.key(), PeopleRelate.wsbs_sure);
            }
            else if (type == RelationType.junior.key())
            {
                List<PeopleRelate> peopleRelateList = getAllRelatedList(relatedMemberId,
                        RelationType.leader.key());
                for (PeopleRelate relate : peopleRelateList)
                {
                    if (peopleMap.get(relate.getRelatedMemberId()) == null)
                    {
                        peoplerelateDao.deletePeopleRelatebyTypeAndWS(relatedMemberId,
                                RelationType.leader.key(), PeopleRelate.wsbs_sure);
                    }
                }
                if (peopleRelateList.isEmpty())
                {
                    peoplerelateDao.deletePeopleRelatebyTypeAndWS(relatedMemberId,
                            RelationType.leader.key(), PeopleRelate.wsbs_sure);
                }
            }
            // else if (type == RelationType.assistant.key())
            // {
            // peoplerelateDao.deletePeopleRelatebyTypeAndWS(relatedMemberId,
            // RelationType.leader
            // .key(), PeopleRelate.wsbs_sure);
            // }
            peoplerelateDao.deleteRelatedbyType(relatedMemberId, type);
        }
    }

    /**
     * 当什么也不设置时删除其余人员，并将以这些人作为关联人的人员握手状态变为未确认
     */
    @SuppressWarnings( { "deprecation", "unchecked" })
    public void delRelateMembers(Long relatedMemberId) throws Exception
    {
        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relatedMemberId", relatedMemberId));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);
        PeopleRelate pr = null;
        Long relatedId = null;
        if (l != null && l.size() != 0)
        {
            pr = l.get(0);
            relatedId = pr.getRelateMemberId();
        }

        DetachedCriteria dc1 = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", relatedMemberId)).add(
                Restrictions.eq("relatedMemberId", relatedId));
        List<PeopleRelate> list = peoplerelateDao.executeCriteria(dc1, -1, -1);
        if (list != null && list.size() != 0)
        {
            pr = list.get(0);
            pr.setRelateWsbs(PeopleRelate.wsbs_unsure);
            peoplerelateDao.update(pr);
        }
        peoplerelateDao.bulkUpdate("delete from PeopleRelate p where p.relatedMemberId=?", null, relatedMemberId);

    }

    @SuppressWarnings("unchecked")
    public Map<RelationType, List<V3xOrgMember>> getAllRelateMembers(Long userId) throws Exception
    {
    	
        V3xOrgMember vm = null;
        List<V3xOrgMember> leaderList = new ArrayList<V3xOrgMember>();
        List<V3xOrgMember> assistantList = new ArrayList<V3xOrgMember>();
        List<V3xOrgMember> juniorList = new ArrayList<V3xOrgMember>();
        List<V3xOrgMember> confrereList = new ArrayList<V3xOrgMember>();
        //需要握手确认，才能认为有关系
        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relatedMemberId", userId)).add(
                        Restrictions.eq("relateWsbs", 1)).addOrder(Order.asc("orderNum"));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);

        Map<RelationType, List<V3xOrgMember>> relateMemberMap = new HashMap<RelationType, List<V3xOrgMember>>();
        for (PeopleRelate peopleRelate : l)
        {
            vm = orgManager.getMemberById(new Long(peopleRelate.getRelateMemberId()));
            if(vm == null){
            	continue;
            }
            if (peopleRelate.getRelateType() == RelationType.leader.key())
            {
                leaderList.add(vm);
            }
            else if (peopleRelate.getRelateType() == RelationType.assistant.key())
            {
                assistantList.add(vm);
            }
            else if (peopleRelate.getRelateType() == RelationType.junior.key())
            {
                juniorList.add(vm);
            }
            else if (peopleRelate.getRelateType() == RelationType.confrere.key())
            {
                confrereList.add(vm);
            }
        }
        relateMemberMap.put(RelationType.leader, leaderList);
        relateMemberMap.put(RelationType.assistant, assistantList);
        relateMemberMap.put(RelationType.junior, juniorList);
        relateMemberMap.put(RelationType.confrere, confrereList);
        return relateMemberMap;
    }
    
	public Map<RelationType, List<PeopleRelate>> getAllPeopleRelates(Long userId, boolean fromMore) throws Exception {
		return this.getAllPeopleRelates(userId, fromMore, null);
	}

	@SuppressWarnings("unchecked")
	public Map<RelationType, List<PeopleRelate>> getAllPeopleRelates(Long userId, boolean fromMore, String designated) throws Exception {
		String hql = "from PeopleRelate where relatedMemberId = :relatedMemberId ORDER BY orderNum ASC";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("relatedMemberId", userId);
		List<PeopleRelate> list = peoplerelateDao.find(hql, -1, -1, map);
		return getPeopleRelateMap(fromMore, list, designated);
	}

	@SuppressWarnings("unchecked")
	public Map<RelationType, List<PeopleRelate>> getAllPeopleRelates(Long userId, boolean fromMore, int size) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(Restrictions.eq("relatedMemberId", userId));
		List<PeopleRelate> list = peoplerelateDao.executeCriteria(dc, 0, size);
		return getPeopleRelateMap(fromMore, list, null);
	}
    
	private Map<RelationType, List<PeopleRelate>> getPeopleRelateMap(boolean fromMore, List<PeopleRelate> l, String designated) throws Exception {
		Map<RelationType, List<PeopleRelate>> relateMemberMap = new HashMap<RelationType, List<PeopleRelate>>();
		List<PeopleRelate> leaderList = new ArrayList<PeopleRelate>();
		List<PeopleRelate> assistantList = new ArrayList<PeopleRelate>();
		List<PeopleRelate> juniorList = new ArrayList<PeopleRelate>();
		List<PeopleRelate> confrereList = new ArrayList<PeopleRelate>();

		boolean containsLeader = true;
		boolean containsAssistant = true;
		boolean containsJunior = true;
		boolean containsConfrere = true;
		if (Strings.isNotBlank(designated)) {
			containsLeader = designated.contains(String.valueOf(RelationType.leader.key()));
			containsAssistant = designated.contains(String.valueOf(RelationType.assistant.key()));
			containsJunior = designated.contains(String.valueOf(RelationType.junior.key()));
			containsConfrere = designated.contains(String.valueOf(RelationType.confrere.key()));
		}

		for (PeopleRelate peopleRelate : l) {
			// 构造人员关联信息
			V3xOrgMember vm = orgManager.getMemberById(new Long(peopleRelate.getRelateMemberId()));
			if (vm != null && vm.getEnabled() && !vm.getIsDeleted()) {
				String relateMemberName = vm.getName();
				V3xOrgAccount account = orgManager.getAccountById(vm.getOrgAccountId());
				if (!vm.getOrgAccountId().equals(CurrentUser.get().getLoginAccount())) {
					relateMemberName = relateMemberName + "(" + account.getShortname() + ")";
					peopleRelate.setRelateMemberAccount(account.getShortname());
				}
				peopleRelate.setRelateMemberName(relateMemberName);

				if (vm.getEmailAddress() != null) {
					peopleRelate.setRelateMemberEmail(vm.getEmailAddress());
				}

				if (fromMore) {
					StaffInfo staff = staffInfoManager.getStaffInfoById(new Long(peopleRelate.getRelateMemberId()));
					if (staff != null) {
						peopleRelate.setRelateImageId(staff.getImage_id());
						peopleRelate.setRelateImageDate(staff.getImage_datetime());
					}
					if (vm.getTelNumber() != null) {
						peopleRelate.setRelateMemberHandSet(vm.getTelNumber());
					}
					Long deptId = vm.getOrgDepartmentId();
					V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
					if (dept != null) {
						peopleRelate.setRelateMemberDept(dept.getName());
					}
					Long postId = vm.getOrgPostId();
					V3xOrgPost post = orgManager.getPostById(postId);
					if (post != null) {
						peopleRelate.setRelateMemberPost(post.getName());
					}
					try {
//						orgManager.loadEntityProperty(vm);
						peopleRelate.setRelateMemberTel(vm.getProperty("officeNum"));
					} catch (BusinessException e) {
						log.error("获取扩展属性(办公电话)失败：", e);
					}
				}

				if (peopleRelate.getRelateType() == RelationType.leader.key()) {
					if (containsLeader) {
						leaderList.add(peopleRelate);
					}
				} else if (peopleRelate.getRelateType() == RelationType.assistant.key()) {
					if (containsAssistant) {
						assistantList.add(peopleRelate);
					}
				} else if (peopleRelate.getRelateType() == RelationType.junior.key()) {
					if (containsJunior) {
						juniorList.add(peopleRelate);
					}
				} else if (peopleRelate.getRelateType() == RelationType.confrere.key()) {
					if (containsConfrere) {
						confrereList.add(peopleRelate);
					}
				}
			}
		}
		
		relateMemberMap.put(RelationType.leader, leaderList);
		relateMemberMap.put(RelationType.assistant, assistantList);
		relateMemberMap.put(RelationType.junior, juniorList);
		relateMemberMap.put(RelationType.confrere, confrereList);
		return relateMemberMap;
	}

    @SuppressWarnings("unchecked")
    public Map<RelationType, List<Long>> getAllRelateMembersId(Long userId) throws Exception
    {

        List<Long> leaderList = new ArrayList<Long>();
        List<Long> othersList = new ArrayList<Long>();
        V3xOrgMember vm = null;

        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relatedMemberId", userId));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);

        Map<RelationType, List<Long>> relateMemberMap = new HashMap<RelationType, List<Long>>();
        for (PeopleRelate peopleRelate : l)
        {
            vm = orgManager.getMemberById(new Long(peopleRelate.getRelateMemberId()));
            // 可以取到人并且该人未停用
            if (vm != null && vm.getEnabled())
            {
                if (peopleRelate.getRelateType() == RelationType.leader.key())
                {
                    leaderList.add(peopleRelate.getRelateMemberId());
                }
                else if (peopleRelate.getRelateType() == RelationType.assistant.key())
                {
                    othersList.add(peopleRelate.getRelateMemberId());
                }
                else if (peopleRelate.getRelateType() == RelationType.junior.key())
                {
                    othersList.add(peopleRelate.getRelateMemberId());
                }
                else if (peopleRelate.getRelateType() == RelationType.confrere.key())
                {
                    othersList.add(peopleRelate.getRelateMemberId());
                }
            }
        }
        relateMemberMap.put(RelationType.leader, leaderList);
        relateMemberMap.put(RelationType.otherEscapeLeader, othersList);
        return relateMemberMap;
    }

    @SuppressWarnings("unchecked")
    public List<V3xOrgMember> getRelateMembers(Long userId, int type) throws Exception
    {
        V3xOrgMember vm = null;
        List<V3xOrgMember> oml = new ArrayList<V3xOrgMember>();
        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relatedMemberId", userId))
                .add(Restrictions.eq("relateType", type));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);
        for (PeopleRelate peopleRelate : l)
        {
            vm = orgManager.getMemberById(new Long(peopleRelate.getRelateMemberId()));
            oml.add(vm);
        }
        return oml;
    }

    public Map<Long, PeopleRelate> getAllRelateLongMap(Long userId, int type)
    {
        Map<Long, PeopleRelate> allRelateMap = new HashMap<Long, PeopleRelate>();
        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relatedMemberId", userId))
                .add(Restrictions.eq("relateType", type));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);

        for (PeopleRelate relate : l)
        {
            allRelateMap.put(relate.getRelateMemberId(), relate);
        }
        return allRelateMap;
    }

    public List<PeopleRelate> getAllRelatedList(Long userId, int type)
    {
//        List<PeopleRelate> allRelateList = new ArrayList<PeopleRelate>();
        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", userId)).add(Restrictions.eq("relateType", type));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);

        return l;
    }

    @SuppressWarnings("unchecked")
    public void updateWsbs(Long relateMemberId, Long relatedMemberId) throws Exception
    {

        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", relateMemberId)).add(
                Restrictions.eq("relatedMemberId", relatedMemberId));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);

        PeopleRelate pr = null;
        if (l != null && l.size() != 0)
        {
            pr = l.get(0);
            pr.setRelateWsbs(PeopleRelate.wsbs_sure);
            peoplerelateDao.update(pr);
        }

    }

    /**
     * 将不在设置人员中的与当前人员有关联的人员的握手状态置未未确认
     */
    public void updateWsbs(String deleteIds, Long relatedMemberId) throws Exception
    {
    	String hql = "from PeopleRelate p where (p.relatedMemberId not in (:Ids)"
       +") and (p.relateType!=" + RelationType.confrere.key()
        + ") and (p.relateMemberId= :memberId)" ;
        Map<String,Object> namedParameterMap = new HashMap<String,Object>() ;
        namedParameterMap.put("memberId", relatedMemberId) ;
        namedParameterMap.put("Ids",Constants.parseStrings2Longs(deleteIds, ",") ) ;
    	/**
        List<PeopleRelate> l = peoplerelateDao
                .find("from PeopleRelate p where (p.relatedMemberId not in (" + deleteIds
                        + ")) and (p.relateType!=" + RelationType.confrere.key()
                        + ") and (p.relateMemberId=" + relatedMemberId + ")");
          **/              
        List<PeopleRelate> l = peoplerelateDao.find(hql, namedParameterMap) ;
        PeopleRelate pr = null;
        if (l != null && l.size() != 0)
        {
            for (int i = 0; i < l.size(); i++)
            {
                pr = l.get(i);
                pr.setRelateWsbs(PeopleRelate.wsbs_unsure);
                peoplerelateDao.update(pr);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public PeopleRelate getPeopleRelate(Long relateId, Long relatedId) throws Exception
    {

        PeopleRelate pr = null;

        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", relateId)).add(
                Restrictions.eq("relatedMemberId", relatedId));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);

        if (l != null && l.size() != 0)
        {
            pr = l.get(0);
        }
        return pr;
    }

    /**
     * 根据被关联人员id与类型删除人员
     * 
     * @param relatedMemberId
     * @param type
     * @throws Exception
     */
    @SuppressWarnings( { "deprecation", "unchecked" })
    public void delRelateMembers(Long relatedMemberId, int type) throws Exception
    {

        peoplerelateDao.deleteRelatedbyType(relatedMemberId, type);

        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", relatedMemberId)).add(
                Restrictions.eq("relateType", type));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);

        PeopleRelate pr = null;
        if (l != null && l.size() != 0)
        {
            for (int i = 0; i < l.size(); i++)
            {
                pr = l.get(i);
                pr.setRelateWsbs(PeopleRelate.wsbs_unsure);
                peoplerelateDao.update(pr);
            }
        }
    }

    /**
     * 通过关联类型和被关联人员查询出所有主动关联人员集合
     * 
     * @param long
     *            relatedMemberId
     * @param int
     *            type 关联类型 1.上级 2.秘书 3.下级 4. 我的同事
     * @return List<Long>
     * @throws Exception
     */
    public List<Long> getRelateMemberIdList(long relatedMemberId, int type) throws Exception
    {
        List<Long> relateMemberList = new ArrayList<Long>();

        List<PeopleRelate> l = peoplerelateDao.getRelateMemberIdList(relatedMemberId, type);

        if (l != null && !l.isEmpty())
        {
            for (int i = 0; i < l.size(); i++)
            {
                relateMemberList.add(l.get(i).getRelatedMemberId());
            }
        }
        return relateMemberList;
    }

    public void deleteRelatePeopleRepeat(Long relateId, Long relatedId, int flag) throws Exception
    {
        peoplerelateDao.deletePeopleRelateRepeat(relateId, relatedId, flag);
    }

    public void deletePeopleRelateByOne(Long related, Long relateId, int type) throws Exception
    {
        peoplerelateDao.deletePeopleRelateByOne(related, relateId, type);

    }

    public List<PeopleRelate> getAllRelateMemberList(long relatedMemberId, int type)
            throws Exception
    {
        List<PeopleRelate> list = new ArrayList<PeopleRelate>();
        try
        {
            List<PeopleRelate> peopleRelate = peoplerelateDao.getRelatedMemberIdList(
                    relatedMemberId, type);
            list.addAll(peopleRelate);
            List<PeopleRelate> peopledRelate = peoplerelateDao.getRelateMemberIdList(
                    relatedMemberId, type);
            list.addAll(peopledRelate);
        }
        catch (Exception e)
        {
            log.error("取所有关联人员发生错误", e);
            throw new Exception("取所有关联人员发生错误");
        }

        return list;
    }

    public List<PeopleRelate> getPeopleRelateIsExitRelate(Long relateMemberId, Long uid)
            throws Exception
    {
        DetachedCriteria dc = DetachedCriteria.forClass(PeopleRelate.class).add(
                Restrictions.eq("relateMemberId", relateMemberId)).add(
                Restrictions.eq("relatedMemberId", uid));
        List<PeopleRelate> l = peoplerelateDao.executeCriteria(dc, -1, -1);
        return l;
    }
}
