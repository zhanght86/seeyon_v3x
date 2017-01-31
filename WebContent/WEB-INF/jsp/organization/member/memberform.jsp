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
<c:set var="dis" value="${v3x:outConditionExpression(readOnly, 'disabled', '')}" />
<c:set var="ro" value="${v3x:outConditionExpression(readOnly, 'readOnly', '')}" />
<input type="hidden" id="depsPathStr" value="${depsPathStr}">
<input type="hidden" id="forAgentState" name="forAgentState" value="${member.v3xOrgMember.state}">
<table width="100%" border="0" cellspacing="0" height="96%" cellpadding="0" align="center">
	<tr>
	<td height="28" class="categorySetTitle" colspan="2"><font color="red">*</font><fmt:message key="level.must.write" /></td>
	</tr>
  <tr valign="top">
    <td width="50%">
    <fieldset style="width:95%;border:0px;" align="center">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr>
				<td class="bg-gray" nowrap="nowrap" align="right">
					<div class="hr-blue-font"><strong><fmt:message key="org.member_form.system_fieldset.label"/></strong></div>
				</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label
					for="name"><font color="red">*</font><fmt:message key="org.member_form.name.label"/>:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">
					<fmt:message key="common.default.name.value" var="defName" bundle="${v3xCommonI18N}"/>
					<input name="name" type="text" id="name" class="input-100per" deaultValue="${defName}"
						inputName="<fmt:message key="org.member_form.name.label" />" maxSize="40" maxlength="40" validate="notNull,isDeaultValue,maxLength,isWord" character="|,'&quot;"
					    value="<c:out value="${member.v3xOrgMember.name}" escapeXml="true" default='${defName}' />"
					     ${ro}
					    onfocus='checkDefSubject(this, true)' onblur="checkDefSubject(this, false)">
				</td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="member.loginName"><font color="red">*</font><fmt:message key="org.member_form.loginName.label" />:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">
				<input class="input-100per" type="text" name="loginName" ${ro} maxSize="40" maxLength="40"
					id="member.loginName" value="${member.v3xOrgMember.loginName}" validate="notNull,isWord"
					    value="<c:out value="${member.v3xOrgMember.name}" escapeXml="true" default='${defName}' />"
					inputName="<fmt:message key="org.member_form.loginName.label" />" onfocus="setLoginName();"/></td>
			</tr>
			<c:if test="${hasLDAPAD}">
			<tr <c:if test="${editstate}">style="display: none;"</c:if>>
				<td class="bg-gray" width="25%" nowrap="nowrap"><fmt:message
					key="ldap.lable.type" />:</td>
				<td class="new-column" width="75%" nowrap="nowrap">
				<label for="ldapType1"> <input id="ldapType1" type="radio"
					name="ldapType" value="1" onclick="hiddenEntry()" ${dis} /><fmt:message
					key="ldap.lable.new" /> </label> <label for="ldapType2"> <input
					id="ldapType2" type="radio" name="ldapType" value="0"
					 onclick="showEntry()" ${dis} checked="checked"/><fmt:message key="ldap.lable.select" /> </label>
				</td>
			</tr>
				<tr <c:if test="${addstate && editstate != null}">style="display: none;"</c:if> id="entryLable">
				<td class="bg-gray" width="25%" nowrap="nowrap"><label
					for="member.password"><font color="red">*</font><fmt:message
					key="ldap.lable.entry" />:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap"><input
					class="cursor-hand input-100per" type="text" name="ldapUserCodes"
					id="ldapUserCodes" value="<c:out value="${ldapADLoginName}" escapeXml="true" default=""/>" ${dis}  <c:if test="${editstate || addstate != null}">validate="notNull"</c:if> inputName="<fmt:message
					key="ldap.lable.entry" />"
					onclick="openUserTree('${editstate}','${isModify}')"  readOnly/></td>
			</tr>

			<tr  <c:if test="${editstate || addstate != null}">style="display: none;"</c:if> id="newEntryLable">
			<fmt:setBundle basename="com.seeyon.v3x.organization.resources.i18n.OrganizationResources" var="organizationResources"/>
				<td class="bg-gray" width="25%" nowrap="nowrap"><label
					for="member.password"><fmt:message
					key="ldap.lable.node" bundle="${organizationResources}"/>:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap"><input
					class="cursor-hand input-100per" type="text" name="selectOU"
					id="selectOU" value="" ${dis}
					onclick="openLdap()"/></td>
			</tr>
			</c:if>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="member.password"><font color="red">*</font><fmt:message key="org.member_form.password.label" />:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">
				<input  <c:if test="${editstate!=null&&isModify}">disabled</c:if> class="input-100per" type="password" name="password" id="member.password"
					 value="<c:out value="${member.v3xOrgMember.password==''?'123456':member.v3xOrgMember.password}" escapeXml="true" default='123456' />"  ${dis}  validate="notNull" deaultValue="${member.v3xOrgMember.password}"
				     inputName="<fmt:message key="org.member_form.password.label" />" />
				</td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="member.password1"><font color="red">*</font><fmt:message key="org.account_form.adminPass1.label" />:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">
					<input  <c:if test="${editstate!=null&&isModify}">disabled</c:if> class="input-100per" type="password" name="password1" id="member.password1" validate="notNull"
					value="<c:out value="${member.v3xOrgMember.password==''?'123456':member.v3xOrgMember.password}" escapeXml="true" default='${member.v3xOrgMember.password}' />"  ${dis}  validate="notNull" deaultValue="${member.v3xOrgMember.password}"
					inputName="<fmt:message key="org.account_form.adminPass1.label" />" />
				</td>
			</tr>
			<fmt:setBundle basename="com.seeyon.v3x.system.resources.i18n.SysMgrResources" />
			<tr>
				<td>&nbsp;</td>
				<td class="description-lable">&nbsp;&nbsp;<fmt:message key="manager.member.vaildate.length"/></td>
			</tr>
			<fmt:setBundle basename="com.seeyon.v3x.organization.resources.i18n.OrganizationResources"/>
			<tr>
				<td class="bg-gray" width="25%" nowrap="nowrap"><label for="member.password1"><fmt:message key="org.member_form.primaryLanguange.label" />:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">

				<select name="primaryLanguange" class="input-100per" ${dis}>
				    <c:forEach var="l" items="${v3x:getAllLocales()}">
				        <option value="${l}" ${member.v3xOrgMember.locale == l ? "selected" : ""}><fmt:message key="localeselector.locale.${l}" bundle="${localeI18N}"/></option>
				    </c:forEach>
				</select>
				</td>
			</tr>
			<tr>
				<td class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="organization.member.state"/>:</td>
				<td class="new-column" width="75%" nowrap="nowrap">
				<c:set value="${member.v3xOrgMember.enabled ? 'checked' : ''}" var="c"/>
				<c:set value="${!member.v3xOrgMember.enabled ? 'checked' : ''}" var="d" />
				<c:set value="${v3x:outConditionExpression(!member.v3xOrgMember.enabled, 'disabled', '')}" var="rdis"/>
					<label for="enabled1">
						<input id="enabled1" type="radio" name="enabled" value="1" ${c} ${dis} onclick="enabledMem()"/><fmt:message key="common.state.normal.label" bundle="${v3xCommonI18N}"/>
					</label>
					<label for="enabled2">
						<input id="enabled2" type="radio" name="enabled" value="0" ${d} ${dis}/><fmt:message key="common.state.invalidation.label" bundle="${v3xCommonI18N}"/>
					</label>
				</td>
			</tr>
			<tr>
				<td class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.member_form.system_security.label"/>:</td>
				<td class="new-column" width="75%" nowrap="nowrap">
					<input class="cursor-hand input-100per" type="text" name="securityNames" value="${v3x:toHTML(securityNames)}" readonly="readonly" ${dis} onclick="setMenuSecurity('<html:link renderURL="/menuManager.do"/>?method=showAllMenuSecurity&memberId=${param.id}')" ${ro} validate="notNull" inputName="<fmt:message key="org.member_form.system_security.label"/>" />
					<input type="hidden" id="securityIds" name="securityIds" value="${securityIds}"/>
				</td>
			</tr>
			<!-- 成发集团二开项目 增加涉密 -->
			<tr>
				<td  valign="top" class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="secretLevel" bundle="${v3xSecretI18N}"/>:</td>
				<td class="new-column" width="75%">
	              	<select name="secretLevel"  id="secretLevel" inputName="<fmt:message key="secretLevel" bundle="${v3xSecretI18N}"/>" class="input-100per" ${v3x:outConditionExpression(readOnly, 'disabled', '')}>
	                    <!-- 2017-01-11 诚佰公司 -->
	                    <option value=""></option>
	                    <!-- 诚佰公司 -->
	                    <option value="1"  ${1==member.v3xOrgMember.secretLevel ? 'selected' : ''}>
	                        <fmt:message key='secretLevel.1' bundle="${v3xSecretI18N}"/>
	                    </option>
	                    <option value="2" ${2==member.v3xOrgMember.secretLevel ? 'selected' : ''}>
	                        <fmt:message key='secretLevel.2' bundle="${v3xSecretI18N}"/>
	                    </option>
	                    <option value="3" ${3==member.v3xOrgMember.secretLevel ? 'selected' : ''}>
	                         <fmt:message key='secretLevel.3' bundle="${v3xSecretI18N}"/>
	                    </option>
                  	</select>
				</td>
			</tr>
			<!-- 成发集团二开项目 增加涉密等级 -->
			<%-- <tr>
				<td class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.member_form.space.label"/>:</td>
				<td class="new-column" width="75%" nowrap="nowrap">
					<select name="spaceId" class="input-100per" ${dis}>
				    <c:forEach var="space" items="${spaceList}">
				        <option value="${space.id}" ${currentSpaceId == space.id?'selected':''}>${space.name }</option>
				    </c:forEach>
					</select>
				</td>
			</tr> --%>
			<c:if test="${!empty roleNameList}">
				<tr>
					<td class="bg-gray" width="25%" valign="top">
						<label for="member.roles"><fmt:message key="org.member.roles" />:</label>
					</td>
					<td class="new-column" width="75%">
						<div style="border: 1px #7F9DB9 solid; width: 100%; height: 70px; overflow:scroll;overflow-x: hidden;">
							<c:forEach items="${roleNameList}" var="rnl">
								<fmt:setBundle basename="com.seeyon.v3x.system.resources.i18n.SysMgrResources" var="org"/>
								<c:if test="${rnl[0] != ''}">
									<c:out value="${rnl[0]}"/>-
								</c:if>
								<c:choose>
									<c:when test="${rnl[1]=='SystemAdmin'||rnl[1]=='AccountAdmin'||rnl[1]=='AccountManager'||rnl[1]=='DepAdmin'||rnl[1]=='GroupAdmin'||rnl[1]=='HrAdmin'||rnl[1]=='SalaryAdmin'||rnl[1]=='DepManager'||rnl[1]=='SuperManager'||rnl[1]=='department_exchange'||rnl[1]=='account_exchange'||rnl[1]=='FormAdmin'||rnl[1]=='account_edoccreate'||rnl[1]=='ProjectBuild'||rnl[1]=='DepLeader'||rnl[1]=='AccountEdocAdmin'||rnl[1]=='AccountInfoAdmin'||rnl[1]=='UnitsMeetingAdmin'}">
										<c:choose>
											<c:when test="${rnl[1]=='HrAdmin'}">
											<fmt:message key="sys.role.rolename.${rnl[1]}${v3x:suffix()}"  bundle='${org}' />
											</c:when>
											<c:otherwise>
											<fmt:message key="sys.role.rolename.${rnl[1]}"  bundle='${org}' />
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:out value="${rnl[1]}"/>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</div>
					</td>
				<tr>
			</c:if>
		</table>
	</fieldset>
	<p></p>
	<fieldset style="width:98%;border:0px;" height="50" align="center">
			<table width="97%" height="65" border="0" cellspacing="0" cellpadding="0" align="center">
				<tr>
					<td class="bg-gray" nowrap="nowrap" align="right">
						<div class="hr-blue-font"><strong><fmt:message key="member.move.info"/></strong></div>
					</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td  valign="top" class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.memberext_form.base_fieldset.sexe"/>:</td>
					<td class="new-column" width="75%">
	              <select name="gender" id="gender" inputName="<fmt:message key="org.memberext_form.base_fieldset.sexe"/>" class="input-100per" ${v3x:outConditionExpression(readOnly, 'disabled', '')}>
	              	<c:if test="${member.v3xOrgMember.gender == null || member.v3xOrgMember.gender == -1}">
						<option value=""></option>
					</c:if>
                    <option value="1" ${1==member.v3xOrgMember.gender ? 'selected' : ''}>
                        <fmt:message key='org.memberext_form.base_fieldset.sexe.man'/>
                    </option>
                    <option value="2" ${2==member.v3xOrgMember.gender ? 'selected' : ''}>
                        <fmt:message key='org.memberext_form.base_fieldset.sexe.woman'/>
                    </option>
                  </select>
					</td>
				</tr>
				<tr>
					<td  valign="top" class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.memberext_form.base_fieldset.birthday"/>:</td>
					<td class="new-column" width="75%">
				 <input type="text" name="birthday" id="birthday" class="input-100per" onClick="whenstart('${pageContext.request.contextPath}', this, 175, 140);" readonly ${mdis}${dis}
                  value="<fmt:formatDate value="${member.v3xOrgMember.birthday}" type="both" dateStyle="full" pattern="yyyy-MM-dd"/>" >
   					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="name"><fmt:message key="member.office.number"/>:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<input name="officeNum" type="text" id="officeNum" class="input-100per" maxSize="66" maxLength="66" validate="maxLength" value="<c:out  value="${empty member.officeNum ? '' : member.officeNum}" escapeXml="true" />"
						    ${dis} />
					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="name"><fmt:message key="member.move.number"/>:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<fmt:message key="member.move.dot" var="defNames"/>
						<input name="telNumber" type="text" id="telNumber" class="input-100per" maxSize="20" maxLength="20"  inputName="<fmt:message key="hr.staffInfo.mobileTelephone.label"  bundle="${v3xHRI18N}"/>" validate="isPhoneNumber,maxLength"
						    value="<c:out  value="${empty member.v3xOrgMember.telNumber ? '' : member.v3xOrgMember.telNumber}" escapeXml="true" />"
						    ${dis} />
					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="name"><fmt:message key="org.member.emailaddress"/>:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<input name="emailAddress" inputName="<fmt:message key='org.member.emailaddress'/>" type="text" id="emailAddress" class="input-100per" maxSize="40" maxLength="40" value="<c:out  value="${member.v3xOrgMember.emailAddress}" escapeXml="true" />" ${dis} validate="isEmail,maxLength" />
					</td>
				</tr>
				
				<%--政务版 新增四个hr的字段 start --%>
				<c:if test ="${(v3x:getSysFlagByName('sys_isGovVer')=='true')}">
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="ID_card"><fmt:message key="org.memberext_form.base_fieldset.IDCard"/>:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<input name="ID_card" inputName="<fmt:message key='org.memberext_form.base_fieldset.IDCard'/>" type="text" id="ID_card" class="input-100per" maxSize="40" maxLength="18" value="<c:out  value="${staff.ID_card}" escapeXml="true" />" ${dis} validate="maxLength" />
					</td>
				</tr>
					<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="edu_level"><fmt:message key="org.memberext_form.base_fieldset.edu_level"/>:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
							<select class="input-100per" name="edu_level" id="edu_level" ${dis}>
				                 <option value="-1"></option>
				                 <v3x:metadataItem metadata="${hrMetadata['hr_staffInfo_edulevel']}" showType="option" name="edu_level"	selected="${staff.edu_level}"/>
				  			</select>
					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="degreeLevel"><fmt:message key="org.memberext_form.base_fieldset.degreeLevel"/>:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
						<input name="degreeLevel" inputName="<fmt:message key='org.memberext_form.base_fieldset.degreeLevel'/>" type="text" id="degreeLevel" class="input-100per" maxSize="40" maxLength="100" value="<c:out  value='${staff.degreeLevel}' escapeXml='true' />" ${dis} validate="maxLength" />
					</td>
				</tr>
				<tr>
				  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="political_position"><fmt:message key="org.memberext_form.base_fieldset.political_position"/>:</label></td>
					<td class="new-column" width="75%" nowrap="nowrap">
							<select class="input-100per" name="political_position" id="political_position" ${dis} ${mdis}>
				                <option value="-1"></option>
				                <v3x:metadataItem metadata="${hrMetadata['hr_staffInfo_position']}" showType="option" name="political_position" selected="${staff.political_position}"/>
				  			</select>
					</td>
				</tr>
				</c:if>
				<%--政务版 新增四个hr的字段 end --%>
				<tr>
					<td  valign="top" class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.member_form.descript_fieldset.label"/>:</td>
					<td class="new-column" width="75%">
						<textarea id="memberDescription" maxSize="1000" maxlength="1000" inputName="<fmt:message key="org.member_form.descript_fieldset.label"/>" validate="maxLength" name="description" rows="4" cols="80" ${ro}>${member.v3xOrgMember.description}</textarea>
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
					<div class="hr-blue-font"><strong><fmt:message key="org.member_form.org_fieldset.label"/></strong></div>
				</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><font color="red">*</font><fmt:message key="org.member_form.deptName.label" />:</td>
				<td class="new-column" width="75%" nowrap="nowrap">
				<input class="cursor-hand input-100per" type="text" name="deptName" readonly="readonly" ${dis}
					id="deptName"
					value="<c:out value="${member.departmentName}" escapeXml="true" />" onclick="memberSelectDepartment()" validate="notNull"
					inputName="<fmt:message key="org.member_form.deptName.label" />" />
					<input type="hidden" name="orgDepartmentId" id="orgDepartmentId" value="${member.v3xOrgMember.orgDepartmentId}"/>
			  </td>
			</tr>
			<tr>
			  	<!-- branches_a8_v350_r_gov GOV-1097 lijl Add-->
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="member.code"><fmt:message key="org.member_form.code${v3x:suffix()}"/>:</label></td>
				<td class="new-column" width="75%">
				<input class="input-100per" type="text" name="code" id="member.code" ${ro} maxSize="20" maxLength="20"
					value="<c:out value="${member.v3xOrgMember.code}" escapeXml="true" />" /></td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><font color="red">*</font><fmt:message key="common.sort.label" bundle="${v3xCommonI18N}" />:</td>
				<td class="new-column" width="75%">
				<input class="input-25per" maxlength="10" min="1" type="text" name="sortId" id="sortId" ${ro} value="${member.v3xOrgMember.sortId}"
					inputName="<fmt:message key="common.sort.label" bundle="${v3xCommonI18N}" />"
					validate="isInteger,notNull"/>
					<fmt:message key="org.sort.repeat.deal" />:<label for="isInsert1"><input id="isInsert1" name="isInsert" type="radio" value="1" checked ${dis}><fmt:message key="org.sort.insert" /></label><label for="isInsert2"><input id="isInsert2" name="isInsert" type="radio" value="0" ${dis}><fmt:message key="org.sort.repeat" /></label></td>
			</tr>
			<tr>
			<td colspan="2">
			<div id="intenalDiv">
		    <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><font color="red">*</font><fmt:message key="org.member_form.primaryPost.label" />:</td>
				<td class="new-column" width="75%" nowrap="nowrap">

				<input type="text" readonly="readonly" ${dis}
					name="postName" id="postName" class="cursor-hand input-100per"
					 value="${member.v3xOrgMember.orgPostId!=-1? member.postName : type=='create'? '':noPostLabel}" onclick="selectPeopleFun_post()" validate="notNull"
					 inputName="<fmt:message key="org.member_form.primaryPost.label" />" />
					<input type="hidden" name="orgPostId" id="orgPostId" value="${member.v3xOrgMember.orgPostId}"/>
			  </td>
			</tr>
			<tr>
				<td class="bg-gray" width="25%" nowrap="nowrap"><label for="deptId1"><fmt:message key="org.member_form.secondPost.label" />:</label></td>
				<td class="new-column" width="75%" nowrap="nowrap">
					<input class="cursor-hand input-100per" type="text" name="secondPosts" readonly="readonly" id="secondPosts" ${dis} value="<c:out value="${member.secondPosts }" escapeXml="true" />" onclick="selectPeopleFun_assistantPosts()"/>
					<input type="hidden" name="secondPostIds" id="secondPostIds" value="${secondPostM}" />
				</td>
			</tr>
			<tr>
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><font color="red">*</font><fmt:message key="org.member_form.levelName.label${v3x:suffix()}" />:</td>
				<td class="new-column" width="75%">
	              <select name="orgLevelId" id="orgLevelId" 
	              <c:if test ="${(v3x:getSysFlagByName('sys_isGovVer')=='true')}">
					validate="notNull"
				  </c:if>  
				  inputName="<fmt:message key="org.member_form.levelName.label${v3x:suffix()}" />" class="input-100per" ${v3x:outConditionExpression(readOnly, 'disabled', '')}>
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
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><fmt:message key="org.member_form.dutylevelName.label${v3x:suffix()}" />:</td>
				<td class="new-column" width="75%">
	              <select name="orgDutyLevelId" id="orgDutyLevelId" inputName="<fmt:message key="org.member_form.dutylevelName.label${v3x:suffix()}" />" class="input-100per" ${v3x:outConditionExpression(readOnly, 'disabled', '')}>
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
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="type"><fmt:message key="org.metadata.member_type.label" />:</label></td>
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
				<select class="input-100per" name="type" id="type" ${dis}>
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
			  	<td class="bg-gray" width="25%" nowrap="nowrap"><label for="state"><fmt:message key="org.metadata.member_state.label" />:</label></td>
				<td class="new-column" width="75%">
				<select class="input-100per" name="state" id="state" ${dis} onchange="changeState()" disabled="disabled">
				<v3x:metadataItem metadata="${orgMeta['org_property_member_state']}" showType="option" name="state"
				  selected="${member.v3xOrgMember.state}"/>
				  </select>
				</td>
			</tr>
			<c:if test="${cntList!=null}">
				<tr>
					<td class="bg-gray" width="25%" nowrap="nowrap" valign="top"><label for="cnt"><fmt:message key="org.member_form.cnt.label" />:</label></td>
					<td class="new-column" width="75%">
					<div style="border: 1px #7F9DB9 solid; width: 100%; height: 70px; overflow: scroll;">
					<table width="100%">
					<c:forEach items="${cntList}" var="cnt">
					<tr>
						<td>
							&nbsp;<label for="state"><fmt:message key="org.member_form.cnt.account"/>:</label>&nbsp;<c:out  value="${v3x:showOrgEntitiesOfIds(cnt.cntAccountId, 'Account', pageContext)}" escapeXml="true" />
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;<label for="state"><fmt:message key="org.member_form.cnt.post"/>:</label>&nbsp;<c:out value="${v3x:showOrgEntitiesOfIds(cnt.cntDepId, 'Department', pageContext)}" escapeXml="true" />-<c:out value="${v3x:showOrgEntitiesOfIds(cnt.cntPostId, 'Post', pageContext)}" escapeXml="true" />
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
				    	<input type="button" size="100" value="<fmt:message key="hr.staffInfo.browse.label" bundle="${v3xHRI18N}" />...  " onclick="changeImage()">
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
	document.getElementById("name").focus();
//-->
</script>