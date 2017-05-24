try {
    getA8Top().endProc();
}
catch(e) {
}
/**
 * Greate 2007-3-21 by xuegw
 * xml中特殊字符对应的实体
 * <	&lt;
 * &	&amp;
 * >	&gt;
 * "	&quot;
 * '	&apos;
 */
/*------------------------------------------ JS常量定义 Start ------------------------------------------*/
/**
 * 系统类型定义（用于归档文件的类型判断）
 */
function appEnumData() {
	this.global = '0'; // 全局
	this.collaboration = '1'; // 协同应用
	this.form = '2'; // 表单
	this.doc = '3'; // 知识管理
	this.edoc = '4'; // 公文
	this.plan = '5'; // 计划
	this.meeting = '6'; // 会议
	this.bulletin = '7'; // 公告
	this.news = '8'; // 新闻
	this.bbs = '9'; // 讨论
	this.inquiry = '10'; // 调查
	this.mail = '12'; // 邮件
	this.organization = '13'; //组织模型
	this.info = '32'; //信息报送
	this.infoStat = '33'; //信息报送统计
}
var appData = new appEnumData();

/**
 * 文档库类型定义
 */
var DocLib_Type_Custom = 0; //自定义文档库类型
var DocLib_Type_Private = 1; //个人文档库
var DocLib_Type_Public = 2; // 单位文档库
var DocLib_Type_Edoc = 3; // 公文档案
var DocLib_Type_Project = 4; //项目文档库

/*------------------------------------------ JS常量定义 End -------------------------------------------*/
var winProperties;
function v3xOpenWindow(surl) {
	if(v3x.getBrowserFlag('openWindow') == false){
	winProperties = v3x.openDialog({
		id : "properties",
		title : "",
		url : surl,
		width : 500,
		height : 500,
		//type : 'panel',
		buttons : [{
			id:'btn1',
    	    text: v3x.getMessage("collaborationLang.submit"),
    	    handler: function(){
	    	   	winProperties.getReturnValue();
	        }
		}, {
    		id:'btn2',
    	    text: v3x.getMessage("collaborationLang.cancel"),
    	    handler: function(){
    	    	winProperties.close();
    	    }
		}]
	
	});
	} else {
	v3x.openWindow({
		url : surl,
		width : "500",
		height : "500",
		resizable : "false"
	});
	}
}

/**
 * docMenu页面javascript函数 
 */
function setFlag(flag) {
	searchForm.flag.value = flag;
}
function clearFlag() {
	searchForm.flag.value = "";	
}
function docSimpleSearchEnter(isQuote) {
	var evt = v3x.getEvent();
    if(evt.keyCode == 13){
    	if(isQuote == true || isQuote == 'true') {
    		seachDocRel();
    	} else {
	    	docSimpleSearch();
    	}
    }
}
/**
 * 文档简单属性查询
 * @see com.seeyon.v3x.doc.webmodel.SimpleDocQueryModel中关于查询输入数据延伸名称的常量定义
 * @see com.seeyon.v3x.doc.util.Constants中关于元数据类型部分的常量定义
 */
function docSimpleSearch() {	
	if(!checkForm(searchForm)) {
		return;
	}
	
	var fvalue = searchForm.flag.value;
	var method = "";
	if(fvalue == 'pingHoleAlready') {
		var pingHoleSelect = searchForm.pingHoleSelect.value;
		method = "rightNew&pingHoleSelect=" + pingHoleSelect;
	}
	else if(fvalue && fvalue != '') {
		method = "simpleQuery&propertyNameAndType=" + fvalue;
		var name_type = fvalue.split('|');
		try {
			var propName = name_type[0];
			var propType = name_type[1];
			var isDefault = name_type[2];
			if(propType == '4' || propType == '5') {
				if(document.getElementById(propName + "beginTime").value == '' && document.getElementById(propName + "endTime").value == '') {
					alert(v3x.getMessage("DocLang.doc_search_select_condition_alert"));
					return;
				}
				method += "&" + propName + "beginTime=" + document.getElementById(propName + "beginTime").value;
				method += "&" + propName + "endTime=" + document.getElementById(propName + "endTime").value;
			}
			else {
				// frType页面中已有全局变量定义，避免重复
				var appendFlag= "";
				if(propType == '10') {
					if(document.getElementById("frTypeValue").value == -1) {
						alert(v3x.getMessage("DocLang.doc_type_alter_not_select"));
						return;
					}
					appendFlag = "Value";
				}
				var value = document.getElementById(propName + appendFlag).value;
				if(value && !/^[^\|\\"'<>]*$/.test(value)){
					alert(v3x.getMessage("V3XLang.formValidate_specialCharacter", value))
					return;
				}
				method += "&" + propName + appendFlag + "=" + encodeURIComponent(value);
				if(propType == '8' || propType == '9') {
					if(document.getElementById(propName + "Name").value == '') {
						alert(v3x.getMessage("DocLang.doc_search_select_condition_alert"));
						return;
					}
					method += "&" + propName + "Name=" + encodeURIComponent(document.getElementById(propName + "Name").value);
				}
			}
			method += ("&" + propName + "IsDefault=" + isDefault);
		} catch(e) {
			// -> Ignore
		}
	} 
	else {
		alert(v3x.getMessage("DocLang.doc_search_select_condition_alert"));
		return;
	}
	
	docResId = window.docResId;
	docLibId = window.docLibId;
	docLibType = window.docLibType;
	isShareAndBorrowRoot = window.isShareAndBorrowRoot;
	isLibOwner = window.isLibOwner;

	var docUrl = jsURL + "?method=" + method + "&queryFlag=true&resId=" + docResId + "&docLibId=" + docLibId 
		+ "&docLibType=" + docLibType + "&isShareAndBorrowRoot=" + isShareAndBorrowRoot
		+ "&all=" + all + "&edit=" + edit + "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list
		+ "&depAdminSize=" + depAdminSize + "&isAdministrator=" + isAdministrator
		+ "&isGroupAdmin=" + isGroupAdmin + "&libName=" + encodeURI(libName) + "&isLibOwner=" + isLibOwner + "&frType=" + window.frType + "&flag=" + fvalue;
	
	try{
		//文档查询后，frameset，恢复到默认宽度且不可拖动，点击‘返回’按钮，恢复默认状态
		if(parent.layout){
			if(parent.layout.cols != "0,*") {
				parent.layout.cols="140,*";
				//parent.document.getElementById("treeFrame").noResize=true;		
			}	
		}
	}catch(e){}
	
	location.href = docUrl;
}
/**
 * 高级查询模式下改为get，将参数直接拼接在url地址中，避免post模式下，页面属性时导致form重新提交
 */
function getSearchConditionUrl() {
	var nameAndTypes = document.getElementsByName('propertyNameAndType');
	var nameAndTypeUrl = new StringBuffer();
	var valueUrl = new StringBuffer();
	if(nameAndTypes && nameAndTypes.length > 0) {
		for(var i = 0; i < nameAndTypes.length; i++ ) {
			var nameAndType = nameAndTypes[i].value;
			var arr = nameAndType.split('|');
			var propName = arr[0];
			var propType = arr[1];
			var propIsDefault = document.getElementById(propName + 'IsDefault').value;
			
			
			//日期(时间)
			if(propType == '4' || propType == '5') {
				if(document.getElementById(propName + 'beginTime2') && document.getElementById(propName + 'endTime2') && 
				   (document.getElementById(propName + 'beginTime2').value != '' || 
				    document.getElementById(propName + 'endTime2').value != '')) {
					
					nameAndTypeUrl.append(nameAndType + '|' + propIsDefault + ',');
					valueUrl.append(propName + 'beginTime=' + document.getElementById(propName + 'beginTime2').value + '&');
					valueUrl.append(propName + 'endTime=' + document.getElementById(propName + 'endTime2').value + '&');
					valueUrl.append(propName + 'IsDefault=' + propIsDefault + '&');
				}
			}
			else {
				var doms = document.getElementsByName(propName);
				if(doms && doms.length > 0) {
					var spValue = doms[doms.length - 1].value;
					if(spValue && spValue.trim() != '') {
						nameAndTypeUrl.append(nameAndType + '|' + propIsDefault + ',');
						//历史原因，在查询条件为文档类型的情况下，变量名加上'Value'，避免与url中的frType变量重名冲突
						valueUrl.append(propName + (propType == '10' ? 'Value' : '') + '=' + encodeURIComponent(spValue) + '&');
						valueUrl.append(propName + 'IsDefault=' + propIsDefault + '&');
					}
				}
			}
		}
		
		if(nameAndTypeUrl.toString().trim() != '') {
			valueUrl.append('propertyNameAndTypes=' + nameAndTypeUrl.toString());
		}
	}
	return valueUrl.toString();
}
function docAdvancedSearchEnter() {
	var evt = v3x.getEvent();
    if(evt.keyCode == 13) {
    	docAdvancedSearch();
    }
}
/**
 * 文档高级查询
 */
function docAdvancedSearch() {
	if(!checkForm(advancedSearchForm)) {
		return;
	}
	
	docResId = window.docResId;
	docLibId = window.docLibId;
	docLibType = window.docLibType;
	isShareAndBorrowRoot = window.isShareAndBorrowRoot;
	isLibOwner = window.isLibOwner;

	var docUrl = jsURL + "?method=advancedQuery&queryFlag=true&resId=" + docResId + "&docLibId=" + docLibId 
		+ "&docLibType=" + docLibType + "&isShareAndBorrowRoot=" + isShareAndBorrowRoot
		+ "&all=" + all + "&edit=" + edit + "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list
		+ "&depAdminSize=" + depAdminSize + "&isAdministrator=" + isAdministrator
		+ "&isGroupAdmin=" + isGroupAdmin + "&libName=" + encodeURI(libName) + "&isLibOwner=" + isLibOwner + "&frType=" + window.frType;
	
	try{
		//文档查询后，frameset，恢复到默认宽度且不可拖动，点击‘返回’按钮，恢复默认状态
		if(parent.layout){
			if(parent.layout.cols != "0,*") {
				parent.layout.cols="140,*";
				//parent.document.getElementById("treeFrame").noResize=true;		
			}	
		}
	}catch(e){}
	
	var conditionUrl = getSearchConditionUrl();
	if(conditionUrl == '') {
		alert(v3x.getMessage('DocLang.no_advanced_search_condition'));
		return;
	}
	//Url路径超长问题处理(get模式下url最长允许2048)
	if(conditionUrl.length > 2024) {
		alert(v3x.getMessage('DocLang.advanced_search_url_too_long', conditionUrl.length + 24));
		return;
	}
	document.getElementById("advancedSearchButton").disabled = true;
	dataIFrame.location.href = docUrl + '&' + conditionUrl;
}
function showOrHideAdvancedSearch() {
	var as = document.getElementById("advancedSearch");
	var mainTable = document.getElementById('ScrollDIV');
	var menuBar = document.getElementById('rightMenuBar');
	if(as.style.display == "none") {
		as.style.display = "block";
		mainTable.style.display = "none";
		menuBar.style.display = "none";
	} else {
		as.style.display = "none";
		mainTable.style.display = "block";
		menuBar.style.display = "block";
	}
	
	var asButton = document.getElementById("advancedSearchImg");
	if(asButton.className == "advanceSearchUp")
		asButton.className = "advanceSearchDown";
	else
		asButton.className = "advanceSearchUp";
}
/**
 * 文档查询中，选择组织模型相关信息进行查询（比如：人员、部门等情况）
 */
function setDocSearchPeopleFields(orgName, elements) {
	// 简单属性处也存在同名dom，此处应当将高级查询处的属性设值
	var aform = document.getElementById("advancedSearchForm");
	var doms = aform.elements[orgName];
	if(doms) {
		doms.value = getIdsString(elements, false);
	}
	document.getElementById(orgName + "ASName").value = getNamesString(elements);
	document.getElementById(orgName + "ASName").title = getNamesString(elements);
}
function setSimpleDocSearchPeopleFields(orgName, elements) {
	document.getElementById(orgName).value = getIdsString(elements, false);	
	document.getElementById(orgName + "Name").value = getNamesString(elements);
	document.getElementById(orgName + "Name").title = getNamesString(elements);
}
/************************
 * create folder begin
 */
var winCreateFolder;
function createFolder(parentVersionEnabled, parentCommentEnabled) {
	var parentId = window.docResId;
	var docLibId = window.docLibId;
	var docLibType = window.docLibType;
	winCreateFolder = v3x.openDialog({
		id : "createFolder",
		title : "new",
		url : jsURL + "?method=createF&parentId=" + parentId + "&docLibId" + docLibId + "&docLibType=" + docLibType + 
			  '&parentVersionEnabled=' + parentVersionEnabled + '&parentCommentEnabled=' + parentCommentEnabled,
		width : 380,
		height : 200,
		type : 'panel',
		buttons : [{
			id:'btn1',
    	    text: v3x.getMessage("collaborationLang.submit"),
    	    handler: function(){
	    	    var returnValues = winCreateFolder.getReturnValue();
	    	    
	        }
		}, {
    		id:'btn2',
    	    text: v3x.getMessage("collaborationLang.cancel"),
    	    handler: function(){
    	    	winCreateFolder.close();
    	    }
		}]
	
	});
}
var winEdocCreateFolder;
function createEdocFolder(parentCommentEnabled) {
	var parentId = window.docResId;
	var docLibId = window.docLibId;
	winEdocCreateFolder = v3x.openDialog({
		id : "edocCreateFolder",
		title : "new",
		url : jsURL + "?method=createEdocFolder&parentId=" + parentId + "&docLibId" + docLibId + '&parentCommentEnabled=' + parentCommentEnabled,
		width : 450,
		height : 250,
		type : 'panel',
		buttons : [{
			id:'btn1',
    	    text: v3x.getMessage("collaborationLang.submit"),
    	    handler: function(){
	    	    var returnValues = winEdocCreateFolder.getReturnValue();
	        }
		}, {
    		id:'btn2',
    	    text: v3x.getMessage("collaborationLang.cancel"),
    	    handler: function(){
    	    	winEdocCreateFolder.close();
    	    }
		}]
	
	});
}
/**
 * 创建文件夹页面 greate by xuegw
 */
function newCreate(detailurl) {
	if(!checkForm(mainForm)){
		return false;
	}
	
 	var obj = self.dialogArguments; 
	var parentId = obj.window.docResId;
	var docLibId = obj.window.docLibId;
	var docLibType = obj.window.docLibType;
	mainForm.action = detailurl + "?method=createFolder&parentId=" + parentId + "&docLibId=" + docLibId + "&docLibType=" + docLibType;
}

function newCreateEdoc(detailurl) {
	if(document.all.title.value!="") {  
 		var obj = self.dialogArguments; 
		var parentId = obj.window.docResId;
		var docLibId = obj.window.docLibId;
		mainForm.action = detailurl + "?method=doCreateEdocFolder&parentId=" + parentId + "&docLibId=" + docLibId;
	}
	else{
		alert(v3x.getMessage("DocLang.doc_jsp_createf_null_failure_alert"));
		document.all.title.focus();
		return false;
	}
}
/**
 * 使用弹出式菜单进行操作时，进行锁（包括应用锁和并发锁）状态校验
 */
function checkLock(docId, isFolder) {
	var msg_status = getLockMsgAndStatus(docId);
	if(msg_status && msg_status[0] != LOCK_MSG_NONE && msg_status[1] != LockStatus_None) {
		// 如果是应用锁定或文档已被删除，需刷新列表显示
		if(msg_status[1] == LockStatus_DocInvalid || msg_status[1] == LockStatus_AppLock) {
			if(msg_status[1] == LockStatus_DocInvalid) {
				if(isFolder == 'true') {
					alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_folder'));
				} else {
					alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));
				}
			}
			else {
				alert(msg_status[0]);
			}
			window.location.reload(true);
		}
		else {
			// 隐藏弹出菜单之后弹出提示信息
			try {
				setTimeout("HideAll('rbpm', 0)", 100);
			} catch(e) {
				// -> Ignore
			}
			alert(msg_status[0]);
		}
		return false;
	}
	return true;
}
/**
 * 重命名文档或文档夹。
 */
