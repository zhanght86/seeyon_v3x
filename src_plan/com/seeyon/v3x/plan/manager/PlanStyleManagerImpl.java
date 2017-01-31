package com.seeyon.v3x.plan.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.seeyon.cap.meeting.domain.MtContentTemplateCAP;
import com.seeyon.cap.meeting.manager.MtContentTemplateManagerCAP;
import com.seeyon.v3x.plan.dao.PlanStyleBodyDao;
import com.seeyon.v3x.plan.dao.PlanStyleDao;
import com.seeyon.v3x.plan.domain.PlanStyle;
import com.seeyon.v3x.plan.domain.PlanStyleBody;

public class PlanStyleManagerImpl implements PlanStyleManager {

	private PlanStyleDao planStyleDao;

	private PlanStyleBodyDao planStyleBodyDao;
	
	private MtContentTemplateManagerCAP mtContentTemplateManagerCAP;

	private static final Log log = LogFactory.getLog(PlanStyleManagerImpl.class);
	
	public void setMtContentTemplateManagerCAP(MtContentTemplateManagerCAP mtContentTemplateManagerCAP) {
		this.mtContentTemplateManagerCAP = mtContentTemplateManagerCAP;
	}

	public PlanStyleDao getPlanStyleDao() {
		return planStyleDao;
	}

	public void setPlanStyleDao(PlanStyleDao planStyleDao) {
		this.planStyleDao = planStyleDao;
	}

	public PlanStyleBodyDao getPlanStyleBodyDao() {
		return planStyleBodyDao;
	}
	
	public void setPlanStyleBodyDao(PlanStyleBodyDao planStyleBodyDao) {
		this.planStyleBodyDao = planStyleBodyDao;
	}

	public void addPlanStyle(PlanStyle planStyle) {
		planStyle.setIdIfNew();
		planStyle.setCreateTime(new Date());
		getPlanStyleDao().fushSave(planStyle);
	}

	public void deletePlanStyle(Long id) {
		getPlanStyleDao().delete(id);
	}

	public void deletePlanStyles(Long[] ids) {
		getPlanStyleDao().delete(ids);
	}

	public PlanStyle getPlanStyleByPk(Long id) {
		return getPlanStyleDao().findByPrimaryKey(id);
	}

	public List listPlanStyle() {
		return getPlanStyleDao().listPlanStyleByPage();
	}

	public void updatePlanStyle(PlanStyle planStyle) {
		getPlanStyleDao().update(planStyle);
	}

	public PlanStyle getPlanStyleAllInfo(Long id) {
		PlanStyle planStyle = getPlanStyleDao().findByPrimaryKey(id);
		Hibernate.initialize(planStyle.getPlanStyleBody());
		return planStyle;
	}

	public void addPlanStyleBody(PlanStyleBody planStyleBody,PlanStyle style) {
		planStyleBody.setIdIfNew();
		planStyleBody.setCreateDate(new Date());
		planStyleBody.setPlanStyle(style);
		getPlanStyleBodyDao().fushSave(planStyleBody);
	}

	public void updatePlanStyleBody(PlanStyleBody planStyleBody) {
		planStyleBody.setIdIfNew();
		getPlanStyleBodyDao().saveOrUpdate(planStyleBody);
	}

	public void deletePlanStyleBodyByPlanStyleId(Long id) {
		getPlanStyleBodyDao().deleteByPlanStyleId(id);
	}

	public List getPlanStyleByType(String type) {
		// TODO Auto-generated method stub
		return getPlanStyleDao().listPlanStyleByType(type);
	}

	public List getPlanStyleByTypeAndAccount(String type,Long accountId) {
		// TODO Auto-generated method stub
		return getPlanStyleDao().listPlanStyleByTypeAndAccount(type,accountId);
	}
	/**
	 * 初始化
	 */
	@SuppressWarnings("deprecation")
	public void init(){
		List<PlanStyle> planStyle = (List<PlanStyle>) getPlanStyleDao().find();
		if(planStyle.size() > 0){
			List<MtContentTemplateCAP> mct = new ArrayList<MtContentTemplateCAP>();
			for(PlanStyle ps : planStyle){
				Hibernate.initialize(ps.getPlanStyleBody());
				MtContentTemplateCAP mt = new MtContentTemplateCAP();
				mt.setId(ps.getId());
				mt.setTemplateName(ps.getTitle());
				mt.setUsedFlag(true);
				mt.setTemplateFormat(ps.getTextType());
				mt.setContent(ps.getPlanStyleBody().getContent());
				mt.setCreateUser(Long.valueOf(1));//计划表没这个字段，而会议表这个是非空。
				mt.setCreateDate(ps.getCreateTime());
				mt.setUpdateDate(ps.getCreateTime());
				mt.setExt1("2");
				mt.setAccountId(ps.getAccountId());
				mct.add(mt);
				planStyleBodyDao.delete(ps.getPlanStyleBody().getId());	
				planStyleDao.delete(ps.getId());
			}
			mtContentTemplateManagerCAP.saveAll(mct);
			log.info("计划模板表，复制数据完成，共 " + planStyle.size() + " 条记录。");					
		}
	}
}
