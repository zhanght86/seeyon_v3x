<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="../common/INC/noCache.jsp"%>
<%@ include file="Collaborationheader.jsp" %>
<fmt:setBundle basename="${bundleName}" var="v3xNodePolicyI18N"/>
<fmt:setBundle
	basename="www.seeyon.com.v3x.form.resources.i18n.FormResources" var="v3xFormI18N"/>
<title><fmt:message key="selectPolicy.please.select" /></title>

<script type="text/javascript">
var _parent = window.dialogArguments;
var nodePolicyName = '${nodePolicyName}';
var desArr = new Array();
<c:forEach items='${desList}' var="desStr" varStatus="status">
	desArr[${status.index}] = '${desStr}'; 
</c:forEach>
<%--
/*window.onload = function(){	
	var description = "";
	var policyId = "";
	for(var i=0; i<desArr.length; i++){
		var policyAndDes = desArr[i].split("split");
		var _description = policyAndDes[1];
		var _policyId = policyAndDes[0];
		if(_policyId == '${policyId}' && _description != "null" && _description != null){
			description = _description;
		}
	}
	document.getElementById("content").value = description;
}*/
--%>
function ok(){
	var selectPolicyForm = document.getElementById("selectPolicyForm");
	if(!checkForm(selectPolicyForm)) return;
	var form = document.getElementsByName("selectPolicyForm")[0];
    var policyOption = form.policy.options[form.policy.selectedIndex];
    var itemName = policyOption.getAttribute("itemName");
    var dealTerm = form.dealTerm.options[form.dealTerm.selectedIndex];
    var remindTime = form.remindTime.options[form.remindTime.selectedIndex];
    var deal = new Number(dealTerm.value);
    var remind = new Number(remindTime.value);
    var desc  = "";
    var hasDesc = "1";
    var descObj =  document.getElementById("desc");
    if(descObj){
    	desc = descObj.value;
    }
    if( desc.trim()=="" || desc.trim()=="\t"){
    	hasDesc= "0"
    }
	if(deal <= remind){
		alert(v3x.getMessage("collaborationLang.remindTimeLessThanDealDeadLine"));
		//form.dealTerm.selectedIndex = 0;
		form.remindTime.selectedIndex = 0;
    	return;
    }
    var processMode = "${processMode}";
    /**
    if(${_isTemplete}){
	    var node_process_mode = form.node_process_mode;
	    if(node_process_mode){
	    	for(var i=0; i<node_process_mode.length; i++){
				if(node_process_mode[i].checked){
					processMode = node_process_mode[i].value;
				}
			}
		}
	}else{
		processMode = "${processMode}";
	}
	***/
    var node_process_mode = form.node_process_mode;
    var disableORShowObj = document.getElementById('disableORShow') ;
    if(node_process_mode  && (disableORShowObj == null || (policyOption.value != "inform" && policyOption.value != "zhihui"))){
    	for(var i=0; i<node_process_mode.length; i++){
			if(node_process_mode[i].checked){
				processMode = node_process_mode[i].value;
			}
		}
	}	
	
	var formApp = "${formApp}";
	var formName = "";
	var operationName = "";
	if(document.getElementById("operations")){
	    var operations = document.getElementById("operations");
	    var arr = operations.options[operations.selectedIndex].value.split("|");
	    if(arr){
	      formName = arr[0];
	      operationName = arr[1];
	    }
	}
	
	//
	var matchScope = 1;
	try{
		var matchScopeObj = form.matchScope;
		if(matchScopeObj.length){
		    for(var i=0; i<matchScopeObj.length; i++){
				if(matchScopeObj[i].checked){
					matchScope = matchScopeObj[i].value;
				}
			}
		}else{
			matchScope = matchScopeObj.value;
		}
	}
	catch(e){
		alert(e);
	}
	
	var formFieldValue = "";
	try{
		var formFieldselect = form.formFieldValue;
		if(formFieldselect){
		    for(var i=0; i<formFieldselect.length; i++){
				if(formFieldselect[i].selected){
					formFieldValue = formFieldselect[i].value;
					break ;
				}
			}
		}
	}
	catch(e){
	}

	if(formFieldValue == "" && matchScope == "4"){
		alert(v3x.getMessage("collaborationLang.formCreat_must_value"));
	
		return ;
	} 
	
	if(policyOption.value == "inform" || policyOption.value == "zhihui"){
		if(${param.hasNewflow eq 'true'}){
			alert(v3x.getMessage("collaborationLang.inform_alert_alreadyHasNewflow"));
			return;
		}
		else if(${param.hasBranch eq 'true'}){
			//alert(v3x.getMessage("collaborationLang.inform_alert_alreadyHasBranch"));
			//return;
		}
	}
	var isApplyToAll = "false";
	if(form.applyToAll && form.applyToAll.checked){
		isApplyToAll = "true";
	}
	var dealTermType="";
	var dealTermUserId="";
	var dealTermUserName="";
	//var dealTermUserAccountShortName="";
	if(${_isTemplete} || _parent.isTempleteSupervise =="true" ){
		dealTermType= document.getElementById("dealTermAction").value;//到期处理类型
		if(dealTermType=='0'){//仅消息提醒
			dealTermUserId="";
			dealTermUserName="";
			//dealTermUserAccountShortName="";
		}else if(dealTermType=='1'){//转给指定人
			if(document.getElementById("dealTermUserId")){
				dealTermUserId= document.getElementById("dealTermUserId").value;//到期处理动作参数
				dealTermUserName= document.getElementById("workflowInfo").value;
				//dealTermUserAccountShortName= document.getElementById("dealTermAccountShortname").value;
			}else{
				//alert("必须为选项[转给指定人]指定具体人员!");
				alert(v3x.getMessage("collaborationLang.must_to_a_pople"));
				return;
			}
		}else{//自动跳过
			dealTermUserId="";
			dealTermUserName="";
		}
	}
    var result = [policyOption.value, policyOption.text, dealTerm.value, remindTime.value, 
                  processMode, formApp, formName, operationName, itemName, matchScope, 
                  "${param.hasNewflow}", isApplyToAll,formFieldValue,desc,dealTermType,dealTermUserId,dealTermUserName,hasDesc];
    window.returnValue = result;
	window.close();
}

