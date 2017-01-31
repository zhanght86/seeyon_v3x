package com.seeyon.v3x.doc.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.doc.domain.DocFavorite;
import com.seeyon.v3x.doc.domain.DocLearning;
import com.seeyon.v3x.doc.domain.DocResource;

/**
 * 常用文档
 */
public interface DocFavoriteManager {
	/**
	 * 设置收藏
	 * 
	 * @param docResourceId
	 * @param siteId
	 * @param siteType
	 * @param orderNum
	 * @param createUserId
	 * 
	 * @return true：发送成功  false：重复发送
	 */
	public void setFavoriteDoc(long docResourceId, List<Long> orgIds, String orgType);
	public void setFavoriteDoc(List<Long> docResourceIds, List<Long> orgIds, String orgType);

	/**
	 * 根据收藏id删除收藏
	 * 
	 * @param id
	 */
	public void deleteFavoriteDocById(Long id);
	/**
	 * 根据收藏id删除收藏
	 * 
	 * @param id
	 */
	public void deleteFavoriteDocByIds(String ids);

	/**
	 * 根据文档id删除收藏
	 * 
	 * @param docResourceId
	 * @param siteId
	 * @param siteType
	 */
	public void deleteFavoriteDocByDoc(Long docResourceId, Long orgId, String orgType);
	/**
	 * 删除收藏
	 * 
	 * @param docResourceId
	 * @param siteId
	 * @param siteType
	 */
	public void deleteFavorites(String docResIds, Long orgId, String orgType);
	
	/**
	 * 根据文档id删除收藏
	 * 如果是文档夹，删除所有下级
	 */
	public void deleteFavoriteDocByDoc(DocResource dr);

	/**
	 * 根据用户id查找个人首页文档对象列表
	 * 
	 * @param siteId
	 * @param siteType
	 * @return
	 */
	public List<DocFavorite> findFavoritePersonalDocsByOrg(Long userId);
	/**
	 * 根据用户id查找个人首页文档对象列表
	 * 
	 * @param siteId
	 * @param siteType
	 * @return
	 */
	public List<DocFavorite> findFavoritePersonalDocsByOrgByCount(Long userId, int count);

//	/**
//	 * 根据首页id和首页类型查找公共文档对象列表
//	 * 
//	 * @param siteId
//	 * @param siteType
//	 * @return
//	 */
//	public List<DocFavorite> findFavoriteCommonDocsByOrg(Long orgId, String orgType, String userIds);
//	/**
//	 * 根据首页id和首页类型查找公共文档对象列表
//	 * 
//	 * @param siteId
//	 * @param siteType
//	 * @return
//	 */
//	public List<DocFavorite> findFavoriteCommonDocsByOrgByCount(Long orgId, String orgType, 
//			String userIds, int count);

	/**
	 * 根据首页id和类型查找收藏对象列表
	 * 
	 * @param siteId
	 * @param siteType
	 * @return
	 */
	public List<DocFavorite> findFavoriteByOrg(Long orgId, String orgType);
	
	/**
	 * 根据首页id和类型查找收藏对象列表
	 * 
	 * @param siteId
	 * @param siteType
	 * @return
	 */
	public List<DocFavorite> findFavoriteByOrgByCount(Long orgId, String orgType, int count);
	
//	/**
//	 * 分页查找收藏
//	 */
//	public List<DocFavorite> pagedFindFavorite(Long orgId, String orgType, String aclIds)throws Exception;
//	public int findFavoriteTotal(Long orgId, String orgType, String aclIds)throws Exception;

	/**
	 * 根据id获取收藏对象
	 * 
	 * @param id
	 * @return
	 */
	public DocFavorite getDocFavoriteById(Long id);

	/**
	 * 修改显示顺序（升）
	 * 
	 * @param id
	 */
	public void updateDocFavoriteOrderUp(Long id, Long pervId);

	/**
	 * 修改显示顺序（降）
	 * 
	 * @param id
	 */
	public void updateDocFavoriteOrderDown(Long id, Long nextId); 
	
	/**
	 * 
	 */
	public List<DocFavorite> getDocFavoritesByOrgTypeAndDocId(String orgType, long docResourceId);
	public Map<Long, List<DocFavorite>> getDocFavoritesByOrgTypeAndDocIds(String orgType, List<Long> docResourceIds);
	public List<DocFavorite> getFavoritesByCount(final String orgType, final long orgId, final int count);
	public List getFavoritesByPage(final String orgType, final long orgId,final String name, final String value);
	public List getFavoritesByPage(final String orgType, final long orgId);
//	public List<DocFavorite> findFavoritesPersonalDocsBySite(Long userId);
	public boolean isFavorite(long docId);

}
