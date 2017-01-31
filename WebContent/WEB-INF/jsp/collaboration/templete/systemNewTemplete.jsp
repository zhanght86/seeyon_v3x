<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/collaboration" prefix="col"%>
<%@page import="com.seeyon.v3x.common.constants.ApplicationCategoryEnum" %>
<%@page import="com.seeyon.v3x.common.constants.Constants" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="header.jsp" %>
<%@ include file="../../doc/pigeonholeHeader.jsp" %>
<c:choose>
	<c:when test="${templete.id != null}">
		<c:set value="${templete.categoryId}" var="categoryId" />
	</c:when>
	<c:otherwise>
		<c:set value="${param.categoryId}" var="categoryId" />
	</c:otherwise>
</c:choose>
<script type="text/javascript">
<!--
var hasDiagram = <c:out value="${hasWorkflow}" default="false" />;        
var caseProcessXML = '${process_xml}';
var caseLogXML = "";
var caseWorkItemLogXML = "";
var showMode = 1;
var currentNodeId = null;
var showHastenButton = "";
var isTempleteEditor = true;
var isTemplete = true;
var templeteFrom = "${param.from}";

var appName = "collaboration";

var editWorkFlowFlag = "true"

//分支
var branchs = new Array();
var keys = new Array();
var hasKeys = true;

var policys = null;
var nodes = null;
<c:if test="${branchs != null}">
	<c:forEach items="${branchs}" var="branch" varStatus="status">
		var branch = new ColBranch();
		branch.id = ${branch.id};
		branch.conditionType = "${branch.conditionType}";
		branch.formCondition = "${v3x:escapeJavascript(branch.formCondition)}";
		branch.conditionTitle = "${v3x:escapeJavascript(branch.conditionTitle)}";
		branch.conditionDesc = "${v3x:escapeJavascript(branch.conditionDesc)}";
		branch.isForce = "${branch.isForce}";
		branch.conditionBase = "${branch.conditionBase}";
		eval("branchs["+${branch.linkId}+"]=branch");
		keys[${status.index}] = ${branch.linkId};
	</c:forEach>
</c:if>

hasWorkflow = <c:out value='${hasWorkflow}' default='false' />;

var selectedElements = null;

var showOriginalElement_wf = false; //不回显原先选择的人员
var flowtype_wf = "sequence";
var hiddenPostOfDepartment_auth = true;
var showAllOuterDepartment_auth = true;
var isNeedCheckLevelScope_auth = false;

var hiddenColAssignRadio_wf = true;
var hiddenGroupLevel_wf = true;
var hiddenSaveAsTeam_wf = true;
var isNeedCheckLevelScope_wf = false;

//flowSecretLevel_wf = "${!empty secret ? secret : 1}";
//2017-01-11 诚佰公司
flowSecretLevel_wf = "${!empty secret ? secret : ''}";

function init() {
	//if (${templete.type eq 'workflow'}) {
		//setNoCheckedAndDisabled("allow_transmit");
		//setNoCheckedAndDisabled("allow_edit");
		//setNoCheckedAndDisabled("allow_edit_attachment");
		//setNoCheckedAndDisabled("allow_pipeonhole");		
	//}
	if (${templete.type eq 'text'}) {
		setNoCheckedAndDisabled("allow_chanage_flow");
	}
}

//-->
</script>
</head>
<body scroll="no" style="overflow: hidden" onload="init();">
<form name="sendForm" id="sendForm" method="post" action="${templeteURL}?method=systemSaveTemplete" style="margin: 0px; padding: 0px">
<div id="branchDiv"></div>
<input type="hidden" name="id" value="${templete.id}">
<input type="hidden" name="process_desc_by" value="${process_desc_by}" />
<input type="hidden" name="process_xml" value="" />
<input type="hidden" name="archiveId" value="${summary.archiveId}" />
<input type="hidden" name="prevArchiveId" value="${summary.archiveId}" />
<input type="hidden" name="workflowRule" id="workflowRule" value="<c:out value='${templete.workflowRule}' escapeXml='true' />" />

