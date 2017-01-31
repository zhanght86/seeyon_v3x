package com.seeyon.v3x.common.taglibs.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.taglibs.table.Cell;
import com.seeyon.v3x.common.taglibs.table.Header;
import com.seeyon.v3x.common.taglibs.table.Row;
import com.seeyon.v3x.common.taglibs.table.TableModel;
import com.seeyon.v3x.common.taglibs.util.Constants;
import com.seeyon.v3x.common.taglibs.util.ResourceUtil;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-8-30
 */
public abstract class TableSupport extends BodyTagSupport {

	public static final String TAG_NAME = "table";

	protected String htmlId;

	protected Object data;// 原数据

	protected String var;
	
	protected String varIndex;

	protected int size;// 总记录数

	protected int pageSize;// 每页显示数

	protected String onRowClick; // 行tr事件

	protected String onRowDblClick;

	protected String width;

	protected int page; // 当前页码

	protected int pages; // 总页数

	protected boolean showPager;

	protected boolean showHeader;

	protected String className;

	protected int leastSize; // 最少显示行，不足，用空白行补足

	protected boolean isChangeTRColor;
	
	protected LocalizationContext bundleAttrValue; //指定的ResourceBundle
	
	protected String formMethod = "get";

	// ////////////////////////////////

	private HttpServletRequest request;

	private TableModel tableModel;

	private Row currentRow;

	private List list;
	
	/**
	 * Iterator on collection.
	 */
	private Iterator tableIterator;

	private boolean doAfterBodyExecuted;

	/**
	 * actual row number, updated during iteration.
	 */
	private int rowNumber = 0;
	
	protected boolean dragable;
	
	protected int subHeight;

	public TableSupport() {
		super();
		init();
	}

	private void init() {
		list = Collections.EMPTY_LIST;
		showHeader = true;
		showPager = true;
		htmlId = null;
		var = null;
		varIndex = null;
		data = null;
		size = 0;
		pageSize = 0;
		onRowClick = "";
		width = "100%";
		onRowDblClick = "";
		page = 1;
		pages = 1;
		tableIterator = null;
		tableModel = null;
		currentRow = null;
		rowNumber = 0;
		doAfterBodyExecuted = false;
		className = null;
		leastSize = 0;
		isChangeTRColor = true;
		bundleAttrValue = null;
		formMethod = "get";
		super.bodyContent = null;
		dragable = true;
		subHeight = 0;
	}

	/**
	 * 从子标签得到column对象
	 * 
	 * @param header
	 */
	public void addHeader(Header header) {
		this.tableModel.addHeader(header);
	}

	public void addCell(Cell cell) {
		if (this.currentRow != null) {
			this.currentRow.addCell(cell);
		}
	}

	@Override
	public int doStartTag() throws JspException {
		size = Pagination.getRowCount();

		request = (HttpServletRequest) pageContext.getRequest();
		// 表格样式
		if (className == null) { // 没有指定样式，使用默认样式
			className = showHeader ? "sort" : "sort-noheader";
		}
		// 每页条数
		try {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}
		catch (Exception e) {
		}
		if (pageSize < 1) {
			pageSize = Pagination.getMaxResults();
		}
		// 当前页码
		try {
			if(size<=pageSize){
				page=1;
			}else{
				page = Integer.parseInt(request.getParameter("page"));
			}
		}
		catch (Exception e) {
		}
		// 数据
		if (data != null) {
			this.doData();

			// 总记录数
			if (size == 0) {
				size = this.list.size();
			}

			if (this.showPager && this.list.size() > this.pageSize) {
				this.list = this.list.subList(0, pageSize);
			}
		}

		// 总页数
		pages = (size + pageSize - 1) / pageSize;
		if (pages < 1) {
			pages = 1;
		}

		if (page < 1) {
			page = 1;
		}

		// 表格 Model
		this.tableModel = new TableModel(pageContext);

		this.tableIterator = list.iterator();

		doIteration();

		return 2;
	}

