/**
 * 
 */
package com.seeyon.v3x.space.domain;

import java.util.List;

import com.seeyon.v3x.space.Constants;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-9-6
 */
public class SpaceModel extends com.seeyon.v3x.common.ObjectToXMLBase implements Comparable<SpaceModel> {
	private Long id;

	private String name;

	private String spacePath;

	//private String banner;

	private String slogan;

	private String spaceName;

    private Constants.SpaceType type; 
    
    private Constants.SpaceState state;
    
    private List<SpaceSecurity> spaceSecurity;
    
    private boolean isAllowDefined = false;

	private boolean spaceMenuEnabled = false;
	
	private Long parentId;

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public boolean isSpaceMenuEnabled() {
		return spaceMenuEnabled;
	}

	public void setSpaceMenuEnabled(boolean spaceMenuEnabled) {
		this.spaceMenuEnabled = spaceMenuEnabled;
	}

	public boolean isAllowDefined() {
		return isAllowDefined;
	}

	public void setAllowDefined(boolean isAllowDefined) {
		this.isAllowDefined = isAllowDefined;
	}

	private Long entityId;
    
    /** 排序号，等同于部门排序号 */
    private Integer sortId;
    
    
	public Integer getSortId() {
		return sortId;
	}
	
	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}

	public boolean isSystem() {
		return Constants.isSystem(this.type);
	}

	public SpaceModel(Long id, String name, String spacePath, String slogan, String spaceName, Constants.SpaceType type, Constants.SpaceState state, List<SpaceSecurity> spaceSecurity) {
		super();
		this.id = id;
		this.name = name;
		this.spacePath = spacePath;
        this.slogan = slogan;
		this.spaceName = spaceName;
        this.type = type;
        this.state = state;
        this.spaceSecurity = spaceSecurity;
	}
	public SpaceModel(Long id, String name, String spacePath, String slogan, String spaceName, Constants.SpaceType type, Constants.SpaceState state, List<SpaceSecurity> spaceSecurity,boolean isAllowDefined) {
		super();
		this.id = id;
		this.name = name;
		this.spacePath = spacePath;
        this.slogan = slogan;
		this.spaceName = spaceName;
        this.type = type;
        this.state = state;
        this.spaceSecurity = spaceSecurity;
        this.isAllowDefined = isAllowDefined;
	}
	public SpaceModel(Long id, String name, String spacePath, String slogan, String spaceName, Constants.SpaceType type, Constants.SpaceState state, List<SpaceSecurity> spaceSecurity,boolean isAllowDefined,Long parentId) {
		super();
		this.id = id;
		this.name = name;
		this.spacePath = spacePath;
        this.slogan = slogan;
		this.spaceName = spaceName;
        this.type = type;
        this.state = state;
        this.spaceSecurity = spaceSecurity;
        this.isAllowDefined = isAllowDefined;
        this.parentId = parentId;
	}
	public SpaceModel(Long id, String name, String spacePath, String slogan, 
			String spaceName, Constants.SpaceType type, Constants.SpaceState state, 
			List<SpaceSecurity> spaceSecurity, Integer sortId) {
		super();
		this.id = id;
		this.name = name;
		this.spacePath = spacePath;
        this.slogan = slogan;
		this.spaceName = spaceName;
        this.type = type;
        this.state = state;
        this.spaceSecurity = spaceSecurity;
        this.sortId = sortId;
	}
	public SpaceModel(Long id, String name, String spacePath, String slogan, 
			String spaceName, Constants.SpaceType type, Constants.SpaceState state, 
			List<SpaceSecurity> spaceSecurity, Integer sortId,Long entityId) {
		this(id,name,spacePath,slogan,spaceName,type,state,spaceSecurity,sortId);
		this.entityId = entityId;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public String getSpacePath() {
		return spacePath;
	}

	public void setSpacePath(String spacePath) {
		this.spacePath = spacePath;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}

    
    public Constants.SpaceType getType() {
        return type;
    }

    
    public void setType(Constants.SpaceType type) {
        this.type = type;
    }

    public Constants.SpaceState getState() {
        return state;
    }

    public void setState(Constants.SpaceState state) {
        this.state = state;
    }

    public List<SpaceSecurity> getSpaceSecurity() {
        return spaceSecurity;
    }

    public void setSpaceSecurity(List<SpaceSecurity> spaceSecurity) {
        this.spaceSecurity = spaceSecurity;
    }

	public int compareTo(SpaceModel o) {
		return this.getSortId().compareTo(o.getSortId());
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

}