var winRename; 
function rename(surl, isFolder, docId) {
	if(checkLock(docId, isFolder) == false) {
		return;
	}
	
	if(v3x.getBrowserFlag('openWindow') == false){
	winRename = v3x.openDialog({
		id : "rename",
		title : "rename",
		url : surl,
		width : 380,
		height : 200,
		type : 'panel',
		buttons : [{
			id:'btn1',
    	    text: v3x.getMessage("collaborationLang.submit"),
    	    handler: function(){
	    	    var returnValues = winRename.getReturnValue();
	
	        }
		}, {
    		id:'btn2',
    	    text: v3x.getMessage("collaborationLang.cancel"),
    	    handler: function(){
    	    	winRename.close();
    	    }
		}]
	
	});
	} else {
	var returnvalue = v3x.openWindow({
		url : surl,
		width : "380",
		height : "200",
		resizable : "yes"
	});
	if(returnvalue) {
		var docResId = returnvalue[0];
		var newName = returnvalue[1];
		window.location.reload(true);
		if (isFolder == "true") {		
			var obj = parent.treeFrame;
			if (obj.webFXTreeHandler.getIdByBusinessId(docResId) != undefined) {			
				obj.webFXTreeHandler.all[obj.webFXTreeHandler.getIdByBusinessId(docResId)].setText(newName);
			}
		}
	}
	}
}
function readyToRename(parentId, frType, oldname){
	var name = document.getElementById('newName').value;
	if(name.trim() == oldname){
		window.close();
		return false;
	}
	
	if(!checkForm(mainForm))
		return false;

	if(dupliName(parentId, name, frType, false) == 'true'){
        alert(v3x.getMessage('DocLang.doc_upload_dupli_name_failure_alert',name));
		document.mainForm.newName.focus();
		return false;
	}
	return true;
}
/**
 * 删除
 */
function delF(checkedid, flag, isFolder) {
	var parentId = window.docResId;
	var docLibType = window.docLibType;
	var mainForm = document.getElementById("mainForm");
	
	if(checkedid != "topOperate" && checkLock(checkedid, isFolder) == false) {
		return;
	}

	if(flag == "self") {
		delOk(checkedid,mainForm, jsURL, "delete&parentId=" + parentId + "&docLibType=" + docLibType + "&id=" + checkedid, '', isFolder);
	}
	else {		
		delOk(checkedid, document.mainForm, jsURL, "delete&parentId=" + parentId + "&docLibType=" + docLibType, document.mainForm.id);
	}
}

/**
 * 批量下载
 */
function doloadFile(userId){
	if(getA8Top().xmlDoc == null){
		alert(v3x.getMessage("V3XLang.batch_download_control_error"));
		return;
	}
	getA8Top().contentFrame.topFrame.showDowloadPicture("doc");
	var ipUrl = window.location.href;
	var startUrl = ipUrl.substring(0, ipUrl.indexOf("/seeyon")) + "/seeyon";
	var ids = document.getElementsByName("id");
	var size = 0;
	var pigCount = 0;
	for (var i = 0; i < ids.length; i ++) {
		if (ids[i].checked) {
			size += 1;
			var id = ids[i].value;
			var downloadFrName = document.getElementById(id + "_Name").value;
			var downloadFrSize = document.getElementById(id + "_Size").value;
			var downloadIsFolder = document.getElementById("isFolder" + id).value;
			var downloadIsPig = document.getElementById(id + "_IsPig").value;
			if(downloadIsPig == true || downloadIsPig == "true") {
				pigCount ++;
				continue;
			}
			var url;
			var result;
			if(downloadIsFolder != "true"){// 文件
				var isBorrow = isShareAndBorrowRoot == "true" && (frType == "102" || frType == "103");
				var downloadIsUploadFile = document.getElementById(id + "_IsUploadFile").value;
				if (downloadIsUploadFile != "true") {// 复合文档
					downloadFrName += ".zip";
				}
				url = startUrl + "/doc.do?method=checkFile&docId=" + id + "&isBorrow=" + isBorrow;
				result = getA8Top().xmlDoc.AddDownloadFile(userId, downloadFrSize, downloadFrName, url);
			}else{// 文件夹
				url = startUrl + "/doc.do?method=getFilesFromFolder&folderId=" + id;
				result = getA8Top().xmlDoc.AddDownloadFolder(userId, downloadFrName, url);
			}
			
			if("FD_ERROR_USER_NO_FIND" == result){
				alert(_("V3XLang.batch_download_no_user"));
				return;
			}
		}
	}
	
	if(pigCount > 0){
		alert(_("DocLang.batch_download_not_pig"));
	}
	
	if(size == 0){
		alert(_("V3XLang.selectPeople_alert_minSize", 1, 0));
		return;
	}
}

function delOk(checkedid, mainForm, actionname, methodname, checkid, isFolder) {
	 var isalert =	document.getElementById("isalert").value ;
	 
	mainForm.action = actionname + "?method=" + methodname;
	if(checkedid == "topOperate") {
		var len = checkid.length;
		var size = 0;	
		var chkboxval;	
		if(!checkid[0]){ // 页面中没有或只有一项内容
			if(checkid.checked == undefined){
				alert(v3x.getMessage("DocLang.doc_delete_no_content_alert"))
				return;
			}else if(checkid.checked) {
				size = 1;
				chkboxval = checkid.value;
			}else {
				size = 0;
			}
		} else {
			for (i = 0; i <len; i++) {
				if (checkid[i].checked) {
					size += 1;
					chkboxval = checkid[i].value;
				}
			}
		}
		
		if (size == 0) {
			alert(v3x.getMessage("DocLang.doc_delete_select_alert"));
			return false;
		}
		else {
			var ss = " " + size + v3x.getMessage("DocLang.doc_delete_items");
			if(size == 1){
				var isf = eval("document.mainForm.isFolder" + chkboxval).value;
				var ret;
				if(isf == 'true')
					ret = confirm(v3x.getMessage("DocLang.doc_delete_confirm_folder"));
				else
					ret = confirm(v3x.getMessage("DocLang.doc_delete_confirm_doc"));
				if(ret){
					mainForm.target = "empty";
					mainForm.submit();
				}	
			}else{
				if(confirm(v3x.getMessage("DocLang.doc_delete_confirm_head") + ss + v3x.getMessage("DocLang.doc_delete_confirm_tail"))) {	
					mainForm.target = "empty";
					mainForm.submit();
				}
			}
		}
	}
	else {
		var ret = false;
		if(isFolder == 'true') {
			ret = confirm(v3x.getMessage("DocLang.doc_delete_confirm_folder"));
			if(ret){
		       mainForm.target = "empty";
		       mainForm.submit();
			}
		} else if(isalert=="true"){
		    ret = confirm(v3x.getMessage("DocLang.doc_delete_confirm_doc"));
	        if(ret){
		       mainForm.target = "empty";
		       mainForm.submit();
	        }
		} else {
			mainForm.target = "empty";
			mainForm.submit();
		}
	}
}
function validateParent(parentId, path){
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "docResourceNoChange", false);
		requestCaller.addParameter(1, "Long", parentId);
		requestCaller.addParameter(2, "String", path);
				
		var flag = requestCaller.serviceRequest();
		return flag;
	}
	catch (ex1) {
		alert("Exception : " + ex1.message);
	}
}
function showNoHidden() {
	if(parent.layout){
		if(parent.layout.cols == "0,*") {
			parent.layout.cols = "140,*";
			getA8Top().contentFrame.LeftRightFrameSet.cols = "0,*";
			try{
				if(parent.document.getElementById("treeFrame")!=null){
					parent.document.getElementById("treeFrame").noResize=false;	
				}
			}catch(e){}
		}
	}
}
/**
 * 新建文档
 */
function createDoc(bodyType) {
	showNoHidden();
	
	var parentId = window.docResId;
	var parentPath = window.parentPath;
	
	var existFlag = validateParent(parentId, parentPath);
	if(existFlag == 'delete'){
		alert(v3x.getMessage('DocLang.doc_alert_source_deleted_folder'));
		parent.location.reload(true);
		return;
	}else if(existFlag == 'move'){
		alert(v3x.getMessage('DocLang.doc_alert_source_move_folder'));
		parent.location.reload(true);
		return;
	}

	var frType = window.frType;
	var docLibId = window.docLibId;
	var docLibType = window.docLibType;
	var parentCommentEnabled = window.parentCommentEnabled;
	var parentVersionEnabled = window.parentVersionEnabled;
	
	var url = jsURL + "?method=addDoc&resId=" + parentId + "&frType=" + frType + "&docLibId=" + docLibId 
		+ "&docLibType=" + docLibType + "&bodyType="+bodyType + "&all=" + all + "&edit=" + edit 
		+ "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list 
		+ "&parentCommentEnabled=" + parentCommentEnabled + "&parentVersionEnabled=" + parentVersionEnabled + "&parentPath=" + parentPath;
	location.href = url;
}
function createDocWithoutHidden(bodyType) {
	var parentId = window.docResId;
	var parentPath = window.parentPath;
	
	var existFlag = validateParent(parentId, parentPath);
	if(existFlag == 'delete'){
		alert(v3x.getMessage('DocLang.doc_alert_source_deleted_folder'));
		parent.location.reload(true);
		return;
	}else if(existFlag == 'move'){
		alert(v3x.getMessage('DocLang.doc_alert_source_move_folder'));
		parent.location.reload(true);
		return;
	}
	var frType = window.frType;
	var docLibId = window.docLibId;
	var docLibType = window.docLibType;
	var parentCommentEnabled = window.parentCommentEnabled;
	var parentVersionEnabled = window.parentVersionEnabled;
	
	var url = jsURL + "?method=addDoc&resId=" + parentId + "&frType=" + frType + "&docLibId=" + docLibId 
		+ "&docLibType=" + docLibType + "&bodyType="+bodyType + "&all=" + all + "&edit=" + edit 
		+ "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list 
		+ "&parentCommentEnabled=" + parentCommentEnabled + "&parentVersionEnabled=" + parentVersionEnabled + "&parentPath=" + parentPath + "&flag=formBizConfig";
	location.href = url;
}
// 执行上传文件操作
function fileUpload(flag) {
	var docResId = window.docResId;
	var docLibId = window.docLibId;
	var docLibType = window.docLibType;
	var parentCommentEnabled = window.parentCommentEnabled;
	var parentVersionEnabled = window.parentVersionEnabled;
	fileUploadQuantity = 5;
	//清空缓存
	fileUploadAttachments.clear();		
	insertAttachment();
	if(fileUploadAttachments.isEmpty() == false) {
		saveAttachment();
		var the_form = document.getElementById("mainForm");
		the_form.target = "delIframe";
		the_form.action = jsURL + "?method=docUpload&docResourceId=" + docResId + "&docLibId=" + docLibId 
				+ "&docLibType=" + docLibType + "&parentCommentEnabled=" + parentCommentEnabled + "&parentVersionEnabled=" + parentVersionEnabled;
		the_form.submit();			
	}
}
//新建文档保存
var whitespace = " \t\n\r";
function addDocument(docLibId,docLibType,folderId,frType,all,edit,add,readonly,browse,list,parentCommentEnabled,bodyType, parentPath, flag, parentVersionEnabled) {
	disableDocButtons('false');
	var existFlag = validateParent(folderId, parentPath);
	if(existFlag == 'delete'){
		alert(v3x.getMessage('DocLang.doc_alert_source_deleted_folder'));
				isFormSumit = true;
		enableDocButtons('false');
		parent.location.reload(true);
		return;
	}else if(existFlag == 'move'){
		alert(v3x.getMessage('DocLang.doc_alert_source_move_folder'));
				isFormSumit = true;
		enableDocButtons('false');
		parent.location.reload(true);
		return;
	}
	
	if(!checkForm(addDoc)){
		if(V3X.checkFormAdvanceAttribute=="docAdvance"){
			editDocProperties('0');
		}
		enableDocButtons('false');
		return;
	}
	getA8Top().startProc();
	if(!saveOffice()){
		enableDocButtons('false');
		return;	
	}
	
	var ctid = 21;
	if(window.contentSelect == 'true')
		ctid = document.getElementById("contentTypeId").value;
		var exist = dupliName(folderId, document.addDoc.docName.value, ctid, false);
		
		if(exist == 'false'){
			isFormSumit = true;	
			var contentTypeFlag = 'true';
			if(docLibType != '1'){
				
				contentTypeFlag = contentTypeExist(ctid);
			}

			if(contentTypeFlag == 'false'){
				alert(v3x.getMessage("DocLang.doc_alert_doctype_deleted"));
				window.history.back(-1);
			}else{	
				saveAttachment();
				document.addDoc.action = jsURL + "?method=addDocument&docLibId=" + docLibId + "&docLibType=" + docLibType
				+ "&resId=" + folderId + "&frType=" + frType + "&isShareAndBorrowRoot=false" + "&all=" + all + "&edit=" + edit 
				+ "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list
				+ "&parentCommentEnabled=" + parentCommentEnabled + "&flag="+flag + "&parentVersionEnabled=" + parentVersionEnabled;
				document.addDoc.submit();
			}
		}else{
			alert(v3x.getMessage('DocLang.doc_upload_dupli_name_failure_alert',document.addDoc.docName.value));
			enableDocButtons('false');
			document.addDoc.docName.focus();
		}
	if(parent.layout){
		if(parent.layout.cols == "0,*") {
			parent.layout.cols = "140,*";
			parent.document.getElementById("treeFrame").noResize=false;	
		}
		else {
			parent.document.getElementById("treeFrame").noResize=false;	
		}
	}
}
function addProDocument(docLibId,docLibType,folderId,frType,bodyType,commentEnabled,versionEnabled, projectId, projectPhaseId){	
	if(!checkForm(addDoc)){
		if(V3X.checkFormAdvanceAttribute=="docAdvance"){
			editDocProperties('0');
		}
		return;
	}	
	
    isFormSumit = true;
	if(!saveOffice()){
		   enableDocButtons('false');
           return;	
	 } 
    var ctid = 21;
        ctid = document.getElementById("contentTypeId").value;
 	var exist = dupliName(folderId, document.addDoc.docName.value, ctid, false);
	if(exist == 'false') {
		   var contentTypeFlag = 'true';
	       saveAttachment();
		   document.addDoc.action = jsURL + "?method=addProDocument&docLibId=" + docLibId + "&docLibType=" + docLibType
		      + "&resId=" + folderId + "&frType=" + frType + "&isShareAndBorrowRoot=false&parentCommentEnabled="+commentEnabled+"&parentVersionEnabled="+versionEnabled + "&projectId=" + projectId + "&projectPhaseId="+projectPhaseId;
	       disableDocButtons('false');
		   document.addDoc.submit();
	} else {
		alert(v3x.getMessage('DocLang.doc_upload_dupli_name_failure_alert',document.addDoc.docName.value));
	    enableDocButtons('false');
	    document.addDoc.docName.focus();
	}
	
	if(parent.layout) {
		if(parent.layout.cols == "0,*") {
			parent.layout.cols = "140,*";
			parent.document.getElementById("treeFrame").noResize=false;	
		}
		else {
			parent.document.getElementById("treeFrame").noResize=false;	
		}
	}
}
function contentTypeExist(typeid) {
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "contentTypeExist", false);
		requestCaller.addParameter(1, "long", typeid);
		var flag = requestCaller.serviceRequest();
		return flag;
	}
	catch (ex1) {
		return 'false';
	}
}
/**
 * 控制文档新建，修改页面的button
 */
