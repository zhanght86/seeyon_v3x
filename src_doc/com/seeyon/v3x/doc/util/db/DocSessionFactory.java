package com.seeyon.v3x.doc.util.db;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.seeyon.v3x.doc.util.Constants;

/**
 * 文档元数据自己的SessionFactory
 */
public class DocSessionFactory extends org.springframework.orm.hibernate3.LocalSessionFactoryBean {
	private String[] mappingResources;

	public void setMappingResources(String[] mappingResources) {
		this.mappingResources = mappingResources;
		reSetMappingResources();
	}
	
	/**
	 * 实现 DocMetadata.hbm.xml 文件的重新加载
	 */
	public void reSetMappingResources(){
		Resource[] resources = new FileSystemResource[mappingResources.length];
		int i = 0;
		for (String mappingResource : mappingResources) {
//			String srcmd = DocSessionFactory.class.getClassLoader().getResource(mappingResource).toString();
//			String mdpath = srcmd.substring(5, srcmd.length()).replace("%5c", "/");
//			if(mdpath.indexOf("%20")!=-1){
//				mdpath=mdpath.replaceAll("%20", " ");
//			}

			String mdpath = "";
			if ("DocResource.hbm.xml".equals(mappingResource)) {
				mdpath = this.getClass().getClassLoader().getResource("com/seeyon/v3x/doc/domain/DocResource.hbm.xml").getPath();
			} else {
				mdpath = Constants.getCanonicalPathOfDynamicHbm(mappingResource);
			}
			resources[i++] = new FileSystemResource(mdpath);
			
//			try {
//				InputStream is = resources[i-1].getInputStream();
//				FileOutputStream out = new FileOutputStream("F:/a.xml");
//				IOUtils.copy(is, out);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
		}
		super.setMappingLocations(resources);
	}

}
