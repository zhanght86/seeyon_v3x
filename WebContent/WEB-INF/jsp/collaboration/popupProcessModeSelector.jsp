<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="../common/INC/noCache.jsp" %>
<c:if test="${param.app eq 'edoc'}">
<%@ include file="../edoc/edocHeader.jsp"%>
<%@ include file="../doc/pigeonholeHeader.jsp" %>
</c:if>
<c:if test="${param.app ne 'edoc'}">
<%@ include file="Collaborationheader.jsp" %>
</c:if>
<title><fmt:message key="common.node.select.people.label" bundle="${v3xCommonI18N}" /><c:if test="${param.hasNewflow eq 'true'}">/ <fmt:message key="newflow.label" /></c:if>
</title>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery.js${v3x:resSuffix()}" />"></script>
<v3x:selectPeople id="node1" panels="Department,Team,Post,RelatePeople" selectType="Member" maxSize="1" jsFunction="doSelectPeople(elements)" />
<v3x:selectPeople id="nodeN" panels="Department,Team,Post,RelatePeople" selectType="Member" jsFunction="doSelectPeople(elements)" />

<script type="text/javascript">
<!--
var hiddenRootAccount_node1 = true;
var hiddenRootAccount_nodeN = true;
var flowSecretLevel_node1 = "${param.secretLevel}";
var flowSecretLevel_nodeN = "${param.secretLevel}";

//分支debug模式
var isDebug = true;

var selectPeopleElements = {};
<c:if test="${param.app eq 'edoc'}">
var showLabel = "<fmt:message key='col.branch.show' bundle="${colI18N}"/>";
var hideLabel = "<fmt:message key='col.branch.hide' bundle="${colI18N}"/>";
</c:if>
<c:if test="${param.app ne 'edoc'}">
var showLabel = "<fmt:message key='col.branch.show'/>";
var hideLabel = "<fmt:message key='col.branch.hide'/>";
</c:if>

var newflowConditionTrue = "<fmt:message key='newflow.condition.true'/>";
var newflowConditionFalse = "<fmt:message key='newflow.condition.false'/>";
//获得父窗口对象的引用
var _parent = window.opener;
//定义当前流程节点id对象
var currentNodeId = null;
//获得自定义的父窗口对象的引用
if(window.dialogArguments){
    _parent = window.dialogArguments;
}
if(!v3x.getBrowserFlag('OpenDivWindow')){
	_parent = parent.parent.detailRightFrame;
	if(_parent == null){
		_parent = parent;
	}
}
//从父页面获得是否来自表单的标志
var isForm= _parent.isForm;
var rootNodeName= _parent.rootNodeName;
//从父页面获得分支计算需要的一些系统参数，例如team
var team = _parent.team;
var secondpost = _parent.secondpost;
var startTeam = _parent.startTeam;
var startSecondpost = _parent.startSecondpost;
if(_parent && _parent.document.getElementById("currentNodeId")){
	if(_parent.document.getElementById("currentNodeId").value=='start'){//发起者新建协同页面
		startTeam= team;
		startSecondpost= secondpost;
	}
	//alert(_parent.document.getElementById("currentNodeId").value);
}

var keys = _parent.keys;

//页面加载完成时调用此方法进行一些初始化
function _doOnLoad(){
	//从父页面获得json字符串
	var parentJsonData= _parent.document.getElementById("popJsonId").value;
	//alert("parentJsonData:="+parentJsonData);
	//将json字符串转成json对象
	var parentJsonObj= eval('('+parentJsonData+')');
	//从父页面获得相关表单参数信息,为以ajax提交至后台获取分支条件、新流程信息提供参数
	//流程模板定义xml文件
	if(_parent.document.getElementById("process_xml")){
		document.getElementById("process_xml").value= _parent.document.getElementById("process_xml").value;
	}
	if(_parent.document.getElementById("people")){
		document.getElementById("people").innerHTML= _parent.document.getElementById("people").innerHTML;
	}
	//流程模板描述格式：目前已固定为xml格式
	if(_parent.document.getElementById("process_desc_by")){
		document.getElementById("process_desc_by").value= _parent.document.getElementById("process_desc_by").value;
	}
	//当前流程处理节点的节点ID
	document.getElementById("currentNodeId").value= parentJsonObj.currentNodeId;
	//当前处理人员对应的待办事项Id
	if(_parent.document.getElementById("affair_id")){
		document.getElementById("affair_id").value= _parent.document.getElementById("affair_id").value;
	}
	//标志该流程是否来自协同模板或表单模板
	document.getElementById("isFromTemplate").value= parentJsonObj.templateFlag;
	//表单数据
	if(_parent.document.getElementById("formData")){
		document.getElementById("formData").value= _parent.document.getElementById("formData").value;
	}
	//应用名称：1表示普通协同,4表公文协同
	if(_parent.document.getElementById("appName")){
		document.getElementById("appName").value= _parent.document.getElementById("appName").value;
	}
	//流程模板Id
	document.getElementById("processId").value= parentJsonObj.processId;
	//流程实例id
	if(parentJsonObj.caseId  && parentJsonObj.caseId!= undefined && parentJsonObj.caseId !="undefined"){
		document.getElementById("caseId").value= parentJsonObj.caseId;
	}
	//是否来自模板
	var templateFlag= parentJsonObj.templateFlag;
	//是否需要匹配(必要条件)
	document.getElementById("isMatch").value= parentJsonObj.isMatch;
	document.getElementById("popFlag1").value= parentJsonObj.popFlag1;
	document.getElementById("hasNewflow").value= parentJsonObj.hasNewflow;
	//alert("popFlag1:="+document.getElementById("popFlag1").value);
	//向后台发起流程分支和新流程匹配，并返回分支和新流程的显示页面代码
	//对页面div1中的分支条件根据表单数据进行初始化
	<c:if test="${param.app eq 'edoc'}">
	$('#form1').ajaxSubmit({
    	//url : colWorkFlowURL + "?method=popProcessSelectPage&isFromTemplate="+templateFlag,
    	url : colWorkFlowURL + "?method=popProcessSelectPageNew&isFromTemplate="+templateFlag+"&secretLevel="+flowSecretLevel_node1,
        type : 'POST',
        async : false,
        success : function(data) {
        	//alert("data_sdafsda:="+data);
        	$('#divPop').html(data);
        }
	});
	var isNewColl = _parent.isNewColl;
	//alert("isNewColl111:="+isNewColl);
	initCondition_edoc(isNewColl);
	</c:if>
	<c:if test="${param.app ne 'edoc'}">
	$('#form1').ajaxSubmit({
    	//url : genericURL + "?method=popProcessSelectPage&isFromTemplate="+templateFlag,
    	url : genericURL + "?method=popProcessSelectPageNew&isFromTemplate="+templateFlag+"&secretLevel="+flowSecretLevel_node1,
        type : 'POST',
        async : false,
        success : function(data) {
        	//alert("data_sdafsda:="+data);
        	$('#divPop').html(data);
        }
	});
	initCondition_coll();
	</c:if>
	//当没有符合条件的分支时，显示隐藏的分支流程。
	var hasBranchSelected="";
	if(document.getElementById("hasBranchSelected")){
		hasBranchSelected=document.getElementById("hasBranchSelected").innerHTML;
		//alert("hasBranchSelected:="+hasBranchSelected);
		//判断是否有分支被选中
		if(hasBranchSelected!="yes"){
			//如果没有选中，则显示不符合要求的分支条件
			showFailedCondition("");
		}
		processSelector();
		toggleDynamicSelector();
	}
	_parent.document.getElementById("popNodeSelected").value="";
	_parent.document.getElementById("popNodeCondition").value="";
	_parent.document.getElementById("popNodeNewFlow").value="";
}

