<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
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
<%-- 组织信息管理 --%>
var menu31 = new Menu(31, "<fmt:message key='menu.organization.info.manage' />"); 
menu31.add(new MenuItem(1401, "<fmt:message key='menu.organization.top.account' />", "${organ}?method=editAccount&from=updateAccount&accountAdminis=account&isDetail=readOnly&showSymbol=true", "main", '<c:url value="/common/images/left/icon/3101.gif" />', "", true));
menu31.add(new MenuItem(1402, "<fmt:message key='menu.organization.top.department' />", "<html:link renderURL='/organization.do?method=showframe&style=tree' />", "main", '<c:url value="/common/images/left/icon/3102.gif" />', "", true));
menu31.add(new MenuItem(1403, "<fmt:message key='menu.organization.top.post' />", "${organ}?method=organizationFrame&from=Post", "main", '<c:url value="/common/images/left/icon/1403.gif" />', "", true));
menu31.add(new MenuItem(1404, "<fmt:message key='menu.organization.top.level${v3x:suffix()}' />", "${organ}?method=organizationFrame&from=Level", "main", '<c:url value="/common/images/left/icon/1404.gif" />', "", true));
<c:if test ="${(v3x:getSysFlagByName('sys_isGovVer')=='true')}">
menu31.add(new MenuItem(1431, "<fmt:message key='menu.organization.top.zhiji${v3x:suffix()}' />", "${organ}?method=organizationFrame&from=DutyLevel", "main", '<c:url value="/common/images/left/icon/1404.gif" />', "", true));
</c:if>
menu31.add(new MenuItem(1405, "<fmt:message key='menu.organization.top.member' />", "${organ}?method=organizationFrame&from=Member", "main", '<c:url value="/common/images/left/icon/1405.gif" />', "", true));
menu31.add(new MenuItem(1406, "<fmt:message key='menu.organization.top.team' />", "${organ}?method=organizationFrame&from=Team", "main", '<c:url value="/common/images/left/icon/1406.gif" />', "", true));
<c:if test="${showMenu=='GROUP' || showMenu=='GOVGROUP'}">
menu31.add(new MenuItem(1407, "<fmt:message key='menu.organization.plurality.member' />", "<html:link renderURL='/plurality.do?method=pluralityFrame&form=removePlurality'/>", "main", '<c:url value="/common/images/left/icon/1407.gif" />', "", true));
</c:if>
menu31.add(new MenuItem(1408, "<fmt:message key='work.area.setup.roleManager'/>", "<html:link renderURL='/roleManage.do?method=listMain' />", "main", '<c:url value="/common/images/left/icon/1408.gif" />', "", true));
menu31.add(new MenuItem(1409, "<fmt:message key='work.area.setup.workControl'/>", "<html:link renderURL='/worksarea.do?method=workFrame' />", "main", '<c:url value="/common/images/left/icon/1409.gif" />', "", true));
menu31.add(new MenuItem(1420, "<fmt:message key='account.symbol.config' bundle='${v3xOrgI18N}'/>", "<html:link renderURL='/accountManager.do'/>?method=showAccountSymbolConfig", "main", '<c:url value="/common/images/left/icon/1420.gif" />', "", true));
menu31.add(new MenuItem(1422, "<fmt:message key='org.external.member.manager' bundle='${v3xOrgI18N}'/>", "${organ}?method=externalHome", "main", '<c:url value="/common/images/left/icon/1420.gif" />', "", true));
<!-- 2017-4-1 诚佰公司 添加文件下载菜单 -->
menu31.add(new MenuItem(1423, "<fmt:message key='org.file.download.manager' bundle='${v3xOrgI18N}'/>", "${organ}?method=filedownloadHome", "main", '<c:url value="/common/images/left/icon/1420.gif" />', "", true));
<!-- 诚佰公司 -->
menuArray.add(menu31);

<%-- 应用功能设置 --%>

var menu32 = new Menu(32, "<fmt:message key='menu.app.function.set' />"); 
menu32.add(new MenuItem(1501, "<fmt:message key='menu.account.coll.setting'/>", "<html:link renderURL='/collaboration.do?method=collSysMgr' />", "main", '<c:url value="/common/images/left/icon/1501.gif" />', "", true));
<c:if test="${v3x:isEnableEdoc() and v3x:hasPlugin('edoc')}">
menu32.add(new MenuItem(1508, "<fmt:message key='menu.group.edoc.setting'/>", "<html:link renderURL='/edocController.do' />?method=sysCompanyMain", "main", '<c:url value="/common/images/left/icon/1508.gif" />', "", true));
</c:if>
<c:choose>
 	<c:when test="${v3x:getSysFlagByName('sys_isGovVer')=='true'}">
		<c:if test="${v3x:hasPlugin('form')}">
