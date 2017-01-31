<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<%@ include file="../common/INC/noCache.jsp"%>
<%@ include file="Collaborationheader.jsp"%>

<html:link renderURL='/edocController.do' var="edoc" />
<html:link renderURL="/collaboration.do" var="col" />

<link href="<c:url value="/apps_res/collaboration/css/collaboration.css${v3x:resSuffix()}" />" type="text/css" rel="stylesheet">
<title><fmt:message key="col.metadata_item.assign"/></title>

<script type="text/javascript">

var colInsertPeopleUrl = "${col}";
var edocInsertPeopleUrl = "${edoc}";

	var showAccountShortname_colAssign = "yes";
	var hiddenFlowTypeRadio_colAssign = true;
	var showOriginalElement_colAssign = false;
	var unallowedSelectEmptyGroup_colAssign = true;
	var hiddenRootAccount_colAssign = true;
	var isConfirmExcludeSubDepartment_colAssign = true;
	var flowSecretLevel_colAssign = "${secretLevel}";
	
function ok(){
	var selObjStr=document.getElementById("people").innerHTML;
	if(selObjStr=="")
	{  
	  alert(_("collaborationLang.alert_select_person"));
	  return;
	}
	v3x.getParentWindow().workflowUpdate = true ;
	var form = document.getElementsByName("insertPeopleForm")[0];

    var processId = form.processId.value;
	
    var itemName = '${nodePolicy.category}';
    var policyId = '${nodePolicy.name}';
    var policyName ='${nodePolicy.label}';
    var summaryId = form.summaryId.value;
    var affairId = form.affairId.value;
	var processMode;
	try { getA8Top().startProc(''); }catch (e) { }
	var from = "${from == 'edoc'}";
	var _genericURL = null;
	if(from == "true"){	
		v3x.getParentWindow().workflowUpdate = true ;
		form.action = edocInsertPeopleUrl+"?method=colAssign&summary_id="+encodeURIComponent(summaryId)+"&affairId="+encodeURIComponent(affairId)
		+"&itemName="+encodeURIComponent(itemName)+"&policyId="+encodeURIComponent(policyId)
		+"&flowcomm=col&policyName="+encodeURIComponent(policyName)+"&processId="+encodeURIComponent(processId);
	}else{
		v3x.getParentWindow().parent.detailMainFrame.workflowUpdate = true ;
		form.action = colInsertPeopleUrl+"?method=colAssign&summary_id="+encodeURIComponent(summaryId)+"&affairId="+encodeURIComponent(affairId)
		+"&itemName="+encodeURIComponent(itemName)+"&policyId="+encodeURIComponent(policyId)
		+"&policyName="+encodeURIComponent(policyName)+"&processId="+encodeURIComponent(processId);
	}

    document.getElementById("b1").disabled = true;
    document.getElementById("b2").disabled = true;
    
    form.submit();
}

</script>
<c:choose>
	<c:when test="${'edoc' eq from }">
		<v3x:selectPeople id="colAssign" panels="Department,Team,Post,RelatePeople" selectType="Department,Team,Member,Post,Account"
	                  departmentId="${v3x:currentUser().departmentId}"
	                  jsFunction="setPeopleInsert(elements)"  minSize="1" />
	</c:when>
	<c:otherwise>
		<v3x:selectPeople id="colAssign" panels="Department,Team,Post,Outworker,RelatePeople" selectType="Department,Team,Member,Post,Account"
	                  departmentId="${v3x:currentUser().departmentId}"
	                  jsFunction="setPeopleInsert(elements)"  minSize="1" />
	</c:otherwise>
</c:choose>
</head>
<body scroll="no" onkeypress="listenerKeyESC()">
<form name="insertPeopleForm" action="" target="inserPeopleIframe" method="post" >
<input type="hidden" name="summaryId" id="summaryId" value="${summaryId}">
<input type="hidden" name="affairId" id="affairId" value="${affairId}">
<input type="hidden" name="processId" id="processId" value="${processId}">
<input type="hidden" name="process_desc_by" id="process_desc_by" value="people">
<input type="hidden" name="appName" id="appName" value="${appName}">
<input type="hidden" id="currentLoginAccountId" name="currentLoginAccountId" value="${v3x:currentUser().loginAccount}">

<span id="people" style="display:none;"></span>
<table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" class="PopupTitle"><fmt:message key="col.metadata_item.assign"/></td>
	</tr>
	<tr>
		<td class="bg-advance-middel">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td height="32" nowrap="nowrap" align="right" width="25%">
					<fmt:message key="selectPolicy.please.select" />:&nbsp;
				</td>
				<td width="56%">	
					<input type="text" name="policy" class="input-100per" 
					 <c:if test="${nodePolicy.type == 0}">
					 	value="<fmt:message key="${nodePolicy.label}"  bundle="${v3xCommonI18N}"/>"
					 </c:if>
					 <c:if test="${nodePolicy.type == 1}"> value="${nodePolicy.name}"</c:if>
					 readonly="readonly" disabled="disabled">					
		    	</td>
		    	<td align="center">
    			<div class="like-a"  onClick="${param.edoc=='edoc'?'showEdocNodeDescription':'showNodeDescription'}()" style="text-decoration:none">
    				[<fmt:message key="node.policy.explain"/>]
    			</div> 
	    		</td>
	    	</tr>
			
			<tr>
				<td height="32" nowrap="nowrap" align="right">
					<fmt:message key="select.excutive.people.lable"/>:&nbsp;
				</td>
				<td align="left"><input id="workflowInfo" name="workflowInfo" class="input-100per cursor-hand" readonly value="<<fmt:message key='urger.alt' />>" onclick="selectPeopleFun_colAssign()"></td>
		  		<td></td>
		  	</tr>			
			
			<tr id="nodeProcessMode" class="hidden">			
				<td height="32" nowrap="nowrap" align="right">					
					<fmt:message key="node.process.mode"  bundle="${v3xCommonI18N}"/>:&nbsp;
				</td>
				<td>
							<label for="all_mode">
								<input type="radio" name="node_process_mode" id="all_mode" value="all"  checked>
								<fmt:message key="node.all.mode" bundle="${v3xCommonI18N}"/>
							</label>
							<label for="competition_mode">	
								<input type="radio" name="node_process_mode" id="competition_mode" value="competition">
								<fmt:message key="node.competition.mode" bundle="${v3xCommonI18N}"/>
							</label>
					</td>
				</tr>					
			
		</table>
		</td>
	</tr>
	<tr>
		<td height="42" align="right" class="bg-advance-bottom">
			<input type="button" name="b1" id="b1" onclick="ok()" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">&nbsp;&nbsp;
			<input type="button" name="b2" id="b2" onclick="window.close()" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
		</td>
	</tr>
</table>

</form>
<iframe src="" name="inserPeopleIframe" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
</body>
</html>