<%@ page import="com.seeyon.v3x.common.web.util.WebUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@page import="com.seeyon.v3x.common.constants.ApplicationCategoryEnum" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="../common/INC/noCache.jsp"%>
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
<html:link renderURL="/doc.do" var="pigeonholeDetailURL" />
<c:set value="${v3x:currentUser()}" var="currentUser"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/default.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/collaboration/css/collaboration.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/jquery-ui.custom.css${v3x:resSuffix()}" />">
${v3x:skin()}
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/layout.css${v3x:resSuffix()}" />">
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/V3X.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/collaboration/js/collaboration.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/menu/xmenu.js${v3x:resSuffix()}" />"></script>
<script>
var v3x = new V3X();
v3x.init("${pageContext.request.contextPath}", "${v3x:getLanguage(pageContext.request)}");
_ = v3x.getMessage;
var genericURL = '${detailURL}';
</script>
</head>
<body scroll="no" class="listPadding">

<div class="main_div_row2">
  <div class="right_div_row2">
    <div class="top_div_row2">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" >
		    <tr>
		        <td class="webfx-menu-bar">
				    <script type = "text/javascript" >
				    var myBar = new WebFXMenuBar("${pageContext.request.contextPath}");
				
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
					if(v3x.getBrowserFlag('hideMenu')){
				    	myBar.add(new WebFXMenuButton("pigeonhole", "<fmt:message key='common.toolbar.pigeonhole.label' bundle='${v3xCommonI18N}' />", "javascript:colpigeonhole('<%=ApplicationCategoryEnum.collaboration.getKey()%>','sent')", [1,9], "", null));
					}
				    myBar.add(new WebFXMenuButton("repeal", "<fmt:message key='common.toolbar.repeal.label' bundle='${v3xCommonI18N}'/>", "javascript:repealItems('pending')", [3,8], "", null));
				    if(v3x.getBrowserFlag('onlyIe')){
				    	myBar.add(new WebFXMenuButton("designWorkflow", "<fmt:message key='common.design.workflow.label' bundle='${v3xCommonI18N}' />", "javascript:_designWorkflow()", [3,6], "", null));
				    }
					<c:if test="${v3x:hasNewCollaboration()}">
				    myBar.add(new WebFXMenuButton("resend", "<fmt:message key='common.toolbar.resend.label' bundle='${v3xCommonI18N}'/>", "javascript:resend()", [3,9], "", null));
				    </c:if>
					myBar.add(new WebFXMenuButton("delete", "<fmt:message key='common.toolbar.delete.label' bundle='${v3xCommonI18N}' />", "javascript:deleteItems('sent')", [1,3], "", null));
				    myBar.add(new WebFXMenuButton("setAuthority", "<fmt:message key='common.toolbar.relationAuthority.label' bundle='${v3xCommonI18N}'/>", "javascript:setRelationAuth()", [2,2], "", null));
					<v3x:showThirdMenus rootBarName="myBar" addinMenus="${AddinMenus}"/>
					document.write(myBar);
					document.close();
					</script>	
				</td>
				<v3x:selectPeople id="relationAuthority" panels="Department,Team,Post,Outworker,RelatePeople" selectType="Account,Department,Team,Post,Member" minSize="-1" showAllAccount="false" departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
	                     jsFunction="setRelationAuthority(elements);"/>
	                     	
				<td class="webfx-menu-bar"><form action="" name="searchForm" id="searchForm" method="get" onkeypress="doSearchEnter()" onsubmit="return false" style="margin: 0px">
					<input type="hidden" value="<c:out value='${param.method}' />" name="method">
					<input type="hidden" value="${param.flag}" name="flag">
					<input type="hidden" value="${param.bizConfigId}" name="bizConfigId">
					<input type="hidden" value="${param.tempIds}" name="tempIds">
					<input type="hidden" value="${param.type}" name="type">
					<div class="div-float-right condition-search-div">
						<div class="div-float">
							<select name="condition" id ="condition" onChange="showNextSpecialCondition(this)" class="condition">
						    	<option value=""><fmt:message key="common.option.selectCondition.text" bundle="${v3xCommonI18N}" /></option>
							    <option value="subject"><fmt:message key="common.subject.label" bundle="${v3xCommonI18N}" /></option>
							    <option value="importantLevel"><fmt:message key="common.importance.label" bundle='${v3xCommonI18N}' /></option>
							    <option value="createDate"><fmt:message key="common.date.sendtime.label" bundle="${v3xCommonI18N}" /></option>
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
					  		<input type="text" name="textfield" class="input-date cursor-hand" onclick="whenstart('${pageContext.request.contextPath}',this,575,140);" readonly>
					  		-
					  		<input type="text" name="textfield1" class="input-date cursor-hand" onclick="whenstart('${pageContext.request.contextPath}',this,675,140);" readonly>
					  	</div>
					  	<div onclick="javascript:doSearch()" class="div-float condition-search-button"></div>
				  	</div></form>
				</td>		
			</tr>
		</table>
    </div>
    <div class="center_div_row2" id="scrollListDiv">
		<form name="listForm" id="listForm" action="" method="get" onsubmit="return false" style="margin: 0px">
			<input type="hidden" value="" name="process_xml" id="process_xml" />
			<input type="hidden" value="" name="processId" id="processId" />
			<input type="hidden" value="" name="deadline" id="deadline" />
			<input type="hidden" value="" name="advanceRemind" id="advanceRemind" />
			<v3x:table htmlId="pending" data="csList" var="col" isChangeTRColor="true" className="sort ellipsis">
				<c:set value="${col:showSubjectOfSummary(col.summary, col.proxy, -1, col.proxyName)}" var="theSubject"/>
				<v3x:column width="5%" align="center" label="<input type='checkbox' id='allCheckbox' onclick='selectAll(this, \"id\")'/>">
					<input type='checkbox' name='id' value="${col.summary.id}" affairId="${col.affairId}" canArchive="${col.summary.canArchive}" canForward="${col.summary.canForward}" 
					 bodyType="${col.bodyType }" isFinished="${col.finshed}" caseId="${col.caseId }" processId="${col.processId }" attsFlag="${col.hasAttsFlag}" 
					 remindDate="${col.advanceRemindTime }" deadlineDate="${col.deadLine}" templeteId="${col.summary.templeteId }" archiveId="${col.summary.archiveId}"
					 colSubject="${v3x:toHTML(theSubject)}"  isNewflow="${col.summary.newflowType eq 1}" secretLevel="${col.summary.secretLevel}"/><!--成发集团 -->
				</v3x:column>
				
				<c:set value="${v3x:escapeJavascript(col.summary.subject)}" var="subject"  />
				
				<c:choose>
					<c:when test="${v3x:getBrowserFlagByRequest('PageBreak', pageContext.request)}">
						<c:set var="click" value="showDetail('from=Sent&affairId=${col.affairId}');setPositionObj(this);"/>
					</c:when>
					<c:otherwise>
						<c:set var="click" value="openDetail('', 'from=Sent&affairId=${col.affairId}')"/>
					</c:otherwise>
				</c:choose>
				
				<c:set var="dblclick" value="openDetail('', 'from=Sent&affairId=${col.affairId}')"/>
				
				<v3x:column  width="50%" type="String" label="common.subject.label" className="cursor-hand sort" 
				relationAuthorized="${col.affair.isRelationAuthority}"
				bodyType="${col.bodyType}" hasAttachments="${col.summary.hasAttachments}" importantLevel="${col.summary.importantLevel}" 
				value="${theSubject}" flowState="${col.flowState}" onClick="${click}" onDblClick="${dblclick}" />
				
				<v3x:column width="15%" type="Date" label="common.date.sendtime.label" onClick="${click}" onDblClick="${dblclick}" className="cursor-hand sort">
					<fmt:formatDate value="${col.summary.startDate}" pattern="${datePattern}"/>
				</v3x:column>
	
				<fmt:message var="isOvertop" key='process.mouseover.overtop.${col.summary.worklfowTimeout}.title'/>
				<v3x:column width="10%" type="String" label="process.cycle.label" align="left" className="cursor-hand sort deadline-${col.summary.worklfowTimeout}" 
				onClick="${click}" onDblClick="${dblclick}" alt="${v3x:_(pageContext, isOvertop)}" >
					<v3x:metadataItemLabel metadata="${colMetadata.collaboration_deadline}" value="${col.summary.deadline}"/>	
				</v3x:column>
				<v3x:column width="10%" type="String" align="center" label="col.isTrack.label">
					<span class="link-blue" onclick="preChangeTrack('${col.affairId}', ${col.isTrack})">
			    		<span id="track${col.affairId}"><fmt:message key="common.${col.isTrack}" bundle="${v3xCommonI18N}"/></span>
			    	</span>
				</v3x:column>
				<v3x:column width="10%" align="center" label="processLog.list.title.label" >
					<span onclick="showDetailAndLog('${col.summary.id}', '${col.processId}', '1');" class="icon_com display_block flowdaily_com cursor-hand"></span>
				</v3x:column>
			</v3x:table>
		</form>
    </div>
  </div>