	@Override
	public int doAfterBody() throws JspException {
		// doAfterBody() has been called, body is not empty
		this.doAfterBodyExecuted = true;

		// increment this.rowNumber
		this.rowNumber++;

		if (rowNumber == 1 && this.tableModel.getHeaders().isEmpty()) {
			throw new NullPointerException("There no any column in this table.");
		}

		// Call doIteration() to do the common work
		return doIteration();
	}

	@Override
	public int doEndTag() throws JspException {
		dragable = dragable ? true :false;
		if (!this.doAfterBodyExecuted) {
			// first row (created in doStartTag)
			if (this.currentRow != null) {
				// if yes add to table model and remove
				this.tableModel.addRow(this.currentRow);
			}

			// other rows
			while (this.tableIterator.hasNext()) {
				Object iteratedObject = this.tableIterator.next();
				this.rowNumber++;

				// Row object for Cell values
				this.currentRow = new Row(iteratedObject, this.rowNumber);
//				this.currentRow.setOnRowDblClick("");
//				this.currentRow.setOnRowClick("");

				this.tableModel.addRow(this.currentRow);
			}
		}

		// if (tableModel.getHeaders().isEmpty()) {
		// throw new NullPointerException("There no any column in this table.");
		// }

		JspWriter out = this.pageContext.getOut();

		try {
			String sortColumn = showHeader ? "sortColumn(event, " + isChangeTRColor + ", "+dragable+")" : "";
			out.println(Constants.getString("list.table.begin.html."+dragable, htmlId, className, width, sortColumn,dragable,subHeight));

			// 标题栏,
			// <thead><tr><td>...</td></tr></tread>
			if (showHeader) {
				this.writerHeaders(out);
			}

			//拖动不拖动在后台输出，js操作慢
			if(dragable){
				this.writerDrag(out);
			}
			out.println(Constants.getString("list.table.tbody.begin.html."+dragable,htmlId));

			this.writerDatas(out);

			out.println(Constants.getString("list.table.tbody.end.html."+dragable));

			if (showPager) { // 显示分页栏
				writerPager(out);
			}

			out.println(Constants.getString("list.table.end.html."+dragable));
			//解决firefox下grid没有滚动跳的问题，实在没招了（这样修改代码少，如果光靠修改css 每个引用grid的jsp也要修改）
			StringBuffer sb = new StringBuffer("<script>setFFGrid('");
			sb.append(this.htmlId);
			sb.append("',");
			if(this.dragable){
				sb.append("true");
			}else{
				sb.append("false");
			}
			sb.append(")</script>");
			out.println(sb.toString());
		}
		catch (Exception e) {
			throw new JspTagException(e.getMessage(), e);
		}

		init();

		return super.doEndTag();
	}

	protected int doIteration() {
		if (this.currentRow != null) {
			// if yes add to table model and remove
			this.tableModel.addRow(this.currentRow);
			this.currentRow = null;
		}

		if (this.tableIterator.hasNext()) {
			Object iteratedObject = this.tableIterator.next();

			if (this.var != null) {
				this.pageContext.setAttribute(var, iteratedObject);
			}
			
			if (this.varIndex != null) {
				this.pageContext.setAttribute(varIndex, this.rowNumber);
			}

			this.currentRow = new Row(iteratedObject, rowNumber);
//			this.currentRow.setOnRowClick("");
//			this.currentRow.setOnRowDblClick("");

			return 2;
		}

		// end iteration
		return SKIP_BODY;
	}

	private void writerPager(JspWriter out) {
		StringBuffer sb = new StringBuffer();

		sb.append(getInputString());
		sb.append(getPageAction());

		try {
			out.println(Constants.getString("list.table.page.all.html."+dragable, sb
					.toString(), this.tableModel.getHeaders().size()));
		}
		catch (IOException e) {
		}
	}

