<%@ page language="java" contentType="text/html;charset=UTF-8"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>添加人员</title>
<%@include file="../organizationHeader.jsp"%>

<script type="text/javascript">
	function validateTeamName(){   
		var orgLevelId = document.getElementById("orgLevelId").value;
		if(orgLevelId == ''){
	        alert(_("organizationLang.orgainzation_level_not_null"));
	        return false;		    
		}	        
		var memberNameValue = document.getElementsByName("loginName")[0].value;
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxOrgManagerDirect", "isPropertyDuplicated", false);
		requestCaller.addParameter(1, "String", "JetspeedPrincipal");
		requestCaller.addParameter(2, "String", "fullPath");
		requestCaller.addParameter(3, "String", memberNameValue);
		var isDbName = requestCaller.serviceRequest();
		if (isDbName=="true") {
			var requestCaller1 = new XMLHttpRequestCaller(this, "ajaxOrgManager", "isAdministrator", false);
			requestCaller1.addParameter(1, "String", memberNameValue);
			var isAdmin = requestCaller1.serviceRequest();
			if(isAdmin=="true"){
				var requestCaller4 = new XMLHttpRequestCaller(this, "ajaxOrgManager", "getAccountByLoginName", false);
				requestCaller4.addParameter(1, "String", memberNameValue);
				var accountBylonginName = requestCaller4.serviceRequest();
				var accountLongName = accountBylonginName.get("N");
				if(accountLongName!=null){
					alert(_("organizationLang.organization_member_login_account_name",accountBylonginName.get("N")));
					return false;
				}else{
					alert(_("organizationLang.organization_member_login_system_name"));
					return false;
				}
			}else{
				var requestCaller2 = new XMLHttpRequestCaller(this, "ajaxOrgManagerDirect", "toValidateName", false);
				requestCaller2.addParameter(1, "String", memberNameValue);
				var toValidateName = requestCaller2.serviceRequest();
				if(toValidateName!=null){
					var name = toValidateName[0];
					var accountId = toValidateName[1];
					var requestCaller3 = new XMLHttpRequestCaller(this, "ajaxOrgManagerDirect", "getAccountById", false);
					requestCaller3.addParameter(1, "Long", accountId);
					var account = requestCaller3.serviceRequest();
					if(account!=null){
						alert(_("organizationLang.organization_member_longin_name",account.get("N"),name));
						return false;
					}else{
					}
				}else{
				}
			} 
		} else {
			return true;
		}
	}
    function canceladdMemberForm(){
	  	parent.location.href = "${organizationURL}?method=organizationFrame&from=Member&deptAdmin=${param.deptAdmin}";
	}
	function setLoginName(){}


	/*
	身份证验证
*/
//--身份证号码验证-支持新的带x身份证
function isIdCardNo() 
{
	var num = document.getElementById("ID_card").value ;
    if(num==null || num==""){
       	return true;
    }  
    
    var factorArr = new Array(7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2,1);
    var error;
    var varArray = new Array();
    var intValue;
    var lngProduct = 0;
    var intCheckDigit;
    var intStrLen = num.length;
    var idNumber = num;    
    var aCity ={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:" 上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:" 湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:" 陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"};
    

    if ((intStrLen != 15) && (intStrLen != 18)) {       
        alert("输入身份证号码长度不对！");  
        return false;
    }    
    for(i=0;i<intStrLen;i++) {
        varArray[i] = idNumber.charAt(i);
        if ((varArray[i] < '0' || varArray[i] > '9') && (i != 17)) {           
            alert("错误的身份证号码！");
            return false;
        } else if (i < 17) {
            varArray[i] = varArray[i]*factorArr[i];
        }
    }
    if (intStrLen == 18) {    
    //地区判断
        idNumber=idNumber.replace(/x$/i,"a");
        //非法地区
        if(aCity[parseInt(idNumber.substr(0,2))]==null)
        {
        		alert("身份证输入的前6位不正确！")
                return false;
        }
    //生日判断
        var sBirthday=idNumber.substr(6,4)+"-"+Number(idNumber.substr(10,2))+"-"+Number(idNumber.substr(12,2));

        var d=new Date(sBirthday.replace(/-/g,"/"))
        
        //非法生日
        if(sBirthday!=(d.getFullYear()+"-"+ (d.getMonth()+1) + "-" + d.getDate()))
        {	
        		alert("身份证日期信息有误！");
                return false;
        }        
        for(i=0;i<17;i++) {
            lngProduct = lngProduct + varArray[i];
        }        
        intCheckDigit = 12 - lngProduct % 11;
        switch (intCheckDigit) {
            case 10:
                intCheckDigit = 'X';
                break;
            case 11:
                intCheckDigit = 0;
                break;
            case 12:
                intCheckDigit = 1;
                break;
        }        
        if (varArray[17].toUpperCase() != intCheckDigit) {
            alert("身份证效验位错误!...正确为： " + intCheckDigit + "!");
            return false;
        }
    } 
    else{        //length is 15        
        var date6 = idNumber.substring(6,12);
        if (checkDate(date6) == false) {
            alert("身份证日期信息有误！");
            return false;
        }
    }
   
    return true;
    
}
	
	//2017-01-11 诚佰公司
	function checkSecret(){
		var secretLevel = document.getElementById("secretLevel").value;
	    if(secretLevel==null || secretLevel==""){
	    	alert("涉密等级不能为空。")
	       	return false;
	    }
	    return true;
	}
	
</script>

</head>
<body scroll="no" style="overflow: no">
<c:set value="${(v3x:getSysFlagByName('sys_isGovVer')=='true')?'&& isIdCardNo()':''}" var="checkIdCard" />
<form id="memberForm" method="post" target="editMemberFrame" action="${organizationURL}?method=createMember&deptAdmin=${param.deptAdmin}" onsubmit="return (submitOrgForm(checkForm(this) && checkSecret() &&tirmElementById('member.loginName') &&  validateTeamName() && checkPassword()${checkIdCard},this))">
<c:set value="create" var="type" />
<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" align="center" class="">
<input type="hidden" name="deptAdmin" id="deptAdmin" value="${param.deptAdmin}">
<input type="hidden" name="id" id="id" value="${id}">
	<tr align="center">
		<td height="8" class="detail-top">
			<script type="text/javascript">
			getDetailPageBreak(); 
		</script>
		</td>
	</tr>	
	<tr>
		<td class="">
			<div class="scrollList" id="scrollListDiv">
			<%@include file="memberform.jsp"%>
			</div>		
		</td>
	</tr>
	<tr>
		<td height="42" align="center" class="bg-advance-bottom">
			<table width="100%" border="0">
			  <tr>
				<td width="20%" align="center">
					<label for="cont">
						<input id="cont" type="checkbox" name="cont" checked> <fmt:message key="continue.org"/>
					</label>
				</td>
				<td width="60%" align="center">
					<input id="submintButton" type="submit" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">&nbsp;
					<input id="submintCancel" type="button" onclick="window.location.href='<c:url value="/common/detail.jsp" />'" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
				</td>
				<td width="20%"></td>
			  </tr>
			</table>
		</td>
	</tr>
</table>
</form>
<iframe name="editMemberFrame" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
<script type="text/javascript">
document.getElementById("name").focus();
initIe10AutoScroll('scrollListDiv',60);
</script>
</body>
</html>