function disableORShow(){
	var form = document.getElementsByName("selectPolicyForm")[0];
	var policyOption = form.policy.options[form.policy.selectedIndex];
	var value = policyOption.value ;
	var disableORShowObj = document.getElementById('disableORShow') ;
	if( disableORShowObj && disableORShow.className != 'hidden' && value && (value == "inform" || value == "zhihui")){
			disableORShowObj.className = 'hidden' ;
	}else if( disableORShowObj && disableORShowObj.className == 'hidden'){
			disableORShowObj.className = "" ;
	}
	if(policyOption.value == "inform" || policyOption.value == "zhihui"){
		document.getElementById("dealTermTR").style.display="none";
    }else{
    	var dealTermValue= document.getElementById("dealTerm").value;
    	if(dealTermValue!=0 && ( ${_isTemplete} || _parent.isTempleteSupervise =="true" ) ){
    		document.getElementById("dealTermTR").style.display="";
    		var dealTermType= document.getElementById("dealTermAction").value;//到期处理类型
    		if(dealTermType=='2'){
    			if(policyOption.value == "fengfa"){//公文封发节点不允许设置自动跳过
    				//alert("公文封发节点不支持到期自动跳过设置!");
    	    		alert(v3x.getMessage("collaborationLang.policy_edoc_dealterm_skip_not_support_fengfa"));
    	    		document.getElementById("dealTermAction").value='0';
    			}else if(policyOption.value == "vouch"){
		    		alert(v3x.getMessage("collaborationLang.policy_edoc_dealterm_skip_not_support_vouch"));
		    		document.getElementById("dealTermAction").value='0';
    			}
	    	}
    	}
    }
	thisPolicyId = value;
}

function disableFormFieldValue(value){	
	var formFieldselect = document.getElementById("formFieldValue") ;
	if(formFieldselect){
		if(value == 'false'){
			formFieldselect.disabled = "" ;
		}else{		
			 for(var i=0; i<formFieldselect.length; i++){
					if(formFieldselect[i].value == ""){
						formFieldselect[i].selected = true;						
					}else{
						formFieldselect[i].selected = false;
					}
				}
			 formFieldselect.disabled = true ;			
		}
	}
}

