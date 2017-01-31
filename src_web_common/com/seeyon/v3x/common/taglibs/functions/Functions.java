package com.seeyon.v3x.common.taglibs.functions;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.agent.domain.V3xAgent;
import com.seeyon.v3x.agent.domain.V3xAgentDetail;
import com.seeyon.v3x.agent.manager.AgentIntercalateManager;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.comboTree.ComboTreeNode;
import com.seeyon.v3x.common.comboTree.ComboTreeUtils;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.constants.ProductVersionEnum;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.oncealert.OncealertManager;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.doc.domain.DocMimeType;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;
import com.seeyon.v3x.edoc.domain.EdocObjTeam;
import com.seeyon.v3x.edoc.manager.EdocObjTeamManager;
import com.seeyon.v3x.exchange.domain.ExchangeAccount;
import com.seeyon.v3x.exchange.manager.ExchangeAccountManager;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.online.util.OuterWorkerAuthUtil;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.MemberHelper;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.PluginSystemInit;
import com.seeyon.v3x.util.Cookies;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.JSObject;
import com.seeyon.v3x.util.LightWeightEncoder;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;
import com.seeyon.v3x.worktimeset.exception.WorkTimeSetExecption;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;
import com.seeyon.v3x.worktimeset.manager.WorkTimeSetManager;

/**
 * V3X Functions
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-1-8
 */
public class Functions {
	private static Log log = LogFactory.getLog(Functions.class);
	
	private static final String OrganizationResources = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";

	/**
	 * 类似于out.println(condition ? y : n);
	 * 
	 * @param statment
	 * @param y
	 * @param n
	 * @return
	 */
	public static String outConditionExpression(Boolean statment, String y,
			String n) {
		if (BooleanUtils.isTrue(statment)) {
			return y;
		}

		return n;
	}

	/**
	 * 检测List中是否包含指定object
	 * 
	 * @param <E>
	 * @param list
	 * @param object
	 * @return
	 */
	public static <E> boolean containInCollection(Collection<E> list, E object) {
		if (list == null || list.isEmpty() || object == null) {
			return false;
		}

		return list.contains(object);
	}
	
	/**
	 * 检测List中是否包含指定Integer
	 * 
	 * @param <E>
	 * @param list
	 * @param object
	 * @return
	 */
	public static boolean isIntegerInCollection(Collection<Integer> list, Integer obj) {
		if (list == null || list.isEmpty() || obj == null) {
			return false;
		}
		
		return list.contains(obj);
	}
	
	/**
	 * 将字符串转换成HTML,不包括 \n
	 * @param text
	 * @return
	 */
	public static String toHTMLAlt(String text) {
		return com.seeyon.v3x.util.Strings.toHTMLAlt(text);
	}

	/**
	 * 将字符串转换成HTML，将对\r \n < > & 空格进行转换
	 * 
	 * @param text
	 * @return
	 */
	public static String toHTML(String text) {
		return com.seeyon.v3x.util.Strings.toHTML(text);
	}
	
	/**
	 * 将字符串转换成HTML，将对\r \n < > & 空格不进行转换
	 * 
	 * @param text
	 * @return
	 */
	public static String toHTMLWithoutSpace(String text) {
		return com.seeyon.v3x.util.Strings.toHTML(text, false);
	}
	
	/**
	 * <pre>
	 * 将字符串转换成HTML，空格不进行转换，<b>单引号不转成<code>"&amp;#039;"</code>，而是转为"\'"</b>
	 * 使用场景，作为js参数传入时，需要转移，比如：
	 * <c:set value="OnMouseUp(new DocResource('${docsList.docResource.id}','${<b>v3x:toHTMLWithoutSpaceEscapeQuote</b>(docsList.docResource.frName)}',...
	 * 如使用toHTMLWithoutSpace则会因为单引号被转成<code>"&amp;#039;"</code>而报js错
	 * </pre>
	 */
	public static String toHTMLWithoutSpaceEscapeQuote(String text) {
		return toHTMLWithoutSpace(text).replaceAll("&#039;", "\\\\'");
	}

	/**
	 * 将字符串转换成Javascript，将对\r \n < > & 空格进行转换
	 * 
	 * @param text
	 * @return
	 */
	public static String escapeJavascript(String str) {
		return Strings.escapeJavascript(str);
	}
	public static String escapeQuot(String str) {
		return Strings.escapeQuot(str);
	}
	/**
	 * 国际化,不支持参数
	 * 
	 * @param pageContext
	 * @param key
	 * @return
	 */
	public static String _(PageContext pageContext, String key) {
		return ResourceBundleUtil.getString(pageContext, key);
	}
	
	/**
	 * 
	 * @param baseName
	 * @param key
	 * @return
	 */
	public static String messageFromResource(String baseName, String key) {
		return ResourceBundleUtil.getString(baseName, key);
	}
	
	public static String messageFromBundle(LocalizationContext locCtxt, String key) {
		String val = ResourceBundleUtil.getString(locCtxt, key);
		return val == null ? key : val;
	}
	
	/**
	 * 国际化，参数被序列化成XML
	 * 
	 * @param pageContext
	 * @param key
	 * @param paramXML
	 * @return
	 */
	public static String messageOfParameterXML(PageContext pageContext, String key, String paramXML) {
		Object[] params = null;
		if(Strings.isNotBlank(paramXML)){
			params = (Object[])XMLCoder.decoder(paramXML);
		}
		
		return ResourceBundleUtil.getString(pageContext, key, params);
	}

	/**
	 * 得到当前的语言(字符串zh-cn),用在JS中
	 * 
	 * @param request
	 * @return 如zh-cn
	 */
	public static String getLanguage(HttpServletRequest request) {
		return com.seeyon.v3x.common.i18n.LocaleContext.getLanguage(request);
	}
	
	/**
	 * 得到当前的语言(Locale)
	 * 
	 * @param request
	 * @return
	 */
	public static Locale getLocale(HttpServletRequest request) {
		return com.seeyon.v3x.common.i18n.LocaleContext.getLocale(request);
	}

	/**
	 * 把集合中的元素的某个属性值分隔符连接起来
	 * 
	 * <pre>
	 * <code>
	 *     class Member{
	 *          private long id;
	 *          private String name;
	 *          private Department department;
	 *     }
	 *     class Department{
	 *          private long id;
	 *          private String name;
	 *     }
	 *     
	 *     join(list&lt;Member&gt;, &quot;name&quot;, &quot;,&quot;)	= 人名字的字符串
	 *     join(list&lt;Member&gt;, &quot;department.name&quot;, &quot;,&quot;)	= 部门名字的字符串
	 * </code>
	 * </pre>
	 * 
	 * @param list
	 * @param property
	 *            支持多级属性,用.分隔
	 * @param pageContext
	 *            为了实现分隔符的国际化
	 * @return
	 */
	public static String join(Collection<? extends Object> list,
			String properties, PageContext pageContext) {
		if (list == null || list.isEmpty() || properties == null
				|| pageContext == null) {
			return null;
		}

		String separator = _(pageContext, "common.separator.label");

		return join(list, properties, separator);
	}

	/**
	 * 与<code>join(Collection, String, PageContext)</code>雷同，只是分隔符在调用放指定，常用于id的分割
	 * 
	 * @param list
	 * @param properties
	 * @param separator
	 *            分隔符，如果要实现国际化，请用<code>join(Collection, String, PageContext)</code>
	 * @return
	 */
	public static String join(Collection<? extends Object> list,
			String properties, String separator) {
		if (list == null || list.isEmpty() || properties == null) {
			return null;
		}

		List<Object> objects = new ArrayList<Object>();

		String[] props = properties.split("[.]");
		for (Object object : list) {
			for (int i = 0; i < props.length; i++) {
				String property = props[i];
				if(object == null){
					log.warn("", new Exception("Collection中的数据有null"));
					break;
				}
				
				try {
					object = PropertyUtils.getProperty(object, property);
				}
				catch (Exception e) {
					log.error("从[" + object + "]中获取属性'" + property + "'错误", e);
				}
			}

			objects.add(object);
		}

		return StringUtils.join(objects.iterator(), separator);
	}
	
	/**
	 * 将集合连接起来，分隔符采用系统默认
	 * 
	 * @param list
	 * @param pageContext
	 * @return
	 */
	public static String join(Collection<? extends Object> list, PageContext pageContext){
		String separator = _(pageContext, "common.separator.label");
		return join(list, separator);
	}
	
	/**
	 * 将集合连接起来
	 * @param list
	 * @param separator 分隔符
	 * @return
	 */
	public static String join(Collection<? extends Object> list, String separator){
		if (list == null || list.isEmpty()) {
			return null;
		}
		
		return StringUtils.join(list.iterator(), separator);
	}

	/**
	 * 显示异常，在抛出异常时，需要制定resource key 和对应的Parameter。如：<br>
	 * 
	 * <pre>
	 *   Manager: throw new BusinessException(&quot;fileupload.exception.MaxSize&quot;, maxSize);
	 *    
	 *    Controller:
	 *    try {
	 *    }
	 *    catch (BusinessException e) {
	 *      modelAndView.addObject(&quot;e&quot;, e);
	 *    }
	 *   
	 *    JSP :
	 *    &lt;c:if test=&quot;${e ne null}&quot;&gt;
	 *    &lt;script type=&quot;text/javascript&quot;&gt;
	 *    alert("${v3x:showException(e, pageContext)}");
	 *    &lt;/script&gt;
	 *    &lt;/c:if&gt;
	 * </pre>
	 * 
	 * 在该方法中直接alert(异常内容)，自动完成国际化
	 * 
	 * @param e
	 * 
	 */
	public static String showException(BusinessException e,
			PageContext pageContext) {
		if (e == null) {
			return null;
		}

		String key = e.getErrorCode();	
		
		String message = "";

		if (StringUtils.isNotBlank(key)) {
			Object[] parameters = e.getErrorArgs();
			message = ResourceBundleUtil.getString(pageContext, key, parameters);
		}
		else{
			message = e.toString();
		}
		
		if(StringUtils.isNotBlank(message)){
			return (escapeJavascript(message));
		}
		
		return null;
	}
	
	/**
	 * 显示长度
	 * 
	 * @param content
	 * @param len
	 * @param symbol
	 * @return
	 */
	public static String getLimitLengthString(String content, int len,
			String symbol){
		return Strings.getLimitLengthString(content, len, symbol);
	}
	
	/**
	 * 获取按照指定长度截取之后的字符串内容，按照UTF-8编码<br>
	 * 上传文件时，文件名中的空格，在提交之后会转为UTF-8编码空格，而默认的截取字符串内容<br>
	 * 在不设定字符集时，是采用GBK编码，为兼容，增加此方法，指定字符集为UTF-8<br>
	 */
	public static String getLimitLengthStringUTF8(String content, int len,
			String symbol){
		if(len < 0){
			return content;
		}
		
		try {
			return Strings.getLimitLengthString(content, "UTF-8", len, symbol);
		}
		catch (UnsupportedEncodingException e) {
			return content;
		}
	}
	
	/**
	 * 显示常用语调用
	 * 
	 * <pre>
	 * &lt;script type=&quot;text/javascript&quot; src=&quot;&lt;c:url value=&quot;/apps_res/v3xmain/js/phrase.js&quot; /&gt;&quot;&gt;&lt;/script&gt;
	 * ${v3x:showCommonPhrase(pageContext)}
	 * &lt;a href=&quot;javascript:showPhrase()&quot;&gt;Phrase&lt;/a&gt;
	 * </pre>
	 * 
	 * @param pageContext
	 * @return
	 */
	public static String showCommonPhrase(PageContext pageContext){
		StringBuffer str = new StringBuffer();
		
		try {
			String url = com.seeyon.v3x.portlets.bridge.spring.taglibs.LinkTag.calculateURL("/phrase.do?method=list", pageContext);
		
			str.append("<script type=\"text/javascript\">\n")
			   .append("var phraseURL = '" + url + "';\n")
			   .append("</script>\n");
		
			str.append("<div oncontextmenu=\"return false\"\n")
			   .append("    class=\"border-tree\" style=\"position:absolute; right:20px; top:100px; width:260px; height:160px; z-index:2; background-color: #ffffff;display:none;overflow:no;\"\n")
			   .append("     id=\"divPhrase\" onMouseOver=\"showPhrase()\" onMouseOut=\"hiddenPhrase()\">\n")
			   .append("    <IFRAME width=\"100%\" id=\"phraseFrame\" name=\"phraseFrame\" height=\"100%\" frameborder=\"0\" align=\"middle\" scrolling=\"no\"\n")
			   .append("            marginheight=\"0\" marginwidth=\"0\"></IFRAME>\n")
			   .append("</div>\n");
		}
		catch (JspException e) {
			log.error("", e);
		}
		
		return str.toString();
	}
	
