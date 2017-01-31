/**
 * 
 */
package com.seeyon.v3x.collaboration.manager.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.dao.ManagementSetAclDao;
import com.seeyon.v3x.collaboration.dao.ManagementSetDao;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ManagementSet;
import com.seeyon.v3x.collaboration.domain.ManagementSetAcl;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.WorkStatManager;
import com.seeyon.v3x.collaboration.webmodel.ColSummaryModel;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.dao.EdocSummaryDao;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.StatUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class WorkStatManagerImpl extends BaseHibernateDao<ColSummary> implements WorkStatManager{
    private final static Log log = LogFactory.getLog(WorkStatManagerImpl.class);
    private OrgManager orgManager;
    private ManagementSetDao managementSetDao;
    private ManagementSetAclDao managementSetAclDao;
    private EdocSummaryDao edocSummaryDao;
    
    /**
	 * @return the edocSummaryDao
	 */
	public EdocSummaryDao getEdocSummaryDao() {
		return edocSummaryDao;
	}

	/**
	 * @param edocSummaryDao the edocSummaryDao to set
	 */
	public void setEdocSummaryDao(EdocSummaryDao edocSummaryDao) {
		this.edocSummaryDao = edocSummaryDao;
	}

	public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    
    public void setManagementSetDao(ManagementSetDao managementSetDao) {
        this.managementSetDao = managementSetDao;
    }
    
    public void init(){
        //TODO 在这里初始化管理设置
        //log.info("初始化工作管理设置完成.");
    }
    
    /** 
     * 工作管理 - 协同统计
     * 取回所需的统计数据，目前被删除的事项是记入已办统计范围的�?
     * 基本条件：四种状态待发、已发、代办、已�?时间�?
     * 目前为止只需查个人事项一张表 
     */
   public Map<Long, int[]> colStat4WorkManage(int app, List<Long> memberIds, Date beginOfDate, Date endOfDate) throws ColException{
        Map<Long, int[]> resultMap = new HashMap<Long, int[]>();
        try {
            StringBuffer hqlBf = new StringBuffer();
            hqlBf.append("select count(*),affair.state,affair.memberId from Affair as affair");
            hqlBf.append(" where affair.memberId in(:USERIDS) and affair.app in(:APP) and affair.isDelete=false");
            /*hqlBf.append(" and ((affair.createDate > :beginDate and affair.createDate < :endDate)");
            hqlBf.append(" or (affair.receiveTime > :beginDate and affair.receiveTime < :endDate)");
            hqlBf.append(" or (affair.completeTime > :beginDate and affair.completeTime < :endDate))");*/
            
            String hql = hqlBf.toString();
            Map<String, Object> namedParameterMap = new HashMap<String, Object>();
            //已办只查正常的发文，收文，签报情况
            namedParameterMap.put("APP", getAppNormalList(app));
            namedParameterMap.put("USERIDS", memberIds);
            Map<Long, int[]> countItem = countPending(memberIds, app); //待办、暂存待办数目
            Map<Long, int[]> result_D = getStatData(hql, namedParameterMap, 1, null, null);//本日的已发、已办数据
            namedParameterMap.put("USERIDS", memberIds);
            Map<Long, int[]> result_W = getStatData(hql, namedParameterMap, 2, null, null);//本周的已发、已办数据
            namedParameterMap.put("USERIDS", memberIds);
            Map<Long, int[]> result_M = getStatData(hql, namedParameterMap, 3, null, null);//本月的已发、已办数据
            namedParameterMap.put("USERIDS", memberIds);
            Map<Long, int[]> result_A = getStatData(hql, namedParameterMap, 4, beginOfDate, endOfDate);//指定期限的已发、已办数据
            Map<Long, Integer> pigeonholeSum = countPigeonhole(memberIds, app); //归档数目

            for (Long key : memberIds) {
                int[] results = new int[11];
                //待办、暂存待办数目
                int[] countArr = countItem.get(key);
                if(countArr != null){
                    results[0] =  countArr[0];
                    results[1] =  countArr[1];
                }
                //本日已办、已发
                int[] dayArr = result_D.get(key);
                if(dayArr != null){
                    results[2] =  dayArr[0];
                    results[3] =  dayArr[1];
                }
                //本周已办、已发
                int[] weekArr = result_W.get(key);
                if(weekArr != null){
                    results[4] =  weekArr[0];
                    results[5] =  weekArr[1];
                }
                //本月已办、已发
                int[] monthArr = result_M.get(key);
                if(monthArr != null){
                    results[6] =  monthArr[0];
                    results[7] =  monthArr[1];
                }
                //指定期限已办、已发
                int[] quatArr = result_A.get(key);
                if(quatArr != null){
                    results[8] =  quatArr[0];
                    results[9] =  quatArr[1];
                }
                //归档数目
                if(pigeonholeSum.get(key) != null){                    
                    results[10] = pigeonholeSum.get(key);
                }
                resultMap.put(key, results);
            }
        } 
        catch (Exception e) {
            log.error("工作管理 - 协同统计异常", e);
        }
       return resultMap;
   }
   /*
    * 取出待办和暂存待办的总数
    */
   @SuppressWarnings("unchecked")
   private Map<Long, int[]> countPending(List<Long> memberIds, int app) throws ColException{
       Map<String, Object> namedParameterMap = new HashMap<String, Object>();
       String hql = "select count(*),affair.subState,affair.memberId from Affair as affair"
           + " where affair.memberId in (:MEMBERIDS) and affair.state=:STATE and affair.app in(:APP) and affair.isDelete=false group by affair.subState,affair.memberId";
       namedParameterMap.put("APP", getAppList(app));
       //namedParameterMap.put("MEMBERIDS", memberIds);
       namedParameterMap.put("STATE", StateEnum.col_pending.key());
       
       
       List<Object[]> results = new ArrayList<Object[]>();

       List<Long>[] memberIdArray = Strings.splitList(memberIds, 500);
       if( memberIdArray!=null && memberIdArray.length!=0 ){
	       for(int i=0 ; i<memberIdArray.length ; i++){
	    	   List<Long> tempMemberIdList = memberIdArray[i];
	    	   namedParameterMap.put("MEMBERIDS", tempMemberIdList);
	    	   
	    	   List<Object[]> tempList = (List<Object[]>)find(hql, -1, -1, namedParameterMap);
	    	   
	    	   results.addAll(tempList);
	       }
       }
       
       Map<Long, int[]> resultMap = new HashMap<Long, int[]>();
       for (int i = 0; i < results.size(); i++) {
           Object[] result = (Object[]) results.get(i);
           int count = (Integer) result[0];
           int subState = result[1] != null ? (Integer) result[1] : 0;
           Long memberId = (Long)result[2];
           int[] stat = resultMap.get(memberId);
           int index = 0;
           if (subState==SubStateEnum.col_pending_ZCDB.key()) {
               index = 1;
           }
           else{
               index = 0;               
           }
           if(stat == null){
               stat = new int[2];
           }
           stat[index] += count;
           resultMap.put(memberId, stat);
       }
       return resultMap;
   }
   /*
    * 取出归档的总数
    */
   @SuppressWarnings("unchecked")
    private Map<Long, Integer> countPigeonhole(List<Long> memberIds, int app) throws ColException{
       Map<String, Object> namedParameterMap = new HashMap<String, Object>();
       String hql="select count(*),affair.memberId from Affair as affair where affair.memberId in(:MEMBERIDS) and affair.app in(:APP) and affair.isDelete=false and affair.archiveId is not null group by affair.memberId";
       namedParameterMap.put("APP", getAppList(app));
       namedParameterMap.put("MEMBERIDS", memberIds);
       List<Object[]> results = (List<Object[]>)find(hql, -1, -1, namedParameterMap);
       Map<Long, Integer> resultMap = new HashMap<Long, Integer>();
       for (int i = 0; i < results.size(); i++) {
           Object[] result = (Object[]) results.get(i);
           int count = (Integer) result[0];
           Long memberId = (Long)result[1];
           resultMap.put(memberId, count);
       }
       return resultMap;
   }


    public List<ColSummaryModel> queryColList(Long memberId, int type, Date beginDate, Date endDate) throws ColException {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        
        StringBuffer hql = new StringBuffer();
        hql.append("select ").append(selectAffair);
        hql.append(" from Affair as affair,ColSummary as summary,").append(V3xOrgMember.class.getName()).append(" as mem");
        hql.append(" where affair.objectId=summary.id and affair.senderId=mem.id and ");
        hql.append(" affair.memberId=:memberId");
        hql.append(" and affair.app=1 and affair.isDelete=false");
        parameterMap.put("memberId", memberId);
        if(type >1 && type < 8){
            hql.append(" and ((affair.createDate > :timestamp1 and affair.createDate < :timestamp2)");
            hql.append(" or (affair.receiveTime > :timestamp1 and affair.receiveTime < :timestamp2)");
            hql.append(" or (affair.completeTime > :timestamp1 and affair.completeTime < :timestamp2))");
        }else if((beginDate != null || endDate != null)&& (type == 8 || type == 9) ){
        	ifTheDateIsNull(beginDate, endDate, hql,"timestamp1","timestamp2");
        }
        Map map = StatUtil.getStatDate();
        switch(type){
            case 0: //待办
                hql.append(" and affair.state=:state and affair.subState!= :subState");
                parameterMap.put("state", StateEnum.col_pending.key());
                parameterMap.put("subState", SubStateEnum.col_pending_ZCDB.key());
                break;
            case 1: //暂存待办
                hql.append(" and affair.state=:state and affair.subState= :subState");
                parameterMap.put("state", StateEnum.col_pending.key());
                parameterMap.put("subState", SubStateEnum.col_pending_ZCDB.key());
                break;
            case 2: //本日已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                parameterMap.put("timestamp1", map.get("BeginOfDay"));
                parameterMap.put("timestamp2", map.get("EndOfDay"));
                break;
            case 3: //本日已发
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_sent.key());
                parameterMap.put("timestamp1", map.get("BeginOfDay"));
                parameterMap.put("timestamp2", map.get("EndOfDay"));
                break;
            case 4: //本周已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                parameterMap.put("timestamp1", map.get("BeginOfWeek"));
                parameterMap.put("timestamp2", map.get("EndOfWeek"));
                break;
            case 5: //本周已发
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_sent.key());
                parameterMap.put("timestamp1", map.get("BeginOfWeek"));
                parameterMap.put("timestamp2", map.get("EndOfWeek"));
                break;
            case 6: //本月已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                parameterMap.put("timestamp1", map.get("BeginOfMonth"));
                parameterMap.put("timestamp2", map.get("EndOfMonth"));
                break;
            case 7: //本月已发
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_sent.key());
                parameterMap.put("timestamp1", map.get("BeginOfMonth"));
                parameterMap.put("timestamp2", map.get("EndOfMonth"));
                break;
            case 8: //指定期限的已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                if(beginDate != null){
                	 Timestamp beginOfDateTS = new Timestamp(beginDate.getTime());
                	 parameterMap.put("timestamp1", beginOfDateTS);
                }
                if(endDate != null){
                	Timestamp endOfDateTS = new Timestamp(endDate.getTime());
                    
                    parameterMap.put("timestamp2", endOfDateTS);
                }
                break;
            case 9: //指定期限的已发
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_sent.key());
                if(beginDate != null){
               	 Timestamp beginOfDateTS = new Timestamp(beginDate.getTime());
               	 parameterMap.put("timestamp1", beginOfDateTS);
               }
               if(endDate != null){
               	Timestamp endOfDateTS = new Timestamp(endDate.getTime());
                   
                   parameterMap.put("timestamp2", endOfDateTS);
               }
                break;
            case 10: //归档的
                hql.append(" and affair.archiveId is not null");
                break;
        }
        if(type == 0 || type == 1){
            hql.append(" order by affair.receiveTime desc");
        }
        else if(type == 2 || type == 4 || type == 6 || type == 8){
            hql.append(" order by affair.completeTime desc");
        }
        else{
            hql.append(" order by affair.createDate desc");
        }
        
        List result = super.find(hql.toString(), parameterMap);
        
        List<ColSummaryModel> models = new ArrayList<ColSummaryModel>();
        for (int i = 0; i < result.size(); i++) {
            Object[] object = (Object[]) result.get(i);
            ColSummary summary = new ColSummary();
            Affair affair = new Affair();
            make(object, summary, affair);
            long summaryId = summary.getId();
            // 开始组装最后返回的结果�?
            ColSummaryModel model = new ColSummaryModel();
            model.setWorkitemId(String.valueOf(summaryId));
            model.setProcessId(summary.getProcessId());
            model.setCaseId(summary.getCaseId() + "");
            model.setSummary(summary);
            model.setAffairId(affair.getId());
            
            int affairState = affair.getState();
            switch (StateEnum.valueOf(affairState)) {
            case col_waitSend:
                model.setColType(ColSummaryModel.COLTYPE.WaitSend.name());
                break;
            case col_sent:
                model.setColType(ColSummaryModel.COLTYPE.Sent.name());
                break;
            case col_done:
                model.setColType(ColSummaryModel.COLTYPE.Done.name());
                if(affair.getCompleteTime() != null){
                    model.setDealTime(new Date(affair.getCompleteTime().getTime()));
                }
                break;
            case col_pending:
                model.setColType(ColSummaryModel.COLTYPE.Pending.name());
                break;
            }
            model.setBodyType(summary.getBodyType());
            // 是否跟踪
            Boolean isTrack = affair.getIsTrack();
            if (isTrack != null) {
                model.setIsTrack(isTrack.booleanValue());
            }
            // 催办次数
            Integer hastenTimes = affair.getHastenTimes();
            if (hastenTimes != null) {
                model.setHastenTimes(hastenTimes);
            }
            // 是否超期
            Boolean overtopTime = affair.getIsOvertopTime();
            if (overtopTime != null) {
                model.setOvertopTime(overtopTime.booleanValue());
            }
            //协同处理期限
            Long deadLine = affair.getDeadlineDate();
            if (deadLine != null) {
                model.setDeadLine(deadLine);
            }
            //是否有附件
            model.setHasAttsFlag(summary.isHasAttachments());

            //取得转发人姓名
            model.setForwardMemberNames(ColHelper.getForwardMemberNames(summary.getForwardMember(), orgManager));
            models.add(model);
        }
        return models;
    }
    
    public List<EdocSummaryModel> queryEdocList(Long memberId, int type, Date beginDate, Date endDate) throws ColException {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        
        User user = CurrentUser.get();
        long user_id = user.getId();
		//获取代理相关信息
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(user_id);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(user_id);
		List<AgentModel> agentModelList = null;
		boolean agentToFlag = false;
		boolean agentFlag = false;
		if(_agentModelList != null && !_agentModelList.isEmpty()){
			agentModelList = _agentModelList;
			agentFlag = true;
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			agentModelList = _agentModelToList;
			agentToFlag = true;
		}
		Map<Integer, AgentModel> agentModelMap = new HashMap<Integer, AgentModel>();
		AgentModel edocAgent = null;
		if(agentModelList != null && !agentModelList.isEmpty()){
	    	for(AgentModel agentModel : agentModelList){
	    		String agentOptionStr = agentModel.getAgentOption();
	    		String[] agentOptions = agentOptionStr.split("&");
	    		for(String agentOption : agentOptions){
	    			int _agentOption = Integer.parseInt(agentOption);
	    			if(_agentOption == ApplicationCategoryEnum.edoc.key()){
	    				edocAgent = agentModel;
	    				agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
	    			}
	    		}
	    	}
		}
        
        StringBuffer hql = new StringBuffer();
        hql.append("select ").append(edocSelectAffair);
        hql.append(" from Affair as affair,EdocSummary as summary,").append(V3xOrgMember.class.getName()).append(" as mem");
        hql.append(" where affair.objectId=summary.id and affair.senderId=mem.id and ");
        hql.append(" affair.memberId=:memberId");
        hql.append(" and affair.app in(:appList) and affair.isDelete=false");
        parameterMap.put("memberId", memberId);
        
        //已办的列表中只查询正常处理流程的，不包括已签收，已发送，已登记之类
        if(type == 2 || type == 4 || type == 6 || type == 8){
        	 parameterMap.put("appList", this.getAppNormalList(ApplicationCategoryEnum.edoc.key()));
        }else{
        	 parameterMap.put("appList", this.getAppList(ApplicationCategoryEnum.edoc.key()));
        }
        if(type >1 && type < 8){
            hql.append(" and ((affair.createDate > :timestamp1 and affair.createDate < :timestamp2)");
            hql.append(" or (affair.receiveTime > :timestamp1 and affair.receiveTime < :timestamp2)");
            hql.append(" or (affair.completeTime > :timestamp1 and affair.completeTime < :timestamp2))");
        }if((type == 8 || type ==9) && (beginDate != null || endDate != null)){
        	ifTheDateIsNull(beginDate, endDate, hql,"timestamp1","timestamp2");
        }
        Map map = StatUtil.getStatDate();
        switch(type){
            case 0: //待办
                hql.append(" and affair.state=:state and (affair.subState!= :subState or affair.subState is  null) ");
                parameterMap.put("state", StateEnum.col_pending.key());
                parameterMap.put("subState", SubStateEnum.col_pending_ZCDB.key());
                break;
            case 1: //暂存待办
                hql.append(" and affair.state=:state and affair.subState= :subState");
                parameterMap.put("state", StateEnum.col_pending.key());
                parameterMap.put("subState", SubStateEnum.col_pending_ZCDB.key());
                break;
            case 2: //本日已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                parameterMap.put("timestamp1", map.get("BeginOfDay"));
                parameterMap.put("timestamp2", map.get("EndOfDay"));
                break;
            case 3: //本日已发
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_sent.key());
                parameterMap.put("timestamp1", map.get("BeginOfDay"));
                parameterMap.put("timestamp2", map.get("EndOfDay"));
                break;
            case 4: //本周已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                parameterMap.put("timestamp1", map.get("BeginOfWeek"));
                parameterMap.put("timestamp2", map.get("EndOfWeek"));
                break;
            case 5: //本周已发
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_sent.key());
                parameterMap.put("timestamp1", map.get("BeginOfWeek"));
                parameterMap.put("timestamp2", map.get("EndOfWeek"));
                break;
            case 6: //本月已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                parameterMap.put("timestamp1", map.get("BeginOfMonth"));
                parameterMap.put("timestamp2", map.get("EndOfMonth"));
                break;
            case 7: //本月已发
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_sent.key());
                parameterMap.put("timestamp1", map.get("BeginOfMonth"));
                parameterMap.put("timestamp2", map.get("EndOfMonth"));
                break;
            case 8: //指定期限的已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                if(beginDate != null){
                  	 Timestamp beginOfDateTS = new Timestamp((Datetimes.getTodayFirstTime(beginDate)).getTime());
                  	 parameterMap.put("timestamp1", beginOfDateTS);
                  }
                  if(endDate != null){
                  	Timestamp endOfDateTS = new Timestamp((Datetimes.getTodayLastTime(endDate)).getTime());
                      
                      parameterMap.put("timestamp2", endOfDateTS);
                  }
                break;
            case 9: //指定期限的已发
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_sent.key());
                if(beginDate != null){
                  	 Timestamp beginOfDateTS = new Timestamp(beginDate.getTime());
                  	 parameterMap.put("timestamp1", beginOfDateTS);
                }
                  if(endDate != null){
                  	Timestamp endOfDateTS = new Timestamp(endDate.getTime());
                      
                      parameterMap.put("timestamp2", endOfDateTS);
                  }
                break;
            case 10: //归档的
                hql.append(" and affair.archiveId is not null");
                break;
        }
        if(type == 0 || type == 1){
            hql.append(" order by affair.receiveTime desc");
        }
        else if(type == 2 || type == 4 || type == 6 || type == 8){
            hql.append(" order by affair.completeTime desc");
        }
        else{
            hql.append(" order by affair.createDate desc");
        }
        
        List result = edocSummaryDao.find(hql.toString(), parameterMap);
        
        List<EdocSummaryModel> models = new ArrayList<EdocSummaryModel>();
        for (int i = 0; i < result.size(); i++) {
            Object[] object = (Object[]) result.get(i);
            Affair affair = new Affair();
            EdocSummary summary = new EdocSummary();
            makeEdoc(object,summary,affair);

            try {
                OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
                V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
                summary.setStartMember(member);
            }
            catch (BusinessException e) {
                log.error("", e);
            }

            //开始组装最后返回的结果
            EdocSummaryModel model = new EdocSummaryModel();
            int affairState = affair.getState();
            if (affairState == StateEnum.col_waitSend.key()) {
                model.setWorkitemId(null);
                model.setCaseId(null);
                model.setStartDate(new java.sql.Date(summary.getCreateTime().getTime()));
                model.setSummary(summary);
                model.setAffairId(affair.getId());                

            } else if (affairState == StateEnum.col_sent.key()) {
                model.setWorkitemId(null);
                model.setCaseId(summary.getCaseId() + "");
                model.setStartDate(new java.sql.Date(summary.getCreateTime().getTime()));
                model.setSummary(summary);
                model.setAffairId(affair.getId()); 
                //设置流程是否超期标志
                java.sql.Timestamp startDate = summary.getStartTime();
				java.sql.Timestamp finishDate = summary.getCompleteTime();
				Date now = new Date(System.currentTimeMillis());
				if(summary.getDeadline() != null && summary.getDeadline() != 0){
					Long deadline = summary.getDeadline()*60000;
					if(finishDate == null){
						if((now.getTime()-startDate.getTime()) > deadline){
							summary.setWorklfowTimeout(true);
						}
					}else{
						Long expendTime = summary.getCompleteTime().getTime() - summary.getStartTime().getTime();
						if((deadline-expendTime) < 0){
							summary.setWorklfowTimeout(true);
						}
					}
				}
            } else if (affairState == StateEnum.col_done.key()) {
                model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            } else if (affairState == StateEnum.col_pending.key()) {
                model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            }else{
               model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            }

            if(affairState == StateEnum.col_waitSend.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.WaitSend.name());}
            else if(affairState == StateEnum.col_sent.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Sent.name());}
            else if(affairState == StateEnum.col_done.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Done.name());}
            else if(affairState == StateEnum.col_pending.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Pending.name());}            

            model.setFinshed(summary.getCompleteTime()!= null);
    
            model.setAffair(affair);
            model.setBodyType(affair.getBodyType());

            //公文状态
            Integer sub_state = affair.getSubState();
            if (sub_state != null) {
                model.setState(sub_state.intValue());
            }

            //是否跟踪
            Boolean isTrack = affair.getIsTrack();
            if (isTrack != null) {
                model.setIsTrack(isTrack.booleanValue());
            }

            //催办次数
            Integer hastenTimes = affair.getHastenTimes();
            if (hastenTimes != null) {
                model.setHastenTimes(hastenTimes);
            }

            //检查是否有附件
            model.setHasAttachments(affair.isHasAttachments());

            //是否超期
            Boolean overtopTime = affair.getIsOvertopTime();
            if(overtopTime != null){
            	model.setOvertopTime(overtopTime.booleanValue());
            }
            
            //提前提醒
            Long advanceRemind = affair.getRemindDate();
            if(advanceRemind == null){
            	advanceRemind = 0L;
            }
            model.setAdvanceRemindTime(advanceRemind);
            
            //协同处理期限
            Long deadLine = affair.getDeadlineDate();
            if(deadLine == null){
            	deadLine = 0L;
            }
            model.setDeadLine(deadLine);
            //是否代理			
			if (affairState == StateEnum.col_done.key()) {
			    if(affair.getTransactorId() != null){
					try {
                            V3xOrgMember member = orgManager.getMemberById(affair.getTransactorId());
						    model.setProxyName(member.getName());
						    model.setProxy(true);
					} catch (BusinessException e) {
						log.error("", e);
					}
			    }
			}
			
			if (affairState == StateEnum.col_pending.key() && agentFlag && affair.getMemberId() != user.getId()) {
				Long proxyMemberId = edocAgent.getAgentToId();
				V3xOrgMember member = null;
				try {
					member = orgManager.getMemberById(proxyMemberId);
				} catch (BusinessException e) {
					log.error("", e);
				}
				model.setProxyName(member.getName());
				model.setProxy(true);
			}
			
			if(affair.getCompleteTime() != null){
				model.setDealTime(new java.sql.Date(affair.getCompleteTime().getTime()));
			}
            models.add(model);
        }
        return models;
    }
    
    public List<EdocSummaryModel> queryOverEdocTimeList(Long memberId, int type, Date beginDate, Date endDate) throws ColException {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        
        User user = CurrentUser.get();
        long user_id = user.getId();
		//获取代理相关信息
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(user_id);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(user_id);
		List<AgentModel> agentModelList = null;
		boolean agentToFlag = false;
		boolean agentFlag = false;
		if(_agentModelList != null && !_agentModelList.isEmpty()){
			agentModelList = _agentModelList;
			agentFlag = true;
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			agentModelList = _agentModelToList;
			agentToFlag = true;
		}
		Map<Integer, AgentModel> agentModelMap = new HashMap<Integer, AgentModel>();
		AgentModel edocAgent = null;
		if(agentModelList != null && !agentModelList.isEmpty()){
	    	for(AgentModel agentModel : agentModelList){
	    		String agentOptionStr = agentModel.getAgentOption();
	    		String[] agentOptions = agentOptionStr.split("&");
	    		for(String agentOption : agentOptions){
	    			int _agentOption = Integer.parseInt(agentOption);
	    			if(_agentOption == ApplicationCategoryEnum.edoc.key()){
	    				edocAgent = agentModel;
	    				agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
	    			}
	    		}
	    	}
		}
        
        StringBuffer hql = new StringBuffer();
        hql.append("select ").append(edocSelectAffair);
        hql.append(" from Affair as affair,EdocSummary as summary,").append(V3xOrgMember.class.getName()).append(" as mem");
        hql.append(" where affair.objectId=summary.id and affair.senderId=mem.id and ");
        hql.append(" affair.memberId=:memberId");
        hql.append(" and affair.isOvertopTime=true ");        
        hql.append(" and affair.app in(:appList) and affair.isDelete=false");
        parameterMap.put("memberId", memberId);
        parameterMap.put("appList", this.getAppList(ApplicationCategoryEnum.edoc.key()));
        if(type >-1 && type < 10 && type != 6 && type != 7 ){
            hql.append(" and ((affair.createDate > :timestamp1 and affair.createDate < :timestamp2)");
            hql.append(" or (affair.receiveTime > :timestamp1 and affair.receiveTime < :timestamp2)");
            hql.append(" or (affair.completeTime > :timestamp1 and affair.completeTime < :timestamp2))");
        }else if(type == 6 || type == 7){
        	ifTheDateIsNull(beginDate, endDate, hql,"timestamp1","timestamp2");
        }
        Map map = StatUtil.getStatDate();
        switch(type){
        case 0: //本周超期待办
            hql.append(" and affair.state=:state ");
            parameterMap.put("state", StateEnum.col_pending.key());
            parameterMap.put("timestamp1", map.get("BeginOfWeek"));
            parameterMap.put("timestamp2", map.get("EndOfWeek"));
            break;
        case 1: //本周超期已办
            hql.append(" and affair.state=:state ");
            parameterMap.put("state", StateEnum.col_done.key());
            parameterMap.put("timestamp1", map.get("BeginOfWeek"));
            parameterMap.put("timestamp2", map.get("EndOfWeek"));
            break;
        case 2: //本月超期待办
            hql.append(" and affair.state=:state ");
            parameterMap.put("state", StateEnum.col_pending.key());
            parameterMap.put("timestamp1", map.get("BeginOfMonth"));
            parameterMap.put("timestamp2", map.get("EndOfMonth"));
            break;
        case 3: //本月超期已办
            hql.append(" and affair.state=:state ");
            parameterMap.put("state", StateEnum.col_done.key());
            parameterMap.put("timestamp1", map.get("BeginOfMonth"));
            parameterMap.put("timestamp2", map.get("EndOfMonth"));
            break;
        case 4: //本月超期待办
            hql.append(" and affair.state=:state ");
            parameterMap.put("state", StateEnum.col_pending.key());
            parameterMap.put("timestamp1", map.get("BeginOfQuarter"));
            parameterMap.put("timestamp2", map.get("EndOfQuarter"));
            break;
        case 5: //本月超期已办
            hql.append(" and affair.state=:state ");
            parameterMap.put("state", StateEnum.col_done.key());
            parameterMap.put("timestamp1", map.get("BeginOfQuarter"));
            parameterMap.put("timestamp2", map.get("EndOfQuarter"));
            break;
        case 6: //指定期限的超期待办
            hql.append(" and affair.state=:state ");
            parameterMap.put("state", StateEnum.col_pending.key());
            if(beginDate != null){
             	 Timestamp beginOfDateTS = new Timestamp(beginDate.getTime());
             	 parameterMap.put("timestamp1", beginOfDateTS);
           }
             if(endDate != null){
             	Timestamp endOfDateTS = new Timestamp(endDate.getTime());
                 parameterMap.put("timestamp2", endOfDateTS);
             }
            break;
        case 7: //指定期限的超期已办
            hql.append(" and affair.state=:state ");
            parameterMap.put("state", StateEnum.col_done.key());
            if(beginDate != null){
             	 Timestamp beginOfDateTS = new Timestamp(beginDate.getTime());
             	 parameterMap.put("timestamp1", beginOfDateTS);
            }
            if(endDate != null){
            	Timestamp endOfDateTS = new Timestamp(endDate.getTime());
                parameterMap.put("timestamp2", endOfDateTS);
            }
            break;
    }
    if(type%2 == 1){
        hql.append(" order by affair.completeTime desc");
    }
    else{
        hql.append(" order by affair.receiveTime desc");
    }
        
        List result = edocSummaryDao.find(hql.toString(), parameterMap);
        
        List<EdocSummaryModel> models = new ArrayList<EdocSummaryModel>();
        for (int i = 0; i < result.size(); i++) {
            Object[] object = (Object[]) result.get(i);
            Affair affair = new Affair();
            EdocSummary summary = new EdocSummary();
            makeEdoc(object,summary,affair);

            try {
                OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
                V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
                summary.setStartMember(member);
            }
            catch (BusinessException e) {
                log.error("", e);
            }

            //开始组装最后返回的结果
            EdocSummaryModel model = new EdocSummaryModel();
            int affairState = affair.getState();
            
            if (affairState == StateEnum.col_waitSend.key()) {
                model.setWorkitemId(null);
                model.setCaseId(null);
                model.setStartDate(new java.sql.Date(summary.getCreateTime().getTime()));
                model.setSummary(summary);
                model.setAffairId(affair.getId());                

            } else if (affairState == StateEnum.col_sent.key()) {
                model.setWorkitemId(null);
                model.setCaseId(summary.getCaseId() + "");
                model.setStartDate(new java.sql.Date(summary.getCreateTime().getTime()));
                model.setSummary(summary);
                model.setAffairId(affair.getId()); 
                //设置流程是否超期标志
                java.sql.Timestamp startDate = summary.getStartTime();
				java.sql.Timestamp finishDate = summary.getCompleteTime();
				Date now = new Date(System.currentTimeMillis());
				if(summary.getDeadline() != null && summary.getDeadline() != 0){
					Long deadline = summary.getDeadline()*60000;
					if(finishDate == null){
						if((now.getTime()-startDate.getTime()) > deadline){
							summary.setWorklfowTimeout(true);
						}
					}else{
						Long expendTime = summary.getCompleteTime().getTime() - summary.getStartTime().getTime();
						if((deadline-expendTime) < 0){
							summary.setWorklfowTimeout(true);
						}
					}
				}
            } else if (affairState == StateEnum.col_done.key()) {
            	if(affair.getCompleteTime() != null){
                    model.setDealTime(new java.sql.Date(affair.getCompleteTime().getTime()));
                }            	
                model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            } else if (affairState == StateEnum.col_pending.key()) {
                model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            }else{
               model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            }

            if(affairState == StateEnum.col_waitSend.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.WaitSend.name());}
            else if(affairState == StateEnum.col_sent.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Sent.name());}
            else if(affairState == StateEnum.col_done.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Done.name());}
            else if(affairState == StateEnum.col_pending.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Pending.name());}            

            model.setFinshed(summary.getCompleteTime()!= null);

            model.setBodyType(affair.getBodyType());

            //公文状态
            Integer sub_state = affair.getSubState();
            if (sub_state != null) {
                model.setState(sub_state.intValue());
            }

            //是否跟踪
            Boolean isTrack = affair.getIsTrack();
            if (isTrack != null) {
                model.setIsTrack(isTrack.booleanValue());
            }

            //催办次数
            Integer hastenTimes = affair.getHastenTimes();
            if (hastenTimes != null) {
                model.setHastenTimes(hastenTimes);
            }

            //检查是否有附件
            model.setHasAttachments(affair.isHasAttachments());

            //是否超期
            Boolean overtopTime = affair.getIsOvertopTime();
            if(overtopTime != null){
            	model.setOvertopTime(overtopTime.booleanValue());
            }
            
            //提前提醒
            Long advanceRemind = affair.getRemindDate();
            if(advanceRemind == null){
            	advanceRemind = 0L;
            }
            model.setAdvanceRemindTime(advanceRemind);
            
            //协同处理期限
            Long deadLine = affair.getDeadlineDate();
            if(deadLine == null){
            	deadLine = 0L;
            }
            model.setDeadLine(deadLine);
            //是否代理			
			if (affairState == StateEnum.col_done.key()) {
			    if(affair.getTransactorId() != null){
					try {
                            V3xOrgMember member = orgManager.getMemberById(affair.getTransactorId());
						    model.setProxyName(member.getName());
						    model.setProxy(true);
					} catch (BusinessException e) {
						log.error("", e);
					}
			    }
			}
			
			if (affairState == StateEnum.col_pending.key() && agentFlag && affair.getMemberId() != user.getId()) {
				Long proxyMemberId = edocAgent.getAgentToId();
				V3xOrgMember member = null;
				try {
					member = orgManager.getMemberById(proxyMemberId);
				} catch (BusinessException e) {
					log.error("", e);
				}
				model.setProxyName(member.getName());
				model.setProxy(true);
			}
			
            models.add(model);
        }
        return models;
    }

    
    
	
    public Map<Long, int[]> colOverTimeStat(int app, List<Long> memberIds, Date beginOfDate, Date endOfDate) throws ColException{
        //将数据放入
        Map<Long, int[]> resultMap = new HashMap<Long, int[]>();
        try {
            Map<String, Object> namedParameterMap = new HashMap<String, Object>();
            StringBuffer hqlBf = new StringBuffer();
            hqlBf.append("select count(*),affair.state,affair.memberId from Affair as affair");
            hqlBf.append(" where  affair.memberId in(:MEMBERIDS)");
            hqlBf.append(" and affair.app in(:APP) and  affair.deadlineDate>0 and affair.isOvertopTime=true and affair.isDelete=false");
            
            String hql = hqlBf.toString();
            
            namedParameterMap.put("APP", getAppList(app));
            namedParameterMap.put("MEMBERIDS", memberIds);
            
            Map<Long, int[]> result_W = getOverTimeStatData(hql, namedParameterMap, 1, null, null);//本周超期的待办、已办数据
            Map<Long, int[]> result_M = getOverTimeStatData(hql, namedParameterMap, 2, null, null);//本月超期的待办、已办数据
            Map<Long, int[]> result_Q = getOverTimeStatData(hql, namedParameterMap, 3, null, null);//本季度超期的待办、已办数据
            Map<Long, int[]> result_A = getOverTimeStatData(hql, namedParameterMap, 4, beginOfDate, endOfDate);//指定期限超期的待办、已办数据

            for (Long key : memberIds) {
                int[] results = new int[8];
                //本周超期待办、已办
                int[] weekArr = result_W.get(key);
                if(weekArr != null){
                    results[0] =  weekArr[0];
                    results[1] =  weekArr[1];
                }
                //本月超期待办、已办
                int[] monthArr = result_M.get(key);
                if(monthArr != null){
                    results[2] =  monthArr[0];
                    results[3] =  monthArr[1];
                }
                //本季度超期待办、已办
                int[] quatArr = result_Q.get(key);
                if(quatArr != null){
                    results[4] =  quatArr[0];
                    results[5] =  quatArr[1];
                }
                //指定期限的超期待办、已办
                int[] setArr = result_A.get(key);
                if(setArr != null){
                    results[6] =  setArr[0];
                    results[7] =  setArr[1];
                }
                resultMap.put(key, results);
            }
        } catch (Exception e) {
            log.error("工作管理 - 超期统计异常", e);
        }
       return resultMap;
    }
    
    public List<ColSummaryModel> queryOverTimeList(Long memberId, int type, Date beginDate, Date endDate) throws ColException {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer();        
        hql.append("select ").append(selectAffair);
        hql.append(" from Affair as affair, ColSummary as summary ");
        hql.append(" where affair.objectId=summary.id ");
        hql.append(" and affair.memberId=:memberId and affair.app=1 and affair.isDelete=false and affair.isOvertopTime=true and affair.deadlineDate>0");
        if(type ==6 || type ==7){
        	ifTheDateIsNull(beginDate, endDate, hql,"timestamp1","timestamp2");
        }else{
        	hql.append(" and ((affair.createDate > :timestamp1 and affair.createDate < :timestamp2)");
            hql.append(" or (affair.receiveTime > :timestamp1 and affair.receiveTime < :timestamp2)");
            hql.append(" or (affair.completeTime > :timestamp1 and affair.completeTime < :timestamp2))");
        }
        parameterMap.put("memberId", memberId);
        Map map = StatUtil.getStatDate();
        switch(type){
            case 0: //本周超期待办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_pending.key());
                parameterMap.put("timestamp1", map.get("BeginOfWeek"));
                parameterMap.put("timestamp2", map.get("EndOfWeek"));
                break;
            case 1: //本周超期已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                parameterMap.put("timestamp1", map.get("BeginOfWeek"));
                parameterMap.put("timestamp2", map.get("EndOfWeek"));
                break;
            case 2: //本月超期待办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_pending.key());
                parameterMap.put("timestamp1", map.get("BeginOfMonth"));
                parameterMap.put("timestamp2", map.get("EndOfMonth"));
                break;
            case 3: //本月超期已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                parameterMap.put("timestamp1", map.get("BeginOfMonth"));
                parameterMap.put("timestamp2", map.get("EndOfMonth"));
                break;
            case 4: //本月超期待办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_pending.key());
                parameterMap.put("timestamp1", map.get("BeginOfQuarter"));
                parameterMap.put("timestamp2", map.get("EndOfQuarter"));
                break;
            case 5: //本月超期已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                parameterMap.put("timestamp1", map.get("BeginOfQuarter"));
                parameterMap.put("timestamp2", map.get("EndOfQuarter"));
                break;
            case 6: //指定期限的超期待办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_pending.key());
                if(beginDate != null){
         		  Timestamp beginOfDateTS = new Timestamp(beginDate.getTime());
         		  parameterMap.put("timestamp1", beginOfDateTS);
         	   }
         	   if(endDate != null){
         		  Timestamp endOfDateTS = new Timestamp(endDate.getTime());
         		  parameterMap.put("timestamp2", endOfDateTS);
         	   }
                break;
            case 7: //指定期限的超期已办
                hql.append(" and affair.state=:state ");
                parameterMap.put("state", StateEnum.col_done.key());
                if(beginDate != null){
           		  Timestamp beginOfDateTS = new Timestamp(beginDate.getTime());
           		  parameterMap.put("timestamp1", beginOfDateTS);
           	   }
           	   if(endDate != null){
           		  Timestamp endOfDateTS = new Timestamp(endDate.getTime());
           		  parameterMap.put("timestamp2", endOfDateTS);
           	   }
                break;
        }
        if(type%2 == 1){
            hql.append(" order by affair.completeTime desc");
        }
        else{
            hql.append(" order by affair.receiveTime desc");
        }
        
        List result = super.find(hql.toString(), parameterMap);
        List<ColSummaryModel> models = new ArrayList<ColSummaryModel>();
        for (int i = 0; i < result.size(); i++) {
            Object[] object = (Object[]) result.get(i);
            ColSummary summary = new ColSummary();
            Affair affair = new Affair();
            make(object, summary, affair);
            long summaryId = summary.getId();
            // 开始组装最后返回的结果�?
            ColSummaryModel model = new ColSummaryModel();
            model.setWorkitemId(String.valueOf(summaryId));
            model.setProcessId(summary.getProcessId());
            model.setCaseId(summary.getCaseId() + "");
            model.setSummary(summary);
            model.setAffairId(affair.getId());
            
            int affairState = affair.getState();
            switch (StateEnum.valueOf(affairState)) {
                case col_done:
                    model.setColType(ColSummaryModel.COLTYPE.Done.name());
                    if(affair.getCompleteTime() != null){
                        model.setDealTime(new Date(affair.getCompleteTime().getTime()));
                    }
                    break;
                case col_pending:
                    model.setColType(ColSummaryModel.COLTYPE.Pending.name());
                    break;
            }
            model.setBodyType(summary.getBodyType());
            // 是否跟踪
            Boolean isTrack = affair.getIsTrack();
            if (isTrack != null) {
                model.setIsTrack(isTrack.booleanValue());
            }
            // 催办次数
            Integer hastenTimes = affair.getHastenTimes();
            if (hastenTimes != null) {
                model.setHastenTimes(hastenTimes);
            }
            // 是否超期
            Boolean overtopTime = affair.getIsOvertopTime();
            if (overtopTime != null) {
                model.setOvertopTime(overtopTime.booleanValue());
            }
            //协同处理期限
            Long deadLine = affair.getDeadlineDate();
            if (deadLine != null) {
                model.setDeadLine(deadLine);
            }
            //是否有附件
            model.setHasAttsFlag(summary.isHasAttachments());
            //取得转发人姓名
            model.setForwardMemberNames(ColHelper.getForwardMemberNames(summary.getForwardMember(), orgManager));
            models.add(model);
        }
        return models;
    }
       
    public String[][] ajaxUpdateStatValue(int app, String[] memberIdsArray, String beginDateStr, String endDateStr) throws ColException{
        if(memberIdsArray == null || memberIdsArray.length == 0){
            return null;
        }
        Map<String, Object> namedParameterMap = new HashMap<String, Object>();
        String[][] resultArray = new String[memberIdsArray.length][3];
        StringBuffer hqlBf = new StringBuffer();
        hqlBf.append("select count(*),affair.state,affair.memberId from Affair as affair");
        hqlBf.append(" where affair.memberId in(:memberIds)");
        hqlBf.append(" and affair.app in(:app) and affair.isDelete=false");
        Date beginDate =null;
        if(Strings.isNotBlank(beginDateStr)){
        	beginDate = Datetimes.getTodayFirstTime(beginDateStr);
        }
        Date endDate = null;
        if(Strings.isNotBlank(endDateStr)){
        	endDate = Datetimes.getTodayLastTime(endDateStr);
        }
        ifTheDateIsNull(beginDate, endDate, hqlBf,"beginDate","endDate");
       /* hqlBf.append(" and ((affair.createDate > :beginDate and affair.createDate < :endDate)");
        hqlBf.append(" or (affair.receiveTime > :beginDate and affair.receiveTime < :endDate)");
        hqlBf.append(" or (affair.completeTime > :beginDate and affair.completeTime < :endDate))");*/
        hqlBf.append(" group by affair.state,affair.memberId");
        
        List<Long> memberIds = new ArrayList<Long>();
        for(int i=0; i<memberIdsArray.length; i++){
            memberIds.add(Long.parseLong(memberIdsArray[i]));
        }
        
        namedParameterMap.put("app", getAppList(app));
        namedParameterMap.put("memberIds", memberIds);
        if(beginDate != null){
        	Timestamp beginOfDateTS = new Timestamp(beginDate.getTime());
        	 namedParameterMap.put("beginDate", beginOfDateTS);
        }
        if(endDate != null){
        	Timestamp endOfDateTS = new Timestamp(endDate.getTime());
        	namedParameterMap.put("endDate", endOfDateTS);
        }
        
        String hql = hqlBf.toString();
        List results = find(hql, -1, -1, namedParameterMap);

        //处理查询结果
        Map<Long, Integer> sendMap = new HashMap<Long, Integer>();
        Map<Long, Integer> doneMap = new HashMap<Long, Integer>();
        for (int i = 0; i < results.size(); i++) {
            Object[] result = (Object[]) results.get(i);
            int state = (Integer) result[1];
            Long memberId = (Long)result[2];
            //已办
            if (state == StateEnum.col_done.key()) {
                int count = (Integer) result[0];
                if(doneMap.get(memberId) != null){
                    count += doneMap.get(memberId);
                }
                doneMap.put(memberId, count);
            }
            //已发
            if (state == StateEnum.col_sent.key()) {
                int count = (Integer) result[0];
                if(sendMap.get(memberId) != null){
                    count += sendMap.get(memberId);
                }
                sendMap.put(memberId, count);
            }
        }
        //组装返回数组
        for(int i=0; i<memberIdsArray.length; i++){
            resultArray[i][0] = memberIdsArray[i];
            Long memberId = Long.parseLong(memberIdsArray[i]);
            int doneSum = 0, sentSum = 0;
            if(doneMap.get(memberId) != null){
                doneSum = doneMap.get(memberId);
            }
            if(sendMap.get(memberId) != null){
                sentSum = sendMap.get(memberId);
            }
            resultArray[i][1] = doneSum+"";
            resultArray[i][2] = sentSum+"";                
        }
        
        return resultArray;
    }

    public String[][] ajaxUpdateOverTimeStat(int app, String[] memberIdsArray, String beginDateStr, String endDateStr) throws ColException{
        if(memberIdsArray == null || memberIdsArray.length == 0){
            return null;
        }
        Map<String, Object> namedParameterMap = new HashMap<String, Object>();
        String[][] resultArray = new String[memberIdsArray.length][3];
        StringBuffer hqlBf = new StringBuffer();
        hqlBf.append("select count(*),affair.state,affair.memberId from Affair as affair");
        hqlBf.append(" where affair.memberId in(:memberIds) ");
        hqlBf.append(" and affair.app in(:app)  and affair.isDelete=false and  affair.deadlineDate>0 and affair.isOvertopTime=true");
        
        Date beginDate =null;
        if(Strings.isNotBlank(beginDateStr)){
        	beginDate = Datetimes.getTodayFirstTime(beginDateStr);
        }
        Date endDate = null;
        if(Strings.isNotBlank(endDateStr)){
        	endDate = Datetimes.getTodayLastTime(endDateStr);
        }
        ifTheDateIsNull(beginDate, endDate, hqlBf,"beginDate","endDate");
        /*
        hqlBf.append(" and ((affair.createDate > :beginDate and affair.createDate < :endDate)");
        hqlBf.append(" or (affair.receiveTime > :beginDate and affair.receiveTime < :endDate)");
        hqlBf.append(" or (affair.completeTime > :beginDate and affair.completeTime < :endDate))");*/
        hqlBf.append(" group by affair.state,affair.memberId");

        List<Long> memberIds = new ArrayList<Long>();
        for(int i=0; i<memberIdsArray.length; i++){
            memberIds.add(Long.parseLong(memberIdsArray[i]));
        }
        namedParameterMap.put("app", getAppList(app));
        namedParameterMap.put("memberIds", memberIds);
        if(beginDate != null){
        	Timestamp beginOfDateTS = new Timestamp(beginDate.getTime());
        	 namedParameterMap.put("beginDate", beginOfDateTS);
        }
        if(endDate != null){
        	Timestamp endOfDateTS = new Timestamp(endDate.getTime());
        	namedParameterMap.put("endDate", endOfDateTS);
        }
        List results = find(hqlBf.toString(), -1, -1, namedParameterMap);

        //处理查询结果
        Map<Long, Integer> overTimePendingMap = new HashMap<Long, Integer>();
        Map<Long, Integer> overTimeDoneMap = new HashMap<Long, Integer>();
        for (int i = 0; i < results.size(); i++) {
            Object[] result = (Object[]) results.get(i);
            int state = (Integer) result[1];
            Long memberId = (Long)result[2];
            //超期待办
            if (state == StateEnum.col_pending.key()) {
                int count = (Integer) result[0];
                if(overTimePendingMap.get(memberId) != null){
                    count += overTimePendingMap.get(memberId);
                }
                overTimePendingMap.put(memberId, count);
            }
            //超期已办
            if (state == StateEnum.col_done.key()) {
                int count = (Integer) result[0];
                if(overTimeDoneMap.get(memberId) != null){
                    count += overTimeDoneMap.get(memberId);
                }
                overTimeDoneMap.put(memberId, count);
            }
        }
        //组装返回数组
        for(int i=0; i<memberIdsArray.length; i++){
            resultArray[i][0] = memberIdsArray[i];
            Long memberId = Long.parseLong(memberIdsArray[i]);
            int overTimePendingSum = 0, overTimeDoneSum = 0;
            if(overTimePendingMap.get(memberId) != null){
                overTimePendingSum = overTimePendingMap.get(memberId);
            }
            if(overTimeDoneMap.get(memberId) != null){
                overTimeDoneSum = overTimeDoneMap.get(memberId);
            }
            resultArray[i][1] = overTimePendingSum+"";
            resultArray[i][2] = overTimeDoneSum+"";              
        }
        
        return resultArray;
    }
    
    /*
     * 根据SQL语句求出相应时间段的�?
     */
    private Map<Long, int[]> getStatData(String hql, Map<String, Object> namedParameterMap, int type, Date beginOfDate, Date endOfDate) throws ColException{
       Map map = StatUtil.getStatDate();
       StringBuffer hqlBf = new StringBuffer(hql);
       if(type == 4){
    	   ifTheDateIsNull(beginOfDate, endOfDate, hqlBf,"beginDate","endDate");
       }else{
    	   hqlBf.append(" and ((affair.createDate > :beginDate and affair.createDate < :endDate)");
           hqlBf.append(" or (affair.receiveTime > :beginDate and affair.receiveTime < :endDate)");
           hqlBf.append(" or (affair.completeTime > :beginDate and affair.completeTime < :endDate))");
       }
       hqlBf.append(" group by affair.state,affair.memberId");
       switch (type) {
            case 1://每日
                namedParameterMap.put("beginDate", map.get("BeginOfDay"));
                namedParameterMap.put("endDate", map.get("EndOfDay"));
                break;
            case 2://每周
                namedParameterMap.put("beginDate", map.get("BeginOfWeek"));
                namedParameterMap.put("endDate", map.get("EndOfWeek"));
                break;
            case 3://每月
                namedParameterMap.put("beginDate", map.get("BeginOfMonth"));
                namedParameterMap.put("endDate", map.get("EndOfMonth"));
                break;
           case 4://指定期限
        	   if(beginOfDate != null){
        		   Timestamp beginOfDateTS = new Timestamp(beginOfDate.getTime());
        		   namedParameterMap.put("beginDate", beginOfDateTS);
        	   }
        	   if(endOfDate != null){
        		   Timestamp endOfDateTS = new Timestamp(endOfDate.getTime());
                   namedParameterMap.put("endDate", endOfDateTS);
        	   }
               break;
        }
       List results = new ArrayList();
       
       List<Long> memberIdList = (List)namedParameterMap.get("USERIDS");
       
       List<Long>[] memberIdArray = Strings.splitList(memberIdList, 500);
       if( memberIdArray!=null && memberIdArray.length!=0 ){
	       for(int i=0 ; i<memberIdArray.length ; i++){
	    	   List<Long> tempMemberIdList = memberIdArray[i];
	    	   namedParameterMap.put("USERIDS", tempMemberIdList);
	    	   List tempList = find(hqlBf.toString(), -1, -1, namedParameterMap);
	    	   results.addAll( tempList );
	       }
       }
       Map<Long, int[]> resultMap = new HashMap<Long, int[]>();
       for (int i = 0; i < results.size(); i++) {
           Object[] result = (Object[]) results.get(i);
           int count = (Integer) result[0];
           int state = (Integer) result[1];
           Long memberId = (Long)result[2];
           int[] stat = resultMap.get(memberId);
           if(stat == null){
               stat = new int[2];
           }
           if (state == StateEnum.col_done.key()) {
               stat[0] += count;
               resultMap.put(memberId, stat);
           }
           else if (state == StateEnum.col_sent.key()) {
               stat[1] += count;
               resultMap.put(memberId, stat);
           }
       }
       return resultMap;
   }
    
    private Map<Long, int[]> getOverTimeStatData(String hql, Map<String, Object> namedParameterMap, int type, Date beginOfDate, Date endOfDate) {
       StringBuffer hqlBf = new StringBuffer(hql);
    	if(type ==4){
    	   ifTheDateIsNull(beginOfDate, endOfDate, hqlBf,"beginDate","endDate");
       }else{
    	   hqlBf.append(" and ((affair.createDate > :beginDate and affair.createDate < :endDate)");
           hqlBf.append(" or (affair.receiveTime > :beginDate and affair.receiveTime < :endDate)");
           hqlBf.append(" or (affair.completeTime > :beginDate and affair.completeTime < :endDate))");
       }
    	hqlBf.append(" group by affair.state,affair.memberId");
    	Map map = StatUtil.getStatDate();
       switch (type) {
            case 1://每周
                namedParameterMap.put("beginDate", map.get("BeginOfWeek"));
                namedParameterMap.put("endDate", map.get("EndOfWeek"));
                break;
            case 2://每月
                namedParameterMap.put("beginDate", map.get("BeginOfMonth"));
                namedParameterMap.put("endDate", map.get("EndOfMonth"));
                break;
            case 3://每季度
                namedParameterMap.put("beginDate", map.get("BeginOfQuarter"));
                namedParameterMap.put("endDate", map.get("EndOfQuarter"));
                break;
           case 4://指定期限
        	   if(beginOfDate != null){
        		   Timestamp beginOfDateTS = new Timestamp(beginOfDate.getTime());
        		   namedParameterMap.put("beginDate", beginOfDateTS);
        	   }
        	   if(endOfDate != null){
        		   Timestamp endOfDateTS = new Timestamp(endOfDate.getTime());
                   namedParameterMap.put("endDate", endOfDateTS);
        	   }
               break;
       }
       
       List results = new ArrayList();
       
       List<Long> memberIdList = (List)namedParameterMap.get("MEMBERIDS");
       
       List<Long>[] memberIdArray = Strings.splitList(memberIdList, 500);
       if( memberIdArray!=null && memberIdArray.length!=0 ){
	       for(int i=0 ; i<memberIdArray.length ; i++){
	    	   List<Long> tempMemberIdList = memberIdArray[i];
	    	   namedParameterMap.put("MEMBERIDS", tempMemberIdList);
	    	   List tempList = find(hqlBf.toString(), -1, -1, namedParameterMap);
	    	   results.addAll( tempList );
	       }
       }
       Map<Long, int[]> resultMap = new HashMap<Long, int[]>();
       for (int i = 0; i < results.size(); i++) {
           Object[] result = (Object[]) results.get(i);
           int count = (Integer) result[0];
           int state = (Integer) result[1];
           Long memberId = (Long)result[2];
           if(state != StateEnum.col_pending.key() && state != StateEnum.col_done.key()){
        	   continue;
           }
           int[] stat = resultMap.get(memberId);
           int index = 0;
           if (state == StateEnum.col_pending.key()) {
               index = 0;
           }
           else if (state == StateEnum.col_done.key()) {
               index = 1;
           }
           if(stat == null){
               stat = new int[2];
           }
           stat[index] += count;
           resultMap.put(memberId, stat);
       }
       return resultMap;
    }
    
    private static final String selectAffair = "summary.id," +
    "summary.canArchive," +
    "summary.subject," +
    "summary.importantLevel," +
    "summary.startMemberId," +
    "summary.forwardMember," +
    "summary.createDate," +
    "summary.startDate," +
    "summary.finishDate," +
    "summary.resentTime," +
    "summary.deadline," +
    "summary.bodyType," +
    "summary.identifier," +
    "summary.caseId," +
    "summary.processId," +
    "summary.templeteId," +
    "summary.source," +
    "summary.archiveId," +
    "affair.id," +
    "affair.state," +
    "affair.subState," +
    "affair.isTrack," +
    "affair.hastenTimes," +
    "affair.isOvertopTime," +
    "affair.remindDate," +
    "affair.deadlineDate," +
    "affair.receiveTime," +
    "affair.completeTime," +
    "affair.createDate," +
    "affair.memberId," +
    "affair.transactorId ";
    
    private static void make(Object[] object, ColSummary summary, Affair affair){
        int n = 0;
        summary.setId((Long)object[n++]);
        summary.setCanArchive((Boolean)object[n++]);
        String subject = (String)object[n++];
        summary.setImportantLevel((Integer)object[n++]);
        summary.setStartMemberId((Long)object[n++]);
        summary.setForwardMember((String)object[n++]);
        summary.setCreateDate((Timestamp)object[n++]);
        summary.setStartDate((Timestamp)object[n++]);
        summary.setFinishDate((Timestamp)object[n++]);
        summary.setResentTime((Integer)object[n++]);
        summary.setDeadline((Long)object[n++]);
        summary.setBodyType((String)object[n++]);
        summary.setIdentifier((String)object[n++]);
        summary.setCaseId((Long)object[n++]);
        summary.setProcessId((String)object[n++]);
        summary.setTempleteId((Long)object[n++]);
        String source = (String)object[n++];
        if(Strings.isNotBlank(source)){
            subject += "(" + source + ")";
            summary.setSource(source);
        }
        summary.setSubject(subject);
        summary.setArchiveId((Long)object[n++]);
        
        affair.setId((Long)object[n++]);
        affair.setState((Integer)object[n++]);
        affair.setSubState((Integer)object[n++]);
        affair.setIsTrack((Boolean)object[n++]);
        affair.setHastenTimes((Integer)object[n++]);
        affair.setIsOvertopTime((Boolean)object[n++]);
        affair.setRemindDate((Long)object[n++]);
        affair.setDeadlineDate((Long)object[n++]);
        affair.setReceiveTime((Timestamp)object[n++]);
        affair.setCompleteTime((Timestamp)object[n++]);
        affair.setCreateDate((Timestamp)object[n++]);
        affair.setMemberId((Long)object[n++]);
        affair.setTransactorId((Long)object[n++]);  
    }
    
	private static final String selectSummary = "summary.id,summary.startUserId,summary.caseId,summary.completeTime"+
	",summary.subject,summary.secretLevel,summary.identifier,summary.docMark,summary.serialNo,summary.createTime"+
	",summary.sendTo,summary.issuer,summary.signingDate,summary.deadline,summary.startTime,summary.copies,summary.createPerson,summary.sendUnit,summary.hasArchive";

	private static final String edocSelectAffair = selectSummary+
	",affair.id," +
	"affair.state," +
	"affair.subState," +
	"affair.isTrack," +
	"affair.hastenTimes," +
	"affair.isOvertopTime," +
	"affair.remindDate," +
	"affair.deadlineDate," +
	"affair.receiveTime," +
	"affair.completeTime," +
	"affair.createDate," +
	"affair.memberId,affair.bodyType,affair.transactorId,affair.app,affair.objectId,affair.subObjectId,affair.importantLevel ";
    
	private static void makeEdoc(Object[] object, EdocSummary summary, Affair affair)
	{
		int n = 0;
		summary.setId((Long)object[n++]);
		summary.setStartUserId((Long)object[n++]);
        summary.setCaseId((Long)object[n++]);
        summary.setCompleteTime((Timestamp)object[n++]);        
        summary.setSubject((String)object[n++]);
        summary.setSecretLevel((String)object[n++]);
        summary.setIdentifier((String)object[n++]);
        summary.setDocMark((String)object[n++]);
        summary.setSerialNo((String)object[n++]);
        summary.setCreateTime((Timestamp)object[n++]);
        summary.setSendTo((String)object[n++]);
        summary.setIssuer((String)object[n++]);
        summary.setSigningDate((java.sql.Date)object[n++]);
        summary.setDeadline((Long)object[n++]);
        summary.setStartTime((Timestamp)object[n++]);
        summary.setCopies((Integer)object[n++]);
        summary.setCreatePerson((String)object[n++]);
        summary.setSendUnit((String)object[n++]);
        summary.setHasArchive((Boolean)object[n++]);
        
        affair.setId((Long)object[n++]);
		affair.setState((Integer)object[n++]);
		affair.setSubState((Integer)object[n++]);
		affair.setIsTrack((Boolean)object[n++]);
		affair.setHastenTimes((Integer)object[n++]);
		affair.setIsOvertopTime((Boolean)object[n++]);
		affair.setRemindDate((Long)object[n++]);
		affair.setDeadlineDate((Long)object[n++]);
		affair.setReceiveTime((Timestamp)object[n++]);
		affair.setCompleteTime((Timestamp)object[n++]);
		affair.setCreateDate((Timestamp)object[n++]);
		affair.setMemberId((Long)object[n++]);
		affair.setBodyType((String)object[n++]);
		affair.setTransactorId((Long)object[n++]);
		affair.setApp((Integer)object[n++]);
		affair.setObjectId((Long)object[n++]);
		affair.setSubObjectId((Long)object[n++]);
		affair.setImportantLevel((Integer)object[n++]);
		summary.setImportantLevel(affair.getImportantLevel());
	}

    /**
     * 封装应用ID，如app=4为公文，则需要查询其他公文类别ID
     * @param app
     * @return
     */
    private static List<Integer> getAppList(int app){
        List<Integer> appList = new ArrayList<Integer>();
        if(app == ApplicationCategoryEnum.edoc.key()){
            appList.add(ApplicationCategoryEnum.edoc.key());
            appList.add(ApplicationCategoryEnum.edocRec.key());
            appList.add(ApplicationCategoryEnum.edocRegister.key());
            appList.add(ApplicationCategoryEnum.edocSend.key());
            appList.add(ApplicationCategoryEnum.edocSign.key());
            appList.add(ApplicationCategoryEnum.exchange.key());
            appList.add(ApplicationCategoryEnum.exSend.key());
            appList.add(ApplicationCategoryEnum.exSign.key());
        }
        else{
            appList.add(app);
        }
        return appList;
    }
    /**
     * 封装应用ID，如app=4为公文，则需要查询其他公文类别ID
     * @param app
     * @return
     */
    private static List<Integer> getAppNormalList(int app){
        List<Integer> appList = new ArrayList<Integer>();
        if(app == ApplicationCategoryEnum.edoc.key()){
            appList.add(ApplicationCategoryEnum.edoc.key());
            appList.add(ApplicationCategoryEnum.edocRec.key());
            appList.add(ApplicationCategoryEnum.edocSend.key());
            appList.add(ApplicationCategoryEnum.edocSign.key());
        }
        else{
            appList.add(app);
        }
        return appList;
    }
	/**
	 * 根据domainId查找管理授权设置记录
	 * 方法描述：
	 *
	 */
	public List<ManagementSet> findSetListByDomainId(long domainId){
		List<ManagementSet> list =  managementSetDao.findByDomainId(domainId);
		ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources",CurrentUser.get().getLocale());
		for(ManagementSet set : list){
			String s = set.getManageRange();
			String typeNames = "";
			if(!Strings.isBlank(s)){
				String[] types = s.split("\\&");
				for(String type : types){
						typeNames +=  ResourceBundleUtil.getString(r, "application."+type+".label") + ",";
				}
				if(typeNames.endsWith(",")){
					typeNames = typeNames.substring(0, typeNames.lastIndexOf(","));
				}
				set.setTypeNames(typeNames);
			}
		}
		return list;
	}
	
	public ManagementSet findById(long id){
		ManagementSet set = managementSetDao.get(id);
		List<ManagementSetAcl> acls = managementSetAclDao.findBySetId(id);
		Set<ManagementSetAcl> sets = new HashSet<ManagementSetAcl>();
		for(ManagementSetAcl acl: acls){
			sets.add(acl);
		}
		set.setManagementSetAcls(sets);
		return set;
	}
	
	public void updateSet(ManagementSet set){
		managementSetDao.update(set);
	}
	
	private void deleteSetAcls(long id){
		managementSetAclDao.deleteAclsBySetId(id);
		managementSetDao.delete(id);
	}
	
	public void deleteSetAndAcls(String setId){
		String[] ids = setId.split(",");
		for(String id : ids){
			this.deleteSetAcls(Long.valueOf(id));
		}
	}

	/**
	 * @return the managementSetAclDao
	 */
	public ManagementSetAclDao getManagementSetAclDao() {
		return managementSetAclDao;
	}

	/**
	 * @param managementSetAclDao the managementSetAclDao to set
	 */
	public void setManagementSetAclDao(ManagementSetAclDao managementSetAclDao) {
		this.managementSetAclDao = managementSetAclDao;
	}
	
	public void saveSet(ManagementSet set){
		managementSetDao.save(set);
	}
	
	public void saveSetAcls(ManagementSetAcl acl){
		managementSetAclDao.save(acl);
	}
	
	public void saveSetAndAcls(String personId, String aclId, String manageRange){
		User user = CurrentUser.get();
		ManagementSet set = new ManagementSet();
		set.setIdIfNew();
		set.setCreateTime(new java.sql.Timestamp(new Date().getTime()));
		set.setCreateUserId(user.getId());
		set.setDomainId(user.getLoginAccount());
		set.setShowContent(com.seeyon.v3x.collaboration.Constant.workManageSetShowContent.show.ordinal());
		set.setExtConfigure(com.seeyon.v3x.collaboration.Constant.workManageSetShowContent.show.ordinal());
		set.setLastUpdate(new java.sql.Timestamp(new Date().getTime()));
		set.setLastUserId(user.getLoginAccount());
		set.setManageRange(manageRange);
		set.setMemberId(personId);
		
		this.saveSet(set);
		
		String[] acls = aclId.split(",");
		for(String ids : acls){
			ManagementSetAcl acl = new ManagementSetAcl();
			acl.setIdIfNew();
			String[] bDeptId = ids.split("\\|");
			acl.setManagementSetId(set.getId());
			acl.setAclId(Long.valueOf(bDeptId[1]));
			acl.setAclType(bDeptId[0]);
			this.saveSetAcls(acl);
		}
	}
	
	public void updateSetAndAcls(long id,String personId, String aclId, String manageRange){
		User user = CurrentUser.get();
		ManagementSet set = this.findById(id);
		
		if(null!=set){
		this.deleteAclsBySetId(set.getId());
		
		set.setLastUpdate(new java.sql.Timestamp(new Date().getTime()));
		set.setLastUserId(user.getLoginAccount());
		set.setManageRange(manageRange);
		set.setMemberId(personId);
		
		this.updateSet(set);
		
		String[] acls = aclId.split(",");
		for(String ids : acls){
			ManagementSetAcl acl = new ManagementSetAcl();
			acl.setIdIfNew();
			String[] bDeptId = ids.split("\\|");
			acl.setManagementSetId(set.getId());
			acl.setAclId(Long.valueOf(bDeptId[1]));
			acl.setAclType(bDeptId[0]);
			this.saveSetAcls(acl);
		}
		}
	}
	
	private void deleteAclsBySetId(long setId){
		managementSetAclDao.deleteAclsBySetId(setId);
	}
	
	public List<Long> getMembersByGrantorIdAndType(long domainId,long memberId , int type){
		
		List<ManagementSet> list =  managementSetDao.findByGrantorIdAndType(domainId,memberId,type);
		List<Long> memberList = new ArrayList<Long>();
		Set <Long>memberSet = new HashSet<Long>();
		for(ManagementSet set : list){
			List<ManagementSetAcl> aclList = managementSetAclDao.findBySetId(set.getId());
			for(ManagementSetAcl acl : aclList){
				if(acl.getAclType().equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT)){
					try{
						List<V3xOrgMember> mList = null;
						V3xOrgDepartment department = orgManager.getDepartmentById(acl.getAclId());
						if(department != null && department.getIsInternal()){
							mList = orgManager.getMembersByDepartment(acl.getAclId(), false);
						}else{
							mList = orgManager.getExtMembersByDepartment(acl.getAclId(), false);
						}
						if(mList != null){
							for(V3xOrgMember m : mList){
								memberSet.add(m.getId());
							}
						}
					}catch(Exception e){
						log.error(" 工作管理》 OrgManager 查询人员错误 : ",e);
					}

				}else{
					try {
						Set<V3xOrgMember> members = orgManager.getMembersByType(acl.getAclType(), acl.getAclId());
						if(members != null){
							for(V3xOrgMember m : members){
								memberSet.add(m.getId());
							}
						}
					} catch (BusinessException e) {
						log.error(" 工作管理》OrgManager 查询人员错误 : ",e);
					}
					
				}
			}
		}
		
		for(Long mId : memberSet){
			memberList.add(mId);
		}
		return memberList;
	}
	
	public boolean hasThePermission(long domainId,long memberId){
		
		List<ManagementSet> list =  managementSetDao.findByGrantorId(domainId,memberId);
		if(null == list || list.size() == 0){
			return false;
		}
		return true;
	}
    
	//如果时间为空 组成的sql 语句
	private void ifTheDateIsNull(Date beginDate,Date endDate,StringBuffer hql,String begin,String end){
		if(beginDate == null && endDate == null) return ;
		hql.append(" and (("+(beginDate !=null?(endDate !=null?" affair.createDate > :"+begin+" and affair.createDate < :"+end+" ":" affair.createDate > :"+begin+" "):(endDate != null?" affair.createDate < :"+end+" ":""))+")");
        hql.append(" or ("+(beginDate != null? (endDate != null?" affair.receiveTime > :"+begin+" and affair.receiveTime < :"+end+" ":" affair.receiveTime > :"+begin+" "):(endDate != null?" affair.receiveTime < :"+end+" ":""))+")");
        hql.append(" or ("+(beginDate !=null ? (endDate != null?" affair.completeTime > :"+begin+" and affair.completeTime < :"+end+" ":" affair.completeTime > :"+begin+" "):(endDate !=null?" affair.completeTime < :"+end+" ":""))+"))");
	}
	
	//判断是否重复设置管理权限
	public String checkRepeatPermission(String memberId,String grantId,String manageRange,Long id){
		//1.查询授权人的管理范围 2.判断是否有重复的。将重复信息全部打出来
		if(Strings.isBlank(memberId) || Strings.isBlank(grantId) || Strings.isBlank(manageRange)){
			return null;
		}
		StringBuffer warnning = new StringBuffer();
		ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources",CurrentUser.get().getLocale());
		ResourceBundle coll = ResourceBundle.getBundle("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource",CurrentUser.get().getLocale());
		Long domainId  = CurrentUser.get().getLoginAccount();
		String[] members = memberId.split(",");
		String[] ranges = manageRange.split("\\&");
		String[] grantIds = grantId.split(",");
		for(String member : members){
			List<ManagementSet> manager = managementSetDao.findByGrantorId(domainId,Long.parseLong(member));
			StringBuffer repeatGrant = new StringBuffer();//管理范围
			StringBuffer repeatRange = new StringBuffer();//管理事项
			V3xOrgMember repeatMember = null;
			try {
				repeatMember = orgManager.getMemberById(Long.parseLong(member));
			} catch (Exception e1) {
				log.error(e1.getMessage(), e1);
			}
			//判断管理对象是否有重复的
			for(ManagementSet set : manager){
				if(id != null && id != -1L && set.getId().longValue() == id)continue;
				for(ManagementSetAcl acl : set.getManagementSetAcls()){
					for(String grant : grantIds){
						String grants[] = grant.split("\\|");
						if(acl.getAclType().equals(grants[0]) && acl.getAclId().toString().equals(grants[1])){
							if(repeatGrant.length() != 0){
								repeatGrant.append(",");
							}
							try {
								V3xOrgEntity entity = orgManager.getEntity(grants[0], acl.getAclId());
								repeatGrant.append(entity.getName());
							} catch (BusinessException e) {
								log.error(e.getMessage(), e);
							}
						}
					}
				}
				if(repeatGrant.length() != 0){
					//有重复的管理范围 判断是否有重复的管理事项
					for(String range : ranges){
						if(set.getManageRange().indexOf(range) > -1){
							//重复的管理事项
							if(repeatRange.length() != 0){
								repeatRange.append(",");
							}
							repeatRange.append(ResourceBundleUtil.getString(r, "application."+range+".label"));
						}
					}
					if(repeatRange.length() != 0){
						if(warnning.length() != 0){
							warnning.append("\n");
						}
						if(repeatMember != null){
							warnning.append(ResourceBundleUtil.getString(coll, "work.manage.repeat.set",repeatMember.getName(),repeatGrant.toString(),repeatRange.toString()));
						}
					}
				}
			}
		}
		return warnning.length() > 0? warnning.toString():null;
	}
	
}