var informSelector = new Array();
// 暂存innerHTML,避免validate的影响
var manualSelectHtmls = new Array();
function processSelector(){
	var eles = document.getElementsByTagName("input");
	if(eles){
		for(var k=0;k<eles.length;k++){
			var ele = eles[k];
			if(ele.type=='checkbox' && ele.name.indexOf("condition")!=-1){
				var cid= 'td'+ele.id;
				if(ele.checked){
					document.getElementById(cid).style.display='block';					
				}else{
					document.getElementById(cid).style.display='none';					
				}
			}
			
			if(ele.fromIsInform=="true"){
				var negativeNodes = ele.negativeNodes;
				if(negativeNodes){
					negativeNodes = eval(negativeNodes);
				}
				var sourceInform = ele.sourceInformNodes;
				if(sourceInform){
					sourceInform = eval(sourceInform);
				}
				if(!(sourceInform.length==0&&negativeNodes.length==0)){
					informSelector.push({'id':ele.nodeId,'sourceInforms':sourceInform,'negatives':negativeNodes});
				}
			}
		}
	}	
}

function toggleDynamicSelector(){
	for(var i=0;i<informSelector.length;i++){
		var id = informSelector[i].id;
		var input = document.getElementById('manual_select_node_id'+id);
		var ele = document.getElementById('d'+id);
		var negatives = informSelector[i].negatives;
		var sourceInforms = informSelector[i].sourceInforms;
		var display = true;
		for(var j=0;j<negatives.length;j++){
			// 这些节点都没选中才显示知会后的选人
			display = display && !document.getElementById(negatives[j]).checked;
			if(!display) break;
		}
		if(display){
			// 前面的知会分支必须选中1个以上,否则也不显示
			var selectInform = false;
			if(sourceInforms.length>0){
				for(var k=0;k<sourceInforms.length;k++){
					var chk = document.getElementById(sourceInforms[k])
					if(chk==null){
						selectInform = true;
						continue;
					}
					selectInform = selectInform || chk.checked;
					if(selectInform)break;
				}
				display = selectInform;
			}else{
				display = negatives.length==0;
			}
		}
		if(!manualSelectHtmls[id])
			manualSelectHtmls[id] = ele.innerHTML;		
		if(!display){
			ele.innerHTML = '';
			ele.style.display='none';
		}else{
			ele.style.display='block';
			ele.innerHTML = manualSelectHtmls[id];
		}		
	}
}
function changeButton(dis){
	var submitBut = document.getElementById("submitButton");
	var cancelBut = document.getElementById("cancelButton");
	if(submitBut && cancelBut){
		if(dis){
			submitBut.disabled = true;
			cancelBut.disabled = true;
		}else{
			submitBut.disabled = false;
			cancelBut.disabled = false;
		}
	}
}

