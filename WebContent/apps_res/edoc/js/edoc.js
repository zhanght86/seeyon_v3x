//遍历页面中span中属性xd:binding，放到js的hashtable中，便于以后读取
var opinionSpans=null;

//记录是否进行了正文修改
var contentUpdate=false;
//记录流程是否修改
var workflowUpdate = false ;
//是否是提交或者暂存待办的操作
var isSubmitOperation = false;
var supervised = false;
var canUpdateContent=true;//正文是否允许修改
var hasTaohong = false;//套红记录
var changeWord = false ;//正文修改，主要用于区别套红 签章操作与修改正文
var changeSignature = false ;//签章
//全局变量，公文单是否修改标志
var isUpdateEdocForm=false;
/* 是否为下拉提示，默认为不是，用于修改文号处,控制wordNoChange()方法多次调用 */
var isEdocLike = false;

//公文流程密级
//var flowSecretLevel_wf = 1;
//2017-01-11 诚佰公司
var flowSecretLevel_wf = "";

var flowSecretLevel_passRead = 1;
var flowSecretLevel_addMoreSign = 1;
var flowSecretLevel_track = 1;
try {
    getA8Top().endProc();
}
catch(e) {
}

function setMenuState(menu_id)
{
  var menuDiv=document.getElementById(menu_id);
  if(menuDiv!=null)
  {
    menuDiv.className='webfx-menu--button-sel';
    menuDiv.firstChild.className="webfx-menu--button-content-sel";
    menuDiv.onmouseover="";
    menuDiv.onmouseout="";
  }
}

function addReplySenderOpinion() {	
  initHtmlReplyOpinion=document.getElementById("replyDivsenderOpinion").innerHTML;
  document.getElementById("replyDivsenderOpinion").style.display = "block";
  var theForm = document.getElementsByName("repform")[0];
  theForm.postscriptContent.focus();
}

function replyCommentOK(date){
	parent.location.href=parent.location.href;
	/*
	var theForm = document.getElementsByName("repform")[0];
	
	
		var str = "";
		str += '<div class="div-float-clear" style="width: 100%">';
		str += '	<div class="sendOptionWriterName">' + date + '</div>';
		str += '	<div class="optionContent wordbreak">' + theForm.postscriptContent.value + '</div>';
		
		if(!fileUploadAttachments.isEmpty()){
			var atts = fileUploadAttachments.values();
			str += '	<div class="div-float attsContent">';
			str += '		<div class="atts-label">' + attachmentLabel + ' :&nbsp;&nbsp;</div>';
			
			for(var i = 0; i < atts.size(); i++) {
				str += atts.get(i).toString(true, false);
			}
			
			str += '	</div>';
		}
		str += '</div>';
		
		document.getElementById("replyDivsenderOpinionDIV").innerHTML += str;
		document.getElementById("replyDivsenderOpinion").innerHTML=initHtmlReplyOpinion;
		document.getElementById("replyDivsenderOpinion").style.display = "none";
	
	fileUploadAttachments.clear();
	*/
}
/**
 * 流程节点处理明细,直接调用协同的方法
 */
function showFlowNodeDetail(){
	var rv = getA8Top().v3x.openWindow({
        url: colWorkFlowURL + "?method=showFlowNodeDetailFrame&summaryId="+summary_id+"&appName=4&appTypeName="+appTypeName,
        dialogType : v3x.getBrowserFlag('openWindow') == true ? "modal" : "1",
        width: "800",
        height: "600"
    });
}
//打印
function colPrint(){
	try {
		
		var printEdocBody= v3x.getMessage("edocLang.edoc_form");
		var edocBody = parent.detailMainFrame.contentIframe.document.getElementById("html").innerHTML;
		
		var x = edocBody.split("<INPUT ");
		var font_type = "";
		for(var i=0;i<x.length;i++){
			var x_a = x[i];
			var b = x_a.substring(x_a.indexOf(" style=\"")+8,x_a.indexOf("\" value"));
			if(b.indexOf("FONT")>-1){
				font_type = b;
			}
		}
		
		
		var re = /disabled/g;
		edocBody = edocBody.replace(re," READONLY=\"READONLY\"");
		
		re = /INPUT/g;
		edocBody = edocBody.replace(re,"INPUT style=\"border:0\"");			
					
		var a = edocBody.split("</SELECT>");
			var result = "";		
		
		for(var i=0;i<a.length;i++){
			var aa = a[i];
			var b = aa.substring(aa.indexOf("<SELECT"),aa.length+9);
			var bb = b.substring(b.indexOf("selected>")+9,b.length);
			var c = bb.substring(0,bb.indexOf("</OPTION>"));

			re = /<SELECT/g;
			var n = aa.replace(re,"<input type=\"text\" style=\"border:0;"+font_type+"\" value=\""+c+"\"><SELECT class=\"hidden\"");
			
			result += n;
		}
		
		//替换多行文本的回车,以及空格符号，让打印预览的页面的多行文本也有回车换行及空格的效果
		var textAreas = result.split("</TEXTAREA>");
		for(var i = 0;i<textAreas.length ; i++){
			var index = textAreas[i].indexOf("<TEXTAREA");
			if(index!=-1){
				var textArea = textAreas[i].substring(index);
				var ind = textArea.indexOf(">");
				var textAreaInnerHtml = textArea.substring(ind);
				var textAreaResult = textAreaInnerHtml.replace(/\r\n|\n/gm,'<br/>').replace(/\s/gm,'&nbsp;');
				result = result.replace(textAreaInnerHtml,textAreaResult);
			}
		}
		var styleStr=result.split("style=\"");
		var newResult=styleStr[0];
		for(var i=1;i<styleStr.length;i++){
			var a=styleStr[i];
			var inde=a.indexOf("\"");
			var style=a.substring(0,inde);
			if(style.indexOf("COLOR")==-1&&style.length>20){
				a="COLOR:BLACK;"+a;
			}
			a="style=\""+a;
			newResult+=a;
		}
		result=newResult;
		result = result.replace(new RegExp("TEXTAREA",'gm'),'SPAN');
		
		//39413 打印出来的控件内的内容为黑色。
		result = result.replace(/link-blue/gm,'');
		
		//32718 处理意见过长，公文单打印异常。
		var a=result; 
		while(a.indexOf("<SPAN")!=-1){
			a=a.substring(a.indexOf("<SPAN")+1);
			var span=a.substring(0,a.indexOf(">"));
			var aft="";
			if(span.indexOf("shenpi")!=-1||span.indexOf("niwen")!=-1||
			span.indexOf("shenhe")!=-1||span.indexOf("fuhe")!=-1||
			span.indexOf("fengfa")!=-1||span.indexOf("huiqian")!=-1||
			span.indexOf("qianfa")!=-1||span.indexOf("zhihui")!=-1||
			span.indexOf("yuedu")!=-1||span.indexOf("banli")!=-1||
			span.indexOf("dengji")!=-1||span.indexOf("niban")!=-1||
			span.indexOf("wenshuguanli")!=-1||span.indexOf("chengban")!=-1||
			span.indexOf("otherOpinion")!=-1||span.indexOf("opinion")!=-1){
				aft=span.replace("SPAN","div");
				result=result.replace(span,aft);
			}
		}
		var edocBodyFrag = new PrintFragment(printEdocBody, result);
	} catch (e) {
	
	}	
	
	var cssList = new ArrayList();
	
	var pl = new ArrayList();
	pl.add(edocBodyFrag);
	
	var setHidden=false;
	var hiddenReplay=false;
	
	try{
	
	//增加发起人意见,处理意见打印
	var contentDoc=parent.detailMainFrame.contentIframe.document;
	var sendOpinionTitleObj = contentDoc.getElementById("sendOpinionTitle");
	var repDiv=contentDoc.getElementById("replyDivsenderOpinion");
	if(repDiv!=null && repDiv.style.display == "block")
	{
		repDiv.style.display="none";
		hiddenReplay=true;
	}
	
	
	if(contentDoc.getElementById("addSenderOpinionDiv")!=null)
	{
		setHidden=true;
		contentDoc.getElementById("addSenderOpinionDiv").style.visibility="hidden";
	}
	
	if(sendOpinionTitleObj!=null)	
	{
		var sendOpinionTitleFrag = new PrintFragment(sendOpinionTitleObj.innerHTML, contentDoc.getElementById("printSenderOpinionsTable").outerHTML);		
		pl.add(sendOpinionTitleFrag);
	}
	
	var dealOpinionTitleObj = contentDoc.getElementById("dealOpinionTitle");
	if(sendOpinionTitleObj!=null)	
	{
		var dealOpinionTitleFrag = new PrintFragment(dealOpinionTitleObj.innerHTML, contentDoc.getElementById("printOtherOpinionsTable").outerHTML);		
		pl.add(dealOpinionTitleFrag);
	}
	}catch(e){}	
	//cssList.add(v3x.baseURL + "/common/css/default.css");
	cssList.add(v3x.baseURL + "/apps_res/edoc/css/edoc.css");
	//cssList.add(v3x.baseURL + "apps_res/form/css/SeeyonForm.css");
	printList(pl,cssList);
	
	try{
	if(setHidden){contentDoc.getElementById("addSenderOpinionDiv").style.visibility="inherit";}
	if(hiddenReplay){contentDoc.getElementById("replyDivsenderOpinion").style.display = "block";}
	}catch(e){}
}


function compareTime(selObj){
	var newCollForm = document.getElementsByName("sendForm")[0];
	var advanceRemind = document.getElementById("advanceRemind");//.options[newCollForm.advanceRemind.selectedIndex];
	var deadline = document.getElementById("deadline");//newCollForm.deadline.options[newCollForm.deadline.selectedIndex];
	var advanceRemindTime = new Number(advanceRemind.value);
	var deadLineTime = new Number(deadline.value);
	if(deadLineTime <= advanceRemindTime){
		alert(v3x.getMessage("edocLang.remindTimeLessThanDeadLine"));
		try{selObj.selectedIndex = 0;}catch(e){}
		return false;
	}
	return true;
}

//added by lius. 得到国际化字符串的简易表达，�?_("i18n key")
//_ = v3x.getMessage;

/**
 * 按map中的内容当成数据提交
 * 注意:javascript调用时最好用return false,以免默认的Form也被提交
 * target默认是_self,method默认是post
 */
function submitMap(map, action, target, method) {
    var form = document.createElement("form");
	form.setAttribute('method',method ? method : 'post');
	form.setAttribute('action',action);
	form.setAttribute('target',target ? target:'');
	
    document.body.appendChild(form);
    for (var item in map) {
        //自定义元素的
        if ((typeof(map[item]) == "object") && ("toFields" in map[item])) {
            var fields = map[item].toFields();
            //need jquery support
            $(form).append(fields);
        } else if (! (map[item] instanceof Array)) {
            var value = map[item];
            var field = document.createElement('input');
            field.setAttribute('type','hidden');
            field.setAttribute('name',item);
            field.setAttribute('value',value);
            form.appendChild(field);
        } else {
            var arr = map[item];
            for(var i = 0; i < arr.length; i++) {
                var value = arr[i];
                var field = document.createElement('input');
                field.setAttribute('type','hidden');
                field.setAttribute('name',item);
                field.setAttribute('value',value);
                form.appendChild(field);
            }
        }
    }
    try{    	
    	var tempInput = document.createElement('INPUT');
    	tempInput.setAttribute('type','hidden');
    	tempInput.setAttribute('name','appName');
    	tempInput.setAttribute('value','4');
    	form.appendChild(tempInput);
    	var edocType=document.getElementById('theform')['edocType'].value;
    	
    	var tempInput2 = document.createElement('INPUT');
    	tempInput2.setAttribute('type','hidden');
    	tempInput2.setAttribute('name','edocType');
    	tempInput2.setAttribute('value',edocType);
    	form.appendChild(tempInput2);
    }catch(e){}
    form.submit();
}

function map2params(map) {
    var container = [];
    for (var item in map) {
        if (! (map[item] instanceof Array)) {
            var value = map[item];
            var str = item + "=" + escape(value);
            container[container.length] = str;
        } else {
            var arr = map[item];
            for (var index in arr) {
                var value = arr[index];
                var str = item + "=" + escape(value);
                container[container.length] = str;
            }
        }
    }
    var result = container.join("&");
    return result;
}

var hasWorkflow = false;

//var workitemSelected = [];
//isFromTemplate
//通过ajax加载了模板的选人界面，并手工选了人。
var loadAndManualSelectedPreSend = false;
var isAllMember = true;
var isCheckNodePolicyFlag = false;

//Ajax判断文号定义是否被删除，并且判断内部文号是否已经存在
function checkMarkDefinitionExsit(definitionId,doc_mark,num,selectMode,summaryId){
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocController", "checkEdocMark", false);
		requestCaller.addParameter(1, "Long", definitionId);
		requestCaller.addParameter(2,'String',doc_mark);
		requestCaller.addParameter(3,'Integer',selectMode);
		requestCaller.addParameter(4,'String',summaryId);
		var rs = requestCaller.serviceRequest();
		//rs:0,0 (返回以,连接的两个整数)   （0：已删除文号定义 1：未删除文号定义）|(0:未使用内部文号 | 1：已使用该内部文号)
		var ret=rs.split(",");
		if(ret[0]== "0"){//已经被删除
			if(num==1){
				alert(_("edocLang.doc_mark_definition_alter_deleted"));
			}else if(num==2){
				alert(_("edocLang.doc_innermark_definition_alter_deleted"));
			}
			return false;
		}
		if(num==2){//内部文号
			if(ret[1]=="1"){ //内部文号已经存在
				alert(_("edocLang.doc_innermark_used"));
				return false;
			}
		}
		return true;
	}
	catch (ex1) {
		alert("Exception : " + ex1);
		return false;
	}	
}
//检查文号是否是格式良好的。
function isEdocMarkWellFormated(edocmark){
    var arr=edocmark.split("\|");
    if(!(arr.length==4 || arr.length==1)){//直接登记的时候是1
        alert(_("edocLang.edoc_mark_isnotwellformated"));
        return false;
    }
    return true;
}
function checkMarkDefinition(theForm){
	 //下拉："-2835738348978420994|不按年度]第0001号|1|1" 
	 //断号： "-4856736416063797664|BB]第0001号||2"
	 //输入: "0|9999||3
	 var summaryId=document.getElementsByName("summaryId")[0].value;
	 if(theForm.elements["my:doc_mark"]) {
		 var edocMarkObj = theForm.elements["my:doc_mark"][0]; 
		 if(edocMarkObj){
		 	var edocmark=edocMarkObj.value;
		 	var definitionId=edocmark.substring(0,edocmark.indexOf("|"));//文号定义ID
		 	//var edocmarkString=edocmark.substring(edocmark.indexOf("|")+1);
		 	//var doc_mark=edocmarkString.substring(0,edocmarkString.indexOf("|"));//文号
		 	var selectMode=edocmark.substring(edocmark.length-1);
		 	if(definitionId!=0){
		 		if(!checkMarkDefinitionExsit(definitionId,null,1,selectMode,summaryId)){
		 			return false;
		 		}
		 	}
		 }
	 }
	 var edocMarkObj = theForm.elements["my:doc_mark2"]; 
	 if(edocMarkObj){
	 	var edocmark=edocMarkObj.value;
	 	var definitionId=edocmark.substring(0,edocmark.indexOf("|"));//文号定义ID
	 	//var edocmarkString=edocmark.substring(edocmark.indexOf("|")+1);
	 	//var doc_mark2=edocmarkString.substring(0,edocmarkString.indexOf("|"));//文号
	 	var selectMode=edocmark.substring(edocmark.length-1);
	 	if(definitionId!=0){
	 		if(!checkMarkDefinitionExsit(definitionId,null ,1,selectMode,summaryId)){
	 			return false;
	 		}
	 	}
	 }
	 var edocMarkObj = theForm.elements["my:serial_no"]; 
	 if(edocMarkObj){
	 	var edocmark=edocMarkObj.value;
	 	var definitionId=edocmark.substring(0,edocmark.indexOf("|"));//文号定义ID
	 	var edocmarkString=edocmark.substring(edocmark.indexOf("|")+1);
	 	var serial_no=edocmarkString.substring(0,edocmarkString.indexOf("|"));//文号
	 	var selectMode=edocmark.substring(edocmark.length-1);
	 	if(serial_no!=""){
	 		if(!checkMarkDefinitionExsit(definitionId,serial_no,2,selectMode,summaryId)){
	 			return false;
	 		}
	 	}
	 }
	 return true;
}
function send() {
	 var theForm = document.getElementsByName("sendForm")[0];
	 //增加对公文文号长度校验，最大长度不能超过66，主要考虑归档时，doc_metadata表长度200
     if(!checkEdocMark()) return;
     //验证文号定义是否存在
	 if(!checkMarkDefinition(theForm))return false;
	//验证交换单位是否有重复 -- start --
	var bool = checkExchangeAccountIsDuplicatedOrNot();
	
	if(bool == false){
		alert(_("edocLang.exchange_unit_duplicated"));
		return;
	}
	// -- end --

	if(validFieldData()==false){return;}
	if(compareTime()==false){return;}
	
    if (!theForm) {
        return;
    }
    theForm.action = genericURL + "?method=send";
    
    //2017-01-11 诚佰公司 添加密级空值校验
    if (checkForm(theForm) && checkSelectSecret()) {	
    	//标题不能为空并且不含有特殊字符	
    	if(!checkSubject(theForm)){return;}
    	//检查主送单位、主送单位2的是否设置有值
		if(!checkSendUnitAndSendUnit2(theForm)){return;}
        if (!checkSelectWF()) {        
            return;
        }
       
        //检查签报公文文号是否已使用
        var edocMarkObj = document.getElementById("my:doc_mark");
        if(edocMarkObj){
	        var edocMark = _getWordNoValue(edocMarkObj);
	        var edocType = document.getElementById("edocType").value;
	        var summaryId=document.getElementById("summaryId").value;
	        if(edocType == "2" && edocMark != "" && !loadAndManualSelectedPreSend && checkMarkHistoryExist(edocMark,summaryId)){
	        	return;
	        }
        }
        //var html = $('#processModeSelectorContainer').html();
    	//$('#processModeSelectorContainer').html("");
    	if (!loadAndManualSelectedPreSend) {
            disableButtons();
            $('#sendForm').ajaxSubmit({
                //url : colWorkFlowURL + "?method=preSend&isFromTemplate="+isFromTemplate,
            	url : colWorkFlowURL + "?method=prePopNew&isFromTemplate="+isFromTemplate,
                type : 'POST',
                async : false,
                success : function(data) {
                    //$('#processModeSelectorContainer').html(data);
                	//alert("data:="+data);
                	//赋值给页面的popJsonId隐藏域
                	document.getElementById("popJsonId").value=data;
                	//转换成页面js的json对象
                	var dataObj= eval('('+data+')');
                	var invalidateActivity= dataObj.invalidateActivity;
                	//alert("invalidateActivity:="+invalidateActivity);
                	if(dataObj.invalidateActivity!=""){
                	//if(document.getElementById("invalidateActivity")){
                    	alert(_("collaborationLang.collaboration_invalidateActivity", dataObj.invalidateActivity));
                        enableButtons();
                    	return;
                    }
                	//alert("dataObj.isPop:="+dataObj.isPop);
    	            var ret = manualSelectByProcessModePop(dataObj.isPop,dataObj.hasNewflow);
                    //var ret = manualSelectByProcessMode();
                    if (!ret) {
                    	//$('#processModeSelectorContainer').html("");
                        loadAndManualSelectedPreSend = false;
                        enableButtons();
                        return;
                    } else {
                        loadAndManualSelectedPreSend = true;
                        if(document.getElementById("processXML") && document.getElementById("desc_by")){
                        	var processXML = document.getElementById("processXML").value;
                        	var desc_by = document.getElementById("desc_by").value;
                        	if(processXML != "" && desc_by != ""){
                        		document.getElementById("process_xml").value = processXML;
                        		document.getElementById("process_desc_by").value = desc_by;
                        	}
                        }
                        send();
                    }
                }
            });
            return;
        }

        //节点权限引用更新
		if(!isCheckNodePolicyFlag){
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
		   	
		   	if(policyArr.length > 0){
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
		   	}
		}

        //$('#processModeSelectorContainer').html(html);
        //保存正文
       // checkExistBody();
        fileId=newEdocBodyId;
        //拟文套红,只有发送后才会有效果
        var bodyType=document.getElementById("bodyType").value;
        var edocType=document.getElementById("edocType").value;//收文的时候不能刷新
	    if(edocType&&edocType!=1 &&(bodyType=="OfficeWord"  ||  bodyType=="WpsWord" )){
	    	try{
			    checkOpenState();
			    if(getBookmarksCount()>0){
			    	if(confirm(_("edocLang.edoc_deleteBookMark"))){
			    		delBookMarks();
			    	}else{
			    		refreshOfficeLable();
			    	}
			    }
	    	}catch(e){
	    	}
    	}
        if(!saveOcx())
        {
        	enableButtons();
        	return;
        }

        saveAttachment();

        disableButtons();

        isFormSumit = true;
        
        var branchNodes = document.getElementById("allNodes");
        if(branchNodes && branchNodes.value){
        	theForm.action += "&branchNodes=" + branchNodes.value;
        }

        theForm.target = "_self";     
        
        try{adjustReadFormForSubmit();}catch(e){}
        
        if(nodes != null && nodes.length>0){
        	var hidden;
        	for(var i=0;i<nodes.length;i++){
		        //hidden = document.createElement('<INPUT TYPE="hidden" name="policys" value="' + policys[nodes[i]].name + '" />');
		        hidden = document.createElement('input');
		        hidden.setAttribute('type','hidden');
		        hidden.setAttribute('name','policys');
		        hidden.setAttribute('value',policys[nodes[i]].name);
		        
		        theForm.appendChild(hidden);
		        //hidden = document.createElement('<INPUT TYPE="hidden" name="itemNames" value="' + policys[nodes[i]].value + '" />');
		        hidden = document.createElement('input');
		        hidden.setAttribute('type','hidden');
		        hidden.setAttribute('name','itemNames');
		        hidden.setAttribute('value',policys[nodes[i]].value);
		        
		        theForm.appendChild(hidden);
	        }
        }
           
        theForm.submit();

        getA8Top().startProc('');
    }
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

//检查标题是否含有特殊字符并且不能为空
function checkSubject(theForm){
	
    if(theForm.elements["my:subject"].value.trim()=="")	
	{
		alert(_("edocLang.edoc_inputSubject"));
		if(theForm.elements["my:subject"].disabled==true)
		{
			alert(_("edocLang.edoc_alertSetPerm"));
			return false;
		}    		
		theForm.elements["my:subject"].focus();
		return false;
	}
	if(!(/^[^\|"']*$/.test(theForm.elements["my:subject"].value))){
		alert(_("edocLang.edoc_inputSpecialChar"));
		if(theForm.elements["my:subject"].disabled==true)
		{
			alert(_("edocLang.edoc_alertSetPerm"));
			return false;
		}    		
		theForm.elements["my:subject"].focus();
		return false;
	}
	return true;
}
//检查主送单位、主送单位2的是否设置有值
function checkSendUnitAndSendUnit2(theForm){
	
	var sendAccount = document.getElementById("my:send_to");
	if(sendAccount && jsEdocType == 0){
		if(theForm.elements["my:send_to"].value.trim()==""){
			alert(_("edocLang.edoc_inputSendTo"));
	  		if(theForm.elements["my:send_to"].disabled==true){
				alert(_("edocLang.edoc_alertSetSendTo"));
				return false;
			}	  
	 		theForm.elements["my:send_to"].focus();
			return false;	
		}
	}
	
	var sendAccount2 = document.getElementById("my:send_to2");
	if(sendAccount2 && jsEdocType == 0){
		if(theForm.elements["my:send_to2"].value.trim()==""){
			alert(_("edocLang.edoc_inputSendTo2"));
	  		if(theForm.elements["my:send_to2"].disabled==true){
				alert(_("edocLang.edoc_alertSetSendTo"));
				return false;
			}	  
	 		theForm.elements["my:send_to2"].focus();
			return false;	
		}
	}
	return true;
}
function _saveOffice()
{
	try{
	var comm=document.getElementById("comm").value;
    var bodyType = document.getElementById("bodyType").value;
    if(bodyType!="HTML" && comm=="register" && canUpdateContent==false)
    {//登记时office正文不可以修改，保存前修改为可编辑模式，否则不保存
     	updateOfficeState("1,0");
    }
	}catch(e)
	{
		alert(e.description);
	}
	return saveOffice();
}

/**
 * 保存待发
 */
function save() {
    if(!checkEdocMark()) return;
	//验证交换单位是否有重复 -- start --
	var bool = checkExchangeAccountIsDuplicatedOrNot();
	
	if(bool == false){
		alert(_("edocLang.exchange_unit_duplicated"));
		return;
	}

    var theForm = document.getElementsByName("sendForm")[0];
    theForm.action = genericURL + "?method=save";
    
	if(validFieldData()==false){return;}
	if(compareTime()==false){return;}
	
	//节点权限引用更新
	if(!isCheckNodePolicyFlag){
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
	}	
	
    if (checkForm(theForm)) {
    	//标题不能为空并且不含有特殊字符	
    	if(!checkSubject(theForm)){return;}
    	//检查主送单位、主送单位2的是否设置有值
		if(!checkSendUnitAndSendUnit2(theForm)){return;}
    	//检查是否设置了工作流
    	if (!checkSelectWF()) {        
            return;
        }
        //检查签报公文文号是否已使用
        var edocMarkObj = document.getElementById("my:doc_mark");
        if(edocMarkObj){
	        var edocMark = _getWordNoValue(edocMarkObj);
	        var edocType = document.getElementById("edocType").value;
	        var summaryId=document.getElementById("summaryId").value;
	        if(edocType == "2" && edocMark != "" && !loadAndManualSelectedPreSend && checkMarkHistoryExist(edocMark,summaryId)){
	        	return;
	        }
        }
        //保存正文
       // checkExistBody();
       	fileId=newEdocBodyId;
        if (!saveOcx()) {
            return;
        }
        saveAttachment();
        disableButtons();
        isFormSumit = true;
        theForm.target = "_self";
        try{adjustReadFormForSubmit();}catch(e){}
        theForm.submit();	
        getA8Top().startProc('');
    }
}
function saveOcx(){
	var bodyType = document.getElementById("bodyType").value;
	if(bodyType == 'Pdf'){
	    return savePdf();
	}else{
	    return _saveOffice();
	}
}
function resend(from) {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return;
    }

    var checkedNum = 0;
    var summaryId = null;
    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            summaryId = id_checkbox[i].value;
            checkedNum ++;
        }
    }

    if (checkedNum == 0) {
        alert(_("edocLang.edoc_alertSelectResentItem"));
        return;
    }

    if (checkedNum > 1) {
        alert(v3x.getMessage("edocLang.edoc_alertCanotTurn"));
        return;
    }

    var data = {
        from : from,
        summaryId : summaryId
    }

    var action = collaborationCanstant.resendActionURL;
    var target = "_parent";
    var method = "GET";
    submitMap(data, action, target, method);
}

function checkSelectWF() {
    if (!hasWorkflow) {
        alert(v3x.getMessage("edocLang.edoc_selectWorkflow"));
        if(selfCreateFlow==false){return false;}
        doWorkFlow("new");

        return false;
    }

    return true;
}

function alertPigeonhole()
{
	alert(_("edocLang.edoc_alertPigeonhole"));
}

function sendFromWaitSend() {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return;
    }


    var hasMoreElement = false;
    var isSendImmediate = false;
    var len = id_checkbox.length;
    var summaryId = "";
    var caseId = "";
    var processId = "";
    var hasTemplete = false;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
        	summaryId = id_checkbox[i].value;
        	caseId = id_checkbox[i].caseId;
        	if(isSendImmediate){
        		alert(v3x.getMessage("edocLang.edoc_alertDontSelectMulti"));
    			return;
        	}
        	processId = id_checkbox[i].processId;
        	hasTemplete = id_checkbox[i].templeteId!="";
        	if(hasTemplete){
        		alert(v3x.getMessage("edocLang.edoc_alertPleaseDoubleClick"));
        		return;
        	}
            hasMoreElement = true;
            isSendImmediate = true;
        }
    }
    if (!hasMoreElement) {
        alert(v3x.getMessage("edocLang.edoc_alertSentItem"));
        return;
    }
	//check if need popup "select people" dialog	
	//模板公文不能够直接发送，所以这里不需要访问preSend匹配人和分支 modify by yuhj at 2010-8-13
    //但是非模板公文能从待办列表中选择直接发送，因此这里还是需要访问preSend(现在为prePop)匹配人，如果有离职人员，则应不让发送
	/*var html = $('#processModeSelectorContainer').html();	
	$('#processModeSelectorContainer').html("");
	if (!loadAndManualSelectedPreSend) {
	            disableButtons();   
	            $('#listForm').ajaxSubmit({url : genericURL + "?method=preSend&summaryId="+summaryId + "&caseId=" + caseId + "&processId=" + processId + "&currentNodeId=start&isFromTemplate="+hasTemplete,
	                type : 'post',
	                success : function(data) {                	
	                	if(data.trim().search("err:noflow")>=0)
						{
							alert(v3x.getMessage("edocLang.edoc_noWorkflowNoSend"));
							return;
						}
	                    $('#processModeSelectorContainer').html(data);
	                    if(document.getElementById("invalidateActivity")){
	                    	alert(_("collaborationLang.collaboration_invalidateActivity", document.getElementById("invalidateActivity").value));
	                        enableButtons();
	                    	return;
	                    }
	                    var ret = manualSelectByProcessMode();
	                    if (!ret) {
	                        loadAndManualSelectedPreSend = false;
	                        enableButtons();
	                        return;
	                    } else {
	                        loadAndManualSelectedPreSend = true;
	                        sendFromWaitSend();
	                    }
	                }
	            });
	            return;
	        }
	        $('#processModeSelectorContainer').html(html);*/
	//end 
    //alert("loadAndManualSelectedPreSend:="+loadAndManualSelectedPreSend);
    if (!loadAndManualSelectedPreSend) {
    	disableButtons();   
        $('#listForm').ajaxSubmit({
            url : colWorkFlowURL + "?method=prePopNew&summaryId="+summaryId + "&caseId=" + caseId + "&processId=" + processId + "&currentNodeId=start&isFromTemplate="+hasTemplete,
        	type : 'post',
        	async : false,
            success : function(data) {                	
            	//赋值给页面的popJsonId隐藏域
            	document.getElementById("popJsonId").value=data;
            	//转换成页面js的json对象
            	var dataObj= eval('('+data+')');
            	var invalidateActivity= dataObj.invalidateActivity;
            	if(dataObj.invalidateActivity!=""){
                	alert(_("collaborationLang.collaboration_invalidateActivity", dataObj.invalidateActivity));
                    enableButtons();
                	return;
                }
                var ret = manualSelectByProcessModePop(dataObj.isPop,dataObj.hasNewflow);
                if (!ret) {
                    loadAndManualSelectedPreSend = false;
                    enableButtons();
                    return;
                } else {
                    loadAndManualSelectedPreSend = true;
                    sendFromWaitSend();
                }
            }
        });
        return;
    }
    
    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            var affairId = id_checkbox[i].getAttribute("affairId");

            //var c = document.createElement("<input type='hidden' name='affairId' value='" + affairId + "'>")
		    var c = document.createElement('input');
		    c.setAttribute('type','hidden');
		    c.setAttribute('name','affairId');
		    c.setAttribute('value',affairId);
            
            theForm.appendChild(c);
        }
    }



    theForm.method = "post";
    theForm.action = genericURL + "?method=sendImmediate";
    theForm.target = "_parent";

    disableButtons();

    getA8Top().startProc('');


    theForm.submit();
}

