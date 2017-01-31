package com.seeyon.v3x.collaboration.domain;

/**
 * User: lius
 * Date: 2007-2-7
 * Time: 18:05:28
 */
public class SeeyonPolicy {
    //协同策略
    public static SeeyonPolicy SEEYON_POLICY_COLLABORATE = new SeeyonPolicy("collaboration","协同");

    public static String DEFAULT_POLICY = "collaboration";
    
    private String id;
    private String name;

    public SeeyonPolicy() {
		
	}

    public SeeyonPolicy(SeeyonPolicy p){
        this(p.getId(),p.getName());
    }
    
    public SeeyonPolicy(String id,String name){
        this.id = id;
        this.name = name;
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


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeeyonPolicy that = (SeeyonPolicy) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
