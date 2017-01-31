package com.seeyon.v3x.collaboration.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.joinwork.bpm.definition.BPMProcess;
import www.seeyon.com.v3x.form.manager.define.data.base.DisplayValue;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.Constant.SendType;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColTrackMember;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.WorkflowData;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.webmodel.ColSummaryModel;
import com.seeyon.v3x.collaboration.webmodel.StatModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.processlog.domain.ProcessLog;
import com.seeyon.v3x.edoc.domain.EdocSummary;

public interface ColManager {
	public void save(Object object);
	public void update(Object object);
	
    // 启动一个流程
    Long runCase(FlowData flowData, ColSummary summary, ColBody body, ColOpinion senderOpinion, Constant.SendType sendType, Map options, boolean isNew, Long senderId, String... newProcessId)
            throws ColException;
    
    /**
     * 设置跟踪
     * @param affairId
     * @param isTrack
     * @param trackMembers
     */
    public void setTrack(Long affairId,boolean isTrack,String trackMembers);
    
    // 签收工作项
    void claimWorkItem(int workItemId) throws ColException;

//  完成工作项，第3个参数是当下一结点需要人工选择执行人时，由这里传入。注意：是下一结点的执行人。
    //void finishWorkItem(long affairId, ColOpinion signOpinion,Map<String,String[]> manualMap) throws ColException;
    
    void finishWorkItem(long affairId, ColOpinion signOpinion,Map<String,String[]> manualMap, Map<String, String> condition, String processId, User user) throws ColException;
    /**
     * 暂存待办
     *
     * @param affairId
     * @param opinion
     * @throws ColException
     */
    public void zcdb(long summaryId, Affair affair, ColOpinion opinion, String processId, String userId) throws ColException;

//    // 完成工作项
//    void finishWorkItem(int workItemId, String opinionContent) throws ColException;

    // 保存一个流程草稿（保存待发）

    Long saveDraft(FlowData flowData, ColSummary summary, ColBody body, ColOpinion senderOpinion, boolean isNew)
            throws ColException;

    // 查询待办列表
    List<ColSummaryModel> queryTodoList(List<Long> templeteIds) throws ColException;

    // 查询已办列表
    List<ColSummaryModel> queryFinishedList(List<Long> templeteIds) throws ColException;
    //成发集团项目 程炯 2012-8-31 重写queryFinishedList
    List<ColSummaryModel> queryFinishedList(List<Long> templeteIds,Integer secretLevel) throws ColException;

    // 查询已发列表
    List<ColSummaryModel> querySentList(List<Long> templeteIds) throws ColException;
  //成发集团项目 程炯 2012-8-31 重写querySentList
    List<ColSummaryModel> querySentList(List<Long> templeteIds,Integer secretLevel) throws ColException;

    // 查询未发列表
    List<ColSummaryModel> queryDraftList(List<Long> templeteIds) throws ColException;
  //成发集团项目 程炯 2012-8-31 重写queryDraftList
    List<ColSummaryModel> queryDraftList(List<Long> templeteIds,Integer secretLevel) throws ColException;
    
    /**
     * 专为移动应用提供的接口，主要用于查询待办、已办、待发、已发列表，只取Affair的id、subject字段
     * 
     * @param memberId
     * @param state
     * @param condition
     * @param textfeild
     * @param textfeild1
     * @return
     * @throws ColException
     */
    public List<Affair> queryList4Mobile(Long memberId, StateEnum state,
    		String condition, String textfeild, String textfeild1,Integer... category) throws ColException;

    /**
     * 查询批复意见列表
     *
     * @param summarId
     * @return
     * @throws ColException
     * @deprecated
     */
    List<ColOpinion> queryOpinionListBySummaryId(long summarId) throws ColException;

    List<ColSummaryModel> queryTrackList(List<Long> templeteIds) throws ColException;

    /**
     * 列出我的待办、已办、已发，并根据是否允许转发进行权限过滤，用在协同用引用场景
     *
     * @param state
     * @param condition
     * @param field
     * @param field1
     * @return
     */
    public List<ColSummaryModel> queryByCondition4Quote(StateEnum state, String condition,
                                                        String field, String field1);