function disableDocButtons(edit){
	try{
		disableButton('save');
		disableButton('insert');
		if(edit == 'false')
			disableButton('back');	
	}catch(e){}
}
function enableDocButtons(edit){
	try{
		enableButton('save');
		enableButton('insert');
		if(edit == 'false')
			enableButton('back');	
		getA8Top().endProc();
	}catch(e){}
}
// 判断当前文档夹是否存在同名同类型文档 ajax实现
function dupliName(parentId, name, type, stringType) {
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "hasSameNameAndSameTypeDr", false);
		requestCaller.addParameter(1, "Long", parentId);
		requestCaller.addParameter(2, "String", name);
		if(stringType)
			requestCaller.addParameter(3, "String", type);
		else
			requestCaller.addParameter(3, "Long", type);
				
		var flag = requestCaller.serviceRequest();
		return flag;
	}
	catch (ex1) {
		alert("Exception : " + ex1.message);
	}
}
// 执行替换文件操作	，点击操作lable的替换就执行此方法
function docReplace(docResId, docLibType, objName,parentId,frtype) {	
	if(checkLock(docResId, false) == false) {
		return;
	}
	
	fileUploadQuantity = 1;
	fileUploadAttachments.clear();		//清空缓存
	insertAttachment();
	if(fileUploadAttachments.isEmpty() == false) {
		var keys=fileUploadAttachments.keys();
		var attach=fileUploadAttachments.get(keys.get(0),null);	//附件对象
		
		if(objName != attach.filename){
			if(!window.confirm(v3x.getMessage('DocLang.doc_replace_different_name_confirm'))){
				fileUploadAttachments.clear();	
				return;
			}
			var typeId = 21;//21 说明是文件的比较
	
			var exist = dupliName(parentId,attach.filename,typeId,false);
	
			if('true' == exist){
				alert(v3x.getMessage('DocLang.doc_upload_dupli_name_failure_alert',attach.filename));
				return;
			}
		}
		
		saveAttachment();
		var theForm = document.mainForm;
		theForm.target = "empty";
		theForm.action = jsURL + "?method=docReplace&docLibType=" + docLibType + "&docResId=" + docResId;
		theForm.submit();
	}
}
// 查看文档日志	
function logView(id, isFolder, name) {
	var theURL = jsURL + "?method=docLogViewIframe&docResId=" + id + "&docLibId=" + docLibId + "&isFolder=" + isFolder ;
	var openArgs = {};
	openArgs["url"] = theURL;
	if(v3x.getBrowserFlag('openWindow') == false){
		openArgs["dialogType"] = "open";
	}
	openArgs["workSpace"] = 'yes';
	openArgs["resizable"] = 'false';

	var log = v3x.openWindow(openArgs);
}
/**************************
 * grant beigin
 */
function grantFunction() {	  	

	var docResId = window.docResId;
	var docLibType = window.docLibType;
 				
	var flagisM = "false";
	var flagisC = "false";			
	if(docLibType == DocLib_Type_Private) {
		flagisM = "true";			
	}
	else {
		flagisC = "true";
	}
	var surl = baseurl + "/doc.do?method=docPropertyIframe&isP=false&isB=false&isM=" + flagisM + "&isC=" + flagisC
			 + "&docResId=" + docResId + "&docLibType=" + docLibType + "&isFolder=true";					 
	
	var movevalue = v3x.openWindow({
		url : surl,
		width : "500",
		height : "500",
		resizable : "false"
	});
	if(movevalue == "true") {
		window.location.reload(true);
	}
	
}
/**
 * grant end
 *************************************/

/****************************************
 * myDocument Grant begin create by xuegw 
 */
var MyPeoplemap = new Properties();
function setMyPeopleFields(elements, accountId) {
	if(!elements) {
		return;
	}
 
	var grantT = document.getElementById("mygrantgrantId");	
	var elementSize  = myOriginalElements.size();

	for(var i = 0 ;i < elementSize ; i ++){
		if(document.getElementById("mygrantuid"+i)==null){
              elementSize++;
			  continue;
		 }
		var mygrantuserid = document.getElementById("mygrantuid"+i).value;	
		MyPeoplemap.put(mygrantuserid,mygrantuserid);
	}
	
	ucfPersonalShare = true;
	myOriginalElements = new ArrayList() ;
	if(myOriginalElements != null) {
		var osize = myOriginalElements.size();
		var len = window.perShareNum;
		for(var i = 0; i < elements.length; i++) {			 
			
			for(var j = 0; j < osize; j++) {
							
				if(myOriginalElements.contains(elements[i].id)) {
					
					continue;
				}
				var theName = getNameString(elements[i], accountId);	
				if(MyPeoplemap.get(elements[i].id) == null){
				var tr = grantT.insertRow(-1);
				tr.id = elements[i].id;
				var td0 = tr.insertCell(-1);
							td0.align = "center";
				td0.innerHTML = "<input type='checkbox'  name='mygrantid' value='" + elements[i].id + "' />";
				td0.className = "sort";
				var td = tr.insertCell(-1);
					
				td.innerHTML = theName + "<input type='hidden' name='mygrantusername' value='" + theName + "'> "
							+ "<input type='hidden' id=mygrantuid" + len + " name=mygrantuid" + len + " value='" + elements[i].id + "'>"
							+ "<input type='hidden' id=mygrantutype" + len + " name=mygrantutype" + len + " value='" + elements[i].type + "'>"
							+ "<input type='hidden' id=mygrantinherit" + len + " name=mygrantinherit" + len + " value='false'>"
							+ "<input type='hidden' id=mygrantalert" + len + " name=mygrantalert" + len + " value='false'>	"	
							+ "<input type='hidden' id=mygrantaclid" + len + " name=mygrantaclid" + len + " value='0'>	"		
							+ "<input type='hidden' id=mygrantalertnew" + len + " name=mygrantalertnew" + len + " value='false'>	"	
							+ "<input type='hidden' id=mygrantalertid" + len + " name=mygrantalertid" + len + " value=''>	";	
				
				//alert(td.innerHTML)
				myOriginalElements.add("" + elements[i].id + "");
				
				td.className = "sort";	
				
				var td1 = tr.insertCell(-1);
							td1.align = "center";
				td1.innerHTML = "<input type='checkbox' id='mygrantalertckb" + len + "' name='mygrantalertckb" + len +"' value='" + elements[i].id 
						+ "' onchange=\"userChange('ucfPersonalShare')\""
						+ " onclick=\"mygrantalert('" + len + "')\"/>";
				td1.className = "sort";													
				
				len += 1;
			} // end for-loop
			}
		}	// end for-loop
		window.perShareNum = len;
	} // end if
	
	if(myOriginalElements.size() == 0) {
	    var len = window.perShareNum;
		for(var i = 0; i < elements.length; i++){
			var theName = getNameString(elements[i], accountId);	
			if(MyPeoplemap.get(elements[i].id) == null){
			var tr = grantT.insertRow(-1);
			tr.id = elements[i].id;
			var td0 = tr.insertCell(-1);
						td0.align = "center";
			td0.innerHTML = "<input type='checkbox' name='mygrantid' value='" + elements[i].id + "' />";
			td0.className = "sort";
			var td = tr.insertCell(-1);
				
			td.innerHTML = theName + "<input type='hidden' name='mygrantusername' value='" + theName + "'> "
						+ "<input type='hidden' id=mygrantuid" + (len+i) + " name=mygrantuid" + (len+i) + " value='" + elements[i].id + "'>"
						+ "<input type='hidden' id=mygrantutype" + (len+i) + " name=mygrantutype" + (len+i) + " value='" + elements[i].type + "'>"
						+ "<input type='hidden' id=mygrantinherit" + (len+i) + " name=mygrantinherit" + (len+i) + " value='false'>	"
						+ "<input type='hidden' id=mygrantalert" + (len+i) + " name=mygrantalert" + (len+i) + " value='false'>	"	
						+ "<input type='hidden' id=mygrantaclid" + (len+i) + " name=mygrantaclid" + (len+i) + " value='0'>	"		
						+ "<input type='hidden' id=mygrantalertnew" + (len+i) + " name=mygrantalertnew" + (len+i) + " value='false'>	"	
						+ "<input type='hidden' id=mygrantalertid" + (len+i) + " name=mygrantalertid" + (len+i) + " value=''>	";	
			myOriginalElements.add("" + elements[i].id + "");
			//alert(td.innerHTML)
			td.className = "sort";	
			
			var td1 = tr.insertCell(-1);
			td1.align = "center";
				td1.innerHTML = "<input type='checkbox' id='mygrantalertckb" + (len+i) + "' name='mygrantalertckb" + (len+i) +"' value='" + elements[i].id 
					+ "' onchange=\"userChange('ucfPersonalShare')\""
					+ " onclick=\"mygrantalert('" + (len+i) + "')\"/>";
			td1.className = "sort";		
			}
			//window.perShareNum = i + 1;											
		}
		window.perShareNum = len+elements.length
		
	} // end if
}
function mygrantdeleteUser() {
  var checkedids = document.getElementsByName('mygrantid');
  var len = checkedids.length;
  var count = 0;
  for(var i = 0; i < len; i++) {
  		var checkedid = checkedids[i];
		
		if(checkedid && checkedid.checked && checkedid.parentNode.parentNode.tagName == "TR"){			
			checkedid.parentNode.parentNode.parentNode.removeChild(checkedid.parentNode.parentNode);
			myOriginalElements.remove("" + checkedid.value + "");
			i--;
			count++;
		}
	}
	
	if(count == 0){
   		alert(v3x.getMessage("DocLang.doc_delete_select_alert"));
   }else{
		ucfPersonalShare = true;
   }
}
/**
 * myDocument Grant end create by xuegw 
 ***************************************/
 
/**************************
 * docgrant     begin     create by xuegw
 */
 
var grantsetpepomap = new Properties();
function docGrantSetPeopleFields(elements, accountId) {
	if(!elements) {
		return;
	}		
	ucfPublicShare = true;
	var elementSize  = originalElements.size();
	
	var grantT = document.getElementById("grantId");
	for(var i = 0 ; i < elementSize ; i++ ){
		 //grantT.deleteRow(1) ;
		 if(document.getElementById("uid"+i)==null){
              elementSize++;
			  continue;
		 }
		 var uid = document.getElementById("uid"+i).value;
		 grantsetpepomap.put(uid,uid);
	}
	originalElements = new ArrayList() ;
	var isGroupRes = window.isGroupLib;
		
	if(originalElements != null) {
		var osize = originalElements.size();
		var len = window.deptShareNum;
		for(var i = 0; i < elements.length; i++) {
		 		
			for(var j = 0; j < osize; j++) {
							
				if(originalElements.contains(elements[i].id)) {					
					continue;
				}
				if(grantsetpepomap.get(elements[i].id) == null){
				var tr = grantT.insertRow(-1);
				tr.id = elements[i].id;
				var td0 = tr.insertCell(-1);
				td0.align = "center";
				td0.innerHTML = "<input type='checkbox' name='id' value='" + elements[i].id + "' />";
				td0.className = "sort";
				var td = tr.insertCell(-1);
				
				var theName = getNameString(elements[i], accountId);	
				if(isGroupRes == 'true'){
					var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
						 "getEntityNameWithAccountShort", false);
					requestCaller.addParameter(1, "String", elements[i].type);
					requestCaller.addParameter(2, "Long", elements[i].id);
							
					theName = requestCaller.serviceRequest();
				}
				
				td.innerHTML = theName + "<input type='hidden' name='username' value='" + theName + "'> "
							+ "<input type='hidden' name=uid" + len + " value='" + elements[i].id + "'>"
							+ "<input type='hidden' name=utype" + len + " value='" + elements[i].type + "'>"
							+ "<input type='hidden' name=inherit" + len + " value='false'>	";
				
				originalElements.add("" + elements[i].id + "");
				
				td.className = "sort";
				
				var td1 = tr.insertCell(-1);
				td1.align = "center";
				td1.innerHTML = '<input type="checkbox" name="cAll' + len + '" id="cAll' + len 
					+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + len + '\')"'
					+ ' value="true" >';
				
				td1.className = "sort";
				
				var td2 = tr.insertCell(-1);
				td2.align = "center";
				td2.innerHTML = '<input type="checkbox" name="cEdit' + len + '" id="cEdit' + len 
										+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + len + '\')"'
					+ ' value="true" >';
				
				td2.className = "sort";
				
				var td3 = tr.insertCell(-1);
				td3.align = "center";
				td3.innerHTML = '<input type="checkbox" name="cAdd' + len + '" id="cAdd' + len 
										+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + len + '\')"'
					+ ' value="true" >';
				
				td3.className = "sort";
				
				var td4 = tr.insertCell(-1);
				td4.align = "center";
				td4.innerHTML = '<input type="checkbox" name="cRead' + len + '" id="cRead' + len 
										+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + len + '\')"'
					+ ' value="true" checked>';
				
				td4.className = "sort";
				
				var td5 = tr.insertCell(-1);
				td5.align = "center";
				td5.innerHTML = '<input type="checkbox" name="cBrowse' + len + '" id="cBrowse' + len 
										+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + len + '\')"'
					+ ' value="true" checked>';
				
				td5.className = "sort";
				
				var td6 = tr.insertCell(-1);
				td6.align = "center";
				td6.innerHTML = '<input type="checkbox" name="cList' + len + '" id="cList' + len 
										+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + len + '\')"'
					+ ' value="true" checked>';
				
				td6.className = "sort";
				
				var td7 = tr.insertCell(-1);
				td7.align = "center";
				td7.innerHTML = '<input type="checkbox" name="cAlert' + len + '" id="cAlert' + len 
										+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + len + '\')"'
					+ ' value="true" >';
				
				td7.className = "sort";
										
				len += 1;
			}
			}
		}	
		window.deptShareNum = len;
	}

	if(originalElements.size() == 0) {
    	 var len = window.deptShareNum;
		for(var i = 0; i < elements.length; i++){
			if(grantsetpepomap.get(elements[i].id) == null){
			var tr = grantT.insertRow(-1);
			tr.id = elements[i].id;
			var td0 = tr.insertCell(-1);
			td0.align = "center";
			td0.innerHTML = "<input type='checkbox' id='docGrant"+(len+i)+"' name='id' value='" + elements[i].id + "' />";
			td0.className = "sort";
			var td = tr.insertCell(-1);
			
			var theName = getNameString(elements[i], accountId);	
			if(isGroupRes == 'true'){
				var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
					 "getEntityNameWithAccountShort", false);
				requestCaller.addParameter(1, "String", elements[i].type);
				requestCaller.addParameter(2, "Long", elements[i].id);
						
				theName = requestCaller.serviceRequest();
			}
			
			td.innerHTML = theName + "<input type='hidden' name='username' value='" + theName + "'> "
						+ "<input type='hidden' name=uid" + (len+i) + " value='" + elements[i].id + "'>"
						+ "<input type='hidden' name=utype" + (len+i) + " value='" + elements[i].type + "'>"
						+ "<input type='hidden' name=inherit" +(len+i)+ " value='false'>	";
			originalElements.add("" + elements[i].id + "");
			
			td.className = "sort";
			
			var td1 = tr.insertCell(-1);
			td1.align = "center";
			td1.innerHTML = '<input type="checkbox"  name="cAll' + (len+i) + '" id="cAll' +(len+i) 
										+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + (len+i) + '\')"'
					+ ' value="true" >';
			
			td1.className = "sort";
			
			var td2 = tr.insertCell(-1);
			td2.align = "center";
			td2.innerHTML = '<input type="checkbox" name="cEdit' + (len+i) + '" id="cEdit' +(len+i)
															+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + (len+i) + '\')"'
					+ ' value="true" >';
			
			td2.className = "sort";
			
			var td3 = tr.insertCell(-1);
			td3.align = "center";
			td3.innerHTML = '<input type="checkbox" name="cAdd' + (len+i) + '" id="cAdd' + (len+i) 
															+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + (len+i) + '\')"'
					+ ' value="true" >';
			
			td3.className = "sort";
			
			var td4 = tr.insertCell(-1);
			td4.align = "center";
			td4.innerHTML = '<input type="checkbox" name="cRead' + (len+i) + '" id="cRead' + (len+i) 
															+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + (len+i) + '\')"'
					+ ' value="true" checked>';
			
			td4.className = "sort";
			
			var td5 = tr.insertCell(-1);
			td5.align = "center";
			td5.innerHTML = '<input type="checkbox" name="cBrowse' +(len+i) + '" id="cBrowse' + (len+i) 
															+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + (len+i) + '\')"'
					+ ' value="true" checked>';
			
			td5.className = "sort";
			
			var td6 = tr.insertCell(-1);
			td6.align = "center";
			td6.innerHTML = '<input type="checkbox" name="cList' +(len+i) + '" id="cList' +(len+i) 
															+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' +(len+i) + '\')"'
					+ ' value="true" checked>';
			
			td6.className = "sort";
			
			var td7 = tr.insertCell(-1);
			td7.align = "center";
			td7.innerHTML = '<input type="checkbox" name="cAlert' + (len+i) + '" id="cAlert' + (len+i) 
														+ '" onchange="userChange(\'ucfPublicShare\')"'
					+ ' onclick="validateAcl(\'' + (len+i) + '\')"'
				+ ' value="true" >';
			
			td7.className = "sort";
			}
			//window.deptShareNum = i + 1;	
		}
		window.deptShareNum = len + elements.length 
	}
 	
}
//-->
function docGrantdeleteUser() {
  var checkedids = document.getElementsByName('id');
  var len = checkedids.length;
  var count = 0;
  for(var i = 0; i < len; i++) {
  		var checkedid = checkedids[i];
		
		if(checkedid && checkedid.checked && checkedid.parentNode.parentNode.tagName == "TR"){			
			checkedid.parentNode.parentNode.parentNode.removeChild(checkedid.parentNode.parentNode);
			originalElements.remove("" + checkedid.value + "");
			i--;
			count++;
		}
	}
   
   if(count == 0){
   		alert(v3x.getMessage("DocLang.doc_delete_select_alert"));
   }else{
	//alert("doc.js ucf::Prop--" + ucfProp+"ucf::Public--" + ucfPublicShare+"ucf::Personal--" + ucfPersonalShare+"ucf::Borrow--" + ucfBorrow)
	ucfPublicShare = true;
   }
}
/**
 * 根据element得到名称，自动判断是否添加单位简称
 */
