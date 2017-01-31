function updateSomething(categoryType){
	try{
	if(currentSelected == null)
	{
		alert(_("collaborationLang.templete_alertSelectTemplete"));
		return;
	}
	if(currentSelected == 'category'){ //分类
		if (categoryType == '1' || categoryType == '2' || categoryType == '3' || categoryType == '5'){
			parent.templeteListFrame.updateTemplete(curCategorType);
		}
		else {
			categoryManager('update', curCategorType);
		}
	}
	else if(currentSelected == 'templete'){
		parent.templeteListFrame.updateTemplete(curCategorType);
	}
	}catch(e){}
}

function deleteSomething(categoryType){
	if(currentSelected == null)
	{
		alert(_("collaborationLang.templete_alertSelectTemplete"));
		return;
	}
	if(currentSelected == 'category'){ //分类
		if (categoryType == '1' || categoryType == '2' || categoryType == '3' || categoryType == '5'){
			parent.templeteListFrame.deleteTemplete();
		}
		else {
			categoryManager('delete');
			currentSelected = null;
		}
	}
	else if(currentSelected == 'templete'){
		parent.templeteListFrame.deleteTemplete();
	}
}

function categoryManager(act, categoryType){
	if(!parent.templeteTreeFrame || !parent.templeteTreeFrame.tree){
		return;
	}

	var selected = parent.templeteTreeFrame.tree.getSelected();

	var selectId = "";
	var selectName = "";

	if(selected){
		selectId = selected.businessId;
		selectName = selected.text;
	}
	else{
		parent.templeteTreeFrame.tree.select();
		selectName = parent.templeteTreeFrame.tree.getSelected().text;
	}

	if(selectId=="" && curCategorType != "0")
	{
	  alert(_("collaborationLang.templete_alertSelectTreeNode"));
	  return;
	}

	var result = null;

	if(act == 'new'){
		var result = v3x.openWindow({
			url : templeteURL + "?method=showSystemCategory&act=new&categoryType=" + curCategorType + "&parentId=" + selectId + "&from=" + getParameter("from") + "&parentName=" + encodeURI(selectName),
			width : 400,
			height : 300
		});

		//处理返回值
		if(result){
				var businessId = result[0];
				var text = result[1];
				var parentBusinessId = result[2];
				var sort = result[3];
				var catType= result[4];

				var newParent = parent.templeteTreeFrame.webFXTreeHandler.all[parent.templeteTreeFrame.webFXTreeHandler.getIdByBusinessId(parentBusinessId)];

				if(newParent){
					//parent.templeteTreeFrame.location.href = parent.templeteTreeFrame.location.href;
					parent.templeteTreeFrame.location.reload();
					//newParent.addWebFXTreeItem(businessId, text, sort, "javascript:showTempleteList('" + businessId + "','"+catType+"')");
				}
				else{
					//parent.templeteTreeFrame.location.href = parent.templeteTreeFrame.location.href;
					parent.templeteTreeFrame.location.reload();
				}
			}
	}
	else if(act == 'update'){
		var businessId = selected.businessId;
		if(!businessId || businessId == 0 || businessId == 1 || businessId == 2 || businessId == 3 || businessId == 4 || businessId == 5){
			alert(_("collaborationLang.templete_alertSelectTreeNode"));
			return;
		}
		if(!selected){
			alert(_("collaborationLang.templete_alertSelectTreeNode"));
			return;
		}

		var parentNode = selected.parentNode;
		if(!parentNode){
			alert(_("collaborationLang.templete_alertSelectTreeNode"));
			return;
		}

		var result = v3x.openWindow({
			url : templeteURL + "?method=showSystemCategory&act=update&categoryType=" + curCategorType + "&id=" + selectId + "&from=" + getParameter("from") + "&parentName=" + encodeURI(parentNode.text),
			width : 400,
			height : 300
		});

		//处理返回值
		if(result){
			/**selected.businessId = result[0];
			selected.setText(result[1]);
			var sort = result[3];
			*/
			//parent.templeteTreeFrame.location.href = parent.templeteTreeFrame.location.href;
			parent.templeteTreeFrame.location.reload();
		}
	}
	else if(act == 'delete'){
		var businessId = selected.businessId;

		if(!businessId || businessId == 0 || businessId == 1 || businessId == 2 || businessId == 3 || businessId == 4 || businessId == 5){
			alert(_("collaborationLang.templete_alertSelectTreeNode"));
			return;
		}
		if(deleteFormCategory()){
			if(window.confirm(_("collaborationLang.templete_confirmDeleteTreeNode"))){
				parent.templeteTreeFrame.hiddenIframe.location.href = templeteURL + "?method=deleteCategory&id=" + selectId;
			}
		}
	}
}
function deleteFormCategory(act, categoryType){
	if(!parent.templeteTreeFrame || !parent.templeteTreeFrame.tree){
		 return false;
	}
	var selected = parent.templeteTreeFrame.tree.getSelected();
	var selectId = "";
	var selectName = "";

	if(selected){
		selectId = selected.businessId;
		selectName = selected.text;
	}
	else{
		parent.templeteTreeFrame.tree.select();
		selectName = parent.templeteTreeFrame.tree.getSelected().text;
	}

	if(selectId=="" && curCategorType != "0")
	{
	  alert(_("formLang.formenum_selectAppsort"));
	  return false;
	}
	if(parent.templeteTreeFrame.tree.childNodes.length == 1){
	    //alert(_("collaborationLang.templete_alertSelectTreeNode"));
	    if(parent.templeteTreeFrame.tree.childNodes[0].businessId == selectId){
		     alert(v3x.getMessage("collaborationLang.templete_must_has_one"));
			 return false;
	    }
	}
	 var dataUrl = v3x.baseURL + encodeURI(encodeURI("/formappMgrController.do?method=judgeuerscond&categoryid=" + selectId));
	    var str = init(dataUrl);
	    if(str == "true"){
		  alert(v3x.getMessage("formLang.formcreate_sorttransfer"));
		  return false;
	  }
	 return true;
}
function NewHTTPCall()
{
   var xmlhttp;
   try{
     xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
     return xmlhttp;
   }
   catch (e)
   {
     return null;
   }
}
function init(aUrl)
{
  var httpCall = NewHTTPCall();
  var nowresult;
  if (httpCall ==  null){
    alert(v3x.getMessage("formLang.formdisplay_nonsupportXMLHttp"));
    return null;
  }
      // debugger;
	httpCall.open('GET', aUrl + "&" + Math.random(), false);
	httpCall.onreadystatechange = function() {
  	if (httpCall.readyState != 4)

    	return;
  	nowresult = httpCall.responseText;
  	if (nowresult == "")
  		throw ""+v3x.getMessage("formLang.formdisplay_loaderror")+"";
  };
  httpCall.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8');
  httpCall.send(null);
  return nowresult;
}
function showTempleteList(categoryId,categorType,canManage){
	if(!parent.templeteToolBarFrame){
		return;
	}

	parent.templeteToolBarFrame.curCategorType = categorType;
	parent.templeteToolBarFrame.currentSelected = 'category';
	//增加 判断是否是管理员指派的 如果是就不能删除 dongyj
	var barDelete = parent.templeteToolBarFrame.document.getElementById('deleteCategory');
	var barUpdate = parent.templeteToolBarFrame.document.getElementById('updateCategory');
	if(canManage != null){
		if(barDelete){
			if(canManage == '1'){
				 barDelete.disabled='disabled';
				 barUpdate.disabled='disabled';
			}
		}
	}else{
		if(barDelete){
			barDelete.disabled='';
			barUpdate.disabled='';
		}
	}
	// end 增加
	if(categoryId != null && parent.templeteListFrame){
		parent.templeteListFrame.location.href = templeteURL + "?method=systemList&categoryId=" + categoryId + "&categoryType=" + categorType + "&from=" + getParameter("from");
	}
}

