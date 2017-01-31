package com.seeyon.v3x.blog.manager;

/**
 * @author xiaoqiuhe
 */

import java.util.List;

import com.seeyon.v3x.blog.domain.BlogEmployee;
import com.seeyon.v3x.blog.domain.BlogFamily;
import com.seeyon.v3x.blog.domain.BlogFavorites;
import com.seeyon.v3x.blog.domain.BlogShare;
import com.seeyon.v3x.blog.domain.BlogAttention;
import com.seeyon.v3x.blog.webmodel.AttentionModel;


public interface BlogManager {
	
	/**
	 * 查询分类信息(需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	public List<BlogFamily> listFamily(String type) throws Exception;

	/**
	 * 查询分类信息(不需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	public List<BlogFamily> listFamily2(String type) throws Exception ;
	/**
	 * 查询他人分类信息(需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	public List<BlogFamily> listFamilyOther(String type,Long userId) throws Exception;

	/**
	 * 查询他人分类信息(不需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	public List<BlogFamily> listFamilyOther2(String type,Long userId) throws Exception ;
	
	/**
	 * 查询某分类信息
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	public BlogFamily getSingleFamily(Long id) throws Exception;
	/**
	 * 新增分类信息
	 * 
	 * @param blogFamily
	 *            分类信息
	 * @throws Exception
	 */
	public void createFamily(BlogFamily blogFamily) throws Exception;

	/**
	 * 变更分类信息
	 * 
	 * @param blogFamily
	 *            分类信息
	 * @throws Exception
	 */
	public void modifyFamily(BlogFamily blogFamily) throws Exception;

	/**
	 * 删除分类信息
	 * 
	 * @param blogFamilyId
	 *            分类编号
	 * @throws Exception
	 */
	public void deleteFamily(Long blogFamilyId) throws Exception;
	/**
	 * 得到默认分类ID
	 * 
	 * @param blogFamilyId
	 *            分类编号
	 * @throws Exception
	 */
	public Long getDefaultFamilyID(Long employeeId,String type) throws Exception;
	/**
	 * 得到私有分类ID
	 * 
	 * @param blogFamilyId
	 *            分类编号
	 * @throws Exception
	 */
	public Long getPrivateFamilyID(Long employeeId,String type) throws Exception;
	/**
	 * 新增收藏信息
	 * 
	 * @param BlogFavorites
	 *            收藏信息
	 * @throws Exception
	 */
	public void createFavorites(BlogFavorites blogFavorites) throws Exception;
	/**
	 * 变更收藏信息
	 * 
	 * @param BlogFavorites
	 *            收藏信息
	 * @throws Exception
	 */
	public void modifyFavorites(BlogFavorites blogFavorites) throws Exception;
	/**
	 * 删除收藏信息
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */
	public void deleteFavorites(Long blogFavoritesId) throws Exception;
	/**
	 * 根据文章ID删除收藏信息
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */
	public void deleteFavoritesByArticleId(Long articleId) throws Exception;
	/**
	 * 查询员工信息(需要分页)
	 * 
	 * @return 员工信息
	 * @throws Exception
	 */
	public List<BlogEmployee> listEmployee() throws Exception;

	/**
	 * 查询员工信息(不需要分页)
	 * 
	 * @return 员工信息
	 * @throws Exception
	 */
	public List<BlogEmployee> listEmployee2() throws Exception ;
	/**
	 * 新增员工
	 * 
	 * @param userId 员工ID
	 * @param domainId 单位ID
	 * @throws Exception
	 */
	public BlogEmployee createEmployee(long userId, long domainId) throws Exception;
	/**
	 * 变更员工
	 * 
	 * @param blogFamily
	 *            员工信息
	 * @throws Exception
	 */
	public void modifyEmployee(BlogEmployee blogEmployee) throws Exception;
	/**
	 * 删除员工信息
	 * 
	 * @param blogFamilyId
	 *            员工编号
	 * @throws Exception
	 */
	public void deleteEmployee(Long blogEmployeeId) throws Exception;
	/**
	 * 查询某员工信息
	 * 
	 * @return 员工信息
	 * @throws Exception
	 */
	public BlogEmployee getEmployeeById(Long employeeId) throws Exception;
	
