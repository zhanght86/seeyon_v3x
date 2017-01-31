<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>个人维护</title>
<%@include file="../header.jsp"%>
<html:link renderURL='/main.do' var="mainURL"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/passwdcheck.css${v3x:resSuffix()}" />">
<script type="text/javascript" src="<c:url value='/common/js/passwdcheck.js${v3x:resSuffix()}'/>"></script>
<script type="text/javascript">
getA8Top().showLocation(801, "<fmt:message key='menu.individual.manager' bundle='${v3xMainI18N}'/>");
<%--验证弹出的信息-输入新密码是否相同-原密码错误--%>
var sameOrNot = "<fmt:message key='manager.vialdateword'/>";
var oldPasswordMsg = "<fmt:message key='manager.oldword'/>";
</script>
<script type="text/javascript"
	src="<c:url value='/apps_res/systemmanager/js/individualManager.js${v3x:resSuffix()}'/>">
</script>
</head>
<body class="padding5" scroll="no">
<form id="postForm" method="post" action="<html:link renderURL='/individual.do' />?method=modifyIndividual" onsubmit="return (validateOldPassword1() && checkForm(this) && validate1() && verifyPwdStrength())">
<input id="individualName" type="hidden" name="individualName" value="${logerName}" />
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	   <tr>
	      <td valign="bottom" height="26" class="tab-tag">
				<%@include file="../settingCommon.jsp" %>
		  </td>
	  </tr>  
	  <%--<tr>
	  	<td class="init-password-border">
	  		<c:if test="${initPage != 'true' }">
	    	<script type="text/javascript">
	    	var myBar = new WebFXMenuBar("${pageContext.request.contextPath}","gray");
			myBar.add(new WebFXMenuButton("editBtn","<fmt:message key='common.toolbar.update.label' bundle="${v3xCommonI18N}" />", "editData();", [1,2], "",null));
	    	document.write(myBar);
	    	document.close();
	    	</script>
	    	</c:if>
	  	</td>
	  </tr> --%>   
	  <tr>
	    <td valign="top" class="tab-body-bg" height="100%" align="center">
			<br><br><br><br>
			<div style="width: 500px;">	
			<fieldset>
			<legend>
			<fmt:message key="personal.password"/>
			</legend>	<br/>			
						<table width="70%" border="0" cellspacing="0" cellpadding="0"
							align="center">
							<c:set var="disabled" value="${param.disabled }" />
							<tr>
								<td class="bg-gray" width="20%" nowrap="nowrap"><label
									for="post.code">  <font color="red">*</font><fmt:message key="manager.formerpassword.notnull" />:</label></td>
								<td class="new-column" width="80%">
									<input class="input-100per" type="password" name="formerpassword" id="formerpassword" value="${systemPassword }" maxlength="50" inputName="<fmt:message key="manager.formerpassword.notnull" />" validate="notNull" />
								</td>
							</tr>
							<tr>
								<td class="bg-gray" width="20%" nowrap="nowrap"><label
									for="post.code"> <font color="red">*</font> <fmt:message key="manager.password.notnull" />:</label></td>
								<td class="new-column" width="80%">
									<input class="input-100per" type="password" name="nowpassword" id="nowpassword" value="" inputName="<fmt:message key="manager.password.notnull" />" minLength="10" maxLength="50" validate="notNull,minLength,maxLength"
									<c:if test="${pwdStrengthValidation==1 }">
									 onKeyUp="EvalPwdStrength(document.forms[0],this.value);"
									 </c:if> />
								</td>
							</tr>
							<c:if test="${pwdStrengthValidation==1 }">
							<tr>
							<td class="bg-gray" width="20%" nowrap="nowrap"><label
									for="post.code"><fmt:message key="common.pwd.pwdStrength.label" bundle="${v3xCommonI18N }"/>:</label></td>
							<td class="new-column" width="80%">
							<table cellpadding="0" cellspacing="0" class="pwdChkTbl2">
								<tr>
									<td id="idSM1" width="25%" class="pwdChkCon0" align="center"><span
										style="font-size:1px">&nbsp;</span><span id="idSMT1"
										style="display:none;"><fmt:message key="common.pwd.pwdStrength.value1" bundle="${v3xCommonI18N }"/></span></td>
									<td id="idSM2" width="25%" class="pwdChkCon0" align="center"
										style="border-left:solid 1px #fff"><span style="font-size:1px">&nbsp;</span>
										<span id="idSMT0" style="display:inline;font-weight:normal;color:#666"><fmt:message key="common.pwd.pwdStrength.value0" bundle="${v3xCommonI18N }"/></span>
										<span id="idSMT2" style="display:none;"><fmt:message key="common.pwd.pwdStrength.value2" bundle="${v3xCommonI18N }"/></span></td>
									<td id="idSM3" width="25%" class="pwdChkCon0" align="center"
										style="border-left:solid 1px #fff"><span style="font-size:1px">&nbsp;</span><span
										id="idSMT3" style="display:none;"><fmt:message key="common.pwd.pwdStrength.value3" bundle="${v3xCommonI18N }"/></span></td>
									<td id="idSM4" width="25%" class="pwdChkCon0" align="center"
										style="border-left:solid 1px #fff"><span style="font-size:1px">&nbsp;</span><span
										id="idSMT4" style="display:none;"><fmt:message key="common.pwd.pwdStrength.value4" bundle="${v3xCommonI18N }"/></span></td>
								</tr>
							</table>
							</td>
						</tr>
						</c:if>
							<tr>
								<td class="bg-gray" width="20%" nowrap="nowrap"><label
									for="post.code"> <font color="red">*</font><fmt:message key="manager.validate.notnull" />:</label></td>
								<td class="new-column" width="80%">
									<input id="validatepass" class="input-100per" type="password" name="validatepass" value="" inputName="<fmt:message key="manager.validate.notnull" />"  minLength="10" maxLength="50" validate="notNull" />
								</td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td class="description-lable"><fmt:message key="manager.vaildate.length"/></td>
							</tr>
						</table>
						<br/>			
						</fieldset>				
			</div>
			
		</td>
	</tr>
	<tr id="submitOk">
		<td height="42" align="center" class="tab-body-bg bg-advance-bottom" >
			<input type="submit" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
			<input type="button" onclick="getA8Top().contentFrame.topFrame.back();" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2 button-left-margin-10">
		</td>
	</tr>
</table>
</form>
</body>
</html>