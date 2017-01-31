package com.seeyon.v3x.taskmanage.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * 甘特图中用于展现的节点，不同模块数据欲以甘特图样式展现时，需将对应数据转换、设置为组件能够解析和辨识的集合<br>
 * 通常而言，POJO中需要扩展：parent、children、ganttId、logicalPath等非持久化属性用于转换为甘特图节点集合<br>
 * @see com.seeyon.v3x.taskmanage.domain.TaskInfo
 * @see com.seeyon.v3x.taskmanage.utils.GanttUtils#parse2GanttItem(com.seeyon.v3x.taskmanage.domain.TaskInfo)
 * @see com.seeyon.v3x.taskmanage.utils.GanttUtils#parse2GanttItems(List, Long, com.seeyon.v3x.project.manager.ProjectManager)
 * @see com.seeyon.v3x.taskmanage.controller.TaskController#ganttChartTasks(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 * <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-3-19
 */
public class GanttItem implements Comparable<GanttItem> {
	/**
	 * 甘特图中节点ID
	 */
	private int id;
	/**
	 * 节点名称
	 */
	private String name;
	/**
	 * 开始日期
	 */
	private Date beginDate;
	/**
	 * 结束日期
	 */
	private Date endDate;
	/**
	 * 是否为Sprint(特殊节点，显示时作为类似项目阶段或scrum中的sprint)
	 */
	private boolean sprint;
	/**
	 * 当前节点的上级父节点是否为Sprint(绘制依赖关系时，此种情况下不予绘制关系连接线)
	 */
	private boolean parentIsSprint;
	/**
	 * 完成率
	 */
	private float finishRate;
	/**
	 * 填充颜色
	 */
	private String color;
	/**
	 * 链接地址
	 */
	private String link;
	/**
	 * 父节点
	 */
	private GanttItem parent;
	/**
	 * 子节点
	 */
	private List<GanttItem> children;
	/**
	 * 逻辑层级深度
	 */
	private int logicalDepth;
	
	/**
	 * 添加一个子节点，同时将该子节点的父节点设为自己
	 * @param child	待添加的子节点
	 */
	public void addChild(GanttItem child) {
		if(this.children == null) {
			this.setChildren(new ArrayList<GanttItem>());
		}
		this.getChildren().add(child);
		child.setParent(this);
	}
	
	/**
	 * 获取父节点的ID，如果父节点为空，则返回空值0
	 * @return
	 */
	public int getParentGanttId() {
		return this.parent == null ? 0 : this.parent.getId();
	}
	
	/**
	 * 当前节点是否存在父节点
	 * @return	是否存在父节点
	 */
	public boolean hasParent() {
		return this.parent != null;
	}
	
	/**
	 * 当前节点是否存在子节点
	 * @return	是否存在子节点
	 */
	public boolean hasChildren() {
		return CollectionUtils.isNotEmpty(children);
	}
	
	/**
	 * 排序：逻辑层级升序、开始日期倒序
	 */
	public int compareTo(GanttItem o) {
		Integer depth = this.getLogicalDepth();
		Integer depth_o = o.getLogicalDepth();
		
		if(depth.compareTo(depth_o) == 0) {
			return -this.getBeginDate().compareTo(o.getBeginDate());
		}
		
		return depth.compareTo(depth_o);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beginDate == null) ? 0 : beginDate.hashCode());
		result = prime * result + id;
		result = prime * result + logicalDepth;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GanttItem)) {
			return false;
		}
		GanttItem other = (GanttItem) obj;
		if (beginDate == null) {
			if (other.beginDate != null) {
				return false;
			}
		} else if (!beginDate.equals(other.beginDate)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (logicalDepth != other.logicalDepth) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/*---------------setter/getter--------------*/
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * 是否为Sprint(特殊节点，显示时作为类似项目阶段或scrum中的sprint)
	 * @return	是否为sprint
	 */
	public boolean isSprint() {
		return sprint;
	}
	public void setSprint(boolean sprint) {
		this.sprint = sprint;
	}
	/**
	 * 当前节点的上级父节点是否为Sprint(绘制依赖关系时，此种情况下不予绘制关系连接线)
	 */
	public boolean isParentIsSprint() {
		return parentIsSprint;
	}
	public void setParentIsSprint(boolean parentIsSprint) {
		this.parentIsSprint = parentIsSprint;
	}
	public float getFinishRate() {
		return finishRate;
	}
	public void setFinishRate(float finishRate) {
		this.finishRate = finishRate;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public GanttItem getParent() {
		return parent;
	}
	public void setParent(GanttItem parent) {
		this.parent = parent;
	}
	public List<GanttItem> getChildren() {
		return children;
	}
	public void setChildren(List<GanttItem> children) {
		this.children = children;
	}
	public int getLogicalDepth() {
		return logicalDepth;
	}
	public void setLogicalDepth(int logicalDepth) {
		this.logicalDepth = logicalDepth;
	}
}
