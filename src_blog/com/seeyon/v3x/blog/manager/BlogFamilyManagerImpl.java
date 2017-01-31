package com.seeyon.v3x.blog.manager;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.blog.dao.BlogFavoritesDao;
import com.seeyon.v3x.blog.dao.BlogFamilyDao;
import com.seeyon.v3x.blog.dao.BlogEmployeeDao;
import com.seeyon.v3x.blog.domain.BlogArticle;
import com.seeyon.v3x.blog.domain.BlogFamily;
import com.seeyon.v3x.blog.domain.BlogFavorites;
import com.seeyon.v3x.blog.domain.BlogEmployee;
import com.seeyon.v3x.blog.webmodel.BlogCountArticle;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.controller.DocController;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;

public class BlogFamilyManagerImpl implements BlogFamilyManager {
	
	private BlogFamilyDao blogFamilyDao;
	private BlogFavoritesDao BlogFavoritesDao;
	private BlogEmployeeDao blogEmployeeDao;
	private static final Log log = LogFactory.getLog(BlogFamilyManagerImpl.class);

	private OrgManager orgManager;
	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public BlogFamilyDao getBlogFamilyDao() {
		return blogFamilyDao;
	}

	public void setBlogFamilyDao(BlogFamilyDao blogFamilyDao) {
		this.blogFamilyDao = blogFamilyDao;
	}

	public BlogFavoritesDao getBlogFamilyAuthDao() {
		return BlogFavoritesDao;
	}

	public void setBlogEmployeeDao(BlogEmployeeDao EmployeeDao) {
		this.blogEmployeeDao = EmployeeDao;
	}

	// 分类管理主界面
	public List<BlogArticle> listArticle(Long familyId,String condition,String textfield,String textfield1 ) throws Exception{
		return blogFamilyDao.listArticle(familyId,condition,textfield,textfield1);
	}

	// 分类授权
	public void assignFamilyAuth(String authType, Long familyId,
			List<BlogFamilyDao> list) throws Exception {
		 try {
			 //BlogFamilyDao.delFamilyAuth(authType, familyId);
			 //BlogFamilyDao.assignFamilyAuth(list);
		 } catch (Exception e) {
			log.error(e.getMessage(), e);
		 }
	}

	// 取得分类授权信息
	public String getFamilyAuth(String authType, Long familyId) throws Exception {
		//return blogFamilyDao.getFamilyAuth(authType, familyId);
		return"";
	}

	// 分类发帖统计
	//public List<BlogCountArticle> countArticle(String countType,
	//		String departmentid, Long familyId) throws Exception {
	//	return blogFamilyDao.countArticle(countType, departmentid, familyId);
	//}

	public void deleteArticle(Long blogArticleId) throws Exception {
	}

	public BlogFamily getFamilyById(Long familyId) throws Exception {
		return blogFamilyDao.getFamilyById(familyId);
	}

	// 删除主题
	public void delArticle(String articleId) throws Exception {
		blogFamilyDao.delArticle(articleId);
	}
	
	// 检查用户是否有发贴的权限
	public boolean validIssueAuth(Long familyId,Long userId ){
		//return blogFamilyDao.validIssueAuth(familyId, userId);
		return true;
	}
	
	// 检查用户是否有回复的权限
	public boolean validReplyAuth(Long userId ){
		//所有可以浏览文章的人都可以回复
		return true;
		//return blogFamilyAuthDao.validReplyAuth(familyId, userId);
	}
	
	// 检查用户是否管理员
	public boolean validUserIsAdmin(Long userId ){
		boolean isAdministrator = false;
		
		V3xOrgRole accountAdmin;
		try {
			accountAdmin = orgManager.getRoleByName("AccountAdmin");
			if(accountAdmin != null){
				User user = CurrentUser.get();
				isAdministrator = orgManager.isInDomain(user.getLoginAccount(), accountAdmin.getId(), user.getId());
			}
		} catch (BusinessException e) {
			log.error("blog通过orgManager判断当前用户是否具有单位管理权限", e);

		}		


	
		return isAdministrator;
	}
	

}
