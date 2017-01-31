package com.seeyon.v3x.link.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.UrlValidator;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.link.dao.LinkAclDao;
import com.seeyon.v3x.link.dao.LinkCategoryDao;
import com.seeyon.v3x.link.dao.LinkOptionDao;
import com.seeyon.v3x.link.dao.LinkOptionValueDao;
import com.seeyon.v3x.link.dao.LinkSpaceAclDao;
import com.seeyon.v3x.link.dao.LinkSpaceDao;
import com.seeyon.v3x.link.dao.LinkSystemDao;
import com.seeyon.v3x.link.domain.LinkAcl;
import com.seeyon.v3x.link.domain.LinkCategory;
import com.seeyon.v3x.link.domain.LinkOption;
import com.seeyon.v3x.link.domain.LinkOptionValue;
import com.seeyon.v3x.link.domain.LinkSpace;
import com.seeyon.v3x.link.domain.LinkSpaceAcl;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.util.Constants;
import com.seeyon.v3x.link.webmodel.WebLinkOptionValueImportResultVO;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.LightWeightEncoder;
import com.seeyon.v3x.util.Strings;


public class OuterlinkManagerImpl implements OuterlinkManager {
	private static final Log logger = LogFactory.getLog(OuterlinkManagerImpl.class);
	private LinkSystemDao linkSystemDao;
	private LinkAclDao 	  linkAclDao;
	private LinkOptionDao linkOptionDao;
	private LinkOptionValueDao linkOptionValueDao;
	private LinkCategoryDao linkCategoryDao;
	private LinkSpaceDao linkSpaceDao;
	private LinkSpaceAclDao linkSpaceAclDao;
	private OrgManager 	   orgManager;
	private FileManager fileManager;
	private AttachmentManager attachmentManager;
	
	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

    public void addLinkAcl(long linkSystemId, String userType, long... userId) throws Exception {
	//	User user=CurrentUser.get();
	//	if(user.isAdministrator()==false)throw new Exception("不是系统管理员，不能进行授权操作");
		for(int i=0;i<userId.length;i++){
			LinkAcl linkAcl=new LinkAcl();
			linkAcl.setIdIfNew();
			linkAcl.setLinkSystemId(linkSystemId);
			linkAcl.setUserType(userType);
			linkAcl.setUserId(userId[i]);
			linkAclDao.save(linkAcl);
		}
		
	}

	public void addLinkOption(String paramName, String paramSign, String paramValue, boolean isPwd,int orderNum, long linkSystemId) throws Exception{
		LinkOption linkOption = new LinkOption();
		linkOption.setIdIfNew();
		linkOption.setParamName(paramName);
		linkOption.setParamSign(paramSign);
		linkOption.setIsPassword(isPwd);
		linkOption.setOrderNum(orderNum);
		linkOption.setLinkSystemId(linkSystemId);
		if(Strings.isBlank(paramValue)){
			linkOption.setIsDefault(false);
			linkOption.setParamValue("");
		}else {
			linkOption.setParamValue(LightWeightEncoder.encodeString(paramValue));
			linkOption.setIsDefault(true);
		}	
		linkOptionDao.save(linkOption);
	}

	public void addLinkOptionValue(long linkOptionId, String value,long userId) {
		LinkOptionValue optionValue=linkOptionValueDao.findOptionValues(userId, linkOptionId);
		if(optionValue == null){
			LinkOptionValue option=new LinkOptionValue();
			option.setIdIfNew();
			option.setLinkOptionId(linkOptionId);
			option.setValue(Strings.isBlank(value) ? " " : LightWeightEncoder.encodeString(value));
			option.setUserId(userId);
			linkOptionValueDao.save(option);
		}else{
			optionValue.setLinkOptionId(Long.valueOf(linkOptionId));
			optionValue.setUserId(userId);
			optionValue.setValue(Strings.isBlank(value) ? " " : LightWeightEncoder.encodeString(value));
			linkOptionValueDao.update(optionValue);
		}
		
	}

	public long addLinkSystem(String name, int orderNum, String description, String url, boolean needContentCheck, String contentForCheck, boolean sameRegion, String agentUrl, String method, String image, 
			long categoryId, boolean allowedAsSpaceNavigation, boolean allowedAsSection) throws Exception {

		User user=CurrentUser.get();
		LinkSystem linkSystem=new LinkSystem();
		linkSystem.setIdIfNew();
		linkSystem.setName(name);
		linkSystem.setOrderNum(orderNum);
		linkSystem.setDescription(description);
		linkSystem.setUrl(url);
		linkSystem.setNeedContentCheck(needContentCheck);
		linkSystem.setContentForCheck(contentForCheck);
		linkSystem.setSameRegion(sameRegion);
		linkSystem.setAgentUrl(agentUrl);
		linkSystem.setCreateUserId(user.getId());
		linkSystem.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
		linkSystem.setLastUserId(user.getId());
		linkSystem.setLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
		linkSystem.setMethod(method);
		linkSystem.setImage(image);
		LinkCategory lc = linkCategoryDao.get(categoryId);
		linkSystem.setIsSystem(lc.getIsSystem());
		linkSystem.setStatus(Constants.LINK_STATUS);
		linkSystem.setLinkCategoryId(categoryId);
		linkSystem.setAllowedAsSpace(allowedAsSpaceNavigation);
		linkSystem.setAllowedAsSection(allowedAsSection);
		//linkSystem.setOpenType(openType);
		linkSystemDao.save(linkSystem);
		
		return linkSystem.getId();
		
	}