function serializeElements(_elements) {
    function SerializableElement(_elements) {
        var elements = _elements;
        this.toFields = function() {
            if (!elements) {
                return "";
            }
            var personList = (elements && elements.length == 2) ? elements[0] : [];
            var flowType = (elements && elements.length == 2) ? elements[1] : 0;
            var str = "";
            for (var i = 0; i < personList.length; i++) {
                var person = personList[i];
                str += '<input type="hidden" name="userType" value="' + person.type + '" />';
                str += '<input type="hidden" name="userId" value="' + person.id + '" />';
                //        workFlowContent += person.name + ",";
            }
            str += '<input type="hidden" name="flowType" value="' + flowType + '" />';
            return str;
        }
        return this;
    }
    return SerializableElement(_elements);
}

function serializeElementsNoType(_elements) {
    function SerializableElement(_elements) {
        var elements = _elements;
        this.toFields = function() {
            if (!elements) {
                return "";
            }
            var str = "";
            var isShowShortName = false;
            if(document.getElementById("currentLoginAccountId")){
		    	var loginAccountId = document.getElementById("currentLoginAccountId").value;
			    for (var i = 0; i < elements.length; i++) {
				    var person = elements[i];
			    	if(person.accountId != loginAccountId){
			    		isShowShortName = true;
			    		break;
			    	}
			    }
			}
            
            for (var i = 0; i < elements.length; i++) {
                var person = elements[i];
                str += '<input type="hidden" name="userType" value="' + person.type + '" />';
                str += '<input type="hidden" name="userId" value="' + person.id + '" />';
                str += '<input type="hidden" name="userName" value="' + person.name + '" />';
                str += '<input type="hidden" name="accountId" value="' + person.accountId + '" />';
                str += '<input type="hidden" name="userExcludeChildDepartment" value="' + person.excludeChildDepartment + '" />';
		        str += '<input type="hidden" name="accountShortname" value="' + person.accountShortname + '" />';
            }
            str += '<input type="hidden" name="isShowShortName" value="' + isShowShortName + '" />';
            return str;
        }
        return this;
    }
    return SerializableElement(_elements);
}

function serializePeople(elements) {
    if (!elements) {
        return "";
    }

    var personList = (elements && elements.length == 2) ? elements[0] : [];
    var flowType = (elements && elements.length == 2) ? elements[1] : 0;

    var arr = [];
    //    var str = "";
    //    var workFlowContent = "";
    for (var i = 0; i < personList.length; i++) {
        var person = personList[i];
        //        str += '<input type="hidden" name="userType" value="' + person.type + '" />';
        //        str += '<input type="hidden" name="userId" value="' + person.id + '" />';

        var obj = new Object();
        obj.name = "userType";
        obj.value = person.type;
        arr.add(obj);

        obj = new Object();
        obj.name = "userId";
        obj.value = person.id;
        arr.add(obj);

        //        workFlowContent += person.name + ",";
    }

    //    str += '<input type="hidden" name="flowType" value="' + flowType + '" />';
    var obj = new Object();
    obj.name = "flowType";
    obj.value = flowType;
    arr.add(obj);

    hasWorkflow = true;
    return arr;
}

//加签选人后的回调
function selectInsertPeople(elements) {
    document.getElementById("selectPeoplePanel").innerHTML = "";
    if (!elements) {
        return false;
    }

    var personList = elements[0] || [];
    var flowType = elements[1] || 0;

    var str = "";
    var workFlowContent = "";
    for (var i = 0; i < personList.length; i++) {
        var person = personList[i];
        str += '<input type="hidden" name="userType" value="' + person.type + '" />';
        str += '<input type="hidden" name="userId" value="' + person.id + '" />';
        str += '<input type="hidden" name="userName" value="' + person.name + '" />';
        str += '<input type="hidden" name="accountId" value="' + person.accountId + '" />';
        str += '<input type="hidden" name="accountShortname" value="' + person.accountShortname + '" />';
        str += '<input type="hidden" name="flowcomm" value="add" />';

        workFlowContent += person.name + ",";
    }
    str += '<input type="hidden" name="flowType" value="' + flowType + '" />';
    document.getElementById("process_desc_by").value = "people";

    document.getElementById("selectPeoplePanel").innerHTML = str;
    hasWorkflow = true;
    
    try { getA8Top().startProc(''); }catch (e) { }

    var form = document.forms.theform;
    form.action = genericURL+"?method=insertPeople";
    form.target = "showDiagramFrame";
    form.submit();
    return false;
}

//减签辅助函数 - 提交减签人员名单
function commitDeletePeople(summary_id, affairId) {
    var people = [];
    var userName=[];
    var userType=[];
    var accountId = [];
    var accountShortname = [];
    var data = {
        userId : [],
        summary_id : summary_id,
        affairId : affairId,
        userName :[],
        userType :[],
        accountId : [],
        accountShortname : []
    };
    $("INPUT").each(function() {
        if (this.name == "deletePeople" && this.checked) {
            people[people.length] = this.value;
            userName[userName.length] = this.pname;
            userType[userType.length] = this.ptype;
            accountId[accountId.length] = this.paccountId;
            accountShortname[accountShortname.length] = this.paccountShortName;
        }
    });
    if (people.length == 0) {
        alert("至少选择一项！");
        return false;
    }

    data.userId = people;
    data.userName=userName;
    data.userType=userType;
    data.accountId=accountId;
    data.accountShortname=accountShortname;
    
    try { getA8Top().startProc(''); }catch (e) { }
    submitMap(data, genericURL+"?method=deletePeople","showDiagramFrame");
    //    $("#deletePeoplePanel").html("").css("display", "none");
    $("#deletePeoplePanel").hide();
    //("").css("display", "none");
    //    $("#darkbox").hide();
}

//取消减签
function cancelDeletePeople() {
    $("#deletePeoplePanel").html("").css("display", "none");
    $("#darkbox").hide();
    //hide("fast");
}

//终止,要求保存终止的意见,附件信息
function stepStop(theForm)
{
	var contentOP = theForm.contentOP;
	var opinionPolicy = theForm.opinionPolicy;
	if(opinionPolicy && opinionPolicy.value==1 && contentOP && contentOP.value == ''){
		disabledPrecessButtonEdoc(false);
		alert(v3x.getMessage("collaborationLang.collaboration_opinion_mustbe_gived"));
		return;
	}
	if(!checkModifyingProcessAndLock(theForm.processId.value, theForm.summary_id.value)){
		return;
	}
	
	//回退的时候清除匹配人员,避免因为下一节点没有匹配到人员禁止回退
	$('#processModeSelectorContainer').html("");
    if (checkForm(theForm))
    {
        if (!window.confirm(_("edocLang.edoc_confirmStepStopItem")))
        {
            return;
        }
                
        if(!parent.detailMainFrame.contentIframe.saveEdocForm())
		{
	  		return;
		}
		if(!parent.detailMainFrame.contentIframe.saveContent())
		{
	  		return;
		}
		if(!parent.detailMainFrame.contentIframe.saveHwData())
		{
	  		return;
		}
        
        theForm.action = genericURL + "?method=stepStop";
        saveAttachment();
        document.getElementById("processButton").disabled = true;
        try {
            document.getElementById("zcdbButton").disabled = true;
        } catch(e) {
        }
        
	    try { //如果是弹出窗口，则不能显示“处理中”
	        getA8Top().startProc('');
	    }
	    catch (e) {
	    }
	    
	    disableButton("stepStopSpan");
	    disableButton("stepBackSpan");
	    
        theForm.submit();
    }
}
//公文转发为，主要应用是收文转发文，发文也转发文
function transmitSend(summaryId,affairId,edocType)
{
	if(isEdocCreateRole!="true")
	{//没有公文发起权
		alert(_("edocLang.alert_not_edoccreate"));
		return;
	}
	//保存一份清除了痕迹的正文，否则查看转发的正文时，可以看到痕迹。
	if(typeof(fileId)!='string'){
		fileId="";
	}
	fileId=transmitSendNewEdocId; 
	//var bodyType=parent.detailMainFrame.contentIframe.bodyType ;
  	if(typeof(bodyType)!="undefined" && bodyType!="HTML" && bodyType!="Pdf"){
  	    //如果清除痕迹失败就返回
  	   if(!(removeTrailAndSave())) return;
  	}
  	var url=genericURL+"?method=newEdoc&comm=transmitSend&edocType="+edocType+"&edocId="+summaryId+"&transmitSendNewEdocId="+transmitSendNewEdocId;
    if(top.dialogArguments)
    {
    	var parentUrl=top.dialogArguments.location.href;
		
    	if(parentUrl.search("method=listPending")>0)
    	{			
    		top.dialogArguments.parent.location.href=url;
    		top.close();
    	}
		else
		{
			url=genericURL+"?method=entryManager&entry=newEdoc&comm=transmitSend&edocType="+edocType+"&edocId="+summaryId+"&transmitSendNewEdocId="+transmitSendNewEdocId;
			if(top.dialogArguments.contentFrame==undefined)
			{
                // 精灵打开
                if(top.contentFrame){
                    top.contentFrame.mainFrame.location.href=url;
                    return;
                }			
				getA8Top().contentFrame.mainFrame.location.href=url;
			}
			else
			{
				top.dialogArguments.contentFrame.mainFrame.location.href=url;
				top.close();
			}
		}
    }
    else
    {
    	parent.parent.location.href=url;	
    }	
}
//回退,要求保存回退的意�?附件信息
function stepBack(theForm)
{
	var contentOP = theForm.contentOP;
	var opinionPolicy = theForm.opinionPolicy;
	if(opinionPolicy && opinionPolicy.value==1 && contentOP && contentOP.value == ''){
		disabledPrecessButtonEdoc(false);
		alert(v3x.getMessage("collaborationLang.collaboration_opinion_mustbe_gived"));
		return;
	}
	if(!checkModifyingProcessAndLock(theForm.processId.value, theForm.summary_id.value)){
		return;
	}
	
	//回退的时候清除匹配人员,避免因为下一节点没有匹配到人员禁止回退
	$('#processModeSelectorContainer').html("");
    if (checkForm(theForm))
    {
        if (!window.confirm(_("edocLang.edoc_confirmStepBackItem")))
        {
            return;
        }
        if(!contentIframe.saveEdocForm())
		{
	  		return;
		}
		if(!saveContent())
		{
	  		return;
		}
		if(!contentIframe.saveHwData())
		{
	  		return;
		}
        theForm.action = genericURL + "?method=stepBack";
        saveAttachment();
        document.getElementById("processButton").disabled = true;
        try {
            document.getElementById("zcdbButton").disabled = true;
        } catch(e) {
        }
        
	    try { //如果是弹出窗口，则不能显示“处理中”
	        getA8Top().startProc('');
	    }
	    catch (e) {
	    }
	    
	    disableButton("stepStopSpan");
	    disableButton("stepBackSpan");
        
        theForm.submit();
    }
}


//会签
function selectColAssign(elements) {
	if(!checkModifyingProcessAndLock(process_Id, summary_id)){
		return;
	}
	
    if (!elements || elements == undefined) return;
    var people = serializeElementsNoType(elements);
    if (!people) {
        return;
    }
    var data = {
        summary_id : summary_id,
        affairId : affairId,
        people: people,
        flowcomm:"col"
    };
    try { getA8Top().startProc(''); }catch (e) { }    
    submitMap(data, genericURL + "?method=colAssign","showDiagramFrame","post");
}

//传阅选人回调函数
function selectPassRead(elements) {
	workflowUpdate = true ;	
	if(!checkModifyingProcessAndLock(process_Id, summary_id)){
		return;
	}
	
    if (!elements || elements == undefined) return;
    var people = serializeElementsNoType(elements);

    if (!people) {
        return;
    }

    var data = {
        summary_id : summary_id,
        affairId : affairId,
        people: people,
        flowcomm:"chuanyue"
    };
    try { getA8Top().startProc(''); }catch (e) { }
    submitMap(data, genericURL + "?method=addPassRead","showDiagramFrame");
}

//会签
function colAssign(_summary_id, _processId, _affairId) {
	if(!checkModifyingProcessAndLock(_processId, _summary_id)){
		return;
	}
	
	//检测xmls是否已经被加载
	initCaseProcessXML();
	
    //设置成全局变量
    summary_id = _summary_id;
    affairId = _affairId;
    process_Id = _processId;
    selectPeopleFun_colAssign();
}

//传阅
function addPassInform(_summary_id, _processId, _affairId, secretLevel) {
	if(!checkModifyingProcessAndLock(_processId, _summary_id)){
		return;
	}
	
	//检测xmls是否已经被加载
	initCaseProcessXML();
	if(v3x.getBrowserFlag('pageBreak')){
	    //设置成全局变量
	    summary_id = _summary_id;
	    affairId = _affairId;
	    process_Id = _processId;
	    flowSecretLevel_passRead = secretLevel;
	    selectPeopleFun_passRead();
	}else{
		var app = document.getElementById("appName");
		var appName = "";
		if(app){
			appName = app.value;
		}
		var divObj = "<div id=\"addInformWin\" closed=\"true\">" +
					 	"<iframe id=\"addInformWin_Iframe\" name=\"addInformWin_Iframe\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"0\"></iframe>" +
					 "</div>";
		$(divObj).appendTo("body");
		$("#addInformWin").dialog({
			title: v3x.getMessage("collaborationLang.alert_select_person"),
			top: 50,
			left:50,
			width: 630,
			height: 450,
			closed: false,
			modal: true,
			buttons:[{
						text:v3x.getMessage("collaborationLang.submit"),
						handler:function(){
							var rv = $("#addInformWin_Iframe").get(0).contentWindow.OK();
							$('#addInformWin').dialog('destroy');
						}
					},{
						text:v3x.getMessage("collaborationLang.cancel"),
						handler:function(){
						$('#addInformWin').dialog('destroy');
						}
					}]
		});
		$("#addInformWin_Iframe").attr("src",colWorkFlowURL + "?method=preAddInform&summaryId=" + _summary_id + "&affairId=" + _affairId + "&processId=" + _processId + "&appName="+appName);
	}
}


function setPeopleFields(elements, frameNames) {
    var theForm = document.getElementsByName("sendForm")[0];
    document.getElementById("people").innerHTML = "";
    if (!elements) {
        return false;
    }
    var personList = elements[0] || [];
    var flowType = elements[1] || 0;
    var isShowShortName = elements[2];
    var isShowWorkflow = (flowType == "2");
    //多层,直接弹出编辑流程图页面,已选中成员为并发模式
    if (isShowWorkflow) {
        flowType = 0;
    }
    var str = new StringBuffer();
    var workFlowContent = "";
    
    str.append("<processes>");
	str.append("<process isShowShortName=\"" + isShowShortName + "\" index=\"0\" sortIndex=\"0\" flowType=\"workflow\" y=\"0\" x=\"0\" type=\"Node\" desc=\"\" name=\"\" id=\"\">")

	//开始节点
	str.append("<node sortIndex=\"0\" y=\"\" x=\"\" type=\"8\" desc=\"\" name=\"\" id=\"start\">");
	str.append("<actor accountShortName=\"\" accountId=\"\" condition=\"1\" partyIdName=\"\" partyTypeName=\"\" partyId=\"\" partyType=\"\" includeChild=\"false\" const=\"false\" role=\"\"/>");
	str.append("<seeyonPolicy matchScope=\"1\" operationName=\"\" form=\"\" formApp=\"\" isOvertopTime=\"0\" processMode=\"multiple\" remindTime=\"\" dealTerm=\"\" isDelete=\"false\" isPass=\"success\" desc=\"\" type=\"\" name=\"\" id=\"collaboration\"/>");
	str.append("</node>");
	
	//结束节点
	str.append("<node sortIndex=\"0\" y=\"430\" x=\"720\" type=\"4\" desc=\"\" name=\"end\" id=\"end\">");
	str.append("<actor accountShortName=\"\" accountId=\"\" condition=\"1\" partyIdName=\"\" partyTypeName=\"\" partyId=\"\" partyType=\"\" includeChild=\"false\" const=\"false\" role=\"\"/>");
	str.append("<seeyonPolicy matchScope=\"1\" operationName=\"\" form=\"\" formApp=\"\" isOvertopTime=\"0\" processMode=\"multiple\" remindTime=\"\" dealTerm=\"\" isDelete=\"false\" isPass=\"success\" desc=\"\" type=\"\" name=\""+_("collaborationLang.collaboration")+"\" id=\"collaboration\"/>");
	str.append("</node>");
	
	var nodeIds = [];
	
	var PolicyName = null;
	var PolicyID = null;
	if(appName == "sendEdoc" || appName == "signReport"){
		PolicyName = _("collaborationLang.edoc_sendEdocPolicyName");
		PolicyID = "shenpi";
	}
	else if(appName == "recEdoc"){
		PolicyName = _("collaborationLang.edoc_recEdocPolicyName");
		PolicyID = "yuedu";
	}
	 else if(appName == "sendInfo"){
		defaultPolicyId = "shenhe";
		defaultPolicyName = _('collaborationLang.info_sendInfoPolicyName');
	} 

    for (var i = 0; i < personList.length; i++) {
        var person = personList[i];
		
		var nodeId = getUUID();
		
		nodeIds[nodeIds.length] = nodeId
		
		var type = person.type;
		if(type == 'Member'){
			type = 'user';
		}
		//单位简称特殊字符转义&
		var pShortName = person.accountShortname || "";
		if(pShortName){
			pShortName = pShortName.escapeXML();
		}
		
		var matchScope = 1;
		if(type == "Post" && person.accountId == "-1730833917365171641"){ //判断是否是集团标准岗位，偷了个懒，直接比id
			matchScope = 2; //集团标准岗，全集团范围内匹配
		}
		
		str.append("<node task_num_value=\"1\" personStatus=\"normal\" task_num=\"false\" finishNum2=\"1000\" finishNum=\"0\" type=\"6\" desc=\"\" name=\"" + person.name.escapeXML() + "\" id=\"" + nodeId + "\">");
		str.append("<actor accountShortName=\"" + pShortName + "\" accountId=\"" + person.accountId + "\" condition=\"1\" partyIdName=\"" + person.name.escapeXML() + "\" partyTypeName=\"\" partyId=\"" + person.id + "\" partyExcludeChildDepartment=\"" + person.excludeChildDepartment + "\" partyType=\"" + type + "\" includeChild=\"false\" const=\"false\" role=\"roleadmin\"/>");
		str.append("<seeyonPolicy matchScope=\"" + matchScope + "\" operationName=\"\" form=\"\" formApp=\"\" isOvertopTime=\"0\" processMode=\"all\" remindTime=\"\" dealTerm=\"\" isDelete=\"false\" isPass=\"success\" desc=\"\" type=\"\" name=\""+PolicyName+"\" id=\""+PolicyID+"\"/>");
		str.append("</node>");
		
		if(i < 12){
			if(i > 0){
	    		workFlowContent += _("V3XLang.common_separator_label")
	    	}
			var _text = person.name + "(" + PolicyName + ")";
	        if(isShowShortName == true && person.accountShortname != "null" && person.accountShortname != "undefined" && person.accountShortname != ""){
	        	_text = "(" + person.accountShortname + ")" + _text;
	        }
	        workFlowContent += _text;
		}
    }
    
    if(flowType == 0 || nodeIds.length == 1){ //串发(1个节点算串发)：两个节点之间创建线
		str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="' + nodeIds[0] + '" from="start" type="11" desc="" name="" id="' + getUUID() + '"/>');
		str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="end" from="' + nodeIds[nodeIds.length - 1] + '" type="11" desc="" name="" id="' + getUUID() + '"/>');
    	
    	for(var i = 0; i < nodeIds.length - 1; i++) {
    		var f = nodeIds[i];
    		var t = nodeIds[i + 1];
    		
			str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="' + t + '" from="' + f + '" type="11" desc="" name="" id="' + getUUID() + '"/>');
    	}
	}
	else{ //并发：创建split\join
		var splitId = getUUID();
		var joinId = getUUID();
		
		str.append("<node parallelismNodeId=\"\" start=\"true\" y=\"430\" x=\"240\" type=\"2\" desc=\"\" name=\"split\" id=\""+ splitId +"\">");
		str.append("<seeyonPolicy matchScope=\"1\" operationName=\"\" form=\"\" formApp=\"\" isOvertopTime=\"0\" processMode=\"all\" remindTime=\"\" dealTerm=\"\" isDelete=\"false\" isPass=\"success\" desc=\"\" type=\"\" name=\"协同\" id=\"collaboration\"/>");
		str.append("</node>");
		str.append("<node parallelismNodeId=\"\" start=\"false\" y=\"430\" x=\"560\" type=\"2\" desc=\"\" name=\"join\" id=\"" + joinId + "\">");
		str.append("<seeyonPolicy matchScope=\"1\" operationName=\"\" form=\"\" formApp=\"\" isOvertopTime=\"0\" processMode=\"all\" remindTime=\"\" dealTerm=\"\" isDelete=\"false\" isPass=\"success\" desc=\"\" type=\"\" name=\"协同\" id=\"collaboration\"/>");
		str.append("</node>");
		
		str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="' + splitId + '" from="start" type="11" desc="" name="" id="' + getUUID() + '"/>');
		str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="end" from="' + joinId + '" type="11" desc="" name="" id="' + getUUID() + '"/>');
		
		for(var i = nodeIds.length - 1; i >= 0; i--) {
			var nId = nodeIds[i];
			
			str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="' + nId + '" from="' + splitId + '" type="11" desc="" name="" id="' + getUUID() + '"/>');
			str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="' + joinId + '" from="' + nId + '" type="11" desc="" name="" id="' + getUUID() + '"/>');
		}
	}

    str.append("</process></processes>");
    
    caseProcessXML = str.toString();
    
    hasWorkflow = true;
    isFromTemplate = false;
    
    var workflowInfoObj = document.getElementById("workflowInfo");
    if(workflowInfoObj){
    	workflowInfoObj.value = workFlowContent;
    }
	
    theForm.process_xml.value = caseProcessXML;
    theForm.process_desc_by.value = 'xml';

    if (isShowWorkflow) {    //显示流程
        designWorkFlow(frameNames);
    }

    return true;
}

/*
 
function selectPolicy(nodeName, policyName, dealTerm, remindTime, processMode, isNewCollaboration) {
    var heightDifference = 0;
    if (isNewCollaboration) {
        heightDifference = 60;
    }
    var rv = v3x.openWindow({
        url: genericURL + "?method=selectPolicy&nodeName=" + encodeURIComponent(nodeName)
                + "&policyName=" + encodeURIComponent(policyName) + "&dealTerm=" + encodeURIComponent(dealTerm)
                + "&remindTime=" + encodeURIComponent(remindTime) + "&processMode=" + encodeURIComponent(processMode)
                + "&isNewCollaboration=" + encodeURIComponent(isNewCollaboration),

        height:300 + heightDifference,
        width:300
    });

    if (!rv || rv.length == 0) return null;
    return rv;
}

function checkPolicy(stateStr, nodeName, nodePolicy, receiveTime, dealTime, overtopTime) {
    v3x.openWindow({
        url: genericURL + "?method=checkPolicy&stateStr=" + encodeURIComponent(stateStr)
                + "&nodeName=" + encodeURIComponent(nodeName) + "&nodePolicy=" + encodeURIComponent(nodePolicy)
                + "&receiveTime=" + encodeURIComponent(receiveTime) + "&dealTime=" + encodeURIComponent(dealTime) + "&overtopTime=" + encodeURIComponent(overtopTime),
        height:310,
        width:300
    });
}
*/

function openDetail(subject, _url) {
    _url = genericURL + "?method=detail&" + _url;
    var rv = v3x.openWindow({
        url: _url,
        workSpace: 'yes',
        dialogType: v3x.getBrowserFlag('pageBreak') == true ? 'modal' : '1'
    });

    if (rv == "true") {
        getA8Top().reFlesh();
        //        parent.location.href = '../../../portal/_ns:YVAtMTBkZjFmNmRjMWItMTAwMDF8YzB8ZDB8ZV9zcGFnZT0xPS9jb2xsYWJvcmF0aW9uLmRv/seeyon/collaboration.psml?method=collaborationFrame&from=Pending'
        //        top.showPanel('collaboration_pending');
    }

}

/**
 * 删除
 */
function deleteIt() {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return;
    }

    var hasMoreElement = false;
    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            hasMoreElement = true;
            break;
        }
    }

    if (!hasMoreElement) {
        alert(v3x.getMessage("edocLang.edoc_alertDeleteItem"));
        return;
    }

    if (window.confirm(v3x.getMessage("edocLang.edoc_confirmDeleteItem"))) {
        theForm.action = deleteActionURL;

        disableButtons();
        theForm.target = "_self";
        theForm.submit();
    }
}

var page_types = {
    'draft' : 'draft',
    'sent' : 'sent',
    'pending' : 'pending',
    'finish' : 'finish'
}
function deleteItems(pageType) {
    if (!pageType || !page_types[pageType]) {
        alert('pageType is illegal:' + pageType);
        return false;
    }

    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return true;
    }

    var hasMoreElement = false;
    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            hasMoreElement = true;
            break;
        }
    }

    if (!hasMoreElement) {
        alert(v3x.getMessage("edocLang.edoc_alertDeleteItem"));
        return true;
    }

    if (window.confirm(v3x.getMessage("edocLang.edoc_confirmDeleteItem"))) {
        theForm.action = collaborationCanstant.deleteActionURL;

        disableButtons();

        for (var i = 0; i < id_checkbox.length; i++) {
            var checkbox = id_checkbox[i];
            if (!checkbox.checked)
                continue;
            var affairId = checkbox.getAttribute("affairId");
            //var element = document.createElement("<INPUT TYPE=HIDDEN NAME=affairId value='" + affairId + "' />");
            var element = document.createElement("input");
            	element.setAttribute('type','hidden');
            	element.setAttribute('name','affairId');
            	element.setAttribute('value',affairId);
            theForm.appendChild(element);
        }

        //var element = document.createElement("<INPUT TYPE=HIDDEN NAME=pageType value='" + pageType + "' />");
        var element2 = document.createElement("input");
    	element2.setAttribute('type','hidden');
    	element2.setAttribute('name','pageType');
    	element2.setAttribute('value',pageType);
        theForm.appendChild(element2);

        theForm.target = "tempIframe";
        theForm.method = "POST";
        theForm.submit();
        return true;
    }
}

//取回
function takeBack(pageType) {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return true;
    }

    var hasMoreElement = false;
    var len = id_checkbox.length;
    var countChecked = 0;
    var obj;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
        	obj = id_checkbox[i];
            hasMoreElement = true;
            countChecked++;
        }
    }

    if (!hasMoreElement) {
        alert(v3x.getMessage("edocLang.edoc_alertTakeBackItem"));
        return true;
    }
    
    if(countChecked > 1){
    	alert(v3x.getMessage("edocLang.edoc_alertSelectTakeBackOnlyOne"));
        return true;
    }
    var nodePolicyValue= obj.getAttribute("nodePolicy");
	if(nodePolicyValue == "zhihui" || nodePolicyValue == "inform"){
    	alert(v3x.getMessage("collaborationLang.collaboration_zhihuiTakeBackItem"));
        return;
    }
    //已结束流程不能取回
	 var isFinishValue= obj.getAttribute("isFinish");
    if(isFinishValue == "true"){
    	alert(alert_cannotTakeBack);
    	return false;
    }
	//由主流程自动触发的新流程不可撤销
    var isNewflowValue= obj.getAttribute("isNewflow");
    if(isNewflowValue == "true"){
    	alert(v3x.getMessage("collaborationLang.warn_workflowIsNewflow_cannotTakeBack"));
        return;
    }    
    
 	var summaryId = obj.value;
 	var processIdValue= obj.getAttribute("processId");
	if(!checkModifyingProcess(processIdValue, summaryId)){
		return;
	}
    if (window.confirm(v3x.getMessage("edocLang.edoc_confirmTakeBackItem"))) {        
        theForm.action=collaborationCanstant.takeBackURL;

        disableButtons();
        var affairId = obj.getAttribute("affairId");
        //var element = document.createElement("<INPUT TYPE=HIDDEN NAME=affairId value='" + affairId + "' />");
        var element = document.createElement('input');
        element.setAttribute('type','hidden');
        element.setAttribute('name','affairId');
        element.setAttribute('value',affairId);
       // var element1 = document.createElement("<INPUT TYPE=HIDDEN NAME=summaryId value='" + summaryId + "' />");
        var element1 = document.createElement('input');
        element1.setAttribute('type','hidden');
        element1.setAttribute('name','summaryId');
        element1.setAttribute('value',summaryId);
        
        theForm.appendChild(element);
        theForm.appendChild(element1);

       // var element = document.createElement("<INPUT TYPE=HIDDEN NAME=pageType value='" + pageType + "' />");
        var element3 = document.createElement('input');
        element3.setAttribute('type','hidden');
        element3.setAttribute('name','pageType');
        element3.setAttribute('value',pageType);
        
        theForm.appendChild(element3);

        theForm.target = "tempIframe";
        theForm.method = "POST";
        getA8Top().startProc('');
        theForm.submit();
        return true;
    }
}

