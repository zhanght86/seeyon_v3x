package com.seeyon.v3x.plugin.deeSection.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.Document;
import com.seeyon.v3x.dee.Document.Attribute;
import com.seeyon.v3x.dee.Document.Element;
import com.seeyon.v3x.dee.Parameters;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.db.code.model.FlowTypeBean;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionDefine;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionProps;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionSecurity;
import com.seeyon.v3x.plugin.deeSection.manager.DeeSectionManager;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class DeeSectionController extends BaseController {

	private static Log log = LogFactory.getLog(DeeSectionController.class);

	private DeeSectionManager deeSectionManager;
	
	private PortletEntityPropertyManager portletEntityPropertyManager;
	
	public void setPortletEntityPropertyManager(
			PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}

	public void setDeeSectionManager(DeeSectionManager deeSectionManager) {
		this.deeSectionManager = deeSectionManager;
	}

	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
	public ModelAndView main(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("plugin/deeSection/main");
	}

	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("plugin/deeSection/list");
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		if(Strings.isNotBlank(textfield)){
			mv.addObject("sectionName", textfield);
		}
		List<DeeSectionDefine> deeSections = deeSectionManager.findAllDeeSection(textfield);
			
		Map<Long, List<DeeSectionSecurity>> deeSectionSecurityMap = new HashMap<Long, List<DeeSectionSecurity>>();
		if(deeSections != null){
			for(DeeSectionDefine dee : deeSections){
				List<DeeSectionSecurity> sectionSecurities = this.deeSectionManager.getSectionSecurity(dee.getId());
				deeSectionSecurityMap.put(dee.getId(), sectionSecurities);
			}
		}
		mv.addObject("securityMap", deeSectionSecurityMap);

		mv.addObject("list", deeSections);

		return mv;
	}

	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
	public ModelAndView create(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("plugin/deeSection/create");
		String idStr = request.getParameter("id");

		if (Strings.isNotBlank(idStr)) {
			long deeSectionId = Long.parseLong(idStr);
			DeeSectionDefine deeSection = this.deeSectionManager.findDeeSectionById(deeSectionId);

			List<DeeSectionSecurity> sectionSecurities = this.deeSectionManager.getSectionSecurity(deeSectionId);
			List<DeeSectionProps> props = this.deeSectionManager.getSectionProps(deeSection.getId());
			
			mv.addObject("sectionSecurities", sectionSecurities);

			mv.addObject("deeSection", deeSection);
			
			mv.addObject("deeSectionProps", props);
		}
		
		return mv;
	}
	
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String[][] securities = Strings.getSelectPeopleElements(request.getParameter("selectPeopleStr"));
		
		String id = request.getParameter("id");
		String flowId = request.getParameter("flowId");
		String deeSectionName = request.getParameter("deeSectionName");
		String flowDisName = request.getParameter("flowDisName");
		String pageHeight = request.getParameter("pageHeight");
		if(Strings.isBlank(pageHeight)){
			pageHeight = "200";
		}
		String moduleName = DEEConfigService.MODULENAME_PORTAL;
		
		Map<String,Map<String,String>> allProps = deeSectionManager.getShowFieldMap(flowId);
		
		String[] keys = request.getParameterValues("showFieldKey");
		if(keys!=null&&keys.length>0&&allProps!=null&&!allProps.isEmpty()){
			for(String key : keys){
				if(allProps.get(key)!=null){
					String sort = request.getParameter("sort_"+key);
					allProps.get(key).put("isShow", "0");
					allProps.get(key).put("sort", sort);
				}
			}
		}
		
		DeeSectionDefine deeSection = new DeeSectionDefine();
		deeSection.setFlowId(Long.valueOf(flowId));
		deeSection.setFlowDisName(flowDisName);
		deeSection.setModuleName(moduleName);
		deeSection.setPageHeight(Integer.parseInt(pageHeight));
		deeSection.setDeeSectionName(deeSectionName);
		
		if (Strings.isBlank(id)) {
			deeSection.setIdIfNew();
			deeSectionManager.save(deeSection, securities);
		} else {
			deeSection.setId(Long.valueOf(id));
			deeSectionManager.update(deeSection, securities);
		}
		
		deeSectionManager.saveSectionProps(deeSection.getId(), allProps);
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('"
				+ Constants.getString4CurrentUser("system.manager.ok") + "')");
		out.println("</script>");
		out.flush();
		
		return super.redirectModelAndView("/deeSectionController.do?method=main","parent");
	}
	
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
	public ModelAndView selectDataSource(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("plugin/deeSection/treeDataSource");
		DEEConfigService deeService = DEEConfigService.getInstance();
		List<FlowTypeBean> flowTypeList = deeService.getFlowTypeList();
		List<FlowTypeBean> flowList = new ArrayList<FlowTypeBean>();
		if(CollectionUtils.isNotEmpty(flowTypeList)){
			for (FlowTypeBean bean : flowTypeList) {
				if(Strings.isNotBlank(bean.getPARENT_ID())){
					flowList.add(bean);
				}
			}
		}
		mv.addObject("flowTypeList", flowList);
		return mv;
	}
	
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
	public ModelAndView getDataSourceFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("plugin/deeSection/dataSourceFrame");
		return mv;
	}
	
	@SuppressWarnings("unchecked")
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
	public ModelAndView getFlowList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("plugin/deeSection/flowList");
		
		String pageSize = request.getParameter("pageSize");
		String page = request.getParameter("page");
		if(pageSize==null){
			pageSize = "20";
		}
		if(page==null){
			page = "1";
		}
		mv.addObject("pageSize", pageSize);
		mv.addObject("page", page);
		
		String flowTypeId = request.getParameter("flowTypeId");
		
		if(Strings.isNotBlank(flowTypeId)){
			Map<String, Object> flowMap = deeSectionManager.getFlowList(flowTypeId, DEEConfigService.MODULENAME_PORTAL, null, Integer.parseInt(page), Integer.parseInt(pageSize));
			if(flowMap!=null){
				Object total = flowMap.get(DEEConfigService.MAP_KEY_TOTALCOUNT);
				Object list = flowMap.get(DEEConfigService.MAP_KEY_RESULT);
				
				if(total!=null){
					Pagination.setRowCount(Integer.parseInt(String.valueOf(total)));
				}
				if(list!=null){
					mv.addObject("flowList", (List<FlowBean>)list);
				}
			}
		
		}
		return mv;
	}
	
	@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String[] ids = request.getParameterValues("id");
		if(ids!=null&&ids.length>0){
				deeSectionManager.deleteDeeSection(ids);
		}
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('"
				+ Constants.getString4CurrentUser("system.manager.ok") + "')");
		out.println("</script>");
		out.flush();

		return super.redirectModelAndView("/deeSectionController.do?method=main","parent");
	}
	
	public ModelAndView showSectionData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("plugin/deeSection/showSectionData");
		String sectionDefineId = request.getParameter("sectionDefineId");
		String entityId = request.getParameter("entityId");
		String ordinal = request.getParameter("ordinal");
		
		mv.addObject("sectionDefineId", sectionDefineId);
		mv.addObject("entityId", entityId);
		mv.addObject("ordinal", ordinal);
		
		String pageSize = request.getParameter("pageSize");
		String page = request.getParameter("page");
		if(pageSize==null){
			pageSize = "20";
		}
		if(page==null){
			page = "1";
		}
		mv.addObject("pageSize", pageSize);
		mv.addObject("page", page);
		
		if(Strings.isNotBlank(sectionDefineId)){
			List<DeeSectionProps> props = deeSectionManager.getSectionProps(Long.valueOf(sectionDefineId));
			Map<String,String> defaultShowProps = new LinkedHashMap<String,String>();
			Map<String,String> searchProps = new LinkedHashMap<String,String>();
			if(CollectionUtils.isNotEmpty(props)){
				for(DeeSectionProps p : props){
					if(p.getIsShow()==0){
						defaultShowProps.put(p.getPropName(), p.getPropValue());
						if(notDateField(p.getPropMeta())){
							searchProps.put(p.getPropName(), p.getPropValue());
						}
					}
				}
			}
			
			
			
			Map<String,String> entityProps = this.portletEntityPropertyManager.getPropertys(Long.valueOf(entityId), ordinal);
			Map<String,String> showProps = new LinkedHashMap<String,String>();
			
			
			if(entityProps!=null&&!entityProps.isEmpty()){
				String rowList = entityProps.get("rowList");
				String sectionName = entityProps.get("columnsName");
				mv.addObject("sectionName", sectionName);
				if(Strings.isNotBlank(rowList)&&rowList.equals("showField")){
					String showFields = entityProps.get("showField_value");
					if(Strings.isNotBlank(showFields)){
						searchProps.clear();
						String[] fields = showFields.split(",");
						for(DeeSectionProps prop : props){
							for(int i=0; i<fields.length; i++){
								if(fields[i].equals(prop.getPropName())){
									showProps.put(prop.getPropName(), prop.getPropValue());
									if(notDateField(prop.getPropMeta())){
										searchProps.put(prop.getPropName(), prop.getPropValue());
									}
								}
							}
						}
					}
				}else{
					showProps = defaultShowProps;
				}
			}
			
			DeeSectionDefine deeSectionDefine = deeSectionManager.findDeeSectionById(Long.valueOf(sectionDefineId));
			
			List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
			DEEClient client = new DEEClient();
			try {
				Parameters param = new Parameters();
				param.add(DEEConfigService.PARAM_PAGESIZE, Integer.valueOf(pageSize));
				param.add(DEEConfigService.PARAM_PAGENUMBER, Integer.valueOf(page));
				String condition = request.getParameter("condition");
				String textfield = request.getParameter("textfield");
				String where = " where 1=1 ";
				if(Strings.isNotBlank(condition)&&Strings.isNotBlank("textfield")){
					where += " AND "+condition+" like '%"+StringEscapeUtils.escapeSql(textfield)+"%'";
				}
				
				param.add("whereString", where);
				Document document = client.execute(String.valueOf(deeSectionDefine.getFlowId()),param);
				Element root =  document.getRootElement();
				List<Element> list = root.getChildren();
				if(CollectionUtils.isNotEmpty(list)){
					for(Element t : list){
						Attribute total = t.getAttribute("totalCount");
						int totalCount = 0;
						if(total!=null&&total.getValue()!=null){
							totalCount = Integer.parseInt(String.valueOf(total.getValue()));
							Pagination.setRowCount(totalCount);
						}
						
						List<Element> rows = t.getChildren();
						if(CollectionUtils.isNotEmpty(rows)&&totalCount>0){
							for(Element row : rows){
								if(props!=null&&!props.isEmpty()){
									Map<String,Object> rowMap = new LinkedHashMap<String,Object>();
									
									Set<String> keys = null;
									
									keys = showProps.keySet();
									
									for(String key : keys){
										Element e = row.getChild(key);
										if(e!=null){
											if(e.getValue()!=null){
												rowMap.put(new String(key), e.getValue());
											}else{
												rowMap.put(new String(key), "");
											}
										}else{
											rowMap.put(new String(key), "");
										}
									}
									data.add(rowMap);
								}
							}
						}
					}
				}
				document = null;
			} catch (TransformException e) {
				log.error("DEE栏目执行引擎查询出错："+e);
			}
			int columnLength = 100;
			if(showProps.size()>0){
				columnLength = 100/showProps.size();
			}
			mv.addObject("columnLength", columnLength);
			mv.addObject("props", showProps);
			mv.addObject("data", data);
			mv.addObject("searchProps", searchProps);
		}
		return mv;
	}
	public ModelAndView showField4Portal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("plugin/deeSection/showField4Portal");
		String entityId = request.getParameter("entityId");
		String ordinal = request.getParameter("ordinal");
		if(Strings.isNotBlank(entityId)){
			Map<String,String> entityProps = this.portletEntityPropertyManager.getPropertys(Long.valueOf(entityId), ordinal);
			if(entityProps!=null&&!entityProps.isEmpty()){
				String singleBoardId = entityProps.get("singleBoardId");
				if(Strings.isNotBlank(singleBoardId)){
					List<DeeSectionProps> props = deeSectionManager.getSectionProps(Long.valueOf(singleBoardId));
					Map<String,String> defaultShowProps = new LinkedHashMap<String,String>();
					if(CollectionUtils.isNotEmpty(props)){
						for(DeeSectionProps p : props){
							if(p.getIsShow()==0){
								defaultShowProps.put(p.getPropName(), p.getPropValue());
							}
						}
					}
					mv.addObject("props", defaultShowProps);
				}
			}else{
				log.info("获取DEE栏目属性失败，栏目entityId为："+entityId);
			}
		}else{
			log.info("获取DEE栏目ID失败，栏目entityId为："+entityId);
		}
		return mv;
	}
	private boolean notDateField(String fieldType){
		String type = fieldType.toLowerCase();
		if(type.indexOf("time")!=-1||type.indexOf("date")!=-1||type.indexOf("timestamp")!=-1){
			return false;
		}else{
			return true;
		}
	}
}
