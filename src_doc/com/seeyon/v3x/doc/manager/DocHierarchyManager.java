package com.seeyon.v3x.doc.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.cap.info.domain.InfoStatCAP;
import com.seeyon.cap.info.domain.InfoSummaryCAP;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocBody;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.webmodel.DocEditVO;
import com.seeyon.v3x.doc.webmodel.DocSearchModel;
import com.seeyon.v3x.doc.webmodel.DocSortProperty;
import com.seeyon.v3x.doc.webmodel.DocTreeVO;
import com.seeyon.v3x.doc.webmodel.FolderItemDoc;
import com.seeyon.v3x.doc.webmodel.SimpleDocQueryModel;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.project.domain.ProjectSummary;

public interface DocHierarchyManager extends IndexEnable {
	// 页面判断基础上，所有权限在此都要重新判断一遍

	// 创建文档夹
	/**
	 * 创建一个普通文档夹
	 */
	public DocResource createCommonFolder(String name, Long destFolderId, Long userId, String orgIds) throws DocException;
	
	public DocResource createCommonFolderWithoutAcl(String name, Long destFolderId,
			Long userId, boolean parentVersionEnabled, boolean parentCommentEnabled) throws DocException;

	/**
	 * 创建一个普通文档夹, 不考虑权限
	 */
	public DocResource createCommonFolderWithoutAcl(String name, Long destFolderId, Long userId) throws DocException;
	
	/**
	 * 根据类型创建文档夹
	 */
	public DocResource createFolderByTypeWithoutAcl(String name, Long type,
			Long docLibId, Long destFolderId, Long userId) throws DocException;
	
	public DocResource createFolderByTypeWithoutAcl(String name, Long type,
			Long docLibId, Long destFolderId, Long userId, boolean parentVersionEnabled, boolean parentCommentEnabled) throws DocException;

	/**
	 * 初始化个人文档库
	 */
	public Long initPersonalLib(Long docLibId, String docLibName, Long userId)
			throws DocException;

	/**
	 * 初始化单位文档库
	 */
	public Long initCorpLib(Long docLibId, String docLibName, Long userId)
			throws DocException;
	/**
	 * 初始化项目文档库
	 */
	public Long initCaseLib(Long docLibId, String docLibName, Long userId)
			throws DocException;

	/**
	 * 初始化公文文档库
	 */
	public Long initArcsLib(Long docLibId, String docLibName, Long userId)
			throws DocException;

	/**
	 * 初始化自定义文档库
	 */
	public Long initCustomLib(Long docLibId, String docLibName, Long userId)
			throws DocException;

	/**
	 * 上传单个文件，作为一个单独文档
	 */
	public Long uploadFile(V3XFile file, Long docLibId, byte docLibType, Long destFolderId,
			Long userId, String orgIds, boolean parentCommentEnabled, boolean parentVersionEnabled) throws DocException;

	/**
	 * 上传单个文件，作为一个单独文档，不考虑权限
	 */
	public DocResource uploadFileWithoutAcl(V3XFile file, Long docLibId, byte docLibType,
			Long destFolderId, Long userId, boolean parentCommentEnabled, boolean parentVersionEnabled) throws DocException;

	/**
	 * 创建复合文档
	 */
	public DocResource createDoc(String name, DocBody docBody, Long docLibId,
			Long destFolderId, Long userId, String orgIds, boolean parentCommentEnabled, boolean parentVersionEnabled,
			long contentTypeId, Map<String, Comparable> metadatas) throws DocException;

	/**
	 * 创建复合文档， 不考虑权限
	 */
	public DocResource createDocWithoutAcl(String name, DocBody docBody, Long docLibId,
			Long destFolderId, Long userId, boolean parentCommentEnabled, boolean parentVersionEnabled, 
			long contentTypeId, Map<String, Comparable> metadatas) throws DocException;
	
	/**
	 * 创建复合文档， 不考虑权限
	 */
	public DocResource createDocWithoutAcl(String name, String description, String keyword, 
			DocBody docBody, Long docLibId, Long destFolderId, Long userId, 
			boolean parentCommentEnabled, boolean parentVersionEnabled, long contentTypeId, Map<String, 
			Comparable> metadatas) ;
	
	/**
	 * 判断同名文档是否已经存在
	 */
	public  boolean hasSameNameAndSameTypeDr(Long parentId, String name,
			Long type);
	public  boolean hasSameNameAndSameTypeDr(Long parentId, String name,
			String type);
	
	/**
	 * 判断一个文档是否归档类型
	 * 
	 * common: 直接打开类型
	 * link,4654646:  源文件存在的链接类型，sourceId
	 * link:   链接类型，源文件不存在
	 * 2,46466565465465  归档类型，第一个是key，第二个是sourceId
	 */
	public String getTheOpenType(Long docResourceId);
	
