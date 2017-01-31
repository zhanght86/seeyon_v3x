/**
 * 该文件用于生成文档操作菜单。
 * @Author: 薛国伟
 * @Date: 2007年4月1日
 */

var i = 0;

var contextPath = "";

function DocResource(docResId, docResName, parentFrId, isFolder, isFile, isLink, 
	isLocked, lockedUserId, isPig, isFolderLink, 
	isLearningDoc, appEnumKey, isSysInit, mimeType, frType, versionEnabled,createUserId) {
	this.docResId = docResId;
	this.docResName = docResName;
	this.parentFrId = parentFrId;
	this.isFolder = isFolder;
	this.isFile = isFile;
//	if(isLink == 'true' || isFolderLink == 'true')
//		this.isLink = 'true';
//	else
//		this.isLink = 'false';
	this.isLink = isLink;
	this.isFolderLink = isFolderLink;

	this.isLocked = isLocked;
	this.lockedUserId = lockedUserId;
	this.isPig = isPig; // if it is an archived document,such as info,edoc,news,bulletin,...
	this.isLearningDoc = isLearningDoc;
	
	this.appEnumKey = appEnumKey;
	this.frType = frType;
	
	this.isSysInit = isSysInit;
	
	this.createUserId = createUserId;//成发集团项目
	
	if(arguments[13]!=null){
		this.mimeType = arguments[13];
	}
	if(arguments[14]!=null)
		this.versionEnabled = arguments[14];
}

var allAcl1 = "false";
var editAcl1 = "false";
var addAcl1 = "false";
var readonlyAcl1 = "false";
var browseAcl1 = "false";
var listAcl1 = "false";
function DocAcl(all, edit, write, readonly, browse, list,isCreater,lenPotent) {
	allAcl1 = this.all = all;
	editAcl1 = this.edit = edit;
	addAcl1 = this.write = write;
	readonlyAcl1 = this.readonly = readonly;
	browseAcl1 = this.browse = browse;
	listAcl1 = this.list = list;
	this.lenPotent=lenPotent;
	this.isCreater = isCreater;
}

/**
 * 主函数。
 */
function RightMenu(_contextPath) {
	contextPath = _contextPath || "";
	this.AddExtendMenu = AddExtendMenu;
	this.AddItem = AddItem;
	this.GetMenu = GetMenu;
	this.HideAll = HideAll;
	this.I_OnMouseOver = I_OnMouseOver;
	this.I_OnMouseOut = I_OnMouseOut;
	this.I_OnMouseUp = I_OnMouseUp;
	this.P_OnMouseOver = P_OnMouseOver;
	this.P_OnMouseOut = P_OnMouseOut;

	A_rbpm = new Array();
	HTMLstr  = "";
	HTMLstr += "<!-- RightButton PopMenu -->\n";
	HTMLstr += "\n";
	HTMLstr += "<!-- PopMenu Starts -->\n";
	HTMLstr += "<div id='E_rbpm' class='rm_div'>\n";
		// rbpm = right button pop menu
	HTMLstr += "<table width='100' border='0' cellspacing='0' id='docMenuTable' ";
	HTMLstr += "style='background-repeat: repeat-y;'";
	HTMLstr += ">";
	HTMLstr += "<!-- Insert A Extend Menu or Item On Here For E_rbpm -->\n";
	HTMLstr += "</table>\n";
	HTMLstr += "</div>\n";
	HTMLstr += "<!-- Insert A Extend_Menu Area on Here For E_rbpm -->";
	HTMLstr += "\n";
	HTMLstr += "<!-- PopMenu Ends -->\n";
} 

/**
 * 增加子菜单项。
 * popup为true，按钮事件从parent中取，一般情况不需要这个参数，用于createPopup时。
 */
