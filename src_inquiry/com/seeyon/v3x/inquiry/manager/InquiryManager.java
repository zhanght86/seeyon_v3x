/**
 * 
 */
package com.seeyon.v3x.inquiry.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.inquiry.domain.InquiryAuthority;
import com.seeyon.v3x.inquiry.domain.InquiryScope;
import com.seeyon.v3x.inquiry.domain.InquirySubsurvey;
import com.seeyon.v3x.inquiry.domain.InquirySubsurveyitem;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;
import com.seeyon.v3x.inquiry.util.InquiryLock;
import com.seeyon.v3x.inquiry.webmdoel.DiscussAndUserCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyAuthCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyBasicCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyTypeCompose;
import com.seeyon.v3x.organization.domain.V3xOrgMember;


/**
 * @author lin tian
 * 
 * 2007-2-27
 */
public interface InquiryManager {
	public static final String CENSOR_PASS = "0";// 审核员操作 审核通过

	public static final String CENSOR_NO = "1";// 审核员操作 审核不通过

	public static final String CENSOR_SEND = "2";// 审核员操作 立即发送

	/**
	 * 加载所有调查版块
	 *
	 */
	public void initAllSurveyType();
	
	public Collection<InquirySurveytype> getAllInquiryTypes();
	
	public Map<Long, InquiryLock> getLockInfo4Dump();
	
	/**
	 * 保存新的调查类型
	 * 
	 * @param inquirytype
	 * @return
	 */
	public void saveInquiryType(InquirySurveytype inquirytype) throws Exception;

	/**
	 * 归档调查
	 * 
	 * @param inquirytype
	 * @return
	 */
	 public void pigeonholeInquiry(String values) throws Exception;
	/**
	 * 获取已创建调查名称列表
	 * @param isGroup  判断是否是集团的  用来过滤各空间下重复名称问题
	 * @return
	 * @deprecated 未区分单位
	 * @throws Exception
	 */
	public List<String>  getTypeNameList(boolean isGroup)throws Exception;
	
	public List<String> getTypeNameList(boolean isGroup, Long loginAccountId) throws Exception;
	/**
	 * 获取自定义单位或集团已创建调查名称列表
	 * @param loginAccountId
	 * @param spaceType
	 * @return
	 * @throws Exception
	 */
	public List<String> getTypeNameList(Long loginAccountId, int spaceType) throws Exception;

	/**
	 * 管理员选择删除当前调查类型下发布的调查列表
	 * 
	 * @param bid
	 * @throws Exception
	 */
	public void removeSendBasicByManager(String[] bid) throws Exception;

	/**
	 * 更新调查类型
	 */
	public void updateInquiryType(InquirySurveytype type, Set<InquirySurveytypeextend> set) throws Exception;

	public void updateInquiryType(InquirySurveytype type, Set<InquirySurveytypeextend> set, boolean isReloadAllType) throws Exception;

	public void updateInquiryType(InquirySurveytype type, Set<InquirySurveytypeextend> set, Set<InquiryAuthority> removeAuthSet, boolean isReloadAllType) throws Exception;

	public void updateInquiryType(InquirySurveytype type) throws Exception;

	public void updateInquiryType(InquirySurveytype type, boolean isReloadAllType) throws Exception;

	/**
	 * 删除调查
	 * 
	 * @param inquirytype
	 * @return
	 */
	public void deleteInquiryBasic(long id) throws Exception;
	
	/**
	 * 取消发布调查
	 * 
	 * @param inquirytype
	 * @return
	 */
	public void cancelInquiryBasic(long id) throws Exception;

	/**
	 * 获取调查列表
	 * 
	 * @return
	 */
	public List<SurveyTypeCompose> getInquiryList(User user) throws Exception;
	
	public List<InquirySurveytype> getAccountSurveyTypeList(Long accountId) throws Exception;
	/**
	 * 获取所有自定义空间中的调查版块(集团/单位)
	 * @return
	 * @throws BusinessException
	 */
	public List<InquirySurveytype> getAllCustomSurveyTypeList() throws BusinessException;
	