//催办回调
function hastenCallback() {
    $("#flashContainer").children(".information").css("background-color", "yellow").html(_("collaborationLang.operation_completed"));
    setTimeout('$("#flashContainer").children(".information").hide()', 2000);
}

//催办提交
function submitHasten() {
    var val = $(".additional_remark");
    val = val[0].value;
    //[$(".additional_remark").length-1].val();
    //    TB_remove();
    $("#hastenPanel").hide();
    var data = {
        processId : processId,
        activityId : activityId,
        additional_remark : val
    };
    if ($("#flashContainer").children(".information").length == 0) {
        $("#flashContainer").prepend("<div class='information'>" + _("collaborationLang.operation_processing") + "</div>");
    }
    $("#flashContainer").children(".information").css("background-color", "#FF0000").show();


    $.post(collaborationCanstant.hastenActionURL, data, hastenCallback);
}

//催办功能的两个全局变量
var processId = null;
var activityId = null;
//催办
function hasten(_processId, _activityId) {
    processId = _processId;
    activityId = _activityId;
    //    TB_show(_("collaborationLang.hasten_caption"),"#TB_inline?width=300&height=400&inlineId=hastenPanel",false);

    $("#hastenPanel").show();


    return false;
}

//催办对话框取�?
function cancelHasten() {
    $("#hastenPanel").hide();
}


////催办
//function hastenItems(pageType) {
//    if (!pageType || !page_types[pageType]) {
//        alert('pageType is illegal:' + pageType);
//        return false;
//    }
//
//    var theForm = document.getElementsByName("listForm")[0];
//    if (!theForm) {
//        return false;
//    }
//
//    var id_checkbox = document.getElementsByName("id");
//    if (!id_checkbox) {
//        return true;
//    }
//
//    var hasMoreElement = false;
//    var len = id_checkbox.length;
//    for (var i = 0; i < len; i++) {
//        if (id_checkbox[i].checked) {
//            hasMoreElement = true;
//            break;
//        }
//    }
//
//    if (!hasMoreElement) {
//        alert(v3x.getMessage("edocLang.edoc_alertHastenItem"));
//        return true;
//    }
//
//    if (window.confirm(v3x.getMessage("edocLang.edoc_confirmHastenItem"))) {
//        theForm.action = collaborationCanstant.hastenActionURL;
//
//        disableButtons();
//
//        for (var i = 0; i < id_checkbox.length; i++) {
//            var checkbox = id_checkbox[i];
//            if (!checkbox.checked)
//                continue;
//            var affairId = checkbox.getAttribute("affairId");
//            var element = document.createElement("<INPUT TYPE=HIDDEN NAME=affairId value='" + affairId + "' />");
//            theForm.appendChild(element);
//        }
//
//        var element = document.createElement("<INPUT TYPE=HIDDEN NAME=pageType value='" + pageType + "' />");
//        theForm.appendChild(element);
//
//        theForm.target = "_self";
//        theForm.submit();
//        return true;
//    }
//}

//从待办列表中直接发送的选人界面
function selectPeopleSendImmediate() {
    selectPeopleFun_receive();
}

function selectPeopleFromWaitSend(elements) {
    var str = serializePeople(elements);

}

function forwardItem() {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return true;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return true;
    }

    var selectedCount = 0;
    var summaryId = null;
    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            summaryId = id_checkbox[i].value;
            selectedCount ++;
        }
    }

    if (selectedCount == 0) {
        alert(v3x.getMessage("edocLang.edoc_alertSelectForwardItem"));
        return true;
    }

    if (selectedCount > 1) {
        alert(v3x.getMessage("edocLang.edoc_alertSelectForwardOnlyOne"));
        return true;
    }

    var rv = getA8Top().v3x.openWindow({
        url : genericURL + "?method=showForward&summaryId=" + summaryId,
        height : 400,
        width : 360
    });

    return true;
}

function initProcessXml(){
	document.getElementsByName("sendForm")[0].process_xml.value = caseProcessXML;
}

function disableButtons() {
    disableButton("send");
    disableButton("save");
    disableButton("saveAs");
    disableButton("templete");
    disableButton("delete");
}

function enableButtons() {
    enableButton("send");
    enableButton("save");
    enableButton("saveAs");
    enableButton("templete");
    enableButton("delete");
}

function openDetailNewWin(url) {
    var rv = getA8Top().v3x.openWindow({
        url: url,
        workSpace: "true"
    });

    if (rv != null && (rv == "true" || rv)) {
        refreshIt();
    }
}

//function selectWorkitemOrNot(checked, itemId) {
//    if (checked)
//        workitemSelected[workitemSelected.length] = itemId;
//    else {
//        for (var i = workitemSelected.length - 1; i >= 0; i--) {
//            if (workitemSelected[i] == itemId)
//                workitemSelected.splice(i, 1);
//        }
//    }
//}

function showDetail(detailURL1) {
    //    setTimeout("parent.detailFrame.location.href = '" + detailURL + "'",1000);
    parent.detailFrame.location.href = genericURL + "?method=detail&" + detailURL1;
}

function doWorkFlow(flag) {
    if (flag == "no") {
        //TODO 清空流程
    }
    else if (flag == "new") {
    	try{	 
    		// 2017-01-18 诚佰公司
        	if (!editWorkFlow()) {
        		return;
        	}
        	var result=selectPeopleFun_wf();    
    	}catch(e){}    
    }
}
function selectPersonToXml()
{
  processing=true;
  var url=genericControllerURL + "collaboration/monitor&comm=toxml";
  var toXmlFrame=document.getElementById("toXmlFrame");
  toXmlFrame.src=url;
}

/**
 * 处理后事�?
 */
function doEndSign() {    
	if(window.opener){
		window.close();
	}
	
    if (window.dialogArguments) {
        window.returnValue = "true";
        top.close();
    }
    else {
        parent.location.href = genericURL + '?method=edocFrame&from=listPending&edocType='+edocType;
        getA8Top().showPanel('collaboration_pending');
    }
}

function manualSelectByProcessMode() {
	if(!document.getElementById("showProcessModeSelector")){
		alert("操作失败：网络异常，系统无法获取所需的提交数据。");
		return false;
	}
	
    var showProcessModeSelector = document.getElementById("showProcessModeSelector").innerText;
    if (showProcessModeSelector == '2') {
        var rv = v3x.openWindow({
            url: genericControllerURL + "collaboration/popupProcessModeSelector&app=edoc",
            width: "580",
            height: "500"
        });
        if (rv != "True") {
            return false;
        }
    }
    else if (showProcessModeSelector == '1') {
        //选人界面

    }
    return true;

}

/**
 * manualSelectByProcessModePop()
 * 弹出流程选择页面处理
 * @param isPop 是否弹出
 * @param hasNewflow 是否有新流程
 * @returns {Boolean}
 */
function manualSelectByProcessModePop(isPop,hasNewflow) {
	var popDataObj= $('#popJsonId').val();
	if(popDataObj==""){
		alert("操作失败：网络异常，系统无法获取所需的提交数据。");
		return false;
	}
    if (isPop=="true") {
        var rv = v3x.openWindow({
        	url: genericControllerURL + "collaboration/popupProcessModeSelector&app=edoc&secretLevel="+flowSecretLevel_wf,
            width: 580,
            height: 500
        });
        if (rv != "True") {
            return false;
        }
    }
    return true;
}

/**
 * 验证内部文号是否重复，除开自己。
 */
function checkSerialNoExcludeSelf(){
	//	格式：serialNoValue	"0|String||3"	手写输入
	
	var serialNo=document.getElementById("my:serial_no");
	if(!serialNo)	return true;
	var serialNoValueStr=serialNo.value;
 	var serialNoValueStr=serialNoValueStr.substring(serialNoValueStr.indexOf("|")+1);
 	var serialNoValue=serialNoValueStr.substring(0,serialNoValueStr.indexOf("|"));//文号
 	
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocController", "checkSerialNoExcludeSelf", false);
	requestCaller.addParameter(1, "String", summaryId);
	requestCaller.addParameter(2,"String",serialNoValue);
	var rs = requestCaller.serviceRequest();
	//(0:未使用内部文号 | 1：已使用该内部文号)
	if(rs == "1"){//已经被占用
		alert(_("edocLang.doc_innermark_used"));
		return false;
	}
	return true;
}
/**
 * 校验是否已经选择了归档路径
 */
function isSelectPigeonholePath(){
    var selectObj = document.getElementById("archiveId");
    if(selectObj){
        var archiveId=selectObj.value;
        if(archiveId == ''){
            alert(_("edocLang.edoc_alertPleaseSelectPigeonholePath"));
            return false;
        }
        //如果设置了预归档路径，Aajax判断归档路径是否存在,没有设置预归档为了性能则不判断。
        if(hasPrepigeonholePath == 'true'){
            if(!checkPigFolder(archiveId)) {
             //设置选择框可见
                var showPrePigeonhole = document.getElementById("showPrePigeonhole");
    	        var showSelectPigeonholePath = document.getElementById("showSelectPigeonholePath");
    	        if(showPrePigeonhole && showSelectPigeonholePath){
    	            showPrePigeonhole.style.display="none";
    	            showSelectPigeonholePath.style.display="";
    	        }
                return false;
            }
        }
        return true;
    }else{
         //TODO 页面没有找到对象
    }
    return false;
}
function checkPigFolder(archiveId){
    var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "docResourceExist", false);
	requestCaller.addParameter(1, "Long", archiveId);
	var rs = requestCaller.serviceRequest();
	if(rs == 'false'){
	   alert(_("edocLang.edoc_alertPigeonholeFolderNotExsit")); 
	   return false;
	}
	return true;
}
function doSign(theForm, action) {
	 isSubmitOperation = true;
    //验证归档路径的选择
    var pipeonhole=document.getElementById("pipeonhole");
    if(pipeonhole && pipeonhole.checked){//假设选择了处理后归档，则判断是否选择了文件路径 && 归档文件夹是否存在
        if(!isSelectPigeonholePath()) return;
    }
	//验证内部文号是否被占用
	if(contentIframe.isUpdateEdocForm){//修改了公文单
		if(!contentIframe.checkSerialNoExcludeSelf())return;
	}
	
	//增加对公文文号长度校验，最大长度不能超过66，主要考虑归档时，doc_metadata表长度200
    if(!contentIframe.checkEdocMark())
      	return;
	
	disabledPrecessButtonEdoc();
	//验证交换单位是否有重复 -- start --
	var bool =contentIframe.checkExchangeAccountIsDuplicatedOrNot();
	
	if(bool == false){
		alert(_("edocLang.exchange_unit_duplicated"));
		disabledPrecessButtonEdoc(false);
		return;
	}
	
	if (checkForm(theForm)) {
		
	//意见不能为空	
	var content = document.getElementById("contentOP");
	var opinionPolicy = document.getElementById("opinionPolicy");
	if(opinionPolicy && opinionPolicy.value == 1 && content){
		if(content.value == ''){
			alert(v3x.getMessage("edocLang.edoc_opinion_mustbe_gived"));
			disabledPrecessButtonEdoc(false);
			return;
		}
	}
	
//	if (!manualSelectByProcessMode())
//        return;

	if(!checkModifyingProcess(theForm.processId.value, theForm.summary_id.value)){
		disabledPrecessButtonEdoc(false);
		return;
	}

	//检测xmls是否已经被加载
	//initCaseProcessXML();

	//节点匹配
	    //var html = $('#processModeSelectorContainer').html();
	    //$('#processModeSelectorContainer').html("");
	    if (theForm.isMatch.value == "true" && !loadAndManualSelectedPreSend) {
	    	//disableButtons();		    	
	        $('#theform').ajaxSubmit({
	        	//url : colWorkFlowURL + "?method=preSend&isFromTemplate="+templateFlag,
	        	url : colWorkFlowURL + "?method=prePopNew&isFromTemplate="+templateFlag,
	            type : 'POST',
	            async : false,
	            success : function(data) {
	            	//$('#processModeSelectorContainer').html(data);
	            	//赋值给页面的popJsonId隐藏域
	            	document.getElementById("popJsonId").value=data;
	            	//转换成页面js的json对象
	            	var dataObj= eval('('+data+')');
		            //jQuery的html方法有性能问题
		            //document.getElementById('processModeSelectorContainer').innerHTML= data;
		            var invalidateActivity= dataObj.invalidateActivity;
	            	if(dataObj.invalidateActivity!=""){
                    	//alert(_("collaborationLang.collaboration_invalidateNode", document.getElementById("invalidateActivity").value));
                    	alert(_("collaborationLang.collaboration_invalidateNode", dataObj.invalidateActivity));
                    	disabledPrecessButton(false);
                    	return;
                    }
		            var ret = manualSelectByProcessModePop(dataObj.isPop,dataObj.hasNewflow);
		            //var ret = manualSelectByProcessMode();
		            if (!ret) {
		            	loadAndManualSelectedPreSend = false;
		                //$('#processModeSelectorContainer').html("");
		                enableButtons();
		                disabledPrecessButtonEdoc(false);
		                return;
		            } else {
		            	loadAndManualSelectedPreSend = true;
		                doSign(theForm, action);
		            }
	            }
	        });
	        return;
	    }
	    //$('#processModeSelectorContainer').html(html);

	if(!contentIframe.saveEdocForm())
	{
		disabledPrecessButtonEdoc(false);
	  return;
	}
	checkExistBody();
	//保存ISIGNATURE HTML网页签章。
	if(bodyType =='HTML' && isSigned){
		if(!htmlContentIframe.saveISignatureHtml())
			return false;
	}
	if(!saveContent())
	{
		disabledPrecessButtonEdoc(false);
	    return;
	} 
	//word转PDF
	//try{if(!convertToPdf(theForm))return false; }catch(e){}
	
	if(!contentIframe.saveHwData())
	{
		disabledPrecessButtonEdoc(false);
	    return;
	}
    
/*
        if (document.getElementById("delete").checked && !window.confirm(_("edocLang.edoc_confirmSignAfterDelete"))) {
            return;
        }

        saveAttachment();
        document.getElementById("processButton").disabled = true;
        try {
            document.getElementById("zcdbButton").disabled = true;
        } catch(e) {
        }
*/

		superviseRemindMode();
		//var bool = superviseCheck();
		//if(bool==false){
		//	return false;
		//}
		if(!checkModifyingProcess(theForm.processId.value, theForm.summary_id.value)){
			disabledPrecessButtonEdoc(false);
			return;
		}
		//保存当前处理人提交的处理意见所带的附件
		saveAttachment();
		
		if(contentIframe.checkUpdateHw()){				
		  try{
			  recordChangeWord(theForm.affair_id.value ,theForm.summary_id.value ,",wendanqianp",theForm.ajaxUserId.value);			
			}catch(e){}
		}	    
		theForm.action = action;
		var branchNodes = document.getElementById("allNodes");
        if(branchNodes && branchNodes.value){
        	theForm.action += "&branchNodes=" + branchNodes.value;
        }
        theForm.method = "POST";
        theForm.target = "_self";
        theForm.submit();
        
        try { //如果是弹出窗口，则不能显示“处理中”
	        getA8Top().startProc('');
        }
        catch (e) {
        }
        
        return;
    }
    disabledPrecessButtonEdoc(false);
}
function convertToPdf(){
    //只有WORD和WPS具有转PDF的功能
    var bodyType=document.getElementById("bodyType").value;
    if( bodyType=="OfficeWord"  ||  bodyType=="WpsWord" ){
       if(convertWordToPdf()){
    	   alert(v3x.getMessage("edocLang.edoc_tans2PdfSuccess"));
       }else{
    	   alert(v3x.getMessage("edocLang.edoc_tans2PdfError"));
       }
    }else{
    	alert(v3x.getMessage("edocLang.edoc_tans2PdfOnlyWordAndWps"));
    }
    
}
function convertWordToPdf(){ 
    var isunit     = document.getElementById("isUniteSend");
	var hasExchange = document.getElementById("edocExchangeType_depart");
	var newPdfIdFirst  = document.getElementById("newPdfIdFirst").value;
	var newPdfIdSecond = document.getElementById("newPdfIdSecond").value;
	//if(hasExchange && canTransformToPdf=="true"){
	    
	     //联合发文不支持转PDF
     if(isunit && isunit.value=="true") return true;
     
     if(!transformWordToPdf(newPdfIdFirst))
     {
       return false;
     }
	     //联合发文暂时不支持转PDF，但是代码机构基本出来了，所以保留，下面联合发文的代码，但是实际执行的时候进不到下面来的。
//    	 if(isunit && isunit.value=="true"){
//    	      if(!transformWordToPdf(newPdfIdSecond))
//    	      {
//    	        return true;
//    	      }
//    	 }
        //增加这个隐藏域主要是用来告诉服务器当前操作是否成功的执行了转PDF操作，如果是的话后台需要保存PDF相关的信息。
	document.getElementById("isConvertPdf").value="isConvertPdf";
	//document.getElementById("newPdfIdFirst").value=newPdfIdFirst;
    //	document.getElementById("newPdfIdSecond").value=newPdfIdSecond;
	//}
	return true;
}
function edocContentUnLoad()
{
	try{
		unLoadHtmlHandWrite();
	}catch(e){}
}

function superviseCheck(){
		var mId = document.getElementById("supervisorMemberId");
		var sDate = document.getElementById("superviseDate");
		if(mId!=null && sDate!=null){//首先判断有无督办时间和督办人员的元素
			if(mId.value == "" && sDate.value != ""){//如果选择了督办时间,而督办人员没有选,提示
				alert(v3x.getMessage("edocLang.edoc_supervise_select_member"));
				return false;
			}
			if(mId.value!= "" && sDate.value == ""){//如果选择了督办人员,而督办时间没有选,提示
				alert(v3x.getMessage("edocLang.edoc_supervise_select_date"));
				return false;
			}
			if(mId.value !="" && sDate.value !=""){
				supervised = true;  //如果督办时间和督办人员都设置了参数				
			}
		}
	}

function superviseRemindMode(){
		//暂时为在线
		//督办的提醒方式： 在线是第0位，短信是第1位，电邮是第2位 ， 分别用0,1来表示是否采用了此种提醒方式
		/*
		var online = document.getElementById("online");
		var mobile = document.getElementById("mobile");
		var email = document.getElementById("email");
		
		var primary = "0";
		var second = "0";
		var third = "0";
		
		if(online!=null && mobile!=null && email!=null){
		if(online.checked){
			primary = "1";
		}
		if(mobile.checked){
			second = "1";
		}
		if(email.checked){
			third = "1";
		}
		
		var remindMode = primary+second+third;
		*/
		if(document.getElementById("remindMode")){
			document.getElementById("remindMode").value = "100";//100为在线,010为手机短信,001为电邮,如此类推
		}
}

function doZcdb(obj) {
	 isSubmitOperation = true;
	//copyContentIframePra2Suammry();
		//验证内部文号是否被占用
	if(contentIframe.isUpdateEdocForm){//修改了公文单
		if(!contentIframe.checkSerialNoExcludeSelf())return;
	}
	var theForm = obj.form;
	if(!checkModifyingProcess(theForm.processId.value, theForm.summary_id.value)){
		return;
	}
	var bool =contentIframe.checkExchangeAccountIsDuplicatedOrNot();
	
	if(bool == false){
		alert(_("edocLang.exchange_unit_duplicated"));
		disabledPrecessButtonEdoc(false);
		return;
	}
			
	if(!contentIframe.saveEdocForm())
	{
	  return;
	}
	checkExistBody();
    if(!saveContent())
	{
	  return;
	}
	if(!contentIframe.saveHwData())
	{
	  return;
	}
  //debugger;
	superviseRemindMode(); //公文待办督办仍然有效
	//保存当前处理人所提交处理意见所带的附件 
	saveAttachment();		  
	//执行过"删除"和"插入"操作的时候，保存公文正文的附件。
	//if(hasUploadAtt || removeChanged){
   // 	saveContentAttachment();
	//}

	//保存ISIGNATURE HTML网页签章。
	if(bodyType =='HTML' && isSigned){
		htmlContentIframe.saveISignatureHtml();
	}
	if(contentIframe.checkUpdateHw()){				
	  try{
		  recordChangeWord(theForm.affair_id.value ,theForm.summary_id.value ,",wendanqianp",theForm.ajaxUserId.value);			
		}catch(e){}
	}
    //假如修改了附件，就保存附件，记录日志。发送消息
    //if(parent.detailMainFrame._updateAttachmentState){
	//	parent.detailMainFrame.updateAttachmentOnly();
    //}
    theForm.action = genericURL + "?method=doZCDB";
    theForm.submit();
    
    document.getElementById("processButton").disabled = true;
    document.getElementById("zcdbButton").disabled = true;
}

function showAdvance() {
    var rv = v3x.openWindow({
        url: genericControllerURL + "collaboration/advance",
        width: "300",
        height: "280"
    });

    if (rv != null && (rv == "true" || rv)) {

    }
}


function selectUrger(elements) {
    if (!elements) {
        return;
    }

    document.getElementById("urgertext").value = getNamesString(elements);
    document.getElementById("urgerinput").innerHTML = getIdsInput(elements, "urger");
}

/**
 * 检查是否可以修改流程
 * 
 * @return true 可以， false 不可以，表示有其他人在修改流程
 */
function checkModifyingProcess(_processId, _summaryId){
	try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkModifyingProcess", false);
    	requestCaller.addParameter(1, "String", _processId);
    	requestCaller.addParameter(2, "Long", _summaryId);
    	var ds = requestCaller.serviceRequest();
    	if(ds){
    		if(ds.startsWith("--NoSuchSummary--")){
    			alert(v3x.getMessage("edocLang.edoc_hasCancelOrStepback"));
    			return false;
    		}
    		alert(v3x.getMessage("collaborationLang.editing_process", ds));
    		return false;
	    }
    }
    catch(e){
    	alert(e.message)
    	return false;
    }
    
    return true;
}

/**
 * 判断流程是否已经被锁定，未被锁定则给该流程加上一个同步锁，为接下来的修改流程做准备
 * 
 * @param _processId
 * @param _summaryId
 * @return true 可以， false 不可以，表示有其他人在修改流程
 */
function checkModifyingProcessAndLock(_processId, _summaryId){
	try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "colCheckAndupdateLock", false);
    	requestCaller.addParameter(1, "String", _processId);
    	requestCaller.addParameter(2, "Long", _summaryId);
    	var ds = requestCaller.serviceRequest();
    	if(ds != null && ds != ""){
    	    if(ds.startsWith("--NoSuchSummary--")){
    			alert(v3x.getMessage("edocLang.edoc_hasCancelOrStepback"));
    			return;
    		}
    		
    		alert(v3x.getMessage("collaborationLang.editing_process", ds));
    		return false;
	    }
	    else{
	    	return true;
	    }
    }
    catch(e){
    	alert(e.message)
    }
    
	return false;
}

/**
 * 修改完流程，解除流程同步锁
 */
function colDelLock(_processId, _summaryId){
	try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "colDelLock", false,"GET",false);
    	requestCaller.addParameter(1, "String", _processId);
    	requestCaller.addParameter(2, "String", _summaryId);
    	if((arguments.length>2))
    	{
    		requestCaller.addParameter(3, "String", arguments[2]);	
    	}
    	requestCaller.serviceRequest();
    }catch(e){
    }
}

function checkIsCanBeRepealed(summaryId){
	var isCanBeRepealedFlag;
	try {
	  		//debugger;
	  		var requestCaller = new XMLHttpRequestCaller(this, "edocManager", "checkIsCanBeRepealed", false);
	  		requestCaller.addParameter(1,'String',summaryId);
	  		isCanBeRepealedFlag = requestCaller.serviceRequest();
		  	}
	  	catch (ex1) {
	  		alert("Exception : " + ex1);
	  		return false;
	  	}
	return isCanBeRepealedFlag;
	
}


/*
 * 撤销流程
 */
 
 function repealItems(fromPageType) {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return;
    }

    var hasMoreElement = false;
    var len = id_checkbox.length;
    var countChecked = 0;
    var _processId = null;
    var summaryId;
    var affairIds = "";
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            hasMoreElement = true;
            countChecked++;
            _processId = id_checkbox[i].getAttribute("processId");
            summaryId = id_checkbox[i].value;
            affairIds += id_checkbox[i].getAttribute("affairId")+";";
        }
    }

    if (!hasMoreElement) {
        alert(v3x.getMessage("edocLang.edoc_alertCancelItem"));
        return;
    }
    
    if(countChecked > 1){
    	alert(v3x.getMessage("edocLang.edoc_alertSelectCancelOnlyOne"));
        return;
    }

	if(!checkModifyingProcess(_processId, summaryId)){
		return;
	}
	var checkIsCanBeRepealedFlg = checkIsCanBeRepealed(summaryId);
    if("Y"!=checkIsCanBeRepealedFlg){
    	alert(checkIsCanBeRepealedFlg);
        return;
    }
	
    var rv = getA8Top().v3x.openWindow({
        url: genericControllerURL + "collaboration/repealCommentDialog",
        width: 400,
       	height: 280,
       	resizable: true
    });
    if(rv){
        disableButtons();
    	//var element = document.createElement("<INPUT TYPE=HIDDEN NAME='repealComment' value='" + rv + "' />");
        var element2 = document.createElement("input");
    	element2.setAttribute('type','hidden');
    	element2.setAttribute('name','repealComment');
    	element2.setAttribute('value',rv);
        theForm.appendChild(element2); 
        
        var elementAffairIds = document.createElement("input");
        elementAffairIds.setAttribute('type','hidden');
        elementAffairIds.setAttribute('name','affairId');
        elementAffairIds.setAttribute('value',affairIds);
        theForm.appendChild(elementAffairIds); 
        theForm.action = genericURL + "?method=repeal";
        theForm.target = "tempIframe";
        theForm.method = "POST";
        theForm.submit();
    }
}

/*
 * 审批节点撤销流程
 */
function repealItem(fromPageType,summaryId) {
	var theForm = document.getElementById("theform");
	var affairId = document.getElementById("affair_id").value;
	var currentNodeId = document.getElementById("currentNodeId").value;
	var attitude = "";
	var attitudeName = document.getElementsByName("attitude");
	if(attitudeName){
		for(i = 0;i<attitudeName.length;i++)
		{
			if(attitudeName[i].checked){
				attitude = attitudeName[i].value;
				break;
			}
		}
	}
    if (!theForm) {
        return false;
    }
    var opinionContent="";
    if(theForm.contentOP){
    	//意见是否可为空
    	var opinionPolicy = theForm.opinionPolicy;
    	opinionContent=theForm.contentOP.value.trim();
    	//撤销操作必填写意见
    	if(opinionPolicy!=null&&typeof(opinionPolicy)!='undefined'&&opinionPolicy.value==1&&opinionContent == ""){
    		alert(v3x.getMessage("edocLang.edoc_confirmCancelItem_null"));
    		return ;
    	}
    }
    var data = {
        page:"dealrepeal",
        id: [summaryId],
        content: opinionContent,
        affairId:affairId,
        currentNodeId:currentNodeId,
        attitude:attitude
    }
    var action = genericURL + "?method=repeal";
    
        var target = "showDiagramFrame";
    	//var method = "GET";
    	
    	//submitMap(data, genericURL+"?method=deletePeople","showDiagramFrame");
    	
	if(!checkModifyingProcessAndLock(theForm.processId.value, summaryId)){
		return;
	}

    if (window.confirm(v3x.getMessage("edocLang.edoc_confirmCancelItem"))) {
    	if(contentUpdate){
    		contentUpdate=false;
    	}
    	if(workflowUpdate){
    		workflowUpdate=false;
    	}
    	if(edocUpdateForm){
    		edocUpdateForm=false;
    	}
        submitMap(data, action,target);
        //submitMap(data, genericURL+"?method=deletePeople","");
    }
}

//2017-01-18 诚佰公司 编辑流程前选择流程密级
function editWorkFlow() {
	if (flowSecretLevel_wf == null || flowSecretLevel_wf == "") {
		alert("编辑流程前请选择流程密级。");
		return false;
	}
	return true;
}

var isShowingDesigner = false;
function designWorkFlow(frameNames,isOnlyView) {
	// 2017-01-11 诚佰公司
	if (!editWorkFlow()) {
		return;
	}
	
	frameNames = frameNames || "";
    isShowingDesigner = true;
    var onlyView = (isOnlyView == "true");
    var rv = getA8Top().v3x.openWindow({
    	url: genericControllerURL + "collaboration/monitor&isShowButton=true&frameNames=" + frameNames+"&appName="+appName+ "&isOnlyView=" + onlyView+"&secretLevel="+flowSecretLevel_wf,
        width: "860",
        height: "690",
        resizable: "no"
    });

    isShowingDesigner = false;
    if (rv != null) {

    }
    if(rv==true){processing=false;}
}

function newColl() {
    
}

/**
 * 另存�?
 */
function saveAsTemplete(type) {
    if (!type) {
        return;
    }

    if (type != 'workflow') {
        var bodyType = document.getElementById("bodyType").value;
        if (bodyType != "HTML") {
            alert(getA8Top().v3x.getMessage('edocLang.edoc_alertSaveTemplete'));
            return;
        }
    }

    var theForm = document.getElementsByName("sendForm")[0];
    if (!theForm) {
        return;
    }

    var subjectObj = document.getElementById("subject");

    if (!notNull(subjectObj) || !isDeaultValue(subjectObj) || !checkRepeatTempleteSubject(theForm, false)) {
        return;
    }

    if (type != "text") {//非纯文本，必须含流程
        if (!checkSelectWF()) {
            return;
        }
    }

    isFormSumit = true;

    theForm.target = "personalTempleteIframe";
    theForm.action = templeteURL + "?method=saveTemplete&type=" + type;
    theForm.submit();
    getA8Top().startProc('');
}

