<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../common/INC/noCache.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="Collaborationheader.jsp" %>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery.js${v3x:resSuffix()}" />"></script>
<title><fmt:message key="select.space.type" bundle='${v3xCommonI18N}' /></title>
<script type="text/javascript">
<%--
//var accountId_scope1="${v3x:currentUser().loginAccount}";
--%>
var selectPeopleFlag = 1;//1--选人界面scope1,2--选人界面scope2
if('${typeObj.spaceType}' == '0'){
	selectPeopleFlag = 2;
}else if('${typeObj.spaceType}' == '1'){
	onlyLoginAccount_scope1=true;
	accountId_scope1 = "${typeObj.accountId}";
	selectPeopleFlag = 1;
}else if('${typeObj.spaceType}' == '2'){
	if(document.getElementById("issus_scope")){
		document.getElementById("issus_scope").style.display = "none";
	}
}else{
	if(document.getElementById("issus_scope")){
		document.getElementById("issus_scope").style.display = "";
	}
}
isNeedCheckLevelScope_scope1 = false;
isNeedCheckLevelScope_scope2 = false;
var flowSecretLevel_scope1 = "${param.secretLevel}";
var flowSecretLevel_scope2 = "${param.secretLevel}";

window.onload = function(){
	if("${type}" == "bulletionaudit"){
		document.getElementById("issus_scope").style.display = "";
	}
	var selectedArr = document.getElementsByName("issus_space_type");
	if(selectedArr && selectedArr.length>0){
		selectedArr[0].checked = true;
		
		updateType(selectedArr[0].extAttribute1, selectedArr[0].extAttribute2);
	}
}

var selPanels = null;
function updateType(spaceType, accountId){
	if(spaceType == 1){
		if("${type}" == "bulletionaudit"){
			document.getElementById("issus_scope").style.display = "";
		}
		onlyLoginAccount_scope1=true;
		accountId_scope1 = accountId;
		selectPeopleFlag = 1;
	}else if(spaceType == 2){
		if("${type}" == "bulletionaudit"){
			document.getElementById("issus_scope").style.display = "none";
		}
	}else{
		if("${type}" == "bulletionaudit")
			document.getElementById("issus_scope").style.display = "";
		selectPeopleFlag = 2;
	}
	clearSelectPeopleResult();
}

function callSelectPeople(){
	if(selectPeopleFlag == 1){
		selectPeopleFun_scope1();
	}else{
		selectPeopleFun_scope2();
	}
}

function ok(){
	var selectedArr = document.getElementsByName("issus_space_type");
 	var selectedValue = null;
 	var selectedType = null
	for(var i=0; i<selectedArr.length; i++){
		if(selectedArr[i].checked == true){
			selectedValue = selectedArr[i].value;
			selectedType = selectedArr[i].extAttribute1;
			break;
		}
	}
	var valueArr = new Array();
	valueArr.push(selectedValue);
	var scopePeople = document.getElementById("memberIdsStr").value;
	if("${type}"=="bulletionaudit" && selectedType!=2 && scopePeople==""){
		alert(_("collaborationLang.please_set_issus_scope"));
		return;
	}
	else{
		valueArr.push(scopePeople);
	}
	<%-- 增加打印控制 --%>
	valueArr.push(document.getElementById("allowPrint") && document.getElementById("allowPrint").checked ? "1" : "0");
	valueArr.push(document.getElementById("b") && document.getElementById("b").checked ? "1" : "0");
	
	if(valueArr.length > 0){
		window.returnValue = valueArr;
		window.close();	
	}else{
		alert(_("collaborationLang.please_select_issus_space"));
		return;
	}
}

function cancelIssus(){
	window.returnValue = new Array();
	window.close();
}
</script>
<c:set var="bullspace" value=""/>
<script type="text/javascript">
function changeBullSpace(st, accountId)
{
   if(st=="0")
   {
     <c:set var="bullspace" value="Account,"/>
   }
   else
   {
     <c:set var="bullspace" value=""/>
   }
   clearSelectPeopleResult();
}
function clearSelectPeopleResult() {
	<%-- 清空之前的发布范围，避免用户先选择了集团公告发布范围，随后又切换到单位公告板块，造成发布范围越级，但对用户略有不便 --%>
   if(document.getElementById("memberIdsStr")) {
   		document.getElementById("memberIdsStr").value = '';
   }
   if(document.getElementById("issusScope")) {
   		document.getElementById("issusScope").value = "<fmt:message key='common.default.issueScope.value' bundle='${v3xCommonI18N}'/>";
   }
}
var csount=0;
function defaultSpace(st, accountId)
{
  if(csount=="0")
  {
    changeBullSpace(st, accountId);
  }
}
</script>
<c:if test="${notHasOutworker!=null}">
<v3x:selectPeople id="scope1" panels="${bullspace}Department,Team,Level" selectType="Member,Department,Account,Level,Team" jsFunction="setIssusPeopleFields(elements)"  />	
</c:if>
<c:if test="${notHasOutworker==null}">
<v3x:selectPeople id="scope1" panels="${bullspace}Department,Team,Level,Outworker" selectType="Member,Department,Account,Level,Team" jsFunction="setIssusPeopleFields(elements)"  />	
</c:if>
<v3x:selectPeople id="scope2" panels="Account,Department,Team,Post,Level" selectType="Member,Department,Account,Level,Team" jsFunction="setIssusPeopleFields(elements)" />	
<script type="text/javascript">
<%-- 为了避免先选择了发布范围，随后再切换不同空间类型公告板块所导致的发布范围与空间类型不互相匹配的问题，将选人界面设定为不回显原有被选数据，对用户略有不便 --%>
showOriginalElement_scope1 = false;
showOriginalElement_scope2 = false;
</script>
</head>