function focusTempleteList(){
	parent.templeteToolBarFrame.currentSelected = 'templete';
	parent.templeteToolBarFrame.document.getElementById('deleteCategory').disabled='';
	parent.templeteToolBarFrame.document.getElementById('updateCategory').disabled='';
}

function newTemplete(categoryType,category){
	var selected = parent.templeteTreeFrame.tree.getSelected();
	var selectId = 0;
	if(selected && selected.businessId){
		selectId = selected.businessId;
	}
	var url="";
	if(curCategorType=="0")
	{
	  if(parent.templeteTreeFrame.canNew == 'false'){
	  	alert(_("collaborationLang.templete_alertNewTemplete"));
	  	return false;
	  }
	  url=templeteURL;
	}
	else
	{
	  url=edocTempleteURL;
	}

	if(categoryType == 1){ //公文
		if(category!=null && category!=""){
			curCategorType = category;
		}else{
			categoryType = selected.categoryType;
			if(!categoryType){
				categoryType = 2;
			}
		}
	}

	url+="?method=systemNewTemplete&categoryId=" + selectId + "&categoryType=" + curCategorType + "&from=" + getParameter("from");
	parent.location.href = url;
}

function updateTemplete(categoryType){
	var num = validateCheckbox("id");
	if(num == 0){
		alert(_("collaborationLang.templete_alertSelectTemplete"));
		return;
	}
	if(num > 1){
		alert(_("collaborationLang.templete_alertSelectOneTemplete"));
		return;
	}

	var templeteId = getCheckboxSingleValue("id");

	editTemplete(templeteId, categoryType, getParameter("from"));
}

function editTemplete(templeteId, categoryType, from){
    var url="";
	if(categoryType=="0"){url=templeteURL;}
	else{url=edocTempleteURL;}
	parent.location.href = url + "?method=systemNewTemplete&templeteId=" + templeteId + "&categoryType=" + categoryType + "&from=" + from;
}

function showSystemDetail(templeteId,categoryType){
	focusTempleteList();
    var url="";
	if(categoryType=="0"){url=templeteURL;}
	else{url=edocTempleteURL;}
	parent.templeteDetailFrame.location.href = url + "?method=systemDetail&templeteId=" + templeteId;
}

