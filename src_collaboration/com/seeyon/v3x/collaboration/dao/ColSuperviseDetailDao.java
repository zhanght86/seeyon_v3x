package com.seeyon.v3x.collaboration.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSuperviseLog;
import com.seeyon.v3x.collaboration.domain.ColSuperviseReceiver;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.webmodel.ColSuperviseModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.IdentifierUtil;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class ColSuperviseDetailDao extends BaseHibernateDao<ColSuperviseDetail> {
	public ColSuperviseDetail getCurrentUserSupervise(int entityType, long entityId, long userId) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("entityId", entityId);
		paramMap.put("entityType", entityType);
		paramMap.put("userId", userId);
		StringBuffer hql = new StringBuffer("select de from " + ColSuperviseDetail.class.getName() + " as de");
		hql.append(" ," + ColSupervisor.class.getName() + " as su ");
		hql.append(" where de.entityId=:entityId and de.entityType=:entityType ");
		hql.append(" and su.superviseId=de.id and su.supervisorId=:userId ");
		List<ColSuperviseDetail> result = super.find(hql.toString(), -1, -1, paramMap);
		if(result != null && !result.isEmpty()){
			return result.get(0);
		}
		else{
			return null;
		}
	}
	public ColSuperviseDetail getSupervise(int entityType, long entityId) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ColSuperviseDetail.class);
		criteria.add(Restrictions.eq("entityId", entityId));
		criteria.add(Restrictions.eq("entityType", entityType));
		return (ColSuperviseDetail) super.executeUniqueCriteria(criteria);
	}

	public Integer getMySuperviseCount(long userId, int status) {
		StringBuffer hql = new StringBuffer("from "
				+ ColSuperviseDetail.class.getName() + " as de");
		hql.append(" left join de.colSupervisors as su where su.supervisorId=? and de.entityType=? and de.status=?");
		return super.getQueryCount(hql.toString(), new Object[]{userId, Constant.superviseType.summary.ordinal(), status}, new Type[]{Hibernate.LONG, Hibernate.INTEGER, Hibernate.INTEGER});
	}

	public Integer getMySuperviseCount(long userId, int status, int type) {
		StringBuffer hql = new StringBuffer("from "
				+ ColSuperviseDetail.class.getName() + " as de");
		hql.append(" left join de.colSupervisors as su where su.supervisorId=? and de.entityType=? and de.status=?");
		return super.getQueryCount(hql.toString(), new Object[]{userId, type, status}, new Type[]{Hibernate.LONG, Hibernate.INTEGER, Hibernate.INTEGER});
	}

	public Integer getMySuperviseTotalCount(long userId, int status,Integer... superviseType) {
		StringBuffer hql = new StringBuffer("select count(de.id) from "
				+ ColSuperviseDetail.class.getName() + " as de");
		hql.append(","+ColSupervisor.class.getName()+" as su ,"+Affair.class.getName()+" as aff" +
				" where su.supervisorId=:supervisorId and su.superviseId=de.id  and de.entityType in (:entityType) and de.status=:status and aff.state=2 and de.entityId=aff.objectId and aff.app in (:affApp) ");
		if(superviseType == null || superviseType.length ==0){
			superviseType = new Integer[2];
			superviseType[0] = 1;
			superviseType[1] = 2;
		}
		List<Integer> affApp = new ArrayList<Integer>();
		for(int i = 0 ; i < superviseType.length ; i++){
			if(superviseType[i] == 1){
				affApp.add(ApplicationCategoryEnum.collaboration.key());
				continue;
			}
			if(superviseType[i] ==2){
				affApp.add(ApplicationCategoryEnum.edoc.key());
				affApp.add(ApplicationCategoryEnum.edocSend.key());
				affApp.add(ApplicationCategoryEnum.edocRec.key());
				affApp.add(ApplicationCategoryEnum.edocSign.key());
			}
		}
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("supervisorId", userId);
		parameter.put("entityType", superviseType);
		parameter.put("status", status);
		parameter.put("affApp", affApp);
		return (Integer)super.findUnique(hql.toString(), parameter);
	}

	public Integer getMySuperviseTotalCountByCateOrImportant(long userId, int status,List<Integer> superviseType, List<Integer> importantList) {
		StringBuffer hql = new StringBuffer("select count(de.id) from "
				+ ColSuperviseDetail.class.getName() + " as de");
		hql.append(","+ColSupervisor.class.getName()+" as su ,"+Affair.class.getName()+" as aff" +
				" where su.supervisorId=:supervisorId and su.superviseId=de.id  and de.entityType in (:entityType) and de.status=:status and aff.state=2 and de.entityId=aff.objectId and aff.app in (:affApp) ");
		if(superviseType == null || superviseType.size() ==0){
			superviseType = new ArrayList<Integer>();
			superviseType.add(0);
			superviseType.add(1);
			superviseType.add(2);
		}

		List<Integer> affApp = new ArrayList<Integer>();
		for(int i = 0 ; i < superviseType.size() ; i++){
			if(superviseType.get(i) == 0) {
				affApp.add(ApplicationCategoryEnum.form.key());
				continue;
			}
			if(superviseType.get(i) == 1){
				affApp.add(ApplicationCategoryEnum.collaboration.key());
				continue;
			}
			if(superviseType.get(i) == 2){
				affApp.add(ApplicationCategoryEnum.edoc.key());
				affApp.add(ApplicationCategoryEnum.edocSend.key());
				affApp.add(ApplicationCategoryEnum.edocRec.key());
				affApp.add(ApplicationCategoryEnum.edocSign.key());
			}
		}
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("supervisorId", userId);
		parameter.put("entityType", superviseType);
		parameter.put("status", status);
		parameter.put("affApp", affApp);
		if(null == importantList || importantList.size() == 0) {
		} else {
			hql.append(" and aff.importantLevel in (:importantLevel) ");
			parameter.put("importantLevel", importantList);
		}
		return (Integer)super.findUnique(hql.toString(), parameter);
	}

    /**
	 * 根据搜索条件condition、field、field1和UserId、Status取得协同督办信息
	 * @param userId
	 * @param status
	 * @return
	 */
	public List<ColSuperviseModel> getColSuperviseModelList(String condition, String field, String field1, long userId,int status, List<Long> templeteIds){
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer("select summ.subject,")
                            .append("summ.startMemberId,")
                            .append("summ.startDate,")
                            .append("summ.importantLevel,")
                            .append("summ.deadline,")
                            .append("summ.finishDate,")
                            .append("summ.newflowType,")
                            .append("de.id,")
                            .append("de.awakeDate,")
                            .append("de.count,")
                            //.append("de.supervisors,")
                            .append("de.entityId,")
                            .append("de.description,")
                            .append("de.status,")
                            .append("de.entityType,")
                            .append("summ.resentTime,")
        					.append("summ.forwardMember,")
        					.append("summ.bodyType,")
					        .append("summ.identifier");

        hql.append(" from " ).append( ColSuperviseDetail.class.getName() ).append( " as de," ).append( ColSupervisor.class.getName() ).append( " as su, " ).append( ColSummary.class.getName() ).append( " as summ ");
        if("startMemberName".equals(condition)){
            hql.append(",").append(V3xOrgMember.class.getName()).append(" as mem ");
        }
        hql.append(" where su.superviseId=de.id and de.entityId=summ.id ");
        if("startMemberName".equals(condition)){
        	//流程的发起人，不是指谁设置的督办。
            hql.append(" and summ.startMemberId=mem.id and su.supervisorId=:userId and de.entityType=:entityType and de.status=:status ");
        }
        else if("subject".equals(condition) || "importantLevel".equals(condition)){
            hql.append(" and su.supervisorId=:userId and de.entityType=:entityType and de.status=:status ");
        }
        else{
            hql.append(" and su.supervisorId=:userId and de.entityType=:entityType and de.status=:status ");
        }
        boolean hasTemplete = templeteIds != null && !templeteIds.isEmpty();
        if(hasTemplete){
            hql.append(" and summ.templeteId in(:templeteIds) ");
            parameterMap.put("templeteIds", templeteIds);
        }
        parameterMap.put("userId", userId);
        parameterMap.put("entityType", Constant.superviseType.summary.ordinal());
        parameterMap.put("status", status);

        //标题
        if("subject".equals(condition)){
            hql.append(" and summ.subject like :subject ");
            parameterMap.put("subject", "%" + SQLWildcardUtil.escape(field) + "%");
        }
        else if("importantLevel".equals(condition)){
            hql.append(" and summ.importantLevel=:importantLevel");
            parameterMap.put("importantLevel", Integer.parseInt(field));
        }
        else if("startMemberName".equals(condition)){
            hql.append(" and mem.name like :startMemberName");
            parameterMap.put("startMemberName", "%" + SQLWildcardUtil.escape(field) + "%");
        }
        else if("createDate".equals(condition)){
            if (StringUtils.isNotBlank(field)) {
                java.util.Date stamp = Datetimes.getTodayFirstTime(field);
                hql.append(" and summ.createDate >= :timestamp1");
                parameterMap.put("timestamp1", stamp);
            }
            if (StringUtils.isNotBlank(field1)) {
                java.util.Date stamp = Datetimes.getTodayLastTime(field1);
                hql.append(" and summ.createDate <= :timestamp2");
                parameterMap.put("timestamp2", stamp);
            }
        }

        hql.append(" order by de.createDate desc");
        List result = super.find(hql.toString(), parameterMap);
        List<ColSuperviseModel> modelList = null;
        if(result != null && !result.isEmpty()){
            modelList = new ArrayList<ColSuperviseModel>();
            for (int i = 0; i < result.size(); i++) {
                Object[] res = (Object[]) result.get(i);
                ColSuperviseModel model = new ColSuperviseModel();
                int j = 0;
                model.setTitle((String)res[j++]);
                model.setSender((Long)res[j++]);
                Date startDate = (Date)res[j++];
                model.setSendDate(startDate);
                model.setImportantLevel((Integer)res[j++]);
                Long deadline = (Long)res[j++];
                model.setDeadline(deadline);
                Date finishDate = (Date)res[j++];
                //流程是否超期
                if(deadline != null && deadline > 0){
                    Date now = new Date();
                    if(finishDate == null){
                        if((now.getTime()-startDate.getTime()) > deadline*60000){
                            model.setWorkflowTimeout(true);
                        }
                    }
                    else{
                        Long expendTime = finishDate.getTime() - startDate.getTime();
                        if((deadline-expendTime) < 0){
                            model.setWorkflowTimeout(true);
                        }
                    }
                }
                model.setAppType(ApplicationCategoryEnum.collaboration.ordinal());
                model.setNewflowType((Integer)res[j++]);
                Date now = new Date(System.currentTimeMillis());
                //ColSuperviseDetail detail = (ColSuperviseDetail)res[j++];
                model.setId((Long)res[j++]);
                Date awakeDate = (Date)res[j++];
                if(awakeDate != null && now.after(awakeDate)){
                    model.setIsRed(true);
                }
                model.setAwakeDate(awakeDate);
                model.setCount((Integer)res[j++]);
                /*
                Set<ColSupervisor> colSupervisors = ;
                StringBuffer ids = new StringBuffer();
                if(colSupervisors != null && colSupervisors.size()>0) {
                    for(ColSupervisor colSupervisor:colSupervisors) {
                        ids.append(colSupervisor.getSupervisorId() + ",");
                    }
                }*/
                //model.setSupervisor((String)res[j++]);
                //model.setCanModify(detail.isCanModify());
                model.setSummaryId((Long)res[j++]);
                model.setContent(Strings.toHTML((String)res[j++]));
                model.setStatus((Integer)res[j++]);
                //model.setHasWorkflow(false);
                //model.setProcessDescBy(FlowData.DESC_BY_XML);
                model.setEntityType((Integer)res[j++]);
                model.setResendTime((Integer)res[j++]);
                model.setForwardMember((String)res[j++]);
                model.setBodyType((String)res[j++]);
                Boolean hasAtt = IdentifierUtil.lookupInner(res[j++].toString(),0, '1');
                model.setHasAttachment(hasAtt);

                modelList.add(model);
            }
        }
        return modelList;
	}
	//重写getColSuperviseModelList
	public List<ColSuperviseModel> getColSuperviseModelList(String condition, String field, String field1, long userId,int status, List<Long> templeteIds,Integer secretLevel){
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer("select summ.subject,")
                            .append("summ.startMemberId,")
                            .append("summ.startDate,")
                            .append("summ.importantLevel,")
                            .append("summ.deadline,")
                            .append("summ.finishDate,")
                            .append("summ.newflowType,")
                            .append("de.id,")
                            .append("de.awakeDate,")
                            .append("de.count,")
                            //.append("de.supervisors,")
                            .append("de.entityId,")
                            .append("de.description,")
                            .append("de.status,")
                            .append("de.entityType,")
                            .append("summ.resentTime,")
        					.append("summ.forwardMember,")
        					.append("summ.bodyType,")
					        .append("summ.identifier");

        hql.append(" from " ).append( ColSuperviseDetail.class.getName() ).append( " as de," ).append( ColSupervisor.class.getName() ).append( " as su, " ).append( ColSummary.class.getName() ).append( " as summ ");
        if("startMemberName".equals(condition)){
            hql.append(",").append(V3xOrgMember.class.getName()).append(" as mem ");
        }
        hql.append(" where su.superviseId=de.id and de.entityId=summ.id ");
        if("startMemberName".equals(condition)){
        	//流程的发起人，不是指谁设置的督办。
            hql.append(" and summ.startMemberId=mem.id and su.supervisorId=:userId and de.entityType=:entityType and de.status=:status ");
        }
        else if("subject".equals(condition) || "importantLevel".equals(condition)){
            hql.append(" and su.supervisorId=:userId and de.entityType=:entityType and de.status=:status ");
        }
        else{
            hql.append(" and su.supervisorId=:userId and de.entityType=:entityType and de.status=:status ");
        }
        boolean hasTemplete = templeteIds != null && !templeteIds.isEmpty();
        if(hasTemplete){
            hql.append(" and summ.templeteId in(:templeteIds) ");
            parameterMap.put("templeteIds", templeteIds);
        }
        parameterMap.put("userId", userId);
        parameterMap.put("entityType", Constant.superviseType.summary.ordinal());
        parameterMap.put("status", status);

        //标题
        if("subject".equals(condition)){
            hql.append(" and summ.subject like :subject ");
            parameterMap.put("subject", "%" + SQLWildcardUtil.escape(field) + "%");
        }
        else if("importantLevel".equals(condition)){
            hql.append(" and summ.importantLevel=:importantLevel");
            parameterMap.put("importantLevel", Integer.parseInt(field));
        }
        else if("startMemberName".equals(condition)){
            hql.append(" and mem.name like :startMemberName");
            parameterMap.put("startMemberName", "%" + SQLWildcardUtil.escape(field) + "%");
        }
        else if("createDate".equals(condition)){
            if (StringUtils.isNotBlank(field)) {
                java.util.Date stamp = Datetimes.getTodayFirstTime(field);
                hql.append(" and summ.createDate >= :timestamp1");
                parameterMap.put("timestamp1", stamp);
            }
            if (StringUtils.isNotBlank(field1)) {
                java.util.Date stamp = Datetimes.getTodayLastTime(field1);
                hql.append(" and summ.createDate <= :timestamp2");
                parameterMap.put("timestamp2", stamp);
            }
        }
        
        //成发集团项目
        if(secretLevel !=null){
        	hql.append(" and (summ.secretLevel <= :secretLevel or summ.secretLevel is null)");
        	parameterMap.put("secretLevel", secretLevel);
        }

        hql.append(" order by de.createDate desc");
        List result = super.find(hql.toString(), parameterMap);
        List<ColSuperviseModel> modelList = null;
        if(result != null && !result.isEmpty()){
            modelList = new ArrayList<ColSuperviseModel>();
            for (int i = 0; i < result.size(); i++) {
                Object[] res = (Object[]) result.get(i);
                ColSuperviseModel model = new ColSuperviseModel();
                int j = 0;
                model.setTitle((String)res[j++]);
                model.setSender((Long)res[j++]);
                Date startDate = (Date)res[j++];
                model.setSendDate(startDate);
                model.setImportantLevel((Integer)res[j++]);
                Long deadline = (Long)res[j++];
                model.setDeadline(deadline);
                Date finishDate = (Date)res[j++];
                //流程是否超期
                if(deadline != null && deadline > 0){
                    Date now = new Date();
                    if(finishDate == null){
                        if((now.getTime()-startDate.getTime()) > deadline*60000){
                            model.setWorkflowTimeout(true);
                        }
                    }
                    else{
                        Long expendTime = finishDate.getTime() - startDate.getTime();
                        if((deadline-expendTime) < 0){
                            model.setWorkflowTimeout(true);
                        }
                    }
                }
                model.setAppType(ApplicationCategoryEnum.collaboration.ordinal());
                model.setNewflowType((Integer)res[j++]);
                Date now = new Date(System.currentTimeMillis());
                //ColSuperviseDetail detail = (ColSuperviseDetail)res[j++];
                model.setId((Long)res[j++]);
                Date awakeDate = (Date)res[j++];
                if(awakeDate != null && now.after(awakeDate)){
                    model.setIsRed(true);
                }
                model.setAwakeDate(awakeDate);
                model.setCount((Integer)res[j++]);
                /*
                Set<ColSupervisor> colSupervisors = ;
                StringBuffer ids = new StringBuffer();
                if(colSupervisors != null && colSupervisors.size()>0) {
                    for(ColSupervisor colSupervisor:colSupervisors) {
                        ids.append(colSupervisor.getSupervisorId() + ",");
                    }
                }*/
                //model.setSupervisor((String)res[j++]);
                //model.setCanModify(detail.isCanModify());
                model.setSummaryId((Long)res[j++]);
                model.setContent(Strings.toHTML((String)res[j++]));
                model.setStatus((Integer)res[j++]);
                //model.setHasWorkflow(false);
                //model.setProcessDescBy(FlowData.DESC_BY_XML);
                model.setEntityType((Integer)res[j++]);
                model.setResendTime((Integer)res[j++]);
                model.setForwardMember((String)res[j++]);
                model.setBodyType((String)res[j++]);
                Boolean hasAtt = IdentifierUtil.lookupInner(res[j++].toString(),0, '1');
                model.setHasAttachment(hasAtt);

                modelList.add(model);
            }
        }
        return modelList;
	}

	/**
	 * 根据UserId、Status取得全部督办信息。
	 * @param userId
	 * @param status
	 * @return
	 */
	public List<ColSuperviseDetail> getAllSuperviseDetailListInMySupervise(long userId,int status){
		StringBuffer hql = new StringBuffer("select de from " + ColSuperviseDetail.class.getName() + " as de");
		hql.append(" left join de.colSupervisors as su where su.supervisorId=? and de.status=?  order by de.createDate desc");
		List<ColSuperviseDetail> list = super.find(hql.toString(),null,userId,status);
		return list;
	}
	
	public List<ColSuperviseModel> getAllSuperviseModelListInMySuperviseWithoutTemplate(long userId, int status, String condition, String textfield, String textfield1, List<Integer> entityType, int maxCount){
		Map<String,List<String>> m = new HashMap<String,List<String>>();
		List<String> l = new ArrayList<String>();
		l.add(textfield);
		l.add(textfield1);
		
		m.put(condition, l);
		return (List<ColSuperviseModel>)getSuperviseModelList(userId,status,m,entityType,maxCount,false);
		
	}
	/**
	 * 根据UserId、Status取得全部督办信息。
	 * @param userId
	 * @param status
	 * @return
	 */
	public Object getSuperviseModelList(long userId, int status, Map<String,List<String>> queryCondition, List<Integer> entityType, int maxCount,boolean isCount){
        List<ColSuperviseModel> modelList = null;
        List<Integer> appList = new ArrayList<Integer>();
        if(Strings.isEmpty(entityType) 
        		|| entityType.contains(Constant.superviseType.template.ordinal())) {
        	appList.add(ApplicationCategoryEnum.collaboration.key());
        	appList.add(ApplicationCategoryEnum.edoc.key());
            appList.add(ApplicationCategoryEnum.edocSend.key());
            appList.add(ApplicationCategoryEnum.edocRec.key());
            appList.add(ApplicationCategoryEnum.edocSign.key());
            appList.add(ApplicationCategoryEnum.form.key());
        }
        if(entityType.contains(Constant.superviseType.summary.ordinal())){
            appList.add(ApplicationCategoryEnum.collaboration.key());
        }
        if(entityType.contains(Constant.superviseType.edoc.ordinal())){
            appList.add(ApplicationCategoryEnum.edoc.key());
            appList.add(ApplicationCategoryEnum.edocSend.key());
            appList.add(ApplicationCategoryEnum.edocRec.key());
            appList.add(ApplicationCategoryEnum.edocSign.key());
        }

        Map<String, Object> parameterMap = new HashMap<String, Object>();

        StringBuffer hql = new StringBuffer("select ");
        if(isCount){
        	hql.append(" count(distinct aff.id) as cnt ");
        }else{
        	 hql.append("de.id")
             .append(",de.awakeDate")
             .append(",de.count")
             .append(",de.entityId")
             .append(",de.description")
             .append(",de.status")
             .append(",de.entityType");
         hql.append(",aff.app")
             .append(",aff.subject")
             .append(",aff.senderId")
             .append(",aff.createDate")
             .append(",aff.importantLevel")
             .append(",aff.resentTime")
     		.append(",aff.forwardMember")
             .append(",aff.bodyType")
             .append(",aff.identifier ");
        }
           
            hql.append(" from " ).append( ColSuperviseDetail.class.getName() ).append( " as de")
                .append(", " ).append(ColSupervisor.class.getName()).append( " as su" )
                .append(", " ).append(Affair.class.getName() ).append( " as aff ");

            hql.append(" where su.superviseId=de.id and su.supervisorId=:userId and de.status=:status");
            if(Strings.isNotEmpty(entityType)){
	            hql.append(" and de.entityType in (:entityType)");
	            parameterMap.put("entityType", entityType);
            }
            hql.append(" and de.entityId=aff.objectId and aff.state=2 and aff.app in(:appList)");

            parameterMap.put("userId", userId);
            parameterMap.put("status", status);
            parameterMap.put("appList", appList);
            
		    if(queryCondition!=null){
		    	for(Iterator<String> it = queryCondition.keySet().iterator();it.hasNext();){
	    		  String condition = it.next();
	    		  String textfield = "";
	    		  String textfield1 = "";
	    		  List<String> l = queryCondition.get(condition);
	    		  if(Strings.isNotEmpty(l)){
	    			  if(l.size()==1){
	    				  textfield = l.get(0);
	    			  }
	    			  if(l.size()==2){
	    				  textfield = l.get(0);
	    				  textfield1 = l.get(1);
	    			  }
	    		  }
	    		  
	    		  if(StringUtils.isNotBlank(condition)){
	      			if("subject".equals(condition)){
	      				if(StringUtils.isNotBlank(textfield)){
	      					hql.append(" and aff.subject like :subject ");
	      					parameterMap.put("subject", "%" + SQLWildcardUtil.escape(textfield) + "%");
	      				}
	      			}
	      			
	      			if("importantLevel".equals(condition)){
	      				if(StringUtils.isNotBlank(textfield)){
	      					String[] levels = textfield.split(",");
	      					if(levels.length == 1){
	      						hql.append(" and aff.importantLevel =:importantLevel ");
	          					parameterMap.put("importantLevel", NumberUtils.toInt(textfield));
	      					}else if(levels.length > 1){
	      						hql.append(" and aff.importantLevel in (:importantLevel) ");
	      						List<Integer> list  = new ArrayList<Integer>();
	      						for(String le: levels){
	      							list.add(NumberUtils.toInt(le));
	      						}
	          					parameterMap.put("importantLevel", list);
	      					}
	      					
	      				}
	      			}
	      			
	      			if("sender".equals(condition)){
	      				if(StringUtils.isNotBlank(textfield)){
	      					hql.append(" and aff.senderId =:senderId ");
	      					parameterMap.put("senderId", NumberUtils.toLong(textfield));
	      				}
	      			}
	      			
	      			if("createDate".equals(condition)){
	      				if(Strings.isNotBlank(textfield)){
	      					hql.append(" and aff.createDate>=:beginDate ");
	      					parameterMap.put("beginDate", Datetimes.getTodayFirstTime(textfield));
	      				}
	
	      				if(Strings.isNotBlank(textfield1)){
	      					hql.append(" and aff.createDate<=:endDate ");
	      					parameterMap.put("endDate", Datetimes.getTodayLastTime(textfield1));
	      				}
	      			}
	      			
	      			if("category".equals(condition)){
	      				//首页分类   自由协同/表单。协同模板/公文 
	      				if(Strings.isNotBlank(textfield)){
	      					String[] categorys = textfield.split(",");
	      					hql.append(" and (");
	      					boolean needOr = false;
	      					for(String c :categorys){
	      						
	      						if(needOr) hql.append(" or ");
	  							else needOr = true;
	      						
	      						if("catagory_coll".equals(c)) {
	          						hql.append(" (aff.app = :capp and templete_id is null) ");
	          						parameterMap.put("capp",ApplicationCategoryEnum.collaboration.key());
	          					} else if("catagory_edoc".equals(c)) {
	          						hql.append(" (aff.app in (:eapp))");
	          						parameterMap.put("eapp",EdocUtil.getAllEdocApplicationCategoryEnumKey());
	          					} else if("catagory_collOrFormTemplete".equals(c)) {
	          						hql.append(" (aff.app = :tapp and templete_id is not null) ");
	          						parameterMap.put("tapp",ApplicationCategoryEnum.collaboration.key());
	          					}
	      					}
	      					hql.append(")");
	      				}
	      			}
	      			
	    		  }
		    	}
			}
            List result = null;
            if(isCount){
            	//计算记录数
            	List l = super.find(hql.toString(), parameterMap);
            	if(Strings.isEmpty(l))return 0;
            	else return l.get(0);
            }
            hql.append(" order by de.createDate desc");
            if(maxCount == -1){
                result = super.find(hql.toString(), parameterMap);
            }
            else{
                result = super.find(hql.toString(), 0, maxCount, parameterMap);
            }

            if(result != null && !result.isEmpty()){
                modelList = new ArrayList<ColSuperviseModel>();
                Date now = new Date();
                for (int i = 0; i < result.size(); i++) {
                    Object[] res = (Object[]) result.get(i);
                    ColSuperviseModel model = new ColSuperviseModel();
                    int j = 0;
                    model.setId((Long)res[j++]);
                    Date awakeDate = (Date)res[j++];
                    if(awakeDate != null && now.after(awakeDate)){
                        model.setIsRed(true);
                    }
                    model.setAwakeDate(awakeDate);
                    model.setCount((Integer)res[j++]);
                    model.setSummaryId((Long)res[j++]);
                    model.setContent(Strings.toHTML((String)res[j++]));
                    model.setStatus((Integer)res[j++]);
                    model.setHasWorkflow(false);
                    model.setProcessDescBy(FlowData.DESC_BY_XML);
                    int theEntityType = (Integer)res[j++];
                    model.setEntityType(theEntityType);
                    model.setAppType((Integer)res[j++]);
                    model.setTitle((String)res[j++]);
                    model.setSender((Long)res[j++]);
                    Date startDate = (Date)res[j++];
                    model.setSendDate(startDate);
                    model.setImportantLevel((Integer)res[j++]);
                    model.setResendTime((Integer)res[j++]);
                    model.setForwardMember((String)res[j++]);
                    model.setBodyType(res[j++].toString());
                    Boolean hasAtt = IdentifierUtil.lookupInner(res[j++].toString(),0, '1');
                    model.setHasAttachment(hasAtt);
                    modelList.add(model);
                }
            }
        return modelList;
	}

	/**
	 * 为栏目增加按照重要程度查询督办信息
	 * @param userId
	 * @param status
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param entityType
	 * @param maxCount
	 * @param importantList
	 * @return
	 */
	/*public List<ColSuperviseModel> getAllSuperviseModelListInMySuperviseWithoutTemplate(long userId, int status, String condition, String textfield, String textfield1, List<Integer> entityType, int maxCount, List<Integer> importantList){
        List<ColSuperviseModel> modelList = null;
        List<Integer> appList = new ArrayList<Integer>();
        if(entityType.size() == 0) {
        	entityType.add(0);
            entityType.add(1);
            entityType.add(2);
        }
        if(entityType.contains(Constant.superviseType.summary.ordinal())){
            appList.add(ApplicationCategoryEnum.collaboration.key());
        }
        if(entityType.contains(Constant.superviseType.edoc.ordinal())){
            appList.add(ApplicationCategoryEnum.edoc.key());
            appList.add(ApplicationCategoryEnum.edocSend.key());
            appList.add(ApplicationCategoryEnum.edocRec.key());
            appList.add(ApplicationCategoryEnum.edocSign.key());
        }
        if(entityType.contains(Constant.superviseType.template.ordinal())) {
        	 appList.add(ApplicationCategoryEnum.form.key());
        }

        Map<String, Object> parameterMap = new HashMap<String, Object>();

        StringBuffer hql = new StringBuffer("select ");
            hql.append("de.id")
                .append(",de.awakeDate")
                .append(",de.count")
                .append(",de.entityId")
                .append(",de.description")
                .append(",de.status")
                .append(",de.entityType");
            hql.append(",aff.app")
                .append(",aff.subject")
                .append(",aff.senderId")
                .append(",aff.createDate")
                .append(",aff.importantLevel")
                .append(",aff.resentTime")
        		.append(",aff.forwardMember")
                .append(",aff.bodyType")
                .append(",aff.identifier ");
            hql.append(" from " ).append( ColSuperviseDetail.class.getName() ).append( " as de")
                .append(", " ).append(ColSupervisor.class.getName()).append( " as su" )
                .append(", " ).append(Affair.class.getName() ).append( " as aff ");

            hql.append(" where su.superviseId=de.id and su.supervisorId=:userId and de.status=:status");
            hql.append(" and de.entityType in (:entityType) and de.entityId=aff.objectId and aff.state=2 and aff.app in(:appList)");
            hql.append(" and aff.importantLevel in(:importantLevelList)");

            parameterMap.put("userId", userId);
            parameterMap.put("status", status);
            parameterMap.put("entityType", entityType);
            parameterMap.put("appList", appList);
            parameterMap.put("importantLevelList", importantList);

            if(StringUtils.isNotBlank(condition)){
    			if("subject".equals(condition)){
    				if(StringUtils.isNotBlank(textfield)){
    					hql.append(" and aff.subject like :subject ");
    					parameterMap.put("subject", "%" + SQLWildcardUtil.escape(textfield) + "%");
    				}
    			}else if("importantLevel".equals(condition)){
    				if(StringUtils.isNotBlank(textfield)){
    					hql.append(" and aff.importantLevel =:importantLevel ");
    					parameterMap.put("importantLevel", NumberUtils.toInt(textfield));
    				}
    			}else if("sender".equals(condition)){
    				if(StringUtils.isNotBlank(textfield)){
    					hql.append(" and aff.senderId =:senderId ");
    					parameterMap.put("senderId", NumberUtils.toLong(textfield));
    				}
    			}else if("createDate".equals(condition)){
    				if(Strings.isNotBlank(textfield)){
    					hql.append(" and aff.createDate>=:beginDate ");
    					parameterMap.put("beginDate", Datetimes.getTodayFirstTime(textfield));
    				}

    				if(Strings.isNotBlank(textfield1)){
    					hql.append(" and aff.createDate<=:endDate ");
    					parameterMap.put("endDate", Datetimes.getTodayLastTime(textfield1));
    				}
    			}
    		}

            hql.append(" order by de.createDate desc");

            List result = null;
            if(maxCount == -1){
                result = super.find(hql.toString(), parameterMap);
            }
            else{
                result = super.find(hql.toString(), 0, maxCount, parameterMap);
            }

            if(result != null && !result.isEmpty()){
                modelList = new ArrayList<ColSuperviseModel>();
                Date now = new Date();
                for (int i = 0; i < result.size(); i++) {
                    Object[] res = (Object[]) result.get(i);
                    ColSuperviseModel model = new ColSuperviseModel();
                    int j = 0;
                    model.setId((Long)res[j++]);
                    Date awakeDate = (Date)res[j++];
                    if(awakeDate != null && now.after(awakeDate)){
                        model.setIsRed(true);
                    }
                    model.setAwakeDate(awakeDate);
                    model.setCount((Integer)res[j++]);
                    model.setSummaryId((Long)res[j++]);
                    model.setContent(Strings.toHTML((String)res[j++]));
                    model.setStatus((Integer)res[j++]);
                    model.setHasWorkflow(false);
                    model.setProcessDescBy(FlowData.DESC_BY_XML);
                    int theEntityType = (Integer)res[j++];
                    model.setEntityType(theEntityType);
                    model.setAppType((Integer)res[j++]);
                    model.setTitle((String)res[j++]);
                    model.setSender((Long)res[j++]);
                    Date startDate = (Date)res[j++];
                    model.setSendDate(startDate);
                    model.setImportantLevel((Integer)res[j++]);
                    model.setResendTime((Integer)res[j++]);
                    model.setForwardMember((String)res[j++]);
                    model.setBodyType(res[j++].toString());
                    Boolean hasAtt = IdentifierUtil.lookupInner(res[j++].toString(),0, '1');
                    model.setHasAttachment(hasAtt);
                    modelList.add(model);
                }
            }
        return modelList;
	}*/

	/**
	 * 根据UserId、Status取得全部督办信息。
	 * @param userId
	 * @param status
	 * @return
	 */
	public Integer countMySuperviseWithoutTemplate(long userId,int status,List<Integer> entityType){
		StringBuffer hql = new StringBuffer("select count(de.id) from " + ColSuperviseDetail.class.getName() + " as de");
		hql.append(" left join de.colSupervisors as su where su.supervisorId=? and de.status=? and de.entityType in(:entityType) ");
		if(entityType != null && entityType.size() >0){
			hql.append(" and ( ");
			int size = entityType.size();
			for(int i = 0 ; i < size ; i++){
				if(i != 0)
					hql.append(" or ");
				if(entityType.get(i) == 1){
					hql.append("exists(select id from "+ColSummary.class.getName()+" where id = de.entityId and de.entityType=1)");
					continue;
				}
				if(entityType.get(i) ==2){
					hql.append("exists(select id from "+EdocSummary.class.getName()+" where id=de.entityId and de.entityType = 2)");
				}
			}
			hql.append(" ) ");
		}
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("entityType", entityType);
		return (Integer)super.findUnique(hql.toString(), parameter, userId,status);
	}

	/**
	 * 根据UserId、Status取得全部督办信息 except template。
	 * @param userId
	 * @param status
	 * @return
	 */
	public List<ColSuperviseDetail> getAllSuperviseDetailListInMySuperviseForPendingMore(long userId,int status){
		StringBuffer hql = new StringBuffer("select de from " + ColSuperviseDetail.class.getName() + " as de");
		hql.append(" left join de.colSupervisors as su where su.supervisorId=? and de.status=? and de.entityType <> "+ Constant.superviseType.template.ordinal() +" order by de.createDate desc");
		List<ColSuperviseDetail> list = super.find(hql.toString(),-1,-1,null,userId,status);
		return list;
	}

	/**
	 * 根据督办类型、UserId、Status取得督办信息
	 * @param userId
	 * @param status
	 * @param superviseType 督办类型
	 * @return
	 */
	public List<ColSuperviseDetail> getColSuperviseDetailListInMySupervise(long userId,int status,Integer... superviseType){
		StringBuffer hql = new StringBuffer("select de from " + ColSuperviseDetail.class.getName() + " as de");
		hql.append(" left join de.colSupervisors as su where su.supervisorId=:userId and de.entityType in (:entityType) and de.status=:status ");
		if(superviseType != null && superviseType.length > 0){
			hql.append(" and (");
			for(int i = 0 ; i < superviseType.length ; i++){
				if(i != 0)
					hql.append(" or ");
				if(superviseType[i] == 1){
					hql.append("exists(select id from "+ColSummary.class.getName()+" where id = de.entityId and de.entityType=1)");
					continue;
				}
				if(superviseType[i] ==2){
					hql.append("exists(select id from "+EdocSummary.class.getName()+" where id=de.entityId and de.entityType = 2)");
				}
			}
			hql.append(")");
		}
		hql.append(" order by de.createDate desc ");
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("userId", userId);
		parameter.put("entityType", superviseType);
		parameter.put("status", status);
		List<ColSuperviseDetail> list = super.find(hql.toString(),parameter);
		return list;
	}

    /**
     * 保存日志
     * @param superviseId
     */
    public void saveDbLog(long superviseId) {
    	Object[] obj = {superviseId};
    	super.bulkUpdate("update "+ ColSuperviseDetail.class.getName()+" as de set de.count=de.count+1 where id=?", null, obj);
    }

    /**
     * 获取催办总次数
     * @param superviseId
     */
    public int getHastenTimes(long superviseId) {
    	Integer count = (Integer)this.findUnique("select de.count from " + ColSuperviseDetail.class.getName() + " as de where de.id=?", null, superviseId);
    	return count==null ? 0 : count;
    }

    public List<ColSuperviseLog> getLogByDetailId(long superviseId){
    	DetachedCriteria criteria = DetachedCriteria.forClass(ColSuperviseLog.class);
    	criteria.add(Restrictions.eq("superviseId", superviseId)).addOrder(Order.desc("sendTime"));
    	List<ColSuperviseLog> logList = super.executeCriteria(criteria);
    	if(logList != null) {
    		Set<ColSuperviseReceiver> set = null;
    		for(ColSuperviseLog l:logList) {
    			StringBuffer ids = null;
    			set = l.getReceivers();
    			if(set != null && set.size()>0) {
    				ids = new StringBuffer();
    				for(ColSuperviseReceiver reveiver:set)
    					ids.append(reveiver.getReceiver() + ",");
    				l.setReveiverIds(ids.substring(0, ids.length()-1));
    			}
    		}
    	}
    	return logList;
    }

    public void updateContent(long superviseId,String content) {
    	Object[] obj = {content,superviseId};
    	super.bulkUpdate("update "+ColSuperviseDetail.class.getName()+" as de set de.description=? where id=?", null, obj);
    }

    /**
	 * 删除已办结督办
	 * @param userId
	 * @param superviseIds
	 */
    public void deleteSupervised(long userId,Map<String, Object> nameParameters) {
    		super.bulkUpdate("delete from "+ColSupervisor.class.getName()+" as cs where cs.supervisorId=? and cs.superviseId in (:superviseIds)", nameParameters, userId);
    }

	/**
	 * 通过summaryId更新status
	 * @param summaryId
	 */
	public void updateStatusBySummaryId(long summaryId) {
		super.bulkUpdate("update "+ColSuperviseDetail.class.getName()
				+" as de set status=? where entityId=? and entityType=?", null,
				Constant.superviseState.supervised.ordinal(),
				summaryId,
				Constant.superviseType.summary.ordinal());
	}

	/**
	 * 通过summaryId更新公文status
	 * @param summaryId
	 */
	public void updateEdocStatusBySummaryId(long summaryId) {
		super.bulkUpdate("update "+ColSuperviseDetail.class.getName()
				+" as de set status=? where entityId=? and entityType=?", null,
				Constant.superviseState.supervised.ordinal(),
				summaryId,
				Constant.superviseType.edoc.ordinal());
	}

	public List<ColSuperviseDetail> findToBeProcessedDetailBySupervisor(Long supervisorId){

		// TODO 请林大哥帮忙核对下 SQL，我对公文不熟悉，怕有错误。
		String queryString = "from ColSuperviseDetail as detail where detail.colSupervisors.supervisorId = ? and detail.status = ?";
		Object[] values = new Object[]{supervisorId, Constants.EDOC_SUPERVISE_PROGRESSING};
		return super.find(queryString, values);
		/*
		Query query = getSession().createQuery(queryString);
		query.setLong(0, supervisorId);
		List<EdocSuperviseDetail> list = query.list();
		if(null!=list && list.size()>0){
			return list;
		}else{
			return null;
		}
		*/
	}

	public List<ColSuperviseDetail> findProcessedDetailBySupervisor(Long supervisorId){

		// TODO 请林大哥帮忙核对下 SQL，我对公文不熟悉，怕有错误。
		String queryString = "from ColSuperviseDetail as detail where detail.colSupervisors.supervisorId = ? and detail.status = ?";
		Object[] values = new Object[]{supervisorId, Constants.EDOC_SUPERVISE_TERMINAL};
		return super.find(queryString, values);
		/*
		Query query = getSession().createQuery(queryString);
		query.setLong(0, supervisorId);
		List<EdocSuperviseDetail> list = query.list();
		if(null!=list && list.size()>0){
			return list;
		}else{
			return null;
		}
		*/
	}

	/**
	 * 根据公文id查找所有的detail记录,每一条公文只可能对应一条督办记录
	 * @param summaryId
	 * @return
	 */
	public ColSuperviseDetail findEdocSuperviseDetailBySummaryId(Long summaryId){

		// TODO 请林大哥帮忙核对下 SQL，我对公文不熟悉，恐怕字段对应有错误。
		String queryString = "from ColSuperviseDetail as de where de.entityId = ?";
		List <ColSuperviseDetail> list = super.find(queryString, summaryId);
		if(null!=list && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	/*	林大哥： ColSuperviseDetail 表中 有一个entityType 字段。 我目前用这个字段来区别这条记录是公文还是协同。
		该字段的取值为其类型在枚举定义中的序数，模板为0，协同为1，公文为2
		取值方法：com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal()
	*/
	public void saveOrUpdateDetail(ColSuperviseDetail detail){
		super.getHibernateTemplate().saveOrUpdate(detail);
	}

	/**
	 * 删除督办日志
	 * @param superviseId
	 */
	public void deleteLogBySuperviseId(long superviseId) {
		super.bulkUpdate("delete from "+ColSuperviseLog.class.getName()+" as l where l.superviseId=?", null, superviseId);
	}

    public void deleteSupervised(Long superviseId){
        super.bulkUpdate("delete from "+ColSuperviseDetail.class.getName()+" as d where d.id=?", null, superviseId);
        super.bulkUpdate("delete from "+ColSupervisor.class.getName()+" as cs where cs.superviseId=?", null, superviseId);
    }

    @SuppressWarnings("unchecked")
	public boolean isSupervisor(Long userId, Long summaryId)
    {
    	boolean isSuperviser = false;
    	String hql = "select count(de.id) from ColSuperviseDetail as de ,ColSupervisor as su where su.superviseId=de.id and de.entityId=? and su.supervisorId=?";
        List results = super.find(hql, summaryId, userId);
        if(results!=null && !results.isEmpty()){
            if((Integer)results.get(0) > 0){
            	isSuperviser = true;
            }
        }
        return isSuperviser;
    }

    @SuppressWarnings("unchecked")
    public List<Affair> getAffairByStatus(Long memberId, int state,int firstResult,int maxResults,List<Integer> entityType )
    {
    	List<Integer> appList = new ArrayList<Integer>();
        if(entityType.contains(Constant.superviseType.summary.ordinal())){
            appList.add(ApplicationCategoryEnum.collaboration.key());
        }
        if(entityType.contains(Constant.superviseType.edoc.ordinal())){
            appList.add(ApplicationCategoryEnum.edoc.key());
            appList.add(ApplicationCategoryEnum.edocSend.key());
            appList.add(ApplicationCategoryEnum.edocRec.key());
            appList.add(ApplicationCategoryEnum.edocSign.key());
     }
	List<Affair> affairList = new ArrayList<Affair>();
	int status=state==5?Constant.superviseState.waitSupervise.ordinal():Constant.superviseState.supervised.ordinal();
    	StringBuffer hql = new StringBuffer();
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
        hql.append("from " ).append(Affair.class.getName() )
           .append( " as aff ")
           .append(" where aff.objectId in(select de.entityId from ")
           .append( ColSuperviseDetail.class.getName() )
           .append( " as de")
           .append(", " )
           .append(ColSupervisor.class.getName())
           .append( " as su" )
           .append(" where su.superviseId=de.id and su.supervisorId=:userId and de.status=:status)")
           .append(" and aff.state=2 and aff.app in(:appList)")
           .append(" order by aff.receiveTime desc");;
		namedParameterMap.put("userId", memberId);
		namedParameterMap.put("status", status);
		namedParameterMap.put("appList", appList);
		affairList = super.find(hql.toString(), firstResult, maxResults, namedParameterMap);

		return affairList;
    }
    
    public ColSuperviseDetail getSuperviseDetailByEntityId(Long entityId) {
    	DetachedCriteria criteria = DetachedCriteria
				.forClass(ColSuperviseDetail.class);
		criteria.add(Restrictions.eq("entityId", entityId));
		return (ColSuperviseDetail) super.executeUniqueCriteria(criteria);
    }
}