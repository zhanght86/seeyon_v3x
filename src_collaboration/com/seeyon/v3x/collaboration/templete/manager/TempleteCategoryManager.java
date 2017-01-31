/**
 * 
 */
package com.seeyon.v3x.collaboration.templete.manager;

import java.util.List;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.formbizconfig.webmodel.TempleteCategorysWebModel;

/**
 * 采用单列模式，将模板分类缓存
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-3-27
 */
public interface TempleteCategoryManager {
	/**
	 * 保存分类
	 * 
	 * @param templeteCategory
	 */
	public void save(TempleteCategory templeteCategory);

	/**
	 * 读取指定单位下的所有分类
	 * 
	 * @param orgAccountId
	 *            单位id
	 * @param type
	 *            类型
	 * @return
	 */
	public List<TempleteCategory> getCategorys(Long orgAccountId, int type);
	   /**
     * 读取指定单位下的所有分类
     * 
     * @param orgAccountId
     *            单位id
     * @param types
     *            类型
     * @return
     */
    public List<TempleteCategory> getCategorys(Long orgAccountId, List<Integer> types);
	
	/**
	 * 快速查找
	 * @param orgAccountId
	 * @param id
	 * @return
	 */
	public TempleteCategory getByAccountAndId(Long orgAccountId,Long id);
	/**
	 * 
	 * @param id
	 * @return
	 */
	public TempleteCategory get(Long id);

	public void update(TempleteCategory templeteCategory);

	public void deleteCategory(long templeteCategoryId)
			throws BusinessException;

	/**
	 * 检测是否在同一个分类下存在重名
	 * 
	 * @param parentId
	 * @param name
	 * @return 符合名字的纪录Id
	 */
	public List<Long> checkName(Long parentId, String name);

	/**
	 * 检测是否在数据库中存在重名
	 * 
	 * @param parentId
	 * @param name
	 * @return 是否重名 重名返回第一个id
	 */
	public Long checkName(String name);
	/**
	 * 检测用户是否是模板管理员，检测标准：
	 * 
	 * 只要任何一个模板分类授权给他了，他就是模板管理员
	 * 
	 * @param memberId
	 * @param accountId
	 * @return
	 */
	public boolean isTempleteManager(Long memberId, Long accountId);
	
	/**
	 * 判断模板分类是否存在
	 * @param templateCategoryId
	 * @return
	 */
	public boolean exist(Long templateCategoryId);
	
	/**
	 * 在用户以指定条件查询获取表单模板之后，解析出与之对应的表单模板所属应用类型集合，以匹配展现
	 * @param accountId   	单位ID
	 * @param searchType	查询条件类型
     * @param textfield 	查询条件值：按照表单模板名称进行查询 or 按照表单模板所属应用类型进行查询
	 * @param tempList		查询所得的表单模板结果
	 * @return 查询所得表单模板所属应用类型集合
	 */
	public TempleteCategorysWebModel getCategorys(Long accountId, String searchType, String textfield, List<Templete> tempList);
	
	/**
	 * 得到用户可以访问的所有模板分类（如果此模板分类下面有用户可以访问的模板，此模板分类用户就可以访问）
	 * @param memberId
	 * @return
	 */
	public List<TempleteCategory> getCategory4User(Long memberId,Integer... category);
}
