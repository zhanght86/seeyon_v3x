package com.seeyon.v3x.doc.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.webmodel.DocLibTableVo;

public interface DocLibManager {
	
	/**
	 * 加载文档库显示栏目、内容类型和文档属性数据到内存（系统初始化时调用）。
	 */
	public void initialize();	
	
	/**
	 * 添加自定义文档库
	 * @param doclib	通过请求信息绑定的bean
	 * @param domainId	当前所在单位id
	 * @param owners	文档库管理员ID集合
	 */
	public long addDocLib(DocLib doclib, Long domainId, List<Long> owners) throws DocException;
	
	/**
	 * 添加单位虚拟库，主要用于新建文档库时设置栏目和内容类型的外键
	 */
	public boolean addVirtualDocLib(long domainId);
	
	/**
	 * 添加个人文档库
	 * @param userId	用户ID
	 */
	public DocLib addDocLib(long userId)throws DocException;
	
	/**
	 * 添加单位系统文档库（创建单位时调用）。
	 * @param domainId 单位id
	 */
	public void addSysDocLibs(long domainId) throws DocException;
	
	/**
	 * 删除单位所有公共文档库（删除单位时调用）。
	 * @param domainId
	 * @throws DocException
	 */
	public void deleteOrgDocLibs(long domainId) throws DocException;
	
	/**
	 * 根据ID删除自定义文档库
	 * @param id		文档库ID
	 */
	public void deleteDocLib(long id) throws DocException;
	
	public void deleteDocLibs(List<Long> ids) throws DocException;
	
	/**
	 * 删除指定用户的文档库，供用户管理模块调用
	 * @param userId	用户ID
	 */
	public void deleteUserDocLib(long userId)throws DocException;
		
	/**
	 * 获取文档库的详细信息
	 * @param id		文档库ID
	 * @return 			文档库对象
	 */
	public DocLib getDocLibById(long id);
	
	/**
	 * 获取文档库的详细信息列表
	 * @param ids docLib的ID列表。
	 * @return
	 */
	public List<DocLib> getDocLibByIds(List<Long> ids);
	/**
	 * 修改一个文档库
	 * @param docLib	文档库对象
	 * @param name		此参数实际并未用到
	 */
	public void modifyDocLib(DocLib docLib,String name)throws DocException;
	
	/**
	 * 修改一个文档库
	 * @param docLib	文档库对象
	 */
	public void modifyDocLib(DocLib docLib) throws DocException;
	
	/**
	 * 对某一个文档库授权
	 * @param docLibId
	 * @param userId
	 */
	public void addDocLibOwners(long docLibId ,long... userId);
	
	/**
	 * 取消某人或一组人对库的操作权利
	 * @param docLibId		数据库ID
	 * @param userId		用户ID
	 */
	public void deleteDocLibOwners(long docLibId);
	
//	public List<DocLibOwner> getDocLibOwnersById(long docLibId);
	
	public void addDocLibOwners(long docLibId,List<Long> userId);
	
	/**
	 * 获取所有文档库列表(不包含个人文档库)。
	 * 单位知识管理员进行文档库管理时调用。
	 * @return List<DocLib>
	 */
	public List<DocLib> getDocLibs(long domainId);
	public List<DocLib> getDocLibsWithoutGroupLib(long domainId);
	public DocLib getGroupDocLib();
	public DocLib getProjectDocLib();
	
	/**
	 * 获取当前用户的所有文档库列表
	 * @param userId 当前用户id
	 * @param domainId 单位id
	 * @return	List中存储了所有文档库
	 */
	public List<DocLib> getDocLibsByUserId(long userId, long domainId);
	
	/**
	 * 获取当前用户未隐藏的库
	 * @param userId 当前用户id
	 * @param domainId 单位id
	 * @return
	 * @throws DocException
	 */
	public List<DocLib> getDocLibsByUserIdNav(long userId, long domainId);
	
	/**
	 * 添加一个库的内容类型
	 * @param docTypeId
	 * @param docLibId
	 */
	public void addDocTypeList(long docLibId,List<List> docTypeId)throws DocException;
	
	/**
	 * 删除一个库的内容类型
	 * @param docLibId
	 * @param docTypeId
	 */
	public void deleteDocTypeList(long docLibId);
	
	/**
	 * 获得当前文档库对应的内容类型(根据orderNumer从小到大默认排序)
	 * @param docLibId		库ID
	 * @return				当前库所有的内容类型
	 */
	public List<DocType> getContentTypes(long docLibId);
	
	/**
	 * 获得文档库内容类型列表（生成新建2级菜单项时调用）
	 * @param docLibId
	 * @return
	 */
	public List<DocType> getContentTypesForNew(long docLibId);
	