	public List<SurveyTypeCompose> getCustomAccInquiryList(Long accountId, String spaceType) throws Exception;
	
	public List<InquirySurveytype> getCustomAccInquiryTypeList(Long spaceId, int spaceType) throws Exception;
	
	public List<InquirySurveytype> getGroupSurveyTypeList() throws Exception;
	
	public List<InquirySurveytype> getSurveytypeList() throws Exception;

	/**
	 * 获取当前用户有权发布的调查类型列表
	 */
	public List<InquirySurveytype> getInquiryTypeListByUserAuth()
			throws Exception;
	/**
	 * 获取当前自定义空间用户有权发布的调查类型列表
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveytype> getInquiryTypeListByUserAuth(long spaceId) throws Exception;
	
	/**
	 * 获取当前用户有权发布的集团调查类型列表
	 */
	public List<InquirySurveytype> getGroupInquiryTypeListByUserAuth()
			throws Exception;


	/**
	 * 获取用户首页调查类型列表
	 * boolean isGroup 区分是否为集团
	 * needSurveyCountOfType 是否需要抽取板块下调查数
	 * @return
	 */
	public List<SurveyTypeCompose> getUserIndexInquiryList(boolean isGroup,boolean needSurveyCountOfType) throws Exception;
	public List<SurveyTypeCompose> getAllCustomInquiryList() throws BusinessException;
	/**
	 * 获取用户自定义单位或集团首页调查类型列表
	 * @param spaceId
	 * @param spaceType
	 * @param needSurveyCountOfType 是否需要抽取板块下调查数
	 * @return
	 * @throws Exception
	 */
	public List<SurveyTypeCompose> getUserIndexInquiryList(long spaceId, int spaceType, boolean needSurveyCountOfType) throws Exception;
	
	/**
	 * 集团空间调查类型列表
	 * @return
	 * @throws Exception
	 */
	public List<SurveyTypeCompose> getGroupInquiryTypeList() throws Exception;
	/**
	 * 根据ID获取InquirySurveytype
	 * 
	 * @param id
	 * @return
	 */
	public InquirySurveytype getSurveyTypeById(Long id) throws Exception;
	
	/**
	 * 判断此调查类型是否存在
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public boolean getInquirytypeById(Long typeId) throws Exception;

	/**
	 * 判断此调查是否存在
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public boolean hasInquiryExist(Long bid) throws Exception;
		
	/**
	 * 根据ID获取InquirySurveytype不区分flag(删除标记)状态
	 * 
	 * @param id
	 * @return
	 */
	public InquirySurveytype getInquirySurveytypeByIdNoFlag(long id) throws Exception;
	
	/**
	 * 根据InquirySurveytype ID获取SurveyTypeCompose
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SurveyTypeCompose getSurveyTypeComposeBYID(long id) throws Exception;

	/**
	 * 判断当前用户是否为系统中任一调查类型下的管理员
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isInquiryManagerInSys(User user,boolean isGroup) throws Exception;

	/**
	 * 判断当前用户是否为当前调查类型的管理员
	 * 
	 * @param inquirytype
	 * @return
	 */
	// public boolean isInquiryManager(InquirySurveytype inquirytype)
	// throws Exception;
	public boolean isInquiryManager(long typeid) throws Exception;

	/**
	 * 判断当前用户是否有任一调查类型下的发布权限
	 * 
	 * @return
	 */
	public boolean isInquiryAuthorities() throws Exception;

	/**
	 * 判断当前用户是否有当前调查类型下的发布权限
	 * 
	 * @param inquirytype
	 * @return
	 */
	public boolean isInquiryAuthorities(long inquirytype) throws Exception;
	
	/**
	 * 判断当前用户是否有当前调查类型下的发布权限(集团空间用管理员也有发布权限)
	 * 
	 * @param inquirytype
	 * @return
	 */
	public boolean isInquiryAuthorities(long inquirytype,List<V3xOrgMember> managers) throws Exception;

