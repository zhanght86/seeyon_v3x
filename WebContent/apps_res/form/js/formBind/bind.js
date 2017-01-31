var selrownumber = null;
function send(){
  isSaveAction = true;
  var heightDifference = 60;
  //var templateName = document.getElementById("templateName");
  /*var formAppName = document.getElementById("formAppName").value;
  var formApp = document.getElementById("formApp").value;*/
  var rv = v3x.openWindow({
        url: bindFormURL + "?method=systemNewTemplete"
        +"&categoryType=4&categoryId=2194563217745201356",
        height:600 + heightDifference,
        width:520,
        dialogType: "modal",
        resizable: true
    });
  if(rv)
  	window.location.href = window.location.href;
}

function addTemplateAppBind(){
	var hTable = document.getElementById("hiddenTable");	
	var hRow = hTable.rows[0];
	
	var table = document.getElementById("listTable"); 
	var row = table.insertRow();
	var cell = row.insertCell();
	cell.setAttribute("width","10%");	
	cell.setAttribute("align","center");
	cell.style.cssText="border-bottom:solid 1px #DBDBDB;";
	cell.innerHTML=hRow.cells[0].innerHTML;

	/*cell = row.insertCell();
	cell.setAttribute("width","10%");		
	cell.className="sort";
	cellAddListener(cell);	
	cell.innerHTML=hRow.cells[1].innerHTML;*/
	
	cell = row.insertCell();
	cell.setAttribute("width","90%");	
	cell.className="sort";
	cellAddListener(cell);
	cell.innerHTML=hRow.cells[1].innerHTML;

	/*cell = row.insertCell();
	cell.setAttribute("width","20%");	
	cell.className="sort";
	cellAddListener(cell);
	cell.innerHTML=hRow.cells[3].innerHTML;
	
	cell = row.insertCell();
	cell.setAttribute("width","20%");	
	cell.className="sort";
	cellAddListener(cell);
	cell.innerHTML=hRow.cells[4].innerHTML;	*/
	
//	document.getElementById("formType").disabled="true";
}

function cellAddListener(obj){ 
	if(window.addEventListener){ 
	//其它浏览器的事件代码: Mozilla, Netscape, Firefox 
	//添加的事件的顺序即执行顺序 //注意用 addEventListener 添加带on的事件，不用加on	
		obj.addEventListener("click",selectColumn, true); 
	}else{ 	//IE 的事件代码 在原先事件上添加 add 方法 		
		obj.attachEvent("onclick",selectColumn);
	} 
} 

function selectColumn(){	
	selectedColumn(event, true)
}

function setTemplateAppBind(obj){
  isSaveAction = true;
  var heightDifference = 60;
  var mainIndexNum = obj.parentElement.parentElement.parentElement.rowIndex;
  var id = document.getElementsByName("bindId")[mainIndexNum].value;
  var rv = v3x.openWindow({
        url: bindFormURL + "?method=systemNewTempleteAppBind&id="+id,
        height:400 + heightDifference,
        width:520,
        dialogType: "modal",
        resizable: true
    });
  if(rv){
  	var returnStr=rv;
  	var bindIdAry = document.getElementsByName("bindId");
  	var theForm = document.getElementsByName("submitForm")[0];
  	bindIdAry[mainIndexNum].value = returnStr;
  	theForm.action = bindFormURL + "?method=systemSaveAppBindMain&add=0";
  	theForm.submit();
  }
}

function addRowBindAuth(name,xmlStr){
	var table = document.getElementById("listTable"); 
	var row = table.insertRow();
	var cell = row.insertCell();
	cell.setAttribute("width","10%");
	cell.setAttribute("align","center");
	cell.className="sort";
	cell.innerHTML='<input type="checkbox" value="'+name+'" name="ids">';
	cell = row.insertCell();
	cell.setAttribute("width","90%");
	cell.className="sort";
	cellAddListener(cell);
	cell.innerHTML=name;	
	appAuthNames.push(name);
	appAuthXmlStr.push(xmlStr);
} 

function updateRowBindAuth(name,xmlStr,indexNum){
	var table = document.getElementById("listTable"); 
	var row = table.rows[indexNum];
	var cell = row.cells[0];
	cell.innerHTML='<input type="checkbox" value="'+name+'" name="ids">';
	cell = row.cells[1];
	cell.innerHTML=name;	
	appAuthNames[indexNum] = name;
	appAuthXmlStr[indexNum] = xmlStr;
} 