/**
 * 保存模板
 */
function saveTemplete() {
    var theForm = document.getElementsByName("sendForm")[0];

    if(!notSpecChar(theForm.subject)) {
    	return;
    }
    
    _type = theForm.type.value;

    var from = getParameter("from");
    if(from == "TM" && !theForm.categoryId.value){
    	alert(_('collaborationLang.templete_alertNoCategorys'));
    	return;
    }
    var categoryId = document.getElementById("categoryId");
    if(categoryId.selectedIndex == -1){
    	alert(v3x.getMessage("collaborationLang.templete_alertNoCategorys"));
		return false;
    }
    categoryId = categoryId.options[categoryId.selectedIndex];
    
    //2017-01-11 诚佰公司 添加密级空值校验
    if (checkForm(theForm) && checkSelectSecret() && checkRepeatTempleteSubject(theForm, true) && checkTemplateCategory(categoryId.value,trim(categoryId.text))) {
    	if(_type != 'text' && !checkSelectWF1()){ //协同正文，不用流程
    		return;
    	}
    	/*if(_type == 'text'){
    		var bodyType = document.getElementById("bodyType").value;
	        if (bodyType != "HTML") {
	            alert(_('collaborationLang.collaboration_alertSaveTemplete'));
	            return;
	        }
    	}*/
        if (_type != "workflow") { //流程模板，不保存office正文
        	if(!saveOffice()){
            	return;
        	}
        }
		if(branchs){
			for(var i=0,j=keys.length;i<j;i++){
				var branch = branchs[keys[i]];
				if(branch!=null){
					var str = "<input type=\"hidden\" name=\"branchs\" value=\""+keys[i]+"↗"+branch.id
					+"↗"+branch.conditionType+"↗"+branch.formCondition+"↗"+branch.conditionTitle+"↗"+branch.isForce+"↗"+(branch.conditionDesc?branch.conditionDesc.escapeQuot():"")
					+"↗"+branch.conditionBase+"\">";
					branchDiv.innerHTML += str;
				}
			}
		}

		/*if(nodes != null && nodes.length>0){
        	var hidden;
        	for(var i=0;i<nodes.length;i++){
		        hidden = document.createElement('<INPUT TYPE="hidden" name="policys" value="' + policys[nodes[i]].name + '" />');
		        theForm.appendChild(hidden);
		        hidden = document.createElement('<INPUT TYPE="hidden" name="itemNames" value="' + policys[nodes[i]].value + '" />');
		        theForm.appendChild(hidden);
	        }
        }*/

        //节点权限引用更新
		var policyArr = new Array();
	    var itemNameArr = new Array();
	    if(nodes != null && nodes.length>0){
	    	for(var i=0;i<nodes.length;i++){
	    		var policyName = policys[nodes[i]].name;
	    		var itemName = policys[nodes[i]].value;
	    		policyArr.push(policyName);
	    		itemNameArr.push(itemName);
	    	}
	    }
		try {
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkNodePolicy", false);
			requestCaller.addParameter(1, "String[]", policyArr);
			requestCaller.addParameter(2, "String[]", itemNameArr);
			requestCaller.addParameter(3, "String", theForm.loginAccountId.value);
			var rs = requestCaller.serviceRequest();
			if(rs == "1"){
				alert(_("collaborationLang.node_policy_not_existence"));
				enableButtons();
				return;
			}
		}
		catch (ex1) {
			alert("Exception : " + ex1);
			return;
		}

        saveAttachment();

        disableButtons();

		isFormSumit = true;

        theForm.target = "_self";
        theForm.setAttribute("method", "post");
        theForm.submit();

        getA8Top().startProc('');
    }
}

function checkSelectWF1() {
    if (!hasWorkflow) {
        alert(v3x.getMessage("collaborationLang.collaboration_selectWorkflow"));

        designWorkFlow('detailIframe');

        return false;
    }

    return true;
}

function doAuth(elements){
	if(elements){
		document.getElementById("auth").value = getIdsString(elements);
	}
}

function doAuth4Category(elements){
	if(elements){
		document.getElementById("auth").value = getIdsString(elements);
		document.getElementById("authStr").value = getNamesString(elements);
	}
}

function showTempleteDetail(id, type, categoryType) {
	//校验模板是否存在
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxTempleteManager", "checkTempleteIsExist", false);
	requestCaller.addParameter(1, "long", id);
	requestCaller.needCheckLogin = true;
	var result = requestCaller.serviceRequest();
	if(result == "false"){
		alert(_("collaborationLang.templete_alertNotExist"));
		return;
	}
	var url = templeteURL;
	if(categoryType==32){//信息报送
		url = "infoTempleteControll.do";
	} else if(categoryType!="0" && categoryType!="4"){
		url = "edocTempleteController.do";
	}
	
	parent.templeteDetailFrame.location.href = url + "?method=detail&id=" + id + "&type=" + type;
    
}

var intervalObj = null;

