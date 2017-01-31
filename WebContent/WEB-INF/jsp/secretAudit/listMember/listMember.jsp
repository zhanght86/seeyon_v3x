<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>	
<%@page import="java.util.Properties"%>
<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
			<title>成员列表</title>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
			<%@include file="../head.jsp"%>
			<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/layout.css${v3x:resSuffix()}" />"/>
			<script type="text/javascript">
				function showDetail(id){
					parent.detailFrame.location.href = secretAuditURL+"?method=editMember&id="+id;
				}
				
				function audit(){
					var idsObj = parent.listFrame.document.getElementsByName('id');
					var id = '';
					for(var i = 0; i < idsObj.length; i++){
						var idCheckBox = idsObj[i];
						if(idCheckBox.checked){
							id = id + idCheckBox.value + ',';
						}
					}
					var ids = id.split(",");
					if(ids.length == 2){
						parent.detailFrame.location.href = secretAuditURL+"?method=editMember&id="+ids[0];
					}else if(ids.length > 2){
						alert(v3x.getMessage("organizationLang.orgainzation_select_one_once"));
						return false;
					}else{
						alert("<fmt:message key='secret.audit.chosece'/>");
						return false;
					}
				}
			</script>
		</head>
	<body >
	
		<div class="main_div_row2" >
  			<div class="right_div_row2">
  				<div class="top_div_row2">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="22" id="toolbar-top-border" class="">
								<script type="text/javascript">
									var myBar = new WebFXMenuBar("<c:out value='${pageContext.request.contextPath}' />","gray");
									myBar.add(new WebFXMenuButton("audit","<fmt:message key='secret.audit'/>","audit()",[2,2], "",null));	
							        document.write(myBar);
							    	document.close();
						    	</script>
						    </td>
						</tr>
					</table>
				</div>
    			<div class="center_div_row2" id="scrollListDiv">
					<form id="memberform" name="memberform" method="post">
						<fmt:message key="org.entity.disabled" var="orgDisabled"/>
						<fmt:message key="org.entity.deleted" var="orgDeleted"/>
						<fmt:message key="org.entity.transfered" var="orgTransfered"/>
						<v3x:table htmlId="memberlist" data="memberlist" var="member" className="sort ellipsis" bundle="${orgI18N}">
							<c:set var="dbclick" value="showDetail('${member.v3xOrgMember.id}');"/>
							<c:set var="status" value=""/>
							<c:if test="${member.v3xOrgMember.status == 2}"><c:set var="status" value="(${orgDisabled})"/></c:if>
							<c:if test="${member.v3xOrgMember.status == 3}"><c:set var="status" value="(${orgDeleted})"/></c:if>	
							<c:if test="${member.v3xOrgMember.status == 4}"><c:set var="status" value="(${orgTransfered})"/></c:if>	
							<c:if test="${member.stateName==''||member.stateName==null}"><c:set var="showALT" value="${member.v3xOrgMember.loginName}"/></c:if>		
							<c:if test="${member.stateName!=''&&member.stateName!=null}"><c:set var="showALT" value=""/></c:if>
							<v3x:column width="5%" align="center" label="<input type='checkbox' id='allCheckbox' onclick='selectAll(this, \"id\")'/>">
								<input type="checkbox" name="id" id="${member.v3xOrgMember.id}" value="${member.v3xOrgMember.id}" isInternal="${member.v3xOrgMember.isInternal}">
							</v3x:column>
							<v3x:column width="15%" align="left" label="org.member_form.name.label" type="String"
								value="${member.v3xOrgMember.name}${status}" className="cursor-hand sort" 
								alt="${member.v3xOrgMember.name}${status}"  onDblClick="${dbclick }"/>
							<v3x:column width="10%" align="left" label="org.member_form.loginName.label" type="String"
								className="cursor-hand sort"  alt="${showALT}"  onDblClick="${dbclick }">
								<c:out value='${member.v3xOrgMember.loginName}' escapeXml='true'/><c:if test="${member.stateName!=''&&member.stateName!=null}">&nbsp;<img style="vertical-align:middle;" src="<c:url value='/common/images/ldapbinding.gif' />" title="<fmt:message key='ldap.user.prompt' bundle='${ldaplocale}'><fmt:param value='${member.stateName}'></fmt:param></fmt:message>"/></c:if>
							</v3x:column>
							<!-- branches_a8_v350_r_gov GOV-1097 lijl Add-->
							<v3x:column width="10%" align="left" label="org.member_form.code${v3x:suffix()}" type="String" 
							    className="cursor-hand sort" alt="${member.v3xOrgMember.code}"  onDblClick="${dbclick }">
							    <c:out value='${member.v3xOrgMember.code}' escapeXml='true'/>
							</v3x:column>	
							<v3x:column width="10%" align="center" label="secretLevel" type="String" className="cursor-hand sort"  onDblClick="${dbclick }">
								 <fmt:message key='secretLevel.${member.newSecretLevel}'/>
							</v3x:column>
							<v3x:column width="10%" align="left" label="org.member_form.deptName.label" type="String"
								value="${member.departmentName}" className="cursor-hand sort" 
								alt="${member.departmentName}"  onDblClick="${dbclick }"/>
							<v3x:column width="10%" align="left" label="org.member_form.primaryPost.label" type="String"
								className="cursor-hand sort" maxLength="55" symbol="..." alt="${member.postName}"  onDblClick="${dbclick }">
								<c:choose>
								<c:when test="${member.v3xOrgMember.orgPostId != -1}">
									<c:out value='${member.postName}' escapeXml='true'/>
								</c:when>
								<c:otherwise>
									<font color="red"><fmt:message key="org.member.noPost"/></font>						
								</c:otherwise>
								</c:choose>
							</v3x:column>	
							<v3x:column width="10%" align="left" label="org.member_form.levelName.label${v3x:suffix()}" type="String"
								className="cursor-hand sort" maxLength="13"  symbol="..." alt="${member.levelName}"  onDblClick="${dbclick }">
								<c:choose>
								<c:when test="${member.v3xOrgMember.orgLevelId != -1}">
									<c:out value='${member.levelName}' escapeXml='true'/>
								</c:when>
								<c:otherwise>
									<font color="red"><fmt:message key="org.member.noPost"/></font>						
								</c:otherwise>
								</c:choose>
							</v3x:column>
							<v3x:column width="10%" align="left" maxLength="10"  symbol="..."  label="org.metadata.member_type.label" type="String"
								className="cursor-hand sort"  onDblClick="${dbclick }">
								<v3x:metadataItemLabel metadata="${orgMeta['org_property_member_type']}" value="${member.v3xOrgMember.type}" />	
							</v3x:column>
							<v3x:column width="10%" align="left" label="org.metadata.member_state.label" type="String"
								className="cursor-hand sort"  onDblClick="${dbclick }">
								<v3x:metadataItemLabel metadata="${orgMeta['org_property_member_state']}" value="${member.v3xOrgMember.state}"/>
							</v3x:column>
						</v3x:table>
					</form>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			try{
				var io=null;
				var ok=canIO(io);
				if('doing'!=ok){
					var isShow = parent.detailFrame.showOrgDetail;
					if(typeof(isShow) == "undefined"||isShow||isShow == 'true'){
						showDetailPageBaseInfo("detailFrame", "<fmt:message key='secretLevel.audit.lable' /> ", [3,2], pageQueryMap.get('count'), v3x.getMessage("secretLang.detail_info"));
					}
				}
			}catch(e){
			}
		</script>
		<iframe width="0" height="0" name="exportIFrame" id="exportIFrame"></iframe>
	</body>
</html>