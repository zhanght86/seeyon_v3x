/**
 * $Id: StatisticController.java,v 1.20 2010/07/23 01:53:02 renhy Exp $
 `* 
 * Licensed to the UFIDA
 */
package com.seeyon.v3x.hr.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.StaffInfoFlag;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StatisticManager;
import com.seeyon.v3x.hr.webmodel.WebStatisticStaff;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OrgManager;
/**
 * 
 * <p/> Title: 统计分析<控制器>
 * </p>
 * <p/> Description: 统计分析<控制器>
 * </p>
 * <p/> Date: 2007-6-19
 * </p>
 * 
 * @author gaiht TODO 事务性的处理过程将移入到Manager
 */
@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
public class StatisticController extends BaseController{
	private transient static final Log LOG = LogFactory
	.getLog(StatisticController.class);
	
	private StatisticManager statisticManager;
	private OrgManager orgManager;
	
	private int male = 0;
	private int female = 0;
	private int tweentyFiveHereinafter = 0;
	private int tweentySixToThirty = 0;
	private int thirtyOneToThirtyFive = 0;
	private int thirtySixToForty = 0;
	private int fortyHereinbefore = 0;
    private static final Integer[] allPolitical_Position = {1, 2, 3, 4};
    
	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public StatisticManager getStatisticManager() {
		return statisticManager;
	}

	public void setStatisticManager(StatisticManager statisticManager) {
		this.statisticManager = statisticManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO 自动生成方法存根
		return null;
	}
	