function getNameString(e, accountId){
		var _name = null;
		if(e.accountId != accountId){
			if(e.accountShortname)
				_name = e.name + "(" + e.accountShortname + ")";
			else{
				var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
					 "getEntityNameWithAccountShort", false);
				requestCaller.addParameter(1, "String", e.type);
				requestCaller.addParameter(2, "Long", e.id);
						
				_name = requestCaller.serviceRequest();
			}			
		}
		else{
			_name = e.name;
		}
		return _name;
}
var mapdatafield = new Properties();
function borrowSetPeopleFields(elements, accountId) {
	if(!elements) {
		return;
	}
	ucfBorrow = true;

	var borrowgrantT = document.getElementById("borrowgrantId");
	var endTimeLable = v3x.getMessage("DocLang.doc_time_end_lable");
	var startTimeLable = v3x.getMessage("DocLang.doc_time_start_lable");
	for(var i = 0 ; i<originalElementsborrow.size(); i++) {
		var borrowuid ;
		//判断如果对应input不为null则将value取出放入更新map，否则continue
		if(document.getElementById("borrowuid"+i) && document.getElementById("borrowuid"+i) != null) { 
			borrowuid = document.getElementById("borrowuid"+i).value;
		} else {
			continue;
		}
		mapdatafield.put(borrowuid,borrowuid);
	}
	originalElementsborrow = new ArrayList();
	
	var isGroupRes = window.isGroupLib;
	var len = window.borrowNum;
	for(var i = 0; i < elements.length; i++) {
		var theName = getNameString(elements[i], accountId);
		if(isGroupRes == 'true') {
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "getEntityNameWithAccountShort", false);
			requestCaller.addParameter(1, "String", elements[i].type);
			requestCaller.addParameter(2, "Long", elements[i].id);
			theName = requestCaller.serviceRequest();
		}
		if(mapdatafield.get(elements[i].id) == null){
			var tr = borrowgrantT.insertRow(-1);
			tr.id = elements[i].id; 
			var td0 = tr.insertCell(-1);
			td0.align = "center";
			td0.innerHTML = "<input type='checkbox' name='borrowid' value='" + elements[i].id + "' />";
			td0.className = "sort";
			var td = tr.insertCell(-1);
			td.innerHTML = theName + "<input type='hidden' name='borrowusername' value='" + theName + "'><input type='hidden' name=borrowusername" + (len + i) + " value='" + theName + "'> "
						+ "<input type='hidden' id=borrowuid" + (len + i) + " name=borrowuid" + (len + i) + " value='" + elements[i].id + "'>"
						+ "<input type='hidden' id=borrowutype" + (len + i) + " name=borrowutype" + (len + i) + " value='" + elements[i].type + "'>";
			originalElementsborrow.add("" + elements[i].id + "");
			mapdatafield.put("" + elements[i].id + "", "" + elements[i].id + "");
			
			td.className = "sort";
			var dateWidth = "";
			if(doc_fr_type=="2")//公文增加借阅权限
			{
				var td3=tr.insertCell(-1); 
				td3.innerHTML="<select name='lenPotent"+(len + i)+"'><option value='1'>"+lenPotentLan_all+"</option><option value='2' selected>"+lenPotentLan_content+"</option></select>";
				td3.className = "sort";
				var td4=tr.insertCell(-1); 
				td4.innerHTML="<input type='checkbox' name='lenPotent2a"+(len + i)+"' value='1'>"+lenPotentLan_save+" <input type='checkbox' name='lenPotent2b"+(len + i)+"' value='2'>"+lenPotentLan_print;
				td4.className = "sort";
				dateWidth = ' style=\"width:72px\" ';
				var td1 = tr.insertCell(-1);

				td1.innerHTML = '<input readonly="readonly" type=text validate="notNull" inputName=\"'+startTimeLable+'" name=begintime' + (len + i) + ' value=' + dtb.substring(0, 10) + dateWidth
				
				+ ' onclick=\'whenstart(\"' + contpath + '\",this,300,200,"date");userChangeCalendar(this,"'+dtb+'")\'>';
			
			td1.className = "sort";
			
			var td2 = tr.insertCell(-1);
			td2.innerHTML = '<input readonly="readonly" validate="notNull" inputName=\"'+endTimeLable+'" type=text name=endtime' + (len + i) + ' value=' + dte.substring(0, 10) + dateWidth
				
				+ ' onclick=\'whenstart(\"' + contpath + '\",this,300,200,"date");userChangeCalendar(this,"'+dte+'")\'>';
			
			td2.className = "sort";	
						
			}else{
			var td1=tr.insertCell(-1);
				td1.innerHTML='<input type="checkbox" name=\"bRead'+ (len + i) + '\" id=\"bRead' +(len + i) +'\" value="true" checked>';
				td1.className = "sort";

		    var td2=tr.insertCell(-1); 
				td2.innerHTML='<input type="checkbox" name=\"bBrowse'+ (len + i) + '\" id=\"bBrowse' +(len + i) +'\" value="true" checked>';
				td2.className = "sort";
			
			dateWidth = ' style=\"width:90px\" ';
			
			var td3 = tr.insertCell(-1);

				td3.innerHTML = '<input readonly="readonly" type=text validate="notNull" inputName=\"'+startTimeLable+'" name=begintime' + (len + i) + ' value=' + dtb.substring(0, 10) + dateWidth
				
				+ ' onclick=\'whenstart(\"' + contpath + '\",this,300,200,"date");userChangeCalendar(this,"'+dtb+'")\'>';
			
			    td3.className = "sort";
			
			var td4 = tr.insertCell(-1);
			    td4.innerHTML = '<input readonly="readonly" validate="notNull" inputName=\"'+endTimeLable+'" type=text name=endtime' + (len + i) + ' value=' + dte.substring(0, 10) + dateWidth
				
				+ ' onclick=\'whenstart(\"' + contpath + '\",this,300,200,"date");userChangeCalendar(this,"'+dte+'")\'>';
			
			    td4.className = "sort";	
			}
						
		}			
			

	}
	window.borrowNum = elements.length + len;	
}
/**
 * borrow grant create by xuegw end
 ***************************************/

/**
 * 发送文档或文档夹到常用文档。
 */
function addMyFavorite(rowid){//alert(11111)
	var aUrl = jsURL + "?method=sendToFavorites&userType=member";
	var checkid = rowid;
	var mainForm = document.getElementById("mainForm");
	if (checkid == "undefined"){//alert(222222)
		mainForm.target = "empty";
		mainForm.action = aUrl;

		if(hasSelectedData())
			mainForm.submit();
		else
			return;
	}
	else {
		mainForm.target = "empty";
		mainForm.action = aUrl + "&docId=" + rowid;
		mainForm.submit();
	}

}

/**
 * 判断是否选了列表数据，菜单项使用
 */
function hasSelectedData(){
	var chkid = document.getElementsByName('id');//alert(chkid)
	var checked = false;
	for(var i = 0; i < chkid.length; i++){
		if(chkid[i].checked){
			checked = true;
			break;
		}
	}
	//alert(checked)
	if(!checked) {
		alert(v3x.getMessage("DocLang.doc_more_select_alert"))
		return false;
	}else
		return true;
	    
}
/*
 * 得到选中的条数
 */
function getSelectedCount(){
	var chkid = document.getElementsByName('id');//alert(chkid)
	var count = 0;
	var id;
	for(var i = 0; i < chkid.length; i++){
		if(chkid[i].checked){
			count++;
			id = chkid[i].value;
		}
	}
	
	return count;    
}
/**
 * 得到选中的id串
 */
 function getSelectedIds(){
	var chkid = document.getElementsByName('id');//alert(chkid)
//	var count = 0;
	var ids = "";
	for(var i = 0; i < chkid.length; i++){
		if(chkid[i].checked){
//			count++;
			ids += "," + chkid[i].value;
		}
	}
	
	if(ids != "")
		ids = ids.substring(1, ids.length);
	return ids;    
}


// 发送到部门文档
function sendToDeptDoc(depAdminSize, flag){

	//var mf = document.getElementById('docUploadDiv');
	
	if('right' == flag){//alert(111)
	// rightNew
		document.mainForm.target = "empty";
		document.mainForm.action = jsURL + "?method=sendToFavorites&docId=&userIds=&userType=dept";
		
		if(!hasSelectedData())
			return;
		
				if(depAdminSize == '1'){
					document.mainForm.submit();
				}else{
					var theURL = jsURL + "?method=selectDepts";
			    	
			    	var depts = v3x.openWindow({
						url : theURL,
						width : "360",
						height : "240"
					});
					
					
					if(depts == "" || depts == undefined)
						return;
					
					document.mainForm.action = jsURL + "?method=sendToFavorites&docId=&userIds="+ depts +"&userType=dept";
					document.mainForm.submit();
				}


	}else{//alert(22222)
	
	   //if(!hasSelectedData())
		//     	 return;
		if(depAdminSize == '1'){
			mainForm.target = "empty";
			mainForm.action = jsURL + "?method=sendToFavorites&docId=" + mainForm.selectedRowId.value  
				+ "&userIds=&userType=dept";
			mainForm.submit();
		}else{
			var theURL = jsURL + "?method=selectDepts";
	    	
	    	var depts = v3x.openWindow({
				url : theURL,
				width : "360",
				height : "240"
			});
			
			
			if(depts == "" || depts == undefined)
				return;
			
			mainForm.target = "empty";
			mainForm.action = jsURL + "?method=sendToFavorites&docId=" + mainForm.selectedRowId.value  
				+ "&userIds="+ depts +"&userType=dept";
			mainForm.submit();
		}
	}
	
}

/**
 * 发布文档或文档夹到单位空间
 */
function publishDoc(rowid) {
	var aUrl = jsURL + "?method=sendToFavorites&userType=account";
	var checkid = rowid;
	if (checkid == "undefined"){
		document.mainForm.target = "empty";
		document.mainForm.action = aUrl;

		if(!hasSelectedData())
			return;

		document.mainForm.submit();
//			}
//		}
	}
	else {
		mainForm.target = "empty";
		mainForm.action = aUrl + "&docId=" + rowid;
		mainForm.submit();
	}
}

// 发送到个人学习区
function sendToLearn(elements, flag){	
	if(!elements) {
		return;
	}
	var ids = "";
	for(var i = 0; i < elements.length; i++) {								
		ids += "," + elements[i].id;	
	}
	//var mf = document.getElementById('mainForm');
	
	if('right' == flag){
		document.mainForm.target = "empty";
		document.mainForm.action = jsURL + "?method=sendToLearn&docId=&userIds=" 
			+ ids.substring(1, ids.length) + "&userType=member";

		if(!hasSelectedData())
			return;
			
		document.mainForm.submit();

	}else{
//		alert(11111)
		mainForm.target = "empty";
		mainForm.action = jsURL + "?method=sendToLearn&docId=" + mainForm.selectedRowId.value 
			+ "&userIds=" + ids.substring(1, ids.length) + "&userType=member";
//		alert(mainForm.action)
		mainForm.submit();
	}


}

// 发送到单位学习区
function sendToAccountLearn(flag){

	//var mf = document.getElementById('mainForm');
	
	if('right' == flag){
		document.mainForm.target = "empty";
		document.mainForm.action = jsURL + "?method=sendToLearn&docId=&userIds=&userType=account";

		if(!hasSelectedData())
			return;

		document.mainForm.submit();
//			}
//		}
	}else{
		mainForm.target = "empty";
		mainForm.action = jsURL + "?method=sendToLearn&docId=" + mainForm.selectedRowId.value 
			+ "&userIds=&userType=account";
//		alert(mainForm.action)
		mainForm.submit();
	}
	

}

// 发送到部门学习区
function sendToDeptLearn(depAdminSize, flag){
	//var mf = document.getElementById('mainForm');
	// mainForm 在 打开页面菜单、右键弹出菜单 可以拿到
	//          在 列表菜单 拿不到
	if('right' == flag){
		document.mainForm.target = "empty";
		document.mainForm.action = jsURL + "?method=sendToLearn&docId=&userIds=&userType=dept";

		if(!hasSelectedData())
			return;
			
				if(depAdminSize == '1'){
					document.mainForm.submit();
				}else{
					var theURL = jsURL + "?method=selectDepts";
	    	
			    	var depts = v3x.openWindow({
						url : theURL,
						width : "360",
						height : "240"
					});
					
					if(depts == "" || depts == undefined)
						return;

					document.mainForm.action = jsURL + "?method=sendToLearn&docId=&userIds="+ depts +"&userType=dept";
					document.mainForm.submit();
				}

	}else{
	
	 	//if(!hasSelectedData())
		//	return;
		if(depAdminSize == '1'){
			mainForm.target = "empty";
			mainForm.action = jsURL + "?method=sendToLearn&docId=" + mainForm.selectedRowId.value  
				+ "&userIds=&userType=dept";
			mainForm.submit();
		}else{
			var theURL = jsURL + "?method=selectDepts";
	    	
	    	var depts = v3x.openWindow({
				url : theURL,
				width : "360",
				height : "240"
			});
			
			if(depts == "" || depts == undefined)
				return;
			
			mainForm.target = "empty";
			mainForm.action = jsURL + "?method=sendToLearn&docId=" + mainForm.selectedRowId.value  
				+ "&userIds="+ depts +"&userType=dept";
			mainForm.submit();
		}
	}
	
}

/**
 * 发送到集团首页
 */
 function sendToGroup(rowid) {
	var aUrl = jsURL + "?method=sendToFavorites&userType=group";
	var checkid = rowid;
	if (checkid == "undefined"){
		document.mainForm.target = "empty";
		document.mainForm.action = aUrl;

		if(!hasSelectedData())
			return;
			
		document.mainForm.submit();

	}
	else {
		mainForm.target = "empty";
		mainForm.action = aUrl + "&docId=" + rowid;
		mainForm.submit();
	}
}
/**
 * 发送到集团学习区
 */
 function sendToGroupLearn(flag){

	//var mf = document.getElementById('mainForm');
	
	if('right' == flag){
		document.mainForm.target = "empty";
		document.mainForm.action = jsURL + "?method=sendToLearn&docId=&userIds=&userType=group";

		if(!hasSelectedData())
			return;

		document.mainForm.submit();

	}else{
		mainForm.target = "empty";
		mainForm.action = jsURL + "?method=sendToLearn&docId=" + mainForm.selectedRowId.value 
			+ "&userIds=&userType=group";
//		alert(mainForm.action)
		mainForm.submit();
	}
	

}