//单击确定按钮进行提交操作
function ok(){
	//是否有不可用节点
	//if(document.getElementById("invalidateActivity")){
	var invalidateNodeObjs= document.getElementsByName("invalidateNodeId");
	var invalidateNodeNames= new Array();
	var sameLevelNodeMapInfo= new Array();
	if(invalidateNodeObjs){
		for(var i=0;i<invalidateNodeObjs.length;i++){
			var aInvalidateNode= invalidateNodeObjs[i];
			var invalidate= aInvalidateNode.getAttribute("invalidate");
			var nodeName= aInvalidateNode.getAttribute("nodeName");
			if(invalidate=="true"){//该节点用户不可用
				var aInvalidateNodeHasPatchObj= document.getElementById(aInvalidateNode.value);
				if(aInvalidateNodeHasPatchObj){//是分支流程节点,则判断该分支是否被选中
					if(aInvalidateNodeHasPatchObj.checked == true){//选中，则提示该节点用户不可用
						invalidateNodeNames.push(nodeName);
					}else{//没选中，则继续
						continue;
					}
				}else{//普通节点，则提示该节点用户不可用
					invalidateNodeNames.push(nodeName);
				}
			}
		}
		if(invalidateNodeNames.length >0){
			alert(_("collaborationLang.collaboration_invalidateNode", invalidateNodeNames.toString()));
			<c:if test="${param.app eq 'edoc'}">
			disabledPrecessButtonEdoc(false);
			</c:if>
			<c:if test="${param.app ne 'edoc'}">
			disabledPrecessButton(false);
			</c:if>
	    	return;
		}
	}
	//只取条件部分内容
	var inputs = null;
	var	newflowDIVInputs = null;
	if(document.getElementById("conditionDiv")){
		inputs = document.getElementById("conditionDiv").getElementsByTagName("INPUT");
	}
	if(document.getElementById("newflowDIV")){
		newflowDIVInputs = document.getElementById("newflowDIV").getElementsByTagName("INPUT");
	}
	changeButton(true);
	var selectCount = 0;
	var isRealyHasBranch= false;
	if(inputs){
		for(var i=0;i<inputs.length;i++){
		   	if(inputs[i].type=="checkbox"){
		   		isRealyHasBranch= true;
		   		var selectPeople = document.getElementsByName("manual_select_node_id"+inputs[i].id+"Name");
		   		//单人执行下拉列表的名字中没有"Name"
		   		if(selectPeople && selectPeople.length==0){
		   			selectPeople = document.getElementsByName("manual_select_node_id"+inputs[i].id);
		   		}
		   	    if(!inputs[i].checked){
		   			selectCount++;
		   			if(selectPeople&&selectPeople.length>0){
		   				selectPeople[0].setAttribute("clearValue","true");
		   			}	
		   		}else{
			   		if(selectPeople&&selectPeople.length>0){
			   			selectPeople[0].setAttribute("clearValue","");
			   		}
			   	}
			   	//如果没有选中，就不要验证了。
			   	var validateId = "manual_select_node_id"+inputs[i].id;
			   	setValidate(inputs[i],validateId);
		   	}
		}
	}
	
	if(newflowDIVInputs){//新流程触发条件没选中时，不需要对人员进行校验
	    for(var i=0;i<newflowDIVInputs.length;i++){
	    	if(newflowDIVInputs[i].type=="checkbox"){
	    		//如果没有选中，就不要验证了。
			   	var validateId = "senderId_"+newflowDIVInputs[i].value;
			   	setValidate(newflowDIVInputs[i],validateId);
	    	}
	    }
	}
    
    setValue(inputs);
    
    if(!checkForm(document.form1)){
    	changeButton(false);
        return;
    }
    //最后对所有节点的类型进行判断，如果有知会节点，则需要做进一步的处理
    var informNodes="";
    var allSelectInformNodes="";
    var allSelectNodes="";
    //获得前面步骤选择的节点字符串
    var allSelectNodesBefore= document.getElementById("allSelectNodes").value;
    var allSelectInformNodesBefore= document.getElementById("allSelectInformNodes").value;
    //alert("allSelectNodesBefore:="+allSelectNodesBefore);
    var hasBefore= false;
    var hasSelectInformNodesBefore= false;
    if(allSelectNodesBefore.lastIndexOf("allSelectNodes")!=-1 && allSelectNodesBefore.lastIndexOf("]}")!=-1){
    	allSelectNodes= allSelectNodesBefore.substring(0,allSelectNodesBefore.lastIndexOf("]}"));
    	//alert("allSelectNodes:="+allSelectNodes);
    	hasBefore= true;
    }
    if(allSelectInformNodesBefore.lastIndexOf("allSelectNodes")!=-1 && allSelectInformNodesBefore.lastIndexOf("]}")!=-1){
    	allSelectInformNodes= allSelectInformNodesBefore.substring(0,allSelectNodesBefore.lastIndexOf("]}"));
    	hasSelectInformNodesBefore= true;
    }
    var allNotSelectNodes="";
  	//获得前面步骤没选择的节点字符串
  	allNotSelectNodesBefore= document.getElementById("allNotSelectNodes").value;
  	//alert("allNotSelectNodesBefore:="+allNotSelectNodesBefore);
  	var hasBeforeOfNotSelect= false;
  	if(allNotSelectNodesBefore.lastIndexOf("allNotSelectNodes")!=-1 && allNotSelectNodesBefore.lastIndexOf("]}")!=-1){
  		allNotSelectNodes= allNotSelectNodesBefore.substring(0,allNotSelectNodesBefore.lastIndexOf("]}"));
    	//alert("allNotSelectNodes:="+allNotSelectNodes);
    	hasBeforeOfNotSelect= true;
    }
  	//获得当前页面的所有选中的、未选中的和所有选中的知会节点，并拼成json格式的字符串
    var nodeTypeIdObjs= document.getElementsByName("nodeTypeId");
    if(nodeTypeIdObjs){
    	informNodes += "{\"informNodes\":[";
    	if(!hasSelectInformNodesBefore){
    		allSelectInformNodes += "{\"allSelectNodes\":[";
    	}else{
    		allSelectInformNodes += ",";
    	}
    	if(!hasBefore){
    		allSelectNodes += "{\"allSelectNodes\":[";
    	}else{
    		allSelectNodes +=",";
    	}
    	//alert("allSelectNodes:="+allSelectNodes);
    	if(!hasBeforeOfNotSelect){
    		allNotSelectNodes += "{\"allNotSelectNodes\":[";
    	}else{
    		allNotSelectNodes += ",";
    	}
    	//alert("allNotSelectNodes:="+allNotSelectNodes);
    	var notSelectCount= 0;
    	var selectCount= 0;
    	var slectInformCount= 0;
		for(var i=0;i<nodeTypeIdObjs.length;i++){
			var aNodeTypeObj= nodeTypeIdObjs[i];
			var nodeId= aNodeTypeObj.getAttribute("nodeId");
			if(aNodeTypeObj.value=="inform"){//知会节点
				//看是否为分支节点
				var hasPatchNodeObj= document.getElementById(nodeId);
				if(hasPatchNodeObj){//有分支条件
					if(hasPatchNodeObj.checked==true){//该分支已被选中
						informNodes +="\""+nodeId+"\",";
						allSelectNodes +="\""+nodeId+"\",";
						allSelectInformNodes +="\""+nodeId+"\",";
						slectInformCount++;
						selectCount++;
					}else{//该分支没有被选中
						allNotSelectNodes +="\""+nodeId+"\",";
						notSelectCount++;
					}
				}else{//无分支条件
					informNodes +="\""+nodeId+"\",";
					allSelectNodes +="\""+nodeId+"\",";
					allSelectInformNodes +="\""+nodeId+"\",";
					slectInformCount++;
					selectCount++;
				}
			}else{//非知会节点
				//看是否为分支节点
				var hasPatchNodeObj= document.getElementById(nodeId);
				if(hasPatchNodeObj){//有分支条件
					if(hasPatchNodeObj.checked==true){//该分支已被选中
						allSelectNodes +="\""+nodeId+"\",";
						selectCount++;
					}else{//该分支没有被选中
						allNotSelectNodes +="\""+nodeId+"\",";
						notSelectCount++;
					}
				}else{//无分支条件
					allSelectNodes +="\""+nodeId+"\",";
					selectCount++;
				}
			}
		}
		if(informNodes.lastIndexOf(",")==(informNodes.length-1)){
			informNodes= informNodes.substring(0,informNodes.length-1);
    	}
		if(allSelectNodes.lastIndexOf(",")==(allSelectNodes.length-1)){
			allSelectNodes= allSelectNodes.substring(0,allSelectNodes.length-1);
    	}
		if(allSelectInformNodes.lastIndexOf(",")==(allSelectInformNodes.length-1)){
			allSelectInformNodes= allSelectInformNodes.substring(0,allSelectInformNodes.length-1);
    	}
		if(allNotSelectNodes.lastIndexOf(",")==(allNotSelectNodes.length-1)){
			allNotSelectNodes= allNotSelectNodes.substring(0,allNotSelectNodes.length-1);
    	}
		informNodes +="]}";
		allSelectNodes +="]}";
		allSelectInformNodes +="]}";
		allNotSelectNodes +="]}";
		
		if(notSelectCount==0){
			if(allNotSelectNodesBefore.lastIndexOf("allNotSelectNodes")!=-1 && allNotSelectNodesBefore.lastIndexOf("]}")!=-1){
				//do nothing
			}else{
				allNotSelectNodes= "";
			}
		}
		if(selectCount==0){
			if(allSelectNodesBefore.lastIndexOf("allSelectNodes")!=-1 && allSelectNodesBefore.lastIndexOf("]}")!=-1){
				//do nothing
			}else{
				allSelectNodes="";
			}
		}
		if(slectInformCount==0){
			if(allSelectInformNodesBefore.lastIndexOf("allSelectNodes")!=-1 && allSelectInformNodesBefore.lastIndexOf("]}")!=-1){
				//do nothing
			}else{
				allSelectInformNodes="";
			}
		}
    }
    //alert("informNodes:="+informNodes+";allSelectNodes:="+allSelectNodes+";allNotSelectNodes:="+allNotSelectNodes);
    
    document.getElementById("informNodes").value=informNodes;
	document.getElementById("allSelectNodes").value=allSelectNodes;
	document.getElementById("allSelectInformNodes").value=allSelectInformNodes;
	document.getElementById("allNotSelectNodes").value=allNotSelectNodes;
	
	if(document.getElementById("nodeCount")){
	    var nodeCount = document.getElementById("nodeCount").value;
	    var preAllSelectedNodes= document.getElementById("allSelectNodes").value;
	    var preAllSelectedInformNodes= document.getElementById("allSelectInformNodes").value;
	    var selectorModelNodeIdsSize= document.getElementById("selectorModelNodeIdsSize").value;
	    if(preAllSelectedNodes=="" 
	    		&& selectorModelNodeIdsSize!='0' 
	    		&& selectorModelNodeIdsSize!="" 
	    		&& selectorModelNodeIdsSize!=null 
	    		&& selectorModelNodeIdsSize!="undefined"  && isRealyHasBranch){
	    	alert(v3x.getMessage("collaborationLang.branch_notflow"));
	    	changeButton(false);
	    	return;
	    }
    }
	
    if(informNodes=="" || informNodes.indexOf("{\"informNodes\":[]}")==0){
   		//var isSure= confirm("<fmt:message key="common.node.select.people.submittiplabel" bundle="${v3xCommonI18N}" />");
   		//if(isSure){
   		var selectorModelNodeIdsSize= document.getElementById("selectorModelNodeIdsSize").value;
   		if(selectorModelNodeIdsSize=='0' || selectorModelNodeIdsSize=='' || selectorModelNodeIdsSize==null || selectorModelNodeIdsSize=="undefined"){//提交
   			if(inputs){
   			    for(var i=0;i<inputs.length;i++){
   			    	if(inputs[i].type=="checkbox"){
   			    		inputs[i].disabled = false;
   			    	}
   			    }
   		    }
   		    if(newflowDIVInputs){
   			    for(var i=0;i<newflowDIVInputs.length;i++){
   			    	if(newflowDIVInputs[i].type=="checkbox"){
   			    		newflowDIVInputs[i].disabled = false;
   			    	}
   			    }
   		    }
   		 	doNodeBranchParse();
   			window.returnValue = "True";
   			v3x.setResultValue("True");
	   	    window.close();
   		}else{
   			var preAllSelectedNodes= document.getElementById("allSelectNodes").value;
   		    var preAllSelectedInformNodes= document.getElementById("allSelectInformNodes").value;
   		 if((preAllSelectedNodes=="" || preAllSelectedInformNodes== preAllSelectedNodes)  && isRealyHasBranch){
   		    	alert(v3x.getMessage("collaborationLang.branch_notflow"));
   		    	changeButton(false);
   		    	return;
   		    }else{
   		    	if(inputs){
	   		 	    for(var i=0;i<inputs.length;i++){
	   		 	    	if(inputs[i].type=="checkbox"){
	   		 	    		inputs[i].disabled = false;
	   		 	    	}
	   		 	    }
	   		     }
	   		     if(newflowDIVInputs){
	   		 	    for(var i=0;i<newflowDIVInputs.length;i++){
	   		 	    	if(newflowDIVInputs[i].type=="checkbox"){
	   		 	    		newflowDIVInputs[i].disabled = false;
	   		 	    	}
	   		 	    }
	   		     }
	   		  	doNodeBranchParse();
   		    	window.returnValue = "True";
   		    	v3x.setResultValue("True");
   	   	    	window.close();
   		    }
   		}
   		//}
    }else{//继续往后查找节点
    	//doHandleInformNodes
    	var isFromTemplate= document.getElementById("isFromTemplate").value;
    	//alert("isFromTemplate:="+isFromTemplate);
    	if(isFromTemplate=="true" || isFromTemplate==true){
    		doNodeBranchParse();
    		doHandleInformNodes();
    		//var selectorModelNodeIdsSize= document.getElementById("selectorModelNodeIdsSize").value;
    		//alert("selectorModelNodeIdsSize:="+selectorModelNodeIdsSize);
    		//if(selectorModelNodeIdsSize=='0'){//提交
    			//var isSure= confirm("<fmt:message key="common.node.select.people.submittiplabel" bundle="${v3xCommonI18N}" />");
    			//if(isSure){
    				//window.returnValue = "True";
    				//v3x.setResultValue("True");
        	    	//window.close();
    			//}
    		//}
    	}else{
    		var selectorModelNodeIdsSize= document.getElementById("selectorModelNodeIdsSize").value;
    		if(selectorModelNodeIdsSize=='0' || selectorModelNodeIdsSize=='' || selectorModelNodeIdsSize==null || selectorModelNodeIdsSize=="undefined"){//提交
    			if(inputs){
    			    for(var i=0;i<inputs.length;i++){
    			    	if(inputs[i].type=="checkbox"){
    			    		inputs[i].disabled = false;
    			    	}
    			    }
    		    }
    		    if(newflowDIVInputs){
    			    for(var i=0;i<newflowDIVInputs.length;i++){
    			    	if(newflowDIVInputs[i].type=="checkbox"){
    			    		newflowDIVInputs[i].disabled = false;
    			    	}
    			    }
    		    }
    		    doNodeBranchParse();
    			window.returnValue = "True";
    			v3x.setResultValue("True");
    	   	    window.close();
       		}else{
       			var preAllSelectedNodes= document.getElementById("allSelectNodes").value;
       		    var preAllSelectedInformNodes= document.getElementById("allSelectInformNodes").value;
       		 if((preAllSelectedNodes=="" || preAllSelectedInformNodes== preAllSelectedNodes) && isRealyHasBranch){
       		    	alert(v3x.getMessage("collaborationLang.branch_notflow"));
       		    	changeButton(false);
       		    	return;
       		    }else{
       		    	if(inputs){
	       		 	    for(var i=0;i<inputs.length;i++){
	       		 	    	if(inputs[i].type=="checkbox"){
	       		 	    		inputs[i].disabled = false;
	       		 	    	}
	       		 	    }
	       		     }
	       		     if(newflowDIVInputs){
	       		 	    for(var i=0;i<newflowDIVInputs.length;i++){
	       		 	    	if(newflowDIVInputs[i].type=="checkbox"){
	       		 	    		newflowDIVInputs[i].disabled = false;
	       		 	    	}
	       		 	    }
	       		     }
	       		  	doNodeBranchParse();
       		    	window.returnValue = "True";
       		    	v3x.setResultValue("True");
       	   	    	window.close();
       		    }
       		}
    	}
    }
}