function AddExtendMenu(id, name, icon, parent, popup) {
	var TempStr = "";
	if(HTMLstr.indexOf("<!-- Extend Menu Area : E_" + id + " -->") != -1)
	{
		alert("E_" + id + "already exist!");
		return;
	}
	eval("A_" + parent + ".length++");
	eval("A_" + parent + "[A_" + parent + ".length-1] = id");  // 将此项注册到父菜单项的ID数组中去
	TempStr += "<!-- Extend Menu Area : E_" + id + " -->\n";
	TempStr += "<div id='E_" + id + "' class='rm_div'>\n";
	TempStr += "<table width='100%' border='0' cellspacing='0' ";
	TempStr += "style='background-image: url("+contextPath+"/common/skin/default/images/xmenu/toolbar_items_bg.gif);background-repeat: repeat-y;'";
	TempStr += ">\n";
	TempStr += "<!-- Insert A Extend Menu or Item On Here For E_" + id + " -->";
	TempStr += "</table>\n";
	TempStr += "</div>\n";
	TempStr += "<!-- Insert A Extend_Menu Area on Here For E_" + id + " -->";
	TempStr += "<!-- Insert A Extend_Menu Area on Here For E_" + parent + " -->";
	HTMLstr = HTMLstr.replace("<!-- Insert A Extend_Menu Area on Here For E_" + parent + " -->", TempStr);

	eval("A_" + id + " = new Array()");
	TempStr  = "";
	TempStr += "<!-- Extend Item : P_" + id + " -->\n";

	var style1 = "padding:2px 4px 0px " + (icon ? "0" : "24") + "px;";
	var styleOver = style1 + "background-image: url("+contextPath+"/common/skin/default/images/xmenu/toolbar_select_bg.gif);background-position: center center;background-repeat: repeat-x;";

	TempStr += "<tr id='P_" + id + "' style='cursor: hand;FONT-SIZE: 12px; height: 22px;' "		
		+ "onmouseup='window.v3x.getEvent().cancelBubble=true;' "
		+ "onclick='window.v3x.getEvent().cancelBubble=true;' "
	
	TempStr	+= "><td nowrap='nowrap' style=\"" + style1 + "\" " 
		+ "onmouseover='this.style.cssText=\"" + styleOver + "\";"
		if(popup){
			TempStr += "parent.P_OnMouseOver(\"" + id + "\",\"" + parent + "\");' "
		} else {
			TempStr += "P_OnMouseOver(\"" + id + "\",\"" + parent + "\");' "
		}
		TempStr += "onmouseout='this.style.cssText=\"" + style1 + "\";" 
		if(popup){
			TempStr += "parent.P_OnMouseOut(\"" + id + "\",\"" + parent + "\");' " + ">"
		} else {
			TempStr += "P_OnMouseOut(\"" + id + "\",\"" + parent + "\");' " + ">"
		}
		
		//icon坐标
		if(typeof(icon) == 'object'){
	        var y = parseInt(icon[0],10)-1;
	        var x = parseInt(icon[1],10)-1;
	        //var g =  contextPath + '/common/images/toolbar/toolbar.trip.gif';
			TempStr += "<div style='background-image: url(/seeyon/common/skin/default/images/xmenu/arrow.right.png);background-position: right center;background-repeat: no-repeat;' >"
				+ '<IMG src="'+contextPath+'/common/images/space.gif" style="margin-right: 6px;margin-left:6px;vertical-align: middle; BACKGROUND-POSITION: -'+ (x*16) +'px -' + (y*16) + 'px;" height=16; width=16; border=0; class="toolbar-button-icon" align="absmiddle">'
				+ name
				+ "</div>"
				+ "</td></tr>\n";
		}else{
			TempStr += "<div style='background-image: url(/seeyon/common/skin/default/images/xmenu/arrow.right.png);background-position: right center;background-repeat: no-repeat;'>"
				+ (icon ? "<img src='" + icon + "' border='0' height=16 style='margin-right: 6px;margin-left:6px;vertical-align: middle;'>" : "")
				+ name
				+ "</div>"
				+ "</td></tr>\n";
		}

	TempStr += "<!-- Insert A Extend Menu or Item On Here For E_" + parent + " -->";
	HTMLstr = HTMLstr.replace("<!-- Insert A Extend Menu or Item On Here For E_" + parent + " -->", TempStr);
}
/**
 * 增加菜单项。
 * popup为true，按钮事件从parent中取，一般情况不需要这个参数，用于createPopup时。
 */
