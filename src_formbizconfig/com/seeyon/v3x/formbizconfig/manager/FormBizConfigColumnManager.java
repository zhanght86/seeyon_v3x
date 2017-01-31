package com.seeyon.v3x.formbizconfig.manager;

import java.util.List;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigColumn;
import com.seeyon.v3x.formbizconfig.webmodel.ColumnNodeModel;

/**
 * 
 * 专注于栏目挂接项相关的业务逻辑处理，减轻{@link FormBizConfigManagerImpl}的压力并将不易单元测试的部分抽离到此中来
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-04-02
 */
public interface FormBizConfigColumnManager {
	/**
	 * 获取表单业务配置所对应的全部栏目挂接项，过滤其中无权使用的表单查询或统计模板
	 */
	public List<FormBizConfigColumn> getSelectedColumns(List<Long> domainIds, Long bizConfigId) throws DataDefineException, BusinessException;
	
	/**
	 * 用户在创建及修改表单业务配置时，选择了若干表单模板之后<br>
	 * 解析出这些<b>表单模板</b>对应<b>表单</b>的<b>表单查询</b>和<b>表单统计</b>模板信息<br>
	 * 前端页面通过AJAX调用此方法获取结果，据此在栏目挂接项可选项树状结构中的表单查询和表单统计节点下面加上子节点<br>
	 * 也用于用户在查看或修改业务配置时，根据业务配置对应的表单模板，按照默认用户权限显示栏目可选项<br>
	 * @param formTempIds 选中的表单模板ID，以","拼接在一起的字符串
	 * @param from 		  来自普通用户(进行权限判断)还是表单管理员(不进行权限判断)
	 * @return List&lt;ColumnNodeModel&gt; 表单查询和统计模板信息结果集合<br>
	 * @see com.seeyon.v3x.formbizconfig.controller.FormBizConfigController#showAllColumns
	 */
	public List<ColumnNodeModel> getQueryAndStatisticInfo(String formTempIds, String from) throws Exception;
	
	/**
	 * 用户在首页栏目中使用表单查询或表单统计模板时进行权限校验，配合AJAX调用
	 * @param formId  			 表单ID
	 * @param queryOrReportName  表单查询或统计模板名称
	 * @param type    			 属于表单查询(1)还是表单统计(2)
	 * @return 					 是否具备使用权限
	 */
	public boolean validateAuth4QueryOrReport(Long formId, String queryOrReportName, String type) throws DataDefineException, BusinessException;
	
	/**
	 * 复制旧有业务配置对应的栏目挂接项到新业务配置中
	 * @param orginalBizConfigId   旧有业务配置ID
	 * @param newBizConfigId	   新的业务配置ID
	 */
	public void cloneColumns(Long orginalBizConfigId, Long newBizConfigId) throws CloneNotSupportedException;
	
	/**
	 * 通过获取的栏目挂接项信息生成对应的栏目挂接项记录
	 * @param idAndCategorysColumns:<br>
	 * 		  如果不是表单查询和表单统计下的子节点，为其所属类型category，比如"1"、"11"、"3"、"6"等<br>
	 * 		  如果是表单查询和表单统计下的子节点，其格式为："模板ID_所属类型"，比如"-4521441272810223816_3"或"-7469240748318962637_4"<br>
	 * @param columnNames  栏目挂接项名称数组
	 * @param bizConfigId  表单业务配置ID
	 */
	public void saveColumns(String[] idAndCategorysColumns, String[] columnNames, Long bizConfigId);
	
	/**
	 * 在进行表单查询和表单统计操作时，判断对应的查询或统计模板是否已被业务配置创建者从已选项中去除
	 * @param bizConfigId       业务配置ID
	 * @param formId			该模板对应的表单ID
	 * @param queryOrReportName 该模板名称
	 */
	public boolean isQueryOrReportColumnExist(Long bizConfigId, Long formId, String queryOrReportName);
	
	/**
	 * 删除表单业务配置对应的所有栏目挂接项
	 */
	public void deleteColumns(Long bizConfigId);

}