function _setWorkFlow(_caseProcessXML,_workflowInfo)
{
          hasDiagram = true;
          hasWorkflow = true;
          isFromTemplate = true;
          document.getElementsByName("sendForm")[0].process_desc_by.value = 'xml';
          document.getElementsByName("sendForm")[0].process_xml.value = _caseProcessXML;
          if (workflowInfo) {
        	document.getElementsByName("sendForm")[0].workflowInfo.value = _workflowInfo;
    	  }
    	  document.getElementsByName("sendForm")[0].workflowInfo.disabled=true;
    	  showMode=0;
}

/**
 * 
 */
function openTemplete(templeteCategrory) {
    //window.open(genericControllerURL + "collaboration/templete/index&categoryType="+templeteCategrory);
    //return;
    var comm=document.getElementById("comm").value;
    var orgAccountId=document.getElementById("orgAccountId").value;
    var _url;
    if(comm=="register")
    	_url=genericControllerURL + "collaboration/templete/index&categoryType="+templeteCategrory+"&accountId="+orgAccountId;
    else
    	_url=genericControllerURL + "collaboration/templete/index&categoryType="+templeteCategrory;
   
    if(v3x.getBrowserFlag('OpenDivWindow')==true){
	    var rv = getA8Top().v3x.openWindow({
	        url : _url,
	        height : "600",
	        width : "800"
	    });
	    if (rv) {
	        var templeteType=rv[0];
	        var workflow = "";
	        var bodyContent = "";
	        var xml="";
	        var xslt="";
	        var edocFormId="";
	        var exchangeId=document.sendForm.exchangeId.value;
	
	          var edocType=sendForm.edocType.value;
	          isFormSumit = true;
	          var fromStateParam="";
	       	  var exchangeRegister="";
	          var comm=document.sendForm.comm.value;
	          if(comm=="transmitSend")
	          {
	          	fromStateParam="&fromState=transmitSend";
	          }
	          if(exchangeId!=""){
	          	var strEdocId=document.getElementById("strEdocId").value;
	          	exchangeRegister="&strEdocId="+strEdocId+"&register="+comm+"&exchangeId="+exchangeId;
	          }
	          self.location.href=genericURL+"?method=newEdoc&edocType="+edocType+"&templeteId="+rv[8]+fromStateParam+exchangeRegister;    
	    }
    }else{
		var win = v3x.openDialog({
	    	id:"templateIpad",
	    	title:v3x.getMessage("collaborationLang.find_template"),
	    	url :_url,
	    	width: 800,
	        height: 500,
	        //isDrag:false,
	        //targetWindow:getA8Top(),
	        //fromWindow:window,
	        type:'window',
	        //relativeElement:obj,
	        buttons:[{
				id:'btn121',
	            text: v3x.getMessage("collaborationLang.submit"),
	            handler: function(){    	        	
	        		var rv = win.getReturnValue();
	        		if(rv){
	        	        var templeteType=rv[0];
	        	        var workflow = "";
	        	        var bodyContent = "";
	        	        var xml="";
	        	        var xslt="";
	        	        var edocFormId="";
	        	        var exchangeId=document.sendForm.exchangeId.value;
	        	
	        	          var edocType=sendForm.edocType.value;
	        	          isFormSumit = true;
	        	          var fromStateParam="";
	        	       	  var exchangeRegister="";
	        	          var comm=document.sendForm.comm.value;
	        	          if(comm=="transmitSend")
	        	          {
	        	          	fromStateParam="&fromState=transmitSend";
	        	          }
	        	          if(exchangeId!=""){
	        	          	var strEdocId=document.getElementById("strEdocId").value;
	        	          	exchangeRegister="&strEdocId="+strEdocId+"&register="+comm+"&exchangeId="+exchangeId;
	        	          }
	        	          self.location.href=genericURL+"?method=newEdoc&edocType="+edocType+"&templeteId="+rv[8]+fromStateParam+exchangeRegister; 
	        		}
	        		win.close();
            }
	            
	        }, {
				id:'btn211',
	            text: v3x.getMessage("collaborationLang.cancel"),
	            handler: function(){
	        	win.close();
	            }
	        }]
	    });
    	
    }
}
function advanceViews(flag) {
    var processAdvanceDIVObj = document.getElementById("processAdvanceDIV");
    var processAdvanceDivIframe = document.getElementById("processAdvanceDivIframe");
    if(flag == null){
    	clickClose = false;
    }
    if ((flag || processAdvanceDIVObj.style.display == "none") && !clickClose) {
		processAdvanceDIVObj.style.display = "";
		if(processAdvanceDivIframe){
			var tempheight = processAdvanceDIVObj.clientHeight;
			processAdvanceDivIframe.style.height = tempheight+"px";
			processAdvanceDivIframe.style.display = "";
		}
		//document.getElementById("processAdvance").innerHTML = "&gt;&gt;&nbsp;&nbsp;"+_('collaborationLang.advance');
	}
	else{
		processAdvanceDIVObj.style.display = "none";
		if(processAdvanceDivIframe)processAdvanceDivIframe.style.display = "none";
		//document.getElementById("processAdvance").innerHTML = "&lt;&lt;&nbsp;&nbsp;"+_('collaborationLang.advance');
	}
}
function closeAdvance(){
	 var processAdvanceDIVObj = document.getElementById("processAdvanceDIV");
	 var processAdvanceDivIframe = document.getElementById("processAdvanceDivIframe");
	 if(processAdvanceDIVObj){
	 	clickClose = true;
	 	processAdvanceDIVObj.style.display = "none";
	 	if(processAdvanceDivIframe)processAdvanceDivIframe.style.display = "none";
	 }
}
function openNewEditWin()
{
	if(canUpdateContent){contentUpdate=true;}
	var isFormTemplete = "isFromTemplete";
	//来自公文模板打开正文
	popupContentWin(isFormTemplete);
}

/**
 * 修改正文
 */
function modifyBody(summaryId,hasSign) {   
  var bodyType = document.getElementById("bodyType").value;
  if(bodyType=="HTML")
  {
  	contentUpdate=true;
  	showPrecessAreaTd("content");
	if(htmlISignatureCount>0){
		alert(_("collaborationLang.collaboration_alertCantModifyBecauseOfIsignature"));
		return false;
	}
	popupContentWin();
   
  }else if(bodyType=="Pdf"){
       popupContentWin();
        var tempContentUpdate=ModifyContent(hasSign);
        if(contentUpdate==false)
             contentUpdate=tempContentUpdate;
        
        if(changeWord == false) 
             changeWord = tempContentUpdate;
  }else
  {
    //检查正文区域是否装载完成  
    if(!hasLoadOfficeFrameComplete()) return false;
  	//是否将公文单中的内容自动更新到公文正文中
  	//1.修改正文 2.书签>0，3.给出套红提示。
  	checkOpenState();
  	if(getBookmarksCount()>0){
  		if(confirm(_("edocLang.edoc_refreshContentAuto"))){
  			refreshOfficeLable(contentIframe.document.getElementById("sendForm"));
  		}
  	}
  	popupContentWin();
  	//先签章,后修改正文,有问题
  	//if(contentUpdate==true){return;}  	
    var tempContentUpdate=ModifyContent(hasSign);
	if(contentUpdate==false){
		contentUpdate=tempContentUpdate;
	}
    if(changeWord == false) 
     changeWord = tempContentUpdate;
  }  
}


/**
 * 判断是否装载正文完成
 */
function hashtmlContentIframeComplete(){ 
    if(!htmlContentIframe || htmlContentIframe.document.readyState != 'complete'){
        return false;
    }else{
        return true;
    }
}
function waitLoadComplete(){
	if(!hashtmlContentIframeComplete()){
		window.setTimeout("openSignature()", 500);
		return false;
	}
	else{
		return true;
	}
}
//是否进行了HTML签章的盖章，如果盖章了后面提交的时候才发送保存的请求。
var isSigned = false;
function openSignature()
{
  var bodyType = document.getElementById("bodyType").value;
  if(bodyType=="HTML")
	  
  {	
	  showPrecessAreaTd('content');
	  
	  if(!waitLoadComplete()) return ;
	  
	  if (!htmlContentIframe.isInstallIsignatureHtml()) {
			alert(_("collaborationLang.client_not_installed_professional_signature"));
			return ;
		}
	  //判断当前是否是编辑状态
	  	var fckObj = document.getElementById("fckObj");
		var htmlContentDiv =  document.getElementById("htmlContentDiv");
		if(fckObj  && htmlContentDiv.style.display =='none'){ //编辑状态
			alert(_("collaborationLang.collaboration_alertCantISignatureWhenEdit"));
			return false;
		}
	  //不存在并发修改的情况下可以盖章。
	  if(!checkConcurrentModifyForHtmlContent(summaryId) ){
		  if(!isLoadHtmlContent){
			  loadHtmlContent('isSign');
			  isLoadHtmlContent = true;
		  }else{
			  htmlContentIframe.doSignature(genericControllerURL,"");
		  }
		  isSigned = true;
	  }else{
		  if(!isLoadHtmlContent){
			  loadHtmlContent('read');
		  }
	  }
	
  }else if(bodyType=="Pdf"){
	  popupContentWin();
  }
  else
  {
  	//联合发文套多次正文后，是否进行了保存
  	var isUniteSend=document.getElementById("isUniteSend").value;
  	if(isUniteSend=="true")
  	{
  		var curContentNum= contentIframe.sendForm.currContentNum.value;
  		var curRecordId=contentOfficeId.get(curContentNum,null);
  		if(curRecordId==null)
  		{//刚刚进行了正文套红，正文还没有保存
  			if(window.confirm(_("edocLang.edoc_contentNoSave"))==false){return;}
  			var newRecordId=checkExistBody();
  	  		askUserSave(false);
  			contentOfficeId.put(curContentNum,newRecordId);
  		}
  	}  	
    WebOpenSignature();
  }  
  contentUpdate=true;
  changeSignature = true ;
}

function modifyBodySave() {
    if (!_saveOffice()) {
        return;
    }

    disableButton("save");
    disableButton("cancel");

    theForm.submit();
}

function doEndModifyBodySave() {
    window.returnValue = "true";
    window.close();
}

/**
 * 新建 显示发起人附言区域
 */
function showNoteArea() {
		  if(document.getElementById("noteAreaTd")!=undefined&&document.getElementById("noteAreaTd")!=null)
	  {
	  document.getElementById("noteAreaTd").width = "180px";

    document.getElementById('noteAreaTable').style.display = "";
    var _noteMinDiv = document.getElementById('noteMinDiv');
    _noteMinDiv.style.display = "none";
    _noteMinDiv.style.height = "0px";
	  }

}

function hiddenNoteArea() {
	  if(document.getElementById("noteAreaTd")!=undefined&&document.getElementById("noteAreaTd")!=null)
	  {
	  document.getElementById("noteAreaTd").width = "45px";

    document.getElementById('noteAreaTable').style.display = "none";
    var _noteMinDiv = document.getElementById('noteMinDiv');
    _noteMinDiv.style.display = "";
    _noteMinDiv.style.height = "100%";
	  }

}

/**
 * 解析流程XML，返回字符串 Meber|1321234
 */
function getWFInfoFromXML(xmlString) {
    if (!xmlString) {
        return "";
    }

    var xmlDom = null;
    try {
        xmlDom = new ActiveXObject("MSXML2.DOMDocument");
    }
    catch (e) {
        return "";
    }

    try {
        xmlDom.loadXML(xmlString);

        var root = xmlDom.documentElement;
        if (!root) {
            return "";
        }

        var process = root.selectNodes("process");
        if (!process) {
            return "";
        }

        var nodes = process[0].selectNodes("node");
        if (!nodes || nodes.length < 1) {
            return "";
        }

        var partyIds = [];

        for (var i = 0; i < nodes.length; i++) {
            var node = nodes[i];

            var actors = node.selectNodes("actor");

            if (!actors || actors.length < 1) {
                continue;
            }

            for (var j = 0; j < actors.length; j++) {
                var actor = actors[j];
                var partyId = actor.getAttribute("partyId");
                var partyType = actor.getAttribute("partyType");

                if (partyId && !isNaN(partyId)) {
                    partyIds[partyIds.length] = partyType + "|" + partyId;
                }
            }
        }

        return partyIds.join(",");
    } catch(e) {
    }

    return "";
}

function doForward() {
    var theForm = document.getElementsByName("sendForm")[0];
    if (!theForm) {
        return false;
    }

    if (!hasWorkflow) {
        alert(v3x.getMessage("edocLang.edoc_selectWorkflow"));
        selectPeopleFun_forward()
        return false;
    }

    if (!checkForm(theForm)) {
        return false;
    }

    saveAttachment();

    theForm.b1.disabled = true;
    theForm.b2.disabled = true;
    theForm.submit();
}

function addNote(_isNoteAddOrReply, opinionId) {
    reply(opinionId);

    //发起人增加附言
    if (_isNoteAddOrReply == 'addnote') {
        var theForm = document.getElementsByName("repform")[0];

        document.getElementById("isHiddenDiv").style.display = "none";
        theForm.isNoteAddOrReply.value = _isNoteAddOrReply;
    }
}

function quoteDocumentEdoc(appType) {
    var atts = v3x.openWindow({
        url: genericURL + "?method=showList4QuoteFrame&appType="+appType,
        height: 600,
        width: 800
    });
    if (atts) {
		deleteAllAttachment(2);
        for (var i = 0; i < atts.length; i++) {
            var att = atts[i]

            //addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description)
            addAttachment(att.type, att.filename, att.mimeType, att.createDate, att.size, att.fileUrl, true, false, att.description, null, att.mimeType + ".gif",att.reference, att.category)
        }
    }
}
function jumpState(workstate,edocType)
{
  var url=edocContorller;
  if(workstate=="darft"){url+="?method=edocFrame&from=listWaitSend&edocType="+edocType;}
  else if(workstate=="sended"){url+="?method=edocFrame&from=listSent&edocType="+edocType;}
  else if(workstate=="pending"){url+="?method=edocFrame&from=listPending&edocType="+edocType;}
  else if(workstate=="done"){url+="?method=edocFrame&from=listDone&edocType="+edocType;}
  else if(workstate=="waitRegister"){url+="?method=edocFrame&from=listRegisterPending&edocType="+edocType;}
  parent.location.href=url;  
}
function quoteDocumentOK() {
    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return null;
    }
    var atts = [];

    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        var c = id_checkbox[i];

        if (c.checked) {
            var type = "2";
            var filename = c.getAttribute("subject");
            var mimeType = c.getAttribute("documentType");
            var createDate = "0000-00-00 00:00:00";
            var fileUrl = c.getAttribute("url");
            var description = c.getAttribute("url");

            //function Attachment(id, reference, subReference, category, type, filename, mimeType, createDate, size, fileUrl, description, needClone)
            atts[atts.length] = new Attachment('', '', '', '', type, filename, mimeType, createDate, '0', fileUrl, description);
        }
    }

    if (!atts || atts.length < 1) {
        alert(getA8Top().v3x.getMessage('edocLang.edoc_alertQuoteItem'));
        return;
    }

    parent.window.returnValue = atts;
    parent.window.close();
}
function newEdoc(edocType)
{
  parent.location.href=genericURL+"?method=newEdoc&edocType="+edocType;
}
function changeEdocForm(selectObj)
{
  //var formId=selectObj.options[selectObj.selectedIndex].value;
  //getEdocFormModel(formId);  
  document.sendForm.action=collaborationCanstant.changeEdocFormURL;
  var tempCanUpdateContent=null;
  try{tempCanUpdateContent=_canUpdateContent;}catch(e){}
  //if(tempCanUpdateContent==false)
  //{
  	try{adjustReadFormForSubmit();}catch(e){}
 // }
  var retData=ajaxFormSubmit(document.sendForm);
  var xmlObj=document.getElementById("xml");
  //yangzd 转移数据中的特殊字符
  temp=new String(retData.get("xml"));
  temp = temp.replace(/&amp;/gi,"&");
	//temp = temp.replace(/&lt;/gi,"<");
	//temp = temp.replace(/&gt;/gi,">");
	temp = temp.replace(/<br>/gi,"");
	temp = temp.replace(/&#039;/gi,"\'");
	temp = temp.replace(/&#034;/gi,"\"");
	temp = temp.replace(/&apos;/gi,"\'");
  xmlObj.value=temp;
//  xmlObj.value=retData.get("xml");
//yangzd
  var xlsObj=document.getElementById("xslt");
  xlsObj.value=retData.get("xslt");edocFormDisplay();  
}
function getEdocFormModel(formId)
{
  try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocFormManager", "getEdocFormModel",false);
		requestCaller.addParameter(1, "long", formId);
		requestCaller.addParameter(2, "long", actorId);
		var ds = requestCaller.serviceRequest();
		var xmlObj=document.getElementById("xml");
		xmlObj.value=ds.get("xml");
		var xlsObj=document.getElementById("xslt");
		xlsObj.value=ds.get("xslt");
		edocFormDisplay();
	}
	catch (ex1) {
		alert("Exception : " + (ex1.number & 0xFFFF)+ex1.description);
	}
}

//newEdoc.jsp页面使用
function dealPopupContentWinWhenDraft(contentNum){
	if(canUpdateContent){contentUpdate=true;}
	dealPopupContentWin(contentNum);
}
function dealPopupContentWin()
{

	try{
	if(window.document.readyState!="complete") {return false;}
	var bodyType = document.getElementById("bodyType").value;
  	if(bodyType=="HTML")
  	{  	
  		popupContentWin();
  	}else if(bodyType=='Pdf'){
  	    popupContentWin();
  	}
  	else
  	{	
  		var contentNum = document.getElementById("currContentNum").value;
  		var newOfficeId=contentOfficeId.get(contentNum,null);
  		if(newOfficeId){
	  		if(newOfficeId!=getOfficeOcxCurVerRecordID())
	  		{
	  			askUserSave(true);
	  			setOfficeOcxRecordID(newOfficeId);
	  			//为保证印章有效，控件FileName参数属性必须和改章的时候的参数一样，所以复制一份后要想保证原来印章有效，这个参数不能变化
	  			document.getElementById("contentNameId").value=contentOfficeId.get("0",null);
	  			//theform.currContentNum.value=contentNum;
	  			contentUpdate=false;
	  		}
  		}
  		popupContentWin();
  	}
	}catch(e){}
}
/**
 * 点击页面按钮的时候加载office插件，但是不全屏，嵌入到页面中
 * @return
 */
function LazyloadOffice(contentNum){
	try{
		if(window.document.readyState!="complete") {return false;}
		var bodyType = document.getElementById("bodyType").value;
		//加载office插件
		if(bodyType!="HTML" && bodyType!='Pdf'){
	  		var newOfficeId=contentOfficeId.get(contentNum,null);
	  		if(newOfficeId){
		  		if(newOfficeId!=getOfficeOcxCurVerRecordID())
		  		{
		  			askUserSave(true);
		  			setOfficeOcxRecordID(newOfficeId);
		  			//为保证印章有效，控件FileName参数属性必须和改章的时候的参数一样，所以复制一份后要想保证原来印章有效，这个参数不能变化
		  			document.getElementById("contentNameId").value=contentOfficeId.get("0",null);
		  			theform.currContentNum.value=contentNum;
		  			contentUpdate=false;
		  		}
	  		}
	  		document.getElementById("edocContentDiv").style.display="block";
	  		checkOpenState();
	  		fullSize();
		}else if(bodyType == 'Pdf'){
			checkPDFOpenState();
			pdfFullSize();
		}
	}catch(e){}
}
//正文修改后，提示用户是否修改
function askUserSaveOnly()
{
	if(contentUpdate==true)
	{
		return window.confirm(_("edocLang.edoc_contentConfirmSave"));				
	}
	else
	{
		return false;
	}
}
//正文修改后，提示用户是否修改
function askUserSave(isAsk)
{
	var isSave=true;
	if(contentUpdate==true)
	{
		if(isAsk)
		{
			isSave=window.confirm(_("edocLang.edoc_contentConfirmSave"));
		}
		if(isSave)
		{
			if(saveOffice()==false)
  	  		{
  	  			alert(_("edocLang.edoc_contentSaveFalse"));
  	  			return false;
  	  		}
  	  		contentUpdate=false;						
		}		
	}
	return true;
}

/**
 * 装载HTML正文
 * @param operationType ： 操作类型 {read | isSign(盖章) | edit 修改正文}
 */
function loadHtmlContent(operationType,summaryId){
	
	var tempUrl = fullEditorURL+"&isEmbedModel=true&summaryId="+summaryId;
	if(operationType == 'isSign') 
		tempUrl+="&isSign=true";
	if(operationType == 'edit') 
		tempUrl+="&canEdit=true";
	
	if(typeof(affairState)!='undefined'){
  		tempUrl+="&affairState="+affairState;
  	}
	htmlContentIframe.location.href  = tempUrl;
	
	isLoadHtmlContent = true;
}
/**
弹出正文窗口
**/
function popupContentWin()
{
  if(window.document.readyState!="complete") {return false;}
  var bodyType = document.getElementById("bodyType").value;
  if(bodyType=="HTML")
  { 
  	var isFromTemplete = false;	
    for(var i = 0; i < arguments.length; i++) {
				var tempArg = arguments[i];
				if(tempArg == 'isFromTemplete'){
					isFromTemplete = true;
					break;
				}
			}
  	var tempUrl=fullEditorURL;
	if(isFromTemplete || (typeof(currentPage)!='undefined' && currentPage =="newEdoc")){
		//来自公文模板，不检查正文是否被并发修改；
	  	if(contentUpdate==false){tempUrl+="&canEdit=false";}
		else{tempUrl+="&canEdit=true";}	
	}
	else{
		//非公文模板，检查正文是否被并发修改；
	  	if(contentUpdate==false ||checkConcurrentModifyForHtmlContent(summaryId)){tempUrl+="&canEdit=false";}
	  	else{tempUrl+="&canEdit=true";}	
	}
	
  	if(typeof(officecanPrint)!="undefined" && officecanPrint!=null)
	{
  		tempUrl+="&canPrint="+officecanPrint;
	}
	else
	{
		tempUrl+="&canPrint=true";
	}
  	if(typeof(affairState)!='undefined'){
  		tempUrl+="&affairState="+affairState;
  	}
    var rv = v3x.openWindow({url: tempUrl,workSpace: 'yes'});  
    if(document.getElementById("content")!=null && (typeof(oFCKeditor) != "undefined"))
    {
      if(rv==null){return;}
      oFCKeditor.SetContent(rv);
      oFCKeditor.remove();//提交的时候不在拷贝编辑区域到输入text;
    }
    else
    {
      if(rv==null){return;}
     try{ htmlContentIframe.document.getElementById("edoc-contentText").innerHTML=rv;}catch(e){}
    }
  }else if(bodyType=="Pdf"){
    pdfFullSize();
  }else{
    fullSize();
  }
}
function getHtmlContent()
{
  var str="";
  if(document.getElementById("content")!=null && (typeof(oFCKeditor) != "undefined"))
  {
	  str=oFCKeditor.GetContent();
   // str=oFCKeditor.EditingArea.Document.body.innerHTML;
  }
  else
  {//浏览状态,正文放到Div里面了
    str=document.getElementById("edoc-contentText").innerHTML;
  }
  return str;
}
//处理时保存修改的公文单
function saveEdocForm()
{
  if(document.sendForm.elements["my:subject"].value.trim()=="")	
  {
    		alert(_("edocLang.edoc_inputSubject"));
    		/*
    		if(document.sendForm.elements("my:subject").disabled==true)
    		{
    			alert(_("edocLang.edoc_alertSetPerm"));
    			return false;
    		}
    		*/
    		try{document.sendForm.elements["my:subject"].focus();}catch(e){}
    		return false;
  }
  if(isUpdateEdocForm==false){return true;}
  document.sendForm.action=collaborationCanstant.updateEdocFormURL;
  if(validFieldData()==false){return false;}
  
  // 确保发文单位被提交
  if(document.getElementById('my:send_unit'))
	document.getElementById('my:send_unit').setAttribute('canSubmit','true');
  var retData=ajaxFormSubmit(document.sendForm);
  var ret;
  if(retData.indexOf("result=true")>=0){
  	ret = true;
  }else if(retData.indexOf("result=historyMarkExist:")>=0){
  	//签报时判断文号是否重复
  	var position = retData.indexOf(":");
  	alert(retData.substring(position+1));
  	ret = false;
  }else{
  	ret = false;
  }
  return ret;
}
//处理时保存正文
function saveContent()
{ 
	if(contentUpdate==false){
	    return true;
	}  
	var ret = "";
	var ajaxStr = "" ;//记录的是修改的类型的记录
	var affair_IdValue = document.getElementById("affair_id") ;
	var summary_IdValue = document.getElementById("summary_id");
	var ajaxUserId = document.getElementById("ajaxUserId");
	var redFormObj = document.getElementById("redForm");	
	if(redFormObj)  {
		var redFormValue = redFormObj.value ;	
		if(redFormValue == "true" && affair_IdValue && summary_IdValue){			
			ajaxStr = ajaxStr + ",taohongwendan" ;					   		
		  }		
	}

 
  var bodyType = document.getElementById("bodyType").value;  
  if(bodyType=="HTML")
  {
      try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocManager", "updateHtmlBody",false);
		var bodyContentId=document.getElementById("bodyContentId").value;
		requestCaller.addParameter(1, "long", bodyContentId);
		requestCaller.addParameter(2, "String",htmlContentIframe.getHtmlContent());
		var ds = requestCaller.serviceRequest();	
		ajaxStr = ajaxStr + ",contentUpdate" ;
		submitToRecord();		
		ret = (ds=="true");
	}
	catch (ex1) {
		alert("Exception : " + (ex1.number & 0xFFFF)+ex1.description);
		return false;
	}            
  }else if(bodyType=="Pdf"){
      savePdf();
      if(contentUpdate) {
		if(changeWord){			
		    ajaxStr = ajaxStr + ",contentUpdate" ;			
		}			
	  }
      submitToRecord();	
  }else
  { 
  	/**
	 * 记录正文被修改的记录
	 */
	if(contentUpdate) {
		if(changeWord){			
		    ajaxStr = ajaxStr + ",contentUpdate" ;			
		}			
	}
	/**
	 * 记录正文套红
	 */
	if(hasTaohong) {
	    var redContentValue = theform.redContent;
	    if(redContentValue && redContentValue.value == "true") 
		    ajaxStr = ajaxStr + ",taohong" ;		    			
	}
	/**
	 * 签章
	 */
	if(changeSignature) {		
	    ajaxStr = ajaxStr + ",qianzhang" ;						
	} 
	
	submitToRecord();	
    if(saveOffice()==false)
    {
      return false;
    }
  }
  
	// AJax记录操作日志
    function submitToRecord(){
        if(ajaxStr != ""  && affair_IdValue && summary_IdValue && ajaxUserId) {
    		recordChangeWord(affair_IdValue.value ,summary_IdValue.value ,ajaxStr, ajaxUserId.value)
    		ajaxStr = "" ;
        }	
    }
  contentUpdate= false;
  return true;
}
/**
* AJax记录流程日志
*/
function recordChangeWord(affair_IdValue ,summary_IdValue ,ajaxStr,userId) {
	if(affair_IdValue == "" && summary_IdValue == "" && ajaxStr == "") 
	  return ;
		try{
	    	if(affair_IdValue && summary_IdValue) {	    
		    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocManager", "recoidChangeWord", false);
		    	requestCaller.addParameter(1, "String", affair_IdValue);
		    	requestCaller.addParameter(2, "String", summary_IdValue);	
		    	requestCaller.addParameter(3, "String", ajaxStr) ;
		    	requestCaller.addParameter(4, "String", userId) ;
		    	requestCaller.serviceRequest() ;		    		
	    	}	
		}catch(e){
		}	  
}

