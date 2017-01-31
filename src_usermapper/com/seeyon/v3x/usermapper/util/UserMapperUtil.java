package com.seeyon.v3x.usermapper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.seeyon.v3x.organization.inexportutil.ResultObject;
import com.seeyon.v3x.usermapper.common.constants.RefreshUserMapperPolice;
import com.seeyon.v3x.usermapper.http.HttpUserMapperDispatcher;
import com.seeyon.v3x.usermapper.report.domain.ReportDetail;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.usermapper.domain.V3xOrgUserMapper;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;

public class UserMapperUtil {
	protected static Log log = LogFactory.getLog(UserMapperUtil.class);
	
  static public Map<String, List<V3xOrgUserMapper>> listToMap4Login(List<V3xOrgUserMapper> uml,boolean ignorSameExlogin){
	  Map<String, List<V3xOrgUserMapper>> rums
	                   =new HashMap<String, List<V3xOrgUserMapper>>();
	  Map<String,String> lexl=new HashMap<String,String>();
	  
	  if(uml==null)
		  return rums;
	  
	  for(V3xOrgUserMapper um:uml){
		  if(um==null)
			  continue;
		  
		  String login=um.getLoginName();
		  if(!StringUtils.hasText(login)
				  || !!StringUtils.hasText(um.getExLoginName()))
			  continue;
		  
		  String elogin=lexl.get(um.getExLoginName());
		  if(ignorSameExlogin && StringUtils.hasText(elogin))
			  continue;
		  
		  List<V3xOrgUserMapper> loginUml=rums.get(login) ;
		  if(loginUml==null){
			  loginUml=new ArrayList<V3xOrgUserMapper>();
		  }
		  loginUml.add(um);
		  rums.put(login, loginUml);
	  }
	  
	  return rums;
  }
  
  static public Map<String, List<V3xOrgUserMapper>> listToMap4Login(List<V3xOrgUserMapper> uml){
	  return listToMap4Login(uml,true);
  }
  
  static public RefreshUserMapperPolice providerPolice(String strPolice){
		
		try{			
			//return RefreshUserMapperPolice.valueOf(strPolice);
			return Enum.valueOf(RefreshUserMapperPolice.class, strPolice);
		}catch(Exception e){
			log.error("", e);
			return null;
		}
	}
  
  static public String type(HttpServletRequest request, HttpServletResponse response)
	throws Exception {
       // TODO Auto-generated method stub
	  return request.getParameter(HttpUserMapperDispatcher.HTTP_PARA_TYPE);
	
  }
  
  static public UserMapperUtil newInstance(){
	  return new UserMapperUtil();
  }
  
  public synchronized List<List<String>> readFile(FileToExcelManager fileToExcelManager,File f)throws Exception{
		List<List<String>> ol=fileToExcelManager.readExcel(f);
		if(ol==null)
			return null;
		if(ol.size()<2)
			return null;
		return ol.subList(2, ol.size());
  }
  public synchronized File uploadFile(HttpServletRequest request,FileManager fileManager,String filetag) throws Exception {
		Map<String, V3XFile> v3xFiles = new HashMap<String, V3XFile>();		
		File fil = null;
		try {
			V3XFile v3x = null;
			v3xFiles = fileManager.uploadFiles(request, filetag, null);	
			//v3xFiles = fileManager.uploadFiles(request, "xls", null);
			String key="";
			if(v3xFiles != null) {
				Iterator<String> keys = v3xFiles.keySet().iterator();
				while(keys.hasNext()) {
					key = keys.next();
					v3x = (V3XFile)v3xFiles.get(key);					
				}
			}
			fil = fileManager.getFile(v3x.getId(), v3x.getCreateDate());
		} catch (Exception e) {
			log.error("", e);
		}
		return fil;
	}
	
  public synchronized void copyFile(File in, File out) throws Exception {   
		FileInputStream fis  = new FileInputStream(in);     
		FileOutputStream fos = new FileOutputStream(out);     
		byte[] buf = new byte[1024];     
		int i = 0;     
		while((i=fis.read(buf))!=-1) {       
			fos.write(buf, 0, i);       
			}     
		fis.close();     
		fos.close();  
	} 
	
  public synchronized File  provideFile(HttpServletRequest request,FileManager fileManager,String filetag)throws Exception{
		if(!StringUtils.hasText(filetag))
			throw new Exception("no file tag");
		
		File file = uploadFile(request,fileManager,filetag);
		String path = file.getAbsolutePath()+"."+filetag;
		File realfile = new File(path);
		copyFile(file,realfile);
		
		return realfile;
	}
  
  public static DataRow[] createDataRowsFromReportDetails(List<ReportDetail> resultlst){
		DataRow[] datarow=null;
		if(resultlst!=null){
			datarow=new DataRow[resultlst.size()];
			
			for(int i=0;i<resultlst.size();i++){
				ReportDetail ro = resultlst.get(i);
				DataRow row = new DataRow();
				row.addDataCell(ro.getData(), 1);
				row.addDataCell(ro.getAction(), 1);
				row.addDataCell(ro.getMemo(), 1);
				datarow[i] = row;
			}
		}else{
			datarow=new DataRow[0];
		}
		
		return datarow;
	}
}//end class