	/**
	 * 判断当前用户是否有当前调查类型下的审核权限
	 * 
	 * @param inquirytype
	 * @return
	 * @throws Exception
	 */
	public boolean isInquiryChecker(long typeid) throws Exception;

	/**
	 * 判断当前用户是否有任一调查类型下审核权限
	 * 
	 * @param inquirytype
	 * @return
	 * @throws Exception
	 */
	public boolean isInquiryChecker() throws Exception;

	/**
	 * 获取对当前用户可见的调查列表
	 * 
	 * @return
	 */
	public List<SurveyBasicCompose> getInquiryBasicListByUserID(int size)
			throws Exception;
	/**
	 * 自定义空间获取对当前用户可见的调查列表
	 * @param spaceId
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getCustomInquiryBasicListByUserScope(long spaceId, int size) throws Exception;
	
	/**
	 * 获取集团空间的调查列表
	 * 
	 * @return
	 */
	public List<SurveyBasicCompose> getGroupInquiryBasicList(int size)
			throws Exception;

	/**
	 * 获取对当前用户全部可见的调查列表
	 * 
	 * @return
	 */
	public List<SurveyBasicCompose> getALLInquiryBasicListByUserID( String typeId , String condition , String textfield , String textfield1 , boolean isGroup )
			throws Exception;
	/**
	 * 获取对当前用户全部可见的自定义空间调查列表
	 * @param spaceId
	 * @param spaceType
	 * @param typeId
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param isGroup
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getALLCustomInquiryBasicListByUserID(long spaceId, int spaceType, String typeId , String condition , String textfield , String textfield1 , boolean isGroup)
			throws Exception;
	
	/**
	 * 获取对当前用户全部可见的调查列表  不要分页
	 * 
	 * @return
	 */
	public List<SurveyBasicCompose> getALLInquiryBasicListByUserID(boolean isGroup) throws Exception;
	
	/**
	 * 集团空间section中更多调用方法
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getGroupInquiryList( String typeId , String condition , String textfield , String textfield1 ) throws Exception;

	/**
	 * 获取授权的用户组织列表
	 * 
	 * @return
	 * @param inquirytype
	 * @throws Exception
	 */
	public SurveyAuthCompose getAuthoritiesList(long typeid) throws Exception;

	/**
	 * 保存授权用户
	 * 
	 * @param iAuth
	 * @throws Exception
	 */
	public void saveInquiryAuthorities(long tid, String authscope)
			throws Exception;

	/**
	 * 获取当前审核员待审核调查列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getCheckSurveyBasicListByChecher()
			throws Exception;

	/**
	 * 获取当前调查类型下的调查列表   xut   12-12  支持查询操作
	 * 
	 * @param inquirytype
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getSurveyBasicListByType(long typeid,String mid , String condition, String textfield, String textfield1 ) throws Exception;
	
	/**
	 * 获取当前调查类型下的调查列表   按条数取   首页栏目用到
	 * 
	 * @param inquirytype
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getSurveyBasicListByType( long typeid, int size ) throws Exception;

	/**
	 * 新建调查
	 * 
	 * @param basic
	 * @throws Exception
	 */
	public void saveSurveyBasic(InquirySurveybasic basic,String bid) throws Exception;
	
	/**
	 * 保存调查模板
	 * 
	 * @param basic
	 * @throws Exception
	 */
	public void saveSurveyBasicTemp(InquirySurveybasic basic,String bid) throws Exception;
	
   /**
    * 更新调查
    * @param basic
    * @throws Exception
    */
	public void updateSurveyBasic(InquirySurveybasic basic) throws Exception;
	
	/**
	 * 根据调查ID查找当前用户有权看见的调查
	 * 
	 * @param basicid
	 * @return
	 * @throws Exception
	 */
	public SurveyBasicCompose getInquiryBasicByUserIDAndBasicID(long basicid)
			throws Exception;

