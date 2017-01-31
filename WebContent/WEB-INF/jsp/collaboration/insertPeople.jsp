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
<title><fmt:message key="insertPeople.label"/></title>

<script type="text/javascript">

var colInsertPeopleUrl = "${col}";
var edocInsertPeopleUrl = "${edoc}";

var desArr = new Array();
<c:forEach items='${desList}' var="desStr" varStatus="status">
	desArr[${status.index}] = '${desStr}'; 
</c:forEach>
window.onload = function(){
	var description = "";
	var policyId = "";
	for(var i=0; i<desArr.length; i++){
		var policyAndDes = desArr[i].split("split");
		var _description = policyAndDes[1];
		var _policyId = policyAndDes[0];
		if(_policyId == '${policyId}'){
			description = _description;
		}
	}
	document.getElementById("content").value = description;
}

function setAppOnload()
{
	var isColl = "${appName == 'collaboration'}";
	if(isColl != "true"){
		onlyLoginAccount_insertPeople=true;
	}
	<%--发文收文签报--%>
	var appName ='${appName}';
	if(appName == '0' || appName == '1' || appName == '2'){ 
		onlyLoginAccount_insertPeople=false;
	}
}

function ok(){
	var selObjStr=document.getElementById("people").innerHTML;
	if(selObjStr=="")
	{  
	  alert(_("collaborationLang.alert_select_person"));
	  return;
	}
	v3x.getParentWindow().parent.detailMainFrame.workflowUpdate = true ;
	var form = document.getElementsByName("insertPeopleForm")[0];

    var processId = form.processId.value;
	var policyOption = form.policy.options[form.policy.selectedIndex];
    var itemName = policyOption.getAttribute("itemName");
    var policyId = policyOption.value;
    var policyName = policyOption.text;
    var summaryId = form.summaryId.value;
    var affairId = form.affairId.value;
	var process_mode = form.process_mode;
	var processMode;
    for(var i=0; i<process_mode.length; i++){
		if(process_mode[i].checked){
			processMode = process_mode[i].value;
		}
	}
	try { getA8Top().startProc(''); }catch (e) { }
	var isColl = "${appName == 'collaboration'}";
	var _genericURL = null;
	_genericURL = colInsertPeopleUrl;
	form.action = _genericURL+"?method=insertPeople&summaryId="+encodeURIComponent(summaryId)+"&affairId="+encodeURIComponent(affairId)
		+"&itemName="+encodeURIComponent(itemName)+"&processMode="+encodeURIComponent(processMode)+"&policyId="+encodeURIComponent(policyId)
		+"&policyName="+encodeURIComponent(policyName)+"&processId="+encodeURIComponent(processId)+"&appName="+encodeURIComponent('${appName}');
    
    document.getElementById("b1").disabled = true;
    document.getElementById("b2").disabled = true;
    
    form.submit();
    // 流程是否改变全局变量 
    v3x.getParentWindow().workflowUpdate = true ;
}

var isConfirmExcludeSubDepartment_insertPeople = true;
var unallowedSelectEmptyGroup_insertPeople = true;
var hiddenRootAccount_insertPeople = true;
var flowSecretLevel_insertPeople = "${secretLevel}";
function enAbleButton(){
	document.getElementById("b1").disabled = false;
    document.getElementById("b2").disabled = false;
}

function policyChange(poli){
	if(poli == "inform" || poli =="zhihui"){
		document.getElementById("processMode_parallel").checked = true;
		//document.getElementById("processMode_serial").disabled = true;
		//document.getElementById("processMode_nextparallel").disabled = true;
		if(document.getElementById("nodeProcessMode") && document.getElementById("nodeProcessMode").className == ""){
			document.getElementById("nodeProcessMode").className = "hidden";
		}
		document.getElementById("all_mode").checked = true;	
		if(document.getElementById("formOperationPolicy1")){
			document.getElementById("formOperationPolicy1").disabled = true;
		}
		if(document.getElementById("formOperationPolicy2")){
			document.getElementById("formOperationPolicy2").checked = true;
		}
	}
	else{
		if(document.getElementById("selectUserType").value!='Member'){
			if(document.getElementById("selectUserType").value && document.getElementById("nodeProcessMode") && document.getElementById("nodeProcessMode").className == "hidden"){
				document.getElementById("nodeProcessMode").className = "";
			}
			document.getElementById("processMode_serial").disabled = false;
			document.getElementById("processMode_nextparallel").disabled = false;
			/*if(document.getElementById("formOperationPolicy1")){ //只有“同当前节点”选项存在时，才需这样处理，如果“同当前节点”选项不存在时，默认还是要选中“只读”
				document.getElementById("formOperationPolicy1").disabled = false;
				document.getElementById("formOperationPolicy1").checked = true;
				if(document.getElementById("formOperationPolicy2")){
					document.getElementById("formOperationPolicy2").checked = false;
				}
			}*/
		}
		if(document.getElementById("formOperationPolicy1")){ //只有“同当前节点”选项存在时，才需这样处理，如果“同当前节点”选项不存在时，默认还是要选中“只读”
			document.getElementById("formOperationPolicy1").disabled = false;
			document.getElementById("formOperationPolicy1").checked = true;
			if(document.getElementById("formOperationPolicy2")){
				document.getElementById("formOperationPolicy2").checked = false;
			}
		}
	}
}
</script>
<v3x:selectPeople id="insertPeople" panels="${appName eq 'collaboration'?'Department,Team,Post,Outworker':'Department,Team,Post'},RelatePeople" selectType="Department,Team,Post,Member"
                  jsFunction="setPeopleInsert(elements)" minSize="1" />
