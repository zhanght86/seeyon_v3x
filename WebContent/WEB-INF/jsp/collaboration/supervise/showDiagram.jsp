<%@ page import="com.seeyon.v3x.workflow.event.WorkflowEventListener" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@page import="com.seeyon.v3x.exchange.util.Constants"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="../Collaborationheader.jsp" %>
<%@ include file="../../doc/pigeonholeHeader.jsp" %>
<fmt:setBundle basename="com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources" var="v3xWorkflowAnalysisI18N"/>
<title>
<c:choose>
	<c:when test="${param.pageFlag == 'timeout' or param.pageFlag == 'efficiency'}">
		<fmt:message key="common.view.the.processflow.label" bundle="${v3xWorkflowAnalysisI18N}" />
	</c:when>
	<c:otherwise>
		<fmt:message key="col.supervise.label" />
	</c:otherwise>
</c:choose>
</title>
<%@page import="com.seeyon.v3x.common.constants.ApplicationCategoryEnum" %>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/v3xmain/js/phrase.js${v3x:resSuffix()}" />"></script>
<c:set value="${param.from eq 'Pending'}" var="hasSignButton"/>
<v3x:selectPeople id="flash" panels="Department,Team,Outworker" selectType="Department,Team,Member"
                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
                  jsFunction="monitorFrame.dataToFlash(elements)" viewPage="selectNode4Workflow"/>
                  
<fmt:message key='common.toolbar.print.label' bundle='${v3xCommonI18N}' var="printLabel"/>
<fmt:message key="common.attribute.label" bundle="${v3xCommonI18N}" var="attributeLabel" />
                  
<script type="text/javascript">
<!--
var hasDiagram = <c:out value="${hasWorkflow}" default="false" />;
var currentNodeId = "${currentNodeId}";
var showMode = 0;
var showHastenButton = "false";
var isNewCollaboration = "false";
var isTemplete = false;
var isCheckTemplete = false;
var editWorkFlowFlag = "true";
var processing= false;
var templeteCategrory="${templeteCategrory}";
var hasWorkflow = "${hasWorkflow}";
var process_desc_by = "${process_desc_by}";
var appName="${appName}";
var defaultPermName="<fmt:message key='${defaultPermLabel}' bundle='${v3xCommonI18N}'/>";
var actorId="${actorId}";
var policys = null;
var nodes = null;
var isFromEdoc = true;
var isFromSupervies = true;
var caseId = "${caseId }";
var processId = "${processId }";
var summaryId = "${summary.id}";
var bodyType = "${summary.bodyType}";

//异步加载流程信息
var isLoadProcessXML = false;
var caseProcessXML = "";
var caseLogXML = "";
var caseWorkItemLogXML = "";
var colCheckAndupdateLock = false;

function initCaseProcessXML(){
	if(isLoadProcessXML == false){
		try {
			var requestCaller = new XMLHttpRequestCaller(null, "ajaxColManager", "getXML", false, "POST");
			requestCaller.addParameter(1, "String", caseId);
			requestCaller.addParameter(2, "String", processId);
			var processXMLs = requestCaller.serviceRequest();
			
			if(processXMLs){
				caseProcessXML = processXMLs[0];
				caseLogXML = processXMLs[1];
				caseWorkItemLogXML = processXMLs[2];
				document.getElementById("process_xml").value = caseProcessXML;
				document.getElementById("process_desc_by").value = "xml";
			}
		}
		catch (ex1) {
		}
		isLoadProcessXML = true;
	}
}

//分支
var branchs = new Array();
<c:if test="${branchs != null}">
	var handworkCondition = _('collaborationLang.handworkCondition');
	<c:forEach items="${branchs}" var="branch" varStatus="status">
		var branch = new ColBranch();
		branch.id = ${branch.id};
		branch.conditionType = "${branch.conditionType}";
		branch.formCondition = "${v3x:escapeJavascript(branch.formCondition)}";
		branch.conditionTitle = "${v3x:escapeJavascript(branch.conditionTitle)}";
		branch.conditionDesc = "${v3x:escapeJavascript(branch.conditionDesc)}";
		branch.isForce = "${branch.isForce}";
		eval("branchs["+${branch.linkId}+"]=branch");
	</c:forEach>
</c:if>

var panels = new ArrayList();
<c:if test="${hasSignButton == true}">
panels.add(new Panel("sign", '<fmt:message key="sign.label" />', "showPrecessArea()"));
</c:if>
panels.add(new Panel("workflow", '<fmt:message key="workflow.label" />', "showPrecessArea()"));