	public void deleteLinkAcl(long systemLinkId, byte userType, long... userId) throws Exception {
		User user=CurrentUser.get();
		if(user.isAdministrator()==false)throw new Exception("不是系统管理员");
		List list=new ArrayList();
		LinkSystem linkSystem=this.getLinkSystemById(systemLinkId);
		Set set=linkSystem.getLinkAcl();
		Iterator it=set.iterator();
//		for(int i=0;i<userId.length;i++){
//			LinkAcl linkAcl=(LinkAcl)it.next();
//			if(linkAcl.getUserType()==userType){
//				for(int j=0;j<userId.length;j++){
//					if(linkAcl.getUserId()==userId[j]){
//						list.add(linkAcl);
//					}
//				}
//			}
//		}
		set.removeAll(list);
		linkSystem.setLinkAcl(set);
		linkSystemDao.update(linkSystem);
	}

	public void deleteLinkOptionById(long linkOptionId) {
		LinkOption linkOption=linkOptionDao.get(linkOptionId);
		linkOptionDao.deleteObject(linkOption);
		
	}

	public void deleteLinkSystem(List<Long> linkSystemId) throws Exception{
	//	User user=CurrentUser.get();
	//	if(user.isAdministrator()==false)throw new Exception("不是系统管理员，无权执行该操作");
		for(int i=0;i<linkSystemId.size();i++){
			LinkSystem linkSystem=this.getLinkSystemById(linkSystemId.get(i));
//			String image = linkSystem.getImage();
//			String sFileId = image.substring(image.indexOf("&fileId=") + 8, image.indexOf("&createDate"));
//			System.out.println("***********************" + sFileId + "*************");
//			fileManager.deleteFile(Long.valueOf(sFileId), true);
			attachmentManager.deleteByReference(linkSystem.getId());
			linkSystemDao.deleteObject(linkSystem);	//级联删除授权和高级设置以及设置得值
		}	
	}

	public void deleteOptionValueByUserId(long userId, long linkSystemId) {
		LinkSystem linkSystem=this.getLinkSystemById(linkSystemId);
		Set set=linkSystem.getLinkOption();
		Iterator it=set.iterator();
		while(it.hasNext()){
			LinkOption option=(LinkOption)it.next();
			LinkOptionValue value=linkOptionValueDao.findOptionValues(userId, option.getId());
			linkOptionValueDao.deleteObject(value);
		}
		
	}

	public List<LinkSystem> findOutLinkByUserId(long userId) throws Exception{
		String userInfo = com.seeyon.v3x.doc.util.Constants.getOrgIdsOfUser(userId);
		List<Long> list = linkAclDao.findLinkByAcl(userInfo);	
		List<LinkSystem> allSystem = linkSystemDao.getAllSystem(list, null, userId);
		return allSystem;
	}
	
	/**
	 * 获取当前用户可以访问的、允许配置为空间导航的内部、外部关联项目
	 * @param userId  当前用户ID
	 */
	public List<LinkSystem> findAllRelatedSystemsAllowedAsSpace(Long userId) {
		return this.linkSystemDao.getLinkSystemsAllowedAsSpace(com.seeyon.v3x.doc.util.Constants.getOrgIdsOfUser1(userId));
	}
	
	/**
	 * 校验当前用户是否能够继续使用关联系统
	 * @param userId			当前用户ID
	 * @param systemId			关联系统ID
	 * @param systemCategoryId	关联系统所属系统分类ID
	 * @return
	 */
	public boolean canUseTheSystem(Long userId, Long systemId, Long systemCategoryId) {
		return this.linkSystemDao.canUseTheSystem(systemId, systemCategoryId, com.seeyon.v3x.doc.util.Constants.getOrgIdsOfUser1(userId));
	}
	
	public List<LinkSystem> findAllLinkSystemByUser(Long userId) throws Exception {
		return this.findOutLinkByUserId(userId);
	}
	
	public List<LinkSystem> findOutLinkOfCurrentUserByPage() throws Exception {
		List<LinkSystem> all = this.findOutLinkByUserId(CurrentUser.get().getId());
		List<LinkSystem> ret = new ArrayList<LinkSystem>();
		int size = all.size();
		int end = Pagination.getFirstResult() + Pagination.getMaxResults();
		if (Pagination.isNeedCount()) {
			Pagination.setRowCount(size);
		}
		ret.addAll(all.subList(Pagination.getFirstResult(), size < end ? size : end));
		return ret;
	}
	
	private List getLinks(Set set){
		List list=new ArrayList();
		List list1=new ArrayList();		//常用链接
		List list2=new ArrayList();		//内部系统
		List list3=new ArrayList();		//外部系统
		Iterator it=set.iterator();
		while(it.hasNext()){
			long id=(Long)it.next();
			LinkSystem linkSystem=this.getLinkSystemById(id);
			if(linkSystem.getLinkCategoryId()==Constants.LINK_COMMON){
				list1.add(linkSystem);
			}else if(linkSystem.getLinkCategoryId()==Constants.LINK_IN){
				list2.add(linkSystem);
			}else if(linkSystem.getLinkCategoryId()==Constants.LINK_OUT){
				list3.add(linkSystem);
			}
		}
		list.add(list1);
		list.add(list2);
		list.add(list3);
		return list;
	}