// 学习记录查看
function learnHistoryView(docid, isGroupLib){
	var theURL = jsURL + "?method=docLearningHistoryIframe&docId=" + docid + "&isGroupLib=" + isGroupLib;
	var openArgs = {};
	openArgs["url"] = theURL;
	openArgs["width"] = '800';
	openArgs["height"] = '700';
	if(v3x.getBrowserFlag('openWindow') == false){
		openArgs["dialogType"] = "open";
	} 
	v3x.openWindow(openArgs);
}
var winMove;
function openDialog4Ipad(url){
 winMove=v3x.openDialog({
    	 id:"move",
    	 title:"",
    	 url : url,
    	 width: 500,
    	 height: 500,
    	 type:'panel',
    	 //relativeElement:obj,
    	 buttons:[{
    	 id:'btn1',
    	 text: v3x.getMessage("collaborationLang.submit"),
    	 handler: function(){
    	        var returnValues = winMove.getReturnValue();
	     }
    	            
    	 }, {
    	 id:'btn2',
    	 text: v3x.getMessage("collaborationLang.cancel"),
    	 handler: function(){
    	    winMove.close();
    	 }
    	 }]
    });
}
function closeSendWindow(){
	if(parent.parent.winMove){
		parent.parent.winMove.close();
	}
}
/**
 * 选择目标文档夹。
 * 移动、映射、归档时调用此方法。
 * @param action [move | link | pigeonhole]
 * @param flag 是否从文档工作区操作
 */
function selectDestFolder(rowid, parentId, docLibId, docLibType, action) {
	
	var surl = jsURL + "?method=docTreeMoveIframe&parentId=" + parentId	+ "&isrightworkspace=" + action 
			+ "&docLibId=" + docLibId + "&docLibType=" + docLibType;

	var result = "false";

	// flag是否从工作区进行操作!!!
	
	

	var checkid = rowid;
	if (checkid == "undefined") {		
		surl += "&flag=false";
		checkid = document.mainForm.id;
		if (checkid == "mainForm") {
			alert(v3x.getMessage('DocLang.doc_more_select_alert'));
			return;
		}
		//alert(checkid.length);
		var len = checkid.length;
		var checked = false;
		if (isNaN(len)) {			
			if (!checkid.checked) {
				alert(v3x.getMessage('DocLang.doc_more_select_alert'));
				return;
			}
			else {
				if(action == "move"){
				if(!window.confirm(parent.v3x.getMessage('DocLang.doc_move_alert')))    return;
				}
				if(v3x.getBrowserFlag('openWindow') == false){
					openDialog4Ipad(surl);
				} else {
					result = v3x.openWindow({url:surl, width:"500", height:"500", resizable:"false"});
				}
			}
		}
		else {
			for (i = 0; i <len; i++) {				
				if (checkid[i].checked == true) {
					checked = true;
					break;
				}
			}
			if (!checked) {
				alert(v3x.getMessage('DocLang.doc_more_select_alert'));
				return ;
			}
			else {
				if(action == "move"){
				if(!window.confirm(parent.v3x.getMessage('DocLang.doc_move_alert')))    return;
				}
				if(v3x.getBrowserFlag('openWindow') == false){
					openDialog4Ipad(surl);
				} else {
					result = v3x.openWindow({url:surl, width:"500", height:"500", resizable:"false"});
				}
			}
		}
	}
	else {
		surl += "&id=" + checkid + "&flag=true";
	if(action == "move"){
				if(!window.confirm(parent.v3x.getMessage('DocLang.doc_move_alert')))    return;
				}
				if(v3x.getBrowserFlag('openWindow') == false){
					openDialog4Ipad(surl);;
				} else {
					result = v3x.openWindow({url:surl, width:"500", height:"500", resizable:"false"});
				}
	}

	if (result == "true" && action == "move") {
		window.location.reload(true);
	}
}
// 锁定文档操作
function lockDoc(rowid) {
	if(checkLock(rowid, false) == false) {
		return;
	}
	
	mainForm.action = jsURL + "?method=lockDoc&docResId=" + rowid;
	mainForm.target = "empty";
	mainForm.submit();
}

// 解除文档锁操作
function unlockDoc(rowid, userId) {
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "isDocAppUnlocked", false);
		requestCaller.addParameter(1, "Long", rowid);
		requestCaller.addParameter(2, "Long", userId);
	var flag = requestCaller.serviceRequest();
	if('true' == flag || true == flag) {
		alert(v3x.getMessage("DocLang.doc_alert_unlocked_again"));
		window.location.reload(true);
		return;
	}
	
	mainForm.action = jsURL + "?method=unlockDoc&docResId=" + rowid;
	mainForm.target = "empty";
	mainForm.submit();
}


/**
  * open move tree
  *************************************/ 
  
/**
 * tr innerHTML disable || enable
 */
function docDisable(id) {
	if(navigator.appName == "Microsoft Internet Explorer"){
		if(document.getElementById(id) != null)
			document.getElementById(id).disabled = true;
	} else {
		var contentMenuObj=document.getElementById(id);
		if(contentMenuObj!=null){
			//TODO 暂时加上hidden样式来屏蔽按钮出现，以解决多浏览器下js不兼容问题
			contentMenuObj.setAttribute("class","hidden disabled webfx-menu--button");
			contentMenuObj.setAttribute("onmouseover","");
			contentMenuObj.setAttribute("onmouseout","");
			contentMenuObj.setAttribute("onclick","");
		}
		var contentSpan = document.getElementById(id+"span");
		if(contentSpan != null){
			contentSpan.setAttribute("onclick1",contentSpan.getAttribute("onclick"));
			contentSpan.setAttribute("onmouseover1",contentSpan.getAttribute("onmouseover"));
			contentSpan.setAttribute("onmouseout1",contentSpan.getAttribute("onmouseout"));
			contentSpan.setAttribute("onmouseover","");
			contentSpan.setAttribute("onmouseout","");
			contentSpan.setAttribute("onclick","");
		}
		var contentImage = document.getElementById(id+"image");
		if(contentImage != null){
			contentImage.setAttribute("onclick1",contentImage.getAttribute("onclick"));
			contentImage.setAttribute("onmouseover1",contentImage.getAttribute("onmouseover"));
			contentImage.setAttribute("onmouseout1",contentImage.getAttribute("onmouseout"));
			contentImage.setAttribute("onmouseover","");
			contentImage.setAttribute("onmouseout","");
			contentImage.setAttribute("onclick","");
		}
	}
}

function docEnable(id) {
	if(navigator.appName == "Microsoft Internet Explorer"){
		if(document.getElementById(id) != null)
			document.getElementById(id).disabled = false;
	} else {
		var contentMenuObj=document.getElementById(id);
		if(contentMenuObj!=null){
			contentMenuObj.setAttribute("class","webfx-menu--button");
		}
		var contentSpan = document.getElementById(id+"span");
		if(contentSpan != null){
			if(contentSpan.getAttribute("onclick1") != null){
				contentSpan.setAttribute("onclick",contentSpan.getAttribute("onclick1"));
			}
			if(contentSpan.getAttribute("onmouseover1") != null){
				contentSpan.setAttribute("onmouseover",contentSpan.getAttribute("onmouseover1"));
			}
			if(contentSpan.getAttribute("onmouseout1") != null){
				contentSpan.setAttribute("onmouseout",contentSpan.getAttribute("onmouseout1"));
			}
		}
		var contentImage = document.getElementById(id+"image");
		if(contentImage != null){
			if(contentImage.getAttribute("onclick1") != null){
				contentImage.setAttribute("onclick",contentImage.getAttribute("onclick1"));
			}
			if(contentImage.getAttribute("onmouseover1") != null){
				contentImage.setAttribute("onmouseover",contentImage.getAttribute("onmouseover1"));
			}
			if(contentImage.getAttribute("onmouseout1") != null){
				contentImage.setAttribute("onmouseout",contentImage.getAttribute("onmouseout1"));
			}
		}
	}
}

function docDisplay(id) {
	var ele = document.getElementById(id);
	if(ele) {
		ele.style.display = "";
		ele.disabled = false; 	
	}
}
function docNoneDisplay(id) {
	var ele = document.getElementById(id);
	if(ele){
		ele.style.display = "none";
		if(ele.parentNode.nextSibling){ele.parentNode.nextSibling.style.display = "none";}
	}
}
function initFun(all, edit, add, readonly, browse, list, isPrivateLib, folderEnabled, a6Enabled, officeEnabled, uploadEnabled, isGroupLib, isEdocLib, isShareOrBorrow) {	
	// 控制菜单项显示
	if (isPrivateLib == "true") {
		sendToSubItems.hidden("deptDoc");
		sendToSubItems.hidden("deptLearn");
		sendToSubItems.hidden("accountLearn");
		sendToSubItems.hidden("publish");
		sendToSubItems.hidden("groupLearn");
		sendToSubItems.hidden("group");
		
		if (officeEnabled == "false") {
			newSubItems.hidden("word");
			newSubItems.hidden("excel");
			newSubItems.hidden("wpsword");
			newSubItems.hidden("wpsexcel");
		}
	} 
	else {
		if(isAdministrator == 'false'){
			sendToSubItems.disabled("publish");	
			sendToSubItems.disabled("accountLearn");			
		}	
		if(isGroupAdmin == 'false'){
			sendToSubItems.disabled("group");	
			sendToSubItems.disabled("groupLearn");			
		}
		if(depAdminSize == '0'){
			sendToSubItems.disabled("deptDoc");
			sendToSubItems.disabled("deptLearn");
		}
		
		if(isGroupLib == 'false'){
			sendToSubItems.hidden("groupLearn");
			sendToSubItems.hidden("group");
		}
		if (folderEnabled == "false") {
			newSubItems.hidden("folder");
		}

		if (a6Enabled == "false") {
			newSubItems.hidden("html");
		}

		if (officeEnabled == "false") {
			newSubItems.hidden("word");
			newSubItems.hidden("excel");
			newSubItems.hidden("wpsword");
			newSubItems.hidden("wpsexcel");
		}

		if (folderEnabled == "false" && a6Enabled == "false" && officeEnabled == "false" && isEdocLib == 'false') {
			docNoneDisplay("new");
		}

		// 控制上传文件菜单显示
		if (uploadEnabled == "false") {
			docNoneDisplay("upload");
		}	
		
		if(isEdocLib == 'true') {
			docNoneDisplay("sendto");
			//公文、预归档也要求排序(具有权限也需要排序时才出现高级菜单及其下拉菜单)
			if(all != 'true' || isNeedSort != 'true') {
				docNoneDisplay("forward");
			}
		}
	}	

	// 根据权限设置文档菜单操作是否可用
	if(all == "true") {
		docEnable("move");
		docEnable("del");
	}
	else {
		docDisable("move");
		docDisable("del");	
	}
	
	if(all == "true" || edit == "true" || add == "true") {		
		docEnable("new");
		docEnable("upload");
	}
	else {
		docDisable("new");
		newSubItems.hidden("html");
		newSubItems.hidden("folder");
		if (officeEnabled == "true") {
		     newSubItems.hidden("word");
		    newSubItems.hidden("word");
			newSubItems.hidden("excel");
			newSubItems.hidden("wpsword");
			newSubItems.hidden("wpsexcel");
		 }
		docDisable("upload");
	}
	if(all == "true" || edit == "true" || readonly == "true" || (isShareOrBorrow == "true" && (frType == "102" || frType == "103"))){
		docEnable("downloadFile");
	}else{
		docDisable("downloadFile");
	}
	if (all == "true" || edit == "true"  || readonly == "true" || add == "true" ) {
		docEnable("sendto");
		docEnable("forward");
	}
	else {
		docDisable("sendto");
		docDisable("forward");
		forwardSubItems.disabled("col");
	    forwardSubItems.disabled("mail");
	}
	
	var canNewColl = window.canNewColl;
	var canNewMail = window.canNewMail;
		if('false' == canNewColl){
			if('false' == canNewMail){
				forwardSubItems.disabled("mail");
			}
			forwardSubItems.disabled("col");
		}else if('false' == canNewMail){
			forwardSubItems.disabled("mail");
		}
		
	if(isShareOrBorrow == 'true'){
		docDisable("sendto");
		forwardSubItems.disabled("mail");
		forwardSubItems.disabled("col");
	}

	//only add acl
	if (all == "false" && edit == "false"  && readonly == "false" && browse == "false" && list == "false" && add == "true" ) {
			docDisable("sendto");
			docDisable("forward");
	}
}




/**
 * 顶部菜单权限控制
 */
 
 function docListAcl(all, edit, add, read, browse, list, folder, folderLink, link, appKey, isSysInit,isCreater){
 	this.all = all;
 	this.edit = edit;
 	this.add = add;
 	this.read = read;
 	this.browse = browse;
 	this.list = list;
 	
 	this.folder = folder;
 	this.folderLink = folderLink;
 	this.link = link;
 	this.appKey = appKey;
 	
 	this.isSysInit = isSysInit;
 	this.isCreater = isCreater;
 	
 	 if(all=="false"&&edit=="false"&&readonly=="false")  docDisable("forward");
 }
 docListAcl.prototype.toString = function(){
	var str = this.all + "," + this.edit + "," + this.add + ","
			+ this.read + "," + this.browse + "," + this.list
			+ "," + this.folder + "," + this.folderLink + "," + this.link;
	return str;
 }
 var docListAclMap = new Properties();
 
