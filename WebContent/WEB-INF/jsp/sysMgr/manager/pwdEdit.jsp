<%@ page language="java" contentType="text/html;charset=UTF-8"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>管理员密码修改</title>

<%@include file="../header.jsp"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/passwdcheck.css${v3x:resSuffix()}" />">
<script type="text/javascript" src="<c:url value='/common/js/passwdcheck.js${v3x:resSuffix()}'/>"></script>
<script type="text/javascript"><!--
	
	// 进行编辑
	function showEdit(){
		document.getElementById("submitOk").style.display= "";
		document.getElementById("name").disabled="";
		document.getElementById("formerpassword").disabled="";
		document.getElementById("oldpassword").disabled="";
		document.getElementById("validatepass").disabled="";
		<c:if test="${isShowMore}">
		document.getElementById("system.name").disabled="";
		document.getElementById("system.phone").disabled="";
		document.getElementById("system.email").disabled="";
		</c:if>
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
		<c:if test="${isShowMore}">
		document.getElementById("system.name").disabled="disabled";
		//document.getElementById("system.name").value="";
		document.getElementById("system.phone").disabled="disabled";
		//document.getElementById("system.phone").value="";
		document.getElementById("system.email").disabled="disabled";
		//document.getElementById("system.email").value="";
		</c:if>
	}
	function validate(){
		var oldpasword = document.getElementById("oldpassword").value;
		var validatepassword = document.getElementById("validatepass").value;
		if (oldpasword == validatepassword){
			return true;
		} else {		
			alert("<fmt:message key='manager.vialdateword'/>");
			document.getElementById("oldpassword").value = "";
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
	function validateName(){
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
		 if (password.match(/(.*[!,@,#,$,%,^,&,*,?,_,~])/)){ score += 5 ;}else{tmpString += "，至少包含一个特殊字符";}
		 if (password.match(/(.*[0-9])/)){ score += 5;}else{tmpString += "，至少包含一个数字";}
		 if (password.match(/([!,@,#,$,%,^,&,*,?,_,~])/) && password.match(/([0-9])/)){ score += 15;}
		 if (password.match(/([!,@,#,$,%,^,&,*,?,_,~])/) && password.match(/([a-zA-Z])/)){score += 15;}
		 
		 if(score>=45){
			 return true;
		 }else{
			 	
				alert(tmpString);
				return false;
		 }
	}
	
	// 合并方法
	function unit(flag){
		if (validateName() && validateOldPassword() && checkForm(flag) && validate()&& verifyPwdStrength()){
			return true;
		}   return false;
	}
	
	// 2017-2-28 诚佰公司 添加异步提交表单
	function submitForm() {
		var postForm = document.getElementById("postForm");
		if (!unit(postForm)) {
			return;
		}
		
		$('#postForm').ajaxSubmit({
		      url : "<html:link renderURL='/manager.do' />?method=modifyManager&pwdAlert=1",
		      type : 'POST',
		      async : false,
		      success : function(result) {
		    	 result =  eval('(' + result + ')');
		    	 alert(result.message);
		         if (result.success) {
		        	 window.returnValue = true;
			         window.close();
		         }
		      }
		});
	}
</script>


</head>
<body scroll="no" style="overflow: no">
	<form id="postForm" method="post">
		<input type="hidden" id="logerName" name="logerName" value="${logerName}" />
		<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" align="center" class="border_lr">
			<tr >
				<td>
				<div style="width:100%;padding:20px;">
					<fieldset height="100%" style="padding:12px"><legend>系统管理员密码修改</legend>
						<table width="80%" border="0" cellspacing="0" cellpadding="0"
							align="center">
							<c:set var="disabled"
								value="${param.disabled }" />
							<tr>
								<td class="bg-gray" width="20%" nowrap="nowrap">
									<label for="name"> <font color="red">*</font><fmt:message key="manager.name.notnull" />:</label></td>
								<td class="new-column" width="80%">
									<input disabled id="name" name="name" readonly="readonly" maxLength="40" maxSize="40" class="input-100per" type="text" value="<c:out value="${logerName }" escapeXml="true"/>" inputName="<fmt:message key="manager.name.notnull" />" validate="notNull,isCriterionWord" />
								</td>
							</tr>
							<tr>
								<td class="bg-gray" width="20%" nowrap="nowrap"><label
									for="post.code"> <font color="red">*</font>  <fmt:message key="manager.formerpassword.notnull" />:</label></td>
								<td class="new-column" width="80%"><input disabled class="input-100per"
									type="password" name="formerpassword" id="formerpassword"
									value="${systemPassword }" maxSize="50" maxlength="50" inputName="<fmt:message key="manager.formerpassword.notnull" />" validate="notNull" /></td>
							</tr>
							<tr>
								<td class="bg-gray" width="20%" nowrap="nowrap"><label
									for="post.code"> <font color="red">*</font> <fmt:message key="manager.password.notnull" />:</label></td>
								<td class="new-column" width="80%"><input disabled class="input-100per"
									type="password" name="password" id="oldpassword"
									value="" inputName="<fmt:message key="manager.password.notnull" />" minLength="10" maxSize="50" maxLength="50" validate="notNull,minLength,maxLength" 
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
								<td class="new-column" width="80%"><input disabled id="validatepass"
									class="input-100per" type="password" name="validatepass" value="" minLength="6" maxSize="50" maxLength="50" inputName="<fmt:message key="manager.validate.notnull" />"
									validate="notNull" /></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td class="description-lable"><fmt:message key="manager.vaildate.length"/></td>
							</tr>
							<c:if test="${isShowMore}">
							<tr>
								<td class="bg-gray" width="20%" nowrap="nowrap"><label
									for="post.code"> <fmt:message key="manager.system.name" />:</label></td>
								<td class="new-column" width="80%"><input disabled id="system.name" maxSize="50" maxLength="50"
									class="input-100per" type="text" name="system.name" value="<c:out value="${adminName == 'null'? '' : adminName}" escapeXml="true"/>"
									${ro} inputName="<fmt:message key="manager.system.name" />" /></td>
							</tr>
							<tr>
								<td class="bg-gray" width="20%" nowrap="nowrap"><label
									for="post.code"><fmt:message key="manager.system.phone" />:</label></td>
								<td class="new-column" width="80%"><input disabled id="system.phone" maxSize="50" maxLength="50"
									class="input-100per" type="text" name="system.phone" value="<c:out value="${adminPhone == 'null'? '': adminPhone}" escapeXml="true"/>"
									${ro} inputName="<fmt:message key="manager.system.phone" />" /></td>
							</tr>
							<tr>
								<td class="bg-gray" width="20%" nowrap="nowrap"><label
									for="post.code"><fmt:message key="manager.system.email" />:</label></td>
								<td class="new-column" width="80%"><input disabled id="system.email" maxSize="50" maxLength="50"
									class="input-100per" type="text" name="system.email"  value="<c:out value="${adminEmail == 'null'? '': adminEmail}" escapeXml="true"/>"
									${ro} inputName="<fmt:message key="manager.system.email" />" /></td>
							</tr>
							</c:if>
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
						<input type="button" onclick="submitForm();" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">&nbsp;
						<input type="button" onclick="window.close();" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
					</td>
				</tr>
		</table>
</form>
</body>
<script>
	<c:if test="${param.result == true }">
		showEdit();
	</c:if>
</script>
</html>
