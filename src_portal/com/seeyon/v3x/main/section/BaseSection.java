/**
 * 
 */
package com.seeyon.v3x.main.section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.portal.util.PortalConstants;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-5-14
 */
public abstract class BaseSection implements Comparable<BaseSection> {
	private static Log log = LogFactory.getLog(BaseSection.class);
	
	private String sectionType;//栏目类型：常用栏目、时间管理...

	private String sectionCategory;//栏目类型中的分类：常用、计划...
	
	private String[] spaceTypes = null;
	
	private boolean isRegistrer = true;
	
	private boolean isAllowedNarrow = false;
	
	private String baseName ;//add by dongyj 显示系统默认名称(配置栏目时候显示)
	
	private Integer sortId;
	
	private PortletEntityPropertyManager portletEntityPropertyManager;
	
	private List<SectionProperty> properties;
	
	//private SectionReference[] preferences;
	
	private String resourceBundle;
		
	/**
	 * 是否禁止外部人员访问
	 */
	private boolean isFilterOut = false;
	/**
	 * 由于首页做了页面缓存。有的栏目根本不需要刷新，在这里定义一个变量。标示，切换空间的时候不刷新
	 */
	private int delay = -1;
	
	public boolean isFilterOut() {
		return isFilterOut;
	}

	public void setIsFilterOut(boolean isFilterOut) {
		this.isFilterOut = isFilterOut;
	}
	
	public int getDelay() {
		return delay;
	}
	
	/**
	 * 是否有参数配置<br>
	 * 见section.xml.
	 * @return
	 */
	public boolean hasParam(){
		return CollectionUtils.isNotEmpty(properties);
	}
	/**
	 * 设置显示的延迟时间<br>
	 * 默认 -1 标示按照IE加载顺序显示（通过AJAX），切换空间时刷新<br>
	 *      0 标示只第一次加载（通过AJAX），切换空间时永远不刷新，可以通过其他途径刷新（如：手工、消息）<br>
	 * @param delay 
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	public final void setPortletEntityPropertyManager(PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}

	public BaseSection() {
	}
	
	public String getSectionType() {
		return sectionType;
	}

	public void setSectionType(String sectionType) {
		this.sectionType = sectionType;
	}

	public String getSectionCategory() {
		return sectionCategory;
	}

	public void setSectionCategory(String sectionCategory) {
		this.sectionCategory = sectionCategory;
	}

	/**
	 * 栏目所属空间类型：个人personal、部门department、单位corporation、集团group、自定义空间custom, 不设定，表示不限制
	 * 
	 * 请采用枚举常量BaseSection.SpaceType
	 * 
	 * @return
	 */
	public void setSpaceTypes(String[] spaceTypes){
		this.spaceTypes = spaceTypes;
	}
	
	public String[] getSpaceTypes() {
		return spaceTypes;
	}

	/**
	 * 是否注册到栏目管理器, 默认注册
	 * 
	 * @return
	 */
	public void setIsRegistrer(boolean isRegistrer){
		this.isRegistrer = isRegistrer;
	}
	
	/**
	 * 是否允许放在窄栏目中，默认不允许
	 * 
	 * @param isAllowedNarrow
	 */
	public void setAllowedNarrow(boolean isAllowedNarrow) {
		this.isAllowedNarrow = isAllowedNarrow;
	}

	/**
	 * 初始化方法
	 */
	public void init() {
	}

	/**
	 * 栏目的唯一标示，同时也是Spring Bean定义的Id, 如：
	 * 
	 * <pre>
	 * <code>
	 * ***-manager.xml
	 *   
	 * &lt;bean id=&quot;pendingSection&quot; class=&quot;com.seeyon.v3x.main.section.PendingSection&quot;&gt;
	 *   &lt;property name=&quot;id&quot; ref=&quot;pendingSection&quot; /&gt;
	 * &lt;/bean&gt;
	 * </code>
	 * 
	 * 特别提示：该值作为栏目的标示将会写到数据库中去，故：
	 * 1、不要随意变化
	 * 2、要唯一
	 * 3、必须由数字、字母、下划线构成
	 * </pre>
	 * 
	 * @return 直接返回一个有字符+数字组成的字符串，如：pendingSection
	 */
	public abstract String getId();

