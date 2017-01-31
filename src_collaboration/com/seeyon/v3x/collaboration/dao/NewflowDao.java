package com.seeyon.v3x.collaboration.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.NewflowRunning;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.webmodel.NewflowModel;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.util.Strings;
/**
 * 新流程设置DAO
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 */
public class NewflowDao extends BaseHibernateDao<NewflowSetting>
{

    /**
     * 得到新流程设置项
     * @param templeteId
     * @param nodeId
     * @return
     */
    public List<NewflowSetting> getNewflowSettingList(Long templeteId, String nodeId) throws ColException{
        String hql = "from " + NewflowSetting.class.getName() + " as n where n.templeteId = ? and n.nodeId = ? order by n.createTime";
        return super.find(hql, templeteId, nodeId);
    }
    /**
     * 得到新流程设置项
     * @param templeteId
     * @param nodeId
     * @return
     */
    public List<NewflowSetting> getTempleteNewflowSettingList(Long templeteId) throws ColException{
        String hql = "from " + NewflowSetting.class.getName() + " as n where n.templeteId = ? order by n.createTime";
        return super.find(hql, templeteId);
    }
    
    /**
     * 保存新流程设置
     * @param settingList
     */
    public void saveNewFlowSetting(List<NewflowSetting> settingList) throws ColException{
        if(settingList == null || settingList.isEmpty()){
            return;
        }
        super.saveAll(settingList);
    }
    
    /**
     * 删除新流程设置
     * @param Long templeteId
     * @param String nodeId
     */
    public void deleteNewFlowSetting(Long templeteId, String nodeId) throws ColException{
        if(Strings.isBlank(nodeId)){
            super.delete(new Object[][]{{"templeteId", templeteId}});
        }
        else{
            super.delete(new Object[][]{{"templeteId", templeteId}, {"nodeId", nodeId}});
        }
    }
    
    public void clearInvalidRunningData(Long mainSummaryId) throws ColException{
        super.delete(NewflowRunning.class, new Object[][]{{"mainSummaryId", mainSummaryId}});
    }
    
    /**
     * 校验模板是否设置了新流程
     * @param templeteId
     * @return
     */
    public boolean checkTempleteHasNewflow(Long templeteId) throws ColException{
        String hqlStr = "select count(id) from " + NewflowSetting.class.getName() + " where templeteId = ?";
        List countList = super.find(hqlStr, templeteId);
        int totalCount = (Integer) countList.get(0);
        return totalCount > 0;
    }
    
    /**
     * 过滤设置了新流程的模板Ids
     * @param templeteIds
     * @return
     */
    public List<Long> filterHasNewflowTempletes(List<Long> templeteIds) throws ColException{
        String hqlStr = "select templeteId, count(id) from " + NewflowSetting.class.getName() + " where templeteId in(:templeteIds) group by templeteId";
        Map<String, Object> namedParams = new HashMap<String, Object>();
        namedParams.put("templeteIds", templeteIds);
        List countList = super.find(hqlStr, namedParams);
        List<Long> result = new ArrayList<Long>();
        if(countList != null && !countList.isEmpty()){
            for (int i = 0; i < countList.size(); i++) {
                Object[] res = (Object[]) countList.get(i);
                Integer count = (Integer)res[1];
                if(count > 0){
                    result.add((Long)res[0]);
                }
            }
        }
        return result;
    }
    