    /**
     * 用于分表转储, 不做分页，每次取500条
     * @param beginDate
     * @param endDate
     * @param dataScorp
     * @param flowState
     * @return
     */
    public List<Long> queryByCondition4Store(Date beginDate, Date endDate, String dataScorp, Integer[] flowState);
    
    /**
     * 列出我的待办、已办、已发，并根据是否允许转发进行权限过滤，用在表单协同发起时用引用表单数据场景
     *
     * @param state
     * @param condition
     * @param field
     * @param field1
     * @return
     */
    public List<ColSummaryModel> queryByCondition4QuoteForm(StateEnum state, String condition,
                                                        String field, String field1,String formappid,String quoteformtemId);

    /**
     * 查询正文
     *
     * @param summaryId
     * @return
     * @throws ColException
     */
    ColBody getColBody(long summaryId) throws ColException;

    /**
     * 通过id查找对应的ColSummary
     *
     * @param summaryId
     * @param needBody  默认false
     * @return
     * @throws ColException
     */
    public ColSummary getColSummaryById(long summaryId, boolean needBody) throws ColException;
    
    /**
     * 只查询协同subject、startMemberId、importantLevel、createDate、resentTime
     * 
     * @param summaryId
     * @return
     * @throws ColException
     */
    public ColSummary getSimpleColSummaryById(long summaryId) throws ColException;
    
    /**
     * 取得草稿意见
     * 
     * @param affairId
     * @return
     */
    public ColOpinion getDraftOpinion(long affairId);
    
    /**
     * 删除草稿意见
     * 
     * @param opinionId
     */
    public void deleteDraftOpinion(long opinionId);
    
    /**
     * 同时取出协同summary、body、comment、opinion
     * 
     * @param summaryId
     * @return
     */    
    public ColSummary getColAllById(long summaryId) throws ColException;

    //删除一个个人事项
    public void deleteAffair(String pageType, long affairId) throws ColException;

    public void updateAffair(String pageType, long affairId, long archiveId) throws ColException;

    /**
     * 给summary添加评注
     *
     * @param summaryId
     * @param content
     * @throws ColException
     * @deprecated
     */
    public void addPost(long summaryId, String content) throws ColException;

    //根据caseId得到对应的summary
    public ColSummary getSummaryByCaseId(long caseId) throws ColException;

    //根据caseId得到对应的summary
    public ColSummary getSummaryByWorkItemId(long workItemId) throws ColException;

    //更新指定summary的caseId字段
    public void updateCaseIdOfSummary(long summaryId, long caseId) throws ColException;

    public String getCaseLogXML(long caseId) throws ColException;

    public String getCaseWorkItemLogXML(long caseId) throws ColException;

    public String getCaseProcessXML(long caseId) throws ColException;

    public int cancelSummary(long userId, long summaryId, ColOpinion signOpinion, boolean isSaveOpinion, String repealComment) throws ColException;

    public List<ColSummaryModel> queryByCondition(String condition, String field, String field1, int state, List<Long> templeteIds);
    //成发集团项目 程炯 2012-8-31 重载queryByCondition
    public List<ColSummaryModel> queryByCondition(String condition, String field, String field1, int state, List<Long> templeteIds,Integer secretLevel);
    
    //获取流程相关详细信息
    public List<WorkflowData> queryWorkflowDataByCondition(String subject, String beginDate, String endDate,
    		List<String> objectStrs, int flowstate, int appKey, String operationType, String[] operationTypeIds, boolean paginationFlag);

    //加签
    public void insertPeople(ColSummary summaryId, Affair affair, FlowData flowData,
    		BPMProcess process, User user, boolean isFormOperationReadonly) throws ColException;

    //减签前返回可减签的人员列表
    public FlowData preDeletePeople(long summaryId, long affairId, String processId, String userId) throws ColException;

    public FlowData deletePeople(ColSummary summary, Affair affair, List<Party> partyId, String userId) throws ColException;

    //通过processId得到相应的xml定义文件
    public String getProcessXML(String processId) throws ColException;

    //回退
    //@return true:成功回退 false:不允许回退    isFirst 上个节点是否是首节点
    public boolean stepBack(ColSummary summary, Affair affair, ColOpinion signOpinion, User user,Boolean isFirst) throws ColException;    

