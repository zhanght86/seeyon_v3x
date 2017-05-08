<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="docHeader.jsp"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/layout.css${v3x:resSuffix()}" />">
<%@ include file="../common/INC/noCache.jsp"%>
<%@page import="java.util.List" %>
<%@page import="com.seeyon.v3x.plugin.menu.ThirdpartyAddinMenu"%>
<%@page import="com.seeyon.v3x.common.i18n.ResourceBundleUtil" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/doc/js/thirdMenu.js${v3x:resSuffix()}" />"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/doc/css/doc.css${v3x:resSuffix()}" />">
<title></title>
<script type="text/javascript">
<!--
	getA8Top().contentFrame.topFrame.prevMenuItemId = getA8Top().contentFrame.topFrame.cursorMenuItemId;
	getA8Top().contentFrame.topFrame.cursorMenuItemId = '502';
	
	function showOrhiddenDoc() {
		var parentLayout = parent.document.getElementById("layout");
		if(parentLayout){
			if(parentLayout.cols == "0,*") {
				parentLayout.cols = "127,*";
				parent.document.getElementById("treeFrame").noResize=false;
				//getA8Top().contentFrame.document.getElementById('LeftRightFrameSet').cols = "0,*";
			}else {
				parentLayout.cols = "0,*";
				parent.document.getElementById("treeFrame").noResize=true;
			}
			try{
				getA8Top().contentFrame.leftFrame.closeLeft();
			}catch(e){
			}
		}
	}
	function toBack(){
		var exUrl = "&resId=" + docResId + "&frType=" + frType + "&docLibId=" + docLibId 
						+ "&docLibType=" + docLibType + "&isShareAndBorrowRoot=" + isShareAndBorrowRoot
						+ "&all=" + all + "&edit=" + edit + "&add=" + add + "&readonly=" + readonly 
						+ "&browse=" + browse + "&list=" + list;
	
		var url1 = jsURL + "?method=rightNew" + exUrl;
		try{
			//文档查询后，frameset，恢复到默认宽度且不可拖动(防止换行显示)，点击‘返回’按钮，恢复默认状态
			if(parent.layout){
				if(parent.layout.cols != "0,*") {
					parent.layout.cols="140,*";
					//parent.document.getElementById("treeFrame").noResize=false;	
				}
			}	
		}catch(e){}
		location.href = url1;
	}
	function readyToPersonalLearn(){
		if(hasSelectedData())
			selectPeopleFun_perLearn();
		else
			return;
	}

	window.onload = function() {
		var qm = '${param.method}';
		if('${param.queryFlag}' == 'true' && qm != 'advancedQuery'){
			var conditionValue = '';
			var textfieldValue = '';
			var textfield1Value = '';
			if('rightNew' == qm ){
				textfieldValue = '${param.pingHoleSelect}';	
				conditionValue = 'pingHoleAlready';				
			}
			else {
				conditionValue = '${simpleQueryModel.propertyName}|${simpleQueryModel.propertyType}|${simpleQueryModel.simple}';
				textfieldValue = '${simpleQueryModel.value1}';	
				textfield1Value = '${simpleQueryModel.value2}';
			}
			docMenuShowCondition(conditionValue, textfieldValue, textfield1Value);
		}
	}
	
	function docMenuShowCondition(condition, value, value1){
		var conditionObj = document.getElementById("condition");
		selectUtil(conditionObj, condition);
	    showNextCondition(conditionObj);
	    setFlag(condition);

	    if(condition == 'pingHoleAlready') {
	    	var pingHoleSelectObj = document.getElementById("pingHoleSelect");
			selectUtil(pingHoleSelectObj, value);
	    }
	    else if(condition == 'frType') {
	    	selectUtil(document.getElementById("frType"), value);
	    }
	    else {
			try {
		    	document.getElementById("${simpleQueryModel.paramName1}").value = value;
				if("${simpleQueryModel.paramName2}" != "")
					document.getElementById("${simpleQueryModel.paramName2}").value = value1;
			} catch(e) {}
	    }
	}
	var isNeedSort = '${isNeedSort}';

	//截取公共的列表行选中事件
	function selectRow(currentTd){
		var e = v3x.getEvent();
		var tmp;
		if (ie5){
			tmp = e.srcElement;
		}else if (dom){
			tmp = e.target;
		}
		if(tmp.tagName == 'INPUT'){
			return;
		}

		//清零权限列表
		docListAclMap = new Properties();
		docListAclMap.put('parent', new docListAcl('${param.all}', '${param.edit}', '${param.add}',
			'${param.readonly}', '${param.browse}', '${param.list}', 'false', 'false', 'false', appData.doc, 'false'));
		ctrlDocMenuByAclMap();
		
		var currentTr = getParent(currentTd, "TR");
		var currentTbody = getParent(currentTr, "tbody");
		if(currentTr != null && currentTbody != null){
			redoStyle();
			changeSelectedStyle(currentTr);
			currentSelectTr = currentTr;
			var thisCheckbox = getCheckboxFromTr(currentTr);
			if(thisCheckbox != undefined && thisCheckbox != null) {
				noSelected(thisCheckbox.name);
				if(thisCheckbox.disabled != true){
					thisCheckbox.click();
				}
			}
		}
	}
