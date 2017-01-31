/**
 * 
 */
package com.seeyon.v3x.space.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 
 *         2010-11-11
 */
public class SpacePage extends BaseModel implements Serializable {

	private static final long serialVersionUID = -4333541879086867150L;

	// fields
	private java.lang.Integer parentId;
	private java.lang.String path;
	private java.lang.String name;
	private java.lang.String version;
	private java.lang.String title;
	private java.lang.String shortTitle;
	private java.lang.Integer isHidden;
	private java.lang.String skin = "orange";
	private java.lang.String defaultLayoutDecorator = "nothing";
	private java.lang.String defaultPortletDecorator = "nothing";
	private java.lang.String subsite;
	private java.lang.String userPrincipal;
	private java.lang.String rolePrincipal;
	private java.lang.String groupPrincipal;
	private java.lang.String mediatype;
	private java.lang.String locale;
	private java.lang.String extAttrName;
	private java.lang.String extAttrValue;
	private java.lang.String ownerPrincipal;
	
	private Fragment rootFragment;
	
	public SpacePage(){
		
	}
	
	/**
	 * 不拷贝：id, path
	 * @param page
	 */
	public SpacePage(SpacePage page) {
		this.parentId = page.getParentId();
		this.path = null;
		this.name = page.getName();
		this.version = page.getVersion();
		this.title = page.getTitle();
		this.shortTitle = page.getShortTitle();
		this.isHidden = page.getIsHidden();
		this.skin = page.getSkin();
		this.defaultLayoutDecorator = page.getDefaultLayoutDecorator();
		this.defaultPortletDecorator = page.getDefaultPortletDecorator();
		this.subsite = page.getSubsite();
		this.userPrincipal = page.getUserPrincipal();
		this.rolePrincipal = page.getRolePrincipal();
		this.groupPrincipal = page.getGroupPrincipal();
		this.mediatype = page.getMediatype();
		this.locale = page.getLocale();
		this.extAttrName = page.getExtAttrName();
		this.extAttrValue = page.getExtAttrValue();
		this.ownerPrincipal = page.getOwnerPrincipal();
	}

	/**
	 * Return the value associated with the column: PARENT_ID
	 */
	public java.lang.Integer getParentId() {
		return parentId;
	}

	/**
	 * Set the value related to the column: PARENT_ID
	 * 
	 * @param parentId
	 *            the PARENT_ID value
	 */
	public void setParentId(java.lang.Integer parentId) {
		this.parentId = parentId;
	}

	/**
	 * Return the value associated with the column: PATH
	 */
	public java.lang.String getPath() {
		return path;
	}

	/**
	 * Set the value related to the column: PATH
	 * 
	 * @param path
	 *            the PATH value
	 */
	public void setPath(java.lang.String path) {
		this.path = path;
	}

	/**
	 * Return the value associated with the column: NAME
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Set the value related to the column: NAME
	 * 
	 * @param name
	 *            the NAME value
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * Return the value associated with the column: VERSION
	 */
	public java.lang.String getVersion() {
		return version;
	}

	/**
	 * Set the value related to the column: VERSION
	 * 
	 * @param version
	 *            the VERSION value
	 */
	public void setVersion(java.lang.String version) {
		this.version = version;
	}

	/**
	 * Return the value associated with the column: TITLE
	 */
	public java.lang.String getTitle() {
		return title;
	}

	/**
	 * Set the value related to the column: TITLE
	 * 
	 * @param title
	 *            the TITLE value
	 */
	public void setTitle(java.lang.String title) {
		this.title = title;
	}

	/**
	 * Return the value associated with the column: SHORT_TITLE
	 */
	public java.lang.String getShortTitle() {
		return shortTitle;
	}

	/**
	 * Set the value related to the column: SHORT_TITLE
	 * 
	 * @param shortTitle
	 *            the SHORT_TITLE value
	 */
	public void setShortTitle(java.lang.String shortTitle) {
		this.shortTitle = shortTitle;
	}

	/**
	 * Return the value associated with the column: IS_HIDDEN
	 */
	public java.lang.Integer getIsHidden() {
		return isHidden;
	}

	/**
	 * Set the value related to the column: IS_HIDDEN
	 * 
	 * @param isHidden
	 *            the IS_HIDDEN value
	 */
	public void setIsHidden(java.lang.Integer isHidden) {
		this.isHidden = isHidden;
	}

	/**
	 * Return the value associated with the column: SKIN
	 */
	public java.lang.String getSkin() {
		return skin;
	}

	/**
	 * Set the value related to the column: SKIN
	 * 
	 * @param skin
	 *            the SKIN value
	 */
	public void setSkin(java.lang.String skin) {
		this.skin = skin;
	}

	/**
	 * Return the value associated with the column: DEFAULT_LAYOUT_DECORATOR
	 */
	public java.lang.String getDefaultLayoutDecorator() {
		return defaultLayoutDecorator;
	}

	/**
	 * Set the value related to the column: DEFAULT_LAYOUT_DECORATOR
	 * 
	 * @param defaultLayoutDecorator
	 *            the DEFAULT_LAYOUT_DECORATOR value
	 */
	public void setDefaultLayoutDecorator(java.lang.String defaultLayoutDecorator) {
		this.defaultLayoutDecorator = defaultLayoutDecorator;
	}

