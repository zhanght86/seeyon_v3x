/**
 * EdocElementManagerImpl.java
 * Created on 2007-4-19
 */
package com.seeyon.v3x.edoc.manager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.dao.EdocElementDao;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 *
 * @author <a href="mailto:handy@seeyon.com">Han Dongyou</a>
 *
 */
public class EdocElementManagerImpl implements EdocElementManager
{   
	private final static Log log = LogFactory.getLog(EdocElementManagerImpl.class);
    
    private EdocElementDao edocElementDao;
 
    private static boolean initialized = false;
    private static Long groupDomainId=0L;
    private static Hashtable<String, EdocElement> elementTable = null;
    private static Hashtable<String, EdocElement> filedEleTable = null;
    private static Hashtable<Long, EdocElement> elementIdTable = null;
    private static Hashtable<Long, List<EdocElement>> cmpElementsTable = null;
    private static List<EdocElement> elements = null;
    private static List<EdocElement> allElements = null;
    
    /**
     * 构造函数。
     *
     */
    public EdocElementManagerImpl()
    {        
    }
    
    private synchronized void initialize()
    {      
        if (initialized)
            return ;        
        elementTable = new Hashtable<String, EdocElement>();
        filedEleTable= new Hashtable<String, EdocElement>();
        elementIdTable= new Hashtable<Long, EdocElement>();
        cmpElementsTable=new Hashtable<Long, List<EdocElement>>();
        elements = new ArrayList<EdocElement>();
        allElements = new ArrayList<EdocElement>();             
        
        //EdocElementManagerImpl manager = new EdocElementManagerImpl();
        //EdocElementDao edocElementDao = manager.getEdocElementDao();
        allElements = edocElementDao.getAllEdocElements();
        if (allElements != null)
        {
            for (int i = 0; i < allElements.size(); i++)
            {
                EdocElement element = (EdocElement)allElements.get(i);
                String elementId = element.getElementId()+element.getDomainId();
                String fieldKey=element.getFieldName()+element.getDomainId();
                elementTable.put(elementId, element);
                filedEleTable.put(fieldKey,element);
                elementIdTable.put(element.getId(), element);
                if (element.getStatus() == EdocElement.C_iStatus_Active)
                {
                    elements.add(element);
                }
                //每个单位公文元素放到hs中
                List<EdocElement> cmpList=cmpElementsTable.get(element.getDomainId());
                if(cmpList==null){cmpList=new ArrayList<EdocElement>();}
                cmpList.add(element);
                cmpElementsTable.put(element.getDomainId(),cmpList);
            }
        }
        initialized = !changeVerAddElement();
    }
    
    public EdocElementDao getEdocElementDao() {
        return edocElementDao;
    }

    public void setEdocElementDao(EdocElementDao edocElementDao) {
        this.edocElementDao = edocElementDao;
    }    
        
    public void updateEdocElement(EdocElement element)
    {      
        String groupId=""+CurrentUser.get().getLoginAccount();
        edocElementDao.update(element);
        elementTable.put(element.getElementId()+groupId, element);//修改内存中对象
        /*initialized = false;
        initialize();
        */
    }
    
    public void updateElementTable(String accountId,EdocElement element){
    	elementTable.put(element.getElementId()+accountId, element);
    	initialized = false;
    }
    
    public EdocElement getEdocElement(String elementId)
    {
        if (!initialized)
            initialize();
        String groupId=""+CurrentUser.get().getLoginAccount();
        if(CurrentUser.get().isGroupAdmin()){groupId=groupDomainId.toString();}
        return elementTable.get(elementId+groupId);
    }    
        
    public List<EdocElement> getEdocElements()
    {            
        if (!initialized)
            initialize();
        List<EdocElement> tl=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
        if(tl==null || tl.size()<=0)
        {
        	initCmpElement();
        	tl=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
        }
        return tl;
    }
    