function compare(){
	var dealTermValue= document.getElementById("dealTerm").value;
	//alert("dealTermValue:="+dealTermValue);
	if(${_isTemplete}){
		var form = document.getElementsByName("selectPolicyForm")[0];
		var node_process_mode = form.node_process_mode;
		if(node_process_mode){
			for(var i=0; i<node_process_mode.length; i++){
				if(node_process_mode[i].value == "${processMode}"){
					node_process_mode[i].checked = true;
				}
			}
		}
		if(dealTermValue !='0'){
			document.getElementById("dealTermTR").style.display="";
			if(${param.dealTermType!='' && param.dealTermType !='null' && param.dealTermType !='undefined'}){
				document.getElementById("dealTermAction").value='${param.dealTermType}';
			}else{
				document.getElementById("dealTermAction").value='0';
			}
			if(${param.dealTermUserId !='' && param.dealTermUserName !='' && param.dealTermType!='' && param.dealTermUserId !='null' && param.dealTermUserName !='null' && param.dealTermType !='null'}){
				if(${param.dealTermType=='1'}){
					document.getElementById("workflowInfo").style.display="";
					document.getElementById("workflowInfo").value='${param.dealTermUserName}';
					if(document.getElementById("dealTermUserId")){
						document.getElementById("dealTermUserId").value='${param.dealTermUserId}';
					}else{
						var str="";
						str += '<input type="hidden" id="dealTermUserId" name="dealTermUserId" value="${param.dealTermUserId}" />';
						document.getElementById("workflowInfo_pepole_inputs").innerHTML= str;
					}
				}
			}
		}else{
			document.getElementById("dealTermTR").style.display="none";
		}
		var portyid = '${policyId}' ;
		if(portyid == 'inform' || portyid == 'zhihui'){
			document.getElementById("dealTermTR").style.display="none";
		}
	}else{
		document.getElementById("dealTermTR").style.display="none";
	}
	
	if( _parent.isTempleteSupervise =="true" ){
		if(dealTermValue !='0'){
			document.getElementById("dealTermTR").style.display="";
			if(${param.dealTermType!='' && param.dealTermType !='null' && param.dealTermType !='undefined'}){
				document.getElementById("dealTermAction").value='${param.dealTermType}';
			}else{
				document.getElementById("dealTermAction").value='0';
			}
			if(${param.dealTermUserId !='' && param.dealTermUserName !='' && param.dealTermType!='' && param.dealTermUserId !='null' && param.dealTermUserName !='null' && param.dealTermType !='null'}){
				if(${param.dealTermType=='1'}){
					document.getElementById("workflowInfo").style.display="";
					document.getElementById("workflowInfo").value='${param.dealTermUserName}';
					if(document.getElementById("dealTermUserId")){
						document.getElementById("dealTermUserId").value='${param.dealTermUserId}';
					}else{
						var str="";
						str += '<input type="hidden" id="dealTermUserId" name="dealTermUserId" value="${param.dealTermUserId}" />';
						document.getElementById("workflowInfo_pepole_inputs").innerHTML= str;
					}
				}
			}
		}else{
			document.getElementById("dealTermTR").style.display="none";
		}
	}
	
	if(${!_isTemplete &&  partyType != 'user' && (param.nodeState eq '1')}){
		var portyid = '${policyId}' ;
		var disableORShowObj = document.getElementById('disableORShow') ;
		if(portyid != 'inform' && portyid != 'zhihui' && disableORShowObj){
			disableORShowObj.className = '' ;
		}
	}
}

//暂时将所有节点权限的描述设成一个值
var descriptionStr = "${des}";
var thisPolicyId = '${policyId}'
function policyExplain2(){
	policyExplain(thisPolicyId,'','','${param.appName}');
}
/**
 * 删除被停用的 option 节点
 * 前提：当一个自定义节点被停用后，修改有这个节点的模版流程
 * 要求：进入页面时在设置节点权限中显示这个被停用的节点，当选择其它节点后被停用的这个节点自动消失
 */