	/////////////////////////// 归档开始 /////////////////////////////////////////
	
	/**
	 * 预归档之后的正式归档
	 * @return 正常情况是归档生成记录的id; 如果目标文档夹不存在，返回 null
	 */
	public Long pigeonholeAfterPre(int appEnumKey, Long sourceId, boolean hasAttachments, Long destFolderId,
			Long userId) throws DocException;
	
	/**
	 * 预归档之后表单视图和操作id
	 * @return 
	 */
	public Long pigeonFormpotent(Long sourceId, Long docresid,String formids) throws DocException;
	
	/**
	 * 预归档之后查询表单视图和操作id
	 * @return 
	 */
	public List queryFormpotent(BaseModel bm) throws DocException;


	/**
	 * 链接方式归档
	 */
	public Long pigeonholeAsLink(Long sourceId, boolean hasAttachments, 
			int appEnumKey, Long docLibId, Long destFolderId,
			Long userId, String orgIds) throws DocException;
	
	/**
	 * 链接方式归档
	 */
	public List<Long> pigeonholeAsLink(List<Long> sourceIds, List<Boolean> hasAttachments, 
			int appEnumKey, Long docLibId, Long destFolderId,
			Long userId) throws DocException;

	/**
	 * 链接方式归档，不考虑权限
	 */
	public Long pigeonholeAsLinkWithoutAcl(Long sourceId, boolean hasAttachments, 
			int appEnumKey, Long docLibId, Long destFolderId,
			Long userId) throws DocException;
	/**
	 * lijl重写,添加departPigeonhole参数,用来区分"部门归档"的来历,链接方式归档，不考虑权限
	 * 判断是不是从"发文管理"——"已办"中的"部门归档"中来的
	 */
	public Long pigeonholeAsLinkWithoutAcl(Long sourceId, boolean hasAttachments, 
			int appEnumKey, Long docLibId, Long destFolderId,
			Long userId,String departPigeonhole) throws DocException;
	
	/**
	 * 从首页查找文档库下文档
	 */
	public List<DocResource> findAllDocsByPageBySection(Long parentId, Long contentType,
			Integer pageNo, Integer pageSize, Long userId);

	/**
	 * 对通过分页方法进行适度简化，因为分页参数可以直接通过ThreadLocal获取，无需重复的分页信息处理代码
	 * @param parentId
	 * @param contentType
	 * @param userId
	 * @see #findAllDocsByPage(Long, Long, Integer, Integer, Long)
	 */
	public List<DocResource> findAllDocsByPage(Long parentId, Long contentType, Long userId,String... type);

	/**
	 * 是否归档的查询
	 * @param parentId
	 * @param contentType
	 * @param pageNo
	 * @param pageSize
	 * @param userId
	 * @param flag
	 * @return
	 */
	public List<DocResource> findAllDocsByPage(Long parentId, Long contentType,
				Integer pageNo, Integer pageSize, Long userId,int flag);
	
	/**
	 * 是否归档的查询中剥离出分页参数，避免重复的分页信息处理代码
	 */
	public List<DocResource> findAllDocsByPage(Long parentId, Long contentType, Long userId,int flag);
	
	/**
	 * sourceId          文档ID
	 * appEnumKey        类型
	 * userId            人员ID
	 * 修改部门归档的公文单的内容
	 */
	public void updatePigeHoleFile(Long sourceId, int appEnumKey, Long userId) throws DocException;
	/**
	 * 公文归档
	 */
	public Long pigeonholeEdoc(EdocSummary summary, boolean hasAttachments) throws DocException;
	
	/**
	 * 归档源是否存在
	 */
	public boolean hasPigeonholeSource(Long docId , Integer appKey);
	/**
	 * 归档源是否存在
	 */
	public boolean hasPigeonholeSource(ApplicationCategoryEnum app, Long sourceId);
	/**
	 * 归档源是否存在
	 */
	public boolean hasPigeonholeSource(Integer appKey, Long sourceId);
	
	/////////////////////////// 归档结束 /////////////////////////////////////////
	
	/**
	 * 创建链接,包括文档、文档夹
	 */
	public DocResource createLink(Long sourceId, Long docLibId, Long destFolderId,
			Long userId, String orgIds) throws DocException;

	/**
	 * 创建链接,包括文档、文档夹，不考虑权限
	 */
	public DocResource createLinkWithoutAcl(Long sourceId, Long docLibId,
			Long destFolderId, Long userId) throws DocException;	

	/**
	 * 批量创建链接,包括文档、文档夹
	 */
	public List<Long> createLinks(List<Long> sourceIds, Long docLibId, Long destFolderId,
			Long userId, String orgIds) throws DocException;
	
	/**
	 * 批量创建链接,包括文档、文档夹，不考虑权限
	 */
	public List<Long> createLinksWithoutAcl(List<Long> sourceIds, Long docLibId,
			Long destFolderId, Long userId) throws DocException;