</div>
<div id="formContainer" style="display:none"></div>
<form name="mailForm" method="post" action="">
<input type="hidden" name="formContent" value="">
</form>
</body>
</html>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/flowperm/js/flowperm.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/workflow/workflow.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/doc/js/thirdMenu.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery-ui.custom.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.plugin.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/form/js/formdisplay/SeeyonForm.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript">
var pigeonholeURL = "${pigeonholeDetailURL}";
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
var showMode = 1;
var editWorkFlowFlag = "true";
var isFormTemplate = false;
var isFromEdoc = true;
var summaryId = "";
var isTemplete = false;
var appName = "collaboration";
var endFlag = false;

//异步加载流程信息
var caseProcessXML = "";
var caseLogXML = "";
var caseWorkItemLogXML = "";

v3x.loadLanguage("/apps_res/collaboration/js/i18n");
v3x.loadLanguage("/apps_res/v3xmain/js/i18n");
if('${param.flag}'=='formBizConfig') {
	if("${menuId}"!=""){
		getA8Top().showLocation("${menuId}");
	}else if("${param.menuId}"!=""){
		getA8Top().showLocation("${param.menuId}");
	}
} else {
	getA8Top().showLocation(103);
}

function _designWorkflow(){
	var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) return false;
    
    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) return;

    var hasMoreElement = false;
    var len = id_checkbox.length;
    var countChecked = 0;
    var caseId = null;
    var processId = null;
    var deadline = null;
    var advanceRemind = null;
    var templeteFlag = null;
    var secretLevel = null;//成发集团
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            hasMoreElement = true;
            caseId = id_checkbox[i].caseId;
            processId = id_checkbox[i].processId;
            summaryId = id_checkbox[i].value;
            deadline = id_checkbox[i].deadlineDate;
            advanceRemind = id_checkbox[i].remindDate;
            endFlag = id_checkbox[i].isFinished;
            templeteId = id_checkbox[i].templeteId;
            secretLevel = id_checkbox[i].secretLevel;//成发集团
            countChecked++;
        }
    }
    if (!hasMoreElement) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertEditFlow"));
        return;
    }
    if(countChecked > 1){
    	alert(v3x.getMessage("collaborationLang.collaboration_confirmEditflowOnlyOne"));
        return;
    }
        
    if(templeteId != "") endFlag = "true";
    
    if((endFlag && endFlag == "true")) {
    	alert(v3x.getMessage("collaborationLang.flow_end_or_template"));
    	return;
    }
    
    //将processId,advanceRemind,deadline设置到隐藏域中
    theForm.document.getElementById("processId").value = processId;
    theForm.document.getElementById("deadline").value = deadline;
    theForm.document.getElementById("advanceRemind").value = advanceRemind;
    //获取流程相关xml
	try {
		var requestCaller = new XMLHttpRequestCaller(null, "ajaxColManager", "getXML", false, "POST");
		requestCaller.addParameter(1, "String", caseId);
		requestCaller.addParameter(2, "String", processId);
		var processXMLs = requestCaller.serviceRequest();
		if(processXMLs){
			caseProcessXML = processXMLs[0];
			caseLogXML = processXMLs[1];
			caseWorkItemLogXML = processXMLs[2];
			theForm.document.getElementById("process_xml").value = caseProcessXML;
		}
	}
	catch (ex1) {}

	//判断流程是否已经被锁定，未被锁定则给该流程加上一个同步锁，为接下来的修改流程做准备
	if(!checkModifyingProcessAndLock(processId, summaryId)){
		return;
	}
	var frameNames = "collIframe.listFrame"; 
	var rv = getA8Top().v3x.openWindow({
        url: genericControllerURL + "collaboration/monitor&showColAssign=false&isShowButton=true&from=Sent&frameNames="+frameNames+"&endFlag="+endFlag+"&secretLevel="+secretLevel,//成发集团
        width: "860",
        height: "690",
        resizable: "no"
    });
    //将是否编辑标记置回初始化状态
}
initIpadScroll("scrollListDiv",550,870);
showDetailPageBaseInfo("detailFrame", "<fmt:message key='menu.collaboration.listsent' bundle='${v3xMainI18N}' />", "/common/images/detailBannner/103.gif", pageQueryMap.get('count'), _("collaborationLang.detail_info_103"));
showCondition("${param.condition}", "<v3x:out value='${param.textfield}' escapeJavaScript='true' />", "<v3x:out value='${param.textfield1}' escapeJavaScript='true' />");
</script>