	/**
	 * 获取系统配置
	 * 
	 * @param key
	 * @return
	 */
	public static String getSystemProperty(String key){
		return SystemProperties.getInstance().getProperty(key);
	}
	
	/********************************** 组织模型相关 *****************************************/
	public static User currentUser(){
		return CurrentUser.get();
	}
	
	private static OrgManagerDirect orgManagerDirect = null;
	private static OrgManager orgManager = null;
	private static OrgManager getOrgManager(){
		if(orgManager == null){
			orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		}
		
		return orgManager;
	}
	
	private static OrgManagerDirect getOrgManagerDirect(){
		if(orgManagerDirect == null){
			orgManagerDirect = (OrgManagerDirect)ApplicationContextHolder.getBean("OrgManagerDirect");
		}
		
		return orgManagerDirect;
	}
	
	private static EdocObjTeamManager edocTeamManager = null;
	private static EdocObjTeamManager getEdocTeamManager(){
		if(edocTeamManager == null){
			edocTeamManager = (EdocObjTeamManager)ApplicationContextHolder.getBean("edocObjTeamManager");
		}
		
		return edocTeamManager;
	}
	
	private static ExchangeAccountManager exchangeAccountManager=null;
	private static ExchangeAccountManager getExchangeAccountManager(){
		if(exchangeAccountManager == null){
			exchangeAccountManager = (ExchangeAccountManager)ApplicationContextHolder.getBean("exchangeAccountManager");
		}
		
		return exchangeAccountManager;
	}
	
    private static SystemConfig systemConfig = null;
    private static SystemConfig getSystemConfig(){
        if(systemConfig == null){
        	systemConfig = (SystemConfig)ApplicationContextHolder.getBean("systemConfig");
        }
        return systemConfig;
    }
    
    private static ConfigManager configManager = null;
    private static ConfigManager getConfigManager(){
        if(configManager == null){
            configManager = (ConfigManager)ApplicationContextHolder.getBean("configManager");
        }
        return configManager;
    }
	
	public static V3xOrgEntity getEntity(String entityType, long entityId) {
		if(entityId == -1){
			return null;
		}
		
		try {
			return getOrgManager().getGlobalEntity(entityType, entityId);
		}
		catch (Exception e) {
			log.warn("", e);
			return null;
		}
	}

	public static V3xOrgMember getMember(long memberId) {
		if(memberId == -1){
			return null;
		}
		
		try {
			return getOrgManager().getMemberById(memberId);
		}
		catch (Exception e) {
			log.warn("", e);
			return null;
		}
	}
	
	/**
	 * 显示人员名字，不带单位简称
	 * 
	 * @param memberId
	 * @return
	 */
	public static String showMemberNameOnly(long memberId){
		if(memberId == 1){
			return ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.account_form.systemAdminName.value");
		}
		else if(memberId == 0){
			return ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.auditAdminName.value");			
		}
		
		V3xOrgMember m = getMember(memberId);
		if(m == null){
			return null;
		}
		V3xOrgAccount account1 = getAccount(m.getOrgAccountId());
		if(account1 != null && account1.getIsRoot()){
			return ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.account_form.groupAdminName.value" + suffix());
		}
		return m.getName();
	}
	
	
	/**
	 * 显示人员名字，如果不是一个单位的，则显示单位简称
	 * 
	 * @param memberId
	 * @return
	 */
	public static String showMemberName(long memberId){
		if(memberId == V3xOrgEntity.CONFIG_SYSTEM_ADMIN_ID){
			return ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.account_form.systemAdminName.value");
		}
		else if(memberId == V3xOrgEntity.CONFIG_AUDIT_ADMIN_ID){
			return ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.auditAdminName.value");			
		}
		else if(memberId == V3xOrgEntity.CONFIG_SYSTEM_AUTO_TRIGGER_ID){
			return ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.system.auto.trigger");			
		}
		V3xOrgMember m = getMember(memberId);
		return showMemberName(m);
	}
	/**
	 * 人员所在单位的名称
	 * @param memberId
	 * @return
	 */
	public static String showOrgAccountNameByMemberid(long memberId){
		if(memberId == 1 || memberId == 0){
			return "-" ;
		}
		V3xOrgMember m = getMember(memberId) ;	
		if(m  == null){
			log.error("获取的人员为空") ;
			return null ;
		}
		return showOrgAccountName(m.getOrgAccountId()) ;
	}
	
	public static String showOrgAccountName(long accountId){
		V3xOrgAccount acconut = getAccount(accountId) ;
		try{
			if(acconut != null && acconut.getIsRoot()){
				return "-" ;
			}
			return acconut.getName() ;
		}catch(Exception e){
			log.error("得到单位的为null") ;
			return "-" ;
		}
	}
	/**
	 * 人员的岗位名称
	 * @param memberId
	 * @return
	 */
	public static String showOrgPostNameByMemberid(long memberId){
		if(memberId == 1 || memberId == 0){
			return "-" ;
		}
		V3xOrgMember m = getMember(memberId) ;	
		if(m  == null){
			log.error("获取的人员为空") ;
			return null ;
		}
		if(m.getIsAdmin()){
			return "-"  ;
		}
		return showOrgPostName(m.getOrgPostId()) ;
	}
	
	public static String showOrgPostName(long postId){
		V3xOrgPost v3xOrgPost = getPost(postId) ;
		try{
			return v3xOrgPost.getName() ;
		}catch(Exception e){
			log.error("得到v3xOrgPost的为null") ;
			return "-" ;
		}
	}
	
	/**
	 * 人员的职务级别的名称
	 * @param memberId
	 * @return
	 */
	public static String showMemberLeave(long memberId){
		if(memberId == 1 || memberId == 0){
			return "-" ;
		}
		
		V3xOrgMember m = getMember(memberId);	
		
		if(m  == null){
			log.error("获取的人员为空") ;
			return null ;
		}
		
		return showOrgLeaveName(m) ;
	}	
	
	public static String showOrgLeaveName(V3xOrgMember m){
		
		if(m.getIsAdmin()){
			return "-" ;
		}
		
		V3xOrgLevel level =  getLeave(m.getOrgLevelId()) ;
		if(level == null){
			log.error("获取的职务级别为空") ;
			return null ;
		}	
		return level.getName() ;
	}
	
	public static String showOrgLeaveName(V3xOrgLevel level){		
		if(level == null){
			log.error("获取的职务级别为空") ;
			return null ;
		}	
		return level.getName() ;
	}

	
	public static V3xOrgLevel getLeave(long leaveId){
		if(leaveId == -1){
			return null;
		}		
		try {
			return getOrgManager().getLevelById(leaveId);
		}
		catch (Exception e) {
			log.warn("", e);
			return null;
		}		
	}
	
	/**
	 * 显示人员名字，如果不是一个单位的，则显示单位简称
	 * 
	 * @param member
	 * @return
	 */
	public static String showMemberName(V3xOrgMember member){
		if(member == null){
			return null;
		}
		
		if(!(Boolean)SysFlag.selectPeople_showAccounts.getFlag()){
			return member.getName();
		}
		
		User user = CurrentUser.get();
		
		if(user == null || user.getLoginAccount() == member.getOrgAccountId().longValue()){ //同一个单位的
			return member.getName();
		}
		else{
			V3xOrgAccount account1 = getAccount(member.getOrgAccountId());
			if(null == account1) {
				return member.getName();
			} else 	if(account1.getIsRoot()){
                String groupAdminLabel = "org.account_form.groupAdminName.value" + suffix();
				return ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", groupAdminLabel);
			}
			
			return member.getName() + "(" + account1.getShortname() + ")";
		}
	}
	
	/**
	 * 显示人员的基础信息
	 * 
	 * @param memberId
	 * @return
	 */
	public static String showMemberAlt(long memberId){
		V3xOrgMember m = getMember(memberId);
        if(m == null){
            return null;
        }
		return showMemberAlt(m);
	}
	
	/**
	 * 显示人员的基础信息
	 * 
	 * @param member
	 * @return
	 */
	public static String showMemberAlt(V3xOrgMember member){
		return showMemberAlt(member, false);
	}
	
	/**
	 * 显示人员的基础信息，部门显示全称
	 * 
	 * @param memberId
	 * @return
	 */
	public static String showMemberAltWithFullDeptPath(long memberId){
		V3xOrgMember m = getMember(memberId);
		if(m == null){
			return null;
		}
		return showMemberAltWithFullDeptPath(m);
	}
	
	/**
	 * 显示人员的基础信息，部门显示全称
	 * 
	 * @param member
	 * @return
	 */
	public static String showMemberAltWithFullDeptPath(V3xOrgMember member){
		return showMemberAlt(member, true);
	}
	
	private static String showMemberAlt(V3xOrgMember member, boolean isShowFullDepartmentPath){
		if(member == null || Boolean.TRUE.equals(member.getIsAdmin())){
			return null;
		}
		
		User user = CurrentUser.get();
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(Strings.toHTMLAlt(showMemberName(member)));
		
		ResourceBundle rb = ResourceBundle.getBundle("com.seeyon.v3x.main.resources.i18n.MainResources", user.getLocale());
		
		String departName = null;
		if(isShowFullDepartmentPath){
			departName = showDepartmentFullPath(member.getOrgDepartmentId());
		}
		else{
			V3xOrgDepartment dept = getDepartment(member.getOrgDepartmentId());
			if(dept != null){
				departName = dept.getName();
			}
		}
		
		if(Strings.isNotBlank(departName)){
			sb.append("\n");
			sb.append(ResourceBundleUtil.getString(rb, "org.department.label")).append(" : ").append(departName);
		}
		
		V3xOrgPost post = getPost(member.getOrgPostId());
		if(post != null){
			sb.append("\n");
			sb.append(ResourceBundleUtil.getString(rb, "org.post.label")).append(" : ").append(post.getName());
		}
		
//		V3xOrgEntity level = getLevel(member.getOrgLevelId());
//		if(level != null){
//			sb.append("\n");
//			sb.append(ResourceBundleUtil.getString(rb, "org.level.label")).append(" : ").append(level.getName());
//		}
		
		return sb.toString();
	}
	
	public static String showDepartmentFullPath(long departmentId){
		StringBuffer sb = new StringBuffer();
		try {
			V3xOrgDepartment dept = getDepartment(departmentId);
			if(dept != null){
				List<V3xOrgDepartment> pDs = orgManager.getAllParentDepartments(departmentId);
				for (V3xOrgDepartment department : pDs) {
					sb.append(department.getName()).append("/");
				}
				sb.append(dept.getName());
			}
			
		}
		catch (Exception e) {
		}
		
		return sb.toString(); 
	}
	