function AddItem(id, name, icon, parent, location, flag, popup) {
	var TempStr = "";

	var ItemStr = "<!-- ITEM : I_" + id + i +" -->";
	if(id.indexOf("separator") != -1)
	{
	  TempStr += ItemStr + "\n";
	  TempStr += "<tr id='I_" + id + i + "' style='height:5px;' onclick='window.v3x.getEvent().cancelBubble=true;' onmouseup='window.v3x.getEvent().cancelBubble=true;'><td><hr></td></tr>";
	  TempStr += "<!-- Insert A Extend Menu or Item On Here For E_" + parent + " -->";
	  HTMLstr = HTMLstr.replace("<!-- Insert A Extend Menu or Item On Here For E_" + parent + " -->", TempStr);
	  i++;
	  return;
	}
	if(HTMLstr.indexOf(ItemStr) != -1)
	{
	  alert("I_" + id + "already exist!");
	  return;
	}

	var style1 = "padding:2px 4px 0px " + (icon ? "0" : "24") + "px;";
	var styleOver = style1 + "background-image: url("+contextPath+"/common/skin/default/images/xmenu/toolbar_select_bg.gif);background-position: center center;background-repeat: repeat-x;";

	TempStr += ItemStr + "\n";
	TempStr += "<tr id='I_" + id + "' style='cursor: hand;FONT-SIZE: 12px; height: 22px;' "
	if(popup){
		//TempStr += "onclick='parent.window.v3x.getEvent().cancelBubble=true;' "
		TempStr += "onmouseover='parent.I_OnMouseOver(\"" + id + "\",\"" + parent + "\")' ";
		TempStr += "onmouseout='parent.I_OnMouseOut(\"" + id + "\")' ";
		if(location == null)
			TempStr += "onmouseup='parent.I_OnMouseUp(\"" + id + "\",\"" + parent + "\",null)' ";
		else
			TempStr += "onmouseup='parent.I_OnMouseUp(\"" + id + "\",\"" + parent + "\",\"" + location + "\",\"" + flag + "\")' ";
	} else { 
		TempStr += "onclick='window.v3x.getEvent().cancelBubble=true;' "
		TempStr += "onmouseover='I_OnMouseOver(\"" + id + "\",\"" + parent + "\")' ";
		TempStr += "onmouseout='I_OnMouseOut(\"" + id + "\")' ";
		if(location == null)
			TempStr += "onmouseup='I_OnMouseUp(\"" + id + "\",\"" + parent + "\",null)' ";
		else
			TempStr += "onmouseup='I_OnMouseUp(\"" + id + "\",\"" + parent + "\",\"" + location + "\",\"" + flag + "\")' ";
	}

	
	//icon坐标
	if(typeof(icon) == 'object'){
        var y = parseInt(icon[0],10)-1;
        var x = parseInt(icon[1],10)-1;
        //var g =  contextPath + '/common/images/toolbar/toolbar.trip.gif';
		TempStr	+= "><td nowrap='nowrap' style=\"" + style1 + "\" " 
		+ "onmouseover='this.style.cssText=\"" + styleOver + "\"' "
		+ "onmouseout='this.style.cssText=\"" + style1 + "\"' " + ">"
		+ '<IMG src="'+contextPath+'/common/images/space.gif" style="margin-right: 6px;margin-left:6px;vertical-align: middle; BACKGROUND-POSITION: -'+ (x*16) +'px -' + (y*16) + 'px;" height=16; width=16; border=0; class="toolbar-button-icon" align="absmiddle">'
		+ name 		
		+ "</td></tr>\n"
	}else{
		TempStr	+= "><td nowrap='nowrap' style=\"" + style1 + "\" " 
		+ "onmouseover='this.style.cssText=\"" + styleOver + "\"' "
		+ "onmouseout='this.style.cssText=\"" + style1 + "\"' " + ">"
		+ (icon ? "<img src='" + icon + "' border='0' height=16 style='margin-right: 6px;margin-left:6px;vertical-align: middle;'>" : "")
		+ name 		
		+ "</td></tr>\n"
	}
		
	TempStr += "<!-- Insert A Extend Menu or Item On Here For E_" + parent + " -->";

	HTMLstr = HTMLstr.replace("<!-- Insert A Extend Menu or Item On Here For E_" + parent + " -->", TempStr);
}
function GetMenu() {
	return HTMLstr;
}
function I_OnMouseOver(id, parent)
{
	var Item;
	if(parent != "rbpm")
	{
		var ParentItem;
		ParentItem = document.getElementById("P_" + parent);
	}
	Item = document.getElementById("I_" + id);
	HideAll(parent, 1);
}
function I_OnMouseOut(id)
{
	var Item;
	Item = document.getElementById("I_" + id);
}
function I_OnMouseUp(id, parent, location, flag) {	
	var name = id;
	if(name == 'forward' || name == 'sendto')
		name = 'P_' + name;
	else
		name = "I_" + name;
	if(document.getElementById(name).isDisabled)
		return;
	
	var ParentMenu;
	var event = window.v3x.getEvent(); 
	if(event)
		cancelBubble = true;
	OnClick()
	ParentMenu = document.getElementById("E_" + parent);	
	ParentMenu.display = "none";

	var docMenuTable = document.getElementById("docMenuTable");	
	var rowid = docMenuTable.className;
	var mainForm = document.getElementById("mainForm");	
	var objname = mainForm.oname.value;

	var m = 0;

	var isFolder = mainForm.is_folder.value;
	var isPersonalLib = mainForm.isPersonalLib.value;
	var parentId = mainForm.parentId.value;
	var docLibId = mainForm.docLibId.value;
	var docLibType = mainForm.docLibType.value;
	
	var isGroupLib = window.isGroupLib;

	if(location == null)
	{
	  eval("Do_" + id + "()");
	}
	else
	{
		// 检查选中的记录是否存在
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "docResourceExist", false);
		requestCaller.addParameter(1, "Long", rowid);
			
		var existFlag = requestCaller.serviceRequest();
		if(existFlag == 'false') {
			if(isFolder == 'true'){
				alert(v3x.getMessage('DocLang.doc_alert_source_deleted_folder'));
			}else{
				alert(v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));
			}
			window.parent.parent.location.reload(true);
			return ;
		}
		
		var surl = "";
		if(flag == "move") {		
			selectDestFolder(rowid, parentId, docLibId, docLibType, "move");
		}
		else if(flag == "del") {	
			delF(rowid, "self", isFolder);
		}
		else if(flag == "properties") {
			var propEditValue = eval("prop_edit_" + rowid);
			location+="&all="+allAcl1+"&edit="+editAcl1+"&add="+addAcl1+"&readonly="+readonlyAcl1+"&browse="+browseAcl1+"&list="+listAcl1+"";
			docProperties(location, rowid, isFolder, isPersonalLib, propEditValue, rowdata.all, window.frType);
		}
		else if(flag == "rename") {		
			surl = location + "&rowid=" + rowid;			
			rename(surl, isFolder, rowid);
		}
		else if(flag == "mygrant") {	
			surl = location + "&docResId=" + rowid;
			v3xOpenWindow(surl);
		}
		else if(flag == "docgrant") {					
			surl = location + "&docResId=" + rowid;
			v3xOpenWindow(surl);
		}	
		else if(flag == "link")	{
			selectDestFolder(rowid, parentId, docLibId, docLibType, "link");
		}
		else if(flag == "replace") {
			docReplace(rowid, rowdata.docLibType, rowdata.objName,parentId,rowdata.frType);
		}
		else if(flag == "logView") {
			logView(rowid,isFolder,objname);
		}
		else if (flag == "lock") {
			lockDoc(rowid);
		}
		else if (flag == "unlock") {
			unlockDoc(rowid, window.currentUserId);
		}
		else if (flag == "alert") {
			alertview(rowid, isFolder);
		}
		else if (flag == "favorite") {
			addMyFavorite(rowid);
		}
		else if (flag == "publish") {
			publishDoc(rowid);
		} 
		else if(id == "info") {
			sendToColl(location, rowid);
		}
		else if (flag == "edit") {
			editDoc(rowid,objname);
		}
		else if(flag == "docHistory"){
			docHistory(rowid, all, edit, add, readonly, browse, list, isShareAndBorrowRoot, docLibId, docLibType, objname);
		}
		else if (id == "mail"){
			sendToMail(location, rowid);
		}
		else if (id == "deptDoc"){
			sendToDeptDoc(depAdminSize, 'pop')
		}
		else if (id == "learning"){
			selectPeopleFun_perLearnPop();
		}
		else if (id == "deptLearn"){
			sendToDeptLearn(depAdminSize, 'pop');
		}
		else if (id == "accountLearn"){
			sendToAccountLearn('pop');
		}				
		else if (id == "learnHistory"){
			learnHistoryView(rowid, isGroupLib);
		}	
		else if (id == "group"){
			sendToGroup(rowid);
		}	
		else if (id == "groupLearn"){
			sendToGroupLearn('pop');
		}else if(id == 'download'){
			menuDownload(rowid, objname);
		}  else if(id == 'sendToCollFromOpen'){
			sendToCollFromOpen(rowid);
		} else if(id == 'sendToMailFromOpen'){
			sendToMailFromOpen(rowid);
		//成发集团项目 程炯 为文档加入密级 begin
		}else if(flag == 'secretLevel'){
			setDocSecretLevel(location,rowid);
		}
		//end
	}
}
//成发集团项目 程炯 为文档加入密级 begin
function setDocSecretLevel(location,rowid){
	var surl = location + "&docResId=" + rowid;
	var result=v3x.openWindow({
		 url: surl,
		 width: 260,
     	 height: 190,
     	 resizable: "no"
	  });
	if(result == true || result == 'true'){
		window.location.reload(true);
	}
}
//end
function menuDownload(rowid, objname){
	var isUploadFile = eval("isUploadFile_" + rowid);
	ajaxRecordOptionLog(rowid,"downLoadFile");	
	if(isUploadFile == 'true'){
		var fileId = eval("sourceId_" + rowid);
		var theDate = eval("createDate_" + rowid);
		empty.location.href="/seeyon/fileUpload.do?method=download&viewMode=download&fileId="+fileId+"&createDate="+theDate+"&filename=" + encodeURIComponent(objname);
	}else{
		top.startProc(v3x.getMessage("DocLang.doc_alert_compress_progesss"));
			
		// 压缩
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "docDownloadCompress", true);
		requestCaller.addParameter(1, "long", rowid);
		
		var flag = 'false';
		this.invoke = function(ds) {
			flag = 'true';
			top.endProc();
			empty.location.href = "/seeyon/doc.do?method=docDownloadNew&id="+rowid;
		}
			
		requestCaller.serviceRequest();
	}
}