<input type="hidden" name="categoryType" value="${param.categoryType}" />
<input type="hidden" name="from" id ="from"  value="${param.from}" />
<input type="hidden" name="supervisorId" id="supervisorId" value="${colSupervisors }">
<input type="hidden" name="supervisors" id="supervisors" value="${colSupervise.supervisors }">
<input type="hidden" name="awakeDate" id="awakeDate" value="${colSupervise.templateDateTerminal }">
<input type="hidden" name="superviseTitle" id="superviseTitle" value="${colSupervise.title }">
<input type="hidden" name="superviseRole" id="superviseRole" value="${colSuperviseRole }">
<input type="hidden" name="superviseId" id="superviseId" value="${colSupervise.id }">
<input type="hidden" name="loginAccountId" id="loginAccountId" value="${v3x:currentUser().loginAccount}" >

<c:set value="${v3x:parseElements(templete.templeteAuths, 'authId', 'authType')}" var="authInfo" />
<input type="hidden" name="authInfo" id="auth" value="${authInfo}" />

<span id="people" style="display:none;"></span>
<c:set value="${col:isCanSendAccountColl() ? ',Account' : ''}" var="canSendAccountColl" />

<v3x:selectPeople id="auth" panels="Department,Team,Post,Level,Outworker,Account" selectType="Account,Department,Team,Member,Post,Level" minSize="0" jsFunction="doAuth(elements)" originalElements="${authInfo}" />