    /**
     * 显示关联人员的基础信息
     * @param memberId
     */
    public static String showRelateMemberAlt(long memberId){
        V3xOrgMember member = getMember(memberId);
        if(member == null){
            return null;
        }
        User user = CurrentUser.get();
        
        StringBuffer sb = new StringBuffer();
        ResourceBundle rb = ResourceBundle.getBundle("com.seeyon.v3x.main.resources.i18n.MainResources", user.getLocale());
        
        V3xOrgDepartment dept = getDepartment(member.getOrgDepartmentId());
        if(dept != null){
            sb.append(ResourceBundleUtil.getString(rb, "org.department.label")).append(" : ").append(dept.getName());
            sb.append("\n");
        }
        
        V3xOrgPost post = getPost(member.getOrgPostId());
        if(post != null){
            sb.append(ResourceBundleUtil.getString(rb, "org.post.label")).append(" : ").append(post.getName());
            sb.append("\n");
        }
        
        try {
            String telNumber = member.getProperty("officeNum");
            if(Strings.isBlank(telNumber)){
                telNumber = member.getTelNumber();;
            }
            if(Strings.isNotBlank(telNumber)){
                sb.append(ResourceBundleUtil.getString(rb, "org.telNumber.label")).append(" : ").append(telNumber);
            }
        }
        catch (BusinessException e) {
            log.warn("", e);
        }
        
        return sb.toString();
    }
    
	public static V3xOrgPost getPost(long postId) {
		try {
			return getOrgManager().getPostById(postId);
		}
		catch (Exception e) {
			log.warn("", e);
			return null;
		}
	}
	
	public static V3xOrgDepartment getDepartment(long departmentId) {
		try {
			return getOrgManager().getDepartmentById(departmentId);
		}
		catch (Exception e) {
			log.warn("", e);
			return null;
		}
	}
	
	public static V3xOrgLevel getLevel(long levelId) {
		try {
			return getOrgManager().getLevelById(levelId);
		}
		catch (Exception e) {
			log.warn("", e);
			return null;
		}
	}
	
	public static V3xOrgAccount getAccount(long accountId) {
		try {
			return getOrgManager().getAccountById(accountId);
		}
		catch (Exception e) {
			log.warn("", e);
			return null;
		}
	}
	
	public static String getAccountShortName(long accountId) {
		try {
			V3xOrgAccount a = getAccount(accountId);
			if(a != null){
				return a.getShortname();
			}
		}
		catch (Exception e) {
			log.warn("", e);
		}
		
		return null;
	}
	
	public static V3xOrgTeam getTeam(long teamId) {
		try {
			return getOrgManager().getTeamById(teamId);
		}
		catch (Exception e) {
			log.warn("", e);
			return null;
		}
	}
	
	public static String getTeamName(long teamId) {
		try {
			return getTeam(teamId).getName();
		}
		catch (Exception e) {
			log.warn("", e);
			return null;
		}
	}
	
	/**
	 * 我的单位，包括兼职单位
	 * 
	 * @return
	 */
	public static List<V3xOrgAccount> concurrentAccount(){
		User user = CurrentUser.get();
		try {
			List<V3xOrgAccount> a = getOrgManager().concurrentAccount(user.getId());
			if(a != null){
				return a;
			}
			
			return new ArrayList<V3xOrgAccount>();
		}
		catch (Exception e) {
			return new ArrayList<V3xOrgAccount>();
		}
	}
	
	/**
	 * 我能访问的单位
	 * 
	 * @return
	 */
	public static List<V3xOrgAccount> accessableAccounts(){
		User user = CurrentUser.get();
		try {
			return getOrgManager().accessableAccounts(user.getId());
		}
		catch (BusinessException e) {
			return null;
		}
	}
	/**
	 * 我能否访问集团
	 * 
	 * @return 
	 */
	public static boolean isGroupAccessable(long accountId){
		try {
			return getOrgManager().getAccountById(accountId).isGroupAccessable();
		}
		catch (Exception e) {
			return false;
		}
	}
	/**
	 * 将对象人员/部门/单位等数据链接起来，显示方式为： (致远)开发中心、(股份)U8事业本部、(金融)赵大伟
	 * 
	 * 自动根据
	 * 
	 * <pre>
	 * public class TempleteAuth extends BaseModel implements Serializable {
	 * 	private Long authId;
	 * 	private String authType;
	 * 	private Integer sort;
	 * 	private long templeteId;
	 * 
	 *  //setter / getter
	 * }
	 * </pre>
	 * 
	 * ${v3x:showOrgEntities(List<TempleteAuth>, "authId", "authType", pageContext)}
	 * 
	 * @param list 数据集合
	 * @param idProperty V3xOrgEntity的id
	 * @param typeProperty V3xOrgEntity的type
	 * @param pageContext
	 * @return
	 */
	public static String showOrgEntities(Collection<? extends Object> list, String idProperty, String typeProperty, PageContext pageContext){
		if (list == null || list.isEmpty() || StringUtils.isBlank(idProperty)
				|| StringUtils.isBlank(typeProperty)) {
			return null;
		}
				
		List<String[]> entities = new ArrayList<String[]>();
		for (Object object : list) {
			if(object == null){
				log.warn("", new Exception("Collection中的数据有null"));
				continue;
			}
			try {
				String type = String.valueOf(PropertyUtils.getProperty(object, typeProperty));
				String id = String.valueOf(PropertyUtils.getProperty(object, idProperty));
				
				if(StringUtils.isBlank(type) || id == null
						|| id.equals(String.valueOf(com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID))){
					continue;
				}

				entities.add(new String[]{type, id});
			}
			catch (Exception e) {
				log.error("取得Bean属性", e);
			}
		}
		
		String separator = _(pageContext, "common.separator.label");
		return showOrgEntities1(entities, separator);
	}
	
	/**
	 * 将EntityType|EntityId,EntityType|EntityId转换成名称字符串
	 * 
	 * @param typeAndIds Member|13241234,Department|23452345234
	 * @return (致远)开发中心、(股份)U8事业本部、(金融)赵大伟
	 */
	public static String showOrgEntities(String typeAndIds, PageContext pageContext) {
		if(StringUtils.isBlank(typeAndIds)){
			return null;
		}
		
		StringTokenizer str = new StringTokenizer(typeAndIds, ",|");
		List<String[]> entities = new ArrayList<String[]>();
		while (str.hasMoreTokens()) {
			String type = str.nextToken();
			String id = str.nextToken();
			
			entities.add(new String[]{type, id});
		}
		
		String separator = _(pageContext, "common.separator.label");
		
		return showOrgEntities1(entities, separator);
	}
	
	/**
	 * 将EntityType|EntityId,EntityType|EntityId转换成名称字符串
	 * * @param separator 分隔符
	 * @param typeAndIds Member|13241234,Department|23452345234
	 * @return (致远)开发中心、(股份)U8事业本部、(金融)赵大伟
	 */
	public static String showOrgEntities(String typeAndIds, String separator) {
		if(StringUtils.isBlank(typeAndIds)){
			return null;
		}
		
		StringTokenizer str = new StringTokenizer(typeAndIds, ",|");
		List<String[]> entities = new ArrayList<String[]>();
		while (str.hasMoreTokens()) {
			String type = str.nextToken();
			String id = str.nextToken();
			
			entities.add(new String[]{type, id});
		}
		
		return showOrgEntities1(entities, separator);
	}
	
	/**
	 * 将格式为EntityId,EntityId的数据转换成Element[]
	 * 
	 * @param ids 1234123,234534563
	 * @param type 指定类型
	 * @param separator	显示内容的间隔符号
	 * @return
	 */
	public static String showOrgEntities(String ids, String type, String separator) {
		if(StringUtils.isBlank(ids)){
			return null;
		}
		List<String[]> entities = new ArrayList<String[]>();
		
		String[] idstr = ids.split(",");
		for (String id : idstr) {
			entities.add(new String[]{type, id});
		}
		
		return showOrgEntities1(entities, separator);
	}
	
	/**
	 * 将格式为EntityId,EntityId的数据转换成Element[]
	 * 
	 * @param ids 1234123,234534563
	 * @param type 指定类型
	 * @param pageContext
	 * @return
	 */
	public static String showOrgEntities(String ids, String type, PageContext pageContext) {
		String separator = _(pageContext, "common.separator.label");
		return showOrgEntities(ids, type, separator);
	}
	
	public static String showOrgEntities(List<Object[]> entities, String separator){
		List<String[]> entities1 = new ArrayList<String[]>();
		for (Object[] strings : entities) {
			entities1.add(new String[]{String.valueOf(strings[0]), String.valueOf(strings[1])});
		}
		
		return showOrgEntities1(entities1, separator);
	}
	
	/**
	 * 组装最终显示的人员信息
	 * 
	 * @param entities Object[(Stirng)类型, (Long)对应id]
	 * @param separator 分隔符
	 * @return 王文京(集团)、徐石、胡守云、王五(金融)
	 */
	private static String showOrgEntities1(List<String[]> entities, String separator){
		if(entities == null || entities.isEmpty()){
			return null;
		}
		
		long loginAccountId = CurrentUser.get().getLoginAccount();
		
		boolean isShowAccountShortname = false;
		boolean isAdmin = false;
		List<Long> accountIds = new ArrayList<Long>();
		List<String> names = new ArrayList<String>();
		
		for (String[] object : entities) {
			try {
				String typeStr = object[0];
				String idStr = object[1];
				
				if(idStr.equals(String.valueOf(com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID))){
					return null;
				}
				
				Object[] elements = parseElement(typeStr, idStr);
				//数据不存在
				if(elements == null){
					continue;
				}
				
				long entityAccountId = (Long)elements[1];
				
				if(!isShowAccountShortname && loginAccountId != entityAccountId){
					isShowAccountShortname = true;
				}
				
				if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(typeStr)){
					accountIds.add(com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID);
				}
				else{
					accountIds.add(entityAccountId);
				}
				V3xOrgEntity entity = getOrgManager().getGlobalEntity(typeStr, Long.parseLong(idStr));
				//判断V3xOrgMember是否为管理员
				if(entity instanceof V3xOrgMember){
					V3xOrgMember member = (V3xOrgMember) entity;
					if(member.getIsAdmin()){
						V3xOrgAccount account = getAccount(member.getOrgAccountId());
						if(null == account) continue;
						if(account.getIsRoot()){
							names.add(ResourceBundleUtil.getString(OrganizationResources, "org.account_form.groupAdminName.value" + (String)SysFlag.EditionSuffix.getFlag()));
						} else {
							names.add(account.getName() + ResourceBundleUtil.getString(OrganizationResources, "org.account_form.adminName.value"));
						}
						isAdmin = true;
					} else {
						names.add((String)elements[0]);
					}
				} else {
					names.add((String)elements[0]);
				}
			}
			catch (Exception e) {
				log.error("取得Bean属性", e);
			}
		}
		
		if(isShowAccountShortname){
			for (int i = 0; i < accountIds.size(); i++) {
				long aId = accountIds.get(i);
				if(aId == com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID || loginAccountId == aId){
					continue;
				}
				
				V3xOrgAccount account = getAccount(aId);
				
				if(account != null && !isAdmin){
					String name = names.get(i) + "(" + account.getShortname() + ")";
					names.set(i, name);
				}
			}
		}
		
