package com.seeyon.v3x.taskmanage.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 甘特图工具类
 * <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-3-19
 */
public abstract class GanttUtils {
	private static final Log logger = LogFactory.getLog(GanttUtils.class);
	
	/**
	 * 甘特图展现任务阶段时，需要为其设定一个虚拟逻辑层级深度，定义一个初始值用于累加
	 */
	private static final int VIRTUAL_LOGICAL_DEPTH = -10000;
	/**
	 * 甘特图节点填充颜色，按照风险级别从低到高显示不同颜色，如：风险高 - 红色，无风险 - 绿色等，用于工作任务的甘特图展现
	 */
	private static final String[] ITEM_COLOR = {"A6CEF1", "A7CDF0", "00ffff", "FF0000"};
	/**
	 * 某些特殊的甘特图节点(如项目阶段转换成的节点)无有效链接地址，赋予一个空值
	 */
	private static final String NULL_LINK = "javascript:void(0);";
	
	/**
	 * 将工作任务信息转换为甘特图节点
	 * @param t	工作任务信息
	 */
	public static GanttItem parse2GanttItem(TaskInfo t) {
		GanttItem ret = new GanttItem();
		
		ret.setName(t.getSubject());
		ret.setBeginDate(t.getPlannedStartTime());
		ret.setEndDate(t.getPlannedEndTime());
		ret.setSprint(t.isFromProjectPhase());
		ret.setFinishRate(t.getFinishRate());
		ret.setColor(ITEM_COLOR[t.getRiskLevel()]);
		ret.setLogicalDepth(t.getLogicalDepth());
		String url = t.isFromProjectPhase() ? NULL_LINK : TaskUtils.getViewUrl(t.getId());
		ret.setLink(url);
		
		return ret;
	}
	
	/**
	 * 对需要以甘特图展现的任务数据进行处理，设置好甘特图中的父子、依赖关系
	 * @param projectPhaseId	项目阶段ID，区分全部阶段或某一阶段，对应的甘特图展现形式也不同
	 */
	public static List<GanttItem> parse2GanttItems(List<TaskInfo> tasks, Long projectPhaseId, ProjectManager projectManager) {
		if(CollectionUtils.isEmpty(tasks))
			return null;
		
		Map<Long, TaskInfo> mapT = new HashMap<Long, TaskInfo>();
		Map<Integer, GanttItem> mapG = new HashMap<Integer, GanttItem>();
		int gantt = 1;
		Set<Long> phaseIds = new HashSet<Long>();
		
		List<GanttItem> items = new ArrayList<GanttItem>(tasks.size());
		for(TaskInfo t : tasks) {
			GanttItem i = parse2GanttItem(t);
			i.setId(gantt ++);
			items.add(i);
			mapG.put(i.getId(), i);
			
			t.setGanttId(i.getId());
			mapT.put(t.getId(), t);
			
			phaseIds.add(t.getProjectPhaseId());
		}
		
		if(projectPhaseId == TaskConstants.PROJECT_PHASE_ALL) {
			List<ProjectPhase> phases = projectManager.getProjectPhases(phaseIds);
			if(CollectionUtils.isNotEmpty(phases)) {
				int depth = VIRTUAL_LOGICAL_DEPTH;
				for(ProjectPhase phase : phases) {
					TaskInfo tp = new TaskInfo(phase);
					tp.setLogicalDepth(depth ++);
					
					GanttItem i = parse2GanttItem(tp);
					i.setId(gantt ++);
					items.add(i);
					mapG.put(i.getId(), i);
					
					tp.setGanttId(i.getId());
					tasks.add(tp);
					mapT.put(phase.getId(), tp);
				}
			}
		}
		
		for(TaskInfo t2 : tasks) {
			Long parentId = t2.getParentTaskId();
			TaskInfo tParent = mapT.get(parentId);
			GanttItem item_t2 = mapG.get(t2.getGanttId());
			
			if(parentId != -1l && tParent != null) {
				mapG.get(tParent.getGanttId()).addChild(item_t2);
			}
			else if(t2.getProjectPhaseId() != t2.getId().longValue() && mapT.get(t2.getProjectPhaseId()) != null) {
				item_t2.setParentIsSprint(true);
				mapG.get(mapT.get(t2.getProjectPhaseId()).getGanttId()).addChild(item_t2);
			}
		}
		
		Collections.sort(items);
		
		if(logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("名称 - 甘特ID - 父甘特ID - 开始日期 - 结束日期\n");
			for(GanttItem i : items) {
				sb.append(i.getName() + " - " + i.getId() + " - " + i.getParentGanttId() + " - " + 
				Datetimes.formatDate(i.getBeginDate()) + " - " + Datetimes.formatDate(i.getEndDate()) + "\n");
			}
			logger.debug("甘特图填充数据:\n" + sb.toString());
		}
		
		return items;
	}
	
	/**
	 * 获取待展现为甘特图任务数据的JS脚本内容
	 */
	public static String getGanttScript(List<GanttItem> items) {
		StringBuilder sb = new StringBuilder();
		if(CollectionUtils.isNotEmpty(items)) {
			for(GanttItem item : items) {
				if(!item.hasParent()) {
					parseItem(item, sb);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 递归添加甘特图所需脚本
	 * @param task	当前任务，如果有子任务，则其子任务也递归添加甘特图脚本
	 * @param sb	总JavaScript脚本字符串(StringBuilder，方便附加)
	 */
	private static void parseItem(GanttItem item, StringBuilder sb) {
		List<GanttItem> children = item.getChildren();
		boolean hasChild = CollectionUtils.isNotEmpty(children);
		sb.append(getScript(item));
		if(hasChild) {
			for(GanttItem i : children) {
				parseItem(i, sb);
			}
		}
	}
	
	/**
	 * 获取单个任务的甘特图节点脚本内容
	 * @param t	任务
	 */
	private static String getScript(GanttItem item) {
		StringBuilder ret = new StringBuilder();
		ret.append("pStart = '" + Datetimes.formatDate(item.getBeginDate()) + "';\n");
		ret.append("pEnd = '" + Datetimes.formatDate(item.getEndDate()) + "';\n");
		ret.append("pComp = " + item.getFinishRate() + ";\n");
		ret.append("pColor = '" + item.getColor() + "';\n");
		String url = item.isSprint() ? "javascript:void(0);" : item.getLink();
		ret.append("pLink = '" + url + "';\n");
		ret.append("pGroup = " + (item.hasChildren() ? 1 : 0) + ";\n");
		ret.append("pSprint = " + (item.isSprint() ? 1 : 0) + ";\n");
		
		// JSGantt.TaskItem(pID, pName, pStart, pEnd, pColor, pLink, pRes, pComp, pGroup, pSprint, pParent, pOpen, (pDepend, pCaption))
		ret.append("g.AddTaskItem(new JSGantt.TaskItem(" + item.getId() + ", '" + Strings.escapeQuot(item.getName()) + "', " + 
								  "pStart, pEnd, pColor, pLink, '', pComp, pGroup, pSprint, " + 
								  item.getParentGanttId() + ", 1" + (item.isParentIsSprint() ? "" : (", "+ item.getParentGanttId() + ", ''")) + "));\n\n");
		return ret.toString();
	}
}
