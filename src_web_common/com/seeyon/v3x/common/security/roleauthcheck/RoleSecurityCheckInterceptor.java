package com.seeyon.v3x.common.security.roleauthcheck;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.controller.GenericController;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.fileupload.FileUploadController;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.selectPeople.SelectPeopleController;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.main.controller.MainController;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.annotation.AnnotationAware;
import com.seeyon.v3x.util.annotation.AnnotationFactory;
import com.seeyon.v3x.util.annotation.ClassAnnotation;
import com.seeyon.v3x.util.annotation.MethodAnnotation;

/**
 * <pre>
 * 用户角色权限校验拦截器，实现Controller类或方法级别上的拦截，保证某些url地址只能被特定的角色对象访问。
 * 拦截信息包括两个来源：
 * 1.注解（主干开发时采取注解较为合适）；
 * 2.配置文件（分支开发、修改客户bug时采取配置文件较为合适）。
 * 优先读取注解上的校验信息，如无，则继续读取配置文件中与之对应的校验信息。
 * 无论是注解还是配置文件，都优先读取方法上的校验信息，如无，则继续读取该类上的校验信息。
 * </pre>
 * @see CheckRoleAccess
 * @see RoleType
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-6-30
 */
public class RoleSecurityCheckInterceptor extends HandlerInterceptorAdapter implements AnnotationAware {
	private transient final Log logger = LogFactory.getLog(getClass());

	private static final String Method_Index = "index";
	/** 通过注解读取的角色权限校验信息，key - clazzName, value - List&ltRoleType&gt */
	private Map<String, List<RoleType>> clazzNeedRoleCheck = new HashMap<String, List<RoleType>>();
	
	/** 通过注解读取的角色权限校验信息，key - methodName, value - List&ltRoleType&gt */
	private Map<String, List<RoleType>> methodNeedRoleCheck = new HashMap<String, List<RoleType>>();
	
	/** 通过配置文件读取的角色权限校验信息，key - clazzName, methodName, value - RoleType... */
	private Properties roleCheckProps;
	
	private OrgManager orgManager;
	private TempleteCategoryManager templeteCategoryManager;
	private SpaceManager spaceManager;

	private String getMethodName(Object handler, HttpServletRequest request) {
		return handler.getClass().getCanonicalName() + '.' + StringUtils.defaultIfEmpty(request.getParameter("method"), Method_Index);
	}
	
