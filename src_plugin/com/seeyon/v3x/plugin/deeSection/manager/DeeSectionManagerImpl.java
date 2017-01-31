package com.seeyon.v3x.plugin.deeSection.manager;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jfree.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.cache.loader.AbstractMapDataLoader;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.plugin.deeSection.dao.DeeSectionDao;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionDefine;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionProps;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionSecurity;
import com.seeyon.v3x.util.Strings;

public class DeeSectionManagerImpl implements DeeSectionManager {
	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(DeeSectionManagerImpl.class);
	private CacheMap<Long, DeeSectionDefine> DeeSectionDefineMap;

	private CacheMap<Long, ArrayList<DeeSectionProps>> DeeSectionPropsMap;


	private DeeSectionDao deeSectionDao;

	public void setDeeSectionDao(DeeSectionDao deeSectionDao) {
		this.deeSectionDao = deeSectionDao;
	}
	public void init(){ 
		DeeSectionDefineMap = cacheFactory.createLinkedMap("DeeSectionDefineMap");
		DeeSectionPropsMap = cacheFactory.createLinkedMap("DeeSectionPropsMap");		
		
		DeeSectionDefineMap.setDataLoader(new AbstractMapDataLoader<Long, DeeSectionDefine> (DeeSectionDefineMap) {
			@Override
			protected Map<Long, DeeSectionDefine> loadLocal() {
				Map<Long, DeeSectionDefine> result = new HashMap<Long, DeeSectionDefine>();
				List<DeeSectionDefine> ds = deeSectionDao.getAllDeeSection();
				if(CollectionUtils.isNotEmpty(ds)){
					for (DeeSectionDefine definition : ds) {
						result.put(definition.getId(), definition);
					}
				}
				return result;
			}

			@Override
			protected DeeSectionDefine loadLocal(Long k) {
				return deeSectionDao.getDeeSectinById(k);
			}
		});
		DeeSectionDefineMap.reload();
		
		DeeSectionPropsMap.setDataLoader(new AbstractMapDataLoader<Long, ArrayList<DeeSectionProps>>(DeeSectionPropsMap) {

			@Override
			protected Map<Long, ArrayList<DeeSectionProps>> loadLocal() {
				Map<Long, ArrayList<DeeSectionProps>> result = new LinkedHashMap<Long, ArrayList<DeeSectionProps>>();
				List<DeeSectionProps> ps = deeSectionDao.getAllSectionProps();
				for (DeeSectionProps props : ps) {
					ArrayList<DeeSectionProps> prop = result.get(props.getDeeSectionId());
					if(prop == null){
						prop = new ArrayList<DeeSectionProps>();
						result.put(props.getDeeSectionId(), prop);
					}
					prop.add(props);
				}
				return result;
			}

			@Override
			protected ArrayList<DeeSectionProps> loadLocal(Long k) {
				return new ArrayList<DeeSectionProps>(deeSectionDao.getPropsByDeeSectionId(k));
			}
		});
		DeeSectionPropsMap.reload();
	}
	@Override
	public void createDeeSection(DeeSectionDefine deeSection) {
		deeSection.setIdIfNew();
		deeSectionDao.saveDeeSection(deeSection);
	}

	@Override
	public void updateDeeSection(DeeSectionDefine deeSection) {
		deeSectionDao.updateDeeSection(deeSection);
	}

	@Override
	public void deleteDeeSection(String[] ids) {
		for(String id : ids){
			deeSectionDao.deleteDeeSection(Long.valueOf(id));
		}
	}

	@Override
	public List<DeeSectionDefine> findAllDeeSection() {
		return deeSectionDao.getAllDeeSection();
	}

	@Override
	public DeeSectionDefine findDeeSectionById(long id) {
		return deeSectionDao.getDeeSectinById(id);
	}

	@Override
	public List<DeeSectionProps> getSectionProps(long id) {
		List<DeeSectionProps> props = this.deeSectionDao.getPropsByDeeSectionId(id);
		return props;
	}

