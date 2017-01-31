package com.seeyon.v3x.doc.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.domain.DocMetadataUse;
import com.seeyon.v3x.doc.util.Constants;

public class MetadataUseDao extends BaseHibernateDao<DocMetadataUse> {
	private static final Log log = LogFactory.getLog(MetadataUseDao.class);
	
//	private DocMetadataDao docMetadataDao;
	
	/**
	 * 获取可以使用的元数据字段
	 * 
	 * @param type
	 * @return
	 */
	public synchronized String getCanBeUsedFieldName(byte type) {
		String fieldname = "";
		
		// lihf: 转换 userId deptId 类型为 reference 类型
		byte getType = type;
		if(getType == Constants.USER_ID || getType == Constants.DEPT_ID)
			getType = Constants.REFERENCE;
		
		String hsql = "from DocMetadataUse as a where a.type=? and a.useMark=0 order by a.fieldName";
		List list = super.find(hsql, getType);// 按类型和使用标记查找记录
		if (list != null && list.size() > 0) {// 有未使用的
			DocMetadataUse m = (DocMetadataUse) list.get(0);
			fieldname = m.getFieldName();
			m.setUseMark(true);
			super.update(m);
		} else {// 无未使用的
			String hsql2 = "from DocMetadataUse as a where a.type=? order by a.fieldName";
			List list2 = super.find(hsql2, getType);
			int length = list2.size();
			
			String trueType = Constants.getTrueType(type);
			fieldname = trueType + (length + 1);
			
			// 修改表 doc_metadata 结构
			for (int i = 1; i <= 5; i++) {
				String columnName = trueType + String.valueOf(length + i);
				final String sql = this.getAddColumnSql(type, columnName);
				getHibernateTemplate().execute(new HibernateCallback(){
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						Connection conn = session.connection();
						PreparedStatement stmt = null;
						stmt = conn.prepareStatement(sql);
						stmt.execute();
						
						return null;
					}
		    	});
			}
			
			// doc_metadata_use  增加标记
			List<DocMetadataUse> dmus = new ArrayList<DocMetadataUse>();			
			for (int i = 1; i <= 5; i++) {
				String newName = trueType + String.valueOf(length + i);
				DocMetadataUse newm2 = new DocMetadataUse();
				newm2.setFieldName(newName);
				newm2.setIdIfNew();
				newm2.setType(getType);				
				newm2.setUseMark(i == 1 ? true : false);
				
				dmus.add(newm2);
			}
			super.saveAll(dmus);
			
			
			// 修改配置文件 DocMetadata.hbm.xml
//			String _type = "\"" + Constants.getType(type) + "\"";
//			String _length = "\"" + Constants.getLength(type) + "\"";
//			String template = "\n   <property name={NAME}    type={TYPE}   column={COLUMN}   length={LENGTH}/>    ";
//			String add = "";
//			for (int i = 0; i < 5; i++) {
//				String _name = "\"" + trueType + String.valueOf(length + i + 1)
//						+ "\"";
//				add = add + template;
//				add = add.replaceAll("\\{NAME\\}", _name);
//				add = add.replaceAll("\\{TYPE\\}", _type);
//				add = add.replaceAll("\\{COLUMN\\}", _name);
//				add = add.replaceAll("\\{LENGTH\\}", _length);
//			}
			


			StringBuffer all = new StringBuffer();
			try {
//				String srcmd = this.getClass().getClassLoader().getResource("com\\seeyon\\v3x\\doc\\domain\\DocMetadata.hbm.xml").toString();
//				String mdpath = srcmd.substring(6, srcmd.length()).replace("%5c", "\\");
				String mdpath = Constants.getCanonicalPathOfDynamicHbm("DocMetadata.hbm.xml");
				// 建立FileReader对象，并实例化为fr
				FileReader fr = new FileReader(mdpath);
				// 建立BufferedReader对象，并实例化为br
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while (line != null) {
					all = all.append(line);

					line = br.readLine();
				}
				br.close();
				fr.close();

				String[] part = all.toString().split("<!-- edit flag,do not delete !!! -->");
				// 判断是否需要进行oracle9的特殊大字段处理
				boolean oracle9 = (part[0].indexOf("org.springframework.orm.hibernate3.support.ClobStringType") != -1);
				
				String add = "\n";
				for (int i = 1; i <= 5; i++) {
					String columnName = trueType + String.valueOf(length + i);
					String one = Constants.getString2Add(type, columnName, oracle9);
					add += "\n" + one;
				}
				
				String all2 = part[0] + add + "\n <!-- edit flag,do not delete !!! --> \n" + part[1];
				// 建立FileWriter对象，并实例化fw
				FileWriter fw = new FileWriter(mdpath);
				fw.write(all2);
				fw.close();
			} catch (Exception e) {
				log.debug("获取可以使用的元数据字段：", e);
			}
			
			// 重新加载
			DocMetadataDao dmd = (DocMetadataDao)ApplicationContextHolder.getBean("docMetadataDao");
			dmd.reloadConfigXml();
		}
		return fieldname;
	}
	/**
	 * 初始化
	 */
	public void init(){
		// docmetadata.hbm.xml 检查
		String mdpath = Constants.getCanonicalPathOfDynamicHbm("DocMetadata.hbm.xml");
		File file = new File(mdpath);
		boolean exist = file.exists();
		if(!exist){
			// 需要新生成动态表映射文件。
			List<DocMetadataUse> useAll = super.getAll();
//				log.info("类加载器：" + this.getClass().getClassLoader());
				
				String mdp = "com" + File.separator + "seeyon" + File.separator + "v3x" + File.separator 
						+ "doc" + File.separator + "domain" + File.separator + "DocMetadata.hbm.xml";
//				log.info("路径：" + mdp);
//				log.info("加载的Resource == null: " + (this.getClass().getClassLoader().getResource(mdp) == null));
				String srcmd = this.getClass().getClassLoader().getResource(mdp).toString();
				String mdpath2 = srcmd.substring(6, srcmd.length()).replace("%5c", File.separator);
//				if(File.separator.equals("/"))
					mdpath2 = File.separator + mdpath2;
				try {
				// 建立FileReader对象，并实例化为fr
				FileReader fr = new FileReader(mdpath2);
				// 建立BufferedReader对象，并实例化为br
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				StringBuffer all = new StringBuffer();
				while (line != null) {
					all = all.append(line);

					line = br.readLine();
				}
				br.close();
				fr.close();

				String[] part = all.toString().split("<!-- edit flag,do not delete !!! -->");
				// 判断是否需要进行oracle9的特殊大字段处理
				boolean oracle9 = (part[0].indexOf("org.springframework.orm.hibernate3.support.ClobStringType") != -1);
				
				String add = "\n";
				for(DocMetadataUse dmu : useAll){
					if(Constants.isSystemMetaId(dmu.getId()))
						continue;
					String one = Constants.getString2Add(dmu.getType(), dmu.getFieldName(), oracle9);
					add += "\n" + one;
				}
				
				String all2 = part[0] + add + "\n <!-- edit flag,do not delete !!! --> \n" + part[1];
				// 建立FileWriter对象，并实例化fw
				
				FileWriter fw = new FileWriter(mdpath);
				fw.write(all2);
				fw.close();
				} catch (IOException e) {
					log.error("系统生成docmetadata.hbm.xml文件出错：" + e.getMessage());
				}			
		}
	}
	// 根据数据库类型不同，取得对应的表修改sql
	private String getAddColumnSql(byte type, String columnName){
		String ret = "ALTER TABLE doc_metadata ADD " + columnName;
		
		String dbtype = Constants.getDBType();
		
		String dataType = "";
		
		if("oracle".equals(dbtype)){
			dataType = Constants.getDBTypeOfOracle(type);
		}else if("mysql".equals(dbtype)){
			dataType = Constants.getDBTypeOfMySql(type);
		}else if("sqlserver".equals(dbtype))
			dataType = Constants.getDBTypeOfSqlServer(type);
		
		ret += " " + dataType;
		
		return ret;
	}

	/**
	 * 更新使用记录（删除元数据时调用）
	 * 
	 * @param fieldname
	 */
	public void updateFileUse(String fieldname) {
		String hsql = "from DocMetadataUse as a where a.fieldName=?";
		List list = super.find(hsql, fieldname);
		if (list != null && list.size() > 0) {
			DocMetadataUse m = (DocMetadataUse) list.get(0);
			m.setUseMark(false);
			super.update(m);
		}
	}
//	public DocMetadataDao getDocMetadataDao() {
//		return docMetadataDao;
//	}
//	public void setDocMetadataDao(DocMetadataDao docMetadataDao) {
//		this.docMetadataDao = docMetadataDao;
//	}
	


}
