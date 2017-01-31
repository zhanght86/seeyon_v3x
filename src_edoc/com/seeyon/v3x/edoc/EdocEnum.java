package com.seeyon.v3x.edoc;

import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;

public class EdocEnum {
	//公文类型枚举，收文，发文
	static public enum edocType{sendEdoc,recEdoc,signReport, distributeEdoc, edocRegister, archiveModifyEdoc};
    public static enum SendType {
        normal,
        resend,
        forward
    };
    //公文文号，内部文号
    public static enum MarkType{edocMark,edocInMark};
    public static enum MarkCategory{docMark,docMark2,serialNo} 
    static public int getTempleteCategory(int iEdocType)
    {
    	if(iEdocType==edocType.sendEdoc.ordinal())
    	{
    		return TempleteCategory.TYPE.edoc_send.ordinal();
    	}
    	else if(iEdocType==edocType.recEdoc.ordinal())
    	{
    		return TempleteCategory.TYPE.edoc_rec.ordinal();
    	}
    	else if(iEdocType==edocType.signReport.ordinal())
    	{
    		return TempleteCategory.TYPE.sginReport.ordinal();
    	}
    	return -1;
    }
    
    
    static public int getEdocTypeByTemplateCategory(int templateCategory)
    {
    	if(templateCategory==TempleteCategory.TYPE.edoc_send.ordinal())
    	{
    		return edocType.sendEdoc.ordinal();
    	}
    	else if(templateCategory==TempleteCategory.TYPE.edoc_rec.ordinal())
    	{
    		return edocType.recEdoc.ordinal();
    	}
    	else if(templateCategory==TempleteCategory.TYPE.sginReport.ordinal())
    	{
    		return edocType.signReport.ordinal();
    	}
    	return -1;
    }
    
    
    
    static public int getStartAccessId(int iEdocType)
    {
    	if(iEdocType==edocType.sendEdoc.ordinal())
    	{
    		return 3000;
    	}
    	else if(iEdocType==edocType.recEdoc.ordinal())
    	{
    		return 3100;
    	} 
    	return -1;
    }
    /**
     * 画流程图的时候,根据应用类型选择节点权限
     * @param iEdocType:1收文,0发文
     * @return
     */
    static public String getEdocAppName(int iEdocType)
    {
    	edocType [] values=edocType.values();
    	if(iEdocType<0 || iEdocType>values.length){return "";}
    	return edocType.values()[iEdocType].name();    	
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