function refTemplete() {
	var id = getParameter("id");
	var type = getParameter("type");

	if(!id || !type){
		alert(_("collaborationLang.templete_alertSelectTemplete"));
		return;
	}

    var _parent = top.window.opener;

    if (top.window.dialogArguments) {
        _parent = top.window.dialogArguments;
    }

    //if (type == 'templete' || type == 'workflow') {
    	_parent.isFormSumit = true;
    	var from ='';
    	var projectId=''
    	try
    	{
    		from = _parent.document.getElementById('from').value;
    		projectId =  _parent.document.getElementById('projectId').value;
    	}
    	catch(e){}
        _parent.location.href = collaborationURL + "?method=newColl&templeteId=" + id +'&from='+from+"&projectId="+projectId;
        top.window.close();
    /*}else {
        if (parent.templeteDetailFrame.document.readyState == 'complete') {
            if (intervalObj) {
                window.clearInterval("intervalObj");
            }

            parent.templeteDetailFrame.refTempleteContent(type, id);
            top.window.close();
        }
        else {
            intervalObj = window.setInterval("refTemplete('" + id + "', '" + type + "')");
        }
    }*/
}
function refTempleteIpad() {
	var id = getParameter("id");
	var type = getParameter("type");

	if(!id || !type){
		alert(_("collaborationLang.templete_alertSelectTemplete"));
		return;
	}

    var _parent = window.parent.parent.parent;

    //if (type == 'templete' || type == 'workflow') {
    	_parent.isFormSumit = true;
    	var from ='';
    	var projectId=''
    	try
    	{
    		from = _parent.document.getElementById('from').value;
    		projectId =  _parent.document.getElementById('projectId').value;
    	}
    	catch(e){}
        _parent.location.href = collaborationURL + "?method=newColl&templeteId=" + id +'&from='+from+"&projectId="+projectId;
        return null;
        /*}else {
        if (parent.templeteDetailFrame.document.readyState == 'complete') {
            if (intervalObj) {
                window.clearInterval("intervalObj");
            }
            return parent.templeteDetailFrame.refTempleteContentIpad(type, id);

        }
        else {
            intervalObj = window.setInterval("refTempleteIpad('" + id + "', '" + type + "')");
        }
    }*/
}
function refTempleteContent(type, id) {
    var rv = [];
    //不是纯文本，需要流程
    if (type != 'text') {
        rv[0] = caseProcessXML;
        rv[1] = workflowInfo;
        rv[2] = subject;
        rv[3] = id;
        rv[4] = workflowRuleStr;
    }

    //不是纯流程，需要正文
    if (type != 'workflow') {
    	if(bodyType == "HTML"){
        	rv[5] = {
        		bodyType : bodyType,
        		content : document.getElementById("templeteContentText").innerHTML,
        		subject : subject
        	};
    	}
    	else if(bodyType == "OfficeWord" || bodyType == "OfficeExcel" || bodyType == "WpsWord" || bodyType == "WpsExcel"){
    		rv[5] = {
    			bodyType : bodyType,
    			fileType : fileType,
    			fileId : fileId,
    			createDate : createDate,
    			newOfficeFileId : newOfficeFileId,
    			subject : subject
    		};
    	}
    }

    top.window.returnValue = rv;
    top.close();
}
function refTempleteContentIpad(type, id) {
    var rv = [];
    //不是纯文本，需要流程
    if (type != 'text') {
        rv[0] = caseProcessXML;
        rv[1] = workflowInfo;
        rv[2] = subject;
        rv[3] = id;
        rv[4] = workflowRuleStr;
    }

    //不是纯流程，需要正文
    if (type != 'workflow') {
    	if(bodyType == "HTML"){
        	rv[5] = {
        		bodyType : bodyType,
        		content : document.getElementById("templeteContentText").innerHTML,
        		subject : subject
        	};
    	}
    	else if(bodyType == "OfficeWord" || bodyType == "OfficeExcel" || bodyType == "WpsWord" || bodyType == "WpsExcel"){
    		rv[5] = {
    			bodyType : bodyType,
    			fileType : fileType,
    			fileId : fileId,
    			createDate : createDate,
    			newOfficeFileId : newOfficeFileId,
    			subject : subject
    		};
    	}
    }

    return rv;
}

function deleteTemplete(){
	var id_checkbox = document.getElementsByName('id');
    if (!id_checkbox) {
        return 0;
    }

    var hasSelected = false;
    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
        	hasSelected = true;
        	break;
        }
    }

	if(!hasSelected){
		alert(_("collaborationLang.templete_alertSelectTemplete"));
		return;
	}

	if(window.confirm(_("collaborationLang.templete_confirmDeleteTemplete"))){
		var listForm = document.getElementsByName("listForm")[0];
		if(listForm){
			listForm.action = templeteURL + "?method=deleteTemplete";
			listForm.method = "post";
			listForm.target = "_self";
			listForm.submit();
		}
	}
}

