package com.seeyon.v3x.blog.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.blog.dao.BlogAttentionDao;
import com.seeyon.v3x.blog.dao.BlogDao;
import com.seeyon.v3x.blog.dao.BlogEmployeeDao;
import com.seeyon.v3x.blog.dao.BlogFavoritesDao;
import com.seeyon.v3x.blog.dao.BlogShareDao;
import com.seeyon.v3x.blog.domain.BlogArticle;
import com.seeyon.v3x.blog.domain.BlogAttention;
import com.seeyon.v3x.blog.domain.BlogEmployee;
import com.seeyon.v3x.blog.domain.BlogFamily;
import com.seeyon.v3x.blog.domain.BlogFavorites;
import com.seeyon.v3x.blog.domain.BlogShare;
import com.seeyon.v3x.blog.webmodel.AttentionModel;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OrgManager;

public class BlogManagerImpl implements BlogManager {
	private static final Log logger = LogFactory.getLog(BlogManagerImpl.class);
	private BlogDao blogDao;
	private BlogEmployeeDao blogEmployeeDao;
	private BlogShareDao blogShareDao;
	private BlogAttentionDao blogAttentionDao;
	private BlogFavoritesDao blogFavoritesDao;
	private OrgManager orgManager;
	private SystemConfig systemConfig; 

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public BlogDao getBlogDao() {
		return blogDao;
	}

	public void setBlogDao(BlogDao blogDao) {
		this.blogDao = blogDao;
	}
	public BlogEmployeeDao getBlogEmployeeDao() {
		return blogEmployeeDao;
	}
	
	public void setBlogEmployeeDao(BlogEmployeeDao blogEmployeeDao) {
		this.blogEmployeeDao = blogEmployeeDao;
	}
	public BlogShareDao getBlogShareDao() {
		return blogShareDao;
	}

	public void setBlogShareDao(BlogShareDao blogShareDao) {
		this.blogShareDao = blogShareDao;
	}
	public BlogAttentionDao getBlogAttentionDao() {
		return blogAttentionDao;
	}

	public void setBlogAttentionDao(BlogAttentionDao blogAttentionDao) {
		this.blogAttentionDao = blogAttentionDao;
	}
	public BlogFavoritesDao getBlogFavoritesDao() {
		return blogFavoritesDao;
	}

