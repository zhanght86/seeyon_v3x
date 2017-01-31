package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.ObjectToXMLBase;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;

/**
 * 两行展现一项 模板
 * 适用于　如集团空间调查栏目
 * 第１行　标题，另起一行　发布时间和类型
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 * 
 */
public class OneItemUseTwoRowTemplete extends BaseSectionTemplete
{
    private static final long serialVersionUID = 8084617770183560680L;
    
    private List<OneItemUseTwoRowTemplete.Row> rows;

    @Override
    public String getResolveFunction()
    {
        return "oneItemUseTwoRowTemplete";
    }

    public OneItemUseTwoRowTemplete.Row addRow()
    {
        if (this.rows == null)
        {
            this.rows = new ArrayList<OneItemUseTwoRowTemplete.Row>();
        }

        OneItemUseTwoRowTemplete.Row row = new OneItemUseTwoRowTemplete.Row();
        this.rows.add(row);

        return row;
    }

    public List<OneItemUseTwoRowTemplete.Row> getRows()
    {
        return this.rows;
    }

    /**
     * 行对象
     */
    public class Row extends ObjectToXMLBase implements Serializable
    {

        private static final long serialVersionUID = 1354610946219042600L;

        private String subject; // 标题

        private String link; // 标题链接地址

        private Date createDate; //创建时间
        
        private String categoryLabel; //类型

        private String categoryLink; //类型连接地址
           
        private Boolean hasAttachments; // 是否有附件

        private int openType;
        
        private List<String> extIcons;

        public String getCategoryLabel()
        {
            return categoryLabel;
        }
     
        public String getCategoryLink()
        {
            return categoryLink;
        }

        public Date getCreateDate()
        {
            return createDate;
        }

        public void setCreateDate(Date createDate)
        {
            this.createDate = createDate;
        }

        public String getSubject()
        {
            return subject;
        }

        public void setSubject(String subject)
        {
            this.subject = subject;
        }

        public String getLink()
        {
            return link;
        }

        public void setLink(String link)
        {
            this.link = link;
        }

        /**
         * 标题链接，直接用/*.do?method=**&...
         * 
         * @param link
         * @param openType 打开方式
         */
        public void setLink(String link, OPEN_TYPE openType) {
            this.link = link;
            this.openType = openType.ordinal();
        }
        
        public int getOpenType() {
            return openType;
        }

        public void setOpenType(OPEN_TYPE openType) {
            this.openType = openType.ordinal();
        }
        
        public void setExtIcons(List<String> extIcons)
        {
            this.extIcons = extIcons;
        }

        public Boolean getHasAttachments()
        {
            return hasAttachments;
        }

        public void setHasAttachments(Boolean hasAttachments)
        {
            this.hasAttachments = hasAttachments;
        }


        public List<String> getExtIcons()
        {
            return extIcons;
        }

        public void addExtIcon(String extIcon)
        {
            if(this.extIcons == null){
            	this.extIcons = new ArrayList<String>();
            }
            
            this.extIcons.add(extIcon);
        }
        
        /**
         * 类别链接，直接用/*.do?method=**&...
         * 
         * @param label
         *            直接输出的文本，不做国际化
         * @param link
         *            如 "/collaboration.do?method=detail&from=Done&affairId=" +
         *            affair.getId())
         */
        public void setCategory(String label, String link) {
            this.categoryLabel = label;
            this.categoryLink = link;
        }

        public void setCategory(String label, String link, OPEN_TYPE openType) {
            this.categoryLabel = label;
            this.categoryLink = link;
            this.openType = openType.ordinal();
        }
        
    }
  
}
