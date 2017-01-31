package com.seeyon.v3x.edoc.manager;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.joinwork.bpm.definition.BPMProcess;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.domain.ColTrackMember;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.WorkflowData;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.processlog.domain.ProcessLog;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocBody;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.webmodel.EdocOpinionModel;
import com.seeyon.v3x.edoc.webmodel.EdocSearchModel;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;
import com.seeyon.v3x.edoc.webmodel.MoreSignSelectPerson;

public interface EdocManager {

    Long runCase(FlowData flowData, EdocSummary summary, EdocBody body, EdocOpinion senderOpinion, EdocEnum.SendType sendType, Map options,String from,
    		Long agentToId)
    throws EdocException;

    // 签收工作项
    void claimWorkItem(int workItemId) throws EdocException;

    // 完成工作项，第3个参数是当下一结点需要人工选择执行人时，由这里传入。注意：是下一结点的执行人。
    String finishWorkItem(EdocSummary summary,long affairId, EdocOpinion signOpinion,Map<String,String[]> manualMap, Map<String,String> condition, String processId, String userId, String edocMangerID) throws EdocException;
    /**
     *判断当前事项是否能指定的操作 。
     * @param affairIds  : 个人事项ID
     * @param operationName ： 操作名(如：DepartPigeonhole)
     * @return 当传入的事项都有权限的时候，返回为空值，当传入的某些事项没有指定操作的权限的时候，返回没有权限的事项的标题。
     */
    public String checkHasAclNodePolicyOperation(String affairIds,String operationName);
    /**
     * 重载完成工作项，增加督办参数
     * @param edocMangerID TODO
     */
    String finishWorkItem(EdocSummary summary,long affairId, EdocOpinion signOpinion,
			Map<String, String[]> manualMap, Map<String,String> condition,String title,String supervisorMemberId,String supervisorNames,String superviseDate, String processId, String userId, String edocMangerID)throws Exception;

    /**
     * 暂存待办
     *
     * @param summaryId
     * @param opinion
     * @throws ColException
     */
    public void zcdb(EdocSummary edocSummary, Affair affair, EdocOpinion opinion, String processId, String userId) throws EdocException;
    
    /**
     * 暂存待办保存方式
     * @param affairId
     * @param opinion
     * @param remindMode
     * @param supervisorMemberId
     * @param supervisorNames
     * @param superviseDate
     * @throws EdocException
     */
    public void zcdb(Affair affair, EdocOpinion opinion ,String title,String supervisorMemberId,String supervisorNames,String superviseDate, EdocSummary summary, String processId, String userId) throws EdocException;
    
    public void deleteEdocOpinion(Long opinionId) throws EdocException;

//    // 完成工作项
//    void finishWorkItem(int workItemId, String opinionContent) throws EdocException;

    // 保存一个流程草稿（保存待发）

    Long saveDraft(FlowData flowData, EdocSummary summary, EdocBody body, EdocOpinion senderOpinion)
            throws EdocException;

    // 查询待办列表
    List<EdocSummaryModel> queryTodoList(int edocType) throws EdocException;
    //重写
    List<EdocSummaryModel> queryTodoList(int edocType,Integer edocSecretLevel) throws EdocException;

    // 查询已办列表
    List<EdocSummaryModel> queryFinishedList(int edocType) throws EdocException;
    //重写queryFinishedList
    List<EdocSummaryModel> queryFinishedList(int edocType,Integer edocSecretLevel) throws EdocException;

    // 查询已发列表
    List<EdocSummaryModel> querySentList(int edocType) throws EdocException;
    //成发集团 程炯 2012-8-31 重写querySentList
    List<EdocSummaryModel> querySentList(int edocType,Integer edocSecretLevel) throws EdocException;

    // 查询未发列表
    List<EdocSummaryModel> queryDraftList(int edocType) throws EdocException;
    //重写
    List<EdocSummaryModel> queryDraftList(int edocType,Integer edocSecretLevel) throws EdocException;

    List<EdocSummaryModel> queryTrackList(int edocType) throws EdocException;

    /**
     * 列出我的待办、已办、已发
     *
     * @param condition
     * @param field
     * @param field1
     * @return
     */
    public List<EdocSummaryModel> queryByCondition4Quote(ApplicationCategoryEnum appEnum, String condition,
            String field, String field1);