function chkMenuGrantControl(all, edit, add, readonly, browse, list, ele, folder, folderLink, link, appKey, isSysInit,isCreater) {
	if(!ele)
		return;

	if(ele.checked){
		if(docListAclMap.containsKey('parent'))
			docListAclMap.remove('parent');
		docListAclMap.put(ele.value, new docListAcl(all, edit, add, readonly, browse, 
				list, folder, folderLink, link, appKey, isSysInit,isCreater));
	}
	else{
		docListAclMap.remove(ele.value);
		if(docListAclMap.size() == 0)
			docListAclMap.put('parent', new docListAcl(parentAclAll, parentAclEdit, parentAclAdd, 
					parentAclReadonly, parentAclBrowse, parentAclList, 
					'false', 'false', 'false', appData.doc, 'false',isCreater));
	}

	ctrlDocMenuByAclMap(appKey);		

}
function ctrlDocMenuByAclMap(appKey){	
	var docListAclAll = true;
	var docListAclEdit = true;
	var docListAclAdd = true;
	var docListAclReadonly = true;
	var docListAclBrowse = true;
	var docListAclList = true;
	var docListIsCreater = true
	
	var isFolder = false;
	var isFolderLink = false;
	var isLink = false;
	var isCreater = false;
	var isSysInit = false;
	var values = docListAclMap.values();
	
	var isShareAndBorrowRoot=window.isShareAndBorrowRoot;
	for(var i = 0; i < values.size(); i++){
		var obj = values.get(i);
		
		isFolder = (isFolder || (obj.folder == 'true'));
		isFolderLink = (isFolderLink || (obj.folderLink == 'true'));
		isLink = (isLink || (obj.link == 'true'));
		isSysInit = (isSysInit || (obj.isSysInit == 'true'));
		isCreater = (obj.isCreater == 'true');

		if(obj.all == 'true')
			continue;
		docListAclAll = false;
		var edit = (obj.edit == 'true');
		var add = (obj.add == 'true');
		var read = (obj.read == 'true');
		var browse = (obj.browse == 'true');
		var list = (obj.list == 'true');
		if(browse){
			list = true;
		}	
		if(read){
			browse = true;
			list = true;
		}
		if(edit){
			read = true;
			browse = true;
			list = true;
		}

		docListAclEdit = (docListAclEdit && edit);
		docListAclAdd = (docListAclAdd && add);
		docListAclReadonly = (docListAclReadonly && read);
		docListAclBrowse = (docListAclBrowse && browse);
		docListAclList = (docListAclList && list);
		docListIsCreater = (docListIsCreater && isCreater);
	}
	
	if(docListAclAll && !isSysInit) {		
		window.document.getElementById("move").disabled = false;
		window.document.getElementById("del").disabled = false;		
	}
	else {
		window.document.getElementById("move").disabled = true;
		// 公文预归档夹允许删除 2010-11-30
		if(isSysInit && appKey != 3)
			window.document.getElementById("del").disabled = true;		
	}

	var canNewColl = window.canNewColl;
	var canNewMail = window.canNewMail;

	var forward = window.forwardSubItems;
	var sendto = window.sendToSubItems;
	docEnable("sendto");
	sendto.display("favorite");
	sendto.enabled("favorite");
	sendto.display("deptDoc");
	sendto.enabled("deptDoc");
	sendto.display("publish");
	sendto.enabled("publish");
	sendto.display("learning");
	sendto.enabled("learning");
	sendto.display("deptLearn");
	sendto.enabled("deptLearn");
	sendto.display("accountLearn");
	sendto.enabled("accountLearn");
	sendto.display("group");
	sendto.enabled("group");
	sendto.display("groupLearn");
	sendto.enabled("groupLearn");
	sendto.display("link");
	sendto.enabled("link");
	
	docEnable("forward");
	forward.display("col");
	forward.enabled("col");
	forward.display("mail");
	forward.enabled("mail");
	
	if(window.depAdminSize == '0'){
		sendto.disabled("deptDoc");
		sendto.disabled("deptLearn");
	}
	
	if(isGroupLib == 'false'){
		sendto.hidden("group");
		sendto.hidden("groupLearn");
	}
	
	if(isAdministrator == 'false'){
		sendto.disabled("accountLearn");
		sendto.disabled("publish");		
	}
	
	if(isGroupAdmin == 'false'){
		sendto.disabled("group");	
		sendto.disabled("groupLearn");			
	}
	//only add acl
	if(!docListAclAll && !docListAclEdit && !docListAclReadonly && !docListAclBrowse && !docListAclList && docListAclAdd){
		if(!docListIsCreater){
			docDisable("sendto");
			docDisable("forward");
		}
	}
	if (docListAclAll || docListAclEdit || docListAclReadonly || docListAclAdd) {
		
		if(isPersonalLib == 'true'){
			sendto.hidden("deptDoc");
			sendto.hidden("deptLearn");
			sendto.hidden("accountLearn");
			sendto.hidden("publish");
			sendto.hidden("group");
			sendto.hidden("groupLearn");
		}
				
		
		if(isFolder || isLink || isFolderLink) {
			sendto.hidden("learning");
			sendto.hidden("deptLearn");
			sendto.hidden("accountLearn");
			sendto.hidden("groupLearn");
			sendto.hidden("link");
		}
		
		if(isFolder || isLink || isFolderLink){
			forward.disabled("col");
			forward.disabled("mail");	
		} else {
			if(getSelectedCount() == 1){
				var appKey = values.get(0).appKey;
				if(appKey != appData.doc && appKey != appData.collaboration && appKey != appData.mail){
					forward.disabled("col");
					forward.disabled("mail");
				}
			} else if(getSelectedCount() > 1){
				forward.disabled("col");
				forward.disabled("mail");
			}
		}
		
		if('false' == canNewColl){
			if('false' == canNewMail){
				forward.disabled("col");
				forward.disabled("mail");
			} else {
				forward.disabled("col");
			}
		} else if('false' == canNewMail){
			forward.disabled("mail");
		}
	}
	else {
		if (browse)	{
			if(isFolder || isLink || isFolderLink) {
				sendto.hidden("learning");
				sendto.hidden("deptLearn");
				sendto.hidden("accountLearn");
				sendto.hidden("groupLearn");
				sendto.hidden("link");
			}
		} else {
			docDisable("sendto");
		}
		forward.disabled("col");
		forward.disabled("mail");
		sendto.disabled("link");
	}
	// 同步docMenu.js如下调整：2008.06.17 个人共享，借阅屏蔽掉发送
	if(isShareAndBorrowRoot == 'true'){
		docDisable("sendto");
	}
}

function showOrhidden() {
	var parentLayout = parent.document.getElementById("layout");
	if(parentLayout){
		if(parentLayout.cols == "0,*") {
			parentLayout.cols = "140,*";
			getA8Top().contentFrame.document.getElementById('LeftRightFrameSet').cols = "0,*";
		}
		else {
			parentLayout.cols = "0,*";
			getA8Top().contentFrame.leftFrame.closeLeft();
		}
	}
}



/***********************************begin
 * 属性页相关函数
 */ 
function borrowDeleteUser() {
  var checkedids = document.getElementsByName('borrowid');
  var len = checkedids.length;
  var count = 0;
  for(var i = 0; i < len; i++) {
  		var checkedid = checkedids[i];
		
		if(checkedid && checkedid.checked && checkedid.parentNode.parentNode.tagName == "TR"){			
			checkedid.parentNode.parentNode.parentNode.removeChild(checkedid.parentNode.parentNode);
			originalElementsborrow.remove("" + checkedid.value + "");
			//删除时同时删除缓存的map
			mapdatafield.remove("" + checkedid.value + "");
			i--;
			count++;
		}
	}

   if(count == 0){
   		alert(v3x.getMessage("DocLang.doc_delete_select_alert"));
   }else{
	//alert("doc.js ucf::Prop--" + ucfProp+"ucf::Public--" + ucfPublicShare+"ucf::Personal--" + ucfPersonalShare+"ucf::Borrow--" + ucfBorrow)
	ucfBorrow = true;
   }
}


/**
*属性页相关函数      end
*********************************/
// 弹出文档打开窗口
function docOpenFun(id, name, all, edit, add, readonly, browse,isBorrowOrShare, list, isLink) {
	if((isBorrowOrShare == false || isBorrowOrShare == 'false') && !hasOpenAcl(all,edit,add,readonly,browse,list)){
	return;	
	}
	var exist = true;//判断文件是否存在
	if(isLink == 'true') {
		exist = docExist(id,false);//如果是链接类型，判断源文件是否仍存在
	}
	if(exist == 'false'){
		return; 
	}

//	var surl = jsURL + "?method=docOpenIframe&docResId=" + id  + "&all=" + all 
//		+ "&edit=" + edit + "&add=" + add + "&readonly=" + readonly + "&browse=" + browse +"&isBorrowOrShare=" + isBorrowOrShare
//		+ "&list=" + list + "&docLibId=" + docLibId + "&docLibType=" + docLibType + "&isLink=" + isLink;

	var surl = jsURL + "?method=docOpenIframe&" + document.getElementById(id + "_Url").value;
	var openArgs = {};
	openArgs["url"] = surl;

	if(v3x.getBrowserFlag('openWindow') == false){
		openArgs["dialogType"] = "open";
	}
	if((arguments[10] !=null && arguments[10]==true) && (arguments[11]!=null && arguments[11]!='101' && arguments[11]!='102' && arguments[11]!='120' && arguments[11]!='121')){
		openArgs["workSpaceRight"] = 'yes';
	} else if(!v3x.isIpad){
		openArgs["workSpace"] = 'yes';
	}

	var ret = v3x.openWindow(openArgs);
	if(ret == true || 'true' == ret){	
		try{
			window.location.reload(true);
		}
		catch(e){}
	}
}

/**
 * 打开文档，只有docResourceId作为参数
 */
function openDocOnlyId(id, openFrom) {
	if(window.browse)
		if((window.isShareAndBorrowRoot == 'false' || window.isShareAndBorrowRoot == false) && !hasOpenAcl(window.all,window.edit,window.add,window.readonly,window.browse,window.list))
			return;	
	
	
	// docRes有效性判断
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
		 "docResourceExist", false);
	requestCaller.addParameter(1, "Long", id);
			
	var flag = requestCaller.serviceRequest();
	if(flag == 'false') {
		//alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));
		alert(v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));
		
		window.location.reload(true);
		return ;
	}
				
	var flag = openType(id);
	var first = flag.charAt(0);
	if(first!='l' && first != 'c' ){
		// 归档类型
		var loc = flag.indexOf(',');
		var key = flag.substring(0, loc);
		var srcid = flag.substring(loc + 1, flag.length);
		openPigDetail(key, srcid, id);
	}else if(first != 'c' && flag.indexOf(',') == -1){
		// 源文件不存在的链接类型
		alert(v3x.getMessage('DocLang.doc_source_doc_no_exist'));
		return;
	}else {	
		// 源文件存在的链接
		var docid = id;
		if(first != 'c')
			docid = flag.substring(flag.indexOf(',') + 1, flag.length);
		var str = jsURL + "?method=docOpenIframeOnlyId&docResId=" + docid + "&openFrom=" + openFrom + "&linkId=" + id;
//		alert(str)
		var openArgs = {};
		openArgs["url"] = str;
		openArgs["workSpace"] = "yes";
		openArgs["resizable"] = "false";
		if(v3x.getBrowserFlag('openWindow') == false){
			openArgs["dialogType"] = "open";
		}
		var theInfo = v3x.openWindow(openArgs);
		
		if(theInfo && 'true' == theInfo){
			document.location.reload(true);
		}
	}
	

}

// 得到打开类型
function openType(id) {
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
			 "getTheOpenType", false);
		requestCaller.addParameter(1, "Long", id);
				
		var flag = requestCaller.serviceRequest();
		
		return flag;
	}
	catch (ex1) {
		alert("Exception : " + ex1.message);
	}

}
// 判断文档链接的源文件是否存在
function docExist(id, folderLink) {
    try {
        var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "docResourceExist", false);
        	requestCaller.addParameter(1, "Long", id);

        var flag = requestCaller.serviceRequest();
        if (flag == 'false') {
            if (folderLink) {
                alert(v3x.getMessage('DocLang.doc_source_folder_no_exist'));
            } else {
                alert(v3x.getMessage('DocLang.doc_source_doc_no_exist'));
            }
        }

        return flag;
    }
    catch(ex1) {
        alert("Exception : " + ex1.message);
    }
}
/**
 * 验证是否有查看权限
 */
 function hasOpenAcl(all,edit,add,readonly,browse,list){
 	if('true' == all || 'true' == edit || 'true' == readonly || 'true' == browse || 'true' == add)
 		return true;
 	else{
 		alert(v3x.getMessage('DocLang.doc_open_no_acl_alert'))
 		return false;
 	}
 }

/**
 * 从文档列表中打开文档夹。
 */
function folderOpenFun(id, frType, all, edit, add, readonly, browse, list, isFolderLink) {
	var exist = 'true';
	if(isFolderLink == 'true') {
		exist = docExist(id, true);
	}
	if(exist == 'false')
		return; 
	
	var surl = jsURL + "?method=rightNew&resId=" + id + "&frType=" + frType + "&docLibId=" + docLibId
		+ "&docLibType=" + docLibType + "&isShareAndBorrowRoot=" + isShareAndBorrowRoot + "&all=" + all 
		+ "&edit=" + edit + "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list
		+ "&isFolderLink=" + isFolderLink;
	var query = document.getElementById('method').value == 'advancedQuery';
	if(query) {
		parent.location.href = surl;
	}
	else {
		location.href = surl;
	}
}
function folderOpenFunWithoutAcl(id,frType) {		
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocAclManager", "getAclString", false);
		requestCaller.addParameter(1, "long", id);
	var flag = requestCaller.serviceRequest();
	
	if (frType == "40") {
	    isShareAndBorrowRoot = false;
	    flag = "all=true&edit=true&add=true&readonly=false&browse=false&list=true"
	}
	
	if (frType == "110") {
	    isShareAndBorrowRoot = false;
	}
	
	var surl = jsURL + "?method=rightNew&resId=" + id + "&frType=" + frType + "&docLibId=" + docLibId + "&docLibType=" + docLibType + "&isShareAndBorrowRoot=" + isShareAndBorrowRoot + "&" + flag + "&isFolderLink=false";
	location.href = surl;
}
/**
 * 从主页打开文档夹
 */
function folderOpenFunHomepage(id, frType, all, edit, add, readonly, browse, list, isFolderLink, docLibId, docLibType) {
	var exist;
	if (isFolderLink == 'true') {
	    exist = docExist(id, true);
	}
	if (exist == 'false') 
		return;
	
	var surl = jsURL + "?method=rightNew&resId=" + id + "&frType=" + frType + "&docLibId=" + docLibId + "&docLibType=" + docLibType + "&isShareAndBorrowRoot=false&all=" + all + "&edit=" + edit + "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list + "&isFolderLink=" + isFolderLink;
	var dest = parent.rightFrame;
	if (dest) {
	    parent.rightFrame.location.href = surl;
	}
	else if (parent.parent.rightFrame) {
	    parent.parent.rightFrame.location.href = surl;
	}
	else {
	    window.location = jsURL + "?method=docHomepageIndex&docResId=" + id;
	}
}

/**
 * 从關聯打开文档夹
 */
function folderOpenFunQuote(id,frType,all,edit,add,readonly,browse,list,isFolderLink, docLibId, docLibType) {
	var exist;
	var isQuote = "";
	if(isFolderLink == 'true') {
		exist = docExist(id, true);
	}else{
		isQuote = "true";
	}
	if(exist == 'false')
		return; 
	
	if(parent.listFrame){
		var surl = jsURL + "?method=listDocs4Quote&resId=" + id + "&frType=" + frType + "&docLibId=" + docLibId
			+ "&docLibType=" + docLibType + "&isShareAndBorrowRoot=false&all=" + all 
			+ "&edit=" + edit + "&add=" + add + "&readonly=" + readonly + "&browse=" + browse + "&list=" + list
			+ "&isFolderLink=" + isFolderLink + "&isQuote=" + isQuote;
 		parent.listFrame.location.href = surl;
 	}
}
// 弹出文档修改窗口
function docBodyEdit(id) {
 	
	var returnflag = v3x.openWindow({
		url : jsURL + "?method=editDoc&id=" + id,
		workSpace : 'yes',
		resizable : "false"		
	});
	
	if(returnflag == "true") {				
		window.dialogArguments.window.location.reload(true);	
	}
}


/**
 * 编辑文档(从弹出菜单进入)。
 */
function editDoc(id, filename) {
	if(isOffice2007(filename) && !confirmToOffice2003()) 
		return false;
	
	if(checkLock(id, false) == false) {
		return;
	}
		
	if(isUploadFileMimeType!='0' && isUploadFileMimeType!='101' && isUploadFileMimeType!='102' && isUploadFileMimeType!='120' && isUploadFileMimeType!='121'){
		v3x.openWindow({
			url : jsURL + "?method=editDoc&docResId=" + id + "&docLibType=" + docLibType+"&isUploadFileMimeType=true",
			width:"400",
			height:"350"
			});
	}else{
		v3x.openWindow({
			url : jsURL + "?method=editDoc&docResId=" + id + "&docLibType=" + docLibType,
			workSpace : 'yes',
			resizable : "false"});
	}
	window.location.reload(true);
}
// 归档源存在判断，根据rowId
function pigSourceExistById(rowId,appEnumKey){
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
			 "hasPigeonholeSource", false);
		
		requestCaller.addParameter(1, "Long", rowId);
		requestCaller.addParameter(2, "Integer", appEnumKey);
				
		var flag = requestCaller.serviceRequest();

		
		return flag;
	}
	catch (ex1) {
		//alert("Exception : " + ex1.message);
		return 'false';
	}

}