    public List<NewflowModel> getNewflowModelList(Long summaryId, Long templeteId, String nodeId) throws ColException{
        String hql = "select n.id, t.subject, n.sender, n.triggerCondition, n.conditionBase, n.isForce from " + NewflowRunning.class.getName() + " as n, " + Templete.class.getName() + " as t ";
        hql += " where n.mainSummaryId = ? and n.mainTempleteId = ? and n.mainNodeId = ? and n.templeteId=t.id and n.summaryId is null and n.isActivate=false and n.isDelete=false ";
        List resultList = super.find(hql, -1, -1, null, summaryId, templeteId, nodeId);
        if(resultList != null && !resultList.isEmpty()){
            List<NewflowModel> result = new ArrayList<NewflowModel>();
            for(int i=0; i<resultList.size(); i++){
                Object[] temp = (Object[])resultList.get(i);
                Long id = (Long) temp[0];
                String subject = (String) temp[1];
                String sender = (String) temp[2];
                String condition = (String) temp[3];
                String conditionBase = (String) temp[4];
                Boolean isForce = (Boolean)temp[5];
                if(Strings.isNotBlank(condition)){
                	condition = condition.replaceAll("isNotRole", "isnotrole").replaceAll("isRole", "isrole").replaceAll("isPost", "ispost")
                	.replaceAll("isNotPost", "isNotpost");
                }
                if("start".equals(conditionBase)){
                	condition= condition.replace("[Level]", "[startlevel]").replace("[Account]", "[startaccount]")
    	        		.replace("Concurrent_Acunt", "Start_ConcurrentAcunt").replace("Concurrent_Levl", "Start_ConcurrentLevl")
    	        		.replace("Account,Concurrent_Acunt", "startaccount,Start_ConcurrentAcunt").replace("Level,Concurrent_Levl", "startlevel,Start_ConcurrentLevl");
                    condition = condition.replaceAll("Department", "startdepartment").replaceAll("Post", "startpost").replaceAll("Level", "startlevel")
                	.replaceAll("team", "startTeam").replaceAll("secondpost", "startSecondpost").replaceAll("Account", "startaccount")
                	.replaceAll("standardpost", "startStandardpost").replaceAll("grouplevel", "startGrouplevel").replaceAll("Role", "startrole")
                	.replaceAll("ispost", "isStartpost").replaceAll("isNotpost", "isNotStartpost").replaceAll("isNotDep", "isNotStartDep").replaceAll("isDep", "isStartDep");
                }
                result.add(new NewflowModel(id, subject, sender, condition, isForce));
            }
            return result;
        }
        return null;
    }
    
    public List<NewflowRunning> getChildflowRunningList(Long summaryId, List<String> nodeIds) throws ColException{
        DetachedCriteria criteria = DetachedCriteria.forClass(NewflowRunning.class)
        .add(Expression.eq("mainSummaryId", summaryId));
        if(nodeIds != null){
            criteria.add(Expression.in("mainNodeId", nodeIds));
        }
        criteria.add(Expression.eq("isActivate", true)).add(Expression.eq("isDelete", false)).addOrder(Order.asc("createTime"));
        
        List<NewflowRunning> resultList = (ArrayList<NewflowRunning>)super.executeCriteria(criteria, -1, -1);
        return resultList;
    }
    
    public List<NewflowRunning> getNewflowRunningList(Long templeteId, String nodeId,boolean flag) throws ColException{
        DetachedCriteria criteria = DetachedCriteria.forClass(NewflowRunning.class)
        .add(Expression.eq("mainTempleteId", templeteId))
        .add(Expression.eq("mainNodeId", nodeId));
        //为了兼容以前，扩展
        if(flag){
        	criteria.add(Expression.eq("isActivate", true));
        }else{
        	criteria.add(Expression.isNull("summaryId"))
            .add(Expression.eq("isActivate", false));	
        }
        criteria.add(Expression.eq("isDelete", false))
        .addOrder(Order.asc("createTime"));

        List<NewflowRunning> resultList = (ArrayList<NewflowRunning>)super.executeCriteria(criteria, -1, -1);
        return resultList;
    }
    
    public List<NewflowRunning> getNewflowRunningList(Long summaryId, Long templeteId, int flowType) throws ColException{
        DetachedCriteria criteria = DetachedCriteria.forClass(NewflowRunning.class);
        if(flowType == Constant.NewflowType.main.ordinal()){//当前为主流程，取关联子流程信息
            criteria.add(Expression.eq("mainSummaryId", summaryId))
            .add(Expression.eq("mainTempleteId", templeteId));            
        }
        else if(flowType == Constant.NewflowType.child.ordinal()){//当前为子流程，取关联主流程信息
            criteria.add(Expression.eq("summaryId", summaryId))
            .add(Expression.eq("templeteId", templeteId));
        }
        else{
            criteria.add(
                Expression.or(
                    Expression.and(Expression.eq("summaryId", summaryId), Expression.eq("templeteId", templeteId)),
                    Expression.and(Expression.eq("mainSummaryId", summaryId), Expression.eq("mainTempleteId", templeteId)))
                );
        }
        criteria.add(Expression.eq("isActivate", true)).add(Expression.eq("isDelete", false)).addOrder(Order.asc("updateTime"));
        List<NewflowRunning> resultList = (ArrayList<NewflowRunning>)super.executeCriteria(criteria, -1, -1);
        return resultList;
    }
    