	/**
	 * 获得文档库内容类型列表（新建或编辑文档时调用）
	 * @param docLibId
	 * @return List
	 */
	public List<DocType> getContentTypesForDoc(long docLibId);
	
	/**
	 * 获得文档库的有效(未删除)内容类型列表（新建或编辑文档时调用）
	 * @param docLibId
	 * @return List	可能为空，获取结果时需进行校验
	 */
	public List<DocType> getValidContentTypesForDoc(long docLibId);
		
	/**
	 * 设置内容类型显示顺序   待修改
	 * @param list    list中存放了多个list，每个list中存放的是对应的内容类型的ID和其排列序号
	 */
	public void setDocTypeView(List<List> list);

	/**
	 * 根据文档库id查询对应的列表栏目对象集合
	 * @param docLibId 文档库id
	 * @param isDefaultColumn 是否为默认列表栏目
	 * @return List
	 */
	public List<DocMetadataDefinition> getListColumnsByDocLibId(long docLibId, boolean isDefaultColumn);
	
	/**
	 * 根据根据文档库id查询对应的列表栏目对象集合
	 * @param docLibId 文档库id
	 * @return LIst
	 */
	public List<DocMetadataDefinition> getListColumnsByDocLibId(long docLibId);
	
	/**
	 * 根据内容类型详细表ID获取相应的元数据定义
	 * @param detailId				DocTypeDetail.getId()
	 * @return
	 */
	public DocMetadataDefinition getDocMetadataDefByDetailId(long detailId);
	
	/**
	 * 取得默认显示栏目
	 */
	public List<DocMetadataDefinition> getDefaultColumnList();
	
	/**
	 * 设置文档库栏目列表
	 * @param docLibId
	 * @param list					DocTypeDetail集合
	 */
	public void setDocListColumn(long docLibId,List<List> list);
	
	/**
	 * 删除文档库显示栏目
	 * @param listColumnId
	 */
	public void deleteListColumn(long docLibId,long... listColumnId);
	
	/**
	 * 删除某个特定的栏目设置，当DocMetadataDef被删除时候调用
	 */
	public void deleteSpecificColumn(long docMetadataDefId);
	
	/**
	 * 删除某个特定的搜索条件设置，当DocMetadataDef被删除时候调用
	 */
	public void deleteSpecificSearchConfig(long docMetadataDefId);
	
	/**
	 * 设置文档库显示栏目显示顺序
	 * @param docLibId
	 * @param list
	 */
	public void setListColumnOrder(List<List> list);
	
	public void setDefaultListColumnOrder(long docLibId,List<List> list);
	
	public void deleteDocTypeListByTypeId(long docTypeId);
	/**
	 * 判断当前用户是否某个库的owner
	 */
	public boolean isOwnerOfLib(Long userId, Long libId);
	
	/**
	 * 向上移动一个文档库
	 * @param docLibId
	 * @param domainId 单位id
	 */
//	public void moveUpDocLib(long docLibId, long domainId);
	
	/**
	 * 向下移动一个文档库
	 * @param docLibId
	 * @param domainId 单位id
	 */
//	public void moveDownDocLib(long docLibId, long domainId);
	
	public void moveDocLib(long docLibId, long domainId, boolean up);
	
	public void moveDocLib(List<List> list) ;
	/**
	 * 根据用户ID得到个人文档库对象
	 * @param userId
	 * @return
	 */
	public DocLib getOwnerDocLibByUserId(long userId);
	/**
	 * 根据docLibId 得到 owners
	 * @param userId
	 * @return
	 */
	public List<Long> getOwnersByDocLibId(long docLibId);

	
	/**
	 * 将文档库栏目设为默认
	 */
	public String[] setListColumnToDefault(Long docLibId);
	
	/**
	 * 在创建或修改文档库(不可能是个人文档库)时，判断是否存在同名文档库
	 * @param docLibId  新建时传 0
	 * @deprecated	废弃，提示信息不具体，改为{@link #validateDocLibName(String, long)}
	 */
	public boolean hasSameNameDocLib(String name, long docLibId);
	
	/**
	 * 在创建或修改文档库(不可能是个人文档库)时，判断是否存在同名文档库并返回提示信息
	 * @param docLibId  新建时传 0
	 * @return String[] 0-是否存在重名文档库  1-重名文档库具体信息
	 */
	public String[] validateDocLibName(String name, long docLibId);
	
		
	/**
	 * 取消文档库新建
	 */
	public void cancelAdd();
	
	/**
	 * 取得某个单位下某种类型的文档库
	 * 
	 */
	public List<DocLib> getLibsOfAccount(long domainId, byte libType);
	
	/**
	 * 取得某个人的个人文档库
	 * 
	 */
	public DocLib getPersonalLibOfUser(long userId);
	 