function doNodeBranchParse(){
	//获得各流程节点执行人员选择结果
    var node_str= getNodesSelectPepole();
  	//获得各流程节点分支条件匹配结果 
  	var conditon_str= getNodesCondition();
    //获得当前处理节点触发的新流程信息
    var flow_str= getNodeTrigNewFlowInfo();
    //将获得的流程中的节点信息和新流程信息，返回给父页面
    var oldPopNodeSelected= _parent.document.getElementById("popNodeSelected").value;
    //alert("oldPopNodeSelected:="+oldPopNodeSelected+";node_str:="+node_str);
    //对选人信息进行合并处理
    if(oldPopNodeSelected.lastIndexOf("nodeAdditon")!=-1 && oldPopNodeSelected.lastIndexOf("]}")!=-1 && oldPopNodeSelected.lastIndexOf("pepole")!=-1){
    	oldPopNodeSelected= oldPopNodeSelected.substring(0,oldPopNodeSelected.lastIndexOf("]}"));
    	//alert("oldPopNodeSelected:="+oldPopNodeSelected);
    	if(node_str.lastIndexOf("nodeAdditon")!=-1 && node_str.lastIndexOf("]}")!=-1 && node_str.lastIndexOf("pepole")!=-1 ){
    		var beginStr= "{\"nodeAdditon\":[";
    		node_str= node_str.substring(beginStr.length);
    		//alert("node_str:="+node_str);
    		oldPopNodeSelected +=","+node_str;
    		//alert("oldPopNodeSelected:="+oldPopNodeSelected);
    		_parent.document.getElementById("popNodeSelected").value= oldPopNodeSelected;
    	}else{
			oldPopNodeSelected +="]}";
			//alert("oldPopNodeSelected:="+oldPopNodeSelected);
			_parent.document.getElementById("popNodeSelected").value= oldPopNodeSelected;
		}
    }else{
    	_parent.document.getElementById("popNodeSelected").value= node_str;
    }
    //alert("_parent.document.getElementById(\"popNodeSelected\").value:="+_parent.document.getElementById("popNodeSelected").value);
    var oldPopNodeCondition= _parent.document.getElementById("popNodeCondition").value;
    //alert("oldPopNodeCondition:="+oldPopNodeCondition+";conditon_str:="+conditon_str);
  	//对条件分支匹配结果进行合并处理
    if(oldPopNodeCondition.lastIndexOf("condition")!=-1 && oldPopNodeCondition.lastIndexOf("]}")!=-1 && oldPopNodeCondition.lastIndexOf("nodeId")!=-1){
    	oldPopNodeCondition= oldPopNodeCondition.substring(0,oldPopNodeCondition.lastIndexOf("]}"));
    	//alert("oldPopNodeCondition:="+oldPopNodeCondition);
    	if(conditon_str.lastIndexOf("condition")!=-1 && conditon_str.lastIndexOf("]}")!=-1 && conditon_str.lastIndexOf("nodeId")!=-1 ){
    		var beginStr= "{\"condition\":[";
    		conditon_str= conditon_str.substring(beginStr.length);
    		//alert("conditon_str:="+conditon_str);
    		oldPopNodeCondition +=","+conditon_str;
    		//alert("oldPopNodeCondition:="+oldPopNodeCondition);
    		_parent.document.getElementById("popNodeCondition").value= oldPopNodeCondition;
    	}else{
    		oldPopNodeCondition +="]}";
    		_parent.document.getElementById("popNodeCondition").value= oldPopNodeCondition;
    	}
    }else{
    	_parent.document.getElementById("popNodeCondition").value= conditon_str;
    }
    //alert("_parent.document.getElementById(\"popNodeCondition\").value:="+_parent.document.getElementById("popNodeCondition").value);
    
    var oldPopNodeNewFlow= _parent.document.getElementById("popNodeNewFlow").value;
    //alert("oldPopNodeNewFlow:="+oldPopNodeNewFlow);
    //对新流程匹配结果进行合并处理
    if(oldPopNodeNewFlow==""){
    	_parent.document.getElementById("popNodeNewFlow").value= flow_str;
    }
    var oldallNodes= _parent.document.getElementById("allNodes").value;
    if(oldallNodes==""){
    	if(document.getElementById("allNodes")){
    		_parent.document.getElementById("allNodes").value= document.getElementById("allNodes").value;
    	}
    }
    var oldnodeCount= _parent.document.getElementById("nodeCount").value;
    if(oldnodeCount==""){
    	if(document.getElementById("nodeCount")){
    		_parent.document.getElementById("nodeCount").value= document.getElementById("nodeCount").value;
    	}
    }
}

