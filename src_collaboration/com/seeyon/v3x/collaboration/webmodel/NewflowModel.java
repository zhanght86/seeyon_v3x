package com.seeyon.v3x.collaboration.webmodel;

import java.util.List;

import com.seeyon.v3x.workflow.event.WorkflowEventListener.PersonInfo;

/**
 *  新流程简化WebModel
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 */
public class NewflowModel{
    private Long id;
    private String templeteName;
    private String newflowSender;
    private String triggerCondition;
    //private String conditionBase;    
    private Boolean isForce = false;
    private List<PersonInfo> people;
    public NewflowModel(Long id, String templeteName, String newflowSender, String triggerCondition, Boolean isForce){
        this.id = id;
        this.templeteName = templeteName;
        this.newflowSender = newflowSender;
        this.triggerCondition = triggerCondition;
        //this.conditionBase = conditionBase;
        this.isForce = isForce;
    }
    public Long getId() {
        return id;
    }
    public String getNewflowSender() {
        return newflowSender;
    }
    public String getTempleteName() {
        return templeteName;
    }
    public Boolean getIsForce() {
        return isForce;
    }
    public String getTriggerCondition() {
        return triggerCondition;
    }
    //public String getConditionBase() {
    //    return conditionBase;
    //}
    public List<PersonInfo> getPeople() {
        return people;
    }
    public void setPeople(List<PersonInfo> people) {
        this.people = people;
    }
}