<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td colspan="9" height="22" valign="top">
		<script type="text/javascript">
		var myBar = new WebFXMenuBar("${pageContext.request.contextPath}", "gray");
		
		var insert = new WebFXMenu;
		insert.add(new WebFXMenuItem("", "<fmt:message key='common.toolbar.insert.localfile.label' bundle='${v3xCommonI18N}' />", "insertAttachment()"));
		
		<%--var bodyTypeSelector = new WebFXMenu;
		bodyTypeSelector.add(new WebFXMenuItem("menu_bodytype_HTML", "<fmt:message key='common.body.type.html.label' bundle='${v3xCommonI18N}' />", "chanageBodyType('<%=Constants.EDITOR_TYPE_HTML%>');", "<c:url value='/common/images/toolbar/bodyType_html.gif'/>"));
		bodyTypeSelector.add(new WebFXMenuItem("menu_bodytype_OfficeWord", "<fmt:message key='common.body.type.officeword.label' bundle='${v3xCommonI18N}' />", "chanageBodyType('<%=Constants.EDITOR_TYPE_OFFICE_WORD%>');", "<c:url value='/common/images/toolbar/bodyType_word.gif'/>"));
		bodyTypeSelector.add(new WebFXMenuItem("menu_bodytype_OfficeExcel", "<fmt:message key='common.body.type.officeexcel.label' bundle='${v3xCommonI18N}' />", "chanageBodyType('<%=Constants.EDITOR_TYPE_OFFICE_EXCEL%>');", "<c:url value='/common/images/toolbar/bodyType_excel.gif'/>"));
		bodyTypeSelector.add(new WebFXMenuItem("menu_bodytype_WpsWord", "<fmt:message key='common.body.type.wpsword.label' bundle='${v3xCommonI18N}' />", "chanageBodyType('<%=Constants.EDITOR_TYPE_WPS_WORD%>')", "<c:url value='/common/images/toolbar/bodyType_wpsword.gif'/>"));
		bodyTypeSelector.add(new WebFXMenuItem("menu_bodytype_WpsExcel", "<fmt:message key='common.body.type.wpsexcel.label' bundle='${v3xCommonI18N}' />", "chanageBodyType('<%=Constants.EDITOR_TYPE_WPS_EXCEL%>')", "<c:url value='/common/images/toolbar/bodyType_wpsexcel.gif'/>"));
		--%>
		<%--
		var save = new WebFXMenu;
		save.add(new WebFXMenuItem("saveAsText", "<fmt:message key='templete.text.label' />", "saveTemplete('text')", "<c:url value='/apps_res/collaboration/images/text.gif'/>"));
		save.add(new WebFXMenuItem("saveAsWorkflow", "<fmt:message key='templete.workflow.label' />", "saveTemplete('workflow')", "<c:url value='/apps_res/collaboration/images/workflow.gif'/>"));
		save.add(new WebFXMenuItem("saveAsTemplete", "<fmt:message key='templete.category.type.${param.categoryType}' />", "saveTemplete('templete')", "<c:url value='/apps_res/collaboration/images/text_wf.gif'/>"));
		--%>
		
		myBar.add(new WebFXMenuButton("save", "<fmt:message key='common.toolbar.save.label' bundle='${v3xCommonI18N}' />", "saveTemplete()", [1,5], ""));
		myBar.add(new WebFXMenuButton("authButton", "<fmt:message key='common.toolbar.auth.label' bundle='${v3xCommonI18N}' />", "selectPeopleFun_auth()", [2,2]));
		if(v3x.getBrowserFlag('signature')==true){
			myBar.add(new WebFXMenuButton("workflow", "<fmt:message key='common.design.workflow.label' bundle='${v3xCommonI18N}'/>", "designWorkFlow('detailIframe')", [3,6], ""));
		}
		myBar.add(new WebFXMenuButton("insert", "<fmt:message key='common.toolbar.insert.label' bundle='${v3xCommonI18N}' />", null, [1,6], "", insert));
		myBar.add(${v3x:bodyTypeSelector("v3x")});
		<%--myBar.add(new WebFXMenuButton("bodyTypeSelector", "<fmt:message key='common.body.type.label' bundle='${v3xCommonI18N}' />", null, "<c:url value='/common/images/toolbar/bodyTypeSelector.gif'/>", "", bodyTypeSelector));--%>
		
		myBar.add(new WebFXMenuButton("superviseSetup", "<fmt:message key='common.toolbar.supervise.label' bundle='${v3xCommonI18N}' />", "openSuperviseWindowForTemplate()", [3,10], "", null));
		
		document.write(myBar);
		document.close();
		<c:if test="${templete == null}">
			if(bodyTypeSelector){
				bodyTypeSelector.disabled("menu_bodytype_HTML");//默认的置灰
			}
		</c:if>
		</script>
	</td>
  </tr>
  <tr class="bg-summary">
    <td width="8%" height="29" class="bg-gray"><fmt:message key="common.subject.label" bundle="${v3xCommonI18N}" />:</td>
    <td width="30%"><fmt:message key="common.default.subject.value" var="defSubject" bundle="${v3xCommonI18N}" />
        <input name="subject" type="text" id="subject" class="input-100per" maxlength="60" deaultValue="${defSubject}"
               inputName="<fmt:message key="common.subject.label" bundle="${v3xCommonI18N}" />" validate="isDeaultValue,notNull"
               value="<c:out value="${summary.subject}" escapeXml="true" default='${defSubject}' />"
               ${v3x:outConditionExpression(readOnly, 'readonly', '')}
               onfocus='checkDefSubject(this, true)' onblur="checkDefSubject(this, false)">
    </td>
    <!-- 流程密级 -->
    <td width="8%" class="bg-gray"><font color="red"><fmt:message key="collaboration.secret.flowsecret"/>:</font></td>
    <td width="10%">
    	<!-- 2017-01-11 诚佰公司 注释新增
    	<select name="secretLevel" id="secretLevel" class="input-100per" onchange="changeSecretLevel(this);">
    	<option value="1" ${secret == 1 ? 'selected' :''}><fmt:message key="collaboration.secret.nosecret"/></option>
    	<option value="2" ${secret == 2 ? 'selected' :''}><fmt:message key="collaboration.secret.secret"/></option>
    	<option value="3" ${secret == 3 ? 'selected' :''}><fmt:message key="collaboration.secret.secretmore"/></option>
    	-->
    	<select name="secretLevel" id="secretLevel" class="input-100per" onchange="changeSecretLevel(this);">
    	<option value=""></option>
    	<option value="1" ${secret == 1 ? 'selected' :''}><fmt:message key="collaboration.secret.nosecret"/></option>
    	<option value="2" ${secret == 2 ? 'selected' :''}><fmt:message key="collaboration.secret.secret"/></option>
    	<option value="3" ${secret == 3 ? 'selected' :''}><fmt:message key="collaboration.secret.secretmore"/></option>
    </select>
    </td>
    
    <td width="8%" nowrap="nowrap" class="bg-gray"><fmt:message key="templete.toolbar.category" />:</td> 
    <td width="10%" colspan="3">
        <select name="categoryId" id="categoryId" class="input-100per">
			${categoryHTML}
		</select>
		<script type="text/javascript">setSelectValue('categoryId', '${categoryId}');</script>
    </td>
    <!-- 模板类型 -->
    <td align="center"><fmt:message key="templete.type.label"/>:
	    <select name="type" style="width:90px" onchange="preAlertChangeType(this)">
		    <option value="templete" ${templete.type eq 'templete' ?'selected' :'' }><fmt:message key='templete.category.type.${param.categoryType}' /></option>
		    <option value="text" ${templete.type eq 'text' ?'selected' :'' }><fmt:message key='templete.text.label' /></option>
		    <option value="workflow" ${templete.type eq 'workflow' ?'selected' :'' }><fmt:message key='templete.workflow.label' /></option>
	    </select>
    </td>
  </tr>
  <tr class="bg-summary"> 
    <td nowrap="nowrap" height="24" class="bg-gray"><fmt:message key="workflow.label" />:</td>
    <td nowrap="nowrap"><fmt:message key='default.workflowInfo.value' var="dfwf" />
    	<input name="workflowInfo" class="input-100per cursor-hand" readonly 
    	value="<c:out value="${col:getWorkflowInfo(workflowInfo, colFlowPermPolicyMetadata, pageContext)}" default="${dfwf}" />" onclick="designWorkFlow('detailIframe')">
    </td>
    <td class="bg-gray"><fmt:message key="common.importance.label" bundle='${v3xCommonI18N}' />:</td>
    <td>
    	<select name="importantLevel" class="input-100per">
    		<v3x:metadataItem metadata="${comImportanceMetadata}" showType="option" name="importantLevel" selected="${summary.importantLevel}" />
    	</select>         	
    </td>  
    <td width="8%" nowrap="nowrap" class="bg-gray"><fmt:message key="prep-pigeonhole.label" />:</td>     
    <td width="8%" colspan="1">
	    <select id="colPigeonhole" ${templete.type eq 'text' or templete.type eq 'workflow' ? 'disabled':'' } class="input-100per" onchange="pigeonholeEvent(this)">
	    	<option id="defaultOption" value="1"><fmt:message key="common.default" bundle="${v3xCommonI18N}"/></option>   
	    	<option id="modifyOption" value="2">${v3x:_(pageContext, 'click.choice')}</option>
	    	<c:if test="${archiveName ne null && archiveName ne ''}" >
	    		<option value="3" selected>${archiveName}</option>
	    	</c:if>
	    </select>
    </td>
    <td width="8%" nowrap="nowrap" class="bg-gray"><fmt:message key="project.label"/>:</td>     
    <td width="8%">
        <select name="projectId" id="projectId" ${templete.type eq 'text' or templete.type eq 'workflow' ? 'disabled':'' } class="input-100per">
   			<option value=""><fmt:message key='project.nothing.label' /></option>
    		<c:forEach items="${relevancyProject}" var="project">
				<option value="${project.id}" ${summary.projectId == project.id ? 'selected' : ''}>${v3x:toHTML(project.projectName)}</option>
			</c:forEach>
    	</select>
    </td>
    <td align="center">
    	<div onclick="showAdvance()" id="advanceButton" class="cursor-hand link-blue"><fmt:message key="common.advance.label" bundle="${v3xCommonI18N}" /></div>
    </td>
  </tr>
  <tr id="attachmentTR" class="bg-summary" style="display:none;">
      <td nowrap="nowrap" height="18" class="bg-gray" valign="top"><fmt:message key="common.attachment.label" bundle="${v3xCommonI18N}" />:</td>
      <td colspan="8" valign="top"><div class="div-float">(<span id="attachmentNumberDiv"></span>)</div>
		<v3x:fileUpload attachments="${attachments}" applicationCategory="1" canDeleteOriginalAtts="${canDeleteOriginalAtts}" originalAttsNeedClone="${cloneOriginalAtts}" />
      </td>
  </tr>
 
  <tr>
  	<td colspan="9" height="6" class="bg-b"></td>
  </tr>
  <tr valign="top">
	<td colspan="9"><table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr valign="top">
			<td><v3x:editor htmlId="content" content="${body.content}" type="${body.bodyType}" createDate="${body.createDate}" originalNeedClone="${cloneOriginalAtts}" category="<%=ApplicationCategoryEnum.collaboration.getKey()%>" /></td>
			<td width="45px" id="noteAreaTd" nowrap="nowrap">
		    	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" class="noteArea">
		   			<tr>
				  		<td valign="top" class="sign-button-bg"><table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				   			<tr>
				   				<td class="right-scroll-bg">
								<div id="noteMinDiv" style="height: 100%" class="sign-min-bg">
									<div class="sign-min-label" onclick="changeLocation('senderNote');showNoteArea()"><fmt:message key="sender.note.label" /></div>
									<div class="separatorDIV"></div>
								</div>
				   				<table id="noteAreaTable" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				   					<tr>
						   				<td height="25">
											<div id="hiddenPrecessAreaDiv" onclick="hiddenNoteArea()" title="<fmt:message key='common.display.hidden.label' bundle='${v3xCommonI18N}' />"></div>
											<script type="text/javascript">
											var panels = new ArrayList();
											panels.add(new Panel("senderNote", '<fmt:message key="sender.note.label" />'));
											<%-- panels.add(new Panel("colQuery", '<fmt:message key="col.query.label"/>')); --%>
											
											showPanels(false);
											</script>
								  		</td>
						  			</tr>
									<tr>
										<td height="25" class="senderNode"><fmt:message key="sender.note.label"/>(<fmt:message key="common.charactor.limit.label" bundle="${v3xCommonI18N}"><fmt:param value="500" /></fmt:message>)<td>
									</tr>
									<tr id="senderNoteTR" style="display:none;">
										<td class="note-textarea-td">
											<textarea cols="" rows="" name="note" validate="maxLength" inputName="<fmt:message key='sender.note.label' />" maxSize="200" class="note-textarea wordbreak"><c:out value='${summary.senderOpinion.content}' escapeXml='true' /></textarea>
										</td>
									</tr>	   				
				   				</table></td>
							</tr>
				    	</table></td>
				   </tr>
				</table>
			</td>
		</tr>
	</table></td>
  </tr>
