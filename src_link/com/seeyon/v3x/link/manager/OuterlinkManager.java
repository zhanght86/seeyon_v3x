package com.seeyon.v3x.link.manager;

import java.util.List;

import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.link.domain.LinkAcl;
import com.seeyon.v3x.link.domain.LinkCategory;
import com.seeyon.v3x.link.domain.LinkOption;
import com.seeyon.v3x.link.domain.LinkOptionValue;
import com.seeyon.v3x.link.domain.LinkSpace;
import com.seeyon.v3x.link.domain.LinkSpaceAcl;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.webmodel.WebLinkOptionValueImportResultVO;

public interface OuterlinkManager {
	/**
	 * 添加一个常用链接
	 * @param name
	 * @param orderNum
	 * @param description
	 * @param url
	 * @param method
	 * @param image
	 * @param category_id
	 */
	public long addLinkSystem(String name, int orderNum, String description, String url, boolean needContentCheck, String contentForCheck, boolean sameRegion, String agentUrl, String method , String image ,
			long categoryId, boolean allowedAsSpaceNavigation, boolean allowedAsSection)throws Exception;
	
	/**
	 * 更新
	 * @param linkSystem
	 */
	public void updateLinkSystem(String name, int orderNum, String description, String url, boolean needContentCheck, String contentForCheck, boolean sameRegion, String agentUrl, String method , String image ,long categoryId,
			boolean allowedAsSpace, boolean allowedAsSection, LinkSystem linkSystem)throws Exception;
	/**
	 * 对一个常用链接进行授权
	 * @param outLinkId
	 * @param userType
	 * @param userId
	 */
	public void addLinkAcl(long linkSystemId, String userType,long... userId)throws Exception;
	
	/**
	 * 按类别进行授权
	 * @param categoryId
	 */
	public void addLinkAclByCategory(long categoryId,String userType,long... userId);
	/**
	 * 添加一个类别
	 * @param name
	 * @throws Exception
	 */
	public long addCategory(String name)throws Exception;
	/**
	 * 删除一个类别
	 * @param theIds
	 */
	public void deleteCategory(String theIds);
	/**
	 * 根据ID获取一个类别
	 * @param categoryId
	 * @return
	 */
	public LinkCategory getCategoryById(long categoryId);
	/**
	 * 更新一个类别
	 * @param category
	 */
	public void updateCategory(LinkCategory category);
	
	public void validateCategory(String name,long id)throws Exception;
	/**
	 * 记录用户的高级设置
	 * @param paramName
	 * @param sign
	 * @param paramValue
	 * @param isPwd
	 */
	public void addLinkOption(String paramName,String paramSign ,String paramValue, boolean isPwd,int orderNum,long linkSystemId)throws Exception;
	
	/**
	 * 记录用户的输入值
	 * @param outlinkOptionId
	 * @param value
	 */
	public void addLinkOptionValue(long linkOptionId,String value,long userId);

	
	/**
	 * 根据链接ID删除链接
	 * @param linkId
	 */
	public void deleteLinkSystem(List<Long> linkSystemId)throws Exception;
	
//	/**
//	 * 根据ID删除一个类别
//	 * @param category_id
//	 */
//	public void deleteOuterlinkCategoryById(long categoryId);
	
	/**
	 * 删除某人或部门等的浏览该链接的权限
	 * @param id
	 */
	public void deleteLinkAcl(long systemLinkId,byte userType,long... userId)throws Exception;

	
	/**
	 * 根据ID删除一个高级设置
	 * @param optionId
	 */
	public void deleteLinkOptionById(long linkOptionId);
	
	/**
	 * 删除用户对应的设置值
	 * @param UserId
	 */
	public void deleteOptionValueByUserId(long UserId,long linkSystemId);
	
//	/**
//	 * 获取一个链接的完整地址(包括当前用户曾输入过的值)
//	 * @param linkSystemId
//	 * @return
//	 */
//	public String getUrlBySystemId(long linkSystemId);
	
