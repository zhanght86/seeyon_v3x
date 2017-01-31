	// 进行编辑
	function showEdit1(){
		document.getElementById("submitOk").style.display= "";
		document.getElementById("formerpassword").disabled="";
		document.getElementById("nowpassword").disabled="";
		document.getElementById("validatepass").disabled="";
	}
	// 取消编辑
	function notEdit1(){
		try{
		document.getElementById("submitOk").style.display="none";
		document.forms['postForm'].reset();//去掉填写的内容
		document.getElementById("formerpassword").disabled=true;
		document.getElementById("nowpassword").disabled=true;
		document.getElementById("validatepass").disabled=true;
		myBar.enabled("editBtn");
		}
		catch(e){}
	}
	function editData(){
		document.getElementById("submitOk").style.display="block";
		document.forms['postForm'].reset();//去掉填写的内容
		document.getElementById("formerpassword").disabled=false;
		document.getElementById("nowpassword").disabled=false;
		document.getElementById("validatepass").disabled=false;
		//myBar.disabled("editBtn");
	}

	// 验证密码相同否
	function validate1(){
		var oldpasword = document.getElementById("nowpassword").value;
		var validatepassword = document.getElementById("validatepass").value;
		if (oldpasword == validatepassword){
			return true;
		} else {		
			alert(sameOrNot);
			document.getElementById("validatepass").value = "";
			return false;
		}
	}
	
	// 验证原密码
	function validateOldPassword1(){
		var oldPassword = document.getElementById("formerpassword").value;
		var individualName    = document.getElementById("individualName").value;
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxOrgManagerDirect", "isOldPasswordCorrect", false);
		requestCaller.addParameter(1, "String", oldPassword);
		requestCaller.addParameter(2, "String", individualName);
		var ds = requestCaller.serviceRequest();
		if(ds=="true"){
			return true;
		}else{
			alert(oldPasswordMsg);
			return false;
		}
	}
	
	//验证密码强度     174工程     汪成平
	function verifyPwdStrength(){
		var password =  document.getElementById("nowpassword").value;
		var score = 0;
		var tmpString = "密码强度不够";
		 if (password.match(/(.*[0-9])/)){ score += 5;}else{tmpString += "，至少包含一个数字";}
		 if (password.match(/(.*[!,@,#,$,%,^,&,*,?,_,~])/)){ score += 5 ;}else{tmpString += "，至少包含一个特殊字符";}
		 if (password.match(/(.*[a-z])/)){ score += 5;}else{tmpString += "，至少包含一个小写字母";}
		 if (password.match(/(.*[A-Z])/)){ score += 5;}else{tmpString += "，至少包含一个大写字母";}
		 if (password.match(/([a-zA-Z])/) && password.match(/([0-9])/)){ score += 15;}
		 if (password.match(/([!,@,#,$,%,^,&,*,?,_,~])/) && password.match(/([0-9])/)){ score += 15;}
		 if (password.match(/([!,@,#,$,%,^,&,*,?,_,~])/) && password.match(/([a-zA-Z])/)){score += 15;}
		 if (password.match(/(\w)*(\w)\2{2}(\w)*/)){ score -= 10;tmpString += "，不能包含3个连续相同字符";}
		 if(score>=65){
			 return true;
		 }else{
				alert(tmpString);
				return false;
		 }
	}