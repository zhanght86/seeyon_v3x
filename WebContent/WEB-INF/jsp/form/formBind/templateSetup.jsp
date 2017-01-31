<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/collaboration" prefix="col"%>
<%@page import="com.seeyon.v3x.common.constants.ApplicationCategoryEnum" %>
<%@ page import="www.seeyon.com.v3x.form.controller.pageobject.Operation" %>
<%@ page import="www.seeyon.com.v3x.form.controller.pageobject.FormPage" %>
<%@ page import="www.seeyon.com.v3x.form.controller.pageobject.SessionObject" %>
<%@ page import="java.util.*" %>
<%@ include file="/WEB-INF/jsp/common/INC/noCache.jsp"%>
<%@ include file="/WEB-INF/jsp/form/formcreate/formHeader.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="/WEB-INF/jsp/collaboration/templete/header.jsp" %>
<%@ include file="/WEB-INF/jsp/doc/pigeonholeHeader.jsp" %>
<title><fmt:message key="form.bind.bindseeyon.label" /></title>
<script type="text/javascript">
var templateCodeAlertLabel = "<fmt:message key='template.number.alert.label'/>";
var templateCodeDupleLabel = "<fmt:message key='template.number.alert.duple'/>";
flowSecretLevel_wf = '${ summary.secretLevel != null  ? summary.secretLevel : 1}';
  var selectedElements = null;
  var hasDiagram = <c:out value="${hasWorkflow}" default="false" />;        
  var caseProcessXML = '${process_xml}';
  var caseLogXML = "";
  var caseWorkItemLogXML = "";
  var showMode = 1;
  var currentNodeId = null;
  var showHastenButton = "";
  var isTempleteEditor = true;
  var isTemplete = true;
  var appName = "collaboration";
  var isFormTemplate = true;
  var formAppName = "${formAppName}";                                 //表单应用名
  var formApp = "${formApp}";                                         //表单应用id
  var defaultForm = "${defaultFormId }";                                    //缺省单据id
  var defaultOtherNodeOperationId = "${defaultOtherNodeOperationId}";       //非首节点缺省操作id    
  var hiddenPostOfDepartment_auth = true;
  var isNeedCheckLevelScope_auth = false;
  var showAllOuterDepartment_auth = true;

  var editWorkFlowFlag = "true"
  var isUpdate = "${param.isUpdate}";
  hasWorkflow = <c:out value='${hasWorkflow}' default='false' />;
  
  var policys = null;
  var nodes = null;
  //发起节点显示当前用户名还是‘发起者’，用于表单绑定时显示用
  var showSenderName = true;
  var isTempleteFromMake = "true";

  function pigeonholeEventform(obj){
	var theForm = document.getElementsByName("sendForm")[0];
	var archiveFormIdTR = document.getElementById("archiveFormIdTR");
	switch(obj.selectedIndex){
		case 0 :
			var oldArchiveId = theForm.archiveId.value;
			if(oldArchiveId != ""){
				var selectObj = theForm.colPigeonhole;
				var option = document.getElementById(oldArchiveId);
				selectObj.options.length = selectObj.options.length-1;
			}
			theForm.archiveId.value = "";
			archiveFormIdTR.style.display = "none";
			theForm.archiverFormid.value = "";
			cleararchiveFormId();
			break;
		case 1 : 
			doFormPigeonhole('new', '<%=ApplicationCategoryEnum.collaboration.key()%>');
			break;
		default :
			return;
	}
}
  //分支
	var branchs = new Array();
	var keys = new Array();
	var hasKeys = true;
	<c:if test="${branchs != null}">
		<c:forEach items="${branchs}" var="branch" varStatus="status">
			var branch = new ColBranch();
			branch.id = ${branch.id};
			branch.conditionType = "${branch.conditionType}";
			branch.formCondition = "${fn:replace(branch.formCondition,"\"","\\\"")}";
			//branch.conditionTitle = "${fn:replace(v3x:escapeJavascript(branch.conditionTitle),"\"","\\\"")}";
			branch.conditionTitle =  "${v3x:escapeJavascript(branch.conditionTitle)}";
			branch.conditionDesc = "${v3x:escapeJavascript(branch.conditionDesc)}";
			branch.isForce = "${branch.isForce}";
			branch.conditionBase = "${branch.conditionBase}";
			eval("branchs["+${branch.linkId}+"]=branch");
			keys[${status.index}] = ${branch.linkId};
		</c:forEach>
	</c:if>
	var isNeedCheckLevelScope_auth = false;
	var templeteFrom = "SYS";
	
	function cancbind(){
	window.dialogArguments.isSaveAction = false;
	  window.close();
	  
	}
	
	//清空显示明细的值
	function cleararchiveFormId(){
	    var archiverFormid = document.getElementById("archiverFormid").value;
	     var showdetail = document.all("showdetail");
	      if(showdetail.length==undefined){
	         showdetail.checked = false;
	         var operationObj = document.all("operation");
	         var b =operationObj.getElementsByTagName("option");         
                 b[0].selected = true;						   
	      }else{
	         for(var i = 0; i < showdetail.length; i++){ 
			 	showdetail[i].checked = false;
			 	var operationObj = document.all("operation");
	            var b =operationObj[i].getElementsByTagName("option");         
                b[0].selected = true;		
			 }
	      }
	}
	
	function tmpleteload(){
	//debugger;
	    /*
	     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	     * 视图1.操作|视图2.操作  .......
	     * 对视图、操作进行赋值
	     */
	    var archiveId = document.getElementById("archiveId").value;
	    var archiveName =  "${archiveName}";
	    var archiveFormIdTR = document.getElementById("archiveFormIdTR");
	    if(archiveId !="" && archiveName !=""){
	        archiveFormIdTR.style.display = "";
	        var archiverFormid = document.getElementById("archiverFormid").value;
		    var showdetail = document.all("showdetail");
		    //当前表单为一个视图
		    if(showdetail.length==undefined){
				showdetail.checked = false;
				if(archiverFormid !=""){
				  if(archiverFormid.indexOf("|") !=-1){
			 		for(var a = 0;a<archiverFormid.split("|").length-1;a++){
			 		  if(archiverFormid.split("|")[a].split(".")[0] == showdetail.value)
					     showdetail.checked = true;
					     var operationObj = document.all("operation");
					     var b =operationObj.getElementsByTagName("option");      
							  for(var y =0;y<b.length ;y++){
							     var be= b[y].value;
								 if(be==archiverFormid.split("|")[a].split(".")[1]){
								   b[y].selected = true;
								 }
							  }
			 	    }
			 	 }else{
			 		if(archiverFormid.split(".")[0] == showdetail.value)
					     showdetail.checked = true;
					     var operationObj = document.all("operation");
					     var b =operationObj.getElementsByTagName("option");      
							  for(var y =0;y<b.length ;y++){
							     var be= b[y].value;
								 if(be==archiverFormid.split(".")[1]){
								   b[y].selected = true;
								 }
							  }
			 	 }
				}else{
				   showdetail.checked = true;
				}
			
			}
			//当前表单为多个视图
			else{
				for(var i = 0; i < showdetail.length; i++){ 
			 	showdetail[i].checked = false;
			 	 if(archiverFormid !=""){
			 	  if(archiverFormid.indexOf("|") !=-1){
			 		for(var a = 0;a<archiverFormid.split("|").length-1;a++){
			 		  if(archiverFormid.split("|")[a].split(".")[0] == showdetail[i].value)
					     showdetail[i].checked = true;
					   var operationObj = document.all("operation");
					   var b =operationObj[i].getElementsByTagName("option");      
							  for(var y =0;y<b.length ;y++){
							     var be= b[y].value;
								 if(be==archiverFormid.split("|")[a].split(".")[1]){
								   b[y].selected = true;
								 }
							  }
			 	    }
			 	  }else{
			 		if(archiverFormid.split(".")[0] == showdetail[i].value)
					     showdetail[i].checked = true;
					var operationObj = document.all("operation");
					var b =operationObj[i].getElementsByTagName("option");      
							  for(var y =0;y<b.length ;y++){
							     var be= b[y].value;
								 if(be==archiverFormid.split(".")[1]){
								   b[y].selected = true;
								 }
							  }
			 	  }
			 	}else{
			 	  showdetail[0].checked = true;
			 	}			 		
			 }
			 
			}
		    }

	}
	
	