	/**
	 * 根据ID获取调查
	 * 
	 * @param basicid
	 * @return
	 * @throws Exception
	 */
	public SurveyBasicCompose getInquiryBasicByBasicID(long basicid)
			throws Exception;

	/**
	 * 判断当前用户有权看见该调查否
	 * 
	 * @param basicid
	 * @return
	 * @throws Exception
	 */
	public boolean isPowerDatialBasic(long id) throws Exception;

	/**
	 * 根据ID获取未审核的调查
	 * 
	 * @param bid
	 * @param tid
	 * @return
	 * @throws Exception
	 */
	public SurveyBasicCompose getInquiryBasic(String bid)
			throws Exception;

	/**
	 * 保存审核人员的操作
	 * 
	 * @param bid
	 *            调查ID
	 * @param hid
	 *            操作描述
	 * @param tid
	 *            调查类型ID
	 * @throws Exception
	 */
	public boolean  saveCheckerHandle(String bid, String hid,String name,Long id,String checkMind)
			throws Exception;

	/**
	 * 获取当前调查审核员待审核的调查列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getWaitCensorBasicListByChecker(String condition, String textfield, String textfield1,String surveyTypeId) throws Exception;
	
	/**
	 * 自定义空间获取当前调查审核员待审核的调查列表
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param surveyTypeId
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getWaitCensorBasicListByChecker(String condition, String textfield, String textfield1,String surveyTypeId,long spaceId) throws Exception;
	/**
	 * 获取当前调查审核员待审核的集团调查列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getWaitCensorGroupBasicListByChecker(String condition, String textfield, String textfield1,String surveyTypeId)
			throws Exception ;
	
	/**
	 * 获取当前调查审核员待审核的调查列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getWaitCensorBasicListByCheckerInt(String condition, String textfield, String textfield1,String surveyTypeId) throws Exception;
	/**
	 * 自定义空间获取当前调查审核员待审核的调查列表
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param surveyTypeId
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	public int getCustomWaitCensorBasicListByCheckerInt(String condition, String textfield, String textfield1, String surveyTypeId, long spaceId) throws Exception;
	
	/**
	 * 获取当前调查审核员待审核的集团调查列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getWaitCensorGroupBasicListByCheckerInt(String condition, String textfield, String textfield1,String surveyTypeId)
			throws Exception ;

	/**
	 * 获取当前用户在当前调查类型下发布的调查列表
	 * 
	 * @param tid
	 *            调查类型ID
	 * @return
	 * @throws Exception
	 * 
	 */
	public List<SurveyBasicCompose> getSendBasicListByCreator(long tid)
			throws Exception;

	/**
	 * 获取当前用户在当前调查类型下未发布的调查列表
	 * 
	 * @param tid
	 *            调查类型ID
	 * @return
	 * @throws Exception
	 * 
	 */
	public List<SurveyBasicCompose> getNOSendBasicListByCreator(long tid,String condition, String textfield, String textfield1)
			throws Exception;

	/**
	 * 选择删除当前用户在当前调查类型下未发布的调查列表
	 * 
	 * @param tid
	 * @param bid
	 * @throws Exception
	 */
	public void removeNoSendBasicByCreator(long tid, String[] bid)
			throws Exception;
	/**
	 * 删除个人模板
	 * @param tid
	 * @throws Exception
	 */
	public void removeTemplate(String[] tid)throws Exception;

	/**
	 * 终止当前调查类型
	 * 
	 * @param tid
	 * @param bid
	 * 
	 * @throws Exception
	 */
	public void closeSendBasicByCreator(String bid) throws Exception;

	/**
	 * 根据用户输入内容查找调查
	 * 
	 * @param oid
	 *            查询选项
	 * @param content
	 *            查询内容
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getBasicByUserQuery(String oid, String[] content, String typeid,Map<String,String> map , boolean isOtherAccount ) throws Exception;

	/**
	 * 获取某调查版块下的所有调查（不是删除状态并且状态为未归档和不是保存待发）
	 * @param typeId
	 * @return
	 */
	public List<InquirySurveybasic> getInquirySurveyByTypeId(Long typeId);
	
