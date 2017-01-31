/**
 * 
 */
package com.seeyon.v3x.space.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-8-7
 */
public class PortletEntityProperty extends BaseModel {

	private static final long serialVersionUID = 2157037905254263821L;
	
	/**
	 * 属性名称
	 * 
	 * 注意增加一个属性的时候，需要<code>PropertyName_No_Save_Pattern</code>进行设置
	 */
	public static enum PropertyName{
		spaceType, //空间类型：com.seeyon.v3x.space.Constants.SpaceType
		ownerId, //所属主体的Id 部门、单位采有值
		entityId, //portlet实体Id
		ordinal,//栏目在fragment中的位置
		layoutType, //布局类型 （TwoColumns/ThreeColumns）
		sections, //频道里面包含的栏目
		singleBoardId, //单板块Id：新闻/公告/调查/BBS的栏目Id/表单业务配置ID：对应栏目挂接
		x,
		y,
		width, //宽度
		isNarrow, //是否是窄栏
		panelId,//选中的页签
		spaceId,//空间id
		singlePanel,//
	}
	
	/**
	 * 不需要记录的属性
	 */
	public static final String PropertyName_No_Save_Pattern = "spaceType|ownerId|entityId|layoutType|x|y|width|isNarrow";

	private String entityId;

	private String propertyName;

	private String propertyValue;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

}