	public List findOutLinks()throws Exception {
		User user=CurrentUser.get();
		if(user.isAdministrator()==false)throw new Exception("不是系统管理员，无权执行此操作");
		List list=linkSystemDao.getAll();	//获取所有的链接
		List the_list=new ArrayList();
		List list1=new ArrayList();			//常用链接
		List list2=new ArrayList();			//内部系统
		List list3=new ArrayList();			//外部系统
		for(int i=0;i<list.size();i++){
			LinkSystem linkSystem=(LinkSystem)list.get(i);
			if(linkSystem.getLinkCategoryId()==Constants.LINK_COMMON){
				list1.add(linkSystem);
			}else if(linkSystem.getLinkCategoryId()==Constants.LINK_IN){
				list2.add(linkSystem);
			}else if(linkSystem.getLinkCategoryId()==Constants.LINK_OUT){
				list3.add(linkSystem);
			}
		}
		list.add(list1);
		list.add(list2);
		list.add(list3);
		return list;
	}


	//按顺序得到的高级选项集合
	public List<LinkOption> getlinkOptionBySystemId(long linkSystemId) {
		
		
		return linkOptionDao.getLinkOptionBySystemId(linkSystemId);
	}

	
	public List<LinkOptionValue> findOptionValueById(List<Long> linkOptionId, long userId) {
	
		return linkOptionValueDao.getOptionValues(userId, linkOptionId);
	}

	public LinkSystemDao getLinkSystemDao() {
		return linkSystemDao;
	}

	public void setLinkSystemDao(LinkSystemDao linkSystemDao) {
		this.linkSystemDao = linkSystemDao;
	}

	public LinkAclDao getLinkAclDao() {
		return linkAclDao;
	}

	public void setLinkAclDao(LinkAclDao linkAclDao) {
		this.linkAclDao = linkAclDao;
	}

	public LinkOptionDao getLinkOptionDao() {
		return linkOptionDao;
	}

	public void setLinkOptionDao(LinkOptionDao linkOptionDao) {
		this.linkOptionDao = linkOptionDao;
	}

	public LinkOptionValueDao getLinkOptionValueDao() {
		return linkOptionValueDao;
	}

	public void setLinkOptionValueDao(LinkOptionValueDao linkOptionValueDao) {
		this.linkOptionValueDao = linkOptionValueDao;
	}