function sendToColl(location, rowid) {
	var appEnumKey = eval("appEnumKey_" + rowid);
	var pigData = new appEnumData();
	if(appEnumKey == pigData.doc){
		var surl = location + "&docResId=" + rowid + "&docLibId=" + docLibId;
		if(getA8Top().contentFrame.LeftRightFrameSet && getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*"){
			getA8Top().contentFrame.leftFrame.closeLeft();
		}
		parent.location.href = surl;
	}else if(appEnumKey == pigData.collaboration){
		//检查源协同是否存在
		//var existFlag = pigSourceExist(appEnumKey, rowid);
		var existFlag = pigSourceExistById(rowid,appEnumKey);
	    if(existFlag == 'false') {		
		    alert(v3x.getMessage('DocLang.doc_source_doc_no_exist'));
		    return;
	     }
				// 记录转发日志
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "logForward", false);
			requestCaller.addParameter(1, "String", "false");
			requestCaller.addParameter(2, "Long", rowid);
				
			requestCaller.serviceRequest();
		
		try {
			var affairId = eval("sourceId_" + rowid);
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "getSummaryIdByAffairId", false);
			requestCaller.addParameter(1, "long", affairId);
			var summaryId = requestCaller.serviceRequest();
			
			//判断是否允许转发协同或邮件
			try{
		    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "hasForward", false);
		    	requestCaller.addParameter(1, "String", summaryId);
		    	requestCaller.addParameter(2, "String", "transColl");
		    	var ds = requestCaller.serviceRequest();
		    	if(ds && ds == "false"){
		    		alert(v3x.getMessage("collaborationLang.unallowed_forward_affair"));
		    		return;
		    	}
		    }catch(e){
		    }
			
			var rv = v3x.openWindow({
		        url : jsColURL + "?method=showForward&summaryId=" + summaryId + "&affairId=" + affairId,
		        height : 420,
		        width : 360
	    	});
			
		}catch (ex1) {
			alert("Exception : " + ex1.message);
		}
	}else if(appEnumKey == pigData.mail){
						// 记录转发日志
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "logForward", false);
			requestCaller.addParameter(1, "String", "false");
			requestCaller.addParameter(2, "Long", rowid);
				
			requestCaller.serviceRequest();
		
		var mailId = eval("sourceId_" + rowid);
		var surl = jsMailURL + "?method=convertToCol&id=" + mailId;
		if(getA8Top().contentFrame.LeftRightFrameSet && getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*"){
			getA8Top().contentFrame.leftFrame.closeLeft();
		}
		parent.location.href = surl;	
	}
	
}