//-->
</script>
</head>
<body scroll="no">
<form name="sendForm" id="sendForm" method="post">
<input type="hidden" name="startMemberId" value="${summary.startMemberId}"/>
<input type="hidden" name="appName" value='<%=ApplicationCategoryEnum.collaboration.getKey()%>'/>
<input type="hidden" name="policy" value="${nodePermissionPolicyKey}"/>
<input type="hidden" name="caseProcessXML" id="caseProcessXML" value="">
<input type="hidden" name="caseLogXML" id="caseLogXML" value="">
<input type="hidden" name="caseWorkItemLogXML" id="caseWorkItemLogXML" value="">
<input type="hidden" name="process_desc_by" id="process_desc_by" value="${process_desc_by}" />
<input type="hidden" name="process_xml" id="process_xml" value="" />
<input type="hidden" name="actorId" value="${actorId}"/> 
<input type="hidden" name="activityId" id="activityId" value="">
<input type="hidden" name="processId" id="processId" value="${summary.processId }">
<input type="hidden" name="caseId" id="caseId" value="${summary.caseId }">
<input type="hidden" name="workflowInfo" class="input-100per cursor-hand">
<input type="hidden" name="operationType" value="" >

<script type="text/javascript">
	document.all.caseProcessXML.value = caseProcessXML;
	document.all.caseLogXML.value = caseLogXML;
	document.all.caseWorkItemLogXML.value = caseWorkItemLogXML;
	
	//判断流程是否已经被锁定，未被锁定则给该流程加上一个同步锁，为接下来的修改流程做准备
	colCheckAndupdateLock = checkModifyingProcessAndLock(processId, "${summary.id}");
</script>


<span id="selectPeoplePanel"></span>

<div class="hidden">
<table>
  <tr class="bg-summary lest-shadow">
    <td width="8%" height="29" class="bg-gray"><fmt:message key="workflow.label" /></td>
    <td>  
    <fmt:message key="${selfCreateFlow?'default.workflowInfo.value':'alert_notcreateflow_loadtemplate'}" var="dfwf" /><c:set value="${col:getWorkflowInfo(workflowInfo, flowPermPolicyMetadata, pageContext)}" var="wfInfo" />      
        <input name="workflowInfo" class="input-100per cursor-hand" readonly value="<c:out value="${wfInfo}" default="${dfwf}" />" onClick="doWorkFlow('new')" ${(isFromTemplate == true || selfCreateFlow==false) ? 'disabled' : ''}></td>
    <td width="8%" nowrap="nowrap" class="bg-gray"><fmt:message key="process.cycle.label"/></td>     
    <td width="10%">
    	<select name="deadline" id="deadline" class="input-100per" onChange="javascript:compareTime(this)">
    	<v3x:metadataItem metadata="${deadlineMetadata}" showType="option" name="deadline" selected="${formModel.deadline}" bundle="${colI18N}"/>
    	</select>    </td>
    <td width="8%" class="bg-gray"><fmt:message key="common.remind.time.label" bundle='${v3xCommonI18N}' /></td>
    <td width="10%">
    	<select name="advanceRemind" id="advanceRemind" class="input-100per" onChange="javascript:compareTime(this)">
    	<v3x:metadataItem metadata="${remindMetadata}" showType="option" name="deadline" selected="${formModel.edocSummary.advanceRemind}"  bundle="${v3xCommonI18N}"/>
    	</select>    </td>
    <td align="center"></td>
  </tr>
</table>
</div>

<table width="100%" id="signAreaTable" height="100%" border="0" cellspacing="0" cellpadding="0" class="sign-bg">
	<tr id="workflowTR">
	    <td colspan="3">
	    	<!-- 成发集团项目 -->
	        <iframe src="<html:link renderURL='/genericController.do?ViewPage=collaboration/monitor&showColAssign=false&comm=toxml&isFromSupervise=true&fromList=${param.fromList}&affairId=${affairId}&isTempleteSupervise=${isFromTemplete}&isSupervise=${isSupervise}&secretLevel=${secretLevel }' />" name="monitorFrame"
	                frameborder="0" marginheight="0" marginwidth="0" height="100%" width="100%" scrolling="auto"></iframe>
	    </td>
	</tr>		
	<tr id="senderNoteTR" style="display:none;">
		<td class="note-textarea-td">
			<input type="hidden" name="policy" value="${policy}">
			<textarea cols="" rows="" name="note" validate="maxLength" inputName="<fmt:message key='sender.note.label' />" maxSize="200" class="note-textarea wordbreak"><c:out value='${formModel.senderOpinion.content}' escapeXml='true' /></textarea>
		</td>
	</tr>
	<tr id="colQueryTR" style="display:none;">
		<td>&nbsp;</td>
	</tr>	
</table>

</form>
</body>
</html>