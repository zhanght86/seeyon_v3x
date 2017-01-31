package com.seeyon.v3x.blog.manager;

import java.util.List;

import com.seeyon.v3x.blog.domain.BlogArticle;
import com.seeyon.v3x.blog.domain.BlogFamily;
import com.seeyon.v3x.blog.domain.BlogFavorites;
import com.seeyon.v3x.blog.webmodel.BlogCountArticle;

public interface BlogFamilyManager {
	// 分类管理主界面
	public List<BlogArticle> listArticle(Long familyId,String condition,String textfield,String textfield1 ) throws Exception;

	// 分类收藏
	//public void assignBlogFavorites(String authType,Long familyId,List<BlogFavorites> list) throws Exception;
	
	// 取得分类授权信息
	public String getFamilyAuth(String authType,Long familyId) throws Exception;

	//发贴统计
	//public List<BlogCountArticle> countArticle(String countType,String departmentid,Long familyId) throws Exception;

	//删除贴子
	public void deleteArticle(Long blogArticleId) throws Exception;
	
	// 取得分类信息
	public BlogFamily getFamilyById(Long familyId) throws Exception;
	
	
	// 删除主题
	public void delArticle(String articleId) throws Exception;
	
	// 检查用户是否有发贴的权限
	public boolean validIssueAuth(Long familyId,Long userId );
	
	// 检查用户是否有回贴的权限
	public boolean validReplyAuth(Long userId );
	
	// 检查用户是否管理员
	public boolean validUserIsAdmin(Long userId );


}