    /**
     * 查询正文
     *
     * @param summaryId
     * @return
     * @throws EdocException
     */
    EdocBody getEdocBody(long summaryId) throws EdocException;

    /**
     * 通过id查找对应的EdocSummary
     *
     * @param summaryId
     * @param needBody  默认false
     * @return
     * @throws EdocException
     */
    public EdocSummary getEdocSummaryById(long summaryId, boolean needBody) throws EdocException;
    
    public EdocSummary getColAllById(long summaryId) throws EdocException;

    //删除一个个人事项
    public void deleteAffair(String pageType, long affairId) throws EdocException;
    //归档
    public void pigeonholeAffair(String pageType, long affairId, Long summaryId) throws EdocException;
    public void pigeonholeAffair(String pageType, Affair affair, Long summaryId) throws EdocException;  
    public void pigeonholeAffair(String pageType,Affair affair, Long summaryId,Long archiveId,boolean needcheckFinish) throws EdocException;
    /**
     * 公文单位归档
     * @param pageType
     * @param affairId   ：当前Affair对象的ID
     * @param summaryId : 当前Summary对象的ID
     * @param archiveId ：当前归档路径
     * @throws EdocException
     */
    public void pigeonholeAffair(String pageType, long affairId, Long summaryId,Long archiveId) throws EdocException;
    public void pigeonholeAffair(String pageType, Affair affair, Long summaryId,Long archiveId) throws EdocException;    

   //根据caseId得到对应的summary
    public EdocSummary getSummaryByCaseId(long caseId) throws EdocException;
    
    public Long getSummaryIdByCaseId(long caseId) throws ColException;

    //根据caseId得到对应的summary
    public EdocSummary getSummaryByWorkItemId(int workItemId) throws EdocException;

    public String getCaseLogXML(long caseId) throws EdocException;

    public String getCaseWorkItemLogXML(long caseId) throws EdocException;

    public String getCaseProcessXML(long caseId) throws EdocException;

    public int cancelSummary(long userId, long summaryId, String repealComment) throws EdocException;
    
    public int cancelSummary(long userId, long summaryId, String repealComment, EdocOpinion edocOpinion) throws EdocException;

    public List<EdocSummaryModel> queryByCondition(int edocType,String condition, String field, String field1, int state);
    //成发集团项目 程炯 2012-8-31 重写queryByCondition
    public List<EdocSummaryModel> queryByCondition(int edocType,String condition, String field, String field1, int state,Integer edocSecretLevel);

    //加签,多级会签，最后一个参数设置操作类型
    public void insertPeople(EdocSummary summary, Affair affair, FlowData flowData, BPMProcess process, String userId,String operationType) throws EdocException;

    //减签前返回可减签的人员列表
    public FlowData preDeletePeople(long summaryId, long affairId, String processId, String userId) throws EdocException;

    public FlowData deletePeople(long summaryId, long affairId, List<Party> parties, String userId) throws EdocException;

    //通过processId得到相应的xml定义文件
    public String getProcessXML(String processId) throws EdocException;

    //回退
    //@return true:成功回退 false:不允许回退
    public boolean stepBack(Long summaryId, Long affairId, EdocOpinion signOpinion) throws EdocException;    

    //终止    
    public boolean stepStop(Long summaryId, Long affairId, EdocOpinion signOpinion) throws EdocException;
    
    //取回
    //@return true:成功取回 false:不允许取回
    public boolean takeBack(Long affairId) throws EdocException;

    //会签
    public void colAssign(Long summaryId, Long affairId, FlowData flowData, String userId) throws EdocException;

    //知会
    public void addInform(Long summaryId, Long affairId, FlowData flowData, String userId) throws EdocException;
    //传阅
    public void addPassRead(Long summaryId, Long affairId, FlowData flowData) throws EdocException;

    //催办
    public void hasten(String processId, String activityId, String additional_remark) throws EdocException;

    /**
     * 修改正文-保存
     *
     * @throws EdocException
     */
    public void saveBody(EdocBody body) throws EdocException;

    public void saveOpinion(EdocOpinion opinion,boolean isSendMessage) throws EdocException;

    /**
     * 转发
     *
     * @param summaryId
     * @param forwardOriginalNode
     * @param foreardOriginalopinion
     * @return
     * @throws EdocException
     */
    public EdocSummary saveForward(Long summaryId, FlowData flowData, boolean forwardOriginalNode,
                                  boolean foreardOriginalopinion, EdocOpinion senderOpinion) throws EdocException;

//    public StatModel PersonalStatFilter(long user_id);