function doHandleInformNodes(){
	//alert('doHandleInformNodes');
	//alert("含有知会节点，需要继续往后查找人工节点!");
	var templateFlag= document.getElementById("isFromTemplate").value;
	//向后台发起流程分支和新流程匹配，并返回分支和新流程的显示页面代码
	//对页面div1中的分支条件根据表单数据进行初始化
	<c:if test="${param.app eq 'edoc'}">
	$('#form1').ajaxSubmit({
    	//url : colWorkFlowURL + "?method=popProcessSelectPage&isFromTemplate="+templateFlag,
    	url : colWorkFlowURL + "?method=popProcessSelectPageNext&isFromTemplate="+templateFlag,
        type : 'POST',
        async : false,
        success : function(data) {
        	//alert("data_sdafsda:="+data);
        	$('#divPop').html(data);
        }
	});
	var isNewColl = _parent.isNewColl;
	//alert("isNewColl111:="+isNewColl);
	initCondition_edoc(isNewColl);
	</c:if>
	<c:if test="${param.app ne 'edoc'}">
	//alert('ssss');
	//alert(genericURL + "?method=popProcessSelectPageNext&isFromTemplate="+templateFlag);
	$('#form1').ajaxSubmit({
    	//url : genericURL + "?method=popProcessSelectPage&isFromTemplate="+templateFlag,
    	url : genericURL + "?method=popProcessSelectPageNext&isFromTemplate="+templateFlag,
        type : 'POST',
        async : false,
        success : function(data) {
        	//alert("data_sdafsda:="+data);
        	$('#divPop').html(data);
        }
	});
	initCondition_coll();
	</c:if>
	//当没有符合条件的分支时，显示隐藏的分支流程。
	var hasBranchSelected="";
	if(document.getElementById("hasBranchSelected")){
		hasBranchSelected=document.getElementById("hasBranchSelected").innerHTML;
		//alert("hasBranchSelected:="+hasBranchSelected);
		//判断是否有分支被选中
		if(hasBranchSelected!="yes"){
			//如果没有选中，则显示不符合要求的分支条件
			showFailedCondition("");
		}
		processSelector();
		toggleDynamicSelector();
	}
	
}

/**
 * 获得各流程节点执行人员选择结果
 */
function getNodesSelectPepole(){
	
	var nodestr= "";
    var nodeSelectList= document.getElementsByName("manual_select_node_id");
    //alert("nodeSelectList:="+nodeSelectList.length);
    if(nodeSelectList){
    	if(nodeSelectList.length > 0){
        	nodestr += "{\"nodeAdditon\":[";
        	for(var i=0;i<nodeSelectList.length;i++){
            	//获得流程节点Id
            	var aNodeId=  nodeSelectList[i].value;
            	//判断该流程节点对应的条件分支是否被选中
            	var aChexBoxObj= document.getElementById("condition"+aNodeId);
            	if(aChexBoxObj){//是否有前面的复选框
            		if(aChexBoxObj.checked== false){//是否被选中
            			//alert(aNodeId+",没有被选中!!!");
            			//没有被选中，则继续遍历
            			continue;
            		}
            	}
            	nodestr += "{\"nodeId\":\""+aNodeId+"\",\"pepole\":[";
            	//根据流程节点Id值获得对应的人员Id
            	var pepolesObj= document.getElementsByName("manual_select_node_id"+aNodeId);
            	for(var j=0;j<pepolesObj.length;j++){
            		var aPepole= pepolesObj[j].value;
            		nodestr +="\""+aPepole+"\"";
            		if(j<pepolesObj.length-1){
            			nodestr +=",";
            		}
            	}
            	nodestr +="]},";
            }
        	if(nodestr.lastIndexOf(",")==(nodestr.length-1)){
        		nodestr= nodestr.substring(0,nodestr.length-1);
        	}
        	nodestr +="]}";
            //alert("nodestr:="+nodestr);
        }
    }
    return nodestr;
}

/**
 * 获得各流程节点分支条件匹配结果 
 */
function getNodesCondition(){
    var conditon_Str="";
    if(document.getElementById("allNodes")){
    	var allNodesObj= document.getElementById("allNodes").value;
        var allNodes_Str= allNodesObj.split(":");
        if(allNodes_Str.length > 0){
        	conditon_Str +="{\"condition\":[";
        	for(var i=0;i< allNodes_Str.length;i++){
            	var aNode= allNodes_Str[i];
            	//alert("aNode:="+aNode);
            	if(aNode != ""){
            		//获得流程节点对应的条件分支对象
                	var aNodeConditionObj= document.getElementById(aNode);
                	if(aNodeConditionObj){
                		conditon_Str +="{\"nodeId\":\""+aNode+"\",";
                		if(aNodeConditionObj.checked==true){
                    		conditon_Str +="\"isDelete\":\"false\"},";
                    	}else{
                    		conditon_Str +="\"isDelete\":\"true\"},";
                    	}
                	}else{
                		conditon_Str +="\"isDelete\":\"false\"},";
                	}
            	}
            }
        	if(conditon_Str.lastIndexOf(",")==(conditon_Str.length-1)){
        		conditon_Str= conditon_Str.substring(0,conditon_Str.length-1);
        	}
        	conditon_Str +="]}";
            //alert("conditon_Str:="+conditon_Str);
        }
    }
    return conditon_Str;
}

/**
 * 获得当前处理节点触发的新流程信息
 */