	/**
	 * 判断一个用户是否开通了博客
	 * 
	 */
	public boolean blogIsOpen(Long memberId);
	
	/**
	 * 查询某员工ID是否存在
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkEmployeeId(Long employeeId) throws Exception;
	/**
	 * 方法描述：创建文章，该员工的文章数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateArticleNumber(Long employeeId, int step)throws Exception;
	/**
	 * 方法描述：创建文章，该分类的文章数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateFamilyArticleNumber(Long familyId, int step)throws Exception;
	/**
	 * 查询本部门共享信息(需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public List<BlogShare> listDepartmentShare(Long departmentId) throws Exception;
	/**
	 * 查询共享信息(需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public List<BlogShare> listShare() throws Exception;

	/**
	 * 查询共享信息(不需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public List<BlogShare> listShare2() throws Exception ;
	/**
	 * 查询他人共享信息(需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public List<BlogShare> listShareOther() throws Exception;

	/**
	 * 查询他人共享信息(不需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public List<BlogShare> listShareOther2() throws Exception ;
	
	/**
	 * 查询某共享信息
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public BlogShare getSingleShare(Long id) throws Exception;
	/**
	 * 新增共享信息
	 * 
	 * @param blogShare
	 *            共享信息
	 * @throws Exception
	 */
	public void createShare(BlogShare blogShare) throws Exception;

	/**
	 * 变更共享信息
	 * 
	 * @param blogShare
	 *           共享信息
	 * @throws Exception
	 */
	public void modifyShare(BlogShare blogShare) throws Exception;

	/**
	 * 删除共享信息
	 * 
	 * @param blogShareId
	 *            共享编号
	 * @throws Exception
	 */
	public void deleteShare(Long blogShareId) throws Exception;
	/**
	 * 查询某员工ID是否已经共享
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkEmployeeShared(Long employeeId) throws Exception;
	/**
	 * 查询别人是否给我开通共享
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkSharedEmployee(Long employeeId) throws Exception;
	/**
	 * 检查此员工是否共享给了我所在的部门
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkSharedDeparment(Long employeeId,Long departmentId) throws Exception;
	//-------------------------------------------------------
	/**
	 * 查询他人关注信息(需要分页)
	 * 
	 * @return 关注信息
	 * @throws Exception
	 */
	public List<BlogAttention> listAttention() throws Exception;
	/**
	 * 方法描述：获取一定条数的关注的人员信息
	 * 
	 * @return 关注信息
	 * @throws Exception
	 */
	public List<AttentionModel> getBlogAttentionByCount(Long userId,int count) throws Exception;
	/**
	 * 方法描述：将List<BlogAttention>转换为List<AttentionModel>
	 * 
	 * @return List 关注信息的集合
	 * @throws Exception
	 */
	public List<AttentionModel> getAttentionModelList(List<BlogAttention> attentionList)
			throws Exception;
	/**
	 * 查询他人关注信息(不需要分页)
	 * 
	 * @return 关注信息
	 * @throws Exception
	 */
	public List<BlogAttention> listAttention2() throws Exception ;
	/**
	 * 检查文章是否已经收藏
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */
	public Integer checkFavorites(Long articleId,Long employeeId) throws Exception;	
	/**
	 * 查询某关注信息
	 * 
	 * @return 关注信息
	 * @throws Exception
	 */
	public BlogAttention getSingleAttention(Long id) throws Exception;
	/**
	 * 新增关注信息
	 * 
	 * @param blogAttention
	 *            关注信息
	 * @throws Exception
	 */
	public void createAttention(BlogAttention blogAttention) throws Exception;

	/**
	 * 变更关注信息
	 * 
	 * @param blogAttention
	 *           关注信息
	 * @throws Exception
	 */
	public void modifyAttention(BlogAttention blogAttention) throws Exception;

	/**
	 * 删除关注信息
	 * 
	 * @param blogAttentionId
	 *            关注编号
	 * @throws Exception
	 */
	public void deleteAttention(Long blogAttentionId) throws Exception;
	/**
	 * 查询某员工ID是否已经关注
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkEmployeeAttention(Long blogAttentionId) throws Exception;

     /**
      * 更新博客收藏的分类
      * @param favids
      * @param newFamId
      */

    public void updateFavorites(String favids, long newFamId);
}