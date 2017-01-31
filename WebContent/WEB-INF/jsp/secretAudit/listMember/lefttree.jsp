<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Insert title here</title>
	<%@ include file="../head.jsp"%>
	<script type="text/javascript" charset="UTF-8" src="/seeyon/apps_res/doc/js/xmlextras.js${v3x:resSuffix()}"></script>
	<script type="text/javascript" charset="UTF-8" src="/seeyon/apps_res/doc/js/xloadtree.js${v3x:resSuffix()}"></script>
	<link type="text/css" rel="stylesheet" href="/seeyon/apps_res/doc/css/xtree.css${v3x:resSuffix()}">
</head>
<body scroll="no" onresize="resizeBody(120,'treeandlist','min','left')">
<fmt:message key="org.entity.disabled" var="orgDisabled"/>
<fmt:message key="org.entity.deleted" var="orgDeleted"/>
<div  class="scrollList border-padding">
	<script type="text/javascript">

		//点击部门树的单位节点,显示单位信息页面
		function showAccountInfo(accountid){
			parent.listFrame.location.href = secretAuditURL+"?method=listMember&selectAccountId="+accountid;
		}
		
		//点击部门树的部门节点,显示部门信息页面
		function showDepartmentInfo(deptid){
			parent.listFrame.location.href = secretAuditURL+"?method=listMember&selectDepartmentId="+deptid;
		}
	
		var root = new WebFXTree("root", "<fmt:message key='import.type.organization' bundle='${orgI18N}'/>", "");
		root.setBehavior('classic');
		root.icon = "<c:url value='/common/images/left/icon/5101.gif'/>";
		root.openIcon = "<c:url value='/common/images/left/icon/5101.gif'/>";
		
		<c:forEach items="${accountlist}" var="account">
			<c:if test="${account.status == 2}"><c:set var="status" value="(${orgDisabled})"/></c:if>
	    	<c:if test="${account.status == 3}"><c:set var="status" value="(${orgDeleted})"/></c:if>
			<c:choose>
				<c:when test="${account.superior==null || account.superior==-1}">
					var account${fn:replace(account.id,'-','_')} = new WebFXTreeItem("${account.id}","${v3x:escapeJavascript(account.name)}${v3x:escapeJavascript(status)}","javascript:showAccountInfo('${account.id}');", "javascript:showAccountInfo('${account.id}')");
					account${fn:replace(account.id,'-','_')}.icon = "<c:url value='/common/images/left/icon/1201.gif'/>";
					account${fn:replace(account.id,'-','_')}.openIcon = "<c:url value='/common/images/left/icon/1201.gif'/>";
				</c:when>
				<c:otherwise>
					var account${fn:replace(account.id,'-','_')} = new WebFXTreeItem("${account.id}","${v3x:escapeJavascript(account.name)}","javascript:showAccountInfo('${account.id}');", "javascript:showAccountInfo('${account.id}')");
						account${fn:replace(account.id,'-','_')}.icon = "<c:url value='/common/images/left/icon/5104.gif'/>";
						account${fn:replace(account.id,'-','_')}.openIcon = "<c:url value='/common/images/left/icon/5104.gif'/>";
				</c:otherwise>
			</c:choose>
		</c:forEach>
	
		<c:forEach items="${accountlist}" var="a">
			<c:choose>
				<c:when test="${a.superior==null || a.superior==-1}">
					root.add(account${fn:replace(a.id,'-','_')});
				</c:when>
				<c:otherwise>
					try{
						account${fn:replace(a.superior,'-','_')}.add(account${fn:replace(a.id,'-','_')});
					}
					catch(e){
						//alert(e.message);
					}
				</c:otherwise>
			</c:choose>
		</c:forEach>

		<c:forEach items="${deptlist}" var="dept">
			<c:if test="${t.v3xOrgDepartment.status == 2}"><c:set var="status" value="(${orgDisabled})"/></c:if>
			<c:if test="${t.v3xOrgDepartment.status == 3}"><c:set var="status" value="(${orgDeleted})"/></c:if>
			var dept${fn:replace(dept.v3xOrgDepartment.id,'-','_')} = new WebFXTreeItem("${dept.v3xOrgDepartment.id}","${v3x:escapeJavascript(dept.v3xOrgDepartment.name)}","javascript:showDepartmentInfo('${dept.v3xOrgDepartment.id}');", "javascript:showDepartmentInfo('${dept.v3xOrgDepartment.id}')");
			dept${fn:replace(dept.v3xOrgDepartment.id,'-','_')}.icon = "<c:url value='/common/js/xtree/images/file1.gif'/>";
			dept${fn:replace(dept.v3xOrgDepartment.id,'-','_')}.openIcon = "<c:url value='/common/js/xtree/images/file1.gif'/>";
		</c:forEach>
	
		<c:forEach items="${deptlist}" var="dept">
			<c:choose>
				<c:when test="${dept.parentId == null}">
					account${fn:replace(dept.v3xOrgDepartment.orgAccountId,'-','_')}.add(dept${fn:replace(dept.v3xOrgDepartment.id,'-','_')});
				</c:when>
				<c:otherwise>
					try{
						dept${fn:replace(dept.parentId,'-','_')}.add(dept${fn:replace(dept.v3xOrgDepartment.id,'-','_')});
					}
					catch(e){
						//alert(e.message);
					}
				</c:otherwise>
			</c:choose>
		</c:forEach>
		document.write(root);
		account${fn:replace(groupAccountId,'-','_')}.expand();
	</script>
</div>
</body>
</html>
