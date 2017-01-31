package com.seeyon.v3x.system.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.metadata.MetadataException;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.NoSuchMetadataException;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgPost;

/**
 * 岗位管理
 * 
 * @author Administrator
 * 
 */
public class PostController extends BaseController {

	private MetadataManager metadataManager;
	private OrgManagerDirect orgManagerDirect; 

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 进入岗位管理的进入方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView postFrame(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/post/postFrame");
		return result;
	}

	/**
	 * 进入岗位管理的数据列表方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView postList(HttpServletRequest request,
			HttpServletResponse response) {
		List<MetadataItem> list = metadataManager.getMetadata(MetadataNameEnum.organization_post_types).getItems();
		//分页
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<MetadataItem> sublist = new ArrayList<MetadataItem>();
		if ((first + pageSize) > list.size()) {
			sublist = list.subList(first, list.size());
		} else {
			sublist = list.subList(first, first + pageSize);
		}
		ModelAndView result = new ModelAndView("sysMgr/post/postList");
		result.addObject("metadataItem", sublist);
		return result;
	}

	/**
	 * 进入岗位管理的添加方法
	 * 
	 * @param request
	 * @param respose
	 * @return
	 */
	public ModelAndView addPost(HttpServletRequest request,
			HttpServletResponse respose) {
		ModelAndView result = new ModelAndView("sysMgr/post/post");
		result.addObject("postOption", request.getParameter("form"));
		result.addObject("systemPost", 0);
		return result;
	}

	/**
	 * 添加岗位管理方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView createPost(HttpServletRequest request,
			HttpServletResponse response) {
		String label = request.getParameter("label");
		List<MetadataItem> list = metadataManager.getMetadata(MetadataNameEnum.organization_post_types).getItems();
		int maxValue = 0;
		for (MetadataItem item : list) {
			int newValue = Integer.parseInt(item.getValue());
			if(newValue > maxValue){
				maxValue = newValue;
			}
		}
		int sort = Integer.parseInt(request.getParameter("sort"));
		String description = request.getParameter("description");

		try {
			metadataManager.addMetadataItem(MetadataNameEnum.organization_post_types, label, String.valueOf(maxValue + 1),sort, description);
		} catch (NoSuchMetadataException e1) {
			e1.printStackTrace();
		} catch (MetadataException e1) {
			e1.printStackTrace();
		}

		try {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'))");
			out.println("</script>");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return super.refreshWorkspace();
	}
	
	/**
	 * 进入岗位管理的修改方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView editPost(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/post/post");
		MetadataItem item ;
		Long id = Long.parseLong(request.getParameter("id"));
		item = metadataManager.getMetadataItem(MetadataNameEnum.organization_post_types, id);
		String _postValue = item.getValue();
		item = metadataManager.getMetadataItem(MetadataNameEnum.organization_post_types, _postValue);
		result.addObject("postOption", request.getParameter("form"));
		result.addObject("metadataItem", item);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
			result.addObject("systemPost", 2);
		}else{
			result.addObject("systemPost", 1);
		}
		return result;
	}

	/**
	 * 对岗位管理进行修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView modifyPost(HttpServletRequest request,
			HttpServletResponse response) {
		String _postValue = request.getParameter("value");
		MetadataItem item = metadataManager.getMetadataItem(MetadataNameEnum.organization_post_types, _postValue);
		item.setLabel(request.getParameter("label"));
		item.setSort(Integer.parseInt(request.getParameter("sort")));
		item.setDescription(request.getParameter("description"));
		try {
			metadataManager.updateMetadataItem(
					MetadataNameEnum.organization_post_types, item.getId(),
					request.getParameter("label"), item.getValue(), Integer
							.parseInt(request.getParameter("sort")), request
							.getParameter("description"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (NoSuchMetadataException e) {
			e.printStackTrace();
		} catch (MetadataException e) {
			e.printStackTrace();
		}
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'))");
			out.println("</script>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return super.refreshWorkspace();
	}

	/**
	 * 删除岗位管理的记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView deletePost(HttpServletRequest request,
			HttpServletResponse response) {
		String postId = request.getParameter("id");
		String[] _postId = postId.split(",");
		Long id = null;
		for (int i = 0; i < _postId.length; i++) {
			id = Long.parseLong(_postId[i]);
			if (_postId != null && !_postId.equals("")) {
				try {
					String value = metadataManager.getMetadataItem(MetadataNameEnum.organization_post_types, id).getValue();
					List entList = orgManagerDirect.getEntityList(V3xOrgPost.class.getSimpleName(), "typeId", value, V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
					if(entList.size()>0){
						PrintWriter out;
						try {
							out = response.getWriter();
							out.println("<script>");
							out.println("alert(parent.v3x.getMessage('sysMgrLang.post_delete_type'))");
							out.println("</script>");
						} catch (IOException e) {
							e.printStackTrace();
						}
						return super.refreshWorkspace();
					}else{
						metadataManager.deleteMetadataItem(MetadataNameEnum.organization_post_types, id);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (NoSuchMetadataException e) {
					e.printStackTrace();
				} catch (MetadataException e) {
					e.printStackTrace();
				} catch (BusinessException e) {
					e.printStackTrace();
				}
			}
		}
		
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'))");
			out.println("</script>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.refreshWorkspace();
	}
}
