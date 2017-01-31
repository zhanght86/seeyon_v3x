package com.seeyon.v3x.mobile.manager;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputValueAll;

import com.seeyon.cap.meeting.domain.MtReplyCAP;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.index.share.datamodel.SearchResult;
import com.seeyon.v3x.mobile.MobileException;
import com.seeyon.v3x.mobile.webmodel.AffairsListObject;
import com.seeyon.v3x.mobile.webmodel.Bulletion;
import com.seeyon.v3x.mobile.webmodel.Calendar;
import com.seeyon.v3x.mobile.webmodel.Collaboration;
import com.seeyon.v3x.mobile.webmodel.Edoc;
import com.seeyon.v3x.mobile.webmodel.EdocItem;
import com.seeyon.v3x.mobile.webmodel.MeetingDetial;
import com.seeyon.v3x.mobile.webmodel.MobileBookEntity;
import com.seeyon.v3x.mobile.webmodel.MobileForm;
import com.seeyon.v3x.mobile.webmodel.MobileHistoryMessage;
import com.seeyon.v3x.mobile.webmodel.MobileOrgEntity;
import com.seeyon.v3x.mobile.webmodel.News;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.peoplerelate.RelationType;

/**
 * 接口定义
 * 
 * @author hub
 * 
 */
public interface OAManagerInterface {

	public static String myPengding = "4";

	public static String myTrack = "5";

	public static String myMeeting = "7";
	

	/**
	 * 得到登录用户待办事项，跟踪事项，待办协同，会议的个数
	 * 
	 * @param uid
	 *            (uid是登录用户的ID)
	 * @param needCount        
	 *            需要取得总数的菜单
	 * @return Map 泛型 String key值定义如下： "我的待办事项"=="myPending" "我得跟踪事项"=="myTrack"
	 *         "我的会议信息"=="myMeeting" 修改：String
	 */
	public Map<String, Integer> getHomePageInfo(Long uid,List<String> needCount);

	/**
	 * 得到登录用户未过期会议列表
	 * 
	 * @param uid
	 *            是登录用户的ID
	 * @param pagecounter
	 *            是每一个页面显示的个数
	 * @param pagenumber
	 *            当前显示的页码
	 * @param currentlist
	 *            内部存放全部未过期的会议，排序以会议开始时间为标准。
	 * @return 全部会议的总数 如果pagenumber==1返回全部会议的总数，如果pagenumber!=1 则返回-1.
	 */
	public int getMeetingObjectList(Long uid, int pagecounter, int pagenumber,
			List<AffairsListObject> currentlist, String titleKeyword);

	/**
	 * 得到会议细节 对于未处理的meeting MeetingDetial::isAttend==null
	 * 
	 * @param mid
	 *            是会议的ID
	 * @param uid
	 *            是登录用户的ID
	 * @return
	 */
	public MeetingDetial getMeetingDetial(Long mid, Long uid);
	
	/**
	 * 查询符合条件的会议回执列表
	 * @param meetingId
	 * @param userId
	 * @return
	 */
	public List<MtReplyCAP> findByMeetingIdAndUserId(Long meetingId,Long userId);

	/**
	 * 处理会议
	 * 
	 * @param mid
	 *            是会议的ID
	 * @param uid
	 *            是登录用户的ID
	 * @param isAttend
	 *            参加不参加
	 * @param opinion
	 *            意见
	 *  @param opinion
	 *            代理
	 */
	public void processMeeting(Long mid, Long uid, int process, String opinion,boolean proxy)
			throws MobileException;

	/**
	 * 得到登录用户待办事项列表
	 * 
	 * @param uid
	 *            是登录用户的ID
	 * @param titleKeyword
	 *            查询关键字
	 * @return 
	 */
	public List<Affair> getPendingAffairObjectList(Long uid, String titleKeyword);

	/**
	 * 得到登录用户跟踪事项列表
	 * 
	 * @param uid
	 *            是登录用户的ID
	 * @param pagecounter
	 *            是每一个页面显示的个数
	 * @param pagenumber
	 *            如果pagenumber==1返回全部待办协同的总数，如果pagenumber!=1 则返回-1.
	 * @param pengingaffairlist
	 * @param titleKeyword
	 *            查询关键字
	 * @return 全部跟踪协同的总数
	 */
	public int getTrackAffairObjectList(Long uid, int pagecounter,
			int pagenumber, List<Affair> pengingaffairlist,
			String titleKeyword);

