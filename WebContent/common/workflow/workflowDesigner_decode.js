
/** 
* 在js中调用此方法对flash中使用escape编码的参数数组进行统一解码,返回一个字符串string数组
*/
function decodeArgsFromFlash(args) {
    if(args && (typeof args == 'object') ){
        for(var i=0;i<args.length;i++){
            if(args[i]){
                args[i]= flashArgDecode(args[i]);
            }
        }
    }
    return args;
}

/**
* 在js中调用此方法对flash中使用escape编码的参数进行统一解码,返回一个字符串string
*/
function flashArgDecode(str) {
	return unescape(str);
}