function showAuth(elements){
  if(elements){
		document.getElementById("authName").value = getNamesString(elements);
		var value = document.getElementById("auth").value ;
		if(value != getIdsString(elements)){
			document.getElementById("authChange").value = 'true' ;
		}
		document.getElementById("auth").value = getIdsString(elements);
  }
}

function selectPeople(isNew){
    //designWorkFlow();
    isShowingDesigner = true;
    var secretLevel = document.getElementById("secretLevel").value;
	var rv = getA8Top().v3x.openWindow({
		url: genericControllerURL + "collaboration/monitor&isShowButton=true&isNew=" + isNew + "&isFormBind=true&isUpdate=" + isUpdate+"&secretLevel="+secretLevel,
		width: "860",
		height: "690",
		resizable: "no"
	});

    isShowingDesigner = false;
    if (rv != null) {

    }
    if(rv==true){
    	processing=false;
    	
    }
}


function checkSelect() {
    if (!hasWorkflow) {
        alert(v3x.getMessage("formLang.formenum_selectFlow"));
        return false;
    }
    return true;
}

function buttondis(){
 var saveFormTemp = document.all("saveFormTemp");
 if(saveFormTemp != null){
		saveFormTemp.disabled=true;
	}
}

function buttonEnable(){
 var saveFormTemp = document.getElementById("saveFormTemp");
 if(saveFormTemp){
		saveFormTemp.disabled=false;
	}
}