	// 修改类操作
	/**
	 * 更新DocResource指定字段
	 * 
	 * @param properties
	 *            key--属性名, value--值
	 */
	public void updateDocResource(Long docResourceId, Map<String, Object> properties);

	/**
	 * 文档替换
	 * @param remainOld		在替换时，是否保存旧源文件（用于保留历史版本）
	 */
	public DocResource replaceDoc(DocResource dr, V3XFile file, Long userId, String orgIds, boolean remainOld) throws DocException;
	
	/**
	 * 文档替换，不考虑权限
	 * @param remainOld		在替换时，是否保存旧源文件（用于保留历史版本）
	 */
	public DocResource replaceDocWithoutAcl(Long docResourceId, V3XFile file, Long userId, boolean remainOld) throws DocException;
	
	/**
	 * 文档替换，不考虑权限
	 * @param remainOld		在替换时，是否保存旧源文件（用于保留历史版本）
	 */
	public DocResource replaceDocWithoutAcl(DocResource dr, V3XFile file, Long userId, boolean remainOld) throws DocException;

	/**
	 * 签出文档准备修改
	 */
	public void checkOutDocResource(Long docResourceId, Long userId);

	/**
	 * 签入文档
	 */
	public void checkInDocResource(Long docResourceId, Long userId,
			String orgIds) throws DocException;
	/**
	 * 签入文档,不考虑权限
	 */
	public void checkInDocResourceWithoutAcl(Long docResourceId, Long userId);
	/**
	 * 批量签入文档,不考虑权限
	 */
	public void checkInDocResourcesWithoutAcl(List<Long> drIds, Long userId) throws DocException;

	/**
	 * 修改复合文档
	 */
	public void updateDoc(FolderItemDoc doc, Long userId, String orgIds)
			throws DocException;

	/**
	 * 更改复合文档的大小
	 */
	public void updateDocSize(Long docResourceId, DocBody docBody,
			List<Attachment> atts) throws DocException;
	/**
	 *更改附件标志
	 */
	public void updateDocAttFlag(Long docResourceId, boolean attaFlag) throws DocException;
	/**
	 * 更改上传文件的大小
	 */
	public void updateFileSize(Long docResourceId) throws DocException;

	/**
	 * 保存复合文档的正文
	 */
	public void saveBody(Long docResourceId, DocBody docBody);

	/**
	 * 更改复合文档的正文
	 */
	public void updateBody(Long docResourceId, String content);

	/**
	 * 删除复合文档的正文
	 */
	public void removeBody(Long docResourceId);

	/**
	 * 查找复合文档的正文
	 */
	public DocBody getBody(Long docResourceId);

	/**
	 * 修改复合文档，不考虑权限
	 */
	public DocResource updateDocWithoutAcl(FolderItemDoc doc, Long userId) throws DocException;
	
	/**
	 * 修改word，excel
	 */
	public DocResource updateFileWithoutAcl(DocEditVO vo, byte docLibType) throws DocException;
	
	public DocResource updateFileWithoutAcl(DocEditVO vo, byte docLibType, boolean remainOldFile) throws DocException;
	
	public DocResource updateFileWithoutAcl(DocEditVO vo, byte docLibType, boolean remainOldFile, boolean replaceFlag) throws DocException;

	/**
	 * 文档访问次数统计 随着每一次访问，增加一次访问次数
	 */
	public void accessOneTime(Long docResourceId, boolean learning, boolean personalLib) ;
	/**
	 * 文档访问次数统计 随着每一次访问，增加一次访问次数
	 * @deprecated
	 */
	public void accessOneTime(Long docResourceId);
	/**
	 * 文档评论次数统计
	 */
	public void forumOneTime(Long docResourceId);
	public void deleteForumOneTime(Long docResourceId);

	/**
	 * 移动单个文档，不考虑权限
	 * @param	destFolderLevelPath		目标文档夹的层级深度
	 */
	public void moveDocWithoutAcl(DocResource dr, Long srcLibId, Long destLibId, Long destFolderId, 
			Long userId, boolean destPersonal, boolean parentCommentEnabled, int destFolderLevelPath) throws DocException;
	
	/**
	 * 统一项目文档库使用
	 */
	public void moveDocWithoutAcl4Project(DocResource dr);

	/**
	 * 重命名文档/文档夹
	 */
	public void renameDoc(Long docResourceId, String newName, Long userId,
			String orgIds) throws DocException;
	public void renameDocWithoutAcl(Long docResourceId, String newName, Long userId);
	