	/**
	 * 用户投票操作权限判断
	 * 
	 * @param bid
	 * @return
	 * @throws Exception
	 */
	public boolean getUserVoteBasic(long bid) throws Exception;

	/**
	 * 发布审核通过的调查
	 * 
	 * @param did
	 * @return
	 * @throws Exception
	 */
	public void creatorSendBasic(String[] bids, String tid,String name ,Long id) throws Exception;


	/**
	 * 保存投票投票
	 * 
	 * @param sbid
	 * @return
	 * @throws Exception
	 */
	public void updateBasicAndVote(long bid, List<Object> objlist,
			List<String> alist) throws Exception;

	/**
	 * 根据调查ID和问题ID获取评论列表
	 * 
	 * @param bid
	 * @param pid
	 * @return
	 * @throws Exception
	 */
	public List<DiscussAndUserCompose> getDiscussList(long bid, long qid)
			throws Exception;

	/**
	 * 根据ID删除评论
	 * 
	 * @param did
	 * @return
	 * @throws Exception
	 */
	public void removeDiscuss(long did) throws Exception;

	/**
	 * 合并调查项
	 * 
	 * @param items
	 * @param newItem
	 * @throws Exception
	 */
	public void saveMergeInquiry(String[] items, String newItem, String bid)
			throws Exception;

	/**
	 * 获取调查模板列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveybasic> getTemplateList() throws Exception;

	/**
	 * 获取自定义集团调查模板列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveybasic> getSpaceTemplateList(String spaceType) throws Exception;
	
	/**
	 * 获取单位或者集团调查模板列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveybasic> getAccOrGroupTemplateList(String group) throws Exception;
	
	
	/**
	 * 获取调查模板
	 * 
	 * @return
	 * @throws Exception
	 */
	public SurveyBasicCompose getTemplateListByID(long id , boolean getTemp) throws Exception;

	/**
	 * 根据id获取调查
	 * @param basicid
	 * @return
	 * @throws Exception
	 */
	public InquirySurveybasic getBasicByID(long basicid) throws Exception;
	 /**
	  * 获取当前调查版块的调查列表
	  * @param tid
	  * @return
	  * @throws Exception
	  */ 
	public List<SurveyBasicCompose> getCheckListByType(long tid) throws Exception;
	/**
	 * 判断是否有同名的模板
	 * @param tid
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public boolean isTheSameName(String name) throws Exception;
	
	/**
	 * 判断是否有同名调查
	 * @param typeId
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public boolean isInquiryExist(String name,Long typeId) throws Exception;
	
	/**
	 * 判断用户的管理身份
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<String> getAuthorities(InquirySurveytype type)throws Exception;
	/**
	 * 获取当前用户有管理权限的调查版块
	 * @return
	 * @throws Exception
	 */
	public  List<SurveyTypeCompose>  getAuthoritiesTypeList()throws Exception;
	/**
	 * 自定义空间获取当前用户有管理权限的调查版块
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	public List<SurveyTypeCompose> getCustomAuthoritiesTypeList(long spaceId) throws Exception;
	/**
	 * 获取当前用户有管理权限的集团调查版块
	 * @return
	 * @throws Exception
	 */
	public List<SurveyTypeCompose> getAuthoritiesGroupTypeList() throws Exception;

	public SurveyBasicCompose surveyBasicComposeViewObject(
			InquirySurveybasic ibasic, Set<InquiryScope> scopeset,
			Set<InquirySubsurvey> subsurveySet,
			Set<InquirySubsurveyitem> itemSet) throws Exception ;
	
	public List<InquirySurveytypeextend> getSerById(Long surveryId,int type) throws Exception ;
	