</table>

<div id="advanceHTML" class="hidden">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
		    <td width="30%" nowrap="nowrap" class="bg-gray"><fmt:message key="process.cycle.label"/>:</td>     
		    <td width="70%">
		    	<select name="deadline" id="deadline" class="input-100per" onchange="javascript:templeteCompareTime()">
		    		<v3x:metadataItem metadata="${colMetadata['collaboration_deadline']}" showType="option" name="deadline" selected="${summary.deadline}" />
		    	</select></td>
		</tr>
		<tr>
			<td colspan="2" height="5"></td>
		</tr>
		<tr>
		    <td width="30%" nowrap="nowrap" class="bg-gray"><fmt:message key="common.reference.time.label" bundle='${v3xCommonI18N}'/>:</td>     
		    <td width="70%">
		    	<select name="referenceTime" id="referenceTime" class="input-100per" onchange="javascript:templeteCompareTime()">
		    		<v3x:metadataItem metadata="${colMetadata['collaboration_deadline']}" showType="option" name="referenceTime1" selected="${templete.standardDuration}" />
		    	</select></td>
		</tr>
		<tr>
			<td colspan="2" height="5"></td>
		</tr>
		<tr>
		    <td width="30%" nowrap="nowrap" class="bg-gray"><fmt:message key="node.advanceremindtime" />:</td>     
		    <td width="70%">
		    	<select name="advanceRemind" id="advanceRemind" class="input-100per" onchange="javascript:templeteCompareTime()">
		    		<v3x:metadataItem metadata="${comMetadata}" showType="option" name="advanceRemind" selected="${summary.advanceRemind}" />
		    	</select></td>
		</tr>
		<tr>
			<td colspan="2" class="separatorDIV" height="2"></td>
		</tr>
		<tr>
			<td colspan="2" style="padding-left: 20px">
			    <div style="height: 28px;">
			    	<label for="allow_transmit">
			    		<input type="checkbox" name="canForward" id="allow_transmit" ${v3x:outConditionExpression(summary.canForward, 'checked', '')}>
			    		<fmt:message key="collaboration.allow.transmit.label" />
			    	</label>
			    </div>
			    
			    <div style="height: 28px;">
			    	<label for="allow_chanage_flow">
			    		<input type="checkbox" name="canModify" id="allow_chanage_flow" ${v3x:outConditionExpression(summary.canModify, 'checked', '')}>
			    		<fmt:message key="collaboration.allow.chanage.flow.label" />
			    	</label>
			    </div>
			    
			    <div style="height: 28px;">
			    	<label for="allow_edit">
			    		<input type="checkbox" name="canEdit" id="allow_edit" ${v3x:outConditionExpression(summary.canEdit, 'checked', '')}>
			    		<fmt:message key="collaboration.allow.edit.label" />
			    	</label>
			    </div>
			    <div style="height: 28px;">
			    	<label for="allow_edit_attachment">
			    		<input type="checkbox" name="canEditAttachment" id="allow_edit_attachment" ${v3x:outConditionExpression(summary.canEditAttachment, 'checked', '')}>
			    		<fmt:message key="collaboration.allow.edit.attachment.label" />
			    	</label>
			    </div>
			    <div style="height: 28px;">
			    	<label for="allow_pipeonhole">
			    		<input type="checkbox" name="canArchive" id="allow_pipeonhole" ${v3x:outConditionExpression(summary.canArchive, 'checked', '')}>
			    		<fmt:message key="collaboration.allow.pipeonhole.label" />
			    	</label>
			    </div>	
			    
			    <div style="height: 28px;">
			    	<label for="allow_auto_stop_flow">
			    		<input type="checkbox" name="canAutoStopFlow" id="allow_auto_stop_flow" ${v3x:outConditionExpression(summary.canAutoStopFlow, 'checked', '')} ${(isFromTemplate == true || isForm == true) && nonTextTemplete && isSystemTemplete?'disabled' :'' }>
			    		<fmt:message key="collaboration.allow.autostopflow.label" />
			    	</label>
			    </div>		
			</td>
		</tr>
		<tr>
			<td colspan="2" class="separatorDIV" height="2"></td>
		</tr>
	    <tr>
			<td width="30%" class="bg-gray"><fmt:message key="template.number.label"/>:</td>
			<td width="70%"><input type="text" id="templeteNumber" name="templeteNumber" class="input-100per" value="${templete.templeteNumber}" maxlength="20">
			<input type="hidden" id="templeteId4Number" value="${templete.id}">
			</td>
		</tr>
		<tr>
			<td colspan="2" height="28" class="description-lable"><fmt:message key="template.number.description.label"/></td>
	    </tr>
 	</table>
</div>

</form>

<iframe name="personalTempleteIframe" scrolling="no" frameborder="0" height="0" width="0"></iframe>

<script type="text/javascript">
initProcessXml();
hiddenNoteArea();

<c:if test="${!empty summary.senderOpinion.content}">
changeLocation('senderNote');
showNoteArea();
</c:if>

function pigeonholeEvent(obj){
	var theForm = document.getElementsByName("sendForm")[0];
	switch(obj.selectedIndex){
		case 0 :
			var oldArchiveId = theForm.archiveId.value;
			if(oldArchiveId != ""){
				<%--
				//var selectObj = theForm.colPigeonhole;
				//var option = document.getElementById(oldArchiveId);
				//selectObj.options.length = selectObj.options.length-1;
				--%>
				theForm.archiveId.value = "";
			}
			break;
		case 1 : 
			doPigeonhole('new', '<%=ApplicationCategoryEnum.collaboration.key()%>', 'templete');
			break;
		default :
			theForm.archiveId.value = document.getElementById("prevArchiveId").value;
			return;
	}
}

</script>
</body>
</html>