		return join(names, separator);
	}
	
	/**
	 * 将授权、发布范围等信息连接成elements 格式EntityType|EntityId|EntityName|AccountId<br>
	 * 注意：id或者type为null，以及id=-1的将被过滤掉
	 * 
	 * <pre>
	 * public class TempleteAuth extends BaseModel implements Serializable {
	 * 	private Long authId;
	 * 	private String authType;
	 * 	private Integer sort;
	 * 	private long templeteId;
	 * 
	 *  //setter / getter
	 * }
	 * 
	 * 转换
	 * parseElements(List<TempleteAuth>, "authId", "authType")
	 * 
	 * 结果
	 * Member|1234123|谭敏锋|34561234,Department|234534563|开发中心|34561234
	 * </pre>
	 * 
	 * @param list
	 *            发布范围、授权集合
	 * @param idProperty
	 *            组织模型实体的Id字段的属性
	 * @param typeProperty
	 *            组织模型实体的类型字段的属性
	 * @param accountType
	 *            组织模型实体的所属单位字段的属性
	 * @return
	 */
	public static String parseElements(Collection<? extends Object> list,
			String idProperty, String typeProperty) {
		if (list == null || list.isEmpty() || StringUtils.isBlank(idProperty)
				|| StringUtils.isBlank(typeProperty)) {
			return null;
		}
		
		List<String> elements = new ArrayList<String>();
		for (Object object : list) {
			if(object == null){
				log.warn("", new Exception("Collection中的数据有null"));
				break;
			}
			try {
				String type = String.valueOf(PropertyUtils.getProperty(object, typeProperty));
				String id = String.valueOf(PropertyUtils.getProperty(object, idProperty));
				
				if(StringUtils.isBlank(type) || StringUtils.isBlank(id)){
					continue;
				}
				
				String e = parseElement1(type, id);
				if(StringUtils.isNotBlank(e)){
					elements.add(e);
				}
			}
			catch (Exception e) {
				log.error("取得Bean属性", e);
			}
		}

		return StringUtils.join(elements.iterator(), ",");
	}
	
	/**
	 * 将格式为EntityType|EntityId,EntityType|EntityId的数据转换成Element[]
	 * 
	 * @param typeAndIds Member|1234123,Department|234534563
	 * @return
	 */
	public static String parseElements(String typeAndIds) {
		if(StringUtils.isBlank(typeAndIds)){
			return null;
		}
		List<String> elements = new ArrayList<String>();
		StringTokenizer str = new StringTokenizer(typeAndIds, "|,");
		
		while (str.hasMoreTokens()) {
			String type = str.nextToken();
			String id = str.nextToken();

			String e = parseElement1(type, id);
			if(StringUtils.isNotBlank(e)){
				elements.add(e);
			}
		}
		
		return StringUtils.join(elements.iterator(), ",");
	}
	
	/**
	 * 将格式为EntityId,EntityId的数据转换成Element[]
	 * 
	 * @param ids 1234123,234534563
	 * @param type 指定类型
	 * @return
	 */
	public static String parseElements(String ids, String type) {
		if(StringUtils.isBlank(ids)){
			return null;
		}
		List<String> elements = new ArrayList<String>();
		
		String[] idstr = ids.split(",");
		for (String id : idstr) {
			String e = parseElement1(type, id);
			if(StringUtils.isNotBlank(e)){
				elements.add(e);
			}
		}
		
		return StringUtils.join(elements.iterator(), ",");
	}
	
	private static String parseElement1(String typeStr, String idStr){
		Object[] elements = parseElement(typeStr, idStr);
		if(elements == null){
			return null;
		}
		
		return typeStr + "|" + idStr + "|" + elements[0] + "|" + elements[1] + "|" + elements[2];
	}
	
	private static Object[] parseElement(String typeStr, String idStr){
		try {
			String[] types = typeStr.split("_");
			String[] ids   = idStr.split("_");
			
			List<String> elementName = new ArrayList<String>();
			long accountId = -1;
			String isEnabled = "true";
			for(int j = 0; j < types.length; j++) {
				String type = types[j];
				String id = ids[j];
				
				//固定角色
				if(type.equals(V3xOrgEntity.ORGENT_TYPE_ROLE) && !StringUtils.isNumeric(id)){
					String label = ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources", "sys.role.rolename." + id);
					elementName.add(label);
				}
				else if(type.equals(EdocObjTeam.ENTITY_TYPE_OrgTeam))
				{
					EdocObjTeam et=getEdocTeamManager().getById(Long.parseLong(ids[j]));
					if(et==null){return null;}
					elementName.add(et.getName());
					accountId = et.getOrgAccountId();
				}
				else if(type.equals(ExchangeAccount.ENTITY_TYPE_EXCHANGEACCOUNT))
				{//外部单位
					ExchangeAccount ea=getExchangeAccountManager().getExchangeAccount(Long.parseLong(ids[j]));
					if(ea==null){return null;}
					elementName.add(ea.getName());
					accountId = ea.getDomainId();
				}
				else if(type.equals("FormField"))
				{//表单控件
					elementName.add(id);
				}
				else{
					V3xOrgEntity entity = getOrgManager().getGlobalEntity(type, Long.parseLong(ids[j]));
					if(entity == null){
						return null;
					}
					elementName.add(entity.getName());
					accountId = entity.getOrgAccountId();
					isEnabled = entity.getIsDeleted() == null || entity.getIsDeleted().booleanValue() ? "false" : "true";
				}
			}
			
			return new Object[]{StringUtils.join(elementName.iterator(), "-"), accountId, isEnabled};
		}
		catch (Exception e) {
			log.error("取得Bean属性", e);
		}
		
		return null;
	}
	
	/**
	 * 与当前登录者比较是否是同一个单位的（包括兼职单位）
	 * 
	 * @param memberId 被检测对象
	 * @return true-是同一个单位的
	 */
	public static boolean isSameAccount(long memberId){
		try {
			List<V3xOrgAccount> myAccounts = concurrentAccount();
			long aId = getOrgManager().getMemberById(memberId).getOrgAccountId();
			
			for (V3xOrgAccount account : myAccounts) {
				if(account.getId().equals(aId)){
					return true;
				}
			}
		}
		catch (Exception e1) {
			log.error("", e1);
		}
		
		return false;
	}
	
	/**
	 * 该单位是否是我的单位（包含兼职）
	 * 
	 * @param accountId
	 * @return
	 */
	public static boolean isMyAccount(Long accountId){
		List<V3xOrgAccount> myAccounts = concurrentAccount();
		for (V3xOrgAccount account : myAccounts) {
			if(account.getId().equals(accountId)){
				return true;
			}
		}
		
		return false;
	}
	
	public static String toString(Object o){
		return String.valueOf(o);
	}
	
	public static Set<Object> keys(Map<Object, Object> map){
		if(map != null){
			return map.keySet();
		}
		
		return null;
	}
	public static Collection<Object> mapValues(Map<Object,Object> map){
		if(map != null){
			return map.values();
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	public static String encodeURI(String p){
		try {
			return java.net.URLEncoder.encode(p,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			return java.net.URLEncoder.encode(p);
		}
	}
	
	public static ApplicationCategoryEnum getApplicationCategoryEnum(int key){
		return ApplicationCategoryEnum.valueOf(key);
	}
	
	public static ApplicationSubCategoryEnum getApplicationSubCategoryEnum(int key){
		return ApplicationSubCategoryEnum.valueOf(key);
	}
	
	/**
	 * 
	 * @param list
	 * @param o
	 * @return
	 */
	public static List<Object> addToList(List<Object> list, Object o){
		if(list == null){
			list = new ArrayList<Object>();
		}
		
		list.add(o);
		
		return list;
	}
	
	/**
	 * 读取系统标志
	 * 
	 * @param flagName 标志名称
	 * @return
	 */
	public static Object getSysFlag(String flagName){
		SysFlag sysFlag = SysFlag.valueOf(flagName);

		return getSysFlag(sysFlag);
	}
	
	/**
	 * 读取系统标志
	 * 
	 * @param sysFlag
	 * @return
	 */
	public static Object getSysFlag(SysFlag sysFlag){
		if(sysFlag == null){
			return null;
		}
		
		return sysFlag.getFlag();
	}
	
	/**
	 * 根据当前用户判断浏览器差异
	 * @param flagName
	 * @param user
	 * @return
	 */
	public static Object getBrowserFlag(String flagName, User user){
		BrowserFlag browserFlag = BrowserFlag.valueOf(flagName);
		return browserFlag.getFlag(user);
	}
	
	/**
	 * 根据request请求判断浏览器差异
	 * @param flagName
	 * @param request
	 * @return
	 */
	public static Object getBrowserFlag(String flagName, HttpServletRequest request){
		BrowserFlag browserFlag = BrowserFlag.valueOf(flagName);
		return browserFlag.getFlag(request);
	}
	
	public static String bodyTypeSelector(String v3xjsObj){
		//if(!SystemEnvironment.hasPlugin("officeOcx")){return null;}
		boolean hasPluginOffice=SystemEnvironment.hasPlugin("officeOcx");
		boolean hasPluginPdf=SystemEnvironment.hasPlugin("pdf");
		return "createOfficeMenu("+v3xjsObj+","+hasPluginOffice+","+hasPluginPdf+")";
	}
	
	/**
	 * 是否安装了Office控件
	 * @return
	 */
	public static boolean isOfficeOcxEnable(){
		return SystemEnvironment.hasPlugin("officeOcx");
	}
	
	public static String urlEncoder(String s) {
		try {
			return java.net.URLEncoder.encode(s, "UTF-8");
		}
		catch (Exception e) {
		}

		return null;
	}
	
	public static String urlDecoder(String s) {
		try {
			return java.net.URLDecoder.decode(s, "UTF-8");
		}
		catch (Exception e) {
		}
		
		return null;
	}
	
	/**
	 * 对字符串进行轻度加密
	 * @param s
	 * @return
	 */
	public static String encodeStr(String s) {
		try {
			if(Strings.isNotBlank(s))
				return LightWeightEncoder.encodeString(s);
		}
		catch (Exception e) {
		}
		
		return null;
	}
	
	/**
	 * 对轻度加密过的字符串进行解密
	 * @param s
	 * @return
	 */
	public static String decodeStr(String s) {
		try {
			if(Strings.isNotBlank(s))
				return LightWeightEncoder.decodeString(s);
		}
		catch (Exception e) {
		}
		
		return null;
	}

	/**
	 * 得到应用类别的显示文本，支持插件，需要在插件定义文件中配置applicationCategory属性
	 * 
	 * @param applicationCategory
	 * @param pageContext
	 * @return
	 */
	public static String getApplicationCategoryName(int applicationCategory, PageContext pageContext){
		if(applicationCategory < 100){
			return ResourceBundleUtil.getString(pageContext, "application." + applicationCategory + ".label");
		}
		else{
			return PluginSystemInit.getInstance().getPluginApplicationCategoryName(applicationCategory);
		}
	}
    
    /**
     * 判断当前单位是否是集团下的单位
     * @throws BusinessException 
     * 
     */
    public static boolean isAccountInGroup(long accountId) {
		try {
			return getOrgManager().isAccountInGroupTree(accountId);
		}
		catch (Exception e) {
			return false;
		}
	}
    
       
    /**
     * 用Integer作为Map的key时，JSTL的表达式 ${map[1]}是不能返回值的，故提供该方法
     * 
     * @param map
     * @param key
     * @return
     */
    public static Object getMapValueOfIntegerKey(Map<Integer, ? extends Object> map, Integer key){
        if(map == null){
            return null;
        }
        
        return map.get(key);
    }

    /**
     * 判断当前在线用户是否在其主单位而非兼职单位
     * @param memberId
     * @param accountId
     * @return
     */
    public static boolean isUsersMainAccount(long memberId, long accountId){
        V3xOrgMember m = getMember(memberId);
        if(m != null){
            return m.getOrgAccountId().equals(accountId);
        }
        return false;
    }
    
    /**
     * 取得用户的兼职部门岗位等信息
     * @return
     */
    public static Map<String, String> getPluralityInfo4User(long memberId, long accountId){
        Map<String, String> result = new HashMap<String, String>(); 
        try {
            List<V3xOrgEntity> deptOrgEntity = getOrgManager().getUserDomain(memberId, accountId,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
            if(deptOrgEntity!=null && !deptOrgEntity.isEmpty()){
                V3xOrgDepartment dept = (V3xOrgDepartment)deptOrgEntity.get(0);
                result.put("departmentSimpleName", dept.getName());
                result.put("departmentPath", dept.getPath());
                result.put("departmentId", dept.getId().toString());
            }
            List<V3xOrgEntity> postOrgEntity = getOrgManager().getUserDomain(memberId, accountId,V3xOrgEntity.ORGENT_TYPE_POST);
            if(postOrgEntity!=null && !postOrgEntity.isEmpty()){
                V3xOrgPost post = (V3xOrgPost)postOrgEntity.get(0);
                result.put("postName", post.getName());                
            }
        }
        catch (BusinessException e) {
            log.error("", e);
        }
        return result;
    }
    
    /**
     * 检测工作范围
     * 
     * @param currentMemberId 当前登录者
     * @param memberId 被访问人
     * @return true:当前登录者可以访问被访问人
     */
    public static boolean checkLevelScope(long currentMemberId, long memberId){
    	OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
    	try {
			V3xOrgMember currentMember = getMember(currentMemberId); // 当前登录者
			V3xOrgMember member = getMember(memberId); // 被检测的人
			if (currentMember == null) {
				return false;
			}
			//外部人员 在显示上就已经有了限制，所以这里没必要做限制了 -dongyj bug26117
			List<V3xOrgDepartment> dl = new ArrayList<V3xOrgDepartment>();
			if(!currentMember.getIsInternal()){
				User user = CurrentUser.get();
				if(user!=null && !member.getOrgAccountId().equals(user.getLoginAccount())){
					List<ConcurrentPost> cntList = getOrgManagerDirect().getAllConcurrentPostByMemberId(member.getId());
					for(ConcurrentPost cp : cntList){
						if(cp.getCntAccountId().equals(user.getLoginAccount())){
							dl.add(orgManager.getDepartmentById(cp.getCntDepId()));
						}
					}
				}else{
					V3xOrgDepartment d = orgManager.getDepartmentById(member.getOrgDepartmentId());
					dl.add(d);
				}
				Collection<V3xOrgDepartment> canAccountDeps = OuterWorkerAuthUtil.getCanAccessDep(currentMemberId,
						currentMember.getOrgDepartmentId(),
						currentMember.getOrgAccountId(), orgManager);
				boolean returnValue = false;
				for(V3xOrgDepartment d :dl){
					if(canAccountDeps.contains(d)) {
						returnValue = true;
						break;
					}
				}
				return returnValue;
			}
			
			if (member == null) { 
				return false;
			}
			
			if(currentMember.getIsAdmin())
				return true;
			
			if(currentMember.getIsAdmin() || member.getIsAdmin()){ //管理员不能发送消息
				return false; 
			}
			
			//同一个部门
			if(currentMember.getOrgDepartmentId().longValue() == member.getOrgDepartmentId().longValue()){
				return true;
			}
			
			//相同的职务级别
			if(currentMember.getOrgLevelId().longValue() == member.getOrgLevelId().longValue()){
				return true;
			}
			
			//内部人员都可以看到外部人员
			if(currentMember.getOrgLevelId() == -1 || member.getOrgLevelId() == -1){
				return currentMember.getOrgLevelId() != -1;
			}
			
			// 副岗在这个部门的有权限
			if (MemberHelper.isSndPostContainDept(currentMember, member.getOrgDepartmentId())) {
				return true;
			}
			
			/*V3xOrgLevel currentMemberLevel = orgManager.getLevelById(currentMember.getOrgLevelId());
			int currentMemberLevelSortId = currentMemberLevel!=null ? currentMemberLevel.getLevelId() : 0;
			
			V3xOrgLevel memberLevel = orgManager.getLevelById(member.getOrgLevelId());
			int memberLevelSortId = memberLevel!=null ? memberLevel.getLevelId() : 0;

			int currentAccountLevelScope = orgManager.getAccountById(currentMember.getOrgAccountId()).getLevelScope();

			if ((currentMember.getOrgDepartmentId().equals(member.getOrgDepartmentId()))
					|| currentAccountLevelScope < 0) {
				return true;
			}

			if (currentMemberLevelSortId - memberLevelSortId <= currentAccountLevelScope) {
				return true;
			}*/
			
			//映射集团职务级别
			int currentAccountLevelScope = orgManager.getAccountById(currentMember.getOrgAccountId()).getLevelScope();
			if ((currentMember.getOrgDepartmentId().equals(member.getOrgDepartmentId()))) {//AEIGHT-9737_V350SP1_力帆实业（集团）股份有限公司_A和B单位都是映射了集团的职务级别，B单位做了职务级别限制，A单位的人员可以访问到B单位任意人员_2013-8-23
				return true;
			}
			//切换的单位级别范围
			int newAccountLevelScope = orgManager.getAccountById(member.getOrgAccountId()).getLevelScope();
			if (newAccountLevelScope < 0) {
				return true;
			}
			int currentMemberLevelSortId=0;
			int accountLevelScope=0;
			V3xOrgLevel memberLevel = orgManager.getLevelById(member.getOrgLevelId());
			int memberLevelSortId = memberLevel!=null ? memberLevel.getLevelId() : 0;
			if(currentMember.getOrgAccountId().equals(member.getOrgAccountId())){
				V3xOrgLevel currentMemberLevel = orgManager.getLevelById(currentMember.getOrgLevelId());
				currentMemberLevelSortId = currentMemberLevel!=null ? currentMemberLevel.getLevelId() : 0;
				accountLevelScope = currentAccountLevelScope;
			}else{
				currentMemberLevelSortId = mappingLevelSortId(member,currentMember);
				accountLevelScope = newAccountLevelScope;
			}
			if (currentMemberLevelSortId - memberLevelSortId <= accountLevelScope) {
				return true;
			}
		}
    	catch (Exception e) {
			log.warn("检测工作范围", e);
		}
    	
    	return false;
    }
    
	//映射集团职务   by wusb 2010-09-25
	private static int mappingLevelSortId(V3xOrgMember member, V3xOrgMember currentMember) throws BusinessException{
		int currentMemberLevelSortId=0;
		V3xOrgLevel level = null;
		User user = CurrentUser.get();
		boolean isNeedCheckLevelScope=true;
		if(user.isAdministrator() || user.isGroupAdmin() || user.isSystemAdmin()){ //管理员默认不限制
			isNeedCheckLevelScope = false;
		}
		if(isNeedCheckLevelScope){
			Map<Long, List<ConcurrentPost>> concurrentPostMap= orgManager.getConcurentPostsByMemberId(member.getOrgAccountId(),currentMember.getId());
			if(concurrentPostMap != null && !concurrentPostMap.isEmpty()){ //我在当前单位兼职
				Iterator<List<ConcurrentPost>> it = concurrentPostMap.values().iterator();
				boolean isExist=false;
				while(it.hasNext()){
					List<ConcurrentPost> cnPostList = it.next();
					for(ConcurrentPost cnPost : cnPostList){
						if(cnPost!=null){
							Long cnLevelId = cnPost.getCntLevelId();
							if(cnLevelId!=null){
								V3xOrgLevel cnLevel = orgManager.getLevelById(cnLevelId);
								if(cnLevel!=null){
									currentMemberLevelSortId = cnLevel.getLevelId();
									isExist=true;
									break;
								}else{ 
									level = getLowestLevel(member.getOrgAccountId());
									currentMemberLevelSortId = level!=null ? level.getLevelId().intValue():0;
								}
							}
						}
					}
					if(isExist) break;
				}
				return currentMemberLevelSortId;
			}
			
			Long levelIdOfGroup = null;
			V3xOrgLevel currentMemberLevel = orgManager.getLevelById(currentMember.getOrgLevelId());
			if(null == currentMemberLevel.getGroupLevelId()) {//AEIGHT-10191
				if(currentMember.getOrgLevelId() != -1) {
					levelIdOfGroup = null;
				} else {
					levelIdOfGroup = -1L;
				}
			} else {
				levelIdOfGroup = (currentMember.getOrgLevelId()!=-1) ? currentMemberLevel.getGroupLevelId() : -1; //当前登录者对应集团的职务级别id
			}
			//切换单位的所有职务级别
			List<V3xOrgLevel> levels = orgManager.getAllLevels(member.getOrgAccountId());
			for(V3xOrgLevel _level:levels){
				if(levelIdOfGroup!=null){
					if(levelIdOfGroup.equals(_level.getGroupLevelId())){
						level=_level;
						break;
					}
				}
			}
			if(level==null){
				level = getLowestLevel(member.getOrgAccountId()); //最低职务级别
			}
			
			if(level!=null){
				currentMemberLevelSortId = level.getLevelId();
			}
		}
		return currentMemberLevelSortId;
	}
	
	private static V3xOrgLevel getLowestLevel(Long accountId)
			throws BusinessException {
		List<V3xOrgLevel> levels = orgManager.getAllLevels(accountId, false);
		V3xOrgLevel low = null;
		for (V3xOrgLevel level : levels) {
			if (level.isValid()) {
				if (low != null) {
					if (level.getLevelId() > low.getLevelId()) {
						low = level;
					}
				} else {
					low = level;
				}
			}
		}
		return low;
	}
    
    /**
     * 添加Cookie
     * 
     * @param response
     * @param name 
     * @param value
     * @param isForever 是否永久有效
     * @param isEncode 是否加密
     */
    public static void addCookie(HttpServletResponse response, String name,
			String value, boolean isForever, boolean isEncode){
    	int Expires = 0;
    	if(isForever){
    		Expires = Cookies.COOKIE_EXPIRES_FOREVER;
    	}
    	
    	Cookies.add(response, name, value, Expires, isEncode);
    }
    
    /**
     * 读取Cookie值
     * 
     * @param request
     * @param name
     * @param isEncode
     * @return
     */
    public static String getCookie(HttpServletRequest request, String name, boolean isEncode){
    	return Cookies.get(request, name, isEncode);
    }
    
    /**
     * 用户登录时，代理设置提醒
     * @return
     */
    public static String agentSettingAlert() {  
    	
    	AgentIntercalateManager agentIntercalateManager = (AgentIntercalateManager)ApplicationContextHolder.getBean("agentIntercalateManager");
    	
    	StringBuffer alertMsg = new StringBuffer();
    	String resourceName = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources"; 
    	
//    	List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(currentUser().getId());
//    	List<AgentModel> agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(currentUser().getId());
//    	List<V3xAgent> agentList = agentIntercalateManager.getAgentByMemberId(currentUser().getId());
    	List<V3xAgent> agentList= new ArrayList<V3xAgent>();
		try {
			agentList = agentIntercalateManager.queryAvailabilityList1(currentUser().getId(),"agent");
		} catch (Exception e1) {
			log.error(e1.getMessage(), e1);
		}
//    	List<V3xAgent> agentToList = agentIntercalateManager.getToAgentByMemberId(currentUser().getId());
    	List<V3xAgent> agentToList= new ArrayList<V3xAgent>();
		try {
			agentToList = agentIntercalateManager.queryAvailabilityList(currentUser().getId(),"agent");
		} catch (Exception e1) {
			log.error(e1.getMessage(), e1);
		}
    	
		int index = 1;
    	Date now = new Date();
    	boolean isProxy = false;
    	if(agentList != null && !agentList.isEmpty()){
    		/*alertMsg.append(ResourceBundleUtil.getString(resourceName, "agent.currentUser.alert"));
    		alertMsg.append("#");*/
    		StringBuilder sb1Id = new StringBuilder();// 拼接代理表主键 用于调整代理状态
    		for (int j = 0 ; j < agentList.size() ; j ++) {
    			V3xAgent agent = agentList.get(j);
    			if(now.before(agent.getStartDate()) || now.after(agent.getEndDate()))
    				continue;
    			if (j != 0)
    				sb1Id.append("_");
    			sb1Id.append(agent.getId());
    			if(!isProxy)
    				isProxy = true;
    			Long memberId = agent.getAgentToId(); 
    			OrgManager orgManager = getOrgManager();
    			V3xOrgMember member = null;
    			try {
					member = orgManager.getMemberById(memberId);
					if((null == member) || (member!=null && member.getState()==2)){//如果被代理人已经离职，则不提醒
						continue;
					}
				} catch (BusinessException e) {
					log.error("", e);
				}
				String agentOption = agent.getAgentOption();
				String[] agentOptions = agentOption.split("&");
				StringBuffer appNames = new StringBuffer();
				boolean isAudit = false;
				String appName = null;
				List<V3xAgentDetail> v3xdetails= agent.getAgentDetails();
				for(int i=0; i<agentOptions.length; i++){
					if(agentOptions[i].equals("1") && v3xdetails!= null && v3xdetails.size()>0){//协同细分下:模板和自由协同
						boolean isTemplate= false;
						boolean isFree= false;
						for (V3xAgentDetail v3xAgentDetail : v3xdetails) {
							if(v3xAgentDetail.getEntityId()==2){//代理了全部模板
								if(!isTemplate){
									isTemplate= true;
								}
							}else if(v3xAgentDetail.getEntityId()!=1){//代理了具体模板，有模板编号
								if(!isTemplate){
									isTemplate= true;
								}
							}else{//自由协同
								isFree= true;
							}
						}
						if( isTemplate && isFree){
							appName = ResourceBundleUtil.getString(resourceName, "application.1.label");//協同:包含自由協同和表單模板協同
						}else if( isTemplate ){
							String agentResourceName = "com.seeyon.v3x.agent.resources.i18n.AgentResources";
							//appName = ResourceBundleUtil.getString(resourceName, "application.1.2.label");//表單模板協同
							appName = ResourceBundleUtil.getString(agentResourceName, "templatecoll.label"+(SystemEnvironment.hasPlugin("form")?"":".noForm"));//表單模板協同
						}else if(isFree){
							appName = ResourceBundleUtil.getString(resourceName, "application.1.1.label");//自由協同
						}
					}else if(agentOptions[i].equals(String.valueOf(ApplicationCategoryEnum.bulletin.getKey())) 
							|| agentOptions[i].equals(String.valueOf(ApplicationCategoryEnum.inquiry.getKey())) 
							|| agentOptions[i].equals(String.valueOf(ApplicationCategoryEnum.news.getKey()))){
						if(isAudit){
							continue;
						}
						isAudit = true;
						appName = ResourceBundleUtil.getString("com.seeyon.v3x.agent.resources.i18n.AgentResources", "audit.label");
					}else
						appName = ResourceBundleUtil.getString(resourceName, "application." + agentOptions[i] + ".label");
					if(i != 0){
						appNames.append("、");
					}
					appNames.append(appName);
				}
				
				if(agentList.size() > 1){
					alertMsg.append((index++) + ". ");
	    		}
				alertMsg.append(ResourceBundleUtil.getString(resourceName, "agent.detail.remind", member.getName(), appNames.toString()));
				alertMsg.append("<br>");
				alertMsg.append(ResourceBundleUtil.getString("com.seeyon.v3x.agent.resources.i18n.AgentResources", "common.time.limit.label")).append(Datetimes.formatDatetimeWithoutSecond(agent.getStartDate()))
						.append("  -  ").append(Datetimes.formatDatetimeWithoutSecond(agent.getEndDate()));
				alertMsg.append("<br>");
    		}
    		if(isProxy)
    			return alertMsg.toString()+"::"+sb1Id.toString();
    	}
    	
    	if(agentToList != null && !agentToList.isEmpty()){
    		/*alertMsg.append(ResourceBundleUtil.getString(resourceName, "agent.currentUser.alert"));
    		alertMsg.append("#");*/
    		StringBuilder sb2Id = new StringBuilder();// 拼接代理表主键 用于调整代理状态
    		for(int j = 0 ; j < agentToList.size() ; j ++){
    			V3xAgent agent = agentToList.get(j);
    			if(now.before(agent.getStartDate()) || now.after(agent.getEndDate()))
    				continue;
    			if(!isProxy)
    				isProxy = true;
    			if (j != 0)
    				sb2Id.append("_");
    			sb2Id.append(agent.getId());
    			Long memberId = agent.getAgentId(); 
    			OrgManager orgManager = getOrgManager();
    			V3xOrgMember member = null;
    			try {
					member = orgManager.getMemberById(memberId);
				} catch (BusinessException e) {
					log.error("", e);
				}
				String agentOption = agent.getAgentOption();
				String[] agentOptions = agentOption.split("&");
				StringBuffer appNames = new StringBuffer();
				boolean isAudit = false;
				String appName = null;
				List<V3xAgentDetail> v3xdetails= agent.getAgentDetails();
				for(int i=0; i<agentOptions.length; i++){
					if(agentOptions[i].equals("1") && v3xdetails!= null && v3xdetails.size()>0){//协同细分下:模板和自由协同
						boolean isTemplate= false;
						boolean isFree= false;
						for (V3xAgentDetail v3xAgentDetail : v3xdetails) {
							if(v3xAgentDetail.getEntityId()==2){//代理了全部模板
								if(!isTemplate){
									isTemplate= true;
								}
							}else if(v3xAgentDetail.getEntityId()!=1){//代理了具体模板，有模板编号
								if(!isTemplate){
									isTemplate= true;
								}
							}else{//自由协同
								isFree= true;
							}
						}
						if( isTemplate && isFree){
							appName = ResourceBundleUtil.getString(resourceName, "application.1.label");//協同:包含自由協同和表單模板協同
						}else if( isTemplate ){
							appName = ResourceBundleUtil.getString(resourceName, "application.1.2.label");//表單模板協同
						}else if(isFree){
							appName = ResourceBundleUtil.getString(resourceName, "application.1.1.label");//自由協同
						}
					}else if(agentOptions[i].equals(String.valueOf(ApplicationCategoryEnum.bulletin.getKey())) 
							|| agentOptions[i].equals(String.valueOf(ApplicationCategoryEnum.inquiry.getKey())) 
							|| agentOptions[i].equals(String.valueOf(ApplicationCategoryEnum.news.getKey()))){
						if(isAudit){
							continue;
						}
						isAudit = true;
						appName = ResourceBundleUtil.getString("com.seeyon.v3x.agent.resources.i18n.AgentResources", "audit.label");
					}else
						appName = ResourceBundleUtil.getString(resourceName, "application." + agentOptions[i] + ".label");
					if(i != 0){
						appNames.append("、");
					}
					appNames.append(appName);
					
				}
				
				if(agentToList.size() > 1){
					alertMsg.append((index++) + ". ");
	    		}
				alertMsg.append(ResourceBundleUtil.getString(resourceName, "agentTo.detail.remind", member.getName(), appNames.toString()));
				alertMsg.append("<br>");
				alertMsg.append(ResourceBundleUtil.getString("com.seeyon.v3x.agent.resources.i18n.AgentResources", "common.time.limit.label")).append(Datetimes.formatDatetimeWithoutSecond(agent.getStartDate()))
						.append("  -  ").append(Datetimes.formatDatetimeWithoutSecond(agent.getEndDate()));
				alertMsg.append("<br>");
    		}
    		if(isProxy)
    			return alertMsg.toString()+"::"+sb2Id.toString();
    	} 
    	return alertMsg.toString(); 
    }
    
    /**
     * 取得皮肤Path , 格式如 "/common/skin/default4GOV"
     */
    public static String getSkin(){
		return "/common/skin/default" + com.seeyon.v3x.skin.Constants.getUserSkinSuffix();
    }
    public static String skin(){ 
    	return "<link href=\"" +SystemEnvironment.getA8ContextPath()+ getSkin() + "/skin.css" + resSuffix() + "\" type=\"text/css\" rel=\"stylesheet\">" 
    			+"<script type=\"text/javascript\">var skinType = '" +SystemEnvironment.getA8ContextPath()+ getSkin() + "';</script>" ; 
    }
    public static String getXUA(){ 
    	if((Boolean)BrowserFlag.XUA.getFlag(WebUtil.getRequest())){
    		return "<meta http-equiv=X-UA-Compatible content=IE=EmulateIE9>";
    	}else{
    		return "";
    	}
    }
    /**
     * 
     */
    public static String showAgentMemberName(long memberId, int appKey){
    	String agentName = "";
    	try{
	    	if(memberId != currentUser().getId()){
	    		V3xOrgMember member = orgManager.getMemberById(memberId);
	    		if(member == null){
	    			return agentName;
	    		}
	    		agentName = member.getName();
	    	}else{
	    		if(appKey == ApplicationCategoryEnum.edocRec.key()
	    				|| appKey == ApplicationCategoryEnum.edocRegister.key()
	    				|| appKey == ApplicationCategoryEnum.edocSend.key()
	    				|| appKey == ApplicationCategoryEnum.edocSign.key()
	    				|| appKey == ApplicationCategoryEnum.exSign.key()
	    				|| appKey == ApplicationCategoryEnum.exSend.key()){
	    			appKey = ApplicationCategoryEnum.edoc.key();
	    		}
	    		Long agentToId = MemberAgentBean.getInstance().getAgentMemberId(appKey, currentUser().getId());
	    		if(agentToId != null){
	    			V3xOrgMember member = orgManager.getMemberById(agentToId);
	    			agentName = member.getName();
	    		}
	    	}
    	}catch(Exception e){
    		log.error("", e);
    	}
    	return agentName;
    }
    
    /**
     * 是否启用公文
     * @return
     */
    public static boolean isEnableEdoc(){
        boolean isEnable = true;
        String enableEdocConfig = getSystemConfig().get(IConfigPublicKey.EDOC_ENABLE);
        if(enableEdocConfig != null){
            isEnable = "enable".equals(enableEdocConfig);
        }
        return isEnable;
    }
    
    /**
     * 是否启用系统开关
     * @param key 系统开关关键字
     * @return
     */
    public static boolean isEnableSwitch(String key){
        boolean isEnable = false;
        String enableSwitchConfig = null;
        if(IConfigPublicKey.RSS_ENABLE.equals(key) || "RSS".equals(key)){
            enableSwitchConfig = getSystemConfig().get(IConfigPublicKey.RSS_ENABLE);
        }
        else if(IConfigPublicKey.EDOC_ENABLE.equals(key) || "Edoc".equals(key)){
            enableSwitchConfig = getSystemConfig().get(IConfigPublicKey.EDOC_ENABLE);                
        }
        else if(IConfigPublicKey.READ_STATE_ENABLE.equals(key) || "ReadState".equals(key)){
            enableSwitchConfig = getSystemConfig().get(IConfigPublicKey.READ_STATE_ENABLE);
        }else if(IConfigPublicKey.BLOG_ENABLE.equals(key) || "Blog".equals(key)){
        	enableSwitchConfig = getSystemConfig().get(IConfigPublicKey.BLOG_ENABLE);
        }
        //TODO 其他添加在这里
        
        if(enableSwitchConfig != null){
            isEnable = "enable".equals(enableSwitchConfig);
        }
        return isEnable;
    }
    
    /**
     * 国际化Label的后缀， 用于支持政务版的key.<br>
     * 政务版与集团版的Label不同时，需要增加一个以.GOV区分的后缀，引用该key后附加这个<br>
     * 如：<fmt:message key='menu.group.info.set${v3x:suffix()}'/>
     * 集团版引用key：menu.group.info.set<br>
     * 政务版key为：menu.group.info.set.GOV
     * @return
     */
    public static String suffix(){
       return (String)SysFlag.EditionSuffix.getFlag();
    }
    public static String sysSuffix(){
        return (String)SysFlag.sys_edition.getFlag();
     }
    public static String oemSuffix(){
    	return (String)SysFlag.NCSuffix.getFlag();
    }
    public static String oemSuffixInJS(){
    	String suffix = (String)SysFlag.NCSuffix.getFlag().toString();
        return suffix.replace(".", "_");
    }
    /**
     * JS国际化Label的后缀， 用于支持政务版的key.<br>
     * 政务版与集团版的Label不同时，需要增加一个以_GOV区分的后缀，引用该key后附加这个<br>
     * 集团版引用key：group_info_set<br>
     * 政务版key为：group_info_set_GOV<br>
     * JSP调用:var msgLabel = "group_info_label" + "${v3x:suffixInJS()}");  
     * @return
     */
    public static String suffixInJS(){
       String suffix = (String)SysFlag.EditionSuffix.getFlag().toString();
       return suffix.replace(".", "_");
    }
    
    private static String resSuffix = null;
    /**
     * 静态资源文件的后缀.（每次发版需要更新该值）<br>
     * 用于解决客户端缓存问题导致的CSS、JS、SWF等的加载异常问题
     * @return ?V=320_20100
     */
    public static String resSuffix(){
    	if(resSuffix == null){
    		resSuffix = "?V=" + ProductVersionEnum.getCurrentVersion().name() + "_" + SystemProperties.getInstance().getProperty("product.build.date");
    	}
    	
    	return resSuffix; 
    }
    
    /**
     * 得到被代理人的ID（我给<谁>干活） 
     */
    public static Long getMyAgentId(int appEnum){
        List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(currentUser().getId());
        Long agentId = -1L;
        if(agentModelList != null && !agentModelList.isEmpty()){
            for(AgentModel agentModel : agentModelList){
                String agentOptionStr = agentModel.getAgentOption();
                if(agentOptionStr.indexOf(String.valueOf(appEnum)) != -1){
                    agentId = agentModel.getAgentToId();
                    break;
                }
            }
        }
        return agentId;
    }
    
    /**
     * 根据类型和ID得到所有人员Id，包括兼职<br>
     * <ul>应用场景：
     * <li>1、模板推送时根据授权类型获取所有人员ID，（orgManager.getMembersByType接口不能返回兼职人员）</li>
     * @param type
     * @param id
     * @return
     */
    public static Set<Long> getAllMembersId(String type, Long id){
    	Set<Long> memberIds = new HashSet<Long>();
    	if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(type)){
    		memberIds.add(id);
    		return memberIds;
		}
    	
    	List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
    	if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(type)){
			try{
				memberList = getOrgManager().getAllMembers(id);
				
				Map<Long, List<V3xOrgMember>> parttimeMap = getOrgManager().getConcurentPostByAccount(id); //兼职
				if(parttimeMap != null && !parttimeMap.isEmpty()){
					Set<Map.Entry<Long, List<V3xOrgMember>>> enities = parttimeMap.entrySet();
					for (Map.Entry<Long, List<V3xOrgMember>> entry : enities) {
						for(V3xOrgMember m : entry.getValue()){
							memberIds.add(m.getId()); //直接放入结果，不追加到List再转换
						}
					}
				}
			}
	        catch (BusinessException e) {
	            log.error("", e);
	        }
		}
    	else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(type)){
			try{
				V3xOrgDepartment department = getOrgManager().getDepartmentById(id);
				if(department != null && department.getIsInternal()){
					memberList = getOrgManager().getMembersByDepartment(id, false);
				}else{
					memberList = getOrgManager().getExtMembersByDepartment(id, false);
				}
	    	}
			catch (BusinessException e) {
	            log.error("", e);
	        }
		}
    	else if(V3xOrgEntity.ORGENT_TYPE_LEVEL.equals(type)){
    		try{
    			memberList = getOrgManager().getMembersByLevel(id);
    		}
    		catch (BusinessException e) {
    			log.error("", e);
    		}
		}
    	else if(V3xOrgEntity.ORGENT_TYPE_POST.equals(type)){
			try{
				memberList = getOrgManager().getMembersByPost(id);
			}
			catch (BusinessException e) {
				log.error("", e);
			}
		}
		else if(V3xOrgEntity.ORGENT_TYPE_TEAM.equals(type)){
			try{
				memberList = getOrgManager().getTeamMember(id);	
			}
			catch (BusinessException e) {
				log.error("", e);
			}
		}
    	
    	if(memberList != null && !memberList.isEmpty()){
			for (V3xOrgMember m : memberList) {
				memberIds.add(m.getId());
			}
		}
    	return memberIds;
    }
    
    public static V3xOrgAccount getGroup(){
    	try{
    		return getOrgManager().getRootAccount();
    	}catch(Exception e){
    		log.error("",e);
    	}
    	return null;
    }
    
    public static String getVersion(){
		// branches_a8_v350_r_gov GOV-2857 任会阳 修改政务版本号 start
		boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();
		if (isGovVersion) {
			return ProductVersionEnum.getCurrentVersion().getGovOAVersion();
		} else {
			if (Strings.isBlank(oemSuffix())) {
				return ProductVersionEnum.getCurrentVersion().getCanonicalVersion();
			} else {
				return ProductVersionEnum.getCurrentVersion().getNCOAVersion();
			}
		}
		// branches_a8_v350_r_gov GOV-2857 任会阳 修改政务版本号 end
    }
    
	public static String getPageTitle(){
		ConfigItem configItem_login = getConfigManager().getConfigItem("System_Login_Title", "loginTitleName");
        String title = null;
        if(configItem_login != null){
        	title = configItem_login.getConfigValue();
        }
        else{
        	// branches_a8_v350_r_gov GOV-2907 任会阳 修改 政务title
			title = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.page.title" + oemSuffix() + suffix());
        }
        
        title += " " + getVersion();
        
        if(CurrentUser.get() != null){
        	title += ", " + ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "welcome.label", CurrentUser.get().getName());
        }
        
        return title;
	}
	
	/**
	 * 判断此人是否在这个单位是该角色
	 * @param roleName 角色名称
	 * @param user     人员对象
	 * @return
	 */
	public static boolean isRole(String roleName, User user) {
		try {
			V3xOrgRole formRole = getOrgManager().getRoleByName(roleName, user.getLoginAccount());
			if (formRole != null) {
				List<Long> domain = getOrgManager().getUserDomainIDs(user.getId(),user.getLoginAccount(), V3xOrgEntity.ORGENT_TYPE_ROLE);
				if (domain != null && domain.contains(formRole.getId()))
					return true;
				else
					return false;
			}
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		return false;
	}
	
	public static OnlineUser getOnlineUser(Long memberId){
		String loginName = getUserName(memberId);
		return OnlineRecorder.getOnlineUser(loginName);
	}
	
	public static String getUserName(Long memberId){
		String userName = "";
		if(memberId.longValue() == 1){
			ConfigItem auditName = getConfigManager().getConfigItem(V3xOrgEntity.CONFIG_SYSTEM_ADMIN_CATEGORY, V3xOrgEntity.CONFIG_SYSTEM_ADMIN_NAME);
			if(auditName != null){
				return auditName.getConfigValue();
			}
		}else if(memberId.longValue() ==0){
			ConfigItem auditName = getConfigManager().getConfigItem(V3xOrgEntity.CONFIG_AUDIT_ADMIN_CATEGORY, V3xOrgEntity.CONFIG_AUDIT_ADMIN_NAME);
			if(auditName != null){
				return auditName.getConfigValue();
			}
		}else{
			V3xOrgMember member = getMember(memberId);
			if(member != null){
				return member.getLoginName();
			}
		}
		return userName;
	}
	
	private static DocMimeTypeManager docMimeTypeManager = null;
	private static DocMimeTypeManager getDocMimeTypeManager(){
        if(docMimeTypeManager == null){
        	docMimeTypeManager = (DocMimeTypeManager)ApplicationContextHolder.getBean("docMimeTypeManager");
        }
        return docMimeTypeManager;
    }
	
	/**
	 * 根据文件类型获取其对应的展现图标
	 * @param mimeTypeId	文件类型
	 * @return	展现图标
	 */
	public static String getIcon(Long mimeTypeId) {
		DocMimeType dmt = getDocMimeTypeManager().getDocMimeTypeById(mimeTypeId);
		return dmt != null ? dmt.getIcon() : null;
	}
	
	private static TempleteManager templeteManager;
	public static String getTempleteNames(String ids){
		if(Strings.isBlank(ids)) return "";
		if(templeteManager == null){
			templeteManager = (TempleteManager)ApplicationContextHolder.getBean("templeteManager");
		}
		List<Long> idList = new ArrayList<Long>();
		String[] idStr = ids.split(",");
		for(String id : idStr){
			idList.add(Long.parseLong(id));
		}
		List<Templete> tempList = templeteManager.getListByIds(idList);
		StringBuilder sb = new StringBuilder();
		User user = CurrentUser.get();
		if(tempList != null)
		for(Templete templete : tempList){
			if(sb.length() != 0){
				sb.append(",");
			}
			sb.append(templete.getSubject());
			if(user != null && !templete.getOrgAccountId().equals(user.getLoginAccount())){
				String accountName = getAccountShortName(templete.getOrgAccountId());
				sb.append("(").append(accountName).append("");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 显示完成率或进度等百分比数字时，将百分比数字只显示为整数
	 */
	public static String showRate(Float f) {
		return showRate(f, false);
	}
	
	/**
	 * 显示完成率或进度等百分比数字时，将百分比数字只显示为整数
	 * 视选择加上百分号
	 * @param f 			浮点数
	 * @param showPercent	是否显示百分比
	 */
	public static String showRate(Float f, boolean showPercent) {
		String percent = showPercent ? "%" : "";
		String ret = "0";
		if(f != null && f > 0.0f) {
			String rate = String.valueOf(f);
			if(rate.indexOf('.') != -1) {
				ret = rate.substring(0, rate.indexOf('.'));
			}
		}
		return ret + percent;
	}
	/**
	 * 将小数显示为百分比的数字,保留两个小数 
	 * 例如 0.3933 显示为39.33%
	 * @param d : 浮点数
	 * @return
	 */
	public static String showNumber2Percent(Number d){
		if(d==null)
			return "0.00%";
		NumberFormat num = NumberFormat.getPercentInstance(); 
		num.setMaximumIntegerDigits(10); //小数点前面最多显示几位的
	    num.setMaximumFractionDigits(2); //小数点后面最多显示几位
	    num.setMinimumFractionDigits(2);
		return num.format(d);
	}
	/**
	 * 显示标题，如果有代理标蓝显示
	 * @param obj  实体对象
	 * @param userId  显示标题的人员id
	 * @param length  需要截取的长度
	 * @param app     所属应用
	 * @param showProxyName    是否显示代理信息，比如“由XXX代理”或“XXX代理”
	 * @return
	 */
	public static String showSubject(Object obj,Long userId,int length,int app,boolean showProxyName){
		boolean isProxy = false;
		//代理列表
		List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(CurrentUser.get().getId());
		AgentModel agent = null;
		Date date = new Date();
		String appKey = String.valueOf(app);
		if(agentModelList == null){
			//被代理列表
			agentModelList = MemberAgentBean.getInstance().getAgentModelToList(CurrentUser.get().getId());
			agent = getAgent(agentModelList,date,appKey);
		}else{
			agent = getAgent(agentModelList,date,appKey);
			if(agent == null){
				agentModelList = MemberAgentBean.getInstance().getAgentModelToList(CurrentUser.get().getId());
				agent = getAgent(agentModelList,date,appKey);
			}
		}
		
		String subject = "";
		String proxyLabel = "";
		String proxyName = null;
		int realLength = length;
		Long realUserId = null;
		if(obj instanceof BulData){
			BulData bulDate = (BulData)obj;
			subject = bulDate.getTitle();
			realUserId = bulDate.getAuditUserId();
		}else if(obj instanceof InquirySurveybasic){
			InquirySurveybasic inquiry = (InquirySurveybasic)obj;
			subject = inquiry.getSurveyName();
			realUserId = inquiry.getCensorId();
		}else if(obj instanceof NewsData){
			NewsData newsData = (NewsData)obj;
			subject = newsData.getTitle();
			realUserId = newsData.getAuditUserId();
		}
		isProxy = agent != null && ((agent.getAgentId().equals(realUserId)) || agent.getAgentToId().equals(realUserId) || !showProxyName);
		if(!isProxy){
			subject = Strings.getLimitLengthString(subject, realLength, "...");
		}else{
			if(showProxyName){              //显示“(由XXX代理)”或“(代理XXX)”
				if(agent.getAgentToId().equals(userId)){
					if(realUserId != null && realUserId.equals(agent.getAgentId())){
						proxyName = showMemberNameOnly(realUserId);
						proxyLabel = "(" + proxyName + Constant.getString4CurrentUser("col.proxy") + ")";
					}
				}else{
					proxyName = showMemberNameOnly(agent.getAgentToId());
					proxyLabel = "(" + Constant.getString4CurrentUser("col.proxy") + proxyName + ")";
				}
				realLength -= proxyLabel.getBytes().length;
				subject = "<span class=\"link-blue\">" + Strings.getLimitLengthString(subject, realLength, "...") + proxyLabel + "</span>";
			}else{                     //不显示代理信息，但是字体要标蓝
				subject = "<span class=\"link-blue\">" + Strings.getLimitLengthString(subject, realLength, "...") + "</span>";
			}
		}
		return subject;
	}
	
	private static AgentModel getAgent(List<AgentModel> agentModelList,Date date,String app){
		if(agentModelList == null || agentModelList.isEmpty())
			return null;
		String options;
		String[] arr;
		for(AgentModel model:agentModelList){
			//在代理期限内的
			if(date.after(model.getStartDate()) && date.before(model.getEndDate())){
				options = model.getAgentOption();
				if(Strings.isNotBlank(options)){
					arr = options.split("&");
					if(arr != null){
						for(String o:arr){
							if(app.equals(o))
								return model;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 下拉框树型结构
	 * ${v3x:comboTree(accountList, 'id', 'superior', 'name', rootAccountId, 'currentAccountId')}
	 * @param list 节点集合
	 * @param idProperty 节点value来自属性 
	 * @param parentProperty 节点上级来自属性
	 * @param textProperty 节点显示名称来自属性
	 * @param root 根节点value
	 * @param id 页面标签id
	 * @return json
	 */
	public static String getComboTree(Collection<? extends Object> list, String idProperty, String parentProperty, String textProperty, String root, String id) {
		List<String> roots = new ArrayList<String>();
		if (StringUtils.isNotBlank(root)) {
			roots.add(root);
		}
		
		return Functions.getComboTreeList(list, idProperty, parentProperty, textProperty, roots, id);
	}
	
	public static String getComboTreeList(Collection<? extends Object> list, String idProperty, String parentProperty, String textProperty, List<String> roots, String id) {
		if (CollectionUtils.isEmpty(list)) {
			return "";
		}
		
		Map<String, ComboTreeNode> map = new LinkedHashMap<String, ComboTreeNode>();
		try {
			for (Object object : list) {
				String nodeId = String.valueOf(PropertyUtils.getProperty(object, idProperty));
				String nodeParentId = String.valueOf(PropertyUtils.getProperty(object, parentProperty));
				String nodeText = String.valueOf(PropertyUtils.getProperty(object, textProperty));
				ComboTreeNode node = new ComboTreeNode(nodeId, nodeParentId, nodeText);
				map.put(nodeId, node);
			}
		} catch (Exception e) {
			log.error("获取属性:", e);
		}

		for (ComboTreeNode node : map.values()) {
			ComboTreeUtils.findParentNode(map, node, roots);
		}

		StringBuilder sb = new StringBuilder();
		
		if (CollectionUtils.isNotEmpty(roots)) {
			String json = "";
			for (int i = 0; i < roots.size(); i ++) {
				ComboTreeNode rootNode = map.get(roots.get(i));
				
				if (rootNode != null) {
					if ("-1".equals(rootNode.getParentId())) {
						rootNode.setIconCls("icon-root");
					} else {// 单位访问权限控制（上级、平级、下级访问）
						rootNode.setIconCls("icon-children");
					}
					
					rootNode.setState("open");
					
					if (i == roots.size() - 1) {
						json += rootNode.toJsonString();
					} else {
						json += rootNode.toJsonString() + ",";
					}
				}
			}
			
			sb.append("var comboTreeData;\n" +
					  "var comboTreeStr = '" + json + "';\n");
			if (roots.size() > 1) {
				sb.append("comboTreeData = '[' + comboTreeStr + ']';\n" +
						  "eval('comboTreeData = ' + comboTreeData);\n");
			} else {
				sb.append("if(typeof(hiddenComboTreeRoot) != 'undefined' && (hiddenComboTreeRoot == true || hiddenComboTreeRoot == 'true')){\n" +
						  "		eval('comboTreeJson = ' + comboTreeStr);\n" +
						  "		comboTreeData = comboTreeJson['children'];\n" +
						  "}else{\n" +
						  "		comboTreeData = '[' + comboTreeStr + ']';\n" +
						  "		eval('comboTreeData = ' + comboTreeData);\n" +
						  "}\n");
			}
			sb.append("jQuery('#" + id + "').combotree({onChange:function(newValue, oldValue){if(oldValue != ''){changeComboTree(newValue);}},data:comboTreeData});");
		}

		return sb.toString();
	}
	
	public static void selectTree(Collection<? extends Object> list, String idProperty, String parentProperty, String textProperty, PageContext pageContext) {
		if (CollectionUtils.isNotEmpty(list)) {
			try {
				JspWriter out = pageContext.getOut();
				StringBuilder sb = new StringBuilder();
				for (Object object : list) {
					String nodeId = String.valueOf(PropertyUtils.getProperty(object, idProperty));
					String nodeParentId = String.valueOf(PropertyUtils.getProperty(object, parentProperty));
					String nodeText = String.valueOf(PropertyUtils.getProperty(object, textProperty));
					sb.append("{" + 
									"id:'" + nodeId + "'," + 
									"pId:'" + nodeParentId + "'," + 
									"name:'" + nodeText + "'," + 
									"iconSkin:'" + ("-1".equals(nodeParentId) ? "nodeRoot" : "nodeChildren") + "'" + 
									("-1".equals(nodeParentId) ? ",'open':'true'" : "") 
							+ "},");
				}
				if (sb.length() > 0) {
					out.print(sb.substring(0, sb.length() - 1));
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}
	
	public static String onceAlert(String message, Long memberId, Long accountId, String name){
		if(memberId == null){
			throw new IllegalArgumentException("memberId is null");
		}
		OncealertManager oncealertManager = (OncealertManager)ApplicationContextHolder.getBean("oncealertManager");
		if(!oncealertManager.isAlert(memberId,accountId, name)){ 
			return null;
		}
		
		return "<script>"
				+ "v3x.openWindow({"
				+ "url		: '/seeyon/main.do?method=commonAlert&memberId=" + memberId + "&accountId=" + ((accountId ==null) ? "" : accountId) + "&message="+urlEncoder(urlDecoder(message))+"&name="+name+"'," 
				+ "width	: 380,"
				+ "height	: 200,"
				+ "resizable	: 'yes'"
				+ "});" 
				+ "</script>";
	}

	/**
	 * 获取系统开关配置值
	 * 
	 * @param name
	 * @return
	 */
	public static String getSystemSwitch(String name){
		return getSystemConfig().get(name);
	}
	
	/**
	 * 检查请求IP,是否一定应用CA登录
	 * @param request
	 * @return
	 */
	public static String caCheckUserIP(HttpServletRequest request) {
		final String ConfigItem_MustCheckCA = "MustCheckCA";
		final String ConfigCategory = "IdentificationValidateCA";
		String showLoginWay = "";
		ConfigItem configItem = configManager.getConfigItem(ConfigCategory,
				ConfigItem_MustCheckCA);
		if ( configItem != null && "true".equals(configItem.getConfigValue())) {
			String ipString = configItem.getExtConfigValue();
			showLoginWay = ipString.indexOf(request.getRemoteAddr()) > -1?"IPisIncluding":"IPisNotIncluding";
		}else{
			showLoginWay = "unCheckIP";
		}
		return showLoginWay;
	}
	
	 private static WorkTimeSetManager workTimeSetManager = null;
	 private static WorkTimeManager workTimeManager = null;
	 private static Integer workTime = 0;
	 private static int year = 0;
     private static WorkTimeSetManager getWorkTimeSetManager(){
        if(workTimeSetManager == null){
        	workTimeSetManager = (WorkTimeSetManager)ApplicationContextHolder.getBean("workTimeSetManager");
        }
        return workTimeSetManager;
     }
     private static WorkTimeManager getWorkTimeManager(){
         if(workTimeManager == null){
        	 workTimeManager = (WorkTimeManager)ApplicationContextHolder.getBean("workTimeManager");
         }
         return workTimeManager;
      }
    
     public static String showDateByNature(Integer minutes){
 		return showDate(minutes,false);
 	 }
     public static String showDateByWork(Integer minutes){
  		return showDate(minutes,true);
  	 }
	/**
	 * 将分钟数按当前工作时间转化为按天表示的时间。
	 * 例如 1天7小时2分。
	 */
	public static String showDate(Integer minutes,boolean isWork){
		if(minutes == null || minutes == 0) 
			return "－";
		int dayH = 24*60;
		if(isWork){
			Calendar cal = Calendar.getInstance();
			int y = cal.get(Calendar.YEAR);
			if(year != y || workTime ==0 ){ //需要取工作时间
				workTime = getCurrentYearWorkTime();
				year = y;
			}
			if(workTime == 0 || workTime == null)
				return "－";
			
			dayH = workTime;
		}
		
		long m = minutes.longValue();
		long day = m/dayH;
		long d1 = m%dayH;
		long hour = d1/60;
		long minute = d1%60;
		String display 
			= ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", 
					"wfanalysis.date.display",
					day>0 ? day: "" , day > 0 ? 1:0,
					hour>0 ? hour : "" , hour >0 ?1:0,
					minute >0 ? minute : "",minute >0 ? 1 : 0);
		return display;
	}
	private static int  getCurrentYearWorkTime(){
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int t = 0;
		try {
			t = getWorkTimeManager().getEachDayWorkTime(year, CurrentUser.get().getLoginAccount());
		} catch (WorkTimeSetExecption e) {
			log.error("",e);
		}
		return t;
	}
	
	/**
	 * 返回两个时间之间按工作时间计算的分钟数。
	 * @param startDate
	 * @param endDate
	 * @param orgAccountId
	 * @return
	 */
	public static Long getMinutesBetweenDatesByWorkTime(Date startDate,Date endDate ,Long orgAccountId ){
		if(startDate == null
			||endDate == null
			||orgAccountId == null){
			return 0L;
		}
		Long workTime = 0L;
		try {
			workTime = getWorkTimeManager().getDealWithTimeValue(startDate,endDate,orgAccountId);
			workTime = workTime/(60*1000);
		} catch (WorkTimeSetExecption e1) {
			log.error("",e1);
		}
		return workTime;
	}
	public static Long convert2WorkTime(Long time, Long accountId){
		return workTimeManager.convert2WorkTime(time,accountId);
	} 
	/**
	 * 得到枚举的顺序号
	 * 
	 * @param e
	 * @return
	 */
	public static int getEnumOrdinal(Enum e){
		return e.ordinal();
	}
	/**
	 * 得到产品升级的时间
	 * 将安装时间与2012-04-30进行比较，取最靠后的时间。
	 * eg. 安装时间为2011-01-01.则这个函数返回2012-04-30,  如果安装时间为2012-09-30,则返回2012-09-30
	 */
	public static String getProductInstallDate4WF(){
		Date d = SystemEnvironment.getProductInstallDate();
		Calendar cal = Calendar.getInstance();
		//2012-04-30 : V3.5发版，流程效率分析模块开始运行
		cal.set(Calendar.YEAR, 2012);
		cal.set(Calendar.MONTH, 3);
		cal.set(Calendar.DAY_OF_MONTH,30);
		Date sDate = cal.getTime();
		String date = "";
		
//		if(d.before(sDate)){
//			d = sDate;
//		}
		
		if(d!=null){
			date = Datetimes.formatDate(d);
		}
		return date;
	}
	
	/**
	 * 得到分页组件的条数
	 * @return
	 */
	public static int getPaginationRowCount(){
		return Pagination.getRowCount(false);
	}
	
	/**
	 * 暂实现Map
	 * @param o
	 * @return
	 */
	public static String toJson(Object o){
		try {
			if(o instanceof Map){
				JSObject js = new JSObject();
				for (Iterator<Map.Entry<?, ?>> iterator = ((Map)o).entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<?, ?> entry = iterator.next();
					js.put(String.valueOf(entry.getKey()), entry.getValue());
				}
				
				return js.toString();
			}
		}
		catch (Exception e) {
		}
		
		return null;
	}
	
	/**
	 * 流程期限换算成时间点
	 * @param createTime 创建时间
	 * @param deadline 流程期限，单位是分钟
	 * @return 创建时间+流程期限（分钟）换算出来流程期限时间点
	 */
	public static String  showDeadlineTime(String createTime,Long deadline){
		if(deadline==null || deadline<=0){
			return null;
		}
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");   
		try {
			Date date=myFormatter.parse(createTime);
			Date afterDate = new Date(date.getTime() +(deadline!=null?deadline:0)*60*1000);
            return myFormatter.format(afterDate);
		} catch (ParseException e) {
          return null;
		}

	}

}