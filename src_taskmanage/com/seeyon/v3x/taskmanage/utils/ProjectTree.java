package com.seeyon.v3x.taskmanage.utils;

import java.util.List;

import com.seeyon.v3x.project.domain.ProjectType;

/**
 * 项目树数据模型，包括：默认选中的项目、默认展开的项目分类以及项目树所需的全部数据
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-3-10
 */
public class ProjectTree {
	/**
	 * 默认选中的项目：按照时间降序排列所得的第一个任务
	 */
	private Long selectedProject;
	/**
	 * 默认展开的项目分类：按照时间降序排列所得的第一个任务所在的项目分类
	 */
	private Long selectedProjectType;
	/**
	 * 项目树数据，项目分类集合，每个项目分类下面包含对应的用户可访问的项目集合
	 */
	private List<ProjectType> treeData;
	
	public ProjectTree(Long selectedProject, Long selectedProjectType, List<ProjectType> treeData) {
		super();
		this.selectedProject = selectedProject;
		this.selectedProjectType = selectedProjectType;
		this.treeData = treeData;
	}
	
	public Long getSelectedProject() {
		return selectedProject;
	}
	public void setSelectedProject(Long selectedProject) {
		this.selectedProject = selectedProject;
	}
	public Long getSelectedProjectType() {
		return selectedProjectType;
	}
	public void setSelectedProjectType(Long selectedProjectType) {
		this.selectedProjectType = selectedProjectType;
	}
	public List<ProjectType> getTreeData() {
		return treeData;
	}
	public void setTreeData(List<ProjectType> treeData) {
		this.treeData = treeData;
	}
	
}