	public void setBlogFavoritesDao(BlogFavoritesDao blogFavoritesDao) {
		this.blogFavoritesDao = blogFavoritesDao;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	/**
	 * 查询他人分类信息(需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	public List<BlogFamily> listFamilyOther(String type,Long userId) throws Exception {
		return blogDao.listFamilyOther(type,userId);
	}
	
	/**
	 * 查询他人分类信息(不需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	public List<BlogFamily> listFamilyOther2(String type,Long userId) throws Exception{
		return blogDao.listFamilyOther2(type,userId);
	}
	/**
	 * 查询分类信息(需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	public List<BlogFamily> listFamily(String type) throws Exception {
		return blogDao.listFamily(type);
	}
	
	/**
	 * 查询分类信息(不需要分页)
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	public List<BlogFamily> listFamily2(String type) throws Exception{
		return blogDao.listFamily2(type);
	}

	/**
	 * 查询某分类信息
	 * 
	 * @return 分类信息
	 * @throws Exception
	 */
	public BlogFamily getSingleFamily(Long id) throws Exception {
		return blogDao.getSingleFamily(id);
	}
	/**
	 * 新增分类信息
	 * 
	 * @param blogFamily
	 *            分类信息
	 * @throws Exception
	 */
	public void createFamily(BlogFamily blogFamily) throws Exception {
		blogDao.createFamily(blogFamily);
	}

	/**
	 * 变更分类信息
	 * 
	 * @param blogFamily
	 *            分类信息
	 * @throws Exception
	 */
	public void modifyFamily(BlogFamily blogFamily) throws Exception {
		blogDao.modifyFamily(blogFamily);
	}

	/**
	 * 删除分类信息
	 * 
	 * @param blogFamilyId
	 *            分类编号
	 * @throws Exception
	 */
	public void deleteFamily(Long blogFamilyId) throws Exception {
		blogDao.deleteFamily(blogFamilyId);
	}
	/**
	 * 得到默认分类ID
	 * 
	 * @param blogFamilyId
	 *            分类编号
	 * @throws Exception
	 */
	public Long getDefaultFamilyID(Long employeeId,String type) throws Exception{
		return blogDao.getDefaultFamilyID(employeeId,type);
	}
	/**
	 * 得到私有分类ID
	 * 
	 * @param blogFamilyId
	 *            分类编号
	 * @throws Exception
	 */
	public Long getPrivateFamilyID(Long employeeId,String type) throws Exception{
		return blogDao.getPrivateFamilyID(employeeId,type);
	}
	/**
	 * 新增收藏信息
	 * 
	 * @param BlogFavorites
	 *            收藏信息
	 * @throws Exception
	 */
	public void createFavorites(BlogFavorites blogFavorites) throws Exception{
		blogDao.createFavorites(blogFavorites);
	}
	/**
	 * 检查文章是否已经收藏
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */
	public Integer checkFavorites(Long articleId,Long employeeId) throws Exception{
		return blogDao.checkFavorites(articleId,employeeId);
	}
	/**
	 * 变更收藏信息
	 * 
	 * @param BlogFavorites
	 *            收藏信息
	 * @throws Exception
	 */
	public void modifyFavorites(BlogFavorites blogFavorites) throws Exception{
		blogDao.modifyFavorites(blogFavorites);
	}
	/**
	 * 删除收藏信息
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */
	public void deleteFavorites(Long blogFavoritesId) throws Exception{
		blogFavoritesDao.deleteFavorites(blogFavoritesId);
	}
	
	/**
	 * 根据文章ID修改收藏分类
	 * @param blogFavoritesId
	 * @throws Exception
	 */
    public void updateFavorites(Long blogFavoritesId,Long familyId) throws Exception{
    	
		BlogFavorites dr = blogFavoritesDao.get(blogFavoritesId);
		   dr.setFamilyId(familyId);
	}
	/**
	 * 根据文章ID删除收藏信息
	 * 
	 * @param BlogFavorites
	 *           收藏信息
	 * @throws Exception
	 */	
	public void deleteFavoritesByArticleId(Long articleId) throws Exception{
		blogFavoritesDao.deleteFavoritesByArticleId(articleId);
	}
	/**
	 * 查询员工信息(需要分页)
	 * 
	 * @return 员工信息
	 * @throws Exception
	 */
	public List<BlogEmployee> listEmployee() throws Exception{
		return blogEmployeeDao.listEmployee();
	}

	/**
	 * 查询员工信息(不需要分页)
	 * 
	 * @return 员工信息
	 * @throws Exception
	 */
	public List<BlogEmployee> listEmployee2() throws Exception{
		return blogEmployeeDao.listEmployee2();
	}
	/**
	 * 新增员工
	 * 
	 * @param userId 员工ID
	 * @param domainId 单位ID
	 * @throws Exception
	 */
	public BlogEmployee createEmployee(long userId, long domainId) throws Exception {
		BlogEmployee blogEmployee = new BlogEmployee();
		blogEmployee.setId(userId);
		blogEmployee.setIntroduce("");
		blogEmployee.setImage("0");
		blogEmployee.setFlagStart((byte) 0);
		blogEmployee.setFlagShare((byte) 0);
		blogEmployee.setArticleNumber(0);
		blogEmployee.setIdCompany(domainId);
		blogEmployeeDao.createEmployee(blogEmployee);
		
		return blogEmployee;
	}
	/**
	 * 变更员工
	 * 
	 * @param blogFamily
	 *            员工信息
	 * @throws Exception
	 */
	public void modifyEmployee(BlogEmployee blogEmployee) throws Exception{
		blogEmployeeDao.modifyEmployee(blogEmployee);
	}
	/**
	 * 删除员工信息
	 * 
	 * @param blogFamilyId
	 *            员工编号
	 * @throws Exception
	 */
	public void deleteEmployee(Long blogEmployeeId) throws Exception{
		blogEmployeeDao.deleteEmployee(blogEmployeeId);
	}
	/**
	 * 查询某员工信息
	 * 
	 * @return 员工信息
	 * @throws Exception
	 */
	public BlogEmployee getEmployeeById(Long employeeId) throws Exception{
		BlogEmployee be = blogEmployeeDao.getEmployeeById(employeeId);
		
		// 防止没有信息，如果org存在则马上生成
		if(be == null){
			V3xOrgMember member = orgManager.getMemberById(employeeId);
			if(member != null){
				be = this.createEmployee(member.getId(), member.getOrgAccountId());	
			}
			
		}
		
		return be;
	}

	public boolean blogIsOpen(Long memberId){
		try {
			String enableBlog = systemConfig.get(IConfigPublicKey.BLOG_ENABLE);
			if (enableBlog != null && "enable".equals(enableBlog)) {
				if(memberId != null) {
					BlogEmployee be = this.getEmployeeById(memberId);
					if(be != null && be.getFlagStart().byteValue() == Byte.parseByte("1")) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			logger.error("根据id[" + memberId + "]获取人员博客信息过程中出现异常：", e);
		}
		return false;
	}
	
	/**
	 * 查询某员工ID是否存在
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkEmployeeId(Long employeeId) throws Exception{
		return blogEmployeeDao.checkEmployeeId(employeeId);
	}
	/**
	 * 方法描述：创建文章，该员工的文章数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateArticleNumber(Long employeeId, int step)throws Exception{
		blogEmployeeDao.updateArticleNumber(employeeId,step);
	}
	/**
	 * 方法描述：创建文章，该分类的文章数加一
	 *
	 * @param articleId 主题ID
	 * @throws Exception
	 */
	public void updateFamilyArticleNumber(Long familyId, int step)throws Exception{
		blogDao.updateArticleNumber(familyId,step);
	}
	/**
	 * 查询本部门共享信息(需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public List<BlogShare> listDepartmentShare(Long departmentId) throws Exception{
		return blogShareDao.listDepartmentShare(departmentId);
	}
	/**
	 * 查询共享信息(需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public List<BlogShare> listShare() throws Exception{
		return blogShareDao.listShare();
	}

	/**
	 * 查询共享信息(不需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public List<BlogShare> listShare2() throws Exception {
		return blogShareDao.listShare2();
	}
	/**
	 * 查询他人共享信息(需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public List<BlogShare> listShareOther() throws Exception{
		return blogShareDao.listShareOther();
	}

	/**
	 * 查询他人共享信息(不需要分页)
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public List<BlogShare> listShareOther2() throws Exception {
		return blogShareDao.listShareOther2();
	}
	
	/**
	 * 查询某共享信息
	 * 
	 * @return 共享信息
	 * @throws Exception
	 */
	public BlogShare getSingleShare(Long id) throws Exception{
		return blogShareDao.getSingleShare(id);
	}
	/**
	 * 新增共享信息
	 * 
	 * @param blogShare
	 *            共享信息
	 * @throws Exception
	 */
	public void createShare(BlogShare blogShare) throws Exception{
		blogShareDao.createShare(blogShare);
	}

	/**
	 * 变更共享信息
	 * 
	 * @param blogShare
	 *           共享信息
	 * @throws Exception
	 */
	public void modifyShare(BlogShare blogShare) throws Exception{
		blogShareDao.modifyShare(blogShare);
	}

	/**
	 * 删除共享信息
	 * 
	 * @param blogShareId
	 *            共享编号
	 * @throws Exception
	 */
	public void deleteShare(Long blogShareId) throws Exception{
		blogShareDao.deleteShare(blogShareId);
	}
	/**
	 * 查询某员工ID是否已经共享
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkEmployeeShared(Long employeeId) throws Exception{
		return blogShareDao.checkEmployeeShared(employeeId);
	}
	/**
	 * 查询别人是否给我开通共享
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkSharedEmployee(Long employeeId) throws Exception
	{
		return blogShareDao.checkSharedEmployee(employeeId);	
	}
	/**
	 * 检查此员工是否共享给了我所在的部门
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkSharedDeparment(Long employeeId,Long departmentId) throws Exception{
		return blogShareDao.checkSharedDeparment( employeeId, departmentId);	
	}

//	-------------------------------------------------------
	/**
	 * 查询他人关注信息(需要分页)
	 * 
	 * @return 关注信息
	 * @throws Exception
	 */
	public List<BlogAttention> listAttention() throws Exception{
		return blogAttentionDao.listAttention();
	}

	/**
	 * 方法描述：获取一定条数的关注的人员信息
	 * 
	 * @return 关注信息
	 * @throws Exception
	 */
	public List<AttentionModel> getBlogAttentionByCount(Long userId,int count) throws Exception {
//		列出关注信息
		List<BlogAttention> AttentionList = blogAttentionDao.listAttention2();
		List<AttentionModel> AttentionModelList = null;
		if (AttentionList.size() > count) {
			AttentionModelList = this.getAttentionModelList(AttentionList.subList(0, count));
		} else {
			AttentionModelList = this.getAttentionModelList(AttentionList);
		}
		
		return AttentionModelList;

	}
	/**
	 * 方法描述：将List<BlogAttention>转换为List<AttentionModel>
	 * 
	 * @return List 关注信息的集合
	 * @throws Exception
	 */
	public List<AttentionModel> getAttentionModelList(List<BlogAttention> attentionList)
			throws Exception {
		List<AttentionModel> AttentionModelList = null;
		AttentionModelList = new ArrayList<AttentionModel>();
		Iterator<BlogAttention> BlogAttentionIterator = null;

		BlogAttentionIterator = attentionList.iterator();
		Long id = new Long(0);
		long deptId = -1;
		long levelId = -1;
		long postId = -1;
		long employeeId = -1;
		BlogEmployee blogEmployee = null;
		// 构造显示列表
		String typeProperty = V3xOrgMember.ORGENT_TYPE_MEMBER;	//parseElements（）这个方法中用到
		while (BlogAttentionIterator.hasNext()) {
			BlogAttention blogAttention = null;
			AttentionModel attentionModel = null;

			blogAttention = this.getSingleAttention(BlogAttentionIterator.next()
					.getId());

			id = blogAttention.getAttentionId();
			
			// 用户有效性判断
			if(!Constants.isValidOrgEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, id))
				continue;
			
			//id=Long.parseLong("-5362937964371884064");
			employeeId = blogAttention.getEmployeeId();
			//System.out.println("listAllAttention id==" + id);
			attentionModel = new AttentionModel();
			attentionModel.setId(blogAttention.getId());
			attentionModel.setAttentionId(id);
			attentionModel.setEmployeeId(employeeId);
			attentionModel.setTypeProperty(typeProperty);
			
			blogEmployee = this.getEmployeeById(id);
			
		   blogEmployee.getBlogArticle();
		   int shareNum= 0;
		   
		   if(blogEmployee.getBlogArticle()!= null){
				Iterator it=blogEmployee.getBlogArticle().iterator();
				
				while(it.hasNext()){
					BlogArticle article=(BlogArticle)it.next();
					if(article.getState()==0) shareNum++;
				}
		   }
			
			attentionModel.setIntroduce(blogEmployee.getIntroduce());
			attentionModel.setArticleNumber(shareNum);
			String img = blogEmployee.getImage();
			if(img == null || "".equals(img))
				img = "0";
			attentionModel.setImage(img);
			attentionModel.setFlagStart(blogEmployee.getFlagStart());
            if(blogEmployee.getFlagStart() != null && blogEmployee.getFlagStart().intValue() != 0){
            	attentionModel.setStartFlag(true) ;
            }
			V3xOrgMember v3xOrgMember = null;
			if (orgManager==null){
				//System.out.println("listAllAttention orgManager==null");
			}else{
				v3xOrgMember = orgManager.getMemberById(id);
			}
			
			if (v3xOrgMember != null){
				attentionModel.setUserName(v3xOrgMember.getName());

				deptId = v3xOrgMember.getOrgDepartmentId();
				levelId = v3xOrgMember.getOrgLevelId();
				postId = v3xOrgMember.getOrgPostId();

				V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
				
				if (dept != null) {
					attentionModel.setDepartmentName(dept.getName());
				}

				V3xOrgLevel level = orgManager.getLevelById(levelId);
				if (null != level) {
					attentionModel.setLevelName(level.getName());
				}

				V3xOrgPost post = orgManager.getPostById(postId);
				if (null != post) {
					attentionModel.setPostName(post.getName());
				}
			}else{
				attentionModel.setUserName("");
			}
			AttentionModelList.add(attentionModel);
		}
		return AttentionModelList;
	}
	/**
	 * 查询他人关注信息(不需要分页)
	 * 
	 * @return 关注信息
	 * @throws Exception
	 */
	public List<BlogAttention> listAttention2() throws Exception {
		return blogAttentionDao.listAttention2();
	}
	
	/**
	 * 查询某关注信息
	 * 
	 * @return 关注信息
	 * @throws Exception
	 */
	public BlogAttention getSingleAttention(Long id) throws Exception{
		return blogAttentionDao.getSingleAttention(id);
	}
	/**
	 * 新增关注信息
	 * 
	 * @param blogAttention
	 *            关注信息
	 * @throws Exception
	 */
	public void createAttention(BlogAttention blogAttention) throws Exception{
		blogAttentionDao.createAttention(blogAttention);
	}

	/**
	 * 变更关注信息
	 * 
	 * @param blogAttention
	 *           关注信息
	 * @throws Exception
	 */
	public void modifyAttention(BlogAttention blogAttention) throws Exception{
		blogAttentionDao.modifyAttention(blogAttention);
	}

	/**
	 * 删除关注信息
	 * 
	 * @param blogAttentionId
	 *            关注编号
	 * @throws Exception
	 */
	public void deleteAttention(Long blogAttentionId) throws Exception{
		blogAttentionDao.deleteAttention(blogAttentionId);
	}
	/**
	 * 查询某员工ID是否已经关注
	 * 
	 * @param employeeId
	 *           员工数
	 * @throws Exception
	 */
	public Integer checkEmployeeAttention(Long blogAttentionId) throws Exception{
		return blogAttentionDao.checkEmployeeAttention(blogAttentionId);
	}
	
	public void updateFavorites(String favids, long newFamId){
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("newFamId",newFamId);
		namedParameters.put("ids", Constants.parseStrings2Longs(favids, ","));
		
		String hql = "update BlogFavorites set familyId =:newFamId where id in(:ids)";
		this.blogDao.bulkUpdate(hql, namedParameters);
	}

}