    public List<String> checkHasNoFinishNewflow(Long summaryId, List<String> prevNodeIdsArray) throws ColException{
        StringBuffer hqlBf = new StringBuffer(); 
        hqlBf.append("select summary.subject from " + NewflowRunning.class.getName() + " as running, " + ColSummary.class.getName() + " as summary ");
        hqlBf.append("where running.summaryId=summary.id and summary.finishDate is null and running.mainSummaryId=:summaryId and running.mainNodeId in(:prevNodeIds) ");
        hqlBf.append(" and running.flowRelateType=:relateType and running.isActivate=true and running.isDelete=false ");
        Map<String, Object> nameParams = new HashMap<String, Object>();
        nameParams.put("summaryId", summaryId);
        nameParams.put("prevNodeIds", prevNodeIdsArray);
        nameParams.put("relateType", Constant.FlowRelateType.continueByNewflowEnd.ordinal());
        List<String> resultList = (ArrayList<String>)super.find(hqlBf.toString(), nameParams);
        return resultList;
    }
    
    public List<String> checkFinishedNewflow(Long summaryId, List<String> nodeIds) throws ColException{
        StringBuffer hqlBf = new StringBuffer(); 
        Map<String, Object> nameParams = new HashMap<String, Object>();
        hqlBf.append("select summary.subject from " + NewflowRunning.class.getName() + " as running, " + ColSummary.class.getName() + " as summary ");
        hqlBf.append("where running.summaryId=summary.id and summary.finishDate is not null and running.mainSummaryId=:summaryId and running.isActivate=true and running.isDelete=false ");
        nameParams.put("summaryId", summaryId);
        if(nodeIds != null){
            hqlBf.append(" and running.mainNodeId in(:nodeIds) ");
            nameParams.put("nodeIds", nodeIds);
        }
        List<String> resultList = (ArrayList<String>)super.find(hqlBf.toString(), nameParams);
        return resultList;
    }
    
    public NewflowRunning getAffinedMainflow(Long childSummaryId)throws ColException{
        DetachedCriteria criteria = DetachedCriteria.forClass(NewflowRunning.class);
        criteria.add(Expression.eq("summaryId", childSummaryId))
        .add(Expression.eq("flowRelateType", Constant.FlowRelateType.continueByNewflowEnd.ordinal()));
        List<NewflowRunning> result = (List<NewflowRunning>)super.executeCriteria(criteria, -1, -1);
        if(result != null && !result.isEmpty()){
            return result.get(0);
        }
        return null;
    }
    
    public boolean isRelateNewflow(Long baseSummaryId, Long relateSummaryId) throws ColException{
    	String hqlStr = "select count(id) from " + NewflowRunning.class.getName() + " where (summaryId=:baseSummaryId and mainSummaryId=:relateSummaryId) " +
    			" or (summaryId=:relateSummaryId and mainSummaryId=:baseSummaryId)";
    	Map<String, Object> nameParams = new HashMap<String, Object>();
    	nameParams.put("baseSummaryId", baseSummaryId);
    	nameParams.put("relateSummaryId", relateSummaryId);
        List countList = super.find(hqlStr, nameParams);
        int totalCount = (Integer) countList.get(0);
        return totalCount > 0;
    }
    
	public List<String> getNewflowRunningList(Long templeteId) throws ColException{
        String hsql= "select t2.id as id,t2.subject as subject from NewflowRunning t1,Templete t2 where t1.templeteId=? and t1.mainTempleteId= t2.id and t1.processId is null";
        List resultList = super.find(hsql, templeteId);
        List<String> result = new ArrayList<String>();
        if(resultList != null && !resultList.isEmpty()){
            Map<Long,Long> map= new HashMap<Long, Long>();
            for(int i=0; i<resultList.size(); i++){
                Object[] temp = (Object[])resultList.get(i);
                Long id = (Long) temp[0];
                if(null== map.get(id)){
                	String subject = (String) temp[1];
                	result.add(subject);
                	map.put(id, id);
                }
            }
        }
        return result;
	}
}