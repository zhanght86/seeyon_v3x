<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<SCRIPT language=JavaScript><!--

<%-- 
/**
 * 一级菜单定义
 * Menu(id, name, isShow)
 * MenuItem(uId, name, action, target, icon, description, isShow)
 */
--%>
<%--系统管理--%>
var menu51 = new Menu(51, "<fmt:message key='menu.system.manage'/>");
<%--
<c:choose>
<c:when test="${showMenu=='GROUP'}"><c:set value="menu.group.info.set" var="accountInfoMenuKey" /></c:when>
<c:when test="${showMenu=='GOVGROUP'}"><c:set value="menu.group.info.set.GOV" var="accountInfoMenuKey" /></c:when>
<c:otherwise><c:set value="menu.group.info.enter" var="accountInfoMenuKey" /></c:otherwise>
</c:choose>
menu51.add(new MenuItem(2201, "<fmt:message key='${accountInfoMenuKey}'/>", "${organ}?method=editGroupAccount&from=updateAccount&accountAdminis=account&isDetail=readOnly&isSystemOperation=true&showLocation=true", "main", '<c:url value="/common/images/left/icon/5101.gif" />', "", true));
--%>
menu51.add(new MenuItem(5101, "<fmt:message key='work.area.setup.metadata'/>", "<html:link renderURL='/metadata.do?method=userDefinedindex' />", "main", '<c:url value="/common/images/left/icon/5103.gif" />', "", true));
<%--
menu51.add(new MenuItem(1701, "<fmt:message key='menu.run.base.setup.personspace'/>", "<html:link renderURL='/docSpace.do?method=index' />", "main", '<c:url value="/common/images/left/icon/1701.gif"/>', "", true));
--%>
menu51.add(new MenuItem(2301, "<fmt:message key='menu.manager.modifyPassword'/>", "<html:link renderURL='/manager.do?method=managerFrame' />", "main", '<c:url value="/common/images/left/icon/2301.gif" />', "", true));
menuArray.add(menu51);

