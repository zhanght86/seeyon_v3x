package com.seeyon.v3x.cluster.beans;

import java.io.Serializable;
import java.util.List;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.util.Constants.OperEnum;

public class NotificationDocMetadataDefinition  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4031225421639867083L;
	
	private OperEnum oper;
	private List<DocMetadataDefinition> defs ;
	
	public NotificationDocMetadataDefinition(){}
	
	public NotificationDocMetadataDefinition(OperEnum oper ,List<DocMetadataDefinition> defs){
		this.oper = oper ;
		this.defs = defs ;
	}
	
	public NotificationDocMetadataDefinition(List<DocMetadataDefinition> defs){		
		this.defs = defs ;
	}
	
	public List<DocMetadataDefinition> getDefs() {
		return defs;
	}
	public void setDefs(List<DocMetadataDefinition> defs) {
		this.defs = defs;
	}
	public OperEnum getOper() {
		return oper;
	}
	public void setOper(OperEnum oper) {
		this.oper = oper;
	}

	public String toString(NotificationDocMetadataDefinition bean){
		StringBuffer str = new StringBuffer() ;
		for(DocMetadataDefinition docMetadataDefinition : defs){
			str.append( "DocMetadataDefinition name = "+docMetadataDefinition.getName() + "     " ) ;
			str.append("\n") ;
		}
		return "NotificationDocMetadataDefinition [oper=" + oper +"defs的长度=" + defs.size() + "  " + str.toString()
		+ "]";
	}
	
}
