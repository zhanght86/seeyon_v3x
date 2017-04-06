<%@page import="com.seeyon.v3x.common.flag.SysFlag"%>
<%@page import="com.seeyon.v3x.common.taglibs.functions.Functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.seeyon.v3x.common.web.login.CurrentUser"%>
<%@page import="com.seeyon.v3x.common.authenticate.domain.User"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<html>
<head>
${v3x:getXUA()}
<%
boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
request.setAttribute("isGov", isGov);
%>
<script type="text/javascript">
	var isGov = ${isGov};
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, width=device-width">
<%@ include file="INC/noCache.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/main" prefix="main"%>
<fmt:setBundle basename="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources" var="v3xCommonI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.system.resources.i18n.SysMgrResources" var="v3xSysI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.peoplerelate.resources.i18n.RelateResources" var="relateResourcesI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.main.resources.i18n.MainResources"/>

<title><%=com.seeyon.v3x.common.taglibs.functions.Functions.getPageTitle()%></title>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/default.css${v3x:resSuffix()}" />">
${v3x:skin()}
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/v3xmain/css/message.css${v3x:resSuffix()}" />" />
<link href="<c:url value="/common/skin/default${v3x:getSysFlagByName('SkinSuffix')}/images/favicon.ico" />" type="image/x-icon" rel="icon"/>
<link href="<c:url value="/common/skin/default${v3x:getSysFlagByName('SkinSuffix')}/images/favicon.ico" />" type="image/x-icon" rel="shortcut icon"/>
<c:set value="${v3x:currentUser().userSSOFrom}" var="topFrameName" />


<c:set value="${topFrameName==null || topFrameName==''? 110 : 65}" var="menuMargin"/>
<c:set value="${topFrameName==null || topFrameName==''? 76 : 30}" var="spaceMargin"/>
<style>
#MenuItemsDIVShim{
	z-index:99;
	position:absolute;
	top:${menuMargin}px;
	background-color:#fff;
}
#DrawdownMenuItemsDIV{
	z-index:100;
	position:absolute;
	top:${menuMargin}px;
	background-color:#fff;
}
#MoreSpaceTagDIV{
	z-index:102;
	position:absolute;
	overflow:hidden;
	top:${spaceMargin}px;
	background-color:#fff;
	padding-right: 3px;
	border: solid 1px #d1d1d1;
}
#onlineStateDiv{
	z-index:106;
	position:absolute;
	top:${menuMargin}px;
	left:92px;
	width:125px;
	height:88px;
	background-color:#fff;
	border: solid 1px #b6b6b6;
}
.spaceSettinMenuDiv{
	text-align: center;
	height: 20px;
	margin-top: 2px;
	cursor: pointer;
	border: 1px gray solid;
}
#moreMenuDiv{
	z-index:204;
	position:absolute;
	top:${menuMargin}px;
	background-color:#fff;
	border: solid 1px #d1d1d1;
	width:130px;
}
#moreMenuSubDiv{
	z-index:211;
	position:absolute;
	top:${menuMargin}px;
	background-color:#fff;
	border: solid 1px #b6b6b6;
	width:130px;
}
</style>
<script type="text/javascript">
<!--
function agentAlert(){ //必须放在iframe前面，应用Iframe要调用
	<% 
	String agentInfo = Functions.agentSettingAlert();
	if (agentInfo != null && !"".equals(agentInfo)) {
		String info[] = agentInfo.split("::");
		pageContext.setAttribute("message", info[0]);
		pageContext.setAttribute("ids", info[1]);
	}
	%>
	<c:if test="${not empty message and not empty ids }">
	v3x.openWindow({
		url		: '/seeyon/agent.do?method=agentAlert&ids=${ids}&message=${v3x:urlEncoder(message)}',
		width	: 380,
		height	: 200,
		resizable	: 'yes'
	});
	</c:if>
}