function sendToMail(location, rowid) {
	var appEnumKey = eval("appEnumKey_" + rowid);
	var pigData = new appEnumData();
	if(appEnumKey == pigData.doc){
		var surl = location + "&docResId=" + rowid + "&docLibId=" + docLibId;
		if(getA8Top().contentFrame.LeftRightFrameSet && getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*"){
			getA8Top().contentFrame.leftFrame.closeLeft();
		}
		parent.location.href = surl;
	}else if(appEnumKey == pigData.collaboration){
		//检查源协同是否存在
		//var existFlag = pigSourceExist(appEnumKey, rowid);
		var existFlag = pigSourceExistById(rowid,appEnumKey);
	    if(existFlag == 'false') {		
		    alert(v3x.getMessage('DocLang.doc_source_doc_no_exist'));
		    return;
	     }
								// 记录转发日志
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "logForward", false);
			requestCaller.addParameter(1, "String", "true");
			requestCaller.addParameter(2, "Long", rowid);
			requestCaller.serviceRequest();
		
		try {
			var affairId = eval("sourceId_" + rowid);
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "getSummaryIdByAffairId", false);
			requestCaller.addParameter(1, "long", affairId);
				
			var summaryId = requestCaller.serviceRequest();
    
    			//判断是否允许转发协同或邮件
			try{
		    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "hasForward", false);
		    	requestCaller.addParameter(1, "String", summaryId);
		    	requestCaller.addParameter(2, "String", "transMail");
		    	var ds = requestCaller.serviceRequest();
		    	if(ds && ds == "false"){
		    		alert(v3x.getMessage("DocLang.formcol_not_allowed_send_mail"));
		    		return;
		    	}
		    }catch(e){
		    }
			

			if(getA8Top().contentFrame.LeftRightFrameSet && getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*"){
				getA8Top().contentFrame.leftFrame.closeLeft();
			}
			var surl = jsColURL + "?method=forwordMail&id=" + summaryId;
//			alert()
			parent.location.href = surl;	
			
		}catch (ex1) {
			alert("Exception : " + ex1.message);
		}
	}else if(appEnumKey == pigData.mail){
								// 记录转发日志
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "logForward", false);
			requestCaller.addParameter(1, "String", "true");
			requestCaller.addParameter(2, "Long", rowid);
			requestCaller.serviceRequest();
		
		var mailId = eval("sourceId_" + rowid);
		var surl = jsMailURL + "?method=autoToMail&id=" + mailId;
		if(getA8Top().contentFrame.LeftRightFrameSet && getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*"){
			getA8Top().contentFrame.leftFrame.closeLeft();
		}
		parent.location.href = surl;	
	}
}

