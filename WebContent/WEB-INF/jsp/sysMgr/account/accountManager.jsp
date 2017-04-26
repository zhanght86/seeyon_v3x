<%@ page language="java" contentType="text/html;charset=UTF-8"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>管理员维护</title>

<%@include file="../header.jsp"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/passwdcheck.css${v3x:resSuffix()}" />">
<script type="text/javascript" src="<c:url value='/common/js/passwdcheck.js${v3x:resSuffix()}'/>"></script>
<script type="text/javascript"><!--
getA8Top().showLocation(1702);
	// 进行编辑
	function showEdit(){
		document.getElementById("submitOk").style.display= "";
		document.getElementById("name").disabled="";
		document.getElementById("formerpassword").disabled="";
		document.getElementById("oldpassword").disabled="";
		document.getElementById("validatepass").disabled="";
	}
	// 取消编辑
	function notEdit(){
		document.getElementById("submitOk").style.display="none";
		document.getElementById("name").disabled="disabled";
		document.getElementById("formerpassword").disabled="disabled";
		document.getElementById("formerpassword").value="";
		document.getElementById("oldpassword").disabled="disabled";
		document.getElementById("oldpassword").value="";
		document.getElementById("validatepass").disabled="disabled";
		document.getElementById("validatepass").value="";
	}
	function validate(){
		var oldpasword = document.getElementById("oldpassword").value;
		var validatepassword = document.getElementById("validatepass").value;
		if (oldpasword == validatepassword){
			return true;
		} else {		
			alert("<fmt:message key='manager.vialdateword'/>");
			document.getElementById("validatepass").value = "";
			return false;
		}
	}
	// 验证原密码
	function validateOldPassword(){
		var oldPassword = document.getElementById("formerpassword").value;
		var systemName    = document.getElementById("logerName").value;
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxOrgManagerDirect", "isOldPasswordCorrect", false);
		requestCaller.addParameter(1, "String", oldPassword);
		requestCaller.addParameter(2, "String", systemName);
		var ds = requestCaller.serviceRequest();
		if(ds=="true"){
			return true;
		}else{
			alert("<fmt:message key='manager.oldword'/>");
			return false;
		}
	}
		// 验证重名字方法
	function validateNameAccount(){
		var systemName = document.getElementById("name").value;
		var oldName = document.getElementById("logerName").value;
		if (systemName != oldName){
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxOrgManagerDirect", "isPropertyDuplicated", false);
			requestCaller.addParameter(1, "String", "JetspeedPrincipal");
			requestCaller.addParameter(2, "String", "fullPath");
			requestCaller.addParameter(3, "String", systemName);
			var team = requestCaller.serviceRequest();
			if (team=="true") {
				alert(v3x.getMessage("sysMgrLang.system_manager_name_exit"));
				return false ;
			} else {
				return true;
			}
		}else{
			return true;
		}
	}

	//验证密码强度     174工程     汪成平
	function verifyPwdStrength(){
		var password =  document.getElementById("oldpassword").value;
		var score = 0;
		var tmpString = "密码强度不够";
		
		if (password.match(/(.*[a-zA-Z])/)){ score += 5;}else{tmpString += "，至少包含一个大小写字母";}
		 if (password.match(/(.*[^a-zA-Z0-9])/)){ score += 5 ;}else{tmpString += "，至少包含一个特殊字符";}
		 if (password.match(/(.*[0-9])/)){ score += 5;}else{tmpString += "，至少包含一个数字";}
		 if (password.match(/([^a-zA-Z0-9])/) && password.match(/([0-9])/)){ score += 15;}
		 if (password.match(/([^a-zA-Z0-9])/) && password.match(/([a-zA-Z])/)){score += 15;}
		 
		 if(score>=45){
			 return true;
		 }else{
				alert(tmpString);
				return false;
		 }
	}
</script>

</head>
<body scroll="no" style="overflow: auto">
	<form id="postForm" target="temp_iframe" method="post" action="<html:link renderURL='/accountManager.do' />?method=modifyManager" onsubmit="return (validateNameAccount() && validateOldPassword() && checkForm(this) && validate() && verifyPwdStrength())">
		<input type="hidden" name="id" id="logerName" value="${logerName}" />
		<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="border_lr" align="center">
			<tr>
				<td height="12" colspan="2">
					<script type="text/javascript">
						var myBar = new WebFXMenuBar("<c:out value='${pageContext.request.contextPath}' />","");
						// Add By Lif Start
						myBar.add(new WebFXMenuButton("mod","<fmt:message key="common.toolbar.update.label" bundle='${v3xCommonI18N}'/>","showEdit()",[1,2],"", null));
						// Add End
						document.write(myBar);
				    	document.close();
			    	</script>
			    </td>
			</tr>
			<tr >
				<td>
				<div style="padding:50px">
				<fieldset height="100%"><legend ><fmt:message key="system.manager.set"/></legend>
				<table width="45%" border="0" cellspacing="0" cellpadding="0"
					align="center">
					<c:set var="disabled"
						value="${param.disabled }" />
					<tr>
						<td class="bg-gray" width="20%" nowrap="nowrap"><label
							for="name"> <font color="red">*</font><fmt:message key="manager.name.notnull" />:</label></td>
						<td class="new-column" width="80%">
							<input disabled name="name" maxLength="40" maxSize="40" class="input-100per" type="text" id="name" value="<c:out value="${logerName }" escapeXml="true" />" inputName="<fmt:message key="manager.name.notnull" />" validate="notNull,isCriterionWord" />
						</td>
					</tr>
					<tr>
						<td class="bg-gray" width="20%" nowrap="nowrap"><label
							for="post.code">  <font color="red">*</font> <fmt:message key="manager.formerpassword.notnull" />:</label></td>
						<td class="new-column" width="80%">
							<input disabled class="input-100per" type="password" name="formerpassword" id="formerpassword" value="${systemPassword }" maxlength="50"  maxSize="50" inputName="<fmt:message key="manager.formerpassword.notnull" />" validate="notNull" />
						</td>
					</tr>
					<tr>
						<td class="bg-gray" width="20%" nowrap="nowrap"><label
							for="post.code"> <font color="red">*</font> <fmt:message key="manager.password.notnull" />:</label></td>
						<td class="new-column" width="80%">
							<input disabled class="input-100per" type="password" name="password" id="oldpassword" value="" inputName="<fmt:message key="manager.password.notnull" />" minLength="10" maxSize="50" maxLength="50"  validate="notNull,minLength,maxLength" 
							<c:if test="${pwdStrengthValidation==1 }">
							onKeyUp="EvalPwdStrength(document.forms[0],this.value);"
							</c:if>/>
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
							<input disabled id="validatepass" class="input-100per" type="password" name="validatepass" value="" inputName="<fmt:message key="manager.validate.notnull" />" minLength="6" maxSize="50" maxLength="50"  validate="notNull" />
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><font color="red"><fmt:message key="manager.vaildate.length"/></font></td>
					</tr>
				</table>
				</fieldset>
				</div>
				</td>
			</tr>
			<tr>
				<td height="100%"></td>
			</tr>
				<tr id="submitOk" style="display:none">
					<td height="42" align="center" class="bg-advance-bottom" >
						<input type="submit" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">&nbsp;
						<input type="button" onclick="notEdit();" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
					</td>
				</tr>
		</table>
</form>
<div class="hidden">
<iframe id="temp_iframe" name="temp_iframe">&nbsp;</iframe>
</div>
</body>
<script>
	<c:if test="${param.result == true }">
		showEdit();
	</c:if>
</script>
</html>