function authTemplete(){
	var num = validateCheckbox("id");
	if(num == 0){
		alert(_("collaborationLang.templete_alertSelectTemplete"));
		return;
	}
	else if(num == 1){
		var o = getCheckboxSingleObject("id");
		var authInfo = o.getAttribute("authInfo");

		elements_auth = parseElements(authInfo);
	}
	else{
		elements_auth = null;
	}
	var listForm = document.getElementsByName("listForm")[0];
	var catType=listForm.categoryType.value;
	if(catType=="0")
	{//公文授权只能选择本单位
		onlyLoginAccount_auth=false;
	}else{//公文
		//格式模板和流程模板只能授权给本单位，公文模板可以跨单位授权
		var isOnlySelf = false;
		var checkBox = document.getElementsByName("id");
		var text = false ;
		var workflow =false ;
		var temp = false ;
		for(var i = 0; i<checkBox.length;i++){
			if(checkBox[i].checked){
				if (checkBox[i].templeteType == 'text') 
					text = true ; 		// 格式模板
				if (checkBox[i].templeteType == 'workflow')
					workflow = true ; 	// 流程模板
				if (checkBox[i].templeteType == 'templete')
					temp = true ; 		// 发文模板
			}
		}
		if (temp) {
			if (workflow || text) {
				alert(_("collaborationLang.templete_alertTempleteAuthOnlyChoose"));
				return ;
			} else {
				onlyLoginAccount_auth=false; 	// 是否可以切换单位
				showAccountPanel_auth = true;	// 是否显示选人界面页签
			}
		} else {
			alert(_("collaborationLang.templete_alertTempleteAuthOnly"));
			onlyLoginAccount_auth=true;
			showAccountPanel_auth = false;
		}
	}
	selectPeopleFun_auth();
}

function doAuthTemplete(elements){
	if(elements){
		document.getElementById("auth").value = getIdsString(elements);

		var listForm = parent.templeteListFrame.document.getElementsByName("listForm")[0];
		if(listForm){
			listForm.action = templeteURL + "?method=doAuthTemplete";
			listForm.method = "post";
			listForm.target = "_self";
			listForm.submit();
		}
	}
}

function moveTemplete(){
	var num = validateCheckbox("id");
	if(num == 0){
		alert(_("collaborationLang.templete_alertSelectTemplete"));
		return;
	}

	var newCategoryId = v3x.openWindow({
		url : genericControllerURL + 'collaboration/templete/systemMoveTemplete&categoryType=' + getParameter('categoryType') + "&from=" + getParameter('from')+"&move=true",
		width : 400,
		height : 500
	});

	if(newCategoryId != null){
		var listForm = parent.templeteListFrame.document.getElementsByName("listForm")[0];
		if(listForm){
			listForm.action = templeteURL + "?method=doMoveTemplete&newCategoryId=" + newCategoryId;
			listForm.method = "post";
			listForm.target = "_self";
			listForm.submit();
		}
	}
}

function checkAuthInCategory(){
	var obj = document.getElementById("parentId");

	var allowAuth =  false;
	if((obj.value == "0" || obj.value == "4") && getParameter("from") != "TM"){
		allowAuth =  false;
	}
	else{
		allowAuth =  true;
	}

	document.getElementById("auth").disabled = allowAuth;
	var authStrObj = document.getElementById("authStr");
	if(authStrObj){
		authStrObj.disabled = allowAuth;

		if(allowAuth){
			authStrObj.value = _("collaborationLang.templete_categoryDisabledTip")
			authStrObj.title = _("collaborationLang.templete_categoryDisabledTip");
		}else{
			authStrObj.value = authStrObj.getAttribute("defaultValue");
			authStrObj.title = authStrObj.getAttribute("defaultValue");
		}
	}
}

function doInvalidateTemplete(state){
	var num = validateCheckbox("id");
	if(num == 0){
		alert(_("collaborationLang.templete_alertSelectTemplete"));
		return;
	}

	var listForm = parent.templeteListFrame.document.getElementsByName("listForm")[0];
	if(listForm){
		listForm.action = templeteURL + "?method=doInvalidateTemplete&state=" + state;
		listForm.method = "post";
		listForm.target = "_self";
		listForm.submit();
	}

}

/**
 * 检测分类是否重名
 */
function checkRepeatCategoryName(form1){
	var parentId = form1.elements['parentId'].value;
	var name = form1.elements['name'].value;
	var id = form1.elements['id'].value;

	var isUpdate = (getParameter("act") == "update");

	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxTempleteCategoryManager", "checkName", false);
		requestCaller.addParameter(1, "Long", parentId);
		requestCaller.addParameter(2, "String", name);

		var idList = requestCaller.serviceRequest();
		if(!idList){
			return true;
		}

		var count = idList.length;

		if(count < 1) return true;

		if(isUpdate == true && count == 1 && id == idList[0]){ //修改，存在的数据就是它自己
			return true;
		}

		alert(_("collaborationLang.templete_alertRepeatCategoryName"));
		return false;
	}
	catch (ex1) {
		alert("Exception : " + ex1.message);
	}

	return true;
}

//2017-01-11 诚佰公司 发送表单验证流程密级是否为空
function checkSelectSecret() {
    if (flowSecretLevel_wf == null || flowSecretLevel_wf == "") {
    	alert("流程密级不能为空。");
        return false;
    }

    return true;
}
// 诚佰公司