//选择表单流程
function selectForm(){
	var currentId = document.getElementById("id").value;
	var rv = v3x.openWindow({
        url: templeteURL + "?method=selectQuoteForm&templeteId=" + currentId,
        height: 420,
        width: 400,
        scrollbars:"no",
		dialogType: "modal",
        resizable: "no"
    });
    
    if(!rv){
    	return;
    }
    if(rv.length == 5){
	    if(rv == "false"){
	        var valueObj = document.getElementById("quoteformtemId");
		    var nameObj = document.getElementById("quoteformtemName");
		    var quoteformtem = document.getElementById("quoteformtem");
		    nameObj.value = "";
			valueObj.value = "";
			quoteformtem.value = "";
	    }else{
          showValueHelper(rv[0], rv[1]);
        }
    }else{
      showValueHelper(rv[0], rv[1]);
    }
    
}



//回显值的辅助方法
function showValueHelper(nameObjValue, valueObjValue){
	var valueObj = document.getElementById("quoteformtemId");
	var nameObj = document.getElementById("quoteformtemName");
	var quoteformtem = document.getElementById("quoteformtem");
	if(nameObj){
		nameObj.value = nameObjValue;
		valueObj.value = valueObjValue;
		quoteformtem.value = nameObjValue;
	}
}
</script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/form/js/formBind/bind.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/office/js/office.js${v3x:resSuffix()}" />"></script>
</head>
<body bgcolor="#f6f6f6" scroll="no" onload="tmpleteload()">
<div class="scrollList">
<% 
	SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
	List<FormPage> formLst = sessionObject.getFormLst();
