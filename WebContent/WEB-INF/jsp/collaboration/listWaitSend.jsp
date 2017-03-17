<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="../common/INC/noCache.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/collaboration" prefix="col"%>
<%@ taglib uri="http://v3x.seeyon.com/bridges/spring-portlet-html" prefix="html"%>
<fmt:setBundle basename="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources" var="v3xCommonI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.main.resources.i18n.MainResources" var="v3xMainI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.flowperm.resources.i18n.FlowPermResource" var="permRes"/>
<fmt:setBundle basename="com.seeyon.v3x.edoc.resources.i18n.EdocResource" var="edocRes"/>
<fmt:setBundle basename="com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource"/>
<fmt:setBundle basename="com.seeyon.v3x.bulletin.resources.i18n.BulletinResources" var="bulI18N" />
<fmt:setBundle basename="com.seeyon.v3x.taskmanage.resources.i18n.TaskManageResources" var="taskI18N" />
<fmt:setBundle basename="com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources" var="workflowI18N" />
<fmt:message key="common.datetime.pattern" var="datePattern" bundle="${v3xCommonI18N}"/>
<html:link renderURL="/collaboration.do" var="detailURL" />
<html:link renderURL='/templete.do' var="templeteURL" />
<html:link renderURL='/edocSupervise.do' var="supervise" />
<html:link renderURL='/genericController.do' var="genericController" />
<html:link renderURL="/collaboration.do?method=fullEditor" var="fullEditorURL" />
<html:link renderURL="/form.do" var="formURL" />
<html:link renderURL="/edocController.do" var="edocURL" />
<html:link renderURL="/doc.do" var="docURL" />
<html:link renderURL="/mtMeeting.do" var="mtMeetingURL" />
<html:link renderURL='/webmail.do' var='webmailURL' />
<html:link renderURL="/colSupervise.do" var="colSuperviseURL"/>
<html:link renderURL="/workManage.do" var="workManageURL"/>
<html:link renderURL="/processLog.do" var="processLogURL"/>
<html:link renderURL="/exchangeEdoc.do" var="exchangeURL" />
<c:set value="${v3x:currentUser()}" var="currentUser"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/default.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/collaboration/css/collaboration.css${v3x:resSuffix()}" />">
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/V3X.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/collaboration/js/collaboration.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/menu/xmenu.js${v3x:resSuffix()}" />"></script>
${v3x:skin()}
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/jquery-ui.custom.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/layout.css${v3x:resSuffix()}" />"> 
<script>
var v3x = new V3X();
v3x.init("${pageContext.request.contextPath}", "${v3x:getLanguage(pageContext.request)}");
_ = v3x.getMessage;
var genericURL = '${detailURL}';
</script>
</head>
<body class="listPadding">
<div class="main_div_row2">
  <div class="right_div_row2">
    <div class="top_div_row2">
		<table width="100%"  border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="webfx-menu-bar">
			    	<script type="text/javascript">
			    	var myBar = new WebFXMenuBar("${pageContext.request.contextPath}");
					myBar.add(new WebFXMenuButton("send", "<fmt:message key='common.toolbar.send.label' bundle='${v3xCommonI18N}' />", "javascript:sendFromWaitSend()", [1,4], "", null));
					myBar.add(new WebFXMenuButton("edit", "<fmt:message key='common.toolbar.edit.label' bundle='${v3xCommonI18N}' />", "javascript:editFromWaitSend()", [1,2], "", null));
					var transmit = new WebFXMenu;
					<c:if test="${v3x:hasNewCollaboration()}">
		    		transmit.add(new WebFXMenuItem("", "<fmt:message key='common.toolbar.transmit.col.label' bundle='${v3xCommonI18N}' />", "forwardItem()", ""));
					</c:if>
					<c:if test="${v3x:hasNewMail()}">
					transmit.add(new WebFXMenuItem("", "<fmt:message key='common.toolbar.transmit.mail.label' bundle='${v3xCommonI18N}' />", "forwardMail()", ""));
					</c:if>
					
					if(transmit.hasChild()&&v3x.getBrowserFlag('hideMenu')){
						myBar.add(new WebFXMenuButton("transmit", "<fmt:message key='common.toolbar.transmit.label' bundle='${v3xCommonI18N}' />", null, [1,7], "", transmit));
					}
						
			    	myBar.add(new WebFXMenuButton("delete", "<fmt:message key='common.toolbar.delete.label' bundle='${v3xCommonI18N}' />", "javascript:deleteItems('draft')", [1,3], "", null));
			    	<%--
			    	myBar.add(new WebFXMenuButton("refresh", "<fmt:message key='common.toolbar.refresh.label' bundle='${v3xCommonI18N}' />", "javascript:refreshIt()", [1,10], "", null));
					--%>
					<v3x:showThirdMenus rootBarName="myBar" addinMenus="${AddinMenus}"/>
			    	
			    	document.write(myBar);
			    	document.close();
			    	</script>
				</td>
				<td class="webfx-menu-bar"><form action="" name="searchForm" id="searchForm" method="get" onkeypress="doSearchEnter()" onsubmit="return false" style="margin: 0px">
					<input type="hidden" value="<c:out value='${param.method}' />" name="method">
					<input type="hidden" value="${param.flag}" name="flag">
					<input type="hidden" value="${param.bizConfigId}" name="bizConfigId">
					<input type="hidden" value="${param.tempIds}" name="tempIds">
					<input type="hidden" value="${param.type}" name="type">
					<div class="div-float-right condition-search-div">
						<div class="div-float">
							<select name="condition" id="condition" onChange="showNextSpecialCondition(this)" class="condition">
						    	<option value=""><fmt:message key="common.option.selectCondition.text" bundle="${v3xCommonI18N}" /></option>
							    <option value="subject"><fmt:message key="common.subject.label" bundle="${v3xCommonI18N}" /></option>
							    <option value="importantLevel"><fmt:message key="common.importance.label" bundle="${v3xCommonI18N}" /></option>
							    <option value="createDate"><fmt:message key="common.date.createtime.label" bundle="${v3xCommonI18N}" /></option>
						  	</select>
					  	</div>
					  	<div id="subjectDiv" class="div-float hidden">
					  		<input type="text" name="textfield" class="textfield">
					  	</div>
					  	<div id="importantLevelDiv" class="div-float hidden">
					  		<select name="textfield" class="textfield">
					  			<v3x:metadataItem metadata="${comImportanceMetadata}" showType="option" name="importantLevel" />
					  		</select>	
					  	</div>
					  	<div id="createDateDiv" class="div-float hidden">
					  		<input type="text" name="textfield" class="input-date cursor-hand" onclick="whenstart('${pageContext.request.contextPath}',this,575,140);" value="" readonly>
					  		-
					  		<input type="text" name="textfield1" class="input-date cursor-hand" onclick="whenstart('${pageContext.request.contextPath}',this,675,140);" value="" readonly>
					  	</div>
					  	<div onclick="javascript:doSearch()" class="div-float condition-search-button"></div>
				  	</div></form>
				</td>
			</tr>
		</table>
    </div>
    <div class="center_div_row2" id="scrollListDiv">
		<form name="listForm" id="listForm" method="get" target="mainFrame" onsubmit="return false" style="margin: 0px" action="">
			<input type="hidden" name="__ActionToken" readonly value="SEEYON_A8" > <%-- post提交的标示，先写死，后续动态 --%>
			<!-- 接收从弹出页面提交过来的数据 -->
			<input type="hidden" name="popJsonId" id="popJsonId" value="">
			<input type="hidden" name="popNodeSelected" id="popNodeSelected" value="">
			<input type="hidden" name="popNodeCondition" id="popNodeCondition" value="">
			<input type="hidden" name="popNodeNewFlow" id="popNodeNewFlow" value="">
			<input type="hidden" name="allNodes" id="allNodes" value="">
			<input type="hidden" name="nodeCount" id="nodeCount" value="">
			<v3x:table htmlId="pending" data="csList" var="col" className="sort ellipsis">
				<v3x:column width="5%" align="center" label="<input type='checkbox' id='allCheckbox' onclick='selectAll(this, \"id\")'/>">
					<input type='checkbox' name='id' value="${col.summary.id}" affairId="${col.affairId}"  
					 colSubject="${col:showSubjectOfSummary(col.summary, col.proxy, -1, col.proxyName)}"
					 bodyType="${col.bodyType }" processId="${col.summary.processId}" caseId="${col.summary.caseId}" 
					 templeteId="${col.summary.templeteId}" secretLevel="${col.summary.secretLevel}" /> 
					 <!-- 2017-3-16 诚佰公司 添加密级参数 发送待办时验证 -->
				</v3x:column>
			
				<c:choose>
					<c:when test="${v3x:getBrowserFlagByRequest('PageBreak', pageContext.request)}">
						<c:set var="click" value="showDetail('from=WaiSend&affairId=${col.affairId}');setPositionObj(this);"/>
					</c:when>
					<c:otherwise>
						<c:set var="click" value="openDetail('', 'from=WaiSend&affairId=${col.affairId}')"/>
					</c:otherwise>
				</c:choose>
				
				<c:set var="dblclick" value="editFromWaitSend('${col.summary.id}','${col.affairId}')"/>
				
				<v3x:column width="53%" type="String" label="common.subject.label" className="cursor-hand sort" 
				bodyType="${col.bodyType}" hasAttachments="${col.summary.hasAttachments}" importantLevel="${col.summary.importantLevel}"
				onClick="${click}" onDblClick="${dblclick}" value="${col:showSubjectOfSummary(col.summary, col.proxy, -1, col.proxyName)}"  />
				
				<v3x:column width="15%" type="Date" label="common.date.createtime.label" className="cursor-hand sort" onClick="${click}" onDblClick="${dblclick}">
					<fmt:formatDate value="${col.startDate}" pattern="${datePattern}"/>
				</v3x:column>
				
				<v3x:column width="15%" type="String" label="process.cycle.label" onClick="${click}" onDblClick="${dblclick}" className="cursor-hand sort">
					<v3x:metadataItemLabel metadata="${colMetadata.collaboration_deadline}" value="${col.summary.deadline}"/>
				</v3x:column>
				
				<v3x:column width="10%" type="String" label="common.state.label" align="center"
					onClick="${click}" onDblClick="${dblclick}" className="cursor-hand sort">
					<fmt:message key="col.substate.${col.state}.label"/>
				</v3x:column>
				
			</v3x:table>
			<div style="display:none" id="processModeSelectorContainer"></div>			
		</form>
    </div>
  </div>