//-->
</script>
<c:set value="${v3x:getSysFlagByName('sys_isGovVer') ? '.rep' : ''}" var="govLabel" />
<%@ include file="rightHead.jsp"%>
<style>
/***layout*row1+row2***/
.main_div_row2 {
 width: 100%;
 height: 100%;
 _padding-left:0px;
}
.right_div_row2 {
 width: 100%;
 height: 100%;
 _padding:54px 0px 0px 0px;
}
.main_div_row2>.right_div_row2 {
 width:auto;
 position:absolute;
 left:0px;
 right:0px;
}
.center_div_row2 {
 width: 100%;
 height: 100%;
 /*background-color:#00CCFF;*/
 overflow:auto;
}
.right_div_row2>.center_div_row2 {
 height:auto;
 position:absolute;
 top:54px;
 bottom:0px;
}
.top_div_row2 {
 height:54px;
 width:100%;
 /*background-color:#9933FF;*/
 position:absolute;
 top:0px;
}
.searchAbselute{
position: absolute;
top:30px;
*top:0px;
_top:0px;
right:0px;
*width:295px;
_width:295px;
}
/***layout*row1+row2****end**/
</style>
</head>
<body  class="listPadding">
<div class="main_div_row2">
  <div class="right_div_row2">
    <div class="top_div_row2">
		<table height="54" border="0" width="100%" cellspacing="0" cellpadding="0" >
			<tr height="24">
				<td  colspan="2" class="border-bottom main-bg" valign="top">
					<span class="location_text">
						<fmt:message key='doc.now.location.label'/> <span id="nowLocation"></span>        
					</span>
					<script type="text/javascript">
						var queryfix = "";
						if('${param.queryFlag}' == 'true')
							queryfix = " - " + "<fmt:message key='doc.loc.search.result' />";
						showLocationText("${docLoc}" + queryfix);
					</script>
				</td>
			</tr>
			<tr>
				<td id="rightMenuBar" height="30" class="webfx-menu-bar"  valign="top">		
					<script>
						//知识管理工具栏菜单
						//新建二级菜单
						var newSubItems = new WebFXMenu;
						newSubItems.add(new WebFXMenuItem("html", "<fmt:message key='doc.menu.new.document.label'/>", "createDoc('${editorHtml}');", "<c:url value='/apps_res/doc/images/docIcon/html_small.gif'/>", "", ""));
						if(v3x.getBrowserFlag("officeMenu") == true&&${v3x:isOfficeOcxEnable()}){
							try{
								var pw=getA8Top();
								var ocxObj=new ActiveXObject("HandWrite.HandWriteCtrl");
								pw.installDoc= ocxObj.WebApplication(".doc");
								pw.installXls=ocxObj.WebApplication(".xls");
								pw.installWps=ocxObj.WebApplication(".wps");
								pw.installEt=ocxObj.WebApplication(".et");
								}catch(e)
								{
									pw.installDoc=false;
									pw.installXls=false;
									pw.installWps=false;
									pw.installEt=false;
								}
								if(pw.installDoc)newSubItems.add(new WebFXMenuItem("word", "<fmt:message key='doc.menu.new.word.label'/>", "createDoc('${editorWord}');", "<c:url value='/apps_res/doc/images/docIcon/doc_small.gif'/>", "", ""));
								if(pw.installXls)newSubItems.add(new WebFXMenuItem("excel", "<fmt:message key='doc.menu.new.excel.label'/>", "createDoc('${editorExcel}');", "<c:url value='/apps_res/doc/images/docIcon/xls_small.gif'/>", "", ""));
								if(pw.installWps)newSubItems.add(new WebFXMenuItem("wpsword", "<fmt:message key='common.body.type.wpsword.label' bundle='${v3xCommonI18N}'/>", "createDoc('${editorWpsWord}')", "<c:url value='/common/images/toolbar/bodyType_wpsword.gif'/>"));
								if(pw.installEt)newSubItems.add(new WebFXMenuItem("wpsexcel", "<fmt:message key='common.body.type.wpsexcel.label' bundle='${v3xCommonI18N}'/>", "createDoc('${editorWpsExcel}')", "<c:url value='/common/images/toolbar/bodyType_wpsexcel.gif'/>"));
						}
						newSubItems.add(new WebFXMenuItem("folder", "<fmt:message key='doc.menu.new.folder.label'/>", "createFolder('${parent.versionEnabled}', '${parent.commentEnabled}');", "<c:url value='/apps_res/doc/images/docIcon/folder_close.gif'/>"));	
						if(isEdocLib == 'true') {
							newSubItems.add(new WebFXMenuItem("edocFolder", "<fmt:message key='doc.menu.new.folder.edoc'/>", "createEdocFolder('${parent.commentEnabled}');", ""));
						}
						
						// 发送到二级菜单
						var sendToSubItems = new WebFXMenu;
						sendToSubItems.add(new WebFXMenuItem("favorite", "<fmt:message key='doc.menu.sendto.favorite.label'/>", "addMyFavorite('undefined');", ""));
						sendToSubItems.add(new WebFXMenuItem("deptDoc", "<fmt:message key='doc.menu.sendto.deptDoc.label'/>", "sendToDeptDoc('${depAdminSize}', 'right')", ""));
						sendToSubItems.add(new WebFXMenuItem("publish", "<fmt:message key='doc.menu.sendto.space.label'/>", "publishDoc('undefined');", ""));	
						sendToSubItems.add(new WebFXMenuItem("group", "<fmt:message key='doc.menu.sendto.group${govLabel}'/>", "sendToGroup('undefined');", ""));	
					
						sendToSubItems.add(new WebFXMenuItem("learning", "<fmt:message key='doc.menu.sendto.learning.label'/>", "readyToPersonalLearn()", ""));	
						sendToSubItems.add(new WebFXMenuItem("deptLearn", "<fmt:message key='doc.menu.sendto.deptLearn.label'/>", "sendToDeptLearn('${depAdminSize}', 'right')", ""));
						sendToSubItems.add(new WebFXMenuItem("accountLearn", "<fmt:message key='doc.menu.sendto.accountLearn.label'/>", "sendToAccountLearn('right')", ""));	
						sendToSubItems.add(new WebFXMenuItem("groupLearn", "<fmt:message key='doc.menu.sendto.group.learning${govLabel}'/>", "sendToGroupLearn('right')", ""));	
					
						sendToSubItems.add(new WebFXMenuItem("link", "<fmt:message key='doc.menu.sendto.other.label'/>", "selectDestFolder('undefined','${param.resId}','${param.docLibId}','${param.docLibType}','link');", ""));
					
						// 高级二级菜单
						var forwardSubItems = new WebFXMenu;
						if(isEdocLib != 'true') {
							forwardSubItems.add(new WebFXMenuItem("col", "<fmt:message key='common.toolbar.transmit.col.label' bundle='${v3xCommonI18N}' />", "sendToCollFromMenu()", ""));
							forwardSubItems.add(new WebFXMenuItem("mail", "<fmt:message key='common.toolbar.transmit.mail.label' bundle='${v3xCommonI18N}' />", "sendToMailFromMenu()", ""));
						}
		
						if(all== 'true'){
					        if('${param.frType}'!=101&&'${param.frType}'!=102&&'${param.frType}'!=34&&'${param.frType}'!=35&&'${param.frType}'!=110&&'${param.frType}'!=111&&'${param.frType}'!=103&&'${param.frType}'!=32&&'${param.frType}'!=43&&'${param.frType}'!=44&&'${param.frType}'!=45&&'${param.frType}'!=46)
					        	forwardSubItems.add(new WebFXMenuItem("orderBtn","<fmt:message key='doc.contenttype.wenjian'/><fmt:message key='common.toolbar.order.label' bundle='${v3xCommonI18N}' />","docResourcesOrder('${parent.id}','${parent.frType}','${isNeedSort}')",[8,9],"",null));
						}
						if(isEdocLib != 'true'){
							forwardSubItems.add(new WebFXMenuItem("downloadFile", "<fmt:message key='doc.menu.downloadFile.label'/>", "doloadFile('${v3x:currentUser().id}')", ""));
						}
						var myBar = new WebFXMenuBar("${pageContext.request.contextPath}");
						myBar.add(new WebFXMenuButton("new", "<fmt:message key='doc.menu.new.label'/>", "", [1,1],"<fmt:message key='doc.menu.new.label'/>", newSubItems));
						if(v3x.getBrowserFlag("hideMenu") == true){
							myBar.add(new WebFXMenuButton("upload", "<fmt:message key='doc.menu.upload.label'/>", "fileUpload('upload');", [1,6],"<fmt:message key='doc.menu.upload.label'/>", null));
						}
						myBar.add(new WebFXMenuButton("sendto", "<fmt:message key='doc.menu.sendto.label'/>", "",[9,1],"<fmt:message key='doc.menu.sendto.label'/>", sendToSubItems));
						myBar.add(new WebFXMenuButton("move", "<fmt:message key='doc.menu.move.label'/>", "selectDestFolder('undefined','${param.resId}','${param.docLibId}','${param.docLibType}','move');",[2,1],"<fmt:message key='doc.menu.move.label'/>", null));
						myBar.add(new WebFXMenuButton("del", "<fmt:message key='common.toolbar.delete.label' bundle='${v3xCommonI18N}' />", "delF('topOperate','topOperate')",[1,3],"<fmt:message key='common.toolbar.delete.label' bundle='${v3xCommonI18N}' />", null));
						myBar.add(new WebFXMenuButton("forward", "<fmt:message key='common.advance.label' bundle='${v3xCommonI18N}'/>", "",[18,6],"<fmt:message key='common.advance.label' bundle='${v3xCommonI18N}'/>", forwardSubItems));
		
						var shareAndBorrow = '${param.isShareAndBorrowRoot}';
						
					 	<v3x:showThirdMenus rootBarName="myBar" parentBarName="forwardSubItems" addinMenus="${AddinMenus}"/>
							
						myBar.add(new WebFXMenuButton("show", "<fmt:message key='doc.menu.hidden.label'/>", "showOrhiddenDoc()",[3,3],"<fmt:message key='doc.menu.hidden.label'/>", null));		
						
						document.write(myBar);
						
						// 控制菜单操作权限
						initFun(all, edit, add, readonly, browse, list, isPrivateLib, folderEnabled, a6Enabled, officeEnabled, uploadEnabled, isGroupLib, isEdocLib, isShareAndBorrowRoot);
						
						if('${param.queryFlag}' == 'true'){
							document.getElementById('new').disabled = true;
							document.getElementById('upload').disabled = true;
						}
					</script>
					
					<%
						List<ThirdpartyAddinMenu> addinMenus = (List)request.getAttribute("AddinMenus");
					%>
							
				</td>
				<td class="webfx-menu-bar" >
				<div class="searchAbselute">
					<c:if test ='${param.frType!=101&&param.frType!=102&&param.frType!=34&&param.frType!=35&&param.frType!=110&&param.frType!=111&&param.frType!=103&&param.frType!=32&&param.frType!=43&&param.frType!=44&&param.frType!=45&&param.frType!=46}'>
						<%@ include file="simplesearch.jsp"%>
					</c:if>
				</div>	
				</td>
			</tr>
		</table>
    </div>
    <div class="center_div_row2" id="scrollListDiv">
		<%@ include file="advancedsearch.jsp"%>
		<form action="" name="theForm" id="theForm" method="post" style='display: none'></form>
		
<div id="ScrollDIV">
<script type="text/javascript">
	try{
		var searchForm = document.getElementById("searchForm");
		document.getElementById("ScrollDIV").style.height = document.body.clientHeight - 55;
	}catch(e){
		
	}
</script>
<%@ include file="rightTable.jsp"%>
</div>
		<iframe name="delIframe" style="display:none;" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
		<form action="" method="post" name="thirdMenuForm" id="thirdMenuForm">
			<input type="hidden" name="thirdMenuIds" id="thirdMenuIds">
		</form>
		<iframe name="orderIframe" style="display:none;" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
		<div id="pubDate"></div>
		<IFRAME height="0%" name="empty" style="display:none;" id="empty" width="0%" frameborder="0"></IFRAME>
		<iframe id="emptyIframe" style="display:none;" name="emptyIframe" frameborder="0"
			height="0" width="0" scrolling="no" marginheight="0" marginwidth="0" />
		<iframe height="100%" name="dataIFrame" scroll="no" id="dataIFrame" width="100%" frameborder="0"></iframe>
    </div>
  </div>
</div>
</body>
</html>