//调用模板确定后的回调函数
function refTemplete() {
    var id = getParameter("id");
    var rv = [];
    rv[0]=templeteType;
	if(templeteType == 'text')
	{
	    rv[1] = document.getElementById("xslt").value;
        rv[2] = document.getElementById("xml").value;
        rv[3] = edocFormId;
        rv[4] = document.getElementById("bodyType").value;
        if(rv[4]=="HTML")
        {
	      rv[5]=document.getElementById("edoc-contentText").innerHTML;
	    }
	    else
	    { 
	      if(typeof(fileId)!='undefined' && fileId)
	    	  rv[5]=fileId;
	      if(typeof(createDate)!='undefined' && createDate)
	    	  rv[6]=createDate;
	    }
	    rv[7]=id;	    
	}
	else if(templeteType == 'workflow')
	{
	    rv[1] = caseProcessXML;
        rv[2] = workflowInfo;
        rv[3]=id;
        //返回分支条件        
        rv[11]=branchs;
        rv[12]=keys;
        rv[13]=team;
        rv[14]=secondpost;
        
	}
	else if(templeteType == 'templete')
	{
	    rv[1]=id;
	    rv[2] = caseProcessXML;
        rv[3] = workflowInfo;
	    /*rv[1] = caseProcessXML;
        rv[2] = workflowInfo;
        rv[3] = edocFormId;
        rv[4] = document.getElementById("xslt").value;
        rv[5] = document.getElementById("xml").value;
	    rv[6]=document.getElementById("edoc-contentText").innerHTML;
	    */
	    //返回分支条件        
        rv[11]=branchs;
        rv[12]=keys;
        rv[13]=team;
        rv[14]=secondpost;
        
        //返回督办信息
        rv[15]=supervisorId;
        rv[16]=supervisors;
        rv[17]=unCancelledVisor;
        rv[18]=sVisorsFromTemplate;
        rv[19]=awakeDate;
        rv[20]=superviseTitle;
	}
	
	rv[8]=id;
    top.window.returnValue = rv;
    top.close();
}
//调用模板确定后的回调函数
function refTempleteIpad() {
    var id = getParameter("id");
    var rv = [];
    rv[0]=templeteType;
	if(templeteType == 'text')
	{
	    rv[1] = document.getElementById("xslt").value;
        rv[2] = document.getElementById("xml").value;
        rv[3] = edocFormId;
        rv[4] = document.getElementById("bodyType").value;
        if(rv[4]=="HTML")
        {
	      rv[5]=document.getElementById("edoc-contentText").innerHTML;
	    }
	    else
	    {
	      rv[5]=fileId;
	      rv[6]=createDate;
	    }
	    rv[7]=id;	    
	}
	else if(templeteType == 'workflow')
	{
	    rv[1] = caseProcessXML;
        rv[2] = workflowInfo;
        rv[3]=id;
        //返回分支条件        
        rv[11]=branchs;
        rv[12]=keys;
        rv[13]=team;
        rv[14]=secondpost;
        
	}
	else if(templeteType == 'templete')
	{
	    rv[1]=id;
	    rv[2] = caseProcessXML;
        rv[3] = workflowInfo;
	    /*rv[1] = caseProcessXML;
        rv[2] = workflowInfo;
        rv[3] = edocFormId;
        rv[4] = document.getElementById("xslt").value;
        rv[5] = document.getElementById("xml").value;
	    rv[6]=document.getElementById("edoc-contentText").innerHTML;
	    */
	    //返回分支条件        
        rv[11]=branchs;
        rv[12]=keys;
        rv[13]=team;
        rv[14]=secondpost;
        
        //返回督办信息
        rv[15]=supervisorId;
        rv[16]=supervisors;
        rv[17]=unCancelledVisor;
        rv[18]=sVisorsFromTemplate;
        rv[19]=awakeDate;
        rv[20]=superviseTitle;
	}
	
	rv[8]=id;

    return rv;
}
//清空所有的affairId的value;
function clearAffairIdValue(){
	var affairIds = document.getElementsByName("affairId");
	try{
		if(affairIds){
			for(var i = 0;i<affairIds.length;i++){
				affairIds[i].value = '';
			}
		}
	}catch(e){
		
	}
}
//公文归档
function pigeonholeForEdoc(pageType,edocType)
{	
	//请空affairId的值，防止上次产生的affairId的值传递到了后台。
	clearAffairIdValue();
	var appName = document.getElementById("appName").value; 
	var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }
    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return true;
    }
    var hasMoreElement = false;
    //是否直接弹出选择归档路径的对话框，当所有的都没有归档路径的时候就直接弹出，否则就不直接弹出。提交到后台
    var isSelectRedirectly = true ;
    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            hasMoreElement = true;
            break;
        }
    }
    if (!hasMoreElement) {
        alert(v3x.getMessage("edocLang.edoc_alertPigeonholeItem"));
        return true;
    }
    //已办列表，判断是否有归档权限，已发列表不需要判断
    if(pageType == 'finish'){ 
    	var affairIds="";
        for (var i = 0; i < len; i++) {
            if (id_checkbox[i].checked) {
              if(affairIds==""){
                affairIds=id_checkbox[i].affairId; 
              }else {
                affairIds+=","+id_checkbox[i].affairId; 
              }
            }
        }
    	var ret=checkHasAclNodePolicyOperation(affairIds,'Archive' );
	    if(ret != 'ok'){
	    	var arr=ret.split("&");
	        alert(v3x.getMessage("edocLang.edoc_alertnotaclaccpigeonhole",arr[0]));
	        for (var i = 0; i < len; i++) {
	        	 if (id_checkbox[i].checked) {
	        		 if(arr[1].indexOf(id_checkbox[i].affairId)!= -1 ){
	        			 id_checkbox[i].checked = false; 
	        		 }
	        	 }
	        }
	        return;
	    }
    }
   
    var _affairId = "";
	theForm.action = collaborationCanstant.pigeonholeActionURL;
	disableButtons();
    var hasArchive="";//已经归档
//    var notFinished = "";//流程未结束
	for (var i = 0; i < id_checkbox.length; i++) {
	    var checkbox = id_checkbox[i];
	    if (!checkbox.checked)
		   	continue;
		
	    //判断是否已经归档，并且归档文件夹没有被删除。如果归档文件夹被删除了还要运行归档。
	    var docResourceIsExist = '';
	    if(checkbox.hasArchive=="true" || checkbox.archiveId != "" ){
	    	docResourceIsExist = checkPigholeDocResourceIsExist(checkbox.archiveId);
	    }
		if(checkbox.hasArchive=="true"){
		    if(docResourceIsExist=='true') {
			    hasArchive+="《"+checkbox.subject+"》\r\n";
			    checkbox.checked = false;
			    continue;
		    }
		}
		//设置了预先归档，并且预归档文件夹存在，就提交到后台
		if(checkbox.archiveId != "" && docResourceIsExist == 'true'){
			isSelectRedirectly = false;
		}
		var affairId = checkbox.getAttribute("affairId");
		//var element = document.createElement("<INPUT TYPE=HIDDEN NAME=affairId value='" + affairId + "' />");
	    var element = document.createElement('input');
	    element.setAttribute('type','hidden');
	    element.setAttribute('name','affairId');
	    element.setAttribute('value',affairId);
		
		theForm.appendChild(element);
	}

	if(hasArchive != ""){
	    alert(_("edocLang.edoc_alertHasPigeonhole",hasArchive));
	    return;
	}
	
	if(isSelectRedirectly){
		doPigeonhole('new',appName,'listDone','');
		var archiveId = document.getElementById('archiveId').value;
		if(archiveId == '') return;
	}
	
	//var element = document.createElement("<INPUT TYPE=HIDDEN NAME=pageType value='" + pageType + "' />");
    var element = document.createElement('input');
    element.setAttribute('type','hidden');
    element.setAttribute('name','pageType');
    element.setAttribute('value',pageType);
	
	
	theForm.appendChild(element);
	//var element = document.createElement("<INPUT TYPE=HIDDEN NAME=edocType value='" + edocType + "' />");
    var element = document.createElement('input');
    element.setAttribute('type','hidden');
    element.setAttribute('name','edocType');
    element.setAttribute('value',edocType);
    
	theForm.appendChild(element);	
	theForm.target = "tempIframe";
	theForm.method = "POST";
	theForm.submit();
	try{getA8Top().startProc('')}catch(e){};
	return true;
}
/**
 *判断当前事项是否能指定的操作 。
 * @param affairIds  : 个人事项ID
 * @param operationName ： 操作名(如：DepartPigeonhole)
 * @return 当传入的事项都有权限的时候，返回为空值，当传入的某些事项没有指定操作的权限的时候，返回没有权限的事项的标题。
 */
function checkHasAclNodePolicyOperation(affairIds,operationName){
    var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocManager", "checkHasAclNodePolicyOperation",false);
    requestCaller.addParameter(1, "String", affairIds);  
    requestCaller.addParameter(2, "String", operationName);  
    var ds = requestCaller.serviceRequest();
    return ds;
}
function checkPigholeDocResourceIsExist(archiveId){
 	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "docResourceExist",false);
    requestCaller.addParameter(1, "Long", archiveId);  
    var ds = requestCaller.serviceRequest();
    return ds;
}
function listDepartPigeonhole(appName){
    //getA8Top().startProc('');
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return true;
    }
    
    var summaryIds="";
    var affairIds="";
    var len = id_checkbox.length;
    var hasMoreElement = false;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
          hasMoreElement = true;
          
          if(summaryIds==""){
               summaryIds=id_checkbox[i].value;
           }else{
               summaryIds+=","+id_checkbox[i].value;
           }
           
          if(affairIds==""){
            affairIds=id_checkbox[i].affairId; 
          }else {
            affairIds+=","+id_checkbox[i].affairId; 
          }
          
        }
    }
    if (!hasMoreElement) {
        alert(v3x.getMessage("edocLang.edoc_alertPigeonholeItem"));
        return true;
    }
    
   //判断是否有部门归档的权限
    var ret=checkHasAclNodePolicyOperation(affairIds,'DepartPigeonhole');
    if(ret != 'ok'){
    	var arr=ret.split("&");
        alert(v3x.getMessage("edocLang.edoc_alertnotacldeppigeonhole",arr[0]));
        for (var i = 0; i < len; i++) {
        	 if (id_checkbox[i].checked) {
        		 if(arr[1].indexOf(id_checkbox[i].affairId)!= -1 ){
        			 id_checkbox[i].checked = false; 
        		 }
        	 }
        }
        return;
    }
   
    var archiveIds=pigeonhole(appName,summaryIds);
	if(archiveIds == "cancel"){
		return;
	}
	if(archiveIds == "" || archiveIds == "failure"){
		alert(v3x.getMessage("edocLang.edoc_alertPigeonholeItemFailure"));	   
	}
	else
	{
		try{
		    var ajaxUserId = document.getElementById("ajaxUserId");    			    	
			if(summaryIds && affairIds && ajaxUserId) {
				recordChangeWord(affairIds ,summaryIds ,"depPinghole",ajaxUserId.value)
			}			    	 							    				    	
		}catch(e){}			
		alert(v3x.getMessage("edocLang.edoc_alertPigeonholeItemSucceed"));		    			
	}
}

function DepartPigeonhole(appName,summaryId)
{
	var archiveIds=pigeonhole(appName,summaryId);
	if(archiveIds == "cancel"){
		return;
	}
	if(archiveIds == "" || archiveIds == "failure"){
		alert(v3x.getMessage("edocLang.edoc_alertPigeonholeItemFailure"));	   
	}
	else
	{
			try{
				var affair_IdValue = document.getElementById("affair_id") ;
			    var summary_IdValue = document.getElementById("summary_id") ;	
			    var ajaxUserId = document.getElementById("ajaxUserId");    			    	
				if(affair_IdValue && summary_IdValue && ajaxUserId) {
					recordChangeWord(affair_IdValue.value ,summary_IdValue.value ,"depPinghole",ajaxUserId.value)
				}			    	 							    				    	
			}catch(e){}			
		alert(v3x.getMessage("edocLang.edoc_alertPigeonholeItemSucceed"));		    			
	}
}


//双击编辑待发公文单
function editWaitSend(summaryId,affairId)
{
	parent.location.href=genericURL+"?method=newEdoc&summaryId="+summaryId+"&from=WaitSend"+"&affairId="+affairId;	
}

//点击编辑图标编辑待发公文单
function editFromWaitSend(summaryId)
{
	try{
		var id_checkbox = document.getElementsByName("id");
	    if (!id_checkbox) {
	        return;
	    }
	    var count = validateCheckbox("id");
	    if(count == 1){
	    	for(var i=0; i<id_checkbox.length; i++){
				var idCheckBox = id_checkbox[i];
				if(idCheckBox.checked){
					var summaryId = idCheckBox.value;
					var affairId =  idCheckBox.getAttribute('affairId');
					parent.location.href=genericURL+"?method=newEdoc&summaryId="+summaryId+"&from=WaitSend&affairId="+affairId;
				}
			}
	    }
	    else if(count == 0){
	    	alert(v3x.getMessage("edocLang.edoc_alertSelectEditItem"));
	        return;
	    }
	    else{
	    	alert(v3x.getMessage("edocLang.edoc_alertDontSelectMulti"));
	 		return;
	    }
	}catch(e){
	}
}

