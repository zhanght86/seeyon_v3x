<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<SCRIPT language=JavaScript>
<!--
<%-- 
/**
 * 一级菜单定义
 * Menu(id, name, isShow)
 * MenuItem(uId, name, action, target, icon, description, isShow)
 */
--%>
<%--审计管理--%>
var menu61 = new Menu(61, "<fmt:message key='menu.audit.manage'/>");
menu61.add(new MenuItem(6101, "<fmt:message key='menu.audit.loginLog'/>", "<html:link renderURL='/logonLog.do?method=detailSearch&from=audit' />", "main", '<c:url value="/common/images/left/icon/2302.gif" />', "", true));
menu61.add(new MenuItem(6102, "<fmt:message key='menu.audit.appLog'/>", "<html:link renderURL='/appLog.do?method=mainFrame&from=audit' />", "main", '<c:url value="/common/images/left/icon/2303.gif" />', "", true));
menuArray.add(menu61);

<%--系统设置--%>
var menu62 = new Menu(62, "<fmt:message key='menu.admin.setting'/>");
menu62.add(new MenuItem(6201, "<fmt:message key='menu.manager.modifyPassword'/>", "<html:link renderURL='/manager.do?method=managerFrame&from=audit' />", "main", '<c:url value="/common/images/left/icon/2301.gif" />', "", true));
menuArray.add(menu62);
//-->
</script>