    /**
     * 回复意见
     *
     * @param comment
     * @throws EdocException
     */
//    public void saveComment(ColComment comment) throws EdocException;

    public String getPolicyBySummary(EdocSummary summary) throws EdocException;

    public String getPolicyByAffair(Affair affair) throws EdocException;

    /**
     * 更新EdocSummary行，指定字段
     *
     * @param affairId
     * @param columnValue key-字段名， value-值
     */
    public void update(Long summaryId, Map<String, Object> columns);
    public void update(EdocSummary summary) throws Exception;

    public void setFinishedFlag(long summaryId, int summaryState) throws EdocException;
    public void setFinishedFlag(long summaryId, 
    							int summaryState,
    							Long runTime,
    							Long runWorkTime,
    							Long overTime,
    							Long overWorkTime) throws EdocException;
    
    public boolean updateHtmlBody(long bodyId,String content) throws EdocException;
    
    
    /**
     * 取当前公文的所有的意见。
     * key:公文元素代码 | otherOpinion | senderOpinion（文单里面：niwen or dengji)
     * @param summaryId
     * @return
     */
    public Map<String,EdocOpinionModel> getEdocOpinion(EdocSummary summary,boolean isOnlyShowLastOpinion);
    
    public Map<String,EdocOpinionModel> getEdocOpinion(EdocSummary summary);
    
    public LinkedHashMap getEdocOpinion(Long summaryId,Long curUser,Long sender) throws EdocException;
    
    /**
     * 根据设置位置,对处理意见近一步整理
     * @param hsOpinion:根据节点权限整理好的意见
     * @return
     * @throws EdocException
     */
    public Hashtable getEdocOpinion(Long edocFormId,LinkedHashMap hsOpinion) throws EdocException;
   /**
    * 
    * @param summaryId
    * @param aclAccountId  :公文单被授权使用的单位
    * @param curUser
    * @param sender
    * @return
    * @throws EdocException
    */
    public LinkedHashMap getEdocOpinion(EdocSummary summary,Long aclAccountId,Long curUser,Long sender) throws EdocException;
    
    /**
     * 根据设置位置,对处理意见近一步整理
     * @param hsOpinion:根据节点权限整理好的意见
     * @param aclAccountId : 公文单被授权使用的单位.
     * @return
     * @throws EdocException
     */
    public Hashtable getEdocOpinion(Long edocFormId,Long  aclAccountId,LinkedHashMap hsOpinion) throws EdocException;
    /**
     * 待发列表，点击理解发送
     * @param summary
     * @param map：  调用模版时候，角色匹配选择人员数据
     * @throws EdocException
     */
    public void sendImmediate(Long affairId,EdocSummary summary,FlowData flowData) throws EdocException;
    /**
     * 查询公文处理意见,用于咱存待办
     * @param summaryId
     * @param affairId
     * @return
     */
    public EdocOpinion findBySummaryIdAndAffairId(long summaryId,long affairId);
    
    /**
     * 正文套红之后生成保存正文
     * @param summaryId
     * @param contentNum
     * @param srcOfficeId：原始office控件ID
     * @return
     */
    public String createContentBody(String summaryId,int contentNum,String srcOfficeId);
    /**
     * 正文套红之后生成保存正文
     * @param summaryId
     * @param contentNum
     * @param srcOfficeId：原始office控件ID
     * @param bodyType 
     * @return
     */
    public String createContentBody(String summaryId,int contentNum,String srcOfficeId,String bodyType);


    /**
     * 得到修改中的流程实例对应的流程定义
     * @param processId
     * @return
     */
    public String getModifyingProcessXML(String processId) throws ColException;
    
    /**
	 * 综合查询
	 */
	public List<ResultModel> iSearch(ConditionModel cModel);	
	/**
	 * 检查是否使用了枚举值，删除枚举值的时候，调用校验
	 * @param metadataId
	 * @param value
	 * @return
	 */
	public boolean useMetadataValue(Long domainId,Long metadataId,String value);
	