	public ModelAndView home(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
//		try{
//			User user = CurrentUser.get() ;
//			if(!Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_HRADMIN, user)){
//				LOG.info("人员登录统计分析--"+user.getId()+request.getRemoteAddr()) ;
//				return null ;
//			}			
//		}catch(Exception e){
//			LOG.info("人员登录统计分析--"+request.getRemoteAddr()) ;
//			return null ;
//		}
		ModelAndView mav = new ModelAndView("hr/statistic/home");
		return mav;
	}
	public ModelAndView homeEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/statistic/homeEntry");
		return mav;
	}
	public ModelAndView initDetailFrame(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("../../common/detail");
		return mav;
	}
	
	public ModelAndView toolBar(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/toolbar");
		return mav;
	}
	
	public ModelAndView statisticTree(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/statisticTree");
		return mav;
	}
	
	public ModelAndView initStatisticFrame(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/statisticFrame");
		return mav;
	}
	
	/**
	 * 
	 * 按部门统计人员数量
	 */
	public ModelAndView statisticOfQuantityByDepartment(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/quantityDep");
		List<WebStatisticStaff> temp = new ArrayList<WebStatisticStaff>();
		List<V3xOrgDepartment> allDepartment = this.orgManager.getAllDepartments();
		for(V3xOrgDepartment dep:allDepartment){
			WebStatisticStaff webstatisticstaff = new WebStatisticStaff();
			webstatisticstaff.setDepName(dep.getName());
			webstatisticstaff.setCount(this.orgManager.getMembersByDepartment(dep.getId(),true,false,dep.getOrgAccountId()).size());
			temp.add(webstatisticstaff);			
		}
		List<WebStatisticStaff> quantityDeps = this.pagenate(temp);
		mav.addObject("quantityDeps", quantityDeps);
		return mav;
	}
	
	/**
	 * 
	 * 按岗位统计人员数量
	 */
	public ModelAndView statisticOfQuantityByPost(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/quantityPost");
		List<WebStatisticStaff> temp = new ArrayList<WebStatisticStaff>();
		List<V3xOrgPost> allPost = this.orgManager.getAllPosts();
		for(V3xOrgPost post:allPost){
			WebStatisticStaff webstatisticstaff = new WebStatisticStaff();
			webstatisticstaff.setPost(post.getName());
			webstatisticstaff.setCount(this.orgManager.getMembersByPost(post.getId()).size());
			temp.add(webstatisticstaff);			
		}
		List<WebStatisticStaff> quantityPosts = this.pagenate(temp);
		mav.addObject("quantityPosts", quantityPosts);
		return mav;
	}
	
	/**
	 * 
	 * 按职务统计人员数量
	 */
	public ModelAndView statisticOfQuantityByLevel(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/quantityLevel");
		List<WebStatisticStaff> temp = new ArrayList<WebStatisticStaff>();
		List<V3xOrgLevel> allLevel = this.orgManager.getAllLevels();
		for(V3xOrgLevel level:allLevel){
			WebStatisticStaff webstatisticstaff = new WebStatisticStaff();
			webstatisticstaff.setLevel(level.getName());
			webstatisticstaff.setCount(this.orgManager.getMembersByLevel(level.getId()).size());
			temp.add(webstatisticstaff);			
		}
		List<WebStatisticStaff> quantityLevels = this.pagenate(temp);
		mav.addObject("quantityLevels", quantityLevels);
		return mav;
	}
	
	/**
	 * 
	 * 按性别统计人员数量
	 */
	public ModelAndView statisticOfQuantityByGender(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/quantityGender");
//		List<StaffInfo> staffinfos = this.statisticManager.getAllStaffInfo();
		List<V3xOrgMember> memberlist = orgManager.getAllMembers();
		List<WebStatisticStaff> quantityGenders = new ArrayList<WebStatisticStaff>();
		int maleCount = 0;
		int femaleCount = 0;
//		for(int i = 0; i<staffinfos.size(); i++){
		for(V3xOrgMember orgm : memberlist){
			if (null == orgm.getGender()) continue;
			if(orgm.getGender().equals(1)){
				maleCount ++;
			}
			if(orgm.getGender().equals(2)){
				femaleCount ++;
			}
		}
		this.male = maleCount;
		this.female = femaleCount;
		WebStatisticStaff malewebstatisticstaff = new WebStatisticStaff();
		WebStatisticStaff femalewebstatisticstaff = new WebStatisticStaff();
		malewebstatisticstaff.setGender("hr.statistic.male.label");
		malewebstatisticstaff.setCount(maleCount);
		quantityGenders.add(malewebstatisticstaff);
		femalewebstatisticstaff.setGender("hr.statistic.female.label");
		femalewebstatisticstaff.setCount(femaleCount);
		quantityGenders.add(femalewebstatisticstaff);
		mav.addObject("quantityGenders", pagenate(quantityGenders));
		return mav;
	}
	
	/**
	 * 
	 * 按年龄段统计人员数量
	 */
	public ModelAndView statisticOfQuantityByAge(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/quantityAge");
//		List<StaffInfo> staffinfos = this.statisticManager.getAllStaffInfo();//获取staffinfo 里的人
		List<V3xOrgMember> memberlist = orgManager.getAllMembers();
		List<WebStatisticStaff> temp = new ArrayList<WebStatisticStaff>();
		int tweentyFiveHereinafter = 0;
		int tweentySixToThirty = 0;
		int thirtyOneToThirtyFive = 0;
		int thirtySixToForty = 0;
		int fortyHereinbefore = 0;
		int ageAVG = 0;
		int ageTal = 0;
		int size = 0;
//		for(int i = 0; i<staffinfos.size(); i++){
		for(V3xOrgMember orgm : memberlist){
//			StaffInfo info = (StaffInfo)staffinfos.get(i);
			StaffInfo info = new StaffInfo();
			info.setAgeByBirthday(orgm.getBirthday());
			String strAge = info.getAge();
			int age = 0;
			if(strAge != null && !strAge.equals("")){
				age = Integer.parseInt(strAge);
			}
			//年龄为1，跳出本次循环//取出年龄和当前时间做差。为0.说明没有修改年龄。然后做了个+1操作。
			if(age<=1){
				continue;
			}
			ageTal += age;
			
			if(age <= 25){
				tweentyFiveHereinafter ++;
			}
			if(age > 25 && age <= 30){
				tweentySixToThirty ++;
			}
			if(age > 30 && age <=35){
				thirtyOneToThirtyFive ++;
			}
			if(age > 35 && age <= 40){
				thirtySixToForty ++;
			}
			if(age > 40){
				fortyHereinbefore ++;
			}
			size++;
		}
		this.tweentyFiveHereinafter = tweentyFiveHereinafter;
		this.tweentySixToThirty = tweentySixToThirty;
		this.thirtyOneToThirtyFive = thirtyOneToThirtyFive;
		this.thirtySixToForty = thirtySixToForty;
		this.fortyHereinbefore = fortyHereinbefore;
		if(size != 0){
			ageAVG = ageTal/size;
		}
		WebStatisticStaff staffOne = new WebStatisticStaff();
		staffOne.setAgeLevel("hr.statistic.tweentyFiveHereinafter.label");
		staffOne.setCount(tweentyFiveHereinafter);
		temp.add(staffOne);
		WebStatisticStaff staffTwo = new WebStatisticStaff();
		staffTwo.setAgeLevel("hr.statistic.tweentySixToThirty.label");
		staffTwo.setCount(tweentySixToThirty);
		temp.add(staffTwo);
		WebStatisticStaff staffThree = new WebStatisticStaff();
		staffThree.setAgeLevel("hr.statistic.thirtyOneToThirtyFive.label");
		staffThree.setCount(thirtyOneToThirtyFive);
		temp.add(staffThree);
		WebStatisticStaff staffFour = new WebStatisticStaff();
		staffFour.setAgeLevel("hr.statistic.thirtySixToForty.label");
		staffFour.setCount(thirtySixToForty);
		temp.add(staffFour);
		WebStatisticStaff staffFive = new WebStatisticStaff();
		staffFive.setAgeLevel("hr.statistic.fortyHereinbefore.label");
		staffFive.setCount(fortyHereinbefore);
		temp.add(staffFive);
		
		WebStatisticStaff avg = new WebStatisticStaff();
		avg.setAgeLevel("hr.statistic.ageAVG.label");
		avg.setCount(ageAVG);
		temp.add(avg);
		
		List<WebStatisticStaff> quantityAges = this.pagenate(temp);
		mav.addObject("quantityAges", quantityAges);
		
		return mav;
	}
	
	/**
	 * 
	 *按部门统计学历
	 */
	public ModelAndView statisticOfEducationByDepartment(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/educationDep");
		List<V3xOrgDepartment> departments = this.orgManager.getAllDepartments();
		List<WebStatisticStaff> temp = new ArrayList<WebStatisticStaff>();
		for(int i = 0; i<departments.size(); i++){
			temp.addAll(this.statisticEduByType(departments.get(i), V3xOrgEntity.ORGENT_TYPE_DEPARTMENT));
		}
		List<WebStatisticStaff> educationDeps = this.pagenate(temp);
		mav.addObject("educationDeps", educationDeps);
		return mav;
	}
		
		
	
	/**
	 * 
	 *按职务统计学历
	 */
	public ModelAndView statisticOfEducationByLevel(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/educationLevel");
		List<V3xOrgLevel> levels = this.orgManager.getAllLevels();
		List<WebStatisticStaff> temp = new ArrayList<WebStatisticStaff>();
		for(int i = 0; i<levels.size(); i++){
			temp.addAll(this.statisticEduByType(levels.get(i), V3xOrgEntity.ORGENT_TYPE_LEVEL));
		}
		List<WebStatisticStaff> educationLevels = this.pagenate(temp);
		mav.addObject("educationLevels", educationLevels);
		return mav;
	}
	
	/**
	 * 根据人员档案信息得到学历分布
	 */
	private HashMap<Integer,Integer> getEducationByStaff(List<StaffInfo> staffinfos){
		HashMap<Integer,Integer> eduLevels = new HashMap<Integer,Integer>();
		if(staffinfos!=null){
			for(StaffInfo staffInfo:staffinfos){
				if(staffInfo.getEdu_level()>0){
					int count = 1;
					if(eduLevels.get(staffInfo.getEdu_level())!=null){
						count = eduLevels.get(staffInfo.getEdu_level())+1;
					}			
					eduLevels.put(staffInfo.getEdu_level(), count);
				}			
			}			
		}		
		return eduLevels;
	}
	
	/**
	 * 
	 *统计总学历
	 */
	public ModelAndView statisticOfEducationTotal(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/statistic/education");
		List<StaffInfo> staffinfos = this.getStaffinfoForStatistic(CurrentUser.get().getLoginAccount());
		HashMap<Integer,Integer> eduLevels = getEducationByStaff(staffinfos);
		List<WebStatisticStaff> edus = new ArrayList<WebStatisticStaff>();
		
		List<Integer> allEnducation = StaffInfoFlag.getAllEducation();
		for(Integer edu:allEnducation){
			WebStatisticStaff ws = new WebStatisticStaff();
			if(eduLevels.get(edu)!=null){
				ws.setCount(eduLevels.get(edu));
			}else{
				ws.setCount(0);
			}			
			ws.setEducation(String.valueOf(edu));
			edus.add(ws);
		}		
		mav.addObject("edus", edus);
		return mav;
	}
	
	
	/**
	 * @param accountId 单位id
	 * @return 过滤后的员工信息
	 * @throws BusinessException
	 */
	private List<StaffInfo> getStaffinfoForStatistic(Long accountId) throws BusinessException{
		List<StaffInfo> staffinfos = this.statisticManager.getAllStaffInfoByAccountId(accountId);
		List<V3xOrgMember> memberlist = orgManager.getAllMembers();
		List<StaffInfo> staffinfosfilter = new ArrayList<StaffInfo>();
		for(StaffInfo staffinfo: staffinfos){
			if(memberlist.contains(orgManager.getMemberById(staffinfo.getOrg_member_id()))){
				staffinfosfilter.add(staffinfo);
			}
		}
		return staffinfosfilter;
	} 
	
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return null;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		LOG.debug("first: " + first + ", pageSize: " + pageSize + ", size: "
				+ list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	/**
	 * 根据List<V3xOrgMember>得到里面id的List
	 */
	private List<Long> memToLongList(List<V3xOrgMember> members){
		List<Long> memIds = new ArrayList<Long>();
		for(V3xOrgMember member : members) {
			memIds.add(member.getId());
		}
		return memIds;
	}
	
	private List<StaffInfo> getAllStaffInfoByMemIds(List<Long> memIds){
		List<StaffInfo> staff = new ArrayList<StaffInfo>();
		if(memIds.size() != 0){
			staff = this.statisticManager.getStaffByMemIds(memIds);
		}
		return staff;
	}
	

	/**
	 * 统计部门或者职务级别的学历分布
	 */	
	private List<WebStatisticStaff> statisticEduByType(V3xOrgEntity ent,String type)throws Exception{
		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
		if(type.equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT)){
			members = this.orgManager.getMembersByDepartment(ent.getId(),false);
		}else if(type.equals(V3xOrgEntity.ORGENT_TYPE_LEVEL)){
			members = this.orgManager.getMembersByLevel(ent.getId(),false);
		}		
		HashMap<Integer,Integer> eduLevels = new HashMap<Integer,Integer>();
		if(members!=null&&members.size()>0){
			List<Long> memIds = this.memToLongList(members);
			List<StaffInfo> staffinfos = this.getAllStaffInfoByMemIds(memIds);			
			eduLevels = getEducationByStaff(staffinfos);			
		}
		List<WebStatisticStaff> edus = new ArrayList<WebStatisticStaff>();
		//这里加上其他的所有学历（自我感觉应用设计有待优化）
		List<Integer> allEnducation = StaffInfoFlag.getAllEducation();
		for(Integer edu:allEnducation){
			WebStatisticStaff ws = new WebStatisticStaff();
			if(type.equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT)){
				ws.setDepName(ent.getName());
			}else if(type.equals(V3xOrgEntity.ORGENT_TYPE_LEVEL)){
				ws.setLevel(ent.getName());
			}			
			if(eduLevels.get(edu)!=null){
				ws.setCount(eduLevels.get(edu));
			}else{
				ws.setCount(0);
			}			
			ws.setEducation(String.valueOf(edu));
			edus.add(ws);
		}		
		return edus;
		
	}
	

	
	public ModelAndView eduDistributing(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/chart/eduDistributing");
		String title = "学历统计";
		String test = request.getParameter("newOrChanged");
		String type = "pie";
		if(test.equals("noChanged")){
			mav.addObject("type", type);
		}else{
			type = request.getParameter("shapeType");
			mav.addObject("type", type);
		}
		
		List<Integer> education = new ArrayList<Integer>();
		List<Integer> educationKeys = new ArrayList<Integer>();
		List<StaffInfo> staffinfos = this.getStaffinfoForStatistic(CurrentUser.get().getLoginAccount());
		HashMap<Integer,Integer> eduLevels = getEducationByStaff(staffinfos);
		
		List<Integer> allEnducation = StaffInfoFlag.getAllEducation();
		for(Integer edu:allEnducation){
			educationKeys.add(edu);
			if(eduLevels.get(edu)!=null){
				education.add(eduLevels.get(edu));
			}else{
				education.add(0);
			}			
		}		

		mav.addObject("educationKeys", educationKeys);
		mav.addObject("education", education);
		mav.addObject("title", title);
		mav.addObject("size", educationKeys.size());
		return mav;
	}
	
	public ModelAndView ageDistributing(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/chart/ageDistributing");
		String title = "年龄统计";
		String test = request.getParameter("newOrChanged");
		String type = "pie";
		if(test.equals("noChanged")){
			mav.addObject("type", type);
		}else{
			type = request.getParameter("shapeType");
			mav.addObject("type", type);
		}
		List<Integer> age = new ArrayList<Integer>();
		
		age.add(this.tweentyFiveHereinafter);
		age.add(this.tweentySixToThirty);
		age.add(this.thirtyOneToThirtyFive);
		age.add(this.thirtySixToForty);
		age.add(this.fortyHereinbefore);
		mav.addObject("age", age);
		mav.addObject("title", title);
		return mav;
	}
	
	public ModelAndView genderDistributing(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/chart/genderDistributing");
		String title = "性别统计";
		String test = request.getParameter("newOrChanged");
		String type = "pie";
		if(test.equals("noChanged")){
			mav.addObject("type", type);
		}else{
			type = request.getParameter("shapeType");
			mav.addObject("type", type);
		}
		List<Integer> gender = new ArrayList<Integer>();
		
		gender.add(this.male);
		gender.add(this.female);
		mav.addObject("gender", gender);
		mav.addObject("title", title);
		return mav;
	}

    public ModelAndView ppDistributing(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("hr/chart/ppDistributing");
        String title = "政治面貌统计";
        String test = request.getParameter("newOrChanged");
        String type = "pie";
        if (test.equals("noChanged")) {
            mav.addObject("type", type);
        } else {
            type = request.getParameter("shapeType");
            mav.addObject("type", type);
        }
        List<Integer> political = new ArrayList<Integer>();
        List<Integer> politicalKeys = new ArrayList<Integer>();
        List<StaffInfo> staffinfos = getStaffinfoForStatistic(CurrentUser.get().getLoginAccount());
        HashMap<Integer, Integer> ppLevels = getPoliticalByStaff(staffinfos);
        for (Integer pp : allPolitical_Position) {
            politicalKeys.add(pp);
            if (ppLevels.containsKey(pp)) {
                political.add((Integer) ppLevels.get(pp));
            } else {
                political.add(Integer.valueOf(0));
            }
        }
        mav.addObject("politicalKeys", politicalKeys);
        mav.addObject("political", political);
        mav.addObject("title", title);
        mav.addObject("size", politicalKeys.size());
        return mav;
    }

    protected HashMap<Integer, Integer> getPoliticalByStaff(List<StaffInfo> staffinfos) {
        HashMap<Integer, Integer> ppLevels = new HashMap<Integer, Integer>();
        if (staffinfos != null) {
            for (StaffInfo staffInfo : staffinfos) {
                if (staffInfo.getPolitical_position() > 0) {
                    int count = 1;
                    if (ppLevels.containsKey(Integer.valueOf(staffInfo.getPolitical_position()))) {
                        count = ((Integer) ppLevels.get(Integer.valueOf(staffInfo.getPolitical_position()))).intValue() + 1;
                    }
                    ppLevels.put(Integer.valueOf(staffInfo.getPolitical_position()), Integer.valueOf(count));
                }
            }
        }
        return ppLevels;
    }

    protected List<WebStatisticStaff> statisticPoliticalByType(V3xOrgEntity ent, String type) throws Exception {
        List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
        if (type.equals("Department")) {
            members = orgManager.getMembersByDepartment(ent.getId(), false);
        } else if (type.equals("Level")) {
            members = orgManager.getMembersByLevel(ent.getId(), false);
        }
        HashMap<Integer, Integer> ppLevels = new HashMap<Integer, Integer> ();
        if ((members != null) && (members.size() > 0)) {
            List<Long> memIds = memToLongList(members);
            List<StaffInfo> staffinfos = getAllStaffInfoByMemIds(memIds);
            ppLevels = getPoliticalByStaff(staffinfos);
        }
        List<WebStatisticStaff> pps = new ArrayList<WebStatisticStaff>();
        for (Integer pp : allPolitical_Position) {
            WebStatisticStaff ws = new WebStatisticStaff();
            if (type.equals("Department")) {
                ws.setDepName(ent.getName());
            } else if (type.equals("Level")) {
                ws.setLevel(ent.getName());
            }
            if (ppLevels.containsKey(pp)) {
                ws.setCount(((Integer) ppLevels.get(pp)).intValue());
            } else {
                ws.setCount(0);
            }
            ws.setPoliticalPosition(String.valueOf(pp));
            pps.add(ws);
        }
        return pps;
    }

    public ModelAndView statisticOfPoliticalByDepartment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("hr/statistic/politicalDep");
        List<V3xOrgDepartment>  departments = orgManager.getAllDepartments();
        List<WebStatisticStaff> temp = new ArrayList<WebStatisticStaff>();
        for (int i = 0; i < departments.size(); ++i) {
            temp.addAll(statisticPoliticalByType((V3xOrgEntity) departments.get(i), "Department"));
        }
        List<WebStatisticStaff> educationDeps = pagenate(temp);
        mav.addObject("ppDeps", educationDeps);
        return mav;
    }

    public ModelAndView statisticOfPoliticalByLevel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("hr/statistic/politicalLevel");
        List<V3xOrgLevel> levels = orgManager.getAllLevels();
        List<WebStatisticStaff> temp = new ArrayList<WebStatisticStaff>();
        for (int i = 0; i < levels.size(); ++i) {
            temp.addAll(statisticPoliticalByType((V3xOrgEntity) levels.get(i), "Level"));
        }
        List<WebStatisticStaff> educationLevels = pagenate(temp);
        mav.addObject("ppLevels", educationLevels);
        return mav;
    }

    public ModelAndView statisticOfPoliticalTotal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("hr/statistic/political");
        List<StaffInfo> staffinfos = getStaffinfoForStatistic(CurrentUser.get().getLoginAccount());
        HashMap<Integer, Integer> ppLevels = getPoliticalByStaff(staffinfos);
        List<WebStatisticStaff> pps = new ArrayList<WebStatisticStaff>();
        for (Integer pp : allPolitical_Position) {
            WebStatisticStaff ws = new WebStatisticStaff();
            if (ppLevels.containsKey(pp)) {
                ws.setCount(((Integer) ppLevels.get(pp)).intValue());
            } else {
                ws.setCount(0);
            }
            ws.setPoliticalPosition(String.valueOf(pp));
            pps.add(ws);
        }
        mav.addObject("politicalPosition", pps);
        return mav;
    }
}