/***************公文单控制部分开始*******************/
//*初始化时全部初始化为只读状态,不根据xml的权限进行控制*//
function initReadSeeyonForm(s,x){
	//var initStr = "http://128.2.2.84:8080/seeyon/form.do?method=creatformxml&";
	//initStr = initStr + "formOperation=" + formOperation;
	
    //var str = init(initStr);
	//alert(str);
	var str = s;
	var xslStart = str.indexOf("&&&&&&&  xsl_start  &&&&&&&&");
	var dataStart = str.indexOf("&&&&&&&&  data_start  &&&&&&&&");
	var inputStart = str.indexOf("&&&&&&&&  input_start  &&&&&&&&");
	
	if(xslStart == -1)
			throw  "没有找到xsl";
	if(dataStart == -1)
			throw "没有找到data";
	if(inputStart == -1)
			throw "没有找到input";
	var xsl = str.slice(xslStart + 28, dataStart);
	var data = str.slice(dataStart + 30, inputStart);
	var finput = str.slice(inputStart + 31);
	
	//替换&字符
  //var reg=/&/g;
  //data=data.replace(reg, "&amp;");
  
  
  var html = document.getElementById("html");
  var fnow=transformNode(data, x); 
  //infopath设置了自动换行，转化为html的时候会自动添加下面这个可编辑的属性，导致意见元素可编辑，所以需要替换掉。
  fnow= fnow.replace(/contentEditable=\"true\"/g,"");
  html.innerHTML = fnow;
  initReadHtml(finput);
  initJSObject(data);
  try{setOpinionSpandefaultHeight();}catch(e){}
}

//取到area并进行替换

function initReadHtml(aInput){
	Init_Image();
	//document.onclick = SeeyonForm_BuildDocClickHandler('common');
	var fFiledList = paseFormatXML(getXmlContent(aInput))
	
	
	
	convertReadHtml(fFiledList);
	
	var repeatTable = new Seeyonform_rtable();
	repeatTable.change();
	
	var reSection = new Seeyonform_rsection();
	reSection.change();
	  
}

function convertReadHtml(aFieldList){
	var field;
	var fAreas;
	for(var i = 0; i < aFieldList.length; i++){
		 field = aFieldList[i];
		 fAreas = field.findArea(field.fieldName);
		 
		 if(fAreas != 0){
    		  for(var j =0; j < fAreas.length;j++){
    		      //312sp1 bug 31615 将内部文号独立出来，不根据开关“是否允许修改外来文登记”来控制。后续进行根细化的处理。
    		      if(field.fieldName=="my:serial_no" && typeof(currentPage)!="undefined" && currentPage=="newEdoc"){
    		         if(field.access == "edit" || field.access == null ){
    					field.change(fAreas[j]);
    					if(firstCanEditElementId==""){firstCanEditElementId=field.fieldName;}
    					isIncludeCanUpdateElement=true;
    		         }else{
    		            field.browse(fAreas[j]);	
    		         }
    		     }else{
    				field.browse(fAreas[j]);							 				
    	 		}
    		  }
		 }else{
		 		//throw "页面没有名为"+ field.fieldName +"的span元素";
		 } 
  }
}


//文号修改标志
var edocMarkUpd=false;
//修改文单标记
var edocUpdateForm=false;

function addSelectOption(obj,value,dis)
{
	if(obj==null || value==""){return;}
	var i,len=obj.options.length;
	for(i=0;i<len;i++)
	{
		if(obj.options[i].value==value)
		{
			obj.options[i].selected=true;
			return;	
		}
	}
	var opt=document.createElement("OPTION");
	obj.options.add(opt);
	opt.value=value;
	opt.text=dis;
	opt.selected=true;
}


// 在多人执行时，判断是否有人修改正文。
function checkAndLockEdocEditForm(summaryId)
{
 var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocSummaryManager", "editObjectState",false);
  requestCaller.addParameter(1, "String", summaryId);  
  var ds = requestCaller.serviceRequest();

  if(ds.get("curEditState")=="true")
  {  
  	  	canUpdateWendan=false;  
  			alert(_("edocLang.edoc_cannotedit"));
  			return true;
  }
  //新建文档，不需要更新
  if(ds.get("lastUpdateTime")==null){return false;}  
  return false;
}

// 解锁，让别人可以修改文单
function unlockEdocEditForm(summaryId)
{
  var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocSummaryManager", "deleteUpdateObj",false,"GET",false);
  requestCaller.addParameter(1, "String", summaryId);  
	if((arguments.length>1))
	{
		requestCaller.addParameter(2, "String", arguments[1]);	
	}  
  requestCaller.serviceRequest();	
}
function UpdateEdocForm(summaryId)
{	
	parent.showPrecessAreaTd('edocform');
	// 判断是否有其他用户在修改文单
	if(checkAndLockEdocEditForm(summaryId)) return;
		//已经是文号修改状态
	if(edocUpdateForm){return;}

	var edocMark1="";
	var edocMarkDis1="";
	var edocMark2="";
	var edocMarkDis2="";
	//检查文号是否修改,避免修改文单后修改文号内容丢失
	if(edocMarkUpd)
	{
		var obj=document.getElementById("my:doc_mark");
		if(obj!=null && obj.tagName=="SELECT")
		{
			edocMark1=obj.options[obj.selectedIndex].value;
			edocMarkDis1=obj.options[obj.selectedIndex].text;
		}
		obj=document.getElementById("my:doc_mark2");
		if(obj!=null && obj.tagName=="SELECT")
		{
			edocMark2=obj.options[obj.selectedIndex].value;
			edocMarkDis2=obj.options[obj.selectedIndex].text;
		}
	}
	//保存手写签批信息，如果修改
	if(checkUpdateHw())
	{
		if(window.confirm(_("edocLang.edoc_confirmSaveHw")))
		{
			saveHwData();
		}
	}
			
      		var xml = document.getElementById("xml");
			var xsl = document.getElementById("xslt");
    		initSeeyonForm(xml.value,xsl.value);
    		
    		//重新生成公文单后，重新显示意见，手写内容
    		initSpans();
    		dispOpinions(opinions,sendOpinionStr);
    		initHandWrite();
    		substituteLogo(logoURL);    		
    		
    		if(isIncludeCanUpdateElement==false)
    		{
    			alert(_("edocLang.edoc_alertNoAccessUpdateEdocForm"));
    			return;
    		}
    		setObjEvent();
    		
			if(edocMarkUpd)
			{
				var obj=document.getElementById("my:doc_mark");
				if(obj!=null && obj.tagName=="SELECT")
				{
					addSelectOption(obj,edocMark1,edocMarkDis1);
				}
				obj=document.getElementById("my:doc_mark2");
				if(obj!=null && obj.tagName=="SELECT")
				{
					addSelectOption(obj,edocMark2,edocMarkDis2);
				}				
			}
    		
    		try{
   			if(firstCanEditElementId!="")
    			{
    			  var firstEditObj=document.getElementById(firstCanEditElementId);
    			  if(firstEditObj!=null)
    			  {    			  	
    			  	firstEditObj.focus();
    			  }
    			}
    		}catch(e)	
    		{		
    		}
    		isUpdateEdocForm=true;
    		//记录日志的时候不能区别出是修改了文单还是仅仅修改了文号。加此变量就是为了区别这个 BUG30034
    		var isOnlyModifyWordNo=document.getElementById("isOnlyModifyWordNo");
    		if(isOnlyModifyWordNo)isOnlyModifyWordNo.value="false";
    		edocUpdateForm=true;
    		parent.contentIframe.showEdocMark();
    		return;
}

function setWordNoEdit(wordObj)
{
	var access = wordObj.getAttribute("access");
	if (access == "browse")
		return ;
	
	var i;
	var jsObj=null;
	var divObj=null;
	if(wordObj.disabled==true || (wordObj.tableName!="SELECT" && wordObj.readOnly==true))
	{
		for(i=0;i<fieldInputListArray.length;i++)
		{			
			if(fieldInputListArray[i].fieldName==wordObj.name)
			{
				jsObj=fieldInputListArray[i];
				break;
			}
		}			
		if(jsObj!=null)
		{	
			jsObj.change(wordObj);		
			addEditWordNoImage(document.getElementById(wordObj.id));
		}
		isUpdateEdocForm=true;
	}
}
/**
 *
 * @param obj
 * @return
 */
function getInputJsObject(obj){
	var jsObj = null;
	for(i=0;i<fieldInputListArray.length;i++)
	{			
		if(fieldInputListArray[i].fieldName==obj.name)
		{
			jsObj=fieldInputListArray[i];
			break;
		}
	}
	return jsObj;
}
function setSerialNoEdit(wordObj)
{
	var i;
	var jsObj=null;
	if(wordObj.disabled==true || (wordObj.tableName!="SELECT" && wordObj.readOnly==true))
	{
		jsObj = getInputJsObject(wordObj);
		if(jsObj!=null)
		{	
			jsObj.change(wordObj);
			if(_isTemplete!=true    //不是模板
					&& personInput=='true'  //允许手写
					&& typeof(isBoundSerialNo)!="undefined" && isBoundSerialNo !="true")//如果是调用模板。模板没有绑定内部文号
			
				addEditSerialNoImage(document.getElementById(wordObj.id));
		}
	}
}
function isWordNoReadCanEdit(obj){
	if(obj == null || (obj && obj.getAttribute("access") == "browse")){
		return false;
	}
	return true;
}
function isWordNoReadonlyAll(){
	var isAlertEdocMark = true;
	var docMark = document.getElementById("my:doc_mark");
	var docMark2 = document.getElementById("my:doc_mark2");
	var sobj = document.getElementById("my:serial_no");
	if(isWordNoReadCanEdit(docMark) 
			||isWordNoReadCanEdit(docMark2)
			||isWordNoReadCanEdit(sobj)){
		isAlertEdocMark = false;
	}
	
	if(isAlertEdocMark){
		alert("只读权限文号不能编辑");
		return true;
	}
	return false;
	
}
/** 点击修改文号小图片跳转
* 33469 修改方案：(320)
*1、当页面只有公文文号，没有内部文号的时候就采用以前的方式，即弹出断号选择框的方式
*2、当页面即有公文文号又有内部文号的时候，则不弹出选择框，直接在文单中置成可以修改的形式。让用户点击图标
*/
function WordNoChange(objid)
{	
	//都不能编辑的时候提醒不能编辑。
	if(isWordNoReadonlyAll())
		return false;
	
	if(!(typeof(currentPage)!='undefined' && currentPage == 'newEdoc')){
		//不是拟文页面点文号修改先执行页面跳转
		parent.showPrecessAreaTd("edocform");
	}
	var _obj;
	var selDocmark="my:doc_mark"; //给选择文号页面传递的参数，用来确定是第一套文号还是第二套文号。
	
	var templeteIdValue = "";
	var templeteId = document.getElementById("templeteId");
	if(templeteId) 	templeteIdValue = templeteId.value;

	if(objid==null)
	{
		_obj = document.getElementById("my:doc_mark");	
		if(_obj==null) {
		    _obj=document.getElementById("my:doc_mark2");	
		    selDocmark="my:doc_mark2";
		}
	}
	else
	{
		_obj = document.getElementById(objid);
		selDocmark=objid;
	}
	var serialNoObj = document.getElementById("my:serial_no");
	var docMarkObj = document.getElementById("my:doc_mark");
	var docMark2Obj = document.getElementById("my:doc_mark2");
	if(docMarkObj==null && docMark2Obj==null&& serialNoObj == null)
	{
		alert(_("edocLang.edoc_form_noDocMark"));
		return;
	}

	isUpdateEdocForm = true;
	var type = document.getElementById("edocType");
	if(type != null && type.value=="1")
	{//收文，直接录入文号，不提供断号选择功能
		_obj.readOnly=false;
		_obj.disabled=false;
		_obj.focus();
		if(!objid){ //修改文号的时候不是点图标
			if(serialNoObj != null){
				setSerialNoEdit(serialNoObj);
			}
		}
		if (!isEdocLike)
			showEdocMark();
		return;
	}
	//判断页面是否有两个文号
	var twoDocmark=objid==null && (document.getElementById("my:doc_mark")!=null && document.getElementById("my:doc_mark2")!=null);
	var orgAccountId=document.getElementById("orgAccountId");
	var _orgAccountId="";
	if(orgAccountId)_orgAccountId=orgAccountId.value;
	
	if(docMarkObj!=null)setWordNoEdit(docMarkObj);
	if(docMark2Obj!=null)setWordNoEdit(docMark2Obj);
	if(serialNoObj != null)setSerialNoEdit(serialNoObj);
	
	if(serialNoObj == null){
		if(_obj != null) {
			if (!isEdocLike)
				showEdocMark();
			openMarkChooseWindow(twoDocmark,selDocmark,_orgAccountId,templeteIdValue);
		}
	}else{  //有内部文号的时候不弹出选文号的界面。
		if(!objid){
			if (!isEdocLike)
				showEdocMark();
		} else {
			openMarkChooseWindow(twoDocmark,selDocmark,_orgAccountId,templeteIdValue);
		}
	}
	return;
}
function openMarkChooseWindow(twoDocmark,selDocmark,_orgAccountId,templeteIdValue){
	var receivedObj = v3x.openWindow({
		url: edocMarkURL+"?method=docMarkChooseEntry"+"&twoDocmark="+twoDocmark+"&selDocmark="+selDocmark
			+"&orgAccountId="+_orgAccountId+"&templeteId="+templeteIdValue,
		width:"450",
		resizable : "no",
		height:"380"
	});
	
	if(receivedObj != undefined){
		var id = receivedObj[2];
		var _objMark = document.getElementById(id);			
		var _obj = document.getElementById(id+"_autocomplete");		

		var markArray = new Array();
		
		if (id=="my:doc_mark" && docMarkOriginalArr != null) {
			docMarkOriginalArr.push(receivedObj[0]);
			markArray = docMarkOriginalArr;
		} else if (id=="my:doc_mark2" && docMark2OriginalArr != null) {
			docMark2OriginalArr.push(receivedObj[0]);
			markArray = docMark2OriginalArr;
		} else if (id=="my:serial_no" && serialNoOriginalArr != null) {
			serialNoOriginalArr.push(receivedObj[0]);
			markArray = serialNoOriginalArr;
		}
		
		if (markArray.length > 0) {
			v3xautocomplete.autocomplete(_objMark.id,returnJson(markArray),{select:function(item,inputName){_objMark.value=item.value},button:true,autoSize:true,appendBlank:false,value:receivedObj[0]});
		}
		
		isUpdateEdocForm = true;
		edocMarkUpd=true;
	}
}
//弹出内部文号录入界面。
function SerialNoChange()
{
	var _obj= document.getElementById("my:serial_no");	

	//判断页面上是否存在内部文号
	if(_obj==null)
	{
		alert(_("edocLang.edoc_form_noSerialNo"));
		return;
	}
	if(_obj != null) {		
		var receivedObj = v3x.openWindow({
			url: edocMarkURL+"?method=serialNoInputEntry",
			width:"350",
			resizable : "no",
			height:"200"
		});

		if(receivedObj != undefined){
			var _objHidden = document.getElementById(receivedObj[2]);
			_obj = document.getElementById(receivedObj[2]+"_autocomplete");
			
			_objHidden.value=receivedObj[0];
			_obj.value=receivedObj[1];
			
			
//			setWordNoEdit(_obj);
//			for (var i = 0; i < _obj.options.length; i++) {
//				var a = _obj.options[i].value;
//				if (a == receivedObj[0]) {
//					_obj.options[i].selected = true;
//					return;
//				}
//			}
//			var option = document.createElement("OPTION");
//			option.value = receivedObj[0];
//			option.text = receivedObj[1];
//			_obj.options.add(option);
//			option.selected = true;
			isUpdateEdocForm = true;
			edocMarkUpd=true;
		}
	}		   
	return;
}
function initSpans()
{	
	var i,key;	
	var spanObjs=document.getElementsByTagName("span");
	opinionSpans=new Properties();
	for(i=0;i<spanObjs.length;i++)
	{		
		key=spanObjs[i].getAttribute("xd:binding");
		if(key!=null)
		{		
			//记录处理意见录入框的初始化大小，确定手写签批对话框大小;
			spanObjs[i].initWidth=spanObjs[i].style.width;
			spanObjs[i].initHeight=spanObjs[i].style.height;
			opinionSpans.put(key,spanObjs[i]);			
		}
	}	
}

function extendData(name, value){
	this.name = name;
	this.value = value;
}
var extendArray = new Array();

/*
 * 处理处理意见和处理人字体大小一致
 */
function changeFontsize(opinion,spanObj){
	try{
		
		if(spanObj!=null&&spanObj!=undefined){
			if(spanObj.style.fontSize!=null&&spanObj.style.fontSize!="")
			{
				//增加一个id，解决了ie7样式不起作用的问题
				opinion = opinion.replace(new RegExp("<span",'gm'),"<span id ='test' style='font-Size:"+spanObj.style.fontSize+";'");
			}
		}
		return opinion;
	}catch(e){}
}

/*显示公文处理意见*/
function dispOpinions(opinions,senderOpinion)
{
try{	
	var i;
	//if((opinions==null || opinions.length<=0) && (senderOpinion==null || senderOpinion=="")){return;}		
	var otherOpinion="";
	var spanObj;
	var isboundSender = false;
	if(opinionSpans==null){initSpans();}
	for(i=0;i<opinions.length;i++)
	{
		if(opinions[i][0] =="niwen" || opinions[i][0] == 'dengji' ){isboundSender = true;}
		spanObj=opinionSpans.get("my:"+opinions[i][0],null);
		opinions[i][1]=changeFontsize(opinions[i][1],spanObj);
		if(spanObj==null||spanObj==undefined)
		{
			if(otherOpinion!=""){otherOpinion+="<br>";}
			otherOpinion+=opinions[i][1];
		}
		else
		{	
			spanObj.innerHTML=opinions[i][1];
		//	spanObj.title=spanObj.innerText;
			spanObj.style.height="auto"||"100%";
			spanObj.style.border="0px";
			spanObj.contentEditable="false";
			spanObj.style.whiteSpace = "normal";
			//spanObj.style.overflowY ="auto";
			extendArray[extendArray.length] = new extendData("my:"+opinions[i][0], spanObj.innerText);
		}
	}
	spanObj=opinionSpans.get("my:otherOpinion",null);
//	if(spanObj!=null)
//	{
//		spanObj.innerHTML=otherOpinion;
//		//20090909guoss修改，后台中将没有匹配成功的都放入了otherOpinion中。
//		//try{spanObj.innerHTML=opinions[opinions.length-1][1];}catch(e){}
//		spanObj.style.height="100%";
//		spanObj.style.whiteSpace = "normal";
//		spanObj.contentEditable="false";
//		spanObj.style.border="0px";		
//	}
	//else
	//{//公文单上面，未找到显示处理意见的位置，显示到公文单后面的div中
		if(otherOpinion!="" && spanObj == null)
		{
			spanObj=document.getElementById("displayOtherOpinions");
			if(spanObj!=null)
			{
				spanObj.innerHTML=otherOpinion;
				spanObj.style.visibility="visible";
				spanObj.style.height="100%";
				spanObj.style.whiteSpace = "normal";
				spanObj.contentEditable="false";
				spanObj.style.border="0px";
			}
		}
	//}
	
	//设置发起人意见
	spanObj=opinionSpans.get("my:niwen",null);
	if(spanObj==null){spanObj=opinionSpans.get("my:dengji",null);}
		//当有登记意见和拟文意见，则为登记意见（实际的情况是不出现登记和拟文意见同时出现的情况）
	if(opinionSpans.get("my:niwen",null)!=null&&opinionSpans.get("my:dengji",null)!=null){
		spanObj=opinionSpans.get("my:dengji",null);
	}
	if(spanObj!=null && senderOpinion!=null && senderOpinion!="")
	{
		//spanObj.innerHTML=senderOpinion;
		//spanObj.style.height="100%";
		//spanObj.style.whiteSpace = "normal";
		//公文单上面有意见显示位置时，隐藏公文单下面的意见
		spanObj=document.getElementById("displaySenderOpinoinDiv");
		if(spanObj!=null && isboundSender ){spanObj.innerHTML="";}
	}
	if(senderOpinion=="")
	{//没有发起意见,或者发起意见意见绑定到其它显示位置;
		spanObj=document.getElementById("displaySenderOpinoinDiv");
		if(spanObj!=null){spanObj.innerHTML="";}
	}
	//意见录入框:320
	
//	spanObj=opinionSpans.get("my:"+position);
//	if(spanObj != null){
//		spanObj.innerHTML += opinionInputWindow(parent.tempOpinion,parent.canUploadRel,relAttButton,
//				parent.canUploadAttachment,attButton,attContfileUpload,parent.attitudeString,parent.commonPhrase);
//		spanObj.style.height="100%";
//		spanObj.focus();
//	}                      
}catch(e){}
}

function hiddeBorder(opn)
{		
	var ops = opn.split(",");
	var spanObj;
	if(opinionSpans==null){initSpans();}
	for(i=0;i<ops.length;i++)
	{
		spanObj=opinionSpans.get("my:"+ops[i]);
		if(spanObj!=null)
		{			
			spanObj.style.border="0px";			
		}
	}
	//20090917guoss修改，其它意见不在ops中，将其它意见的文本框也同时去掉
	if(opinionSpans.get("my:otherOpinion") !=null){
			opinionSpans.get("my:otherOpinion").style.border="0px";
		}
}


/*新建公文，公文提交的时候需要把disable的数据变成可编辑的，否则不能提交到后台
 * 如果已经是修改状态，不做任何修改，如果为disable状态，需要根据数值转换成selet
 * 
 */
 
 function adjustReadFormForSubmit(){
 	
 	try{
 		//公文单下拉不能disable
  		var selEdoctable=document.getElementById("edoctable");
  		if(selEdoctable!=null)
  		{
  			selEdoctable.disabled=false;
  		}
 	}catch(e)
 	{ 		
 	}
	
	var s = document.getElementById("xml").value;
	var x = document.getElementById("xslt").value;
	
	var str = s;
	var xslStart = str.indexOf("&&&&&&&  xsl_start  &&&&&&&&");
	var dataStart = str.indexOf("&&&&&&&&  data_start  &&&&&&&&");
	var inputStart = str.indexOf("&&&&&&&&  input_start  &&&&&&&&");
	
	if(xslStart == -1)
			throw  "没有找到xsl";
	if(dataStart == -1)
			throw "没有找到data";
	if(inputStart == -1)
			throw "没有找到input";
	var xsl = str.slice(xslStart + 28, dataStart);
	var data = str.slice(dataStart + 30, inputStart);
	var finput = str.slice(inputStart + 31);	
  	ajustReadInput(finput);  	
}

function ajustReadInput(aInput)
{
	var fFiledList = paseFormatXML(getXmlContent(aInput));	
	adjustConvertReadHtml(fFiledList);
	var repeatTable = new Seeyonform_rtable();
	repeatTable.change();
	
	var reSection = new Seeyonform_rsection();
	reSection.change();
}
function changeSelectValue(dis,SeeyonformSelect)
{
	var i;
	var ls=	SeeyonformSelect.valueList;
	for(i=0;i<ls.length;i++)
	{
		if(ls[i].label==dis)
		{
			return ls[i].value;
		}
	}
	return dis;
}
function adjustConvertReadHtml(aFieldList){
	var field;
	var fAreas;
	var ocxNameList="";
	for(var i = 0; i < aFieldList.length; i++){
		 field = aFieldList[i];
		 var inputObj=document.getElementById(field.fieldName);
		 if(inputObj==null){continue;}
		 var isSel= field instanceof Seeyonform_select;
		 
		 if(inputObj.disabled==false&&inputObj.readOnly==false){continue;}
		 
		 if(ocxNameList.indexOf("["+field.fieldName+"]")==-1){ocxNameList+="["+field.fieldName+"]";}
		 else {if(isSel==false){continue;}}
		 
		 if(isSel)
		 {
		 	inputObj.value=changeSelectValue(inputObj.value,field);
		 }		 
		 inputObj.disabled=false;
		 inputObj.canSubmit="true";			 
  }
}

/***************公文单控制部分结束*******************/

//----------------------------  公文套红部分中

//套红的时候，如果是联合发文，会用两个单位的套红模板将正文分别套红，形成两套正文。
//套红的时候，如果是联合发文，检查公文的第一套正文或者第二套正文是否已经被创建，没有创建就创建，已经创建就返回当前正文ID
//创建的方式：createContentBody会在后台向edocbody表中添加记录，并且返回新的正文ID（newOfficeID）,
//          并且将newOfficeID赋值给控件的fileID.,这样保存的时候就会创建新的正文，并且向file表中添加新的记录。
function checkExistBody()
{
	var isUniteSend=document.getElementById("isUniteSend").value;
	var summaryId=theform.summary_id.value;
	var contentNum=theform.currContentNum.value;
	var bodyType = document.getElementById("bodyType").value;	
	if(contentUpdate==false || isUniteSend!="true"){return ;}
	if(contentOfficeId.get(contentNum,null)==null)
  	{
  		var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocManager", "createContentBody",false);
		requestCaller.addParameter(1, "String", summaryId);
		requestCaller.addParameter(2, "int", contentNum);
		requestCaller.addParameter(3, "String", contentOfficeId.get("0",null));
		requestCaller.addParameter(4, "String", bodyType);
		var ds = requestCaller.serviceRequest();
		fileId=ds;		
		contentOfficeId.put(contentNum,ds);		
  	}
  	else
  	{
  		fileId=contentOfficeId.get(contentNum,null);
  	}
  	return fileId;
  	//setOfficeOcxRecordID(fileId);
}

//新建公文模板的时候进行正文套红
function taohongWhenTemplate(templateType){
	var bodyType = document.getElementById("bodyType").value;
	var loginAccountId=document.getElementById("loginAccountId").value;
   	if(bodyType=="HTML" && templateType=="edoc")
   	{
  	 	alert(_("edocLang.edoc_htmlnofuntion"));
  	 	return;
   	}
  	if(bodyType=="OfficeExcel" && templateType=="edoc")//excel不能进行正文套红。
   	{
  	 	alert(_("edocLang.edoc_excelnofuntion"));
  	 	return;
   	}
  	if( bodyType == "WpsExcel" && templateType=="edoc")//et不能进行正文套红。
   	{
  	 	alert(_("edocLang.edoc_wpsetnofuntion"));
  	 	return;
   	}
	if(bodyType=="Pdf" && templateType=="edoc"){
	    alert(_("edocLang.edoc_pdfnofuntion"));
	    return;
	}
		//Ajax判断是否存在套红模板
	if(!hasEdocDocTemplate(loginAccountId,templateType,bodyType)){
		alert(v3x.getMessage('edocLang.edoc_docTemplate_record_notFound'));
		return;
	}
	//判断是否是联合发文
	var docMark=document.getElementById("my:doc_mark");
	var docMark2=document.getElementById("my:doc_mark2");
	var isUniteSend=docMark&&docMark2?"true":"false";
	//document.getElementById("isUniteSend").value=isUniteSend;
	var contentNumObj=document.getElementById("currContentNum");
	if(isUniteSend=="true"){
		alert(_("edocLang.edoc_UniteSendnofuntion"));
		return;
	}
	
	if(bodyType.toLowerCase()=="officeword" || bodyType.toLowerCase()=="wpsword" || templateType=="script"){
  	   if(templateType=="edoc")
  	   {
  	   	//正文套紅將會自動清稿，你確定要這麼做嗎?
  	     if(confirm(_("edocLang.edoc_alertAutoRevisions"))){
  	     	//清除正文痕迹并且保存
  	     	//removeTrailAndSave();
  	     	//清除正文痕迹并且保存,与处理过程中的框架结构不一样，所以采用另外的方法
  	     	if(!removeTrailAndSaveWhenTemplate()){
  	     		alert(_("edocLang.edoc_contentSaveFalse")); 
  	     		return;
  	     	}		
  	     }else {
  	     	 return;
  	     }
  	   }
  	   //设置原始正文的ID
  	   //保存的时候后台生成的ID才是正文真正的ID，JSP页面生成的ID不起作用。
  	   if(templateType=="script"){bodyType="";}
   	   var receivedObj = v3x.openWindow({
     	   url: templateURL + "?method=taoHongEntry&templateType="+templateType+"&bodyType="+bodyType+"&isUniteSend="+isUniteSend+"&orgAccountId="+loginAccountId,
     	   width:"350",
    	   height:"250"
  	  });
	  if(receivedObj==null){return;}
  	  var taohongTemplateContentType="";
  	  if(isUniteSend=="true" && templateType=="edoc"){
  	  	 var ts=receivedObj[0].split("&");
  	  	 taohongTemplateContentType=ts[1];
  	  	 receivedObj[0]=ts[0];
  	  }
  	  else
  	  {
  	  	 var ts=receivedObj.split("&");
  	  	 taohongTemplateContentType=ts[1];
  	  	 receivedObj=ts[0];
  	  }
  	  if(taohongTemplateContentType=="officeword"){taohongTemplateContentType="OfficeWord";}
  	  else if(taohongTemplateContentType=="wpsword"){taohongTemplateContentType="WpsWord";}  	  
  	  
  	  if(receivedObj==null){return;}
  	  if(templateType=="script")
  	  {
  	  	var urlStr=genericURL + "?method=wendanTaohongIframe&summaryId="+document.getElementById("summaryId").value;
  	  	urlStr+="&tempContentType="+taohongTemplateContentType;
  	  	
  	  	page_receivedObj=receivedObj;
  	  	page_templateType=templateType;
  	  	page_extendArray=extendArray;
  	  	
  	  	v3x.openWindow({url: urlStr,workSpace: 'yes'});
  	  	var redForm = parent.parent.detailRightFrame.theform.redForm;
  	  	if(redForm && templateType=="script"){
  	  			redForm.value = "true";
  	  		}
  	    //window.open(urlStr);
  	  }
  	  else
  	  {  	  	
  	  	setOfficeOcxRecordID(fileId);
  	  	if(isUniteSend!="true")
  	  	{
  	  		officetaohong(document.getElementsByName("sendForm")[0],receivedObj,templateType,extendArray);
  	  		contentUpdate=true;
  	  		hasTaohong = true ;
  	  	}
  	  	else
  	  	{  	  		
  	  		officetaohong(document.getElementsByName("sendForm")[0],receivedObj[0],templateType,extendArray);
  	  		contentNumObj.value=receivedObj[1];
  	  		//设置此参数，用于保存公文和发送公文时候保存正文。
  	  		document.getElementById("contentNo").value=receivedObj[1];
  	  		contentUpdate=true;
  	  		hasTaohong = true ;
  	  	}
  	  }
  	}
}


//拟文的时候进行正文套红，与处理过程中的套红有所区别所以拟文的时候进行正文套红单独使用此方法
//function taohongWhenDraft(templateType)
//{
//
//	var bodyType = document.getElementById("bodyType").value;	
//	//Ajax判断是否存在套红模板
//	if(!hasEdocDocTemplate(templateType,bodyType)){
//		alert(v3x.getMessage('edocLang.edoc_docTemplate_record_notFound'));
//		return;
//	}
//	//var isUniteSend=document.getElementById("isUniteSend").value;
//	//判断是否是联合发文
//	
//	var docMark=document.getElementById("my:doc_mark");
//	var docMark2=document.getElementById("my:doc_mark2");
//	var isUniteSend=docMark&&docMark2?"true":"false";
//	document.getElementById("isUniteSend").value=isUniteSend;
//	var contentNumObj=document.getElementById("currContentNum");
//	
//   if(bodyType=="HTML" && templateType=="edoc")
//   {
//  	 alert(_("edocLang.edoc_htmlnofuntion"));
//  	 return;
//   }
//   if(bodyType=="OfficeExcel" && templateType=="edoc")//excel不能进行正文套红。
//   {
//  	 alert(_("edocLang.edoc_excelnofuntion"));
//  	 return;
//   }
//   else if(bodyType.toLowerCase()=="officeword" || bodyType.toLowerCase()=="wpsword" || templateType=="script"){
//  	   if(templateType=="edoc")
//  	   {
//  	   	//正文套紅將會自動清稿，你確定要這麼做嗎?
//  	     if(confirm(_("edocLang.edoc_alertAutoRevisions"))){
//  	     	//清除正文痕迹并且保存
//  	     	//removeTrailAndSave();
//  	     	//清除正文痕迹并且保存,与处理过程中的框架结构不一样，所以采用另外的方法
//  	     	if(!removeTrailAndSaveWhenDraft()){
//  	     		alert(_("edocLang.edoc_contentSaveFalse")); 
//  	     		return;
//  	     	}		
//  	     }else {
//  	     	 return;
//  	     }
//  	   }
//  	   //设置原始正文的ID
//  	   //保存的时候后台生成的ID才是正文真正的ID，JSP页面生成的ID不起作用。
//		if(oFileId==""){
//			oFileId=fileId;
//		}
//  	   if(templateType=="script"){bodyType="";}
//   	   var receivedObj = v3x.openWindow({
//     	   url: templateURL + "?method=taoHongEntry&templateType="+templateType+"&bodyType="+bodyType+"&isUniteSend="+isUniteSend,
//     	   width:"350",
//    	   height:"250"
//  	  });
//	  if(receivedObj==null){return;}
//  	  var taohongTemplateContentType="";
//  	  if(isUniteSend=="true" && templateType=="edoc"){
//  	  	 var ts=receivedObj[0].split("&");
//  	  	 taohongTemplateContentType=ts[1];
//  	  	 receivedObj[0]=ts[0];
//  	  }
//  	  else
//  	  {
//  	  	 var ts=receivedObj.split("&");
//  	  	 taohongTemplateContentType=ts[1];
//  	  	 receivedObj=ts[0];
//  	  }
//  	  if(taohongTemplateContentType=="officeword"){taohongTemplateContentType="OfficeWord";}
//  	  else if(taohongTemplateContentType=="wpsword"){taohongTemplateContentType="WpsWord";}  	  
//  	  
//  	  if(receivedObj==null){return;}else{
//  	  	//设置隐藏域的值，用来记录日志，是否进行了套红。
////  	  	var redContent = parent.parent.detailRightFrame.theform.redContent;
////  	  	if(redContent && templateType=="edoc"){
////  	  			redContent.value = "true";
////  	  		}
//  	  	}
//  	  if(templateType=="script")
//  	  {
//  	  	var urlStr=genericURL + "?method=wendanTaohongIframe&summaryId="+document.getElementById("summaryId").value;
//  	  	urlStr+="&tempContentType="+taohongTemplateContentType;
//  	  	
//  	  	page_receivedObj=receivedObj;
//  	  	page_templateType=templateType;
//  	  	page_extendArray=extendArray;
//  	  	
//  	  	v3x.openWindow({url: urlStr,workSpace: 'yes'});
//  	  	var redForm = parent.parent.detailRightFrame.theform.redForm;
//  	  	if(redForm && templateType=="script"){
//  	  			redForm.value = "true";
//  	  		}
//  	    //window.open(urlStr);
//  	  }
//  	  else
//  	  {  	  	
//  	  	setOfficeOcxRecordID(oFileId);
//  	  	if(isUniteSend!="true")
//  	  	{
//  	  		officetaohong(document.getElementsByName("sendForm")[0],receivedObj,templateType,extendArray);
//  	  		contentUpdate=true;
//  	  		hasTaohong = true ;
//  	  	}
//  	  	else
//  	  	{  	  		
//  	  		officetaohong(document.getElementsByName("sendForm")[0],receivedObj[0],templateType,extendArray);
//  	  		contentNumObj.value=receivedObj[1];
//  	  		//设置此参数，用于保存公文和发送公文时候保存正文。
//  	  		document.getElementById("contentNo").value=receivedObj[1];
//  	  		contentUpdate=true;
//  	  		hasTaohong = true ;
//  	  	}
//  	  }
//  	}
//  }
 //ajax判断是否存在套红模板
function hasEdocDocTemplate(orgAccountId,templateType,bodyType){
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocDocTemplateController", "hasEdocDocTemplate",false);
	requestCaller.addParameter(1, "Long", orgAccountId); 
  	requestCaller.addParameter(2, "String", templateType);  
  	requestCaller.addParameter(3, "String", bodyType);  	
  	var ds = requestCaller.serviceRequest();  
  	//"0":没有，“1”：有	
  	if(ds=="1"){return true;}
  	else {return false;} 
}
//处理过程中的正文套红
function taohong(templateType)
{
	var bodyType = document.getElementById("bodyType").value;	
	var isUniteSend=document.getElementById("isUniteSend").value;
	var contentNumObj=document.getElementById("currContentNum");
	var orgAccountId=document.getElementById("orgAccountId").value;
	
   	if(bodyType=="HTML" && templateType=="edoc")
   	{
  	 	alert(_("edocLang.edoc_htmlnofuntion"));
  	 	return;
   	}
   	if(bodyType=="OfficeExcel" && templateType=="edoc")//excel不能进行正文套红。
   	{
  	 	alert(_("edocLang.edoc_excelnofuntion"));
  	 	return;
   	}
 	if(bodyType == "WpsExcel" && templateType=="edoc")//excel不能进行正文套红。
   	{
  	 	alert(_("edocLang.edoc_wpsetnofuntion"));
  	 	return;
   	}
	if(bodyType=="Pdf" && templateType=="edoc"){
	    alert(_("edocLang.edoc_pdfnofuntion"));
	    return;
	}
	//Ajax判断是否存在套红模板
	if(templateType=="script"){bodyType="";}
	if(!hasEdocDocTemplate(orgAccountId,templateType,bodyType)){
		alert(v3x.getMessage('edocLang.edoc_docTemplate_record_notFound'));
		return;
	}
	
	if(bodyType.toLowerCase()=="officeword" || bodyType.toLowerCase()=="wpsword" || templateType=="script"){
  	   if(templateType=="edoc")
  	   {
  	   	//判断是否有印章，有印章的时候不允许套红。
  	   	if(getSignatureCount()>0){
  	   		alert(_("edocLang.edoc_notaohong_signature"));
  			return;
  	   	}
  	   	//正文套紅將會自動清稿，你確定要這麼做嗎?
  	     if(confirm(_("edocLang.edoc_alertAutoRevisions"))){
  	     	//清除正文痕迹并且保存
  	     	if(!removeTrailAndSave())return;	
  	     }else {
  	     	 return;
  	     }
  	   }
   	   var receivedObj = v3x.openWindow({
     	   url: templateURL + "?method=taoHongEntry&templateType="+templateType+"&bodyType="+bodyType+"&isUniteSend="+isUniteSend+"&orgAccountId="+orgAccountId,
     	   width:"350",
    	   height:"250"
  	  });
	  if(receivedObj==null){return;}
  	  var taohongTemplateContentType="";
  	  if(isUniteSend=="true" && templateType=="edoc"){
  	  	 var ts=receivedObj[0].split("&");
  	  	 taohongTemplateContentType=ts[1];
  	  	 receivedObj[0]=ts[0];
  	  }
  	  else
  	  {
  	  	 var ts=receivedObj.split("&");
  	  	 taohongTemplateContentType=ts[1];
  	  	 receivedObj=ts[0];
  	  }
  	  if(taohongTemplateContentType=="officeword"){taohongTemplateContentType="OfficeWord";}
  	  else if(taohongTemplateContentType=="wpsword"){taohongTemplateContentType="WpsWord";}  	  
  	  
  	  //记录字段值为TRUE，JS用来记录套红操作
  	  if(receivedObj==null){
  		  return;
  	   }else{
  	  	var redContent =document.getElementById("redContent");
  	  	if(redContent && templateType=="edoc"){
  	  			redContent.value = "true";
  	  	}
  	  }
  	  
  	  
  	  if(templateType=="script")
  	  {
  	  	var urlStr=genericURL + "?method=wendanTaohongIframe&summaryId="+document.getElementById("summary_id").value;
  	  	urlStr+="&tempContentType="+taohongTemplateContentType;
  	  	
  	  	page_receivedObj=receivedObj;
  	  	page_templateType=templateType;
  	  	page_extendArray=extendArray;
  	  	
  	  	v3x.openWindow({url: urlStr,workSpace: 'yes'});
  	  	var redForm =document.getElementById("redForm");
  	  	if(redForm && templateType=="script"){
  	  		redForm.value = "true";
  	  	}
  	  }
  	  else
  	  {  	  	
  	  	setOfficeOcxRecordID(contentOfficeId.get("0",null));
  	  	if(isUniteSend!="true")
  	  	{
  	  		officetaohong(contentIframe.document.getElementsByName("sendForm")[0],receivedObj,templateType,extendArray);
  	  		contentUpdate=true;
  	  		hasTaohong = true ;
  	  	}
  	  	else
  	  	{  	  		
  	  		officetaohong(contentIframe.document.getElementsByName("sendForm")[0],receivedObj[0],templateType,extendArray);
  	  		contentNumObj.value=receivedObj[1];
  	  		contentUpdate=true;
  	  		hasTaohong = true ;
  	  	}
  	  }
  	}
  }
	/**
	 * 取意见用于套红
	 * 1、取页面对象中的意见，不直接从JS变量里面取，目的就是去掉JS变量里面的
	 * 2、页面没有绑定的意见不能套红到模板中。
	 */
	function getOpinionsForTaoHong(){
		try	{
			var ops = new Array();
			var isContainNiwenOrDengji = false;
			var opinions = contentIframe.opinions;
			var opinionSpans = contentIframe.opinionSpans;
			for(i=0;i<opinions.length;i++)
			{
				spanObj=opinionSpans.get("my:"+opinions[i][0],null);
				if(spanObj!=null){
					var sopin = new Array();
					sopin[0] = opinions[i][0];
					sopin[1] = spanObj.innerText;
					ops.push(sopin);
					if(opinions[i][0] =='niwen'  || opinions[i][0] == 'dengji')
						isContainNiwenOrDengji = true;
				}
			}	
				
			var opArr = new Array();
			opArr[0] = ops;
			if(isContainNiwenOrDengji == false)
				opArr[1] = contentIframe.sendOpinionStr;
			else 
				opArr[1] = '';
			return opArr;
		}catch(e){
			return new Array();
		}
	}

  //-----------------------------------------------
  
  function supervise(){
     document.getElementById("supervise_div_date").className = "";
     document.getElementById("supervise_div_people").className = "";
  }
  
function arrayToArray(array){
	var r = [];
	if(array != null){
		for(var i = 0; i < array.length; i++) {
			r[i] = array[i];
		}
	}
	
	return r;
}
  
//function edocUpdateEdocFlash(processId,activityId,operationType,elements,commandType,manualSelectNodeId,peopleArr,summaryId,conditions,nodes){
//	var rs = null;
//	var str = "";
//		
//	var idArr = new Array();
//	var typeArr = new Array();
//	var nameArr = new Array();
//	var accountIdArr = new Array();
//	var accountShortNameArr = new Array();
//	var selecteNodeIdArr = new Array();
//	var _peopleArr = new Array();
//	var conditionArr = new Array();
//	var nodesArr = new Array();
//	
//	if(commandType == "addNode" || commandType == "replaceNode"){
//		if (!elements) {
//			return false;
//		}
//	
//		var personList = elements[0] || [];
//		var flowType = elements[1] || 0;
//		var isShowShortName = elements[2] || "false";
//		var process_desc_by = "people";
//		
//		str = processId + "," + activityId + "," + operationType + "," + flowType + "," + isShowShortName + "," + process_desc_by;
//		
//		for (var i = 0; i < personList.length; i++) {
//			var person = personList[i];
//			idArr.push(person.id);
//			typeArr.push(person.type);
//			nameArr.push(person.name);
//			accountIdArr.push(person.accountId);
//			accountShortNameArr.push(person.accountShortname);
//			selecteNodeIdArr = [];
//			_peopleArr = [];
//		}
//	}else if(commandType == "delNode"){
//		str = processId + "," + activityId + "," + operationType + "," + null + "," + null + "," + null;
//		idArr = [];
//		typeArr = [];
//		nameArr = [];
//		accountIdArr = [];
//		accountShortNameArr = [];
//		if(manualSelectNodeId && peopleArr && manualSelectNodeId.length != 0 && peopleArr.length != 0){
//			selecteNodeIdArr = arrayToArray(manualSelectNodeId);
//			_peopleArr = arrayToArray(peopleArr);
//		}else{
//			selecteNodeIdArr = [];
//			_peopleArr = [];
//		}
//		if(conditions && conditions.length!=0){
//			conditionArr = arrayToArray(conditions);
//			nodesArr = arrayToArray(nodes);
//		}else{
//			conditionArr = [];
//			nodesArr = [];
//		}
//	}
//	try {
//		var requestCaller = new XMLHttpRequestCaller(null, "ajaxEdocSuperviseManager", "changeProcess", false, "POST");
//		requestCaller.addParameter(1, "String", str);
//		requestCaller.addParameter(2, "String[]", idArr);
//		requestCaller.addParameter(3, "String[]", typeArr);
//		requestCaller.addParameter(4, "String[]", nameArr);
//		requestCaller.addParameter(5, "String[]", accountIdArr);
//		requestCaller.addParameter(6, "String[]", accountShortNameArr);
//		requestCaller.addParameter(7, "String[]", selecteNodeIdArr);
//		requestCaller.addParameter(8, "String[]", _peopleArr);
//		requestCaller.addParameter(9, "String", summaryId);
//		requestCaller.addParameter(10, "String[]", conditionArr);
//		requestCaller.addParameter(11, "String[]", nodesArr);
//		rs = requestCaller.serviceRequest();
//	}
//	catch (ex1) {
//		alert("Exception : " + ex1);
//	}
//	
//    return rs;
//}
//function edocUpdateEdocFlash1(flowProp,policyStr,summaryId){
//	var rs = null;
//	try {
//		var requestCaller = new XMLHttpRequestCaller(null, "ajaxEdocSuperviseManager", "changeProcess1", false, "POST");
//		requestCaller.addParameter(1, "String[]", arrayToArray(flowProp));
//		requestCaller.addParameter(2, "String[]", arrayToArray(policyStr));
//		requestCaller.addParameter(3, "String", summaryId);
//		rs = requestCaller.serviceRequest();
//	}
//	catch (ex1) {
//		alert("Exception : " + ex1);
//	}
//	
//    return rs;
//}
  
  
function selectAllValues(allButton, targetName){
	var objcts = document.getElementsByName(targetName);
	if(objcts != null){
		for(var i = 0; i < objcts.length; i++){
			if(!objcts[i].disabled){
				objcts[i].checked = allButton.checked;
			}
		}
	}
}
function _getWordNoValue(inputObj)
{
	var markStr="";	
	var inputValue="";
	if(inputObj.tagName=="INPUT" && (inputObj.type=="text" || inputObj.type=="hidden"))
	{
		inputValue=inputObj.value;
		if(inputValue!=null&&inputValue.indexOf("|")>0){
			inputValue=inputValue.split("|")[1];
		}
		markStr=inputValue;
	}
	else if(inputObj.tagName=="SELECT")	
	{
		inputValue=inputObj.options[inputObj.selectedIndex].value;
		if(inputValue==""){return "";}
		markStr=inputValue.split("|")[1];
	}else if(inputObj.tagName=="TEXTAREA"){
		inputValue=inputObj.value;
		markStr=inputValue;
	}	
	return markStr.trim();
}
/*校验是否录入了公文文号*/
function checkEdocWordNo()
{	
	var markStr="";
	var inputObj=document.getElementById("my:doc_mark");
	if(inputObj!=null)
	{		
		markStr=_getWordNoValue(inputObj);
		if(markStr=="")
		{
			alert(_("edocLang.doc_mark_alter_not_null"));
			try{inputObj.focus();}catch(e){}
			return false;
		}	
		var markUsed =checkWordNoUser(markStr);
		if(markUsed)
		{
			alert(_("edocLang.doc_mark_alter_used"));
			return false;
		}
	}
	//bug 29522 公文文号封发时判断问题
	//else{//公文单中需要含有公文文号元素且不能为空才可封发。
	//	alert(_("edocLang.doc_mark_alter_include_and_not_null"));
	//	return false;
	//}
	var isUniteSend=document.getElementById("isUniteSend").value;
	if(isUniteSend=="true")
	{
		inputObj=document.getElementById("my:doc_mark2");
		if(inputObj!=null)
		{
			var markStr1=markStr;
			markStr=_getWordNoValue(inputObj);
			if(markStr=="")
			{
				alert(_("edocLang.doc_mark2_alter_not_null"));
				try{inputObj.focus();}catch(e){}
				return false;
			}
			//检查两个文号是否相同
			if(markStr1==markStr)
			{
				alert(_("edocLang.two_doc_mark_no_equ"));
				return false;
			}
			var markUsed =checkWordNoUser(markStr);
			if(markUsed)
			{
				alert(_("edocLang.doc_mark2_alter_used"));
				return false;
			}
		}
	}
	return true;
}
function checkWordNoUser(docMark)
{
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocMarkManager", "isUsed",false);
  	requestCaller.addParameter(1, "String", docMark);  
  	requestCaller.addParameter(2, "String", document.getElementById("summaryId").value);  	
  	var ds = requestCaller.serviceRequest();  	
  	if(ds=="true"){return true;}
  	else {return false;}  	
}
//处理时候，跟踪，处理后删除校验
var checkMulitSign_hasShowAlert = false;
function checkMulitSign(nowSelected){
	var afterSignObj = document.getElementsByName("afterSign");
	
	if(checkMulitSign_hasShowAlert == false){
		var flag = 0;
		for(var i = 0; i < afterSignObj.length; i++) {
			if(afterSignObj[i].checked){
				flag++;
			}
		}
		
		if(flag > 1){
			alert(_("edocLang.edoc_alertSignAfterOption"));
			checkMulitSign_hasShowAlert = true;
		}
	}
	
	for(var i = 0; i < afterSignObj.length; i++) {
		if(afterSignObj[i].id == nowSelected.id){
			continue;
		}
		
		afterSignObj[i].checked = false;
		if(afterSignObj[i].id=='isTrack'){
			var all = document.getElementById("trackRange_all");
			var part = document.getElementById("trackRange_part");
			if(all!=null) all.checked = false;
			if(part!=null) part.checked = false;
		}
	}
	//当前选择的是处理后归档
	var showPrePigeonhole=document.getElementById("showPrePigeonhole");
    var showSelectPigeonholePath=document.getElementById("showSelectPigeonholePath");
	if(nowSelected.id == 'pipeonhole'){
	    if(hasPrepigeonholePath=="true"){//有预归档目录
	        if(nowSelected.checked){  //选择状态，显示，否则隐藏归档路径区域
	            showPrePigeonhole.style.display="";
	        }else{
	            showPrePigeonhole.style.display="none";
	        }
	    }else if(hasPrepigeonholePath=='false'){//模板中没有设置预先归档
	        if(nowSelected.checked){
	            showSelectPigeonholePath.style.display="";
	        }else{
	            showSelectPigeonholePath.style.display="none";
	        }
	    }
	}else if(nowSelected.id =='track'){
		if(nowSelected.checked){
			if(showPrePigeonhole){
				showPrePigeonhole.style.display="none";
			}
			if(showSelectPigeonholePath){
				showSelectPigeonholePath.style.display="none";
			}
		}
	}
}
function chanageHtmlBodyType(contentType)
{
	var ret=chanageBodyType(contentType);
	if(ret)
	{//清空office的id
		var contentObj=document.getElementById("content");
		if(contentObj!=null)
		{
			contentObj.value="";
		}
	}
}

function substituteLogo(logoURL){
	    	//substitution of logo 【logo的替换】
    		var i,key,style,width,height;	
			var spanObjs=document.getElementsByTagName("span");
			for(i=0;i<spanObjs.length;i++)
			{		
				key=spanObjs[i].getAttribute("xd:binding");
				style = spanObjs[i].getAttribute("style");
				if(style!=null){
					if(typeof (style) == "string"){
						var _f = style.indexOf('width');
						if(_f!=-1){
							var _f2 = style.indexOf(':',_f);
							var _f3 = style.indexOf(';',_f);
							width = style.substring(_f2+1,_f3);
						}
						var _h = style.indexOf('height');
						if(_h!=-1){
							var _h2 = style.indexOf(':',_h);
							var _h3 = style.indexOf(';',_h);
							height = style.substring(_h2+1,_h3);
						}
					}else{
						width = style.getAttribute("width");
						height = style.getAttribute("height");
					}
					if(key == 'my:logoimg'){
						logoURL = logoURL.replace('img', 'img width='+ width + ' height='+ height);
						spanObjs[i].outerHTML = logoURL;
					}
				}
			}		
}

function checkExchangeAccountIsDuplicatedOrNot(){

	var returnValue = true;
	
	var sendToIds = getIdsString(selPerElements.get("my:send_to"),false);
	var reportToIds = getIdsString(selPerElements.get("my:report_to"),false);
	var copyToIds = getIdsString(selPerElements.get("my:copy_to"),false);

	//如果主送，抄报，抄送都存在
	if(sendToIds!=null && sendToIds !="" && reportToIds!=null && reportToIds != "" && copyToIds != null && copyToIds !=""){
	var tempArray_a = sendToIds.split(",");
	var tempArray_b = reportToIds.split(",");	
	var tempArray_c = copyToIds.split(",");
	for(var i=0;i<tempArray_a.length;i++){
		for(var j=0;j<tempArray_b.length;j++){
			if(tempArray_a[i] == tempArray_b[j]){;
				returnValue = false;
				break;
			}
				for(var z=0;z<tempArray_c.length;z++){
					if(tempArray_b[j] == tempArray_c[z] || tempArray_a[i] == tempArray_c[z]){
				returnValue = false;
				break;
					}
				}
		}
	}
	}
	
	//如果有主送，有抄报，而无抄送
	else if((sendToIds!=null && sendToIds !="") && (copyToIds !=null && copyToIds!="") && (reportToIds == null || reportToIds == "")){
	var tempArray_a = sendToIds.split(",");	
	var tempArray_c = copyToIds.split(",");
	for(var i=0;i<tempArray_a.length;i++){
				for(var z=0;z<tempArray_c.length;z++){
					if(tempArray_a[i] == tempArray_c[z]){
				returnValue = false;
				break;					}
				}
		}	
	}
	
	//如果有主送，有抄送，而无抄报
	else if((sendToIds!=null && sendToIds !="") && (reportToIds!=null && reportToIds != "") && (copyToIds ==null || copyToIds=="")){
	var tempArray_a = sendToIds.split(",");
	var tempArray_b = reportToIds.split(",");	
	for(var i=0;i<tempArray_a.length;i++){
				for(var j=0;j<tempArray_b.length;j++){
					if(tempArray_a[i] == tempArray_b[j]){
				returnValue = false;
				break;					}
				}
		}	
	}
	
	//如果无主送，有抄送，有抄报
	else if((reportToIds!=null && reportToIds != null) && (copyToIds !=null && copyToIds!="") && (sendToIds==null || sendToIds =="")){
	var tempArray_b = reportToIds.split(",");	
	var tempArray_c = copyToIds.split(",");	
	for(var i=0;i<tempArray_b.length;i++){
				for(var j=0;j<tempArray_c.length;j++){
					if(tempArray_b[i] == tempArray_c[j]){
						returnValue = false;
						break;
					}
				}
		}	
	}
	
	var sendToIds2 = getIdsString(selPerElements.get("my:send_to2"),false);
	var reportToIds2 = getIdsString(selPerElements.get("my:report_to2"),false);
	var copyToIds2 = getIdsString(selPerElements.get("my:copy_to2"),false);
	
	//如果主送，抄报，抄送都存在
	if(sendToIds2!=null && sendToIds2 !="" && reportToIds2!=null && reportToIds2 != "" && copyToIds2 != null && copyToIds2 !=""){
	var tempArray_a2 = sendToIds2.split(",");
	var tempArray_b2 = reportToIds2.split(",");	
	var tempArray_c2 = copyToIds2.split(",");
	for(var i=0;i<tempArray_a2.length;i++){
		for(var j=0;j<tempArray_b2.length;j++){
			if(tempArray_a2[i] == tempArray_b2[j]){;
				returnValue = false;
				break;
			}
				for(var z=0;z<tempArray_c2.length;z++){
					if(tempArray_b2[j] == tempArray_c2[z] || tempArray_a2[i] == tempArray_c2[z]){
				returnValue = false;
				break;
					}
				}
		}
	}
	}
	
	//如果有主送，有抄报，而无抄送
	else if((sendToIds2!=null && sendToIds2 !="") && (copyToIds2 !=null && copyToIds2!="") && (reportToIds2 == null || reportToIds2 == "")){
	var tempArray_a2 = sendToIds2.split(",");	
	var tempArray_c2 = copyToIds2.split(",");
	for(var i=0;i<tempArray_a2.length;i++){
				for(var z=0;z<tempArray_c2.length;z++){
					if(tempArray_a2[i] == tempArray_c2[z]){
				returnValue = false;
				break;					}
				}
		}	
	}
	
	//如果有主送，有抄送，而无抄报
	else if((sendToIds2!=null && sendToIds2 !="") && (reportToIds2!=null && reportToIds2 != "") && (copyToIds2 ==null || copyToIds2=="")){
	var tempArray_a2 = sendToIds2.split(",");
	var tempArray_b2 = reportToIds2.split(",");	
	for(var i=0;i<tempArray_a2.length;i++){
				for(var j=0;j<tempArray_b2.length;j++){
					if(tempArray_a2[i] == tempArray_b2[j]){
				returnValue = false;
				break;					}
				}
		}	
	}
	
	//如果无主送，有抄送，有抄报
	else if((reportToIds2!=null && reportToIds2 != null) && (copyToIds2 !=null && copyToIds2!="") && (sendToIds2==null || sendToIds2 =="")){
	var tempArray_b2 = reportToIds2.split(",");	
	var tempArray_c2 = copyToIds2.split(",");	
	for(var i=0;i<tempArray_b2.length;i++){
				for(var j=0;j<tempArray_c2.length;j++){
					if(tempArray_b2[i] == tempArray_c2[j]){
						returnValue = false;
						break;
					}
				}
		}	
	}
	
	return returnValue;


}
function selectInsertPeopleOK(){
	//把流程操作后的数据放到隐藏域中
	try { top.endProc(); }catch (e) { }
	monitorFrame.location.href = monitorFrame.location.href;
	//showPrecessArea();
	this.waitMin = function (){
		showPrecessAreaTd("workflow");
	}
	setTimeout(this.waitMin,1);
}
	function showAttention(object){
		var divObj = document.getElementById(object);
		var divLeft = event.x;
		if(divLeft+220>document.body.clientWidth){
			divLeft = event.x - 220;
		}
  		divObj.style.top =  event.y - 50;
  		divObj.style.left = divLeft - 150;
  		divObj.style.height = 25;
  		divObj.style.width = 250;
  		divObj.style.display = "block";
  	}
  	
  	function hideAttention(object){
  		var divObj = document.getElementById(object);
  		divObj.style.display = "none";
  	}
  	
  		function showNextSpecialCondition(conditionObject) {
		    var options = conditionObject.options;
		
		    for (var i = 0; i < options.length; i++) {
		        var d = document.getElementById(options[i].value + "Div");
		        //alert(d);
		        if (d) {
		            d.style.display = "none";
		        }
		  }
	if(document.getElementById(conditionObject.value + "Div") == null) return;
		    document.getElementById(conditionObject.value + "Div").style.display = "block";
	}
	
	
function quoteDocument() {
	if(v3x.getBrowserFlag('openWindow')){
	    var atts = v3x.openWindow({
	        url: genericURL + "?method=showList4QuoteFrame",
	        workSpace: 'yes'
	    });
	
	    if (atts) {
	        for (var i = 0; i < atts.length; i++) {
	            var att = atts[i]
	            //addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description)
	            addAttachment(att.type, att.filename, att.mimeType, att.createDate, att.size, att.fileUrl, true, false, att.description, null, att.mimeType + ".gif", att.reference, att.category)
	        }
	    }
	}else{
		var win = v3x.openDialog({
			id:"pigeonholeIpad",
	    	title:v3x.getMessage("edocLang.contactdoc"),
	    	url : genericURL + "?method=showList4QuoteFrame",
	    	width: 600,
	        height: 600,
	        //isDrag:false,
	        targetWindow:window.parent.detailMainFrame,
	        //fromWindow:window,
	        type:'window',
	        //relativeElement:obj,
	        buttons:[{
				id:'btn1',
	            text: v3x.getMessage("edocLang.ok"),
	            handler: function(){    	        	
	        		var returnValues = win.getReturnValue();
	        		if (returnValues) {
	        	        for (var i = 0; i < returnValues.length; i++) {
	        	            var att = returnValues[i]
	        	            //addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description)
	        	            addAttachment(att.type, att.filename, att.mimeType, att.createDate, att.size, att.fileUrl, true, false, att.description, null, att.mimeType + ".gif", att.reference, att.category)
	        	        }
	        	    }
	        		win.close();
            }
	            
	        }, {
				id:'btn2',
	            text: v3x.getMessage("edocLang.cancel"),
	            handler: function(){
	        		win.close();
	            }
	        }]
		});
	}
}

function quoteDocumentUnSelected(url){
	return deleteAttachment(url, fileUploadAttachments.containsKey(url));
}

function quoteDocumentSelected(obj, subject, documentType, url){
	if(!obj){
		return;
	}
	if(!obj.checked){
		var result = quoteDocumentUnSelected(url);
		if(result == 1){
			obj.checked = true;
		}
		return;
	}
	
	if(!subject || !documentType || !url || fileUploadAttachments.containsKey(url)){
		return;
	}
	
    var type = "2";
    var filename = subject;
    var mimeType = documentType;
    var createDate = "2000-01-01 00:00:00";
    if(obj.getAttribute("createDate")){
		createDate = obj.getAttribute("createDate");
	}
    var fileUrl = url;
    var description = url;

    //Attachment(id, reference, subReference, category, type, filename, mimeType, createDate, size, fileUrl, description, needClone,extension,icon)
    //atts[atts.length] = new Attachment('', '', '', '', type, filename, mimeType, createDate, '0', fileUrl, description);
    //function addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description,extension,icon)
    addAttachment(type, filename, mimeType, createDate, '0', fileUrl, true, null, description, documentType, documentType + ".gif");
}

function quoteDocumentOK() {
    var atts = fileUploadAttachments.values().toArray();

    if (!atts || atts.length < 1) {
        alert(getA8Top().v3x.getMessage('collaborationLang.collaboration_alertQuoteItem'));
        return;
    }

    parent.window.returnValue = atts;
    parent.window.close();
}

function officeOcxOperateOver(opType)
{
	if(typeof(openFrom)=="undefined"){return;}
	if(openFrom!="lenPotent"){return;}
	var LogType="";
	if(opType=="print")
	{
		LogType="print";
	}
	else if(opType=="saveLocal")
	{	
		LogType="save";
	}
	
	try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocManager", "writeDocOperateLog", false);
    	requestCaller.addParameter(1, "Long", docId);
    	requestCaller.addParameter(2, "Long", summaryId);
    	requestCaller.addParameter(3, "String", docSubject);
    	requestCaller.addParameter(4, "String", LogType);
    	var ds = requestCaller.serviceRequest();    	
    }catch(e){
    }
}