<%--系统设置--%>
var menu52 = new Menu(52, "<fmt:message key='menu.system.setting'/>");
menu52.add(new MenuItem(2305, "<fmt:message key='system.open'/>", "<html:link renderURL='/systemopen.do?method=showSystemOpenSpace&userType=system' />", "main", '<c:url value="/common/images/left/icon/2304.gif" />', "", true));
menu52.add(new MenuItem(2302, "<fmt:message key='menu.system.subarea'/>", "<html:link renderURL='/partition.do?method=partitionFrame&from=Post' />", "main", '<c:url value="/common/images/left/icon/2302.gif" />', "", true));
<c:if test="${v3x:getSysFlagByName('is_gov_only')}">
menu52.add(new MenuItem(2309, "<fmt:message key='menu.system.skin'/>", "<html:link renderURL='/skinManager.do?method=index' />", "main", '', "", true));
</c:if>
menu52.add(new MenuItem(2303, "<fmt:message key='menu.run.base.loginImage'/>", "<html:link renderURL='/accountManager.do?method=showLoginImage' />", "main", '<c:url value="/common/images/left/icon/2303.gif" />', "", true));
<%--
menu52.add(new MenuItem(2304, "<fmt:message key='menu.run.base.weatherConfig'/>", "<html:link renderURL='/accountManager.do?method=showWeatherConfig' />", "main", '<c:url value="/common/images/left/icon/1003.gif" />', "", true));
--%>
menu52.add(new MenuItem(2306, "<fmt:message key='menu.systemMailbox.setting'/>", "<html:link renderURL='/webmail.do'/>?method=systemMailboxSetting", "main", '<c:url value="/common/images/left/icon/406.gif" />', "", true));
<%if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("luceneIndex")){ %>
menu52.add(new MenuItem(2307, "<fmt:message key='menu.system.index.setting'/>",   "<html:link renderURL='/indexInterface.do?method=showIndexConfig' />", "main", '<c:url value="/common/images/left/icon/2304.gif" />', "", true));
<%}%>
<%if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD.name())){ %>
<fmt:setBundle basename="com.seeyon.v3x.plugin.ldap.resource.i18n.LDAPSynchronResources" var="ldaplocale"/>
menu52.add(new MenuItem(2308, "<fmt:message key='ldap.system.name' bundle='${ldaplocale}'/>","<html:link renderURL='/ldap.do?method=setLdapSwitch' />", "main", '<c:url value="/common/images/left/icon/2304.gif" />', "", true));
<%}%>
menuArray.add(menu52);
<%--系统维护--%>
var menu53 = new Menu(53, "<fmt:message key='menu.system.maintenance'/>");
<%--
menu53.add(new MenuItem(2401, "<fmt:message key='menu.log'/>", "<html:link renderURL='/appLog.do?method=mainFrame' />", "main", '<c:url value="/common/images/left/icon/2401.gif" />', "", true));
--%>
menu53.add(new MenuItem(2403, "<fmt:message key='menu.securityLog'/>", "<html:link renderURL='/logonLog.do?method=summaryStat' />", "main", '<c:url value="/common/images/left/icon/2401.gif" />', "", true));
menu53.add(new MenuItem(2404, "<fmt:message key='menu.system.runtimeServer'/>", "<html:link renderURL='/serverState.do' />", "main", '<c:url value="/common/images/left/icon/2404.gif" />', "", true));
menu53.add(new MenuItem(2402, "<fmt:message key='menu.message.manager'/>", "<html:link renderURL='/messageManager.do?method=initHome' />", "main", '<c:url value="/common/images/left/icon/2402.gif" />', "", true));
<%-- 新增锁定账户管理 --%>
menu53.add(new MenuItem(2405, "<fmt:message key='menu.lockedUser.manager'/>", "<html:link renderURL='/lockedUserManager.do?method=initHome' />", "main", '<c:url value="/common/images/left/icon/2405.gif" />', "", true));
menu53.add(new MenuItem(2406, "<fmt:message key='menu.system.maintenance.infoOpen'/>", "<html:link renderURL='/infoOpen.do?method=index' />", "main", '<c:url value="/common/images/left/icon/2405.gif" />', "", true));
<fmt:setBundle basename="com.seeyon.v3x.indexresume.resource.i18n.IndexResumeResources" var="indexResume"/>
<%if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("luceneIndex")){ %>
menu53.add(new MenuItem(2407, "<fmt:message key='indexresume.oper.mainmenu' bundle='${indexResume}'/>", "<html:link renderURL='/indexResume.do?method=showSettingPage' />", "main", '<c:url value="/common/images/left/icon/2405.gif" />', "", true));
<%}%>
<c:if test="${v3x:hasPlugin('dataDump')}">
menu53.add(new MenuItem(2408, "<fmt:message key='menu.StoreRule.table' />", "<html:link renderURL='/storeRule.do?method=index' />", "main", '<c:url value="/common/images/left/icon/2405.gif" />', "", true));
</c:if>
menuArray.add(menu53);

<%--管理员信息设置--%>
var menu54 = new Menu(54, "<fmt:message key='menu.systemExpand'/>");

menu54.add(new MenuItem(5102, "<fmt:message key='menu.rss'/>", "<html:link renderURL='/rssManager.do?method=index' />", "main", '<c:url value="/common/images/left/icon/5102.gif" />', "", true));

menu54.add(new MenuItem(2005, "<fmt:message key='menu.refsystem'/>", "<html:link renderURL='/linkManager.do?method=linkIframe' />", "main", '<c:url value="/common/images/left/icon/2005.gif" />', "", true));
<%-- 扩展栏目--%>
//menu54.add(new MenuItem(5405, "<fmt:message key='menu.space.sectionExpand'/>", "<html:link renderURL='/sectionDefinition.do?method=main' />", "main", '<c:url value="/common/images/left/icon/2304.gif" />', "", true));
<c:if test="${v3x:hasPlugin('dee')}">
	<%
	 if(com.seeyon.v3x.plugin.deeSection.DeeSectionFunction.isOpenPortalSection()){
	%>
	menu54.add(new MenuItem(5406, "<fmt:message key='menu.space.deeSection'/>", "<html:link renderURL='/deeSectionController.do?method=main' />", "main", '<c:url value="/common/images/left/icon/2304.gif" />', "", true));
	<%}%>
</c:if>
menuArray.add(menu54);

<%
com.seeyon.v3x.common.taglibs.functions.MainFunction.getPluginMenuOfSystem(com.seeyon.v3x.plugin.MenuLocation.system, pageContext);
%>
//-->
</script>