    /**
     * 终止
     * @param summaryId
     * @param signOpinion
     * @param user
     * @return
     * @throws ColException
     */
    public boolean stepStop(Long summaryId, Affair affair, ColOpinion signOpinion, User user) throws ColException;
    
    //取回
    //@return 0:成功取回 -1/-2:不允许取回
    public int takeBack(Long affairId, User user,boolean isSaveOpinion) throws ColException;

    //会签
    public void colAssign(Long summaryId, Long affairId, FlowData flowData, String userId) throws ColException;

    //知会
    public void addInform(Long summaryId, Long affairId, FlowData flowData, String userId) throws ColException;

    /**
     * 催办，返回未催办人员
     * @param summaryId
     * @param people
     * @param additional_remark
     * @return
     * @throws ColException
     */
    public List<Long> hasten(String summaryId, List<Long> people, String additional_remark) throws ColException;

    /**
     * 修改正文-保存
     *
     * @throws ColException
     */
    public void saveBody(ColBody body) throws ColException;

    public void saveOpinion(ColOpinion opinion, boolean isSendMessage) throws ColException;

    /**
     * 转发
     * 
     * @param originalSummaryId
     * @param newSummaryId
     * @param flowData
     * @param forwardOriginalNode
     * @param foreardOriginalopinion
     * @param senderOpinion
     * @return
     * @throws ColException
     */
    public ColSummary saveForward(Long originalSummaryId, Long newSummaryId, FlowData flowData, boolean forwardOriginalNode,
                                  boolean foreardOriginalopinion, ColOpinion senderOpinion, String uploadAttFlag,String originalContent) throws ColException;

    /**
     * @deprecated
     * @param user_id
     * @return
     */
    public StatModel PersonalStatFilter(long user_id);

    /**
     * 回复意见
     *
     * @param comment
     * @throws ColException
     */
    public void saveComment(ColComment comment, boolean isSendMessage) throws ColException;

    public String getPolicyBySummary(ColSummary summary) throws ColException;

    public String getPolicyByAffair(Affair affair) throws ColException;

    /**
     * 更新ColSummary行，指定字段
     *
     * @param affairId
     * @param columnValue key-字段名， value-值
     */
    public void update(Long summaryId, Map<String, Object> columns);
    
    public void update(Class clazz, Map<String, Object> columns, Object[][] where);

    public void setFinishedFlag(ColSummary summary) throws ColException;
    
    /**
     * 删除协同的 Opinion\Comment\Affair
     * 
     * @return void
     */
    public void clearSummaryOCA(Long summaryId, boolean isDeleteSenderOpinion);

    
    /**
     * 项目协同
     *
     * @param projectId
     * @throws ColException
     */
    public List<Affair> getColSummaryByProjectId(Long projectId, int size, Long phaseId) throws ColException;

    /**
     * 项目协同条件查询
     * @param projectId
     * @param size
     * @param phaseId
     * @param paramMap 条件参数，可将参数以键值对的方式封装到 paramMap 中
     * @return
     * @throws ColException
     */
    public List<Affair> getColSummaryByCondition(String condition, Long projectId, int size, Long phaseId, Map<String,Object> paramMap) throws ColException;
       
    /**
     * 更新Html正文
     */
    public boolean updateHtmlBody(Long summaryId, String content, String contentType,Long currentNodeId) throws ColException;
    
    /**
     * 解除表单并发锁定
     * @param summaryId
     * @return
     */
    public boolean removeFormLock(long summaryId);
    
    /**
     * 预催办人员
     * @param memberIdStr
     * @return
     */
    public FlowData preHasten(String memberIdStr);
    
    /**
     * 检查修改中的流程,是否可被修改
     * @param processId
     * @param summaryId
     * @return
     */
    public String checkModifyingProcess(String processId, Long summaryId) throws ColException;
    
    /**
     * 得到修改中的流程实例对应的流程定义
     * @param processId
     * @return
     */
    public String getModifyingProcessXML(String processId) throws ColException;

    /**
     * 通过表单appId 表单id和表单记录id查询协同id
     */
	public List<ColSummary> getSummaryIdByFormIdAndRecordId(Long formAppId, Long formId, Long formRecordId); 

	/**
     * 通过processId取到summary
     * @param processId
     * @return
     */
    public ColSummary getSummaryByProcessId(String processId);
    