	/**
	 * 得到登录用户待办协同，已办协同，待发协同，已发协同的个数
	 * 
	 * @param uid(uid)
	 * @return String key值定义如下： (也可以使用ResourceId string) "已发协同"=="sentCo"
	 *         "已办协同"=="finishedCo" "待办协同"=="pendingCo" "待发协同"=="draftCo"
	 */
	public Map<String, Integer> getCollaborationNumWithType(Long uid);

	/**
	 * 得到在线人员列表 ，移动应用端完成分页功能
	 * 
	 * @param
	 * @return List<OnlineUser>
	 * 
	 */
	public List<OnlineUser> getOnLineUsers();
    
    /**
     * 得到在线人数
     * @return
     */
	public int getOnLineNum();

	/**
	 * 得到协同细节
	 * 
	 * @param cddid
	 *            是affairId
	 * @param uid
	 *            是登录用户的ID
	 * @return Collaboration
	 */
	public Collaboration CollaborationDetial(Long cddid, Long uid) throws ColException;

	/**
	 * 协同流程
	 * 
	 * @param summaryId 
	 * @param caseId
	 * @param processId
	 * @param isProcess
	 * @return nodes : Nodes ; caseWorkItemLog : Map<String, List<Object[]>>(key:节点Id，value：人员(0-id 1-姓名))
	 * @throws MobileException
	 */
	public Map<String, Object> getNodes(Long summaryId,Long caseId,String processId,boolean isProcess) throws MobileException;

	/**
	 * 得到登录用户已办协同列表
	 * 
	 * @param uid
	 *            是登录用户的ID
	 * @param pagecounter
	 *            是每一个页面显示的个数
	 * @param pagenumber
	 *            如果pagenumber==1返回已办协同的总数，如果pagenumber!=1 则返回-1.
	 * @param currentlist
	 * @param titleKeyword
	 *            搜索关键字，null表示不搜索
	 * @return 全部已办协同的总数
	 */
	public int getCollaborationDoneList(Long uid, int pagecounter,
			int pagenumber, List<Affair> currentlist,
			String titleKeyword);

	/**
	 * 得到登录用户已发协同列表
	 * 
	 * @param uid
	 *            是登录用户的ID
	 * @param pagecounter
	 *            是每一个页面显示的个数
	 * @param pagenumber
	 *            当前页码
	 * @param currentlist
	 * @param titleKeyword
	 *            搜索关键字，null表示不搜索
	 * @return 全部已发协同的总数 如果pagenumber==1返回已发协同的总数，如果pagenumber!=1 则返回-1.
	 */
	public int getCollaborationSentList(Long uid, int pagecounter,
			int pagenumber, List<Affair> currentlist,
			String titleKeyword);

	/**
	 * 得到登录用户待办协同列表
	 * 
	 * @param uid
	 *            是登录用户的ID
	 * @param pagecounter
	 *            是每一个页面显示的个数
	 * @param pagenumber
	 *            如果pagenumber==1返回待办协同的总数，如果pagenumber!=1 则返回-1.
	 * @param currentlist
	 * @param titleKeyword
	 *            搜索关键字，null表示不搜索
	 * @return 全部待办协同的总数
	 */
	public int getCollaborationPendingList(Long uid, int pagecounter,
			int pagenumber, List<AffairsListObject> currentlist,
			String titleKeyword);

	/**
	 * 得到登录用户待发协同列表
	 * 
	 * @param uid
	 *            是登录用户的ID
	 * @param pagecounter
	 *            是每一个页面显示的个数
	 * @param pagenumber
	 *            如果pagenumber==1返回待发协同的总数，如果pagenumber!=1 则返回-1.
	 * @param currentlist
	 * @param titleKeyword
	 *            搜索关键字，null表示不搜索
	 * @return 全部待发协同的总数
	 */
	public int getCollaborationWaitSendList(Long uid, int pagecounter,
			int pagenumber, List<AffairsListObject> currentlist,
			String titleKeyword);

	/**
	 * 得到该部门人员列表
	 * 
	 * @param did
	 *            是部门ID
	 * @return List<V3xOrgMember>
	 */
	public List<V3xOrgMember> getMemberByDepartment(Long did);

	/**
	 * 得到单位信息Object
	 * 
	 * @param uid
	 *            是登录用户的ID
	 * @return V3xOrgAccount
	 */
	public V3xOrgAccount getAccount(Long uid);

