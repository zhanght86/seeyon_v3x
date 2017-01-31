<%@page import="com.seeyon.v3x.common.flag.SysFlag"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.seeyon.v3x.common.web.login.CurrentUser"%>
<html>
<head>
<%@ include file="INC/noCache.jsp" %>
<%@ include file="header.jsp" %>
<%@ taglib uri="http://v3x.seeyon.com/taglib/main" prefix="main" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<html:link renderURL="/message.do?method=showOnlineUser" var="onlineUserURL" />
<html:link renderURL="/publicManager.do" var="urlPublicManager" />
<html:link renderURL='/organization.do' var="organ" />
<c:set var="currentUser" value="${v3x:currentUser()}"></c:set>
<c:set value="${v3x:concurrentAccount()}" var="concurrentAccount" />
<c:set value="${param.fromPortal != 'true'}" var="fromA8"/>
<c:if test="${fromA8}">
<style>
<%-- 用户Banner背景样式 --%>
 	.topBanner{
 		<c:if test="${accountSymbol.bannerImagePath != null}">
 		background: url(<c:url value='${accountSymbol.bannerImagePath}'/>${v3x:resSuffix()}) ${accountSymbol.tileBanner? 'repeat-x' : 'no-repeat'};
 		</c:if>
	}
</style>
</c:if>	
<%
	String sessionId = session.getId();
	Cookie[] cookie = request.getCookies();
	for(int i = 0; i < cookie.length; i++)
	{
		if(cookie[i].getName().trim().equalsIgnoreCase("JSESSIONID"))
		{
			sessionId = cookie[i].getValue().trim();
			break;
		}
	}	
%>

</head>
<BODY 
	onunload="endA8genius()"
	oncontextmenu="self.event.returnValue=false" onselect="return false"
	onselectstart="selectStart(event)" ondrag="return false"
	scroll="no" style="overflow: hidden" class="bg_banner" onLoad="addResize()">
	<div id="topBody" style="display:none;"></div>
	<c:set value="${fromA8 && (accountSymbol.bannerImagePath eq '/common/images/space.gif')? 'topBg ':'topBg'}" var="theClass"/>