    /**
	 * 综合查询
	 * @author jincm 2008-3-19
	 * @param cModel
     * @return List
	 */
	public List<Affair> iSearch(ConditionModel cModel);
	
	public List<Object[]> statByGroup(int appType,List<Long> entityId,String entityType,Date beginDate,Date endDate);
	
	public List<Object[]> statList(int appType,long entityId,String entityType,int state,Date beginDate,Date endDate,Long templateId,String appName,String statScope, boolean isPage);
	
	public List<Object[]> statByAccount(int appType,List<Long> templateId, List<Long> entityId,String entityType,Date beginDate,Date endDate);
	
	public void changeTrack(Long affairId, boolean track);
	public void changeTrack(Long affairId, boolean track,List<Long> members);
	public List<ColTrackMember> getColTrackMembers(Long affairId);
	public List<ColTrackMember> getColTrackMembersByObjectIdAndTrackMemberId(Long objectId,Long trackMemberId);
	public void deleteColTrackMembersByObjectId(Long objectId);
	public String checkNodePolicy(String[] policyArr, String[] itemNameArr, String loginAccountId);
	
	/**
	   * 判断表单 应用、视图、操作是否存在
	   * @param appId
	   * @param formId
	   * @param operationId
	   * @return 0:都存在1:appId不存在2:formId不存在3:operationId不存在
	   */
	public int checkForm(long appId,long formId,long operationId);
	
	 public List getFormContent(String affairid ,String summaryid,String formid,String operationid) throws Exception;
		/**
	   * 通过指定的summaryId获取列表
	   * @param summaryidlist
	   */
	 public List getSummaryList(List summaryidlist) throws Exception ;
		/**
	   * 通过指定的parentformSummaryId获取协同列表
	   * @param parentformSummaryId
	   */
	 public boolean getSummaryByParentformId(Long parentformSummaryId) throws ColException ;
	 
	 /**
	  * 取得对应的基准岗和集团职务级别（ajax调用）
	  * @param postIds      岗位id
	  * @param levels       职务级别id
	  * @return  id集合
	  */
	 public List<String> getStandardPostAndLevel(String[] ids,String[] types);
	 
	 /**
	  * 保存分支信息，并记录日志，只用于调试，收集信息
	  * @param beforeScripts
	  * @param afterStripts
	  */
	 public void createBranchLog(String[] beforeScripts,String[] afterStripts,String summaryId,String affairId,String processXML,String formdata);

	 public boolean updateSummaryAttachment(int attSize,List<ProcessLog> logs,Long summaryUd);
	 
	 
	 public String colCheckAndupdateLock(String processId, Long summaryId) throws ColException;
	 public void colDelLock(String processId, String summaryId) throws ColException;
	 
	 /**
	  * 获得某个 人员发起的已经结束的表单流程，
	  * @param formId：表单Id
	  * @param user：人员对象<li>如果user为null 表示得到的是所有的人员<li>
	  * @param condition  查询条件
	  * @param textfield  查询值
	  * @param textfield1  查询值1
	  * @return
	  * @throws Exception
	  */
	 public List<ColSummary> getColSummaryForForm(Long formId, User user, String condition, String textfield, String textfield1) throws Exception;
	 
	 public List<ColSummary> getColSummaryForForm(Long formId, User user, String condition, String textfield, String textfield1, boolean isMysent) throws Exception;

	 /**
	  * 获取表单的某个节点的显示值
	  * @param summaryId
	  * @param filedName
	  * @return
	  * @throws Exception
	  */
	 public String getFormFileDisPlayValue(Long summaryId,String filedName) throws Exception ;
	 
	 /**
	  * 获取表单的节点的值
	  * @param summaryId
	  * @param filedNames
	  * @return
	  * <li>返回值的说明：map中的value是一个数组<li>
	  * <li>数组的第一个数值是显示值，第二个值是实际值<li>
	  * <li>如果没有显示值与实际值之分，那么放的都是一样的值<li>
	  */
	 public Map<String,DisplayValue> getFormFileValue(List<String> filedNames , String... params)throws Exception ;
	 /**
	  * 得到发起者的affair的id
	  * @param summaryId
	  * @return
	  * @throws Exception
	  */
	 public String getSenderAffairIdBysummaryId(Long summaryId) throws Exception ;
	 