// 归档源存在的判断
function pigSourceExist(appEnumKey, sourceId){
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
			 "hasPigeonholeSource", false);
		requestCaller.addParameter(1, "Integer", appEnumKey);
		requestCaller.addParameter(2, "Long", sourceId);
				
		var flag = requestCaller.serviceRequest();

		
		return flag;
	}
	catch (ex1) {
		//alert("Exception : " + ex1.message);
		return 'false';
	}
}
// 系统类型的打开
function openPigDetail(appEnumKey, sourceId, docId,openFrom) {
	var acl = [window.all,window.edit,window.add,window.readonly,window.browse,window.list];
	// 包含权限参数
	if(arguments && arguments.length > 4) {
		for(var i = 0; i < acl.length; i ++) {
			acl[i] = arguments[i + 4];
		}
	}
	
	var dialogType = "modal";
	var data = new appEnumData();
	if(window.browse && appEnumKey != data.plan)
	if((isShareAndBorrowRoot == 'false' || isShareAndBorrowRoot == false) && !hasOpenAcl(acl[0],acl[1],acl[2],acl[3],acl[4],acl[5]))
		return;	
	//判断是不是以模态对话框打开
	var isModel = getA8Top().window.dialogArguments;
	// 归档源是否存在的判断
	var existFlag = pigSourceExist(appEnumKey, sourceId);
	if(existFlag == 'false') {
		if(document.getElementById("isalert")){
		   document.getElementById("isalert").value = false;
		   if(!window.confirm(parent.v3x.getMessage('DocLang.doc_not_exist_confirm_delete')))	return;
		   delF(docId, "self", false);
		   return;
		 }else{
		   alert(v3x.getMessage('DocLang.doc_source_doc_no_exist'));
		    return;
	         }
     }
	
	
	// 访问次数
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
			 "accessOneTime", false);
		requestCaller.addParameter(1, "Long", docId);
				
		requestCaller.serviceRequest();
	
	
	_url = "";
	
	if(appEnumKey == data.collaboration || appEnumKey == data.edoc) {
		var ret = "200";
		if(openFrom=="lenPotent") {
			var requestCaller = new XMLHttpRequestCaller(this, "docAclManager", "getEdocBorrowPotent", false);
			requestCaller.addParameter(1, "long", docId);				
			ret = requestCaller.serviceRequest();
		} else {
			if(appEnumKey == data.edoc) {
				var requestCaller = new XMLHttpRequestCaller(this, "docAclManager", "getEdocSharePotent", false);
				requestCaller.addParameter(1, "long", docId);				
				ret = requestCaller.serviceRequest();
			}
			else {
				if(window.all == 'true' || window.edit == 'true' || window.readonly == 'true') {
					ret = "111";
				}
				//TODO 文档夹未共享只读以上权限时，是否也将借阅权限并入？
//				else {
//					var requestCaller = new XMLHttpRequestCaller(this, "docAclManager", "getEdocBorrowPotent", false);
//					requestCaller.addParameter(1, "long", docId);				
//					ret = requestCaller.serviceRequest();
//				}
			}
		}
		
//		var potent = ret.substring(1);
//		if(potent == '00') {
//			alert(v3x.getMessage('DocLang.doc_open_no_acl_alert'));
//	 		return;
//		}
		
		if(appEnumKey == data.collaboration) {
			_url = jsColURL + "?method=detail&from=Done&affairId=" + sourceId + "&type=doc&docId=" + docId + "&lenPotent=" + ret + "&openFrom=" + openFrom;
		}
		else {
			if(openFrom!=null){
				//lijl添加docEntrance参数,用来判断是从文档中心进入到公文的
				_url = jsEdocURL + "?method=edocDetailInDoc&openFrom="+openFrom+"&summaryId=" + sourceId+"&lenPotent="+ret+"&docId="+docId+"&isLibOwner="+isLibOwner+"&docEntrance=false";
		    }
		    else {
		 		_url = jsEdocURL + "?method=edocDetailInDoc&openFrom="+openFrom+"&summaryId=" + sourceId+"&lenPotent="+ret+"&docId="+docId; 
		 	}
		}
	}
	else if(appEnumKey == data.meeting)
		_url = jsMeetingURL + "?method=mydetail&id=" + sourceId+"&fromdoc=1";
		
	else if(appEnumKey == data.plan)
		_url = jsPlanURL + "?method=initDetailHome&editType=doc&id=" + sourceId;
	else if(appEnumKey == data.mail)
		_url = jsMailURL + "?method=showMail&id=" + sourceId;
	else if(appEnumKey == data.inquiry){
		//_url = jsInquiryURL + "?method=pigeonhole_detail&id=" + sourceId;
		if(!isModel)
			dialogType = "open";
		_url = jsInquiryURL + "?method=showInquiryFrame&bid=" + sourceId+"&fromPigeonhole=true";
	}
	else if(appEnumKey == data.news){
		if(!isModel)
			dialogType = "open";
		_url = jsNewsURL + "?method=userView&id=" + sourceId+"&fromPigeonhole=true";
	}
	else if(appEnumKey == data.bulletin){
		if(!isModel)
			dialogType = "open";
		_url = jsBulURL + "?method=userView&id=" + sourceId+"&fromPigeonhole=true";
	}
	else if(appEnumKey == data.info){
		_url = infoURL + "?method=detail&summaryId=" + sourceId + "&affairId=&from=Done&openFrom=doc";
	}
	else if(appEnumKey == data.infoStat){
		_url = infoStatURL + "?method=showCheckResultDetail&id="+sourceId;
	}
	if(!v3x.getBrowserFlag('openWindow')){
		dialogType = "open";
	};
	var rv = v3x.openWindow({
		url: _url,
		workSpace : 'yes',
		resizable : "false",
		dialogType :dialogType
	});
	
	if(rv == true || rv == 'true') {
		try {
			window.location.href = window.location;
		} catch(e) {}
	}
//		parent.location.href = _url;
}

//判断修改订阅的源文件是否有权限
function aclExist(id){
	try{
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocAlertManager",
			 "hasAlert", false);
		requestCaller.addParameter(1, "String", id);
				
		var flag = requestCaller.serviceRequest();
		
		return flag;
	}catch (ex1) {
		alert("Exception : " + ex1.message);
	}
}
 

//////////////////////////////////////////////////
/////////////日志打印//////////////////////////////

function printFileLog(){
	var theName="";
	if(document.getElementById("pagerTd")){
		document.getElementById("pagerTd").style.display="none";
	}
	var theContent=document.getElementById("scrollListDiv").innerHTML;
	var list=new PrintFragment(theName, theContent);
	
	 var klist = new ArrayList();
	 var theCss="/apps_res/doc/css/doc.css";
	 klist.add("/seeyon/common/skin/default/skin.css");
	 klist.add(theCss);
	 
	var tlist = new ArrayList();
	tlist.add(list);
	
	printList(tlist,klist);
	if(document.getElementById("pagerTd")){
		document.getElementById("pagerTd").style.display="block";
	}
}

function printDoc(){
	var bodyType = document.getElementById("docOpenBodyFrame").contentWindow.document.getElementById("bodyTypeInput").value;
	// 在线office或PDF的打印通过调用编辑器组件的打印实现
	if(bodyType == 'OfficeWord' || bodyType == 'OfficeExcel' || bodyType == 'WpsWord' || bodyType == 'WpsExcel'){
		document.getElementById("docOpenBodyFrame").contentWindow.officeEditorFrame.officePrint();
		return;
	}
	if(bodyType == 'Pdf') {
		document.getElementById("docOpenBodyFrame").contentWindow.officeEditorFrame.pdfPrint();
		return;
	}
	
	var titleContent = document.getElementById("docPrintTitle").innerHTML;
	 
	var theBody=v3x.getMessage('DocLang.doc_print_body');
	var bodyContent=document.getElementById("docOpenBodyFrame").contentWindow.document.getElementById("docBody").innerHTML;
	var list = new PrintFragment("", titleContent + bodyContent);
	
	var mainList=new ArrayList();
	mainList.add(list);
	
	var cssList=new ArrayList();
	cssList.add("../../common/RTE/editor/css/fck_editorarea4Show.css");
	
	printList(mainList, cssList);
}
	
function exportExcel(the_flag,theId,name){

	if(the_flag == 'file'){
		var logView=document.getElementById("logView");
		logView.target="theLogIframe";
		logView.action=jsURL+"?method=fileLogToExcel&docResourceId="+theId+"&flag=fileLog&trueName=" + encodeURI(name);
		logView.submit();
	}
	else{
		var folderLog=document.getElementById("folderLog");
		folderLog.target="theLogIframe";
		folderLog.action=jsURL+"?method=fileLogToExcel&docResourceId="+theId+"&flag=folderLog&trueName=" + encodeURI(name);
		folderLog.submit();
	}	
	
}

function exportExcelNew(the_flag,theId,name,isGroupLib){
	var aflag = "folderLog";
	if(the_flag == 'file'){
		aflag = "fileLog";	
	}
	
	theLogIframe.location.href = "/seeyon/doc.do?method=fileLogToExcel&docResourceId="+theId+"&flag="+aflag+"&isGroupLib="+isGroupLib;
                                          
}
	

// 文档提醒设置
function docalert(docResId, isFolder) {
	var msgchk = document.getElementById("check_box_message");

	var message = msgchk.checked;
	mainForm.action = jsURL + "?method=docAlert&docResId=" + docResId + "&isFolder=" + isFolder 
			+ "&message=" + message;
	
	mainForm.submit();
	parent.close();
}

// 进入主页
function homepage() {
	var theParent=parent.parent.rightFrame;
	theParent.document.location.href = jsURL + "?method=homepageView";
}
function validateExistence(docId) {
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "docResourceExist", false);
	requestCaller.addParameter(1, "Long", docId);
			
	var flag = requestCaller.serviceRequest();
	if(flag == 'false') {
		alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));
		window.location.reload(true);
		return false;
	}
	return true;
}

function sendToCollFromMenu() {
	var rowid = getSelectedIds();
	if(rowid == ""){
		alert(v3x.getMessage("DocLang.doc_forward_no_select"));
		return false;
	}
	if(rowid.indexOf(",") != -1)
		return;
//		alert(eval('window'))
	var appEnumKey = eval("window.appEnumKey_" + rowid);
	var pigData = new appEnumData();
	
	if(!validateExistence(rowid)) {
		return;
	}
	
	if(appEnumKey == pigData.doc){
		var surl = jsURL + "?method=sendToColl&docResId=" + rowid + "&docLibId=" + docLibId;
		if(getA8Top().contentFrame.document.getElementById('LeftRightFrameSet').cols == "0,*")		
			getA8Top().contentFrame.leftFrame.closeLeft();
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
			var affairId = eval("window.sourceId_" + rowid);
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
		        height : 430,
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
		
		var mailId = eval("window.sourceId_" + rowid);
		var surl = jsMailURL + "?method=convertToCol&id=" + mailId;
		if(getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*")		
			getA8Top().contentFrame.leftFrame.closeLeft();	
		parent.location.href = surl;	
	}
	
}

function sendToMailFromMenu() {
	var rowid = getSelectedIds();
	if(rowid == ""){
		alert(v3x.getMessage("DocLang.doc_forward_no_select"));
		return false;
	}
	if(rowid.indexOf(",") != -1)
		return;
	var appEnumKey = eval("window.appEnumKey_" + rowid);
	var pigData = new appEnumData();

	if(appEnumKey == pigData.doc){
		var surl = jsURL + "?method=sendToWebMail&docResId=" + rowid + "&docLibId=" + docLibId;
		if(getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*")		
			getA8Top().contentFrame.leftFrame.closeLeft();
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
			var affairId = eval("window.sourceId_" + rowid);
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
			
			if(getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*")		
				getA8Top().contentFrame.leftFrame.closeLeft();
			var surl = jsColURL + "?method=forwordMail&id=" + summaryId;
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
		
		var mailId = eval("window.sourceId_" + rowid);
		var surl = jsMailURL + "?method=autoToMail&id=" + mailId;
		if(getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*")		
			getA8Top().contentFrame.leftFrame.closeLeft();	
		parent.location.href = surl;	
	}
	

}

// 从打开页面发送到协同
function sendToCollFromOpen(id) {	
		// 验证文档是否存在
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
			 "docResourceExist", false);
		requestCaller.addParameter(1, "Long", id);
				
		var existflag = requestCaller.serviceRequest();
		if(existflag == 'false') {
			alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));
			
			window.dialogArguments.parent.location.reload(true);
			parent.close();
			return ;
		}
	
	
			var dialogArguments = getA8Top().window.dialogArguments;
			// 精灵打开
			if(!dialogArguments){
				getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToColl&docResId=" + id + "&docLibId=" + docLibId;
				window.close();				
				return ;
			}
			
			var loc = getA8Top().window.dialogArguments.location.href;

			var pos = loc.indexOf('rightNew');

			if(pos != -1) {//alert('list');alert(parent.parent.parent.window.dialogArguments.top.contentFrame)
				// 列表打开
				
				//parent.parent.parent.window.dialogArguments.top.contentFrame.LeftRightFrameSet.cols = "140,*";
				if(window.dialogArguments.getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*")		
					window.dialogArguments.getA8Top().contentFrame.leftFrame.closeLeft();
				window.dialogArguments.getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToColl&docResId=" + id + "&docLibId=" + docLibId;
				window.close();
			}else if(getA8Top().window.dialogArguments.contentFrame){// alert('portal')	
				// portal 打开
				
				if(window.dialogArguments.getA8Top().contentFrame.LeftRightFrameSet.cols == "140,*"||window.dialogArguments.getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*"){								
				window.dialogArguments.contentFrame.mainFrame.location.href = jsURL + "?method=sendToColl&docResId=" + id + "&docLibId=" + docLibId;
				setTimeout(" getA8Top().window.close()",1000); 
				}else{
					//getA8Top().window.dialogArguments.contentFrame.mainFrame.document.getElementById("refreshFlag").value="false";
					window.dialogArguments.contentFrame.mainFrame.location.href = jsURL + "?method=sendToColl&docResId=" + id + "&docLibId=" + docLibId;
				window.close();

				}   
			} else{
				if(getA8Top().window.dialogArguments.getA8Top().contentFrame){
					var leftRightFrame = getA8Top().window.dialogArguments.getA8Top().contentFrame.document.getElementById("LeftRightFrameSet")
					if(leftRightFrame.cols == "140,*"||leftRightFrame.cols == "0,*"){								
						getA8Top().window.dialogArguments.parent.getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToColl&docResId=" + id + "&docLibId=" + docLibId;
						setTimeout(" getA8Top().window.close()",1000); 
					}else{
						getA8Top().window.dialogArguments.getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToColl&docResId=" + id + "&docLibId=" + docLibId;
						setTimeout("top.close()",1000); 
					}
				}else{
					if(getA8Top().contentFrame.LeftRightFrameSet.cols == "140,*"||getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*"){								
						getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToColl&docResId=" + id + "&docLibId=" + docLibId;
						setTimeout(" getA8Top().window.close()",1000); 
					}else{
						getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToColl&docResId=" + id + "&docLibId=" + docLibId;
						window.close();
					}
				}
			} 

}

// 从打开页面发送到邮件
function sendToMailFromOpen(id) {
			// 验证文档是否存在
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
			 "docResourceExist", false);
		requestCaller.addParameter(1, "Long", id);
				
		var existflag = requestCaller.serviceRequest();
		if(existflag == 'false') {
			alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));
			
			window.dialogArguments.parent.location.reload(true);
			parent.close();
			return ;
		}
	
			var dialogArguments = getA8Top().window.dialogArguments;
			// 精灵打开
			if(!dialogArguments){
				getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToWebMail&docResId=" + id + "&docLibId=" + docLibId;
				window.close();				
				return ;
			}
			var loc = getA8Top().window.dialogArguments.location.href;

			var pos = loc.indexOf('rightNew');

			if(pos != -1) {//alert('list');alert(parent.parent.parent.window.dialogArguments.top.contentFrame)
				// 列表打开
				
				if(window.dialogArguments.getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*")
					window.dialogArguments.getA8Top().contentFrame.leftFrame.closeLeft();
				window.dialogArguments.getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToWebMail&docResId=" + id + "&docLibId=" + docLibId;
				window.close();
			}else if(getA8Top().window.dialogArguments.contentFrame){// alert('portal')	
				// portal 打开
				
				if(window.dialogArguments.getA8Top().contentFrame.LeftRightFrameSet.cols == "140,*"||window.dialogArguments.getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*"){								
				window.dialogArguments.contentFrame.mainFrame.location.href = jsURL + "?method=sendToWebMail&docResId=" + id + "&docLibId=" + docLibId;
				setTimeout(" getA8Top().window.close()",1000); 
				}else{
					//getA8Top().window.dialogArguments.contentFrame.mainFrame.document.getElementById("refreshFlag").value="false";
					getA8Top().window.dialogArguments.contentFrame.mainFrame.location.href = jsURL + "?method=sendToWebMail&docResId=" + id + "&docLibId=" + docLibId;
				window.close();

				}
			} else{
				if(getA8Top().window.dialogArguments.getA8Top().contentFrame){
					var leftRightFrame = getA8Top().window.dialogArguments.getA8Top().contentFrame.document.getElementById("LeftRightFrameSet")
					if(leftRightFrame.cols == "140,*"||leftRightFrame.cols == "0,*"){								
						getA8Top().window.dialogArguments.parent.getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToWebMail&docResId=" + id + "&docLibId=" + docLibId;
						setTimeout(" getA8Top().window.close()",1000); 
					}else{
						getA8Top().window.dialogArguments.getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToWebMail&docResId=" + id + "&docLibId=" + docLibId;
						setTimeout("top.close()", 1000);
					}
				}else{
					if(getA8Top().contentFrame.LeftRightFrameSet.cols == "140,*"||getA8Top().contentFrame.LeftRightFrameSet.cols == "0,*"){								
						getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToWebMail&docResId=" + id + "&docLibId=" + docLibId;
						setTimeout(" getA8Top().window.close()",1000); 
					}else{
						getA8Top().contentFrame.mainFrame.location.href = jsURL + "?method=sendToWebMail&docResId=" + id + "&docLibId=" + docLibId;
						window.close();
					}
				}
			} 

}
	
	function docResourcesOrder(docResId,frType,isNeedSort){
		//alert(docResId+"-"+frType+"-"+docLibId+"-"+docLibType+"-"+shareAndBorrow+"-"+all+"-"+edit+"-"+add+"-"+readonly+"-"+browse+"-"+list);	
		if(isNeedSort == 'false'){//当文档个数不超过2个时
			alert(v3x.getMessage('DocLang.doc_not_need_sort_alert'));
			return;
		}
		var theURL = jsURL + "?method=sortPropertyIframe&resId=" + docResId + "&frType=" + frType;
		//alert(theURL);
		if(v3x.getBrowserFlag('openWindow') == false){
			 var win=v3x.openDialog({
    	 					id:"order",
    						title:"",
    	 					url : theURL,
    	 					width: 650,
					    	height: 650,
					    	//type:'panel',
					    	 //relativeElement:obj,
					    	buttons:[{
					    	 id:'btn1',
					    	 text: "关闭",
					    	 handler: function(){
					    	    win.close();
					    	    window.location.reload(true);
					    	 }
					    	 }]
					    });
		} else {
			var toSort = v3x.openWindow({
				url : theURL,
				width : 650,
				height : 650,
				resizable : "true"
			});
			 if(toSort == 1 )  window.location.reload(true);
		 }
	}