	/**
	 * 文档夹是否允许评论的修改
	 */
	public void setFolderCommentEnabled(DocResource drs, boolean enabled, int includeDocs, Long userId) ;


	
	/**
	 * 加为学习文档
	 */
	public void setDocLearning(long docResourceId);
	public void setDocLearning(List<Long> docResourceIds);
	/**
	 * 取消学习文档
	 */
	public void cancelDocLearning(long docResourceId);
	public void cancelDocLearning(List<Long> docResourceIds);

	/**
	 * 删除文档，不涉及权限
	 */
	public void removeDocWithoutAcl(DocResource dr, Long userId, boolean first)
			throws DocException;
	
	/** 删除单个节点，不考虑文档、文档夹。考虑权限
	 */
	public void removeDocWithAcl(DocResource dr, Long userId, String orgIds, boolean first)
			throws DocException;

	/**************************** 查找类操作开始*******************************/
	/**
	 * 查找我的文档库下的文档夹
	 */
	public List<DocResource> findMyFolders(Long parentId, Long contentType,
			Long userId, String orgIds) throws DocException;
	/**
	 * 从首页查找我的文档库下的内容
	 */
	
	public List<DocResource> findAllMyDocsByPageByDate(Long parentId,
			Long contentType, Integer pageNo, Integer pageSize, Long userId);
	/**
	 * 分页查找我的文档库下的所有内容
	 */
	public List<DocResource> findAllMyDocsByPage(Long parentId,
			Long contentType, Integer pageNo, Integer pageSize, Long userId) throws DocException;

	/**
	 * 通用文档夹查找
	 */
	public List<DocResource> findFolders(Long parentId, Long contentType,
			Long userId, String orgIds, boolean isPersonalLib);
	
   /**
    * 查找文件夹不过滤权限
    */
	public List<DocResource> findFoldersWithOutAcl(Long parentId);

	/**
	 * 通用分页查找所有内容
	 */
	public List<DocResource> findAllDocsByPage(Long parentId, Long contentType,
			Integer pageNo, Integer pageSize, Long userId,String... type);

	/**
	 * 根据libId 查找 root
	 */
	public DocResource getRootByLibId(Long libId);
	
	/**
	 * 根据libIds获取对应的根文档夹，不做权限设置处理，不同于{@link #getRootsByLibIds(List, String)}
	 * @param libIds	文档库ID集合
	 * @return	文档库对应的根文档夹集合
	 */
	public List<DocResource> getRootByLibIds(List<Long> libIds);
	
	/**
	 * 根据libIds获取对应的根文档夹键值对
	 * @param libIds	文档库ID集合
	 * @return	K - libId，V - 根文档夹
	 */
	public Map<Long, DocResource> getRootMapByLibIds(List<Long> libIds);
	
	public List<DocResource> getRootsByLibIds(List<Long> ids, String orgIds);
	public List<DocResource> getRootsByLibIds(String ids, String orgIds);
	/**
	 * 根据docResourceId 查找 DocResource 对象
	 */
	public DocResource getDocResourceById(Long docResourceId);
//	/**
//	 * 根据ID来查找DocResources 对象
//	 * @param ids
//	 * @return
//	 */
//	public List<Object[]> getDocResProBySourceIds(List<Long> ids);
    /**
	 * 根据docResourceId 查找 DocResource.parentFrId
	 */
	public Long getParentFrIdByResourceId(Long docResourceId);

	/**
	 * 判断一个id下的DocResource是否存在
	 */
	public boolean docResourceExist(Long docResourceId);
	public String docResourceNoChange(Long docResourceId, String logicalPath);
	
	public boolean docResourceEdit(Long docResourceId);

	/**
	 * 根据docResourceId 查找某个文档的从根节点开始的整个文档夹对象链
	 */
	public List<DocResource> getFoldersChainById(Long docResourceId)
			throws DocException;
	
	/**
	 * 查询共享所有人列表
	 */
	public Set<Long> getShareUserIds(Long userId, String orgIds);

	/**
	 * 查询共享所有人列表（分页）
	 */
	public Set<Long> getShareUserIdsByPage(Long userId, String orgIds,
			Integer pageNo, Integer pageSize);

	/**
	 * 根据所有人查询共享第一级文档夹
	 */
	public List<DocResource> getShareRootDocs(Long ownerId, Long userId,
			String orgIds);

	/**
	 * 根据所有人查询共享第一级文档夹（分页）
	 */
	public List<DocResource> getShareRootDocsByPage(Long ownerId,
			Integer pageNo, Integer pageSize, Long userId, String orgIds);

	/**
	 * 查询借阅所有人id列表
	 */
	public Set<Long> getBorrowUserIds(Long userId, String orgIds);

	/**
	 * 查询借阅所有人id列表(分页)
	 */
	public Set<Long> getBorrowUserIdsByPage(Long userId, String orgIds,
			Integer pageNo, Integer pageSize);

	/**
	 * 根据所有人查询借阅文档(分页)
	 */
	public List<DocResource> getBorrowDocsByPage(Long ownerId, Integer pageNo,
			Integer pageSize, Long userId, String orgIds);