	 /**
	  * ajax保存个人设置，打开协同时是否直接展开
	  * @param config
	  * @return
	  * @throws Exception
	  */
	 public boolean saveConfig(Long memberId,String config) throws Exception;
	 /**
	  * 根据office正文附件ID查询协同正文
	  * @param fileid
	  * @return
	  * @throws ColException
	  */
	 public ColBody getColBodyByFileid(long fileid) throws ColException;
	 public void colDelLock(ColSummary summary)  throws ColException;

	/**
	 * 日期时间差计算。
	 * @param beginDealTimeDate
	 * @param endDealTimeDate
	 * @param orgAcconutID
	 * @return 单位“天”
	 */
	public float differDateTime(Date beginDealTimeDate, Date endDealTimeDate, Long orgAcconutID);

	/**
	 * 日期差计算。
	 * @param beginDealDateStr 格式为“yyyy-MM-dd”
	 * @param endDealDateStr
	 * @param orgAcconutID
	 * @return 单位“天”
	 */
	public int differDate(String beginDealDateStr, String endDealDateStr, Long orgAcconutID);

	 public String getComputeDateOfDay(String beginDealTimeDate, String operation, Integer time,String unit,Long orgAcconutID);
	/**
	 * 移动专用加签复杂节点
	 * @param summary
	 * @param affair
	 * @param user
	 * @param memAndPolicy
	 * @param partyNames
	 */
	 public void insertComplexPeople(ColSummary summary, Affair affair, User user,List<String> memAndPolicy,List<String> partyNames)throws ColException;
	 
	 /**
	  * 在业务配置场景中，获取指定状态、表单模板对应的协同事项总数
	  * @param state		事务状态：待办、已办、已发
	  * @param templeteIds  用户进行表单业务配置时选中的表单模板
	  */
	 public int getColCount(int state, List<Long> templeteIds);
	 /**
	  * 用于公文部分的混合加签等操作
	  * @param edocSummary
	  * @param summaryId
	  * @param affair
	  * @param user
	  * @param memAndPolicy
	  * @param partyNames
	  * @throws ColException
	  */
	 public void insertComplexPeopleOfEdoc(EdocSummary edocSummary,Long summaryId,Affair affair, User user,List<String> memAndPolicy,List<String> partyNames) throws ColException;

	 /**
	  * queryPendingByUserAndApp()
	  * @param userid 用户Id
	  * @param appType 应用类型：协同应用、公文应用等，see:com.seeyon.v3x.common.constants.ApplicationCategoryEnum
	  * @param stateType 状态类型：待办、已办等，see:com.seeyon.v3x.affair.constants.StateEnum
	  * @param isTemplate 是否为模板流程
	  * @param isImportant 是否为重要流程
	  * @param isCommon 是否为普通流程
	  * @return
	  */
	 public int queryPendingCountByUserAndApp(String userid,int appType, int stateType, boolean isTemplate, boolean isImportant,boolean isCommon);

	 /**
	  *
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
	 public List<ColSummaryModel> queryPendingByUserAndApp(
			 String userid,
			 int appType, 
			 int stateType, 
			 boolean isTemplate, 
			 boolean isImportant, 
			 boolean isCommon,
			 String condition,
			 String textfield,
			 String textfield1);
	/**
	 * 
	 * @param leaved_userid
	 * @param key
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @return
	 */
	public List queryCommonPendingByUserAndApp(String leaved_userid, int key,
			String condition, String textfield, String textfield1);
	
	 /**
	  * 确定节点描述信息
	  * @param affairId :
	  * @param templeteId:
	  * @return desc描述信息
	  */
	public String getDealExplain(String affairId, String templeteId,String processId);
	
	/**
	 * 
	 * @param templeteId  : 模板ID
	 * @param workFlowState ： 流程状态
	 * @param startDate : 开始时间
	 * @param endDate ： 结束时间
	 * @param isPaging 是否分页
	 * @return
	 */
	public List<ColSummary> getColSummaryList(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState,Date startDate,Date endDate,boolean isPaging);
	