	/**
	 * 栏目名称的国际化key，资源文件在/apps_res/v3xmain/js/i18n
	 * 
	 * @param preference Portlet实例的配置参数
	 * @return
	 */
	protected abstract String getName(Map<String, String> preference);

	/**
	 * 总数，如果不需要显示总数，就返回null
	 * 
	 * @param preference Portlet实例的配置参数
	 * @return
	 */
	protected abstract Integer getTotal(Map<String, String> preference);
	
	/**
	 * 取得数量的单位，比如：个、项、条，默认“项”，注意国际化
	 * 
	 * @param preference
	 * @return
	 */
	protected String getTotalUnit(Map<String, String> preference){
		return null;
	}

	/**
	 * 栏目图标，统一放在 /apps_res/v3xmain/images/section下
	 * 
	 * @return 如 /apps_res/v3xmain/images/section/pending.col.gif
	 */
	public abstract String getIcon();

	/**
	 * 在这里发射数据
	 * 
	 * @param preference Portlet实例的配置参数
	 * @return
	 */
	protected abstract BaseSectionTemplete projection(Map<String, String> preference);
	
	/**
	 * 设置栏目的排序号，用在配置页面的栏目选择中
	 * 
	 * @param sortId
	 */
	public void setSortId(Integer sortId){
		this.sortId = sortId;
	}
	
	public Integer getSortId() {
		return sortId;
	}
	
	/**
	 * 是否允许添加-使用该栏目，默认允许，如果需要特别控制，需要重载该方法，当前登录信息从CurrentUser中取
	 * @return
	 */
	public boolean isAllowUsed() {
		return true;
	}
	/**
	 * 是否允许添加该栏目，默认允许，如果需要特别控制，需要重载该方法，当前登录信息从CurrentUser中取
	 * 如果不允许，将不出现在备选栏目中；但如果是管理员推送的栏目，可以访问
	 * @return
	 */
	public boolean isAllowUsed(String spaceType){
		return isAllowUsed();
	}
	/**
	 * 是否允许用户访问该栏目，默认允许，如果需要特别控制，需要重载该方法，当前登录信息从CurrentUser中取
	 * 如果不允许，将不出现在备选栏目中；即使是管理员推送的栏目，也不可以访问
	 * 重写isAllowUserUsed（String singleBoardId）方法后不需要重写isAllowUsed()方法;
	 * @param singleBoardId 带独立ID的栏目使用该ID获取
	 * @return
	 */
	public boolean isAllowUserUsed(String singleBoardId){
		return isAllowUsed();
	}
	
	public boolean isAllowedNarrow() {
		return isAllowedNarrow;
	}

	public boolean isRegistrer() {
		return isRegistrer;
	}
	
/*	public SectionReference[] getPreferences() {
		return preferences;
	}

	public void setPreferences(SectionReference[] preferences) {
		this.preferences = preferences;
	}*/

	public String getResourceBundle() {
		return resourceBundle;
	}
	/**
	 * 对于空间的权限，是否只读。
	 * @param entityId
	 * @param spaceType
	 * @param ownerId
	 * @return
	 */
	public boolean isReadOnly(String spaceType, String ownerId){
		
		return false;
	}