	/**
	 * 辅助开发调试之用，注解信息不从缓存里面取，而是使用反射直接读，以便本地修改、编译覆盖后可直接生效
	 */
	private boolean preHandle4Debug(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		long time1 = System.currentTimeMillis();
		
		User user = CurrentUser.get();
		//CommonInterceptor中不需要验证登陆的部分，也无需进行角色权限校验
		if(user == null)
			return true;

		boolean result = false;
		//优先处理方法上的注解，其次处理类上的注解，如果两者上面都无注解信息，则再从配置文件中读取
		Method method = handler.getClass().getMethod(StringUtils.defaultIfEmpty(request.getParameter("method"), Method_Index), 
				HttpServletRequest.class, HttpServletResponse.class);
		Annotation[] annotations = method.getAnnotations();
		if(annotations == null || annotations.length == 0) {
			annotations = handler.getClass().getAnnotations();
			if(annotations == null || annotations.length == 0) {
				return this.checkRoleAccessFromConf(request, response, handler, user);
			}
			else {
				result = this.checkRoleAccessFromAnnotations(annotations, request, response, handler, user);
			}
		}
		else {
			result = this.checkRoleAccessFromAnnotations(annotations, request, response, handler, user);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[角色权限校验] - 耗时：" + (System.currentTimeMillis() - time1) + " MS, " +
						 "当前校验：" + user.getName() + "校验结果：[" + (result ? "通过" : "不通过") + "]");
		}
		return result;
	}
	
	/**
	 * <pre>
	 * 配合debug模式下，直接读取类或方法的注解信息，并进而读取注解的角色校验信息，进行权限访问校验
	 * 如果注解中没有角色权限校验信息，则再从配置文件中读取
	 * </pre>
	 */
	private boolean checkRoleAccessFromAnnotations(Annotation[] annotations, 
			HttpServletRequest request, HttpServletResponse response, Object handler, User user) throws IOException {
		for(Annotation annotation : annotations) {
			if(annotation instanceof CheckRoleAccess) {
				CheckRoleAccess roleAccess = (CheckRoleAccess)annotation;
				RoleType[] roleTypes = roleAccess.roleTypes();
				List<RoleType> types = FormBizConfigUtils.parseArr2List(roleTypes);
				return this.checkRoleAccess(request, response, handler, user, types);
			}
		}
		
		return checkRoleAccessFromConf(request, response, handler, user);
	}

	/** 读取配置文件信息中的权限校验信息并得到校验结果  */
	private boolean checkRoleAccessFromConf(HttpServletRequest request, HttpServletResponse response, Object handler, User user) throws IOException {
		String methodName = this.getMethodName(handler, request);
		String clazzName = handler.getClass().getCanonicalName();
		
		String typesStr = this.getRoleTypesFromCache(methodName, clazzName);
		if(StringUtils.isBlank(typesStr)) {
			return true;
		}
		else {
			List<RoleType> types = this.parseStr2RoleTypes(typesStr);
			return this.checkRoleAccess(request, response, handler, user, types);
		}
	}
	
	private static List<String> NOCHECK_CLAZZ = new ArrayList<String>();
	static {
		NOCHECK_CLAZZ.add(SelectPeopleController.class.getCanonicalName());
		NOCHECK_CLAZZ.add(FileUploadController.class.getCanonicalName());
		NOCHECK_CLAZZ.add(GenericController.class.getCanonicalName());
		NOCHECK_CLAZZ.add(MainController.class.getCanonicalName());
	}
	
	/**
	 * 部署、产品环境下调用此方法，注解信息从缓存中读取，参见：{@link #setAnnotationFactory(AnnotationFactory)}
	 */
	private boolean preHandle4Product(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		User user = CurrentUser.get();
		//CommonInterceptor中不需要验证登陆的部分，也无需进行角色权限校验
		if(user == null)
			return true;

		//优先处理方法上的注解，其次处理类上的注解
		String methodName = this.getMethodName(handler, request);
		String clazzName = handler.getClass().getCanonicalName();
		List<RoleType> roleTypes = methodNeedRoleCheck.get(methodName);
		if(CollectionUtils.isEmpty(roleTypes)) {
			roleTypes = clazzNeedRoleCheck.get(clazzName);
			if(CollectionUtils.isEmpty(roleTypes)) {
				if(user.isAdmin() && !NOCHECK_CLAZZ.contains(clazzName)) {
					if(logger.isDebugEnabled()) {
						logger.debug("[" + user.getName() + " - " + methodName + "]未添加角色类型校验注解!");
					}
				}
				
				String roleTypeStr = this.getRoleTypesFromCache(methodName, clazzName);
				if(StringUtils.isBlank(roleTypeStr)) {
					return true;
				}
				else {
					roleTypes = this.parseStr2RoleTypes(roleTypeStr);
				}
			}
		}
		
		return this.checkRoleAccess(request, response, handler, user, roleTypes);
	}
	
	/**
	 * 将配置文件中对应角色管理员解析为枚举集合
	 * @param roleTypes
	 */
	private List<RoleType> parseStr2RoleTypes(String roleTypes) {
		List<RoleType> result = null;
		if(StringUtils.isNotBlank(roleTypes)) {
			String[] roles = StringUtils.split(roleTypes.trim(), ',');
			if(roles != null && roles.length > 0) {
				result = new ArrayList<RoleType>(roles.length);
				RoleType type = null;
				for(int i=0; i<roles.length; i++) {
					if(StringUtils.isNotBlank(roles[i])) {
						type = roleTypeCache.get(roles[i].trim());
					}
					
					if(type == null) {
						logger.warn("无法获取当前配置[" + roles[i] + "]对应的角色类型，请您检查配置文件[conf/rolesecuritycheck.properties]内容是否正确!");
					}
					else {
						result.add(type);
					}
				}
			}
		}
		return result;
	}
	
	/** 角色类型枚举缓存，key - RoleTypeName，value - RoleType */
	private static Map<String, RoleType> roleTypeCache;
	static {
		roleTypeCache = new HashMap<String, RoleType>();
		RoleType[] roleTypes = RoleType.values();
		for(RoleType type : roleTypes) {
			roleTypeCache.put(type.name(), type);
		}
	}
	
	/**
	 * 从配置文件缓存中读取校验标记信息，先从方法上找，如没有，再从类名上找
	 * @param methodName    方法名
	 * @param clazzName	   	类名
	 */
	private String getRoleTypesFromCache(String methodName, String clazzName) {
		String result = roleCheckProps.getProperty(methodName);
		if(StringUtils.isBlank(result)) {
			result = roleCheckProps.getProperty(clazzName);
		}
		return result;
	}
    
	/**
	 * <pre>
	 * 拦截器主要业务逻辑，在每个Controller的方法执行之前进行角色访问权限校验：
	 * 在本地开发时，为方便即时修改生效，可调用{@link #preHandle4Debug(HttpServletRequest, HttpServletResponse, Object)}
	 * 在正式提交时，在部署、产品环境下，应调用{@link #preHandle4Produce(HttpServletRequest, HttpServletResponse, Object)}
	 * <pre>
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//		if(logger.isDebugEnabled())
//			return this.preHandle4Debug(request, response, handler);
//		else
//			return this.preHandle4Product(request, response, handler);
		
		return this.preHandle4Product(request, response, handler);
	}

	private boolean checkRoleAccess(HttpServletRequest request, HttpServletResponse response,
			Object handler, User user, List<RoleType> types) throws IOException {
		boolean canAccess = false;
		String methodName = this.getMethodName(handler, request);
		if(CollectionUtils.isEmpty(types)) {
			logger.warn("当前所要拦截进行权限校验的方法[" + methodName + "]，未注明角色权限类型");
		}
		else {
			canAccess = this.roleSecurityCheck(user, types);
		}

		if(!canAccess) {
			RoleSecurityCheckInterceptor.alertSecurityMsg(response);
			
			logger.warn("用户[" + user.getName() + ", IP=" + user.getRemoteAddr() + "]，不具备[" + 
						StringUtils.join(types, ',') + "]角色权限，正在试图越权访问[" + methodName + "]！");
		}
		
//		if(logger.isDebugEnabled()) {
//			logger.debug("[角色权限校验] - 当前校验：用户[" + user.getName() + "] 是否具有  " + StringUtils.join(types, ',') + " 角色权限，" +
//						 "校验结果：[" + (canAccess ? "通过" : "不通过") + "]");
//		}
		
		return canAccess;
	}
	
	/**
	 * 拦截器对某些较为具体的业务逻辑处理可能无法胜任，此时需要在对应方法中直接编码校验，可调用此静态方法弹出提示信息
	 */
	public static void alertSecurityMsg(HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + SystemEnvironment.getA8ContextPath() + "/common/js/V3X.js\"></script>");
		out.println("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + SystemEnvironment.getA8ContextPath() + "/apps_res/v3xmain/js/seeyon.js\"></script>");
		out.println("<script>" +
					"	alert('" + ResourceBundleUtil.getString(FormBizConfigConstants.COMMON_RESOURCE, "role.security.warning") + "');" +
					//避免处理中界面无法关闭，导致工作区无法点击
					"	try { parent.getA8Top().endProc(); } catch(e) {}" +
					//关闭窗口(如果页面结构允许)
					//branches_a8_v350_r_gov GOV-2640 于荒津修改关闭窗口功能 start
					"	try { parent.getA8Top().close(); } catch(e) {window.close();} " + 
					//branches_a8_v350_r_gov GOV-2640 于荒津修改关闭窗口功能 end
					"</script>");
		out.flush();
	}

	/**
	 * 角色权限校验
	 * @param user	当前用户
	 * @param types	角色类型，只要当前用户拥有其中之一的权限即通过
	 * @param request	当前用户发起请求
	 */
	private boolean roleSecurityCheck(User user, List<RoleType> types) {
		for(RoleType type : types) {
			try {
				if(this.roleSecurityCheck(user, type)) {
					return true;
				}
			} 
			catch (BusinessException e) {
				logger.error("校验用户是否具有[" + type + "]角色权限时出现异常：", e);
			}
		}
		return false;
	}

	/**
	 * 角色权限校验，其中部分涉及到兼职单位、部门的有待随着对应应用同步调整
	 * @param user	当前用户
	 * @param type	角色类型
	 * @param request	当前用户发起请求
	 */
	private boolean roleSecurityCheck(User user, RoleType type) throws BusinessException {
		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
		switch(type) {
		case NeedNoCheck : 
			return true;
		case SystemAdmin :
			return user.isSystemAdmin();
		case GroupAdmin :
			return user.isGroupAdmin();
		case AuditAdmin :
			return user.isAuditAdmin();
	    case SecretAdmin :
	        return user.isSecretAdmin();
		case Administrator :
			return user.isAdministrator();
		case HrAdmin :
			return MainHelper.isHRAdmin(orgManager);
		case FormAdmin :
			return MainHelper.isFORMAdmin(orgManager);
		case DepartmentManager :
			return Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER, user);
		case DepartmentAdmin : 
			return Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_DEPADMIN, user);
		case SalaryAdmin : 
			return Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_SALARYADMIN, user);
		case AccountEdocExchange :
			boolean accountExchangeFlag = EdocRoleHelper.isAccountExchange();
			if(!accountExchangeFlag && isGov) {
				List<Long> agentToIds = MemberAgentBean.getInstance().getAgentToMemberId(ApplicationCategoryEnum.edoc.key(), user.getId());
				if(agentToIds != null && agentToIds.size()>0) {
					for(int i=0; i<agentToIds.size(); i++) {
						if(accountExchangeFlag) {
							break;
						}
						accountExchangeFlag = EdocRoleHelper.isAccountExchange(agentToIds.get(i));
					}					
				}
			}
			return accountExchangeFlag;
		case DepartmentEdocExchange : 
			boolean deptExchangeFlag = EdocRoleHelper.isDepartmentExchangeOfLoginAccout();
			if(!deptExchangeFlag && isGov) {
				List<Long> agentToIds = MemberAgentBean.getInstance().getAgentToMemberId(ApplicationCategoryEnum.edoc.key(), user.getId());
				if(agentToIds != null && agentToIds.size()>0) {
					for(int i=0; i<agentToIds.size(); i++) {
						if(deptExchangeFlag) {
							break;
						}
						if(agentToIds.get(i) != null) {
							deptExchangeFlag = EdocRoleHelper.isDepartmentExchangeOfLoginAccout(agentToIds.get(i));
						}
					}					
				}
			}
			return deptExchangeFlag;
		case ProjectCreator : 
			return Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_PROJECTBUILD, user);
		case TempleteManager :
			return templeteCategoryManager.isTempleteManager(user.getId(), user.getLoginAccount());
		case AccountEdocAdmin :
		    return EdocRoleHelper.isAccountEdocAdmin();
		case SpaceManager : 
			return !this.spaceManager.getCanManagerSpace(user.getId()).isEmpty();
		//branches_a8_v350_r_gov GOV-2628 于荒津 添加会议室管理员 start
		case UnitsMeetingAdmin : 
			return Functions.isRole("UnitsMeetingAdmin", user);
		case AccountInfoAdmin : 
			return Functions.isRole("AccountInfoAdmin", user);
		//branches_a8_v350_r_gov GOV-2628 于荒津 添加会议室管理员 end
		}
		return false;
	}

	/**
	 * 将注解对应的类、方法信息载入缓存
	 */
	public void setAnnotationFactory(AnnotationFactory annotationFactory) {
		long time1 = System.currentTimeMillis();
		
		Set<ClassAnnotation> classAnnotations = annotationFactory.getAnnotationOfClass(CheckRoleAccess.class);
		Set<MethodAnnotation> methodAnnotation = annotationFactory.getAnnotationOfMethod(CheckRoleAccess.class);

		if(CollectionUtils.isNotEmpty(methodAnnotation)) {
			for(MethodAnnotation methodAnno : methodAnnotation) {
				RoleType[] arr = ((CheckRoleAccess)methodAnno.getAnnotation()).roleTypes();
				List<RoleType> list = FormBizConfigUtils.parseArr2List(arr);
				methodNeedRoleCheck.put(methodAnno.getClazz().getCanonicalName() + '.' + methodAnno.getMethodName(), list);
			}
		}

		if(CollectionUtils.isNotEmpty(classAnnotations)) {
			for(ClassAnnotation clazzAnno : classAnnotations) {
				RoleType[] arr = ((CheckRoleAccess)clazzAnno.getAnnotation()).roleTypes();
				List<RoleType> list = FormBizConfigUtils.parseArr2List(arr);
				clazzNeedRoleCheck.put(clazzAnno.getClazz().getCanonicalName(), list);
			}
		}
		
		logger.info("角色权限校验注解信息读取完毕，耗时：" + (System.currentTimeMillis() - time1) + " MS.");
	}
	
	/**
	 * 将配置文件中的角色访问权限校验信息载入缓存
	 */
	public void init() {
		FileInputStream fis = null;
		try {
			long time1 = System.currentTimeMillis();
			URL url = this.getClass().getResource("/conf/rolesecuritycheck.properties");
			
			String fileName = null;
			if(url != null) {
				fileName = url.getFile();
			}
			
			if(StringUtils.isNotBlank(fileName)) {
				fis = new FileInputStream(fileName);
				
				roleCheckProps = new Properties();
				roleCheckProps.load(fis);
				
				logger.info("角色权限校验配置文件读取完毕，耗时：" + (System.currentTimeMillis() - time1) + " MS.");
			}
			else {
				logger.warn("无法在classpath指定路径下[conf/rolesecuritycheck.properties]找到角色权限校验配置文件!");
			}
		} 
		catch (IOException e) {
			logger.error("读取角色权限校验配置文件过程中出现异常：", e);
		} 
		finally {
			IOUtils.closeQuietly(fis);
		}
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setTempleteCategoryManager(TempleteCategoryManager templeteCategoryManager) {
        this.templeteCategoryManager = templeteCategoryManager;
    }

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

}
