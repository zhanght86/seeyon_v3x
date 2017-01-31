package com.seeyon.v3x.formbizconfig.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.SelectPersonOperation;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.domain.FomObjaccess;
import www.seeyon.com.v3x.form.manager.define.bind.auth.FormAppAuth;
import www.seeyon.com.v3x.form.manager.define.bind.auth.OperationAuth;
import www.seeyon.com.v3x.form.manager.define.bind.flow.inf.IFlowTemplet;
import www.seeyon.com.v3x.form.manager.define.query.inf.ISeeyonQuery;
import www.seeyon.com.v3x.form.manager.define.report.inf.ISeeyonReport;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

import com.seeyon.v3x.collaboration.domain.FormBody;
import com.seeyon.v3x.collaboration.domain.FormContent;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfig;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigColumn;
import com.seeyon.v3x.formbizconfig.domain.V3xBizAuthority;
import com.seeyon.v3x.formbizconfig.domain.V3xBizConfig;
import com.seeyon.v3x.formbizconfig.domain.V3xBizConfigItem;
import com.seeyon.v3x.formbizconfig.manager.FormBizConfigManager;
import com.seeyon.v3x.formbizconfig.manager.V3xBizConfigManager;
import com.seeyon.v3x.menu.domain.Menu;
import com.seeyon.v3x.menu.domain.MenuProfile;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.product.ProductInfo;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.CommonTools.CollectionActionType;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;

/**
 * 
 * 由于模块解耦，此类公共方法请用com.seeyon.v3x.util.CommonTools中方法。
 * 
 * 表单业务配置工具类，包括若干公用方法
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-04-02
 */
public class FormBizConfigUtils {
	
	private static final Log logger = LogFactory.getLog(FormBizConfigUtils.class);
	
	/**
	 * 辅助方法：获取<b>表单业务配置国际化key</b>对应值
	 */
	public static String getI18NValue(String key, Object... values) {
		return ResourceBundleUtil.getString(FormBizConfigConstants.FORM_BIZ_CONFIG_RESOURCE, key, values);
	}
	
	/**
	 * 辅助方法：获取表单模板对应的表单ID集合
	 */
	public static List<Long> getFormAppIdList(List<Templete> formTemps) {
		if(CollectionUtils.isNotEmpty(formTemps)) {
			List<Long> result = new ArrayList<Long>(formTemps.size());
			for(Templete t : formTemps) {
				try {
					Long formAppId = getFormAppId(t.getBody());
					if(!result.contains(formAppId)) {  
						result.add(formAppId);
					}
				}
				catch(Exception e) {
					logger.warn("表单模板[id=" + t.getId() + ", 名称=" + t.getSubject() + "]无法获取与之对应的表单信息，请检查该表单模板是否正常!");
				}
			}
			return result;
		}
		return null;
	}
	
	/**
	 * 根据表单模板获取其对应的表单ID
	 * @param templeteBody	表单模板储存信息
	 * @return	表单模板对应的表单ID
	 * @throws Exception	表单或表单模板保存或使用过程中出现异常，相关数据异常，将无法有效获取对应表单ID，抛出异常
	 */
	public static Long getFormAppId(String templeteBody) throws Exception {
		FormContent formContent = (FormContent)XMLCoder.decoder(templeteBody);
		FormBody formBody = formContent.getForms().get(0);
		String formAppIdStr = formBody.getFormApp();
		Long formAppId = Long.valueOf(formAppIdStr);
		return formAppId;
	}
	
	/**
	 * 辅助方法：通过所选取的表单模板获取对应的表单ID集合
	 */
	public static String getFormIds(List<Templete> formTemps) throws SeeyonFormException {
		List<Long> formIdList = FormBizConfigUtils.getFormAppIdList(formTemps);
		String result = StringUtils.join(formIdList, ',');
		return StringUtils.defaultString(result);
	}
	
	/**
	 * 根据用户的操作选择获取业务配置的挂接类型
	 * @param column    是否选中栏目挂接
	 * @param menu	 	是否选中菜单挂接
	 */
	public static int getBizConfigType(boolean column, boolean menu) {
		if(column && menu)
			return FormBizConfigConstants.CONFIG_TYPE_COLUMN_MENU;
		else if(column && !menu)
			return FormBizConfigConstants.CONFIG_TYPE_COLUMN;
		else if(!column && menu)
			return FormBizConfigConstants.CONFIG_TYPE_MENU;
		else
			return FormBizConfigConstants.CONFIG_TYPE_NO;
	}
	