	/**
	 * 国际化资源
	 * 
	 * @param resourceBundle 如：com.seeyon.v3x.resouces.i18n.ApplicationResourceBundle
	 */
	public void setResourceBundle(String resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	/**
	 * 这个方法是供Ajax Service调用的
	 * 2011.2.17 增加panelId 标示页签id
	 * 增加传递参数
	 */
	public final Map<String, Object> doProjection(String entityId, String ordinal, String spaceType, String ownerId, int x, int y, int width,String panelId,String[] paramKeys,String[] paramValues){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Map<String, String> preference = getPrefenerce(entityId, ordinal, spaceType, ownerId, String.valueOf(x), String.valueOf(y), String.valueOf(width));
			boolean isError = false;
			String name = null;
			if(preference == null || preference.get(PropertyName.sections.name()) == null){
				isError = true;
			}else{
				preference.put(PropertyName.panelId.name(), panelId);
				
				if(paramKeys != null && paramValues != null && paramKeys.length == paramValues.length){
					for(int i = 0 ; i < paramKeys.length;i++){
						preference.put(paramKeys[i], paramValues[i]);
					}
				}
				name = this.getName(preference);
			}
			if(isError || name == null){
				//TODO 栏目已经被删除了 或者整个空间已经更新
				result.put("error", "not_exists");
				return result;
			}
			
			BaseSectionTemplete c = this.projection(preference);
			
			/*if(c == null){
				log.warn("方法projection(Map)不能返回null值", new Exception(this.getId() + "," + this.getClass()));
			}*/
			
			result.put("Data", c);
			Integer total = this.getTotal(preference);
			if(total != null){
				result.put("Total", total);
				
				String totalUnit = this.getTotalUnit(preference);
				result.put("TotalUnit", totalUnit);
			}
			
			result.put("Name", name);
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return result;
	}
	/**
	 * 这个方法是供Ajax Service调用的
	 * 
	 * @param entityId
	 * @param ordinal
	 * @param layoutType
	 * @return
	 */
	public final Integer doGetTotal(String entityId, String ordinal, String spaceType, String ownerId){
		Map<String, String> preference = getPrefenerce(entityId, ordinal, spaceType, ownerId, null, null, null);
		try {
			return this.getTotal(preference);
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param entityId
	 * @param ordinal
	 * @param spaceType
	 * @param ownerId
	 * @return
	 */
	public final String doGetTotalUnit(String entityId, String ordinal, String spaceType, String ownerId){
		Map<String, String> preference = getPrefenerce(entityId, ordinal, spaceType, ownerId, null, null, null);
		try {
			return this.getTotalUnit(preference);
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return null;
	}
	/**
	 * 这个方法是供Ajax Service调用的
	 * 
	 * @param entityId
	 * @param ordinal
	 * @return
	 */
	public final String doGetName(String entityId, String ordinal, String spaceType, String ownerId){
		Map<String, String> preference = getPrefenerce(entityId, ordinal, spaceType, ownerId, null, null, null);
		String name = null;
		try{
			name = this.getName(preference);
		}
		catch(Exception e){
			log.warn("", e);
		}
		
		return name;
	}

	/**
	 * 获取栏目原始名称
	 * 
	 * @param entityId
	 * @param ordinal
	 * @param spaceType
	 * @param ownerId
	 * @return
	 */
	public final String doGetBaseName(String entityId, String ordinal, String spaceType, String ownerId) {
		Map<String, String> preference = getPrefenerce(entityId, ordinal, spaceType, ownerId, null, null, null);
		String name = null;
		try {
			name = this.getBaseName(preference);
		} catch (Exception e) {
			log.error("", e);
		}

		return name;
	}
	
	protected Map<String, String> getPrefenerce(String entityId, String ordinal, String spaceType, String ownerId, String x, String y, String width){
		Map<String, String> props = portletEntityPropertyManager.getPropertys(Long.parseLong(entityId));
		props = PortalConstants.getFragmentProp(props, ordinal);	
		//props.put(PropertyName.layoutType.name(), layoutType);
		props.put(PropertyName.spaceType.name(), spaceType);
		props.put(PropertyName.x.name(), x);
		props.put(PropertyName.y.name(), y);
		props.put(PropertyName.width.name(), width);
		props.put(PropertyName.isNarrow.name(), Boolean.toString(isNarrow(width)));
		props.put(PropertyName.entityId.name(), entityId);//增加Fragment.id。
		props.put(PropertyName.ordinal.name(), ordinal);  //增加在Fragment中的排序
		
		if(Strings.isNotBlank(ownerId)){
			props.put(PropertyName.ownerId.name(), ownerId);
		}
		return props;
	}
	
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BaseSection other = (BaseSection) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		}
		else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public int compareTo(BaseSection o) {
		if(this.sortId == null){
			return 1;
		}
		if(o.getSortId() == null){
			return -1;
		}
		return this.getSortId().compareTo(o.getSortId());
	}
	
	/**
	 * 宽度在40%以下就是窄栏目
	 * 
	 * @param width
	 * @return
	 */
	public static boolean isNarrow(String width){
		return width == null ? false : isNarrow(Integer.parseInt(width));
	}
	
	public static boolean isNarrow(int width){
		return width < 4;
	}

	public String getBaseName() {
		return this.baseName;
	}
	
	public String getBaseName(Map<String, String> preference) {
		return this.getBaseName();
	}

	public List<SectionProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<SectionProperty> properties) {
		this.properties = properties;
		for(SectionProperty property : this.properties){
			property.setSectionId(this.getId());
		}
	}
	
}