	/**
	 * 判断一个库下是否只存在一个根文档夹
	 */
	public boolean isLibOnlyRoot(Long libId);

	/**
	 * 判断某个文档库是否个人文档库
	 */
	public boolean isNotPartOfMyLib(Long userId, Long libId);
	/**
	 * 我的文档下查找关联
	 */
	public List<DocResource> findMyDocs4Rel(Long parentId) throws DocException;
	/**
	 * 根据签出时间查找所有签出文档
	 */
	public List<DocResource> findAllCheckedOutDocsByDays(int days) throws DocException;
	/**
	 * 查找某个库下的所有签出文档
	 */
	public List<DocResource> findAllCheckoutDocsByDocLibIdByPage(final long docLibId) ;
	/**
	 * 根据hql和参数查询
	 */
	public List<DocResource> findDocResourceByHql(String hql, Object... args) ;
	/**
	 * 判断给定的libId代表的库是否个人文档库
	 */
	public boolean isPersonalLib(Long libId);
	/**
	 * 判断用户是否库的管理员
	 */
	public boolean isOwnerOfLib(Long libId, Long userId);
	
	/**
	 * 根据文档夹ID查找该文档夹下第一级得所有文档、文档夹
	 * @param docResId
	 * @return
	 */
	public List<DocResource> findFirstDocResourceById(long docResId);
	
	/**
	 * 根据id获名字
	 * @return 正常返回 name; 如果该文档不存在，返回 null
	 */
	public String getNameById(Long docResourceId);
	
	/**
	 * 找出某个文档夹下的所有符合类型的数据记录
	 * @param types 类型连接字符串,逗号分割 如 163,165,56
	 */
	public List<DocResource> getDocsInFolderByType(long folderId, String types);
	
	/**
	 * 查询某人共享给当前用户的所有文档
	 * 关联人员使用
	 */
	public List<DocTreeVO> getShareDocsByOwnerId(Long ownerId) throws DocException;
	/**
	 *得到个人文档库
	 */
	public DocResource getPersonalFolderOfUser(long userId);
	
	/**
	 * 得到某个用户的个人文档库的根
	 */
	public DocResource getPersonalLibRootOfUser(long userId);
	
	/**************************** 查找类操作结束*******************************/
	
	/****************项目管理开始********************/
	
	/**
	 * 新建项目
	 * 1. 生成项目一级文档夹
	 * 2. 生成该项目的二级阶段文档夹
	 * 
	 * @return 项目一级文档夹的id
	 * 不判断权限，在项目管理处做判断
	 */
	public Long createNewProject(ProjectSummary summary, Long userId) throws DocException;
	
	/**
	 * 修改项目信息
	 * @param summary 要修改的项目summary
	 * @param addPhases 要新增的阶段
	 * @param updatePhases 要修改的阶段
	 * @param delPhaseIds 要删除的阶段id串，如 1,2,3
	 */
	public void updateProject(ProjectSummary summary, Set<ProjectPhase> addPhases, 
			Set<ProjectPhase> updatePhases, String[] delPhaseIds, Long userId) throws DocException;
	
	/**
	 * 删除项目
	 * 关联项目模块做了项目删除标记，文档也要做删除标记
	 */
	public void deleteProject(Long summaryId, Long userId) throws DocException;
	
	/**
	 * 根据项目id或项目阶段id得到项目文档夹或项目阶段文档夹
	 * @param folderId	项目id或项目阶段id
	 * @param  isPhase	是否为项目阶段
	 */
	public DocResource getProjectFolderByProjectId(long folderId, boolean isPhase);
	
	/**
	 * 根据项目id获取对应的项目文档夹id
	 * @param projectid		项目id
	 */
	public DocResource getProjectFolderByProjectId(long projectid);
	
	/**
	 * 判断一个项目或项目阶段文档夹下是否有文档（不算文档夹，不论层级）
	 */
	public boolean hasDocsInProject(long sourceId);
	
	/**
	 * 判断项目或项目阶段文档夹下是否有文档（不算文档夹，不论层级）,sourceIds ","分割项目或者项目阶段sourceID
	 */
	public boolean hasDocsInProjects(String sourceIds);
	
	/**
	 * 删除项目文档夹项目阶段文档夹
	 */
	public void removeProjectFolderWithoutAcl(long sourceId)
			throws DocException;
	
	/****************项目管理结束********************/
	
	/**
	 * ajax调用，协同转发的辅助方法
	 */
	public long getSummaryIdByAffairId(long affairId);
	
	/**
	 * 获取带有单位简称的实体名
	 */
	public String getEntityNameWithAccountShort(String orgType, Long orgId);
	
	/**
	 * 判断内容类型
	 */
	public boolean contentTypeExist(long typeId);
	
