<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/main" prefix="main"%>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript">
<!--
	var LOCATION_FLAG = "docRightNew";
	var all = '${param.all}';
	var edit = '${param.edit}';
	var add = '${param.add}';
	var readonly = '${param.readonly}';
	var browse = '${param.browse}';
	var list = '${param.list}';
	
	var isPrivateLib = '${isPrivateLib}';
	var isGroupLib = '${isGroupLib}';
	var isAdministrator = '${isAdministrator}';
	var isGroupAdmin = '${isGroupAdmin}';
	var depAdminSize = '${depAdminSize}';
	
	var isShareAndBorrowRoot = '${param.isShareAndBorrowRoot}';
	
	var libName = '${param.libName}';
	var isLibOwner = "${isLibOwner}";
	
	var folderEnabled = '${folderEnabled}';
	var a6Enabled = '${a6Enabled}';
	var officeEnabled = (${v3x:hasPlugin('officeOcx') == 'true'} && ${officeEnabled == 'true'}).toString();
	var uploadEnabled = '${uploadEnabled}';
	var isEdocLib = '${isEdocLib}';
	
	var docResId = '${param.resId}';
	var parentPath = '${parent.logicalPath}';
	var parentCommentEnabled = '${parent.commentEnabled}';
	var parentVersionEnabled = '${parent.versionEnabled}';
	var frType = '${param.frType}';
	
	var docLibId = '${param.docLibId}';
	var docLibType = '${param.docLibType}';	
	
	var currentUserId = '${sessionScope['com.seeyon.current_user'].id}';	
	var isPersonalLib = '${isPersonalLib}';
	var isEdocLib = '${isEdocLib}';	
	var noShare = '${noShare}';
	
	// 菜单权限
	var canNewColl = "${v3x:hasNewCollaboration()}";
	var canNewMail = "${v3x:hasNewMail()}";
	
	var openFrom="docLib";
	if(isShareAndBorrowRoot=="true"){openFrom="lenPotent";}
	if(isEdocLib=="true"){openFrom="edocDocLib";}
//-->
</script>
<v3x:selectPeople id="perLearnPop" panels="Department,Team,Post,Outworker" selectType="Member"
	departmentId="${sessionScope['com.seeyon.current_user'].departmentId}" 
	jsFunction="sendToLearn(elements, 'pop')" minSize="1" />
<v3x:selectPeople id="perLearn" panels="Department,Team,Post,Outworker" selectType="Member"
	departmentId="${sessionScope['com.seeyon.current_user'].departmentId}" 
	jsFunction="sendToLearn(elements)" minSize="1" />
<script type="text/javascript">
	if('${isGroupLib}' != 'true'){
		onlyLoginAccount_perLearn = true;
		onlyLoginAccount_perLearnPop = true;
	}
	isNeedCheckLevelScope_perLearnPop = false;
    showOriginalElement_perLearn = false;
