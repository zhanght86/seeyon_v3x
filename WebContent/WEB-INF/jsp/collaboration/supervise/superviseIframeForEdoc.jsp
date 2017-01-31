<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../Collaborationheader.jsp"%>
<%@ include file="../../common/INC/noCache.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title id="ititle"><fmt:message key="edoc.supervise.label" /></title>
</head>
<body scroll=no>
<IFRAME name="myframe" id="myframe" scrolling="no" frameborder="0" width="100%" height="100%" src="${colSuperviseURL}?method=edocSuperviseWindow&summaryId=${param.summaryId}&secretLevel=${param.secretLevel}"></IFRAME>
</body>
</html>