	/**
	 * Return the value associated with the column: DEFAULT_PORTLET_DECORATOR
	 */
	public java.lang.String getDefaultPortletDecorator() {
		return defaultPortletDecorator;
	}

	/**
	 * Set the value related to the column: DEFAULT_PORTLET_DECORATOR
	 * 
	 * @param defaultPortletDecorator
	 *            the DEFAULT_PORTLET_DECORATOR value
	 */
	public void setDefaultPortletDecorator(java.lang.String defaultPortletDecorator) {
		this.defaultPortletDecorator = defaultPortletDecorator;
	}

	/**
	 * Return the value associated with the column: SUBSITE
	 */
	public java.lang.String getSubsite() {
		return subsite;
	}

	/**
	 * Set the value related to the column: SUBSITE
	 * 
	 * @param subsite
	 *            the SUBSITE value
	 */
	public void setSubsite(java.lang.String subsite) {
		this.subsite = subsite;
	}

	/**
	 * Return the value associated with the column: USER_PRINCIPAL
	 */
	public java.lang.String getUserPrincipal() {
		return userPrincipal;
	}

	/**
	 * Set the value related to the column: USER_PRINCIPAL
	 * 
	 * @param userPrincipal
	 *            the USER_PRINCIPAL value
	 */
	public void setUserPrincipal(java.lang.String userPrincipal) {
		this.userPrincipal = userPrincipal;
	}

	/**
	 * Return the value associated with the column: ROLE_PRINCIPAL
	 */
	public java.lang.String getRolePrincipal() {
		return rolePrincipal;
	}

	/**
	 * Set the value related to the column: ROLE_PRINCIPAL
	 * 
	 * @param rolePrincipal
	 *            the ROLE_PRINCIPAL value
	 */
	public void setRolePrincipal(java.lang.String rolePrincipal) {
		this.rolePrincipal = rolePrincipal;
	}

	/**
	 * Return the value associated with the column: GROUP_PRINCIPAL
	 */
	public java.lang.String getGroupPrincipal() {
		return groupPrincipal;
	}

	/**
	 * Set the value related to the column: GROUP_PRINCIPAL
	 * 
	 * @param groupPrincipal
	 *            the GROUP_PRINCIPAL value
	 */
	public void setGroupPrincipal(java.lang.String groupPrincipal) {
		this.groupPrincipal = groupPrincipal;
	}

	/**
	 * Return the value associated with the column: MEDIATYPE
	 */
	public java.lang.String getMediatype() {
		return mediatype;
	}

	/**
	 * Set the value related to the column: MEDIATYPE
	 * 
	 * @param mediatype
	 *            the MEDIATYPE value
	 */
	public void setMediatype(java.lang.String mediatype) {
		this.mediatype = mediatype;
	}

	/**
	 * Return the value associated with the column: LOCALE
	 */
	public java.lang.String getLocale() {
		return locale;
	}

	/**
	 * Set the value related to the column: LOCALE
	 * 
	 * @param locale
	 *            the LOCALE value
	 */
	public void setLocale(java.lang.String locale) {
		this.locale = locale;
	}

	/**
	 * Return the value associated with the column: EXT_ATTR_NAME
	 */
	public java.lang.String getExtAttrName() {
		return extAttrName;
	}

	/**
	 * Set the value related to the column: EXT_ATTR_NAME
	 * 
	 * @param extAttrName
	 *            the EXT_ATTR_NAME value
	 */
	public void setExtAttrName(java.lang.String extAttrName) {
		this.extAttrName = extAttrName;
	}

	/**
	 * Return the value associated with the column: EXT_ATTR_VALUE
	 */
	public java.lang.String getExtAttrValue() {
		return extAttrValue;
	}

	/**
	 * Set the value related to the column: EXT_ATTR_VALUE
	 * 
	 * @param extAttrValue
	 *            the EXT_ATTR_VALUE value
	 */
	public void setExtAttrValue(java.lang.String extAttrValue) {
		this.extAttrValue = extAttrValue;
	}

	/**
	 * Return the value associated with the column: OWNER_PRINCIPAL
	 */
	public java.lang.String getOwnerPrincipal() {
		return ownerPrincipal;
	}

	/**
	 * Set the value related to the column: OWNER_PRINCIPAL
	 * 
	 * @param ownerPrincipal
	 *            the OWNER_PRINCIPAL value
	 */
	public void setOwnerPrincipal(java.lang.String ownerPrincipal) {
		this.ownerPrincipal = ownerPrincipal;
	}
	
	public void setDefaultDecorator(String decorator, Fragment.Type type){
		if(Fragment.Type.layout == type){
			this.setDefaultLayoutDecorator(decorator);
		}
		else if(Fragment.Type.portlet == type){
			this.setDefaultPortletDecorator(decorator);
		}
	}
	
	public Fragment getRootFragment() {
		return rootFragment;
	}

	public void setRootFragment(Fragment rootFragment) {
		this.rootFragment = rootFragment;
	}

}