</script>
<link type="text/css" rel="stylesheet" href="<c:url value='/apps_res/doc/css/docMenu.css${v3x:resSuffix()}' />"></link>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/doc/js/docMenu.js${v3x:resSuffix()}" />"></script>
<script>
    <!--
    	<c:set value="${v3x:getSysFlagByName('sys_isGovVer') ? '.rep' : ''}" var="govLabel" />
	 	var menu = new RightMenu("${pageContext.request.contextPath}");
		menu.AddExtendMenu("sendto","<fmt:message key='doc.menu.sendto.label'/>",[1,4],"rbpm");
		menu.AddItem("favorite","<fmt:message key='doc.menu.sendto.favorite.label'/>","","sendto","","favorite");
		menu.AddItem("deptDoc","<fmt:message key='doc.menu.sendto.deptDoc.label'/>","","sendto","","deptDoc");
		menu.AddItem("publish","<fmt:message key='doc.menu.sendto.space.label'/>","","sendto","","publish");
		menu.AddItem("group","<fmt:message key='doc.menu.sendto.group${govLabel}'/>","","sendto","","group");
		menu.AddItem("learning","<fmt:message key='doc.menu.sendto.learning.label'/>","","sendto","","learning");
		menu.AddItem("deptLearn","<fmt:message key='doc.menu.sendto.deptLearn.label'/>","","sendto","","deptLearn");
		menu.AddItem("accountLearn","<fmt:message key='doc.menu.sendto.accountLearn.label'/>","","sendto","","accountLearn");
		menu.AddItem("groupLearn","<fmt:message key='doc.menu.sendto.group.learning${govLabel}'/>","","sendto","","groupLearn");
		menu.AddItem("link","<fmt:message key='doc.menu.sendto.other.label'/>","","sendto","","link");
		menu.AddItem("secretLevel","<fmt:message key='doc.menu.doc.secretLevel.label'/>","<c:url value='/apps_res/doc/images/sendotherto.gif'/>","rbpm","${detailURL}?method=setDocSecretLevel","secretLevel");//成发集团项目 
		menu.AddExtendMenu("forward","<fmt:message key='doc.menu.forward.label'/>",[1,7],"rbpm");
		menu.AddItem("info","<fmt:message key='common.toolbar.transmit.col.label' bundle='${v3xCommonI18N}' />","","forward","${detailURL}?method=sendToColl","forward");
		menu.AddItem("mail","<fmt:message key='common.toolbar.transmit.mail.label' bundle='${v3xCommonI18N}' />","","forward","${detailURL}?method=sendToWebMail","forward");
		if(v3x.getBrowserFlag("hideMenu") == true){
			menu.AddItem("download","<fmt:message key='doc.menu.download.label'/>",[6,4],"rbpm","","download");
		}
		menu.AddItem("separator","","","rbpm",null);
		if(v3x.getBrowserFlag("hideMenu") == true){
			menu.AddItem("edit","<fmt:message key='doc.menu.edit.label'/>",[1,2],"rbpm","","edit");
		}
		menu.AddItem("move","<fmt:message key='doc.menu.move.label'/>",[2,1],"rbpm","","move");
		if(v3x.getBrowserFlag("hideMenu") == true){		
			menu.AddItem("replace","<fmt:message key='doc.menu.replace.label'/>",[7,4],"rbpm","","replace");
		}
		menu.AddItem("del","<fmt:message key='common.toolbar.delete.label' bundle='${v3xCommonI18N}' />",[1,3],"rbpm","","del");
		menu.AddItem("rename","<fmt:message key='doc.menu.rename.label'/>",[3,9],"rbpm","${detailURL}?method=reName","rename");		
			
		menu.AddItem("lock","<fmt:message key='doc.menu.lock.label'/>",[16,6],"rbpm","","lock");
		menu.AddItem("unlock","<fmt:message key='doc.menu.unlock.label'/>",[16,6],"rbpm","","unlock");
		  
		
		if(v3x.getBrowserFlag("hideMenu") == true){
			menu.AddItem("separator","","","rbpm",null);	 		  
			menu.AddItem("share","<fmt:message key='doc.menu.share.label'/>",[8,1],"rbpm","${detailURL}?method=docPropertyIframe&isP=false&isB=false&isM=true&isC=false&docLibType=${docLibType}&docLibId=${param.docLibId}&resId=${param.resId}&frType=${param.frType}&all=${param.all}&edit=${param.edit}&add=${param.add}&readonly=${param.readonly}&browse=${param.browse}&list=${param.list}&parentCommentEnabled=${param.parentCommentEnabled}&flag=${param.flag}&isShareAndBorrowRoot=${isShareAndBorrowRoot}","properties");
			menu.AddItem("share_pub","<fmt:message key='doc.menu.share.label'/>",[8,1],"rbpm","${detailURL}?method=docPropertyIframe&isP=false&isB=false&isM=false&isC=true&docLibType=${docLibType}&docLibId=${param.docLibId}&resId=${param.resId}&frType=${param.frType}&parentCommentEnabled=${param.parentCommentEnabled}&flag=${param.flag}&isShareAndBorrowRoot=${isShareAndBorrowRoot}","properties");
			menu.AddItem("lend","<fmt:message key='doc.menu.lend.label'/>",[17,7],"rbpm","${detailURL}?method=docPropertyIframe&isP=false&isB=true&isM=false&isC=false&docLibType=${docLibType}&isShareAndBorrowRoot=${isShareAndBorrowRoot}","properties");
		}
		menu.AddItem("log","<fmt:message key='doc.menu.viewlog.label'/>",[2,9],"rbpm","","logView");
		menu.AddItem("docHistory", "<fmt:message key='doc.menu.history.label'/>", [8,10], "rbpm", "", "docHistory");
		menu.AddItem("learnHistory","<fmt:message key='doc.menu.learn.history.label'/>",[2,9],"rbpm","","learnHistory");
		menu.AddItem("alert","<fmt:message key='doc.menu.subscribe.label'/>",[13,9],"rbpm","","alert");
		  		  
		menu.AddItem("separator","","","rbpm",null);
		menu.AddItem("property","<fmt:message key='doc.menu.properties.label'/>",[4,8],"rbpm","${detailURL}?method=docPropertyIframe&isP=true&isB=false&isM=false&isC=false&docLibType=${param.docLibType}&docLibId=${param.docLibId}&resId=${param.resId}&frType=${param.frType}&parentCommentEnabled=${param.parentCommentEnabled}&flag=${param.flag}&isShareAndBorrowRoot=${param.isShareAndBorrowRoot}","properties");  
		document.writeln(menu.GetMenu());
