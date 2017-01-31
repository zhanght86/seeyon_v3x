package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.ObjectToXMLBase;

/**
 * 成倍行,不定列 模板<br/>
 * 适用于　三或四列标准列表模板满足不了需要的情况下<br/>
 * 可以自定义列数、宽度、单元格样式、链接地址<br/>
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 * 
 */
public class MultiRowVariableColumnTemplete extends BaseSectionTemplete
{
    private static final long serialVersionUID = -2910452785360815515L;

    private List<MultiRowVariableColumnTemplete.Row> rows;

    @Override
    public String getResolveFunction()
    {
        return "multiRowVariableColumnTemplete";
    }

    public MultiRowVariableColumnTemplete.Row addRow()
    {
        if (this.rows == null)
        {
            this.rows = new ArrayList<MultiRowVariableColumnTemplete.Row>();
        }

        MultiRowVariableColumnTemplete.Row row = new MultiRowVariableColumnTemplete.Row();
        this.rows.add(row);

        return row;
    }

    public List<MultiRowVariableColumnTemplete.Row> getRows()
    {
        return this.rows;
    }

    /**
     * 行对象
     */
    public class Row extends ObjectToXMLBase implements Serializable
    {

        private static final long serialVersionUID = 6799252093506685897L;

        List<MultiRowVariableColumnTemplete.Cell> cells;

        public MultiRowVariableColumnTemplete.Cell addCell()
        {
            if (cells == null)
            {
                cells = new ArrayList<MultiRowVariableColumnTemplete.Cell>();
            }
            MultiRowVariableColumnTemplete.Cell c = new MultiRowVariableColumnTemplete.Cell();

            cells.add(c);

            return c;
        }

        public List<MultiRowVariableColumnTemplete.Cell> getCells()
        {
            return cells;
        }
        
        
    }

    /**
     * 列单元格 对象
     */
    public class Cell extends ObjectToXMLBase implements Serializable
    {
        private static final long serialVersionUID = -3786227930814279877L;

        private String cellContent; // 单元格内容
        
        private String cellContentHTML;
        
        private String alt;

        private Boolean hasAttachments;; // 是否有附件
        
        private Boolean fiexed;; // 固定文字，不裁减，默认false
        
        private int cellWidth; // 单元格宽度 百分比　（用int值，不需要'%'）

        private String linkURL; // 链接地址

        private String className; // 样式名称
        
        private int openType;
        
        private List<String> extIcons;
        
        private String bodyType;
        
        public String getBodyType() {
			return bodyType;
		}

		public void setBodyType(String bodyType) {
			this.bodyType = bodyType;
		}

		public String getCellContent()
        {
            return cellContent;
        }
        
        public void setCellContent(String cellContent)
        {
            this.cellContent = cellContent;
        }

        public int getCellWidth()
        {
            return cellWidth;
        }

        /**
         * 单元格宽度 百分比　（用int值，不需要'%'）
         * @param cellWidth
         */
        public void setCellWidth(int cellWidth)
        {
            this.cellWidth = cellWidth;
        }

        public Boolean getHasAttachments()
        {
            return hasAttachments;
        }

        public void setHasAttachments(Boolean hasAttachments)
        {
            this.hasAttachments = hasAttachments;
        }

        public String getClassName()
        {
            return className;
        }

        public void setClassName(String className)
        {
            this.className = className;
        }

        public String getLinkURL()
        {
            return linkURL;
        }

        public void setLinkURL(String linkURL)
        {
            this.linkURL = linkURL;
        }
        
        public void setLinkURL(String linkURL, OPEN_TYPE openType)
        {
            this.linkURL = linkURL;
            this.openType = openType.ordinal();
        }

        public List<String> getExtIcons()
        {
            return extIcons;
        }
        
        public int getOpenType() {
			return openType;
		}

		public void setOpenType(OPEN_TYPE openType) {
            this.openType = openType.ordinal();
        }

        public void addExtIcon(String extIcon)
        {
            if(this.extIcons == null){
            	this.extIcons = new ArrayList<String>();
            }
            
            this.extIcons.add(extIcon);
        }

		public String getAlt() {
			return alt;
		}

		public void setAlt(String alt) {
			this.alt = alt;
		}

		public String getCellContentHTML() {
			return cellContentHTML;
		}

		/**
		 * 标题的HTML代码，设置这个属性后，其它参数{hasAttachments, extIcons}仍然不起作用
		 * 
		 * @see SectionUtils.mergeSubject(String subject, int maxLength, Integer importantLevel, Boolean hasAttachments, String bodyType, List<String> extIcons)
		 * 
		 * @param cellContentHTML
		 */
		public void setCellContentHTML(String cellContentHTML) {
			this.cellContentHTML = cellContentHTML;
		}

		public Boolean getFiexed() {
			return fiexed;
		}

		public void setFiexed() {
			this.fiexed = true;
		}
		
    }
  
}