function getNodeTrigNewFlowInfo(){
	var flow_Str ="{";
    var hasNewFlowObj= document.getElementById("hasNewflow");
    //alert("hasNewFlowObj:="+hasNewFlowObj.value);
    if(hasNewFlowObj.value=="true"){
    	flow_Str +="\"hasNewflow\":\"true\",\"newFlows\":[";
    	var newFlowsObj= document.getElementsByName("newflow");
    	for(var i=0;i<newFlowsObj.length;i++){
    		var aNewFlowObj= newFlowsObj[i];
    		if(aNewFlowObj.checked==true){//符合条件的新流程分支
    			//获得新流程Id
    			var newFlowIdStr= aNewFlowObj.value;
    			//获得新流程的发送者Id
    			var newFlowSenderIdStr= document.getElementsByName("senderId_"+newFlowIdStr)[0].value;
    			flow_Str +="{\"newFlowId\":\""+newFlowIdStr+"\",\"newFlowSender\":\""+newFlowSenderIdStr+"\"},";
    		}
    	}
    	if(flow_Str.lastIndexOf(",")==(flow_Str.length-1)){
    		flow_Str= flow_Str.substring(0,flow_Str.length-1);
    	}
    	flow_Str +="]}";
    }else{
    	flow_Str +="\"hasNewflow\":\"false\"}";
    }
    //alert("flow_Str:="+flow_Str);
    return flow_Str;
}
//39547漳州喜盈门家具:Ipad处理表单，表单下一个节点是单人执行，弹不出选人界面
function OK(){
	//是否有不可用节点
	//if(document.getElementById("invalidateActivity")){
	var invalidateNodeObjs= document.getElementsByName("invalidateNodeId");
	var invalidateNodeNames= new Array();
	var sameLevelNodeMapInfo= new Array();
	if(invalidateNodeObjs){
		for(var i=0;i<invalidateNodeObjs.length;i++){
			var aInvalidateNode= invalidateNodeObjs[i];
			var invalidate= aInvalidateNode.getAttribute("invalidate");
			var nodeName= aInvalidateNode.getAttribute("nodeName");
			if(invalidate=="true"){//该节点用户不可用
				var aInvalidateNodeHasPatchObj= document.getElementById(aInvalidateNode.value);
				if(aInvalidateNodeHasPatchObj){//是分支流程节点,则判断该分支是否被选中
					if(aInvalidateNodeHasPatchObj.checked == true){//选中，则提示该节点用户不可用
						invalidateNodeNames.push(nodeName);
					}else{//没选中，则继续
						continue;
					}
				}else{//普通节点，则提示该节点用户不可用
					invalidateNodeNames.push(nodeName);
				}
			}
		}
		if(invalidateNodeNames.length >0){
			alert(_("collaborationLang.collaboration_invalidateNode", invalidateNodeNames.toString()));
			<c:if test="${param.app eq 'edoc'}">
			disabledPrecessButtonEdoc(false);
			</c:if>
			<c:if test="${param.app ne 'edoc'}">
			disabledPrecessButton(false);
			</c:if>
			 return "False";
		}
	}
	//只取条件部分内容
	var inputs = null;
	var	newflowDIVInputs = null;
	if(document.getElementById("conditionDiv")){
		inputs = document.getElementById("conditionDiv").getElementsByTagName("INPUT");
	}
	if(document.getElementById("newflowDIV")){
		newflowDIVInputs = document.getElementById("newflowDIV").getElementsByTagName("INPUT");
	}
	changeButton(true);
	var selectCount = 0;
	var isRealyHasBranch= false;
	if(inputs){
		for(var i=0;i<inputs.length;i++){
		   	if(inputs[i].type=="checkbox"){
		   		isRealyHasBranch= true;
		   		var selectPeople = document.getElementsByName("manual_select_node_id"+inputs[i].id+"Name");
		   		//单人执行下拉列表的名字中没有"Name"
		   		if(selectPeople && selectPeople.length==0){
		   			selectPeople = document.getElementsByName("manual_select_node_id"+inputs[i].id);
		   		}
		   	    if(!inputs[i].checked){
		   			selectCount++;
		   			if(selectPeople&&selectPeople.length>0){
		   				selectPeople[0].setAttribute("clearValue","true");
		   			}	
		   		}else{
			   		if(selectPeople&&selectPeople.length>0){
			   			selectPeople[0].setAttribute("clearValue","");
			   		}
			   	}
			   	//如果没有选中，就不要验证了。
			   	var validateId = "manual_select_node_id"+inputs[i].id;
			   	setValidate(inputs[i],validateId);
		   	}
		}
	}
	
	if(newflowDIVInputs){//新流程触发条件没选中时，不需要对人员进行校验
	    for(var i=0;i<newflowDIVInputs.length;i++){
	    	if(newflowDIVInputs[i].type=="checkbox"){
	    		//如果没有选中，就不要验证了。
			   	var validateId = "senderId_"+newflowDIVInputs[i].value;
			   	setValidate(newflowDIVInputs[i],validateId);
	    	}
	    }
	}
	
    setValue(inputs);
    
    if(!checkForm(document.form1)){
    	changeButton(false);
        return "False";
    }
    
    //最后对所有节点的类型进行判断，如果有知会节点，则需要做进一步的处理
    var informNodes="";
    var allSelectInformNodes="";
    var allSelectNodes="";
    //获得前面步骤选择的节点字符串
    var allSelectNodesBefore= document.getElementById("allSelectNodes").value;
    var allSelectInformNodesBefore= document.getElementById("allSelectInformNodes").value;
    //alert("allSelectNodesBefore:="+allSelectNodesBefore);
    var hasBefore= false;
    var hasSelectInformNodesBefore= false;
    if(allSelectNodesBefore.lastIndexOf("allSelectNodes")!=-1 && allSelectNodesBefore.lastIndexOf("]}")!=-1){
    	allSelectNodes= allSelectNodesBefore.substring(0,allSelectNodesBefore.lastIndexOf("]}"));
    	//alert("allSelectNodes:="+allSelectNodes);
    	hasBefore= true;
    }
    if(allSelectInformNodesBefore.lastIndexOf("allSelectNodes")!=-1 && allSelectInformNodesBefore.lastIndexOf("]}")!=-1){
    	allSelectInformNodes= allSelectInformNodesBefore.substring(0,allSelectNodesBefore.lastIndexOf("]}"));
    	hasSelectInformNodesBefore= true;
    }
    var allNotSelectNodes="";
  	//获得前面步骤没选择的节点字符串
  	allNotSelectNodesBefore= document.getElementById("allNotSelectNodes").value;
  	//alert("allNotSelectNodesBefore:="+allNotSelectNodesBefore);
  	var hasBeforeOfNotSelect= false;
  	if(allNotSelectNodesBefore.lastIndexOf("allNotSelectNodes")!=-1 && allNotSelectNodesBefore.lastIndexOf("]}")!=-1){
  		allNotSelectNodes= allNotSelectNodesBefore.substring(0,allNotSelectNodesBefore.lastIndexOf("]}"));
    	//alert("allNotSelectNodes:="+allNotSelectNodes);
    	hasBeforeOfNotSelect= true;
    }
  	//获得当前页面的所有选中的、未选中的和所有选中的知会节点，并拼成json格式的字符串
    var nodeTypeIdObjs= document.getElementsByName("nodeTypeId");
    if(nodeTypeIdObjs){
    	informNodes += "{\"informNodes\":[";
    	if(!hasSelectInformNodesBefore){
    		allSelectInformNodes += "{\"allSelectNodes\":[";
    	}else{
    		allSelectInformNodes += ",";
    	}
    	if(!hasBefore){
    		allSelectNodes += "{\"allSelectNodes\":[";
    	}else{
    		allSelectNodes +=",";
    	}
    	//alert("allSelectNodes:="+allSelectNodes);
    	if(!hasBeforeOfNotSelect){
    		allNotSelectNodes += "{\"allNotSelectNodes\":[";
    	}else{
    		allNotSelectNodes += ",";
    	}
    	//alert("allNotSelectNodes:="+allNotSelectNodes);
    	var notSelectCount= 0;
    	var selectCount= 0;
    	var slectInformCount= 0;
		for(var i=0;i<nodeTypeIdObjs.length;i++){
			var aNodeTypeObj= nodeTypeIdObjs[i];
			var nodeId= aNodeTypeObj.getAttribute("nodeId");
			if(aNodeTypeObj.value=="inform"){//知会节点
				//看是否为分支节点
				var hasPatchNodeObj= document.getElementById(nodeId);
				if(hasPatchNodeObj){//有分支条件
					if(hasPatchNodeObj.checked==true){//该分支已被选中
						informNodes +="\""+nodeId+"\",";
						allSelectNodes +="\""+nodeId+"\",";
						allSelectInformNodes +="\""+nodeId+"\",";
						slectInformCount++;
						selectCount++;
					}else{//该分支没有被选中
						allNotSelectNodes +="\""+nodeId+"\",";
						notSelectCount++;
					}
				}else{//无分支条件
					informNodes +="\""+nodeId+"\",";
					allSelectNodes +="\""+nodeId+"\",";
					allSelectInformNodes +="\""+nodeId+"\",";
					slectInformCount++;
					selectCount++;
				}
			}else{//非知会节点
				//看是否为分支节点
				var hasPatchNodeObj= document.getElementById(nodeId);
				if(hasPatchNodeObj){//有分支条件
					if(hasPatchNodeObj.checked==true){//该分支已被选中
						allSelectNodes +="\""+nodeId+"\",";
						selectCount++;
					}else{//该分支没有被选中
						allNotSelectNodes +="\""+nodeId+"\",";
						notSelectCount++;
					}
				}else{//无分支条件
					allSelectNodes +="\""+nodeId+"\",";
					selectCount++;
				}
			}
		}
		if(informNodes.lastIndexOf(",")==(informNodes.length-1)){
			informNodes= informNodes.substring(0,informNodes.length-1);
    	}
		if(allSelectNodes.lastIndexOf(",")==(allSelectNodes.length-1)){
			allSelectNodes= allSelectNodes.substring(0,allSelectNodes.length-1);
    	}
		if(allSelectInformNodes.lastIndexOf(",")==(allSelectInformNodes.length-1)){
			allSelectInformNodes= allSelectInformNodes.substring(0,allSelectInformNodes.length-1);
    	}
		if(allNotSelectNodes.lastIndexOf(",")==(allNotSelectNodes.length-1)){
			allNotSelectNodes= allNotSelectNodes.substring(0,allNotSelectNodes.length-1);
    	}
		informNodes +="]}";
		allSelectNodes +="]}";
		allSelectInformNodes +="]}";
		allNotSelectNodes +="]}";
		
		if(notSelectCount==0){
			if(allNotSelectNodesBefore.lastIndexOf("allNotSelectNodes")!=-1 && allNotSelectNodesBefore.lastIndexOf("]}")!=-1){
				//do nothing
			}else{
				allNotSelectNodes= "";
			}
		}
		if(selectCount==0){
			if(allSelectNodesBefore.lastIndexOf("allSelectNodes")!=-1 && allSelectNodesBefore.lastIndexOf("]}")!=-1){
				//do nothing
			}else{
				allSelectNodes="";
			}
		}
		if(slectInformCount==0){
			if(allSelectInformNodesBefore.lastIndexOf("allSelectNodes")!=-1 && allSelectInformNodesBefore.lastIndexOf("]}")!=-1){
				//do nothing
			}else{
				allSelectInformNodes="";
			}
		}
    }
    //alert("informNodes:="+informNodes+";allSelectNodes:="+allSelectNodes+";allNotSelectNodes:="+allNotSelectNodes);
    
    document.getElementById("informNodes").value=informNodes;
	document.getElementById("allSelectNodes").value=allSelectNodes;
	document.getElementById("allSelectInformNodes").value=allSelectInformNodes;
	document.getElementById("allNotSelectNodes").value=allNotSelectNodes;
	
	if(document.getElementById("nodeCount")){
	    var nodeCount = document.getElementById("nodeCount").value;
	    var preAllSelectedNodes= document.getElementById("allSelectNodes").value;
	    var preAllSelectedInformNodes= document.getElementById("allSelectInformNodes").value;
	    //if(nodeCount && nodeCount<=selectCount && preAllSelectedNodes==""){
	    var selectorModelNodeIdsSize= document.getElementById("selectorModelNodeIdsSize").value;
	    
	    if(preAllSelectedNodes=="" 
	    		&& selectorModelNodeIdsSize!='0' 
	    		&& selectorModelNodeIdsSize!='' 
	    		&& selectorModelNodeIdsSize!=null 
	    		&& selectorModelNodeIdsSize!="undefined" && isRealyHasBranch){
	    	alert(v3x.getMessage("collaborationLang.branch_notflow"));
	    	changeButton(false);
	    	return "False";
	    }
    }
	
    if(informNodes=="" || informNodes.indexOf("{\"informNodes\":[]}")==0){
    	//var isSure= confirm("<fmt:message key="common.node.select.people.submittiplabel" bundle="${v3xCommonI18N}" />");
		//if(isSure){
		var selectorModelNodeIdsSize= document.getElementById("selectorModelNodeIdsSize").value;
		if(selectorModelNodeIdsSize=='0' || selectorModelNodeIdsSize=='' || selectorModelNodeIdsSize==null || selectorModelNodeIdsSize=="undefined"){//提交
			if(inputs){
			    for(var i=0;i<inputs.length;i++){
			    	if(inputs[i].type=="checkbox"){
			    		inputs[i].disabled = false;
			    	}
			    }
		    }
		    if(newflowDIVInputs){
			    for(var i=0;i<newflowDIVInputs.length;i++){
			    	if(newflowDIVInputs[i].type=="checkbox"){
			    		newflowDIVInputs[i].disabled = false;
			    	}
			    }
		    }
			doNodeBranchParse();
			return "True";
		}else{
			var preAllSelectedNodes= document.getElementById("allSelectNodes").value;
		    var preAllSelectedInformNodes= document.getElementById("allSelectInformNodes").value;
		    if((preAllSelectedNodes=="" || preAllSelectedInformNodes== preAllSelectedNodes) && isRealyHasBranch){
		    	alert(v3x.getMessage("collaborationLang.branch_notflow"));
		    	changeButton(false);
		    	return "False";
		    }else{
		    	if(inputs){
		    	    for(var i=0;i<inputs.length;i++){
		    	    	if(inputs[i].type=="checkbox"){
		    	    		inputs[i].disabled = false;
		    	    	}
		    	    }
		        }
		        if(newflowDIVInputs){
		    	    for(var i=0;i<newflowDIVInputs.length;i++){
		    	    	if(newflowDIVInputs[i].type=="checkbox"){
		    	    		newflowDIVInputs[i].disabled = false;
		    	    	}
		    	    }
		        }
		    	doNodeBranchParse();
		    	return "True";
		    }
		}
		//}
    }else{//继续往后查找节点
    	//doHandleInformNodes
    	var isFromTemplate= document.getElementById("isFromTemplate").value;
    	//alert("isFromTemplate:="+isFromTemplate);
    	if(isFromTemplate=="true" || isFromTemplate==true){
    		doNodeBranchParse();
    		doHandleInformNodes();
    		//alert(document.getElementById("selectorModelNodeIdsSize"));
    		//var selectorModelNodeIdsSize= document.getElementById("selectorModelNodeIdsSize").value;
    		//alert("selectorModelNodeIdsSize:="+selectorModelNodeIdsSize);
    		//if(selectorModelNodeIdsSize=='0'){//提交
    			//var isSure= confirm("<fmt:message key="common.node.select.people.submittiplabel" bundle="${v3xCommonI18N}" />");
    			//if(isSure){
    				//return "True";
    			//}
    		//}
    	}else{
    		var selectorModelNodeIdsSize= document.getElementById("selectorModelNodeIdsSize").value;
    		if(selectorModelNodeIdsSize=='0' || selectorModelNodeIdsSize=='' || selectorModelNodeIdsSize==null || selectorModelNodeIdsSize=="undefined"){//提交
    			if(inputs){
    			    for(var i=0;i<inputs.length;i++){
    			    	if(inputs[i].type=="checkbox"){
    			    		inputs[i].disabled = false;
    			    	}
    			    }
    		    }
    		    if(newflowDIVInputs){
    			    for(var i=0;i<newflowDIVInputs.length;i++){
    			    	if(newflowDIVInputs[i].type=="checkbox"){
    			    		newflowDIVInputs[i].disabled = false;
    			    	}
    			    }
    		    }
    			doNodeBranchParse();
    			return "True";
    		}else{
    			var preAllSelectedNodes= document.getElementById("allSelectNodes").value;
    		    var preAllSelectedInformNodes= document.getElementById("allSelectInformNodes").value;
    		    if((preAllSelectedNodes=="" || preAllSelectedInformNodes== preAllSelectedNodes) && isRealyHasBranch){
    		    	alert(v3x.getMessage("collaborationLang.branch_notflow"));
    		    	changeButton(false);
    		    	return "False";
    		    }else{
    		    	if(inputs){
    		    	    for(var i=0;i<inputs.length;i++){
    		    	    	if(inputs[i].type=="checkbox"){
    		    	    		inputs[i].disabled = false;
    		    	    	}
    		    	    }
    		        }
    		        if(newflowDIVInputs){
    		    	    for(var i=0;i<newflowDIVInputs.length;i++){
    		    	    	if(newflowDIVInputs[i].type=="checkbox"){
    		    	    		newflowDIVInputs[i].disabled = false;
    		    	    	}
    		    	    }
    		        }
    		    	doNodeBranchParse();
    		    	return "True";
    		    }
    		}
    	}
    }
}