	public LinkSystem getLinkSystemById(long linkSystemId) {
		
		return linkSystemDao.get(linkSystemId);
	}
	public List<LinkSystem> getLinkSystemByIds(List<Long> ids){
		return linkSystemDao.getLinkSystems(ids);
	}
	public LinkSystem getLinkSystemByName(String linkSystemName){
		return linkSystemDao.getLinkSystemByName(linkSystemName);
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void addLinkAclByCategory(long categoryId,String userType,long... userId) {
		LinkAcl linkAcl=new LinkAcl();
		for(int i=0;i<userId.length;i++){
			linkAcl.setIdIfNew();
			linkAcl.setLinkCategoryId(categoryId);
			linkAcl.setUserId(userId[i]);
			linkAcl.setUserType(userType);
			linkAclDao.save(linkAcl);
		}
		
	}

	public List<LinkSystem> getAllLinkSystem() {
		
		return linkSystemDao.getAll();
	}

	public List<LinkCategory> getAllLinkCategory() {
		List<LinkCategory> list=linkCategoryDao.getAll();
		List<LinkCategory> system=new ArrayList<LinkCategory>();
		List<LinkCategory> customer=new ArrayList<LinkCategory>();
		for(int i=0;i<list.size();i++){
			LinkCategory category=list.get(i);
			if(category.getIsSystem() == Constants.IS_SYSTEM){
				system.add(category);
			}else{
				customer.add(category);
			}
		}
		
		system.addAll(customer);
		return system;
	}

	public LinkCategoryDao getLinkCategoryDao() {
		return linkCategoryDao;
	}

	public void setLinkCategoryDao(LinkCategoryDao linkCategoryDao) {
		this.linkCategoryDao = linkCategoryDao;
	}
	
    public LinkSpaceDao getLinkSpaceDao() {
        return linkSpaceDao;
    }
    
    public void setLinkSpaceDao(LinkSpaceDao linkSpaceDao) {
        this.linkSpaceDao = linkSpaceDao;
    }
    
    public LinkSpaceAclDao getLinkSpaceAclDao() {
        return linkSpaceAclDao;
    }
    
    public void setLinkSpaceAclDao(LinkSpaceAclDao linkSpaceAclDao) {
        this.linkSpaceAclDao = linkSpaceAclDao;
    }

    public int getMaxLinkSystemOrder(long categoryId) {
		return linkSystemDao.getMaxOrder(categoryId);
	}

	public LinkCategory getLinkCategoryBylinkId(long linkSystemId) {
		LinkSystem link=linkSystemDao.get(linkSystemId);
		return linkCategoryDao.get(link.getLinkCategoryId());
	}

	public void updateLinkSystem(String name, int orderNum, String description, String url, boolean needContentCheck, String contentForCheck, boolean sameRegion, String agentUrl, String method , 
			String image ,long categoryId, boolean allowedAsSpace, boolean allowedAsSection, LinkSystem linkSystem)throws Exception {

		User user=CurrentUser.get();
	//	LinkSystem linkSystem=linkSystemDao.get(systemId);
		linkSystem.setName(name);
		linkSystem.setOrderNum(orderNum);
		linkSystem.setDescription(description);
		linkSystem.setUrl(url);
        linkSystem.setNeedContentCheck(needContentCheck);
        linkSystem.setContentForCheck(contentForCheck);
        linkSystem.setSameRegion(sameRegion);
        linkSystem.setAgentUrl(agentUrl);
		linkSystem.setLastUserId(user.getId());
		linkSystem.setLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
		linkSystem.setMethod(method);
		linkSystem.setImage(image);
		linkSystem.setLinkCategoryId(categoryId);
		linkSystem.setAllowedAsSpace(allowedAsSpace);
		linkSystem.setAllowedAsSection(allowedAsSection);
		//linkSystem.setOpenType(openType);
	}

	public LinkOptionValue getOptionValueById(long linkOptionId, long userId) {
		
		return linkOptionValueDao.findOptionValues(userId, linkOptionId);
	}

	public List<LinkSystem> findOutLinkBySize(long userId, int size,long categoryId)throws Exception {
		String userInfo=com.seeyon.v3x.doc.util.Constants.getOrgIdsOfUser(userId);
		List<Long> the_list=linkAclDao.findLinkByAcl(userInfo);
		List<LinkSystem> link=linkSystemDao.getLinkSystemByIds(the_list, size, categoryId, userId);
		return link;
	}
	
	public List<List<LinkSystem>> findAllInnerAndOutter() throws Exception {
		List<List<LinkSystem>> ret = new ArrayList<List<LinkSystem>>();
		List<LinkSystem> inners = new ArrayList<LinkSystem>();
		List<LinkSystem> outters = new ArrayList<LinkSystem>();
		List<LinkSystem> commons = new ArrayList<LinkSystem>();

		List<LinkSystem> all = this.findOutLinkByUserId(CurrentUser.get().getId());
		for (LinkSystem ls : all) {
			if (ls.getLinkCategoryId() == Constants.LINK_IN) {
				inners.add(ls);
			} else if (ls.getLinkCategoryId() == Constants.LINK_OUT) {
				outters.add(ls);
			} else if (ls.getLinkCategoryId() == Constants.LINK_COMMON) {
				commons.add(ls);
			}
		}

		ret.add(inners);
		ret.add(outters);
		ret.add(commons);
		return ret;
	}
	
	public List<LinkSystem> findAllCommonLinks()throws Exception{
		List<LinkSystem> commons = new ArrayList<LinkSystem>();
		
		List<LinkSystem> all = this.findOutLinkByUserId(CurrentUser.get().getId());
		for(LinkSystem ls : all){
			if(ls.getLinkCategoryId() == Constants.LINK_COMMON)
				commons.add(ls);
		}
		return commons;
	}
	
	public List<LinkSystem> findMoreLinks(long categoryId)throws Exception{
		List<LinkSystem> commons = new ArrayList<LinkSystem>();
		
		List<LinkSystem> all = this.findOutLinkByUserId(CurrentUser.get().getId());
		for(LinkSystem ls : all){
			if(ls.getLinkCategoryId() == categoryId)
				commons.add(ls);
		}
		return commons;
	}
	
	/**
	 * 获取所有关联系统，不含常用链接
	 * 排序： 内、外、自定义
	 */
	public List<List<LinkSystem>> findAllLinksNoCommon() throws Exception{
		List<List<LinkSystem>> ret = new ArrayList<List<LinkSystem>>();
		List<LinkSystem> inners = new ArrayList<LinkSystem>();
		List<LinkSystem> outters = new ArrayList<LinkSystem>();
		
		Map<Long, List<LinkSystem>> customMap = new HashMap<Long, List<LinkSystem>>();
		
		List<LinkSystem> all = this.findOutLinkByUserId(CurrentUser.get().getId());
		for(LinkSystem ls : all){
			if(ls.getLinkCategoryId() == Constants.LINK_IN)
				inners.add(ls);
			else if(ls.getLinkCategoryId() == Constants.LINK_OUT)
				outters.add(ls);
			else if(ls.getLinkCategoryId() != Constants.LINK_COMMON){
				// 自定义
				List<LinkSystem> lss = customMap.get(ls.getLinkCategoryId());
				if(lss == null){
					lss = new ArrayList<LinkSystem>();
					lss.add(ls);
					customMap.put(ls.getLinkCategoryId(), lss);
				}else{
					lss.add(ls);
				}
			}
		}
				
		ret.add(inners);
		ret.add(outters);
		if(customMap.size() > 0){
			Set<Long> set = customMap.keySet();
			for(Long l : set){
				ret.add(customMap.get(l));
			}
		}
		
		if(ret != null && ret.size() > 0){
			for(List<LinkSystem> list : ret)
				Collections.sort(list);
		}
			
		return ret;
	}
	
	/**
	 * 获取所有关联系统
	 * 排序： 内、外、常用、自定义
	 */
	public List<List<LinkSystem>> findAllLinkSystems() throws Exception{
		List<List<LinkSystem>> ret = new ArrayList<List<LinkSystem>>();
		List<LinkSystem> inners = new ArrayList<LinkSystem>();
		List<LinkSystem> outters = new ArrayList<LinkSystem>();
		List<LinkSystem> commons = new ArrayList<LinkSystem>();
		Map<Long, List<LinkSystem>> customMap = new HashMap<Long, List<LinkSystem>>();
		
		List<LinkSystem> all = this.findOutLinkByUserId(CurrentUser.get().getId());
		for(LinkSystem ls : all){
			if (ls.getLinkCategoryId() == Constants.LINK_IN) {
				inners.add(ls);
			} else if (ls.getLinkCategoryId() == Constants.LINK_OUT) {
				outters.add(ls);
			} else if (ls.getLinkCategoryId() == Constants.LINK_COMMON) {
				commons.add(ls);
			} else {
				// 自定义
				List<LinkSystem> lss = customMap.get(ls.getLinkCategoryId());
				if(lss == null){
					lss = new ArrayList<LinkSystem>();
					lss.add(ls);
					customMap.put(ls.getLinkCategoryId(), lss);
				}else{
					lss.add(ls);
				}
			}
		}
		ret.add(inners);
		ret.add(outters);
		ret.add(commons);
		if(customMap.size() > 0){
			Set<Long> set = customMap.keySet();
			for(Long categoryId : set){
				ret.add(customMap.get(categoryId));
			}
		}
		return ret;
	}

	//拼接成最后能访问的URL串
	public String getFinalUrlBySystemId(long linkSystemId,long userId) {
		LinkSystem link=linkSystemDao.get(linkSystemId);	//获取一个链接对象 
		Set linkOption = link.getLinkOption();				//获取高级选项
		if(linkOption == null || linkOption.isEmpty()){
			String temp=link.getUrl();
			if(temp.indexOf("http://") == -1){
				return "http://"+temp;
			}
			return link.getUrl();
		}
		String theUrl="http://";
		if(link.getUrl().indexOf(theUrl) == -1){
			theUrl+=link.getUrl();
		}else{
			theUrl=link.getUrl();
		}
		
		theUrl+="?";
		Iterator it=linkOption.iterator();
		while(it.hasNext()){
			LinkOption option=(LinkOption)it.next();
			theUrl+=option.getParamSign();
			theUrl+="=";
			if(option.getParamValue() != null && !option.getParamValue().trim().equals("")){
				theUrl+=option.getParamValue();
			}else{
				LinkOptionValue optionValue=linkOptionValueDao.findOptionValues(userId, option.getId());
				if(optionValue == null ){
					theUrl+="";
				}else{
					theUrl+=optionValue.getValue();
				}
			}
			
			if(it.hasNext()){
				theUrl+="&";
			}
		}
		return theUrl;
	}

	public List<LinkSystem> getLinkSystemByCategoryId(long categoryId) {
		
		return linkSystemDao.getLinkSystems(categoryId);
	}

	//添加一个类别
	public long addCategory(String name) throws Exception{

		User user=CurrentUser.get();
		LinkCategory category=new LinkCategory();
		category.setIdIfNew();
		category.setCreateTime(new java.sql.Timestamp(new Date().getTime()));
		category.setCreateUserId(user.getId());
		category.setDescription(null);
		category.setIsSystem((byte)0);
		category.setLastUpdate(new java.sql.Timestamp(new Date().getTime()));
		category.setLastUserId(user.getId());
		category.setName(name);
		category.setOrderNum(linkCategoryDao.getMaxOrderNumber());
		linkCategoryDao.save(category);
		return category.getId();
	}

	public void deleteCategory(String theIds) {
		linkCategoryDao.deleteCategorys(theIds);
		
	}

	public LinkCategory getCategoryById(long categoryId) {
		
		return linkCategoryDao.get(categoryId);
	}

	public List<LinkAcl> getLinkAclByCategoryId(long categoryId) {
		
		return linkAclDao.findBy("linkCategoryId", categoryId);
	}
	//更新一个类别
	public void updateCategory(LinkCategory category) {
		linkCategoryDao.update(category);
	}

	public void validateCategory(String name,long id)throws Exception {
		List<LinkCategory> list=linkCategoryDao.findBy("name", name);
		if(list !=null && list.isEmpty()==false){
			for(int i=0;i<list.size();i++){
				LinkCategory category=list.get(i);
				if(category.getId() != id){
					throw new Exception("LinkLang.link_exception_category_name");
				}
			}
		}
	}

	
	/**
	 * 验证url有效性
	 */
	public boolean isValidUrl(String urlString){
		/*-------------------- 格式有效性測試 ----------------------*/
	    UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);

	    if (urlValidator.isValid(urlString)) {
	       return true;
	    } else {
	       return false;
	    }
	    /*-------------------- 格式有效性測試 ----------------------*/
		
		/*-------------------- 業務有效性測試 ------對有參數形式無效，暫放棄所有業務有效性測試----------------*/
//		try {
//			URL url = new URL(urlString);
//			URLConnection conn = url.openConnection();
////			Permission permission = conn.getPermission();
////			if(permission == null)
//				try {
//					conn.connect();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
//			
//				
////			Map hfs = conn.getHeaderFields();
////			if(hfs == null || hfs.size() == 0)
////				return false;
//			
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
		/*-------------------- 業務有效性測試 ----------------------*/

	}
	