window.onload = function(){
	showProcDiv();
	
	//判断IM是否在线
	var hasToken = getA8Top().contentFrame.topFrame.doGetToken();
	if(hasToken == "true"){
		//如果IM在线,先停止token
		getA8Top().contentFrame.topFrame.doStopToken();
	}
}
window.onbeforeunload = function(){
	if(${v3x:getBrowserFlagByUser("CloseWindowLogout", v3x:currentUser())}){
		if(isOpenCloseWindow){
			isOpenCloseWindow = false;

			//关闭聊天窗口
			if(getA8Top().contentFrame.topFrame.onlineWin){
				getA8Top().contentFrame.topFrame.onlineWin.close();
			}

			//判断IM是否在线
			var hasToken = getA8Top().contentFrame.topFrame.doGetToken();
			if(hasToken == "true"){
				//如果IM在线,先停止token
				getA8Top().contentFrame.topFrame.doStopToken();
			}
			
			v3x.openWindow({
				url : "login/logout?close=true",
				height : 10,
				width : 10,
				top : 1,
				left : 1
			});
		}
	}
}
//-->
</script>
<div id="SubDoubleMenuDiv" style="position:absolute; display:none;background: #ffffff"></div>
<div id="SubDrawdownMenuItemsDIV" style="position:absolute; display:none;background: #ffffff"></div>
<div id="DrawdownMenuItemsDIV" style="position:absolute; display:none"></div>
<div id="MoreSpaceTagDIV" style="display:none;" onmouseover="getA8Top().contentFrame.topFrame.showMorePanel(true)" onmouseout="getA8Top().contentFrame.topFrame.showMorePanel(false)"></div>
<div id="onlineStateDiv" style="display:none;"  onmouseover="getA8Top().contentFrame.topFrame.showOnlineVisbale(true)" onmouseout="getA8Top().contentFrame.topFrame.showOnlineVisbale(false)"></div>
<div id="moreMenuDiv" style="display:none;overflow: hidden;" onmouseover="getA8Top().contentFrame.topFrame.setMoreMenuVisible()" onmouseout="getA8Top().contentFrame.topFrame.setMoreMenuUnVisible()"></div>
<div id="moreMenuSubDiv" style="display:none;overflow: hidden;" onmouseover="getA8Top().contentFrame.topFrame.setSubMoreMenuVisible(true)" onmouseout="getA8Top().contentFrame.topFrame.setSubMoreMenuVisible(false)"></div>
</head>
<body scroll="no" style="overflow: hidden">
<iframe src="<c:url value='/main.do?method=main&fromPortal=${topFrameName!=null}' />" id="contentFrame" name="contentFrame" frameborder="0" height="100%" width="100%" scrolling="no" marginheight="0" marginwidth="0"></iframe>

<%-- 菜单设置|空间设置 --%>
<div id="systemSetting" style="position: absolute;display: none;top:110px;right:3px;width:54px;background-color: white;">
	<div onclick="javascript:contentFrame.mainFrame.location.href='/seeyon/main.do?method=menuSetting';document.getElementById('systemSetting').style.display='none';" class="spaceSettinMenuDiv"><fmt:message key='personalSetting.menu.label'/></div>
	<div id="pageSetting" onclick="javascript:editCurrentSpace();" class="spaceSettinMenuDiv"><fmt:message key='menu.space.personalConfig' /></div>
</div>
<div id="procDiv1" style="display:none;"></div>
<iframe id="procDiv1Iframe" scrolling="no" frameborder="0"  style="display:none;"></iframe>
<div id="messageDiv" style="display:none;"></div>
<iframe name="downloadFileFrame" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
<iframe id="SubDoubleMenuItemsDivShim" scrolling="no" frameborder="0" style="position:absolute; display:none;background: #ffffff"></iframe>
<iframe id="SubMenuItemsDIVShim" scrolling="no" frameborder="0" style="position:absolute; display:none;background: #ffffff"></iframe>
<iframe id="MenuItemsDIVShim" scrolling="no" frameborder="0" style="position:absolute;display:none; z-index:99;"></iframe>
<iframe id="MoreSpaceTagDIVShim" scrolling="no" frameborder="0" style="position:absolute; top:${spaceMargin}px; left:0px; display:none; z-index:101;"></iframe>
<iframe id="onlineState" scrolling="no" frameborder="0" style="position:absolute; top:${menuMargin}px; left:92px; display:none; z-index:105;width:127px;height:90px;"></iframe>
<iframe id="moreMenu" scrolling="no" frameborder="0" style="position:absolute; top:${menuMargin}px; left:0; display:none; z-index:203;width:130px;"></iframe>
<iframe id="moreMenuSub" scrolling="no" frameborder="0" style="position:absolute; top:${menuMargin}px; left:0; display:none; z-index:210;width:130px;"></iframe>

<%-- 右下角A8消息弹出窗口start --%>
<iframe id="DivShim4MsgWindow" scrolling="no" frameborder="0" style="position:absolute; right:0px; bottom:0px; display:none; z-index:103;"></iframe>

<div id="msgWindowDIV" style="width:280px; position:absolute; right:0px; bottom:0px; display:none; z-index:104; background-color:#FFF">
<!--[if IE 6]><iframe style="z-index: -1; position: absolute; filter: alpha(opacity=0); width:280px; height: 100%; top: 0px; left: 0px;"> </iframe><![endif]--> 
	<table id="helperTable" width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height='32' class='title-background-1${v3x:ncSuffixInJs()}'>
				<table width='100%' border='0' cellpadding='0' cellspacing='0'>
					<tr>
						<td width='84%' class='msgTitle'></td>
						<td width='8%' title='<fmt:message key="message.header.mini.alt"/>' onclick='getA8Top().contentFrame.topFrame.changeMessageWindow("a8")' class='td-background-min cursor-hand'>&nbsp;</td>
						<td width='8%' title='<fmt:message key="message.header.close.alt"/>' onclick='getA8Top().contentFrame.topFrame.destroyMessageWindow("true", "a8")' class='cursor-hand'>&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr id="PerMsgContainerTR" style="display: none;"><td id="PerMsgContainer" align="right"></td></tr>
		<tr id="SysMsgContainerTR" style="display: none;"><td id="SysMsgContainer" align="right"></td></tr>
		<c:choose>
			<c:when test="${!v3x:currentUser().admin}">
				<tr align='right' valign="middle">
					<td class='bottom-background-1' style="padding: 0 5px;">
						<span class="cursor-hand like-a div-float" onclick="showMessageSet('/seeyon/message.do?method=showMessageSettingModel')"><fmt:message key="message.header.more.set"/></span>						
						<%-- 性能优化:去掉右下角系统消息的未读数量 --%>
						<%--
						<span id='notReadSysCountSpan' style='display:none; color: #154dbd;' class='cursor-hand' onclick='getA8Top().contentFrame.topFrame.showMoreMessage("/seeyon/main.do?method=showMessages&showType=0&readType=notRead")'></span>&nbsp;&nbsp;
						--%>
						<span class='cursor-hand like-a div-float-right' onclick='getA8Top().contentFrame.topFrame.showMoreMessage("/seeyon/main.do?method=showMessages&showType=0")'>[<fmt:message key="message.header.more.alt"/>]</span>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr align='center' valign='bottom'><td class='bottom-background-2'>&nbsp;</td></tr>
			</c:otherwise>
		</c:choose>
	</table>
