package com.seeyon.v3x.edoc.manager;

import java.util.List;
import java.util.Set;
import java.util.Hashtable;

import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;

import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.domain.EdocFormAcl;
import com.seeyon.v3x.edoc.domain.EdocFormElement;
import com.seeyon.v3x.edoc.domain.EdocFormExtendInfo;
import com.seeyon.v3x.edoc.domain.EdocFormFlowPermBound;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.webmodel.EdocFormModel;
import com.seeyon.v3x.edoc.webmodel.EdocOpinionDisplayConfig;
import com.seeyon.v3x.common.authenticate.domain.User;
public interface EdocFormManager 
{
	
	public void initialize();
	
	public void createEdocForm(EdocForm edocForm,List<Long> elementIdList);
	
	public void updateEdocForm(EdocForm edocForm) throws Exception;
	public void updateEdocFormExtendInfo(EdocFormExtendInfo edocForm) throws Exception;

	public EdocForm getEdocForm(long id);
	
	public EdocForm getDefaultEdocForm(Long domainId,int edocType);
	public void setDefaultEdocForm(Long domainId,int edocType,EdocForm edocForm);
	
	public List<EdocForm> getAllEdocForms(Long domainId);
	public List<EdocForm> getAllEdocFormsForWeb(User user,Long domainId);
	public  EdocFormExtendInfo getEdocFormExtendInfo(Long id);
	public List<EdocForm> getAllEdocFormsByStatus(Long domainId,int status);
	 public List<EdocForm> getAllEdocFormsByName(User user ,Long domainId,String name);
	
	public List<EdocForm> getAllEdocFormsByType(Long domainId,int type);
	
	public List<EdocForm> getAllEdocFormsByTypeAndStatus(Long domainId,int type, int status);
	
	public List<EdocForm> getEdocForms(long domainId,String domainIds, int type);
	
	public void removeEdocForm(long id) throws Exception;
	
	public List<EdocFormElement> getAllEdocFormElements();
	
	public List<EdocFormElement> getEdocFormElementByFormId(long formId);
	public String getEdocFormXmlData(long formId,EdocSummary edocSummary,long actorId, int edocType);//新增公文单类型
	
	public EdocFormModel getEdocFormModel(long formId,EdocSummary edocSummary,long actorId) throws EdocException;
	public EdocFormModel getEdocFormModel(long formId,EdocSummary edocSummary,long actorId,boolean isTemplete ,boolean isCallTemplete) throws EdocException;

	public EdocFormModel getEdocFormModel(long formId,long actorId) throws EdocException;
	
	 public void saveEdocXmlData(long id,List<Long> elementIdList);
	 
	 public void deleteFormElementByFormId(long formId);
	
	 public String deleteForm(long id) throws Exception;
	 
	 public void updateForm(EdocForm edocForm);
	 
	 public String getDirectory(String[] urls,String[] createDates,String[] mimeTypes,String[] names)throws Exception;
	 
	  public void updateDefaultEdocForm(long domainId,int type);
	  
	  /**
	   * 新建立单位后，生成默认公文单
	   * @param accountId
	   * @throws Exception
	   */
	  public void initAccountEdocForm(long accountId) throws Exception;
	  /**
	   * 复制公文单到当前单位
	   * @param formIds:逗号分割的公文单id
	   * @throws Exception
	   */
	  public void importEdocForm(String formIds) throws Exception;
	  
	  /**
	   * 检查公文单是否有重名
	   * @param name 名称
	   * @param type 类型(发文,收文,签报)
	   * @param status 状态,是否为启用
	   * @param domainId 单位标识
	   * @return
	   */
	  public boolean checkHasName(String name,int type);

	/**
	 * 绑定公文单处理意见与节点权限
	 * 
	 * @param name
	 *            处理意见： shenpi,fuhe,niban...
	 * @param boundName
	 *            节点权限名称 : shenpi, fuhe, niban...
	 * @param edocFormId
	 *            : 公文单ID
	 * @param sortType
	 *            处理意见排序方式 0,1,2,3
	 * @param orgAccountId
	 *            : 单位Id
	 * @throws Exception
	 */
	  public void bound(String name, String boundName, String boundNameLabel, long edocFormId, String sortType,Long accoutId)throws Exception;
	  /**
	   * 公文单Id
	   * @param edocFormId
	   * @return:返回节点权限为KEY,公文元素名称为value的Hashtable
	   */
	  public Hashtable<String,String> getOpinionLocation(Long edocFormId);
	  /**
	   * 公文单Id
	   * @param edocFormId
	   * @param aclAccountId :公文单被授权使用的单位
	   * @return:返回节点权限为KEY,公文元素名称为value的Hashtable
	   */
	  public Hashtable<String,String> getOpinionLocation(Long edocFormId,Long aclAccountId);
	  /**
	   * 返回公文单中，意见绑定元素的名称
	   * @param edocFormId
	   * @return
	   */
	  public List<String> getOpinionElementLocationNames(Long edocFormId);
	  /**
	   * 返回公文单中，意见绑定元素的名称
	   * @param edocFormId
	   * @param aclAccountId :公文单被授权使用的单位
	   * @return
	   */
	  public List<String> getOpinionElementLocationNames(Long edocFormId,Long aclAccountId);
	  
	  public List<EdocFormFlowPermBound> findBoundByFormId(long edocFormId)throws Exception;
	  public List<EdocFormFlowPermBound> findBoundByFormIdAndDomainId(long edocFormId,Long accountId)throws Exception;
	  public List<EdocFormFlowPermBound> findBoundByFormId(long edocFormId,String processName,long accountId)throws Exception;
	  public List<EdocFormFlowPermBound> findBoundByFormId(long edocFormId, String processName)throws Exception;
		
	  public void deleteEdocFormFlowPermBoundByFormId(long edocFormId)throws Exception;
	  public void deleteEdocFormFlowPermBoundByFormIdAndAccountId(long edocFormId,long AccountId)throws Exception;

	  public String ajaxIsReferenced(String id)throws Exception;	  
	  
		public void saveEdocForm(EdocForm form); 
		
		public void updateFormContentToDBOnlyForOracle1();
		
		public SessionObject getElementByEdocForm(EdocForm edocForm);

		  /**
		   * 方法描述：ajax方法，动态判断是否重名
		   *
		   */
		  public boolean ajaxCheckDuplicatedName(String name, String type, String id);
		  
		  public void removeDefaultEdocForm(Long domainId,int edocType);
		  
		  public boolean ajaxCheckFormIsIdealy(String edocFormId, String isDefault, String isEnabled);
	public void saveEdocFormExtendInfo(EdocFormExtendInfo form);
	public List<EdocForm> getEdocFormByAcl(String domainIds);
	public boolean isExsit(Long formId);
	/**
	 * 取得公文单意见显示设置
	 * @param formId  ： 公文单ID
	 * @param accountId ：单位ID
	 * @return
	 */
	public EdocOpinionDisplayConfig getEdocOpinionDisplayConfig(Long formId,Long accountId);
}