/**
 * 和协同的保持一致的打开督的页面
 */
function openSuperviseWindow(isFromTemplate){
	
		var mId = document.getElementById("supervisorId");
		var sDate = document.getElementById("awakeDate");
		var sNames = document.getElementById("supervisors");
		var title = document.getElementById("superviseTitle");
		var role = document.getElementById("superviseRole");
		//var canModify = document.getElementById("canModifyAwake");
		var urlStr = colSuperviseURL + "?method=superviseWindow";
		if(mId.value != null && mId.value != ""){
			urlStr += "&supervisorId=" + mId.value + "&supervisors=" + encodeURIComponent(sNames.value) 
			+ "&superviseTitle=" + encodeURIComponent(title.value) + "&awakeDate=" + sDate.value + "&role=" + role.value;
		}
	
        var rv = v3x.openWindow({
	        url: urlStr,
	        height: 300,
	        width: 450
     	});

    	if(rv!=null && rv!="undefined"){
    		var sv = rv.split("|");
    		if(sv.length == 5){
				mId.value = sv[0]; //督办人的ID(添加标识的，为的是向后台传送)
				sDate.value = sv[1]; //督办时间
				sNames.value = sv[2]; //督办人的姓名
				title.value = sv[3];
				role.value = sv[4];
				//canModify.value = sv[4];
			}
    	}
}

/**
 * 打开督办页面（供普通使用）
 */
function openSuperviseWindow(){
	
		var mId = document.getElementById("supervisorId");
		var sDate = document.getElementById("awakeDate");
		var sNames = document.getElementById("supervisors");
		var title = document.getElementById("superviseTitle");
		var summaryId = document.getElementById("id");
		var fromSend = document.getElementById("fromSend");
		if(summaryId){
			summaryId = summaryId.value;
		}
		var urlStr = colSuperviseURL;
		//var canModify = document.getElementById("canModifyAwake");
		if(fromSend.value == "true"){
			    urlStr += "?method=superviseWindowForEdocZCDB";
		}else{
				urlStr += "?method=superviseWindow";
		}
		var unCancelledVisor = document.getElementById("unCancelledVisor");
		var sfTemp = document.getElementById("sVisorsFromTemplate");
		if(mId.value != null && mId.value != ""){
			urlStr += "&supervisorId=" + mId.value + "&supervisors=" + encodeURIComponent(sNames.value) 
			+ "&superviseTitle=" + encodeURIComponent(title.value) + "&awakeDate=" + sDate.value  + "&sVisorsFromTemplate="+sfTemp.value +"&unCancelledVisor="+unCancelledVisor.value;
		}
		urlStr +=  "&summaryId="+summaryId + "&isFromEdoc=yes&currentPage="+currentPage + "&secretLevel="+flowSecretLevel_wf;
        var rv = v3x.openWindow({
	        url: urlStr,
	        height: 300,
	        width: 400
     	});

    	if(rv!=null && rv!="undefined"){
    		var sv = rv.split("|");
    		if(sv.length == 4){
				mId.value = sv[0]; //督办人的ID(添加标识的，为的是向后台传送)
				sDate.value = sv[1]; //督办时间
				sNames.value = sv[2]; //督办人的姓名
				title.value = sv[3];
				//canModify.value = sv[4];
			}
    	}
}

/**
 * 打开督办页面（供模板使用）
 */

function openSuperviseWindowForTemplate(){
	
		var mId = document.getElementById("supervisorId");
		var sDate = document.getElementById("awakeDate");
		var sNames = document.getElementById("supervisors");
		var title = document.getElementById("superviseTitle");
		var role = document.getElementById("superviseRole");
		//var canModify = document.getElementById("canModifyAwake");
		var urlStr = colSuperviseURL + "?method=superviseWindowForTemplate";
		if((mId.value != null && mId.value != "") || (role.value!=null && role.value!= "")){
			urlStr += "&supervisorId=" + mId.value + "&supervisors=" + encodeURIComponent(sNames.value) 
			+ "&superviseTitle=" + encodeURIComponent(title.value) + "&awakeDate=" + sDate.value + "&role=" + role.value;
		}
		urlStr += "&isFromEdoc=yes&secretLevel="+flowSecretLevel_wf;			
        var rv = v3x.openWindow({
	        url: urlStr,
	        height: 350,
	        width: 450
     	});

    	if(rv!=null && rv!="undefined"){
    		var sv = rv.split("|");
    		if(sv.length == 6){
				mId.value = sv[0]; //督办人的ID(添加标识的，为的是向后台传送)
				sDate.value = sv[1]; //督办时间
				sNames.value = sv[2]; //督办人的姓名
				title.value = sv[3];
				role.value = sv[4];
				//canModify.value = sv[4];
			}
    	}
}

function showSuperviseWindow(secretLevel){
	try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocSuperviseManager", "ajaxCheckIsSummaryOver", false);
    	requestCaller.addParameter(1, "Long", summary_id);
    	var ds = requestCaller.serviceRequest();
    	if(ds =="true"){
    		alert(v3x.getMessage('edocLang.edoc_supervise_workflow_over'));
    		return;
	    }
    }catch(e){}
	
	var rv = v3x.openWindow({
		url: colSuperviseURL + "?method=edocSuperviseWindowEntry&summaryId=" + summary_id + "&secretLevel="+secretLevel,
    	height: 300,
       	width: 400
    });
}

//分枝代码开始
function setCondition(isForm,args,linkId){
    var link = genericControllerURL + "collaboration/templete/compute&isForm=" + isForm;
    if(args!=undefined){
    	link += "&isNew=0&id="+args.id+"&linkId="+linkId+ "&isForce=" + args.isForce;
    }else{
    	link += "&isNew=1";
    }
    var condition = v3x.openWindow({
			url : link,
			width : 440,
			height : 520,
			scrollbars:"no",
			dialogType: "modal",
            resizable: true
	});
	return condition;
}

function selectCondition(){
	var orgId = document.getElementById("orgId").value;
	var orgName = document.getElementById("orgName").value;
	if(orgId==""||orgName==""){
		alert(v3x.getMessage("collaborationLang.branch_selectorg"));
		return;
	}
	var operationSel = document.getElementById("operation");
	var operation = operationSel.options[operationSel.selectedIndex];
	var arr = new Array();
	arr[0] = operation.value;
	arr[1] = operation.value;
	arr[2] = orgId;
	arr[3] = "["+orgName+"]";
	window.returnValue = arr;
	window.close();
}

function ColBranch(){
  this.id=null;
  this.conditionType=null;
  this.formCondition=null;
  this.conditionTitle=null;
  this.conditionDesc=null;
  this.isForce=null;
  this.conditionBase=null;
}

