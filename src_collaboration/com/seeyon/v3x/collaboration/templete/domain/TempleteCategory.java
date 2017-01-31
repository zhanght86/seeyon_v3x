package com.seeyon.v3x.collaboration.templete.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the v3x_templete_category database table.
 * 
 * @author BEA Workshop Studio
 */
public class TempleteCategory extends com.seeyon.v3x.common.domain.BaseModel
		implements Serializable, Comparable<TempleteCategory> {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		TempleteCategory other = (TempleteCategory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;
		return true;
	}

	private static final long serialVersionUID = 3309295353680276297L;
	
	/**
	 * 模板分类的类型：协同模板
	 * 按照枚举的ordinal()值写入数据库
	 * 如：templeteCategory.setType(TYPE.collaboration_templete.ordinal())
	 *
	 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
	 * @version 1.0 2007-3-28
	 */
	public static enum TYPE {
		collaboration_templete, //协同模板
		edoc, //公文模板
		edoc_send, //发文模板 
		edoc_rec, //收文模板
		form,      //表单模板
		sginReport //签报模版
	}
	

	public static final String ENTITY_NAME = TempleteCategory.class.getCanonicalName();
	
	public static final String PROP_createDate = "createDate";

	public static final String PROP_description = "description";

	public static final String PROP_name = "name";

	public static final String PROP_orgAccountId = "orgAccountId";

	public static final String PROP_parentId = "parentId";

	public static final String PROP_sort = "sort";

	public static final String PROP_type = "type";

	private java.sql.Timestamp createDate;

	private String description;

	private String name;

	private Long orgAccountId;

	private Long parentId;

	private Integer sort = 9999;

	private Integer type = TYPE.collaboration_templete.ordinal();
	
	private java.util.Set<TempleteAuth> categoryAuths;
	
	private Set<Templete> templetes;
	
	public Set<Templete> getTempletes() {
		return templetes;
	}

	public void setTempletes(Set<Templete> templetes) {
		this.templetes = templetes;
	}

	public void addTemplete(Templete templete){
		if(this.templetes == null){
			templetes = new HashSet<Templete>();
		}
		templetes.add(templete);
	}
	
	public TempleteCategory() {
	}

	public java.sql.Timestamp getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getOrgAccountId() {
		return this.orgAccountId;
	}

	public void setOrgAccountId(Long orgAccountId) {
		this.orgAccountId = orgAccountId;
	}

	public Long getParentId() {
		return this.parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public java.util.Set<TempleteAuth> getCategoryAuths() {
		if(this.categoryAuths == null){
			this.categoryAuths = new HashSet<TempleteAuth>();
		}
		
		return categoryAuths;
	}

	public void setCategoryAuths(java.util.Set<TempleteAuth> categoryAuths) {
		this.categoryAuths = categoryAuths;
	}
	
	public boolean isCanManager(long memberId, long loginAccountId){
		Set<TempleteAuth> a = this.getCategoryAuths();
		for (TempleteAuth auth : a) {
			if(auth.getAuthId().longValue() == memberId 
					&& (this.getOrgAccountId() != null && this.getOrgAccountId().longValue() == loginAccountId)){
				return true;
			}
		}
		
		return false;
	}

	public int compareTo(TempleteCategory category) {
	    if(category != null){
            if((this.getParentId() != null && this.getParentId() <= 4L) && category.getParentId() != null && category.getParentId() >4){
                return -1;
            }
            if((this.getParentId() != null && this.getParentId() > 4L) && category.getParentId() != null && category.getParentId() <=4){
                return 1;
            }
            if(this.getSort() != null && category.getSort() != null){
                if(this.getSort().equals(category.getSort())){
                    if(this.getCreateDate()==null && category.getCreateDate()==null) {
                        return 0;
                    } else if(this.getCreateDate()==null && category.getCreateDate()!=null) {
                        return 1;
                    } else if(this.getCreateDate()!=null && category.getCreateDate()==null) {
                        return -1;
                    } else {        
                        if(this.getCreateDate().getTime()>category.getCreateDate().getTime()) {
                            return 1;
                        } else if(this.getCreateDate().getTime()<category.getCreateDate().getTime()) {
                            return -1;
                        }
                    }
                }else{
                    return this.getSort().compareTo(category.getSort());
                }
            }
        }
        return 0;
	}

}