%>
<form name="sendForm" id="sendForm" method="post" action="${bindFormURL}?method=systemSaveTemplete" target="myFrame">
<div id="branchDiv" style='display:none'></div>
  <div style='display:none'>
    <input type='hidden' name='bodyType' id='bodyType' value='FORM'>
  </div>
  <input type="hidden" name="id" value="${templete.id}">
  <input type="hidden" id="process_desc_by"  name="process_desc_by" value="${process_desc_by}" />
  <input type="hidden" id="process_xml" name="process_xml" value="" />
  <input type="hidden" name="type" id="type" value="" />
  <input type="hidden" name="defaultFormApp" value="${formApp}" />
  <input type="hidden" name="defaultForm" value="${defaultFormId }" />
  <input type="hidden" name="defaultFirstNodeOperationId" id="defaultFirstNodeOperationId" value="${defaultFirstNodeOperationId }" />
  <input type="hidden" name="categoryType" value="${param.categoryType}" />
  <input type="hidden" name="archiveId" id="archiveId" value="${summary.archiveId}" />
  <input type="hidden" name="archiverFormid" id="archiverFormid" value="${summary.archiverFormid}" />
  <input type="hidden" name="workflowRule" id="workflowRule" value="<c:out value='${templete.workflowRule}' escapeXml='true' />" />
  <input type="hidden" name="categoryId" value="${categoryId }" />
  <input type="hidden" name="from" value="${param.from}" />
  <input type="hidden" name="from" value="${param.from}" />
  <input type="hidden" name="supervisorId" id="supervisorId" value="${colSupervisors }">
  <input type="hidden" name="supervisors" id="supervisors" value="${colSupervise.supervisors }">
  <input type="hidden" name="awakeDate" id="awakeDate" value="${colSupervise.templateDateTerminal }">
  <input type="hidden" name="superviseTitle" id="superviseTitle" value="${colSupervise.title }">
  <input type="hidden" name="superviseRole" id="superviseRole" value="${colSuperviseRole }">	
  <input type="hidden" name="superviseId" id="superviseId" value="${colSupervise.id }">
  <c:set value="${v3x:parseElements(templete.templeteAuths, 'authId', 'authType')}" var="authInfo" />
  <input type="hidden" name="authInfo" id="auth" value="${authInfo}" />
  <input type="hidden" name="authInfoChanged" id="authChange" value="false" />
  <input type="hidden" name="loginAccountId" id="loginAccountId" value="${v3x:currentUser().loginAccount}" >
  <div id="NewflowDIV" style="display: none">
	<c:forEach items="${newflowSettingList}" var="newflow" varStatus="status">
		<c:if test="${newflow.newflowSender ne 'CurrentNode' && newflow.newflowSender ne 'CurrentSender'}">
			<c:set value="${v3x:showOrgEntitiesOfTypeAndId(newflow.newflowSender, pageContext)}" var="showSenderName" />
		</c:if>
		<input type="hidden" id="NF${newflow.nodeId}" name="NewflowSettings" 
		value="${newflow.nodeId}@${newflow.newflowTempleteId}@${newflow.newflowSender}@${col:transformQuot(newflow.triggerCondition)}@${col:transformQuot(newflow.conditionTitle)}@${newflow.conditionBase}@${newflow.isForce}@${newflow.flowRelateType}@${newflow.isCanViewMainFlow}@${newflow.isCanViewByMainFlow}"
		TName="${formTempleteMap[newflow.newflowTempleteId]}" SenderName="${showSenderName}" />
	</c:forEach>
  </div>
  <span id="people" style="display:none;"></span>
  <v3x:selectPeople id="auth" panels="Account,Department,Team,Post,Level,Outworker" selectType="Account,Department,Team,Post,Level,Member" jsFunction="showAuth(elements)" minSize="0" originalElements="${authInfo}" />
  <v3x:selectPeople id="wf" panels="All" selectType="All" jsFunction="setPeopleFields(elements)" viewPage="selectNode4Workflow" />
  <table width="510" border="0" height="100%" cellpadding="0" cellspacing="0">
    <tr>
	<td class="bg-advance-middel">
	 <table width="98%" border="0" cellpadding="0" cellspacing="0" height="100%" class="ellipsis">
        <colgroup>
          <col style="width:28%"></col>
          <col style="width:36%"></col>
          <col style="width:36%"></col>
        </colgroup>
		<tr>
		  <td align="right"><fmt:message key="form.bind.formname.label" />：</td>
		  <td colspan="2" >${formAppName} </td>
		</tr>
		<tr>
		  <td align="right"><label><font color="red">*</font></label><fmt:message key="form.bind.flowtemplatename.label" />：</td>
		  <td colspan="2">
		   <input name="subject" type="text" id="subject" class="input-100" deaultValue="${defSubject}"
		    inputName="<fmt:message key="common.subject.label" bundle="${v3xCommonI18N}" />" 
		    validate="isDeaultValue,notNull,notSpecChar"
			value="<c:out value="${summary.subject}" escapeXml="true" default='${defSubject}' />" 
			${v3x:outConditionExpression(readOnly, 'readonly', '')}
			onfocus='checkDefSubject(this, true)' onblur="checkDefSubject(this, false)" maxlength="60">  
			<input type = "hidden" name = "oldtemplatename" value = "${summary.subject}">
		  </td>
	  
		</tr>
						
		<tr>
		  <td align="right"><label><font color="red">*</font></label><fmt:message key="form.bind.flow.label" />：</td>
		  <td >
		  <input type="text" id="workflowInfo" name="workflowInfo" class="input-100" value="<c:out value="${col:getWorkflowInfo(workflowInfo, colFlowPermPolicyMetadata, pageContext)}" default="${dfwf}" />" readonly>	 		
		  </td>
		  <td  align="">
		  <input type="button" onClick="selectPeople(true)" value='<fmt:message key="form.bind.new.label" />' 
			  class="button-style button-space"><input type="button" onClick="selectPeople(false)" value='<fmt:message key="form.bind.edit.label" />' 
			  class="button-style button-space">
		  </td>
		</tr>
				<tr>
			<td align="right"><font color="red"><fmt:message key="collaboration.secret.flowsecret"/>:</font></td>
			<td>
		    	<select name="secretLevel" id="secretLevel" style="width:180px" onchange="changeSecretLevel(this);">
		    	<option value="1" ${summary.secretLevel == 1 ? 'selected' :''}><fmt:message key="collaboration.secret.nosecret"/></option>
		    	<c:if test="${peopleSecretLevel >= 2 }">
		    	<option value="2" ${summary.secretLevel == 2 ? 'selected' :''}><fmt:message key="collaboration.secret.secret"/></option>
		    	</c:if>
		    	<c:if test="${peopleSecretLevel >= 3 }">
		    	<option value="3" ${summary.secretLevel == 3 ? 'selected' :''}><fmt:message key="collaboration.secret.secretmore"/></option>
		    	</c:if>
		   	    </select>
			</td>
		</tr>
		<tr>
		 <td align="right"><fmt:message key="form.bind.colSubject.label" />：</td>
		 <td>
		  <input type="text" name="colSubject" class="input-100" id="colSubject" readonly="readonly" validate="notSpecChar" 
		    inputName="<fmt:message key="form.bind.colSubject.label" />" 
		    value="<c:out value="${templete.collSubject}" escapeXml="true"  />" 
		  >
		 </td>		 
		 <td align=""><input type="button" value='<fmt:message key="form.bind.set.label" />' onClick="setSubject()" 
		   class="button-style button-space"></td>			
		</tr>		
		
		 <!-- <tr>
		  <td align="right"  width="25%"><fmt:message key="form.bind.templatecategory.label" />：</td>
		<td   colspan="2">
			<select name="categoryId" id="categoryId" class="input-100per">
				<option value="4"><fmt:message key="templete.category.type.${param.categoryType}" /></option>
			${categoryHTML}
		    </select>
		  </td>
		
		</tr> --> 
		<tr height="5px">
		  <td colspan="3"></td>
		</tr>
		
		<tr>
			<td align="right"  valign="top">
				<fmt:message key="common.mydocument.label" bundle="${v3xCommonI18N}" />：</td> 
			<td  valign="top" >
			  <div  id="attachment2Area"  class="scrollList" >
			  </div>
		    </td>
			<td align=""  valign="top">			
				<input type="button" value='<fmt:message key="common.mydocument.label" bundle="${v3xCommonI18N}" />'  onClick="quoteDocument('isBind')" 
				class="button-style button-space">			
			</td>
		</tr>	
		
		<tr height="5px">
		  <td colspan="3"></td>
		</tr>	
		
		<tr>
		  <td align="right" valign="top">
		   <fmt:message key='common.toolbar.insert.localfile.label' bundle='${v3xCommonI18N}' />：</td>
		  <td valign="top">
		    <div class="scrollList" id="attachmentTR">	
			    <v3x:fileUpload attachments="${attachments}" 
			    canDeleteOriginalAtts="${canDeleteOriginalAtts}" 
			    originalAttsNeedClone="${cloneOriginalAtts}" />	
		    </div>
		  </td>
		  <td align=""  valign="top">			
			  <input type="button" value='<fmt:message key="form.bind.upload.label" />'  onClick="insertAttachment()" 
			    class="button-style button-space">			
		  </td>
		</tr>
		
		<tr>
		  <td align="right">
		    <fmt:message key="common.importance.label" bundle='${v3xCommonI18N}' />：</td>
		  <td colspan="2">
		   <select name="importantLevel" class="input-100">
			 <v3x:metadataItem metadata="${comImportanceMetadata}" showType="option" 
			  name="importantLevel" selected="${summary.importantLevel}" />
		   </select>
		  </td>
		</tr>
		<tr>
		  <td align="right"><fmt:message key="project.label"/>：</td>
		  <td colspan="2">
		    <select name="projectId" class="input-100">
		  		<option value=""><fmt:message key='project.nothing.label' /></option>
    		    <c:forEach items="${relevancyProject}" var="project">
				  <option value="${project.id}" ${summary.projectId == project.id ? 'selected' : ''}>${v3x:toHTML(project.projectName) }</option>
			    </c:forEach>
			</select>
		  </td>
		</tr>
		<tr>
		  <td align="right"><fmt:message key="process.cycle.label"/>：</td>
		  <td colspan="2">
		    <select name="deadline" id="deadline" class="input-100">
			  <v3x:metadataItem metadata="${colMetadata['collaboration_deadline']}" 
			    showType="option" name="deadline" selected="${summary.deadline}" />
			</select>
		  </td>
		</tr>
		<tr>
		  <td align="right"><fmt:message key="common.reference.time.label" bundle="${v3xCommonI18N }"/>：</td>
		  <td colspan="2">
		    <select name="referenceTime" class="input-100">
			  <v3x:metadataItem metadata="${colMetadata['collaboration_deadline']}" 
			    showType="option" name="referenceTime1" selected="${templete.standardDuration}" />
			</select>
		  </td>
		</tr>
        <tr>
		  <td align="right"><fmt:message key="common.remind.time.label" bundle='${v3xCommonI18N}' />：</td>
		  <td colspan="2">
		     <select name="advanceRemind" id="advanceRemind" class="input-100per" onchange="javascript:compareTime()">
    		    <v3x:metadataItem metadata="${comMetadata}" showType="option" name="advanceRemind" selected="${summary.advanceRemind}" />
    	     </select>
		  </td>	 
		</tr> 
		<tr>
		  <td align="right"><fmt:message key="prep-pigeonhole.label" />：</td>
		  <td colspan="2">
		   <%--  <c:if test="${empty archiveName}">
	    	  <c:set value="${v3x:_(pageContext, 'click.choice')}" var="archiveName" />
	        </c:if>
    	    <input name="archiveName" class="input-100 cursor-hand" readonly value="${archiveName }" 
    	           onclick="doFormPigeonhole('new', '<%=ApplicationCategoryEnum.collaboration.key()%>')">
			--%>
			<select id="colPigeonhole" class="input-100per" onchange="pigeonholeEventform(this)">
				<option id="defaultOption" value="1"><fmt:message key="common.default" bundle="${v3xCommonI18N}"/></option>   
				<option id="modifyOption" value="2">${v3x:_(pageContext, 'click.choice')}</option>
				<c:if  test="${!empty archiveName}" >
					<option id="${summary.archiveId}" value="3" selected>${archiveName}</option>
				</c:if>
	        </select>
		  </td>
		  
		</tr>
		<tr id="archiveFormIdTR" style="display:none;">
		  	
		  <td align="right" valign="top" style="padding-top: 6px;"><fmt:message key="form.bind.showdetails.label"/>：</td>
		 
		  <td colspan="2" height="8%">
		    <div class="scrollList" id="archiveFormIdTR1">	
			 	    <%
						for(FormPage form : formLst){
							//for(Operation operation : (List<Operation>)form.getOperlst()){
                                  
					  %>
					  <input type="checkbox" id="showdetail<%=form.getFormPageId()%>" name="showdetail" value="<%=form.getFormPageId()%>" defaultvalue="<%=form.getName()%>"/>
					  <label for="showdetail<%=form.getFormPageId()%>">
					    <%out.print(form.getName());%>
						&nbsp;	
					  </label>	  
						<select id="operation" class="input-245" onchange="">
						<%
						for(int i=0 ;i<form.getOperlst().size() ; i++){
						 Operation operation = (Operation)form.getOperlst().get(i);
						
						%>
				              <option id="operationid<%=i %>" value="<%= operation.getOperationId()%>"><%out.print(operation.getName());%></option>   
				        <%
							
					        }
					    %>
	                    </select>	<br>
							
					  <%
							
					        }
					  %>
		    </div>
		  </td>	
		</tr>
		<tr>
		  <td>&nbsp;</td>
		  <td colspan="2">
		    <fieldset>
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
			  <tr>
				<td>
				 <label for="allow_transmit">
				  <input type="checkbox" name="canForward" 
				    id="allow_transmit" ${v3x:outConditionExpression(summary.canForward, 'checked', '')}>
				    <fmt:message key="collaboration.allow.transmit.label" />
				 </label>   
				</td>
				<td>
				 <label for="allow_chanage_flow">
				  <input type="checkbox" name="canModify" 
				   id="allow_chanage_flow" ${v3x:outConditionExpression(summary.canModify, 'checked', '')}>
				    <fmt:message key="collaboration.allow.chanage.flow.label" />
				 </label>     
				</td>
				<!-- td>
				<input type="checkbox" name="canEdit" 
				id="allow_edit" ${v3x:outConditionExpression(summary.canEdit, 'checked', '')}>
				  <fmt:message key="collaboration.allow.edit.label" />
				</td>
			  </tr>
			  <tr>
			  -->
				<td>
				 <label for="allow_pipeonhole" title="<fmt:message key='collaboration.allow.pipeonhole.label' />">
				   <input type="checkbox" name="canArchive"  
				    id="allow_pipeonhole" ${v3x:outConditionExpression(summary.canArchive, 'checked', '')}>
				    <fmt:message key="collaboration.allow.pipeonhole.label" />
				 </label>   
				</td>
			  </tr>
			  <tr>
				 <td>
				    <div style="height: 28px;">
				    	<label for="allow_edit_attachment">
				    		<input type="checkbox" name="canEditAttachment" id="allow_edit_attachment" ${v3x:outConditionExpression(summary.canEditAttachment, 'checked', '')}>
				    		<fmt:message key="collaboration.allow.edit.attachment.label" />
				    	</label>
				    </div>
				</td>
			    <td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			</table>
			</fieldset></td>
		</tr>
		
	    <tr>
			<td align="right"><fmt:message key="template.number.label"/>：</td>
			<td><input type="text" id="templeteNumber" name="templeteNumber" class="input-100per" value="${templete.templeteNumber}" maxlength="20">
			<input type="hidden" name="templeteId4Number" id="templeteId4Number" value="${templete.id}">
			<input type="hidden" name="oldtempleteNumber" id="oldtempleteNumber" value="${templete.templeteNumber}">
			</td>
		</tr>
		
		<tr>
			<td align="right"></td>
			<td width="500" colspan="2" height="28" class="description-lable" style="word-break:break-all;"><fmt:message key="template.number.description.label"/></td>
	    </tr>
	    <tr style="display: none"><!-- 为不影响其他逻辑，暂时隐藏关联表单设置而不直接删除 -->
		    <td align="right"><fmt:message key="toolbar.insert.form.label"/>：</td>
		    <td><input type="text" id="quoteformtem" name="quoteformtem" value="${quoteformtemName}"  class="input-100per" readonly="readonly">
				<input id="quoteformtemId" name="quoteformtemId" type="hidden" value="${quoteformtemId}"/>
				<input id="quoteformtemName" name="quoteformtemName" type="hidden" value="${quoteformtemName}"/></td>
			<td align=""><input type="button" value='<fmt:message key="form.bind.set.label" />' onClick="selectForm()" 
		   class="button-style button-space"></td>
		</tr>
		<tr  style="display: none"><!-- 为不影响其他逻辑，暂时隐藏关联表单设置而不直接删除 -->
			<td align="right"></td>
			<td width="500" colspan="2" height="28" class="description-lable" style="word-break:break-all;"><fmt:message key="form.bind.quote.label"/></td>
	    </tr>
		<tr>
		  <td align="right">
		  <fmt:message key="col.supervise.staff" />：</td>
		  <td>
		  <input type="text" id="supervisorNames" name="supervisorNames" value="${supervisorNames }" class="input-100" readonly="readonly">			
		 </td>
		   <td align=""><input type="button" value='<fmt:message key="form.bind.set.label" />' onClick="openSuperviseWindowForTemplate()" 
		   class="button-style button-space"></td>
		</tr>	
		<tr>
		  <td align="right">
		  <fmt:message key='common.toolbar.auth.label' bundle='${v3xCommonI18N}' />：</td>
		  <td>
		  <input type="text" id="authName" name="authName" value="${authName }" class="input-100" readonly="readonly">			
		 </td>
		   <td align=""><input type="button" value='<fmt:message key="form.bind.set.label" />' onClick="selectPeopleFun_auth()" 
		   class="button-style button-space"></td>
		</tr>			
	   </table>
	  </td>
	</tr>
	<tr align="right" class="bg-advance-bottom" height="42">
	  <td>
		<input type="button" name = "saveFormTemp" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" 
		  onClick="saveFormTemplate('templete')" class="button-style">       
		<input type="button" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />"  
		  class="button-style button-space" onClick="cancbind()">
	  </td>
	</tr>		
  </table>
</form>
<script type="text/javascript">
  initProcessXml();
</script>
<div style="display:none">
  <iframe id="myFrame" name="myFrame"></iframe>
</div>
</div>
</body>
</html>