	/**
	 * 压缩符合文档的下载文件
	 */
	public boolean docDownloadCompress(long docResourceId);
	
	/**
	 * 压缩历史版本复合文档的下载文件
	 * @param docVersionId	历史版本信息ID
	 */
	public boolean docHistoryDownloadCompress(long docVersionId);
	
	/**
	 * 综合查询
	 */
	public List<DocResource> iSearch(ConditionModel cModel, DocType docType);
	
	public String getPhysicalPath(String logicalPath, String separator);
	/**
	 * 根据逻辑路径解析成文字路径。
	 * @param logicalPath  ：逻辑路径
	 * @param separator	：返回值的分隔符
	 * @param needSub1 ：   解析逻辑路径的时候是否需要将逻辑逻辑减1.
	 * @param beginIndex : 从逻辑路径的第几个坐标开始解析
	 * @return
	 */
	public  String getPhysicalPathDetail(String logicalPath, String separator,boolean needSub1,int beginIndex);
	/**
	 * 综合查询(归档类)
	 */
	public List<DocResource> iSearchPiged(ConditionModel cModel, DocType docType);
	
	public ContentTypeManager getContentTypeManager();
	
	/**
	 * 根据id串得到多个docResource
	 */
	public List<DocResource> getDocsByIds(String ids);
	
	/**
	 * 判断一个人是否正在查看别人（个人）借阅给自己的文档
	 */
	public boolean isViewPerlBorrowDoc(long memberId, long docResId);
	
	public DocResourceDao getDocResourceDao();
	
	/**
	 * 找到某个父文档夹的所有一级子节点
	 */
	public List<DocResource> getAllFirstChildren(long parentId);
	
	/**
	 * 记录转发协同、邮件的日志
	 */
	public void logForward(String isMail, Long docResourceId);
	
	/**
	 * 判断文档是否锁定状态
	 * @see #getLockMsg(Long, Long) 
	 * @deprecated	废弃，应用锁和并发锁区分之后，此方法无法体现这种差别
	 */
	public boolean lockState(long docid, boolean locked);
	
	/**
	 * 根据文档的外键 id 删除文档,并记录文档日志
	 * @param resourceIds 外键ids 
	 * @param user 操作用户
	 * @throws DocException
	 */
	public void deleteDocByResources(List<Long> resourceIds,User user) throws DocException;
	/**
	 * 根据文档Id查找下级所有文档和文档夹
	 * @param id
	 * @return
	 */
	public List<DocResource> findSubFolderDocs(Long id);
	/**
	 * 根据libId和类型查找个人库中的文档
	 */
	public DocResource getDocByType(long libId,long type);
	
	public boolean checkDocResourceIsSystem(String typeId ,String docResId) throws DocException;
	/**
	 * ajax记录操作日志
	 * fileid文档的Id
	 * logType记录的类型（包含打印下载）
	 */
	public void recoidopertionLog(String fileid , String logType) ;
	public void recoidopertionLog(String fileid, String logType, boolean history);
	
	/**
	 * 通过文档的id获得sourceId
	 */
	public Long getDocResSourceId(Long docResId);
	/**
	 * 保存排序后的结果
	 * @param docList需要排序的文档
	 * @param frOrderList对应的顺序，就是从数据库中取出的值，并已有了一定顺序
	 */
	public void saveOrder(List<DocResource> docList , List<Integer> frOrderList);

	/** 取得当前表中的最小 fr_order */
	public int getMinOrder(Long parentId);
	
	/** 取得当前表中的最大 fr_order  */
	public int getMaxOrder(Long parentId);
	
	/**
	 * 协同归档时，判断此协同在同一文档夹下是否已经被归档过
	 * @param docResId
	 * @param appEnumKey
	 * @param sourceId
	 * @return
	 */
	public boolean judgeSamePigeonhole(Long docResId, Integer appEnumKey, String sourceId);

	/**
	 * 获取文件的正文内容，以便用户在文档中心点击文件时，可以直接查看其内容<br>
	 * @param fileId
	 * @see com.seeyon.v3x.doc.controller.DocController#docOpenBody
	 */
	public String getTextContent(Long fileId);

	/**
	 * 根据文档集合获取排序页的显示列表内容
	 */
	public List<DocSortProperty> getDocSortTable(List<DocResource> docs);

	/**
	 * 对文档的4种排序统一成一个方法
	 * @param docResId : 需要排序的文档的主键id
	 * @param sortType:操作类型，即 upwards（上移） 、downwards（下移）、top（置顶）、end（末页）
	 */
	public boolean sort(Long docResId,String sortType);

	/**
	 * 判断文档是否需要排序
	 * 在上移或者置顶时判断在此文档之前的文档是否存在
	 * 在下移或者末页的判断在此文档之后的文档是否存在
	 */
	public boolean isNeedSort(Long docResId,String sortType);

