<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">	
<%@page import="java.util.Properties"%>
<html>
<head>
<title>文件下载列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<%@include file="../organizationHeader.jsp"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/layout.css${v3x:resSuffix()}" />"/>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/src/Set.js${v3x:resSuffix()}" />"></script>
<fmt:setBundle basename="com.seeyon.v3x.hr.resource.i18n.HRResources" var="v3xHRI18N"/>
<script type="text/javascript">

		function remove(){		
			var id = getSelectIds(parent.listFrame);
			var ids = id.split(",");
		
			var theForm = parent.listFrame.document.getElementsByName("memberform")[0];
		    if (!theForm) {
		        return false;
		    }
		    var id_checkbox = parent.listFrame.document.getElementsByName("id");
		    if (!id_checkbox) {
		        return;
		    }
		    var hasMoreElement = false;
		    var len = id_checkbox.length;
		    for (var i = 0; i < len; i++) {
		        if (id_checkbox[i].checked) {
		            hasMoreElement = true;
		            break;
		        }
		    }
		    if (!hasMoreElement) {
		        alert("请至少选择一条要删除的记录!");
		        return;
		    }
			if(!confirm("确定要删除选择的记录吗?"))
				return false;
			var form1 = parent.listFrame.document.getElementById("memberform");
			form1.action=organizationURL+"?method=destroyFiledownload";
			form1.submit();
		}
		
		function setDept(elements) {
	    	if (!elements) {
	        	return;
	    	}
	    	document.getElementById("deptName").value = getNamesString(elements);
	    	document.getElementById("textfields").value = getIdsString(elements, false);
		}		

		/**
		 * 搜索按钮事件
		 */
		function doSearch2() {
			var searchObjValue = document.getElementById('condition').value;
			
			if(searchObjValue==''){
				parent.listFrame.location.href="${organizationURL}?method=listFiledownload";
			}else if(searchObjValue=='memberName'){
				parent.listFrame.location.href="${organizationURL}?method=listFiledownload&condition="+document.getElementById('condition').value+"&textfield="+encodeURI(document.getElementById('memberNametextfield').value);
			}else if(searchObjValue=='filename'){
				parent.listFrame.location.href="${organizationURL}?method=listFiledownload&condition="+document.getElementById('condition').value+"&textfield="+encodeURI(document.getElementById('filenametextfield').value);
			}else if(searchObjValue=='departmentId'){
				parent.listFrame.location.href="${organizationURL}?method=listFiledownload&condition="+document.getElementById('condition').value+"&textfield1="+encodeURI(document.getElementById('deptName').value) +"&textfield="+document.getElementById('textfields').value;
			}
		}
</script>
<style>.webfx-menu-bar-gray-left{border-left: 0px;}</style>
</head>
<v3x:selectPeople id="dept" minSize="0" maxSize="1" panels="Department" selectType="Department" jsFunction="setDept(elements)"  />
<body >
<div class="main_div_row2">
  <div class="right_div_row2">
    <div class="top_div_row2">
		
		<table  width="100%"  border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="webfx-menu-bar">
						<script>	
						var myBar1 = new WebFXMenuBar("<c:out value='${pageContext.request.contextPath}' />","gray");
						myBar1.add(new WebFXMenuButton("delete", "<fmt:message key="common.toolbar.delete.label" bundle='${v3xCommonI18N}'/>", "remove()", [1,3]), "",null );
						document.write(myBar1);
						document.close();
						</script>
				</td>
				<td class="webfx-menu-bar" id="grayTd">
					<form action="" name="searchForm" id="searchForm" method="get" onsubmit="return false" style="margin: 0px">
						<div class="div-float-right condition-search-div">
							<div class="div-float">
								<select name="condition" id="condition" onChange="showNextCondition(this)" class="condition">
							    	<option value=""><fmt:message key="member.list.find"/></option>
								    <option value="memberName" <c:if test="${condition == 'memberName' }">selected</c:if>>下载人</option>
								    <option value="filename" <c:if test="${condition == 'filename' }">selected</c:if>>文件名</option>
								    <option value="departmentId" <c:if test="${condition == 'departmentId' }">selected</c:if>>部门</option>
							  	</select>
						  	</div>
						  	<div id="memberNameDiv" class="div-float hidden">
								<input type="text" name="textfield" id="memberNametextfield" class="textfield-search" <c:if test="${condition == 'memberName'}"> value="${textfield}" </c:if>  onkeydown="javascript:if(event.keyCode==13)return false;">
						  	</div>
						  	<div id="filenameDiv" class="div-float hidden">
								<input type="text" name="textfield" id="filenametextfield" class="textfield-search" <c:if test="${condition == 'filename'}"> value="${textfield}" </c:if> onkeydown="javascript:if(event.keyCode==13)return false;">
							</div>				  	
							<div id="departmentIdDiv" class="div-float hidden">
						  		<fmt:message key="common.default.team.value" var="defaultTP"/>
								<input type="hidden" name="textfield" id="textfields" value="${department.id}" />
								<input type="text" name="textfield1" id="deptName" class="textfield-search"
									<c:if test="${condition == 'departmentId'}"> value="${v3x:showOrgEntitiesOfIds(textfield, 'Department', pageContext)}" </c:if> readonly="readonly" size="18" onclick="selectPeopleFun_dept()" defaultValue="${defaultTP}" inputName="<fmt:message key='org.team_form.part'/>" validate="notNull,isDefaultValue"
								 />
						  	</div>
						  	<div id="grayButton" onclick="javascript:doSearch2()" class="div-float condition-search-button"></div>
					  	</div>
				  	</form>
				</td>
			</tr>
		</table>		
    </div>
    <div class="center_div_row2" id="scrollListDiv">
		<form id="memberform" name="memberform" method="post">
			<v3x:table htmlId="resultlist" data="resultlist" var="entity" className="sort ellipsis">
				<v3x:column width="5%" align="center" label="<input type='checkbox' id='allCheckbox' onclick='selectAll(this, \"id\")'/>">
					<input type="checkbox" name="id" id="${entity.id}" value="${entity.id}">
				</v3x:column>
				<v3x:column width="15%" align="left" label="文件唯一标识" type="String"
					value="${entity.fileId}" className="cursor-hand sort"/>
				<v3x:column width="25%" align="left" label="文件名" type="String"
					value="${entity.filename}" className="cursor-hand sort"/>
				<v3x:column width="10%" align="left" label="下载人" type="String"
					value="${entity.member.name}" className="cursor-hand sort"/>
				<v3x:column width="10%" align="left" label="下载次数" type="String"
					value="${entity.times}" className="cursor-hand sort"/>
				<v3x:column width="15%" align="left" label="下载时间" type="String"
					value="${entity.ts}" className="cursor-hand sort"/>
				<v3x:column width="15%" align="left" label="部门" type="String"
					value="${entity.department.name}" className="cursor-hand sort"/>
			</v3x:table>
		</form>
    </div>
  </div>
  </div>
</body>
</html>