</div>
<div id="formContainer" style="display:none"></div>  
<form name="mailForm" method="post" action="">
<input type="hidden" name="formContent" value="">
</form>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/flowperm/js/flowperm.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/workflow/workflow.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/doc/js/thirdMenu.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery-ui.custom.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.plugin.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/form/js/formdisplay/SeeyonForm.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript">
<!--
var alert_noFlow = "<fmt:message key='alert.sendImmediate.nowf'/>";
var alert_cannotTakeBack = "<fmt:message key='col.takeBack.flowEnd.alert' />";
var docURL = "${docURL}";
var edocURL = "${edocURL}";
var edocSuperviseURL = '${supervise}';
var genericControllerURL = "${genericController}?ViewPage=";
var deleteActionURL = genericURL + "?method=delete&from=${param.method}";
var pigeonholeActionURL = genericURL + "?method=pigeonhole&from=${param.method}";
var templeteURL = "${templeteURL}";
var fullEditorURL = "${fullEditorURL}";
var formURL = "${formURL}";
var mailURL = "${webmailURL}";
var colSuperviseURL = "${colSuperviseURL}";
var workManageURL = "${workManageURL}";
var processLogURL = "${processLogURL}";
var colWorkFlowURL=genericURL;
var mtMeetingUrl = "${mtMeetingURL}";