	/**
	 * 取单位文档库
	 * @param domainId
	 * @return
	 */
	public DocLib getDeptLibById(long domainId);
	
	// 根据用户ID获取用户能够查阅的文档库（不包含个人文档库）
	public List<DocLib> getCommonDocLibsByUserId(long userId, long domainId);
	
	/**
	 * 获取用户能够查阅的外单位单位文档库和自定义文档库
	 * @param userId   当前登录用户ID
	 * @param domainId 当前登录用户所在单位ID
	 * @return
	 */
	public List<DocLib> getDocLibsFromOtherAccountBySharing(long userId, long domainId);
	
	/**
	 * 得到这个人员的所有的兼职单位的文档库
	 * @return
	 */
	public List<DocLib> getAllPartDocResouces(User user) throws Exception ;
	
	/**
	 * 得到这个人员的所有的兼职单位的文档库
	 * @param type 类型
	 * @return
	 */
	public List<DocLib> getAllPartDocResouces(byte type,User user) throws Exception ;
	/**
	 * 批量取得文档库的管理员
	 * @deprecated 废弃，使用{@link #getDocLibOwnersByIds(List)}
	 */
	public Map<Long, List<Long>> getDocLibOwnersByIds(String docLibIds);
	/**
	 * 批量取得文档库的管理员
	 */
	public Map<Long, List<Long>> getDocLibOwnersByIds(List<Long> docLibIds);
	
	/** 设置文档库成员  */
	public void setLibMember(Long docLibId, Long userId, String userType);

	/** 删除文档库成员  */
	public void deleteLibMember(Long docLibId, String userIds);
	/** 判断是否有可排序方法  */
	public boolean isEmpty();
	
	/**
	 * 获取用户管理的文档库id
	 * @param owner
	 * @return          文档库id列表
	 */
	public List<Long> getLibsByOwner(Long owner);
	
	/**
	 * 集团管理员或单位管理员进入文档中心设置时，获取所能查看的文档库列表
	 * @param isGroup		是否集团管理员进行设置
	 * @param accountId		单位ID
	 * @return
	 */
	public List<DocLib> getDocLibs(boolean isGroup, Long accountId);
	
	/**
	 * 集团管理员或单位管理员进入文档中心设置时，获取所能查看的文档库列表
	 * @param isGroup		是否集团管理员进行设置
	 * @param accountId		单位ID
	 * @param status		所要获取文档库状态类型，包括：启用、停用、全部(不区分启用还是停用)
	 * @return
	 */
	public List<DocLib> getDocLibs(boolean isGroup, Long accountId, byte status);
	
	/**
	 * 将所得的文档库列表转换为前端展现所需的VO集合
	 * @param docLibs	文档库集合
	 * @return
	 */
	public List<DocLibTableVo> getDocLibTableVOs(List<DocLib> docLibs);

	/**
	 * 将选中的、启用中的文档库停用
	 * @param docLibIds	所选中要停用的文档库IDs
	 */
	public void disableDocLibs(String docLibIds);
	
	/**
	 * 将选中的、被停用的文档库重新启用
	 * @param docLibIds	所选中要重新启用的文档库IDs
	 */
	public void enableDocLibs(String docLibIds);
	
	public boolean isDocLibEnabled(Long docLibId);

	/**
	 * 将选定的文档库查询条件恢复为默认
	 * @param libId
	 */
	public String[] setSearchConditions2Default(Long libId);

	/**
	 * 获取默认查询条件
	 * @return
	 */
	public List<DocMetadataDefinition> getDefaultSearchConditions();
	
	/**
	 * 获取公文文档库默认查询条件
	 * @return
	 */
	public List<DocMetadataDefinition> getDefaultEdocSearchConditions();

	/**
	 * 获取文档库对应的查询条件设置记录
	 * @param docLibId	文档库ID
	 * @param docLibType	文档库类型
	 */
	public List<DocMetadataDefinition> getSearchConditions4DocLib(Long docLibId, Byte docLibType);
	
	/**
	 * 保存文档库对应的查询条件设置
	 * @param docLibId	文档库ID
	 * @param selectedSearchConditions	选择的查询条件
	 */
	public void setDocSearchConditions(Long docLibId, List<Long> selectedSearchConditions);

	/**
	 * 获取高级查询选项中可被添加的条件选项结果集
	 * @param selectedConditions	已被选中的下拉框查询条件集合
	 */
	public List<DocMetadataDefinition> getMiscSearchConditions4DocLib(List<DocMetadataDefinition> selectedConditions);
	
	/**
	 * 根据单位ID获取对应单位文档库的名称
	 * @param accountId		单位ID
	 * @return	单位文档库名称
	 * @throws DocException		如果根据单位ID无法查询到有效单位文档库，抛出此异常
	 */
	public String getAccountDocLibName(Long accountId) throws DocException;
}