<table width="100%" height="${fromA8? 117:70}" border="0" cellpadding="0" cellspacing="0" class="${theClass}">
  <tr>
  	<td valign="top" width="100%" height="${fromA8? 76:30}" class="bg_banner_right">
		<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" id="topBannerTable" class="topBanner">
		  <tr>
			<c:if test="${fromA8}">
		    <td width="5%" style="padding-left: 18px; display:${accountSymbol.hiddenLogo?'none':''}" id="LogoImgTD" valign="top"><div id="LogoImgDIV"><img src="<c:url value='${accountSymbol.logoImagePath}'/>${v3x:resSuffix()}" height="60" border="0" /></div></td>
			<td width="95%">
			    <div class="companyLabel" id="accountNameDiv">${empty accountName ? v3x:_(pageContext, 'common.page.title') : accountName}</div>
			    <div class="companyLabel2" id="accountSecondNameDiv">${secondAccountName}</div>
			</td>
			</c:if>
			<td id="bannerSpaceTD" width="${fromA8? '440':'100%'}" nowrap="nowrap" align="right" valign="top">
				<table id="bannerRightTable" border="0" width="100%" height="${fromA8? 76:30}"  cellspacing="0" cellpadding="0">
					<tr>
			     		<td nowrap="nowrap" valign="top" align="right">
				     		<table border="0" cellpadding="0" cellspacing="0" style="margin: ${fromA8? 10:5}px 10px 0 0;">
				     			<tr>
				     			<c:if test="${!fromA8}">
				        			<td nowrap="nowrap" align="right" class="topTDPadding">
										
						            </td>
						    	</c:if>
							        <td nowrap="nowrap" align="right" class="topTDPadding">
								        <c:if test="${v3x:getSysFlagByName('frontPage_showMultiWorkAccount') && !currentUser.administrator && !currentUser.systemAdmin && fn:length(concurrentAccount) > 1}">
										  	<font color="white"><fmt:message key="top.switchAccount.label"/>:</font>&nbsp;
										  	<select name="accountSelector" id="accountSelector" onChange="chanageLoginAccount(this.value)" style="width: 100px; margin: -1px;"></select>
								         </c:if>
								         &nbsp;
									</td>
									<c:if test="${userType == 'user'}">
										<td width="25"><div id="download-td" style="display: none;" class="top-download" title="<fmt:message key='seeyon.top.download.alt'/>" onClick="showDownloadFile()"></div></td>
									</c:if>
							        <td width="25"><div class="top-reload" title='<fmt:message key="seeyon.top.reload.alt"/>' onClick="refreshWorkspace();"></div></td>
							        <c:choose>
							        	<c:when test="${userType == 'user'}">
									        <c:if test="${fromA8}">
									        <%-- branches_a8_v350_r_gov GOV-2900 任会阳 修改 首页，logo换成政务logo，右上角文字提示改成'关于G6' --%>
									        <td width="25"><div id="top-a8" class="top-A8" title='<fmt:message key="menu.tools.about${v3x:suffix()}${v3x:oemSuffix()}"/>' onClick="startA8genius();"></div></td>			        	
									        <td width="25"><div   class="top-exit" title='<fmt:message key="seeyon.top.close.alt"/>' onClick="logout()"></div></td>
							        		</c:if>
							        	</c:when>
							        	<c:otherwise>
									        <td width="25"><div   class="top-home" title='<fmt:message key="seeyon.top.home.alt"/>' onClick="backToHome()"></div></td>			        			        	
									        <c:if test="${fromA8}">
									        <td width="25"><div   class="top-exit" title='<fmt:message key="seeyon.top.close.alt"/>' onClick="logout()"></div></td>
									        </c:if>
							        	</c:otherwise>
							        </c:choose>
								    <c:if test="${!fromA8}">
					        			<td  nowrap="nowrap" valign="bottom">
									        <div id="trPanel"></div>
					        			</td>
								    </c:if>
				     			</tr>
				     		</table>	
			     		</td>
			      	 </tr>
			      	 
			      	 <c:if test="${fromA8}">
			      	<tr valign="bottom">
				        <td align="right" nowrap="nowrap" valign="bottom" style="padding-right: 10px;">
					        <div id="trPanel"></div>
			            </td>
			    	</tr>
			    	</c:if>
			    </table>
		    </td>
		  </tr>
		</table>
    </td>
  </tr>
  <tr>
  	<td height="${fromA8? 40:40}" width="100%" class="${fromA8? 'bg_menu':'bg_menu_nc'}" valign="top">
  		<table border="0" cellpadding="0" cellspacing="0"  width="100%"  class="${fromA8? 'bg_menu_left':'bg_menu_left_nc'}">
  			<tr>
  				<td width="142">
  					<table border="0" cellpadding="0" cellspacing="0"  width="100%"  class="online_bg">
  						<tr>
  							<c:if test="${isCanSendSMS}">
  							<td align="center">
								<div class="login_mobile" onClick="sendSMS()" title="<fmt:message key="top.alt.sendMobileMsg"/>"></div>
  							</td>
  							</c:if>
							<c:if test="${userType == 'user' && v3x:hasPlugin('rtx')}">
  							<td align="center" class="cursor-hand" title="<fmt:message key="top.alt.transfer.rtx" bundle="${v3xRTXI18N}"/>" onclick="ssoRTX(1)">
								<div class="login_rtx"></div>
  							</td>
  							</c:if>
  							<td  class="cursor-hand" title="<fmt:message key="top.alt.sendOnlineMsg"/>" onclick="onlineMember()">
  								<table border="0" cellpadding="0" cellspacing="0"  width="100%">
  									<tr>
  										<td>
  											<div class="online_num"></div>
  										</td>
  										<td>
	  										<fmt:message key="seeyon.top.onlineNumber.label">
												<fmt:param value="${onlineNumber}" />
											</fmt:message>
  										</td>
  									</tr>
  								</table>
  							</td>
  							<c:choose>
	  							<c:when test="${currentUser.admin}">
	  								<td align="center" class="cursor-hand">&nbsp;</td>
	  							</c:when>
	  							<c:otherwise>
	  								<td align="center" class="cursor-hand" onclick="showOnlineState(true)">
										<div class="online_state_0" id="onlline_state_div"></div>
  									</td>
	  							</c:otherwise>
  							</c:choose>
  						</tr>
  					</table>	
  				</td>
  				<td>
			 		<table style="margin-left: 0px;margin-right: 5px;" width="100%"  border="0" cellpadding="0" cellspacing="0" class="bg_menu_main">
				  	   <tr>
							<td id="mainMenuBarDiv">
								
							</td>
							<td width="300" align="right">
								<c:if test="${v3x:hasPlugin('luceneIndex') && userType == 'user'}">
									<table cellpadding="0" cellspacing="0" border="0" >
										<tr>
											<td nowrap="nowrap" height="25">
												<INPUT onBlur="setTip()" onKeyDown="doKeyPressedEvent()" id="keyword" class="advance_input" onFocus="clearTip()" name="keyword" maxLength="40" value="" type="text"/> 
												<INPUT id="b1" class="advance_btn" onClick="search()"  type="button">
											</td>
											<td width="47" align="center">
												<INPUT id="b2" class="advance_higher"  onclick="advanced()" value="<fmt:message key="advance.search.label"/>" type="button"/>
											</td>
										</tr>
									</table>

									<SCRIPT type=text/javascript>
										<!--
										String.prototype.trim=function(){   
											return this.replace(/(^(\s|\*|#)*)|((\s|#)*$)/g, "");
										}
										function doKeyPressedEvent(){
											if(event.keyCode==13){
												search();
										   }
										}
										function IsNull(value){    
											if(value.length==0){    
												return true;
											}
											if(value == _("V3XLang.index_input_keyword")){
												return true;
											}
											return false;
										}
										function search(){
										    var keyword = document.getElementById("keyword").value;
										    keyword = keyword.trim();
											if(IsNull(keyword)){
												alert(_("V3XLang.index_input_error"));
												return;
											}
											// kuanghs 限制最大长度
											var max_length = 40;
											if(keyword.length>max_length){
												keyword=keyword.substring(0,max_length);
											}
											getA8Top().showContentPage();
											getA8Top().contentFrame.mainFrame.document.location.href = '/seeyon/indexInterface.do?method=search&keyword=' + encodeURIComponent(keyword);
										}
										function advanced(){
											getA8Top().showContentPage();
											getA8Top().contentFrame.mainFrame.document.location.href = '/seeyon/indexInterface.do?method=goToAdvancePage';
										}
										
										function clearTip(){
											var keyword = document.getElementById("keyword");
											if(keyword.value == _("V3XLang.index_input_keyword")){
									        	keyword.value = "";
									        	keyword.style.color="#000000";
									    	}
										}
										function setTip(){
									        var keyword = document.getElementById("keyword");  
											if(IsNull(keyword.value)){
									           keyword.value = _("V3XLang.index_input_keyword");
									           keyword.style.color="#999999";
									       }
										}
										
										document.getElementById("keyword").value = _("V3XLang.index_input_keyword");
										//document.getElementById("b1").value = _("V3XLang.index_search");
										//document.getElementById("b2").value = _("V3XLang.index_search_heigh");
										//-->
										</SCRIPT>
									</c:if>
									<c:if test="${userType != 'user' && userType != 'secret'}">
										<div style="float: right; margin-right: 15px;" class="bottomIconBg" title="<fmt:message key='menu.tools.help'/>" onclick="showHelp()">
											<span class="icon_com syshelp_com" style="float: right;"></span>
										</div>
									</c:if>
							</td>
				       </tr>
				    </table>
  				</td>
  			</tr>
  		</table>
    </td>
  </tr>
</table>
<script type="text/javascript">
<!--
//在线IM
var onlineWin;

//存储消息
var msgProperties = new Properties();
//总共消息个数
var msgTotalCount = 0;

isNeedCheckLevelScope_mobileMessage = false;
hiddenSaveAsTeam_mobileMessage = true;

var currentUserId = "${currentUser.id}";
var currentAccountId = "${currentUser.loginAccount}";
var isAdministrator = "${currentUser.administrator}";
var isGroupAdmin = "${currentUser.groupAdmin}";
var isSystemAdmin = "${currentUser.systemAdmin}";

var message_header_system_label = "<fmt:message key="message.header.system.label"/>";
var message_header_person_label = "<fmt:message key="messageManager.count.person"/>";
var message_person_reply_label = "<fmt:message key="message.person.reply.label"/>";
var message_header_unit_label = "<fmt:message key="message.header.unit.label"/>";
var message_header_close_alt = "<fmt:message key="message.header.close.alt"/>";
var message_header_max_alt = "<fmt:message key="message.header.max.alt"/>";
var message_header_mini_alt = "<fmt:message key="message.header.mini.alt"/>";
var message_header_more_alt = "<fmt:message key="message.header.more.alt"/>";
var message_click_copy_title = "<fmt:message key="message.header.click.copy.title"/>";
var messageLinkConstants = new Properties();
var urlPublicManager = "${urlPublicManager}";
var organizationURL = "${organ}";
var onlineUserURL = "${onlineUserURL}";
var showAboutURL = "<html:link renderURL='/main.do?method=showAbout'/>";
var isCanSendSMS = "${isCanSendSMS}";
var sendSMSURL = "<html:link renderURL='/message.do'/>?method=showSendSMSDlg";
var mainURL = "<html:link renderURL='/main.do'/>";
var calEventURL =  "<html:link renderURL='/calEvent.do'/>";
var changeAccountMenuURL =  "${genericController}?ViewPage=common/menu/menu";
var hasLuceneIndex = "${v3x:hasPlugin('luceneIndex')}";
var ncSuffix = "${v3x:ncSuffixInJs()}";
var Suffix = "${v3x:sysSuffix()}";
var suffixVersion = "${v3x:suffixInJS()}";
var userType = "${userType}";
var helpURL = "<c:url value='/help/${v3x:sysSuffix()}_${userType}.html' />";
<%-- 链接 --%>
${main:showMessageLink(pageContext)}
<%-- 随OA启动精灵 --%>
var isNeedEndA8genius = true;

//延迟1秒加载精灵控件
var ufa = null;
var unloadA8genius = false;
var isGeniusReady = 0;
setTimeout(initGenius,1000);
function initGenius(){
	try{
		// 加载精灵控件前已经登出，停止加载控件
		if(unloadA8genius) return;
	    if(ufa==null || typeof(ufa)=='undefined'){
	        ufa = new ActiveXObject("UFIDA_IE_Addin.Assistance");
	        var topA8 = document.getElementById('top-a8');
	        if(topA8){
	        	topA8.setAttribute("title",_("V3XLang.genius"));
	        }
	        var isAdmin = '${currentUser.admin}';
	        if(isAdmin == 'false' || !isAdmin){
	             ufa.StartupAssistance('<%=request.getScheme()%>://<%=request.getServerName()%>', '<%=request.getServerPort()%>', '${v3x:escapeJavascript(currentUser.loginName)}', '${v3x:escapeJavascript(currentUser.name)}', '<%=sessionId%>');       
	        }
	    }
	}catch(e){}
	isGeniusReady = 1;
	showDowloadPicture("quartz");
}

/**
 * 批量下载图标控制
 */
var flag = true;
function showDowloadPicture(type){
	if(flag){
		var downloadDiv = document.getElementById("download-td");
		if(downloadDiv){
			if("doc" == type){
				downloadDiv.style.display = "";
				flag = false;
			}else{
				if(getA8Top().xmlDoc){
					var result = getA8Top().xmlDoc.GetDownloadState("${v3x:currentUser().id}");
					if(result == "FD_STATE_DOWNLOADING"){
						downloadDiv.style.display = "";
						flag = false;
					}
				}
			}
		}
	}
}

/**
 * 批量下载窗口
 */
function showDownloadFile(){
	try{
		getA8Top().xmlDoc.ShowWindow("${v3x:currentUser().id}");
	}catch(e){
		alert(_("V3XLang.batch_download_control_error"));
	}
}

function logout(){
	unloadA8genius = true;
	if(window.confirm(_("MainLang.system_logout_confirm"))){
		getA8Top().logout();
		endA8genius();
	}
}

function endA8genius(){
    if(isNeedEndA8genius){
        try{
			if(ufa==null || typeof(ufa)=='undefined'){
				ufa = new ActiveXObject("UFIDA_IE_Addin.Assistance");        	
			}
            ufa.LogoutUser("<%=sessionId%>");
        }catch(e){
        }        
    }else{
		if(!checkGeniusVersion()) return;
        ufa.DisplayAssistanceWindow("<%=sessionId%>");
    }
}
function checkGeniusVersion(){
  try{
  	var geniusVersion = ufa.getGeniusVersion();
  	if(geniusVersion == null || !geniusVersion.startsWith("2.0")){
  		alert("你的精灵和A8服务器版本不兼容，请重新下载安装");
  		return false;
  	}
  }
  catch(e){
  	alert("你的精灵和A8服务器版本不兼容，请重新下载安装");
  	return false;
  }	
  return true;
}
function startA8genius(){
	if(isGeniusReady<1) return;
	try{
		if(ufa==null || typeof(ufa)=='undefined'){
			showAbout();
		}
		else
		{
			if(!checkGeniusVersion()) return;
			if(onlineWin){
				if(window.confirm(_("MainLang.genius_switch_confirm"))){
					//关闭聊天窗口
					onlineWin.close();
					
					//判断IM是否在线
					var hasToken = doGetToken();
					if(hasToken == "true"){
						//如果IM在线,先停止token
						doStopToken();
					}
					
					getA8Top().logout(true);
					isNeedEndA8genius = false;
				}
			}else{
				getA8Top().logout(true);
				isNeedEndA8genius = false;
			}
		}
	}catch(e){
		showAbout();
	}
}
var isShowAccountSymbol = ${fromA8};

function checkPwdIsExpired(){
	//提示密码过期
	if(${!empty pwdExpirationInfo} && !v3x.isIpad){
		<c:choose>
			<c:when test="${empty pwdExpirationInfo[1] && v3x:currentUser().admin}"> <!-- 管理员首次登录，必须修改密码 -->
				var result = true;
				alert(_("V3XLang.message_pwd_expired1"));
			</c:when>
			<c:when test="${empty pwdExpirationInfo[1]}">
				var result = confirm(_("V3XLang.message_pwd_expired1"));
			</c:when>
			<c:otherwise>
				var result = confirm(_("V3XLang.message_pwd_expired", '<fmt:formatDate value="${pwdExpirationInfo[1]}" pattern="${datePattern}"/>'));
			</c:otherwise>
		</c:choose>
		if(result){
			if(${currentUser.systemAdmin}){
			//系统管理员
				parent.mainFrame.location = "${managerController}?method=managerFrame&result=" + result;
			}else if(${currentUser.auditAdmin}){
			//审计管理员	
				parent.mainFrame.location = "${managerController}?method=managerFrame&from=audit&result=" + result;
			}else if(${currentUser.secretAdmin}){
				//安全管理员	
				parent.mainFrame.location = "${managerController}?method=managerFrame&from=secret&result=" + result;
			}else if(${currentUser.groupAdmin}){
			//集团管理员
				parent.mainFrame.location = "${accountManagerController}?method=groupManagerFrame&result=" + result;
			}else if(${currentUser.administrator}){
			//单位管理员
				parent.mainFrame.location = "${accountManagerController}?method=managerFrame&result=" + result;
			}else {
				parent.mainFrame.location = "${individualController}?method=managerFrame";
			}
		}
	}
}

//-->
</script>
<script language="JavaScript" type="text/JavaScript" src="<c:url value="/common/js/message/BaseMessage.js${v3x:resSuffix()}" />"></script>
<script language="JavaScript" type="text/JavaScript" src="<c:url value="/common/js/message/Message.js${v3x:resSuffix()}" />"></script>
<script language="JavaScript" type="text/JavaScript" src="<c:url value="/common/js/message/onlinemessage.js${v3x:resSuffix()}" />"></script>
<script language="JavaScript" type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/v3xmain/js/top.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/left/Menu.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript"><!--

var rootAccountGroupShortname = "${v3x:escapeJavascript(rootAccount.shortname)}";
var allAccounts = new Properties();
//${v3x:getLimitLengthString(account.shortname, 8, '')}
<c:forEach items="${concurrentAccount}" var="account">
	allAccounts.put('${account.id}', new Account('${account.id}', "${v3x:escapeJavascript(account.name)}", "${v3x:escapeJavascript(v3x:getLimitLengthString(account.shortname, 12, ''))}", "${v3x:escapeJavascript(account.secondName)}", ${account.isRoot}, "${v3x:escapeJavascript(account.groupShortname)}"));
</c:forEach>

<%-- 最多显示的空间页签数目 --%>
var maxSpaceTagNum = <c:out value="${v3x:getSystemProperty('frontpage.maxSpaceTag.number')}" default="4" />;
if(maxSpaceTagNum > 4){
	maxSpaceTagNum = v3x.isWidescreen() ? maxSpaceTagNum : 4;
	<c:if test="${fromA8}">
	$("#bannerSpaceTD").width((95 * maxSpaceTagNum) + 60);
	</c:if>
}
var currentDepSpaceURL = null;
var departmentSpaces = new ArrayList();
var managerDepartments = new ArrayList();
managerDepartments.addAll("${v3x:joinDirectWithSpecialSeparator(managerDepartments, ',')}".split(","));

var menuArray = new ArrayList();
var spaceSetting = true;

function swithSpaceMenu(id){
	if(id==null||id==''){
		if(currentSpaceId){
			id = currentSpaceId;
		}else{
			id = 'space_0';
		}
	}
	var panel = Constants_Panels.get(id);
	var link = panel.link;
	var type = panel.type;
	if(link.indexOf('psml') != -1){
		link = link.substring(0, link.indexOf('psml') + 4);
	}
	reloadMenu(link, type);
}

function initDepartmentSpaceEnter(){
	<%-- 初始进入加载第一个 --%>
	if(currentDepSpaceURL == null){
		for(var i = 0; i < departmentSpaces.size(); i++){
			var s = departmentSpaces.get(i);
			if(s[3]=="${currentUser.departmentId}"){
				currentDepSpaceURL = getDepartmentSpaceURL(s);
			 	break;
			}
		}
		if(currentDepSpaceURL == null && !departmentSpaces.isEmpty()){
			currentDepSpaceURL = getDepartmentSpaceURL(departmentSpaces.get(0));
		}
	}
	
	for(var i = 0; i < Constants_Panels.size(); i++){
		var panel = Constants_Panels.get("space_" + i);
		if(panel.type == "department"){
			panel.link = currentDepSpaceURL;
	    }
	}
	<%--getA8Top().contentFrame.leftFrame.isShowSpaceMenuFirst = true;--%>
}

//initDepartmentSpaceEnter();

function getDepartmentSpaces(){
	var str = "";
	if(departmentSpaces){
		if(departmentSpaces.size() > 1){
			str += "<fmt:message key='department.switch.label'/>: <select onchange=\"getA8Top().contentFrame.topFrame.currentDepSpaceURL=this.value; getA8Top().contentFrame.topFrame.initDepartmentSpaceEnter(); location.href=this.value;\">";
			for(var i = 0; i < departmentSpaces.size(); i++){
				var s = departmentSpaces.get(i);
				var optionValue = getDepartmentSpaceURL(s);
				str += "  <option value=\"" + optionValue + "\" " + (getA8Top().contentFrame.topFrame.currentDepSpaceURL == optionValue ? "selected" : "") + ">" + s[0] + "</option>";
			}
			str += "</select>";
		}
	}
	return str;
}

<%-- 取得部门空间的链接地址，统一调这个方法 --%>
function getDepartmentSpaceURL(s){
	var isManage = managerDepartments.contains(s[3]);
	var deptURL = s[1] + "?slogan="+ encodeURIComponent(s[2]) + "&isManage=" + isManage + "&depId=" + s[3];
	return deptURL;
}

<%-- 是否需要播放声音 --%>
getA8Top().isEnableMsgSound = <c:out value="${isEnableMsgSound}" default="false" />;
<%-- 消息查看后是否关闭 --%>
getA8Top().msgClosedEnable = <c:out value="${msgClosedEnable}" default="false" />;
<%-- 系统消息是否已关闭 --%>
getA8Top().isSysMessageWindowEyeable = false;
<%-- 在线消息是否已关闭 --%>
getA8Top().isPerMessageWindowEyeable = false;

<%-- 随OA自动登陆RTX CAOFEI 2009-4-8 start add code --%>
getA8Top().isEnableRtxClient = <c:out value="${isEnableRtxClient}" default="false"/>;
function setLayoutWidth(portal){
	try{
		if(!v3x.isMSIE){
			document.readyState="complete";
		}
	}catch(e){}
	
	var isEnableRtxClient = "${isEnableRtxClient}";
	var isShowRtxClient = "${isShowRtxClient}";
	if(isShowRtxClient=='true'){
 		if(isEnableRtxClient=='true'){
 			if (document.readyState=="complete"){
	 			try{
	 				ssoRTX(0);
	 			}catch(e)
	 			{
	 			//alert("<fmt:message key='org.synchron.sso.error' bundle='${v3xRTXI18N}'/>");
	 			}
 			}
 		}
	}
}
function addResize(){
	setLayoutWidth();
}
function showSpaceSettingMenu(){
	var spaceId = getA8Top().contentFrame.spaceCacheFrame.currentSpaceId;
	var spaceIframeName = "c_"+spaceId;
	var currentFramePage = getA8Top().contentFrame.spaceCacheFrame.frames[spaceIframeName];
	var spaceType = currentFramePage.spaceType;
	var isAllowdefined = currentFramePage.isAllowdefined;
	if((spaceType=="personal"||spaceType=="leader"||spaceType=="outer"||spaceType=="personal_custom")&&isAllowdefined=="true"){
		getA8Top().document.getElementById('pageSetting').style.display="block";
	}else{
		getA8Top().document.getElementById('pageSetting').style.display="none";
	}
	getA8Top().document.getElementById('systemSetting').style.display="block";
}
function hideSpaceSettingMenu(){
	getA8Top().document.getElementById('systemSetting').style.display="none";
}
<%-- 随OA自动登陆RTX CAOFEI 2009-4-8 end add code --%>

//onselectstart="self.event.returnValue=false"

function selectStart(event){
	var obj = event.srcElement ? event.srcElement : event.target;
	if(obj && (obj.id == "keyword")){
		self.event.returnValue=true;
	}else{
		self.event.returnValue=false;
	}
	
}

//
--></script>
<c:choose>
	<c:when test="${userType == 'user'}">
		<script type="text/javascript">
			${main:showSpaceMenu(spacePath, spaceSort, fromA8 ? "" : "nc")}
		</script>
	</c:when>
	<c:when test="${userType == 'unit'}">
		<%@include file="menu/unit.jsp" %>
	</c:when>
	<c:when test="${userType == 'group'}">
		<%@include file="menu/group.jsp" %>
	</c:when>
	<c:when test="${userType == 'system'}">
		<%@include file="menu/system.jsp" %>
	</c:when>
	<c:when test="${userType == 'audit'}">
		<%@include file="menu/audit.jsp" %>
	</c:when>
	<c:when test="${userType == 'secret'}">
		<%@include file="menu/secret.jsp" %>
	</c:when>
</c:choose>
<script type="text/javascript">
<!--
<c:if test="${userType != 'user'}">
	showMenus();
</c:if>

//initAllPanel();
initAccountSelector();
var isA8geniusMsg = false;
initMessage(${v3x:getSystemProperty('message.interval.second')});
var readystate = true;
var taskAddCount = 0;

/**
 * 批量下载用户
 */
var dlS;
function downloadState(){
	if(getA8Top().xmlDoc){
		dlS = getA8Top().xmlDoc.GetDownloadState("${v3x:currentUser().id}");
	}
	if("FD_ERROR_USER_NO_FIND" == dlS){
		alert(_("V3XLang.batch_download_no_user"));
	}
}
//-->
</script>
</BODY>
</html>