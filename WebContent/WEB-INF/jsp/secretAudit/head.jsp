<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/collaboration" prefix="col"%>
<%@ taglib uri="http://v3x.seeyon.com/bridges/spring-portlet-html" prefix="html"%>
<fmt:setBundle basename="com.seeyon.v3x.secret.resources.i18n.SecretAuditResources"/>
<fmt:setBundle basename="com.seeyon.v3x.organization.resources.i18n.OrganizationResources" var="orgI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources" var="v3xCommonI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource" var="colI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.main.resources.i18n.MainResources" var="v3xMainI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.webmail.resources.i18n.WebMailResources" var="v3xMailI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.system.resources.i18n.SysMgrResources" var="sysMgrResources"/>
<fmt:setBundle basename="com.seeyon.v3x.hr.resource.i18n.HRResources" var="v3xHRI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.localeselector.resources.i18n.LocaleSelectorResources" var="localeI18N"/>

<fmt:message key="common.datetime.pattern" var="datePattern" bundle="${v3xCommonI18N}"/>

<html:link renderURL="/ldap.do" var="ldapSynchron"/>  
<html:link renderURL='/genericController.do' var="genericController" />
<html:link renderURL='/secretAudit.do' var="secretAuditURL" />

<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/default.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/edoc/css/edoc.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/isearch/css/isearch.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/form/css/SeeyonForm.css${v3x:resSuffix()}" />">
<link type="text/css" rel="stylesheet" href="<c:url value="/common/js/xtree/xtree.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/hr/css/default.css${v3x:resSuffix()}" />">

<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/secretAudit/js/secretAudit.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/menu/xmenu.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/plugin/ldap/js/ldap.js${v3x:resSuffix()}" />"></script>
${v3x:skin()}
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/V3X.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript">
<!--
var jsContextPath="${pageContext.request.contextPath}";
var genericControllerURL = "${genericController}?ViewPage=";
var secretAuditURL = "${secretAuditURL}";

var v3x = new V3X();
v3x.init("${pageContext.request.contextPath}", "${v3x:getLanguage(pageContext.request)}");
_ = v3x.getMessage;

v3x.loadLanguage("/apps_res/log/i18n");
v3x.loadLanguage("/apps_res/organization/js/i18n");
v3x.loadLanguage("/apps_res/secretAudit/js/i18n");
v3x.loadLanguage("/apps_res/v3xmain/js/i18n");

var jsStr_ClickInput=v3x.getMessage("edocLang.edoc_alertClickInput");
//-->
</script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/edoc/SeeyonForm3.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/xtree/xtree.js${v3x:resSuffix()}"/>"></script>
<script type="text/javascript" src="<c:url value='/common/js/util/URI.js${v3x:resSuffix()}'/>"></script>