function saveFormTemplate(_type) {
	//debugger;
    isSaveAction = true;
    var theForm = document.getElementsByName("sendForm")[0];
    theForm.type.value = _type;
    var from = getParameter("from");
    /*if(from == "TM" && !theForm.categoryId.value){
    	alert(_("collaborationLang.templete_alertNoCategory"));
    	return;    	
    }*/
    if (checkForm(theForm) && checkRepeatFormTempleteSubject(theForm, true)) {
    	if(_type != "text" && !checkIsSelectWF()){ //协同正文，不用流程
    		return;
    	}
    	//校验模板编号是否合法
    	if(checkForOutSysCallOption() == false){
		return;
	    }
    	if(_type == "text"){
    		var bodyType = document.getElementById("bodyType").value;
	        if (bodyType != "HTML") {
	            alert(_('collaborationLang.collaboration_alertSaveTemplete'));
	            return;
	        }
    	}
    	
        if (_type != "workflow") { //流程模板，不保存office正文	        
        	if(!saveOffice()){
            	return;
        	}
        }
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
	     /*
	     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	     * 视图1.操作|视图2.操作  .......
	     */
	    detailid = "";
	     var archiverid = document.getElementById("archiveId");
	      var showdetailObj = document.all("showdetail");
			if(showdetailObj.length==undefined){
			    if(showdetailObj.checked == true){
			    	 var operationObj = document.all("operation");
			    	 detailid = showdetailObj.value+"."+operationObj.value+"|";
			    }										
			}else{
				 for(var i = 0; i < showdetailObj.length; i++){
					if(showdetailObj[i].checked == true){												
						 var operationObj = document.all("operation");
						 detailid += showdetailObj[i].value+"."+operationObj[i].value+"|";
					}						
				 }
			}
	    var archiverFormid = document.getElementById("archiverFormid");
	    archiverFormid.value = detailid;
	    if(archiverid.value !="" && archiverFormid.value == ""){
	    	alert(v3x.getMessage("formLang.formbind_selectform"));
	    	return;
	    }
		try {
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkNodePolicy", false);
			requestCaller.addParameter(1, "String[]", policyArr);
			requestCaller.addParameter(2, "String[]", itemNameArr);
			requestCaller.addParameter(3, "String", theForm.loginAccountId.value);
			var rs = requestCaller.serviceRequest();
			if(rs == "1"){
				alert(_("collaborationLang.node_policy_not_existence"));
				return;
			}
		}
		catch (ex1) {
			alert("Exception : " + ex1);
			return;
		}
        buttondis();
		if(branchs){
			var str = "";
			for(var i=0,j=keys.length;i<j;i++){
				var branch = branchs[keys[i]];
				if(branch!=null){
					str += "<input type=\"hidden\" name=\"branchs\" value=\""+keys[i]+"↗"+branch.id
					+"↗"+branch.conditionType+"↗"+(branch.formCondition?branch.formCondition.replace(/\"/gi,"&quot;"):branch.formCondition)+"↗"+branch.conditionTitle.replace(/\"/gi,"&quot;")+"↗"+branch.isForce+"↗"+(branch.conditionDesc?branch.conditionDesc.escapeQuot():"")
					+"↗"+branch.conditionBase+"\">";
				}
			}
			branchDiv.innerHTML = str;
		}
		
        saveAttachment();        
        theForm.submit();

        //window.dialogArguments.top.startProc('');
        //window.close();
    }
}
function checkDataField(){
	var dataField = document.getElementById("datafield");
	if(dataField.value == ""){
		alert(v3x.getMessage("formLang.formcreate_listdatafieldnotnull"));
		return false;
	}
	return true;
}

//保存应用绑定
function saveFormTemplateAppBind() {
    isSaveAction = true;
    var theForm = document.getElementsByName("sendForm")[0]; 
	var id = theForm.id.value;
	var name = theForm.subject.value;	
    if (checkForm(theForm)&& checkRepeatBindSubject(theForm)&& checkDataField()) {
		mainBindXml="";
		mainBindXml+="<FormAppAuth id=\""+id+"\" name=\""+name+"\">";
		mainBindXml+="<Query id=\""+id+"\" name=\""+name+"\">\r\n";
		mainBindXml += "    <QuerySource masterTable=\"" + masterTableName +  "\">\r\n";
		    for(var i=0;i<slaveTableNames.size();i++){
		    	mainBindXml += "    <slaveTable tableName=\"" + slaveTableNames.get(i) + "\" masterTable=\"" + masterTableName + "\" linkfield=\"" + masterTableName + "Id\"/>\r\n";
		    }
		mainBindXml += "    </QuerySource>\r\n";
		mainBindXml += document.getElementById("datafieldvalue").value;
		mainBindXml += document.getElementById("resultsortvalue").value;
		mainBindXml += document.getElementById("customQueryFieldValue").value;
		mainBindXml += "</Query>";
		
		//应用授权xml
		mainBindXml += "<OperationAuthList>";
		for(var k=0;k<appAuthXmlStr.length;k++){
			mainBindXml += appAuthXmlStr[k];		
		}
		mainBindXml += "</OperationAuthList></FormAppAuth>";
		theForm.mainBindXml.value = mainBindXml;
		theForm.action = bindFormURL + "?method=systemSaveTempleteAppBind";
       	theForm.submit();
    }
}

//保存应用授权
function saveFormTemplateAppAuth() {
    isSaveAction = true;
    var theForm = document.getElementsByName("sendForm")[0]; 
	var name = theForm.subject.value;
	var allowlock="false";
	var allowdelete="false";
	var allowexport="false";
	var allowquery="false";
	var allowreport="false";
	var allowlog="false";
	var allowprint="false";
	if(theForm.lockedAuthority.checked) allowlock="true";
	if(theForm.deleteAuthority.checked) allowdelete="true";
	if(theForm.exportAuthority.checked) allowexport="true";
	if(theForm.queryAuthority.checked)  allowquery="true";
	if(theForm.reportAuthority.checked) allowreport="true";
	if(theForm.logAuthority.checked) allowlog="true";
	if(theForm.printAuthority.checked) allowprint="true";
	
    if (checkForm(theForm)&& checkRepeatAuthSubject(theForm)) {
		mainBindXml="";
		mainBindXml += "<OperationAuth name=\""+name+"\" ";
		mainBindXml += "allowdelete=\""+allowdelete+"\" allowlock=\""+allowlock+"\" allowexport=\""+allowexport +"\" allowquery=\""+allowquery 
		               +"\" allowstat=\""+allowreport+"\" allowprint=\""+allowprint+"\" allowlog=\""+allowlog+"\">";		
		mainBindXml += document.getElementById("areavalue").value;
		
		 /*
	     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	     * 视图1.操作|视图2.操作  .......
	     */
	     
	    var detaiName = "<ShowDetail type=\"browse\" name=\"";
		var browseAuthorityCheck = document.getElementsByName("browseAuthorityCheck");	
		var browseAuthoritySelect = document.getElementsByName("browseAuthoritySelect");
		var flag = true;
		for(var i = 0; i < browseAuthorityCheck.length; i++){
			if(browseAuthorityCheck[i].checked == true){
				 detaiName += browseAuthorityCheck[i].value+"."+browseAuthoritySelect[i].value+"|";
				 flag = false;
			}
		}
		if(flag){
			alert(v3x.getMessage("formLang.formAuth_viewnotnull"));
			return false;
		}
		
		mainBindXml += detaiName+"\"/>";    	
	    
		var addAuthoritySelect = theForm.addAuthoritySelect.value;		
	    mainBindXml += "<ShowDetail type=\"add\" name=\""+addAuthoritySelect+"\"/>";

	    var updateAuthoritySelect = theForm.updateAuthoritySelect.value;		
	    mainBindXml += "<ShowDetail type=\"update\" name=\""+updateAuthoritySelect+"\"/>";
	    
		mainBindXml += "</OperationAuth>";
		theForm.mainBindXml.value=mainBindXml;
       	theForm.submit();
    }
}

/**
 * 检测授权标题是否重名,需要先到session中判断是否有删除的
 */
function checkRepeatBindSubject(form1){	
	var oldtemplatename = document.all("oldtemplatename").value;
	var temid = document.all("id").value;
	var subject = form1.elements['subject'].value;	
	if(oldtemplatename != subject){
		if(window.dialogArguments && window.dialogArguments.allList[subject]){
			alert(v3x.getMessage("formLang.formbind_subjectHasExist",subject));
			return false;
		}
	}	
	return true;
}

function checkRepeatAuthSubject(form1){	
	var oldtemplatename = document.all("oldtemplatename").value;
	var temid = document.all("id").value;
	var subject = form1.elements['subject'].value;	
	if(oldtemplatename != subject){
		if(window.dialogArguments && window.dialogArguments.allList[subject]){
			alert(v3x.getMessage("formLang.formAuth_subjectHasExist",subject));
			return false;
		}
	}	
	return true;
}


function editTemplate(categoryType){
   isSaveAction = true;
    var values = document.getElementsByName("ids");
    var id;
    var categoryId;
    var selectCount = 0;
    if(values){
        for(var i=0;i<values.length;i++){
        	if(values[i].checked){
                selectCount++;
	            var value = values[i].value.split(":");
	            id = value[0];
	            categoryId = value[1];
            }
            if(selectCount>1)
            	break;
        }
        if(selectCount==0){
            alert(v3x.getMessage("formLang.formbind_selectTemplate"));
            return;
        }else if(selectCount>1){
            alert(v3x.getMessage("formLang.formbind_onlyOneToEdit"));
            return;
        }
    }
    var heightDifference = 60;
    var rv = v3x.openWindow({
        url: bindFormURL + "?method=systemNewTemplete&templeteId="+id
        +"&categoryType="+categoryType+"&categoryId="+categoryId+"&isUpdate=1",
        height:600 + heightDifference,
        width:520,
        dialogType: "modal",
        resizable: true
    });
    if(rv)
  		//window.location.reload(true);
  		window.location.href = window.location.href;
}

function editTemplateAppBind(categoryType){
   	isSaveAction = true;
    var values = document.getElementsByName("ids");
    var id;
    var selectCount = 0;
    if(values){
        for(var i=0;i<values.length;i++){
        	if(values[i].checked){
                selectCount++;
	            id = values[i].value;	            
            }
            if(selectCount>1)
            	break;
        }
        if(selectCount==0){
            alert(v3x.getMessage("formLang.formbind_selectTemplate"));
            return;
        }else if(selectCount>1){
            alert(v3x.getMessage("formLang.formbind_onlyOneToEdit"));
            return;
        }
    }
    var heightDifference = 60;
    var rv = v3x.openWindow({
        url: bindFormURL + "?method=systemNewTempleteAppBind&id="+id
        +"&categoryType="+categoryType+"&isUpdate=1",
        height:400 + heightDifference,
        width:520,
        dialogType: "modal",
        resizable: true
    });
    if(rv)
  		window.location.href = window.location.href;
}

function setOperAuth(){
    isSaveAction = true;
    var theForm = document.getElementsByName("sendForm")[0]; 
    var mainId = theForm.id.value;
    var subject = theForm.operAuthSubject.value;
    var heightDifference = 60;
    var indexNum = 0;
    var rv = v3x.openWindow({
        url: bindFormURL + "?method=systemNewTempleteAppAuth&id="+mainId
        +"&subject="+encodeURIComponent(subject)+"&isUpdate=1",
        height:400 + heightDifference,
        width:520,
        dialogType: "modal",
        resizable: true
    });
    if(rv){
    	var returnStr=rv.split("@@");
    	appAuthNames[indexNum] = returnStr[0];
    	appAuthXmlStr[indexNum] = returnStr[1];
    	theForm.operAuthSubject.value = returnStr[0];	
    }
}

function emptyFormTemplateAppAuth(){
    isSaveAction = true;
    var indexNum = 0;
    if(confirm(""+v3x.getMessage("formLang.formcreate_confirmdel")+"")){
        sendForm.action = bindFormURL + "?method=deleteTemplateAppAuth";
        sendForm.submit();
		if(window.dialogArguments){
			window.dialogArguments.appAuthNames.splice(indexNum,1);
			window.dialogArguments.appAuthXmlStr.splice(indexNum,1);
			var parentForm = window.dialogArguments.document.getElementsByName("sendForm")[0]; 
			parentForm.operAuthSubject.value="";
		}
		window.close();
    }
}

/*
function addTemplateAppAuth(){
  var theForm = document.getElementsByName("sendForm")[0];
  isSaveAction = true;
  var heightDifference = 60;
  var mainId = theForm.id.value;
  var rv = v3x.openWindow({
        url: bindFormURL + "?method=systemNewTempleteAppAuth&id="+mainId,
    height:400 + heightDifference,
    width:520,
    dialogType: "modal",
        resizable: true
    });
  if(rv){
  	var returnStr=rv.split("@@");
  	addRowBindAuth(returnStr[0],returnStr[1]);  	
  }
}

function editTemplateAppAuth(categoryType){
    isSaveAction = true;
    var theForm = document.getElementsByName("sendForm")[0]; 
    var values = document.getElementsByName("ids");
    var mainId = theForm.id.value;
    var subject;
    var indexNum =0;
    var selectCount = 0;
    if(values){
        for(var i=0;i<values.length;i++){
        	if(values[i].checked){
                selectCount++;
	            subject = values[i].value;
	            indexNum = values[i].parentElement.parentElement.rowIndex;
            }
            if(selectCount>1)
            	break;
        }
        if(selectCount==0){
            alert(v3x.getMessage("formLang.formoperauth_selectTemplate"));
            return;
        }else if(selectCount>1){
            alert(v3x.getMessage("formLang.formoperauth_onlyOneToEdit"));
            return;
        }
    }
    var heightDifference = 60;
    var rv = v3x.openWindow({
        url: bindFormURL + "?method=systemNewTempleteAppAuth&id="+mainId
        +"&subject="+encodeURIComponent(subject)+"&categoryType="+categoryType+"&isUpdate=1",
        height:400 + heightDifference,
        width:520,
        dialogType: "modal",
        resizable: true
    });
    if(rv){
    	var returnStr=rv.split("@@");
  		updateRowBindAuth(returnStr[0],returnStr[1],indexNum);     	
    }
}

function delTemplateAppAuth(){
    isSaveAction = true;
    var values = document.getElementsByName("ids");
    var selectCount = 0;
    var indexNumAry = new Array();
    if(values){
        for(var i=0;i<values.length;i++){
            id = values[i].value ;
            if(values[i].checked){
                indexNumAry[selectCount]= values[i].parentElement.parentElement;
                selectCount++;
            }
        }
        if(selectCount==0){
            alert(v3x.getMessage("formLang.formoperauth_selectTemplate"));
            return;
        }
        else
        {
           if(confirm(""+v3x.getMessage("formLang.formcreate_delete")+""))
	        {
		        sendForm.action = bindFormURL + "?method=deleteTemplateAppAuth";
		        sendForm.submit();
				deleteRowBindAuth(indexNumAry); 
	        }
        }
    }
}
*/

function delTemplate(){
    isSaveAction = true;
    var values = document.getElementsByName("ids");
    var selectCount = 0;
    if(values){
        for(var i=0;i<values.length;i++){
            var value = values[i].value.split(":");
            id = value[0];
            if(values[i].checked){
                selectCount++;
            }
        }
        if(selectCount==0){
            alert(v3x.getMessage("formLang.formbind_selectTemplate"));
            return;
        }
        else
        {
           if(confirm(""+v3x.getMessage("formLang.formcreate_delete")+""))
	        {
		        submitForm.action = bindFormURL + "?method=deleteTemplate";
		        submitForm.submit();
	        }
        }
    }
    
}

function delTemplateAppBind(){
    isSaveAction = true;
    var values = document.getElementsByName("ids");
    var selectCount = 0;
    if(values){
        for(var i=0;i<values.length;i++){
            var value = values[i].value;
            id = value[0];
            if(values[i].checked){
                selectCount++;
            }
        }
        if(selectCount==0){
            alert(v3x.getMessage("formLang.formbind_selectTemplate"));
            return;
        }
        else
        {
           if(confirm(""+v3x.getMessage("formLang.formcreate_delete")+""))
	        {
		        submitForm.action = bindFormURL + "?method=deleteTemplateAppBind";
		        submitForm.submit();
	        }
        }
    }
}

function deleteRowBindAuth(indexNumAry){
	var table = document.getElementById("listTable"); 	
	for(var k = 0; k<indexNumAry.length; k++){
		appAuthNames.splice(indexNumAry[k].rowIndex,1);
		appAuthXmlStr.splice(indexNumAry[k].rowIndex,1);
		table.deleteRow(indexNumAry[k].rowIndex);
	}
}


function ajaxInvoke(aUrl){
	  var httpCall = NewHTTPCall();
	  var nowresult;
	  if (httpCall ==  null){
		alert(v3x.getMessage("formLang.formdisplay_nonsupportXMLHttp"));
		return null;
	  }
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

function NewHTTPCall(){
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

function getdataIndex(){//tr被选中行	
    var td = event.srcElement;
	if(td.nodeName.toLowerCase() == "td")
	var tr = td.parentElement;
	 selrownumber = tr.rowIndex;         
}

function toMenuBind()
{
	window.showModalDialog(menubindURL + "?method=formMenuBind&status=add",window,"DialogHeight=500px;DialogWidth=480px;status=no;");	  
}

function editMenuBind()
{
   if(selrownumber == undefined){
		alert(v3x.getMessage("formLang.formquery_selectone"));
		return;
	}
	else
	{
       window.showModalDialog(menubindURL + "?method=formMenuBind&status=edit&rowindex="+selrownumber+"",window,"DialogHeight=480px;DialogWidth=450px;status=no;");	  
	}
}

function delMenuBind()
{
	var formname = document.getElementById("formMenu");
	if(selrownumber == undefined){
		alert(v3x.getMessage("formLang.formquery_selectone"));
		return;
	}
	else {
		if(confirm(""+v3x.getMessage("formLang.formcreate_delete")+"")){
           formname.action = menubindURL+"?method=delete&id="+selrownumber+"";
		   formname.submit();
		}
	}
}

function checkIsSelectWF() {
    if (!hasWorkflow) {
        alert(v3x.getMessage("collaborationLang.collaboration_selectWorkflow"));
        return false;
    }
    return true;
}

//预归档
function doFormPigeonhole(flag, appName) {
    if (flag == "no") {
        //TODO 清空信息
    }
    else if (flag == "new") {
        var result = pigeonhole(appName,null,false,false);
        if(result == "cancel"){
        	var oldPigeonholeId = document.getElementsByName("sendForm")[0].archiveId.value;
        	if(oldPigeonholeId != ""){
        		var oldOption = document.getElementById(oldPigeonholeId);
        		oldOption.selected = true;
        	}else{
        		var oldOption = document.getElementById("defaultOption");
        		oldOption.selected = true;
        	}
        	return;
        }
        var pigeonholeData = result.split(",");
        pigeonholeId = pigeonholeData[0];
        pigeonholeName = pigeonholeData[1];
        var archiveFormIdTR = document.getElementById("archiveFormIdTR");
        if(pigeonholeId == "" || pigeonholeId == "failure"){
        	document.getElementsByName("sendForm")[0].archiveName.value = "";
        	alert(v3x.getMessage("collaborationLang.collaboration_alertPigeonholeItemFailure"));
        }else{
        	var oldPigeonholeId = document.getElementsByName("sendForm")[0].archiveId.value;
        	document.getElementsByName("sendForm")[0].archiveId.value = pigeonholeId;
        	var selectObj = document.getElementById("colPigeonhole");
        	var option = document.createElement("OPTION");
        	option.id = pigeonholeId;
        	option.text = pigeonholeName;
        	option.value = pigeonholeId;
        	option.selected = true;
        	archiveFormIdTR.style.display = "";
        	if(oldPigeonholeId == ""){
        		selectObj.options.add(option, selectObj.options.length);
        	}else{
        		selectObj.options[selectObj.options.length-1] = option;
        	}
        }
    }


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
/**
 * 检测模板标题是否重名，不同于协同模板，需要先到session中判断是否有删除的
 */
function checkRepeatFormTempleteSubject(form1){	
	var oldtemplatename = document.all("oldtemplatename").value;
	var temid = document.all("id").value;
	var templeteNumber =  document.all("templeteNumber").value;
	var oldtempleteNumber =  document.all("oldtempleteNumber").value;
	var subject = form1.elements['subject'].value;
	if(oldtemplatename != subject){
		if(window.dialogArguments && window.dialogArguments.allList[subject]){
			alert(v3x.getMessage("formLang.formbind_templetSubjectHasExist",subject));
			return false;
		}
	}
	if(templeteNumber !="" && templeteNumber !="null" && templeteNumber !=null){
			var dataUrl = v3x.baseURL + encodeURI(encodeURI("/bindForm.do?method=checksaveTemplete&templeteNum=" + templeteNumber+"&templeteId=" +temid));
			var str = init(dataUrl);
			if(str == "true"){
			  alert(templateCodeDupleLabel);
			  return;
			}	
	}
	if(window.dialogArguments && window.dialogArguments.delList[subject])
		return true;
	return checkRepeatTemFormSubject(form1);
}

function checkRepeatTemFormSubject(form1){
	//debugger;
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
        alert(v3x.getMessage("formLang.formbind_templetSubjectHasExist",subject))
		return false;
	}
	catch (ex1) {
		alert("Exception : " + ex1.message);
	}

	return true;
}

/**********************************************************
 * ************** 表单的标题的设置 functions *****************
 **********************************************************/
 
function setSubject(){
	var workflowInfo = document.getElementById("workflowInfo") ;
	
	if(!workflowInfo){
		return ;
	}
	if(workflowInfo.value == ""){
		  alert("请先设置流程");
		  return ;
	}
	var process_desc_by = document.getElementById("process_desc_by") ;
	var process_xml = document.getElementById("process_xml") ;
	var defaultFirstNodeOperationId = document.getElementById("defaultFirstNodeOperationId");
	if(!process_desc_by || !process_xml || !defaultFirstNodeOperationId){
		 return ;
	}
	
	var seeyonPolicy = parFlowDataXMl(process_xml.value) ;
	
	if(!seeyonPolicy){
		 return ;
	}	
 	var rv = v3x.openWindow({
        url: bindFormURL + "?method=setAppSubject&formAppid=" + formApp + "&defaultFirstNodeOperationId="+defaultFirstNodeOperationId.value+"&opentionId=" + seeyonPolicy.opentionId + "&from="+seeyonPolicy.from ,
        height: 450,
        width: 500,
        resizable: "no"
    });
    if(rv || rv == "" || rv==null){
    	 if(rv==null) rv="";
	     if(rv != 'false') {
	       var colSubjectObj = document.getElementById('colSubject') ; 
	       colSubjectObj.value = rv ;
	     }
    }
      
}
function Operation(){
	this.opentionId = null ;
	this.from = null ;
}
/**
 *解析流程的XML 
 **/
function parFlowDataXMl(process_xml){
		var xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
		xmlDoc.async="false";
		xmlDoc.loadXML(process_xml);
		if(xmlDoc == null){
			alert("解析流程XML错误！");
			return ;
		}
		var root = xmlDoc.documentElement;
		if(root == null) {
			alert("解析流程XML错误！");
			return ;			
		}
	    var nodes = root.getElementsByTagName("node");
	    for(var i = 0; i < nodes.length; i++){
		if(nodes[i].getAttribute("id") == 'start'){
            return getElement(nodes[i]) ;
		}			
	}	    
}

function getElement(element){
	var operation = new Operation() ;
	var seeyonPolicy = element.getElementsByTagName("seeyonPolicy")[0];
	operation.opentionId = seeyonPolicy.getAttribute("operationName") ;
	operation.from = seeyonPolicy.getAttribute("form");
	return operation ;
}

function paseSubject() {	
 	if(document.getElementById("subjectValueTextArea")){
 		var subjectValue = document.getElementById("subjectValueTextArea").value ;  
 		var strLength = subjectValue.length ;	
 		if(strLength > 85){
 			alert('表单流程标题设置的长度过长！') ;
 			return ;
 		}	
 		var parStr = /({?[^{]+}?)*/ ;	 		
 		if(checkFunction(subjectValue))	{ 			
 			windowReturn('set');
 		}else {
 			alert('输入的字符串不能解析！') ;
 		} 
 	}
 }
 /**
  * 当前当前的字符串是否符合规则
  */
function checkFunction(str){
	var strLength = str.length ;
	var startIndex = "" ;//第一个{在的位置
	var endIndex = "" ;//第一个}在的位置
	var lastStr = "" ;//剩余的字符串
	if(strLength == 0){
		return true ;
	}
	if(str.indexOf('{') == -1){		
		return true ;
	}	
	startIndex = str.indexOf('{') ;
	if(str.indexOf('}') == -1){		
		return false ;
	}			
	lastStr = str.substring(startIndex+1,strLength) ;
	endIndex = lastStr.indexOf("}");
	var flag = false ;
	flag = checkTools(lastStr,endIndex,strLength);
	return flag  ;
} 
/**
 * 递归判断
 */
function checkTools(lastStr,endIndex,strLength){
	var currnetStr = lastStr.substring(0,endIndex) ;//“}”在的位置
    if(dataArrayNameList[currnetStr] == undefined && systemValueNameList[currnetStr] == undefined){		   
		   return false ;
	}
	lastStr = lastStr.substring(endIndex+1,lastStr.length) ;
	if(lastStr.indexOf("{") == -1 && lastStr.indexOf("}") == -1) {		
		return true ;
	}
	if(lastStr.indexOf("{") == -1 || lastStr.indexOf("}") == -1){		
		return false;
	}
	lastStr =  lastStr.substring(lastStr.indexOf("{")+1 ,lastStr.length) ;
	endIndex = lastStr.indexOf("}") ;
	return checkTools(lastStr,endIndex,lastStr.length) ;
}
  
function changedTypeStyle(srt){
	var fieldDiv = document.getElementById("fieldDiv") ;
	var systemValueDiv = document.getElementById("systemValueDiv") ;
	var formdataleft = document.getElementById("formdataleft") ;
	var formdatamiddel = document.getElementById("formdatamiddel") ;
	var formdataright = document.getElementById("formdataright") ;
	
	var systemsetleft = document.getElementById("systemsetleft") ;
	var systemsetmiddel = document.getElementById("systemsetmiddel") ;	
	var systemsetright = document.getElementById("systemsetright") ;
	if(fieldDiv && systemValueDiv && formdataleft && systemsetleft){
		if(srt =='formdata'){
			fieldDiv.style.display = "" ;
			systemValueDiv.style.display = "none" ;
			formdataleft.className = "sign-button-L-sel"; 
			formdatamiddel.className = "sign-button-M-sel";
	    	formdataright.className = "sign-button-R-sel";
	    	
			systemsetleft.className = "sign-button-L" ;
			systemsetmiddel.className = "sign-button-M";
			systemsetright.className = "sign-button-R";
		}else if(srt == 'systemvar'){
			fieldDiv.style.display = "none" ;
			systemValueDiv.style.display = "" ;
			
			formdataleft.className = "sign-button-L" ;
			formdatamiddel.className = "sign-button-M";
			formdataright.className = "sign-button-R";
						
			systemsetleft.className = "sign-button-L-sel"; 
			systemsetmiddel.className = "sign-button-M-sel";
	    	systemsetright.className = "sign-button-R-sel";
		}
	}
 }
 
 function resStart(){
 	var subjectValue = document.getElementById("subjectValueTextArea") ;
 	if(subjectValue){
 		subjectValue.value = "" ;
 	}
 }

 
function windowReturn (str){
 	if(str == 'set'){
 		var subjectValue = document.getElementById("subjectValueTextArea").value ; 
 		window.returnValue = subjectValue ;
 	}else {
 		window.returnValue = 'false' ;
 	}
 	window.close() ;
}
 
function InsertTextselect(obj,selectObj){
	obj.focus();
	 var cursorpostion = document.selection.createRange();  
	 if(selectObj.selectedIndex == -1)
	  return;
	  else{
	  	cursorpostion.text +="" + "{"+selectObj.options[selectObj.selectedIndex].value+"}" +"";	
	  }
}

 