	/**
	 * 得到该部门
	 * 
	 * @param ddid
	 *            (ddid 是某个部门ID)
	 * @return V3xOrgDepartment
	 */
	public V3xOrgDepartment getDepartment(Long ddid);

	/**
	 * 得到一个部门的子部门
	 * 
	 * @param dsid
	 *            (dsid 是父部门的ID)
	 * @return List<V3xOrgDepartment>
	 */
	public List<V3xOrgDepartment> getDepartmentSubordinate(Long dsid);

	/**
	 * 根据部门path得到一个部门对象
	 * 
	 * @param path
	 * 
	 * @return
	 */
	public V3xOrgDepartment getDepartmentByPath(String path,Long accountId);

	/**
	 * 得到组列表
	 * 
	 * @param uid
	 *            (uid 是登录用户的ID)
	 * @return List<V3xOrgTeam>
	 */
	public List<V3xOrgTeam> getTeamList(Long uid);

	/**
	 * 得到组人员列表
	 * 
	 * @param tid
	 *            (tid 是组的ID)
	 * @return List<V3xOrgMember>
	 */
	public List<V3xOrgMember> getMemberByTeam(Long tid);

	/**
	 * 根据人的Id，得到人
	 * 
	 * @param id
	 * @return
	 */
	public V3xOrgMember getMemberById(Long id);

	/**
	 * 根据类型和id取得组织模型元素对象
	 * 
	 * @param type
	 * @param id
	 * @return
	 */
	public V3xOrgEntity getOrgEntity(String type, Long id);

	/**
	 * 得到关联人员列表
	 * 
	 * @param uid
	 *            (uid 是登录用户的ID)
	 * @return List<V3xOrgMember>
	 */
	public Map<RelationType, List<Long>> getRelativeMember(Long uid);

	/**
	 * 执行立即发送操作
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param memberlist
	 *            接收人的ID
	 * @param type
	 *            0串发  1 并发
	 * @param senderid
	 *            发送人的ID
	 * @param flowchart
	 *            流程选择的人员， key-nodeId；value-人员
	 * @param conditionNodes
	 *            分支流程选择的项
	 * @param allNodes
	 *            所有的分支
	 */
	public void sendCollaborationNow(String title, String content,
			List<String[]> memberlist, int type, Long senderid, Long cid, String processId, 
			Map<String, String[]> flowchart, List<String> conditionNodes, String allNodes,HttpServletRequest request) throws MobileException;

	/**
	 * 转发
	 * 
	 * @param opinion
	 * @param memberlist
	 * @param senderid
	 */
	public void transmitCollaboration(String opinion, List<Long> memberlist,
			Long senderid);

	/**
	 * 执行保存待发操作
	 * 
	 * @param Long
	 *            affairId 如果是修改，则是原协同的Id
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param memberlist
	 *            接收人的ID
	 * @param type
	 *            0 并发 1 串发
	 * @param senderid
	 *            发送人的ID
	 */
	public void saveToPendingAffair(Long affairId, String title,
			String content, List<String[]> memberlist, int type, Long senderid,HttpServletRequest request)
			throws MobileException;

	/**
	 * 处理协同
	 * 
	 * @param actiontype
	 *            1 提交，2 暂存待办;
	 * @param id
	 *            协同ID
	 * @param opinion
	 *            意见
	 * @param attitude
	 *            1 已阅, 2 同意， 3 不同意
	 * @param flowchart
	 *            流程选择的人员， key-nodeId；value-人员
	 * @param conditionNodes
	 *            分支流程选择的项
	 */
	public void processCollaboration(int actiontype, Long id, String opinion,
			int attitude, Map<String, String[]> flowchart, Map<String, String> conditionNodes,boolean track) throws MobileException;

	/**
	 * 根据 单位ID 得到该单位的第一级部门
	 * 
	 * @param accountId
	 * @param isFirstLayer
	 * @return
	 */
	public List<V3xOrgDepartment> getDepartmentByAccount(Long accountId,
			boolean isFirstLayer);

	/**
	 * 全文检索接口
	 * 
	 * @param userId
	 * @param keyword
	 * @param pagecounter
	 * @param pagenumber
	 * @param searchResult
	 * @return 总数
	 */
	public int searchResult(Long userId, String keyword, int pagecounter,
			int pagenumber, List<SearchResult> searchResult);