/**
 * 检测模板标题是否重名
 */
function checkRepeatTempleteSubject(form1){
	var categoryIdObj = form1.elements['categoryId'];
	var categoryId = categoryIdObj == null ? "" : categoryIdObj.value;
	var subject = form1.elements['subject'].value;
	var id = form1.elements['id'].value;

	var isUpdate = (id != null) && (id != "");

	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxTempleteManager", "checkSubject4System", false);
		requestCaller.addParameter(1, "Long", categoryId);
		requestCaller.addParameter(2, "String", subject);

		var idList = requestCaller.serviceRequest();
		if(!idList){
			return true;
		}

		var count = idList.length;

		if(count < 1) return true;

		if(isUpdate == true && count == 1 && id == idList[0]){ //修改，存在的数据就是它自己
			return true;
		}

		if(confirm(_("collaborationLang.templete_alertRepeatSubject", subject))){
			form1.elements['id'].value = idList[0];
			return true;
		}
		return false;
	}
	catch (ex1) {
		alert("Exception : " + ex1.message);
	}

	return true;
}

function endDeleteCategory(flag){
	if(flag == true){
		document.location.href = document.location.href;
		parent.templeteTreeFrame.location.reload();
		//tree.getSelected().remove();
	}
	else{
		alert(_("collaborationLang.templete_alertDeleteFaild"));
	}
}




/**
 *
 */
 function openTempleteConfigDlg(){
 	var resultList = v3x.openWindow({
        url : genericControllerURL + "collaboration/templete/templeteConfigIndex&categoryType=0,1,2,3,4,5",
        height : "600",
        width : "800"
    });
    if(!resultList)return;
    var templeteIds = resultList[0];
    var templeteTypes = resultList[1];
	var theForm = document.getElementById("listForm");
	for(var i=0; i<templeteIds.length; i++){
	   var element_id = document.createElement("<INPUT TYPE='HIDDEN' NAME='templeteIds' value='" + templeteIds[i] + "' />");
	   var element_type = document.createElement("<INPUT TYPE='HIDDEN' NAME='templeteTypes' value='" + templeteTypes[i] + "' />");
	   theForm.appendChild(element_id);
	   theForm.appendChild(element_type);
	}
	theForm.action = templeteURL +"?method=updateTemplateConfig";
	theForm.target = "_self";
	theForm.method = "post";
    theForm.submit();
 }

 /**
 *
 */
 function openTempleteType(fromPage){
 	var resultList = v3x.openWindow({
        url : genericControllerURL + "collaboration/templete/templeteConfigIndex&categoryType=0,1,2,3,4,5&from="+fromPage,
        height : "600",
        width : "800"
    });

    if(!resultList)return;
    var templeteIds = resultList[0];
    var templeteTypes = resultList[1];
	var templeteSubjects = resultList[2];
	var templeteIdsStr = "";
	var templeteSubjectsStr = "";
	for(var i in templeteIds){
		if(i == (templeteIds.length-1)){
			templeteIdsStr += templeteIds[i];
			templeteSubjectsStr += templeteSubjects[i];
		}else{
			templeteIdsStr += templeteIds[i] + "@";
			templeteSubjectsStr += templeteSubjects[i] + "、";
		}
	}

	document.getElementById("operationTypeTitle").value = templeteSubjectsStr;
    document.getElementById("operationTypeValue").value = templeteIdsStr;
 }


/**
 * 取消发布到首页
 */
function cancelPush()
{
	    var id_checkbox = document.getElementsByName("configIds");

	    if (!id_checkbox) {
	        return false;
	    }
	    var theForm = document.getElementById("listForm");
	    if (!theForm) {
	        return false;
	    }
	    var selectedCount = 0;
	    var len = id_checkbox.length;
	    for (var i = 0; i < len; i++) {
	        if (!id_checkbox[i].checked) continue;
	        else{
	            var selectId = id_checkbox[i].getAttribute("extAttribute");
	            //var element = document.createElement("<INPUT TYPE=HIDDEN NAME=configIds value='" + selectId + "' />");
	            var element = document.createElement("input");
	            element.setAttribute("type","hidden");
	            element.setAttribute("name","configIds");
	            element.setAttribute("value",selectId);
	            theForm.appendChild(element);
	            selectedCount++;
	        }
	    }
	    if (selectedCount == 0) {
	        alert(v3x.getMessage("collaborationLang.templete_alertSelectTemplete"));
	        return false;
	    }else{
	        theForm.action = templeteURL +"?method=cancelPush";
	        theForm.target = "_self";
	        theForm.setAttribute("method", "post");
            theForm.submit();
	    }
}

/**
 * 模板排序设置
 */
