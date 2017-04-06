<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<SCRIPT language=JavaScript>
<!--
<%-- 
/**
 * 一级菜单定义
 * Menu(id, name, isShow)
 * MenuItem(uId, name, action, target, icon, description, isShow)
 */
--%>
<c:if test="${showMenu=='GROUP' || showMenu=='GOVGROUP'}">
<c:set value="${v3x:suffix()}" var="suffix"/>
<%-- 集团组织管理 --%>
var menu41 = new Menu(41, "<fmt:message key='menu.group.org.manage${suffix}'/>");
menu41.add(new MenuItem(1901, "<fmt:message key='menu.group.organ.setting'/>", "${organ}?method=accountTreeIndex&group=true", "main", '<c:url value="/common/images/left/icon/1901.gif" />', "", true));
menu41.add(new MenuItem(1908, "<fmt:message key='menu.group.bmpost.manage' />", "${organ}?method=organizationFrame&from=Post", "main", '<c:url value="/common/images/left/icon/1420.gif" />', "", true));
menu41.add(new MenuItem(1902, "<fmt:message key='menu.group.level${suffix}'/>", "${organ}?method=organizationFrame&from=GroupLevel", "main", '<c:url value="/common/images/left/icon/1902.gif" />', "", true));
menu41.add(new MenuItem(1904, "<fmt:message key='menu.organization.plurality.manager'/>", "<html:link renderURL='/plurality.do?method=pluralityFrame'/>", "main", '<c:url value="/common/images/left/icon/1904.gif" />', "", true));
menu41.add(new MenuItem(1905, "<fmt:message key='menu.organization.without.member'/>", "<html:link renderURL='/distribute.do?method=distributeFrame'/>", "main", '<c:url value="/common/images/left/icon/1905.gif" />', "", true));
menu41.add(new MenuItem(1907, "<fmt:message key='menu.group.team.manage' />", "${organ}?method=organizationFrame&from=Team", "main", '<c:url value="/common/images/left/icon/1406.gif" />', "", true));
menu41.add(new MenuItem(2202, "<fmt:message key='group.symbol.config.label${suffix}' bundle='${v3xOrgI18N}'/>", "<html:link renderURL='/accountManager.do'/>?method=showGroupSymbolConfig", "main", '<c:url value="/common/images/left/icon/1420.gif" />', "", true));
menu41.add(new MenuItem(2203, "<fmt:message key='menu.group.dept.manage'/>", "${organ}?method=moveDept", "main", '<c:url value="/common/images/left/icon/1420.gif" />', "", true));
menuArray.add(menu41);

<%-- 集团应用设置 --%>
var menu42 = new Menu(42, "<fmt:message key='menu.group.app.setting${suffix}'/>");
//menu42.add(new MenuItem(2001, "<fmt:message key='menu.group.edoc.setting'/>", "<html:link renderURL='/edocController.do'/>?method=sysMain", "main", '<c:url value="/common/images/left/icon/2001.gif" />', "", true));
menu42.add(new MenuItem(2004, "<fmt:message key='menu.group.doc.setting'/>", "<html:link renderURL='/docManager.do?method=docLibIndex&flag=group' />", "main", '<c:url value="/common/images/left/icon/2004.gif" />', "", true));
menu42.add(new MenuItem(2006, "<fmt:message key='menu.group.publicInfo.setting'/>", "<html:link renderURL='/newsType.do?method=newsManageIndex&flag=group' />", "main", '<c:url value="/common/images/left/icon/2102.gif" />', "", true));
menu42.add(new MenuItem(2007, "<fmt:message key='menu.menuManager.label'/>", "<html:link renderURL='/menuManager.do'/>?method=menuManagerIndex", "main", '<c:url value="/common/images/left/icon/2007.gif" />', "", true));
<c:if test="${isShowMobileMenu == true}">
menu42.add(new MenuItem(2008, "<fmt:message key='menu.mobile'/>", "<html:link renderURL='/mobileManager.do'/>?method=popedomManage", "main", '<c:url value="/common/images/left/icon/2305.gif" />', "", true));
</c:if>
menu42.add(new MenuItem(2009, "<fmt:message key='menu.space.group.manage${suffix}'/>", "<html:link renderURL='/space.do' psml='default-page.psml' forcePortal='true'/>&method=home&type=group&isGroup=true", "main", '<c:url value="/common/images/left/icon/2305.gif" />', "", true));
menu42.add(new MenuItem(2002, "<fmt:message key='menu.common.type.setting'/>", "<html:link renderURL='/mtContentTemplate.do' />?method=homeEntry", "main", '<c:url value="/common/images/left/icon/1515.gif" />', "", true));
<c:if test="${isIpcontrol=='true'}">
menu42.add(new MenuItem(2003, "<fmt:message key='menu.common.accesscontrol.setting'/>", "<html:link renderURL='/ipcontrol.do' />?method=index", "main", '<c:url value="/common/images/left/icon/705.gif" />', "", true));
</c:if>
menuArray.add(menu42);

<%--工作管理--%>
var menu44 = new Menu(44, "<fmt:message key='menu.work.manager'/>");
menu44.add(new MenuItem(2101, "<fmt:message key='menu.workflow.manager'/>", "<html:link renderURL='/collaboration.do?method=workflowManager' />", "main", '<c:url value="/common/images/left/icon/1905.gif" />', "", true));
menu44.add(new MenuItem(2102, "<fmt:message key='menu.workflow.stat.'/>", "<html:link renderURL='/collaboration.do?method=workflowStatMain' />", "main", '<c:url value="/common/images/left/icon/2301.gif" />', "", true));
menu44.add(new MenuItem(2103, "<fmt:message key='menu.log'/>", "<html:link renderURL='/appLog.do?method=mainFrame' />", "main", '<c:url value="/common/images/left/icon/2401.gif" />', "", true));
menu44.add(new MenuItem(2104, "<fmt:message key='menu.worktimeset'/>", "<html:link renderURL='/workTimeSetController.do?method=viewByCalendar'/>", "main", '<c:url value="/common/images/left/icon/603.gif"/>', "", true));
menuArray.add(menu44);

<%--管理员信息设置--%>
var menu43 = new Menu(43, "<fmt:message key='menu.run.base.setup.managerset'/>");
menu43.add(new MenuItem(1909, "<fmt:message key='menu.run.base.setup.grouptouser'/>", "<html:link renderURL='/accountManager.do?method=groupToUserManager' />", "main", '<c:url value="/common/images/left/icon/1905.gif" />', "", true));
menu43.add(new MenuItem(1906, "<fmt:message key='menu.manager.modifyPassword'/>", "<html:link renderURL='/accountManager.do?method=groupManagerFrame' />", "main", '<c:url value="/common/images/left/icon/2301.gif" />', "", true));
menuArray.add(menu43);

<%
com.seeyon.v3x.common.taglibs.functions.MainFunction.getPluginMenuOfSystem(com.seeyon.v3x.plugin.MenuLocation.group, pageContext);
%>

</c:if>
//-->
</script>