//-->
</SCRIPT>
<style>
<!--
.bodyc{
	padding: 5px 0px 0px 10px;
}
.docellipsis{
	table-layout: auto;
}
.docellipsis td{
	white-space:nowrap;
	overflow:hidden;
	text-overflow:ellipsis;
}
TD.docsort {
	PADDING-BOTTOM: 2px;
	PADDING-LEFT: 5px;
	PADDING-RIGHT: 5px;
	PADDING-TOP: 2px;	
	border-bottom: solid 1px #BDBDBD;	

	font-size: 12px;
	height: 24px;
}
TABLE.docsort {
	background-color: #FFFFFF;
	font-size: 12px;
}
.docsort THEAD TD {
	BORDER-BOTTOM: buttonshadow 0px solid;
	BORDER-LEFT: #FFFFFF 1px solid;
	BORDER-RIGHT: #c5cad2 1px solid;
	BORDER-TOP: buttonhighlight 0px solid;
	CURSOR: default;
	HEIGHT: 22px;
	PADDING-TOP: 2px;
	BACKGROUND: #eee;
	PADDING-LEFT: 5px;
	PADDING-RIGHT: 5px;
	
	word-break: keep-all;
	font-size: 12px;
	line-height: 18px;
	
	background: url("<c:url value='/common/images/sortbg.gif' />") repeat-x;
}
.docsort THEAD .arrow {
	FONT-FAMILY: webdings;
	FONT-SIZE: 10px;
	HEIGHT: 11px;
	MARGIN-BOTTOM: 2px;
	MARGIN-TOP: -3px;
	OVERFLOW: hidden;
	PADDING-BOTTOM: 2px;
	PADDING-LEFT: 0px;
	PADDING-RIGHT: 0px;
	PADDING-TOP: 0px;
	WIDTH: 10px;
	font-size: 12px;
}
//-->
</style>
<script language="javascript">
var isAdvancedQuery = ${param.method eq 'advancedQuery'};
function onkeydown(ev){
	var ev = ev || window.event; // 事件
    if(ev.keyCode==8){
		var target = ev.target  ||  ev.srcElement;//事件源
		if(target.type!='text'){//如果是输入框就允许后退
	        window.event.keyCode=0;  
	        window.event.returnValue=false; 
		}else{
			window.event.returnValue=true; 
		}
     }  

}
var pagedAclMap = new Properties();
var parentAclAll = '${param.all}';
var parentAclEdit = '${param.edit}';
var parentAclAdd = '${param.add}';
var parentAclReadonly = '${param.readonly}';
var parentAclBrowse = '${param.browse}';
var parentAclList = '${param.list}';

function docSelectAll(allButton, targetName){
	var objcts = document.getElementsByName(targetName);
	
	if(objcts != null){
		for(var i = 0; i < objcts.length; i++){
			if(objcts[i].disabled == true){
				continue;
			}
			objcts[i].checked = allButton.checked;
		}
	}
	
	if(allButton.checked){
		docListAclMap = pagedAclMap;
	}else{
		docListAclMap = new Properties();
		docListAclMap.put('parent', new docListAcl('${param.all}', '${param.edit}', '${param.add}',
			'${param.readonly}', '${param.browse}', '${param.list}', 'false', 'false', 'false', appData.doc, 'false'));
	}
	
	ctrlDocMenuByAclMap();
}
</script>