	/**
	 * startNewflowCaseFromNone
	 * 发起子（新）流程方法(来自无流程表单触发)
	 * @param templateId 子(新)流程所属模板Id
	 * @param senderId 发起者Id
	 * @param formMasterId 表单数据记录主键Id值
	 * @param parentFormId 所属父无流程表单Id
	 * @param parentFormMasterId 所属父无流程表单主键记录Id
	 * @throws Exception
	 */
	public ColSummary startNewflowCaseFromNoFlow(Long templateId,Long senderId,Long formMasterId,Long parentFormId, Long parentFormMasterId,int formType,boolean isRelated) throws Exception;	
	
	/**
	 * 发起子（新）流程方法(来自有流程表单触发)
	 * @param templateId 子(新)流程所属模板Id
	 * @param senderId 发起者Id
	 * @param formMasterId 表单数据记录主键Id值
	 * @param parentSummaryId 所属父协同Id
	 * @param parentNodeId 所属父协同节点Id
	 * @param parentAffairId 所属福协同待办事项Id
	 * @throws Exception
	 */
	public ColSummary startNewflowCaseFromHasFlow(Long templateId,Long senderId,Long formMasterId,Long parentSummaryId,String parentNodeId,Long parentAffairId,boolean isRelated) throws Exception;
	/**
	 * 获得协同标题
	 * @param xml 表单xml数据
	 * @return
	 * @throws Exception
	 */
	public String getColSubjectXML(String xml)throws Exception;
	
	/**
	 * 判断指定节点currentNodeId在processXml流程定义文件中，指定节点之后是否有分支条件(穿过知会)
	 * @param processXml 流程定义文件
	 * @param currentNodeId 当前指定节点Id
	 * @return
	 * @throws Exception
	 */
	public String hasConditionAfterSelectNode(String processXml,String currentNodeId) throws Exception;
	
	/**
	 * 判断指定节点currentNodeId在processXml流程定义文件中，是否已設置了自動跳過
	 * @param processXml 流程定义文件
	 * @param currentNodeId 当前指定节点Id
	 * @return
	 * @throws Exception
	 */
	public String isAutoSkipBeforeNewSetFlowOfNode(String processXml,String currentNodeId) throws Exception;
	
	/**
	 * 判断指定分支currentLinkId在processXml流程定义文件中，指定分支之前是否有可以自动跳过的节点(穿过知会)
	 * @param processXml
	 * @param currentLinkId
	 * @return
	 * @throws Exception
	 */
	public String[] hasAutoSkipNodeBeforeSetCondition(String processXml,String currentLinkId) throws Exception;
	
	/**
	 * 根据模板得到此模板某段时间的实例数
	 * @param templeteId
	 * @param workFlowState
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Integer getCaseCountByTempleteId (
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate);
	/**
	 * 根据模板得到此模板某段时间的平均运行时长。
	 * @param templeteId
	 * @param workFlowState
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Integer getAvgRunWorkTimeByTempleteId(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate);
	/**
	 * 处理时间大于基准时间.
	 * @param templeteId
	 * @param workFlowState
	 * @param startDate
	 * @param endDate
	 * @param standarduration
	 * @return
	 */
	public Integer  getCaseCountGTSD(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState, Date startDate, Date endDate,Integer standarduration);
	
	/**
	 * 得到某个模板某段时间的超期流程数
	 * @param accountId
	 * @param templeteId
	 * @param workFlowState
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Double getOverCaseRatioByTempleteId(
			Long accountId,
			Long templeteId,
			List<Integer> workFlowState,
			Date startDate,
			Date endDate);
    public void clearSession();
	/**
	 * 查询协同的状态
	 * @param affairId
	 * @return
	 */
	public Integer getAffairState(Long affairId) ;
	 public int cancelSummary(long userId,ColSummary summary, ColOpinion signOpinion, boolean isSaveOpinion, String repealComment) throws ColException ;
	/**
	 * 表单设置触发状态
	 * @param summaryId
	 * @param isHasFormTrigger
	 */
    public void updateFormTriggerStatus(Long summaryId, boolean isHasFormTrigger);
    public  String checkLevelScope(Long memberId);
	public void finishWorkItem(Affair affair, ColOpinion signOpinion,
			Map<String, String[]> map, Map<String, String> condition,
			BPMProcess process, User user, ColSummary summary) throws ColException ;
}