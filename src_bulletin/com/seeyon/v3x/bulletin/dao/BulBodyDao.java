package com.seeyon.v3x.bulletin.dao;
import java.util.List;

import com.seeyon.v3x.bulletin.domain.BulBody;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.util.Strings;

public class BulBodyDao extends BaseHibernateDao<BulBody> {
	
	public BulBody getByDataId(long bulDataId){
		String hql = "from BulBody where bulDataId = ?";
		List<BulBody> list = super.find(hql, bulDataId);
		if(list == null || list.size() == 0)
			return new BulBody(bulDataId);
		else
			return list.get(0);
	}
	
	public void saveBody(BulData data, boolean isNew) {
		BulBody body = new BulBody();
		body.setBodyType(data.getDataFormat());
		body.setBulDataId(data.getId());		
		/* 
		 * 对正文内容字符串在写入数据库之前进行处理，过滤掉其中的JS脚本内容如<script ***>***</script>
		 * <script ***>    匹配<[sS][cC][rR][iI][pP][tT].*>   替换为<!--
		 * </script>       匹配</[sS][cC][rR][iI][pP][tT]>    替换为-->
		 */
		String content = data.getContent();
		if(Strings.isNotBlank(content)){
			content = content.replaceAll("<[sS][cC][rR][iI][pP][tT].*>", "<!-- ")  
			 				 .replaceAll("</[sS][cC][rR][iI][pP][tT]>", " -->");
		}
		body.setContent(content); 
		
		body.setCreateDate(data.getCreateDate());
		//为保证印章校验有效，需要记录原正文的名称
		body.setContentName(data.getContentName());
		
		if(isNew){
			this.save(body);
		} else {
			this.update(body);
		}
	}
	
	public BulBody getByFileId(String fileId){
		String hql = "from BulBody where content like ?";
		List<BulBody> list = super.find(hql, fileId);
		if(list == null || list.size() == 0)
			return new BulBody();
		else
			return list.get(0);
	}
}