	public List<InquiryAuthority> authorityList(long tid)throws Exception ;
	
	/**
	 * 判断当前用户是否有集团空间下的管理/审核权限
	 * @return
	 * @throws Exception
	 */
	public boolean hasManageAuthForGroupSpace() throws Exception;
	
	/**
	 * 判断是否有集团空间下的发布权限
	 * @return
	 * @throws Exception
	 */
	public boolean isInquiryAuthoritiesOfGroup() throws Exception;
	
	
	/**
	 * 获取外单位调查
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getOtherAccountSurveyBasicList( String condition , String textfield , String textfield1) throws Exception;
	
	/**
	 * 获取外单位调查总数
	 * @return
	 * @throws Exception
	 */
	public int getOtherAccountSurveyBasicCount() throws Exception;
	
	/**
	 * 判断是否有某板块的审核权限
	 * @param isGroup   区分集团与单位空间
	 * @return
	 */
	public boolean hasCheckAuth(boolean isGroup );
	
	/**
	 * 判断某板块下是否含有子调查
	 * @param typeId
	 * @return
	 */
	public boolean hasInquiryByTypeId(Long typeId);
	
	/**
	 * 判断单位空间下管理/审核权限   用于判断是否显示公共信息菜单   PublicInfoMenuCheckImpl调用
	 * @return
	 * @throws Exception
	 */
	public boolean hasManageAuthForAccountSpace(Long memberId) throws Exception ;
	
	/**
	 * 判断自定义单位/集团空间下管理/审核权限 用于判断是否显示公共信息菜单
	 */
	public boolean showManagerMenuOfCustomSpace(Long memberId, Long spaceId, int spaceType) throws Exception;
	
	/**
	 * 上述方法在跨单位兼职办公时失效，需加入对用户登录单位的判断
	 * @param memberId 当前用户
	 * @param accountId 登陆单位
	 * @return 是否为当前登陆单位的调查管理员/审核员，以便决定点击公共信息管理时调查管理是否亮显
	 * @throws Exception
	 * @author Meng Yang 2009-07-14
	 */
	public boolean hasManageAuthForAccountSpace(Long memberId, long accountId) throws Exception ;
	
	/**
	 * 人员删除时判断该人员是否有未审核的调查
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public boolean hasInquiryNoCheck(Long memberId) throws Exception ;
	
	
	/**
	 * 修改某板块审核员时判断该人员是否有未审核的调查
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public boolean hasInquiryNoCheckByType(Long typeId) throws Exception ;
	
	/**
	 * 检测调查审核员是否可用
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public boolean isInquiryCheckerEnabled(Long typeId) throws Exception ;
	
	/**
	 * 获取单位下当前登录人员
	 * @param checkerId
	 * @return
	 * @throws Exception
	 */
	public int countCheckInquiryByMember(Long checkerId) throws Exception ;
	
	
	/**
	 * 查找是否存在重名调查模板
	 * @param tempName 模板名称
	 * @param typeId 模板ID
	 */
	public boolean isInquiryUnique(String tempName,Long typeId);
	
	/**
	 * 综合查询
	 */
	public List<InquirySurveybasic> iSearch(ConditionModel cModel) throws Exception ;
	
	/**
	 * 用于新建单位时初始化调查板块
	 * @param accountId
	 */
	public void initInquiryType(long accountId);
	
	/**
	 * 用于更新调查版块的排序顺序
	 * @param accountId
	 */
	public void updateSurveyTypeOrder(String[] surveyTypeIds);
	
	/**
	 * @param typeId
	 */
	public List<SurveyBasicCompose> getSurveyByType(Long typeId) throws Exception;
	
//	public void initAllSurveyType();
	/**
	 * 检验文件中否加锁
	 * InquiryLock:为空表示文件已加锁,不能再访问了
	 * InquiryLock:不为空表示文件还没有加锁,可以访问,并进行加锁
	 * action表示当前的动作
	 */
	public InquiryLock lock(Long inquiryId, Long currentUserId, String action);
	public InquiryLock lock(Long inquiryId, String action);
	