function sortSetting()
{
    var idsList = v3x.openWindow({
			url : templeteURL + "?method=showTempleteSort",
			width : 320,
			height : 370,
			scrollbars:"no"
	});
	if(!idsList)return;
	var theForm = document.forms[0];
	for(var i=0; i<idsList.length; i++){
	   //var element = document.createElement("<INPUT TYPE='HIDDEN' NAME='sortConfigIds' value='" + idsList[i] + "' />");
	   var element = document.createElement("input");
	   element.setAttribute('type','hidden');
	   element.setAttribute('name','sortConfigIds');
	   element.setAttribute('value',idsList[i]);
	   theForm.appendChild(element);
	}
	theForm.action = templeteURL +"?method=updateTempleteSort";
	theForm.target = "_self";
	theForm.setAttribute('method','post');
	//theForm.method = "post";
    theForm.submit();
}

/**
 * 排序设置　点击确定
 */
function sortSettingOk(){
	 var oSelect = document.getElementById("templeteSelect");
	 if(!oSelect) return false;
	 var ids = [];
	 for(var selIndex=0; selIndex<oSelect.options.length; selIndex++)
     {
        ids[selIndex] = oSelect.options[selIndex].value;
     }
	 window.returnValue = ids;
	 window.close();
}

/**
 * select使选中的项目上移
 */
function moveUp()
{
    var oSelect = document.getElementById("templeteSelect");
    if(!oSelect || oSelect.options.length<=1) return;
    //如果是多选------------------------------------------------------------------
    if(oSelect.multiple)
    {
        for(var selIndex=0; selIndex<oSelect.options.length; selIndex++)
        {
            if(oSelect.options[selIndex].selected)
                {
                    if(selIndex > 0)
                    {
                        if(!oSelect.options[selIndex - 1].selected){
                        	var textTemp = oSelect.options[selIndex-1].text;
                        	var valueTemp = oSelect.options[selIndex-1].value;
                        	oSelect.options[selIndex-1].text = oSelect.options[selIndex].text;
                        	oSelect.options[selIndex-1].value = oSelect.options[selIndex].value;
                        	oSelect.options[selIndex].text = textTemp;
                        	oSelect.options[selIndex].value = valueTemp;
                        	oSelect.options[selIndex-1].selected = true;
                        	oSelect.options[selIndex].selected = false;
                        	//oSelect.options[selIndex].swapNode(oSelect.options[selIndex - 1]);
                        }
                    }
                }
        }
    }
    //如果是单选--------------------------------------------------------------------
    else
    {
        var selIndex = oSelect.selectedIndex;
        if(selIndex <= 0)return;
        oSelect.options[selIndex].swapNode(oSelect.options[selIndex - 1]);
    }
    //使submit按钮有效
    document.all.submitButton.disabled = false;
}


/**
 * select使选中的项目下移
 */
function moveDown()
{
	var oSelect = document.getElementById("templeteSelect");
	if(!oSelect || oSelect.options.length<=1) return;
    var selLength = oSelect.options.length - 1;
    //如果是多选------------------------------------------------------------------
    if(oSelect.multiple)
    {
        for(var selIndex=oSelect.options.length - 1; selIndex>= 0; selIndex--)
        {
           if(oSelect.options[selIndex].selected)
                {
                    if(selIndex < selLength)
                    {
                        if(!oSelect.options[selIndex + 1].selected){
                        	var textTemp = oSelect.options[selIndex+1].text;
                        	var valueTemp = oSelect.options[selIndex+1].value;
                        	oSelect.options[selIndex+1].text = oSelect.options[selIndex].text;
                        	oSelect.options[selIndex+1].value = oSelect.options[selIndex].value;
                        	oSelect.options[selIndex].text = textTemp;
                        	oSelect.options[selIndex].value = valueTemp;
                        	oSelect.options[selIndex+1].selected = true;
                        	oSelect.options[selIndex].selected = false;
                            //oSelect.options[selIndex].swapNode(oSelect.options[selIndex + 1]);
                        }
                    }
                }
        }
    }
    //如果是单选--------------------------------------------------------------------
    else
    {
        var selIndex = oSelect.selectedIndex;
        if(selIndex >= selLength - 1)
            return;
        oSelect.options[selIndex].swapNode(oSelect.options[selIndex + 1]);
    }

    //使submit按钮有效
    document.all.submitButton.disabled = false;
}

function setNoCheckedAndDisabled(id){
	var obj = document.getElementById(id);
	obj.setAttribute("checked",false);
	obj.setAttribute("disabled",true);
}

function setCheckedAndEnabled(id){
	var obj = document.getElementById(id);
	obj.setAttribute("disabled",false);
	obj.setAttribute("checked",true);
}

function preAlertChangeType(obj){
	if (obj) {
		if (obj.value=="workflow") {
			setCheckedAndEnabled("allow_chanage_flow");
//			setNoCheckedAndDisabled("allow_transmit");
//			setNoCheckedAndDisabled("allow_edit");
//			setNoCheckedAndDisabled("allow_edit_attachment");
//			setNoCheckedAndDisabled("allow_pipeonhole");
			setCheckedAndEnabled("allow_transmit");
			setCheckedAndEnabled("allow_edit");
			setCheckedAndEnabled("allow_edit_attachment");
			setCheckedAndEnabled("allow_pipeonhole");
		} else if (obj.value=="text") {
			setNoCheckedAndDisabled("allow_chanage_flow");
			setCheckedAndEnabled("allow_transmit");
			setCheckedAndEnabled("allow_edit");
			setCheckedAndEnabled("allow_edit_attachment");
			setCheckedAndEnabled("allow_pipeonhole");
		} else if (obj.value=="templete"){
			setCheckedAndEnabled("allow_chanage_flow");
			setCheckedAndEnabled("allow_transmit");
			setCheckedAndEnabled("allow_edit");
			setCheckedAndEnabled("allow_edit_attachment");
			setCheckedAndEnabled("allow_pipeonhole");
		}
		alertChangeType(obj);
	}
}

