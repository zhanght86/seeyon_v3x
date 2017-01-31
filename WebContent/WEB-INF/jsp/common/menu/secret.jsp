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
<%--安全保密管理--%>
var menu91 = new Menu(91, "<fmt:message key='menu.secret.manage'/>");
menu91.add(new MenuItem(9101, "<fmt:message key='menu.secret.audit'/>", "${genericController}?ViewPage=secretAudit/listMember/listMemberFrame", "main", '<c:url value="/common/images/left/icon/2302.gif" />', "", true));
menu91.add(new MenuItem(9102, "<fmt:message key='menu.secret.history'/>", "${genericController}?ViewPage=secretAudit/auditLog/auditLogMainFram", "main", '<c:url value="/common/images/left/icon/2303.gif" />', "", true));
menuArray.add(menu91);

<%--系统设置--%>
var menu92 = new Menu(92, "<fmt:message key='menu.admin.setting'/>");
menu92.add(new MenuItem(9201, "<fmt:message key='menu.manager.modifyPassword'/>", "<html:link renderURL='/manager.do?method=managerFrame&from=secret' />", "main", '<c:url value="/common/images/left/icon/2301.gif" />', "", true));
menuArray.add(menu92);
//-->
</script>