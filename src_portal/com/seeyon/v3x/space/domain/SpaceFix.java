package com.seeyon.v3x.space.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.util.EnumUtil;
import com.seeyon.v3x.util.XMLCoder;

/**
 * The persistent class for the v3x_space_fix database table.
 * TODO 没有设置banner
 * @author BEA Workshop Studio
 */
public class SpaceFix extends com.seeyon.v3x.common.domain.BaseModel implements
		Serializable,Comparable<SpaceFix> {
	
	private static final long serialVersionUID = -4871746843169067856L;

	public static enum ExtPropertiesKey {
		banner, //旗帜
		slogan, //口号
		motto, //格言
		allowdefined, //允许自定义， 当值为false是，表示禁止，其他表示允许
		isShowSearch,//是否显示全文检索
	}
	
	private Long entityId;

	private String extAttributes;

	private String spaceName;
	
	private String pagePath;

	/**
	 * 空间类型
	 * 
	 * @see com.seeyon.v3x.space.Constants.SpaceType
	 */
	private Integer type;

	private java.sql.Timestamp updateTime = new java.sql.Timestamp(System.currentTimeMillis());
	
	private Long accountId;
	
	private int state = Constants.SpaceState.normal.ordinal();

	private java.util.List<SpaceSecurity> spaceSecurities;
	
	private boolean first = false;
	
	private Banner spaceBanner = null;
	
	private boolean spaceMenuEnabled = false;
	
	private Long parentId;

	public SpaceFix() {
	}

	public SpaceFix(Constants.SpaceType spaceType, Long entityId, String pagePath, Long accountId) {
		super();
		this.entityId = entityId;
		this.pagePath = pagePath;
		this.accountId = accountId;
		this.type = spaceType.ordinal();
		first = true;
	}
	
	public boolean isSpaceMenuEnabled() {
		return spaceMenuEnabled;
	}

	public void setSpaceMenuEnabled(boolean spaceMenuEnabled) {
		this.spaceMenuEnabled = spaceMenuEnabled;
	}

	public boolean isFirst() {
		return first;
	}

	public Long getEntityId() {
		return this.entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getExtAttributes() {
		return this.extAttributes;
	}

	public void setExtAttributes(String extAttributes) {
		this.extAttributes = extAttributes;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}

	public String getPagePath() {
		return this.pagePath;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public java.sql.Timestamp getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(java.sql.Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 管理员
	 * @return
	 */
	public java.util.List<SpaceSecurity> getSpaceManagements() {
		return getSpaceSecurity(SpaceSecurity.SecurityType.manager);
	}
	public List<SpaceSecurity> getSpaceSecurity(SpaceSecurity.SecurityType type){
		List<SpaceSecurity> spaceManagements = new ArrayList<SpaceSecurity>();
		if(this.spaceSecurities != null){
			for (SpaceSecurity security : spaceSecurities) {
				if(security.getSecurityType() == type.ordinal()){
					spaceManagements.add(security);
				}
			}
		}
		return spaceManagements;
	}
	public java.util.List<SpaceSecurity> getSpaceSecurities() {
		return spaceSecurities;
	}

	/**
	 * 访问者
	 * @return
	 */
	public java.util.List<SpaceSecurity> getSpaceUsers() {
		return getSpaceSecurity(SpaceSecurity.SecurityType.used);
	}

	/**
	 *
	 * @param spaceSecurities 所有权限，包括管理权、使用权
	 */
	public void setSpaceSecurities(java.util.List<SpaceSecurity> spaceSecurities) {
		this.spaceSecurities = spaceSecurities;
	}
	
	private Map<String, Object> extProperties = null;

	/**
	 * 添加一个扩展属性，记得在添加完最后serialExtProperties();
	 * 
	 * <pre>
	 * Affair a = new Affair();
	 * 
	 * a.addExtProperty(&quot;importmentLevel&quot;, 1);
	 * a.addExtProperty(&quot;bodyType&quot;, &quot;HTML&quot;);
	 * a.serialExtProperties();
	 * </pre>
	 * 
	 * @param key
	 * @param value
	 */
	public void addExtProperty(String key, Object value) {
		if(key == null || value == null){
			return;
		}
		
		if (extProperties == null) {
			extProperties = new HashMap<String, Object>();
		}

		extProperties.put(key, value);
	}
	
	public void addExtProperty(ExtPropertiesKey key, Object value){
		if(key != null && value != null){
			this.addExtProperty(key.name(), value);
		}	
	}
	
	/**
	 * 序列化扩展属性成XML，并把Map清空
	 *
	 */
	public void serialExtProperties(){
		if(extProperties != null){
			this.setExtAttributes(XMLCoder.encoder(extProperties));
			
			extProperties.clear();
			extProperties = null;
		}
	}
	
	/**
	 * 获取扩展属性
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getExtProperties(){
		if(extProperties != null){
			return extProperties;
		}
		
		if(StringUtils.isNotBlank(this.extAttributes)){
			extProperties = (Map<String, Object>)XMLCoder.decoder(this.extAttributes);
		}
		else if(extProperties == null){
			extProperties = new HashMap<String, Object>();
		}
		
		return extProperties;
	}
	
	public Object getExtProperty(String key){
		return this.getExtProperties().get(key);
	}
	
	public Object getExtProperty(ExtPropertiesKey key){
		return this.getExtProperty(key.name());
	}
	
	public void setBanner(String banner){
		this.addExtProperty(ExtPropertiesKey.banner, banner);
	}
	
	public void setSlogan(String slogan){
		this.addExtProperty(ExtPropertiesKey.slogan, slogan);
	}

	public void isShowSearch(String isShowSearch){
		this.addExtProperty(ExtPropertiesKey.isShowSearch, isShowSearch);
	}
	public String getShowSearch(){
		return (String)this.getExtProperty(ExtPropertiesKey.isShowSearch);
	}
	public String getBanner() {
		return (String)this.getExtProperty(ExtPropertiesKey.banner);
	}

	public String getSlogan() {
		return Constants.getValueOfKey((String)this.getExtProperty(ExtPropertiesKey.slogan));
	}
	
	public String getMotto() {
		return (String)this.getExtProperty(ExtPropertiesKey.motto);
	}

	public void setMotto(String motto) {
		if(motto != null){
			this.addExtProperty(ExtPropertiesKey.motto, motto);
		}
	}
	
	public boolean isAllowdefined(){
		Boolean b = (Boolean)this.getExtProperty(ExtPropertiesKey.allowdefined);
		return b == null ? true : b.booleanValue();
	}
	
	public void setAllowdefined(boolean allowdefined){
		this.addExtProperty(ExtPropertiesKey.allowdefined, allowdefined);
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((entityId == null) ? 0 : entityId.hashCode());
		result = PRIME * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		final SpaceFix other = (SpaceFix) obj;
		if (entityId == null) {
			if (other.entityId != null) return false;
		}
		else if (!entityId.equals(other.entityId)) return false;
		if (type == null) {
			if (other.type != null) return false;
		}
		else if (!type.equals(other.type)) return false;
		return true;
	}
	public void init(){
		if(spaceBanner == null){
			spaceBanner = makeBanner();
		}
	}
	public Banner getSpaceBanner() {
		return spaceBanner;
	}
	private Banner makeBanner(){
		Banner b =null;
		String slogan = this.getSlogan();
		if(slogan != null){
			b = new Banner();
			b.setBanner(this.getBanner());
			b.setSlogan(slogan);
			if(this.getShowSearch() != null){
				b.setShowSearch(this.getShowSearch());
			}else{
				b.setShowSearch("1");
			}
		}
		return b;
	}

	public void setSpaceBanner(Banner spaceBanner) {
		this.spaceBanner = spaceBanner;
		if(spaceBanner == null){
			spaceBanner = new Banner();
		}
		this.setBanner(spaceBanner.getBanner());
		this.setSlogan(spaceBanner.getSlogan());
		this.isShowSearch(spaceBanner.getShowSearch());
	}
	
	/**
	 * 排序规则：
	 * 系统空间排上，然后按照空间排序
	 */
	public int compareTo(SpaceFix fix) {
		if(fix == null) return 1;
		SpaceType type = EnumUtil.getEnumByOrdinal(SpaceType.class, this.type);
		SpaceType type1 = EnumUtil.getEnumByOrdinal(SpaceType.class, fix.getType());
		boolean isSystem = Constants.isSystem(type);
		boolean isSystem1 = Constants.isSystem(type1);
		
		if(isSystem && isSystem1){
			return this.type.compareTo(fix.getType());
		}else if(isSystem){
			return -1;
		}else if(isSystem1){
			return 1;
		}else{
			return this.updateTime.compareTo(fix.updateTime);
		}
	}
	
	public Long getParentId() {
		return parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
}