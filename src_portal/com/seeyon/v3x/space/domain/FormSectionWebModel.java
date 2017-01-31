package com.seeyon.v3x.space.domain;

import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author xgghen
 *
 */
public class FormSectionWebModel  {
	
	private String id ;
	
	private String name ;
	
	private Long cartyId ;
	
	private Long refAppformMainId ;
	
	private SectionType sectionType ;
	
	private String singleBoardId ;
	
	private String sectionName ;
	
	public final static String  QUERTSECTION = "singleBoardformqueryResultSection";
	public final static String  REPORT_TABLE_SECTION = "singleBoardFormReportResultTableSection";
	public final static String  REPORT_CHART_SECTION = "singleBoardFormReportResultChartSection";
	
	public String getSectionName() {
		if(sectionType != null){
			if(SectionType.FormQuery.equals(sectionType) || SectionType.FormQueryMyPlan.equals(sectionType)){
				return QUERTSECTION ;
			}else if(SectionType.FormReportMyPlan.equals(sectionType) || SectionType.FormReport.equals(sectionType)){
				String boardId = this.getSingleBoardId();
				boolean chart = Strings.isNotBlank(boardId) && boardId.indexOf(',' + ReportShowType.Picture.name() + ',') != -1;
				return chart ? REPORT_CHART_SECTION : REPORT_TABLE_SECTION;
			}
		}
		return null ;
	}

	public String getSingleBoardId() {

		
		if(this.refAppformMainId != null && Strings.isNotBlank(id)){
			if(SectionType.FormQuery.equals(sectionType) || SectionType.FormQueryMyPlan.equals(sectionType)){
				return this.id  + "," + this.refAppformMainId ;
			}else{
				return this.id  + "," + this.refAppformMainId + "," + this.reportShowType + "," + this.reportChartInfoName ;
			}
			
		}
		return null ;		
	}


	public FormSectionWebModel(String id,String name,Long cartyId ,Long refAppformMainId ){
		this.id = id ;
		this.name = name ;
		this.cartyId = cartyId ;
		this.refAppformMainId = refAppformMainId ;
	}
	
	public FormSectionWebModel(String id,String name,Long cartyId ,Long refAppformMainId ,SectionType sectionType ){
		this.id = id ;
		this.name = name ;
		this.cartyId = cartyId ;
		this.refAppformMainId = refAppformMainId ;
		this.sectionType = sectionType ;
	}
	
	public SectionType getSectionType() {
		return sectionType;
	}

	public void setSectionType(SectionType sectionType) {
		this.sectionType = sectionType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {		
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCartyId() {
		return cartyId;
	}

	public void setCartyId(Long cartyId) {
		this.cartyId = cartyId;
	}

	public Long getRefAppformMainId() {
		return refAppformMainId;
	}

	public void setRefAppformMainId(Long refAppformMainId) {
		this.refAppformMainId = refAppformMainId;
	}
	
	@Override
	public boolean equals(Object obj){		
		if(obj != null && obj instanceof FormSectionWebModel){
			FormSectionWebModel formSectionWebModel = (FormSectionWebModel)obj ;
			if(formSectionWebModel.getId() != null 
					&& formSectionWebModel.getCartyId() != null
					&& formSectionWebModel.getName() != null 
					&& formSectionWebModel.getSectionType() != null 
					&& formSectionWebModel.getRefAppformMainId() != null 
					&& formSectionWebModel.getId().equals(this.id) 
					&& formSectionWebModel.getName().equals(this.name) 
					&& formSectionWebModel.getCartyId().equals(this.cartyId)
					&& formSectionWebModel.getRefAppformMainId().equals(this.refAppformMainId)
					&& formSectionWebModel.getSectionType().equals(sectionType))
				/**
				if(this.reportShowType != null && this.reportShowType.equals(formSectionWebModel.getReportShowType())){
					return true ;
				}else{
					return false ;
				}
				***/
			return true ;
		}
		
		return false ;
	}
	
	@Override
	public int hashCode(){
		return refAppformMainId.hashCode() ;
	}
	
	private ReportShowType reportShowType = ReportShowType.Talbe ;
	
	public ReportShowType getReportShowType() {
		return reportShowType;
	}

	public void setReportShowType(ReportShowType reportShowType) {
		this.reportShowType = reportShowType;
	}
	
	private String reportChartInfoName = "" ;

	
	public String getReportChartInfoName() {
		return reportChartInfoName;
	}

	public void setReportChartInfoName(String reportChartInfoName) {
		this.reportChartInfoName = reportChartInfoName;
	}

	/**
	 * 表单统计的展现结果
	 * @author xgghen
	 *
	 */
	public enum ReportShowType{
		Talbe,
		Picture, 
		;
	}
	
	/**
	 * 
	 * @author xgghen
	 *
	 */
	public enum SectionType{
		
		
		FormQuery(1),
		FormReport(2),
		FormQueryMyPlan(3),
		FormReportMyPlan(4),
		;
		
		private int key ;
		
		SectionType( int key){
			this.key = key ;
		}
		

	    public int getKey() {
	        return this.key;
	    }
	    
	    public static SectionType getSectionType( int key){
	    	SectionType[] sectionTypes = SectionType.values() ;
	    	if(sectionTypes != null){
	    		for(SectionType sectionType : sectionTypes){
	    			if(sectionType.getKey() == key){
	    				return sectionType ;
	    			}
	    		}
	    	}
	    	return null;
	    }
	}
}