	/**
	 * 根据链接ID获取对应的所有高级设置
	 * @param linkSystemId
	 * @return
	 */
	public List<LinkOption> getlinkOptionBySystemId(long linkSystemId);
	
	
//	/**
//	 * 找出某一个类别下有权限查阅的所有链接
//	 * @param categoryId
//	 * @return
//	 */
//	public List findOutLinkByCategoryId(long categoryId);
	
//	/**
//	 * 通过ID获取一个类别对象
//	 * @param categoryId
//	 * @return
//	 */
//	public OuterlinkCategory findOutCategoryById(long categoryId);
	
	/**
	 * 根据用户ID查询出该用户能查看的所有链接
	 * @param userId
	 * @return
	 */
	public List<LinkSystem> findOutLinkByUserId(long userId)throws Exception;
	
	/**
	 * 获取用户能看到的所有关联系统：不分页
	 */
	public List<LinkSystem> findAllLinkSystemByUser(Long userId) throws Exception;

	/**
	 * 获取用户能看到的所有关联系统：分页
	 */
	public List<LinkSystem> findOutLinkOfCurrentUserByPage() throws Exception;
	
	/**
	 * 获取指定数量的用户能查看的所有链接
	 * @param userId
	 * @param size
	 * @return
	 */
	public List<LinkSystem> findOutLinkBySize(long userId,int size,long categoryId)throws Exception;
	/**
	 * 获取所有 内部关联、外部关联、常用链接
	 * @return List<List<LinkSystem>>: 三个集合，依次是内部关联、外部关联、常用链接
	 * @throws Exception
	 */
	public List<List<LinkSystem>> findAllInnerAndOutter()throws Exception;
	public List<LinkSystem> findAllCommonLinks()throws Exception;
	
	/**
	 * 获取某个关联系统类别下的更多关联系统
	 * @return List<LinkSystem>: 关联系统集合
	 * @throws Exception
	 */
	public List<LinkSystem> findMoreLinks(long categoryId)throws Exception;
	
	/**
	 * 获取所有关联系统(不含常用链接)
	 * 排序： 内、外、自定义
	 */
	public List<List<LinkSystem>> findAllLinksNoCommon() throws Exception;
	
	/**
	 * 获取所有关联系统
	 * 排序： 内、外、常用、自定义
	 */
	public List<List<LinkSystem>> findAllLinkSystems() throws Exception;
	
	/**
	 * 获取所有的类别及对应的链接
	 * @return
	 */
	public List<LinkSystem> findOutLinks()throws Exception;
	
	/**
	 * 根据ID获取一个LinkSystem对象
	 * @param linkSystemId
	 * @return
	 */
	public LinkSystem getLinkSystemById(long linkSystemId);
	
	public List<LinkSystem> getLinkSystemByIds(List<Long> ids);
	
	/**
	 * 根据名称获取一个LinkSystem对象
	 * @param linkSystemName
	 * @return
	 */
	public LinkSystem getLinkSystemByName(String linkSystemName);
	
	/**
	 * 根据linkOption ID获取用户对应的值
	 * @param linkOptionId
	 * @return
	 */
	public List<LinkOptionValue> findOptionValueById(List<Long> linkOptionId,long userId);
	
	public LinkOptionValue  getOptionValueById(long linkOptionId,long userId);
	/**
	 * 获取所有的常用链接
	 * @return
	 */
	public List<LinkSystem> getAllLinkSystem();
	
	/**
	 * 获取所有的类别,按是否是系统类别进行排序
	 * @return
	 */
	public List<LinkCategory> getAllLinkCategory();
	
	public int getMaxLinkSystemOrder(long categoryId);
	
	/**
	 * 根据链接ID获取对应得类别
	 * @param linkSystemId
	 * @return
	 */
	public LinkCategory getLinkCategoryBylinkId(long linkSystemId);
	