	/**
	 * 对新闻进行解锁
	 */
	public void unlock(Long inquiryId);
	
	/**
	 * 根据调查的ID获取调查的发布范围
	 */
	public boolean getInquiryScope(long inquiryId);
	
	/**
	 * 判断用户是否在某一调查发布范围之内
	 */
	public boolean isInInquiryScope(User user, long inquiryId) throws Exception;
	
	/**
	 * 根据当前用户的ID和调查项的ID查出用户所选择的项数
	 *第一个参数传当前用户的ID,第二个参数传调查项的ID
	 * @param userid
	 * @param surbasicID
	 * @return
	 */
	public List<Long> findByCurUser(long userid,long surbasicID);
	
	/**
	 * 根據調查名稱模塊名稱查詢
	 */
	public List<SurveyTypeCompose> getInquiryTypeList(String typename , String group) ;
	/**
	 * 根据调查总数查询
	 * @param match 
	 * @param num  
	 * @param group
	 * @return
	 */
	public List<SurveyTypeCompose> getInquiryTypeList(String num,String match,String group) ;
	
	/**
	 * 根据调查是不是需要审核进行查询
	 * @param flag
	 * @param group
	 * @return
	 */
	public List<SurveyTypeCompose> getInqTypeListByauditFlag(String flag ,String group) ;
	
	
	/**
	 * 根据调查的审核员进行查询
	 * @param flag
	 * @param group
	 * @return
	 */
	public List<SurveyTypeCompose> getInqTypeListByauditManager(String auditUserName ,String group) ;
	
	/**
	 * 生成待办事项记录
	 * @param basic
	 * @throws BusinessException
	 */
	public void addPendingAffair(InquirySurveybasic basic, ApplicationSubCategoryEnum subState) throws BusinessException;
	/**
	 * ws接口使用
	 */
	public List<SurveyBasicCompose> getInquiryBasicListByUserIDByRecent(long accountId,long personId, int firstNum, int size) throws Exception;
	
	/**
	 * 将某一指定调查板块下待审核的调查对应待办事项转到新审核员名下，并且修改该板块下未审核的调查的审核员<br>
	 * 由于旧的待办事项可能是较早以前的，在转移时，将其时间改为当前时间，便于新的审核员在其待办事项最开始几项中看到<br>
	 * 这种情况发生的场景：旧审核员离职了，而其具有审核权的调查板块还有待审核调查<br>
	 * @param typeId        调查板块ID
	 * @param oldCheckerId  旧审核员ID
	 * @param newCheckerId  新审核员ID
	 */
	public void transfer2NewChecker(Long typeId, Long oldCheckerId, Long newCheckerId);
	
	/**
	 * 配合管理员进行归档时，前端进行AJAX校验，返回选中调查中已结束的调查ID字符串以便进行下一步操作
	 * @param ids  选中的调查IDs
	 * @return String[] [0] - 已结束的调查ID拼接字符串结果,  [1] - 选中的调查是否包括了未结束的调查
	 */
	public String[] filterWhenPigeonhole(String ids);

	/**
	 * 删除人员时修改板块的管理员、审核员、发起者
	 * @param id
	 * @throws BusinessException
	 */
	public void delMember(Long id) throws Exception;

	/**
	 * 根据userid获得该用户管理或审核的调查板块列表
	 * @param userid
	 * @return
	 */
	public List<InquirySurveytype> getInquiryTypeListByUserId(String userid,int type);
	
	/**
	 * 创建自定义团队空间对应调查板块
	 */
	public InquirySurveytype saveCustomInquirySurveytype(Long spaceId, String spaceName);
	
	/**
	 * 用于判断是否是调查审核员
	 */
	public boolean isAuditorOfInquiry(Long memberId);
	
	/**
	 * 用于判断调查是否有效
	 */
	public boolean isEffective(String bid) throws Exception;

}