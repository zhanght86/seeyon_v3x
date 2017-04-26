<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>account</title>
<%@include file="../../organizationHeader.jsp"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/jquery/themes/default/easyui.css${v3x:resSuffix()}" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/common/jquery/themes/icon.css${v3x:resSuffix()}" />" />
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.easyui.js${v3x:resSuffix()}" />"></script>
<%@ taglib uri="http://v3x.seeyon.com/taglib/main" prefix="main"%>
<script type="text/javascript">
	<!--
		
	function change(){
           window.location.href = "${organizationURL}?method=organizationFrame&from=Account&group=true";
	}
	function editManager(){  //   点击复选筐的事件
		var checkValue = document.getElementById("checkManager").checked;
		if (checkValue == true){
			document.getElementById("account.adminName").readOnly = false;
			document.getElementById("editmanageradmins1").style.display = "";
			document.getElementById("editmanageradmins2").style.display = "";
			document.getElementById("editmanageradmins3").style.display = "";
			document.getElementById("adminPass").validate="notNull,minLength,maxLength";
			document.getElementById("adminPass1").validate="notNull,minLength,maxLength";
		} else {
		    document.getElementById("account.adminName").value = '${account.v3xOrgAccount.adminName}';
			document.getElementById("account.adminName").readOnly = true;
			document.getElementById("editmanageradmins1").style.display = "none";
			document.getElementById("editmanageradmins2").style.display = "none";
			document.getElementById("editmanageradmins3").style.display = "none";
			
			document.getElementById("adminPass").validate="";
			document.getElementById("adminPass").value="";
			document.getElementById("adminPass1").validate="";
			document.getElementById("adminPass1").value="";
		}
	}

	// 验证是否有重名字的管理员登录名  vaildateName
	function validateName(){
		// 获得页面上的数据
		var names     = document.getElementById("name").value;
		var shortname = document.getElementById("account.shortname").value;
		var code = document.getElementById("account.code").value;
		var adminName = document.getElementById("account.adminName").value;
		var resource_key = "organizationLang.orgainzation_code";
		if(${v3x:getSysFlagByName('sys_isGovVer')=='true'}){//向凡 添加 政务判断，修复GOV-4309
			resource_key = "organizationLang.orgainzation_code_gov";
		}
		if(${operation=="create"}){ // 在单位添加的时候进行重名称的验证
			if(validateRepoint("V3xOrgAccount","name",names,_("organizationLang.organization_name"))){
				if(validateRepoint("V3xOrgAccount","shortname",shortname,_("organizationLang.orgainzation_shortname"))){
					if(validateRepoint("V3xOrgAccount","code",code,_(resource_key))){						
							return validateRepoint("JetspeedPrincipal","adminName",adminName,_("organizationLang.orgainzation_adminName"));
					}
				}
			}
			return false;
		}
		else{ // 在单位进行修改的时候进行验证必添字段的重名称的验证
			// 获得后台穿过来的数据
			var nameId = document.getElementById("nameId").value;
			var shortNameId = document.getElementById("shortNameId").value;
			var codeId = document.getElementById("codeId").value;
			var adminId = document.getElementById("adminId").value;
			if(nameId != names ){
				return validateRepoint("V3xOrgAccount","name",names,_("organizationLang.organization_name"));
			}
			if(shortNameId != shortname){
				return validateRepoint("V3xOrgAccount","shortname",shortname,_("organizationLang.orgainzation_shortname"));
			}
			if (codeId != code){
				return validateRepoint("V3xOrgAccount","code",code,_(resource_key));
			}
			if (adminId != adminName){
				return validateRepoint("JetspeedPrincipal","adminName",adminName,_("organizationLang.orgainzation_adminName"));
			}
			return true;
		}
	}

	// 判断上级单位
	function vaidataAccount(){	    
        var isRoot   = "${!account.v3xOrgAccount.isRoot}";
	    var ids      = document.getElementById("id").value;
	    var superior = document.getElementById("superior").value;
	    if (ids==superior){
		    alert(v3x.getMessage("organizationLang.orgainzation_account_sameto_paccount"));
		    return false;
	    }else{
		    return true;
	    }
	}

	// 验证原密码
	function validateOldPassword(){	
		var checkValueOne = document.getElementById("checkManager").checked
		if ( checkValueOne == true ){
			var oldPassword = document.getElementById("adminOldPass").value;
			var systemName    = document.getElementById("adminNames").value;
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
		}else{
			return true;
		}
	}

	// 验证方法
	function createAccontValidate(){
		var isRoot = "${account.v3xOrgAccount.isRoot}"
		var formValue = document.getElementById("editAccountForm");
		if(isRoot != 'true'){
		var firstPasswordObj = document.getElementById("adminPass");
		var towPasswordObj = document.getElementById("adminPass1");
		var adminNameObj = document.getElementById("account.adminName");
		var oldAdminName = "${account.v3xOrgAccount.adminName}";
		if (validateName()){
			var firstPassword = firstPasswordObj.value;
			var towPassword = towPasswordObj.value;
			var adminName = adminNameObj.value;
			if (${operation == 'create'}){
				firstPasswordObj.validate="notNull,minLength,maxLength";
				towPasswordObj.validate="notNull,minLength,maxLength";
			}
			if (checkForm(formValue)){
				if (${operation == 'create'}){ // 新建
					if (vaidataAccount()){
						if (firstPassword == towPassword){					     
							return true;
						}else{
							alert(v3x.getMessage("organizationLang.organization_account_notsame"));
							firstPasswordObj.value = "";
							towPasswordObj.value = "";
							firstPasswordObj.focus();
							return false;
						}
					}else{return false;}
				}
				else{ // 修改
					var checkValue = document.getElementById("checkManager").checked;
					if ( checkValue == true ){						
						if (firstPassword==towPassword){					     
							if (vaidataAccount()){
							  return true;
							}else{return false;}
						}else{
							alert(v3x.getMessage("organizationLang.organization_account_notsame"));
							firstPasswordObj.value = "";
							towPasswordObj.value = "";
							firstPasswordObj.focus();
							return false;
						}
					}else{
						if (vaidataAccount()){
							return true;
						}else{return false;}
					}
				}
			}else{
			    return false;
			}
		}
		else{
			return false;
		}		
		}else{
			return checkForm(formValue)
		}		    
	}
	
	// 取消方法
	function cancelForms(){
	 	location.href = "${organizationURL}?method=showRightMain";
	 }

	// 判断上级单位
	function judgeFun(){	
	    var superiorID = document.getElementById("superior").value;
	    if(superiorID == ""){
	    	if("${account.v3xOrgAccount.isRoot}" != "true"){
		        if(confirm(v3x.getMessage("organizationLang.orgainzation_account_null_superior"))){
	                  return true;
	            }else{
	                  return false;
	            }
            }
            else{
            	return true;
            }
	    }else{
	        return true;
	    }
	} 

	function checkEMail(){
	    var email=document.all.accountMail.value;
	    if(email==null || email==""){
		    return true;
	    }
	    return isEmail(document.getElementById("accountMail"));
	}

	getA8Top().showLocation(1901);
	
	if(${param.reloadTree == 'true'}){
		if(${accountId!=null}&& ${parentId!=null}){
			parent.treeFrame.location.href='${organizationURL}?method=showLeftTree&group=true&accountId='+'${accountId}'+'&parentId='+'${parentId}';
		}else{
			parent.treeFrame.location.reload(true);
		}
	}
	
	function checkShortName(){	
	    var shortName = document.getElementById("account.shortname").value;
	    if(shortName && shortName.trim()==""){
	    	alert(v3x.getMessage("organizationLang.orgainzation_account_shortname_no_null"));
	    	return false;
	    }
	    if(InValidChar(shortName)){
	        if(shortName.length>10){
	            alert(v3x.getMessage("organizationLang.orgainzation_validate_account_shortname_length"));
	            return false;
	        }
	    }else{
	        if(shortName.length>20){
	            alert(v3x.getMessage("organizationLang.orgainzation_validate_account_shortname_length"));
	            return false;
	        }
	    }
	    return true;
	}
	function openLdap()
	{ 
	  	var sendResult = v3x.openWindow({
	    url : "${ldapSynchron}?method=viewOuTree",
	   width : "410",
	   height : "325",
	   resizable : "false",
	   scrollbars:"yes"
	});
	if(!sendResult)
	{
	return;
	}
	else
		{
		document.getElementById("ldapOu").value=sendResult;
	}
	
	}

	function changeComboTree(value){
		document.getElementById("superior").value = value;
	}

	var hiddenComboTreeRoot = false;
	//将$引用的对象映射回原始的对象, 确保jQuery不会与其他库的$对象发生冲突
	jQuery.noConflict();
	jQuery(document).ready(function(){
		<c:if test="${!systemMsgOrgEnable}">
			setTimeout(
				function(){
					${v3x:comboTree(accountlist, 'id', 'superior', 'name', rootAccountId, 'currentAccountId')}
					jQuery('#currentAccountId').combotree('setValue', "${account.v3xOrgAccount.superior == '-1' ? rootAccountId : account.v3xOrgAccount.superior}");
					jQuery('#superior').val("${account.v3xOrgAccount.superior == '-1' && !account.v3xOrgAccount.isRoot ? rootAccountId : account.v3xOrgAccount.superior}");
				}, 
			500);
		</c:if>
	});
	//验证密码强度     174工厂     汪成平
	function verifyPwdStrength(){
		var password =  document.getElementById("adminPass").value;
		var score = 0;
		var tmpString = "密码强度不够";
		
		 //if (password.match(/(.*[0-9])/)){ score += 5;}else{tmpString += "，至少包含一个数字";}
		 //if (password.match(/(.*[!,@,#,$,%,^,&,*,?,_,~])/)){ score += 5 ;}else{tmpString += "，至少包含一个特殊字符";}
		 //if (password.match(/(.*[a-z])/)){ score += 5;}else{tmpString += "，至少包含一个小写字母";}
		 //if (password.match(/(.*[A-Z])/)){ score += 5;}else{tmpString += "，至少包含一个大写字母";}
		 //if (password.match(/([a-zA-Z])/) && password.match(/([0-9])/)){ score += 15;}
		 //if (password.match(/([!,@,#,$,%,^,&,*,?,_,~])/) && password.match(/([0-9])/)){ score += 15;}
		 //if (password.match(/([!,@,#,$,%,^,&,*,?,_,~])/) && password.match(/([a-zA-Z])/)){score += 15;}
		 //if (password.match(/(\w)*(\w)\2{2}(\w)*/)){ score -= 10;tmpString += "，不能包含3个连续相同字符";}
		 
		 // 2017-4-25 诚佰公司 修改密码验证策略 begin
		 if (password.match(/(.*[a-zA-Z])/)){ score += 5;}else{tmpString += "，至少包含一个大小写字母";}
		 if (password.match(/(.*[^a-zA-Z0-9])/)){ score += 5 ;}else{tmpString += "，至少包含一个特殊字符";}
		 if (password.match(/(.*[0-9])/)){ score += 5;}else{tmpString += "，至少包含一个数字";}
		 if (password.match(/([^a-zA-Z0-9])/) && password.match(/([0-9])/)){ score += 15;}
		 if (password.match(/([^a-zA-Z0-9])/) && password.match(/([a-zA-Z])/)){score += 15;}
		 // end
		 
		 if(score>=45){
			 return true;
		 }else{
				alert(tmpString);
				return false;
		 }
	}
	
