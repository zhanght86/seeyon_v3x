package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;

import com.seeyon.v3x.collaboration.templete.domain.Templete;

public class FormFlowTemplete implements Serializable {
	
	private String authName ;
	private Long id ;
	private String superVisName ;
	private String templeteName ;
	
	public String getTempleteName() {
		return templeteName;
	}
	public void setTempleteName(String templeteName) {
		this.templeteName = templeteName;
	}

	private Templete templete = null ;
	
	public String getAuthName() {
		return authName;
	}
	public void setAuthName(String authName) {
		this.authName = authName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSuperVisName() {
		return superVisName;
	}
	public void setSuperVisName(String superVisName) {
		this.superVisName = superVisName;
	}
	public FormFlowTemplete(){
		
	}
	
	public FormFlowTemplete(Templete templete){
		this.templete = templete ; 
	}

}
