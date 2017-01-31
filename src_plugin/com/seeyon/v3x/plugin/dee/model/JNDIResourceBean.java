package com.seeyon.v3x.plugin.dee.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;
import com.seeyon.v3x.dee.common.db.resource.util.SourceUtil;


/**
 * @author Zhang.Wei
 * @date Feb 3, 20125:26:29 PM
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
public class JNDIResourceBean implements DeeResource {
	private final static Log log = LogFactory.getLog(JNDIResourceBean.class);
    /** 数据源名称 */
    private String resource_name;

    /** 数据源描述 */
    private String resoutce_desc;

    /** JNDI */
    private String jndi;
    public JNDIResourceBean(){}
    public JNDIResourceBean(String xml) {
        try {
            jndi = SourceUtil.getValueFromXml(xml, "", "address");
        } catch(Exception e) {
			log.error(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toXML() {
        StringBuffer stringXml = new StringBuffer("<datasource name=\"" + resource_name + "\" class=\"com.seeyon.v3x.dee.datasource.JNDIDataSource\">");
        stringXml.append(" <property name=\"address\" value=\"" + jndi + "\"/>").append(" </datasource>");
        return stringXml.toString();
    }

	public String toXML(String name) {
		resource_name = name;
		return toXML();
	}

    /**
     * 获取resource_name
     * @return resource_name
     */
    public String getResource_name() {
        return resource_name;
    }

    /**
     * 设置resource_name
     * @param resource_name resource_name
     */
    public void setResource_name(String resource_name) {
        this.resource_name = resource_name;
    }

    /**
     * 获取resoutce_desc
     * @return resoutce_desc
     */
    public String getResoutce_desc() {
        return resoutce_desc;
    }

    /**
     * 设置resoutce_desc
     * @param resoutce_desc resoutce_desc
     */
    public void setResoutce_desc(String resoutce_desc) {
        this.resoutce_desc = resoutce_desc;
    }

    /**
     * 获取jndi
     * @return jndi
     */
    public String getJndi() {
        return jndi;
    }

    /**
     * 设置jndi
     * @param jndi jndi
     */
    public void setJndi(String jndi) {
        this.jndi = jndi;
    }
}
