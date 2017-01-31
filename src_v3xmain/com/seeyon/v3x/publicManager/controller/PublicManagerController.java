package com.seeyon.v3x.publicManager.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Strings;


public class PublicManagerController extends BaseController {
	
	private static final Log log = LogFactory.getLog(PublicManagerController.class);
	
    private InquiryManager inquiryManager;
    
    private BbsBoardManager bbsBoardManager;
		
	private BulDataManager bulDataManager;
	
	private NewsDataManager newsDataManager;
	
	private BulTypeManager bulTypeManager;
	
	private NewsTypeManager newsTypeManager;
	
	private SpaceManager spaceManager;

	public BulTypeManager getBulTypeManager() {
		return bulTypeManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	public NewsTypeManager getNewsTypeManager() {
		return newsTypeManager;
	}

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public BbsBoardManager getBbsBoardManager() {
		return bbsBoardManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public BulDataManager getBulDataManager() {
		return bulDataManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	public NewsDataManager getNewsDataManager() {
		return newsDataManager;
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public SpaceManager getSpaceManager() {
		return spaceManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		return null;
	}
	
	public ModelAndView initWindow(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView("v3xmain/publicManager/managerWindow");
		
		long memberId = CurrentUser.get().getId();
		String group = request.getParameter("group");
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		boolean groupFlag = (group != null && "true".equals(group));
		
		boolean bul = false;
		boolean news = false;
		boolean inq = false;
		boolean bbs = false;
		
		if(groupFlag){
	   		 bul = this.bulTypeManager.isGroupBulTypeManager(memberId);	   		 
	   		 if(!bul){
	   			 bul = this.bulTypeManager.isGroupBulTypeAuth(memberId);
	   		 }	
	 		news = newsTypeManager.isGroupNewsTypeManager(memberId);
	 		if(!news)
	 			news = newsTypeManager.isGroupNewsTypeAuth(memberId);
	 		
	 		inq = inquiryManager.hasManageAuthForGroupSpace();
	 		
			List<V3xBbsBoard> v3xBbsBoardList =  this.bbsBoardManager.getAllGroupBbsBoard();
			for(V3xBbsBoard board : v3xBbsBoardList){
				if(bbsBoardManager.validUserIsAdmin(board.getId(), memberId)){
					bbs = true;
					break;
				}
			}
		}else{
			if("custom".equalsIgnoreCase(spaceType)){
				boolean isSpaceManager = spaceManager.isManagerOfThisSpace(memberId, Long.parseLong(spaceId));
				if(isSpaceManager){
					bul = true;
					news = true;
					inq = true;
					bbs = true;
					modelAndView.addObject("custom", true);
					modelAndView.addObject("spaceType", 4);
					modelAndView.addObject("spaceId", spaceId);
				}
			}else if(Strings.isNotBlank(spaceId)){
				int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
				bul = bulDataManager.showManagerMenuOfCustomSpace(memberId, Long.parseLong(spaceId), spaceTypeInt);
				news = newsDataManager.showManagerMenuOfCustomSpace(memberId, Long.parseLong(spaceId), spaceTypeInt);
				inq = inquiryManager.showManagerMenuOfCustomSpace(memberId, Long.parseLong(spaceId), spaceTypeInt);
				List<V3xBbsBoard> v3xBbsBoardList =  this.bbsBoardManager.getAllCustomAccBbsBoard(Long.parseLong(spaceId), spaceTypeInt);
				for(V3xBbsBoard board : v3xBbsBoardList){
					if(bbsBoardManager.validUserIsAdmin(board.getId(), memberId)){
						bbs = true;
						break;
					}
				}
				modelAndView.addObject("spaceType", spaceTypeInt);
				modelAndView.addObject("spaceId", spaceId);
				modelAndView.addObject("customSpace", true);
			}else{
				// 公告
				bul = bulDataManager.showManagerMenuOfLoginAccount(memberId);
				// 新闻
				news = newsDataManager.showManagerMenuOfLoginAccount(memberId);
				// 调查
				try {
					//inq = inquiryManager.hasManageAuthForAccountSpace(memberId);
					//改用适用跨单位兼职办公情况下的判断方法 modified by Meng Yang at 2009-07-14
					inq = inquiryManager.hasManageAuthForAccountSpace(memberId, CurrentUser.get().getLoginAccount());
				} catch (Exception e) {
				}
				// BBS
				List<V3xBbsBoard> v3xBbsBoardList =  this.bbsBoardManager.getAllCorporationBbsBoard(CurrentUser.get().getLoginAccount());
				for(V3xBbsBoard board : v3xBbsBoardList){
					if(bbsBoardManager.validUserIsAdmin(board.getId(),CurrentUser.get().getId())){
						bbs = true;
						break;
					}
				}
			}
		}
		modelAndView.addObject("bulMenu", bul);
		modelAndView.addObject("newsMenu", news);
		modelAndView.addObject("inqMenu", inq);
		modelAndView.addObject("bbsMenu", bbs);
		modelAndView.addObject("groupFlag", groupFlag);
		return modelAndView;
	}
	
	/**
	 * 判断空间管理员和公共信息管理员
	 */
	public Map<String, Boolean> isSpaceAndPublicInfoManager(String spaceType, Long spaceId) throws Exception {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		User user = CurrentUser.get();
		Long userId = user.getId();
		Long accountId = user.getLoginAccount();
		
		if ("custom".equalsIgnoreCase(spaceType)) {
			// 自定义团队空间：如果不存在公共信息板块，需要建立
			this.createPublicInfoType(spaceId);
		}

		boolean isSpaceManager = spaceManager.isManagerOfThisSpace(userId, spaceId);
		result.put("isSpaceManager", isSpaceManager);

		boolean isPublicInfoManager = false;
		if ("public_custom".equalsIgnoreCase(spaceType) || "public_custom_group".equalsIgnoreCase(spaceType)) {// 是否是自定义单位|集团空间公共信息管理员
			int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
			isPublicInfoManager = bulDataManager.showManagerMenuOfCustomSpace(userId, spaceId, spaceTypeInt) || newsDataManager.showManagerMenuOfCustomSpace(userId, spaceId, spaceTypeInt) || inquiryManager.showManagerMenuOfCustomSpace(userId, spaceId, spaceTypeInt);
			if (!isPublicInfoManager) {
				List<V3xBbsBoard> v3xBbsBoardList = this.bbsBoardManager.getAllCustomAccBbsBoard(spaceId, spaceTypeInt);
				for (V3xBbsBoard board : v3xBbsBoardList) {
					if (bbsBoardManager.validUserIsAdmin(board.getId(), userId)) {
						isPublicInfoManager = true;
						break;
					}
				}
			}
		} else if ("corporation".equalsIgnoreCase(spaceType)) {// 是否是单位公共信息管理员
			isPublicInfoManager = bulDataManager.showManagerMenuOfLoginAccount(userId) || newsDataManager.showManagerMenuOfLoginAccount(userId) || inquiryManager.hasManageAuthForAccountSpace(userId);
			if (!isPublicInfoManager) {
				List<V3xBbsBoard> v3xBbsBoardList = bbsBoardManager.getAllCorporationBbsBoard(accountId);
				for (V3xBbsBoard board : v3xBbsBoardList) {
					if (bbsBoardManager.validUserIsAdmin(board.getId(), userId)) {
						isPublicInfoManager = true;
						break;
					}
				}
			}
		} else if ("group".equalsIgnoreCase(spaceType)) {// 是否是集团公共信息管理员
			isPublicInfoManager = bulTypeManager.isGroupBulTypeManager(userId) || newsTypeManager.isGroupNewsTypeManager(userId) || newsTypeManager.isGroupNewsTypeAuth(userId) || inquiryManager.hasManageAuthForGroupSpace();

			if (!isPublicInfoManager) {
				List<V3xBbsBoard> v3xBbsBoardList = bbsBoardManager.getAllGroupBbsBoard();
				for (V3xBbsBoard board : v3xBbsBoardList) {
					if (bbsBoardManager.validUserIsAdmin(board.getId(), userId)) {
						isPublicInfoManager = true;
						break;
					}
				}
			}
		}
		result.put("isPublicInfoManager", isPublicInfoManager);

		return result;
	}
	
	/**
	 * 创建自定义团队空间对应公共信息板块
	 */
	private void createPublicInfoType(Long spaceId) throws Exception {
		SpaceFix fix = spaceManager.getSpace(spaceId);

		BulType bulType = bulTypeManager.getByDeptId(spaceId);
		if (bulType == null) {
			bulTypeManager.saveCustomBulType(spaceId, fix.getSpaceName());
		}

		NewsType newsType = newsTypeManager.getById(spaceId);
		if (newsType == null) {
			newsTypeManager.saveCustomNewsType(spaceId, fix.getEntityId(), fix.getSpaceName());
		}

		InquirySurveytype surveytype = inquiryManager.getSurveyTypeById(spaceId);
		if (surveytype == null) {
			inquiryManager.saveCustomInquirySurveytype(spaceId, fix.getSpaceName());
		}

		V3xBbsBoard board = bbsBoardManager.getBoardById(spaceId);
		if (board == null) {
			bbsBoardManager.createDepartmentBbsBoard(spaceId, fix.getEntityId(), fix.getSpaceName());
		}
	}

}