var collaborationCanstant = {
    deleteActionURL : "collaboration.do?method=delete&from=${param.method}",
    takeBackActionURL : "collaboration.do?method=takeBack",
    deletePeopleActionURL : "collaboration.do?method=deletePeople",
	hastenActionURL : "collaboration.do?method=hasten",
	pigeonholeActionURL : "collaboration.do?method=pigeonhole&from=${param.method}",
	issusNewsActionURL : "collaboration.do?method=issusNews",
	issusBulletionActionURL : "collaboration.do?method=issusBulletion"
}

var edocCanstant = {
	hastenActionURL : "edocSupervise.do?method=sendMessage"
}

v3x.loadLanguage("/apps_res/collaboration/js/i18n");
v3x.loadLanguage("/apps_res/v3xmain/js/i18n");

if('${param.flag}'=='formBizConfig') {
	if("${menuId}"!=""){
		getA8Top().showLocation("${menuId}");
	}else if("${param.menuId}"!=""){
		getA8Top().showLocation("${param.menuId}");
	}
} else {
	getA8Top().showLocation(102);
}

var team = new Array();
var secondpost = new Array();
<c:if test="${teams != null}">
	<c:forEach items="${teams}" var="team">
		team["${team.id}"] = ${team.id};
	</c:forEach>
</c:if>
<c:if test="${secondPosts != null}">
	<c:forEach items="${secondPosts}" var="secondPost">
		secondpost["${secondPost.depId}_${secondPost.postId}"] = "${secondPost.depId}_${secondPost.postId}";
	</c:forEach>
</c:if>
initIpadScroll("scrollListDiv",550,870);
showDetailPageBaseInfo("detailFrame", "<fmt:message key='menu.collaboration.listWaitsend' bundle='${v3xMainI18N}' />", [1,1], pageQueryMap.get('count'), _("collaborationLang.detail_info_102"));
showCondition("${param.condition}", "<v3x:out value='${param.textfield}' escapeJavaScript='true' />", "<v3x:out value='${param.textfield1}' escapeJavaScript='true' />");
//-->
</script>
</body>
</html>