package com.seeyon.v3x.space.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.utils.UUIDLong;

/**
 * The persistent class for the fragment database table.
 * 
 * @author BEA Workshop Studio
 */
public class Fragment extends BaseModel implements Serializable {
	private static final long serialVersionUID = -5486289515840055040L;
	
    public static String ROW_PROPERTY_NAME = "row";

    public static String COLUMN_PROPERTY_NAME = "column";
	
	/**
	 * Fragment类型
	 */
	public enum Type{
		portlet,
		layout
	}
	
	public enum State{
		newFragment,
		removeFragment,
		noChangedFragment
	}
	
	private java.lang.Long parentId;
	private java.lang.Long pageId;
	private java.lang.String name;
	private java.lang.String title;
	private java.lang.String shortTitle;
	private java.lang.String type;
	private java.lang.String skin;
	private java.lang.String decorator;
	private java.lang.String state;
	private java.lang.String pmode;
	private java.lang.Integer layoutRow;
	private java.lang.Integer layoutColumn;
	private java.lang.String layoutSizes;
	private java.lang.Double layoutX = -1D;
	private java.lang.Double layoutY = -1D;
	private java.lang.Double layoutZ = -1D;
	private java.lang.Double layoutWidth = -1D;
	private java.lang.Double layoutHeight = -1D;
	private java.lang.String extPropName1;
	private java.lang.String extPropValue1;
	private java.lang.String extPropName2;
	private java.lang.String extPropValue2;
	private java.lang.String ownerPrincipal;
	
	private List<Fragment> childFragments = new ArrayList<Fragment>();

	public Fragment() {
		
	}
	
	/**
	 * 不拷贝：id, parentId, pageId,childFragments
	 * @param f
	 */
	public Fragment(Fragment f) {
		this.name = f.getName();
		this.title = f.getTitle();
		this.shortTitle = f.getShortTitle();
		this.type = f.getType();
		this.skin = f.getSkin();
		this.decorator = f.getDecorator();
		this.state = f.getState();
		this.pmode = f.getPmode();
		this.layoutRow = f.getLayoutRow();
		this.layoutColumn = f.getLayoutColumn();
		this.layoutSizes = f.getLayoutSizes();
		this.layoutX = f.getLayoutX();
		this.layoutY = f.getLayoutY();
		this.layoutZ = f.getLayoutZ();
		this.layoutWidth = f.getLayoutWidth();
		this.layoutHeight = f.getLayoutHeight();
		this.extPropName1 = f.getExtPropName1();
		this.extPropValue1 = f.getExtPropValue1();
		this.extPropName2 = f.getExtPropName2();
		this.extPropValue2 = f.getExtPropValue2();
		this.ownerPrincipal = f.getOwnerPrincipal();
	}

	public void setIdIfNew() {
		if (isNew()) {
			setId(UUIDLong.absLongUUID());
		}
	}

	public java.lang.Long getParentId() {
		return parentId;
	}

	public void setParentId(java.lang.Long parentId) {
		this.parentId = parentId;
	}

	public java.lang.Long getPageId() {
		return pageId;
	}

	public void setPageId(java.lang.Long pageId) {
		this.pageId = pageId;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getTitle() {
		return title;
	}

	public void setTitle(java.lang.String title) {
		this.title = title;
	}

	public java.lang.String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(java.lang.String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public java.lang.String getType() {
		return type;
	}

	public void setType(java.lang.String type) {
		this.type = type;
	}

	public java.lang.String getSkin() {
		return skin;
	}

	public void setSkin(java.lang.String skin) {
		this.skin = skin;
	}

	public java.lang.String getDecorator() {
		return decorator;
	}

	public void setDecorator(java.lang.String decorator) {
		this.decorator = decorator;
	}

	public java.lang.String getState() {
		return state;
	}

	public void setState(java.lang.String state) {
		this.state = state;
	}

	public java.lang.String getPmode() {
		return pmode;
	}

	public void setPmode(java.lang.String pmode) {
		this.pmode = pmode;
	}

	public java.lang.Integer getLayoutRow() {
		return layoutRow;
	}

	public void setLayoutRow(java.lang.Integer layoutRow) {
		this.layoutRow = layoutRow;
	}

	public java.lang.Integer getLayoutColumn() {
		return layoutColumn;
	}

	public void setLayoutColumn(java.lang.Integer layoutColumn) {
		this.layoutColumn = layoutColumn;
	}

	public java.lang.String getLayoutSizes() {
		return layoutSizes;
	}

	public void setLayoutSizes(java.lang.String layoutSizes) {
		this.layoutSizes = layoutSizes;
	}

	public java.lang.Double getLayoutX() {
		return layoutX;
	}

	public void setLayoutX(java.lang.Double layoutX) {
		this.layoutX = layoutX;
	}

	public java.lang.Double getLayoutY() {
		return layoutY;
	}

	public void setLayoutY(java.lang.Double layoutY) {
		this.layoutY = layoutY;
	}

	public java.lang.Double getLayoutZ() {
		return layoutZ;
	}

	public void setLayoutZ(java.lang.Double layoutZ) {
		this.layoutZ = layoutZ;
	}

	public java.lang.Double getLayoutWidth() {
		return layoutWidth;
	}

	public void setLayoutWidth(java.lang.Double layoutWidth) {
		this.layoutWidth = layoutWidth;
	}

	public java.lang.Double getLayoutHeight() {
		return layoutHeight;
	}

	public void setLayoutHeight(java.lang.Double layoutHeight) {
		this.layoutHeight = layoutHeight;
	}

	public java.lang.String getExtPropName1() {
		return extPropName1;
	}

	public void setExtPropName1(java.lang.String extPropName1) {
		this.extPropName1 = extPropName1;
	}

	public java.lang.String getExtPropValue1() {
		return extPropValue1;
	}

	public void setExtPropValue1(java.lang.String extPropValue1) {
		this.extPropValue1 = extPropValue1;
	}

	public java.lang.String getExtPropName2() {
		return extPropName2;
	}

	public void setExtPropName2(java.lang.String extPropName2) {
		this.extPropName2 = extPropName2;
	}

	public java.lang.String getExtPropValue2() {
		return extPropValue2;
	}

	public void setExtPropValue2(java.lang.String extPropValue2) {
		this.extPropValue2 = extPropValue2;
	}

	public java.lang.String getOwnerPrincipal() {
		return ownerPrincipal;
	}

	public void setOwnerPrincipal(java.lang.String ownerPrincipal) {
		this.ownerPrincipal = ownerPrincipal;
	}

	public void setType(Fragment.Type type) {
		this.type = type.name();
	}

	public List<Fragment> getChildFragments() {
		return childFragments;
	}

	public void setChildFragments(List<Fragment> childFragments) {
		this.childFragments = childFragments;
	}	
}