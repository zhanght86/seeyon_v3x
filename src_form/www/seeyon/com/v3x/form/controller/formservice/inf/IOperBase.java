package www.seeyon.com.v3x.form.controller.formservice.inf;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.DocumentException;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.domain.FomObjaccess;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.domain.FormFlowid;
import www.seeyon.com.v3x.form.engine.infopath.InfoPathObject;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;

public interface IOperBase {
	
	public final static int FORM_TYPE_FLOW = 1;
	public final static int FORM_TYPE_BUSINESS = 2;
	
	/**
	 * 用于装配应用名称,所属人名称
	 * @param categorylst
	 * @return
	 * @throws DataDefineException 
	 * @throws BusinessException 
	 */
	public List assignCategory(List categorylst) throws DataDefineException, BusinessException;
	public void setTempleteCategoryManager(TempleteCategoryManager templeteCategoryManager);
	
	/**
	 * 表单填写完成后，保存表单相应的xml及生成对应的数据库对象

	 * @param sessionobject
	 * @param fdm
	 * @throws SeeyonFormException
	 */
	public void LoadFromCab(HttpSession session,FileManager fileManager) throws SeeyonFormException;
	public void LoadFromCab(SessionObject sessionobject) throws SeeyonFormException;
	
	
	public List queryAllData(BaseModel bm) throws DataDefineException;
	
	public BaseModel findBiggestValue() throws DataDefineException;

	/**
	 * 表单修改时，修改表单中生成的相应xml及对应的数据库中字符串
	 * @param sessionobject
	 * @param fdm
	 * @throws SeeyonFormException
	 */
	public void editSave(SessionObject sessionobject,HttpServletRequest request,HttpServletResponse response,FileManager fileManager) throws SeeyonFormException,SQLException;
	public void editSave(SessionObject sessionobject,HttpServletRequest request) throws SeeyonFormException,SQLException;

	/**
	 * 把table名与其表号存入数据库
	 * @param tablefieldlst
	 * @throws DataDefineException
	 */
	public void saveTableValue(List tablefieldlst) throws DataDefineException;
	
	
	/**
	 * 对接收上传文件方法的封装
	 * @param fileManager
	 * @param urls
	 * @param createDates
	 * @param mimeTypes
	 * @param names
	 * @return
	 * @throws SeeyonFormException
	 */
	public  String getXSNSaveDirectory(FileManager fileManager,String[] urls,
			String[] createDates,String[] mimeTypes,String[] names) throws SeeyonFormException;
	
	/**
	 * 专用于跳转页面时，判断此名是否已经在数据库存在
	 * @param name
	 * @return
	 * @throws DataDefineException
	 */
	public boolean isExistsThisForm(String name) throws DataDefineException;	
	/**
	 * 解析XSN文件
	 * @param so
	 * @param directry
	 * @throws SeeyonFormException
	 */
	public  void parseXSN(SessionObject so,String directry,FileManager fileManager) throws SeeyonFormException;
	
	
	/**
	 * 装配一个新的显示字段lst
	 * @param tablenumber
	 * @param masterlst
	 * @param slavelst
	 * @param tablst
	 * @return
	 */
	public  List parseFieldName(List masterlst,List slavelst,List tablst);
	/**
	 * 
	 * @param request
	 * @throws SeeyonFormException
	 * @throws DocumentException
	 * @throws BusinessException 
	 */
	public void editBaseInfo(HttpServletRequest request) throws SeeyonFormException, DocumentException, BusinessException;
	/**
	 * 
	 */
	public  List parsenewName(List masterlst,List slavelst,List tablst,List newtablst);
	/**
	 * 
	 * @param iapp
	 * @param xsf
	 *            新生成的xsf
	 * @param tablefieldlst
	 *            原有的tablefieldlst
	 * @throws SeeyonFormException
	 * @throws DocumentException
	 */
	public SessionObject loadFromDb(ISeeyonForm_Application iapp, InfoPathObject xsf,
			List tablefieldlst,String[] addfield,String id,SessionObject sessionobj) throws SeeyonFormException,DocumentException,BusinessException;

	/**
	 * 没有进行上传InfoPath的修改
	 * @param iapp
	 * @throws SeeyonFormException
	 * @throws DocumentException
	 */
	public SessionObject loadFromnoInfoPath(ISeeyonForm_Application iapp,String id) throws SeeyonFormException, DocumentException,BusinessException;
	/**
	 * 执行发布操作
	 * @param id
	 * @throws DataDefineException,SQLException
	 */
	public void publishForm(Long id,int state,String aAppName,boolean isDelete) throws SeeyonFormException;

	/**
	 * 执行删除操作
	 * @param id
	 * @throws DataDefineException,SQLException
	 */
	public void delForm(Long id,String aAppName,FileManager fileManager) throws SeeyonFormException, SQLException;
	
	
	public String returnViewStr(ISeeyonForm_Application iapp,HttpServletRequest request,String formname,String fillcode) throws SeeyonFormException;

