<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ include file="../common/INC/noCache.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://v3x.seeyon.com/bridges/spring-portlet-html" prefix="html"%>

<fmt:setBundle basename="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources" var="v3xCommonI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.hr.resource.i18n.HRResources" var="v3xHRI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.secret.resources.i18n.SecretAuditResources" var="v3xSecretI18N"/>
<fmt:message key="common.datetime.pattern" var="datePatternOrg" bundle="${v3xCommonI18N}"/>
<fmt:message key="common.date.pattern" var="datePattern" bundle="${v3xCommonI18N}"/>
<html:link renderURL='/genericController.do' var="genericController" />
<html:link renderURL="/collaboration.do" var="collaborationURL" />
<html:link renderURL="/popeleLeave.do" var="popeleLeaveURL" />
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/default.css${v3x:resSuffix()}" />">
<link rel="STYLESHEET" type="text/css" href="<c:url value="/apps_res/form/css/form.css${v3x:resSuffix()}" />">    
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/menu/xmenu.js${v3x:resSuffix()}" />"></script>
${v3x:skin()}
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/V3X.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/prototype.js${v3x:resSuffix()}" />"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/hr/css/default.css${v3x:resSuffix()}" />">

<script type="text/javascript">
var v3x = new V3X();
v3x.init("${pageContext.request.contextPath}", "${v3x:getLanguage(pageContext.request)}");
_ = v3x.getMessage;
v3x.loadLanguage("/apps_res/organization/js/i18n");
v3x.loadLanguage("/apps_res/v3xmain/js/i18n");
var organizationURL = "<html:link renderURL='/organization.do' />";
var genericURL = "${genericController}?ViewPage=";
var organizationCancale = "<c:url value='/common/detail.jsp' />";
var popeleLeaveURL= "${popeleLeaveURL}";
</script>
<fmt:setBundle basename="com.seeyon.v3x.localeselector.resources.i18n.LocaleSelectorResources" var="locale"/>
<fmt:message key="common.datetime.pattern" var="datetimePattern" bundle="${v3xCommonI18N}"/>
<fmt:message key="common.data.pattern" var="dataPattern" bundle="${v3xCommonI18N}"/>
<fmt:setBundle basename="com.seeyon.v3x.plugin.ldap.resource.i18n.LDAPSynchronResources" var="ldaplocale"/>
<html:link renderURL="/organization.do" var="organizationURL"/>                                         
<html:link renderURL="/organization.do" psml="default-page.psml" forcePortal="true" var="organizationPortalURL"/>                                         
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/organization/js/organization.js${v3x:resSuffix()}" />"></script>
<html:link renderURL="/ldap.do" var="ldapSynchron"/>  
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/xtree/xtree.js${v3x:resSuffix()}"/>"></script>
<link type="text/css" rel="stylesheet" href="<c:url value="/common/js/xtree/xtree.css${v3x:resSuffix()}" />">
<fmt:setBundle basename="com.seeyon.v3x.localeselector.resources.i18n.LocaleSelectorResources" var="localeI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.organization.resources.i18n.OrganizationResources"/>
<fmt:setBundle basename="com.seeyon.v3x.main.resources.i18n.MainResources" var="v3xMainI18N"/>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/plugin/ldap/js/ldap.js${v3x:resSuffix()}" />"></script>