</div>

<div id="msgWindowMaxDIV" style="position:absolute; right:1px; bottom:1px; display:none; z-index:105;" class='td-background-sys_max${v3x:ncSuffixInJs()} cursor-hand' onclick='getA8Top().contentFrame.topFrame.changeMessageWindow("a8")'></div>
<%-- 右下角A8消息弹出窗口end --%>

<iframe id="playSoundHelper" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
</body>
</html>

<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/V3X.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript">
<!--
var v3x = new V3X();
v3x.init("${pageContext.request.contextPath}", "${v3x:getLanguage(pageContext.request)}");
_ = v3x.getMessage;

v3x.loadLanguage("/apps_res/v3xmain/js/i18n");
var genericControllerURL = "${genericController}";
//-->
</script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/SelectPeople/js/orgDataCenter.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/v3xmain/js/sectionMappingLinkType.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/v3xmain/js/seeyon.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" />"></script>

<script type="text/javascript">
<!--
var isNcPortal="${topFrameName}";
var ncSuffix = "${v3x:ncSuffixInJs()}";
var suffixVersion = "${v3x:suffixInJS()}";

var xmlDoc = null;
function getDom(){
	if(xmlDoc == null){
		try{
			xmlDoc = new ActiveXObject( "SeeyonFileDownloadLib.SeeyonFileDownload");
			xmlDoc.AddUserParam("${v3x:currentUser().locale.language}" + "_" + "${v3x:currentUser().locale.country}", "${v3x:currentUser().loginName}", "<%=session.getId()%>", "${v3x:currentUser().id}");
		}catch(ex1){
			//alert("Exception : " + ex1.message+"，批量下载控件加载错误");
		}
	}
	return xmlDoc;
}
<%if(!CurrentUser.get().isAdmin()){%>
getDom();
<%}%>
//-->
</script>
<script type="text/javascript">
<!--
var initMenuTop = '${menuMargin}';
var A8PageTop = true;
var loadMenuId = "${param.menuId}";
var Constants_Component = new Properties();
Constants_Component.put(Constants_Account, "<fmt:message key='org.account.label'/>");
Constants_Component.put(Constants_Department, "<fmt:message key='org.department.label'/>");
Constants_Component.put(Constants_Team, "<fmt:message key='org.team.label'/>");
Constants_Component.put(Constants_Post, "<fmt:message key='org.post.label'/>");
Constants_Component.put(Constants_Level, "<fmt:message key='org.level.label${v3x:suffix()}'/>");
Constants_Component.put(Constants_Member, "<fmt:message key='org.member.label'/>");
Constants_Component.put(Constants_Role, "<fmt:message key='org.role.label'/>");
Constants_Component.put(Constants_Outworker, "<fmt:message key='org.outworker.label'/>");
Constants_Component.put(Constants_ExchangeAccount, "<fmt:message key='org.exchangeAccount.label'/>");
Constants_Component.put(Constants_OrgTeam, "<fmt:message key='org.orgTeam.label'/>");
Constants_Component.put(Constants_RelatePeople, "<fmt:message key='org.RelatePeople.label'/>");
Constants_Component.put(Constants_FormField, "<fmt:message key='form.selectPeople.extend'/>");
Constants_Component.put(Constants_Admin, "<fmt:message key='org.admin.label'/>");

var PeopleRelate_TypeName = {
	1 : "<fmt:message key='relate.type.leader' bundle='${relateResourcesI18N}' />",
	2 : "<fmt:message key='relate.type.assistant' bundle='${relateResourcesI18N}' />",
	3 : "<fmt:message key='relate.type.junior' bundle='${relateResourcesI18N}' />",
	4 : "<fmt:message key='relate.type.confrere' bundle='${relateResourcesI18N}' />"
}

<c:forEach items="${main:getAllRoleNames(pageContext)}" var="roleName">
	Constants_Component.put("${roleName.key}", "${roleName.value}");				
</c:forEach>

//性能优化:去掉右下角系统消息的未读数量
//var notReadSystemMessageCount;
//-->
</SCRIPT>