	/**
	 * 文档归档后更新状态
	 * @param ids
	 */
	public void updateDocResourceAfterPingHole(List<Long> ids);
   
	/**
     * 判断文档是否被归档
     * true 是没有归档 
     * false是已经归档
     * null说明该文件已经被删除
     * @param id
     * @return
     */
	public Boolean hasPingHole(Long id) ;
	/**
	 * 判断该目录下文档的排序号是否合理
	 * @param parentId
	 */
	public void checkOrder(Long parentId);
	
	/**
	 * 判断文档是否锁定
	 * @param docId
	 * @deprecated	废弃，区分应用锁和并发操作锁之后，还需返回不同的提示消息
	 * @see #getLockMsg(Long, Long)
	 */
	public boolean getCheckStatus(Long docId);
	
	public DocResource getDocResBySourceId(long sourceId);
	
	/**
	 * 判断某个用户对某个DocResource记录是否拥有修改权限
	 * @param dr
	 * @param userId
	 * @param orgIds
	 * @return
	 */
	public boolean hasEditPermission(DocResource dr, Long userId, String orgIds);
	/**
	 * 判断某个用户对某个DocResource记录是否拥有下载权限
	 * @param dr
	 * @param userId
	 * @param orgIds
	 * @return
	 */
	public boolean hasDownloadPermission(DocResource dr, Long userId, String orgIds);
	public void setFolderVersionEnabled(DocResource drs, boolean fve, int editScopeAll, Long userId);

	/**
	 * 判断当前文档夹层级数是否已经超过上限
	 * @param drs	文档
	 */
	public boolean deeperThanLimit(DocResource drs);
	
	/**
	 * 获取通过xml文件配置的文件夹层级数上限
	 * @return	文件夹层级数上限，比如：10
	 */
	public int getFolderLevelLimit();
	
	/**
	 * 获取文档<b>单个</b>属性查询所得的结果集
	 * @param sdqm	单个查询值模型
	 * @param parentFrId	父级文档夹ID
	 * @param docLibType	文档库类型
	 * @return	查询所得的分页结果集
	 */
	public List<DocResource> getSimpleQueryResult(SimpleDocQueryModel sdqm, Long parentFrId, Byte docLibType,String... type);
	
	/**
	 * 获取文档<b>多个属性组合</b>高级查询所得的结果集
	 * @param dsm	组合查询值模型
	 * @param parentFrId	父级文档夹ID
	 * @param docLibType	文档库类型
	 * @return	查询所得的分页结果集
	 */
	public List<DocResource> getAdvancedQueryResult(DocSearchModel dsm, Long parentFrId, Byte docLibType,String... type);
	
	/**
	 * 通过文档类型和parentId查找文档
	 */
	public List<DocResource> findDocByType(Long parentId, Long type) throws DocException;
	
	/**
	 * 获取文档的全文检索信息，增加此接口，便于某些调用处减少一条无谓、重复sql
	 * @param dr	文档信息
	 */
	public IndexInfo getIndexInfo(DocResource dr) throws DocException;
	
	/**
	 * 获取某一文件夹下面，用户有权访问的指定类型(比如jpeg、gif或png等格式图片)的文件列表<br>
	 * 图片对应的mimetypeId参见{@link com.seeyon.v3x.doc.util.Constants}：
	 * <pre>
	 * public static final long FORMAT_TYPE_ID_UPLOAD_JPG = 117L;
	 * public static final long FORMAT_TYPE_ID_UPLOAD_PNG = 112L;
	 * public static final long FORMAT_TYPE_ID_UPLOAD_GIF = 109L;
	 * </pre>
	 * @param folderId	文件夹ID
	 * @param userId	当前用户ID
	 * @param docTypes	所要获取的文件类型，其值常量定义可参照{@link com.seeyon.v3x.doc.util.Constants}
	 * 
	 */
	public List<DocResource> getDocsByTypes(Long folderId, Long userId, long...docTypes);

	/**
	 * 获取项目某一或全部阶段下的最新文档
	 * @param projectId		项目ID
	 * @param phaseId		项目阶段ID
	 * @param orgids		当前用户的所有组织ID
	 * @param hasAcl        顶级目录的权限
	 * @return	最新文档
	 */
	public List<FolderItemDoc> getLatestDocsOfProject(Long projectId, Long phaseId, String orgids, boolean hasAcl) throws DocException;
	
	/**
	 * 根据查询条件获取项目某一或全部阶段下的最新文档
	 * @param projectId		项目ID
	 * @param phaseId		项目阶段ID
	 * @param orgids		当前用户的所有组织ID
	 * @param hasAcl        顶级目录的权限
	 * @return	最新文档
	 */
	public List<FolderItemDoc> getLatestDocsOfProjectByCondition(String conditino,Long projectId, Long phaseId,Map<String,String> paramMap, String orgids, boolean hasAcl) throws DocException;
	