function setValue(inputs){
	if(inputs){
		var isSelect=false;
		for(var i=0;i<inputs.length;i++){
		   	if(inputs[i].type=="checkbox"){
		   		if(inputs[i].checked){
		   			isSelect=true;
		   			break;
		   		}		   		
		   	}
		}
		/*
		for(var i=0;i<inputs.length;i++){
		   	if(inputs[i].type=="hidden"){
		   		if(inputs[i].fromIsInform=="true"){
		   			if(isSelect){
		   				 if(inputs[i] && inputs[i].validates){
							inputs[i].validate ='';
						 }
		   			}else{
		   				 if(inputs[i] && inputs[i].validates){
							inputs[i].validate = inputs[i].validates;
						 }
		   			}
		   		}		   		
		   	}
		}
		*/
	}
}

function selectPeople(singleOrMany, nodeId){
    currentNodeId = nodeId;
    eval("elements_node" + singleOrMany + " = selectPeopleElements['" + currentNodeId + "']")
    eval("selectPeopleFun_node" + singleOrMany + "();");
}

function doSelectPeople(elements){
    if(elements){
        document.getElementById(currentNodeId).value = getIdsString(elements, false);
        document.getElementById(currentNodeId + "Name").value = getNamesString(elements);
        
        var elementsNew = new Array();
        for(var i = 0; i < elements.length; i++){
	        var e = elements[i];
        	elementsNew[i] = new Element(e.type, e.id, e.name, e.typeName, e.accountId, e.accountShortname, e.description)
        }
       	selectPeopleElements[currentNodeId] = elementsNew;
    }
}