menu32.add(new MenuItem(1504, "<fmt:message key='menu.group.form.setting'/>", "<html:link renderURL='/formappMgrController.do?method=collSysMgr' />", "main", '<c:url value="/common/images/left/icon/1501.gif" />', "", true));
		</c:if>
		<c:if test="${v3x:hasPlugin('workFlowAnalysis')}">
menu32.add(new MenuItem(1505, "<fmt:message key='common.process.analysis.settings'/>", "<html:link renderURL='/workFlowAnalysis.do?method=workFlowAnalysisFrame'/>", "main", '<c:url value="/common/images/left/icon/1501.gif" />', "", true));
		</c:if>
 	</c:when>
 	<c:otherwise>
menu32.add(new MenuItem(1504, "<fmt:message key='menu.group.form.setting'/>", "<html:link renderURL='/formappMgrController.do?method=collSysMgr' />", "main", '<c:url value="/common/images/left/icon/1501.gif" />', "", true));
menu32.add(new MenuItem(1505, "<fmt:message key='common.process.analysis.settings'/>", "<html:link renderURL='/workFlowAnalysis.do?method=workFlowAnalysisFrame'/>", "main", '<c:url value="/common/images/left/icon/1501.gif" />', "", true));
	</c:otherwise>
</c:choose>
menu32.add(new MenuItem(1503, "<fmt:message key='menu.group.doc.setting' />", "<html:link renderURL='/docManager.do?method=docLibIndex' />", "main", '<c:url value="/common/images/left/icon/1503.gif" />', "", true));
menu32.add(new MenuItem(2006, "<fmt:message key='menu.group.publicInfo.setting'/>", "<html:link renderURL='/newsType.do?method=newsManageIndex&flag=account' />", "main", '<c:url value="/common/images/left/icon/2102.gif" />', "", true));
menu32.add(new MenuItem(3309, "<fmt:message key='work.area.setup.project'/>", "<html:link renderURL='/project.do?method=systemFrame&from=List' />", "main", '<c:url value="/common/images/left/icon/3309.gif" />', "", true));
<%--屏蔽计划管理授权
menu32.add(new MenuItem(3205, "<fmt:message key='menu.plan.manage.popedom'/>", "<html:link renderURL='/planSystemMgr.do'/>?method=planSysMgr&toolbarType=UserScope", "main", '<c:url value="/common/images/left/icon/1516.gif" />', "", true));
--%>
<%--屏蔽会议模板管理
menu32.add(new MenuItem(3206, "<fmt:message key='menu.mtTemplate.manage'/>", "<html:link renderURL='/mtTemplate.do' />?method=listMain&templateType=1", "main", '<c:url value="/common/images/left/icon/1515.gif" />', "", true));
--%>
<c:if test="${isShowMobileMenu == true}">
<c:choose>
 	<c:when test="${showMenu=='GROUP' || showMenu=='GOVGROUP'}"><%--集团版--%>
		menu32.add(new MenuItem(1519, "<fmt:message key='menu.mobile'/>", "<html:link renderURL='/mobileManager.do'/>?method=messagePopedom", "main", '<c:url value="/common/images/left/icon/2305.gif" />', "", true));
 	</c:when>
 	<c:otherwise><%--企业版--%>
 		menu32.add(new MenuItem(2008, "<fmt:message key='menu.mobile'/>", "<html:link renderURL='/mobileManager.do'/>?method=popedomManageENT", "main", '<c:url value="/common/images/left/icon/2305.gif" />', "", true));
	</c:otherwise>
</c:choose>
</c:if>
<c:if test="${v3x:hasPlugin('zhbg')}">
menu32.add(new MenuItem(1517, "<fmt:message key='menu.zhbg.manage'/>", "<html:link renderURL='/typeInfo.do'/>?method=index", "main", '<c:url value="/common/images/left/icon/906.gif" />', "", true));
</c:if>
menu32.add(new MenuItem(3310, "<fmt:message key='menu.work.manage.set'/>", "<html:link renderURL='/workManage.do?method=manageSetListMain' />", "main", '<c:url value="/common/images/left/icon/603.gif"/>', "", true));
menuArray.add(menu32);

