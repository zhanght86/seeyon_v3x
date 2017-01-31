package com.seeyon.v3x.main.section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.manager.OuterlinkManager;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;

/**
 * 我的博客 栏目
 * 
 * @author ruanxm
 * @version 1.0 2008-8-04
 */
public class CommonLinkSection extends BaseSection {
	    private static final Log log = LogFactory.getLog(CommonLinkSection.class);

        private OuterlinkManager outerlinkManager;
        private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();

       public void setOuterlinkManager(OuterlinkManager outerlinkManager) {
            this.outerlinkManager = outerlinkManager;
         }

         @Override
        public String getIcon() {
             return null;
          }

         @Override
        public String getId() {
               return "commonLinkSection";
         }
         
         @Override
     	public String getBaseName() {
     		return "commonLinkSection";
     	}

         @Override
        protected String getName(Map<String, String> preference) {
                return "commonLinkSection";
         }

         @Override
        protected Integer getTotal(Map<String, String> preference) {
        	 return null;
         }

         @Override
        protected BaseSectionTemplete projection(Map<String, String> preference) {
                  ChessboardTemplete c = new ChessboardTemplete();
                  int width = Integer.parseInt(preference.get(PropertyName.width.name()));
                  int newLine = 3;
          		Integer newLineStr = newLine2Column.get(width);
          		if(newLineStr != null){
          			newLine = newLineStr.intValue();
          		}
          		c.setLayout(8, newLine);
              
                 Pagination.setNeedCount(false); // 不需要分页
                 Pagination.setFirstResult(0);
                 Pagination.setMaxResults(6*newLine);
                 List<LinkSystem> list = null;
                try {
					list = outerlinkManager.findAllCommonLinks();
				} catch (Exception e) {
					log.error(e);
				}
                
                if(list != null && !list.isEmpty()){
                    if(list.size() > 6*newLine){
                        list = list.subList(0, 6*newLine);
                    }
                	for(LinkSystem link : list){
                		ChessboardTemplete.Item item = c.addItem();
                        String subject = link.getName();
                        item.setName(subject);
                        item.setTitle(subject);
                        item.setLink(link.getUrl());
                	}
                 }
                c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE,"/linkManager.do?method=commonLinkMore&status=0");
                 return c;
          }
}


