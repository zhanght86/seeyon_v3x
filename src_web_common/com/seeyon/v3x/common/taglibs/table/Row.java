package com.seeyon.v3x.common.taglibs.table;

import static com.seeyon.v3x.common.taglibs.util.ResourceUtil.getNodeAttribute;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-5
 */
public class Row {

	/**
	 * Object holding values for the current row.
	 */
	private Object rowObject;

	/**
	 * List of cell objects.
	 */
	private List<Cell> staticCells;

	/**
	 * Row number.
	 */
	private int rowNumber;

	private String onRowClick;

	private String onRowDblClick;

	/**
	 * Constructor for Row.
	 * 
	 * @param object
	 *            Object
	 * @param number
	 *            int
	 */
	public Row(Object object, int number) {
		this.rowObject = object;
		this.rowNumber = number;
		this.staticCells = new ArrayList<Cell>();
	}

	/**
	 * Setter for the row number.
	 * 
	 * @param number
	 *            row number
	 */
	public void setRowNumber(int number) {
		this.rowNumber = number;
	}

	/**
	 * Getter for the row number.
	 * 
	 * @return row number
	 */
	public int getRowNumber() {
		return this.rowNumber;
	}

	/**
	 * Adds a cell to the row.
	 * 
	 * @param cell
	 *            Cell
	 */
	public void addCell(Cell cell) {
		this.staticCells.add(cell);
	}

	/**
	 * getter for the list of Cell object.
	 * 
	 * @return List containing Cell objects
	 */
	public List<Cell> getCellList() {
		return this.staticCells;
	}

	/**
	 * getter for the object holding values for the current row.
	 * 
	 * @return Object object holding values for the current row
	 */
	public Object getObject() {
		return this.rowObject;
	}

	public String toString() {
		return this.getCellList().toString();
	}

	public String getOnRowClick() {
		return getNodeAttribute("onclick", onRowClick);
	}

	public void setOnRowClick(String onRowClick) {
		this.onRowClick = onRowClick;
	}

	public String getOnRowDblClick() {
		return getNodeAttribute("onDblClick", onRowDblClick);
	}

	public void setOnRowDblClick(String onRowDblClick) {
		this.onRowDblClick = onRowDblClick;
	}

}