var lastSelectedIds = "";
function selectMatchPeople(nodeId){
    try {
		var _ids = document.getElementById(nodeId + "pId").value;
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "initMatchPeople", false, "POST");
		requestCaller.addParameter(1, "String", _ids);
		requestCaller.addParameter(2, "String", nodeId);
		requestCaller.serviceRequest();
	}catch (ex1) {
		alert(ex1.message)
	}
    function toArray(object){
    	var newobject = [];
    	for(var i = 0; i < object.length; i++) {
    		newobject[i] = object[i];
    	}
    	return newobject;
    }
	//是否是ipad
	if(v3x.getBrowserFlag('OpenDivWindow')){
		var rv = v3x.openWindow({
			url: colWorkFlowURL + "?method=preMatchPeople&nodeId="+nodeId,
	        height: 450,
	        width: 350,
	        dialogType:"modal"
	    });

	    if(!rv){
	    	return;
	    }
	    var userIdsStr = "";
	    var userNamesStr = "";
	    var userIdArr = toArray(rv[0]);
	    var userNameArr = toArray(rv[1]);
	    for(var i=0; i<userIdArr.length; i++){
	    	userIdsStr += userIdArr[i];
	    	userNamesStr += userNameArr[i];
	    	if(i < userIdArr.length-1){
		    	userIdsStr += ",";
	    		userNamesStr += "、";
	    	}
	    }
	    lastSelectedIds = userIdsStr;
	    document.getElementById(nodeId).value = userIdsStr;
		document.getElementById(nodeId + "Name").title = userNamesStr;
		document.getElementById(nodeId + "Name").value = userNamesStr;
	}else{
		var rv_win = v3x.openDialog({
	    	id:"rv_win_select",
	    	title:'',
	    	url :colWorkFlowURL + "?method=preMatchPeople&nodeId="+nodeId,
	    	width: 350,
	        height: 400,
	        buttons:[{
				id:'rv_win_select_btn1',
	            text: v3x.getMessage("collaborationLang.submit"),
	            handler: function(){    	        	
	        		var returnValues = rv_win.getReturnValue();
	        	    if(returnValues==false){
	        	    	rv_win.close();
	        	    }
	        	    var userIdsStr = "";
	        	    var userNamesStr = "";
	        	    var userIdArr = toArray(returnValues[0]);
	        	    var userNameArr = toArray(returnValues[1]);
	        	    for(var i=0; i<userIdArr.length; i++){
	        	    	userIdsStr += userIdArr[i];
	        	    	userNamesStr += userNameArr[i];
	        	    	if(i < userIdArr.length-1){
	        		    	userIdsStr += ",";
	        	    		userNamesStr += "、";
	        	    	}
	        	    }
	        	    lastSelectedIds = userIdsStr;
	        		document.getElementById(nodeId).setAttribute('value',userIdsStr);
	        		document.getElementById(nodeId + "Name").title = userNamesStr;
	        	    document.getElementById(nodeId + "Name").setAttribute('value',userNamesStr);
	        		rv_win.close();
            	}
	        }, {
				id:'rv_win_select_btn2',
	            text: v3x.getMessage("collaborationLang.cancel"),
	            handler: function(){
	        		rv_win.close();
	        	}
	        }]
	    });
	}

}
function setValidate(source, id){
	var inputId = document.getElementById(id);
	var inputName = document.getElementById(id+"Name");
	if(source){
		if(source.checked){
			//inputName可能为null，则inputName.getAttribute('validates');报js错误
			var validates_name = null;
			if(inputName){
				validates_name = inputName.getAttribute('validates');
			}
			var validates_id = null;
			if(inputId){
				validates_id= inputId.getAttribute('validates');
			}
			if(inputName && validates_name){
				//inputName.validate = inputName.validates;
				inputName.setAttribute('validate',validates_name);
			}else if(inputId && validates_id){
				//inputId.validate = inputId.validates;
				inputId.setAttribute('validate',validates_id);
			}
		}else{
			if(inputName){
				//inputName.validate = '';
				inputName.setAttribute('validate','');
			}else if(inputId){
				//inputId.validate = '';
				inputId.setAttribute('validate','');
			}
		}
	}
}

<%--解决其它浏览器下select返回不能设值的问题--%>
function setSelectValue(selectObj, id){
	document.getElementById(id).value = selectObj.value;
}
//-->
</script>
</head>
<body scroll="no" onkeydown="listenerKeyESC()">
<form name="form1" name="form1"  method="post" id="form1">
<input type="hidden" id="process_xml" name="process_xml" value=""/>
<input type="hidden" id="process_desc_by" name="process_desc_by" value="" />
<input type="hidden" id="currentNodeId" name="currentNodeId" value="" />
<input type="hidden" id="affair_id" name="affair_id" value=""/>
<input type="hidden" name="isFromTemplate" id="isFromTemplate" value="" />
<input type="hidden" id="formData" name="formData" value="">
<input type="hidden" id="appName" name="appName" value=""/>
<input type="hidden" name="processId" id="processId" value="" />
<input type="hidden" id="isMatch" name="isMatch" value="true" />
<input type="hidden" name="caseId" id="caseId" value="" />
<input type="hidden" name="popFlag1" id="popFlag1" value="" />
<input type="hidden" name="informNodes" id="informNodes" value="" />
<input type="hidden" name="allSelectNodes" id="allSelectNodes" value="" />
<input type="hidden" name="allSelectInformNodes" id="allSelectInformNodes" value="" />
<input type="hidden" name="allNotSelectNodes" id="allNotSelectNodes" value="" />
<input type="hidden" name="secretLevel" id="secretLevel" value="${param.secretLevel}" />
<%-- 在隐藏域中保存有新流程的标志 --%>
<input type="hidden" id="hasNewflow" name="hasNewflow" value="false">
 <%-- post提交的标示，先写死，后续动态 --%>
<input type="hidden" id="__ActionToken" name="__ActionToken" readonly value="SEEYON_A8" >
<%--
<table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" class="PopupTitle"><fmt:message key="common.node.select.people.label" bundle="${v3xCommonI18N}" /><c:if test="${param.hasNewflow eq 'true'}">/ <fmt:message key="newflow.label" /></c:if>
		</td>
	</tr>
	<tr>
		<td class="bg-advance-middel" >
            <div id="div1" class="scrollList" width="100%">	
            </div>
        </td>
	</tr>
	<c:if test="${v3x:getBrowserFlagByRequest('OpenDivWindow', pageContext.request)}">
		<tr>
			<td height="42" align="center" class="bg-advance-bottom">
				<input type="button" onclick="ok()" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">&nbsp;&nbsp;
				<input type="button" onclick="window.close()" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
			</td>
		</tr>
	</c:if>
</table>
 --%>
<div id="divPop"></div>
<span id="people" style="display:none;"></span>
</form>
</body>
</html>
<script>
$(document).ready(function(){
	//开始加载页面内容
	_doOnLoad(); 
});
</script>