	/**
	 * 得到表单业务配置菜单挂接项的链接地址：
	 * @param category 		菜单挂接项所属类型：如新建事项、待发事项、已发事项等等
	 * @param bizConfigId 	表单业务配置ID
	 * @param tempId 		如为新建事项，单一对应表单模板ID
	 * @param tempIds 		为表单模板ID拼串的结果，其格式为:1212231212,12345656567，需要使用时将其拆分
	 * @param formIds 		为表单模板对应的表单ID拼串的结果，其格式为:1212231212,12345656567，需要使用时将其拆分
	 * @return 				点击菜单对应链接地址如：/collaboration.do?method=newColl&flag=formBizConfig&bizConfigId=-123123&templeteId=-121221&type=menu
	 */
	public static String getActionURL4Menu(int category, Long bizConfigId, String tempId, String tempIds, String formIds) throws SeeyonFormException {
		String actionUrl = null;
		String param4Affairs = "&flag=formBizConfig&bizConfigId=" + bizConfigId + "&tempIds=" + tempIds + "&type=menu";
		String param4FormIds = "&flag=formBizConfig&bizConfigId=" + bizConfigId + "&formIds=" + formIds + "&type=menu";
		// 依次为：新建事项、待发事项、已发事项、待办事项、已办事项、督办事项、表单查询、表单统计、信息中心
		switch(category) {
			case FormBizConfigConstants.MENU_NEW_AFFAIRS:
				// 此处参数写法不宜变动，以免通过字符串截取表单模板ID时出现问题
				actionUrl = FormBizConfigConstants.URL_NEW_AFFAIRS + "&flag=formBizConfig&bizConfigId=" + bizConfigId + "&templeteId=" + tempId + "&type=menu";  
				break;
			case FormBizConfigConstants.MENU_TO_SEND_AFFAIRS:
				actionUrl = FormBizConfigConstants.URL_TO_SEND_AFFAIRS + param4Affairs;
				break;
			case FormBizConfigConstants.MENU_SENT_AFFAIRS:
				actionUrl = FormBizConfigConstants.URL_SENT_AFFAIRS + param4Affairs;
				break;
			case FormBizConfigConstants.MENU_TO_DEAL_AFFAIRS:
				actionUrl = FormBizConfigConstants.URL_TO_DEAL_AFFAIRS + param4Affairs;
				break;
			case FormBizConfigConstants.MENU_DEALT_AFFAIRS:
				actionUrl = FormBizConfigConstants.URL_DEALT_AFFAIRS + param4Affairs;
				break;
			case FormBizConfigConstants.MENU_SUPERWISE_AFFAIRS:
				actionUrl = FormBizConfigConstants.URL_SUPERWISE_AFFAIRS + param4Affairs;
				break;
			case FormBizConfigConstants.MENU_FORM_QUERY:
				actionUrl = FormBizConfigConstants.URL_FORM_QUERY + param4FormIds;
				break;
			case FormBizConfigConstants.MENU_FORM_STATISTIC:
				actionUrl = FormBizConfigConstants.URL_FORM_STATISTIC + param4FormIds;
				break;
			case FormBizConfigConstants.MENU_INFO_CENTER:
				actionUrl = FormBizConfigConstants.URL_INFO_CENTER + "&bizConfigId=" + bizConfigId + "&tempIds=" + tempIds + "&type=menu";
				break;
		}
		return actionUrl;
	}
	
	/**
	 * 根据菜单链接地址获取菜单所属分类编号，用于已选菜单项前端展现
	 * @param actionUrl
	 */
	public static int getCategoryNum(String actionUrl) {
		// 依次为：新建事项、待发事项、已发事项、待办事项、已办事项、督办事项、表单查询、表单统计、信息中心
		int result = FormBizConfigConstants.MENU_INVALID;
		if(actionUrl.indexOf(FormBizConfigConstants.URL_NEW_AFFAIRS) != -1) {
			result = FormBizConfigConstants.MENU_NEW_AFFAIRS;
		} 
		else if(actionUrl.indexOf(FormBizConfigConstants.URL_TO_SEND_AFFAIRS) != -1) {
			result = FormBizConfigConstants.MENU_TO_SEND_AFFAIRS;
		} 
		else if(actionUrl.indexOf(FormBizConfigConstants.URL_SENT_AFFAIRS) != -1) {
			result = FormBizConfigConstants.MENU_SENT_AFFAIRS;
		} 
		else if(actionUrl.indexOf(FormBizConfigConstants.URL_TO_DEAL_AFFAIRS) != -1) {
			result = FormBizConfigConstants.MENU_TO_DEAL_AFFAIRS;
		} 
		else if(actionUrl.indexOf(FormBizConfigConstants.URL_DEALT_AFFAIRS) != -1) {
			result = FormBizConfigConstants.MENU_DEALT_AFFAIRS;
		} 
		else if(actionUrl.indexOf(FormBizConfigConstants.URL_SUPERWISE_AFFAIRS) != -1) {
			result = FormBizConfigConstants.MENU_SUPERWISE_AFFAIRS;
		} 
		else if(actionUrl.indexOf(FormBizConfigConstants.URL_FORM_QUERY) != -1) {
			result = FormBizConfigConstants.MENU_FORM_QUERY;
		} 
		else if(actionUrl.indexOf(FormBizConfigConstants.URL_FORM_STATISTIC) != -1) {
			result = FormBizConfigConstants.MENU_FORM_STATISTIC;
		} 
		else if(actionUrl.indexOf(FormBizConfigConstants.URL_INFO_CENTER) != -1) {
			result = FormBizConfigConstants.MENU_INFO_CENTER;
		} 
		return result;
	}
	
	/**
	 * 根据业务配置的挂接项，获取某种类型的栏目挂接项是否存在的信息
	 * @param columns	业务配置的栏目挂接项
	 * @return k - 栏目挂接项类型, v - 存在值
	 */
	public static Map<Integer, Boolean> getColumnCategoryExistInfo(List<FormBizConfigColumn> columns) {
		Map<Integer, Boolean> result = new HashMap<Integer, Boolean>();
		if(CollectionUtils.isNotEmpty(columns)) {
			for(FormBizConfigColumn column : columns) {
				result.put(column.getCategory(), true);
			}
		}
		return result;
	}
	