	/**
	 * 获取流程详细信息
	 * @param subject
	 * @param beginDate
	 * @param endDate
	 * @param objectStrs
	 * @param endFlag
	 * @return
	 */
    public List<WorkflowData> queryWorkflowDataByCondition(String subject, String beginDate, String endDate, List<String> objectStrs,
    		int endFlag, int appKey, String operationType, String[] operationTypeIds, boolean paginationFlag);
    
    
    public EdocSummary getSummaryByProcessId(String processId);
    public List<EdocSummaryModel> queryByCondition(long curUserId,EdocSearchModel em);
    /**
     * 公文查询
     * @param curUserId
     * @param em
     * @param needByPage 是否需要分页
     * @return
     */
    public List<EdocSummaryModel> queryByCondition(long curUserId,EdocSearchModel em,boolean needByPage);
    public List<EdocSummaryModel> queryByCondition(long curUserId,EdocSearchModel em,boolean needByPage,Integer secretLevel);//成发集团项目
    public List<MoreSignSelectPerson> findMoreSignPersons(String typeAndIds);
    
    /**
     * 供公文统计使用，仅仅查询出公文统计表中不存在的秘密级别字段
     * @param ids
     * @return
     */
    public Hashtable<Long,EdocSummary> queryBySummaryIds(List<Long> ids);
    
    /**
     * AJAX方法记录流程日志
     * 主要是记录的是公文对正文的操作
     * @param affairId
     * @param summaryId
     */
    public void recoidChangeWord(String affairId , String summaryId , String changeType,String userId);
    
    /**
     * 修改附件
     * @param edocSummary
     * @throws Exception
     */
    public void updateAttachment(EdocSummary edocSummary,Affair affair,User user,HttpServletRequest request)throws Exception;

    public void saveUpdateAttInfo(int attSize,Long summaryId,List<ProcessLog> logs);
    public String getFullArchiveNameByArchiveId(Long archiveId);
    public  String getShowArchiveNameByArchiveId(Long archiveId);
    /**
     * 设置事项的ArchiveId,并且发送消息
     * @param summary
     * @param needSendMessage
     */
    public void setArchiveIdToAffairsAndSendMessages(EdocSummary summary,Affair affair,boolean needSendMessage);

	/**
	 * 异步判断公文是否可以被撤销
	 * 
	 * @param summaryId4Check
	 *            公文id
	 * @return msgInfo(edoc.state.end.alert) ；Y 可以撤销
	 */
	public String checkIsCanBeRepealed(String summaryId4Check);
	public List<ColTrackMember> getColTrackMembersByObjectIdAndTrackMemberId(Long objectId,Long trackMemberId);
	public void deleteColTrackMembersByObjectId(Long objectId) ;
	/**
	  * 根据office正文附件ID反查公文正文
	  * 该方法会比较耗性能，但目前公文是基于上个版本开发的，还得沿用之前的接口，
	  * 下个版本将废弃此方法。
	  * @param fileid
	  * @return
	  * @throws ColException
	  */
	public EdocBody getEdocBodyByFileid(long fileid);
	
	/**
	  * queryPendingByUserAndApp
	  * @param userid 用户Id
	  * @param appType 应用类型：协同应用、公文应用等，see:com.seeyon.v3x.common.constants.ApplicationCategoryEnum
	  * @param stateType 状态类型：待办、已办等，see:com.seeyon.v3x.affair.constants.StateEnum
	  * @param isTemplate 是否为模板流程
	  * @param isImportant 是否为重要流程
	  * @param isCommon 是否为普通流程
	  * @param condition 条件类型
	  * @param textfield 查询条件值
	  * @param textfield1 查询条件值1
	  * @return
	  */
	List<EdocSummaryModel> queryPendingByUserAndApp(
			String userid,
			int appType, 
			int stateType,  
			boolean isImportant, 
			boolean isCommon,
			String condition,
			String textfield,
			String textfield1);
	
	/**
	 * queryPendingCountByGroup
	 * @param userid 用户Id
	 * @param stateType 状态类型：待办、已办等，see:com.seeyon.v3x.affair.constants.StateEnum
	 * @return
	 */
	Map<Integer,Integer> queryPendingCountByGroup(String userid, int stateType);
	
	/**
	 * 查询指定类型公文的问号列表，返回问号格式如下:
	 * [ { value:"010", label:"Beijing北京"}, { value:"020", label:"guangzhou广州" }, { value:"021",label:"shanghai上海"} ]
	 * @param edocType 应用类型
	 * @param state 状态
	 * @return
	 */
	public String queryMarkList(int edocType, int state);
	
	public void setTrack(Long affairId,boolean isTrack,String trackMembers);
}
