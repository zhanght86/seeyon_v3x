package com.seeyon.v3x.plugin.dee.model;

import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;

public class ScriptBean implements DeeResource {

	/**
	 * 脚本内容
	 */
	private String script;
	private String scriptType = "groovy";


	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public ScriptBean() {
	}

	/**
	 * 例：<script><![CDATA[context.setAttribute(\"WSToken\",tokenStr); println tokenStr]]></script>
	 */
	public ScriptBean(String xml) {
		int beginIndex = xml.indexOf("<script>");
		int endIndex = xml.indexOf("</script>");
		if(beginIndex>-1){
			this.script = xml.substring(beginIndex + 17, endIndex - 3);
		}else{
			this.script = xml;
			this.scriptType = "xml";
		}
	}

	@Override
	public String toXML() {
		StringBuffer scriptInfo = new StringBuffer();
		if("xml".equals(scriptType)){
			scriptInfo.append(script);
		}else{
			scriptInfo.append("<script><![CDATA[");
			scriptInfo.append(script);
			scriptInfo.append("]]></script>");			
		}
		return scriptInfo.toString();
	}

	@Override
	public String toXML(String name) {
		return toXML();
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

}