	/**
	 * 根据在线编辑文件的ID获取文档信息
	 * @param bodyContent 文件的ID--对应docbody的content
	 * @return
	 */
	public DocResource getDocByFileId(String bodyContent);
	
	/**
	 * 当用户对文档进行编辑、重命名等操作时，加上并发锁，避免其他用户同时修改同一文档<br>
	 * 这个并发锁不同于在文档列表中对文档加上的持久化、应用锁(不依附于任何操作)<br>
	 * TODO 替换操作暂不加同步锁，待后续补上
	 * @param docResourceId		文档ID
	 * @param userId			用户ID
	 */
	public void lockWhenAct(Long docResourceId, Long userId);
	
	/**
	 * {@link #lockWhenAct(Long, Long)}简化版
	 * @param docResourceId		文档ID
	 */
	public void lockWhenAct(Long docResourceId);
	
	/**
	 * 在用户完成对文档的编辑等操作之后，解除并发锁
	 * @param docResourceId		文档ID
	 */
	public void unLockAfterAct(Long docResourceId);
	
	/**
	 * <pre>
	 * 对当前文档进行锁状态检查，确定当前用户是否能够进行相关操作，根据检查结果返回对应的提示消息。
	 * 存在如下两种锁：
	 * 1）文档应用锁
	 * 这种锁不依赖于具体的操作（比如编辑文档）。
	 * 其操作方式一般是持有对文档全部权限的人员，直接选中文档并进行锁定操作，避免其他用户对其进行编辑或删除操作。
	 * 2）并发操作锁
	 * 这种锁依赖于于具体的操作（比如编辑文档）。
	 * 当用户编辑文档时，进行加锁，完成编辑操作之后即自动解锁。
	 * 对异常登出的情况，通过登录时间变化进行解锁与否的校验。
	 * 
	 * 这两种锁互斥，也即一个文档只可能存在一种锁定状态。
	 * 比如被应用锁定时，不能进行编辑、删除操作，也不可能加上并发操作锁。反之亦然。
	 * </pre>
	 * @param docResId	文档ID
	 * @param userId	当前用户ID
	 * @return	返回锁状态的检查结果，根据锁状态的不同类型，返回对应的提示信息
	 */
	public String getLockMsg(Long docResId, Long userId);
	
	/**
	 * {@link #getLockMsg(Long, Long)}方法的简化版
	 * @param docResId	文档ID
	 * @see #getLockMsg(Long, Long)
	 * @see #isDocAppUnlocked(Long, Long)
	 */
	public String getLockMsg(Long docResId);
	
	/**
	 * {@link #getLockMsg(Long, Long)}方法的加强版，返回提示信息和锁状态类型
	 * @param docResId	文档ID
	 * @param userId	当前用户ID
	 * @see #getLockMsg(Long, Long)
	 */
	public String[] getLockMsgAndStatus(Long docResId, Long userId);
	
	/**
	 * {@link #getLockMsg(Long, Long)}方法的Sql减少一条版本
	 * @param dr		文档
	 * @param userId	当前用户ID
	 */
	public String[] getLockMsgAndStatus(DocResource dr, Long userId);
	
	/**
	 * {@link #getLockMsgAndStatus(Long, Long)}方法的简化版
	 * @param docResId	文档ID
	 * @see #getLockMsgAndStatus(Long, Long)
	 */
	public String[] getLockMsgAndStatus(Long docResId);
	
	/**
	 * 对文档进行应用解锁时，判定当前文档是否已被他人进行解锁
	 * @param docResId	文档ID
	 * @param userId	当前用户ID
	 */
	public boolean isDocAppUnlocked(Long docResId, Long userId);

	/**
	 * 单位管理员在修改项目负责人的时候，需要同步调整项目负责人的权限
	 * @param projectId		项目ID	
	 * @param oldManagers	旧的项目负责人ID集合
	 * @param newManagers	新的项目负责人ID集合
	 */
	public void updateProjectManagerAuth4ProjectFolder(Long projectId, List<Long> oldManagers, List<Long> newManagers);
	
	/**
	 * 信息报送归档
	 * @param summary
	 * @param hasAttachments
	 * @return
	 */
	public Long pigeonholeInfo(InfoSummaryCAP summary, boolean hasAttachments) throws DocException;
	
	/**
	 * 信息报送统计归档
	 * @param infoStat
	 * @param archiveId
	 * @param hasAttachments
	 * @return
	 * @throws DocException
	 */
	public Long pigeonholeInfoStat(InfoStatCAP infoStat, Long archiveId, boolean hasAttachments) throws DocException;
	/**
	 *成发集团项目 更新文档的密级 
	 */
	public void updateDocSecretLevel(Long docResourceId,Integer secretLevel) throws DocException;
	
}