	/**
	 * 判断是否有同名关联系统
	 */
	public boolean hasSameNameCategory(String name, Long categoryId){
		if(categoryId.longValue() == 0L){
			return this.hasSameNameCategoryOfNew(name);
		}else{
			LinkCategory lc = linkCategoryDao.get(categoryId);
			if(lc.getName().equals(Constants.LINK_CATEGORY_COMMON_KEY)
					|| lc.getName().equals(Constants.LINK_CATEGORY_IN_KEY)
					|| lc.getName().equals(Constants.LINK_CATEGORY_OUT_KEY)
					|| lc.getName().equals(name))
				return false;
			else{
				return this.hasSameNameCategoryOfNew(name);
			}
			
		}
		
	}
	
	private boolean hasSameNameCategoryOfNew(String name){
		if(Constants.LINK_CATEGORY_COMMON_KEY.equals(name) ||
				Constants.LINK_CATEGORY_IN_KEY.equals(name)
				|| Constants.LINK_CATEGORY_OUT_KEY.equals(name))
			return true;
		else{
			List<LinkCategory> list=linkCategoryDao.getLinkCategorys(name);
			if(list != null && list.isEmpty()==false){			
				return true;
			}else{
				String common = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, 
						Constants.LINK_CATEGORY_COMMON_KEY);
				if(common != null)
					if(common.equals(name))
						return true;
				
				String out = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, 
						Constants.LINK_CATEGORY_OUT_KEY);
				if(out != null)
					if(out.equals(name))
						return true;
				
				String in = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, 
						Constants.LINK_CATEGORY_IN_KEY);
				if(in != null)
					if(in.equals(name))
						return true;
				
				
				return false;
			}
		}
	}
	
	/**
	 * 是否有同名关联系统
	 * systemId: 新建时直接传 0
	 */
	public boolean hasSameNameSystem(String name, Long categoryId, Long systemId){
		List list=linkSystemDao.findLinkSystem(name, categoryId);
		if(list != null && list.isEmpty()==false){
			if(systemId.longValue() == 0L)
				return true;
			
			for(int i=0;i<list.size();i++){
				LinkSystem theLink=(LinkSystem)list.get(i);
				if(theLink.getId().longValue() != systemId.longValue()){
					return true;
				}
			}
			return false;
		}else
			return false;
	}
	
	public boolean hasOption(String systemId){
		boolean flag = false ;
		if(Strings.isNotBlank(systemId)){
			List<LinkOption> list = linkOptionDao.getLinkOptionBySystemId(Long.valueOf(systemId)) ;
			if(list != null && list.size()>0){
				flag = true ;
			}
		}
		return flag ;
	}
	public void  delLinkAcl(LinkCategory category){
		if(category == null){
			return ;
		}
		String hql = "delete from LinkAcl linkAcl where linkAcl.linkCategoryId =:categoryId" ;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("categoryId", category.getId()) ;
		linkAclDao.bulkUpdate(hql,map) ;
	}
	
	//更新个人关联系统排序
	public void updateLinkOrder(String[] linkIds,Long userId) throws Exception {
		if(linkIds==null||linkIds.length==0) {
			return;
		}
		int i = 0;
		long linkSystemId;
		long linkCategoryId;
		for(String linkId : linkIds) {
			i++;
			linkSystemId = Long.parseLong(linkId);
			LinkCategory linkCategory = getLinkCategoryBylinkId(linkSystemId);
			linkCategoryId = linkCategory.getId();
			linkSystemDao.updateUserLinkSort(linkCategoryId, linkSystemId, userId, i);
		}
	}

    @Override
    public List<WebLinkOptionValueImportResultVO> importLinkOptinValue(String repeat, long linkSystemId, List<List<String>> strList) {
        int caAccountListSize = 0;
        if(strList != null) caAccountListSize = strList.size();
        List<WebLinkOptionValueImportResultVO> webLinkOptionValueImportResultVOList = new ArrayList<WebLinkOptionValueImportResultVO>();
        if(caAccountListSize == 0){
            return webLinkOptionValueImportResultVOList;
        }
        //导出excel文件的国际化
        String paramNameLabel = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.import.paramName.label");
        String loginNameLabel = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.import.loginName.label");
        String failResult = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.import.result.fail");
        String overcastResult = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.import.result.overcast");
        String overleapResult = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.import.result.overleap");
        String repeatItem = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.import.repeatitem");
        String successResult = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.import.result.success");
        String cannotbenull = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.prompt.cannotbenull");
        String loginNameNullResult = failResult + ","+ loginNameLabel + cannotbenull;
        Map<String, LinkOptionValue> linkOptionValueMapForAdd = new LinkedHashMap<String, LinkOptionValue>();
        Map<String, LinkOptionValue> linkOptionValueMapForUpdate = new LinkedHashMap<String, LinkOptionValue>();
        Map<String, V3xOrgMember> memberCache = new HashMap<String, V3xOrgMember>();
        Map<String, LinkOption> linkOptionCache = new HashMap<String, LinkOption>();
        Map<String, LinkOptionValue> linkOptionValueCache = new HashMap<String, LinkOptionValue>();
        List<String> tableHead = strList.get(0);
        String[] paramNames = new String[tableHead.size() - 2];
        for(int i = 0; i <= paramNames.length - 1; i++){
            paramNames[i] = tableHead.get(i + 1);
        }
        boolean success;
        for(int i = 1; i < caAccountListSize; i++){
        	success = true;
            List<String> record = strList.get(i);
            int lineNum = i + 2;
            WebLinkOptionValueImportResultVO webLinkOptionValueImportResultVO1 = new WebLinkOptionValueImportResultVO();
            webLinkOptionValueImportResultVO1.setLineNum(lineNum);
            String loginName = record.get(0);
            if(loginName == null || loginName.trim().length() == 0){
            	success = false;
                webLinkOptionValueImportResultVO1.setResult(loginNameNullResult);
                webLinkOptionValueImportResultVOList.add(webLinkOptionValueImportResultVO1);
                continue;
            }
            String[] paramValues = new String[paramNames.length];
            for(int j = 0; j < paramValues.length; j++){
                WebLinkOptionValueImportResultVO webLinkOptionValueImportResultVO = new WebLinkOptionValueImportResultVO();
                webLinkOptionValueImportResultVO.setLineNum(lineNum);
                paramValues[j] = record.get(j + 1);
                if(paramValues[j] == null || paramValues[j].trim().length() == 0){
                	success = false;
                    webLinkOptionValueImportResultVO.setResult(failResult + ","+ paramNameLabel + ":" + paramNames[j] + cannotbenull);
                    webLinkOptionValueImportResultVOList.add(webLinkOptionValueImportResultVO);
                    continue;
                } else {
                    V3xOrgMember member = memberCache.get(loginName);
                    try {
                        if(member == null){
                            member = orgManager.getMemberByLoginName(loginName, true);
                            memberCache.put(loginName, member);
                        }
                    } catch(BusinessException e) {
                    	success = false;
                        logger.error("error when importLinkOptinValue, cause by " + e);
                        webLinkOptionValueImportResultVO.setResult("添加失败，通过登录名" + loginName + "找不到user");
                        webLinkOptionValueImportResultVOList.add(webLinkOptionValueImportResultVO);
                        continue;
                    }
                    if(member == null){
                    	success = false;
                        logger.error("error when importLinkOptinValue, can not find V3xOrgMember by loginName:" + loginName);
                        webLinkOptionValueImportResultVO.setResult("添加失败，通过登录名" + loginName + "找不到user");
                        webLinkOptionValueImportResultVOList.add(webLinkOptionValueImportResultVO);
                        continue;
                    }
                    String userId = member.getUser().getId();
                    long userId_long = 0;
                    try {
                        userId_long = Long.parseLong(userId);
                    } catch (NumberFormatException nfe){
                    	success = false;
                        logger.error("error when importLinkOptinValue, can not tranform userId to long, param:" + userId);
                        webLinkOptionValueImportResultVO.setResult("添加失败，不能将userId转化为长整型");
                        webLinkOptionValueImportResultVOList.add(webLinkOptionValueImportResultVO);
                        continue;
                    }
                    LinkOption existLinkOption = linkOptionCache.get(linkSystemId + paramNames[j]);
                    if(existLinkOption == null){
                        existLinkOption = linkOptionDao.findLinkOptionBy(linkSystemId, paramNames[j]);
                        linkOptionCache.put(linkSystemId + paramNames[j], existLinkOption);
                    }
                    if(existLinkOption == null){
                    	success = false;
                        logger.error("error when importLinkOptinValue, 关联系统参数为空, 参数:" + paramNames[j]);
                        webLinkOptionValueImportResultVO.setResult("关联系统参数为空, 参数:" + paramNames[j]);
                        webLinkOptionValueImportResultVOList.add(webLinkOptionValueImportResultVO);
                        continue;
                    } else {
                        LinkOptionValue existLinkOptionValue = linkOptionValueCache.get(linkSystemId + userId_long +  paramNames[j]);
                        if(existLinkOptionValue == null){
                            existLinkOptionValue = linkOptionValueDao.findOptionValues(linkSystemId, paramNames[j], userId_long);
                            linkOptionValueCache.put(linkSystemId + userId_long +  paramNames[j], existLinkOptionValue);
                        }
                        if(existLinkOptionValue == null){
                            LinkOptionValue newLinkOptionValue = new LinkOptionValue();
                            newLinkOptionValue.setNewId();
                            newLinkOptionValue.setLinkOptionId(existLinkOption.getId());
                            newLinkOptionValue.setUserId(userId_long);
                            newLinkOptionValue.setValue(Strings.isBlank(paramValues[j]) ? " " : LightWeightEncoder.encodeString(paramValues[j]));
                            if(linkOptionValueMapForAdd.get(userId + "" + existLinkOption.getId() + paramNames[j]) != null){
                                if(repeat.equals("1")){
                                    webLinkOptionValueImportResultVO.setResult(repeatItem + ":" + paramNames[j] + "," + overleapResult);
                                } else {
                                    webLinkOptionValueImportResultVO.setResult(repeatItem + ":" + paramNames[j] + "," + overcastResult);
                                    linkOptionValueMapForAdd.put(userId + "" + existLinkOption.getId() + paramNames[j], newLinkOptionValue);
                                }
                                success = false;
                                webLinkOptionValueImportResultVOList.add(webLinkOptionValueImportResultVO);
                            } else {
                                linkOptionValueMapForAdd.put(userId + "" + existLinkOption.getId() + paramNames[j], newLinkOptionValue);
                            }
                        } else {
                            if(repeat.equals("1")){
                                webLinkOptionValueImportResultVO.setResult(repeatItem + ":" + paramNames[j] + "," + overleapResult);
                            } else {
                                webLinkOptionValueImportResultVO.setResult(repeatItem + ":" + paramNames[j] + "," + overcastResult);
                                existLinkOptionValue.setValue(Strings.isBlank(paramValues[j]) ? " " : LightWeightEncoder.encodeString(paramValues[j]));
                                linkOptionValueMapForUpdate.put(userId + "" + existLinkOption.getId() + paramNames[j], existLinkOptionValue);
                            }
                            success = false;
                            webLinkOptionValueImportResultVOList.add(webLinkOptionValueImportResultVO);
                        }
                    }
                }
            }
            if (success) {
            	webLinkOptionValueImportResultVO1.setResult(successResult);
                webLinkOptionValueImportResultVOList.add(webLinkOptionValueImportResultVO1);
            }
        }
        if(!linkOptionValueMapForAdd.isEmpty()){
            linkOptionValueDao.savePatchAll(linkOptionValueMapForAdd.values());
        }
        if(!linkOptionValueMapForUpdate.isEmpty()){
            try {
                linkOptionValueDao.updatePatchAll(linkOptionValueMapForUpdate.values());
            } catch(BusinessException e) {
                logger.error("fail to update LinkOptionValue when importLinkOptionValue, caused by:" + e);
            }
        }
        return webLinkOptionValueImportResultVOList;
    }

    @Override
    public DataRecord exportLinkOptionTemplate(List<LinkOption> linkOptionList) {
        if(linkOptionList == null || linkOptionList.size() == 0){
            return null;
        }
        DataRecord dataRecord = new DataRecord();
        //导出excel文件的国际化
        String exportTitleLabel = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.export.linkOptionList.title");
        String loginNameLabel = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.export.loginName");
        String[] columnName = new String[linkOptionList.size() + 1];
        columnName[0] = loginNameLabel;
        if (null != linkOptionList && linkOptionList.size() > 0) {
            for (int i = 0; i < linkOptionList.size(); i++) {
                LinkOption linkOption = linkOptionList.get(i);
                columnName[i + 1] = linkOption.getParamName();
            }
        }
        dataRecord.setColumnName(columnName);
        dataRecord.setTitle(exportTitleLabel);
        dataRecord.setSheetName(exportTitleLabel);
        return dataRecord;
    }

    @Override
    public List<Object[]> getLinkOptionValueStatistics(List<LinkOption> linkOptionList) {
        List<Object[]> linkOptionValueStatistics = linkOptionValueDao.statisticsLinkOptionValue(linkOptionList);
        List<Object[]> statistics = new ArrayList<Object[]>();
        if(linkOptionValueStatistics == null || linkOptionValueStatistics.size() == 0){
            return statistics;
        }
        int size = linkOptionValueStatistics.size();
        for(int i = 0; i < size; i++){
            Object[] objs = linkOptionValueStatistics.get(i);
            Long userId = (Long)objs[0];
            Object[] objStatistics = new Object[objs.length + 2];
            objStatistics[0] = userId;
            try {
                V3xOrgMember v3xOrgMember = orgManager.getMemberById(userId);
                objStatistics[1] = v3xOrgMember.getUser().getName();
                objStatistics[2] = v3xOrgMember.getLoginName();
            } catch(BusinessException e) {
                logger.error("通过memberId" + userId + "找不到v3xOrgMember");
                continue;
            }
            for(int j = 1; j < objs.length; j++){
                objStatistics[2 + j] = objs[j];
            }
            statistics.add(objStatistics);
        }
        return statistics;
    }

    @Override
    public void deleteParamValues(List<Long> linkOptionIds, List<Long> userIds) {
        linkOptionValueDao.deleteParamValues(linkOptionIds, userIds);
    }

    @Override
    public void addLinkSpace(List<LinkSpace> linkSpaceList) {
        if(linkSpaceList == null || linkSpaceList.size() == 0){
            return;
        }
        linkSpaceDao.savePatchAll(linkSpaceList);
    }

    @Override
    public void addLinkSpaceAcl(List<LinkSpaceAcl> linkSpaceAclList) {
        if(linkSpaceAclList == null || linkSpaceAclList.size() == 0){
            return;
        }
        linkSpaceAclDao.savePatchAll(linkSpaceAclList);
    }

    @Override
    public LinkSpace getLinkSpaceById(long linkSpaceId) {
        return linkSpaceDao.get(linkSpaceId);
    }

    @Override
    public List<LinkSpace> findLinkSpacesCanAccess(Long userId) {
        return this.linkSpaceDao.getLinkSpacesCanAccess(com.seeyon.v3x.doc.util.Constants.getOrgIdsOfUser1(userId));
    }

    @Override
    public boolean canUseTheLinkSpace(Long userId, Long linkSpaceId) {
        return this.linkSpaceDao.canUseTheLinkSpace(com.seeyon.v3x.doc.util.Constants.getOrgIdsOfUser1(userId), linkSpaceId);
    }
}