//-->
</script>
</head>
<body scroll="no" onresize="resizeRightBody(500,'treeandlist','60%')">
<form id="editAccountForm" name="editAccountForm" method="post" action="${organizationURL}?method=updateAccountOfTree" onsubmit="return (submitOrgForm(createAccontValidate()&&tirmElementById('name')&&checkShortName()&&judgeFun()&&checkEMail(),this))">
<input type="hidden" name="id" id="id" value="${account.v3xOrgAccount.id}" />
<input type="hidden" name="parentId" id="parentId" value="${parentId}" />
<input type="hidden" name="operation" id="operation" value="${operation}" />
<input type="hidden" id="adminNames" name="adminNames" value="${account.v3xOrgAccount.adminName}" />
<input type="hidden" name="orgAccountId"  id="orgAccountId" value="${account.v3xOrgAccount.orgAccountId}" />
<input type="hidden" id="nameId" value="${account.v3xOrgAccount.name}" />
<input type="hidden" name="oldName" id="oldName" value="${account.v3xOrgAccount.name}" />
<input type="hidden" name="oldSuperiorId" id="oldSuperiorId" value="${account.v3xOrgAccount.superior}" />
<input type="hidden" id="shortNameId" value="${account.v3xOrgAccount.shortname}" />
<input type="hidden" id="codeId" value="${account.v3xOrgAccount.code}" />
<input type="hidden" id="adminId" value="${account.v3xOrgAccount.adminName}" />
<input type="hidden" name="isGroupAccount" id="isGroupAccount" value="${account.v3xOrgAccount.isRoot}" />
<c:set value="${account.v3xOrgAccount.isRoot? 'group':'account'}" var="showLabel"/>
<c:set value="${account.v3xOrgAccount.isRoot? v3x:suffix():''}" var="suffix"/>
<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
<tr>
<td>
<div class="scrollList">
<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" align="center" class="" style="padding:0px 20px;">
<tr>
<td class="categorySetTitle" height="28">
<font color="red">*</font><fmt:message key="depatition.must.chosce"/>
</td>
</tr>
	<tr>
		<td width="100%" height="100%" valign="top" align="center">
				<table border="0"  width="100%" cellspacing="0" cellpadding="0">
				<tr>
					<td width="50%">
							<table align="center"  border="0" width="100%" height="100%" cellspacing="0" cellpadding="0">
								<tr>
									 <td class="bg-gray hr-blue-font"  nowrap width="35%">
										<strong><fmt:message key="org.${showLabel}_form.fieldset1Name.label${suffix}"/></strong>
									 </td>
									 <td>&nbsp;
									 </td>
								</tr>
								<tr>
									<td class="bg-gray" nowrap><label
										for="name"><font color="red">*</font><fmt:message key="org.${showLabel}_form.name.label${suffix}"/>:</label></td>
									<td class="new-column">
										<fmt:message key="common.default.name.value" var="defName" bundle="${v3xCommonI18N}" />
										<input name="name" type="text" maxSize="60" maxlength="60" id="name" style="width: 210px;" deaultValue="${defName}"
											inputName="<fmt:message key="org.${showLabel}_form.name.label${suffix}"/>" validate="isDeaultValue,notNull,maxLength,isWord"
										    value="<c:out value="${account.v3xOrgAccount.name}" escapeXml="true" default='${defName}' />"
										    onfocus='checkDefSubject(this, true)' onblur="checkDefSubject(this, false)">
										<input type="hidden" name="oldName" value="${account.v3xOrgAccount.name}">    
									</td>
								</tr>
								<tr>
									<td class="bg-gray" nowrap><label
										for="account.secondName"><fmt:message key="org.account_form.secondName.label"/>:</label></td>
									<td class="new-column" >
										<input name="secondName" type="text" id="account.secondName" maxSize="84" maxLength="84" style="width: 210px;"
										value="<c:out value="${account.v3xOrgAccount.secondName}" escapeXml="true" />" />
										<input type="hidden" name="oldSecondName" value="${account.v3xOrgAccount.secondName}">    
									</td>
								</tr>							
								<tr>
									<td class="bg-gray" nowrap><label for="account.shortname"><font color="red">*</font><fmt:message key="org.${showLabel}_form.shortname.label${suffix}"/>:</label></td>
									<td class="new-column" >
									<input type="text" style="width: 210px;" maxSize="20" maxLength="20"
										name="shortname" id="account.shortname" value="<c:out value="${account.v3xOrgAccount.shortname}" escapeXml="true" />" validate="notNull,isWord"
										inputName="<fmt:message key="org.${showLabel}_form.shortname.label${suffix}" />" />
									</td>		
								</tr>
								<tr>
									<td class="bg-gray" nowrap><label
										for="account.code"><font color="red">*</font><fmt:message key='org.${showLabel}_form.code.label${v3x:suffix()}'/>:</label></td>
									<td class="new-column" >
									<input type="text" style="width: 210px;" maxSize="30" maxLength="30"
										name="code" id="account.code" validate="notNull"
										inputName="<fmt:message key='org.${showLabel}_form.code.label${v3x:suffix()}' />"
										value="<c:out value="${account.v3xOrgAccount.code}" escapeXml="true" />" />
									</td>
								</tr>
								<tr style="display:${account.v3xOrgAccount.isRoot?'none':''}">
									<td class="bg-gray" nowrap><font color="red">*</font><label
										for="account.sortId"><fmt:message key="common.sort.label" bundle="${v3xCommonI18N}" />:</label></td>
									<td class="new-column" >
									 <input class="input-date" name="sortId" type="text" id="account.sortId" validate="isInteger,notNull" maxlength="10" min="1"
										inputName="<fmt:message key="common.sort.label" bundle="${v3xCommonI18N}" />" value="${account.v3xOrgAccount.sortId}" />
									</td>	
								</tr>
								<tr style="display:${account.v3xOrgAccount.isRoot?'none':''}">
									<td class="bg-gray" nowrap>
									</td>
									<td class="new-column" ><label><fmt:message key="org.sort.repeat.deal" /></label>
										<label for="isInsert1"><input id="isInsert1" name="isInsert" type="radio" value="1" checked><fmt:message key="org.sort.insert" /></label>
										<label for="isInsert2"><input id="isInsert2" name="isInsert" type="radio" value="0"><fmt:message key="org.sort.repeat" /></label>
									</td>	
								</tr>
								<tr style="display:${account.v3xOrgAccount.isRoot?'none':''}">
									<td class="bg-gray" nowrap><label
										for="enabled"><fmt:message key="common.state.label" bundle="${v3xCommonI18N}"/>:</label></td>
									<td class="new-column"  id="enabled">
									<c:choose>
										<c:when test="${account.v3xOrgAccount.enabled==true}">
											<label for="enabled1">
												<input id="enabled1" type="radio" name="enabled" value="1" checked ${v3x:outConditionExpression(readOnly, 'disabled', '')}/><fmt:message key="common.state.normal.label" bundle="${v3xCommonI18N}"/>
											</label>
											<label for="enabled2">
												<input id="enabled2" type="radio" name="enabled" value="0" ${v3x:outConditionExpression(readOnly, 'disabled', '')}/><fmt:message key="common.state.invalidation.label" bundle="${v3xCommonI18N}"/>
											</label>	
										</c:when>
										<c:otherwise>
											<label for="enabled1">
												<input id="enabled1" type="radio" name="enabled" value="1" ${v3x:outConditionExpression(readOnly, 'disabled', '')}/><fmt:message key="common.state.normal.label" bundle="${v3xCommonI18N}"/>
											</label>
											<label for="enabled2">
												<input id="enabled2" type="radio" name="enabled" value="0" checked ${v3x:outConditionExpression(readOnly, 'disabled', '')}/><fmt:message key="common.state.invalidation.label" bundle="${v3xCommonI18N}"/>				
											</label>
										</c:otherwise>
									</c:choose>		
									</td>
								</tr>
								<c:if test="${hasLDAPAD}">
								<tr>
								<td class="bg-gray" nowrap><font color="red">*</font><fmt:message key="ldap.lable.node"/>:</td>
									<td class="new-column">
									<input type="text" id="ldapOu" name="ldapOu" class="cursor-hand" style="width: 210px;" inputName="<fmt:message key="ldap.lable.node"/>" maxlength="40" 
									value="<c:out value="${ldapValue.rootAccountRdn}" escapeXml="true" />" validate="notNull" onclick="openLdap()"/></td>
								</tr>
								</c:if>
								  <tr>
									<td class="bg-gray" nowrap valign="top">
										<div class="hr-blue-font"><strong><fmt:message key="${showLabel}.description${suffix}"/></strong></div>
									</td>									
									<td class="new-column">
										<textarea id="decription" name="decription" maxSize="1000" maxlength="1000" inputName="<fmt:message key="${showLabel}.description${suffix}"/>" validate="maxLength" rows="5" cols="38" ><c:out value="${account.v3xOrgAccount.decription}"/></textarea>											    
									</td>
								 </tr>
								  <tr>
									<td class="bg-gray" nowrap>
										 <div class="hr-blue-font"><strong><fmt:message key="org.${showLabel}_form.fieldset2Name.label${suffix}"/></strong></div>
									</td>
									<td>&nbsp;</td>
								  </tr>								  
								  <tr>	
									<td class="bg-gray" nowrap><fmt:message key="org.account_form.isRoot.label${v3x:suffix()}"/>:</td>
									<td class="new-column">
										<fmt:message key="common.${account.v3xOrgAccount.isRoot?'yes':'no'}" bundle="${v3xCommonI18N}" />
									</td>
								  </tr>		
								  <tr style="display:${account.v3xOrgAccount.isRoot?'none':''}">
									<td class="bg-gray" nowrap><label><fmt:message key="org.${showLabel}_form.permission.label${suffix}"/>:</label></td>
									<td class="new-column"> 
									  <select id="accessPermission" name="accessPermission" style="width: 210px;">
										<v3x:metadataItem metadata="${orgMeta['org_property_account_permission']}" showType="option" name="accessPermission" selected="${account.v3xOrgAccount.accessPermission}"/>
									  </select> 
									</td>
								  </tr>											
								  <tr style="display:${account.v3xOrgAccount.isRoot?'none':''}">
									<td class="bg-gray" nowrap><label><fmt:message key="org.${showLabel}_form.superior.label${suffix}"/>:</label></td>
									<td class="new-column">
										 <c:choose>
										 	<c:when test="${systemMsgOrgEnable}">
										 		<select id="superior" name="superior" style="width: 210px;" ${v3x:outConditionExpression(readOnly, 'disabled', '')} <c:if test="${account.v3xOrgAccount.isRoot}">disabled</c:if> deaultValue="${defName}...">
													<option value="" ${account.v3xOrgAccount.superior==-1? 'selected':''}><fmt:message key="org.metadata.up_unit.label"/></option>
													<c:forEach var="a" items="${accountlist}">
													<c:if test="${(a.superior==null || a.superior==-1)}">
														<c:set var="sed" value="${(account.v3xOrgAccount.superior==a.id) || (param.method=='createAccountOfTree' && a.isRoot)}" />
														<option value="${a.id}" ${sed ? 'selected':''}>${a.name}</option>
														${main:accountList2Tree(accountlist, a.id, account.v3xOrgAccount.superior, 1, pageContext)}
													</c:if>
													</c:forEach>
												</select>
										 	</c:when>
										 	<c:otherwise>
										 		<input type="hidden" id="superior" name="superior">
												<input id="currentAccountId" class="easyui-combotree" style="width: 208px;">
										 	</c:otherwise>
										 </c:choose>
									 </td>
								  </tr>
								  <%--<c:if test="${operation=='create'}">
								  <tr>
									<td class="bg-gray" nowrap></label>
						          </td>
									<td><label for="isCopy"><input id="isCopy" type="checkbox" name="isCopy" checked><fmt:message key="account.add.copy.group.level${v3x:suffix()}"/></label></td>
								  </tr>						          
					              </c:if>--%>
							</table>
					</td>
					<td valign="top" width="50%">
							<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
								<c:if test="${!account.v3xOrgAccount.isRoot}">
						  		<tr>
									<td class="bg-gray hr-blue-font" nowrap>
										 <b><fmt:message key="org.${showLabel}_form.fieldset2Name.label${suffix}"/></b>
									</td>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td class="bg-gray" width="30%" nowrap><label for="account.adminName"><font color="red">*</font><fmt:message key="org.account_form.adminName.label"/>:</label></td>
									<td class="new-column"><input type="text" class="input-100per" validate="notNull,isCriterionWord, isWord" ${operation=='create'? '':'readonly'} maxSize="40" maxLength="40" inputName="<fmt:message key="org.account_form.adminName.label" />"	name="adminName" id="account.adminName" value="<c:out value="${account.v3xOrgAccount.adminName}" escapeXml="true" />" /></td>
								</tr>
								
						          <tr id="editmanageradmins1" style="display:${operation=='create'? '':'none'};">
						            <td class="bg-gray" width="30%"  ><label for="adminPass"><font color="red">*</font><fmt:message key="account.system.newpassword" />:</label></td>
									<td class="new-column" width="70%">
										<input type="password" class="input-100per" name="adminPass" id="adminPass" value="${account.v3xOrgAccount.adminName==''?'ABC&abc&123':''}"  validate="" minLength="10" maxSize="50" maxLength="50"  
										inputName="<fmt:message key="account.system.newpassword" />" />
									</td>  
						          </tr>
						          <tr id="editmanageradmins2" style="display:${operation=='create'? '':'none'};">
						            <td class="bg-gray" width="30%"  ><label for="adminPass"><font color="red">*</font><fmt:message key="account.system.validatepassword" />:</label></td>
									<td class="new-column" width="70%">
									<input type="password" class="input-100per" name="adminPass1" id="adminPass1" value="${account.v3xOrgAccount.adminName==''?'ABC&abc&123':''}"   validate="" minLength="10" maxSize="50" maxLength="50" 
										inputName="<fmt:message key="account.system.validatepassword" />" />
									 </td>
						          </tr>
						          <tr id="editmanageradmins3" style="display:${operation=='create'? '':'none'};">
						          	<td></td>
						          	<td class="description-lable">
						          		<fmt:message key="manager.vaildate.length"/>
						          	</td>
						          </tr>
	       						<tr style="display:${operation=='create'? 'none':''};">
	       						 <td>&nbsp;</td>
	       						 <td>
		       						 <label for="checkManager">
		            					<input id="checkManager" type="checkbox" name="checkManager" value="checkbox" onclick="editManager();" /><fmt:message key="account.message"/>
	            					</label>
	       						 </td>
	       						</tr>
								</c:if>
								<tr>
									 <td class="bg-gray hr-blue-font" nowrap width="35%">
										 <strong><fmt:message key="org.${showLabel}_form.fieldset4Name.label${suffix}"/></strong>
									 </td>
									 <td>&nbsp;
									 </td>
								 </tr>
								<tr>
									<td class="bg-gray" nowrap><label><fmt:message key="org.metadata.account_category.label"/>:</label></td>
									<td class="new-column">
									<select id="accountCategory" name="accountCategory" class="input-100per">
									<option></option>
									<v3x:metadataItem metadata="${orgMeta['org_property_account_category']}" showType="option" name="accountCategory"
									  selected="${account.accountCategory}"/>
									  </select>
									</td>
								</tr>
								<%--政务版：不显示负责人 --%>
								<tr  style="display:${v3x:suffix()=='.GOV'?'none':''}">
									<td class="bg-gray" nowrap><label for="manager"><fmt:message key="org.account_form.manager.label"/>:</label></td>
									<td class="new-column">
									<input type="text" title="${account.chiefLeader}" id="chiefLeader" maxlength="40" name="chiefLeader" class="input-100per"
									 value="<c:out value="${account.chiefLeader}" escapeXml="true" />"/></td>
								</tr>
								<tr>
									<td class="bg-gray" nowrap><label for="address"><fmt:message key="org.account_form.address.label"/>:</label></td>
									<td class="new-column"><input id="address" maxSize="120" maxlength="120" name="address" 
										type="text" class="input-100per" title="${account.address}"
										value="<c:out value="${account.address}" escapeXml="true" />"/></td>
								</tr>
								<tr>
									<td class="bg-gray" nowrap><label for="zipCode"><fmt:message key="org.account_form.zipCode.label"/>:</label></td>
									<td class="new-column">
									<input type="text" id="zipCode" name="zipCode" title="${account.zipCode}" maxlength="40" class="input-100per"
										value="<c:out value="${account.zipCode}" escapeXml="true" />"/></td>								
								</tr>
								<tr>
									<td class="bg-gray" nowrap><label for="telephone"><fmt:message key="org.account_form.telephone.label"/>:</label></td>
									<td class="new-column">
									<input type="text" id="telephone" name="telephone" maxlength="40" title="${account.telephone}" class="input-100per" 
									value="<c:out value="${account.telephone}" escapeXml="true" />"/></td>
								</tr>
								<tr>
									<td class="bg-gray" nowrap><label for="fax"><fmt:message key="org.account_form.fax.label"/>:</label></td>
									<td class="new-column">
									<input type="text" id="fax" name="fax" class="input-100per" maxlength="40" 
									value="<c:out value="${account.fax}" escapeXml="true" />"/></td>
								</tr>
								<tr>
									<td class="bg-gray" nowrap><label for="ipAddress"><fmt:message key="org.account_form.ipAddress.label"/>:</label></td>
									<td class="new-column">
									<input type="text" id="ipAddress" name="ipAddress" class="input-100per" maxlength="40" 
									value="<c:out value="${account.ipAddress}" escapeXml="true" />"/></td>
								</tr>
								<tr>
									<td class="bg-gray" nowrap><label for="accountMail"><fmt:message key="org.account_form.accountMail.label"/>:</label></td>
									<td class="new-column">
									<input type="text" id="accountMail" name="accountMail" class="input-100per" inputName="<fmt:message key='org.account_form.accountMail.label'/>" maxlength="40" 
									value="<c:out value="${account.accountMail}" escapeXml="true" />" validate="" /></td>
								</tr>
								<%--政务版：新增行政级别字段 start--%>
								<c:if test ="${(v3x:getSysFlagByName('sys_isGovVer')=='true')}">
								<tr>
									<td class="bg-gray" nowrap><label for="adminiLevel"><fmt:message key="org.account_form.adminiLevel.label"/>:</label></td>
									<td class="new-column">
									<input type="text" id="adminiLevel" name="adminiLevel" class="input-100per" inputName="<fmt:message key="org.account_form.adminiLevel.label"/>" maxlength="40" 
									value="<c:out value="${account.adminiLevel}" escapeXml="true" />" validate="" /></td>
								</tr>
								</c:if>
								<%--政务版：新增行政级别字段 end--%>
							</table>
					    </td>
				   </tr>
			  </table>  
		</td>
	</tr>