	@Override
	public Map<String, Object> getFlowList(String flowType, String moduleName,
			String flowName,int pageNum, int pageSize) {
		DEEConfigService deeService = DEEConfigService.getInstance();
		Map<String,Object> flowMap = deeService.getFlowList(flowType, moduleName, flowName, pageNum, pageSize);
		return flowMap;
	}
	public static void main(String[] args){
		DEEConfigService deeService = DEEConfigService.getInstance();
		Map<String,Object> flowList = deeService.getFlowList("1", DEEConfigService.MODULENAME_PORTAL, null, 1, 20);
		if(flowList!=null&&flowList.size()>0){
				System.out.println(flowList.get(DEEConfigService.MAP_KEY_TOTALCOUNT).toString());
				@SuppressWarnings("unchecked")
				List<FlowBean> list = (List<FlowBean>) flowList.get(DEEConfigService.MAP_KEY_RESULT);
				if(list!=null){
					for(FlowBean bean : list){
						System.out.println("Name:"+bean.getDIS_NAME()+"\t FLOW_ID:"+bean.getFLOW_ID()+" \t flow_desc:"+bean.getFLOW_DESC()+"\t FLOW_META:"+bean.getFLOW_META());
					}
				}
		}
	}

	@Override
	public void save(DeeSectionDefine deeSection, String[][] security) {
		List<DeeSectionSecurity> securities = new ArrayList<DeeSectionSecurity>();
		if(security != null){
			for (int i = 0; i < security.length; i++) {
				DeeSectionSecurity s = new DeeSectionSecurity();
				s.setIdIfNew();
				s.setDeeSectionId(deeSection.getId());
				s.setEntityType(security[i][0]);
				s.setEntityId(Long.parseLong(security[i][1]));
				s.setSort(i);
				securities.add(s);
			}
		}
		this.deeSectionDao.save(deeSection,securities);
	}

	@Override
	public void update(DeeSectionDefine deeSection, String[][] security) {
		List<DeeSectionSecurity> securities = new ArrayList<DeeSectionSecurity>();
		if(security != null){
			for (int i = 0; i < security.length; i++) {
				DeeSectionSecurity s = new DeeSectionSecurity();
				s.setIdIfNew();
				s.setDeeSectionId(deeSection.getId());
				s.setEntityType(security[i][0]);
				s.setEntityId(Long.parseLong(security[i][1]));
				s.setSort(i);
				securities.add(s);
			}
		}
		this.deeSectionDao.update(deeSection,securities);
	}

	@Override
	public List<DeeSectionSecurity> getSectionSecurity(long entityId) {
		return this.deeSectionDao.getSectionSecurity(entityId);
	}
	