	/**
	 * 查询人员接口
	 * 
	 * @param departmentId
	 *            单位Id或者部门id
	 * @param keyword
	 *            查询的关键字
	 * @param pagecounter
	 *            页面显示的条数
	 * @param pagenumber
	 *            该页的页码
	 * @param list
	 *            该页的显示的内容
	 * @return
	 */
	public int searchMember(Long departmentId, String keyword, int pagecounter, int pagenumber,
			List<MobileOrgEntity> list);

	/**
	 * 得到新闻列表
	 * 
	 * @param uid
	 *            登录用户的Id
	 * @param pagecounter
	 *            页面显示的条数
	 * @param pagenumber
	 *            该页的页码
	 * @param currentlist
	 *            该页的显示的内容
	 * @param titleKeyword
	 *            查询的关键字
	 * @return
	 */
	public int NewsList(Long uid, int pagecounter, int pagenumber,
			List<AffairsListObject> currentlist, String titleKeyword);

	/**
	 * 得到日程列表
	 * 
	 * @param uid
	 *            登录用户的Id
	 * @param pagecounter
	 *            页面显示的条数
	 * @param pagenumber
	 *            该页的页码
	 * @param currentlist
	 *            该页的显示的内容
	 * @param titleKeyword
	 *            查询的关键字
	 * @return
	 */
	public int CalendarList(Long uid, int pagecounter, int pagenumber,
			List<AffairsListObject> currentlist, String titleKeyword);

	/**
	 * 得到公告列表
	 * 
	 * @param uid
	 *            登录用户的Id
	 * @param pagecounter
	 *            页面显示的条数
	 * @param pagenumber
	 *            该页的页码
	 * @param currentlist
	 *            该页的显示的内容
	 * @param titleKeyword
	 *            查询的关键字
	 * @return
	 */
	public int BulletinList(Long uid, int pagecounter, int pagenumber,
			List<AffairsListObject> currentlist, String titleKeyword);

	/**
	 * 得到新闻细节
	 * 
	 * @param nid
	 *            新闻的Id
	 * @param uid
	 *            登录用户的Id
	 * @return
	 */
	public News getNewsDetial(Long nid, Long uid);

	/**
	 * 得到日程的细节
	 * 
	 * @param cid
	 *            日程的Id
	 * @param uid
	 *            当前用户的Id
	 * @return
	 */
	public Calendar getCalendarDetial(Long cid, Long uid);
	
	/**
	 * 得到协同的附件, 0 - 新建协同上传的附件 1 - 所有的附件（附言、回复）
	 * 
	 * @param cId
	 * @return　Object为List<Attachment>
	 */
	public Object[] getColAttachment(long cId);
	
	/**
	 * 得到主题的附件
	 * 
	 * @param objectId
	 * @return
	 */
	public List<Attachment> getAttachment(long objectId);

	/**
	 * 得到公告的细节
	 * 
	 * @param bid
	 *            公告的Id
	 * @param uid
	 *            当前用户的Id
	 * @return
	 */
	public BulData getBulletionDetial(Long bid, Long uid);
	
	public Bulletion getBulletionDetial(BulData bul);

	/**
	 * 查看附件
	 * 
	 * @param fileURL
	 * @param createDate
	 * @return
	 */
	public StringBuffer getAttachmentContent(Long fileURL,
			java.util.Date createDate);

	/**
	 * 判断登录用户是否可以选择该人员
	 * 
	 * @param uid
	 * @param currentUserId
	 * @return
	 */
	public boolean isSeen(Long uid, Long currentUserId);

	/**
	 * 得到的流程节点, 用在协同发起
	 * 
	 * @param cid
	 * @param uid
	 * @return
	 */
	public Map<String, Object> getProcessModeSelectorList(Long cid, Long uid)
			throws MobileException;
	
    /**
     * 判断下节点是否需要选人
     * @param cid
     * @return
     * @throws ColException
     * @throws MobileException
     */
    public boolean isNextNodeUnsure(Long cid) throws ColException, MobileException;
    
    /**
	 * 得到附言的附件
	 * 
	 * @param cid 事项的Id
	 * @param uid 当前用户的Id
	 * @return
	 * @throws MobileException
	 */
	public Map<ColOpinion,List<Attachment>> getOpinionAndAttachments(Long cid,Long uid) throws MobileException;
	
	/**
	 * 得到移动端 表单的列表
	 * @param cid 事项的id
	 * @param type 0－查看细节，1－为 处理
	 * @return 
	 * @return
	 * @throws MobileException
	 */
	public Map<String,TIP_InputValueAll> getFormList(Long cid,boolean readOnly)throws MobileException;
	