    public int getAllEdocElementCount()
    {
    	if (!initialized)
    		initialize();
    	List<EdocElement> tl=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	List<EdocElement> groupElements=cmpElementsTable.get(0L); //集团
    	if(tl==null || tl.size()!= groupElements.size())
        {
        	initCmpElement();
        	tl=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
        }
    	return tl.size();
    }
        
    public List<EdocElement> getAllEdocElements(int startIndex, int numResults)
    {
    	if (!initialized)
            initialize();
        List<EdocElement> _elements = new ArrayList<EdocElement>();
        List<EdocElement> allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());        
        if(allElements==null || allElements.size()<=0)
        {
        	initCmpElement();
        	allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
        }
        
        for (int i = 0; i < allElements.size(); i++)
        {        	
        	if ( (i >= (startIndex - 1) * numResults) && (i < startIndex * numResults) )
			{
				_elements.add(allElements.get(i));		
			}
        }
        return _elements;
    }  
    
    public List<EdocElement> getAllEdocElements()
    {
        if (!initialized)
            initialize();
        List<EdocElement> tl=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
        if(tl==null || tl.size()<=0)
        {
        	initCmpElement();
        	tl=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
        }
        return tl;        
    }  
    
    public int getEdocElementCount(int status)
    {
    	if (!initialized)
    		initialize();
    	List<EdocElement> allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	
    	if(allElements==null || allElements.size()<=0)
    	{
    		initCmpElement();
    		allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	}
    	
    	int count = 0;    	
    	for (int i = 0; i < allElements.size(); i++)
    	{
    		EdocElement element = (EdocElement)allElements.get(i);
    		if (element.getStatus() == status)
    		{
    			count++;
    		}
    	}
    	return count;
    }
    
    public List<EdocElement> getEdocElementsByStatus(int status, int startIndex, int numResults)
    {
    	if (!initialized)
    		initialize();
    	List<EdocElement> _elements = new ArrayList<EdocElement>();
    	
    	List<EdocElement> allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	
    	if(allElements==null || allElements.size()<=0)
    	{
    		initCmpElement();
    		allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	}
    	
    	int j = 0;
    	for (int i = 0; i < allElements.size(); i++)
    	{
    		EdocElement element = (EdocElement)allElements.get(i);
    		if (element.getStatus() == status)
    		{
    			if ( (j >= (startIndex - 1) * numResults) && (j < startIndex * numResults) )
    			{
    				_elements.add(element);    				
    			}
    			j++;
    		}
    	}
    	return _elements;
    }
    
    public List<EdocElement> getEdocElementsByStatus(int status)
    {
    	if (!initialized)
    		initialize();
    	List<EdocElement> _elements = new ArrayList<EdocElement>();
    	List<EdocElement> allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	
    	if(allElements==null || allElements.size()<=0)
    	{
    		initCmpElement();
    		allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	}
    	
    	for (int i = 0; i < allElements.size(); i++)
    	{
    		EdocElement element = (EdocElement)allElements.get(i);
    		if (element.getStatus() == status)
    		{
    				_elements.add(element);    				
    		}
    	}
    	return _elements;
    }
    
    public EdocElement getEdocElementsById(long id){
    	if (!initialized)
    		initialize();
    	return elementIdTable.get(id);    
    }
    
    
    /**
     * add by lindb
     * accroding field_name to get id.
     */
    public long getIdByFieldName(String fieldName){
    	return Long.parseLong(getByFieldName(fieldName).getElementId());
    	//return getByFieldName(fieldName).getId();    	
    }
    
    public EdocElement getByFieldName(String fieldName){
    	if (!initialized)
    		initialize();
    	//判断是否初始化
        List<EdocElement> tl=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
        if(tl==null || tl.size()<=0)
        {
        	initCmpElement();
        }
        //
    	String groupId=""+CurrentUser.get().getLoginAccount();
        if(CurrentUser.get().isGroupAdmin()){groupId=groupDomainId.toString();}
        
    	return filedEleTable.get(fieldName+groupId);    		
    }
    /**
     * getByFieldName
     * fieldName 列表元素的名称
     * userAccountId 发文人的单位ID
     */
    public EdocElement getByFieldName(String fieldName,Long userAccountId){
    	if (!initialized)
    		initialize();
    	//判断是否初始化
    	List<EdocElement> tl = null ;
    	if(userAccountId != null) {
    		tl = cmpElementsTable.get(userAccountId);
    	}else {
    		tl=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	}
       
        if(tl==null || tl.size()<=0)
        {
        	initCmpElement();
        }
        //
        String groupId = "" ;
    	if(userAccountId != null ) {
    		groupId = String.valueOf(userAccountId) ;
    	}else {
    		groupId=""+CurrentUser.get().getLoginAccount();
    	}
    	
        if(CurrentUser.get().isGroupAdmin()) {groupId=groupDomainId.toString();}
        
    	return filedEleTable.get(fieldName+groupId);    		
    }
    
    public synchronized void initCmpElement()
    {
    	if (!initialized){initialize();}
    	OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
    	List<V3xOrgAccount> accounts=null;
    	try{
    		accounts=orgManager.getAllAccounts();
    	}catch(Exception e)
    	{
    		log.error("",e);
    	}
    	List<EdocElement> groupEles=cmpElementsTable.get(groupDomainId);
    	for(V3xOrgAccount account:accounts)
    	{
    		Long domainId=account.getId();
    		List<EdocElement> allElements=cmpElementsTable.get(account.getId());
    		
    		//公文元素的数目和集团的不一样的话，先删除，然后拷贝集团的进行添加
    		//这样做的原因：可能升级的时候直接在升级脚本中给每个单位都添加了新的公文元素，导致进入公文元素页面的时候不拷贝原来的公文元素。
    		
    		if(allElements!=null && allElements.size()== groupEles.size()){
    			continue;
    		}else{
    			//edocElementDao.deleteEdocElementsByDomainId(domainId);
    			int size = allElements==null?0:allElements.size();
    			log.info("初始化公文元素页面。单位ID："+domainId+" 当前元素数目："+size+" 集团元素数目："+groupEles.size());
    		}
    		
    		List<EdocElement> cmpEles=new ArrayList<EdocElement>();
    		for(EdocElement ele:groupEles)
    		{
    			EdocElement tempEle=ele.clone(domainId);
    			edocElementDao.save(tempEle);
    			cmpEles.add(tempEle);
    		}
    		cmpElementsTable.put(domainId,cmpEles);
    	}
    	NotificationManager.getInstance().send(NotificationType.EdocElementCmpElementsTable, null);
    	initialized=false;
    	initialize();
    }
    
    public List<EdocElement> listElementByAccount(Long accountId){
    	return cmpElementsTable.get(accountId);
    }
    
    public void saveCmpElementTable(Long account,List<EdocElement> domainElement){
    	cmpElementsTable.put(account,domainElement);
    }
    
    public synchronized boolean changeVerAddElement()
    {
    	boolean needReload=false;
    	OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
    	List<V3xOrgAccount> accounts=null;
    	try{
    		accounts=orgManager.getAllAccounts();
    	}catch(Exception e)
    	{
    		log.error("",e);
    	}
    	EdocElement ele=null;
    	for(V3xOrgAccount account:accounts)
    	{
    		List<EdocElement> allElements=cmpElementsTable.get(account.getId());
    		if(allElements!=null && allElements.size()>0)
    		{
    			//检查是否存在 docmark2 公文元素
    			String initEle[]={"021","022","023","024","025","026","219"};
    			for(String eleId:initEle)
    			{
    				if(elementTable.get(eleId+account.getId())==null)
    				{
    					ele=elementTable.get(eleId+groupDomainId.toString());
    					if(ele!=null)
    					{
    						EdocElement tempEle=ele.clone(account.getId());
    						//tempEle.setStatus(0);    						
    						edocElementDao.save(tempEle);
    						needReload=true;
    					}
    				}
    			}
    		}    		
    	}
    	return needReload;    	
    }
    
    public List<EdocElement> getByStatusAndType(int status, int type){
    	if (!initialized)
    		initialize();
    	List<EdocElement> _elements = new ArrayList<EdocElement>();
    	List<EdocElement> allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	
    	if(allElements==null || allElements.size()<=0)
    	{
    		initCmpElement();
    		allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	}
    	
    	for (int i = 0; i < allElements.size(); i++)
    	{
    		EdocElement element = (EdocElement)allElements.get(i);
    		if (element.getStatus() == status && element.getType() == type)
    		{
    				_elements.add(element);    				
    		}
    	}
    	return _elements;   	
    }
    
    public String getRefMetadataFieldName(Long domainId,Long metadataId)
    {
    	String fieldName="";
    	if (!initialized)
    		initialize();
    	List <String>fns=new ArrayList<String>();    	
    	for (int i = 0; i < allElements.size(); i++)
    	{
    		EdocElement element = (EdocElement)allElements.get(i);
    		if(element.getType()==EdocElement.C_iElementType_List)
    		{    			
    				if(element.getMetadataId()!=null && element.getMetadataId().intValue()==metadataId.intValue())
    				{
    					if(fns.contains(element.getFieldName())){continue;}
    					if(fieldName.length()>0){fieldName+=",";}
    					fieldName+=element.getFieldName();
    					fns.add(element.getFieldName());
    				}
    		}
    	}    	
    	return fieldName;
    }
    
    /**
     * 取得满足手动输入过滤条件的数据数
     * @param condition
     * @param textfield
     * @return
     */
    private Integer getAllEdocElementCount(String condition, String textfield) {
    	if (!initialized)
    		initialize();
    	List<EdocElement> allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	
    	if(allElements==null || allElements.size()<=0)
    	{
    		initCmpElement();
    		allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	}
    	
    	int count = 0;   
    	if (Strings.isNotBlank(textfield)) {
    		//输入了过滤条件
    		if ("elementName".equals(condition)) {
    			//元素名称
    			for (int i = 0; i < allElements.size(); i++)
    			{
    				EdocElement element = (EdocElement)allElements.get(i);
    				//元素名称
    				String name = element.getName();
    				name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource",name);
    				if(name.contains(textfield)){
    					count++;
    				}
    			}
    			
    		}
    		if ("elementfieldName".equals(condition)) {
    			//元素代码
    			for (int i = 0; i < allElements.size(); i++)
    			{
    				EdocElement element = (EdocElement)allElements.get(i);
    				//元素代码
    				String fildName = element.getFieldName();
    				fildName = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource",fildName);
    				if(fildName.contains(textfield)){
    					count++;
    				}
    			}
    		}
    		return count;
		}
    	else{
    		//未输入过滤条件，返回全部记录
    		return allElements.size();
    	}
	}
    
	public List<EdocElement> getEdocElementsByContidion(String condition,
			String textfield, String statusSelect, int paginationFlag) {
		List<EdocElement> list = null;
		Integer startIndex = 0;
		Integer first = 0;
		Integer pageSize = 0;
		Integer listCount = 0;
		
		if (!initialized)
    		initialize();
    	List<EdocElement> allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	List<EdocElement> groupElements=cmpElementsTable.get(0L); //集团
    	if(allElements==null || allElements.size()!=groupElements.size())
    	{
    		initCmpElement();
    		allElements=cmpElementsTable.get(CurrentUser.get().getLoginAccount());
    	}
		
		//计算listcount
		if (("elementStatus".equals(condition)) && (Strings.isNotBlank(statusSelect))) {
			//状态
			listCount = this.getEdocElementCount(Integer.parseInt(statusSelect));
		}else {
			//取得符合查询条件的总记录数
			listCount = this.getAllEdocElementCount(condition,textfield);
		}
		//计算分页信息
		Pagination.setRowCount(listCount);
		first = Pagination.getFirstResult();
		pageSize = Pagination.getMaxResults();
		if ((first + 1) % pageSize == 0){
			startIndex = first / pageSize;
		}
		else{
			startIndex = first / pageSize + 1;
		}
		if (pageSize == 1){ 
			startIndex = (first+1) / pageSize;
		}
		
		if (("elementStatus".equals(condition)) && (Strings.isNotBlank(statusSelect))) {
			if(paginationFlag == 1){
				//根据公文元素状态进行过滤，带分页
				list = this.getEdocElementsByStatus(Integer.parseInt(statusSelect),startIndex,pageSize);
			}
			else{
				//根据公文元素状态进行过滤，不带分页
				if("1".equals(statusSelect)){
					list = this.getEdocElementsByStatus(EdocElement.C_iStatus_Active);//所有启用的公文元素
				}else if("0".equals(statusSelect)){
					list = this.getEdocElementsByStatus(EdocElement.C_iStatus_Inactive);//所有停用的公文元素
				}
			}
		}
		else {
			//根据手动录入条件数据过滤后的数据
			list = new ArrayList<EdocElement>();
			
	    	if (Strings.isNotBlank(textfield)) {
	    		//输入了过滤条件
	    		if ("elementName".equals(condition)) {
	    			//元素名称
	    			int j = 0;
	    			for (int i = 0; i < allElements.size(); i++)
	    			{
	    				EdocElement element = (EdocElement)allElements.get(i);
	    				//元素名称
	    				String name = element.getName();
	    				name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource",name);
	    				if(name.contains(textfield)){
	    					if(paginationFlag == 1){
	    						if ( (j >= (startIndex - 1) * pageSize) && (j < startIndex * pageSize) )
	    						{
	    							//带分页
	    							list.add(element);    				
	    						}
								j++;
	    					}
	    					else {
	    						//不带分页
	    						list.add(element);
							}
	    				}
	    			}
	    		}
	    		if ("elementfieldName".equals(condition)) {
	    			//元素代码
	    			int j = 0;
	    			for (int i = 0; i < allElements.size(); i++)
	    			{
	    				EdocElement element = (EdocElement)allElements.get(i);
	    				//元素代码
	    				String fildName = element.getFieldName();
	    				fildName = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource",fildName);
	    				if(fildName.contains(textfield)){
	    					if(paginationFlag == 1){
	    						if ( (j >= (startIndex - 1) * pageSize) && (j < startIndex * pageSize) )
	    						{
	    							//带分页
	    							list.add(element);    				
	    						}
								j++;
	    					}
	    					else {
	    						//不带分页
	    						list.add(element);
							}
	    				}
	    			}
	    		}
			}
	    	else{
				// 未输入过滤条件
				if ("elementName".equals(condition)) {
					// 元素名称
					int j = 0;
					for (int i = 0; i < allElements.size(); i++) {
						EdocElement element = (EdocElement) allElements.get(i);
						// 元素名称
						String name = element.getName();
						name = ResourceBundleUtil
								.getString(
										"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
										name);
						if (paginationFlag == 1) {
							if ((j >= (startIndex - 1) * pageSize)
									&& (j < startIndex * pageSize)) {
								// 带分页
								list.add(element);
							}
							j++;
						} else {
							// 不带分页
							list.add(element);
						}
					}
				}
				if ("elementfieldName".equals(condition)) {
					// 元素代码
					int j = 0;
					for (int i = 0; i < allElements.size(); i++) {
						EdocElement element = (EdocElement) allElements.get(i);
						// 元素代码
						String fildName = element.getFieldName();
						fildName = ResourceBundleUtil
								.getString(
										"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
										fildName);
						if (paginationFlag == 1) {
							if ((j >= (startIndex - 1) * pageSize)
									&& (j < startIndex * pageSize)) {
								// 带分页
								list.add(element);
							}
							j++;
						} else {
							// 不带分页
							list.add(element);
						}
					}
				}
	    	}
		}
		return list;
	}
}