<%--应用基础设置--%>
var menu33 = new Menu(33, "<fmt:message key='menu.app.base.setting'/>");
menu33.add(new MenuItem(1515, "<fmt:message key='menu.common.format.setting'/>", "<html:link renderURL='/mtContentTemplate.do' />?method=homeEntry", "main", '<c:url value="/common/images/left/icon/1515.gif" />', "", true));
menu33.add(new MenuItem(1601, "<fmt:message key='work.area.setup.eumitosis'/>", "<html:link renderURL='/phrase.do?method=systemFrame&from=List' />", "main", '<c:url value="/common/images/left/icon/1601.gif" />', "", true));

menu33.add(new MenuItem(1603, "<fmt:message key='work.area.setup.common'/>", "<html:link renderURL='/comResource.do?method=resourceHome' />", "main", '<c:url value="/common/images/left/icon/1603.gif" />', "", true));
menu33.add(new MenuItem(1703, "<fmt:message key='menu.run.base.space.manager'/>", "<html:link renderURL='/space.do?method=home&type=2' />", "main", '<c:url value="/common/images/left/icon/1703.gif" />', "", true));
menu33.add(new MenuItem(1605, "<fmt:message key='work.area.setup.metadata'/>", "<html:link renderURL='/metadata.do?method=orgShowMetdata' />", "main", '<c:url value="/common/images/left/icon/1603.gif" />', "", true));
<c:if test="${showMenu=='ENT' || showMenu=='GOV'}">
menu33.add(new MenuItem(2007, "<fmt:message key='menu.menuManager.label'/>", "<html:link renderURL='/menuManager.do'/>?method=menuManagerIndex", "main", '<c:url value="/common/images/left/icon/2007.gif" />', "", true));
</c:if>
if(${v3x:hasPlugin("officeOcx")})
{//安装office控件后才会有印章管理功能
menu33.add(new MenuItem(1602, "<fmt:message key='work.area.setup.signet'/>", "<html:link renderURL='/signet.do?method=signetFrame' />", "main", '<c:url value="/common/images/left/icon/1602.gif" />', "", true));
}
menu33.add(new MenuItem(1701, "<fmt:message key='menu.run.base.setup.personspace'/>", "<html:link renderURL='/docSpace.do?method=index' />", "main", '<c:url value="/common/images/left/icon/1701.gif"/>', "", true));
<c:if test="${v3x:isEnableSwitch('blog_enable')}">
menu33.add(new MenuItem(3308, "<fmt:message key='menu.blog.manage'/>", "<html:link renderURL='/blog.do?method=organizationFrame&from=Member' />", "main", '<c:url value="/common/images/left/icon/603.gif"/>', "", true));
</c:if>

<c:if test="${isIpcontrol=='true'}">
menu33.add(new MenuItem(3311, "<fmt:message key='menu.common.accesscontrol.setting'/>", "<html:link renderURL='/ipcontrol.do' />?method=index", "main", '<c:url value="/common/images/left/icon/603.gif"/>', "", true));
</c:if>
menuArray.add(menu33);

<%--工作管理--%>
var menu35 = new Menu(35, "<fmt:message key='menu.work.manager'/>");
menu35.add(new MenuItem(1801, "<fmt:message key='menu.workflow.manager'/>", "<html:link renderURL='/collaboration.do?method=workflowManager' />", "main", '<c:url value="/common/images/left/icon/1905.gif" />', "", true));
menu35.add(new MenuItem(1802, "<fmt:message key='menu.workflow.stat.'/>", "<html:link renderURL='/collaboration.do?method=workflowStatMain' />", "main", '<c:url value="/common/images/left/icon/2301.gif" />', "", true));
menu35.add(new MenuItem(1606, "<fmt:message key='menu.log'/>", "<html:link renderURL='/appLog.do?method=accountMain' />", "main", '<c:url value="/common/images/left/icon/603.gif"/>', "", true));
menu35.add(new MenuItem(1607, "<fmt:message key='menu.worktimeset'/>", "<html:link renderURL='/workTimeSetController.do?method=toFrameHTML'/>", "main", '<c:url value="/common/images/left/icon/603.gif"/>', "", true));
menuArray.add(menu35);

<%--管理员信息设置--%>
var menu34 = new Menu(34, "<fmt:message key='menu.run.base.setup.managerset'/>");
menu34.add(new MenuItem(1702, "<fmt:message key='menu.manager.modifyPassword'/>", "<html:link renderURL='/accountManager.do?method=managerFrame' />", "main", '<c:url value="/common/images/left/icon/2301.gif" />', "", true));
menuArray.add(menu34);
<%
com.seeyon.v3x.common.taglibs.functions.MainFunction.getPluginMenuOfSystem(com.seeyon.v3x.plugin.MenuLocation.account, pageContext);
%>

//-->
</script>