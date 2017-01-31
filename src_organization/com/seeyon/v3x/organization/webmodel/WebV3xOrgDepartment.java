package com.seeyon.v3x.organization.webmodel;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgRole;

public class WebV3xOrgDepartment
{

        private V3xOrgDepartment v3xOrgDepartment;
        private Long parentId;
        private String parentName;
        private String adminNames;
        private String adminIds;
        private String managerNames;
        private String managerIds;
        private String postNames;
        private String postIds;
        public List<String> rolelist = new ArrayList<String>();
        
        
		public List<String> getRolelist() {
			return rolelist;
		}
		public void setRolelist(List<String> rolelist) {
			this.rolelist = rolelist;
		}
		public Long getParentId()
        {
            return parentId;
        }
        public void setParentId(Long parentId)
        {
            this.parentId = parentId;
        }
        public String getParentName()
        {
            return parentName;
        }
        public void setParentName(String parentName)
        {
            this.parentName = parentName;
        }
        public V3xOrgDepartment getV3xOrgDepartment()
        {
            return v3xOrgDepartment;
        }
        public void setV3xOrgDepartment(V3xOrgDepartment orgDepartment)
        {
            v3xOrgDepartment = orgDepartment;
        }
        public String getAdminNames()
        {
            return adminNames;
        }
        public void setAdminNames(String adminNames)
        {
            this.adminNames = adminNames;
        }
        public String getManagerNames()
        {
            return managerNames;
        }
        public void setManagerNames(String managerNames)
        {
            this.managerNames = managerNames;
        }
        public String getAdminIds()
        {
            return adminIds;
        }
        public void setAdminIds(String adminIds)
        {
            this.adminIds = adminIds;
        }
        public String getManagerIds()
        {
            return managerIds;
        }
        public void setManagerIds(String managerIds)
        {
            this.managerIds = managerIds;
        }
        public String getPostIds()
        {
            return postIds;
        }
        public void setPostIds(String postIds)
        {
            this.postIds = postIds;
        }
        public String getPostNames()
        {
            return postNames;
        }
        public void setPostNames(String postNames)
        {
            this.postNames = postNames;
        }
        
}
