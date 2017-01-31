<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://v3x.seeyon.com/bridges/spring-portlet-html" prefix="html"%>
<%@ page import="com.seeyon.v3x.common.metadata.*" %>
<script type="text/javascript">
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

	function changeImage()
	 {
		fileUploadAttachments.clear();
		insertAttachment();
		if(fileUploadAttachments.isEmpty() == false){
			var theList = fileUploadAttachments.keys();
			var attach = fileUploadAttachments.get(theList.get(0),null);
			var _fileId = attach.fileUrl;
			var _createDate = attach.createDate;
			var src = "/seeyon/fileUpload.do?method=showRTE&fileId="+_fileId+"&createDate="+_createDate+"&type=image";
			document.getElementById("fileId").value = _fileId;
			document.getElementById("createDate").value = _createDate;
			var str = "<img src='" + src + "' width='100' height='120'>";
			document.getElementById("thePicture").innerHTML = str;
		}
	}

   function changeState(){
	  var state = document.getElementById("state").value;
	  if(state=='1'){
	    if(confirm(v3x.getMessage("organizationLang.orgainzation_employ_memeber"))){
	      document.getElementById("enabled1").checked = true;
	      document.getElementById("enabled2").checked = false;
	    }
	  }else if(state=='2'){
	    if(document.getElementById("enabled1").checked){
	      alert(v3x.getMessage("organizationLang.orgainzation_umemploy_memeber"));
	      document.getElementById("enabled1").checked = false;
	      document.getElementById("enabled2").checked = true;
	    }
	  }
	}
    function enabledMem(){
	  var state = document.getElementById("state").value;
	  if(state == '2'&&document.getElementById("enabled1").checked){
	    //alert(v3x.getMessage("organizationLang.orgainzation_unemploy_enable_memeber"));
	    //document.getElementById("enabled1").checked = false;
	    //document.getElementById("enabled2").checked = true;
		  document.getElementById("state").value= "1";
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
</script>
<v3x:selectPeople id="dept4Out" maxSize="1" minSize="1" panels="Outworker" selectType="Department" jsFunction="setDept(elements)" originalElements="Department|${member.v3xOrgMember.orgDepartmentId}"/>
<v3x:selectPeople id="dept" maxSize="1" minSize="1" panels="Department" selectType="Department" jsFunction="setOrgDept(elements)" originalElements="Department|${member.v3xOrgMember.orgDepartmentId}"/>
<v3x:selectPeople id="post" maxSize="1" minSize="1" panels="Post,Department" selectType="Post" jsFunction="setOrgPost(elements)" originalElements="Post|${member.v3xOrgMember.orgPostId}"/>
<v3x:selectPeople id="level" maxSize="1" minSize="1" panels="Level" selectType="Level" jsFunction="setLevel(elements)" originalElements="Level|${member.v3xOrgMember.orgLevelId}"/>

<c:set value="${v3x:parseElements(secondPostList,'secondPostId','secondPostType')}" var="secondPosts" />

<v3x:selectPeople id="assistantPosts" minSize="0" panels="Department" selectType="Post" jsFunction="setSecondPosts(elements)" originalElements="${secondPosts }"/>

<c:set var="currentLocale" value="${member.v3xOrgMember.primaryLanguange}" />
<input type="hidden" id="depsPathStr" value="${depsPathStr}">
<input type="hidden" id="forAgentState" name="forAgentState" value="${member.v3xOrgMember.state}">
<table width="100%" border="0" cellspacing="0" height="96%" cellpadding="0" align="center">
	<tr>
	<td height="28" class="categorySetTitle" colspan="2"><font color="red">*</font><fmt:message key="level.must.write" bundle="${orgI18N}" /></td>
	</tr>
  <tr valign="top">
    <td width="50%">
    <fieldset style="width:95%;border:0px;" align="center">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr>
				<td class="bg-gray" nowrap="nowrap" align="right">
					<div class="hr-blue-font"><strong><fmt:message key="org.member_form.system_fieldset.label" bundle="${orgI18N}"/></strong></div>
				</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label
					for="name"><font color="red">*</font><fmt:message key="org.member_form.name.label" bundle="${orgI18N}"/>:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">
					<fmt:message key="common.default.name.value" var="defName" bundle="${v3xCommonI18N}"/>
					<input name="name" type="text" id="name" class="input-100per" deaultValue="${defName}" disabled="disabled"
						inputName="<fmt:message key="org.member_form.name.label" bundle="${orgI18N}" />" maxSize="40" maxlength="40" validate="notNull,isDeaultValue,maxLength,isWord" character="|,'&quot;"
					    value="<c:out value="${member.v3xOrgMember.name}" escapeXml="true" default='${defName}' />"
					     
					    onfocus='checkDefSubject(this, true)' onblur="checkDefSubject(this, false)">
				</td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="member.loginName"><font color="red">*</font><fmt:message key="org.member_form.loginName.label" bundle="${orgI18N}"/>:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">
				<input class="input-100per" type="text" name="loginName"  maxSize="40" maxLength="40" disabled="disabled"
					id="member.loginName" value="${member.v3xOrgMember.loginName}" validate="notNull,isWord"
					    value="<c:out value="${member.v3xOrgMember.name}" escapeXml="true" default='${defName}' />"
					inputName="<fmt:message key="org.member_form.loginName.label" bundle="${orgI18N}"/>" onclick="setLoginName();"/></td>
			</tr>
			<c:if test="${hasLDAPAD}">
			<tr <c:if test="${editstate}">style="display: none;"</c:if>>
				<td class="bg-gray" width="25%" nowrap="nowrap"><fmt:message
					key="ldap.lable.type" />:</td>
				<td class="new-column" width="75%" nowrap="nowrap">
				<label for="ldapType1"> <input id="ldapType1" type="radio"
					name="ldapType" value="1" onclick="hiddenEntry()" disabled="disabled" /><fmt:message
					key="ldap.lable.new" /> </label> <label for="ldapType2"> <input
					id="ldapType2" type="radio" name="ldapType" value="0"
					 onclick="showEntry()" disabled="disabled" checked="checked"/><fmt:message key="ldap.lable.select" /> </label>
				</td>
			</tr>
				<tr <c:if test="${addstate && editstate != null}">style="display: none;"</c:if> id="entryLable">
				<td class="bg-gray" width="25%" nowrap="nowrap"><label
					for="member.password"><font color="red">*</font><fmt:message
					key="ldap.lable.entry" />:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap"><input
					class="cursor-hand input-100per" type="text" name="ldapUserCodes"
					id="ldapUserCodes" value="<c:out value="${ldapADLoginName}" escapeXml="true" default=""/>" disabled="disabled"  <c:if test="${editstate || addstate != null}">validate="notNull"</c:if> inputName="<fmt:message
					key="ldap.lable.entry" />"
					onclick="openUserTree('${editstate}','${isModify}')"  readOnly/></td>
			</tr>

			<tr  <c:if test="${editstate || addstate != null}">style="display: none;"</c:if> id="newEntryLable">
				<td class="bg-gray" width="25%" nowrap="nowrap"><label
					for="member.password"><fmt:message
					key="ldap.lable.node" bundle="${orgI18N}"/>:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap"><input
					class="cursor-hand input-100per" type="text" name="selectOU"
					id="selectOU" value="" disabled="disabled"
					onclick="openLdap()"/></td>
			</tr>
			</c:if>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="member.password"><font color="red">*</font><fmt:message key="org.member_form.password.label" bundle="${orgI18N}"/>:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">
				<input  <c:if test="${editstate!=null&&isModify}">disabled</c:if> class="input-100per" type="password" name="password" id="member.password"
					 value="<c:out value="${member.v3xOrgMember.password==''?'123456':member.v3xOrgMember.password}" escapeXml="true" default='123456' />"  disabled="disabled"  validate="notNull" deaultValue="${member.v3xOrgMember.password}"
				     inputName="<fmt:message key="org.member_form.password.label" bundle="${orgI18N}"/>" />
				</td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="member.password1"><font color="red">*</font><fmt:message key="org.account_form.adminPass1.label" bundle="${orgI18N}"/>:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">
					<input  <c:if test="${editstate!=null&&isModify}">disabled</c:if> class="input-100per" type="password" name="password1" id="member.password1" validate="notNull"
					value="<c:out value="${member.v3xOrgMember.password==''?'123456':member.v3xOrgMember.password}" escapeXml="true" default='${member.v3xOrgMember.password}' />"  disabled="disabled"  validate="notNull" deaultValue="${member.v3xOrgMember.password}"
					inputName="<fmt:message key="org.account_form.adminPass1.label" bundle="${orgI18N}"/>" />
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td class="description-lable">&nbsp;&nbsp;<fmt:message key="manager.member.vaildate.length" bundle="${sysMgrResources}" /></td>
			</tr>
			<tr>
				<td class="bg-gray" width="25%" nowrap="nowrap"><label for="member.password1"><fmt:message key="org.member_form.primaryLanguange.label" bundle="${orgI18N}"/>:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">

				<select name="primaryLanguange" class="input-100per" disabled="disabled">
				    <c:forEach var="l" items="${v3x:getAllLocales()}">
				        <option value="${l}" ${member.v3xOrgMember.locale == l ? "selected" : ""}><fmt:message key="localeselector.locale.${l}" bundle="${localeI18N}"/></option>
				    </c:forEach>
				</select>
				</td>
			</tr>
			<tr>
				<td class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="organization.member.state" bundle="${orgI18N}"/>:</td>
				<td class="new-column" width="75%" nowrap="nowrap">
				<c:set value="${member.v3xOrgMember.enabled ? 'checked' : ''}" var="c"/>
				<c:set value="${!member.v3xOrgMember.enabled ? 'checked' : ''}" var="d" />
					<label for="enabled1">
						<input id="enabled1" type="radio" name="enabled" value="1" ${c} disabled="disabled" onclick="enabledMem()"/><fmt:message key="common.state.normal.label" bundle="${v3xCommonI18N}"/>
					</label>
					<label for="enabled2">
						<input id="enabled2" type="radio" name="enabled" value="0" ${d} disabled="disabled"/><fmt:message key="common.state.invalidation.label" bundle="${v3xCommonI18N}"/>
					</label>
				</td>
			</tr>
			<tr>
				<td class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.member_form.system_security.label" bundle="${orgI18N}"/>:</td>
				<td class="new-column" width="75%" nowrap="nowrap">
					<input class="cursor-hand input-100per" type="text" name="securityNames" value="${v3x:toHTML(securityNames)}" readonly="readonly" disabled="disabled" onclick="setMenuSecurity('<html:link renderURL="/menuManager.do"/>?method=showAllMenuSecurity&memberId=${param.id}')"  validate="notNull" inputName="<fmt:message key="org.member_form.system_security.label" bundle="${orgI18N}"/>" />
					<input type="hidden" id="securityIds" name="securityIds" value="${securityIds}"/>
				</td>
			</tr>
			<tr>
			    <td class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="secret.old.lable"/>:</td>
				<td>
					<table width="100%">
						<tr>
							<td class="new-column" width="75%">
								<input type="text" id="oldSecretLevel" readonly="readonly" value="<fmt:message key='secretLevel.${member.v3xOrgMember.secretLevel}'/>" />
							</td>
							<td  valign="top" class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="secret.new.lable"/>:</td>
							<td class="new-column" width="75%">
								<input type="text" id="newSecretLevel" readonly="readonly"  value="<fmt:message key='secretLevel.${member.newSecretLevel}'/>" />
							</td>
						</tr>
					</table>
				</td>
			</tr>		
		</table>
	</fieldset>
	<p></p>
	<fieldset style="width:98%;border:0px;" height="50" align="center">
			<table width="97%" height="65" border="0" cellspacing="0" cellpadding="0" align="center">
				<tr>
					<td class="bg-gray" nowrap="nowrap" align="right">
						<div class="hr-blue-font"><strong><fmt:message key="member.move.info" bundle="${orgI18N}" /></strong></div>
					</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td  valign="top" class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.memberext_form.base_fieldset.sexe" bundle="${orgI18N}" />:</td>
					<td class="new-column" width="75%">
	              <select name="gender" id="gender" inputName="<fmt:message key="org.memberext_form.base_fieldset.sexe" bundle="${orgI18N}" />" class="input-100per" disabled="disabled"}>
	              	<c:if test="${member.v3xOrgMember.gender == null || member.v3xOrgMember.gender == -1}">
						<option value=""></option>
					</c:if>
                    <option value="1" ${1==member.v3xOrgMember.gender ? 'selected' : ''}>
                        <fmt:message key='org.memberext_form.base_fieldset.sexe.man' bundle="${orgI18N}" />
                    </option>
                    <option value="2" ${2==member.v3xOrgMember.gender ? 'selected' : ''}>
                        <fmt:message key='org.memberext_form.base_fieldset.sexe.woman' bundle="${orgI18N}" />
                    </option>
                  </select>
					</td>
				</tr>
				<tr>
					<td  valign="top" class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.memberext_form.base_fieldset.birthday" bundle="${orgI18N}" />:</td>
					<td class="new-column" width="75%">
				 <input type="text" name="birthday" id="birthday" class="input-100per" onClick="whenstart('${pageContext.request.contextPath}', this, 175, 140);" readonly ${mdis} disabled="disabled"
                  value="<fmt:formatDate value="${member.v3xOrgMember.birthday}" type="both" dateStyle="full" pattern="yyyy-MM-dd"/>" >
   					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="name"><fmt:message key="member.office.number" bundle="${orgI18N}" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<input name="officeNum" type="text" id="officeNum" class="input-100per" maxSize="20" maxLength="20" value="<c:out  value="${empty member.officeNum ? '' : member.officeNum}" escapeXml="true" />"
						   disabled="disabled" />
					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="name"><fmt:message key="member.move.number" bundle="${orgI18N}" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<fmt:message key="member.move.dot" var="defNames"/>
						<input name="telNumber" type="text" id="telNumber" class="input-100per" maxSize="20" maxLength="20"  inputName="<fmt:message key="hr.staffInfo.mobileTelephone.label"  bundle="${v3xHRI18N}"/>" validate="isPhoneNumber,maxLength"
						    value="<c:out  value="${empty member.v3xOrgMember.telNumber ? '' : member.v3xOrgMember.telNumber}" escapeXml="true" />"
						    disabled="disabled" />
					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="name"><fmt:message key="org.member.emailaddress" bundle="${orgI18N}" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<input name="emailAddress" inputName="<fmt:message key='org.member.emailaddress' bundle="${orgI18N}" />" type="text" id="emailAddress" class="input-100per" maxSize="40" maxLength="40" value="<c:out  value="${member.v3xOrgMember.emailAddress}" escapeXml="true" />" disabled="disabled" validate="isEmail,maxLength" />
					</td>
				</tr>
				
				<c:if test ="${(v3x:getSysFlagByName('sys_isGovVer')=='true')}">
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="ID_card"><fmt:message key="org.memberext_form.base_fieldset.IDCard" bundle="${orgI18N}" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<input name="ID_card" inputName="<fmt:message key='org.memberext_form.base_fieldset.IDCard' bundle="${orgI18N}" />" type="text" id="ID_card" class="input-100per" maxSize="40" maxLength="18" value="<c:out  value="${staff.ID_card}" escapeXml="true" />" disabled="disabled" validate="maxLength" />
					</td>
				</tr>
					<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="edu_level"><fmt:message key="org.memberext_form.base_fieldset.edu_level" bundle="${orgI18N}" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
							<select class="input-100per" name="edu_level" id="edu_level" disabled="disabled">
				                 <option value="-1"></option>
				                 <v3x:metadataItem metadata="${hrMetadata['hr_staffInfo_edulevel']}" showType="option" name="edu_level"	selected="${staff.edu_level}"/>
				  			</select>
					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="degreeLevel"><fmt:message key="org.memberext_form.base_fieldset.degreeLevel" bundle="${orgI18N}" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<input name="degreeLevel" inputName="<fmt:message key='org.memberext_form.base_fieldset.degreeLevel'/>" type="text" id="degreeLevel" class="input-100per" maxSize="40" maxLength="100" value="<c:out  value='${staff.degreeLevel}' escapeXml='true' />" disabled="disabled" validate="maxLength" />
					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="political_position"><fmt:message key="org.memberext_form.base_fieldset.political_position" bundle="${orgI18N}" />:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
							<select class="input-100per" name="political_position" id="political_position" disabled="disabled" ${mdis}>
				                <option value="-1"></option>
				                <v3x:metadataItem metadata="${hrMetadata['hr_staffInfo_position']}" showType="option" name="political_position" selected="${staff.political_position}"/>
				  			</select>
					</td>
				</tr>
				</c:if>
				<tr>
					<td  valign="top" class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.member_form.descript_fieldset.label" bundle="${orgI18N}" />:</td>
					<td class="new-column" width="75%">
						<textarea id="memberDescription" disabled="disabled" class="input-100per" maxSize="1000" maxlength="1000" inputName="<fmt:message key="org.member_form.descript_fieldset.label" bundle="${orgI18N}" />" validate="maxLength" name="description" rows="4" cols="67" >${member.v3xOrgMember.description}</textarea>
					</td>
				</tr>
			</table>
		</fieldset>
		<p></p>



    </td>
    <td  valign="top">
    	<fmt:message key="org.member.noPost" var="noPostLabel"/>
    	<fieldset style="width:95%;border:0px;" align="center">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr>
				<td class="bg-gray" nowrap="nowrap" align="right">
					<div class="hr-blue-font"><strong><fmt:message key="org.member_form.org_fieldset.label" bundle="${orgI18N}" /></strong></div>
				</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><font color="red">*</font><fmt:message key="org.member_form.deptName.label" bundle="${orgI18N}" />:</td>
				<td class="new-column" width="75%" nowrap="nowrap">
				<input class="cursor-hand input-100per" type="text" name="deptName" readonly="readonly" disabled="disabled"
					id="deptName"
					value="<c:out value="${member.departmentName}" escapeXml="true" />" onclick="memberSelectDepartment()" validate="notNull"
					inputName="<fmt:message key="org.member_form.deptName.label" bundle="${orgI18N}"  />" />
					<input type="hidden" name="orgDepartmentId" id="orgDepartmentId" value="${member.v3xOrgMember.orgDepartmentId}"/>
			  </td>
			</tr>
			<tr>
			  	<!-- branches_a8_v350_r_gov GOV-1097 lijl Add-->
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="member.code"><fmt:message key="org.member_form.code${v3x:suffix()}" bundle="${orgI18N}" />:</label></td>
				<td class="new-column" width="75%">
				<input class="input-100per" type="text" name="code" id="member.code"  maxSize="20" maxLength="20" disabled="disabled"
					value="<c:out value="${member.v3xOrgMember.code}" escapeXml="true" />" /></td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><font color="red">*</font><fmt:message key="common.sort.label" bundle="${v3xCommonI18N}" />:</td>
				<td class="new-column" width="75%">
				<input class="input-25per" maxlength="10" min="1" type="text" name="sortId" id="member.sortId"  value="${member.v3xOrgMember.sortId}"
					inputName="<fmt:message key="common.sort.label" bundle="${v3xCommonI18N}" />" disabled="disabled"
					validate="isInteger,notNull"/>
					<fmt:message key="org.sort.repeat.deal" bundle="${orgI18N}" />:<label for="isInsert1"><input id="isInsert1" name="isInsert" type="radio" value="1" checked disabled="disabled"><fmt:message key="org.sort.insert" bundle="${orgI18N}"/></label><label for="isInsert2"><input id="isInsert2" name="isInsert" type="radio" value="0" disabled="disabled"><fmt:message key="org.sort.repeat" bundle="${orgI18N}"/></label></td>
			</tr>
			<tr>
			<td colspan="2">
			<div id="intenalDiv">
		    <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><font color="red">*</font><fmt:message key="org.member_form.primaryPost.label" bundle="${orgI18N}" />:</td>
				<td class="new-column" width="75%" nowrap="nowrap">

				<input type="text" readonly="readonly" disabled="disabled"
					name="postName" id="postName" class="cursor-hand input-100per"
					 value="${member.v3xOrgMember.orgPostId!=-1? member.postName : type=='create'? '':noPostLabel}" onclick="selectPeopleFun_post()" validate="notNull"
					 inputName="<fmt:message key="org.member_form.primaryPost.label" bundle="${orgI18N}" />" />
					<input type="hidden" name="orgPostId" id="orgPostId" value="${member.v3xOrgMember.orgPostId}"/>
			  </td>
			</tr>
			<tr>
				<td class="bg-gray" width="25%" nowrap="nowrap"><label for="deptId1"><fmt:message key="org.member_form.secondPost.label" bundle="${orgI18N}" />:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">
					<input class="cursor-hand input-100per" type="text" name="secondPosts" readonly="readonly" id="secondPosts" disabled="disabled" value="<c:out value="${member.secondPosts }" escapeXml="true" />" onclick="selectPeopleFun_assistantPosts()"/>
					<input type="hidden" name="secondPostIds" id="secondPostIds" value="${secondPostM}" />
				</td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><font color="red">*</font><fmt:message key="org.member_form.levelName.label${v3x:suffix()}" bundle="${orgI18N}" />:</td>
				<td class="new-column" width="75%">
	              <select name="orgLevelId" id="orgLevelId" 
	              <c:if test ="${(v3x:getSysFlagByName('sys_isGovVer')=='true')}">
					validate="notNull"
				  </c:if>  
				  inputName="<fmt:message key="org.member_form.levelName.label${v3x:suffix()}" bundle="${orgI18N}" />" class="input-100per" disabled="disabled"}>
	              	<c:if test="${member.v3xOrgMember.orgLevelId == -1 && type!='create'}">
						<option value="-1">${noPostLabel}</option>
					</c:if>
					<c:if test="${type!='create'}">
                    <c:forEach items="${levels}" var="item">
                    <option value="${item.id }" ${item.id==member.v3xOrgMember.orgLevelId ? 'selected' : ''}>
                        <c:out value="${item.name}" escapeXml="true" />
                    </option>
                    </c:forEach>
                    </c:if>
                    <c:if test="${type=='create'}">
                    <c:forEach items="${levels}" var="item">
                    <option value="${item.id }" ${item.levelId==minLevelId ? 'selected' : ''}>
                        <c:out value="${item.name}" escapeXml="true" />
                    </option>
                    </c:forEach>
                    </c:if>
                  </select>
			  </td>
			</tr>
			<%--政务版  职级 --%>
			<c:if test ="${(v3x:getSysFlagByName('sys_isGovVer')=='true')}">
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.member_form.dutylevelName.label${v3x:suffix()}" bundle="${orgI18N}" />:</td>
				<td class="new-column" width="75%">
	              <select name="orgDutyLevelId" id="orgDutyLevelId" inputName="<fmt:message key="org.member_form.dutylevelName.label${v3x:suffix()}" bundle="${orgI18N}" />" class="input-100per" disabled="disabled">
	              	<c:if test="${member.v3xOrgMember.orgDutyLevelId == -1 && type!='create'}">
						<option value="-1">${noPostLabel}</option>
					</c:if>
					<c:if test="${type!='create'}">
                    <c:forEach items="${dutyLevels}" var="item">
                    <option value="${item.id }" ${item.id==member.v3xOrgMember.orgDutyLevelId ? 'selected' : ''}>
                        <c:out value="${item.name}" escapeXml="true" />
                    </option>
                    </c:forEach>
                    </c:if>
                    <c:if test="${type=='create'}">
                    <c:forEach items="${dutyLevels}" var="item">
                    <option value="${item.id }" ${item.levelId==minDutyLevelId ? 'selected' : ''}>
                        <c:out value="${item.name}" escapeXml="true" />
                    </option>
                    </c:forEach>
                    </c:if>
                  </select>
			  </td>
			</tr>
			</c:if>
			<%--政务版  职级 end--%>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="type"><fmt:message key="org.metadata.member_type.label" bundle="${orgI18N}" />:</label></td>
				<td class="new-column" width="75%">
				<c:set var="allMemberType" value="${orgMeta['org_property_member_type']}" scope="page" />
				<c:set var="memberTypeByteOfCurrentUser" value="${member.v3xOrgMember.type}" scope="page" />
				<%
					Metadata allMemberType = (Metadata) pageContext.getAttribute("allMemberType");
					Byte memberTypeByteOfCurrentUser = (Byte) pageContext.getAttribute("memberTypeByteOfCurrentUser");
					MetadataItem memberTypeOfCurrentUser = allMemberType.getItem(memberTypeByteOfCurrentUser.toString());
					boolean isDisabled = false;
					if (memberTypeOfCurrentUser != null && memberTypeOfCurrentUser.getState() != null && memberTypeOfCurrentUser.getState().intValue() == Constants.METADATAITEM_SWITCH_DISABLE) {
						isDisabled = true;
					}
					if (isDisabled) {
				%>
				<input id="disabledMemberType" type="hidden" value="<c:out value="${member.v3xOrgMember.type}" escapeXml="true" />" />
				<input id="disabledMemberTypeLabel" type="hidden" value="<v3x:metadataItemLabel metadata="${orgMeta['org_property_member_type']}" value="${member.v3xOrgMember.type}" />" />
				<%
					}
				%>
				<select class="input-100per" name="type" id="type" disabled="disabled">
				<v3x:metadataItem metadata="${orgMeta['org_property_member_type']}" showType="option" name="type"
				  selected="${member.v3xOrgMember.type}"/>
				<%
					if (isDisabled) {
				%>
				<option value="<c:out value="${member.v3xOrgMember.type}" escapeXml="true" />" selected><v3x:metadataItemLabel metadata="${orgMeta['org_property_member_type']}" value="${member.v3xOrgMember.type}" /></option>
				<%
					}
				%>
				  </select>
				  </td>
			</tr>

			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="state"><fmt:message key="org.metadata.member_state.label" bundle="${orgI18N}" />:</label></td>
				<td class="new-column" width="75%">
				<select class="input-100per" name="state" id="state" disabled="disabled" onchange="changeState()" disabled="disabled">
				<v3x:metadataItem metadata="${orgMeta['org_property_member_state']}" showType="option" name="state"
				  selected="${member.v3xOrgMember.state}"/>
				  </select>
				</td>
			</tr>
			<c:if test="${cntList!=null}">
				<tr>
					<td class="bg-gray" width="25%" nowrap="nowrap" valign="top"><label for="cnt"><fmt:message key="org.member_form.cnt.label" bundle="${orgI18N}" />:</label></td>
					<td class="new-column" width="75%">
					<div style="border: 1px #7F9DB9 solid; width: 100%; height: 70px; overflow: scroll;">
					<table width="100%">
					<c:forEach items="${cntList}" var="cnt">
					<tr>
						<td>
							&nbsp;<label for="state"><fmt:message key="org.member_form.cnt.account" bundle="${orgI18N}" />:</label>&nbsp;<c:out  value="${v3x:showOrgEntitiesOfIds(cnt.cntAccountId, 'Account', pageContext)}" escapeXml="true" />
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;<label for="state"><fmt:message key="org.member_form.cnt.post" bundle="${orgI18N}" />:</label>&nbsp;<c:out value="${v3x:showOrgEntitiesOfIds(cnt.cntDepId, 'Department', pageContext)}" escapeXml="true" />-<c:out value="${v3x:showOrgEntitiesOfIds(cnt.cntPostId, 'Post', pageContext)}" escapeXml="true" />
						</td>
					</tr>
					</c:forEach>
					</table>
					</div>
					</td>
				</tr>
		    </c:if>
			</table>
			</div>
			</td>
			</tr>
		</table>
	</fieldset>
	<p></p>

	<fieldset style="width:95%;border:0px;" align="center">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap" valign="top"><strong><fmt:message key="hr.staffInfo.photo.label" bundle="${v3xHRI18N}"/></strong>:</td>
				<td class="new-column" width="106px" nowrap="nowrap">
					<div style="border: 1px #CCC solid; width:106px; height:126px; text-align: center; background-color: #FFF;">
						<div id="thePicture" style="width:100px; height:120px; margin-top: 2px; text-align: center;">
							<c:choose>
								<c:when test="${image == '0'}">
									<img src="/seeyon/fileUpload.do?method=showRTE&fileId=${staff.image_id}
										&createDate=<fmt:formatDate value='${staff.image_datetime}' type='both' dateStyle='full' pattern='yyyy-MM-dd'/>&type=image" width="100" height="120" />
								</c:when>
								<c:otherwise>
									<img src="<c:url value="/apps_res/hr/images/photo.JPG" />" width="100" height="120" />
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</td>
				<td class="new-column description-lable" nowrap="nowrap" align="left" valign="bottom">
	                <c:if test="${!readOnly}">
	                	100*120px<br><br>
				    	<input type="button" size="100" disabled="disabled" value="<fmt:message key="hr.staffInfo.browse.label" bundle="${v3xHRI18N}" />...  " onclick="changeImage()">
				    	<br><br>
				    	<input type="hidden" id="fileId" name="fileId" value="${staff.image_id}">
				    	<input type="hidden" id="createDate" name="createDate" value="<fmt:formatDate value='${staff.image_datetime}' type='both' dateStyle='full' pattern='yyyy-MM-dd'/>">
				    </c:if>
				</td>
			</tr>
		</table>
	</fieldset>
	<p></p>

    </td>
  </tr>
</table>
<div style="display:none;">
	<table>
		<tr id="attachmentTR" class="bg-summary" style="display:none;">
			<td nowrap="nowrap" height="18" class="bg-gray" valign="top"><fmt:message key="common.attachment.label" bundle="${v3xCommonI18N}" /></td>
			<td colspan="8" valign="top"><div class="div-float">(<span id="attachmentNumberDiv"></span>ï¿½ï¿½)</div>
			<v3x:fileUpload extensions="gif,jpg,jpeg,bmp,png" maxSize="512000" />
			<script>
				var fileUploadQuantity = 1;
			</script>
			</td>
		</tr>
	</table>
</div>
<script type="text/javascript">
<!--
	
//-->
</script>