	/**
	 * 生成最后的链接
	 * @param linkSystemId
	 * @return
	 */
	public String getFinalUrlBySystemId(long linkSystemId,long userId);
	
	/**
	 * 获取一个类别下的所有关联系统
	 * @param categoryId
	 * @return
	 */
	public List<LinkSystem> getLinkSystemByCategoryId(long categoryId);
	
	public List<LinkAcl> getLinkAclByCategoryId(long categoryId);
	
	/**
	 * 验证url有效性
	 */
	public boolean isValidUrl(String urlString);
	
	/**
	 * 判断是否有同名关联系统
	 */
	public boolean hasSameNameCategory(String name, Long categoryId);
	/**
	 * 是否有同名关联系统
	 * systemId: 新建时直接传 0
	 */
	public boolean hasSameNameSystem(String name, Long categoryId, Long systemId);
	/**
	 * 判断是不是有参数
	 * @param systemId
	 * @return
	 */
	public boolean hasOption(String systemId);
	
	/**
	 * 获取当前用户可以访问的、允许配置为空间导航的内部、外部关联项目
	 * @param userId  当前用户ID
	 */
	public List<LinkSystem> findAllRelatedSystemsAllowedAsSpace(Long userId);
	
	/**
	 * 校验当前用户是否能够继续使用关联系统
	 * @param userId			当前用户ID
	 * @param systemId			关联系统ID
	 * @param systemCategoryId	关联系统所属系统分类ID
	 * @return
	 */
	public boolean canUseTheSystem(Long userId, Long systemId, Long systemCategoryId);
	/**
	 * 删除系统的授权
	 * @param category
	 */
	public void  delLinkAcl(LinkCategory category);
	
	/**
	 * 更新个人关联系统排序
	 * @param linkIds           当前用户关联系统ID集合
	 * @param userId            当前用户ID
	 * @throws Exception
	 */
	public void updateLinkOrder(String[] linkIds,Long userId) throws Exception;
	
	/**
     * 导入关联系统的参数值
     * @param repeat 重复项处理方式
     * @param list 从excel中读取的字符串数据
     */
    public List<WebLinkOptionValueImportResultVO> importLinkOptinValue(String repeat, long linkSystemId, List<List<String>> list);
    
    /**
     * 导出关联系统的参数
     * @param linkOptionList linkOptionList
     */
    public DataRecord exportLinkOptionTemplate(List<LinkOption> linkOptionList);
    
    /**
     * 按人分组统计某个关联系统的参数值,Object[]里的内容：userId, 用户姓名，用户登录名，参数值1，参数值2...
     * @param linkSystemId linkSystemId
     */
    public List<Object[]> getLinkOptionValueStatistics(List<LinkOption> linkOptionList);
    
    /**
     * 删除关联系统参数值
     * @param userIds userIds
     */
    public void deleteParamValues(List<Long> linkOptionIds, List<Long> userIds);
    
    /**
     * 添加扩展空间
     * @param linkSpaceList linkSpaceList
     */
    public void addLinkSpace(List<LinkSpace> linkSpaceList);
    
    /**
     * 添加扩展空间权限
     * @param linkSpaceAclList linkSpaceAclList
     */
    public void addLinkSpaceAcl(List<LinkSpaceAcl> linkSpaceAclList);
    
    /**
     * 根据id得到扩展空间
     * @param linkSpaceId linkSpaceId
     */
    public LinkSpace getLinkSpaceById(long linkSpaceId);
    
    /**
     * 获取当前用户可以访问的扩展空间
     * @param userId  当前用户ID
     */
    public List<LinkSpace> findLinkSpacesCanAccess(Long userId);
    
    /**
     * 校验当前用户是否能够继续使用扩展空间
     * @param userId            当前用户ID
     * @param linkSpaceId         扩展空间ID
     * @return
     */
    public boolean canUseTheLinkSpace(Long userId, Long linkSpaceId);
}
