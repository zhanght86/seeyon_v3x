<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://v3x.seeyon.com/bridges/spring-portlet-html" prefix="html"%>
<%@include file="../organizationHeader.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="org.button.banch.edit.label" /></title>
<script type="text/javascript">
	function validateTeamName(){   
		var orgLevelId = document.getElementById("orgLevelId").value;
		if(orgLevelId == ''){
	        alert(_("organizationLang.orgainzation_level_not_null"));
	        return false;		    
		}	        
		var memberNameValue = document.getElementsByName("loginName")[0].value;
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxOrgManagerDirect", "isPropertyDuplicated", false);
		requestCaller.addParameter(1, "String", "JetspeedPrincipal");
		requestCaller.addParameter(2, "String", "fullPath");
		requestCaller.addParameter(3, "String", memberNameValue);
		var isDbName = requestCaller.serviceRequest();
		if (isDbName=="true") {
			var requestCaller1 = new XMLHttpRequestCaller(this, "ajaxOrgManager", "isAdministrator", false);
			requestCaller1.addParameter(1, "String", memberNameValue);
			var isAdmin = requestCaller1.serviceRequest();
			if(isAdmin=="true"){
				var requestCaller4 = new XMLHttpRequestCaller(this, "ajaxOrgManager", "getAccountByLoginName", false);
				requestCaller4.addParameter(1, "String", memberNameValue);
				var accountBylonginName = requestCaller4.serviceRequest();
				var accountLongName = accountBylonginName.get("N");
				if(accountLongName!=null){
					alert(_("organizationLang.organization_member_login_account_name",accountBylonginName.get("N")));
					return false;
				}else{
					alert(_("organizationLang.organization_member_login_system_name"));
					return false;
				}
			}else{
				var requestCaller2 = new XMLHttpRequestCaller(this, "ajaxOrgManagerDirect", "toValidateName", false);
				requestCaller2.addParameter(1, "String", memberNameValue);
				var toValidateName = requestCaller2.serviceRequest();
				if(toValidateName!=null){
					var name = toValidateName[0];
					var accountId = toValidateName[1];
					var requestCaller3 = new XMLHttpRequestCaller(this, "ajaxOrgManagerDirect", "getAccountById", false);
					requestCaller3.addParameter(1, "Long", accountId);
					var account = requestCaller3.serviceRequest();
					if(account!=null){
						alert(_("organizationLang.organization_member_longin_name",account.get("N"),name));
						return false;
					}else{
					}
				}else{
				}
			} 
		} else {
			return true;
		}
	}
    function canceladdMemberForm(){
	  	parent.location.href = "${organizationURL}?method=organizationFrame&from=Member&deptAdmin=${param.deptAdmin}";
	}
	function setLoginName(){}
   //  onlyLoginAccount_${id}       true|false   ??????????????  ???false
	var onlyLoginAccount_post=true;
    var onlyLoginAccount_dept = true;
    var onlyLoginAccount_dept4Out = true;
    var onlyLoginAccount_level = true;
    var onlyLoginAccount_assistantPosts = true;
    var onlyCurrentDepartment_post = true;
	// ???????????????
	var isNeedCheckLevelScope_dept= false;
	var isNeedCheckLevelScope_post= false;
	var isNeedCheckLevelScope_level= false;
	var isNeedCheckLevelScope_assistantPosts= false;
	var hiddenSaveAsTeam_dept = true;
	var hiddenSaveAsTeam_post = true;
	var hiddenSaveAsTeam_level = true;
	var hiddenSaveAsTeam_assistantPosts = true;
	var departmentId_post = '${member.v3xOrgMember.orgDepartmentId}';
	showOriginalElement_dept = false;
	showOriginalElement_post = false;
	showOriginalElement_level = false;
	
	var showAllOuterDepartment_dept4Out = true;	
	
   function changeState(){
	  var state = document.getElementById("state").value;
	  var enabledObj = document.getElementById("enabled");
	  if(state=='1'){ // 在职
	    if(confirm(v3x.getMessage("organizationLang.orgainzation_employ_memeber"))){
	      <%--
	      document.getElementById("enabled1").disabled = false;
	      document.getElementById("enabled2").disabled = false;
	      document.getElementById("enabled1").checked = true;
	      document.getElementById("enabled2").checked = false;
	      --%>
	      enabledObj.value = '1'; // 启用
	    }
	  }else if(state=='2'){ // 离职
		<%--
	    if(document.getElementById("enabled1").checked){
		--%>
	      alert(v3x.getMessage("organizationLang.orgainzation_umemploy_memeber"));
	      <%--
	      document.getElementById("enabled1").disabled = false;
	      document.getElementById("enabled2").disabled = false;
	      document.getElementById("enabled1").checked = false;
	      document.getElementById("enabled2").checked = true;
	      --%>
	      enabledObj.value = '0'; // 停用
		<%--
	    }
	    --%>
	  }else{
		  <%--
	      document.getElementById("enabled1").disabled = true;
	      document.getElementById("enabled2").disabled = true;
	      --%>
	  }
	}

	function changeEnabled(enabledObj) {
		var d = document.getElementById("state");
		if (d) {
			var state = d.value;
			if (enabledObj.value != '0' && state == '2') { // 非“停用” && 离职
				alert(v3x.getMessage("organizationLang.orgainzation_umemploy_memeber"));
				enabledObj.value = '0'; // 停用
			}
		}
	}

    function enabledMem(){
	  var state = document.getElementById("state").value;
	  if(state == '2'&&document.getElementById("enabled1").checked){
	    alert(v3x.getMessage("organizationLang.orgainzation_unemploy_enable_memeber"));
	    document.getElementById("enabled1").checked = false;
	    document.getElementById("enabled2").checked = true;
	  }
	}
	
	function setOrgDept(elements) {
		if (!elements) {
	    	return;
		}
    	document.getElementById("deptName").value = getNamesString(elements);
    	document.getElementById("orgDepartmentId").value = getIdsString(elements,false);
    	departmentId_post = document.getElementById("orgDepartmentId").value;
	}
	
	function setOrgPost(elements) {
    	if (!elements) {
        	return;
    	}
    	document.getElementById("postName").value = getNamesString(elements);
    	var orgPostId = getIdsString(elements,false).split('_');
    	if(orgPostId.length > 1){
    	    document.getElementById("orgPostId").value = orgPostId[1];
    	}else{
    	    document.getElementById("orgPostId").value = orgPostId[0];
    	}
	}

	
	function isCriterionWord4Member(element){
		var value = element.value;
		var inputName = element.getAttribute("inputName");
	
		if(!testRegExp(value, '^[\\w-.]+$')){
			writeValidateInfo(element, v3x.getMessage("organizationLang.orgainzation_validate_isCriterionWord4Member", inputName));
			return false;
		}else{
		    if(value.indexOf('.')== 0 ||value.lastIndexOf('.') == value.length-1){
			    writeValidateInfo(element, v3x.getMessage("organizationLang.orgainzation_validate_start_or_end", inputName));
			    return false;	    
		    }
		}	
		return true;
	};
	
	function setIds(){
		document.getElementById("ids").value = window.dialogArguments.newIds;
	}
	function submitBanchEditForm(isVaidata){
		if(isVaidata){
	      document.getElementById("submintButton").disabled = true;
	      document.getElementById("submintCancel").disabled = true;
	      return true;
	    }else{
	      return false;
	    }
	}

	// 检查两次输入的密码是否一致
	function checkPassword() {
		var pass = document.getElementById("password").value;
		var pass1 = document.getElementById("password1").value;
		if (pass != "" || pass1 != "") {
			if(pass != pass1) {
				alert(v3x.getMessage("organizationLang.organization_member_password"));
				return false;
			} else if (pass.length < 6 || pass.length > 50) {
				alert(v3x.getMessage("organizationLang.orgainzation_member_password_limited"));
				return false;
			}		
		}
		return true;
	}