	/**
	 * 用于装配应用名称,查询名称,查询描述
	 * @param categorylst
	 * @return
	 * @throws BusinessException 
	 * @throws SeeyonFormException 
	 */
	public List assignQuery(List categorylst,SessionObject sessionobject, User user) throws BusinessException, SeeyonFormException;
	
	/**
	 * 用于装配应用名称,统计名称,统计描述
	 * @param categorylst
	 * @return
	 * @throws BusinessException 
	 * @throws SeeyonFormException 
	 */
	public List assignReport(List categorylst,SessionObject sessionobject, User user) throws BusinessException, SeeyonFormException;
	/**
	 * 向session对象中塞入系统变量和扩展绑定
	 */
	public SessionObject systemenum(SessionObject sessionobject) throws SeeyonFormException;
	/**
	 * 用于列表分页
	 */
	public  List pagenate(List list) ;
	
	/**
	 * 获得系统时间
	 * @return
	 */
	public String Systemdata();	

	
	/**
	 * 字段校验
	 * @return
	 */
	public String checkFormFields(SessionObject sessionobject,HttpServletRequest request)throws SeeyonFormException;
	
	public List queryAllAccess(List<Long> formobjlist,List<Long> appidlist,int objtype) throws DataDefineException;
	
	public List queryreportlist(List<Long> formobjlist,int objtype,Long userId) throws BusinessException;
	
	public BaseModel findBiggestValueSign() throws DataDefineException;
	/**
	 * 加1后并得到加后的值
	 * @param bm
	 * @return 先前值的4位数形式，如0001
	 * @throws DataDefineException
	 */
	public String incrementAndGetBiggestValueSign() throws DataDefineException;
	
	public boolean delelctTableValueSign(Long id) throws DataDefineException;
	
	public void formenumeditifuse(SessionObject sessionobject) throws DataDefineException;
	
    public  void formenumnewifuse(SessionObject sessionobject) throws DataDefineException;
    
    public List queryFlowIdByVariableName(String name) throws DataDefineException;
   
    public List queryFlowIdByVariableName(String name,Long accountId) throws DataDefineException;
    
    public FormFlowid queryFlowIdById(Long id) throws DataDefineException;
    
    public BaseModel saveFlowId(BaseModel bm) throws DataDefineException;
    
    public void updateFlowIdValue(String variableName, Long value) throws DataDefineException;
    
    public void updateFlowIdValue(String variableName, Long accountId ,Long value) throws DataDefineException;
    
    public void updateFlowId(BaseModel bm) throws DataDefineException;
    
    public void deleteFlowId(String id) throws DataDefineException;

    public void updateFlowIdState(String variableName) throws DataDefineException;

    public List<FormFlowid> getFlowidList(String accountId) throws DataDefineException;
    /**
     * 表单流水号的查询
     * @param accountId
     * @param condition
     * @param conditionValue
     * @return
     * @throws DataDefineException
     */
    public List<FormFlowid> getFlowidList(String accountId,String condition,String conditionValue) throws DataDefineException;
    public List<FormFlowid> getFlowidList(String accountId, String condition, String conditionValue, boolean isNeedPage) throws DataDefineException;
    
    public void othereditSave(SessionObject sessionobject,HttpServletRequest request,FileManager fileManager) throws SeeyonFormException, SQLException;
    public void othereditSave(SessionObject sessionobject,HttpServletRequest request) throws SeeyonFormException, SQLException;
		
    public String queryOwnerByAppname(String appName) throws DataDefineException; 
    public void formindex(ISeeyonForm_Application fapp,String id)throws SeeyonFormException;
    /**
     * 查看人员是否制作表单
     * @param ownerId   人员id
     * @return
     * @throws DataDefineException
     */
    public boolean queryOwnerListByownerid(Long ownerId) throws DataDefineException;
    
    public StringBuffer categoryHTML(TempleteCategoryManager templeteCategoryManager);
    public List categoryList(Long categoryid) throws DataDefineException;
    
    public void updateByformstart(Long id,int formstart,SessionObject sessionobject) throws DataDefineException;
    public List queryAllOther(BaseModel bm) throws DataDefineException;
    /**
	 * 得到该表单流水号的列表
	 * accountId 表单id
	 * @throws DataDefineException 
	 *
	 */
	public List<FormFlowid> getFlowidListbyformid(String name) throws DataDefineException;
	/**
	 * 流水号被调用后更新state和所属表单id 
	 * @param sessionobject
	 * @throws DataDefineException
	 */
	public void updateFlowIdstate(SessionObject sessionobject) throws DataDefineException;
	