	/**
	 * 输出参数
	 * 
	 * <code>
	 *  &lt;input &gt;
	 * </code>
	 * 
	 * ,并对value进行escapeHtml
	 * 
	 * @return
	 */
	private StringBuffer getInputString() {
		StringBuffer sb = new StringBuffer();
		Enumeration e = request.getParameterNames();
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("<!--\n");
		sb.append("var pageFormMethod = \"" + this.formMethod + "\"\n");
		sb.append("var pageQueryMap = new Properties();\n");

		while (e.hasMoreElements()) {
			String param = (String) e.nextElement();
			String[] values = request.getParameterValues(param);

			if (values == null || param.matches("page|count|_spage")) {
				continue;
			}
			
			String valueStr = "";
			if(values.length == 1){
				valueStr = "\"" + Strings.escapeJavascript(values[0]) + "\"";
			}
			else{
				valueStr += "[";
				
				for (int i = 0; i < values.length; i++) {
					if(i > 0){
						valueStr += ",";
					}
					
					valueStr += "\"" + Strings.escapeJavascript(values[i]) + "\"";
				}
				
				valueStr += "]";
			}

			sb.append("pageQueryMap.put('" + param + "', " + valueStr + ");\n");
			
			// sb.append(Constants.getString("list.table.page.input.html",
			// param,
			// escapeHtml(value))).append("\r\n");
		}

		sb.append("pageQueryMap.put('_spage', '" + Strings.escapeNULL(request.getParameter("_spage"), "") + "');\n");
		sb.append("pageQueryMap.put('page', '" + page + "');\n");
		sb.append("pageQueryMap.put('count', '" + size + "');\n");

		// sb.append(Constants.getString("list.table.page.input.html",
		// "page", page)).append("\r\n");
		// sb.append(Constants.getString("list.table.page.input.html",
		// "count", size)).append("\r\n");
		sb.append("//-->\n");
		sb.append("</script>\n");

		return sb;
	}

	/**
	 * 输出分页HTML代码
	 * 
	 * @return
	 */
	public StringBuffer getPageAction() {
		StringBuffer pager = new StringBuffer();
		String flag = page == 1 ? "!a" : "a";
		pager.append(
				Constants.getString("list.table.page.first.html", flag,
						ResourceUtil.getLocaleString(pageContext,
								"taglib.list.table.page.first.label"))).append(
				"\r\n");

		pager.append(
				Constants.getString("list.table.page.prev.html", flag,
						ResourceUtil.getLocaleString(pageContext,
								"taglib.list.table.page.prev.label"))).append(
				"\r\n");

		flag = page >= pages ? "!a" : "a";
		pager.append(
				Constants.getString("list.table.page.next.html", flag,
						ResourceUtil.getLocaleString(pageContext,
								"taglib.list.table.page.next.label"))).append(
				"\r\n");

		pager.append(
				Constants.getString("list.table.page.last.html", flag, pages,
						ResourceUtil.getLocaleString(pageContext,
								"taglib.list.table.page.last.label"))).append(
				"\r\n");

		String pageSizeHtml = Constants.getString(
				"list.table.page.pagesize.input.html."+dragable, pageSize);
		String intpageHtml = Constants.getString(
				"list.table.page.intpage.input.html."+dragable, String.valueOf(page), String.valueOf(pages));

		// 每页{0}条记录 | 共{1}页/{2}条记录 | {3} |第{4}页
		return new StringBuffer().append(ResourceUtil.getLocaleString(
				pageContext, "taglib.list.table.page.html", pageSizeHtml,
				pages, size, pager.toString(), intpageHtml));
	}

