/**
 * 
 */
package com.seeyon.v3x.bbs.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.rss.domain.RssCategory;
import com.seeyon.v3x.common.rss.manager.RssManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.webmdoel.SurveyBasicCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyTypeCompose;
import com.seeyon.v3x.link.domain.LinkCategory;
import com.seeyon.v3x.link.manager.OuterlinkManager;
import com.seeyon.v3x.main.section.definition.SectionDefinitionManager;
import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionDefine;
import com.seeyon.v3x.plugin.deeSection.manager.DeeSectionManager;
import com.seeyon.v3x.util.CommonTools;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class ValidateBoardNameManager {
	private static Log log = LogFactory.getLog(ValidateBoardNameManager.class);
		
	private OrgManager orgManager;
    
	private BbsBoardManager bbsBoardManager;
	
	private InquiryManager inquiryManager;
	
	private BulTypeManager bulTypeManager;
    
    private BulDataManager bulDataManager;
	
	private NewsTypeManager newsTypeManager;

    private NewsDataManager newsDataManager;
     
	private OuterlinkManager outerlinkManager;
	
	private RssManager rssManager;
	
	private SectionDefinitionManager sectionDefinitionManager;
	
	private DeeSectionManager deeSectionManager;

	public void setDeeSectionManager(DeeSectionManager deeSectionManager) {
		this.deeSectionManager = deeSectionManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}
	
	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}
	
	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public void setOuterlinkManager(OuterlinkManager outerlinkManager) {
		this.outerlinkManager = outerlinkManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
        this.bulDataManager = bulDataManager;
    }
    
    public void setNewsDataManager(NewsDataManager newsDataManager) {
        this.newsDataManager = newsDataManager;
    }
    
    public void setSectionDefinitionManager(
			SectionDefinitionManager sectionDefinitionManager) {
		this.sectionDefinitionManager = sectionDefinitionManager;
	}
    
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    

    /**
	 * 验证版块标题是否存在
	 */
	public String validateBoardName(String boardName , Long boardId)throws Exception{
		User user = CurrentUser.get();
		List<V3xBbsBoard> boards = bbsBoardManager.getAllCorporationBbsBoard(user.getLoginAccount());
		if(boardId!=null){//修改的时候有ID传过来
			for (V3xBbsBoard board : boards) {
				if(board.getName().equals(boardName) && board.getId().longValue()!=boardId.longValue() ){
					return Boolean.TRUE.toString();
				}
			}
		}else{
			for (V3xBbsBoard board : boards) {//新建的时候没有ID传来
				if(board.getName().equals(boardName)){
					return Boolean.TRUE.toString();
				}
			}
		}
		
		return Boolean.FALSE.toString();
	}
	/**
	 * 验证集团版块标题是否存在
	 */
	public String validateGroupBoardName(String boardName , Long boardId)throws Exception{
		
		List<V3xBbsBoard> boards = bbsBoardManager.getAllGroupBbsBoard();
		if(boardId!=null){//修改的时候有ID传过来
			for (V3xBbsBoard board : boards) {
				if(board.getName().equals(boardName) && board.getId().longValue()!=boardId.longValue() ){
					return Boolean.TRUE.toString();
				}
			}
		}else{//新建的时候没有ID传来
			for (V3xBbsBoard board : boards) {
				if(board.getName().equals(boardName)){
					return Boolean.TRUE.toString();
				}
			}
		}
		
		return Boolean.FALSE.toString();
	}
	
	/**
	 * 验证讨论文章标题是否存在
	 */
	public String validateArticleName(String articleName)throws Exception{
		 return Boolean.FALSE.toString();
	}
	
	
	/**
	 * 验证调查状态
	 */
	public Integer validateInquiryExist(Long basicid)throws Exception{
		SurveyBasicCompose sbcompose = inquiryManager.getInquiryBasicByBasicID(basicid);
		if(sbcompose!=null){
			return sbcompose.getInquirySurveybasic().getCensor();
		}else{
			return 0;
		}
	}
	
	
	/************************************* 得到板块信息 用于栏目选择******************************************
	 * 
	 * @return string[id, name]
	 */
	public List<String[]> getBbsBoard(){
		User user = CurrentUser.get();
		List<String[]> result = new ArrayList<String[]>();
		try {
			List<V3xBbsBoard> boards = bbsBoardManager.getAllCorporationBbsBoard(user.getLoginAccount());
			for (V3xBbsBoard board : boards) {
				result.add(new String[]{String.valueOf(board.getId()), board.getName()});
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		
		return result;
	}
	
	public List<String[]> getCustomBbsBoard(String spaceType, String spaceId){
		List<String[]> result = new ArrayList<String[]>();
		try {
			List<V3xBbsBoard> boards = bbsBoardManager.getAllCustomAccBbsBoard(Long.parseLong(spaceId), "public_custom".equals(spaceType) ? 5 : 6);
			for (V3xBbsBoard board : boards) {
				result.add(new String[]{String.valueOf(board.getId()), board.getName()});
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		return result;
	}
	
	public List<String[]> getInquiryBoard(){
		List<String[]> result = new ArrayList<String[]>();
		try {
			User currentUser = CurrentUser.get();
			V3xOrgAccount curAccount = orgManager.getAccountById(currentUser.getLoginAccount());
			if(curAccount.getIsRoot()){
				return result;
			}
			List<SurveyTypeCompose> b = null;
			b = inquiryManager.getInquiryList(currentUser);// 获取调查类型列表
			for (SurveyTypeCompose board : b) {
				InquirySurveytype t = board.getInquirySurveytype();
				result.add(new String[]{String.valueOf(t.getId()), t.getTypeName()});
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		
		return result;
	}
	
	public List<String[]> getCustomInquiryBoard(String spaceType, String spaceId){
		List<String[]> result = new ArrayList<String[]>();
		try {
			List<SurveyTypeCompose> b = null;
			b = inquiryManager.getCustomAccInquiryList(Long.parseLong(spaceId), spaceType);// 获取调查类型列表
			for (SurveyTypeCompose board : b) {
				InquirySurveytype t = board.getInquirySurveytype();
				result.add(new String[]{String.valueOf(t.getId()), t.getTypeName()});
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		return result;
	}
	
	public List<String[]> getGroupInquiryBoard(){
		List<String[]> result = new ArrayList<String[]>();
		try {
			List<InquirySurveytype> b = null;
			b = inquiryManager.getGroupSurveyTypeList();// 获取集团调查类型
			for (InquirySurveytype board : b) {
				result.add(new String[]{String.valueOf(board.getId()), board.getTypeName()});
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		
		return result;
	}
	
	public List<String[]> getBulBoard(){
		List<String[]> result = new ArrayList<String[]>();
		try {
			List<BulType> b = this.bulTypeManager.boardFindAll();
			for (BulType board : b) {
				result.add(new String[]{String.valueOf(board.getId()), board.getTypeName()});
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		
		return result;
	}
	
	public List<String[]> getCustomBulBoard(String spaceType, String spaceId){
		List<String[]> result = new ArrayList<String[]>();
		try {
			List<BulType> b = this.bulTypeManager.customAccBoardAllBySpaceId(Long.parseLong(spaceId), "public_custom".equals(spaceType) ? 5 : 6);
			for (BulType board : b) {
				result.add(new String[]{String.valueOf(board.getId()), board.getTypeName()});
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		return result;
	}
	
	public List<String[]> getNewBoard(){
		List<String[]> result = new ArrayList<String[]>();
		try {
			List<NewsType> b = this.newsTypeManager.findAll();
			for (NewsType board : b) {
				result.add(new String[]{String.valueOf(board.getId()), board.getTypeName()});
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return result;
	}
	
	public List<String[]> getCustomNewBoard(String spaceType, String spaceId){
		List<String[]> result = new ArrayList<String[]>();
		try {
			List<NewsType> b = this.newsTypeManager.findAllOfCustom(Long.parseLong(spaceId), "public_custom".equals(spaceType) ? "publicCustom" : "publicCustomGroup");
			for (NewsType board : b) {
				result.add(new String[]{String.valueOf(board.getId()), board.getTypeName()});
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return result;
	}
	
	public List<String[]> getLinkCategory(){
		List<String[]> result = new ArrayList<String[]>();
		
		List<LinkCategory> cs = this.outerlinkManager.getAllLinkCategory();
		ResourceBundle rb = ResourceBundle.getBundle("com.seeyon.v3x.link.i18n.LinkResource", CurrentUser.get().getLocale());
		for (LinkCategory category : cs) {
			String name = ResourceBundleUtil.getString(rb, category.getName());
			result.add(new String[]{String.valueOf(category.getId()), name});
		}
		
		return result;
	}
	
    public List<String[]> getGroupBulBoard(){
        List<String[]> result = new ArrayList<String[]>();
        try {
            List<BulType> b = bulDataManager.groupAllBoardList();
            for (BulType board : b) {
                result.add(new String[]{String.valueOf(board.getId()), board.getTypeName()});
            }
        }
        catch (Exception e) {
            log.error("", e);
        }
        
        
        return result;
    }
    
    
    public List<String[]> getGroupNewsBoard(){
        List<String[]> result = new ArrayList<String[]>();
        try {
            List<NewsType> b = newsDataManager.getGroupAllTypeList();
            for (NewsType board : b) {
                result.add(new String[]{String.valueOf(board.getId()), board.getTypeName()});
            }
        }
        catch (Exception e) {
            log.error("", e);
        }
        
        return result;
    }
    
    public List<String[]> getGroupBbsBoard(){
        List<String[]> result = new ArrayList<String[]>();
        try {
            List<V3xBbsBoard> groupBbsBoards = bbsBoardManager.getAllGroupBbsBoard();
            for (V3xBbsBoard board : groupBbsBoards) {
                result.add(new String[]{String.valueOf(board.getId()), board.getName()});
            }
        }
        catch (Exception e) {
            log.error("", e);
        }
        
        return result;
    }
    
    public List<String[]> getSectionDefinitionOfSSOWebcontent(String spaceType){
    	return this.getSectionDefinition(SectionDefinition.Type.SSOWebcontent, spaceType);
    }
    
    public List<String[]> getSectionDefinitionOfSSOIframe(String spaceType){
    	return this.getSectionDefinition(SectionDefinition.Type.SSOIframe, spaceType);
    }
    
    public List<String[]> getSectionDefinitionOfIframe(String spaceType){
    	return this.getSectionDefinition(SectionDefinition.Type.Iframe, spaceType);
    }
    
    public List<String[]> getDeeSection(){
    	User user = CurrentUser.get();
    	List<String[]> result = new ArrayList<String[]>();
		try{
			List<DeeSectionDefine> deeSectionDefines = null;
			if(user.isAdmin()){
				deeSectionDefines = deeSectionManager.findAllDeeSection();
			}else{
				List<Long> domainIds = CommonTools.getUserDomainIds(user.getId(), orgManager);
				deeSectionDefines = deeSectionManager.getDeeSectionIdBySecurity(domainIds);
			}
			if(CollectionUtils.isNotEmpty(deeSectionDefines)){
				for(DeeSectionDefine deeSectionDefine : deeSectionDefines){
					result.add(new String[]{String.valueOf(deeSectionDefine.getId()),deeSectionDefine.getDeeSectionName()}) ;
				}
			}
		}catch(Exception e){
			log.error("获取DEE栏目出错", e) ;
		}
    	return result ;
    }
    
    private List<String[]> getSectionDefinition(SectionDefinition.Type t, String spaceType){
    	User user = CurrentUser.get();
    	
    	List<String[]> result = new ArrayList<String[]>();
        try {
			List<SectionDefinition> ds = null;
			if (user.isAdmin()) {
				ds = sectionDefinitionManager.getSectionDefinitionByType(t.ordinal());
			} else {
				List<Long> domainIds = CommonTools.getUserDomainIds(user.getId(), orgManager);
				ds = sectionDefinitionManager.getCurrentAccess(domainIds, t.ordinal());
			}
        	
        	if(ds!=null && ds.size()>0) {
	            for (SectionDefinition d : ds) {
	                result.add(new String[]{String.valueOf(d.getId()), d.getName()});
	            }
        	}
        }
        catch (Exception e) {
            log.error("", e);
        }
    	
    	return result;
    }
    /**
     * 得到Rss订阅板块名称的信息
     * @return
     */
    public List<String[]>  getRssMessages(){
    	List<String[]> result = new ArrayList<String[]>();
		User user = CurrentUser.get();
		try{
			List<RssCategory> categories = rssManager.getMyCategories(user.getId());
			for(RssCategory rssCategory : categories){
				result.add(new String[]{String.valueOf(rssCategory.getId()),rssCategory.getName()}) ;
			}
			
		}catch(Exception e){
			log.error("", e) ;
		}
		
         
    	return result ;
    }

	public void setRssManager(RssManager rssManager) {
		this.rssManager = rssManager;
	}
    
}