function changeIsDisplayStopNode(obj) {
	for (var i = 0 ; i < obj.options.length ; i ++) {
		if (obj.options[i].id == 'stopNode')
			obj.remove(i);
	}
}
</script>
</head>
<body onkeydown="listenerKeyESC()" onload="compare()">
<form action="" name="selectPolicyForm" id ="selectPolicyForm">
<c:set var="isShowMatchScope" value="${_isTemplete && (param.nodeState eq '1') && param.partyType eq 'Post' }"/>
<c:set var="isShowApplyToAll" value="${param.isShowApplyToAll eq 'true'}"/> <%-- 是否显示'应用到所有' --%>
<c:if test="${!isShowMatchScope }">
<input type="hidden"  name="matchScope" value="${param.matchScope}">
</c:if>
<table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2" height="20" class="PopupTitle"><fmt:message key="selectPolicy.please.select" />
		</td>
	</tr>
	<tr>
	<td id="policyDiv" colspan="2" valign="top" style="padding:0 10px;">
		<div id="policyHTML">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
				<fieldset width="80%" align="center">
					<legend><fmt:message key="node.property.setting" /></legend>
					<table align="center" width="100%">
					<tr>
						<td width="30%" height="28" align="right"><fmt:message key="common.node.name.label" />:</td>
						<td width="70%" align="left">
			    			<input style="width:200px" name="nodeName" class="input-100per" readonly value="${v3x:toHTML(nodeName)}" title="${v3x:toHTML(nodeName)}">
			    		</td>
					</tr>	
					<tr>
						<td height="28" nowrap="nowrap" align="right">
							<fmt:message key="selectPolicy.please.select" />:
						</td>
						<td height="28">
						<!-- 传过来的节点已经被停用，则默认选中传过来的节点，默认值为选中 -->
						<select style="width:200px" onchange="disableORShow();changeIsDisplayStopNode(this);" name="policy" id="policy" ${(nodeType=='StartNode' || param.nodeState eq '2' || param.nodeState eq '7') ?'disabled':'' }>
					        <c:forEach items='${nodePolicyList}' var="nodePolicy" varStatus="num">
					        	<option id="${nodePolicy.isEnabled == 0 and policyId==nodePolicy.name ? 'stopNode' : ''}" value="${nodePolicy.name}" itemName="${nodePolicy.category}" ${policyId==nodePolicy.name || (policyId==null && param.defaultPolicyId==nodePolicy.name)?"selected":"" }>
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
			    	</tr>
			    	<tr>
			    		<td>
			    		</td>
			    		<td height="28" align="right">
			    			<a href="###" onclick="${nodeType=='StartNode'?'':'policyExplain()' }" ${nodeType=='StartNode'?'disabled':'' } style="text-decoration:none">
			    				[ <fmt:message key="node.policy.explain"/>]
			    			</a> 
			    		</td>
					</tr>
					</table>
					</fieldset>
					</td>
				</tr>
				<tr height="10"><td></td></tr>
				<tr>
					<td>
					<fieldset width="80%" align="center">
					<legend><fmt:message key="node.limittime" /></legend>
					<table align="center" width="100%">
					<tr>
						<td width="30%" height="28" align="right"><fmt:message key="node.cycle.label"/>:</td>
						<td width="70%" height="28">
						<select style="width:200px" id="dealTerm" name="dealTerm"  ${(nodeType=='StartNode' || param.nodeState eq '2' || param.nodeState eq '7') ?'disabled':'' } onchange="doDealTermOnchange(this)">
				        	<v3x:metadataItem metadata="${collaboration_deadline}" showType="option" name="dealTerm" selected="${dealTerm}" />
				    	</select>
			    		</td>
					</tr>	
					<tr>
						<td height="28" nowrap align="right"><fmt:message key="node.advanceremindtime" />:</td>
						<td height="28">
						<select style="width:200px" name="remindTime" ${(nodeType=='StartNode' || param.nodeState eq '2' || param.nodeState eq '7') ?'disabled':'' } }>
				        	<v3x:metadataItem metadata="${comMetadata}" showType="option" name="remindTime" selected="${remindTime}" />
				    	</select>
					</tr>
					<tr id="dealTermTR" style="display:block;">
						<td width="30%" height="28" align="right"><fmt:message key="node.deadline.arrived"/>:</td>
						<td width="70%" height="28">
							<select onchange="doDealTermActionChange(this);" style="width:100px;" name="dealTermAction" id="dealTermAction" ${(nodeType=='StartNode' || param.nodeState eq '2' || param.nodeState eq '7') ?'disabled':'' }>
								<option value="0"><fmt:message key="node.deadline.arrived.do0"/></option>
								<option value="1"><fmt:message key="node.deadline.arrived.do1"/></option>
								<option value="2"><fmt:message key="node.deadline.arrived.do2"/></option>
							</select>
						<v3x:selectPeople id="wf" panels="Department,Post,Team" selectType="Member" maxSize="1" jsFunction="setPeopleFieldsOfDealTerm(elements)"/>
						<fmt:message key='default.workflowInfo.value' var="dfwf" />
						<fmt:message key='policy.people.select.tip' var="dfwf1" />
						<input name="workflowInfo" id="workflowInfo" class="input-100per cursor-hand" style="width: 100px;display: none" readonly value="${dfwf1 }" default="${dfwf}" onclick="doWorkFlow('new')"  ${isFromTemplate == true && nonTextTemplete && isSystemTemplete ?'disabled' :'' }>
						<div id="workflowInfo_pepole_inputs" style="display: none"></div>
						</td>
					</tr>
					<tr height="5"><td></td></tr>
					</table>
					</fieldset>
				</td>
				</tr>
				<c:if test="${!_isTemplete && !isFromTemplete &&  param.partyType != 'user' && (param.nodeState eq '1')}">
			
				<tr height="10"><td></td></tr>	
				<tr id="disableORShow" class="hidden">
				<td>
					<fieldset width="80%" align="center">
					<legend><fmt:message key="node.process.mode"  bundle="${v3xCommonI18N}"/></legend>
					<table align="center">
					<tr>
						<td height="28" colspan="2">
						<c:if test="${param.processMode=='single'}">
							<label for="single_mode">
								<input type="radio" name="node_process_mode" id="single_mode" value="single" ${isEditor == true && nodeType!='StartNode' && param.hasNewflow ne 'true'? '' : 'disabled'} ${param.processMode=='single' || empty param.processModel ?'checked':'' } >
								<fmt:message key="node.single.mode" bundle="${v3xCommonI18N}"/>
							</label>
						</c:if>
						<c:if test="${param.processMode=='multiple'}">
							<label for="multiple_mode">
								<input type="radio" name="node_process_mode" id="multiple_mode" value="multiple" ${ isEditor == true && nodeType!='StartNode' && param.hasNewflow ne 'true'? '' : 'disabled'} ${param.processMode=='multiple' ?'checked':'' }>
								<fmt:message key="node.multiple.mode" bundle="${v3xCommonI18N}"/>
							</label>
						</c:if>
						<!-- 涉密流程 不允许先全体模式和竞争模式 因为无法进行密级过滤 -->
						<!--<c:if test="${param.secretLevel == 1}"> -->
							<label for="all_mode">
								<input type="radio" name="node_process_mode" id="all_mode" value="all" ${isEditor == true && nodeType!='StartNode' && param.hasNewflow ne 'true'? '' : 'disabled'} ${param.processMode=='all' ?'checked':'' }>
								<fmt:message key="node.all.mode" bundle="${v3xCommonI18N}"/>
							</label>
							<label for="competition_mode">	
								<input type="radio" name="node_process_mode" id="competition_mode" value="competition" ${isEditor == true && nodeType!='StartNode' && param.hasNewflow ne 'true'? '' : 'disabled'}  ${param.processMode=='competition' ?'checked':'' }>
								<fmt:message key="node.competition.mode" bundle="${v3xCommonI18N}"/>
							</label>
						<!--</c:if> -->
						</td>
					</tr>
					</table>
					</fieldset>
					</td>
				</tr>				
				</c:if>
				
				<c:if test="${(_isTemplete || isFromTemplete) && (param.nodeState eq '1')}">
				<tr height="10"><td></td></tr>	
				<tr>
				<td>
					<fieldset width="80%" align="center">
					<legend><fmt:message key="node.process.mode"  bundle="${v3xCommonI18N}"/></legend>
					<table align="center">
					<tr>
						<td height="28" colspan="2">
							<label for="single_mode">
								<input type="radio" name="node_process_mode" id="single_mode" value="single" ${isEditor == true && nodeType!='StartNode' && param.hasNewflow ne 'true'? '' : 'disabled'} checked>
								<fmt:message key="node.single.mode" bundle="${v3xCommonI18N}"/>
							</label>
							<label for="multiple_mode">
								<input type="radio" name="node_process_mode" id="multiple_mode" value="multiple" ${isEditor == true && nodeType!='StartNode' && param.hasNewflow ne 'true'? '' : 'disabled'} ${param.processMode=='multiple' ?'checked':''}>
								<fmt:message key="node.multiple.mode" bundle="${v3xCommonI18N}"/>
							</label>
						<!-- 涉密流程 不允许先全体模式和竞争模式 因为无法进行密级过滤 -->
						<c:if test="${param.secretLevel == 1}">
							<label for="all_mode">
								<input type="radio" name="node_process_mode" id="all_mode" value="all" ${isEditor == true && nodeType!='StartNode' && param.hasNewflow ne 'true'? '' : 'disabled'} ${param.processMode=='all' ?'checked':'' }>
								<fmt:message key="node.all.mode" bundle="${v3xCommonI18N}"/>
							</label>
							<label for="competition_mode">	
								<input type="radio" name="node_process_mode" id="competition_mode" value="competition" ${isEditor == true && nodeType!='StartNode' && param.hasNewflow ne 'true'? '' : 'disabled'} ${param.processMode=='competition' ?'checked':''}>
								<fmt:message key="node.competition.mode" bundle="${v3xCommonI18N}"/>
							</label>
						</c:if>
						</td>
					</tr>
					</table>
					</fieldset>
					</td>
				</tr>
				</c:if>
				
				<c:if test="${isShowMatchScope}">
				<tr height="10"><td></td></tr>
				<tr>
					<td>
						<c:set var="partyAccountId" value="${v3x:getOrgEntity(param.partyType, param.partyId).orgAccountId}" />
						<fieldset width="80%" align="center">
						<legend><fmt:message key="node.matchScope" bundle="${v3xCommonI18N}" /></legend>
						<table align="center">
							<tr>
								<c:choose>
									<%-- 集团标准岗 --%>
									<c:when test="${v3x:getOrgEntity('Account', partyAccountId).isRoot && v3x:getSysFlagByName('sys_isGroupVer')}">
								    	<td height="28"><label for="matchScope2"><input type="radio" name="matchScope" onclick="disableFormFieldValue('true')"  id="matchScope2" value="2" ${param.matchScope eq '2' ? 'checked' : ''}><fmt:message key="node.matchScope.2${v3x:suffix()}" bundle="${v3xCommonI18N}" /></label></td>
									    <td height="28"><label for="matchScope5"><input type="radio" name="matchScope" onclick="disableFormFieldValue('true')"   id="matchScope5" value="5" ${(param.matchScope eq '5' || empty param.matchScope) ? 'checked' : ''}><fmt:message key="node.matchScope.5" bundle="${v3xCommonI18N}" /></label></td>
									    <td height="28"><label for="matchScope1"><input type="radio" name="matchScope" onclick="disableFormFieldValue('true')"   id="matchScope1" value="1" ${(param.matchScope eq '1' || empty param.matchScope) ? 'checked' : ''}><fmt:message key="node.matchScope.1" bundle="${v3xCommonI18N}" /></label></td>
									</c:when>
									<%-- 单位岗 --%>
									<c:otherwise>
									    <td height="28"><label for="matchScope1"><input type="radio"  name="matchScope" id="matchScope1" value="1" ${(param.matchScope eq '1' || empty param.matchScope) ? 'checked' : ''}><fmt:message key="node.matchScope.1.A" bundle="${v3xCommonI18N}" /></label></td>
									</c:otherwise>
								</c:choose>
							    <td height="28"><label for="matchScope3"><input type="radio" onclick="disableFormFieldValue('true')" name="matchScope" id="matchScope3" value="3" ${param.matchScope eq '3' ? 'checked' : ''}><fmt:message key="node.matchScope.3" bundle="${v3xCommonI18N}" /></label></td>
							</tr>
							<c:if test="${v3x:getOrgEntity('Account', partyAccountId).isRoot && v3x:getSysFlagByName('sys_isGroupVer')}">
								<c:if test="${formApp ne null && formApp ne '' && formApp ne 'undefined' && formApp ne 'null'}">
								<tr>
									<td colspan="3">
										<label for="matchScope4"><input type="radio" name="matchScope" onclick="disableFormFieldValue('false')" id="matchScope4" value="4" ${param.matchScope eq '4' ? 'checked' : ''}><fmt:message key="node.matchScope.4" bundle="${v3xCommonI18N}" /></label>
										<select id="formFieldValue" name="formFieldValue" disabled="disabled">
											<option value="">--<fmt:message key="node.matchScope.4.A" bundle="${v3xCommonI18N}" />--</option>
											${formFieldString}
										</select>
									</td>
								</tr>
							</c:if>
							</c:if>
						</table>
						</fieldset>	
					</td>
				</tr>
				</c:if>
				
				<c:if test="${formApp ne null && formApp ne '' && formApp ne 'undefined' && formApp ne 'null'}">
				<tr height="10"><td></td></tr>
				<tr>
				  <td>
					<fieldset width="80%" align="center">
					<legend><fmt:message key="form.bind.label" /></legend>
					<table align="center" width="100%">
					<tr>
					    <td width="30%" height="28" align="right"><fmt:message key="form.bind.formAndOperation" />:</td>
					    <td width="70%">
						<select style="width:200px" name="operations" id="operations" ${(param.nodeState eq '2' || param.nodeState eq '7') ?'disabled':'' }>
						<c:set value="${form}|${operationName }" var="tmp"></c:set>
						<c:forEach items="${displays }" var="display" varStatus="status">
						  <option value="${ values[status.index]}" title = "${display }" <c:if test="${values[status.index]==tmp}">selected</c:if>>${display }</option>
						</c:forEach>
				    	</select>
			    		</td>
					</tr>
					</table>
					</fieldset>				   
				  </td>
				</tr>
				</c:if>
				
				<c:if test="${isFromTemplete or _isTemplete}">
					<tr height="10"><td></td></tr>
					<tr height="10">
						<td><fmt:message key="node.deal.explain"/></td>
					</tr>	
					<tr>
						<td>
							<textarea id="desc" name="desc"  rows="5" maxSize="200" 
							inputName="<fmt:message key='node.deal.explain'/>"
							validate="maxLength,notSpecChar,isWord" style="width:100%;">${empty param.desc or null eq param.desc ? "" : param.desc }</textarea>
						</td>
					</tr>
				</c:if>
		 	</table>
			</div>
		</td>
	</tr>
	<tr>
		<td height="42" align="right" class="bg-advance-bottom">
			<%-- 批量修改节点属性  --%>
			<fmt:message key="node.property.applyToAll" var ="applyToAllMessage"/>
			<c:if test ="${_isTemplete}">
				<fmt:message key="node.property.applyToAll.templete"  var ="applyToAllMessage" />
			</c:if>
			<c:if test="${isShowApplyToAll}">
				<label for="applyToAllCbox">
					<input id="applyToAllCbox" type="checkbox" id="applyToAll" name="applyToAll" value="true">${applyToAllMessage} 
				</label>
			</c:if>
			&nbsp;
		</td>
		<td align="right" class="bg-advance-bottom">			
			<input type="button" onclick="ok()" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
			<input type="button" onclick="window.close()" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
		</td>
	</tr>