</head>
<body scroll="no" onkeypress="listenerKeyESC()" onload="setAppOnload()">
<form name="insertPeopleForm" action="" target="inserPeopleIframe" method="post" >
<input type="hidden" name="summaryId" value="${summaryId}">
<input type="hidden" name="affairId" value="${affairId}">
<input type="hidden" name="processId" value="${processId}">
<input type="hidden" id="selectUserType" name="selectUserType" value="">
<input type="hidden" name="process_desc_by" value="people">
<input type="hidden" id="currentLoginAccountId" name="currentLoginAccountId" value="${v3x:currentUser().loginAccount}">

<span id="people" style="display:none;"></span>
<table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#BADCE8">
	<tr>
		<td height="20" class="PopupTitle"><fmt:message key="insertPeople.label"/></td>
	</tr>
	<tr>
		<td class="bg-advance-middel">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td height="32" nowrap="nowrap" align="right" width="25%">
					<fmt:message key="selectPolicy.please.select" />:&nbsp;
				</td>
				<td width="56%">						
				<select name="policy" class="input-100per" onchange="policyChange(this.value)">
		    		<c:forEach items='${nodePolicyList}' var="nodePolicy">				        	
			        	<option value="${nodePolicy.name}" itemName="${nodePolicy.category}" ${defaultPolicyId==nodePolicy.name || defaultPolicyId==nodePolicy.id?"selected":"" }>
				        	<c:if test="${nodePolicy.type == 0}">
				        		<fmt:message key="${nodePolicy.label}"  bundle="${v3xCommonI18N}"/>				        		
				        	</c:if>
			        		<c:if test="${nodePolicy.type == 1}">${nodePolicy.name}</c:if>
			        	</option>
			        </c:forEach>
			        <c:if test="${param.appName == 'collaboration'}">
			        	<v3x:metadataItem metadata="${formAuditPolicy}" showType="option" name="policy"  selected="${policyId}" bundle="${v3xNodePolicyI18N}"/>				        
			        </c:if>		
		    	</select>
		    	</td>
		    	<td align="center">
    			<div class="like-a"  onClick="${param.edoc=='edoc'?'showEdocNodeDescription':'showNodeDescription'}()" style="text-decoration:none">
    				[<fmt:message key="node.policy.explain"/>]
    			</div> 
	    		</td>
	    	</tr>
			<c:if test="${param.isForm eq true}">
			<tr height="32">
				<td align="right">
					<fmt:message key="form.operation.permission" />:&nbsp;
				</td>
				<td colspan="2">
					<c:if test="${isFormReadonly ne true}">
						<label for="formOperationPolicy1">
							<input type="radio" id="formOperationPolicy1" name="formOperationPolicy" value="0"><fmt:message key="operation.permission.sameToCurrentNode" />
						</label>
					</c:if>
					<label for="formOperationPolicy2">
						<input id="formOperationPolicy2" type="radio" name="formOperationPolicy" value="1" checked><fmt:message key="common.readonly" bundle="${v3xCommonI18N}" />
					</label>
				</td>
			</tr>
			</c:if>
			<tr>
				<td height="32" nowrap="nowrap" align="right">
					<fmt:message key="select.excutive.people.lable"/>:&nbsp;
				</td>
				<td align="left"><input id="workflowInfo" name="workflowInfo" class="input-100per cursor-hand" readonly value="<<fmt:message key='urger.alt' />>" onclick="selectPeopleFun_insertPeople()"></td>
		  		<td></td>
		  	</tr>
		  	<tr>
		  		<td height="32" nowrap="nowrap" align="right">
					<fmt:message key="process.mode.label"/>:&nbsp;
				</td>
				<td colspan="2" align="left">
					<label for="processMode_serial">
						<input type="radio" name="process_mode" id="processMode_serial" value="0"><fmt:message key="flow.type.serial"/>
					</label>
					<label for="processMode_parallel">
						<input type="radio" name="process_mode" id="processMode_parallel" value="1" checked><fmt:message key="flow.type.parallel"/>
					</label>
					<label for="processMode_nextparallel">
						<input type="radio" name="process_mode" id="processMode_nextparallel" value="4"><fmt:message key="flow.type.nextparallel"/>
					</label>
				</td>
			</tr>
			
			
			<tr id="nodeProcessMode" class="hidden">			
				<td height="32" nowrap="nowrap" align="right">					
					<fmt:message key="node.process.mode"  bundle="${v3xCommonI18N}"/>:&nbsp;
				</td>
				<td>
						<!-- 
							<label for="single_mode">
								<input type="radio" name="node_process_mode" id="single_mode" value="single" ${isEditor == true && nodeType!='StartNode' && param.hasNewflow ne 'true'? '' : 'disabled'} checked>
								<fmt:message key="node.single.mode" bundle="${v3xCommonI18N}"/>
							</label>

							<label for="multiple_mode">
								<input type="radio" name="node_process_mode" id="multiple_mode" value="multiple" ${isEditor == true && nodeType!='StartNode' && param.hasNewflow ne 'true'? '' : 'disabled'}>
								<fmt:message key="node.multiple.mode" bundle="${v3xCommonI18N}"/>
							</label>
							-->
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
			<input type="button" name="b2" id="b2" onclick="javascript:window.close();" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
		</td>
	</tr>
</table>

<div id="policyExplainHTML" class="hidden">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">	
		<tr>
			<td colspan="2">
			    <div style="height: 28px;">
	        		<textarea name="content" rows="9" cols="46" validate="maxLength"
	                  		inputName="<fmt:message key="common.opinion.label" bundle="${v3xCommonI18N}" />" maxSize="2000" readonly></textarea>
			    </div>	
			</td>
		</tr>	
 	</table>
</div>

</form>
<iframe src="" name="inserPeopleIframe" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
</body>
</html>