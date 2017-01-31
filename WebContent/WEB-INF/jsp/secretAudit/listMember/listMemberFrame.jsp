<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>mainframe</title>
		<%@include file="../head.jsp"%>
	</head>
	<script type="text/javascript">
	getA8Top().showLocation("9101");
	</script>
	<frameset>
		<frameset cols="20%,*" id="treeandlist" name = "treeandlist" rows="*" frameborder="yes" frameSpacing="5" bordercolor="#ececec">
			<frame src="${secretAuditURL}?method=showtree"  name="treeFrame" frameborder="0" id="treeFrame"  scrolling="no"/>
			<frameset rows="35%,*" id='sx' cols="*" border="0" frameBorder="no" frameSpacing="0">
				<frame src="${secretAuditURL}?method=listMember" frameborder="0" name="listFrame" id="listFrame" scrolling="no"/>
				<frame src="<c:url value="/common/detail.jsp?direction=Down" />" name="detailFrame" id="detailFrame" frameborder="0" border="0" scrolling="no"/>
			</frameset>
		</frameset>
	</frameset>

	<noframes>
		<body scroll="no"></body>
	</noframes>
</html>