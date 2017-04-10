<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="../organizationHeader.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
getA8Top().showLocation(1423);
</script>
</head>
<body class="tab-body" scroll="no">

<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
	<td class="tab-body-bg" style="margin: 0px;padding:0px;">
		<iframe  frameborder="no" id="listFrame" name="listFrame" src="${organizationURL}?method=listFiledownload" style="width:100%;height: 100%;" border="0px"></iframe>	
	</td>
  </tr>
</table>

</body>
</html>
