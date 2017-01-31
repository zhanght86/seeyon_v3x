/**
 * NewflowSetting.java
 * Created on 2009-6-18
 */
package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 分段流程 - 新流程发起设置
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 */
public class NewflowSetting extends BaseModel implements Serializable
{
    private static final long serialVersionUID = 2731121136704341786L;
    private Long templeteId;
    private String nodeId;
    private Long newflowTempleteId;
    private String newflowSender;
    private String triggerCondition;
    private String conditionTitle;
    private String conditionBase;    
    private Boolean isForce;
    private int flowRelateType;
    private Boolean isCanViewMainFlow;
    private Boolean isCanViewByMainFlow;
    private Date createTime;
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public int getFlowRelateType() {
        return flowRelateType;
    }
    public void setFlowRelateType(int flowRelateType) {
        this.flowRelateType = flowRelateType;
    }
    public Boolean getIsCanViewByMainFlow() {
        return isCanViewByMainFlow;
    }
    public void setIsCanViewByMainFlow(Boolean isCanViewByMainFlow) {
        this.isCanViewByMainFlow = isCanViewByMainFlow;
    }
    public Boolean getIsCanViewMainFlow() {
        return isCanViewMainFlow;
    }
    public void setIsCanViewMainFlow(Boolean isCanViewMainFlow) {
        this.isCanViewMainFlow = isCanViewMainFlow;
    }
    public Boolean getIsForce() {
        return isForce;
    }
    public void setIsForce(Boolean isForce) {
        this.isForce = isForce;
    }
    public String getNewflowSender() {
        return newflowSender;
    }
    public void setNewflowSender(String newflowSender) {
        this.newflowSender = newflowSender;
    }
    public Long getNewflowTempleteId() {
        return newflowTempleteId;
    }
    public void setNewflowTempleteId(Long newflowTempleteId) {
        this.newflowTempleteId = newflowTempleteId;
    }
    public String getNodeId() {
        return nodeId;
    }
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    public Long getTempleteId() {
        return templeteId;
    }
    public void setTempleteId(Long templeteId) {
        this.templeteId = templeteId;
    }
    public String getTriggerCondition() {
        return triggerCondition;
    }
    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }
    public String getConditionBase() {
        return conditionBase;
    }
    public void setConditionBase(String conditionBase) {
        this.conditionBase = conditionBase;
    }
    public String getConditionTitle() {
        return conditionTitle;
    }
    public void setConditionTitle(String conditionTitle) {
        this.conditionTitle = conditionTitle;
    }
}