/*--------------------------------------- 关联文档 Start --------------------------------------*/
	
//添加关联文档
function addDocLinks(docLibId, sourceId) {
	//在做保存前，删除得关联文档
	var the_delDocLinkId = document.getElementById("delDocLinkId").value;
	var flag = "edit";
	if(sourceId == '')
		flag = "edit";		
	var theInfo = v3x.openWindow({
		url : jsURL + "?method=docRelAddIfram&sourceId=" + sourceId + "&flag=" + flag +"&deletedId=" + the_delDocLinkId + "&docLibId=" + docLibId,
		width : 800,
		height : 600,
		resizable : "true"
	});
	
	if( theInfo == null || theInfo == "") {
		return ;
	}
	else {
		var addString = "";
		var tempString = "";
		var boolString = theInfo.split(":");
		var bool = boolString[1];				//标志是否原来就有关联文档
		
		var stringArray = boolString[0].split(";");
		
		for(var i = 0; i < stringArray.length; i++) {
			var temp_bool = false;
			var lastString = stringArray[i].split(",");
			if(lastString == null || lastString == "") {
				continue;
			}
			else {
				if(i != stringArray.length - 1) {
					addString += lastString[0];
					addString += ",";
				}
				else {
					addString += lastString[0];
				}
				var _bool = isContain(lastString[0]);
				if( _bool == true) {
					if(document.getElementById("doclink_" + lastString[0]) == null) {
						tempString += "<span id=\"doclink_" + lastString[0] + "\" style=\"\">" ;
						tempString += "<img src=\"/seeyon/apps_res/doc/images/docIcon/"+lastString[2]+"\" />";
						tempString += "<a href=\"#\" onclick=\"openDocOnlyId('"+lastString[0]+"')\">"+lastString[1]+"</a>";
						tempString += "<img src=\"/seeyon/common/images/attachmentICON/delete.gif\" onclick=\"deleteDocLink( 'doclink_"+lastString[0]+ "','"+lastString[1] + "','"+lastString[0]+ "')\"/>";
						tempString += "</span>" ;
					}
					else if(document.getElementById("doclink_" + lastString[0]).style.display == "none") {
						document.getElementById("doclink_" + lastString[0]).style.display = "";
					}
				}

			}
		}
		tempString += document.getElementById("initSpan").innerHTML;
		document.getElementById("delDocLinkId").value = boolString[3];	//最后要删除的ID			
		document.getElementById("sourceId").value = boolString[4];		
		
		//要添加的关联文档ID
		lastAddString(addString);
		//更新关联文档的数量
		var theNumber=getNumber();		//关联文档的数量
		document.getElementById("currentNumber").value = theNumber;
		//设置数量显示
		document.getElementById("allDocLinkNumber").innerHTML = theNumber;
		document.getElementById("doclink").style.display = "";
		document.getElementById("initSpan").innerHTML = tempString;		
	}
}


//删除关联文档
function deleteDocLink(theFlag, theName, theId) {
	
	if(window.confirm(v3x.getMessage('DocLang.doc_rel_delete_confirm') + theName + "'?")) {
		var docLinkId = document.getElementById(theFlag);
		docLinkId.style.display = "none";
		//更改关联文档个数
		var the_allDocLinkNumber = document.getElementById("allDocLinkNumber");	
		var _currentNumber = document.getElementById("currentNumber").value; 	//当前的关联文档个数
		var tempNumber = _currentNumber-1;
		document.getElementById("currentNumber").value = tempNumber;  //更新关联文档个数
		if(tempNumber > 0) {
			the_allDocLinkNumber.innerHTML = tempNumber;
		}
		else {
			the_allDocLinkNumber.innerHTML = "";
			document.getElementById("doclink").style.display = "none";
		}
		
		var theValue = document.getElementById("delDocLinkId").value;
		if(theValue == null || theValue == "") {
			document.getElementById("delDocLinkId").value = theId;
		}
		else {
			theValue += ",";
			theValue += theId;
			document.getElementById("delDocLinkId").value = theValue;		//得到删除了的关联文档的ID值
		}
		
		deleteAddLink(theId);		//更新要保存的关联文档ID

	}
	
}
	
// 判断当前ID是否已经存在于删除得ID中
function isContain(temp_id) {
	var deleteId = document.getElementById("delDocLinkId").value;
	var the_delete = deleteId.split(",");
	for(var i = 0; i < the_delete.length; i++) {
		if(temp_id == the_delete[i]) {
			document.getElementById("doclink_" + the_delete[i]).style.display = "";
			return false;
		}
	}
	return true;
	
}
// 从要添加得关联ID串中，移出不需要添加的ID:the_id
function deleteAddLink(the_id) {
	var theLink = document.getElementById("addDocLink").value;
	var the_link = theLink.split(",");
	var str = "";
	for(var i = 0; i < the_link.length; i++) {
		if(the_link[i] == the_id) {
			continue;
		}
			str += the_link[i];
			str += ",";
	}
	document.getElementById("addDocLink").value = str.substring(0, str.length - 1);
}
function lastAddString(_addString) {
	var temp = document.getElementById("addDocLink").value;
	if(temp == null || temp == "") {
		document.getElementById("addDocLink").value = _addString;
	}
	else {
		var temp_length = temp.split(",");
		var addString_length = _addString.split(",");
		for(var i = 0; i < addString_length.length; i++) {
			var number = 0;
			for(var j = 0; j < temp_length.length; j++) {
				if(addString_length[i] == temp_length[j]) {
					break;		//要添加的ID已经存在，跳出
				}
				else {
					number = number + 1;
				}
			}
			
			// 要添加的ID不存在时
			if(number == temp_length.length) {		
				temp += ",";
				temp += addString_length[i];
				document.getElementById("addDocLink").value = temp;
			}
		}
	}
}
// 获取当前得关联文档数
function getNumber() {
	var addString = document.getElementById("addDocLink").value;
	var addString_len = addString.split(",");
	var number = addString_len.length
	return number;
}
//新建或编辑文档时，设置文档属性界面
function editDocProperties(flag) {
	var surl = jsURL + "?method=editDocPropertiesPage&flag=" + flag;
	var result = v3x.openWindow({
		url : surl,
		width : "500",
		height : "500",
		resizable : "false"
	});
	activeOcx();
	if (result != undefined) {
		document.getElementById("contentDiv").innerHTML = result[0];
		document.getElementById("extendDiv").innerHTML = result[1];
		if(document.getElementById("button1")){
			document.getElementById("button1").disabled = false ;
		}
		
	}
}
/*--------------------------------------- 关联文档 End --------------------------------------*/
var winDocAlert;
// 进入文档提醒页面
function alertview(docResId, isFolder){
	var surl = jsURL + "?method=docAlertView&docResId=" + docResId + "&isFolder=" + isFolder;
	if(v3x.getBrowserFlag('openWindow') == false){
			  winDocAlert=v3x.openDialog({
    	 			id:"alertview",
    				title:"",
    	 			url : surl,
    	 			width: 400,
					height: 270,
					//type:'panel',
					//relativeElement:obj,
					 buttons:[{
			    	 id:'btn1',
			    	 text: v3x.getMessage("collaborationLang.submit"),
			    	 handler: function(){
			    	       winDocAlert.getReturnValue();
					        //win.close();
				     }
			    	            
			    	 }, {
			    	 id:'btn2',
			    	 text: v3x.getMessage("collaborationLang.cancel"),
			    	 handler: function(){
			    	    winDocAlert.close();
			    	 }
			    	 }]
			});
		} else {
			v3x.openWindow({
				url : surl,
				width : "400",
				height : "270",
				resizable : "true"
			});
		}
}
/**
 * 从更多的二级页面返回到知识管理首页
 */
function backToHomepage(){
	document.location.href = jsURL + "?method=homepageView";
}

/*--------------------------------------- 关联文档(采用系统统一的附件式存储) Begin ------------------------------------*/
function quoteDocument(from) {
	var url = jsURL + "?method=list4QuoteFrame";
	if(from)
		url += "&from=" + from;
    var atts = v3x.openWindow({
        url: url,
        height: 600,
        width: 800
    });
    if (atts) {
		deleteAllAttachment(2);
        for (var i = 0; i < atts.length; i++) {
            var att = atts[i]
            //addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description)
            addAttachment(att.type, att.filename, att.mimeType, att.createDate, att.size, att.fileUrl, true, false, att.description, null, att.mimeType + ".gif", att.reference, att.category)
        }
    }
    activeOcx();
}
/*--------------------------------------- 关联文档(采用系统统一的附件式存储) End --------------------------------------*/
function openDetail(subject, _url) {
    _url = jsColURL + "?method=detail&" + _url;
    var rv = v3x.openWindow({
        url: _url,
        workSpace: 'yes'
    });
}
function openDetailURL(_url) {
    var rv = v3x.openWindow({
        url: _url,
        workSpace: 'yes'
    });
}
function enableEle(id){
	try{
		var ele = document.getElementById(id);
		if(ele)
			ele.disabled = false;
	}catch(e){alert("ex: " + e)}
}
function disableEle(id){
	try{
		var ele = document.getElementById(id);
		if(ele)
			ele.disabled = true;
	}catch(e){}
}
/**
 * AJAX记录操作日志
 * fileId 文档的id 
 * logType 操作的类型
 */
function ajaxRecordOptionLog(fileId,logType){
	try{
	  var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "recoidopertionLog", false);
	      requestCaller.addParameter(1 ,"String" ,fileId) ;
	      requestCaller.addParameter(2 ,"String" ,logType) ;
	      if(arguments.length > 2)
	      		requestCaller.addParameter(3 ,"boolean" ,arguments[2]) ;
	      requestCaller.serviceRequest() ;	
	}catch(e){
	}
}
/**
 * 获取当前文档的锁定状态和反馈信息
 */
function getLockMsgAndStatus(docId) {
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "getLockMsgAndStatus", false);
			requestCaller.addParameter(1, "Long", docId);
			if(arguments && arguments.length == 2) {
				requestCaller.addParameter(2, "Long", arguments[1]);
			}
			requestCaller.needCheckLogin = false;
			
		return requestCaller.serviceRequest();
	}
	catch(e) {
		alert(e);
		return [LOCK_MSG_NONE, LockStatus_None];
	}
}
function lockWhenAct(docResId) {
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "docHierarchyManager", "lockWhenAct", false);
			requestCaller.addParameter(1, "Long", docResId);
		requestCaller.serviceRequest();
	}
	catch(e) {
		// -> Ignore
	}
}
function unlockAfterAction(docId) {
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "unLockAfterAct", false);
			requestCaller.addParameter(1, "Long", docId);
			requestCaller.needCheckLogin = false;
		requestCaller.serviceRequest();
	}
	catch(e) {
		// -> Ignore
	}
}
/**
 * 文档历史版本，此方法调用有两个入口：
 * 1.文档右键菜单中的"历史版本"；
 * 2.打开文档之后，顶部的功能菜单中的"历史版本"。
 */
function docHistory(docResId, all, edit, add, readonly, browse, list, isBorrowOrShare, docLibId, docLibType, docResName) {
	var requestCaller = new XMLHttpRequestCaller(this, "docVersionInfoManager", "hasDocVersion", false);
    requestCaller.addParameter(1 ,"Long", docResId) ;
    var has = requestCaller.serviceRequest() ;	

    if(has == "false" || has == false){
    	alert(v3x.getMessage('DocLang.doc_has_no_history_alert'));
    	return;
    }
    
	var url = jsURL + "?method=listAllDocVersionsFrame&docResId=" + docResId + "&all=" + all + "&edit=" + edit + "&add=" + add + 
				"&readonly=" + readonly + "&browse=" + browse + "&list=" + list + "&isBorrowOrShare=" + isBorrowOrShare +
			  	"&docLibId=" + docLibId + "&docLibType=" + docLibType + "&docResName=" + encodeURI(docResName);
	var fromTopMenu = arguments[arguments.length - 1] == "TopMenu";
	var refresh = v3x.openWindow({
		url : url,
		workSpace : 'yes'		
	});
	if(refresh == "true"){
		if(fromTopMenu) {
			getA8Top().window.returnValue = "true";
			getA8Top().window.close();
		} else {
			try{
				window.location.reload(true);
			} catch(e){}
		}
	}		
}
/**
 * 文档(库)列表名称栏处的操作图标，点击之后显示操作菜单
 */
function editImg(id) {
	if(isAdvancedQuery == true || isAdvancedQuery == 'true')
		return;
	
	var menudiv = document.getElementById("_" + id);
	if(menudiv == null){
		return;
	}	
	menudiv.parentNode.style.position = "relative";
	menudiv.className = "editContentOver";
}
/**
 * 隐藏文档(库)列表名称栏处的操作图标
 */
function removeEditImg(id){
	if(isAdvancedQuery == true || isAdvancedQuery == 'true')
		return;
		
	var menudiv = document.getElementById("_" + id);
	if(menudiv == null){
		return;
	}	
	menudiv.parentNode.style.position = "static";
	menudiv.className = "editContent";
}
/**
 * 显示当前位置
 */
function showLocation(itemId) {
	var item = getA8Top().contentFrame.topFrame.findMenuItem(itemId);
	var text = "";
	var locations = [];
	if(item != null){
		locations[locations.length] = item.parentMenu.name
		locations[locations.length] = item.name;
		try{
			var parentMenuId = item.parentMenu.id;
			if(parentMenuId){
				getA8Top().contentFrame.topFrame.showMenuItems(parentMenuId);
			}
		}catch(e){}
	}
	
	if(arguments.length > 1){
		for(var i = 1; i < arguments.length; i++){
			if(arguments[i]){
				locations[locations.length] = arguments[i];
			}
		}
	}
	showLocationText(locations.join(" - "));
}
/**
 * 显示文档右侧列表的当前路径，也可用于其它类似页面结构的当前位置显示
 */
function showLocationText(text){
	var obj = getA8Top().contentFrame.document.all.navigationFrameset;
	if(obj == null){
		return;
	}
	
	obj.rows = "0,*";
	document.getElementById("nowLocation").innerHTML = text;
}
function insertAttachmentAndActiveOcx(){
	insertAttachment();
	activeOcx();
}
/**将焦点设置到office控件上，否则容易出现因为打开模态对话框以后
* office控件焦点丢失不能编辑的问题。
* */
function activeOcx(){
	try{
		activeOfficeOcx();
	}catch(e){
		
	}
}