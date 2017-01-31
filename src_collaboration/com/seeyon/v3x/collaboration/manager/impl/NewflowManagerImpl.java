package com.seeyon.v3x.collaboration.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.collaboration.dao.NewflowDao;
import com.seeyon.v3x.collaboration.domain.NewflowRunning;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.collaboration.webmodel.NewflowModel;

/**
 *
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class NewflowManagerImpl implements NewflowManager
{
    private NewflowDao newflowDao;
    
    public void setNewflowDao(NewflowDao newflowDao) {
        this.newflowDao = newflowDao;
    }

    public void deleteNewFlowSetting(Long templeteId, String nodeId) throws ColException {
        newflowDao.deleteNewFlowSetting(templeteId, nodeId);
    }

    public void updateNewFlowSetting(Long templeteId, List<NewflowSetting> newflowSettingList) throws ColException{
        newflowDao.deleteNewFlowSetting(templeteId, null);
        newflowDao.saveNewFlowSetting(newflowSettingList);
    }
    
    public List<NewflowSetting> getNewflowSettingList(Long templeteId, String nodeId) throws ColException {
        return newflowDao.getNewflowSettingList(templeteId, nodeId);
    }

    public void saveNewFlowSetting(List<NewflowSetting> settingList) throws ColException {
        newflowDao.saveNewFlowSetting(settingList);
    }

    public List<NewflowSetting> getNewflowSettingList(Long mainTempleteId) throws ColException{
        List<NewflowSetting> list = newflowDao.getTempleteNewflowSettingList(mainTempleteId);
        return list;
    }
    
    public boolean copyNewflowInfo(List<NewflowSetting> list, Long mainSummaryId, Long mainAffairId)  throws ColException{
        //COPY之前先清掉脏数据
        newflowDao.clearInvalidRunningData(mainSummaryId);
        if(list != null && !list.isEmpty()){
           List<NewflowRunning> runningList = new ArrayList<NewflowRunning>();
           for(NewflowSetting set : list){
               NewflowRunning run = new NewflowRunning();
               run.setNewId();
               run.setTriggerCondition(set.getTriggerCondition());
               run.setConditionTitle(set.getConditionTitle());
               run.setConditionBase(set.getConditionBase());
               run.setIsForce(set.getIsForce());
               run.setSender(set.getNewflowSender());
               run.setMainSummaryId(mainSummaryId);
               run.setMainTempleteId(set.getTempleteId());
               run.setMainNodeId(set.getNodeId());
               run.setMainAffairId(mainAffairId);
               run.setTempleteId(set.getNewflowTempleteId());
               run.setIsCanViewByMainFlow(set.getIsCanViewByMainFlow());
               run.setIsCanViewMainFlow(set.getIsCanViewMainFlow());
               run.setFlowRelateType(set.getFlowRelateType());
               run.setIsActivate(false);
               run.setCreateTime(new Date());
               run.setAffairState(StateEnum.col_waitSend.key());
               run.setIsDelete(false);
               runningList.add(run);
           }
           newflowDao.saveAll(runningList);
           return true;
       }
       return false;
    }

    public Map<Long, NewflowRunning> getNewflowRunningMap(Long templeteId, String nodeId,boolean flag) throws ColException{
        Map<Long, NewflowRunning> result = new HashMap<Long, NewflowRunning>();
        List<NewflowRunning> list = newflowDao.getNewflowRunningList(templeteId, nodeId,flag);
        if(list != null && !list.isEmpty()){
            for (NewflowRunning running : list) {
                result.put(running.getId(), running);
            }
        }
        return result;
    }
    public Map<Long, NewflowRunning> getNewflowRunningMap(Long templeteId, String nodeId) throws ColException{
        return getNewflowRunningMap(templeteId,nodeId,false);
    }
    /* (non-Javadoc)
     * @see com.seeyon.v3x.collaboration.manager.NewflowManager#recallNewflow(java.lang.Long, java.lang.String)
     */
    public void recallNewflow(Long templeteId, String nodeId) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.seeyon.v3x.collaboration.manager.NewflowManager#recallNewflow(java.lang.Long)
     */
    public void recallNewflow(Long summaryId) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.seeyon.v3x.collaboration.manager.NewflowManager#checkTempleteHasNewflow(java.lang.Long)
     */
    public boolean checkTempleteHasNewflow(Long templeteId) throws ColException{
        return newflowDao.checkTempleteHasNewflow(templeteId);
    }

    /* (non-Javadoc)
     * @see com.seeyon.v3x.collaboration.manager.NewflowManager#getTempletesNewflowCount(java.util.List)
     */
    public List<Long> filterHasNewflowTempletes(List<Long> templeteIds) throws ColException {
        List<Long> resultMap = newflowDao.filterHasNewflowTempletes(templeteIds);
        return resultMap;
    }

    public List<NewflowModel> getNewflowModelList(Long summaryId, Long templeteId, String nodeId) throws ColException{
        return newflowDao.getNewflowModelList(summaryId, templeteId, nodeId);
    }
    
    public void updateNewflowRunning(NewflowRunning running){
        newflowDao.update(running);
        //newflowRunningDao.updateNewflowRunning(running);
    }
    
    public List<NewflowRunning> getChildflowRunningList(Long summaryId, List<String> nodeIds) throws ColException{
        return newflowDao.getChildflowRunningList(summaryId, nodeIds);
    }
    
    public List<NewflowRunning> getNewflowRunningList(Long summaryId, Long templeteId, int flowType) throws ColException{
        return newflowDao.getNewflowRunningList(summaryId, templeteId, flowType);
    }
    
    public String checkHasNoFinishNewflow(Long summaryId, List<String> prevNodeIdsArray) throws ColException{
        List<String> resultList = newflowDao.checkHasNoFinishNewflow(summaryId, prevNodeIdsArray);
        if(resultList != null && !resultList.isEmpty()){
            return resultList.get(0);
        }
        return null;
    }
    
    public String getFinishedNewflow(Long summaryId, List<String> nodeIds) throws ColException{
        List<String> resultList = newflowDao.checkFinishedNewflow(summaryId, nodeIds);
        if(resultList != null && !resultList.isEmpty()){
            return resultList.get(0);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.seeyon.v3x.collaboration.manager.NewflowManager#getAffinedMainflowNodeId(java.lang.Long)
     */
    public NewflowRunning getAffinedMainflow(Long childFlowSummaryId) throws ColException {
        return newflowDao.getAffinedMainflow(childFlowSummaryId);
    }

    public boolean isRelateNewflow(Long baseSummaryId, Long relateSummaryId)throws ColException{
    	return newflowDao.isRelateNewflow(baseSummaryId, relateSummaryId);
    }

	@Override
	public List<String> getNewflowRunningList(Long templeteId)
			throws ColException {
		List<String> runList= newflowDao.getNewflowRunningList(templeteId);
		return runList;
	}
}