	 /**
	  * 统计动态建立索引
	  */
	 public void formindexbyreport(ISeeyonForm_Application fapp,String id) throws SeeyonFormException;
	 /**
	  * 流程结束后在表单动态中把finishflag值赋为1
	  * @param formappid
	  * @param recordid
	  * @param summaryState 协同流程状态
	  * @throws SeeyonFormException
	  * @throws SQLException
	  */
	 public void updateFinishedBySummaryId(ColSummary summary,Long formappid,Long recordid, Constant.flowState summaryState) throws SeeyonFormException, SQLException;
	 
	 /**
	  * 修改表单动态表中的state字段
	  * @param formAppId
	  * @param recordId
	  * @param state
	  * @throws SeeyonFormException
	  * @throws SQLException
	  */
	 public void updateState4Form(Long formAppId, Long recordId, int state) throws SeeyonFormException, SQLException;
	 /**
		 * 
		 * 表单管理员一次性将所属表单移交
		 */
	 public void updateOwnerListByownerid(Long ownerId) throws DataDefineException;
	 public FormAppMain findAppbyId(Long formappid) throws DataDefineException;
	 /**
		 * 检验有无执行查询或统计条件的权限
		 * user 当前登陆用户
		 * appId 应用id（表单id）
		 * objectName 查询或统计条件名称
		 * objectType 类型(查询为1，统计为2)
		 */
	public boolean checkAccess(User user, Long appId, String objectName,int objectType ) throws DataDefineException, BusinessException;
	
	/**
	 * 从数据库中获取用户有权查看的表单查询或表单统计模板 added by Meng Yang at 2009-09-21
	 */
	public List<FomObjaccess> getFormQueryOrReportNamesByAppId4User(long refAppmainId, List<Long> domainIds, boolean isAdmin) throws DataDefineException;
	
	/**
	 * 重载权限过滤方法，改为使用一条sql语句获取总数判断用户是否具有对特定表单查询或表单统计模板的权限 added by Meng Yang at 2009-09-21
	 */
	public boolean checkAccess4BizConfig(List<Long> domainIds, Long appId, String objectName,int objectType ) throws DataDefineException, BusinessException;
	/**
	 * 在表单标题设置的时候，能够用于绑定的表单字段的List
	 * 主表的有编辑权限的字段都可以进入这个设置
	 * 过滤掉签章类型的字段
	 * @param SessionObject
	 * @param request
	 * @return
	 */
	public List<String> checkFormbindField(SessionObject sessionObject ,HttpServletRequest request) ;
	/**
	 * 
	 * @param sessionObject
	 * @return
	 */
	public List<String> checkFormbindSystemValue(SessionObject sessionObject ) ;
	
	/**
	 * 返回录入定义为选人的表单字段包含关联情况
	 * 只返回主表字段
	 * @param sessionObject
	 * @return
	 */
	public List<TableFieldDisplay> getSelectPeopleFieldIncludingRef(SessionObject sessionObject);
	
	/**
	 * 返回录入定义为选人的表单字段
	 * 只返回主表字段
	 * @param sessionObject
	 * @return
	 */
	public List<TableFieldDisplay> getSelectPeopleField(SessionObject sessionObject) ;
	/**
	 * 返回录入定义为扩展控件的表单字段
	 * 返回的是主表的定义
	 * @param sessionObject 
	 * @param extendsName 扩展控件名称表
	 * @return
	 */
	public List<TableFieldDisplay> getSelectExtendField(SessionObject sessionObject , String... extendsName) ;
	/**
	 * 返回录入定义为扩展控件的表单字段
	 * @param sessionObject
	 * @param isMainTable  是否只返回主表数据
	 * @param extendsName 扩展控件名称表
	 * @return
	 */
	public List<TableFieldDisplay> getSelectExtendField(SessionObject sessionObject , Boolean isMainTable ,String... extendsName) ;
	
	/**
	 * 表单数据单个字段数据的获取
	 * 
	 * @param summary
	 * @param fileName
	 * @param templeteManager
	 * @return
	 * @deprecated
	 * @throws Exception
	 * 
	 */
	public String getFormFileDisPlayValue(ColSummary summary,String fileName,Templete templete) throws Exception ;
	/**
	 * 获取表单的节点的值
	 * @param summary
	 * @param templete
	 * @return
	 * @throws Exception
	 */
	public Map<String ,String[]> getFieldValueMap(ColSummary summary,Templete templete) throws Exception;
	/**
	 * 
	 * @param domainIds
	 * @return
	 * @throws Exception
	 */
	public List<FomObjaccess> getFomObjaccessByAppIds(List<Long> domainIds) throws Exception;
	/**
	 * 
	 * @param newAppList
	 * @param templeteCategories
	 * @param formNameList
	 * @param appidlist
	 * @param type
	 * @throws Exception
	 */
	public void getformAccess(List <FormAppMain> newAppList,
			Set<TempleteCategory> templeteCategories,
			Set<String> formNameList,
			List<Long> appidlist,
			int type )throws Exception ;
}