	/**
	 * 输出Header的HTML代码
	 * 
	 * @param out
	 * @throws Exception
	 */
	private void writerHeaders(JspWriter out) throws Exception {
		List<Header> headers = this.tableModel.getHeaders();
		out.println(Constants.getString("list.table.thead.begin.html."+dragable, htmlId));
		out.println(Constants.getString("list.table.header.tr.begin.html."+dragable,htmlId));
		String orderByDESCHTML = "";
		String orderByColumn = Pagination.getOrderByColumn();
		String orderByDESC = Pagination.getOrderByDESC();
		if(Strings.isNotBlank(orderByDESC)){
			orderByDESCHTML = "<span class='arrow' id='OrderByColumn_" + orderByColumn + "'>" + (orderByDESC.equals("DESC") ? "6" : "5") + "</span>";
		}
		for (Header header : headers) {
			String label = header.getLabel();
			if(Strings.isNotBlank(header.getOrderBy()) && header.getOrderBy().equals(orderByColumn)){
				label = label + orderByDESCHTML;
			}
			String sortColumn = showHeader ? "sortColumn(event, " + isChangeTRColor + ", "+dragable+")" : "";
			out.println(Constants.getString("list.table.header.td.html."+dragable,
				Strings.join("", header.getWidth(), header.getType(), header.getAlign(), header.getNowarp(), header.getOrderByNode(), header.getWidthFixed()), label,sortColumn,header.getStyleAlign()));
		}
		out.println(Constants.getString("list.table.row.end.html."+dragable));
		out.println(Constants.getString("list.table.thead.end.html."+dragable));
	}
	/**
	 * 输出可拖动div的HTML代码
	 * 
	 * @param out
	 * @throws Exception
	 */
	private void writerDrag(JspWriter out) throws Exception {
		List<Header> headers = this.tableModel.getHeaders();
		out.println(Constants.getString("list.table.drag.begin.html."+dragable,htmlId));
		for (Header header : headers) {
			out.println(Constants.getString("list.table.drag.div.html."+dragable,
				Strings.join("", header.getWidth(), header.getType(), header.getAlign(), header.getNowarp(), header.getOrderByNode(), header.getWidthFixed())));
		}
		out.println(Constants.getString("list.table.drag.end.html."+dragable));
	}
	/**
	 * 
	 * @param out
	 * @throws Exception
	 */
	private void writerDatas(JspWriter out) throws Exception {
		List<Row> rows = this.tableModel.getRowListFull();

		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			out.println(Constants.getString("list.table.body.tr.begin.html."+dragable, Strings.join(i%2==0?"erow":"", row.getOnRowClick(), row.getOnRowDblClick())));

			List<Cell> cells = row.getCellList();

			for (Cell c : cells) {
				String content = null;
				if (StringUtils.isBlank(c.getHref())) {
					content = c.getContent();
				}
				else {
					content = Constants.getString("a.link.html", Strings.join("", c.getHref(), c.getTarget()), c.getContent());
				}

				String attributes = Strings.join("", c.getAlign(), c.getAlt(),
						c.getClassName(), c.getWidth(), 
						c.getOnclick(), c.getOnDblClick(),
						c.getOnmouseover(), c.getOnmouseout(), c.getNowarp());

				out.println(Constants.getString("list.table.body.td.html."+dragable, attributes, content,c.getStyleAlign()));
			}

			out.println(Constants.getString("list.table.row.end.html."+dragable));
		}

		// 不足leastSize，用空白行补充
		if (leastSize != 0 && this.list.size() < leastSize) {
			List<Header> headers = this.tableModel.getHeaders();

			StringBuffer sb = new StringBuffer();
			
			sb.append(Constants.getString("list.table.body.tr.begin.html."+dragable));

			for (Header header : headers) {
				sb.append(Constants.getString("list.table.body.td.html."+dragable, Strings.join("", header.getClassName(), "", header.getWidth()), "&nbsp;"));
			}

			sb.append(Constants.getString("list.table.row.end.html."+dragable));

			for (int i = 0; i < leastSize - this.list.size(); i++) {
				out.println(sb.toString());
			}
		}
	}

	@SuppressWarnings( { "unused", "unchecked" })
	private void doData() {
		list = new ArrayList();

		if (data == null) {
			throw new NullPointerException("Data \"" + data + "\" is null");
		}
		/**
		 * data为字符数类型 1. ${expression} 2. request attribute name
		 */

		if (data instanceof String) {
			data = request.getAttribute(data.toString());
		}

		if (data instanceof Collection) {
			list.addAll((Collection) data);
		}
		else if (data instanceof Object[]) {
			list.addAll(Arrays.asList((Object[]) data));
		}
		else if (data instanceof Enumeration) {
			Enumeration e = (Enumeration) data;
			while (e.hasMoreElements()) {
				list.add(e.nextElement());
			}
		}
	}

	public boolean isFirstIteration() {
		return this.rowNumber == 0;
	}

	public String getVar() {
		return var;
	}

	public Row getCurrentRow() {
		return currentRow;
	}

}
