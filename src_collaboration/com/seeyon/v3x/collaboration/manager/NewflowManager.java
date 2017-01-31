package com.seeyon.v3x.collaboration.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.collaboration.domain.NewflowRunning;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.webmodel.NewflowModel;

/**
 *  表单分段流程 - 新流程发起
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 */
public interface NewflowManager
{
    /**
     * 新流程设置 - 得到对应的新流程设置
     * @param templeteId 模板ID
     * @param nodeId 节点ID
     * @return
     */
    public List<NewflowSetting> getNewflowSettingList(Long templeteId, String nodeId) throws ColException;
    
    /**
     * 保存新流程设置
     * @param settingList
     */
    public void saveNewFlowSetting(List<NewflowSetting> settingList) throws ColException;
    
    /**
     * 删除新流程设置
     * @param templeteId
     * @param nodeId
     */
    public void deleteNewFlowSetting(Long templeteId, String nodeId) throws ColException;
    
    /**
     * 更新模板配置，先Clear再Add
     * @param templeteId
     * @param newflowSettingList
     * @throws ColException
     */
    public void updateNewFlowSetting(Long templeteId, List<NewflowSetting> newflowSettingList) throws ColException;
    
    /**
     * 判读模板是否已设置了新流程<br>
     * 已设置了新流程的模板不可再被设为新流程
     * @param templeteId
     * @return
     */
    public boolean checkTempleteHasNewflow(Long templeteId) throws ColException;
    
    /**
     * 过滤多个模板中已设置新流程的模板IDs<br>
     * 已设置了新流程的模板不可再被设为新流程
     * @param templeteIds
     * @return
     */
    public List<Long> filterHasNewflowTempletes(List<Long> templeteIds) throws ColException;
    
    /**
     * 得到某模板中所有的新流程设置
     * @param templeteId
     * @return
     * @throws Exception
     */
    public List<NewflowSetting> getNewflowSettingList(Long templeteId) throws ColException;
    /**
     * 拷贝新流程设置信息
     * 有新流程并成功拷贝，返回true
     */
    public boolean copyNewflowInfo(List<NewflowSetting> list, Long mainSummaryId, Long mainAffairId) throws ColException; 
    
    /**
     * 得到新流程运行信息
     */
    public Map<Long, NewflowRunning> getNewflowRunningMap(Long templeteId, String nodeId) throws ColException; 
    /**
     * 得到新流程运行信息
     */
    public Map<Long, NewflowRunning> getNewflowRunningMap(Long templeteId, String nodeId, boolean flag) throws ColException;
    
    /**
     * 主流程触发节点取回，同步撤销子流程
     * @param templeteId
     * @param nodeId
     * @return
     */
    public void recallNewflow(Long templeteId, String nodeId) throws ColException;
    
    /**
     * 根据协同ID召回子流程
     * @param summaryId
     */
    public void recallNewflow(Long summaryId) throws ColException;
    /**
     * 得到新流程设置的Model信息
     * @param templeteId
     * @param nodeId
     * @return
     */
    public List<NewflowModel> getNewflowModelList(Long summaryId, Long templeteId, String nodeId)throws ColException;
    
    /**
     * 新流程发起后，更新运行表信息
     */
    public void updateNewflowRunning(NewflowRunning running);
    
    /**
     * 得到主流程当前节点触发的子流程<br>
     * @param summaryId 主流程模板ID
     * @param nodeIds 需要检测的节点Ids
     * @return
     * @throws Exception
     */
    public List<NewflowRunning> getChildflowRunningList(Long summaryId, List<String> nodeIds) throws ColException;
    
    /**
     * 得到某表单协同关联的新流程<br>
     * flowType 0 : 返回关联的主流程（当前为子流程）<br>
     *          1 : 返回关联的子流程（当前为主流程）<br>
     *          其他 : 未知，返回主流程和子流程
     * @param summaryId
     * @param templeteId
     * @return
     * @throws Exception
     */
    public List<NewflowRunning> getNewflowRunningList(Long summaryId, Long templeteId, int flowType) throws ColException;
    
    /**
     * 得到未结束的新流程标题<br>
     * 主流程中，触发新流程节点的后续节点人员处理时，
     * 如果新流程未结束，且设置了‘新流程结束后主流程才可继续’选项，提示子流程未结束，不能处理。
     * @param prevNodeIdsArray
     * @return
     * @throws Exception
     */
    public String checkHasNoFinishNewflow(Long summaryId, List<String> prevNodeIdsArray) throws ColException;
    
    /**
     * 得到是否有已结束的新流程<br>
     * 主流程中，触发新流程的节点取回时，
     * 如果新流程已结束，则不能取回。
     * @param summaryId 主流程模板ID
     * @param nodeIds 需要检测的节点Ids
     * @return
     * @throws Exception
     */
    public String getFinishedNewflow(Long summaryId, List<String> nodeIds) throws ColException;
    
    /**
     * 根据当前子流程Summary得到受约束的主流程节点ID
     * @param childFlowSummaryId
     * @return
     * @throws ColException
     */
    public NewflowRunning getAffinedMainflow(Long childFlowSummaryId) throws ColException;
    
    /**
     * 是否为关联的新流程
     * @param baseSummaryId 源流程
     * @param relateSummaryId 关联的新流程
     * @return
     * @throws ColException
     */
    public boolean isRelateNewflow(Long baseSummaryId, Long relateSummaryId)throws ColException;
    
    /**
     * 得到新流程运行信息
     */
    public List<String> getNewflowRunningList(Long templeteId) throws ColException; 
    
}