	/**
	 * 获取栏目挂接项中待办事项、跟踪事项、督办事项的排序号<br>
	 * 以便在首页栏目中根据用户的设定安排三种事项出现的先后顺序，如果对应事项不存在，返回排序号0<br>
	 * @param columns	业务配置的栏目挂接项	
	 * @return	三种事项的排序号数组
	 */
	public static int[] getFormFlowsSortId(List<FormBizConfigColumn> columns) {
		int[] sortIds = {0, 0, 0};
		if(CollectionUtils.isNotEmpty(columns)) {
			for(FormBizConfigColumn column : columns) {
				if(column.getCategory() == FormBizConfigConstants.COLUMN_FORM_FLOW_WAIT) {
					sortIds[0] = column.getSortId();
				}
				if(column.getCategory() == FormBizConfigConstants.COLUMN_FORM_FLOW_TRACK) {
					sortIds[1] = column.getSortId();
				}
				if(column.getCategory() == FormBizConfigConstants.COLUMN_FORM_FLOW_SUPERWISE) {
					sortIds[2] = column.getSortId();
				}
			}
		}
		return sortIds;
	}
	
	/**
	 * 与表单业务配置相关的各种事项(包括协同各种事项及表单查询、统计)防护及当前位置显示<br>
	 * 如果业务配置被删除或用户被取消共享权，则返回首页，如能正常使用，则传入相关参数<br>
	 * 如果是从点击二级菜单入口而来，还需加入二级菜单是否仍存在的判断<br>
	 * 如果操作时调用模板新建协同，还需加入模板是否有效及当前用户是否有权使用的判断<br>
	 * 如果操作能够正常进行下去，则将当前位置所需的信息(菜单ID或给定的标题)及业务配置传入<br>
	 * @param menuCategory 菜单所属事项分类，以便获取对应二级菜单名称
	 * @see com.seeyon.v3x.collaboration.controller.CollaborationController#newColl   	  协同：新建事项
	 * @see com.seeyon.v3x.collaboration.controller.CollaborationController#listDone     协同：已办事项列表
	 * @see com.seeyon.v3x.collaboration.controller.CollaborationController#listPending  协同：待办事项列表
	 * @see com.seeyon.v3x.collaboration.controller.CollaborationController#listSent 	   协同：已发事项列表
	 * @see com.seeyon.v3x.collaboration.controller.CollaborationController#listWaitSend 协同：待发事项列表
	 * @see com.seeyon.v3x.collaboration.controller.ColSuperviseController#superviseList 协同：督办事项列表
	 * @see www.seeyon.com.v3x.form.controller.query.QueryController#formQueryList       表单：查询模板列表
	 * @see www.seeyon.com.v3x.form.controller.report.ReportController#formReportList	   表单：统计模板列表
	 */
	public static boolean validate(ModelAndView mav, HttpServletRequest request, HttpServletResponse response, int menuCategory) throws Exception {
	    String type = request.getParameter("type");
	    boolean fromMenu = "menu".equalsIgnoreCase(type) || Strings.isBlank(type);
	    Long memberId = CurrentUser.get().getId();
	    
	    // 新建事项，调用表单模板发协同的情况
    	String templeteId = request.getParameter("templeteId");
    	if(Strings.isNotBlank(templeteId) && menuCategory == FormBizConfigConstants.MENU_NEW_AFFAIRS) {
    		boolean valid = false;
        	// 表单模板是否有效、用户是否有权使用(系统模板需进行权限校验，个人模板则不需)
    		TempleteManager templeteManager = (TempleteManager)ApplicationContextHolder.getBean("templeteManager");
    		long temp_id = NumberUtils.toLong(templeteId);
			Templete templete = templeteManager.get(temp_id);
        	if(templete != null) {
        		valid = !templete.getIsSystem() || templeteManager.hasAccSystemTempletes(temp_id, memberId);
        	}
        	
        	if(!valid) {
        		rendJavaScript(response, "alert('" + FormBizConfigUtils.getI18NValue("bizconfig.templete.deletedback.label") + "');"  +
        								 "parent.getA8Top().refreshAndBack('" + fromMenu + "');");
        		return false;
        	}
        	mav.addObject("templete", templete);
    	}
	    
	    String flag = request.getParameter("flag");
	    String bCId = request.getParameter("bizConfigId");
	    if(Strings.isNotBlank(flag) && Strings.isNotBlank(bCId)) {
	    	Long bizConfigId = Long.valueOf(bCId);
	    	FormBizConfigManager formBizConfigManager = (FormBizConfigManager)ApplicationContextHolder.getBean("formBizConfigManager");
	    	FormBizConfig bizConfig = formBizConfigManager.findById(bizConfigId);
	    	// 如果业务配置已被删除
	    	if(bizConfig == null) {
	    		rendJavaScript(response, "alert('" + FormBizConfigUtils.getI18NValue("bizconfig.deleted.back.label") + "');" + 
	    							     "parent.getA8Top().refreshAndBack('" + fromMenu + "');");
	    		return false;
	    	}
	    	
        	if(fromMenu) {
        		Menu menu = null;
        		if(menuCategory == FormBizConfigConstants.MENU_NEW_AFFAIRS)
        			menu = formBizConfigManager.getNewAffairMenu4Location(bizConfigId, templeteId.toString());
        		else
        			menu = formBizConfigManager.getSubMenu4Location(bizConfigId, menuCategory);
        			
        		// 如果二级菜单已被删除
        		if(menu == null) {
        			rendJavaScript(response, "alert('"+ FormBizConfigUtils.getI18NValue("bizconfig.childmenu.deleted.label") + "');" + 
        									 "parent.getA8Top().refreshAndBack('" + fromMenu + "');");
        			return false;
        		}
        		
        		// 如果当前共享用户已不在共享范围中，无权继续使用此二级菜单
        		if(!formBizConfigManager.isCreatorOrInShareScope(bizConfig, memberId)) {
        			rendJavaScript(response, "alert('"+ FormBizConfigUtils.getI18NValue("bizconfig.notinsharescope.label") + "');" + 
        									 "parent.getA8Top().refreshAndBack('" + fromMenu + "');");
        			return false;
        		}
        		
    			// 传入当前子菜单ID，以便当前位置显示
        		mav.addObject("menuId", menu.getId());
	        	mav.addObject("bizConfig", bizConfig);	
        	} 
        	else {
        		// 如果当前共享用户已不在共享范围中，无权继续使用此表单业务配置栏目
        		if(!formBizConfigManager.isCreatorOrInShareScope(bizConfig, memberId)) {
        			rendJavaScript(response, "alert('"+ FormBizConfigUtils.getI18NValue("bizconfig.notinsharescope.label") + "');" + 
        									 "parent.getA8Top().refreshAndBack('" + fromMenu + "');");
        			return false;
        		}
		        mav.addObject("bizConfig", bizConfig);
        	}
	    }
	    return true;
	}
	