function P_OnMouseOver(id, parent) {
	var Item;
	var Extend;
	var Parent;
	if(parent != "rbpm")
	{
		var ParentItem;
		ParentItem = document.getElementById("P_" + parent);
		//ParentItem.className = "over";
	}
	HideAll(parent, 1);
	Item = document.getElementById("P_" + id);
	Extend = document.getElementById("E_" + id);
	Parent = document.getElementById("E_" + parent);
	//Item.className = "over";
	Extend.style.display = "block";
	
	
	
	Extend.style.left = document.body.scrollLeft + Parent.offsetLeft + Parent.offsetWidth-10+"px";
	if(Extend.style.left + Extend.offsetWidth > document.body.scrollLeft + document.body.clientWidth){
		Extend.style.left = Extend.style.left - Parent.offsetWidth - Extend.offsetWidth + 8+"px";
	}
	if(Extend.style.left < 0) {
		Extend.style.left = document.body.scrollLeft + Parent.offsetLeft + Parent.offsetWidth +"px";
	}
	Extend.style.top = Parent.offsetTop + Item.offsetTop+"px";
	if(Extend.style.top + Extend.offsetHeight > document.body.scrollTop + document.body.clientHeight){
		Extend.style.top = document.body.scrollTop + document.body.clientHeight - Extend.offsetHeight;
	}
	if(Extend.style.top < 0){
		Extend.style.top = 0;
	}
}
function P_OnMouseOut(id, parent)
{
}
function HideAll(id, flag) {
	var Area;
	var Temp;
	var i;
	if(!flag)
	{
		//Temp = eval("E_" + id);
		Temp = document.getElementById("E_" + id);
		Temp.style.display = "none";
	}
	Area = eval("A_" + id);
	if(Area.length)
	{
		for(i = 0; i < Area.length; i++)
		{
			HideAll(Area[i], 0);
			//Temp = eval("E_" + Area[i]);
			Temp = document.getElementById("E_" + Area[i]);
			Temp.style.display = "none";
			//Temp = eval("P_" + Area[i]);
			Temp = document.getElementById("P_" + Area[i]);
			//Temp.className = "out";
		}
	}
}
// 保存页面传来的行数据
function rowdata() {
}
document.onmouseup = OnClick;
var isUploadFileMimeType = '0';
function OnMouseUp(docRes, docAcl) {
	var docResId = docRes.docResId;
	var objName = docRes.docResName;
	var parentFrId = docRes.parentFrId;
	var isFolder = docRes.isFolder;
	var isFile = docRes.isFile;
	if(isFile == 'true' && docRes.mimeType != null) {
		isUploadFileMimeType = docRes.mimeType;
	}
	var isLink = docRes.isLink;
	var isFolderLink = docRes.isFolderLink;
	var isLocked = docRes.isLocked;
	var lockedUserId = docRes.lockedUserId;
	var isPig = docRes.isPig;
	var isLearningDoc = docRes.isLearningDoc;
	var appEnumKey = docRes.appEnumKey;
	var isSysInit = docRes.isSysInit;
	var versionEnabled = docRes.versionEnabled;
	
	var all = docAcl.all;
	var edit = docAcl.edit;
	var write = docAcl.write;
	var readonly = docAcl.readonly;
	var browse = docAcl.browse;
	var list = docAcl.list;

	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocAclManager", "getBorrowPotent", false);
		requestCaller.addParameter(1, "long", docResId);
	var lentpotent2 = requestCaller.serviceRequest();
	rowdata.all = all;
	rowdata.docLibType = docLibType;
	rowdata.docResId = docResId;
	rowdata.objName = objName;
	rowdata.frType = docRes.frType;
	var isPersonal = "false";
	if (docLibType == DocLib_Type_Private) {
		isPersonal = "true";
	}	
	var mainForm = document.getElementById('mainForm');

	mainForm.oname.value = objName;
	mainForm.is_folder.value = isFolder;
	mainForm.isPersonalLib.value = isPersonal;
	mainForm.selectedRowId.value = docResId;
	
	var isAdministrator = window.isAdministrator;
	var depAdminSize = window.depAdminSize;
	var noShare = window.noShare;
	var isShareAndBorrowRoot = window.isShareAndBorrowRoot;
	var canNewColl = window.canNewColl;
	var canNewMail = window.canNewMail;
	var isEdocLib = window.isEdocLib;

	/** add by handy,2007-5-29 **/
	document.getElementById("I_separator1").style.display = "";
	if(document.getElementById("I_separator2"))
		document.getElementById("I_separator2").style.display = "";
	if (isFolder == "true") {
		docDisplay("P_sendto");
		docDisplay("I_favorite");
		docDisplay("I_publish");
		docDisplay("I_group");
		docDisplay("I_deptDoc");
		docDisplay("I_log");
		docDisplay("I_alert");
		docDisplay("I_property");
		docDisplay("I_move");
		docDisplay("I_del");
		docDisplay("I_rename");
		docDisplay("I_share");
		docDisplay("I_share_pub");
		//成发集团项目 begin
		docNoneDisplay("I_secretLevel");
		//end
		docNoneDisplay("I_lend");
		docNoneDisplay("P_forward");
		docNoneDisplay("I_info");
		docNoneDisplay("I_mail");
		docNoneDisplay("I_replace");
		docNoneDisplay("I_lock");
		docNoneDisplay("I_unlock");

		docNoneDisplay("I_edit");
		docNoneDisplay("I_download");
		docNoneDisplay("I_learning");
		docNoneDisplay("I_deptLearn");
		docNoneDisplay("I_accountLearn");
		docNoneDisplay("I_groupLearn");
		docNoneDisplay("I_learnHistory");
		docNoneDisplay("I_link");
		docNoneDisplay("I_docHistory");

		if (isPersonal == "true") {
			docNoneDisplay("I_log");
			docNoneDisplay("I_alert");
			docNoneDisplay("I_publish");
			docNoneDisplay("I_group");
			docNoneDisplay("I_deptDoc");
			docNoneDisplay("I_share_pub");
			if (all == "false") {
				docDisable("I_share");
			}
		}
		else {
			docNoneDisplay("I_share");
			if (all == "false") {
				docDisable("I_log");
				docDisable("I_share_pub");
			}
			if(isAdministrator == 'false'){
				docDisable("I_publish");				
			}	
			if(depAdminSize == '0'){
				docDisable("I_deptDoc");
			}
			if(isGroupAdmin == 'false'){
				docDisable("I_group");	
			}
			if(isGroupLib == 'false'){
				docNoneDisplay("I_group");
			}
		}

		if (all == "false" && edit == "false" && readonly == "false" && add=="false") {
			if(browse == 'false') {
				if(list == 'false'){
					docDisable("I_property");
				}
				docDisable("P_sendto");
				docDisable("I_favorite");
			}
			// 浏览权限，文档夹允许发送到常用文档
			else {
				docDisplay("P_sendto");
				docDisplay("I_favorite");
			}
			docDisable("I_publish");
			docDisable("I_group");
			docDisable("I_deptDoc");
			docDisable("I_link");
			docDisable("I_alert");
		}

		if (all == "false") {	
			docDisable("I_move");
			docDisable("I_del");			
		}	
		if (all == "false" && edit == "false") {
			docDisable("I_rename");
		}
		
		if(isSysInit == 'true'){
			docNoneDisplay("I_move");
			docNoneDisplay("I_del");
			docNoneDisplay("I_rename");
			document.getElementById("I_separator1").style.display = "none";
		}
	}
	else  {
		docDisplay("I_rename");
		docDisplay("P_sendto");
		docDisplay("I_favorite");
		docDisplay("I_publish");
		docDisplay("I_deptDoc");
		docDisplay("I_learning");
		docDisplay("I_deptLearn");
		docDisplay("I_accountLearn");
		docDisplay("I_group");
		docDisplay("I_groupLearn");
		docDisplay("P_forward");
		docDisplay("I_info");
		docDisplay("I_mail");
		docDisplay("I_log");
		//成发集团项目 begin
		docDisplay("I_secretLevel");
		//end
		//成发集团项目 begin
		if(docAcl.isCreater == "false"){
			docNoneDisplay("I_secretLevel");
		}
		//end
		
		docDisplay("I_learnHistory");
		docDisplay("I_docHistory");
		docDisplay("I_alert");
		docDisplay("I_move");
		docDisplay("I_del");
		
		docDisplay("I_link");
		docDisplay("I_replace");
		docDisplay("I_lend");
		docDisplay("I_lock");
		docDisplay("I_unlock");
		docDisplay("I_edit");
		docDisplay("I_download");
		docDisplay("I_property");
		docNoneDisplay("I_share");
		docNoneDisplay("I_share_pub");

		if (isFile == "false") {
			docNoneDisplay("I_replace");
		}
		
		if(isLearningDoc == 'false'){
			docNoneDisplay("I_learnHistory");
		}
		
		if(versionEnabled == 'false' || isPig == 'true' || isLink == 'true') {
			docNoneDisplay("I_docHistory");
		}
		
		if(isFolderLink == 'true'){
			docNoneDisplay("I_link");
			docNoneDisplay("P_forward");
			docNoneDisplay("I_info");
			docNoneDisplay("I_mail");
			docNoneDisplay("I_lock");
			docNoneDisplay("I_unlock");
			docNoneDisplay("I_edit");
			docNoneDisplay("I_download");
			docNoneDisplay("I_alert");
			
			docNoneDisplay("I_learning");
			docNoneDisplay("I_deptLearn");
			docNoneDisplay("I_accountLearn");
			docNoneDisplay("I_groupLearn");
			
			docNoneDisplay("I_lend");
			document.getElementById("I_separator2").style.display = "none";
		}

		if (isLink == "true") {
			docNoneDisplay("I_link");
			docNoneDisplay("P_forward")
			docNoneDisplay("I_info");
			docNoneDisplay("I_mail");
			docNoneDisplay("I_lock");
			docNoneDisplay("I_unlock");
			docNoneDisplay("I_edit");
			docNoneDisplay("I_download");
			docNoneDisplay("I_alert");
			
			docNoneDisplay("I_deptLearn");
			docNoneDisplay("I_accountLearn");
			docNoneDisplay("I_learning");
			docNoneDisplay("I_groupLearn");
			
			docNoneDisplay("I_lend");
			docNoneDisplay("I_rename");

			document.getElementById("I_separator2").style.display = "none";
		}

		// 2007.09.04
		// 协同可以转发：协同、邮件
		// 邮件可以转发：协同、邮件
		// 其他暂时不处理
		if (isPig == "true") {
			//成发集团项目 协同屏蔽密级设置 begin
			docNoneDisplay("I_secretLevel");
			//end
			var pigData = new appEnumData();
			if(appEnumKey == pigData.edoc) {
				if(isShareAndBorrowRoot=="true") {
				     docDisable("P_sendto");
				     docDisable("I_favorite");
				     docDisable("I_link");
				     docDisable("I_learning");
				
				     docNoneDisplay("P_forward");
				     docNoneDisplay("I_info");
				     docNoneDisplay("I_mail");
				     
					 document.getElementById("I_separator0").style.display = "none";
			  	}
			} else if(appEnumKey != pigData.collaboration && appEnumKey != pigData.mail) {
				docNoneDisplay("P_forward");
				docNoneDisplay("I_info");
				docNoneDisplay("I_mail");
			}
			
			docNoneDisplay("I_lock");
			docNoneDisplay("I_unlock");
			docNoneDisplay("I_edit");
			docNoneDisplay("I_download");
			docNoneDisplay("I_alert");
		}
		//TODO 此处权限控制待确定，add == 'false'应该不是逻辑条件之一
		if (all == "false" && edit == "false" && readonly == "false" && add=="false") {
			docDisable("P_forward");
			docDisable("I_info");
			docDisable("I_mail");
		}
		
		// 借阅需要只读才能查看历史版本，共享则只需浏览权限
		if((isShareAndBorrowRoot == 'false' && all == "false" && edit == "false" && readonly == "false" && browse == "false" && add == "false") 
			|| (isShareAndBorrowRoot == 'true' && readonly == "false")) {
			docDisable("I_docHistory");
		}

		if (all == "false" && edit == "false" && readonly == "false" && add == "false") {	
			if(browse == 'false') {
				if(list == 'false'){
					docDisable("I_property");
				}
				docDisable("P_sendto");
				docDisable("I_favorite");
				docDisable("I_learning");
				docDisable("I_link");
				docDisable("I_publish");
				docDisable("I_accountLearn");
				docDisable("I_groupLearn");
				docDisable("I_group");
			}
			else {
				docDisplay("P_sendto");
				docDisplay("I_favorite");
				docDisplay("I_learning");
				if(isPersonal != "true" && isAdministrator == 'true') {
					docDisplay("I_publish");
					docDisplay("I_accountLearn");
				}
				
				if(isGroupAdmin == 'true' && isGroupLib == 'true') {
					docDisplay("I_groupLearn");
					docDisplay("I_group");
				}
			}
			docDisable("I_deptDoc");
			docDisable("I_deptLearn");
			docDisable("I_alert");
			docDisable("I_download");
			docDisable("I_link");
		}

		if (isPersonal == "true") {
			docNoneDisplay("I_log");
			docNoneDisplay("I_alert");
			docNoneDisplay("I_lock");
			docNoneDisplay("I_unlock");
			docNoneDisplay("I_publish");
			docNoneDisplay("I_deptDoc");
			docNoneDisplay("I_deptLearn");
			docNoneDisplay("I_accountLearn");
			docNoneDisplay("I_group");
			docNoneDisplay("I_groupLearn");
		}
		else {
			if (isLocked == "true") {
				docNoneDisplay("I_lock");
			}
			else {
				docNoneDisplay("I_unlock");
			}			

			if (all == "false") {
				docDisable("I_log");
			}				
			
			if (all == "false" && edit == "false") {
				docDisable("I_lock");
				docDisable("I_unlock");
			}	
			
			if (isLocked == "true" && lockedUserId != currentUserId) {
				docDisable("I_edit");
				docDisable("I_unlock");
				docDisable("I_replace");
				docDisable("I_rename");
			}
			if(isAdministrator == 'false') {
				docDisable("I_publish");
				docDisable("I_accountLearn");
			}	
			if(depAdminSize == '0'){
				docDisable("I_deptDoc");
				docDisable("I_deptLearn");
			}
			if(isGroupAdmin == 'false'){
				docDisable("I_group");	
				docDisable("I_groupLearn");
			}
			if(isGroupLib == 'false'){
				docNoneDisplay("I_group");
				docNoneDisplay("I_groupLearn");
			}
		}

		if (all == "false") {
			docDisable("I_move");
			docDisable("I_del");	
			if(frType != 111) {  
				docDisable("I_lend");
			}
		}				
		
		if (all == "false" && edit == "false") {
			docDisable("I_replace");
			docDisable("I_rename");
			docDisable("I_edit");
		}
		
		if('false' == canNewColl) {
			if('false' == canNewMail){
				docDisable("P_forward");
				docDisable("I_info");
				docDisable("I_mail");
			} else {
				docDisable("I_info");
			}
		} else if('false' == canNewMail) {
			docDisable("I_mail");
		}	
	}

	//only add acl
	if(all== "false" && edit== "false" && readonly== "false" && browse== "false" && list== "false" && add== "true"){
		if(docAcl.isCreater== "false"){
			docNoneDisplay("P_sendto");
			docNoneDisplay("P_forward");
			docDisable("I_download")
			docDisable("I_move")
			docDisable("I_del")
			docDisable("I_rename")
			docDisable("I_property")
			docDisable("I_share")
			docDisable("I_share_pub")
			docDisable("I_lock");
			docDisable("I_unlock");
			docDisable("I_log");
			docDisable("I_alert");
			docDisable("I_docHistory");
		}
	}
	// 2008.06.17 个人共享，借阅屏蔽掉发送
	if(isShareAndBorrowRoot == "true") {

		//成发集团项目 begin
		docNoneDisplay("I_secretLevel");
		//end
		docNoneDisplay("P_sendto");
		docNoneDisplay("I_favorite");
		docNoneDisplay("I_publish");
		docNoneDisplay("I_deptDoc");
		docNoneDisplay("I_learning");
		docNoneDisplay("I_deptLearn");
		docNoneDisplay("I_accountLearn");
		docNoneDisplay("I_group");
		docNoneDisplay("I_groupLearn");
		
      	if(lentpotent2 != null && lentpotent2.substring(0, 1) != "1") {
	  		docDisable("I_download");
      	}
		if (isFolder == "true") {
			document.getElementById("I_separator0").style.display = "none";
		}
	}
	
	if(isEdocLib == 'true'){
		docNoneDisplay("P_sendto");
		docNoneDisplay("P_forward");
		//成发集团项目  公文屏蔽密级设置begin
		docNoneDisplay("I_secretLevel");
		//end
		document.getElementById("I_separator0").style.display = "none";
	}
	if(appEnumKey == appData.edoc){
		docNoneDisplay("P_forward");
	}
//	if(frType == '42'&& isAdministrator == 'true'){
//		docDisplay("I_share_pub");
//	}	

	var PopMenu;
	//PopMenu = eval("E_rbpm");
	PopMenu = document.getElementById("E_rbpm");
	var docMenuTable = document.getElementById("docMenuTable");
	docMenuTable.className = docResId;

	HideAll("rbpm", 0);
	PopMenu.style.display = "block";
	var scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);    
	var scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
	var popLeft = document.getElementById("_" + docResId).getBoundingClientRect().left + 8 + scrollLeft;
	var popTop = document.getElementById("_" + docResId).getBoundingClientRect().top + 8 + scrollTop;
	if(popLeft + PopMenu.offsetWidth > document.body.clientWidth){
		popLeft -= PopMenu.offsetWidth;
	}
	if(popTop + PopMenu.offsetHeight > document.body.clientHeight){
		popTop -= PopMenu.offsetHeight;
	}
	PopMenu.style.left = popLeft < 0 ? (0+"px") : (popLeft+"px");
	PopMenu.style.top = popTop < 0 ? (0+"px") : (popTop+"px");
}
function OnClick() {
	HideAll("rbpm",0);
}

function docProperties(location, rowid, isFolder, isPersonalLib, propEditValue, all, frType){
	var url = location + "&docResId=" + rowid + "&isFolder=" + isFolder + "&isPersonalLib=" + isPersonalLib + 
		"&propEditValue=" + propEditValue + "&allAcl=" + all + "&frType=" + frType;
	v3xOpenWindow(url);
}