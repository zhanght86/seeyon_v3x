package com.seeyon.v3x.common.taglibs.support;

import java.io.IOException;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.collections.CollectionUtils;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.taskmanage.utils.GanttItem;
import com.seeyon.v3x.taskmanage.utils.GanttUtils;
import com.seeyon.v3x.taskmanage.utils.TaskUtils;

/**
 * 甘特图展现Tag
 * <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-3-19
 */
public class GanttChartTag extends BodyTagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6150167605847069236L;
	
	/**
	 * 设置好Sprint阶段、节点父子关系的甘特图填充数据集合。<br>
	 * 用于工作任务等信息的甘特图展现，其他模块调用此组件之前，需将数据进行转换、设置，否则无法正确显示。<br>
	 * @see com.seeyon.v3x.taskmanage.utils.GanttUtils#parse2GanttItem(com.seeyon.v3x.taskmanage.domain.TaskInfo)
	 * @see com.seeyon.v3x.taskmanage.utils.GanttUtils#parse2GanttItems(List, Long, com.seeyon.v3x.project.manager.ProjectManager)
	 * @see com.seeyon.v3x.taskmanage.controller.TaskController#ganttChartTasks(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	private List<GanttItem> data;

	public List<GanttItem> getData() {
		return data;
	}

	public void setData(List<GanttItem> data) {
		this.data = data;
	}

	public GanttChartTag() {}
	
	public void init() {}
	
	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}
	
	@Override
	public int doEndTag() throws JspException {
		try {
			JspWriter out = this.pageContext.getOut();
			StringBuilder sb = new StringBuilder();
			
			if(CollectionUtils.isNotEmpty(data)) {
				sb.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"" + SystemEnvironment.getA8ContextPath() + "/apps_res/taskmanage/css/jsgantt.css" + Functions.resSuffix() + "\" />\n");
				sb.append("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + SystemEnvironment.getA8ContextPath() + "/apps_res/taskmanage/js/gantt.js" + Functions.resSuffix() + "\" ></script>\n");
				sb.append("<script type=\"text/javascript\" charset=\"UTF-8\">\n");
				sb.append("		v3x.loadLanguage(\"/apps_res/taskmanage/js/i18n\");\n");
				sb.append("</script>\n");
				sb.append("<div id='displayChoiceDIV'>\n");
				sb.append("	<span style='padding-left:14px;'>\n");
				sb.append("		<label for='treeAndChart'>\n");
				sb.append("			<input type='radio' name='displayChoice' id='treeAndChart' checked onclick='javascript:ganttDisplayChoice();' />\n");
				sb.append(			TaskUtils.getI18n("task.showtreeandgantt") + "\n");
				sb.append("		</label>\n");
				sb.append("	</span>\n");
				sb.append("	<span style='padding-left:14px;'>\n");
				sb.append("		<label for='chartOnly'>\n");
				sb.append("			<input type='radio' name='displayChoice' id='chartOnly' onclick='javascript:ganttDisplayChoice();' />\n");
				sb.append(				TaskUtils.getI18n("task.showganttonly") + "\n");
				sb.append("		</label>\n");
				sb.append("	</span>\n");
				sb.append("</div>\n");
				sb.append("<div style=\"position:relative;\" class=\"gantt\" id=\"GanttChartDIV\">\n");
				sb.append("</div>\n");
				sb.append("	<script language=\"javascript\">\n");
				sb.append("		var t1 = new Date().getTime();\n");
				sb.append("		var g = new JSGantt.GanttChart('g', document.getElementById('GanttChartDIV'), 'day');\n");
				sb.append("		var pStart, pEnd, pComp, pColor, pLink, pGroup, pSprint;\n");
				sb.append(		GanttUtils.getGanttScript(data));
				sb.append("		g.Draw();\n");
				sb.append("		g.DrawDependencies();\n");
				sb.append("		var time = new Date().getTime() - t1;\n");
				sb.append("		ganttLog.debug('绘制甘特图耗时：' + time + 'MS');\n");
				sb.append("	</script>\n");
			}
			else {
				sb.append("<table id='emptyTable' width='100%' height='300' border='0' cellspacing='0' cellpadding='0'>\n");
				sb.append("	<tr>\n");
				sb.append("		<td id='emptyTD' align='center' valign='middle' style='background-position:right bottom;background-repeat:no-repeat;' background=\"" + SystemEnvironment.getA8ContextPath() + "/apps_res/v3xmain/images/publicMessageBg.jpg\" />\n");
				sb.append("			<font style='font-size:32px;color:#6c82ac'>" + TaskUtils.getI18n("task.emptygantt") + "</font>\n");
				sb.append("		</td>\n");
				sb.append("	</tr>\n");
				sb.append("</table>\n");
			}
			
			out.print(sb.toString());
		} 
		catch (IOException e) {
			throw new JspTagException(e.getMessage(), e);
		}
		
		return super.doEndTag();
	}
	
	@Override
	public void release() {
		init();
		super.release();
	}

}
