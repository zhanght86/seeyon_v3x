package com.seeyon.v3x.common.taglibs.table;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-5
 */
public class TableModel {

	/**
	 * full list (contains Row objects).
	 */
	private List<Row> rowListFull;

	/**
	 * Jsp page context.
	 */
	private PageContext pageContext;

	private List<Header> headers;

	/**
	 * Constructor for TableModel.
	 * 
	 * @param tableProperties
	 *            table properties
	 * @param charEncoding
	 *            response encoding
	 */
	public TableModel(PageContext pageContext) {
		this.rowListFull = new ArrayList<Row>();
		this.headers = new ArrayList<Header>();
		this.pageContext = pageContext;
	}

	/**
	 * Returns the jsp page context.
	 * 
	 * @return page context
	 */
	protected PageContext getPageContext() {
		return this.pageContext;
	}

	/**
	 * get the full list.
	 * 
	 * @return the full list containing Row objects
	 */
	public List<Row> getRowListFull() {
		return this.rowListFull;
	}

	/**
	 * adds a Row object to the table.
	 * 
	 * @param row
	 *            Row
	 */
	public void addRow(Row row) {
		this.rowListFull.add(row);
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void addHeader(Header header) {
		this.headers.add(header);
	}

}