<body scroll="no" style="overflow: hidden" onkeydown="listenerKeyESC()" >
<form name="preIssusForm" action="" target="preIssusIframe" method="post" >
<input type="hidden" id="memberIdsStr" name="memberIdsStr" value=""/>
<table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" class="PopupTitle" colspan="2">
		<fmt:message key="${type}.space" bundle='${v3xCommonI18N}' />:</td>
	</tr>
	<tr>
		<td class="bg-advance-middel" colspan="2">
			<div class="scrollList" style="border: solid 1px #666666;">
				<table class="sort" width="100%"  border="0" cellspacing="0" cellpadding="0" onClick="sortColumn(event, true)">
					<thead>
					<tr class="sort">
						<td type="String" colspan="2"><fmt:message key="space.name" bundle='${v3xCommonI18N}' /></td>
					</tr>
					</thead>
					<tbody>
						<c:forEach items="${typeList}" var="typeData">
						<c:set value="${typeData.accountId}" var="typeAccountId" />
						<c:if test="${typeData.spaceType eq '2'}">
							<c:set value="${v3x:getDepartment(typeData.accountId).orgAccountId}" var="typeAccountId" />
						</c:if>
						<tr class="sort" align="left" onclick="updateType('${typeData.spaceType}', '${typeData.accountId}')">
							<td align="center" class="sort" width="5%">
							    <script type="text/javascript">
								    defaultSpace('${typeData.spaceType}', '${typeData.accountId}');
								    csount=csount+1;
							    </script>
								<input type="radio" name="issus_space_type" id="spaceTypeName" value="${typeData.id}" extAttribute1="${typeData.spaceType}" extAttribute2="${typeData.accountId}" onclick="changeBullSpace('${typeData.spaceType}', '${typeData.accountId}')"/>
							</td>
							<td class="sort" type="String">
								<c:choose>
									<c:when test="${typeData.spaceType == '4' || typeData.spaceType == '5' || typeData.spaceType == '6'}">
										${typeData.typeName}(${spaceNames[typeData.id]})
									</c:when>
									<c:otherwise>
										${typeData.typeName}(${v3x:getAccount(typeAccountId).shortname})
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</td>
	</tr>
	
	<tr id="issus_scope" style="background-color: #F6F6F6; display: none;">
		<td height="28" width="70" align="right" nowrap="nowrap"><fmt:message key="common.issueScope.label" bundle='${v3xCommonI18N}' />:</td>
		<td style="padding-left:6px;padding-right:16px;">
			<input id="issusScope" name="issusScope" class="input-100per cursor-hand" readonly value="<fmt:message key='common.default.issueScope.value' bundle='${v3xCommonI18N}'/>" onclick="callSelectPeople()">
		</td>
  	</tr>
  	
  	<c:if test="${param.policyName ne 'newsaudit'}">
	  	<tr id="ctrl_print_read" style="background-color: #F6F6F6;">
			<td height="28" nowrap="nowrap" style="padding-left:6px;padding-right:16px;" colspan="2">
				&nbsp;&nbsp;&nbsp;
				<label for="a">
					<input type="checkbox" name="allowPrint" id="allowPrint" /><fmt:message key="bul.dataEdit.printAllow" bundle="${bulI18N}" />
				</label>
				<c:if test="${param.bodyType == 'OfficeWord' || param.bodyType == 'WpsWord'}">
					<label for="b">
						<input type="checkbox" name="changePdf" id="b" /><fmt:message key="common.transmit.pdf" bundle='${v3xCommonI18N}' />
					</label>
				</c:if>
			</td>
	  	</tr>
  	</c:if>
	
	<tr>
		<td height="42" align="right" class="bg-advance-bottom" colspan="2">
			<input type="button" onclick="ok()" value="<fmt:message key='common.button.ok.label' bundle='${v3xCommonI18N}' />" class="button-default-2">&nbsp;
			<input type="button" onclick="cancelIssus()" value="<fmt:message key='common.button.cancel.label' bundle='${v3xCommonI18N}' />" class="button-default-2">
		</td>
	</tr>
</table>
</form>
<iframe src="" name="preIssusIframe" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
</body>
</html>