	/**
	 * 得到移动短表单信息
	 * @param affairId
	 * @param summaryId
	 * @param user
	 * @return
	 * @throws MobileException
	 */
	public MobileFormBean getFormAll(Long affairId,Long summaryId,User user) throws MobileException;
	
	/**
	 * 处理表单 协同
	 * @param objectMap 用户填写的表单项
	 * @param cid 处理事项的id
	 * @param opinion 用户填写的意见
	 * @param attitude 用户的态度
	 * @param members 分支流程所选择的人员
	 * @param pass  1:普通 2：审核通过 3：审核不通过
	 * @param vouchPass 0:默认2：核定通过3：核定不通过
	 * @throws MobileException
	 */
	public void processForm(Map<String,TIP_InputValueAll> objectMap,Long cid,Integer pass,String vouchPass)throws MobileException, SeeyonFormException ;
	
	/**.
	 * 判断表单流程在当前节点下是否含有分支
	 * @param cid
	 * @param list 为前台用户 填写的表单内容
	 * @return Key 为 满足:satisfy,不满足为：dissatisfy. Value 为 人员的id
	 * @throws MobileException
	 */
	public Map<String,Long> isHasOffset(Long cid,List<MobileForm> list)throws MobileException;
	
	
	/**
	 * 得到 表单的标题
	 * @param cid
	 * @return
	 * @throws MobileException
	 */
	public String getFormName(Long cid)throws MobileException;
	
	
	/**
	 * 得到分支的人员
	 * @param map
	 * @return
	 * @throws MobileException
	 */
	public Map<String,Object> getBranchLong(Map<String,TIP_InputValueAll> map,Long cid,String isForm)throws MobileException;
	
	public Map<String,Boolean> getContainSubForm();
	
	public Map<String,Boolean> getContainMark();
	
	public Map<Long,Map<String,Boolean>> getCancelPurview();

	/**
	 * 判读表单是否被 某一个人正在编辑
	 * @param summeryId
	 * @param nodeId
	 * @return
	 * @throws MobileException
	 */
	public Long getModifyMember(String summeryId,String nodeId)throws MobileException;
	
	public void removeModifyMember(String summeryId)throws MobileException;
	
	public String getSummeryIdByAffairId(String cid)throws MobileException;
	
	/**
	 * 得到一个 V3xOrgEntity 根据 名字
	 * @param className
	 * @param property
	 * @param value
	 * @param accountId
	 * @return
	 * @throws MobileException
	 */
	public List<V3xOrgEntity> getOrgEntityByName(String className,String property,String value,Long accountId)throws MobileException;
	
	/**
	 * 得到待办公文列表数据
	 * @param uid 当前登录用户的id
	 * @param pageCounter 每一页显示的个数
	 * @param pageNumber 当前是第几页
	 * @param pendingEdocList 存放待办公文的列表
	 * @param keyWorld 关键字（查询）
	 * @return 待办公文列表的总数
	 */
	public int getPendingEdocList(Long uid,int pageCounter,int pageNumber,List<AffairsListObject> pendingEdocList,String keyWorld);

	
	/**
	 * 得到在移动端显示的对象
	 * @param affairId
	 * @return
	 */
	public Edoc getPendingEdocObj(Affair affi);

	/**
	 * 得到公文的正文内容
	 * @param uid 当前用户的id
	 * @param edocId
	 * @return
	 */
	public String getPendingEdocObjContent(Long uid,Long edocId);
	
	
	/**
	 * 得到公文的公文文单元素列表
	 * @param uid 当前用户的id
	 * @param edocId 公文的Id
	 * @return
	 */
	public Map<String,Object> getPendingEodcItemList(Long uid,Long edocId);

	/**
	 * 将该节点的处理意见写入对应的公文绑定意见
	 * 
	 * @param actiontype (1 提交 2 暂存待办)
	 * @param id 事项的id
	 * @param opinion 处理意见
	 * @param attitude  处理态度（1 已阅 2 同意 3 不同意）
	 * @param flowchart   流程选择的人员， key-nodeId；value-人员
	 * @param conditionNodes  分支流程选择的项
	 */
	public void processEdoc(int actiontype, Long id, String opinion,
			int attitude, Map<String, String[]> flowchart, Map<String, String> conditionNodes,boolean track);
	