	private static void rendJavaScript(HttpServletResponse response, String jsContent) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("<script type=\"text/javascript\">");
		out.println(jsContent);
		out.println("</script>");
		out.close();
	}
	
	@Deprecated
	public static List<Long> parseStrArr2Ids(String[] idStrArray) {
		return CommonTools.parseStrArr2Ids(idStrArray);
	}

	@Deprecated
	public static List<Long> parseStr2Ids(String idStrs) {
		return CommonTools.parseStr2Ids(idStrs);
	}

	@Deprecated
	public static List<Long> parseStr2Ids(String idStrs, String seperator) {
		return CommonTools.parseStr2Ids(idStrs, seperator);
	}

	@Deprecated
	public static List<Long> parseStr2Ids(HttpServletRequest request, String parameterName) {
		return CommonTools.parseStr2Ids(request, parameterName);
	}

	@Deprecated
	public static List<Long> getEntityIds(Collection<? extends V3xOrgEntity> entities) {
		return CommonTools.getEntityIds(entities);
	}

	@Deprecated
	public static List<Long> getMemberIdsByTypeAndId(String typeAndIds, OrgManager orgManager) {
		return CommonTools.getMemberIdsByTypeAndId(typeAndIds, orgManager);
	}

	@Deprecated
	public static List<Long> getUserDomainIds(OrgManager orgManager) {
		return CommonTools.getUserDomainIds(orgManager);
	}

	@Deprecated
	public static List<Long> getUserDomainIds(User user, OrgManager orgManager, long... includeDomains) {
		return CommonTools.getUserDomainIds(user, orgManager, includeDomains);
	}

	@Deprecated
	public static List<Long> getUserDomainIds(Long memberId, OrgManager orgManager) {
		return CommonTools.getUserDomainIds(memberId, orgManager);
	}

	@Deprecated
	public static List<Long> getUserDomainIds(Long memberId, boolean internal, OrgManager orgManager, long... includeDomains) {
		return CommonTools.getUserDomainIds(memberId, internal, orgManager, includeDomains);
	}

	@Deprecated
	public static void filterInvalidEntities(Collection<? extends V3xOrgEntity> entities) {
		CommonTools.filterInvalidEntities(entities);
	}

	@Deprecated
	public static String getTypeAndIdStrs(List<Object[]> typeAndIds) {
		return CommonTools.getTypeAndIdStrs(typeAndIds);
	}

	@Deprecated
	public static List<Long> getSharerIds(String shareScopeTypeAndIds, Long creatorId, OrgManager orgManager) {
		return CommonTools.getSharerIds(shareScopeTypeAndIds, creatorId, orgManager);
	}

	@Deprecated
	public static List<Long> getSharerAndCreatorIds(String shareScopeTypeAndIds, Long creatorId, OrgManager orgManager) {
		return CommonTools.getSharerAndCreatorIds(shareScopeTypeAndIds, creatorId, orgManager);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public static List collectProperty(Collection<? extends BaseModel> models, String propertyName) {
		return CommonTools.collectProperty(models, propertyName);
	}

	@Deprecated
	public static List<Long> getIds(Collection<? extends BaseModel> models) {
		return CommonTools.getIds(models);
	}

	@Deprecated
	public static String getIdStrs(Collection<? extends BaseModel> models, char joinChar) {
		return CommonTools.getIdStrs(models, joinChar);
	}

	@Deprecated
	public static Map<String, Object> newHashMap(String[] keys, Object[] values) {
		return CommonTools.newHashMap(keys, values);
	}

	@Deprecated
	public static <T> ArrayList<T> newArrayList(T... elements) {
		return CommonTools.newArrayList(elements);
	}

	@Deprecated
	public static Map<String, Object> newHashMap(String key, Object value) {
		return CommonTools.newHashMap(key, value);
	}

	@Deprecated
	public static String getIdStrs(Collection<? extends BaseModel> models) {
		return CommonTools.getIdStrs(models);
	}

	@Deprecated
	public static List<Long> parseTypeAndIdStr2Ids(String typeAndIds) {
		return CommonTools.parseTypeAndIdStr2Ids(typeAndIds);
	}

	@Deprecated
	public static <T> List<T> pagenate(List<T> list) {
		return CommonTools.pagenate(list);
	}

	@Deprecated
	public static <T> List<T> pagenate(List<T> list, boolean needCount) {
		return CommonTools.pagenate(list, needCount);
	}

	@Deprecated
	public static <T> List<T> getSubList(List<T> source, int first, int end) {
		return CommonTools.getSubList(source, first, end);
	}

	@Deprecated
	public static <T> List<T> parseArr2List(T[] arr) {
		return CommonTools.parseArr2List(arr);
	}

	@Deprecated
	public static boolean equals(String s1, String s2) {
		return CommonTools.equals(s1, s2);
	}

	@Deprecated
	public static <T> List<T> getCollectionActionResult(Collection<T> oldList, Collection<T> newList, CollectionActionType actionType) {
		return CommonTools.getCollectionActionResult(oldList, newList, actionType);
	}

	@Deprecated
	public static <T> List<T> getSumCollection(Collection<T> a, Collection<T> b) {
		return CommonTools.getSumCollection(a, b);
	}

	@Deprecated
	public static <T> List<T> getIntersection(Collection<T> a, Collection<T> b) {
		return CommonTools.getIntersection(a, b);
	}

	@Deprecated
	public static <T> List<T> getAddedCollection(Collection<T> oldColl, Collection<T> newColl) {
		return CommonTools.getAddedCollection(oldColl, newColl);
	}

	@Deprecated
	public static <T> List<T> getReducedCollection(Collection<T> oldColl, Collection<T> newColl) {
		return CommonTools.getReducedCollection(oldColl, newColl);
	}

	@Deprecated
	public static <T> List<T> parseCollection2ListIgnoreEmpty(Collection<T> c) {
		return CommonTools.parseCollection2ListIgnoreEmpty(c);
	}

	@Deprecated
	public static <T> void addAllIgnoreEmpty(Collection<T> mainCollection, Collection<T> toBeAdded) {
		CommonTools.addAllIgnoreEmpty(mainCollection, toBeAdded);
	}

	@Deprecated
	public static <T> void removeAllIgnoreEmpty(Collection<T> mainCollection, Collection<T> toBeRemoved) {
		CommonTools.removeAllIgnoreEmpty(mainCollection, toBeRemoved);
	}

	@Deprecated
	public static <T> int getSizeIgnoreEmpty(Collection<T> c) {
		return CommonTools.getSizeIgnoreEmpty(c);
	}
	
	
	/**
     * 得到当前业务配置的菜单下的子菜单及当前菜单的权限
     */
    public static Map<Long,Boolean> getV3xBizMenuPurviewMap(Long memberId, List<Long> menuProfile){
    	Map<Long,Boolean> v3xBizMenuPurviewMap  = new HashMap<Long, Boolean>();
    	Map<Long,Set<Long>> menuIdMap = getAccessFirMenuIdsByMemberId(memberId);
    	for (Long menuId : menuIdMap.keySet()) {
    		boolean isShow = CollectionUtils.isEmpty(menuProfile) || menuProfile.contains(menuId);
    		boolean isShow2 = isShow;
    		v3xBizMenuPurviewMap.put(menuId, isShow);
        	Set<Long> accessBizMenuSecMenuIds = menuIdMap.get(menuId);
        	for (Long menuId2: accessBizMenuSecMenuIds) {
        		v3xBizMenuPurviewMap.put(menuId2, isShow2);
    		}
		}
    	return v3xBizMenuPurviewMap;
    }
    
    /**
     * 得到人员拥有的挂接一级菜单ID by wusb
     */
    public static Map<Long,Set<Long>> getAccessFirMenuIdsByMemberId(Long memberId){
    	initManager();
    	
    	Map<Long,Set<Long>> menuIdMap = new HashMap<Long, Set<Long>>();
    	if(!SystemEnvironment.hasPlugin(ProductInfo.PluginNoMapper.formBiz.name())){
    		return menuIdMap;
    	}
    	
    	try {
    		V3xBizConfigManager v3xBizConfigManager = (V3xBizConfigManager) ApplicationContextHolder.getBean("v3xBizConfigManager");
    		List<Long> entIdsList = orgManager.getUserDomainIDs(memberId,V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
    				V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
    		
    		List<Long> menuIdList = v3xBizConfigManager.findAccessMenuIdsByScopeIds(entIdsList);
    		if(menuIdList!=null){
    			//我能访问的模板
    			List<Templete> templetes = templeteManager.getSystemTempletesByMemberId(memberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, 4);
    			Set<Long> templeteIds = new HashSet<Long>(templetes.size());
    			for (Templete templete : templetes) {
    				templeteIds.add(templete.getId());
				}
    			
    			Set<Long> set = new HashSet<Long>(menuIdList);
    			for (Long menuId : set) {
    				Set<Long> accessBizMenuSecMenuIds = getAccessSecMenuIdsByMemberId(memberId,menuId,entIdsList,templeteIds);
    				if(accessBizMenuSecMenuIds!=null && !accessBizMenuSecMenuIds.isEmpty()){
    					menuIdMap.put(menuId, accessBizMenuSecMenuIds);
    				}
				}
    		}
    	} catch (Exception e) {
    		logger.error(e.getMessage(),e);
		}
    	return menuIdMap;
    }
    
	private static OrgManager orgManager = null;
	private static V3xBizConfigManager v3xBizConfigManager;
	private static FormDaoManager formDaoManager;
	private static TempleteManager templeteManager;
	private static MenuManager menuManager;
	private static void initManager(){
		if(orgManager == null){
			orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
			v3xBizConfigManager = (V3xBizConfigManager) ApplicationContextHolder.getBean("v3xBizConfigManager");
			formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
			templeteManager = (TempleteManager)ApplicationContextHolder.getBean("templeteManager");
			menuManager = (MenuManager) ApplicationContextHolder.getBean("menuManager");
		}
	}
    
    /**
     * 得到人员拥有的挂接二级菜单ID  by wusb
     */
    @SuppressWarnings("unchecked")
    public static Set<Long> getAccessSecMenuIdsByMemberId(Long memberId, Long menuId, List<Long> _entIdsList, Set<Long> templetes){
    	initManager();
    	
    	HashSet<Long> entIdsList = new HashSet<Long>(_entIdsList);
    	
    	Set<Long> menuIds = new HashSet<Long>();
    	try {
    		V3xBizConfig bizConfig = v3xBizConfigManager.findBizConfigByMenuId(menuId);
    		if(bizConfig == null){
    			return menuIds; 
    		}
    		List<V3xBizConfigItem> bizConfigItemList = bizConfig.getV3xBizConfigItemList();
    		for (V3xBizConfigItem v3xBizConfigItem : bizConfigItemList) {
    			int sourceType = v3xBizConfigItem.getSourceType();
    			Long sourceId = v3xBizConfigItem.getSourceId();
    			Long secMenuId = v3xBizConfigItem.getMenuId();
    			if(sourceType == FormBizConfigConstants.SOURCE_TYPE_FLOWTEMPLATE || sourceType == FormBizConfigConstants.SOURCE_TYPE_INFOMANAGE
    					|| sourceType == FormBizConfigConstants.SOURCE_TYPE_BASEDATA || sourceType == FormBizConfigConstants.SOURCE_TYPE_QUERY
    					|| sourceType == FormBizConfigConstants.SOURCE_TYPE_REPORT){
	    			ISeeyonForm_Application afapp = SeeyonForm_Runtime.getInstance().getAppManager().findById(v3xBizConfigItem.getFormAppmainId());
	    			if(afapp!=null){
	    				String objectName = "";
	    				int objectType = 0;
		    			if(sourceType == FormBizConfigConstants.SOURCE_TYPE_FLOWTEMPLATE){
		    				if(v3xBizConfigItem.getFlowMenuType().intValue()==1){
			    				IFlowTemplet flowTemplet = afapp.getSeeyonFormBind().findFlowTempletById(sourceId);
			    				if(flowTemplet!=null){
				    				boolean isCanAccess = templetes.contains(flowTemplet.getId());
				    				if(isCanAccess) {
			    						menuIds.add(secMenuId);
									}
			    				}
		    				}
		    				else{
		    					menuIds.add(secMenuId);
		    				}
		    			}else{
		    				if(sourceType == FormBizConfigConstants.SOURCE_TYPE_INFOMANAGE || sourceType == FormBizConfigConstants.SOURCE_TYPE_BASEDATA){
			    				objectType = IPagePublicParam.C_iObjecttype_bill;
			    				FormAppAuth formAuth = afapp.findFormAppAuthById(String.valueOf(sourceId));
			    				if(formAuth!=null){
									for(OperationAuth oa : formAuth.getOperationAuths().values()){
										objectName = oa.getName();
									}
			    				}
			    			}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_QUERY){
			    				objectType = IPagePublicParam.C_iObjecttype_Query;
			    				ISeeyonQuery squery = afapp.findQueryById(String.valueOf(sourceId));
			    				if(squery!=null){
			    					objectName = squery.getQueryName();
			    				}
			    			}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_REPORT){
			    				objectType = IPagePublicParam.C_iObjecttype_Report;
			    				ISeeyonReport sreport = afapp.findReportById(String.valueOf(sourceId));
			    				if(sreport!=null){
			    					objectName = sreport.getReportName();
			    				}
			    			}
			    			List<FomObjaccess> listObjAccess = formDaoManager.queryObjAccessByCondition(v3xBizConfigItem.getFormAppmainId(), objectName,objectType);
			        		for(FomObjaccess access : listObjAccess) {
			        			SelectPersonOperation spo = new SelectPersonOperation();
								String authType = spo.getTypeByTypeId(access.getUsertype());
			    				if(StringUtils.isNotBlank(authType)){
			    					if(access.getUserid() != null && entIdsList.contains(access.getUserid())){
			    						menuIds.add(secMenuId);
			    						break;
			    					}
			    				}
			    			}	
		    			}
	    			}
    			}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_DOCUMENT){
    					menuIds.add(secMenuId);
    			}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_ADMIN_BUL || sourceType == FormBizConfigConstants.SOURCE_TYPE_ADMIN_NEWS 
    					|| sourceType == FormBizConfigConstants.SOURCE_TYPE_ADMIN_BBS || sourceType == FormBizConfigConstants.SOURCE_TYPE_ADMIN_INQUIRY
    					|| sourceType == FormBizConfigConstants.SOURCE_TYPE_GROUP_BUL || sourceType == FormBizConfigConstants.SOURCE_TYPE_GROUP_NEWS
    					|| sourceType == FormBizConfigConstants.SOURCE_TYPE_GROUP_BBS || sourceType == FormBizConfigConstants.SOURCE_TYPE_GROUP_INQUIRY){
    				menuIds.add(secMenuId);
    			}
			}
    	} catch (Exception e) {
    		logger.error(e.getMessage(),e);
		}
		return menuIds;
    }
    
    /**
     * 插入个性化菜单
     */
    public static void saveMenuProfile(List<V3xBizAuthority> newV3xBizAuthorityList, List<V3xBizAuthority> oldV3xBizAuthorityList, Long menuId){
    	if(Strings.isEmpty(newV3xBizAuthorityList) && Strings.isEmpty(oldV3xBizAuthorityList)){
    		return;
    	}
    	//删除原有该菜单的个性化
    	//menuManager.deleteMenuProfilesByMenuId(menuId);
    	
    	//新有菜单权限的人
    	Set<Long> newMemberIds = new HashSet<Long>();
    	if(Strings.isNotEmpty(newV3xBizAuthorityList)){
	    	for (V3xBizAuthority a : newV3xBizAuthorityList) {
	    		try {
					List<V3xOrgMember> members = orgManager.parseMember(a.getScopeTypeStr(), String.valueOf(a.getScopeId()), String.valueOf(V3xOrgEntity.VIRTUAL_ACCOUNT_ID));
					for (V3xOrgMember m : members) {
						newMemberIds.add(m.getId());
					}
	    		}
				catch (Exception e) {
					logger.warn("获取[" + a.getScopeTypeStr() + ", " + a.getScopeId() + "]下面的人员异常：" + e.toString());
				}
			}
    	}
    	
    	//旧有菜单权限的人
    	Set<Long> oldMemberIds = new HashSet<Long>();
    	if(CollectionUtils.isNotEmpty(oldV3xBizAuthorityList)){
	    	for (V3xBizAuthority a : oldV3xBizAuthorityList) {
	    		try {
					List<V3xOrgMember> members = orgManager.parseMember(a.getScopeTypeStr(), String.valueOf(a.getScopeId()), String.valueOf(V3xOrgEntity.VIRTUAL_ACCOUNT_ID));
					for (V3xOrgMember m : members) {
						oldMemberIds.add(m.getId());
					}
	    		}
				catch (Exception e) {
					logger.warn("获取[" + a.getScopeTypeStr() + ", " + a.getScopeId() + "]下面的人员异常：" + e.toString());
				}
			}
    	}
    	
    	List<Long> addMemberIds = CommonTools.getAddedCollection(oldMemberIds, newMemberIds);
    	List<Long> updateMemeberIds = CommonTools.getIntersection(newMemberIds, oldMemberIds);
    	List<Long> deleteMemeberIds = CommonTools.getReducedCollection(oldMemberIds, newMemberIds);
    	
    	if(CollectionUtils.isNotEmpty(addMemberIds)){
    		List<MenuProfile> menuProfiles = new ArrayList<MenuProfile>();
        	
        	Map<Long, Integer> memberIdProfiles = menuManager.getMenuProfileAllValidMemberIds(); //做过个性化的人
        	for (Long memberId : addMemberIds) {
        		Integer sortId = memberIdProfiles.get(memberId);
        		if(sortId != null){ //即有权限，有做了个性化，就追加一个
        			MenuProfile mp = new MenuProfile();
        			mp.setIdIfNew();
        			mp.setMemberId(memberId);
        			mp.setMenuId(menuId);
        			mp.setSortId(sortId + 1);
        			menuProfiles.add(mp);
        		}
        	}
        	
        	menuManager.saveMenuProfiles(menuProfiles);
    	}
    	
		if(CollectionUtils.isNotEmpty(updateMemeberIds)){
		    //do nothing
		}
		if(CollectionUtils.isNotEmpty(deleteMemeberIds)){
			menuManager.deleteMenuProfiles(deleteMemeberIds, menuId);
		}
    }
    
    public static String trunAction(String menuId, String secAction,int menuType){
    	try {
	    	if(Strings.isNotBlank(secAction) && menuType == Menu.TYPE.formAppBindBizConfig.ordinal()){
	    		if(secAction.startsWith("/formquery.do")) {
	    			Long appformId = Long.parseLong(secAction.substring(secAction.indexOf("&formid=") + "&formid=".length(), secAction.indexOf("&queryname")));
	    			String id = secAction.substring(secAction.indexOf("&queryname=") + "&queryname=".length(), secAction.length());
	    			ISeeyonForm_Application afapp = SeeyonForm_Runtime.getInstance().getAppManager().findById(appformId);
	    			if(afapp!=null){
						ISeeyonQuery query = afapp.findQueryById(id);
						if(query!=null){
							String queryName = Functions.encodeURI(query.getQueryName());
							secAction = secAction.replaceAll(id, queryName);
							return secAction+"&flag=formBizConfig&type=menu&menuId="+menuId;
						}
	    			}
	    		}else if(secAction.startsWith("/formreport.do")) {
	    			Long appformId = Long.parseLong(secAction.substring(secAction.indexOf("&formid=") + "&formid=".length(), secAction.indexOf("&reportname")));
					String id = secAction.substring(secAction.indexOf("&reportname=") + "&reportname=".length(), secAction.length());
					ISeeyonForm_Application afapp = SeeyonForm_Runtime.getInstance().getAppManager().findById(appformId);
	    			if(afapp!=null){
						ISeeyonReport report = afapp.findReportById(id);
						if(report!=null){
							String reportName = Functions.encodeURI(report.getReportName());
							secAction = secAction.replaceAll(id, reportName);
							return secAction+"&flag=formBizConfig&type=menu&menuId="+menuId;
						}
	    			}
	    		}else{
	    			return secAction+"&flag=formBizConfig&type=menu&menuId="+menuId;
	    		}
	    	}
	    } catch (Exception e) {
	    	logger.error(e.getMessage(),e);
		}
	    return secAction;
    }
    
    public static void trunMenuName(Menu menu){
    	if(menu.getId()==306){
			if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("formBiz")){
				menu.setName("menu.formcolumnconfig.label");
			}
    	}
    }
    
    public static String getAction(V3xBizConfigItem bizConfigItem){
    	int sourceType = bizConfigItem.getSourceType();
		String action ="";
		if(sourceType == FormBizConfigConstants.SOURCE_TYPE_FLOWTEMPLATE){
			if(bizConfigItem.getFlowMenuType()==1){
				action="/collaboration.do?method=newColl&templeteId="+String.valueOf(bizConfigItem.getSourceId());
			}else if(bizConfigItem.getFlowMenuType()==2){
				action="/formBizConfig.do?method=listBizColList&templeteId="+String.valueOf(bizConfigItem.getSourceId());
			}
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_INFOMANAGE){
			action = "/appFormController.do?method=showAppFormFrame&appformId=" + String.valueOf(bizConfigItem.getFormAppmainId()) + "&templeteId=" + String.valueOf(bizConfigItem.getSourceId()) + "&noStatusRow=-1&formType=2";
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_BASEDATA){
			action = "/appFormController.do?method=showAppFormFrame&appformId=" + String.valueOf(bizConfigItem.getFormAppmainId()) + "&templeteId=" + String.valueOf(bizConfigItem.getSourceId()) + "&noStatusRow=-1&formType=3";
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_QUERY){
			action = "/formquery.do?method=formQuery&formid="+String.valueOf(bizConfigItem.getFormAppmainId())+"&queryname="+String.valueOf(bizConfigItem.getSourceId());
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_REPORT){
			action = "/formreport.do?method=formReport&formid="+String.valueOf(bizConfigItem.getFormAppmainId())+"&reportname="+String.valueOf(bizConfigItem.getSourceId());
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_DOCUMENT){
			action = "/doc.do?method=docHomepageIndex&docResId=" + String.valueOf(bizConfigItem.getSourceId());
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_ADMIN_BUL){
			action = "/bulData.do?method=bulMore&spaceType=1&homeFlag=true&typeId="+String.valueOf(bizConfigItem.getSourceId());
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_ADMIN_NEWS){
			action = "/newsData.do?method=newsMore&orgType=account&spaceType=1&isGroup=&homeFlag=true&typeId="+ String.valueOf(bizConfigItem.getSourceId()) +"&type="+String.valueOf(bizConfigItem.getSourceId());
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_ADMIN_BBS){
			action = "/bbs.do?method=listAllArticle&group=&boardId="+String.valueOf(bizConfigItem.getSourceId());
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_ADMIN_INQUIRY){
			action = "/inquirybasic.do?method=more_recent_or_check&group=&typeId="+String.valueOf(bizConfigItem.getSourceId());
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_GROUP_BUL){
			action = "bulData.do?method=bulMore&spaceType=0&homeFlag=true&typeId="+String.valueOf(bizConfigItem.getSourceId());
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_GROUP_NEWS){
			action = "/newsData.do?method=newsMore&orgType=group&spaceType=0&isGroup=&homeFlag=true&&typeId="+ String.valueOf(bizConfigItem.getSourceId()) +"&type="+String.valueOf(bizConfigItem.getSourceId());
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_GROUP_BBS){
			action = "/bbs.do?method=listAllArticle&group=group&boardId="+String.valueOf(bizConfigItem.getSourceId());
		}else if(sourceType == FormBizConfigConstants.SOURCE_TYPE_GROUP_INQUIRY){
			action = "/inquirybasic.do?method=more_recent_or_check&group=group&typeId="+String.valueOf(bizConfigItem.getSourceId());
		}
		return action;
	}
	
}