</script>
</head>
<body scroll="no" style="overflow: no" onload="return setIds();">
<form id="memberForm" method="post" target="editMemberFrame" action="${organizationURL}?method=updateBanchMember&deptAdmin=${param.deptAdmin}" onsubmit="return (submitBanchEditForm(checkForm(this)<%-- && checkPassword()--%>))">
<input type="hidden" name="orgAccountId" value="${member.v3xOrgMember.orgAccountId}" />
<input type="hidden" name="deptAdmin" id="deptAdmin" value="${param.deptAdmin}">
<input type="hidden" name="ids" id="ids" value="${ids}">
<input type="hidden" id="depsPathStr" value="${depsPathStr}">
<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" align="center" class="popupTitleRight">
<input type="hidden" name="deptAdmin" id="deptAdmin" value="${param.deptAdmin}">
	<tr>
		<td height="28" class="PopupTitle" colspan="2"><fmt:message key="org.button.banch.edit.label" /></td>
	</tr>
	<tr>
		<td valign="top">
			<v3x:selectPeople id="dept" maxSize="1" minSize="1" panels="Department" selectType="Department" jsFunction="setOrgDept(elements)" originalElements="Department|${member.v3xOrgMember.orgDepartmentId}"/>
			<v3x:selectPeople id="post" maxSize="1" minSize="1" panels="Post" selectType="Post" jsFunction="setOrgPost(elements)" originalElements="Post|${member.v3xOrgMember.orgPostId}"/>
			<c:set value="${v3x:parseElements(secondPostList,'secondPostId','secondPostType')}" var="secondPosts" />
			<c:set var="currentLocale" value="${member.v3xOrgMember.primaryLanguange}" />
			<c:set var="dis" value="${v3x:outConditionExpression(readOnly, 'disabled', '')}" />
			<c:set var="ro" value="${v3x:outConditionExpression(readOnly, 'readOnly', '')}" />
			<table width="90%" border="0" cellspacing="0" cellpadding="0" align="center">
				<%--
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="password"><fmt:message key="org.member_form.password.label" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
					<input type="password" ${dis} name="password" id="password" class="input-100per" />
					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="password1"><fmt:message key="org.account_form.adminPass1.label" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
					<input type="password" ${dis} id="password1" class="input-100per" />
					</td>
				</tr>
				--%>
				<tr>
					<td class="bg-gray" width="25%" nowrap="nowrap"><label for="securityNames"><fmt:message key="org.member_form.system_security.label"/>:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<input class="cursor-hand input-100per" type="text" id="securityNames" name="securityNames" value="" readonly="readonly" ${dis} onclick="setMenuSecurity('<html:link renderURL="/menuManager.do"/>?method=showAllMenuSecurity&memberId=${param.id}')" ${ro} validate="" inputName="<fmt:message key="org.member_form.system_security.label"/>" />
						<input type="hidden" id="securityIds" name="securityIds" value="${securityIds}"/>
					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="deptName"><fmt:message key="org.member_form.deptName.label" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
					<input class="cursor-hand input-100per" type="text" name="deptName" readonly="readonly" ${dis}
						id="deptName"
						value="" onclick="memberSelectBanchDepartment()" validate=""
						inputName="<fmt:message key="org.member_form.deptName.label" />" />
						<input type="hidden" name="orgDepartmentId" id="orgDepartmentId" value=""/>
				  </td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="orgLevelId"><fmt:message key="org.member_form.levelName.label${v3x:suffix() }" />:</label></td>
					<td class="new-column" width="75%">
		              <select name="orgLevelId" id="orgLevelId" inputName="<fmt:message key="org.member_form.levelName.label${v3x:suffix() }" />" class="input-100per" ${v3x:outConditionExpression(readOnly, 'disabled', '')}>
						<option value=""></option>
	                    <c:forEach items="${levels}" var="item">
	                    <option value="${item.id }">
	                        <c:out value="${item.name}" escapeXml="true" />
	                    </option>
	                    </c:forEach>
	                  </select>
				  </td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="postName"><fmt:message key="org.member_form.primaryPost.label" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
					
					<input type="text" readonly="readonly" ${dis}
						name="postName" id="postName" class="cursor-hand input-100per"
						 value="" onclick="selectPeopleFun_post()" validate=""
						 inputName="<fmt:message key="org.member_form.primaryPost.label" />" />
						<input type="hidden" name="orgPostId" id="orgPostId" value=""/>
				  </td>
				</tr>
				<!-- 成发集团二开增加涉密等级 -->
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="type"><fmt:message key="secretLevel" bundle="${v3xSecretI18N}"/>:</label></td>
					<td class="new-column" width="75%">
						<select class="input-100per" name="secretLevel"  id="secretLevel" inputName="<fmt:message key="secretLevel" bundle="${v3xSecretI18N}"/>">
		                    <!-- 2017-01-13 诚佰公司 -->
		                    <option value=""></option>
		                    <!-- 诚佰公司 -->
		                    <option value="1" ><fmt:message key='secretLevel.1' bundle="${v3xSecretI18N}"/></option>
		                    <option value="2" ><fmt:message key='secretLevel.2' bundle="${v3xSecretI18N}"/></option>
		                    <option value="3" ><fmt:message key='secretLevel.3' bundle="${v3xSecretI18N}"/></option>
		                    <!--  <option value="4" ><fmt:message key='secretLevel.4' bundle="${v3xSecretI18N}"/></option>-->
					  	</select>
					</td>
				</tr>
				<!-- 成发集团二开增加涉密等级 -->
					<tr>
						<td class="bg-gray" width="25%" nowrap="nowrap"><label for="gender"><fmt:message key="org.memberext_form.base_fieldset.sexe"/>:</label></td>
						<td class="new-column" width="75%">
		              <select name="gender" id="gender" inputName="<fmt:message key="org.memberext_form.base_fieldset.sexe"/>" class="input-100per" ${v3x:outConditionExpression(readOnly, 'disabled', '')}>    
		              	<option value=""></option>
		              	<!-- <c:if test="${member.v3xOrgMember.gender == null || member.v3xOrgMember.gender == -1}">
							<option value=""></option>
						</c:if>    -->   
	                    <option value="1">
	                        <fmt:message key='org.memberext_form.base_fieldset.sexe.man'/>
	                    </option>
	                    <option value="2">
	                        <fmt:message key='org.memberext_form.base_fieldset.sexe.woman'/>
	                    </option>
	                  </select>
						</td>
					</tr>
					<tr>
					  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="type"><fmt:message key="org.metadata.member_type.label" />:</label></td>
						<td class="new-column" width="75%">
						<select class="input-100per" name="type" id="type" ${dis}>
						<option value=""></option>
						<v3x:metadataItem metadata="${orgMeta['org_property_member_type']}" showType="option" name="type"/>
						  </select>
						  </td>
					</tr>
					<%--
					<tr>
					  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="state"><fmt:message key="org.metadata.member_state.label" />:</label></td>
						<td class="new-column" width="75%">
						<select class="input-100per" name="state" id="state" ${dis} onchange="changeState()">
						<option value=""></option>
						<v3x:metadataItem metadata="${orgMeta['org_property_member_state']}" showType="option" name="state"/>
						  </select>
						</td>
					</tr>
					--%>
				
				
				<fmt:setBundle basename="com.seeyon.v3x.organization.resources.i18n.OrganizationResources"/>
				<tr>
					<td class="bg-gray" width="25%" nowrap="nowrap"><label for="primaryLanguange"><fmt:message key="org.member_form.primaryLanguange.label" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
					
					<select id="primaryLanguange" name="primaryLanguange" class="input-100per" ${dis}>
						<option value=""></option>
					    <c:forEach var="l" items="${v3x:getAllLocales()}">
					        <option value="${l}"><fmt:message key="localeselector.locale.${l}" bundle="${localeI18N}"/></option>
					    </c:forEach>
					</select>
					</td>
				</tr>			

				<tr>
					<td class="bg-gray" width="25%" nowrap="nowrap"><label for="enabled"><fmt:message key="organization.member.state"/>:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
					<%--
					<c:set value="${member.v3xOrgMember.enabled ? 'checked' : ''}" var="c"/>
					<c:set value="${!member.v3xOrgMember.enabled ? 'checked' : ''}" var="d" />
					<c:set value="${v3x:outConditionExpression(!member.v3xOrgMember.enabled, 'disabled', '')}" var="rdis"/>
						<label for="enabled1">
							<input id="enabled1" type="radio" name="enabled" value="1" ${c} onclick="enabledMem()"/><fmt:message key="common.state.normal.label" bundle="${v3xCommonI18N}"/>
						</label>
						<label for="enabled2">
							<input id="enabled2" type="radio" name="enabled" value="0" ${d} /><fmt:message key="common.state.invalidation.label" bundle="${v3xCommonI18N}"/>
						</label>
					--%>
					<select id="enabled" name="enabled" class="input-100per" ${dis} onchange="changeEnabled(this)">
						<option value=""></option>
						<option value="1"><fmt:message key="common.state.normal.label" bundle="${v3xCommonI18N}"/></option>
						<option value="0"><fmt:message key="common.state.invalidation.label" bundle="${v3xCommonI18N}"/></option>
					</select>
					</td>
				</tr>

			</table>
		</td>
	</tr>
	<tr>
		<td height="42" align="center" class="bg-advance-bottom">
			<input id="submintButton" type="submit" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">&nbsp;
			<input id="submintCancel" type="button" onclick="window.close()" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
			<iframe name="editMemberFrame" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
		</td>
	</tr>
</table>
</form>
</body>
</html>