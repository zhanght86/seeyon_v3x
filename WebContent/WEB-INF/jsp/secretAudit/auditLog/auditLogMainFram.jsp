<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
	<%@include file="../head.jsp" %>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Insert title here</title>
		<script type="text/javascript">
			getA8Top().showLocation("9102");

			function dataToColony(elements){
			    if (!elements) {
			        return false;
			    }
			    var objectIds = getIdsString(elements);
			    var objectNames = getNamesString(elements, true);
			    document.getElementById("selectPersonIds").value = objectIds;
			    document.getElementById("personIds").value = objectNames;
			    document.getElementById("personIds").title = objectNames;
			}

			function getNamesString(elements,systemFlag){
				if(!elements){
					return "";
				}
				var sp = v3x.getMessage("V3XLang.common_separator_label");
				var names = [];
				for(var i = 0; i < elements.length; i++) {
					var e = elements[i];
					var _name = null;
					if(e.accountShortname && systemFlag){
						_name = e.name + "(" + e.accountShortname + ")";
					}else{
						_name = e.name;
					}
					names[names.length] = _name;
				}
				return names.join(sp);
			}

			function removeall(){
			   window.location.reload() ;
			}

			function submitForm(){
				var theForm = document.getElementById("appLogForm");
			  	if(theForm) {
			    	var beginDate = document.getElementById("beginDate").value;
					var endDate = document.getElementById("endDate").value;
					if(compareDate(endDate, beginDate) < 0){
						alert(v3x.getMessage("LogLang.log_search_overtime")) ;
						return false;
					}
			    	theForm.target = "dataIFrame" ;
			    	theForm.action = "${secretAuditURL}?method=querySecretAuditData" ;
			    	theForm.submit() ;
			  	}
			}
		</script>
	</head>
	<body scroll="no" class="padding5">
		<v3x:selectPeople id="user" panels="Department" selectType="Member" jsFunction="dataToColony(elements)" viewPage="${viewPage}" minSize="0" />
		<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0"  class="page-list-border">
			<tr>
				<td height="20" class="webfx-menu-bar" colspan="2">	
					<script type="text/javascript">
						var myBar = new WebFXMenuBar("${pageContext.request.contextPath}");
				    	myBar.add(new WebFXMenuButton("exportExcel", "<fmt:message key='common.toolbar.exportExcel.label' bundle='${v3xCommonI18N}' />", "debugger;javascript:exportExcel()", [2,6], "", null));
				    	myBar.add(new WebFXMenuButton("print", "<fmt:message key='common.toolbar.print.label' bundle='${v3xCommonI18N}' />", "javascript:doPrint()", [1,8], "", null));
				    	document.write(myBar);
				    	document.close();
				    </script>			
				</td>
			</tr>
			<tr>
				<td align="center" valign="top" colspan="2" class="main-bg border-top">
	  				<form action="" method="post" name="appLogForm"  id = "appLogForm" onsubmit="" >
				   		<table width="" border="0" cellspacing="8" cellpadding="0">
				     		<tr>
				     			<td align="right" nowrap="nowrap"> <fmt:message key="secret.his.userName.lable" />: </td>
				       			<td width="10%"> 	
				        			<input type="text" readonly="readonly" class="cursor-hand"  onclick="selectPeopleFun_user()" id="personIds"  name="personIds" title="" value="">
				        			<input type="hidden" name="selectPersonIds" id="selectPersonIds" value="">
				       			</td>
				       			<td align="right"><fmt:message key="secret.his.auditTime.lable"/>: </td>
				       			<td width="40%" align="" colspan="3">
					           		<input type="text" style="width:150px" class="input-date cursor-hand" id="beginDate" name="beginDate"  value='<fmt:formatDate value="${firstDay}" type="Date" dateStyle="full" pattern="yyyy-MM-dd"/>' readonly="readonly" onclick="whenstart('${pageContext.request.contextPath}',this,575,140, null, false);"> —
				               		<input type="text" style="width:150px" class="input-date cursor-hand" id="endDate"  name="endDate"  value='<fmt:formatDate value="${today}" type="Date" dateStyle="full" pattern="yyyy-MM-dd"/>' readonly="readonly" onclick="whenstart('${pageContext.request.contextPath}',this,575,140, null, false);">
				        		</td>
				        		<td align="left"><input type="button" class="deal_btn" onmouseover="javascript:this.className='deal_btn_over'" onmouseout="javascript:this.className='deal_btn'" value='<fmt:message key="common.button.condition.search.label" bundle="${v3xCommonI18N}" />' class="button-default-2" onclick="submitForm()"/></td>
				     		</tr>
			     		</table>
	  				</form>
	  			</td>
			</tr>
			<tr>
				<td height="100%" valign="top" colspan="2" bgcolor="">
				    <iframe src="${secretAuditURL}?method=querySecretAuditData" name="dataIFrame" id="dataIFrame"
				            frameborder="0" marginheight="0" marginwidth="0" height="100%" width="100%" scrolling="auto">
				    </iframe>
				</td>
			</tr>
		</table>
		<iframe name="appLogDataExportExcel" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
	</body>
</html>