function initCondition(){
	var scripts = document.getElementsByName("scripts");
	if(scripts&&scripts.length>0){
		var prefix = "";
		var parentURL = null;
		/*var isFormFlag = false;
		try{
			isFormFlag = isForm=="true" || isForm==true;
		}catch(e){}
	   	if(isFormFlag){
	   		if(rootNodeName)
		   		prefix = rootNodeName;
		   	else {
		   		parentURL = "parent.detailMainFrame.contentIframe.";
		   		prefix = parent.detailMainFrame.contentIframe.rootNodeName;
		   	}
		   	prefix = prefix.substring(0,prefix.indexOf(":")+1);
	   	}*/
		if(typeof(isNewColl)=="undefined"){
			parentURL = "contentIframe.";
        }
	   	
	   	var reg = new RegExp("[\{][^\{\}]*[\}]","g"); 
	   	var list = new ArrayList();
	   	
	    for(var i=0;i<scripts.length;i++){
	      	var script = scripts[i].value;
	       	var arr = script.match(reg);
	       	if(arr){
		       	for(var j=0;j<arr.length;j++){
		       		script = script.replace(arr[j],(parentURL==null?"":parentURL)+"getFieldValueForFlow(\""+prefix+arr[j].substring(1,arr[j].length-1)+"\")");	
		       	}						        	
	       	}
	       	arr = script.match(/[\[][^\[\]]*[\]]/gi);
	       	if(arr){
	       		for(var j=0;j<arr.length;j++)
	       			if(arr[j].indexOf(":")!=-1)
		       			script = script.replace(arr[j],"\""+arr[j].substring(arr[j].indexOf(":")+1,arr[j].length-1)+"\"");
	       	}
	       	arr = script.match(/include\([^\']*/gi);
	       	if(arr){
	       		//格式：include(team,系统组:'3434328934822');include(secondPost,'4342342345453_-54534534534')
	       		for(var j=0;j<arr.length;j++){
	       			var data = arr[j].substring(arr[j].indexOf(":")+1);
	       			if(data.indexOf("_")!=-1)
	       				data = data.replace(/_/,",");
	       			var con = arr[j].substring(0,arr[j].indexOf(",")+1)+data;
	       			script = script.replace(arr[j],con);
	       		}
	       	}
	       	arr = script.match(/exclude\([^\']*/gi);
	       	if(arr){
	       		//格式：exclude(team,系统组:3434328934822)
	       		for(var j=0;j<arr.length;j++){
	       			var data = arr[j].substring(arr[j].indexOf(":")+1);
	       			if(data.indexOf("_")!=-1)
	       				data = data.replace(/_/,",");
	       			var con = arr[j].substring(0,arr[j].indexOf(",")+1)+data;
	       			script = script.replace(arr[j],con);
	       		}
	       	}
	       	script = script.replace(/<>/g,"!=");
	       	eval(script);
	    }
	}
}


function initCondition_edoc(isNewColl){
	var scripts = document.getElementsByName("scripts");
	if(scripts&&scripts.length>0){
		var prefix = "";
		var parentURL = null;
		/*var isFormFlag = false;
		try{
			isFormFlag = isForm=="true" || isForm==true;
		}catch(e){}
	   	if(isFormFlag){
	   		if(rootNodeName)
		   		prefix = rootNodeName;
		   	else {
		   		parentURL = "parent.detailMainFrame.contentIframe.";
		   		prefix = parent.detailMainFrame.contentIframe.rootNodeName;
		   	}
		   	prefix = prefix.substring(0,prefix.indexOf(":")+1);
	   	}*/
		if(typeof(isNewColl)=="undefined"){
			parentURL = "_parent.contentIframe.";
        }else{
        	parentURL = "_parent.";
        }
	   	var reg = new RegExp("[\{][^\{\}]*[\}]","g"); 
	   	var list = new ArrayList();
	   	//定义存储计算之前的js字符串
	   	var beforeScripts = new Array();
	   	//定义从表单页面获的相应值<value,表达式>
	   	var formDataTrace= new Properties();
	   	//定义存储计算之后的js字符串
	   	var afterStripts = new Array();
	    for(var i=0;i<scripts.length;i++){
	      	var script = scripts[i].value;
	      	if(isDebug){
	      		//记录下运算之前的js运算字符串
	      		beforeScripts[i] = script;
	      	}
	       	var arr = script.match(reg);
	       	if(arr){
		       	for(var j=0;j<arr.length;j++){
		       		var tempFieldName= prefix+arr[j].substring(1,arr[j].length-1);
		       		script = script.replace(arr[j],(parentURL==null?"":parentURL)+"getFieldValueForFlow(\""+tempFieldName+"\")");
		       		formValue = eval((parentURL==null?"":parentURL)+"getFieldValueForFlow(\""+tempFieldName+"\")");
		       		formDataTrace.put(""+formValue,"pageData:[\""+tempFieldName+"\"]");
		       	}						        	
	       	}
	       	arr = script.match(/[\[][^\[\]]*[\]]/gi);
	       	if(arr){
	       		for(var j=0;j<arr.length;j++)
	       			if(arr[j].indexOf(":")!=-1)
		       			script = script.replace(arr[j],"\""+arr[j].substring(arr[j].indexOf(":")+1,arr[j].length-1)+"\"");
	       	}
	       	arr = script.match(/include\([^\']*/gi);
	       	if(arr){
	       		//格式：include(team,系统组:'3434328934822');include(secondPost,'4342342345453_-54534534534')
	       		for(var j=0;j<arr.length;j++){
	       			var data = arr[j].substring(arr[j].indexOf(":")+1);
	       			if(data.indexOf("_")!=-1)
	       				data = data.replace(/_/,",");
	       			var con = arr[j].substring(0,arr[j].indexOf(",")+1)+data;
	       			script = script.replace(arr[j],con);
	       		}
	       	}
	       	arr = script.match(/exclude\([^\']*/gi);
	       	if(arr){
	       		//格式：exclude(team,系统组:3434328934822)
	       		for(var j=0;j<arr.length;j++){
	       			var data = arr[j].substring(arr[j].indexOf(":")+1);
	       			if(data.indexOf("_")!=-1)
	       				data = data.replace(/_/,",");
	       			var con = arr[j].substring(0,arr[j].indexOf(",")+1)+data;
	       			script = script.replace(arr[j],con);
	       		}
	       	}
	       	script = script.replace(/<>/g,"!=");
	       	if(isDebug){
		    	afterStripts[i] = script;
		    }
	       	eval(script);
	    }
	    if(isDebug){
	    	//跟踪代码开始
	    	try{
	    		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "createBranchLogData", false);
	    		requestCaller.addParameter(1,"String[]",beforeScripts);
		    	requestCaller.addParameter(2,"String[]",afterStripts);
		    	var teamstr="";
		    	for(var i in team){
		    		teamstr +="["+i+"],";
		    	}
		    	var secondpoststr="";
		    	for(var i in secondpost){
		    		secondpoststr +="["+i+"],";
		    	}
		    	var startTeamstr="";
		    	for(var i in startTeam){
		    		startTeamstr +="["+i+"],";
		    	}
		    	var startSecondpoststr="";
		    	for(var i in startSecondpost){
		    		startSecondpoststr +="["+i+"],";
		    	}
		    	requestCaller.addParameter(3,"String",teamstr);
		    	requestCaller.addParameter(4,"String",secondpoststr);
		    	requestCaller.addParameter(5,"String",startTeamstr);
		    	requestCaller.addParameter(6,"String",startSecondpoststr);
		    	requestCaller.addParameter(7,"String",formDataTrace.toString());
		    	requestCaller.addParameter(8,"String","edoc");
		    	requestCaller.serviceRequest();
	    	}catch(e){
	    		alert(e);
	    	}
    		//跟踪代码结束
	    }
	}
}

function moreCondition(moreInfo,readonly){
	var readOnly = readonly?readonly:false;
	var link = genericControllerURL + "collaboration/templete/moreCondition&moreInfo="+encodeURIComponent(moreInfo)+"&readonly="+readOnly;
    
	//var link = genericControllerURL + "collaboration/templete/moreCondition&moreInfo="+encodeURIComponent(moreInfo);
    var moreCondition = v3x.openWindow({
			url : link,
			width : 230,
			height : 195,
			scrollbars:"no"
	});
	return moreCondition;
}

function hiddenFailedCondition(nodeId){	
	var showDiv = document.getElementById("d"+nodeId);
	var hiddenDiv = document.getElementById('failedCondition');
	if(showDiv && hiddenDiv){
		hiddenDiv.innerHTML += showDiv.outerHTML;
		showDiv.outerHTML = "";
	}
	hiddenDiv = document.getElementById('failedConditionSelector');
	if(hiddenDiv){
		showDiv = document.getElementById("selector"+nodeId);
		if(showDiv){
			hiddenDiv.innerHTML += showDiv.outerHTML;
			showDiv.outerHTML = "";
		}
	}
	var showDiv = document.getElementById("aDiv");
	if(showDiv && showDiv.style.display=="none")
		showDiv.style.display = "";
}

function showFailedCondition(labelObj){
	//获得不符合条件的分支Div标签
	var failedConditionDiv = document.getElementById("failedCondition");
	//这个DIV好像没什么作用？
	var failedSelectorDiv = document.getElementById("failedConditionSelector");
	//显示不满足条件链接信息
	var aDiv=document.getElementById("aDiv");
	var lgdObj = document.getElementById("lgd");
	if(failedConditionDiv && failedConditionDiv.style.display=="none"){
		failedConditionDiv.style.display = "";
		if(failedSelectorDiv){
			failedSelectorDiv.style.display = "";
		}
		if(lgdObj){
			lgdObj.style.display= "none";
		}
		aDiv.innerHTML = hideLabel;
	}else{
		if(failedConditionDiv){
			failedConditionDiv.style.display = "none";
		}	
		if(failedSelectorDiv){
			failedSelectorDiv.style.display = "none";
		}
		if(lgdObj){
			lgdObj.style.display= "block";
		}
		aDiv.innerHTML = showLabel;
	}
}
function getFieldValueForFlow(fieldName)
{
	if(self.location.href.search("method=showDiagram")!=-1)
	{
		return parent.detailMainFrame.contentIframe.getFieldValueForFlow(fieldName);
	}
	var objValue="";
	var inputObj=document.getElementById("my:"+fieldName);
	if(inputObj!=null)
	{		
		objValue=_getObjValue(inputObj);		
	}	
	return objValue;	
}

function _getObjValue(inputObj)
{	
	var inputValue="";
	if(inputObj.tagName=="INPUT" && inputObj.type=="text")
	{
		if(inputObj.getAttribute("realValue"))
			inputValue = inputObj.getAttribute("realValue");
		else
			inputValue=inputObj.value;		
	}
	else if(inputObj.tagName=="SELECT")	
	{
		inputValue=inputObj.options[inputObj.selectedIndex].value;		
	}	
	return inputValue;
}

function include(arr,id){
	if(!arr)
		return false;
	for(var i in arr){
		if(i == id)
			return true;
	}
	return false;
}

function exclude(arr,id){
	return !include(arr,id);
}

/**
 *发起/处理时显示分支描述
*/
function showBranchDesc(linkId,templateId){
	if(!templateId){
		var parentWin = window.dialogArguments;
		var desc = "";
		if(parentWin && parentWin.branchs){
			var branch = parentWin.branchs[linkId];
			if(branch)
				desc = branch.conditionDesc;
		}
		moreCondition(desc,true);
	}else{
		//待发时不能从页面取描述信息，只能到数据库中取
		var rv = v3x.openWindow({
	        url: templeteURL + "?method=showBranchDesc&readonly=true&linkId=" + linkId + "&templateId=" + templateId,
	        width : 230,
			height : 195,
			scrollbars:"no"
    	});
	}
}
//分枝代码结束

/**
 * 预发布公告,新闻
 * return 0--失败,1--成功
 */
function TransmitBulletin(policyName, summaryId){
    var bodyType = document.getElementById("bodyType").value;
	if(ocxContentIsModify()==true)
	{//正文为修改状态，是否进行保存
		if(window.confirm(_("edocLang.content_modify_info")))
		{
			if(!saveContent()){return;}
		}
		
	}
	//Ajax判断是否有发布新闻、公告的权限
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocController", "AjaxjudgeHasPermitIssueNewsOrBull", false);
	requestCaller.addParameter(1, "String", policyName);
	var rs = requestCaller.serviceRequest();
	if(rs == "false"){
		alert(_("edocLang.edoc_alertNoPermitBull"));//您沒有發布權限!
		return ;
	}
	var d=new Date();

	var rv = v3x.openWindow({
		url: genericURL + "?method=preIssueNewsOrBull&bodyType=" + bodyType + "&policyName=" + policyName+"&t="+d.getTime()+"&secretLevel="+flowSecretLevel_wf,
        height: 320,
        width: 270
    });
    if(!rv || rv.length == 0) return;
    
    var data = {
        typeId : [],
        summaryId : [],
        memberIdsStr : [],
        allowPrint : [],
        ext5 : []
    };
    
    data.typeId = rv[0];
    data.memberIdsStr = rv[1];
    data.summaryId = summaryId;
    data.allowPrint = rv[2];

	var actionUrl = genericURL+"?method=issusBulletion";
	if(policyName == "newsaudit"){
		actionUrl = collaborationCanstant.issusNewsActionURL;
	}
	if(rv[3] == "1"){
		var fileId = getUUID();
		data.ext5 = fileId;
		transformWordToPdf(fileId);
	}
    submitMap(data, actionUrl, "showDiagramFrame", "post");
    return;
}
function SendBulltinResult(errMsg)
{
	if(errMsg=="")
	{
		alert(_("edocLang.TransmitBulletin_Success"));
	}
	else
	{
		alert(errMsg);
	}
}
//检查正文是否被修改
function ocxContentIsModify()
{
	var bodyType = document.getElementById("bodyType").value;
  	if(bodyType=="HTML" || bodyType == 'Pdf')
  	{
  		return contentUpdate;  		
  	}
  	else
  	{
  		return contentIsModify();
  	}
}
//多级会签
function addMoreSign(_summary_id, _processId, _affairId, secretLevel) {		
	if(!checkModifyingProcessAndLock(processId, summary_id)){
		return;
	}
	flowSecretLevel_addMoreSign = secretLevel;
    selectPeopleFun_addMoreSign();
}
//多级会签div实现
var addMoreSignResult_win;
function addMoreSignResult(elements) {
	if(v3x.getBrowserFlag('pageBreak')){
		var rv = v3x.openWindow({
	        url: genericURL + "?method=preAddMoreSign&selObj="+getIdsString(elements)+"&appName="+appTypeName+"&summary_id="+summaryId,
	        height: 350,
	        width: 500
	    });
	}else{
		if(addMoreSign_win){addMoreSign_win.close();}
		addMoreSignResult_win = v3x.openDialog({
			id:"addMoreSignResult",
			title:v3x.getMessage("edocLang.morepeople"),
	        url: genericURL + "?method=preAddMoreSign&selObj="+getIdsString(elements)+"&appName="+appTypeName+"&summary_id="+summaryId,
	        height: 350,
	        width: 500
	    });
	}
}

function disabledPrecessButtonEdoc(state){
	state = state == null ? true : state;
    try{ document.getElementById("processButton").disabled = state; } catch(e) { }
    try{ document.getElementById("zcdbButton").disabled = state; } catch(e) { }
    //try{ document.getElementById("savedraftButton").disabled = state; } catch(e) { }
}
function unescapeHTMLToString(str){
	if(!str){
		return "";
	}
	
	str = str.replace("&amp;","&");
	str = str.replace("&lt;","<");
	str = str.replace("&gt;",">");
	str = str.replace("<br>","");
	str = str.replace("&#039;","\'");
	str = str.replace("&#034;","\"");
	
	return str;
}
function showProcessLog(processId){
	var url = processLogURL+"?method=processLogIframe&processId="+processId;
	var rv = getA8Top().v3x.openWindow({
	    url: url,
	    dialogType : v3x.getBrowserFlag('openWindow') == true ? "modal" : "1",
	    width: "800",
	    height: "600"
	});
}

function checkMarkHistoryExist(edocMarkHistory,edocId){
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocMarkHistoryManager", "isUsed", false);
		requestCaller.addParameter(1, "String", edocMarkHistory);
		requestCaller.addParameter(2, "Long", edocId);
		var rs = requestCaller.serviceRequest();
		if(rs == "true"){
			alert(_("edocLang.doc_mark_alter_used"));
			return true;
		}
	}
	catch (ex1) {
		alert("Exception : " + ex1);
		return true;
	}	
	return false;
}
//跟踪设置窗口。
/*function preChangeTrack(affairId, isTrack){
	alert("dsds");
	var trackValue = '0';
	if(isTrack){
		trackValue = '1';
	}
    var rv = v3x.openWindow({
        url: genericControllerURL + "collaboration/preChangeTrack&affairId="+affairId+"&trackValue="+trackValue,
        width: "250",
        height: "150"
    });
}*/
function preChangeTrack(affairId, isTrack){
	var trackValue = '0';
	if(isTrack){
		trackValue = '1';
	}
    var rv = v3x.openWindow({
      //  url: genericControllerURL + "collaboration/preChangeTrack&affairId="+affairId+"&trackValue="+trackValue,
        url : colWorkFlowURL+"?method=preChangeTrack&affairId="+affairId+"&trackValue="+trackValue,
    	width: "250",
        height: "150"
    });
    
}
//检查某人是否是某公文的督办人。。
//0 :被取消了督办权限 。1：仍然是督办人。
function isStillSupervisor(summaryId){
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocSuperviseManager", "isSupervisorOfOneSummary", false);
		requestCaller.addParameter(1, "String", summaryId);
		var rs = requestCaller.serviceRequest();
		if(rs == "0"){
			alert(_("edocLang.edoc_alert_not_supervise"));
			return false;
		}
	}
	catch (ex1) {
		alert("Exception : " + ex1);
		return false;
	}	
	return true;
}
/**
 * 检查正文是否存在并发修改。
 */
function checkConcurrentModifyForHtmlContent(contentId){
	  var requestCaller = new XMLHttpRequestCaller(this, "ajaxHandWriteManager", "editObjectState",false);
	  requestCaller.addParameter(1, "String", contentId);  
	  var ds = requestCaller.serviceRequest();
	  if(ds.get("curEditState")=="true")
	  {
	  	//:(getOfficeLanguage("用户")+ds.get("userName")+getOfficeLanguage("正在编辑此文件，不能修改！"));    
	  	alert(v3x.getMessage("V3XOfficeLang.alert_NotEdit",ds.get("userName")));    
	    return true;
	  }
	  return false;
}
/**
 * 解锁HTML正文
 */
function unlockHtmlContent(summaryId){
      var requestCaller = new XMLHttpRequestCaller(this, "ajaxHandWriteManager", "deleteUpdateObj",false);
	  requestCaller.addParameter(1, "String", summaryId);  
	  var ds = requestCaller.serviceRequest();
}

function checkEdocMark(){
	var edocMarkObj = document.getElementById("my:doc_mark");
    if(edocMarkObj){
        if(!isEdocMarkWellFormated(edocMarkObj.value)) return false;
	    var edocMark = _getWordNoValue(edocMarkObj);
	    if(edocMark && edocMark.length>66){
	    	alert(v3x.getMessage("edocLang.mark_alter_exceed"));
	    	return false;
	    }
	    	
    }
    edocMarkObj = document.getElementById("my:serial_no");
    if(edocMarkObj && edocMarkObj.value){
        if(!isEdocMarkWellFormated(edocMarkObj.value)) return false;
    	var innerMark = "";
    	if(edocMarkObj.value.indexOf("|")==-1){
    		innerMark = edocMarkObj.value;
    	}else{
    		var arr = edocMarkObj.value.split("|");
    		if(arr && arr.length>1)
    			innerMark = arr[1];
    	}
    	if(innerMark.length>66){
    		alert(v3x.getMessage("edocLang.innermark_alter_exceed"));
    		return false;
    	}
    }
    return true;
}
function updateAtt(summaryId,processId){
	if(!checkModifyingProcess(processId, summaryId)){
		return;
	}
	//var detailFrame =  parent.parent.detailMainFrame;
	var attList = getAttachment(summaryId,summaryId);
	
	var result = editAttachments(attList,summaryId,summaryId,'4');
	if(result){
		updateAttachmentMemory(result,summaryId,summaryId,'')
		showAttachment(summaryId, 2, 'attachment2TrContent', 'attachment2NumberDivContent','attachmentHtml2Span');
		showAttachment(summaryId, 0, 'attachmentTrContent', 'attachmentNumberDivContent','attachmentHtml1Span');
	}
}
/**
 * 对归档Select对象的不同选择做出不同的操作
 * @param obj : 当前下拉选择框对象
 * @param appName : 应用名
 * @param from : 来源（模板、处理页面）
 * @param formObj : form对象
 */
function pigeonholeEvent(obj,appName,from,formObj){
	switch(obj.selectedIndex){
		case 0 :
			var oldArchiveId = formObj.archiveId.value;
			if(oldArchiveId != ""){
				formObj.archiveId.value = "";
			}
			break;
		case 1 : 
			doPigeonhole('new', appName, from,formObj);
			break;
			
		default :
			formObj.archiveId.value = document.getElementById("prevArchiveId").value;
			return;
	}
}

/**
 * 弹出归档选择文件路径界面
 * @param obj : 当前下拉选择框对象
 * @param appName : 应用名
 * @param from : 来源（模板、处理页面）
 * @param formObj : form对象
 */
function doPigeonhole(flag,appName,from,formObj) { 
    if(!formObj)  //已办公文页面单位归档
       formObj = document.getElementById("listForm");
    
    if (flag == "no") {
        //TODO 清空信息
    }
    else if (flag == "new") {
        var result;
    	if(from == "templete"){//公文模板预归档
        	result = pigeonhole(appName, null, false, false,'EdocTempletePrePigeonhole');
    	}else{
    		result = pigeonhole(appName,null,false,true,'EdocAccountPigoenhole');
    	}
    	//var theForm = document.getElementsByName("sendForm")[0];
        if(result == "cancel"){
        	var oldPigeonholeId = formObj.archiveId.value;
    		var selectObj = formObj.selectPigeonholePath;
    		if(selectObj){ //存在下拉选择框的情况
            	if(oldPigeonholeId != "" 
            		&& typeof(selectObj.options)!='undefined'
            		&& selectObj.options.length >= 3){
    				selectObj.options[2].selected = true;
            	}
            	else{
            		var oldOption = document.getElementById("defaultOption");
            		oldOption.selected = true;
            	}
    		}
        	return;
        }
        var pigeonholeData = result.split(",");
        pigeonholeId = pigeonholeData[0];
        pigeonholeName = pigeonholeData[1];
        if(pigeonholeId == "" || pigeonholeId == "failure"){
        	formObj.archiveName.value = "";
        	alert(v3x.getMessage("collaborationLang.collaboration_alertPigeonholeItemFailure"));
        }
        else{
        	var oldPigeonholeId = formObj.archiveId.value;
        	formObj.archiveId.value = pigeonholeId;
        	if(document.getElementById("prevArchiveId")){
        		document.getElementById("prevArchiveId").value = pigeonholeId;
        	}
        	var selectObj = document.getElementById("selectPigeonholePath");
        	if(selectObj){
            	var option = document.createElement("OPTION");
            	option.id = pigeonholeId;
            	option.text = pigeonholeName;
            	option.value = pigeonholeId;
            	option.selected = true;
            	if(oldPigeonholeId == "" && selectObj.options.length<=2){
            		selectObj.options.add(option, selectObj.options.length);
            	}
            	else{
            		selectObj.options[selectObj.options.length-1] = option;
            	}
        	}
        }
    }
}
/**
 * 根据文档的逻辑路径取文档的真实路径
 * @param logicalPath :逻辑路径
 */
function showWholePath(logicalPath,callObj){
        if(logicalPath == '' )return;
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocController", "getPhysicalPath", false);
		requestCaller.addParameter(1,"String", logicalPath);
		requestCaller.addParameter(2,'String',"\\");
		requestCaller.addParameter(3,'boolean',false);
		requestCaller.addParameter(4,'int',0);
		var rs = requestCaller.serviceRequest();
		callObj.title=rs;
}

//批处理
function batchEdoc(){
	var checkBoxs = document.getElementsByName("id");
	if(!checkBoxs){
		alert(_("edocLang.batch_select_affair"));
		return ;
	}
	var process = new BatchProcess();
	for(var i = 0 ; i < checkBoxs.length;i++){
		if(checkBoxs[i].checked){
			var affairId = checkBoxs[i].getAttribute("affairId");
			var subject = checkBoxs[i].getAttribute("subject");
			var app = "4";
			process.addData(affairId,checkBoxs[i].value,app,subject);
		}
	}
	if(!process.isEmpty()){
		var r = process.doBatch("getA8Top().contentFrame.topFrame.refreshWorkspace()");
	}else{
		alert(_("edocLang.batch_select_affair"));
		return ;
	}
}
function opinionInputWindow(tempOpinion,canUploadRel,relAttButton,canUploadAttachment,attButton,attContfileUpload,attitude,commonPhrase){
	var window = "<table id='opinionInputArea' width='100%'  border='0' cellspacing='0' cellpadding='0' class='sign-area'>";
		
		window += "<tr>";
		window += "<td align='left'>"+attitude+"</td>";//态度 
		window += "<td align='right'>"+commonPhrase+"</td>";
		window +="</tr>";	
		
		window += "<tr><td colspan='2'>"
		window += "<textarea id='content' name='content' rows='5' style='width: 100%' validate='maxLength' maxSize='2000'>"+tempOpinion+"</textarea>";
		window += "</td></tr>";
		//推送消息
		window += "<tr><td colspan='2' align='right'>"
		window += pushMessage;
		window += "</td></tr>";
		
		if(canUploadRel || canUploadAttachment){
			window += "<tr><td colspan='2' style='padding: 5px 10px;'>";
			window += "<div height='36'>";
			if(canUploadRel){
				window += relAttButton +"&nbsp;&nbsp;";
			}
			if(canUploadAttachment){	
				window += attButton;
			}	
			window += "</div>"
			window +="</td></tr>"
				
			window += "<tr><td colspan='2'>"	;
			window += ' <div id="attachment2Area" style="overflow: auto;"></div>';
			window += '	<div id="attachmentInputs"></div>'
			window += attContfileUpload;
			window += '</td></tr></table>'
		}
		
		return window;
}
function showSupervise(summaryId,openModal){
	getA8Top().v3x.openWindow({
        url: genericURL + "?method=superviseDiagram&summaryId=" + summaryId + "&openModal=" + openModal,
        dialogType : v3x.getBrowserFlag('openWindow') == true ? "modal" : "1",
        width: "350",
        height: "450"
    });
}
/**
 * 查看属性
 */
function showAttribute(affairId, from){
	getA8Top().v3x.openWindow({
        url: colWorkFlowURL + "?method=showAttribute&affairId=" + affairId + "&from=" + from,
        dialogType : v3x.getBrowserFlag('openWindow') == true ? "modal" : "1",
        width: "350",
        height: "400"
    });
}
function queryMarkList(app, state) {
	var condition = document.getElementById("condition").value;
	var edocMarkValue = document.getElementById("edocMarkValue").value
	var edocInMarkValue = document.getElementById("edocInMarkValue").value
	
	if (edocMarkValue != '[]' && edocMarkValue != '' || edocInMarkValue != '[]' && edocInMarkValue != '') {
		auto(eval(edocMarkValue),eval(edocInMarkValue));
	} else {
		if (condition == 'docMark' || condition == 'docInMark') {
			try {
				var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocManager", "queryMarkList", false);
				requestCaller.addParameter(1, "int", app);
				requestCaller.addParameter(2, "int", state);
				var rs = requestCaller.serviceRequest();
				if (rs != null) {
					var mark = rs.split("::");
					edocMarkValue = eval(mark[0]);
					edocInMarkValue = eval(mark[1]);
					
					document.getElementById("edocMarkValue").value=mark[0];
					document.getElementById("edocInMarkValue").value=mark[1];
					
					auto(edocMarkValue,edocInMarkValue);
				}
			}
			catch (ex1) {
				alert("Exception : " + ex1);
				return;
			}
		}
	}
}

function auto(mark1,mark2) {
	v3xautocomplete.autocomplete('docMark',mark1);
	v3xautocomplete.autocomplete('docInMark',mark2);
}

/** 
 * 公文文号下拉提示 ： 包括 拟文、新建模版、修改文单、签报等对文号或者文单的修改
 * 对于需要进行下拉提示的公文文号只需要在页面加载完成后调用此方法
 */
function showEdocMark() {
	var id="my:doc_mark";
	
	var docMark = document.getElementById("my:doc_mark");
	if(docMark!=null) {
		var markHtml=docMark.outerHTML;
		var styleValue=markHtml.substring(markHtml.indexOf("\"")+1);
		var style=styleValue.substring(-1,styleValue.indexOf("\""));
		// 公文文号
		var str = "<input id='"+id+"' name='"+id+"' style='"+style+"' type='hidden' value='"+docMark.value+"'/>";
			str += "<input id='"+id+"_autocomplete' name='"+id+"_autocomplete' style='"+style+"' type='text' onclick=\"v3xautocomplete.toggle('"+id+"');\" value='请选择内部文号' />";
		commonMarkChange(docMark,str);
	}
	
	id="my:doc_mark2";
	var docMark2 = document.getElementById("my:doc_mark2");
	if(docMark2!=null) {
		// 公文文号
		var markHtml=docMark2.outerHTML;
		var styleValue=markHtml.substring(markHtml.indexOf("\"")+1);
		var style=styleValue.substring(-1,styleValue.indexOf("\""));
		var str = "<input id='"+id+"' name='"+id+"' style='"+style+"' type='hidden' value='"+docMark2.value+"'/>";
			str += "<input id='"+id+"_autocomplete' name='"+id+"_autocomplete' style='"+style+"' type='text' onclick=\"v3xautocomplete.toggle('"+id+"');\" value='请选择内部文号'/>";
		commonMarkChange(docMark2,str);
	}
	
	id="my:serial_no";
	var docInMark = document.getElementById("my:serial_no");
	
	if(docInMark!=null) {
		var markHtml=docInMark.outerHTML;
		var styleValue=markHtml.substring(markHtml.indexOf("\"")+1);
		var style=styleValue.substring(-1,styleValue.indexOf("\""));
		// 内部文号
		var str2 = "<input id='"+id+"' name='"+id+"' style='"+style+"' type='hidden' value='"+docInMark.value+"' />";
			str2 += "<input id='"+id+"_autocomplete' name='"+id+"_autocomplete' style='"+style+"' type='text' onclick=\"v3xautocomplete.toggle('"+id+"');\" value='请选择内部文号'/>";
			commonMarkChange(docInMark,str2);
	}
	isEdocLike = true ;
}
var docMarkOriginalArr = new Array();
var serialNoOriginalArr = new Array();
var docMark2OriginalArr = new Array();
/**
 * 替换拟文、修改文号、修改文单等页面文号的展现风格，支持下拉提示输入
 * markObj	：文号对象，包括公文文号和内部文号
 * id   	：页面中被替换之前的公文文号或者内部文号的select或者input(收文)表单框的id
 * replaceStr	：替换之后的拼装字符串
 * edocType : 0：发文   1：收文
 */
function commonMarkChange(markObj,replaceStr) {
	try {
		var edocTypeValue = "";
		var edocType = document.getElementById("edocType");
		if (edocType)
			edocTypeValue = edocType.value;
		
		/* 空防护和兼容收文登记(收文登记为文本域) */
		if (markObj==null 
				|| markObj.getAttribute("access")=="browse" 
				|| (edocTypeValue=="1" && (markObj.name=="my:doc_mark"||markObj.name=="my:doc_mark2")))
			return ;
		
		var value = getDefaultSelectValue(markObj);
		var arr = getOptionsValueToArr(markObj);
		
		if (markObj.name=="my:doc_mark"){
			docMarkOriginalArr = arr;
		} else if (markObj.name=="my:doc_mark2") {
			docMark2OriginalArr = arr;
		} else if (markObj.name=="my:serial_no") {
			serialNoOriginalArr = arr;
		}
		
		replaceMarkDivContent(markObj,replaceStr);
		
		// 兼容text自己输入修改不显示情况，value默认为 ' '
		if(markObj.type=="text")
			value = " ";
		
		v3xautocomplete.autocomplete(markObj.id,returnJson(arr),{select:function(item,inputName){markObj.value=item.value},button:true,autoSize:true,appendBlank:false,value:value});
		
		/* 为文号的隐藏域赋值:这句语句不能移到自动下拉之前 */
		var hiddenMarkObj = document.getElementById(markObj.id);
		if (hiddenMarkObj) {
			hiddenMarkObj.value = value;
		}
	} catch(e) {
		alert("Exception : 文号下拉提示错误！" + e.message);
	}
}
/**
 * 替换原始文号所在<div>中内容，markObj：文号对象 replaceStr：替换的内容
 */
function replaceMarkDivContent(markObj,replaceStr) {
	if (markObj == null || replaceStr == "") return;
	
	var parentNode = markObj.parentNode;
	if (parentNode.hasChildNodes()) {
		var allChildNodes = parentNode.childNodes;
		for (var i = 0 ; i < allChildNodes.length ; i ++) {
			if (allChildNodes[i].nodeName.toLowerCase() == 'select') {
				allChildNodes[i].outerHTML = replaceStr;
				break;
			}
		}
	}
}
/**
 * 取得下拉提示框中默认的显示值
 * 拟文、修改文号、修改文单、编辑保存待发、新建公文模版时若公文文号和内部文号有值则显示为默认值，如果没有可选文号显示默认提示，若只有一个文号则显示此文号
 */
function getDefaultSelectValue(markObj) {
	var name = markObj.name;
	var value = "";
	var originalMarkValue = "";
	var originalMark2Value = "";
	var originalInMarkValue = "";
	
	var docMarkValue = document.getElementById("docMarkValue");
	if(docMarkValue){
		var markValue = docMarkValue.value;
		originalMarkValue = getCompareLabelValue(markValue,markObj) ;
	}
	var docMarkValue2 = document.getElementById("docMarkValue2");
	if(docMarkValue2){
		var markValue2 = docMarkValue2.value;
		originalMark2Value = getCompareLabelValue(markValue2,markObj);
	}
	var inMark = document.getElementById("docInmarkValue");
	if(inMark){
		var inMarkValue = inMark.value;
		originalInMarkValue = getCompareLabelValue(inMarkValue,markObj);
	}
	
	/* 设置公文文号的默认选中值    规则：当只有一个文号时，默认选中此文号，反之则给出选择提示 ,顺序不能颠倒，默认选择的项要在前面 */		
	if ("my:doc_mark" == name || "my:doc_mark2" == name) {
		if (markObj.type == "select-one") {
			if (markObj.options.length == 2) {							// 只有一个选项	
				if (getDisplayLabel(markObj.options[0].value) != "")
					value = markObj.options[0].value.trim();
				else if (getDisplayLabel(markObj.options[1].value) != "")
					value = markObj.options[1].value.trim();
			}									
			else if (originalMarkValue.trim().length > 0 || originalMark2Value.trim().length > 0) {  // 显示值
				if ("my:doc_mark" == name)
					value = originalMarkValue;
				if ("my:doc_mark2" == name)
					value = originalMark2Value;
			}
			else														// 显示提示语
				value = " ";
		} else if (markObj.type == "text") {
			return "";
		}
	} else if ("my:serial_no" == name) {
		if (markObj.type == "select-one") {
			markObj = removeBlankSelect(markObj);
			
			if (markObj.options.length == 2) {
				if (getDisplayLabel(markObj.options[0].value) != "")
					value = markObj.options[0].value.trim();
				else if (getDisplayLabel(markObj.options[1].value) != "")
					value = markObj.options[1].value.trim();
			}
			else if (originalInMarkValue.trim().length > 0) 
				value = originalInMarkValue;
			else 
				value = " ";
		} else if (markObj.type == "text") {
			return '';
		}
	}
	return value;
}
/**
 * 将文号select对象中每个option值放入数组并返回此数组，并且设置默认提示选项，公文文号与联合发文文号使用同一提示
 * 并且兼容收文登记时文号类型为text情况
 */
function getOptionsValueToArr(markObj) {
	var name = markObj.name;
	var arr = new Array();
	if ("my:doc_mark" == name || "my:doc_mark2" == name) 
		arr.push("请选择公文文号");
	else if ("my:serial_no" == name)
		arr.push("请选择内部文号");
	
	/* mark 对象类型，公文文号：select-one   内部文号：select-one,text */
	var type = markObj.type;
	if (type == "select-one") {
		for (var i = 0 ; i < markObj.length ; i ++) {
			if (markObj.options[i].value != '') 
				arr.push(markObj.options[i].value);
		}
	} else if(type == "text") {
		arr.push(markObj.value);
	}
	return arr;
}
/**
 * 对传过来的公文文号数据进行拆分拼装
 * array  	： 类似于如下数据,例如 123723892792|致远远字(2012)001|1|1,123723892792|致远远字(2012)001|1|1
 * @returns	： 返回 json 对象，内容格式如下：
 * 			  [{value:"010",label:"Beijing北京"},{value:"020",label:"guangzhou"},{value:"021",label:"shanghai"}]
 */ 
function returnJson(array) {
	if (array == null)
		return "";
	
	var data = "";
	data += "[";
	for (var i = 0; i < array.length ; i ++) {
		if (array[i] != "" && array[i] != " ") {
			if (i > 0)
				data +=",";
			data += "{";
			// 拆分字符串 格式为  123723892792|致远远字(2012)001|1|1
			var flag = array[i].lastIndexOf("|");
			if (flag > 0) {
				var str = array[i].split("|");
				if (str[1] != '') {
					data += "value:'"+array[i]+"',";
					data += "label:'"+str[1]+"'";
				}
			} else {
				data += "value:' ',";	// 注意:此处的空格不能去掉，此else只捕捉默认提示
				data += "label:'"+array[i]+"'";
			}
			data += "}";
		}
	}
	data += "]";
	return eval(data);
}
/**
 * 传进来的值和例如如下字符串中[致远远字(2012)001]进行比较，例如：123723892792|致远远字(2012)001|1|1
 * 如果传进来的值与位置2上的值相等，则将字符串返回
 * compareValue	: 传进来进行比较的值
 * object		: 下拉框的对象，其value值为类似这样的字符串：123723892792|致远远字(2012)001|1|1
 */
function getCompareLabelValue(compareValue,object) {
	if (!object || object.type != "select-one")
		return "";
	for (var i = 0 ; i < object.options.length ; i ++) {
		var value = object.options[i].value;
		if (value == compareValue)
			return value ;
		if (value.indexOf("|") > -1) {
			var arrs = value.split("|");
			if (arrs.length > 1 && arrs[1] == compareValue && arrs[1].trim().length > 0) {
				return value ;
			}
		}
	}
	return "";
}
/**
 * 得到如下字符串中的显示值，例如：123723892792|致远远字(2012)001|1|1，则返回置为：致远远字(2012)001
 * 如果参数格式不是这种格式则将参数返回
 */
function getDisplayLabel(value) {
	if (value == null)
		return '';
	var flag = value.lastIndexOf("|");
	if (flag == -1)
		return value;
	var spStr = value.split("|");
	if (spStr.length >= 2)
		return spStr[1].trim();
	else 
		return value;
}
/**
 * 删除对象obj中值为空的option,例如:ojb对象的值为 ''，则会被清除
 * 返回被清除后的 obj
 */
function removeBlankSelect(obj) {
	for (var i = 0 ; i < obj.options.length ; i ++) {
		if (obj.options[i].value.trim() != '' && getDisplayLabel(obj.options[i].value) == '')
			obj.options.remove(i);
	}
	return obj;
}

function changeSecretLevel(object){
	var selectValue = object.value;
	var theForm = document.getElementsByName("sendForm")[0];
	var workflowInfoObj = document.getElementById("workflowInfo");
	var peopleObj= document.getElementById("people");
    if(theForm.process_xml.value != "" && selectValue > flowSecretLevel_wf){
		if(window.confirm('密级变高后需要清空当前流程信息重新编辑，你确定要改变密级吗？')){
			peopleObj.innerHTML = "";
			workflowInfoObj.value = "<点击新建流程>";
			theForm.process_xml.value = ""
			theForm.process_desc_by.value = ""
			flowSecretLevel_wf = object.value;
			hasWorkflow = false;
		    isFromTemplate = false;
            return true;
         }else{
        	document.getElementById("secretLevel").value = flowSecretLevel_wf;
            return false;
        }
    }else{
    	flowSecretLevel_wf = object.value;
	}
    
}