function alertChangeType(obj){
	var opt = obj.options[obj.selectedIndex];
	alert(_("collaborationLang.templete_alertChangeType_" + opt.value, opt.text))
	//非协同模板，不让选择预归档
	var colPigeonhole = document.getElementById("colPigeonhole");
	var project = document.getElementById("projectId");
	if(opt.value != "templete"){
		colPigeonhole.options[0].selected="selected";
		project.options[0].selected="selected";
		colPigeonhole.disabled=true;
		project.disabled=true;
	}else{
		colPigeonhole.disabled=false;
		project.disabled=false;
	}
}

function checkTMCategory(form1){
	var _ParentId = form1.parentId.value;

	if(_ParentId == orginalParentId){
		return true;
	}

	if(_ParentId == "0" && from == "TM"){
		alert(_("collaborationLang.templete_alertCheckTMCategory"));
		return false;
	}


	return true;
}

function openSuperviseWindowForTemplate(){
		var mId = document.getElementById("supervisorId");
		var sDate = document.getElementById("awakeDate");
		var sNames = document.getElementById("supervisors");
		var title = document.getElementById("superviseTitle");
		var role = document.getElementById("superviseRole");
		//只在表单绑定中使用
		var supervisorNames = document.getElementById("supervisorNames");
		//var canModify = document.getElementById("canModifyAwake");
		var urlStr = colSuperviseURL + "?method=superviseWindowForTemplate";
		//if((mId.value != null && mId.value != "") || (role.value!=null && role.value!= "")){
			urlStr += "&supervisorId=" + mId.value + "&supervisors=" + encodeURIComponent(sNames.value)
			+ "&superviseTitle=" + encodeURIComponent(title.value) + "&awakeDate=" + sDate.value + "&role=" + role.value;
		//}
			var secretLevel = document.getElementById("secretLevel").value;
			urlStr += "&secretLevel=" + secretLevel;
        var rv = v3x.openWindow({
	        url: urlStr,
	        height: 320,
	        width: 390
     	});

    	if(rv!=null && rv!="undefined"){
    		var sv = rv.split("|");
    		if(sv.length == 6){
				mId.value = sv[0]; //督办人的ID(添加标识的，为的是向后台传送)
				sDate.value = sv[1]; //督办时间
				if(sv[0]!="")
					sNames.value = sv[2]; //督办人的姓名
				else
					sNames.value = "";
				title.value = sv[3];
				role.value = sv[4];
				if(mId.value == "" && role.value == ""){
					sDate.value = "";
					title.value = "";
				}
				if(supervisorNames)
					supervisorNames.value = (sv[0]==""?"":sv[2]) + ((sv[0]!="" && sv[5]!="")?"、":"") + sv[5];
			}
    	}
}

function checkTemplateCategory(categoryId,categoryName){
	if(!categoryId)
		return true;
	try{
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxTempleteCategoryManager", "exist", false);
		requestCaller.addParameter(1, "Long", categoryId);
		var result = requestCaller.serviceRequest();
		if(result=="false"){
			alert(v3x.getMessage("collaborationLang.templete_alertNoCategory",categoryName));
			return false;
		}
		return true;
	}catch(e){
		alert("Exception : " + e.message);
	}
}

/*
***  去除首尾空格，包括英文空格、中文空格、页面解析后的&nbsp;
*/
function trim(str){
	return str.replace(/^[\s\u3000\xA0]+|[\s\u3000\xA0]+$/g,"");
}

function change(type){
	if(type ==1){
		replayType(1);
		disPlayType(2);
		if(docEnable)
			disPlayType(3);
	}else if(type ==2){
		replayType(2);
		disPlayType(1);
		if(docEnable)
			disPlayType(3);
	}else if(type == 3){
		replayType(3);
		disPlayType(2);
		disPlayType(1);
	}
}
function replayType(type){
	document.getElementById('category'+type+'_1').className='tab-tag-left-sel';
	document.getElementById('category'+type+'_2').className='tab-tag-middel-sel cursor-hand';
	document.getElementById('category'+type+'_3').className='tab-tag-right-sel';
	document.getElementById('category'+type+'_4').style.display='';
}
function disPlayType(type){
	document.getElementById('category'+type+'_1').className='tab-tag-left';
	document.getElementById('category'+type+'_2').className='tab-tag-middel cursor-hand';
	document.getElementById('category'+type+'_3').className='tab-tag-right';
	document.getElementById('category'+type+'_4').style.display='none';
}
function templeteLocation(url){
	window.location.href=url;
}