</table>
</div>
</td>
</tr>
	<tr id="accountSubmitTR">
		<td id="accountmessage" height="42" align="center" class="bg-advance-bottom padding5" >
			<table width="100%" border="0">
			  <tr>
				<td width="20%" align="center">
					<c:if test="${operation=='create'}">
						<label for="cont">
							<input id="cont" type="checkbox" name="cont" checked> <fmt:message key="continue.org"/>
						</label>
					</c:if>
				</td>
				<c:if test="${operation=='create'}">
				<td nowrap="nowrap">
				<label for="isCopy"><input id="isCopy" type="checkbox" name="isCopy" checked><fmt:message key="account.add.copy.group.level${v3x:suffix()}"/></label>
				</td>
				</c:if>
				</td>
				<td width="60%" align="center">
					<input id="submintButton" type="submit" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">&nbsp;&nbsp;
					<c:choose>
						<c:when test="${operation=='create'}">
							<input id="submintCancel" type="reset" onclick="change()" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
						</c:when>
						<c:otherwise>
							<input id="submintCancel" type="reset" onclick="showAccountInfo('${account.v3xOrgAccount.id}')" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
						</c:otherwise>
					</c:choose>
					
					</td>
				<td width="20%"></td>
			  </tr>
			</table>	
		</td>
	</tr>
</table>
</form>
</body>
<script type="text/javascript">
if(${operation=="view"}){
	Form.disable("editAccountForm");
	document.getElementById("accountSubmitTR").style.display = "none";
	document.getElementById("decription").readOnly="true";
	document.getElementById("decription").disabled="";
}else{
	Form.enable("editAccountForm");
	if(${operation=="create"}){
	    document.getElementById("enabled1").disabled = "true";
	    document.getElementById("enabled2").disabled = "true";
	}
	document.getElementById("accountSubmitTR").style.display = "";
	document.getElementById("accountSubmitTR").style.display = "";
}
// 同步组织机构树
var t = parent.treeFrame || parent.parent.treeFrame;
if (t && t.selectAccount) {
	t.selectAccount("${account.v3xOrgAccount.id}");
}
</script>
</html>