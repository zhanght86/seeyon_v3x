<%@ page contentType="text/html; charset=GB2312"%>
<%@ page import="com.bjca.sso.processor.*,com.bjca.sso.bean.*,java.util.*,java.net.*,com.bjca.security.*"%>
<%
	//服务器证书
	String BJCA_SERVER_CERT = request.getParameter("BJCA_SERVER_CERT");
	//票据
	String BJCA_TICKET = request.getParameter("BJCA_TICKET");
	//票据类型
	String BJCA_TICKET_TYPE = request.getParameter("BJCA_TICKET_TYPE");
	
	//out.println("票据："+BJCA_TICKET);
	
	TicketManager ticketmag = new TicketManager();
	//验证签名和解密
	UserTicket userticket = ticketmag.getTicket(BJCA_TICKET, BJCA_TICKET_TYPE, BJCA_SERVER_CERT);
	//处理票据信息
	if(userticket != null) {
		//取领导姓名
		String username = userticket.getUserName();
		//取领导id
		String userid = userticket.getUserUniqueID();
		//取委办局id
		String departid = userticket.getUserDepartCode();

		/**取角色信息
		Hashtable roles = userticket.getUserRoles();
		String s_role = "";
		if(roles != null && roles.size() > 0) {
			int index = 1;
			Enumeration e = roles.keys();
			Enumeration e2 = roles.elements();
			for(;e.hasMoreElements();){
				String rolecode = (String)e.nextElement();
				String rolename = (String)e2.nextElement();
				if(rolename.indexOf("?") != -1) {
					rolename = new String(rolename.getBytes("GBK"),"ISO-8859-1");
				}
				if(index == 1){
					s_role = rolecode;
				}else{
					s_role = s_role + "," + rolecode;
				}
				index++;
			}
		}
		*/
		//角色、领导id、领导姓名、委办局id、功能URL写入SESSION中
		//request.getSession().setAttribute("roles",s_role);
		request.getSession().setAttribute("userid",userid);
		request.getSession().setAttribute("departid",departid);
		//请委办局在此添加转向到子门户的链接地址。
		String ticketurl = "";
		//response.sendRedirect(ticketurl);
		request.getRequestDispatcher(ticketurl).forward(request,response);//转向业务系统
	}else{
		response.sendRedirect("sso_errors.jsp");//这里是临时的错误页面，可以修改错误页面
	}
%>