	@Override
	public String getShowField(String flowId){
		if(flowId==null){
			return null;
		}
		StringBuffer out = new StringBuffer();
		out.append("var data = [];");
		DEEConfigService deeService = DEEConfigService.getInstance();
		String meta = deeService.getFlowMeta(flowId);
		
		if(Strings.isNotBlank(meta)){
			StringReader reader = new StringReader(meta);
			InputSource in = new InputSource(reader);
			SAXBuilder builder=new SAXBuilder();

			try {
				Document doc = builder.build(in);
				Element root = doc.getRootElement();
				List<Element> apps = root.getChildren("App");
				for (Element app : apps) {
					List<Element> tableLists = app.getChildren("TableList");
					for (Element tableList : tableLists) {
						List<Element> tables = tableList.getChildren("Table");
						for (Element table : tables) {
							List<Element> fields = table.getChildren("Field");
							for (Element field : fields) {
								if(field != null) {
									String id = field.getAttributeValue("name");
									String display = field
											.getAttributeValue("display");
									String fieldtype = field.getAttributeValue("fieldtype");
									out.append("var "+id+" = [];");
									out.append(id+"[0] = '"+id+"';");
									out.append(id+"[1] = '"+display+"';");
									out.append(id+"[2] = '"+fieldtype+"';");
									out.append(" data[data.length] = "+id+";");
								}
							}
						}
					}
				}
			} catch (JDOMException e) {
				Log.error("DEE数据级描述文件解析失败：", e);
			} catch (IOException e) {
				Log.error("DEE数据级描述文件加载失败：", e);
			}finally{
				reader.close();
			}
		}
		
		return out.toString();
	}
	@Override
	public Map<String,Map<String,String>> getShowFieldMap(String flowId){
		if(flowId==null){
			return null;
		}
		DEEConfigService deeService = DEEConfigService.getInstance();
		String meta = deeService.getFlowMeta(flowId);
		
		Map<String,Map<String,String>> allProps = new LinkedHashMap<String,Map<String,String>>();
		
		if(Strings.isNotBlank(meta)){
			StringReader reader = new StringReader(meta);
			InputSource in = new InputSource(reader);
			SAXBuilder builder=new SAXBuilder();
			try {
				Document doc = builder.build(in);
				Element root = doc.getRootElement();
				List<Element> apps = root.getChildren("App");
				for (Element app : apps) {
					List<Element> tableLists = app.getChildren("TableList");
					for (Element tableList : tableLists) {
						List<Element> tables = tableList.getChildren("Table");
						for (Element table : tables) {
							List<Element> fields = table.getChildren("Field");
							for (Element field : fields) {
								if(field != null) {
									String id = field.getAttributeValue("name");
									String display = field.getAttributeValue("display");
									String fieldtype = field.getAttributeValue("fieldtype");
									Map<String,String> map = new HashMap<String,String>();
									map.put("id", id);
									map.put("displayName",display);
									map.put("fieldType", fieldtype);
									map.put("isShow", "1");//"0":show;"1":hide
									allProps.put(id, map);
								}
							}
						}
					}
				}
				
			} catch (JDOMException e) {
				Log.error("DEE数据级描述文件解析失败：", e);
			} catch (IOException e) {
				Log.error("DEE数据级描述文件加载失败：", e);
			}
		}
		
		return allProps;
	}
	@Override
	public void saveSectionProps(long id, Map<String, Map<String,String>> props) {
		this.deeSectionDao.saveDeeSectionProps(id, props);
		List<DeeSectionProps> list = this.deeSectionDao.getPropsByDeeSectionId(id);
		this.DeeSectionPropsMap.put(id, (ArrayList<DeeSectionProps>) list);
	}
	@Override
	public boolean hasCurrentSectionName(String sectionName,String id) {
		if(Strings.isNotBlank(id)){
			return false;
		}else{
			List<DeeSectionDefine> list = this.deeSectionDao.getDeeSectionByName(sectionName);
			if(CollectionUtils.isNotEmpty(list)){
				return true;
			}else{
				return false;
			}
		}
	}
	@Override
	public List<DeeSectionDefine> findAllDeeSection(String sectionName) {
		return this.deeSectionDao.getAllDeeSection(sectionName);
	}
	@Override
	public List<DeeSectionDefine> getDeeSectionIdBySecurity(List<Long> entityIds) {
		if(CollectionUtils.isEmpty(entityIds)){
			return null;
		}
		List<DeeSectionSecurity> securities = deeSectionDao.getDeeSectionBySecurity(entityIds);
		List<DeeSectionDefine> deeSections = new ArrayList<DeeSectionDefine>();
		if(CollectionUtils.isNotEmpty(securities)){
			for(DeeSectionSecurity security : securities){
				Long deeSectionId = security.getDeeSectionId();
				DeeSectionDefine deeSectionDefine = this.findDeeSectionById(deeSectionId);
				if(deeSectionDefine!=null){
					deeSections.add(deeSectionDefine);
				}
			}
		}
		return deeSections;
	}
	
}