	/**
	 * 得到流程的相关参数
	 * 
	 * @param affairId 事项的id
	 * @return
	 */
	public Map<String,String> flowChartParam(Long affairId);
	
	
	/**
	 * 得到 公文的附件
	 * @param summaryId
	 * @return
	 */
	public List<Attachment> getEdocAtts(Long summaryId);
	
	
	/**
	 * 得到已办公文列表
	 * @param memberId
	 * @param app
	 * @param state
	 * @return
	 */
	public int getDoneOfEdocs(Long memberId,ApplicationCategoryEnum app, int state);
	
	
	/**
	 * 得到已办公文列表
	 * @param uid
	 * @param pageCounter
	 * @param pageNumber
	 * @param pendingEdocList
	 * @param keyWorld
	 * @return
	 */
	public int getDoneEdocList(Long uid,int pageCounter,int pageNumber,List<AffairsListObject> pendingEdocList,String keyWorld);
	
	
	/**
	 * 得到公文的意见
	 * 
	 * @param edocId 公文的id
	 * @return
	 */
	public List<EdocItem> getEdocOpinion(Long edocId,Long affariId);
	
	
	/**
	 * 得到 部门下的相关的资源
	 * @param id 部门id
	 * @return
	 */
	public List<MobileOrgEntity> getMobileOrgEntity(Long id);
	
	
	/**
	 * 得到 一个 EdocSummary 对象
	 * @param id
	 * @return
	 */
	public EdocSummary getEodcSummaryById(Long id);
	
	
	/**
	 * 得到 公文的节点权限
	 * @param affairId
	 * @param summaryId
	 * @return
	 */
	public Map<String,Object>  getEdocPolicyName(Long affairId,Long summaryId);
	
	public String getPostNameByMember(V3xOrgMember member);
	public String getLevelNameByMember(V3xOrgMember member);
	
	public String getZCDBOpinion(Long summaryId);
	
	/**
	 *  得到 移动消息 
	 * @param mobileMessageList
	 * @param memberId
	 * @param content
	 * @param type
	 * @param pageNum
	 * @param pageCounter
	 * @return 总共消息的个数
	 */
	public int getMobileMessageList(List<MobileHistoryMessage> mobileMessageList,Long memberId,String content,String type,int pageNum,int pageCounter);

	/**
	 * 将消息设置为已读状体
	 * @param memberId
	 * @param msgTypte
	 */
	public void setMessageReadedState(Long memberId,int msgTypte);
	
	/**
	 * 判断当前登录用户是否是单位/部门的公文收发员
	 * @return
	 */
	public boolean isExchangeEdoc();
	
	
	/**
	 * 得到该用户创建所有的外部人员
	 * @param createrId
	 * @return
	 */
	public List<MobileBookEntity> getAllOutTeam(Long createrId);
	
	/**
	 * 得到 移动端的外部联系人
	 * @param id
	 * @return
	 */
	public MobileBookEntity getMobileBookMember(Long id);
	
	
	/**
	 * 展现 组下的人员
	 * @param teamId
	 * @return
	 */
	public List<MobileBookEntity> showTeamMembers(Long teamId);
	
	/**
	 * 得到一个组对象
	 * @param teamId
	 * @return
	 */
	public MobileBookEntity showTeamName(Long teamId);
	
	/**
	 * 根据附件的Id,删除附件
	 * @param attId
	 */
	public void removeAttachmentById(Long attId);
	
	/**
	 * 得到 公文的分支流程
	 * @param edocId
	 * @param affairId
	 * @return
	 */
	public Map<String,Object> getEdocBratch(Long edocId,Long affairId);
	
	/**
	 * 得到 协同的节点处理权限
	 * @param affairId
	 * @param summaryId
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getCollPolicyName(Long affairId,Long summaryId)throws Exception;
	
	/**
	 * 根据 事项的id 得到相应协同的id
	 * 
	 * @param affairId
	 * @return
	 */
	public Long getCollSummaryIdByAffairId(Long affairId);
	
	/**
	 * 查询用户未读的消息
	 * @param userId
	 * @return
	 */
	public List<List<MobileHistoryMessage>> findUnReadMessage(Long userId);
	
	/**
	 * 传入表单数据进行人员匹配
	 * @param collId
	 * @param currentId
	 * @param fieldValueMap
	 * @return
	 */
	public Map<String, Object> getProcessModeSelectorList(Long collId,Long currentId, Map<String, String[]> fieldValueMap) throws MobileException;
}