</table>
<div id="policyExplainHTML" class="hidden">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">	
		<tr>
			<td colspan="2">
			    <div style="height: 28px;">
	        		<textarea id="content" name="content" rows="9" cols="46" validate="maxLength"
	                  		inputName="<fmt:message key="common.opinion.label" bundle="${v3xCommonI18N}" />" readonly></textarea>
			    </div>	
			</td>
		</tr>	
 	</table>
</div>

</form>
<script type="text/javascript">


	var matchScope4 = document.getElementById("matchScope4");
	if(matchScope4 && matchScope4.checked){
		var formFieldValue = document.getElementById("formFieldValue");
		if(formFieldValue){
			formFieldValue.disabled = "" ;
		}
	}

	var partyType = '${partyType}' ;
	if(partyType == "FormField"){
		var single_mode = document.getElementById("single_mode");
		var multiple_mode = document.getElementById("multiple_mode");
		var all_mode = document.getElementById("all_mode");
		var competition_mode = document.getElementById("competition_mode");
		if(single_mode && multiple_mode && all_mode && competition_mode){
			single_mode.disabled = true ;
			multiple_mode.disabled = true ;
			all_mode.disabled = true ;
			competition_mode.disabled = true ;
		}
	}
	
	//不允许选择空的组、部门
	var unallowedSelectEmptyGroup_wf = true;
	//是否隐藏集团单位
	var hiddenRootAccount_wf = true;
	//是否隐藏"多层"按钮
	var hiddenMultipleRadio_wf = true;
	//是否隐藏"另存为组"
	var hiddenSaveAsTeam_wf= true;
	//是否只显示登录单位
	//var onlyLoginAccount_wf= false;
	//是否隐藏组下的外单人员
	var hiddenOtherMemberOfTeam_wf= false;

	function doDealTermActionChange(obj){
		if(obj.value=='1'){
			document.getElementById("workflowInfo").style.display="";
		}else{
			document.getElementById("workflowInfo").style.display="none";
		}
		var selectPolicyForm = document.getElementById("selectPolicyForm");
		checkForm(selectPolicyForm);
		var form = document.getElementsByName("selectPolicyForm")[0];
	    var policyOption = form.policy.options[form.policy.selectedIndex];
	    if(policyOption.value == "fengfa"){//公文封发节点不允许设置自动跳过
	    	if(obj.value=='2'){
	    		//alert("公文封发节点不支持到期自动跳过设置!");
	    		alert(v3x.getMessage("collaborationLang.policy_edoc_dealterm_skip_not_support_fengfa"));
	    		obj.value='0';
	    	}
	    }else if(policyOption.value == "vouch"){//核定节点不允许设置自动跳过
	    	if(obj.value=='2'){
	    		alert(v3x.getMessage("collaborationLang.policy_edoc_dealterm_skip_not_support_vouch"));
	    		obj.value='0';
	    	}
	    }else{
	    	if(obj.value=='2'){
	    		_parent.verifyTermOfSkip('${param.nodeId}');
	    		if(_parent.isNodeAfterHasCondition){
	    			//alert("当前节点之后存在分支或人员不可用，导致不能设置超期时自动跳过！");
	    			alert(v3x.getMessage("collaborationLang.policy_edoc_dealterm_skip_not_support_branch"));
	    			obj.value='0';
	    			_parent.isNodeAfterHasCondition= false;
	    		}
	    	}
	    }
	}

	function setPeopleFieldsOfDealTerm(elements){
		if (!elements) {
	        return false;
	    }
		var person = elements[0] || [];
		var str="";
		str += '<input type="hidden" name="dealTermUserType" value="' + person.type + '" />';
	       str += '<input type="hidden" id="dealTermUserId" name="dealTermUserId" value="' + person.id + '" />';
	       str += '<input type="hidden" id="dealTermUserName" name="dealTermUserName" value="' + escapeStringToHTML(person.name) + '" />';
	       str += '<input type="hidden" name="dealTermAccountId" value="' + person.accountId + '" />';
	       str += '<input type="hidden" id="dealTermAccountShortname" name="dealTermAccountShortname" value="' + escapeStringToHTML(person.accountShortname) + '" />';
	       
		document.getElementById("workflowInfo_pepole_inputs").innerHTML= str;
		if(escapeStringToHTML(person.accountShortname)!=''){
			document.getElementById("workflowInfo").value=escapeStringToHTML(person.name)+"("+escapeStringToHTML(person.accountShortname)+")";
		}else{
			document.getElementById("workflowInfo").value=escapeStringToHTML(person.name);
		}
	}
	
	function doDealTermOnchange(obj){
		var selectPolicyForm = document.getElementById("selectPolicyForm");
		checkForm(selectPolicyForm);
		var form = document.getElementsByName("selectPolicyForm")[0];
	    var policyOption = form.policy.options[form.policy.selectedIndex];
		if( obj.value!='0' && ( ${_isTemplete} || _parent.isTempleteSupervise =="true" ) ){
			if(policyOption.value == "inform" || policyOption.value == "zhihui"){
				document.getElementById("dealTermTR").style.display="none";
		    }else{
		    	document.getElementById("dealTermTR").style.display="";
		    }
		}else{
			document.getElementById("dealTermTR").style.display="none";
			if(obj.value=='0'){
				if(document.getElementById("dealTermAction")){
					document.getElementById("dealTermAction").value="";
				}
				if(document.getElementById("dealTermUserId")){
					document.getElementById("dealTermUserId").value="";
				}
				if(document.getElementById("workflowInfo")){
					document.getElementById("workflowInfo").value="";
				}
			}
		}
	}
	
</script>

</body>
</html>