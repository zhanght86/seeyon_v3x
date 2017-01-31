package com.seeyon.v3x.plugin.dee.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.seeyon.v3x.dee.common.base.annotation.Column;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;

/**
 * @author dkywolf
 * @date 20120514
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class WSCommonWriter implements DeeResource {
	private final static Log log = LogFactory.getLog(WSCommonWriter.class);
    /** 接口名称 */
    private String interfaceName;

    /** xmlns */
    private String xmlns;

    /** 方法名称 */
    private String methodName;

    /** 参数map */
    private Map<String, String> map;

    /** a8WS登录名 */
    private String userName;

    /** 密码 */
    private String password;

    /** url */
    private String a8url;
    public WSCommonWriter(){}
    public WSCommonWriter(String xml) {
        xml="<a8>"+xml+"</a8>";        
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            List<Element> adapterElement = rootElt.element("adapter").elements("property");
            for(Element e:adapterElement){
                if("interfaceName".equals(e.attribute("name").getValue())) {
                    interfaceName= e.attribute("value").getValue();
                }else if("xmlns".equals(e.attribute("name").getValue())) {
                    xmlns= e.attribute("value").getValue();
                }else if("methodName".equals(e.attribute("name").getValue())) {
                    methodName= e.attribute("value").getValue();
                }else if("userName".equals(e.attribute("name").getValue())) {
                	userName= e.attribute("value").getValue();
	            }else if("password".equals(e.attribute("name").getValue())) {
	            	password= e.attribute("value").getValue();
		        }else if("a8url".equals(e.attribute("name").getValue())) {
		        	a8url= e.attribute("value").getValue();
		        }
            }
//            dataSource = dataSourceElement.attributeValue("ref");
            List<Element> sqlIter = rootElt.element("adapter").element("map").elements("key");
            map = new LinkedHashMap<String, String>();
            for (Element sqlElement : sqlIter) {
                map.put(sqlElement.attributeValue("name"),
                        sqlElement.attributeValue("value"));
            }
        } catch (Exception e) {
			log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取userName
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置userName
     * @param userName userName
     */
    @Column(name = "USERNAME")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取password
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置password
     * @param password password
     */
    @Column(name = "PASSWORD")
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取a8url
     * @return a8url
     */
    public String getA8url() {
        return a8url;
    }

    /**
     * 设置a8url
     * @param a8url a8url
     */
    @Column(name = "A8URL")
    public void setA8url(String a8url) {
        this.a8url = a8url;
    }
    
	public String toXML(String name) {
		// 生成WSCommonWriter时，会生成一条processor记录，将其放到一起。
        StringBuffer sb = new StringBuffer("");
        sb.append("<adapter class=\"com.seeyon.v3x.dee.extend.A8CommonWSWriter\" name=\""+name+"\"><description></description>")
			.append("<property name=\"interfaceName\" value=\"" + interfaceName + "\"/>")
			.append("<property name=\"xmlns\" value=\"" + xmlns + "\"/>")
			.append("<property name=\"methodName\" value=\"" + methodName + "\" />")
			.append("<property name=\"userName\" value=\"" + userName + "\"/>")
			.append("<property name=\"password\" value=\"" + password + "\"/>")
			.append("<property name=\"a8url\" value=\"" + a8url + "\"/>")
			.append("<map name=\"paraMap\">");
        for(Entry<String, String> entry : map.entrySet()) {
            sb.append("<key name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\"/>");
        }
        sb.append("</map></adapter>");
        return sb.toString();
	}

    /**
     * 获取interfaceName
     * @return interfaceName
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * 设置interfaceName
     * @param interfaceName interfaceName
     */
    @Column(name = "INTERFACENAME")
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * 获取xmlns
     * @return xmlns
     */
    public String getXmlns() {
        return xmlns;
    }

    /**
     * 设置xmlns
     * @param xmlns xmlns
     */
    @Column(name = "XMLNS")
    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    /**
     * 获取methodName
     * @return methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * 设置methodName
     * @param methodName methodName
     */
    @Column(name = "METHODNAME")
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * 获取map
     * @return map
     */
    public Map<String, String> getMap() {
        return map;
    }

    /**
     * 设置map
     * @param map map
     */
    public void setMap(Map<String, String> map) {
        this.map = map;
    }
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}
}
